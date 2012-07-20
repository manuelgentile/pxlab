package de.pxlab.pxl.data;

import de.pxlab.pxl.*;
import de.pxlab.stat.*;

/**
 * Create a header for a data analysis output file.
 * 
 * @version 0.1.0
 */
public class AnalysisFileHeader extends DataDisplay {
	/** Constructor creating the title of the display. */
	public AnalysisFileHeader() {
		setTitleAndTopic("Data Analysis File Header", Topics.DATA);
	}

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable grandTable) {
		String title = "<" + StatEngine.header1 + ">" + Header.getString()
				+ "</" + StatEngine.header1 + ">\n";
		showResults(title);
		return 0;
	}
}
