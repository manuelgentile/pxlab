package de.pxlab.pxl;

/**
 * Codes for pointer format of rating scales.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 2006/06/28
 * 
 * 2007/06/15 derived from ResponseScalePointerCodes
 */
public interface RatingScalePointerCodes {
	/** Don't show a pointer. */
	public static final int NO_POINTER = 0;
	/** A downward pointer. */
	public static final int DOWN_POINTER = 1;
	/** An upward pointer. */
	public static final int UP_POINTER = 2;
	/** A double pointer. */
	public static final int DOUBLE_POINTER = 3;
	/** A cross pointer. */
	public static final int CROSS_POINTER = 4;
	/** A tick line pointer. */
	public static final int LINE_POINTER = 5;
}
