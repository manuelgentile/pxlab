package de.pxlab.pxl.calib;

import java.awt.*;

import de.pxlab.pxl.PxlColor;

public class SimulatedColorMeasurementDevice extends ColorMeasurementDevice {
	private int mode;
	private Robot robot;
	private Dimension screenSize;

	/**
	 * Open the connection to the measurement device.
	 * 
	 * @return true if the connection is successfully opened.
	 */
	public boolean connect() {
		boolean retVal = false;
		try {
			robot = new Robot();
			retVal = true;
		} catch (AWTException aex) {
		}
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return retVal;
	}

	/** Return some information about the device. */
	public String getInfo() {
		return "Simlated Color Measurement Device";
	}

	/**
	 * Initiate a calibration cycle and wait until calibration is done.
	 */
	public boolean calibrate() {
		return true;
	}

	/**
	 * Send a control signal to the device.
	 * 
	 * @param ctrl
	 *            the type of control signal to be sent.
	 * @param value
	 *            the value of the control signal.
	 * @return a string which describes the device's response to the control
	 *         signal. The string "OK" generally indicates that the signal has
	 *         been processed successfully.
	 */
	public String control(int ctrl, int value) {
		return "OK";
	}

	/**
	 * Set up the device such that it is ready to measure reflectance spectra.
	 * If necessary then the device initiates a calibration cycle before
	 * starting measurement.
	 * 
	 * @return true if the device has been set up successfully.
	 */
	public boolean setSpectralReflectanceMode() {
		return true;
	}

	/**
	 * Set up the device such that it is ready to measure CIE xyL chromaticity
	 * and luminance values of an emitting light source like a CRT. If necessary
	 * then the device initiates a calibration cycle before starting
	 * measurement.
	 * 
	 * @return true if the device has been set up successfully.
	 */
	public boolean setTristimulusEmittanceMode() {
		return true;
	}

	/**
	 * Trigger an intensity measurement cycle and return the data. The intensity
	 * scale used may depend of the measurement device's current state.
	 * 
	 * @return a double value which contains the data.
	 */
	public double getIntensity() {
		double[] d = getTristimulus();
		return d[0];
	}

	/**
	 * Trigger a tristimulus value measurement cycle and return the data. The
	 * coordinate system used may depend of the measurement device's current
	 * state.
	 * 
	 * @return an array which contains the tristimulus data or null if this
	 *         method is not available.
	 */
	public double[] getTristimulus() {
		/*
		 * double[] t = new double[3]; Color c =
		 * robot.getPixelColor(screenSize.width/2, screenSize.height/2); t[0] =
		 * 21.0*Math.pow((c.getRed()/255.0), 2.4) +
		 * 72.0*Math.pow((c.getGreen()/255.0), 2.2) +
		 * 7.0*Math.pow((c.getBlue()/255.0), 2.6); t[1] = 0.313; t[2] = 0.329;
		 */
		PxlColor pc = new PxlColor(robot.getPixelColor(screenSize.width / 2,
				screenSize.height / 2));
		return pc.getYxyComponents();
	}

	/**
	 * Trigger a spectral measurement cycle and return the data.
	 * 
	 * @return the spectral distribution which has been measured.
	 */
	public float[] getSpectrum() {
		return null;
	}
}
