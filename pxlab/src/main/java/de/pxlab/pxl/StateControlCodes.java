package de.pxlab.pxl;

/**
 * Codes for defining state control mechanisms.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface StateControlCodes {
	/** No state control parameter defined. */
	public static final int NO_CONTROL = 0;
	/**
	 * Set the state to STOP if the state control parameter is identical to the
	 * criterion value.
	 */
	public static final int STOP_ON_CRITERION = 1;
}
