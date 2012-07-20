package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Controls an illumination device.
 * 
 * @version 0.1.0
 */
public class IlluminationDeviceControl extends Display implements
		IlluminationDeviceControlCodes {
	/** Illumination color. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.WHITE)), "Illumination Color");
	/** Command code for the device. */
	public ExPar CommandCode = new ExPar(
			GEOMETRY_EDITOR,
			IlluminationDeviceControlCodes.class,
			new ExParValueConstant(
					"de.pxlab.pxl.IlluminationDeviceControlCodes.SET_ILLUMINATION_DEVICE"),
			"Command code for the device");

	public IlluminationDeviceControl() {
		setTitleAndTopic("Illumination Device Control", EXTERNAL_DSP | EXP);
		Timer.set(de.pxlab.pxl.TimerCodes.NO_TIMER);
	}

	public boolean isGraphic() {
		return false;
	}
	protected IlluminationDeviceControlElement idc;

	protected int create() {
		// System.out.println("IlluminationDeviceControl.create()");
		idc = new IlluminationDeviceControlElement(Color);
		enterDisplayElement(idc, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		// System.out.println("IlluminationDeviceControl.computeGeometry()");
		idc.setCommandCode(CommandCode.getInt());
	}
}
