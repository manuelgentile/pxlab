package de.pxlab.pxl.calib;

import java.awt.*;

abstract public class ColorMeasurementDevice {
	public static final String EYEONE = "GretagMacbeth EyeOne";
	public static final String TEKTRONIXJ1810 = "Tektronix J1810";
	public static final String SIMULATION = "Simulation Device";
	public static final String EYEONE_SHORT = "Ey";
	public static final String EYEONE_SHORT2 = "Gr";
	public static final String TEKTRONIXJ1810_SHORT = "Te";
	public static final String SIMULATION_SHORT = "Si";
	public static final int CALIBRATE = 100;
	public static final int MEASUREMENT_MODE = 200;
	public static final int REFLECTANCE = 201;
	public static final int EMITTANCE = 202;
	public static final int OBSERVER = 300;
	public static final int TWO_DEGREE = 301;
	public static final int TEN_DEGREE = 302;
	public static final int COLOR_SPACE = 400;
	public static final int CIE_xyY = 401;
	public static final int CIE_XYZ = 402;
	public static final int CIE_LAB = 403;

	/**
	 * Open the connection to the measurement device.
	 * 
	 * @return true if the connection is successfully opened.
	 */
	abstract public boolean connect();

	/**
	 * Return some descriptive information about this color measurement device.
	 * 
	 * @return a string which contains the information.
	 */
	abstract public String getInfo();

	/** Close the connection to the measurement device. */
	public void close() {
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
	abstract public String control(int ctrl, int value);

	/**
	 * Initiate a calibration cycle and wait until calibration is done.
	 * 
	 * @return true if calibration was successful.
	 */
	abstract public boolean calibrate();

	/**
	 * Set up the device such that it is ready to measure reflectance spectra.
	 * If necessary then the device initiates a calibration cycle before
	 * starting measurement.
	 * 
	 * @return true if the device has been set up successfully.
	 */
	abstract public boolean setSpectralReflectanceMode();

	/**
	 * Set up the device such that it is ready to measure CIE xyL chromaticity
	 * and luminance values of an emitting light source like a CRT. If necessary
	 * then the device initiates a calibration cycle before starting
	 * measurement.
	 * 
	 * @return true if the device has been set up successfully.
	 */
	abstract public boolean setTristimulusEmittanceMode();

	/**
	 * Trigger an intensity measurement cycle and return the data. The intensity
	 * scale used may depend of the measurement device's current state.
	 * 
	 * @return a double value which contains the data.
	 */
	public double getIntensity() {
		return 0.0;
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
		return null;
	}

	/**
	 * Trigger a spectral measurement cycle and return the data.
	 * 
	 * @return the spectral distribution which has been measured.
	 */
	public float[] getSpectrum() {
		return null;
	}

	public int getLowestWavlength() {
		return 380;
	}

	public int getHighestWavlength() {
		return 730;
	}

	public int getWavlengthStep() {
		return 10;
	}
}
