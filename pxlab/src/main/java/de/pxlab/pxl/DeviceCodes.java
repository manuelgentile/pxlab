package de.pxlab.pxl;

/**
 * Codes for input and output devices which have to be controlled explicitly.
 * Most of these need special libraries to be supported. Not all of them are
 * already implemented.
 * 
 * @version 0.1.0
 */
public interface DeviceCodes {
	/** A device connected to the serial port control lines. */
	public static final int SERIAL_PORT = 1;
	/** A device connected to the serial port communication lines. */
	public static final int SERIAL_LINE = 2;
	/** A device connected to the parallel port. */
	public static final int PARALLEL_PORT = 3;
	/** A device connected to the game port. */
	public static final int GAME_PORT = 4;
	/** A DirectInput device. */
	public static final int DI_DEVICE = 5;
	/** A gamepad connected to a USB port. */
	public static final int GAMEPAD = 6;
	/** A joystick connected to a USB port. */
	public static final int JOYSTICK = 7;
	/** A rumble pad connected to a USB port. */
	public static final int RUMBLE_PAD = 8;
	/**
	 * A SpaceMouse device connected to the USB port using the DirectInput
	 * access method.
	 */
	public static final int DI_SPACE_MOUSE = 9;
	/**
	 * A SpaceMouse device connected to the USB port using the 3DConnexion
	 * direct driver access method.
	 */
	public static final int SPACE_MOUSE = 20;
}
