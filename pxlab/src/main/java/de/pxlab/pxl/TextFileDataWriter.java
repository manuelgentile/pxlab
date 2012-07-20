package de.pxlab.pxl;

import java.io.*;

import de.pxlab.util.StringExt;

/**
 * A Text file which consumes standard PXLab data nodes..
 * 
 * @author H. Irtel
 * @version 0.1.6
 * @see ExDesign
 */
/*
 * 
 * 02/26/02 added experiment log at the beginning and at the end of data
 * writing.
 * 
 * 2005/01/28 added factorial data writing
 * 
 * 2005/10/19 write data only if the data file is non-empty
 */
public class TextFileDataWriter extends DataWriter {
	protected BufferedWriter dataWriter;
	private String intendedFileName;
	private boolean storeData;

	/**
	 * Create a data writer which collects data and immediately writes them to
	 * the given data file.
	 * 
	 * @param fn
	 *            name of the data file.
	 */
	public TextFileDataWriter(String fn) {
		super();
		dataFileName = null;
		intendedFileName = fn;
		dataWriter = null;
		storeData = true;
		logSession("Start");
	}

	private void openDataWriter() {
		Debug.show(Debug.FILES, "TextFileDataWriter(): Open file "
				+ intendedFileName);
		try {
			dataWriter = new BufferedWriter(new FileWriter(intendedFileName));
			dataFileName = intendedFileName;
			if (!StringExt.nonEmpty(ExPar.DataFileName.getString())) {
				ExPar.DataFileName.set(dataFileName);
			}
		} catch (IOException iox) {
			new FileError("Can't open data file " + intendedFileName
					+ "\nData are not stored!");
			storeData = false;
		}
	}

	protected void logSession(String prefix) {
		ExPar.setDate();
		String fs = ExPar.SessionDescriptorFormat.getString();
		if (StringExt.nonEmpty(fs)) {
			try {
				File df = new File(intendedFileName);
				File sf = new File(df.getParent(), "experiment.log");
				BufferedWriter wr = new BufferedWriter(new FileWriter(
						sf.getPath(), true));
				String nl = System.getProperty("line.separator");
				wr.write(prefix + " " + fs + nl);
				wr.close();
			} catch (IOException iex) {
				new FileError(
						"Error while trying to write experiment log file!");
			}
		}
	}

	/**
	 * Deliver a data string to this DataWriter.
	 * 
	 * @param data
	 *            the data string to be written.
	 */
	public void store(ExDesignNode data) {
		// System.out.println("TextFileDataWriter.store() " + data);
		String dt = dataStringOf(data);
		if (dt != null) {
			if (storeData && (dataWriter == null))
				openDataWriter();
			if (storeData) {
				try {
					String nl = System.getProperty("line.separator");
					dataWriter.write(dt + nl);
					dataWriter.flush();
				} catch (IOException ioex) {
					new FileError("Errow while trying to write data string <"
							+ dt + "> to destination " + dataFileName);
				}
			}
		}
	}

	/**
	 * Signal this DataWriter object that data collection is complete.
	 * 
	 * @param status
	 *            the final status of the data source.
	 */
	public void dataComplete(int status) {
		if (storeData && (dataWriter != null)) {
			try {
				dataWriter.close();
				Debug.show(Debug.FILES,
						"TextFileDataWriter.dataComplete(): Closed file "
								+ dataFileName);
			} catch (IOException ioex) {
				new FileError(
						"Errow while trying to close the data destination "
								+ dataFileName);
			}
		}
		logSession("End");
	}
}
