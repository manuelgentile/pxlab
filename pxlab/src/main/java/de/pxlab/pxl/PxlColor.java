package de.pxlab.pxl;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * This class defines colors by their CIE 1931 tristimulus values X, Y, and Z,
 * and links them to the Java AWT Color class which is the device color system.
 * The following guidelines are realized:
 * 
 * <OL>
 * 
 * <LI>This class only handles device independent color spaces and properties.
 * All device dependent requests are delegated to an object of class
 * ColorDeviceTransform which is a static class object.
 * 
 * <LI>PxlColor objects are always coded in CIE 1931 XYZ tristimulus values. The
 * chromaticity coordinates xy are also part of the definition in order to allow
 * a more flexible access to the color objects.
 * 
 * <LI>Transforms of PxlColor objects into other device independent color spaces
 * are always stored as arrays of double values.
 * 
 * <LI>The transform(int csType, double[] p) method is the standard method for
 * transforming PxlColor objects into a different color space. Some of the more
 * common transforms have shorthand notations.
 * 
 * <LI>The static instance(int csType, double[] p) method is the standard method
 * for transforming a color from an arbitrary color space into a PxlColor
 * object.
 * 
 * </OL>
 * 
 * @version 0.3.6
 */
/*
 * 01/19/01 limit luminance to minLum, use setBlack() to set black colors
 * 
 * 11/30/01 added logarithmicRampTo(PxlColor c, int n)
 * 
 * 02/07/02 added devFloor() and devCeiling()
 * 
 * 03/04/03 added constructor from string description. Should be more versatile.
 * 
 * 2006/10/24 use ExPar.CIEWhitePoint as the white reference for CIELab and
 * CIELuv transforms. Method getCIEWhitePoint() returns the current CIE
 * reference white point.
 */
public class PxlColor implements ColorSpaceCodes, Cloneable {
	/** Tristimulus coordinate X of our XYZ-space. */
	protected double X;
	/** Tristimulus coordinate Y of our XYZ-space. */
	protected double Y;
	/** Tristimulus coordinate Z of our XYZ-space. */
	protected double Z;
	/** This color's x-chromaticity. */
	protected double x;
	/** This color's y-chromaticity. */
	protected double y;
	protected static NumberFormat colorCoordinate = NumberFormat
			.getInstance(Locale.US);
	protected static NumberFormat chromaticityCoordinate = NumberFormat
			.getInstance(Locale.US);
	static {
		colorCoordinate.setMaximumFractionDigits(3);
		colorCoordinate.setGroupingUsed(false);
		chromaticityCoordinate.setMaximumFractionDigits(3);
		chromaticityCoordinate.setGroupingUsed(false);
	}
	private static final String[] csName = { "XYZ", "CIE 1931 XYZ", "Yxy",
			"CIE 1931 xy-chromaticities and Luminance", "RGB",
			"Linear RGB Device Channels", "Dev", "RGB Device Channels", "LMS",
			"Smith & Pokorny Fundamental Cone Excitation", "Yrb",
			"MacLeod & Boynton Relative Cone Excitation", "L*a*b*",
			"CIE 1976 L*a*b*", "L*a*b*Ch", "CIE 1976 L*a*b* Polar", "Lu'v'",
			"CIE 1976 Lu'v'", "L*u*v*", "CIE 1976 L*u*v*", "L*u*v*Ch",
			"CIE 1976 L*u*v* Polar", "JCh", "CIE 1997 Color Appearance Model" };
	/**
	 * The smallest luminance available for any device. PxlColor objects which
	 * have less luminance than this are considered to be black and send the
	 * RGB-coordinates (0/0/0) to any output device.
	 */
	public static double minLum = 0.0001;

	public static String getShortName(int s) {
		return (csName[s + s]);
	}

	public static String getLongName(int s) {
		return (csName[s + s + 1]);
	}
	/**
	 * The current color device which is used to transform device dependent
	 * colors. Note that this is a class parameter.
	 */
	protected static ColorDeviceTransform device;
	// We initialize the color device to a sensible default.
	static {
		device = new ColorDeviceTransform();
	}
	/** Color of the CIE standard illuminant D5000. */
	public static PxlColor D50 = instance(CS_Yxy, 1.0, 0.3457, 0.3585);
	/** Color of the CIE standard illuminant D6500. */
	public static PxlColor D65 = instance(CS_Yxy, 1.0, 0.3127, 0.3291);
	/**
	 * Color of the CIE standard illuminant E, the equal energy spectrum.
	 */
	public static PxlColor E = instance(CS_Yxy, 1.0, 1.0 / 3.0, 1.0 / 3.0);
	/** Color of the CIE standard illuminant A. */
	public static PxlColor A = instance(CS_Yxy, 1.0, 0.44757, 0.40745);
	/** Color of the CIE standard illuminant B. */
	public static PxlColor B = instance(CS_Yxy, 1.0, 0.34842, 0.35161);
	/** Color of the CIE standard illuminant C. */
	public static PxlColor C = instance(CS_Yxy, 1.0, 0.31006, 0.31616);
	/** This is a collection of standard device colors. */
	private static PxlColor white = new PxlColor(Color.white);
	private static PxlColor lightGray = new PxlColor(Color.lightGray);
	private static PxlColor gray = new PxlColor(Color.gray);
	private static PxlColor darkGray = new PxlColor(Color.darkGray);
	private static PxlColor black = new PxlColor(Color.black);
	private static PxlColor yellow = new PxlColor(Color.yellow);
	private static PxlColor cyan = new PxlColor(Color.cyan);
	private static PxlColor magenta = new PxlColor(Color.magenta);
	private static PxlColor green = new PxlColor(Color.green);
	private static PxlColor red = new PxlColor(Color.red);
	private static PxlColor blue = new PxlColor(Color.blue);
	public static final int WHITE = 0;
	public static final int LIGHT_GRAY = 1;
	public static final int GRAY = 2;
	public static final int DARK_GRAY = 3;
	public static final int BLACK = 4;
	public static final int YELLOW = 5;
	public static final int CYAN = 6;
	public static final int MAGENTA = 7;
	public static final int GREEN = 8;
	public static final int RED = 9;
	public static final int BLUE = 10;
	/**
	 * The matrix to transform from XYZ to LMS space. Note that it is not quite
	 * correct to apply this matrix to CIE 1931 XYZ coordinates since they
	 * should be applied to Stiles' corrected data. Note that we use m[3,3]= 0.1
	 * instead of 0.00801 as sayed by CVRL. Our value is such that the ration
	 * L:M:S of 0.6:0.3:0.1 results in equal energy white.
	 */
	private static final double[][] mXYZToLMS = {
			{ 0.15514, 0.54312, -0.03286 }, { -0.15514, 0.45684, 0.03286 },
			{ 0.0, 0.0, 0.1 } };
	/** Matrix to move from Smith & Bokorny Space to CIE XYZ */
	private static final double[][] mLMSToXYZ = {
			{ 2.944813, -3.500978, 2.118087 }, { 1.000040, 1.000040, 0.0 },
			{ 0.0, 0.0, 10.0 } };
	private static final double[][] mXYZToCIERGB = { { 2.365, -0.897, -0.468 },
			{ -0.515, 1.426, 0.089 }, { 0.005, -0.014, 1.009 } };
	private static final double[][] mCIERGBToXYZ = { { 0.490, 0.310, 0.200 },
			{ 0.177, 0.812, 0.011 }, { 0.000, 0.010, 0.990 } };
	/** After Hunt (1991) */
	private static final double[][] mXYZToHuntPDT = { { 0.39, 0.69, -0.08 },
			{ -0.23, 1.18, 0.05 }, { 0.00, 0.00, 1.00 } };
	private static final double[][] mHuntPDTToXYZ = { { 1.91, -1.11, 0.20 },
			{ 0.37, 0.63, 0.00 }, { 0.00, 0.00, 1.00 } };
	/** After Luther (1927) */
	private static final double[][] mXYZToLMN = { { 0.00, 1.00, 0.00 },
			{ 1.00, -1.00, 0.00 }, { 0.00, -1.00, 1.00 } };
	private static final double[][] mLMNToXYZ = { { 0.00, 1.00, 0.00 },
			{ 1.00, -1.00, -1.00 }, { 0.00, 0.00, 1.00 } };

	/**
	 * Set the current color device.
	 * 
	 * @param d
	 *            the new PxlColorDevice object.
	 */
	public static void setDeviceTransform(ColorDeviceTransform d) {
		device = d;
		white = new PxlColor(device.white);
		lightGray = new PxlColor(device.lightGray);
		gray = new PxlColor(device.gray);
		darkGray = new PxlColor(device.darkGray);
		black = new PxlColor(device.black);
		red = new PxlColor(device.red);
		yellow = new PxlColor(device.yellow);
		green = new PxlColor(device.green);
		magenta = new PxlColor(device.magenta);
		cyan = new PxlColor(device.cyan);
		blue = new PxlColor(device.blue);
		if (Debug.isActive(Debug.COLOR_DEVICE)) {
			System.out.println(device);
			device.printSystemColors();
		}
	}

	public static PxlColor systemColor(int i) {
		switch (i) {
		case WHITE:
			return white;
		case LIGHT_GRAY:
			return lightGray;
		case GRAY:
			return gray;
		case DARK_GRAY:
			return darkGray;
		case BLACK:
			return black;
		case YELLOW:
			return yellow;
		case CYAN:
			return cyan;
		case MAGENTA:
			return magenta;
		case GREEN:
			return green;
		case RED:
			return red;
		default:
			return blue;
		}
	}

	/**
	 * Return the current color device.
	 * 
	 * @return the current PxlColorDevice object.
	 */
	public static ColorDeviceTransform getDeviceTransform() {
		return (device);
	}

	/** Initialize this class for a PxlColorDevice. */
	/*
	 * public static void init() { device.init(); }
	 */
	/** Create a PxlColor object from its XYZ tristimulus coordinates. */
	public PxlColor(double a, double b, double c) {
		if (b < minLum) {
			setBlack();
		} else {
			X = a;
			Y = b;
			Z = c;
			double s = (X + Y + Z);
			x = X / s;
			y = Y / s;
		}
	}

	/** Create a PxlColor object from a XYZ tristimulus array. */
	public PxlColor(double[] a) {
		if (a[1] < minLum) {
			setBlack();
		} else {
			X = a[0];
			Y = a[1];
			Z = a[2];
			double s = (X + Y + Z);
			x = X / s;
			y = Y / s;
		}
	}

	/**
	 * Create a gray PxlColor object with the given relative luminance with
	 * respect to the current color device's white point.
	 */
	public PxlColor(double relY) {
		if (relY < minLum) {
			setBlack();
		} else {
			double w[] = device.getWhitePoint();
			X = w[0] * relY;
			Y = w[1] * relY;
			Z = w[2] * relY;
			double s = (X + Y + Z);
			x = X / s;
			y = Y / s;
		}
	}

	/** Create an empty PxlColor */
	public PxlColor() {
		this(1.0);
	}

	/**
	 * Create a PxlColor object from a Java Color object. Note that Java Color
	 * objects always refer to a device color space and thus conversion has to
	 * apply some color correction.
	 */
	public PxlColor(Color c) {
		// System.out.println("PxlColor(Color) c = " + c);
		double[] a = devToXYZ(c);
		// System.out.println("PxlColor(Color) a = [" + a[0] + ", " + a[1] +
		// ", " + a[2] + "]");
		if (a[1] < minLum) {
			setBlack();
		} else {
			X = a[0];
			Y = a[1];
			Z = a[2];
			double s = (X + Y + Z);
			x = X / s;
			y = Y / s;
		}
	}

	/**
	 * Create a PxlColor from a string description. Currently only the Tektronix
	 * J18 output strings are supported. Their format is:
	 * "x = 0.193E0 y = 0.542E0 LUM = 7.28E0 cd/m^2\r\n" or "X = ... "
	 */
	public PxlColor(String s) {
		this(1.0);
		int i;
		if (s.startsWith("x = ")) {
			if ((i = s.indexOf("  ")) > 0) {
				String sx = s.substring(4, i);
				// System.out.println("x = " + sx);
				s = s.substring(i + 2);
				if (s.startsWith("y = ")) {
					if ((i = s.indexOf("  ")) > 0) {
						String sy = s.substring(4, i);
						// System.out.println("y = " + sy);
						s = s.substring(i + 2);
						if (s.startsWith("LUM = ")) {
							if ((i = s.indexOf(" cd/m^2")) > 0) {
								String sL = s.substring(6, i);
								// System.out.println("L = " + sL);
								try {
									Y = Double.valueOf(sL).doubleValue();
									setxy(Double.valueOf(sx).doubleValue(),
											Double.valueOf(sy).doubleValue());
								} catch (NumberFormatException nfx) {
								}
							}
						}
					}
				}
			}
		} else if (s.startsWith("X = ")) {
			if ((i = s.indexOf("  ")) > 0) {
				String sX = s.substring(4, i);
				// System.out.println("x = " + sx);
				s = s.substring(i + 2);
				if (s.startsWith("Y = ")) {
					if ((i = s.indexOf("  ")) > 0) {
						String sY = s.substring(4, i);
						// System.out.println("y = " + sy);
						s = s.substring(i + 2);
						if (s.startsWith("Z = ")) {
							String sZ = s.substring(4);
							// System.out.println("L = " + sL);
							try {
								X = Double.valueOf(sX).doubleValue();
								Y = Double.valueOf(sY).doubleValue();
								Z = Double.valueOf(sZ).doubleValue();
								double ss = (X + Y + Z);
								x = X / ss;
								y = Y / ss;
							} catch (NumberFormatException nfx) {
							}
						}
					}
				}
			}
		}
	}

	/** Set this color to black while avoiding invalid coordinates. */
	public void setBlack() {
		double w[] = device.getWhitePoint();
		double m = minLum / w[1];
		X = w[0] * m;
		Y = w[1] * m;
		Z = w[2] * m;
		double s = (X + Y + Z);
		x = X / s;
		y = Y / s;
	}

	/**
	 * Create a copy of this object.
	 * 
	 * @return a copy of this <code>ExDesignNode</code> object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new RuntimeException("");
		}
	}

	/**
	 * Determines whether an instance of <code>PxlColor</code> is equal to this
	 * color. Two instances of <code>PxlColor</code> are equal if the values of
	 * their <code>X</code>, <code>Y</code>, and <code>Z</code> member fields,
	 * representing their position in the coordinate space, are the same.
	 * 
	 * @param obj
	 *            an object to be compared with this color
	 * @return <code>true</code> if the object to be compared is an instance of
	 *         <code>PxlColor</code> and has the same values; <code>false</code>
	 *         otherwise
	 */
	public boolean equals(Object obj) {
		if (obj instanceof PxlColor) {
			PxlColor b = (PxlColor) obj;
			return ((X == b.X) && (Y == b.Y) && (Z == b.Z));
		}
		return super.equals(obj);
	}

	/** Return this color's X-value. */
	public double getX() {
		return (X);
	}

	/** Return this color's Y-value. */
	public double getY() {
		return (Y);
	}

	/** Return this color's Z-value. */
	public double getZ() {
		return (Z);
	}

	/** Return this color's x-chromaticity. */
	public double getx() {
		return (x);
	}

	/** Return this color's y-chromaticity. */
	public double gety() {
		return (y);
	}

	/** Set the Y coordinate of this color. */
	public void setY(double YY) {
		if (YY > 0.0) {
			X = x * YY / y;
			Y = YY;
			Z = (1.0 - x - y) * YY / y;
		} else {
			Y = 0.0;
		}
	}

	/** Set the chromaticity coordinates of this color. */
	public void setxy(double xx, double yy) {
		X = xx * Y / yy;
		Z = (1.0 - xx - yy) * Y / yy;
		x = xx;
		y = yy;
	}

	/** Return the components of this color as a double array. */
	public double[] getComponents() {
		double[] a = { X, Y, Z };
		return (a);
	}

	/** Return the components of this color as a double array. */
	public double[] getYxyComponents() {
		double[] a = { Y, x, y };
		return (a);
	}

	/**
	 * Convert this PxlColor object to a device color object. Note that device
	 * color objects always refer to a device color space and thus conversion
	 * has to apply gamma correction.
	 */
	public Color dev() {
		// System.out.println("PxlColor.dev(): " + this.toString());
		// new RuntimeException().printStackTrace();
		Color r = device.dev(this.getComponents());
		// System.out.println(this + ".dev() = " + r);
		// System.out.println(" > " + this + ".devFloor() = " + devFloor());
		// System.out.println(" > " + this + ".devCeiling() = " + devCeiling());
		Debug.show(Debug.COLOR_GAMUT, "PxlColor " + toString() + " -> " + "[ "
				+ r.getRed() + ", " + r.getGreen() + ", " + r.getBlue() + "]");
		return r;
	}

	/**
	 * Convert this PxlColor object to an array of double values of device color
	 * channel values including gamma correction.
	 */
	public double[] devRGB() {
		return device.devRGB(this.getComponents());
	}

	/**
	 * Convert this PxlColor object to a device color object including gamma
	 * correction. Conversion is such that every channel gets the largest
	 * integer value which is less or equal to the respective double.
	 */
	public Color devFloor() {
		double rgb[] = device.devRGB(this.getComponents());
		return new Color((int) (255.0 * rgb[0]), (int) (255.0 * rgb[1]),
				(int) (255.0 * rgb[2]));
	}

	/**
	 * Convert this PxlColor object to a device color object including gamma
	 * correction. Conversion is such that every channel gets the smallest
	 * integer value which is larger or equal to the respective double.
	 */
	public Color devCeiling() {
		double rgb[] = device.devRGB(this.getComponents());
		return new Color((int) (255.0 * rgb[0] + 0.9999999),
				(int) (255.0 * rgb[1] + 0.9999999),
				(int) (255.0 * rgb[2] + 0.9999999));
	}

	/**
	 * Convert a device Color object to a XYZ tristimulus array. This
	 * transformation has to take gamma correction into account.
	 */
	public double[] devToXYZ(Color c) {
		double[] r = device.inverseDev(c);
		// System.out.println("devToXYZ(" + c + ") = " +
		// Utils.stringOfDoubleArray(r));
		return (r);
	}

	/**
	 * Convert this color to the color space whose signature is given in the
	 * argument.
	 */
	public double[] transform(int csType) {
		double[] p = null;
		switch (csType) {
		case CS_XYZ:
			p = this.getComponents();
			break;
		case CS_Yxy:
			p = this.getYxyComponents();
			break;
		case CS_LMS:
			p = mul(mXYZToLMS, this);
			break;
		case CS_Yrb:
			p = this.toYrb();
			break;
		case CS_RGB:
			p = device.toLinearRGB(this.getComponents());
			break;
		case CS_Dev:
			Color c = this.dev();
			p = new double[3];
			p[0] = c.getRed();
			p[1] = c.getGreen();
			p[2] = c.getBlue();
			break;
		case CS_LabStar:
			p = this.toLabStar();
			break;
		case CS_LabLChStar:
			p = this.toLabLChStar();
			break;
		case CS_LuvStar:
			p = this.toLuvStar();
			break;
		case CS_LuvLChStar:
			p = this.toLuvLChStar();
			break;
		case CS_JCh:
			p = CIECAM97.XYZToJCh(this.getComponents());
			break;
		default:
			break;
		}
		// System.out.println(this + ".transform(" + csType + ") = " +
		// Utils.stringOfArray(p));
		return (p);
	}

	/**
	 * Create a PxlColor object from coordinates in the color space given as an
	 * argument.
	 */
	public static PxlColor instance(int csType, double[] p) {
		PxlColor c;
		switch (csType) {
		case CS_XYZ:
			c = new PxlColor(p[0], p[1], p[2]);
			break;
		case CS_Yxy:
			c = new PxlColor(YxyToXYZ(p, null));
			break;
		case CS_LMS:
			c = new PxlColor(mul(mLMSToXYZ, p));
			break;
		case CS_Yrb:
			c = fromYrb(p);
			break;
		case CS_RGB:
			c = new PxlColor(device.fromLinearRGB(p));
			break;
		case CS_Dev:
			Color b = new Color((int) Math.round(p[0]), (int) Math.round(p[1]),
					(int) Math.round(p[2]));
			c = new PxlColor(device.inverseDev(b));
			break;
		case CS_LabStar:
			c = fromLabStar(p);
			break;
		case CS_LabLChStar:
			c = fromLabLChStar(p);
			break;
		case CS_LuvStar:
			c = fromLuvStar(p);
			break;
		case CS_LuvLChStar:
			c = fromLuvLChStar(p);
			break;
		case CS_JCh:
			c = new PxlColor(CIECAM97.JChToXYZ(p));
			break;
		default:
			c = null;
			break;
		}
		// System.out.println("instance of " + Utils.stringOfArray(p) +
		// " in space " + csType + " is " + c);
		return (c);
	}

	/**
	 * Create an instance of a PxlColor from coordinates in the color space
	 * given as an argument.
	 */
	public static PxlColor instance(int csType, double a, double b, double c) {
		double[] p = { a, b, c };
		return (instance(csType, p));
	}

	/** Return the Yxy coordinates of this color. */
	public double[] Yxy() {
		return (this.transform(CS_Yxy));
	}

	/**
	 * Convert from XYZ tristimulus values to Yxy luminance and chromaticity
	 * coordinates.
	 */
	public static double[] XYZToYxy(double[] XYZ, double[] Yxy) {
		if (Yxy == null)
			Yxy = new double[3];
		double t = XYZ[0] + XYZ[1] + XYZ[2];
		if (t > 0.000001) {
			Yxy[1] = XYZ[0] / t;
			Yxy[2] = XYZ[1] / t;
		} else {
			Yxy[1] = 0.3127;
			Yxy[2] = 0.3290;
		}
		Yxy[0] = XYZ[1];
		return (Yxy);
	}

	/**
	 * Convert from Yxy luminance and chromaticity coordinates to XYZ
	 * tristimulus values.
	 */
	public static double[] YxyToXYZ(double[] Yxy, double[] XYZ) {
		if (XYZ == null)
			XYZ = new double[3];
		if (Yxy[0] <= 0.0) {
			XYZ[0] = XYZ[1] = XYZ[2] = 0.0;
		} else {
			double t = Yxy[0] / Yxy[2];
			XYZ[0] = Yxy[1] * t;
			XYZ[1] = Yxy[0];
			XYZ[2] = (1.0 - Yxy[1] - Yxy[2]) * t;
		}
		return (XYZ);
	}

	/**
	 * Convert this color to MaxLeod & Boynton relative cone excitations. The
	 * first component is luminance (L+M), the second is relative L-cone
	 * excitation: L/(L+M), and the third component is relative S-cone
	 * excitation: S/(L+M). Note that the transform is not realy correct since
	 * our cone fundamentals are based on CIE 1931 XYZ and not on Judd's
	 * correction.
	 */
	public double[] toYrb() {
		double[] a = mul(mXYZToLMS, this);
		double LM = (a[0] + a[1]);
		double[] p = { this.Y, a[0] / LM, 2.0075 * a[2] / LM };
		return (p);
	}

	/**
	 * Convert from the MacLeod & Boynton relative cone excitations to CIE XYZ
	 * tristimulus values.
	 */
	public static PxlColor fromYrb(double[] p) {
		double[] a = { p[0] * p[1], p[0] * (1.0 - p[1]), p[0] * p[2] / 2.0075 };
		return (new PxlColor(mul(mLMSToXYZ, a)));
	}

	/**
	 * Get the current CIE reference white point.
	 * 
	 * @return an array of CIE XYZ coordinates for the current white point.
	 */
	public static double[] getCIEWhitePointXYZ() {
		double[] wp = null;
		double[] p = ExPar.CIEWhitePoint.getDoubleArray();
		if (p == null || p.length != 3) {
			wp = device.getWhitePoint();
		} else {
			wp = new YxyColor(p).getComponents();
		}
		// System.out.println("PxlColor.getCIEWhitePointXYZ(): " + (new
		// PxlColor(wp)));
		return wp;
	}

	/**
	 * Get the current CIE reference white point.
	 * 
	 * @return the current white point as a PxlColor.
	 */
	public static PxlColor getCIEWhitePoint() {
		return new PxlColor(getCIEWhitePointXYZ());
	}
	/* 0.3.6 version */
	private static final double LABTHRESHOLD = 0.008856;
	/**/
	/*
	 * Use the Lindbloom (http://www.brucelindbloom.com) modifications here
	 */
	private static final double CIELAB_EPSILON = 216.0 / 24389.0;
	private static final double CIELAB_KAPPA = 24389.0 / 27.0;
	private static final double CIELAB_KAPPA_EPSI = 216.0 / 27.0;
	private static final double CIELAB_7_787 = CIELAB_KAPPA / 116.0;

	/**
	 * Convert from XYZ tristimulus values to CIE L*a*b* coordinates.
	 */
	/*
	 * 0.3.6 version public double[] toLabStar() { double[] Lab = new double[3];
	 * double[] wp = getCIEWhitePointXYZ(); double xxn = this.X/wp[0]; double
	 * xxn1 = (xxn > LABTHRESHOLD)? Math.pow(xxn, 1.0/3.0): 7.787 * xxn +
	 * 16.0/116.0; double yyn = this.Y/wp[1]; double yyn1 = (yyn >
	 * LABTHRESHOLD)? Math.pow(yyn, 1.0/3.0): 7.787 * yyn + 16.0/116.0; double
	 * zzn = this.Z/wp[2]; double zzn1 = (zzn > LABTHRESHOLD)? Math.pow(zzn,
	 * 1.0/3.0): 7.787 * zzn + 16.0/116.0; Lab[0] = (yyn > LABTHRESHOLD)? (116.0
	 * * yyn1 - 16.0): (903.3 * yyn); Lab[1] = 500.0 * (xxn1 - yyn1); Lab[2] =
	 * 200.0 * (yyn1 - zzn1); return(Lab); }
	 */
	/* Bruce Lindbloom version */
	public double[] toLabStar() {
		double[] Lab = new double[3];
		double[] wp = getCIEWhitePointXYZ();
		double xxn = this.X / wp[0];
		double xxn1 = (xxn > CIELAB_EPSILON) ? Math.pow(xxn, 1.0 / 3.0)
				: (CIELAB_KAPPA * xxn + 16.0) / 116.0;
		double yyn = this.Y / wp[1];
		double yyn1 = (yyn > CIELAB_EPSILON) ? Math.pow(yyn, 1.0 / 3.0)
				: (CIELAB_KAPPA * yyn + 16.0) / 116.0;
		double zzn = this.Z / wp[2];
		double zzn1 = (zzn > CIELAB_EPSILON) ? Math.pow(zzn, 1.0 / 3.0)
				: (CIELAB_KAPPA * zzn + 16.0) / 116.0;
		Lab[0] = (yyn > CIELAB_EPSILON) ? (116.0 * yyn1 - 16.0)
				: (CIELAB_KAPPA * yyn);
		Lab[1] = 500.0 * (xxn1 - yyn1);
		Lab[2] = 200.0 * (yyn1 - zzn1);
		return (Lab);
	}

	/**
	 * Convert from CIE L*a*b* coordinates to XYZ tristimulus values. Inverse
	 * CIELab formulas are due to Gernot Hoffmann
	 * (http://www.fho-emden.de/~hoffmann/cielab03022003.pdf)
	 */
	/*
	 * 0.3.6 version public static PxlColor fromLabStar(double[] Lab) { double[]
	 * wp = getCIEWhitePointXYZ(); double yyn1 = (Lab[0] + 16.0)/116.0; double
	 * xxn1 = Lab[1] / 500.0 + yyn1; double zzn1 = - Lab[2] / 200.0 + yyn1; xxn1
	 * = (xxn1 > 0.206893)? xxn1 * xxn1 * xxn1: (xxn1 - 16.0/116.0)/7.787;
	 * double X = wp[0] * xxn1; yyn1 = (yyn1 > 0.206893)? yyn1 * yyn1 * yyn1:
	 * (yyn1 - 16.0/116.0)/7.787; double Y = wp[1] * yyn1; zzn1 = (zzn1 >
	 * 0.206893)? zzn1 * zzn1 * zzn1: (zzn1 - 16.0/116.0)/7.787; double Z =
	 * wp[2] * zzn1; PxlColor c = new PxlColor(X, Y, Z); return c; }
	 */
	/* Bruce Lindbloom version */
	public static PxlColor fromLabStar(double[] Lab) {
		double[] wp = getCIEWhitePointXYZ();
		double yr = Lab[0];
		if (yr > CIELAB_KAPPA_EPSI) {
			yr = (yr + 16.0) / 116.0;
			yr = yr * yr * yr;
		} else {
			yr /= CIELAB_KAPPA;
		}
		double fy = (yr > CIELAB_EPSILON) ? (Lab[0] + 16.0) / 116.0
				: (CIELAB_KAPPA * yr + 16.0) / 116.0;
		double fx = Lab[1] / 500.0 + fy;
		double fz = fy - Lab[2] / 200.0;
		double xr = fx * fx * fx;
		if (xr <= CIELAB_EPSILON)
			xr = (116.0 * fx - 16.0) / CIELAB_KAPPA;
		double zr = fz * fz * fz;
		if (zr <= CIELAB_EPSILON)
			zr = (116.0 * fz - 16.0) / CIELAB_KAPPA;
		double X = wp[0] * xr;
		double Y = wp[1] * yr;
		double Z = wp[2] * zr;
		PxlColor c = new PxlColor(X, Y, Z);
		return c;
	}

	public double[] toLabLChStar() {
		double[] Lab = this.toLabStar();
		double[] LCh = new double[3];
		LCh[0] = Lab[0];
		LCh[1] = Math.sqrt(Lab[1] * Lab[1] + Lab[2] * Lab[2]);
		LCh[2] = Math.atan2(Lab[2], Lab[1]) / Math.PI * 180.0;
		return LCh;
	}

	public static PxlColor fromLabLChStar(double[] LCh) {
		double[] Lab = new double[3];
		double h = LCh[2] / 180.0 * Math.PI;
		Lab[0] = LCh[0];
		Lab[1] = LCh[1] * Math.cos(h);
		Lab[2] = LCh[1] * Math.sin(h);
		// System.out.println("PxlColor.fromLabLChStar(): LCh = " +
		// colorToString(LCh) + " -> Lab = " + colorToString(Lab));
		return fromLabStar(Lab);
	}

	/**
	 * Convert this color from XYZ tristimulus values to CIE L*u*v* coordinates.
	 */
	public double[] toLuvStar() {
		double[] Luv = new double[3];
		double[] wp = getCIEWhitePointXYZ();
		double t = this.X + this.Y * 15.0 + this.Z * 3.0;
		double up = 4.0 * this.X / t;
		double vp = 9.0 * this.Y / t;
		double tn = wp[0] + 15.0 * wp[1] + 3.0 * wp[2];
		double upn = 4.0 * wp[0] / tn;
		double vpn = 9.0 * wp[1] / tn;
		double yyn = this.Y / wp[1];
		double yyn1 = 0.0;
		if (yyn > LABTHRESHOLD) {
			yyn1 = 116.0 * Math.pow(yyn, 1.0 / 3.0) - 16.0;
		} else {
			yyn1 = 903.3 * yyn1;
		}
		Luv[0] = yyn1;
		Luv[1] = 13.0 * yyn1 * (up - upn);
		Luv[2] = 13.0 * yyn1 * (vp - vpn);
		return (Luv);
	}

	/*
	 * Bruce Lindbloom version public double[] toLuvStar() { double[] Luv = new
	 * double[3]; double[] wp = getCIEWhitePointXYZ();
	 * 
	 * double t = this.X + this.Y*15.0 + this.Z *3.0; double up = 4.0*this.X/t;
	 * double vp = 9.0*this.Y/t; double tn = wp[0] + 15.0*wp[1] + 3.0*wp[2];
	 * double upn = 4.0*wp[0]/tn; double vpn = 9.0*wp[1]/tn; double yyn =
	 * this.Y/wp[1]; double yyn1 = 0.0; if (yyn > CIELAB_EPSILON) { yyn1 = 116.0
	 * * Math.pow(yyn, 1.0/3.0) - 16.0; } else { yyn1 = CIELAB_KAPPA * yyn1; }
	 * Luv[0] = yyn1; Luv[1] = 13.0 * yyn1 * (up - upn); Luv[2] = 13.0 * yyn1 *
	 * (vp - vpn); return Luv; }
	 */
	/**
	 * Convert from CIE L*u*v* coordinates to XYZ tristimulus values.
	 */
	public static PxlColor fromLuvStar(double[] Luv) {
		double[] wp = getCIEWhitePointXYZ();
		double tn = wp[0] + 15.0 * wp[1] + 3.0 * wp[2];
		// System.out.println("  tn = " + tn);
		double upn = 4.0 * wp[0] / tn;
		// System.out.println(" upn = " + upn);
		double vpn = 9.0 * wp[1] / tn;
		// System.out.println(" vpn = " + vpn);
		double yyn1 = (Luv[0] + 16.0) / 116.0;
		// System.out.println("yyn1 = " + yyn1);
		double Y = wp[1] * yyn1 * yyn1 * yyn1;
		// System.out.println("   Y = " + Y);
		double drL = 13.0 * Luv[0];
		// System.out.println(" drL = " + drL);
		double up = Luv[1] / drL + upn;
		// System.out.println("  up = " + up);
		double vp = Luv[2] / drL + vpn;
		// System.out.println("  vp = " + vp);
		double fvpY = Y / (4.0 * vp);
		// System.out.println("fvpY = " + fvpY);
		double X = 9.0 * up * fvpY;
		// double Z1 = (36.0 - 60.0*vp - 9.0*up) * fvpY;
		// double Z2 = (3.0*up + 20.0*vp -12.0) * fvpY;
		double Z3 = (4.0 * X - up * (X + 15.0 * Y)) / (3.0 * up);
		// double Z4 = (9.0*Y - vp*(X+15.0*Y))/(3.0*vp);
		// System.out.println("  Z1 = " + Z1);
		// System.out.println("  Z2 = " + Z2);
		// System.out.println("  Z3 = " + Z3);
		// System.out.println("  Z4 = " + Z4);
		return (new PxlColor(X, Y, Z3));
	}

	/*
	 * Bruce Lindbloom version public static PxlColor fromLuvStar(double[] Luv)
	 * {
	 * 
	 * double[] wp = getCIEWhitePointXYZ(); double tn = wp[0] + 15.0*wp[1] +
	 * 3.0*wp[2]; double upn = 4.0*wp[0]/tn; double vpn = 9.0*wp[1]/tn;
	 * 
	 * double Y = Luv[0]; if (Y > CIELAB_KAPPA_EPSI) { Y = (Y + 16.0)/116.0; Y =
	 * Y * Y * Y; } else { Y /= CIELAB_KAPPA; } double a = ((52.0 *
	 * Luv[0])/(Luv[1] + 13.0 * Luv[0] * upn) - 1.0)/3.0; double b = -5.0 * Y;
	 * double c = - 1.0/3.0; double d = Y * ((39.0 * Luv[0])/(Luv[2] + 13.0 *
	 * Luv[0] * vpn) - 5.0);
	 * 
	 * double X = (d - a)/(a -c); double Z = X * a + b;
	 * 
	 * return new PxlColor(X, Y, Z); }
	 */
	public double[] toLuvLChStar() {
		double[] Luv = this.toLuvStar();
		double[] LCh = new double[3];
		LCh[0] = Luv[0];
		LCh[1] = Math.sqrt(Luv[1] * Luv[1] + Luv[2] * Luv[2]);
		LCh[2] = Math.atan2(Luv[2], Luv[1]) / Math.PI * 180.0;
		return LCh;
	}

	public static PxlColor fromLuvLChStar(double[] LCh) {
		double[] Luv = new double[3];
		double h = LCh[2] / 180.0 * Math.PI;
		Luv[0] = LCh[0];
		Luv[1] = LCh[1] * Math.cos(h);
		Luv[2] = LCh[1] * Math.sin(h);
		return fromLuvStar(Luv);
	}

	/**
	 * Premultiply the PxlColor object c by the 3x3-matrix m and return the
	 * resulting 3-dimensional vector.
	 */
	public static double[] mul(double[][] m, PxlColor c) {
		double[] x = new double[3];
		x[0] = m[0][0] * c.X + m[0][1] * c.Y + m[0][2] * c.Z;
		x[1] = m[1][0] * c.X + m[1][1] * c.Y + m[1][2] * c.Z;
		x[2] = m[2][0] * c.X + m[2][1] * c.Y + m[2][2] * c.Z;
		return (x);
	}

	/**
	 * Create a linear ramp with n points from this color to the color given as
	 * an argument. Both limiting colors are included in the return array.
	 */
	public PxlColor[] linearRampTo(PxlColor c, int n) {
		if (n < 2)
			return (null);
		double[] a = this.getComponents();
		double[] b = c.getComponents();
		double dX = (b[0] - a[0]) / (n - 1.0f);
		double dY = (b[1] - a[1]) / (n - 1.0f);
		double dZ = (b[2] - a[2]) / (n - 1.0f);
		PxlColor[] p = new PxlColor[n];
		p[0] = this;
		for (int i = 1; i < (n - 1); i++) {
			a[0] += dX;
			a[1] += dY;
			a[2] += dZ;
			p[i] = new PxlColor(a);
		}
		p[n - 1] = c;
		return (p);
	}

	/**
	 * Create a sinusoidal ramp with n points from this color to the color given
	 * as an argument. Both limiting colors are included in the return array.
	 */
	public PxlColor[] sinusoidalRampTo(PxlColor c, int n) {
		if (n < 2)
			return (null);
		PxlColor[] p = new PxlColor[n];
		p[0] = this;
		double ds = Math.PI / (n - 1.0);
		double d = ds;
		double w;
		for (int i = 1; i < (n - 1); i++) {
			w = (1.0 + Math.cos(d)) / 2.0;
			// System.out.println(i + ": w = " + w);
			p[i] = this.mix(w, c);
			d += ds;
		}
		p[n - 1] = c;
		return (p);
	}

	/**
	 * Create a square root ramp with n points from this color to the color
	 * given as an argument. Both limiting colors are included in the return
	 * array.
	 */
	public PxlColor[] squareRootRampTo(PxlColor c, int n) {
		if (n < 2)
			return (null);
		PxlColor[] p = new PxlColor[n];
		p[0] = this;
		double w;
		for (int i = 1; i < (n - 1); i++) {
			w = Math.sqrt(2.0 * (n - 1.0) * (n - 1.0 - i) - (n - 1.0 - i)
					* (n - 1.0 - i))
					/ (n - 1.0);
			// System.out.println(i + ": w = " + w);
			p[i] = this.mix(w, c);
		}
		p[n - 1] = c;
		return (p);
	}

	/**
	 * Create a logarithmic ramp with n points from this color to the color
	 * given as an argument. The logarithmic stepping is applied to all of the
	 * X, Y, and Z coordinates. Both limiting colors are included in the return
	 * array.
	 */
	public PxlColor[] logarithmicRampTo(PxlColor c, int n) {
		if (n < 2)
			return (null);
		double[] a = this.getComponents();
		double[] b = c.getComponents();
		double dX = Math.log(b[0] / a[0]) / (n - 1.0f);
		double dY = Math.log(b[1] / a[1]) / (n - 1.0f);
		double dZ = Math.log(b[2] / a[2]) / (n - 1.0f);
		PxlColor[] p = new PxlColor[n];
		p[0] = this;
		for (int i = 1; i < (n - 1); i++) {
			a[0] *= Math.exp(dX);
			a[1] *= Math.exp(dY);
			a[2] *= Math.exp(dZ);
			p[i] = new PxlColor(a);
		}
		p[n - 1] = c;
		return (p);
	}

	/** Return the current color device's white point. */
	/*
	 * public static PxlColor getWhitePoint() { return(new
	 * PxlColor(device.getWhitePoint())); }
	 */
	/**
	 * Compute the maximum luminance for this color on the current color device.
	 * Make sure that we also can compute the maximum luminance of a color whose
	 * chromaticity coordinates are meaningfully defined but whose current
	 * luminance is zero.
	 */
	public double maxLum() {
		double[] a;
		if (Y <= 0.0) {
			PxlColor m = new PxlColor(this.x / this.y, 1.0,
					(1.0 - this.x - this.y) / this.y);
			a = device.max(m.getComponents());
		} else {
			a = device.max(this.getComponents());
		}
		return (a[1]);
	}
	/**
	 * RGB coordinates which are larger than this value may savely be considered
	 * to be nonnegative values.
	 */
	private double RGBTolerance = -1.0 / 512.0;

	/**
	 * Returns true if this color has a valid chromaticity. This means that
	 * there exists a luminance for this chromaticity such that a color with
	 * this chromaticity can be displayed.
	 */
	public boolean hasValidChromaticity() {
		double[] a = this.transform(CS_RGB);
		return ((RGBTolerance < a[0]) && (RGBTolerance < a[1]) && (RGBTolerance < a[2]));
	}

	/**
	 * Returns true if this color can be displayed on the current color device.
	 */
	public boolean isDisplayable() {
		double[] a = this.transform(CS_RGB);
		// System.out.println("  RGB: " + colorToString(a));
		return ((RGBTolerance < a[0]) && (RGBTolerance < a[1])
				&& (RGBTolerance < a[2]) && (a[0] <= 1.0) && (a[1] <= 1.0) && (a[2] <= 1.0));
	}

	/**
	 * Returns true if the chromaticity coordinates given in the argument are
	 * valid (x,y)-chromaticities for the current output device.
	 */
	public boolean isValidChromaticity(double x, double y) {
		double[] a = { 1.0, x, y };
		PxlColor c = instance(CS_Yxy, a);
		return (c.hasValidChromaticity());
	}

	/**
	 * Compute the additive mixture of this color with the color given as an
	 * argument.
	 */
	public PxlColor add(PxlColor c) {
		return (new PxlColor(this.X + c.X, this.Y + c.Y, this.Z + c.Z));
	}

	/**
	 * Compute the convex mixture of this color with the color given as an
	 * argument with each color getting the same weight in the mixture.
	 */
	public PxlColor mix(PxlColor c) {
		return (mix(0.5, c));
	}

	/**
	 * Compute the weighted convex mixture of this color with the color given as
	 * an argument. The parameter a specifies the weight for this color and
	 * (1-a) is the weight for the argument color. Note that a must satisfy 0.0
	 * <= a <= 1.0.
	 */
	public PxlColor mix(double a, PxlColor c) {
		if (a < 0.0)
			a = 0.0;
		if (a > 1.0)
			a = 1.0;
		double b = 1.0 - a;
		return (new PxlColor(a * this.X + b * c.X, a * this.Y + b * c.Y, a
				* this.Z + b * c.Z));
	}

	/**
	 * Compute an intensity scaled version of this color.
	 * 
	 * @param s
	 *            scalar factor for scaling this color.
	 */
	public PxlColor scaled(double s) {
		return (new PxlColor(s * this.X, s * this.Y, s * this.Z));
	}

	/**
	 * Premultiply the 3-dimensional vector p by the 3x3-matrix m and return the
	 * resulting 3-dimensional vector.
	 */
	private static double[] mul(double[][] m, double[] p) {
		double[] x = new double[3];
		x[0] = m[0][0] * p[0] + m[0][1] * p[1] + m[0][2] * p[2];
		x[1] = m[1][0] * p[0] + m[1][1] * p[1] + m[1][2] * p[2];
		x[2] = m[2][0] * p[0] + m[2][1] * p[1] + m[2][2] * p[2];
		return (x);
	}

	/** Create a string representation of this color. */
	public String toString() {
		/*
		 * String xs = String.valueOf(X); if (xs.length() > 6) xs =
		 * xs.substring(0, 5); String ys = String.valueOf(Y); if (ys.length() >
		 * 6) ys = ys.substring(0, 5); String zs = String.valueOf(Z); if
		 * (zs.length() > 6) zs = zs.substring(0, 5);
		 * return(getClass().getName() + "[X="+xs+",Y="+ys+",Z="+zs+"]");
		 */
		/*
		 * String Ys = String.valueOf(Y); if (Ys.length() > 7) Ys =
		 * Ys.substring(0, 6); String xs = String.valueOf(x); if (xs.length() >
		 * 6) xs = xs.substring(0, 5); String ys = String.valueOf(y); if
		 * (ys.length() > 6) ys = ys.substring(0, 5); return
		 * (getClass().getName() + "[Y="+Ys+",x="+xs+",y="+ys+"]");
		 */
		return "[" + colorCoordinate.format(Y) + ","
				+ chromaticityCoordinate.format(x) + ","
				+ chromaticityCoordinate.format(y) + "]";
	}

	public static String colorToString(double[] d) {
		return "[" + colorCoordinate.format(d[0]) + ","
				+ colorCoordinate.format(d[1]) + ","
				+ colorCoordinate.format(d[2]) + "]";
	}
	private static final double CubeRMin = 0.0;
	private static final double CubeRMax = 1.0;
	private static final double CubeGMin = 0.0;
	private static final double CubeGMax = 1.0;
	private static final double CubeBMin = 0.0;
	private static final double CubeBMax = 1.0;

	/**
	 * Clip this color into the valid color polytope. The algorithm used is from
	 * Foley & van Dam: the Sutherland algorithm for 3D-clipping. Note that the
	 * point to be clipped to must be located within the clipping cube. Thus its
	 * luminance must be less or equal to maxLum().
	 */
	public PxlColor clipped() {
		// Find the clipping reference. Preferable this is that point on
		// the gray axis, which has the same luminance as the color to
		// be clipped. However, we have to make sure that the clipping
		// reference is within the valid cube.
		// First get our white point chromaticity
		PxlColor wp = new PxlColor(device.getWhitePoint());
		// Now set the white point luminance equal to the target if
		// possible
		if (this.Y < wp.Y)
			wp.setY(this.Y);
		// Now move the reference white point to RGB space
		double[] w = wp.transform(CS_RGB);
		// Also transform this color to RGB space
		double[] c = this.transform(CS_RGB);
		// Now we are ready to start the clipper
		boolean c0 = false, c1 = false, c2 = false, c3 = false, c4 = false, c5 = false, clip_any = false;
		double r, g, b, r0, g0, b0, t;
		// Move to simple variables for reasons of speed.
		r = c[0];
		g = c[1];
		b = c[2];
		r0 = w[0];
		g0 = w[1];
		b0 = w[2];
		while ((clip_any = ((c0 = (r < CubeRMin)) || (c1 = (r > CubeRMax))
				|| (c2 = (g < CubeGMin)) || (c3 = (g > CubeGMax))
				|| (c4 = (b < CubeBMin)) || (c5 = (b > CubeBMax))))) {
			if (c0) { /* clip against CubeRMin */
				t = (CubeRMin - r0) / (r - r0);
				r = CubeRMin;
				g = g0 + t * (g - g0);
				b = b0 + t * (b - b0);
			} else if (c1) { /* clip against CubeRMax */
				t = (CubeRMax - r0) / (r - r0);
				r = CubeRMax;
				g = g0 + t * (g - g0);
				b = b0 + t * (b - b0);
			} else if (c2) { /* clip against CubeGMin */
				t = (CubeGMin - g0) / (g - g0);
				g = CubeGMin;
				r = r0 + t * (r - r0);
				b = b0 + t * (b - b0);
			} else if (c3) { /* clip against CubeGMax */
				t = (CubeGMax - g0) / (g - g0);
				g = CubeGMax;
				r = r0 + t * (r - r0);
				b = b0 + t * (b - b0);
			} else if (c4) { /* clip against CubeBMin */
				t = (CubeBMin - b0) / (b - b0);
				b = CubeBMin;
				r = r0 + t * (r - r0);
				g = g0 + t * (g - g0);
			} else if (c5) { /* clip against CubeBMax */
				t = (CubeBMax - b0) / (b - b0);
				b = CubeBMax;
				r = r0 + t * (r - r0);
				g = g0 + t * (g - g0);
			}
		}
		// We are done, so move back to XYZ space
		c[0] = r;
		c[1] = g;
		c[2] = b;
		return (instance(CS_RGB, c));
	}
}
