package de.pxlab.pxl;

/**
 * Codes for printing of statistical data analysis objects.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/08/16
 */
public interface StatPrintBitCodes {
	/** Don't print anything. */
	public static final int PRINT_NOTHING = 0;
	/**
	 * Print data table only. This prints a data table which can be used as a
	 * starting point for calculations by hand.
	 */
	public static final int PRINT_DATA = 1;
	/** Print descriptive statistics. */
	public static final int PRINT_DESCRIPTIVE = 1 << 1;
	/** Print the results of statistical estimation and tests. */
	public static final int PRINT_RESULTS = 1 << 2;
	/** Print every detail which is available. */
	public static final int PRINT_DETAILS = 1 << 3;
	/** Print computation terms. */
	public static final int PRINT_TERMS = 1 << 4;
}
