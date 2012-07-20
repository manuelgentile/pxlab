package de.pxlab.pxl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.*;
import java.util.*;

import de.pxlab.util.*;

/**
 * This abstract class describes objects which collect the data information of a
 * single session.
 * 
 * @author H. Irtel
 * @version 0.2.7
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
 */
abstract public class DataWriter {
	/** Name of the file for single trial formatted data. */
	protected String dataFileName = null;
	protected String dataFileHeader = null;

	/**
	 * Deliver a data string to this DataWriter.
	 * 
	 * @param data
	 *            the data string to be written.
	 */
	abstract public void store(ExDesignNode data);

	/**
	 * Signal this DataWriter object that data collection is complete.
	 * 
	 * @param status
	 *            the final status of the data source.
	 */
	abstract public void dataComplete(int status);

	public boolean showResults(String s) {
		return false;
	}

	public void setDataFileHeader(String dfh) {
		dataFileHeader = dfh;
	}

	public String getDataFileHeader() {
		return dataFileHeader;
	}

	public String getFileName() {
		return dataFileName;
	}

	/**
	 * Create a single trial data output string for the given node. If
	 * ExPar.DataFileTrialFormat is defined then it is used for the output
	 * format. If it is not defined then the factor levels are used as an output
	 * format. If no factors are defined then the trial arguments are shown.
	 */
	public static String dataStringOf(ExDesignNode data) {
		String dt = null;
		if (data.isTrial()) {
			dt = ExPar.DataFileTrialFormat.getString();
		} else if (data.isBlock()) {
			dt = ExPar.DataFileBlockFormat.getString();
		}
		if (StringExt.nonEmpty(dt)) {
			// we have a format, so we are done
		} else {
			// no format yet, so try to create one from String array
			// ExPar.FactorialDataFormat
			if (data.isTrial()) {
				// for trials only
				String[] fd = ExPar.FactorialDataFormat.getStringArray();
				StringBuffer b = new StringBuffer(80);
				if (StringExt.nonEmpty(fd[0]) && (fd.length > 1)) {
					// seems that factors have been defined
					for (int i = 0; i < fd.length; i++) {
						// include only those trial parameters, which
						// actually are contained in the current
						// trial argument list
						if (fd[i].startsWith("Trial:")
								&& (fd[i].indexOf(data.getName()) < 0)) {
							// System.out.println("DataWriter.dataStringOf()   ignore "
							// + fd[i]);
						} else {
							// System.out.println("DataWriter.dataStringOf()   store "
							// + fd[i]);
							if (i > 0) {
								b.append("\t");
							}
							String s = ExPar.get(fd[i]).getValue().getValue()
									.toString();
							if (s.startsWith("\"") && (s.endsWith("\"")))
								s = StringExt.unquote(s);
							b.append(s);
						}
					}
					dt = b.toString();
				} else {
					// no factors have been defined, so simply use the trial
					// arguments
					fd = data.getParNames();
					if (fd != null && fd.length > 0) {
						for (int i = 0; i < fd.length; i++) {
							// System.out.println("DataWriter.dataStringOf()   store "
							// + fd[i]);
							if (i > 0) {
								b.append("\t");
							}
							String s = ExPar.get(fd[i]).getValue().getValue()
									.toString();
							if (s.startsWith("\"") && (s.endsWith("\"")))
								s = StringExt.unquote(s);
							b.append(s);
						}
						dt = b.toString();
					}
				}
			} else {
				// if not a trial and empty then set it null: no data output
				dt = null;
			}
		}
		return dt;
	}

	/**
	 * Embed the given content string into a HTML document.
	 * 
	 * @param content
	 *            a string which contains the file content.
	 * @return a string which is a valid HTML file with the proper start and end
	 *         tags.
	 */
	public static String createHTMLDocument(String content) {
		return createHTMLDocument(null, content);
	}

	/**
	 * Embed the given content string into a HTML document.
	 * 
	 * @param header
	 *            a string which is used for the HTML file title line and is
	 *            also inserted as a header in front of the text.
	 * @param content
	 *            a string which contains the file content.
	 * @return a string which is a valid HTML file with the proper start and end
	 *         tags.
	 */
	public static String createHTMLDocument(String header, String content) {
		StringBuffer s = new StringBuffer(1000);
		String nl = System.getProperty("line.separator");
		s.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">"
				+ nl);
		s.append("<HTML>" + nl + "<HEAD>" + nl + "<TITLE>PXLab Document");
		if (header != null)
			s.append(" - " + header);
		// s.append("</TITLE>" + nl + "</HEAD>" + nl +
		// "<BODY BGCOLOR=\"#E0E0E0\">" + nl);
		s.append("</TITLE>" + nl + "</HEAD>" + nl + "<BODY>" + nl);
		s.append("<FONT face=\"Arial,Helvetica\">" + nl);
		if (header != null)
			s.append("<H2>" + header + "</H2>" + nl);
		s.append(content);
		s.append(nl
				+ "<hr>"
				+ nl
				+ "<small>Created by <a href=\"http://www.pxlab.de\">PXLab</a> Version "
				+ Version.getMajor()
				+ "."
				+ Version.getMinor()
				+ " on "
				+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.SHORT,
						/* Locale.getDefault() */Locale.US).format(
						new java.util.Date()) + ".</small>" + nl + "</FONT>"
				+ nl + "</BODY>" + nl + "</HTML>" + nl);
		return s.toString();
	}
}
