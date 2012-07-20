package de.pxlab.pxl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * A data writer which collects its data until data collection is finished. It
 * then sends all the collected data to the system clipboard.
 * 
 * @author H. Irtel
 * @version 0.1.3
 * @see ExDesign
 */
/*
 * 
 * 2005/10/19 write data only if the data file is non-empty
 */
public class ClipboardDataWriter extends CollectingDataWriter implements
		ClipboardOwner {
	/**
	 * Create A data writer which collects its data until data collection is
	 * finished and then sends all the collected data to the system clipboard.
	 */
	public ClipboardDataWriter() {
		super();
	}

	/**
	 * Signal this DataWriter object that data collection is complete. The data
	 * are then sent to the system clipoard.
	 * 
	 * @param status
	 *            the final status of the data source.
	 */
	public void dataComplete(int status) {
		if (hasData) {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection s = new StringSelection(data.toString());
			cb.setContents(s, this);
			System.out.println("ClipboardDataWriter.dataComplete():\n" + data);
		}
	}

	public void lostOwnership(Clipboard clp, Transferable tf) {
	}
}
