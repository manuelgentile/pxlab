package de.pxlab.pxl;

/**
 * Control codes for special input and output devices.
 * 
 * @version 0.1.0
 */
public interface DeviceControlCodes {
	/**
	 * Open the connection to the device. This command must be sent before any
	 * other data may be sent or collected.
	 */
	public static final int OPEN_DEVICE = 1;
	/** Close the connection to the device. */
	public static final int CLOSE_DEVICE = 2;
	/** Reset the device to a predefined state. */
	public static final int RESET_DEVICE = 3;
	/** Send data to the device. */
	public static final int SEND_DATA = 4;
}
