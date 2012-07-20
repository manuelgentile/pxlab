package de.pxlab.pxl;

import de.pxlab.util.StringExt;

/**
 * A data writer which collects its data until data collection is finished.
 * 
 * @author H. Irtel
 * @version 0.2.1
 * @see ExDesign
 */
/*
 * 
 * 2005/10/19 write data only if the data file is non-empty
 */
abstract public class CollectingDataWriter extends DataWriter {
	protected StringBuffer data;
	protected String lineBreak;
	protected boolean hasData;

	/**
	 * Create A data writer which collects its data until data collection is
	 * finished and then sends it to some data consumer.
	 */
	public CollectingDataWriter() {
		data = new StringBuffer(1000);
		String nl = System.getProperty("line.separator");
		lineBreak = nl;
		hasData = false;
	}

	/**
	 * Deliver a data node to this DataWriter.
	 * 
	 * @param d
	 *            the experimental design node which should be stored.
	 */
	public void store(ExDesignNode d) {
		String dt = dataStringOf(d);
		if (dt != null) {
			data.append(dt + lineBreak);
			hasData = true;
		}
	}
}
