package de.pxlab.pxl;

/**
 * Codes for controlling a color measurement device.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/07/29
 */
public interface ColorMeasurementCodes {
	/** Open color measurement device. */
	public static final int OPEN_MEASUREMENT_DEVICE = 1 << 0;
	/** Close device. */
	public static final int CLOSE_MEASUREMENT_DEVICE = 1 << 1;
	/** Get CIE 1931 xyY tristimulus value. */
	public static final int GET_TRISTIMULUS = 1 << 2;
	/** Get spectral measuerement data. */
	public static final int GET_SPECTRUM = 1 << 7;
}
