package de.pxlab.pxl;

/**
 * Response code values. These are assigned to the global experimental parameter
 * ResponseCode or may be used to define the acceptable responses by defining
 * ResponseSet to an array of these values. Note that keyboard key codes are not
 * contained here. These are defined in KeyCodes.
 * 
 * @version 0.4.0
 * @see KeyCodes
 */
/*
 * 
 * 2004/12/16 Changed values for error codes such that all error codes are
 * larger than 9990.
 */
public interface ResponseCodes {
	public static final int CORRECT = 0;
	public static final int FALSE = 1;
	public static final int LEFT_MOUSE_BUTTON = KeyCodes.LEFT_BUTTON;
	public static final int MIDDLE_MOUSE_BUTTON = KeyCodes.MIDDLE_BUTTON;
	public static final int RIGHT_MOUSE_BUTTON = KeyCodes.RIGHT_BUTTON;
	public static final int MEDIA_BASE = 2000;
	public static final int SPACE_MOUSE_BASE = 3000;
	public static final int DI_DEVICE_BASE = 4000;
	public static final int SERIAL_PORT_BASE = 7000;
	public static final int PARALLEL_PORT_BASE = 8000;
	public static final int ERROR_BASE = 9000;
	public static final int TIME_OUT = ERROR_BASE + 997;
	public static final int ILLEGAL = ERROR_BASE + 998;
	public static final int UNKNOWN_RESPONSE = ERROR_BASE + 999;
	public static final int SYNC_MEDIA = MEDIA_BASE + 3;
	public static final int CLOSE_MEDIA = MEDIA_BASE + 5;
	/**
	 * If a polled device contains more than a single device id then this is the
	 * code shift per id.
	 */
	public static final int DI_DEVICE_ID_SHIFT = 1000;
	public static final int DI_DEVICE_DIRECTIONAL_OFFSET = 200;
	public static final int DI_DEVICE_DIRECTIONAL_SHIFT = 400;
	/** Response codes for SpaceMouse buttons. */
	public static final int SPACE_MOUSE_BUTTON1 = SPACE_MOUSE_BASE + 1;
	public static final int SPACE_MOUSE_BUTTON2 = SPACE_MOUSE_BASE + 2;
	public static final int SPACE_MOUSE_BUTTON3 = SPACE_MOUSE_BASE + 3;
	public static final int SPACE_MOUSE_BUTTON4 = SPACE_MOUSE_BASE + 4;
	public static final int SPACE_MOUSE_BUTTON5 = SPACE_MOUSE_BASE + 5;
	public static final int SPACE_MOUSE_BUTTON6 = SPACE_MOUSE_BASE + 6;
	public static final int SPACE_MOUSE_BUTTON7 = SPACE_MOUSE_BASE + 7;
	public static final int SPACE_MOUSE_BUTTON8 = SPACE_MOUSE_BASE + 8;
	/** Response codes for DirectInput buttons. */
	public static final int DI_BUTTON1 = DI_DEVICE_BASE + 1;
	public static final int DI_BUTTON2 = DI_DEVICE_BASE + 2;
	public static final int DI_BUTTON3 = DI_DEVICE_BASE + 3;
	public static final int DI_BUTTON4 = DI_DEVICE_BASE + 4;
	public static final int DI_BUTTON5 = DI_DEVICE_BASE + 5;
	public static final int DI_BUTTON6 = DI_DEVICE_BASE + 6;
	public static final int DI_BUTTON7 = DI_DEVICE_BASE + 7;
	public static final int DI_BUTTON8 = DI_DEVICE_BASE + 8;
	public static final int DI_DIRECTIONAL = DI_DEVICE_BASE
			+ DI_DEVICE_DIRECTIONAL_OFFSET;
	/**
	 * Response codes for response lines connected to the serial port input box.
	 * These correspond to the ResponseCode parameter values used to indicate
	 * which response button has been pressed.
	 */
	public static final int SERIAL_PORT_BUTTON1 = SERIAL_PORT_BASE + 1;
	public static final int SERIAL_PORT_BUTTON2 = SERIAL_PORT_BASE + 2;
	public static final int SERIAL_PORT_BUTTON3 = SERIAL_PORT_BASE + 3;
	public static final int SERIAL_PORT_BUTTON4 = SERIAL_PORT_BASE + 4;
	/**
	 * Response codes for response lines connected to a parallel port input box.
	 * These correspond to the ResponseCode parameter values used to indicate
	 * which response button has been pressed.
	 */
	public static final int PARALLEL_PORT_BUTTON1 = PARALLEL_PORT_BASE + 1;
	public static final int PARALLEL_PORT_BUTTON2 = PARALLEL_PORT_BASE + 2;
	public static final int PARALLEL_PORT_BUTTON3 = PARALLEL_PORT_BASE + 3;
	public static final int PARALLEL_PORT_BUTTON4 = PARALLEL_PORT_BASE + 4;
}
