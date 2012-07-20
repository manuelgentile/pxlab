package de.pxlab.pxl;

public class SecondaryScreen extends ScreenDevice implements ExParTypeCodes {
	/**
	 * Color device code.
	 * 
	 * @see ColorDeviceTransformCodes
	 */
	public static ExPar ColorDevice = new ExPar(GEOMETRY_EDITOR,
			ColorDeviceTransformCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.ColorDeviceTransformCodes.SONY_TRINITRON"),
			"Screen Color Device Code");
	/** Color device DAC range. By default this is 256 for 8 bit DACs. */
	public static ExPar ColorDeviceDACRange = new ExPar(INTEGER,
			new ExParValue(255), "Color Device DAC Range");
	/** Name of a color table file for this device. */
	public static ExPar DeviceColorTableFile = new ExPar(STRING,
			new ExParValue(""), "Device Color Table File Name");
	/**
	 * Red primary color coordinates. The Y-coordinate gives the maximum
	 * luminance of this primary.
	 */
	public static ExPar RedPrimary = new ExPar(COLOR, new ExParValue(21.26,
			0.621, 0.34), "Red primary color coordinates");
	/**
	 * Green primary color coordinates. The Y-coordinate gives the maximum
	 * luminance of this primary.
	 */
	public static ExPar GreenPrimary = new ExPar(COLOR, new ExParValue(71.52,
			0.281, 0.606), "Green primary color coordinates");
	/**
	 * Blue primary color coordinates. The Y-coordinate gives the maximum
	 * luminance of this primary.
	 */
	public static ExPar BluePrimary = new ExPar(COLOR, new ExParValue(7.22,
			0.152, 0.067), "Blue primary color coordinates");
	/**
	 * Red channel gamma function parameters. Contains two entries: The first is
	 * the gamma value (default: 2.4) and the second is the scaling parameter
	 * (default: 1.0).
	 */
	public static ExPar RedGamma = new ExPar(SMALL_DOUBLE, new ExParValue(2.4,
			1.0), "Red Channel Gamma Function Parameters");
	/**
	 * Green channel gamma function parameters. Contains two entries: The first
	 * is the gamma value (default: 2.4) and the second is the scaling parameter
	 * (default: 1.0).
	 */
	public static ExPar GreenGamma = new ExPar(SMALL_DOUBLE, new ExParValue(
			2.4, 1.0), "Green Channel Gamma Function Parameters");
	/**
	 * Blue channel gamma function parameters. Contains two entries: The first
	 * is the gamma value (default: 2.4) and the second is the scaling parameter
	 * (default: 1.0).
	 */
	public static ExPar BlueGamma = new ExPar(SMALL_DOUBLE, new ExParValue(2.4,
			1.0), "Blue Channel Gamma Function Parameters");
}
