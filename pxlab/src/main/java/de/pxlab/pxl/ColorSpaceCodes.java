package de.pxlab.pxl;

/**
 * Codes for color spaces.
 * 
 * @version 0.2.0
 */
public interface ColorSpaceCodes {
	/** The CIE 1931 XYZ color space. */
	public static final int CS_XYZ = 0;
	/** The CIE 1931 xy-chromaticities and luminance. */
	public static final int CS_Yxy = 1;
	/**
	 * The current device color space defined by the device's primaries assuming
	 * linear channels.
	 */
	public static final int CS_RGB = 2;
	/**
	 * The current device color space including device primaries and gamma
	 * transforms.
	 */
	public static final int CS_Dev = 3;
	/**
	 * The Smith & Pokorny funamental cone activation space as derived from CIE
	 * XYZ coordinates.
	 */
	public static final int CS_LMS = 4;
	/** The MacLeod & Boynton LMS-based relative cone excitation space. */
	public static final int CS_Yrb = 5;
	/**
	 * The CIE 1976 L*a*b* space with the current device's white point as a
	 * reference. Lightness values range from 0.0 to 100.0, Green/Red
	 * chromaticity values of a* range from -120.0 to +120.0, and Blue/Yellow
	 * chromaticity values b* range from -120.0 to 120.0.
	 */
	public static final int CS_LabStar = 6;
	/**
	 * The polar coordinates version of the CIE L*a*b* space with the current
	 * device's white point as a reference. Lightness values range from 0.0 to
	 * 100.0, Chroma values range from 0.0 up to around 120.0, and Hue values
	 * range from 0.0 to 360.0.
	 */
	public static final int CS_LabLChStar = 7;
	/** The CIE 1976 Lu'v' space. */
	public static final int CS_LuvPrime = 8;
	/**
	 * The CIE 1976 L*u*v* space with the current device's white point as a
	 * reference.
	 */
	public static final int CS_LuvStar = 9;
	/**
	 * The polar coordinates version of the CIE 1976 L*u*v* space with the
	 * current device's white point as a reference.
	 */
	public static final int CS_LuvLChStar = 10;
	/**
	 * The CIE Color Appearance Model (1997) with viewing and scene conditions
	 * to be defined separatley.
	 */
	public static final int CS_JCh = 11;
}
