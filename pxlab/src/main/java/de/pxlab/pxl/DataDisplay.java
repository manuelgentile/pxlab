package de.pxlab.pxl;

import java.util.*;
import java.io.*;

import de.pxlab.pxl.*;
import de.pxlab.util.*;
import de.pxlab.stat.*;

/**
 * Abstract super class for data processing objects.
 * 
 * <p>
 * This class creates a data table of type ExParValueTable from all trial data
 * of the list node which contains this data processing object. Only trials
 * which satisfy the Include condition and do not satisfy the Exclude condition
 * are entered into the data table.
 * 
 * <p>
 * The column entries for the data table must be defined as arguments of the
 * parent data processing list in the design file. If no arguments are defined
 * in the parent node then the current entries of the experimental parameter
 * ExPar.FactorialDataTableFormat are used as columns of the data table. These
 * are all parameters which have been defined as factors in the Factors()
 * section of the design file.
 * 
 * <p>
 * This class contains two methods for creating the data table:
 * 
 * <p>
 * createDataTable(String[], ExDesignNode) creates the data table from all
 * trials which are children of the argument node. This method is used by the
 * de.pxlab.pxl.DataProcessor class of an experimental runtime controller like
 * de.pxlab.pxl.run.ExRun which runs experiments. It can only collect data from
 * the currently active data tree in an ExDesign object.
 * 
 * <p>
 * createDataTable(String[], File[], int) creates the data table from all trials
 * contained in all data files of the argument array of File objects. This
 * method is used by the application de.pxlab.pxl.run.ExStat to create a data
 * table for a list of data files. If the input data files are raw data files
 * then the names of the columns must be given as arguments to this display
 * object's parent node.
 * 
 * <p>
 * The resulting data table is stored here and sent to any concrete subclass by
 * calling its processDataTable() method. This call is evoked by the
 * DataProcessor calling this class's doProcessDataTable() method which
 * delegates the action to the subclass's processDataTable() method. If the data
 * processing object which gets the data table has its own argument list defined
 * then it will generate a subtable containing the respective columns and work
 * on the subtable.
 * 
 * <p>
 * Subclasses may generate two types of computed results files:
 * 
 * <ul>
 * 
 * <li>Files which contain the results of computations, and
 * 
 * <li>Files containing plot data.
 * 
 * </ul>
 * 
 * <p>
 * Computing results file. The name of this file is defined by the parameter
 * FileName or may be defined in the application's command line. This file is
 * stored by the DataDestination object. If the file name is defined in the
 * application's command line (option -t) then the DataDestination object uses
 * this as the respective file name. If the file name is not defined in the
 * command line then it is created from experimental parameters. The destination
 * directory is defined by the values of the global experimental parameters
 * DataFileDestination and ProcessedDataDirectory. DataFileDestination defines
 * the directory containing the data files and ProcessedDataDirectory defines
 * the subdirectory for processed data. The default for ProcessedDataDirectory
 * is 'pdt' as a subdirectory of the directory defined by the global
 * experimental parameter DataFileDestination. If the parameter FileName is
 * non-empty then the DataDestination object uses the string value of FileName
 * as the respective file name for the respective directory. If the parameter
 * FileName is empty, then the file name is created from the base of the
 * ordinary data file using the extension defined by the global parameter
 * ProcessedDataFileExtension.
 * 
 * <p>
 * Plot data files. The destination directory for plot data is determined in the
 * same way as the processed data directory, but uses the global parameter
 * PlotDataDirectory as its subdirectory. The data file extension is defined by
 * the parameter PlotDataFileExtension. The file name base is prefixed by the
 * parameter PlotDataFilePrefix and this prefix is followed by a condition
 * description defined by the DataDisplay subclass.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * @see DataProcessor
 * @see de.pxlab.pxl.run.ExStat
 */
/*
 * 
 * 2005/12/08
 * 
 * 2007/05/12 allow empty data tables for objects which get their data from
 * other sources.
 */
abstract public class DataDisplay extends DisplaySupport implements
		ExParTypeCodes {
	/** Input data file type for PXLab data tree files. */
	public static final int DTR_FILE_TYPE = 0;
	/**
	 * Input data file type for raw data files. These are files where every line
	 * corresponds to a trial and every column corresponds to an experimental
	 * parameter value.
	 */
	public static final int DAT_FILE_TYPE = 1;
	/**
	 * If this condition is true for a trial during data collection then the
	 * trial is considered for being entered into the data table.
	 */
	public ExPar Include = new ExPar(FLAG, new ExParValue(1),
			"Inclusion condition");
	/**
	 * If this condition is true for a trial during data collection then the
	 * trial is excluded from the data table.
	 */
	public ExPar Exclude = new ExPar(FLAG, new ExParValue(0),
			"Exclusion condition");
	/**
	 * Defines the detail of the output protocol. Possible values are defined in
	 * class de.pxlab.pxl.StatPrintBitCodes.
	 */
	public ExPar PrintLevel = new ExPar(
			GEOMETRY_EDITOR,
			StatPrintBitCodes.class,
			new ExParValueFunction(
					ExParExpression.BIN_OR_OP,
					new ExParValueConstant(
							"de.pxlab.pxl.StatPrintBitCodes.PRINT_DESCRIPTIVE"),
					new ExParValueConstant(
							"de.pxlab.pxl.StatPrintBitCodes.PRINT_RESULTS")),
			"Detail of printed results");
	/** Flag to create plot data files. */
	public ExPar CreatePlotData = new ExPar(FLAG, new ExParValue(0),
			"Flag to create data files for plotting");
	/** Data analysis section header in the output file. */
	public ExPar Header = new ExPar(STRING, new ExParValue("Data Analysis"),
			"Data Analysis Section Header");
	/**
	 * Name of the file which gets the output of this data processing object. By
	 * default this is empty and the output file name is derived from the
	 * subject code or the design file.
	 */
	public ExPar FileName = new ExPar(STRING, new ExParValue(""),
			"Name of the output data file");
	public ExPar PlotDataFilePrefix = new ExPar(STRING, new ExParValue("plot"),
			"Plot data file name prefix");

	/** Initialize this instance of the data display object. */
	public void createInstance() {
		// System.out.println("DataDisplay.createInstance()");
		// Make sure that the display and timing element lists are empty
		destroyInstance();
		// Create this Display object's ExPar descriptor array for
		// later use
		createExParFields();
	}

	public void destroyInstance() {
	}
	private ExParValueTable dataTable;
	protected int total_N;
	protected int included_N;
	protected int excluded_N;
	protected DataDestination dataDestination;

	/**
	 * Create the data table from the trials in the given node.
	 * <p>
	 * This method is used by de.pxlab.pxl.DataProcessor to create the data
	 * table. It is the first method called for each data processing list by the
	 * data processor. The second method is doProcessDataTable().
	 * 
	 * @param parNames
	 *            the names of the experimental parameters which should be
	 *            contained in the data table columns.
	 * @param data
	 *            the data node which contains the data trials.
	 */
	public void createDataTable(String[] parNames, ExDesignNode data) {
		dataTable = new ExParValueTable(parNames, 500);
		total_N = 0;
		included_N = 0;
		excluded_N = 0;
		collectData(parNames, data);
		if (Debug.isActive(Debug.DATA)) {
			String nl = System.getProperty("line.separator");
			System.out.println(nl + "DataDisplay.createDataTable()");
			System.out.println("  Columns:");
			for (int i = 0; i < parNames.length; i++)
				System.out.println("    " + parNames[i]);
			System.out.println("  Data:");
			System.out.println(dataTable.toString());
			System.out.println("     total = " + total_N);
			System.out.println("  included = " + included_N);
			System.out.println("  excluded = " + excluded_N);
			System.out.println("     final = " + dataTable.size() + nl);
		}
	}

	/**
	 * Create a data table from the trials in the given data files. We may
	 * either have a list of PXLab data tree files (DTR_FILE_TYPE) or a list of
	 * raw data files (DAT_FILE_TYPE). If we have data tree files then all data
	 * trees must have been generated by the same design file.
	 * 
	 * <p>
	 * This method is used by de.pxlab.pxl.run.ExStat to create the data table.
	 * After this has been done the method doProcessdataTable() is called.
	 * 
	 * @param exDesignDataProc
	 *            the complete design tree which contained the data processing
	 *            list we are working on.
	 * @param parNames
	 *            the names of the experimental parameters which should be
	 *            contained in the data table columns.
	 * @param dataFiles
	 *            an array of data files.
	 * @param fileType
	 *            the type of data file we have to expect. Possible types are
	 *            DAT_FILE_TYPE or DTR_FILE_TYPE.
	 */
	public void createDataTable(ExDesign exDesignDataProc, String[] parNames,
			File[] dataFiles, int fileType) {
		dataTable = new ExParValueTable(parNames, 2000);
		total_N = 0;
		included_N = 0;
		excluded_N = 0;
		if (fileType == DTR_FILE_TYPE) {
			// we have structured data files
			for (int i = 0; i < dataFiles.length; i++) {
				try {
					ExDesign design = new ExDesign(dataFiles[i].getPath(), null);
					ExDesignNode designTree = design.getExDesignTree();
					designTree.pushArgs();
					design.pushContext();
					design.createConditionTable();
					design.pushCovariateFactors();
					ExDesignNode procTree = design.getProcedureTree();
					collectData(parNames, procTree);
					design.popCovariateFactors();
					design.popContext();
					designTree.popArgs();
				} catch (Exception ex) {
					System.err
							.println("DataDisplay.createDataTable(): Error when parsing file "
									+ dataFiles[i].getPath());
					System.exit(3);
				}
			}
		} else {
			// we have raw data files
			collectData(exDesignDataProc, parNames, dataFiles);
		}
		if (Debug.isActive(Debug.DATA)) {
			String nl = System.getProperty("line.separator");
			System.out.println(nl + "DataDisplay.createDataTable()");
			System.out.println("  Columns:");
			for (int i = 0; i < parNames.length; i++)
				System.out.println("    " + parNames[i]);
			// System.out.println("  Data:");
			// System.out.println(dataTable.toString());
			System.out.println("     total = " + total_N);
			System.out.println("  included = " + included_N);
			System.out.println("  excluded = " + excluded_N);
			System.out.println("     final = " + dataTable.size() + nl);
		}
	}

	private void collectData(String[] parNames, ExDesignNode n) {
		if (n.hasChildren()) {
			ArrayList childs = n.getChildrenList();
			for (int i = 0; i < childs.size(); i++) {
				ExDesignNode m = (ExDesignNode) childs.get(i);
				m.pushArgs();
				collectData(parNames, m);
				m.popArgs();
			}
		} else {
			total_N++;
			if (Include.getFlag()) {
				included_N++;
				if (Exclude.getFlag()) {
					excluded_N++;
				} else {
					ExParValue[] row = new ExParValue[parNames.length];
					for (int i = 0; i < parNames.length; i++) {
						row[i] = ExPar.get(parNames[i]).getValue().getValue();
					}
					dataTable.addRow(row);
				}
			}
		}
	}

	/**
	 * Collect data from a list of raw data files. If the data processing design
	 * file contains a Trial declaration then its arguments are used as
	 * parameter names of the ram data file columns. If the data processing
	 * design tree does not contain a Trial declaration then the data processing
	 * list arguments are used as names for the input parameters. In both cases
	 * the number of columns in the input data files must be identical to the
	 * number of arguments of the respective node.
	 */
	private void collectData(ExDesign exDesign, String[] parNames,
			File[] dataFiles) {
		String[] dataParNames = parNames;
		ExDesignNode trialDeclaration = null;
		ExDesignNode context = exDesign.getContextTree();
		if (context != null) {
			trialDeclaration = context
					.getFirstChildOfType(ExDesignNode.TrialDisplayNode);
			// context.print();
		}
		if (trialDeclaration == null) {
			// System.out.println("DataDisplay.collectData() No Trial Declaration.");
		} else {
			// System.out.println("DataDisplay.collectData() Found Trial Declaration.");
			dataParNames = trialDeclaration.getParNames();
			// trialDeclaration.print();
		}
		if (dataParNames == null) {
			System.out
					.println("DataDisplay.collectData() No input data names.");
			System.exit(3);
		}
		// first make sure that the
		// respective data parameters exist in the runtime table
		int dm = dataParNames.length;
		ExPar[] dataPars = new ExPar[dm];
		for (int i = 0; i < dm; i++) {
			if (ExPar.contains(dataParNames[i])) {
				dataPars[i] = ExPar.get(dataParNames[i]);
			} else {
				System.out
						.println("DataDisplay.collectData() Undeclared parameter name: "
								+ dataParNames[i]);
				System.out.println(" --- This should never happen!");
				dataPars[i] = new ExPar(0, new ExParValue(0), "");
				ExPar.enter(dataParNames[i], dataPars[i]);
			}
		}
		int tm = parNames.length;
		ExPar[] tablePars = new ExPar[tm];
		for (int i = 0; i < tm; i++) {
			if (ExPar.contains(parNames[i])) {
				tablePars[i] = ExPar.get(parNames[i]);
			} else {
				System.out
						.println("DataDisplay.collectData() Undeclared parameter name: "
								+ parNames[i]);
				System.out.println(" --- This should never happen!");
				tablePars[i] = new ExPar(0, new ExParValue(0), "");
				ExPar.enter(parNames[i], tablePars[i]);
			}
		}
		for (int i = 0; i < dataFiles.length; i++) {
			ExParValueTable d = new ExParValueTable(dataFiles[i].getPath());
			int n = d.getRows();
			for (int j = 0; j < n; j++) {
				ExParValue[] a = (ExParValue[]) (d.row(j));
				for (int k = 0; k < dm; k++) {
					dataPars[k].push(a[k]);
				}
				ExParValue[] b = new ExParValue[tm];
				total_N++;
				if (Include.getFlag()) {
					included_N++;
					if (Exclude.getFlag()) {
						excluded_N++;
					} else {
						for (int k = 0; k < tm; k++) {
							b[k] = tablePars[k].getValue().getValue();
						}
						dataTable.addRow(b);
					}
				}
				for (int k = 0; k < dm; k++) {
					dataPars[k].pop();
				}
			}
		}
	}

	/**
	 * Compute the statistical data analysis as defined by the data processing
	 * object which implements this class. The data table used is the table
	 * which has been generated by the most recent call to one of the
	 * createDataTable() methods.
	 * 
	 * @param exDesign
	 *            the ExDesign object which contains this display object.
	 * @param dataDest
	 *            the data destination object which shows the computation
	 *            results.
	 */
	public int doProcessDataTable(ExDesign exDesign, DataDestination dataDest) {
		// System.out.println("DataDisplay.compute()");
		if (valid(dataTable)) {
			dataDestination = dataDest;
			String[] pn = getExDesignNode().getParNames();
			if (pn == null || pn.length == 0) {
				pn = dataTable.getColumnNames();
			}
			int r = processDataTable(exDesign, pn, dataTable);
		} else {
			System.out
					.println("DataDisplay.processDataTable(): data table empty.");
		}
		return 0;
	}

	/**
	 * Check whether the given table is a valid data table for this analysis
	 * object.
	 */
	protected boolean valid(ExParValueTable t) {
		return (t != null) && (t.size() > 0);
	}

	/**
	 * Process the given data table. This method must be implemented by every
	 * subclass. This is where the actual work is done.
	 * 
	 * @param exDesign
	 *            the experimental design tree which contained the data
	 *            processing list object which is the parent of this data
	 *            processing object.
	 * @param parNames
	 *            the argument list of this object's design file node.
	 * @param table
	 *            the data table.
	 */
	abstract protected int processDataTable(ExDesign exDesign,
			String[] parNames, ExParValueTable table);

	/**
	 * Send the results string given as an argument to the data destination for
	 * this display object.
	 * 
	 * @param a
	 *            a string which contains results of statistical computations.
	 *            By default this string should be HTML-formatted.
	 */
	protected void showResults(String a) {
		Debug.show(Debug.DATA,
				"DataDisplay.showResults() to file " + FileName.getString());
		dataDestination.showResults(FileName.getString(), a);
	}

	protected void storePlotData(StatEngine engine) {
		if (CreatePlotData.getFlag()) {
			String ext = ExPar.PlotDataFileExtension.getString();
			if (!ext.startsWith("."))
				ext = "." + ext;
			engine.printPlotData(
					dataDestination.destinationDirectory(
							ExPar.PlotDataDirectory).getPath(),
					PlotDataFilePrefix.getString(), ext);
		}
	}

	public String toString() {
		return (instanceName != null) ? instanceName : getClass().getName();
	}
}
