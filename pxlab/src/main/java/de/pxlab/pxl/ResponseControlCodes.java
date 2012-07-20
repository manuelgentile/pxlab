package de.pxlab.pxl;

/**
 * Response control codes for the ResponseControlCheck display object.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface ResponseControlCodes {
	/**
	 * Check whether any spurious response has been found during the checking
	 * interval.
	 */
	public static final int ANY_RESPONSE = 0;
	/**
	 * Look at the last response found in the critical interval and check
	 * whether it satisfies the critical condition.
	 */
	public static final int LAST_RESPONSE = 1;
	/**
	 * Look at the first response found in the critical interval and check
	 * whether it satisfies the critical condition.
	 */
	public static final int FIRST_RESPONSE = 2;
}
