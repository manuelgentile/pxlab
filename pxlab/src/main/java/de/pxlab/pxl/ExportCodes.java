package de.pxlab.pxl;

/**
 * Codes for exporting statistical data tables.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see DataProcessor
 * @see DataDisplay
 */
/*
 * 
 * 2006/01/10
 */
public interface ExportCodes {
	/** Export raw data table. */
	public static final int RAW_DATA = 0;
	/**
	 * Export factorial data table with repetitions replaced by their means.
	 */
	public static final int FACTORIAL_DATA = 1;
	/**
	 * Export a data table containing the frequency of each factor level
	 * combination.
	 */
	public static final int FACTORIAL_FREQUENCY_DATA = 2;
	/**
	 * Export a data table which contains a single row for every subject as it
	 * is required for repeated measures analysis in SYSTAT or SPSS.
	 */
	public static final int REPEATED_MEASURES_DATA = 3;
	/**
	 * Export a data table which takes the first and second factor as cross
	 * table factors. The first factor's levels are contained in the rows and
	 * the second factor's levels are contained in the columns of the output
	 * table.
	 */
	public static final int CROSS_TABLE_DATA = 4;
}
