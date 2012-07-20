package de.pxlab.pxl;

/**
 * Codes for defining adaptive procedure stopping methods.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see AdaptiveControl
 */
/*
 * 09/13/01
 */
public interface AdaptiveStopCodes {
	/** Don't ever stop. */
	public static final int DONT_STOP = 0;
	/**
	 * Step when a certain number of turning points have been found.
	 */
	public static final int TURNPOINTS = 1;
	/**
	 * Step when a certain number of turning points at the minimum step size
	 * have been found.
	 */
	public static final int TURNPOINTS_AT_MINIMUM = 2;
	/** Step when the subject presses the stop key. */
	public static final int STOPKEY_RESPONSE = 3;
	// Modify AdaptiveControl.LastAdaptiveStopCode if you add another code!
}
