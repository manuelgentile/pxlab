package de.pxlab.pxl.device;

import java.awt.*;
import com.gretagmacbeth.eyeone.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.calib.*;

/**
 * An interface to the GretagMacbeth Eye-One color measurement device library.
 * See <a href="http://www.i1color.com">the Eye-One WWW page</a> for more
 * information about the Eye-One family of measurement devices. The device
 * itself is connected to the PC via the USB.
 */
public class GretagMacbethEyeOne extends ColorMeasurementDevice {
	private EyeOne i1;

	/**
	 * Open the connection to the measurement device.
	 * 
	 * @return true if the connection is successfully opened.
	 */
	public boolean connect() {
		boolean retVal = false;
		try {
			System.out
					.print("GretagMacbethEyeOne.connect() instantiate device ...");
			i1 = EyeOne.getInstance();
			System.out.println(" successful.");
			System.out
					.print("GretagMacbethEyeOne.connect() trying to connect ...");
			retVal = i1.isConnected();
			System.out.println(retVal ? " connected." : " failed.");
		} catch (EyeOneException eox) {
			eox.printStackTrace();
		}
		return retVal;
	}

	/**
	 * Return some descriptive information about this color measurement device.
	 * 
	 * @return a string which contains the information.
	 */
	public String getInfo() {
		StringBuffer info = new StringBuffer(200);
		String nl = System.getProperty("line.separator");
		info.append("GretagMacbeth Eye-One.");
		try {
			if (i1.isConnected()) {
				info.append(nl + "Device is connected.");
				info.append(nl + "SKD Version: " + i1.getOption("Version"));
				info.append(nl + "Device serial number: "
						+ i1.getOption("SerialNumber"));
				info.append(nl + "Measurement mode: "
						+ i1.getOption("MeasurementMode"));
			} else {
				info.append(nl + "Device is not connected.");
			}
		} catch (EyeOneException eox) {
			eox.printStackTrace();
		}
		return info.toString();
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
		String r = null;
		try {
			switch (ctrl) {
			case CALIBRATE:
				calibrate();
				break;
			case MEASUREMENT_MODE:
				switch (value) {
				case REFLECTANCE:
					i1.setOption("MeasurementMode", "SingleReflectance");
					break;
				case EMITTANCE:
					i1.setOption("MeasurementMode", "SingleEmission");
					break;
				}
				break;
			case OBSERVER:
				switch (value) {
				case TWO_DEGREE:
					i1.setOption("Colorimetric.Observer", "TwoDegree");
					break;
				case TEN_DEGREE:
					i1.setOption("Colorimetric.Observer", "TenDegree");
					break;
				}
				break;
			case COLOR_SPACE:
				switch (value) {
				case CIE_xyY:
					i1.setOption("ColorSpaceDescription.Type", "CIExyY");
					break;
				case CIE_XYZ:
					break;
				case CIE_LAB:
					break;
				}
				break;
			}
			r = "OK";
		} catch (EyeOneException eox) {
			eox.printStackTrace();
			System.out.println("GretagMacbethEyeOne.setMode() Error");
			r = "Error";
		}
		return r;
	}

	/**
	 * Set up the device such that it is ready to measure reflectance spectra.
	 * If necessary then the device initiates a calibration cycle before
	 * starting measurement.
	 * 
	 * @return true if the device has been set up successfully.
	 */
	public boolean setSpectralReflectanceMode() {
		boolean r = false;
		try {
			i1.setOption("MeasurementMode", "SingleReflectance");
			new CalibrationDialog(this);
			r = true;
		} catch (EyeOneException eox) {
			eox.printStackTrace();
			System.out.println("GretagMacbethEyeOne.setMode() Error");
		}
		return r;
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
		boolean r = false;
		try {
			i1.setOption("MeasurementMode", "SingleEmission");
			System.out
					.println("GretagMacbethEyeOne.setTristimulusEmittanceMode() mode set. Start calibration.");
			new CalibrationDialog(this);
			System.out
					.println("GretagMacbethEyeOne.setTristimulusEmittanceMode() calibrated.");
			i1.setOption("ColorSpaceDescription.Type", "CIExyY");
			r = true;
		} catch (EyeOneException eox) {
			eox.printStackTrace();
			System.out
					.println("GretagMacbethEyeOne.setTristimulusEmittanceMode() Error");
		}
		return r;
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
		double[] dt = null;
		// System.out.println("GretagMacbethEyeOne.getTristimulus()");
		try {
			i1.triggerMeasurement();
			float[] t = i1.getTriStimulus(EyeOne.SPOT_INDEX);
			dt = new double[3];
			dt[0] = t[2];
			dt[1] = t[0];
			dt[2] = t[1];
		} catch (EyeOneException eox) {
			System.out
					.println("GretagMacbethEyeOne.getTristimulus(): Measurement Failed!");
		}
		return dt;
	}

	/**
	 * Trigger a spectral measurement cycle and return the data.
	 * 
	 * @return the spectral distribution which has been measured.
	 */
	public float[] getSpectrum() {
		float[] sd = null;
		try {
			i1.triggerMeasurement();
			float[] s = i1.getSpectrum(EyeOne.SPOT_INDEX);
			sd = new float[s.length];
			System.arraycopy(s, 0, sd, 0, s.length);
		} catch (EyeOneException eox) {
			System.out
					.print("GretagMacbethEyeOne.getSpectrum(): Measurement Failed!");
		}
		return sd;
	}

	/**
	 * Initiate a calibration cycle and wait until calibration is done.
	 * 
	 * @return true if calibration was successful.
	 */
	public boolean calibrate() {
		boolean r = true;
		System.out.print("GretagMacbethEyeOne.calibrate() start ...");
		try {
			i1.calibrate();
			System.out.println(" finished.");
		} catch (EyeOneException eox) {
			System.out.println(" failed.");
			r = false;
			// eox.printStackTrace();
		}
		return r;
	}
}
