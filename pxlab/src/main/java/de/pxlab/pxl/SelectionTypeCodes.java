package de.pxlab.pxl;

/**
 * Codes for defining selection sets.
 * 
 * @version 0.1.0
 * @see de.pxlab.pxl.display.PictureMatrix
 */
/*
 * 
 * 2007/05/16
 */
public interface SelectionTypeCodes {
	/** Allow unrestrained multiple selection. */
	public static final int MULTIPLE_SELECTION = 0;
	/** Allow selection of a single element from all. */
	public static final int SINGLE_SELECTION = 1;
	/** Allow selection of a single element in every row. */
	public static final int SINGLE_IN_ROW_SELECTION = 2;
}
