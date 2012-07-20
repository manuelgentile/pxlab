package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Controls external input and output devices.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/02/02
 */
public class DeviceControl extends Display implements DeviceCodes,
		DeviceControlCodes {
	/**
	 * Device type. Possible device types are listed in class
	 * de.pxlab.pxl.DeviceCodes.
	 */
	public ExPar DeviceType = new ExPar(GEOMETRY_EDITOR, DeviceCodes.class,
			new ExParValueConstant("de.pxlab.pxl.DeviceCodes.DI_DEVICE"),
			"Device type code");
	/**
	 * Device ID number. This id is used if there may be more than a single
	 * device of the given DeviceType connected to the PC. An example are
	 * multiple gamepads which will be numbered from 0, 1, ...
	 */
	public ExPar DeviceID = new ExPar(SMALL_INT, new ExParValue(0),
			"Device ID number");
	/**
	 * Port description for the device connection. Serial devices need this most
	 * other devices do not need this. Possible names are 'COMn' under Windows
	 * with 'n' replaced by the serial port number (default: 'COM1' or 'COM2')
	 * or 'Serial A' or 'Serial B' on Sun unix machines.
	 */
	public ExPar Port = new ExPar(STRING, new ExParValue("COM1"),
			"Device port name");
	/**
	 * Delay between polling cycles for polled devices. Slows down polling and
	 * decreases timing resolution.
	 */
	public ExPar PollingDelay = new ExPar(DURATION, new ExParValue(0),
			"Delay between polling cycles");
	/**
	 * An assumption about the maximum axis value created by axis type devices.
	 * The AxisLimit should be set such that axis values are in the range [-1.0,
	 * 1.0].
	 */
	public ExPar AxisLimit = new ExPar(DOUBLE, new ExParValue(1.0),
			"Assumption about the maximum axis value");
	/**
	 * Command to be sent to the device. Possible command codes are defined in
	 * class de.pxlab.pxl.
	 */
	public ExPar CommandCode = new ExPar(GEOMETRY_EDITOR,
			DeviceControlCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.DeviceControlCodes.OPEN_DEVICE"),
			"Command code for the device");
	/**
	 * Command string to be sent to the device. Not all devices accept command
	 * contents. Most devices only accept command codes as defined by
	 * CommandCode.
	 */
	public ExPar Command = new ExPar(STRING, new ExParValue("!NEW\n"),
			"Device command string");

	public DeviceControl() {
		setTitleAndTopic("Device control", EXTERNAL_DSP | EXP);
		Timer.set(de.pxlab.pxl.TimerCodes.NO_TIMER);
	}

	public boolean isGraphic() {
		return false;
	}

	protected int create() {
		enterDisplayElement(new Clear(), group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		/*
		 * switch(DeviceType.getInt()) { case SERIAL_PORT: break; case
		 * SERIAL_LINE: break; case PARALLEL_PORT: break; case GAME_PORT: break;
		 * case GAMEPAD: break; case RUMBLE_PAD: break; case SPACE_MOUSE: break;
		 * }
		 */
	}

	public void showGroup() {
		show();
	}

	public void show(java.awt.Graphics g) {
		setGraphicsContext(g);
		show();
	}

	public void show() {
		switch (DeviceType.getInt()) {
		case SERIAL_PORT:
			switch (CommandCode.getInt()) {
			case OPEN_DEVICE:
				if (!presentationManager.openExternalControlBox(Port
						.getString())) {
					System.out
							.println("DeviceControl.show(): Can't open device!");
				} else {
					// System.out.println("SerialCommunicationDeviceControl.show(): device is open!");
				}
				break;
			case CLOSE_DEVICE:
				presentationManager.closeExternalControlBox();
				break;
			}
			break;
		case SERIAL_LINE:
			switch (CommandCode.getInt()) {
			case OPEN_DEVICE:
				if (!presentationManager.openSerialCommunicationDevice(Port
						.getString())) {
					System.out
							.println("DeviceControl.show(): Can't open device!");
				}
				break;
			case SEND_DATA:
				SerialCommunicationDevice s = presentationManager
						.getSerialCommunicationDevice();
				if (s != null) {
					s.send(Command.getString());
				} else {
					System.out
							.println("DeviceControl.show(): Device is missing! Can't send.");
				}
				break;
			case CLOSE_DEVICE:
				presentationManager.closeSerialCommunicationDevice();
				break;
			}
			break;
		case PARALLEL_PORT:
			break;
		case GAME_PORT:
			break;
		case DI_DEVICE:
		case GAMEPAD:
		case JOYSTICK:
		case RUMBLE_PAD:
		case DI_SPACE_MOUSE:
			switch (CommandCode.getInt()) {
			case OPEN_DEVICE:
				presentationManager.openPolledDevice(DeviceType.getInt(),
						DeviceID.getInt(), PollingDelay.getInt(),
						AxisLimit.getDouble());
				break;
			case RESET_DEVICE:
				presentationManager.resetPolledDevice(DeviceType.getInt(),
						DeviceID.getInt());
				break;
			case CLOSE_DEVICE:
				presentationManager.closePolledDevice(DeviceType.getInt(),
						DeviceID.getInt());
				break;
			}
			break;
		case SPACE_MOUSE:
			switch (CommandCode.getInt()) {
			case OPEN_DEVICE:
				presentationManager.openSpaceMouse(DeviceType.getInt(),
						DeviceID.getInt(), PollingDelay.getInt(),
						AxisLimit.getDouble());
				break;
			case RESET_DEVICE:
				presentationManager.resetSpaceMouse(DeviceType.getInt(),
						DeviceID.getInt());
				break;
			case CLOSE_DEVICE:
				presentationManager.closeSpaceMouse(DeviceType.getInt(),
						DeviceID.getInt());
				break;
			}
			break;
		}
	}
}
