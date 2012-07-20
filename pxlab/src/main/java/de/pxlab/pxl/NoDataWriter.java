package de.pxlab.pxl;

/**
 * This data writer class consumes input but does not write anything.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see ExDesign
 */
public class NoDataWriter extends DataWriter {
	/**
	 * Deliver a data string to this DataWriter.
	 * 
	 * @param data
	 *            the data string to be written.
	 */
	public void store(ExDesignNode data) {
	}

	/**
	 * Signal this DataWriter object that data collection is complete.
	 * 
	 * @param status
	 *            the final status of the data source.
	 */
	public void dataComplete(int status) {
	}
}
