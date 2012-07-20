package de.pxlab.pxl;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.text.*;

import de.pxlab.util.*;

import de.pxlab.pxl.gui.DescriptionFrame;

/**
 * This abstract class describes objects which collect the data information of a
 * single session.
 * 
 * @version 0.2.8
 * @see ExDesign
 */
/*
 * 
 * 02/05/04 Fixed bug which always showed the data twice.
 * 
 * 05/14/01 Fixed bug with data file name creation in applets
 * 
 * 2005/01/18 create and tell the most recent data file name.
 * 
 * 2005/10/19 write data only if the data file is non-empty
 * 
 * 2005/12/16 added store(String) method.
 * 
 * 2006/04/26 collect data processing results and use displayResults() to
 * actually show them after dataComplete() has been called.
 * 
 * 2007/03/16 localized processedDataFileName
 */
public class DataDestination {
	/** Name of the file for single trial formatted data. */
	protected String dataFileName = null;
	private String dataFileBase = null;
	/** Name of the file for complete data trees. */
	protected String dataTreeFileName = null;
	/**
	 * Contains the name of the most recent data file which contain data from
	 * the same subject we are currently running.
	 */
	protected static String mostRecentDataFileName = null;
	protected DataWriter trialDataWriter;

	/**
	 * Create a DataWriter for this run. We have these cases:
	 * 
	 * <ol>
	 * 
	 * <li>We are running as an Applet.
	 * 
	 * <ol>
	 * <li>The DataFileDestination parameter is defined and gives a valid URL.
	 * In this case we use the HTTP GET or POST (depending on the value of the
	 * global parameter HTTPRequestMethod) command to send the data string to
	 * the given URL as if it were a query string resulting from a HTML FORM
	 * element. We define two FORM subelements: 'DataFileName' gives the data
	 * file name as it is derived from the subject code and 'Data' is the
	 * HTML-coded formatted data output. Here is an example how a simple
	 * php-script may be used to process this query:
	 * 
	 * <pre>
	 * 	    &lt;? echo $DataFileName; ?&gt;
	 * 	    &lt;? echo $Data; ?&gt;
	 * </pre>
	 * 
	 * <li>The DataFileDestination parameter is undefined or is not a valid URL.
	 * In this case we open a new browser window and send the data string to
	 * this window after data collection is complete. This requires that the
	 * APPLET tag which executes the PXLab applet allows Java to JavaScript
	 * communication by setting the 'MAYSCRIPT'-parameter. The HTML-file which
	 * contains the APPLET tag also must define the following JavaScript
	 * function:
	 * 
	 * <pre>
	 * 	    &lt;SCRIPT&gt;
	 * 	        function showData(s) {
	 * 	            ergWin = window.open()
	 * 	            ergWin.document.write(s)
	 * 	        }
	 * 	    &lt;/SCRIPT&gt;
	 * </pre>
	 * 
	 * This function opens an empty browser window and then writes the data
	 * string to it. The method showData() is called by the DataWriter.
	 * 
	 * </ol>
	 * 
	 * <li>We are an Application. In this case the data are written to a file.
	 * If the DataFileDestination parameter is defined then this is the
	 * directory where the data file is created. If DataFileDestination is not
	 * defined then the data file is written to the current directory.
	 * 
	 * <ol>
	 * <li>The parameter 'DataFileName' is defined. This gives the name of the
	 * data file. Existing files with the same name are deleted.
	 * 
	 * <li>The parameter 'DataFileName' is not defined. Then the name of the
	 * data file is derived from the content of the 'SubjectCode' parameter. If
	 * this parameter is empty then the data are not stored at all. If the
	 * parameter is equal to its default value then the data are written to the
	 * respective file while overwriting any existing file with the same name.
	 * In all other cases the name of the file gets a numerical postfix if the
	 * file already exists.
	 * 
	 * </ol>
	 * </ol>
	 * 
	 * This constructor is called by the ExDesign method runExperiment().
	 */
	public DataDestination() {
		// System.out.println("DataWriter.create()");
		if (!ExPar.StoreData.getFlag() && !ExPar.StoreDataTree.getFlag()) {
			trialDataWriter = new NoDataWriter();
		}
		if (Base.isApplication()) {
			// System.out.println("ExRun.createDataWriter(): Application");
			// We are an application
			String dfn = applicationDataFilePath();
			// System.out.println("DataWriter.create(): " + dfn);
			if (dfn != null) {
				// System.out.println("ExRun.createDataWriter(): Write data to text file "
				// + dfn);
				dataFileName = dfn;
				trialDataWriter = new TextFileDataWriter(dataFileName);
			} else {
				// System.out.println("ExRun.createDataWriter(): data not stored.");
				trialDataWriter = new NoDataWriter();
			}
		} else {
			// System.out.println("ExRun.createDataWriter(): Applet");
			// We are an applet
			try {
				URL destURL = new URL(ExPar.DataFileDestination.getString());
				// We have a valid URL
				trialDataWriter = new URLDataWriter(destURL,
						appletDataFileName());
				// System.out.println("DataWriter.create(): Write data to URL "
				// + destURL);
			} catch (MalformedURLException mfe) {
				// We do not have a valid URL
				trialDataWriter = new BrowserWindowDataWriter(
						appletDataFileName());
				// System.out.println("DataWriter.create(): Browser Window Data Writer created.");
			}
		}
	}

	/**
	 * Create a data destination object for output of data processing results
	 * only. This constructor does not create a DataWriter for the execution of
	 * experiments. It is used by the constructor of class ExStat.
	 * 
	 * @param dfn
	 *            the name of the data file.
	 */
	public DataDestination(String dfn) {
		if (StringExt.nonEmpty(dfn)) {
			dataFileName = dfn;
		}
		trialDataWriter = new NoDataWriter();
	}

	/**
	 * Create a data file destination directory. The location is defined by the
	 * experimental parameter DataFileDestination.
	 * 
	 * @param subdirPar
	 *            name of a subdrirectory for the data files. If empty then the
	 *            location is defined by DataFileDestination. If nonempty then
	 *            the respective subdirectory is created and its File path
	 *            returned.
	 * @return the abstract path name of the destination directory.
	 */
	public static File destinationDirectory(ExPar subdirPar) {
		String dest = ExPar.DataFileDestination.getString();
		if ((dest == null) || (dest.length() == 0)) {
			// We have no destination, so use the current directory
			dest = ".";
		}
		File destDir = new File(dest);
		if (!destDir.isDirectory()) {
			String nl = System.getProperty("line.separator");
			new FileError("Invalid data file destination: " + dest + nl
					+ "Data are stored in the current working directory!");
			destDir = new File(".");
		}
		String subdir = subdirPar.getString();
		if (StringExt.nonEmpty(subdir)) {
			File sd = new File(destDir, subdir);
			if (sd.exists()) {
				if (sd.isDirectory()) {
					// OK
					destDir = sd;
				} else {
					// file exists, but is not a directory
					new FileError("Invalid data file subdirectory: " + subdir
							+ " exists but is not a directory!");
				}
			} else {
				if (sd.mkdirs()) {
					destDir = sd;
				} else {
					new FileError(
							"Invalid data file subdirectory: Can't create "
									+ subdir);
				}
			}
		}
		return destDir;
	}

	/**
	 * Create the data file name from the parameter DataFileDestination and the
	 * parameter DataFileName or the subject code for experiments running as an
	 * application. We always try to create a unique data file name with
	 * incremental numbers added to the subject code. This requires that the
	 * parameter <code>DataFileDestination</code> specifies a valid directory
	 * name on the local file system.
	 * 
	 * @return a string which can be used as data file name int the local file
	 *         system.
	 */
	private String applicationDataFilePath() {
		String dataFilePath = null;
		// Get destination directory
		File destDir = destinationDirectory(ExPar.TrialDataDirectory);
		String fileName = ExPar.DataFileName.getString();
		if (StringExt.nonEmpty(fileName)) {
			// If the data file name is explicitly defined then we always use it
			File f = new File(destDir, fileName);
			dataFilePath = f.getPath();
			dataFileBase = FileExt.getBase(fileName);
		} else {
			// Data file name is not explicitly defined
			String scode = ExPar.SubjectCode.getString();
			dataFileBase = scode;
			String ext = ExPar.DataFileExtension.getString();
			if ((scode != null) && (scode.length() > 0)) {
				// We have a valid subject code
				fileName = scode + ext;
				File f = new File(destDir, fileName);
				if (!scode.equals(Base.demoSubjectCode)) {
					// For non-default subject codes we increment
					// the id-number until we find a new file name
					int i = 1;
					if (f.exists()) {
						NumberFormat idf = NumberFormat.getInstance(Locale.US);
						idf.setMinimumIntegerDigits(3);
						while (f.exists()) {
							mostRecentDataFileName = f.getPath();
							dataFileBase = scode + idf.format(i++);
							f = new File(destDir, dataFileBase + ext);
						}
					}
				}
				dataFilePath = f.getPath();
			} else {
				// We have no subject code, so create a default data file
				// without directory prefix
				dataFileBase = Base.demoSubjectCode;
				File f = new File(dataFileBase + ext);
				if (!f.exists() || f.canWrite()) {
					dataFilePath = f.getName();
				} else {
					// return null if we can't write to this file
				}
			}
			// System.out.println("ExRun.createDataFilePath() path is " +
			// dataFilePath);
		}
		destDir = destinationDirectory(ExPar.DataTreeDirectory);
		dataTreeFileName = new File(destDir, dataFileBase
				+ ExPar.DataTreeFileExtension.getString()).getPath();
		Debug.show(Debug.FILES, "DataDestination.applicationDataFilePath()");
		Debug.show(Debug.FILES, "               Trial data file path = "
				+ dataFilePath);
		Debug.show(Debug.FILES, "                Data tree file path = "
				+ dataTreeFileName);
		return dataFilePath;
	}

	/**
	 * Return the most recent data file name which contains data from the
	 * current subject.
	 * 
	 * @return a string containing the file name or null if no data files exist
	 *         for the current subject.
	 */
	public String getMostRecentDataFileName() {
		String fn = mostRecentDataFileName;
		if (fn != null) {
			int i = fn.lastIndexOf(ExPar.DataFileExtension.getString());
			fn = fn.substring(0, i) + ExPar.DataTreeFileExtension.getString();
			File f = new File(fn);
			if (!f.exists()) {
				System.out
						.println("DataWriter.getMostRecentDataFileName(): File "
								+ fn + " should exist, but does not.");
				fn = null;
			}
		}
		return fn;
	}

	/**
	 * Send data from a single node to the data destination.
	 * 
	 * @param data
	 *            the data node to be sent.
	 */
	public void store(ExDesignNode data) {
		trialDataWriter.store(data);
	}

	/**
	 * Signal the data destination that data collection is complete.
	 * 
	 * @param status
	 *            the final status of the data source.
	 */
	public void dataComplete(int status) {
		trialDataWriter.dataComplete(status);
		displayResults();
	}

	public void setDataFileHeader() {
		String dfh = ExPar.DataFileHeader.getString();
		if (StringExt.nonEmpty(dfh)) {
		} else {
			String[] fd = ExPar.FactorialDataFormat.getStringArray();
			if (StringExt.nonEmpty(fd[0])) {
				StringBuffer b = new StringBuffer(80);
				for (int i = 0; i < fd.length; i++) {
					if (i > 0) {
						b.append("\t");
					}
					b.append(fd[i]);
				}
				dfh = b.toString();
			}
		}
		trialDataWriter.setDataFileHeader(dfh);
	}

	public void storeDataTree(ExDesignNode data) {
		if (dataTreeFileName != null) {
			data.store(dataTreeFileName);
		}
	}

	/**
	 * Store the data tree to the given file after correcting the file
	 * extension.
	 * 
	 * @param filePath
	 *            full filename and path where to store the tree. The extension
	 *            of this path is removed and replaced by the value of the
	 *            parameter DataTreeFileExtension.
	 */
	/*
	 * public void storeDataTree(String filePath) { }
	 */
	/**
	 * Create the data file name from the subject code for experiments running
	 * as an applet.
	 * 
	 * @return a string which can be used as data file name.
	 */
	private static String appletDataFileName() {
		// If the data file name is explicitly defined then we use it
		String fileName = ExPar.DataFileName.getString();
		if ((fileName == null) || (fileName.length() == 0)) {
			String scode = ExPar.SubjectCode.getString();
			String ext = ExPar.DataFileExtension.getString();
			if ((scode != null) && (scode.length() > 0)) {
				// We have a valid subject code
				fileName = scode + ext;
			} else {
				// We have no subject code, so create a default data file
				// without directory prefix
				fileName = Base.demoSubjectCode + ext;
			}
		}
		return (fileName);
	}
	private ArrayList resultStrings = null;
	private boolean resultsInWindow = false;

	/**
	 * Show the results of some data analysis computations. By default the
	 * results will be in HTML format.
	 * 
	 * @param fn
	 *            the file name defined in the DataDisplay object's FileName
	 *            parameter.
	 * @param a
	 *            a String which contains the complete results to be shown.
	 */
	public void showResults(String fn, String a) {
		if (resultsInWindow || !Base.isApplication()) {
			if (resultStrings == null) {
				resultStrings = new ArrayList(5);
			}
			resultStrings.add(a);
		} else {
			if (Base.isApplication()) {
				String processedDataFileName = Base.getProcessedDataFileName();
				if (processedDataFileName == null) {
					File destDir = destinationDirectory(ExPar.ProcessedDataDirectory);
					if (StringExt.nonEmpty(fn)) {
						processedDataFileName = new File(destDir, fn).getPath();
					} else {
						processedDataFileName = new File(destDir, dataFileBase
								+ ExPar.ProcessedDataFileExtension.getString())
								.getPath();
					}
				}
				File f = new File(processedDataFileName);
				try {
					Debug.show(Debug.FILES,
							"DataDisplay.showResults(): Open file "
									+ processedDataFileName);
					PrintWriter ps = new PrintWriter(new FileOutputStream(f,
							true));
					ps.print(a);
					ps.close();
					Debug.show(Debug.FILES,
							"DataDisplay.showResults(): Closed file "
									+ processedDataFileName);
				} catch (IOException ioex) {
					new FileError(
							"DataDisplay.showResults(): Error writing file "
									+ processedDataFileName);
				}
			} else {
				// we are an applet
			}
		}
	}

	private void displayResults() {
		if (resultStrings != null) {
			DescriptionFrame df = new DescriptionFrame("PXLab " + toString());
			StringBuffer b = new StringBuffer(10000);
			for (int i = 0; i < resultStrings.size(); i++) {
				b.append(resultStrings.get(i));
			}
			df.addText(DataWriter.createHTMLDocument(b.toString()));
		}
	}
}
