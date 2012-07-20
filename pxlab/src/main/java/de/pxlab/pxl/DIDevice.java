//**********************************************************************************************
//		(C) Copyright 2002 by Dipl. Phys. Joerg Plewe, HARDCODE Development
//		All rights reserved. Copying, modification,
//		distribution or publication without the prior written
//		consent of the author is prohibited.
//
//	Created on 26. Dezember 2001, 00:40
// ---------------------------------------------------------------------------------------------
//
//     Modified for PXLab by H. Irtel
//     2007/02/12
//
//**********************************************************************************************
package de.pxlab.pxl;

import de.hardcode.jxinput.*;
import de.hardcode.jxinput.directinput.*;

/**
 * A DirectInput device has buttons, directionals, and axes. The driver allows
 * access to multiple devices. This code is derived from Joerg Plewe's JXInput
 * package.
 */
public class DIDevice extends PolledDevice {
	private DirectInputDevice diDevice;
	private int changedButtonIndex;
	private double[] axisValue;
	private double[] lastAxisValue;
	private double[] axisDelta;
	private boolean[] buttonState;
	private int changedDirectionalIndex;
	private boolean[] directionalState;
	private int[] directionalValue;
	private Axis[] diAxis;
	private Button[] diButton;
	private Directional[] diDirectional;
	private int deviceType;
	private DIAxisTransform axisTransform;
	private boolean useAxisDeltas = false;
	private double axisLimit = 1.0;

	/**
	 * Creates a new instance of a DIDevice.
	 * 
	 * @param type
	 *            device type as described in class DeviceCodes.
	 * @param devidx
	 *            the index of the device in the list of DirectInput devices.
	 * @param ax
	 *            this device's axis limit value. This is needed such that the
	 *            driver can scale the axis values into the range [-1.0, 1.0].
	 */
	public DIDevice(int type, int devidx, double ax) {
		deviceType = type;
		diDevice = new DirectInputDevice(devidx);
		id = devidx;
		axisLimit = ax;
		if (axisLimit == 0.0)
			axisLimit = 1.0;
		init();
		reset();
	}

	public static int getNumberOfDevices() {
		return DirectInputDevice.getNumberOfDevices();
	}

	public static void showDIDevices() {
		int n = DirectInputDevice.getNumberOfDevices();
		for (int i = 0; i < n; i++) {
			DIDevice d = new DIDevice(0, i, 1.0);
			System.out.println(d);
		}
	}

	public DirectInputDevice getDevice() {
		return diDevice;
	}

	/**
	 * Open the device.
	 * 
	 * @return true if opening is successfull and false if not.
	 */
	public boolean open() {
		return true;
	}

	/** Close the device. No further input will be requested. */
	public void close() {
	}

	/**
	 * Reset the DirectInput connection.
	 */
	public void reset() {
		diDevice.reset();
		diDevice.update();
		for (int i = 0; i < diAxis.length; i++)
			lastAxisValue[i] = axisTransform.valueOf(diAxis[i].getValue());
		for (int i = 0; i < diButton.length; i++)
			buttonState[i] = diButton[i].getState();
		for (int i = 0; i < diDirectional.length; i++)
			directionalState[i] = diDirectional[i].isCentered();
	}

	/**
	 * Initialisation of fields.
	 */
	private void init() {
		if (deviceType == DeviceCodes.DI_SPACE_MOUSE) {
			axisTransform = new SpaceWareDIAxisTransform();
			useAxisDeltas = true;
		} else {
			axisTransform = new DefaultDIAxisTransform();
			useAxisDeltas = false;
		}
		int m = diDevice.getMaxNumberOfAxes();
		int n = diDevice.getNumberOfAxes();
		diAxis = new Axis[n];
		int k = 0;
		for (int i = 0; i < m; ++i) {
			Axis a = diDevice.getAxis(i);
			if (a != null) {
				diAxis[k++] = a;
			}
		}
		axisValue = new double[n];
		lastAxisValue = new double[n];
		axisDelta = new double[n];
		m = diDevice.getMaxNumberOfButtons();
		n = diDevice.getNumberOfButtons();
		diButton = new Button[n];
		k = 0;
		for (int i = 0; i < m; ++i) {
			Button b = diDevice.getButton(i);
			if (b != null)
				diButton[k++] = b;
		}
		buttonState = new boolean[n];
		m = diDevice.getMaxNumberOfDirectionals();
		n = diDevice.getNumberOfDirectionals();
		diDirectional = new Directional[n];
		k = 0;
		for (int i = 0; i < m; ++i) {
			Directional d = diDevice.getDirectional(i);
			if (d != null)
				diDirectional[k++] = d;
		}
		directionalState = new boolean[n];
		directionalValue = new int[n];
	}
	private static final long B32 = 4294967295L;
	class SpaceWareDIAxisTransform implements DIAxisTransform {
		public double valueOf(double x) {
			return (double) (int) (((long) x) & B32);
		}
	}
	class DefaultDIAxisTransform implements DIAxisTransform {
		public double valueOf(double x) {
			return x;
		}
	}

	public PolledDeviceData read() {
		delay();
		// long t1 = HiresClock.getTimeNanos();
		long time = HiresClock.getTimeNanos();
		diDevice.update();
		// System.out.println("DIDevice.read() takes time: " + (time-t1));
		if (useAxisDeltas) {
			for (int i = 0; i < diAxis.length; i++) {
				axisValue[i] = axisTransform.valueOf(diAxis[i].getValue());
				axisDelta[i] = (axisValue[i] - lastAxisValue[i]) / axisLimit;
				lastAxisValue[i] = axisValue[i];
			}
		} else {
			for (int i = 0; i < diAxis.length; i++) {
				axisValue[i] = axisTransform.valueOf(diAxis[i].getValue())
						/ axisLimit;
			}
		}
		changedButtonIndex = -1;
		for (int i = diButton.length - 1; i >= 0; i--) {
			boolean s = diButton[i].getState();
			if (s != buttonState[i])
				changedButtonIndex = i;
			buttonState[i] = s;
		}
		changedDirectionalIndex = -1;
		for (int i = diDirectional.length - 1; i >= 0; i--) {
			boolean s = diDirectional[i].isCentered();
			if (s != directionalState[i])
				changedDirectionalIndex = i;
			directionalState[i] = s;
			directionalValue[i] = diDirectional[i].getDirection() / 100;
		}
		return new PolledDeviceData(this, changedButtonIndex, buttonState,
				changedDirectionalIndex, directionalState, directionalValue,
				useAxisDeltas ? axisDelta : axisValue, time);
	}

	/** Devices may have a name. */
	public String getName() {
		return diDevice.getName();
	}

	/** Actual number of available buttons. */
	public int getNumberOfButtons() {
		return diDevice.getNumberOfButtons();
	}

	/** Actual number of available axes. */
	public int getNumberOfAxes() {
		return diDevice.getNumberOfAxes();
	}

	/** Actual number of available directional features. */
	public int getNumberOfDirectionals() {
		return diDevice.getNumberOfDirectionals();
	}

	public Axis getAxis(int i) {
		return diAxis[i];
	}

	public Button getButton(int i) {
		return diButton[i];
	}

	public Directional getDirectional(int i) {
		return diDirectional[i];
	}

	public String toString() {
		StringBuffer b = new StringBuffer(200);
		diDevice.update();
		b.append(getName());
		b.append("\n  Axes: " + getNumberOfAxes());
		for (int i = 0; i < diAxis.length; i++)
			b.append("\n    " + diAxis[i].getName() + " = "
					+ diAxis[i].getValue());
		b.append("\n  Buttons: " + getNumberOfButtons());
		for (int i = 0; i < diButton.length; i++)
			b.append("\n    " + diButton[i].getName() + " = "
					+ (diButton[i].getState() ? "|" : "o"));
		b.append("\n  Directionals: " + getNumberOfDirectionals());
		for (int i = 0; i < diDirectional.length; i++)
			b.append("\n    " + diDirectional[i].getName() + " = "
					+ diDirectional[i].getDirection());
		return b.toString();
	}
}
