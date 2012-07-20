package de.pxlab.pxl;

/**
 * Codes for defining fixation marks.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface FixationCodes {
	/** Don't show a fixation mark. */
	public static final int NO_FIXATION = 0;
	/** Show a fixation cross centered at the given position. */
	public static final int FIXATION_CROSS = 1;
	/** Show a fixation dot centered at the given position. */
	public static final int FIXATION_DOT = 2;
	/** Mark the corners of the given rectangle. */
	public static final int CORNER_MARKS = 3;
}
