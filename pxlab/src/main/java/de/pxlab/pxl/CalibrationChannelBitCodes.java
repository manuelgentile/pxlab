package de.pxlab.pxl;

/**
 * Codes to identify the color channels of a color device for calibration.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface CalibrationChannelBitCodes {
	public static final int RED_CHANNEL = 1;
	public static final int GREEN_CHANNEL = 2;
	public static final int BLUE_CHANNEL = 4;
	public static final int YELLOW_CHANNEL = RED_CHANNEL | GREEN_CHANNEL;
	public static final int CYAN_CHANNEL = GREEN_CHANNEL | BLUE_CHANNEL;
	public static final int MAGENTA_CHANNEL = RED_CHANNEL | BLUE_CHANNEL;
	public static final int WHITE_CHANNEL = RED_CHANNEL | GREEN_CHANNEL
			| BLUE_CHANNEL;
}
