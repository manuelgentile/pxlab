package de.pxlab.pxl;

import java.awt.Component;

/**
 * Control a graphics device's output look-up-tables under Windows OS. Uses the
 * Windows GDI methods set/getDeviceGammaRamp() to set and retrieve a graphics
 * controller's output look-up-table. This makes it possible to use high
 * resolution devices for precise gamma control.
 * 
 * <p>
 * This class also provides methods to set virtual gamma values and to create
 * test patterns for visual examination of DAC and look-up-table resolution.
 * 
 * <p>
 * The Windwos GDI places certain restrictions on the possible entries of a
 * look-up-table. This makes it impossible to load arbitrary values, like all
 * zeros, into the tables.
 * 
 * <p>
 * This class relies on Windows OS native methods contained in the dynamic link
 * libraries pxlab.dll and jawt.dll.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 08/24/03 verified functionality for an ATI Radeon 8500 graphics card BIOS
 * version 7.004 Driverversion 7.91 and a Matrox Parhelia BIOS version 1.0-4
 * Driver version 1.4.1.3 both running under Windows 2000.
 */
public class WindowsGammaControl {
	static {
		System.loadLibrary("pxlab");
	}
	private static final int m = 65535;
	private static final int LUTSize = 256;
	private Component component;
	private double deviceGamma = 2.4;
	private double virtualGamma = 1.0;

	/**
	 * Create a Windows gamma control object for a graphics device.
	 * 
	 * @param c
	 *            an AWT Component whose hardware drawing surface should be
	 *            controlled by this gamma control object.
	 * @param g
	 *            the actual physical gamma of the display decvice. This
	 *            parameter is required if this gamma control object intends to
	 *            set a virtual gamma by using the setVirtualGamma() method.
	 */
	public WindowsGammaControl(Component c, double g) {
		component = c;
		deviceGamma = g;
	}

	/**
	 * Set the virtual gamma for the device controlled by this GammaControl.
	 * 
	 * Suppose we want to have a virtual gamma of 1.4 such that
	 * 
	 * <pre>
	 * L = E &circ; 1.4
	 * </pre>
	 * 
	 * and we have an actual device gamma of 2.2. Thus the physical condition is
	 * 
	 * <pre>
	 * L = D &circ; 2.2
	 * </pre>
	 * 
	 * we then have to replace D by some function D = F(E) such that
	 * 
	 * <pre>
	 *    L = D^2.2 = F(E)^2.2 = [E^(1.4/2.2)]^2.2 = E^1.4
	 * </pre>
	 * 
	 * Thus given the physical device gamma is 2.2 and the virtual device gamma
	 * should be 1.4 we have to load the look-up-table with the function
	 * X^(1.4/2.2).
	 * 
	 * @param vg
	 *            the virtual gamma value.
	 */
	public boolean setVirtualGamma(double vg) {
		virtualGamma = vg;
		double g = virtualGamma / deviceGamma;
		char e;
		char[] redLUT = new char[LUTSize];
		char[] greenLUT = new char[LUTSize];
		char[] blueLUT = new char[LUTSize];
		/*
		 * getLUT(component, redLUT, greenLUT, blueLUT);
		 * 
		 * for (int i = 0; i < LUTSize; i++) { System.out.println((int)redLUT[i]
		 * + " " + (int)greenLUT[i] + " " + (int)blueLUT[i]); }
		 */
		for (int i = 0; i < LUTSize; i++) {
			e = (char) Math.round(m * Math.pow((double) i / 255.0, g));
			redLUT[i] = greenLUT[i] = blueLUT[i] = e;
		}
		return setDeviceLUT(component, redLUT, greenLUT, blueLUT);
	}

	/**
	 * Set the hardware look-up-tables to their default values.
	 * 
	 * @return true if the hardware look-up-tables have been set successfully or
	 *         false if writing to the hardware look-up-tables failed.
	 */
	public boolean setDefaultLUT() {
		char[] redLUT = new char[LUTSize];
		char[] greenLUT = new char[LUTSize];
		char[] blueLUT = new char[LUTSize];
		redLUT[0] = greenLUT[0] = blueLUT[0] = (char) 0;
		for (int i = 1; i < LUTSize; i++) {
			redLUT[i] = greenLUT[i] = blueLUT[i] = (char) (i * 256 + 255);
		}
		return setDeviceLUT(component, redLUT, greenLUT, blueLUT);
	}

	/**
	 * Set the hardware look-up-tables to a set of test entries. Entries at
	 * index i (i = 0, ..., 255) contain the value 256*i. The entries at k, k+1,
	 * k+2, ..., k+8 are set such that if k and (k+1) result in different output
	 * intensities then there are at least 8+1 bits of DAC resolution. If index
	 * (k+1) and index (k+2) result in different output then there are at least
	 * 8+2 bits of DAC resoluton. The same holds for all successive pairs of
	 * indices at (k+j) and (k+j+1). If these create different output then there
	 * are 8+j+1 bits of resolution in the look-up-table.
	 * 
	 * @param k
	 *            the index of the look-up-table which should be used as a base
	 *            index for testing. Must be less than 248.
	 * @return true if (k<248) and the hardware look-up-tables have been set
	 *         successfully or false if writing to the hardware look-up-tables
	 *         failed.
	 */
	public boolean setTestLUT(int k) {
		char[] redLUT = new char[LUTSize];
		char[] greenLUT = new char[LUTSize];
		char[] blueLUT = new char[LUTSize];
		for (int i = 0; i < LUTSize; i++) {
			redLUT[i] = greenLUT[i] = blueLUT[i] = (char) (i * 256);
		}
		if (k < 248) {
			redLUT[k + 0] = greenLUT[k + 0] = blueLUT[k + 0] = (char) (k * 256 + 0);
			redLUT[k + 1] = greenLUT[k + 1] = blueLUT[k + 1] = (char) (k * 256 + 128);
			redLUT[k + 2] = greenLUT[k + 2] = blueLUT[k + 2] = (char) (k * 256 + 192);
			redLUT[k + 3] = greenLUT[k + 3] = blueLUT[k + 3] = (char) (k * 256 + 224);
			redLUT[k + 4] = greenLUT[k + 4] = blueLUT[k + 4] = (char) (k * 256 + 240);
			redLUT[k + 5] = greenLUT[k + 5] = blueLUT[k + 5] = (char) (k * 256 + 248);
			redLUT[k + 6] = greenLUT[k + 6] = blueLUT[k + 6] = (char) (k * 256 + 252);
			redLUT[k + 7] = greenLUT[k + 7] = blueLUT[k + 7] = (char) (k * 256 + 254);
			redLUT[k + 8] = greenLUT[k + 8] = blueLUT[k + 8] = (char) (k * 256 + 255);
		} else {
			return false;
		}
		return setDeviceLUT(component, redLUT, greenLUT, blueLUT);
	}

	/**
	 * Return the current content of the hardware look-up-tables.
	 * 
	 * @param redLUT
	 *            an array for storing the red look-up-table.
	 * @param greenLUT
	 *            an array for storing the green look-up-table.
	 * @param blueLUT
	 *            an array for storing the blue look-up-table. If any of these
	 *            arrays is null or has not the correct length then the arrays
	 *            are created new.
	 * @return true if the hardware look-up-tables have been read successfully
	 *         or false if reading the hardware look-up-tables failed.
	 */
	public boolean getLUT(char[] redLUT, char[] greenLUT, char[] blueLUT) {
		if ((redLUT == null) || (greenLUT == null) || (blueLUT == null)
				|| (redLUT.length != LUTSize) || (greenLUT.length != LUTSize)
				|| (blueLUT.length != LUTSize)) {
			redLUT = new char[LUTSize];
			greenLUT = new char[LUTSize];
			blueLUT = new char[LUTSize];
		}
		return getDeviceLUT(component, redLUT, greenLUT, blueLUT);
	}

	/**
	 * Set the current hardware look-up-tables.
	 * 
	 * @param redLUT
	 *            an array containing the red look-up-table.
	 * @param greenLUT
	 *            an array containing the green look-up-table.
	 * @param blueLUT
	 *            an array containing the blue look-up-table.
	 * @return true if the hardware look-up-tables have been written
	 *         successfully or false if writing to the hardware look-up-tables
	 *         failed.
	 */
	public boolean setLUT(char[] redLUT, char[] greenLUT, char[] blueLUT) {
		if ((redLUT == null) || (greenLUT == null) || (blueLUT == null)
				|| (redLUT.length != LUTSize) || (greenLUT.length != LUTSize)
				|| (blueLUT.length != LUTSize)) {
			return false;
		}
		return setDeviceLUT(component, redLUT, greenLUT, blueLUT);
	}

	private native boolean setDeviceLUT(Component c, char[] redLUT,
			char[] greenLUT, char[] blueLUT);

	private native boolean getDeviceLUT(Component c, char[] redLUT,
			char[] greenLUT, char[] blueLUT);
}
