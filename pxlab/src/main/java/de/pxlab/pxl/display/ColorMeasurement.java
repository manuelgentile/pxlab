package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Control a color measurement device for measuring color tristimulus values
 * from the screen. This display object does not change screen content. The
 * usual cycle is: OPEN, GET_TRISTIMULUS, CLOSE.
 * 
 * <p>
 * The display object MUST have its JustInTime parameter set to 1. The Timer
 * parameter should be set to NO_TIMER since all activity is done in real time.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.calib.ColorMeasurementDevice
 * @see de.pxlab.pxl.calib.ColorCalibrationTool
 */
/*
 * 
 * 2005/07/29
 */
public class ColorMeasurement extends Display {
	/** Color measurement device name. */
	public ExPar Device = new ExPar(STRING,
			new ExParValue("Simulation Device"),
			"Color measurement device name");
	/** Command code for the device. */
	public ExPar Command = new ExPar(
			GEOMETRY_EDITOR,
			ColorMeasurementCodes.class,
			new ExParValueConstant(
					"de.pxlab.pxl.ColorMeasurementCodes.OPEN_MEASUREMENT_DEVICE"),
			"Device command code");
	/** Data array measured from the device. */
	public ExPar Color = new ExPar(RTDATA, new ExParValue(0),
			"Measurement data");

	public boolean getCanPreload() {
		return false;
	}

	public ColorMeasurement() {
		setTitleAndTopic("Color measurement device control", EXTERNAL_DSP | EXP);
		JustInTime.set(1);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		removeDisplayElements(backgroundFieldIndex);
		backgroundFieldIndex = -1;
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		ColorMeasurementDeviceElement staticDisplayElement = (ColorMeasurementDeviceElement) RuntimeRegistry
				.get(Device.getString());
		if (staticDisplayElement == null) {
			staticDisplayElement = new ColorMeasurementDeviceElement();
			RuntimeRegistry.put(Device.getString(), staticDisplayElement);
			// System.out.println("ColorMeasurement.computeGeometry() created "
			// + Device.getString());
		} else {
			// System.out.println("ColorMeasurement.computeGeometry() reusing device "
			// + Device.getString());
		}
		if (nextDisplayElementIndex() == 0) {
			// System.out.println("ColorMeasurement.computeGeometry() enter device "
			// + Device.getString());
			enterDisplayElement(staticDisplayElement, group[0]);
			defaultTiming(0);
		}
		staticDisplayElement.setProperties(Device.getString(),
				Command.getInt(), Color);
	}
}
