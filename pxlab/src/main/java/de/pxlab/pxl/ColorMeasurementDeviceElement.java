package de.pxlab.pxl;

import de.pxlab.pxl.calib.*;
import de.pxlab.pxl.device.*;

/* Controls a color measurement device. Commands are available for
 opening and closing the device and for getting measurement data. 

 @author H. Irtel
 @version 0.1.0
 @see de.pxlab.pxl.calib.ColorMeasurementDevice
 @see de.pxlab.pxl.calib.ColorCalibrationTool

 */
public class ColorMeasurementDeviceElement extends DisplayElement implements
		ColorMeasurementCodes {
	private String deviceName;
	private int command;
	private ExPar dataPar;
	private ColorMeasurementDevice device;

	public ColorMeasurementDeviceElement() {
	}

	public void setProperties(String dev, int cmd, ExPar data) {
		deviceName = dev;
		command = cmd;
		dataPar = data;
	}

	public void show() {
		// System.out.println("ColorMeasurementDevice.show() command = " +
		// command);
		switch (command) {
		case OPEN_MEASUREMENT_DEVICE:
			open();
			break;
		case GET_TRISTIMULUS:
			dataPar.set(getTristimulus());
			break;
		case GET_SPECTRUM:
			dataPar.set(getSpectrum());
			break;
		case CLOSE_MEASUREMENT_DEVICE:
			close();
			break;
		}
		// System.out.println("ColorMeasurementDevice.show() data = " +
		// dataPar);
	}

	/**
	 * Open a color measurement device, set it to tristimulus emittance
	 * measurement mode and initiate calibration if necessary.
	 */
	private void open() {
		// System.out.println("ColorMeasurementDeviceElement.open() " +
		// deviceName);
		if (deviceName.toUpperCase().startsWith(
				ColorMeasurementDevice.EYEONE_SHORT.toUpperCase())
				|| deviceName.toUpperCase().startsWith(
						ColorMeasurementDevice.EYEONE_SHORT2.toUpperCase())) {
			device = new GretagMacbethEyeOne();
		} else if (deviceName.toUpperCase().startsWith(
				ColorMeasurementDevice.TEKTRONIXJ1810_SHORT.toUpperCase())) {
			device = new TektronixJ1810();
		} else {
			device = new SimulatedColorMeasurementDevice();
		}
		if (!device.connect()) {
			new ParameterValueError("Can't connect to device '" + deviceName
					+ "'. Using device simulation.");
			device = new SimulatedColorMeasurementDevice();
			device.connect();
		}
		// System.out.println(device.getInfo());
		device.setTristimulusEmittanceMode();
	}

	private double[] getTristimulus() {
		double[] d = device.getTristimulus();
		// System.out.println("[" + d[0] + ", " + d[1] + ", " + d[2] + "]");
		return d;
	}

	private double[] getSpectrum() {
		float[] f = device.getSpectrum();
		double[] d = new double[f.length];
		for (int i = 0; i < f.length; i++)
			d[i] = f[i];
		return d;
	}

	private void close() {
		device.close();
	}
}
