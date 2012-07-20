package de.pxlab.pxl.device;

import java.awt.*;
import javax.comm.*;

import de.pxlab.pxl.ExPar;
import de.pxlab.pxl.ExParValue;
import de.pxlab.pxl.ExParTypeCodes;

import de.pxlab.pxl.calib.ColorMeasurementDevice;

/**
 * A connection to the Tektronix Lumacolor II with J1810 colorimeter head. This
 * device can measure tristimulus color values and connects to the PC via the
 * serial port.
 */
public class TektronixJ1810 extends ColorMeasurementDevice implements
		SerialLineReceiver {
	protected SerialLineConnection serialLineConnection;
	/** Name of the serial port which is connected to the device. */
	public static ExPar Port = new ExPar(ExParTypeCodes.STRING, new ExParValue(
			"COM1"), "Serial Port Name");
	private SerialLineMonitor monitor;
	private StringBuffer receiveBuffer;
	private boolean active = false;

	/**
	 * Open the connection to the measurement device.
	 * 
	 * @return true if the connection is successfully opened.
	 */
	public boolean connect() {
		boolean retval = false;
		serialLineConnection = new SerialLineConnection(
				new SerialLineParameters(Port.getString(), 2400,
						SerialPort.FLOWCONTROL_NONE,
						SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE), this);
		try {
			serialLineConnection.openConnection();
			retval = true;
		} catch (SerialLineException sx) {
			// sx.printStackTrace();
			retval = false;
		}
		return retval;
	}

	/**
	 * Return some descriptive information about this color measurement device.
	 * 
	 * @return a string which contains the information.
	 */
	public String getInfo() {
		return "Tektronix Lumacolor II with J1810 Colorimeter Head";
	}

	/** Close the connection to the measurement device. */
	public void close() {
		serialLineConnection.closeConnection();
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
		return null;
	}

	/**
	 * Initiate a calibration cycle and wait until calibration is done.
	 * 
	 * @return true if calibration was successful.
	 */
	public boolean calibrate() {
		return true;
	}

	/**
	 * Set up the device such that it is ready to measure reflectance spectra.
	 * If necessary then the device initiates a calibration cycle before
	 * starting measurement.
	 * 
	 * @return true if the device has been set up successfully.
	 */
	public boolean setSpectralReflectanceMode() {
		return false;
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
	 * Trigger a tristimulus value measurement cycle and return the data in the
	 * format [L,x,y].
	 * 
	 * @return an array which contains the tristimulus [L,x,y]-data or null if
	 *         this method is not available.
	 */
	public double[] getTristimulus() {
		String data = getDataString("!NEW\n");
		/* Tektronix J18 Format = 'x = 0.418E0 y = 0.375E0 LUM = 18.32E0 cd/m^2' */
		int x1 = 4;
		int x2 = data.indexOf("  y = ");
		int y1 = x2 + 6;
		int y2 = data.indexOf(" LUM = ");
		int L1 = y2 + 7;
		int L2 = data.indexOf(" cd/m");
		double[] d = new double[3];
		try {
			d[0] = Double.valueOf(data.substring(L1, L2)).doubleValue();
			d[1] = Double.valueOf(data.substring(x1, x2)).doubleValue();
			d[2] = Double.valueOf(data.substring(y1, y2)).doubleValue();
		} catch (NumberFormatException nfx) {
		}
		return d;
	}

	private void setActive(boolean t) {
		active = t;
	}

	private boolean isActive() {
		return active;
	}

	/**
	 * Send the given control string to the Tektronix J18 Colorimeter device and
	 * return the data line sent back by the device.
	 * 
	 * @param ctrlCmd
	 *            a valid Tektronix J18 control string.
	 */
	protected String getDataString(String ctrlCmd) {
		setActive(true);
		receiveBuffer = new StringBuffer(100);
		monitor = new SerialLineMonitor();
		serialLineConnection.send(ctrlCmd);
		monitor.waitForData();
		setActive(false);
		if (monitor.hasValidData()) {
			return receiveBuffer.toString();
		} else {
			return null;
		}
	}

	// private static int receiveCount = 0;
	/** Implements the SerialLineReceiver. */
	public void receive(String s) {
		if (isActive()) {
			// receiveCount++;
			// System.out.println("Receive count = " + receiveCount +
			// " received: \"" + s + "\"");
			receiveBuffer.append(s);
			if (s.charAt(s.length() - 1) == (char) 10) {
				// System.out.print("Input line found: ");
				// System.out.print(receiveBuffer);
				monitor.measurementFinished();
			}
		}
	}
}
