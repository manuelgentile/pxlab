package de.pxlab.pxl;

/**
 * Codes for defining position references.
 * 
 * @version 0.2.0
 * @see TextElement
 * @see TextParagraphElement
 */
public interface PositionReferenceCodes {
	/*
	 * Reference point values for text positioning. Do NEVER change these since
	 * their values are expected to be those defined here.
	 */
	/** The reference position is at the base line and the left edge. */
	public static final int BASE_LEFT = 0;
	/** The reference position is at the middle height and the left edge. */
	public static final int MIDDLE_LEFT = 1;
	/** The reference position is at the top line and the left edge. */
	public static final int TOP_LEFT = 2;
	/** The reference position is at the base line and the horizontal center. */
	public static final int BASE_CENTER = 3;
	/**
	 * The reference position is at the middle height and the horizontal center.
	 */
	public static final int MIDDLE_CENTER = 4;
	/** The reference position is at the top line and the horizontal center. */
	public static final int TOP_CENTER = 5;
	/** The reference position is at the base line and the right edge. */
	public static final int BASE_RIGHT = 6;
	/** The reference position is at the middle height and the right edge. */
	public static final int MIDDLE_RIGHT = 7;
	/** The reference position is at the top line and the right edge. */
	public static final int TOP_RIGHT = 8;
}
