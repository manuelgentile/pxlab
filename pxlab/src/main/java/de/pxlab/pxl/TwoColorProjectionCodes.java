package de.pxlab.pxl;

/**
 * Codes for two color image projection mixture types.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface TwoColorProjectionCodes {
	/** Show a mixture of the short and long wave image. */
	public static final int MIXTURE = 0;
	/** Show the short wave image only. */
	public static final int SHORT_ONLY = 1;
	/** Show the long wave image only. */
	public static final int LONG_ONLY = 2;
}
