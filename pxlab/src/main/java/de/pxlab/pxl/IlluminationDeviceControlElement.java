package de.pxlab.pxl;

/**
 * Controls an illumination device.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 03/13/03
 */
public class IlluminationDeviceControlElement extends DisplayElement implements
		IlluminationDeviceControlCodes {
	protected int commandCode = 0;
	protected int deviceSignature = ColorDeviceTransformCodes.OSRAM_COLOR_TUBES;
	private String key = "IlluminationDevice";

	/** Create an illumination device control element. */
	public IlluminationDeviceControlElement(ExPar c) {
		type = DisplayElement.ILLUMINATION_CONTROL;
		colorPar = c;
	}

	/** Set the device's command code for the next command. */
	public void setCommandCode(int code) {
		commandCode = code;
	}

	/** Set the device signature for identifying the device. */
	public void setDeviceSignature(int code) {
		deviceSignature = code;
	}

	/**
	 * Execute the device control command. Note that no action is executed if we
	 * are running as an applet.
	 */
	public void show() {
		if (Base.isApplication()) {
			IlluminationDevice id;
			switch (commandCode) {
			case IlluminationDeviceControlCodes.OPEN_ILLUMINATION_DEVICE:
				id = new IlluminationDevice(deviceSignature);
				RuntimeRegistry.put(key, id);
				id.set(colorPar.getPxlColor());
				break;
			case IlluminationDeviceControlCodes.SET_ILLUMINATION_DEVICE:
				id = (IlluminationDevice) RuntimeRegistry.get(key);
				if (id == null) {
					id = new IlluminationDevice(deviceSignature);
					RuntimeRegistry.put(key, id);
				}
				id.set(colorPar.getPxlColor());
				break;
			case IlluminationDeviceControlCodes.CLOSE_ILLUMINATION_DEVICE:
				id = (IlluminationDevice) RuntimeRegistry.get(key);
				if (id != null) {
					id.close();
				}
				break;
			}
		}
	}
}
