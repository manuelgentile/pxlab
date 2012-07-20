package de.pxlab.pxl;

// import java.util.ArrayList;
/**
 * An ExDesignDataProcessor gets the respective node data after the node has
 * been executed.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see ExDesign
 */
public interface ExDesignDataProcessor {
	/** Tell the data processor where to send its data. */
	public void setDataDestination(DataDestination ds);

	/**
	 * This method is called when all trials of a node have been run.
	 * 
	 * @param displayList
	 *            the DisplayList which will show the data.
	 * @param data
	 *            the ExDesignNode object filled with data.
	 */
	public void processData(DisplayList displayList, ExDesignNode data);
}
