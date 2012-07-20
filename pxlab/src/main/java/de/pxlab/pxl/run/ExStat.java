package de.pxlab.pxl.run;

import java.io.File;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.*;

import de.pxlab.awtx.*;
import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * Run statistical data processing on one or more data files. This program
 * executes statistical data processing objects on structured PXLab data files
 * (extension 'dtr') or raw data files (extension 'dat'). The data processing
 * objects my be defined within the data files or may be defined in a separate
 * design file which only contains the data processing objects.
 * 
 * <p>
 * Command line options are:
 * 
 * <dl>
 * 
 * <dt>-f file
 * <dd>gives an explicit design file which contains the data processing object
 * definitions. The syntax of such a file is the same as an ordinary design file
 * but only the data processing objects of the file are used. If this option is
 * missing then the data processing objects of the data files are excuted for
 * each data file but no ExperimentData object is executed.
 * 
 * <dt>-d file
 * <dd>specifies a data file or a directory containing data files. All files in
 * the directory must have been created by the same design file. If this option
 * is missing then the current working directory is assumed to contain the data
 * files. This option may be used more than once.
 * 
 * <dt>-t file
 * <dd>defines the name of the output file. If this option is missing then
 * 'pxlab_stat.html' is used as the output file name. Note that output data are
 * appended to existing result files if the same name is used more than once.
 * 
 * <dt>-r
 * <dd>tells ExStat that the data files are raw tables of data ("dat" instead of
 * structured PXLab data files ("dtr").
 * 
 * <dt>-D name
 * <dd>activate debugging option 'name'.
 * 
 * <dt>file
 * <dd>any other strings are assumed to be file or directory names containing
 * data.
 * 
 * </dl>
 * 
 * @version 0.1.2
 */
/*
 * 
 * 2005/12/20
 * 
 * 2006/08/18 added feature to specify multiple data files/directories
 * 
 * 2007/03/16 send argument of -t option to Base
 */
public class ExStat implements CommandLineOptionHandler {
	private String options = "d:f:t:ri:D:VM?";
	private String designFileName = null;
	private ArrayList dataSource = null;
	private int fileType = DataDisplay.DTR_FILE_TYPE;

	/**
	 * Run the data processing objects on files as specified by the command
	 * line.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public ExStat(String[] args) {
		dataSource = new ArrayList(10);
		// Check/Exceute command line arguments
		if ((args != null) && (args.length > 0)) {
			CommandLineParser clp = new CommandLineParser(args, options, this);
			while (clp.hasMoreArgs()) {
				dataSource.add(clp.getArg());
			}
		}
		// get list of data files
		File[] dataFiles = null;
		dataFiles = getDataFiles();
		if (dataFiles == null || dataFiles.length == 0) {
			System.out.println("ExStat: No data files found.");
			showOptions();
			System.exit(0);
		}
		// The design which contains the data processing objects
		ExDesign exDesign = null;
		// its context node
		ExDesignNode exDesignContext = null;
		// an the data processing node
		ExDesignNode exDisplay = null;
		if (designFileName == null) {
			// System.out.println("No design file. Processing individual data files.");
			// we don't have an explicit design file, so use the data
			// processing objects of the data files and run each file
			// individually.
			for (int i = 0; i < dataFiles.length; i++) {
				try {
					exDesign = new ExDesign(dataFiles[i].getPath(), null);
				} catch (Exception ex) {
					System.out.println("Error when parsing data file "
							+ designFileName);
					System.exit(3);
				}
				exDesign.runDataSession(new DataProcessor(null, exDesign));
			}
			// we are done in this case
			System.exit(0);
		}
		// we have an explicit design file name
		try {
			// System.out.println("Parsing " + designFileName);
			exDesign = new ExDesign(designFileName, null);
		} catch (Exception ex) {
			System.out.println("Error when parsing file " + designFileName);
			System.out.println(ex.getMessage());
			System.exit(3);
		}
		// exDesign.print();
		// get the data display nodes in the design file
		exDesignContext = exDesign.getContextTree();
		ArrayList contextDsp = exDesignContext.getChildrenList();
		boolean hasNonExperimentDataDisplay = false;
		for (Iterator it = contextDsp.iterator(); it.hasNext();) {
			ExDesignNode node = (ExDesignNode) (it.next());
			if (node.isDataDisplayList()) {
				if (node.isExperimentDataDisplayList()) {
					// only a single ExperimentData node is allowed
					exDisplay = node;
				} else {
					hasNonExperimentDataDisplay = true;
				}
			}
		}
		if (hasNonExperimentDataDisplay) {
			// we have to cycle through the list of data files and
			// execute the non-ExperimentData display objects
			// System.out.println("ExStat() Design file contains non-ExperimentData processing nodes - execute these first.");
			for (int i = 0; i < dataFiles.length; i++) {
				exDesign = fixedExDesign(exDesignContext, dataFiles[i]);
				exDesign.runDataSession(new DataProcessor(null, exDesign));
			}
		}
		if (exDisplay != null) {
			// we also have an ExperimentData node, so execute this one also
			DataDestination dataDestination = new DataDestination(
					designFileName);
			// create an active design tree
			ExDesign active = exDesign;
			if (fileType == DataDisplay.DTR_FILE_TYPE) {
				active = fixedExDesign(exDesignContext, dataFiles[0]);
			}
			// active.print();
			// System.out.println("ExStat() Initialize runtime context.");
			active.initRuntimeContext();
			// System.out.println("ExStat() Runtime context initialized.");
			// get the active data display list
			// System.out.println("ExStat() DisplayList = " +
			// exDisplay.getInstanceName());
			DisplayList displayList = active.getDataDisplayList(exDisplay
					.getInstanceName());
			// get the list's parameter names or use the factor names
			boolean hasData = true;
			String[] parNames = displayList.getExDesignNode().getParNames();
			if (parNames == null || parNames.length == 0) {
				// no parameters in the list, so use factor definitions
				parNames = ExPar.FactorialDataFormat.getStringArray();
				if (parNames == null || parNames.length == 0) {
					hasData = false;
				}
			}
			if (hasData) {
				// at this point we should have a non-empty array of parameter
				// names which make up the data table.
				int dls = displayList.size();
				DataDisplay dsp;
				for (int i = 0; i < dls; i++) {
					dsp = (DataDisplay) displayList.get(i);
					// System.out.println("ExStat() Create data table for " +
					// dsp.getInstanceName());
					dsp.createDataTable(active, parNames, dataFiles, fileType);
					// System.out.println("ExStat() Process data for " +
					// dsp.getInstanceName());
					dsp.doProcessDataTable(exDesign, dataDestination);
				}
			}
		}
	}

	/**
	 * Create an ExDesign where all DataDisplay nodes of the design in the given
	 * file have been replaced by the corresponding DataDisplay nodes of the
	 * given context. This is needed in order to call initRuntimeContext() and
	 * create a proper runtime context for the execution of the data processing
	 * nodes.
	 */
	private ExDesign fixedExDesign(ExDesignNode newContext, File f) {
		// System.out.println("ExStat.fixedExDesign(): " + f.getPath());
		ExDesign exDesign = null;
		try {
			exDesign = new ExDesign(f.getPath(), null);
		} catch (Exception ex) {
			System.out.println("Error when parsing file " + f.getPath()
					+ " for fixup");
			System.exit(3);
		}
		ExDesignNode context = exDesign.getContextTree();
		ArrayList dsp = context.getChildrenList();
		ArrayList newDsp = newContext.getChildrenList();
		for (Iterator it = newDsp.iterator(); it.hasNext();) {
			ExDesignNode newNode = (ExDesignNode) (it.next());
			if (newNode.isDataDisplayList()) {
				for (Iterator jt = dsp.iterator(); jt.hasNext();) {
					ExDesignNode node = (ExDesignNode) (jt.next());
					if (node.getType() == newNode.getType()
							&& node.getInstanceName().equals(
									node.getInstanceName())) {
						context.remove(node);
					}
				}
				context.add(newNode);
			} else if (newNode.isAssignmentGroup()) {
				context.add(newNode);
			}
		}
		return exDesign;
	}

	// --------------------------------------------------------------------
	// Implementation of CommandLineOptionHandler
	// --------------------------------------------------------------------
	/** This method is called for every command line option found. */
	public void commandLineOption(char c, String arg) {
		switch (c) {
		case 'f':
			designFileName = arg;
			break;
		case 'd':
			dataSource.add(arg);
			break;
		case 't':
			Base.setProcessedDataFileName(arg);
			break;
		case 'r':
			fileType = DataDisplay.DAT_FILE_TYPE;
			break;
		case 'i':
			GlobalAssignments.add(arg);
			break;
		case 'D':
			Debug.add(arg);
			break;
		case 'M':
			Debug.startMemoryMonitor();
			break;
		case 'V':
			showVersion();
			System.exit(1);
			break;
		case '?':
			showOptions();
			System.exit(1);
			break;
		}
	}

	/**
	 * This method is called whenever an error is found in the command line
	 * options.
	 */
	public void commandLineError(int e, String s) {
		System.out.println("Command line error: " + s);
		showOptions();
	}

	/** This shows the PXLab version and build number. */
	private void showVersion() {
		System.out.println("PXLab Version " + Version.instance());
	}

	/** This shows the available command line options. */
	private void showOptions() {
		System.out.println("Usage: java " + this.getClass().getName()
				+ " [options]");
		System.out.println("Options are:");
		if (options.indexOf('f') >= 0)
			System.out.println("   -f file  use design file \'file\'");
		if (options.indexOf('d') >= 0)
			System.out
					.println("   -d file  get data from data file or directory \'file\'");
		if (options.indexOf('t') >= 0)
			System.out.println("   -t file  write output to file \'file\'");
		if (options.indexOf('r') >= 0)
			System.out.println("   -r       data files are of type \'dat\'");
		if (options.indexOf('i') >= 0)
			System.out
					.println("   -i file  add file \'file\' to list of initialization files");
		if (options.indexOf('D') >= 0)
			System.out
					.println("   -D code  set debugging option \'code\' (use \'?\' to get list)");
		if (options.indexOf('M') >= 0)
			System.out
					.println("   -M       start a continuously running memory monitor");
		if (options.indexOf('V') >= 0)
			System.out.println("   -V       show version number only");
		if (options.indexOf('?') >= 0)
			System.out.println("   -?       show this help text");
	}

	// --------------------------------------------------------------------
	// End of CommandLineOptionHandler implementation
	// --------------------------------------------------------------------
	/** Get an array of data files */
	private File[] getDataFiles() {
		ArrayList list = new ArrayList(100);
		if (dataSource.isEmpty()) {
			dataSource.add(".");
		}
		for (Iterator it = dataSource.iterator(); it.hasNext();) {
			File fd = new File((String) it.next());
			if (fd.isDirectory()) {
				File[] ds = fd
						.listFiles(new FilenameExtensionFilter(
								(fileType == DataDisplay.DTR_FILE_TYPE) ? "dtr"
										: "dat"));
				for (int i = 0; i < ds.length; i++)
					list.add(ds[i]);
			} else if (fd.isFile() && fd.canRead()) {
				list.add(fd);
			}
		}
		File[] flst = new File[list.size()];
		for (int i = 0; i < list.size(); i++)
			flst[i] = (File) list.get(i);
		return flst;
	}

	public static void main(String[] args) {
		new ExStat(args);
	}
}
