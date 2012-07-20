package de.pxlab.pxl;

/**
 * Codes for stripe patterns.
 * 
 * @version 0.1.0
 */
public interface StripePatternCodes {
	/** The reference position is at the base line and the left edge. */
	public static final int REPETITION_PATTERN = 0;
	/** The reference position is at the middle height and the left edge. */
	public static final int RANDOM_PATTERN = 1;
	/** The reference position is at the top line and the left edge. */
	public static final int SHIFT_PATTERN = 2;
}
