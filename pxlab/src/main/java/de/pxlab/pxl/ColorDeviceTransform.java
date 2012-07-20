package de.pxlab.pxl;

import java.awt.Color;
import java.util.*;

import de.pxlab.util.StringExt;

/**
 * A device color space transformation including the linear color space
 * transform and gamma correction. The color space is defined by setting its
 * basis, its white point, and the device's gamma correction parameters.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 12/13/03
 * 
 * 2007/10/08 added color table lookup
 */
public class ColorDeviceTransform extends LinearColorTransform {
	/** The gain parameter of the red channel. */
	protected double redGain = 1.0;
	/** The offset parameter of the red channel. */
	protected double redOffset = 0.0;
	/** The gamma parameter of the red channel. */
	protected double redGamma = 2.2;
	/** The gain parameter of the green channel. */
	protected double greenGain = 1.0;
	/** The offset parameter of the green channel. */
	protected double greenOffset = 0.0;
	/** The gamma parameter of the green channel. */
	protected double greenGamma = 2.2;
	/** The gain parameter of the blue channel. */
	protected double blueGain = 1.0;
	/** The offset parameter of the blue channel. */
	protected double blueOffset = 0.0;
	/** The gamma parameter of the blue channel. */
	protected double blueGamma = 2.2;
	/**
	 * Scaling factor to map the range [0.0, ... 1.0] to the device's range of
	 * DAC values.
	 */
	public double DACRange;
	/**
	 * If true then every color which is converted to device coordinates is
	 * checked whether this device is able to display it or not.
	 */
	private boolean checkColorGamut = false;
	/** Default red gamma. */
	private double[] rGamma = { 2.2 };
	/** Default green gamma. */
	private double[] gGamma = { 2.2 };
	/** Default blue gamma. */
	private double[] bGamma = { 2.2 };
	public double[] white;
	public double[] lightGray;
	public double[] gray;
	public double[] darkGray;
	public double[] black;
	public double[] red;
	public double[] yellow;
	public double[] green;
	public double[] magenta;
	public double[] cyan;
	public double[] blue;
	private HashMap devColorTable = null;

	/**
	 * Create a default color device transform for a sRGB device with a white
	 * point at 100 cd/qm and a DAC resolution of 8 bits per channel.
	 */
	public ColorDeviceTransform() {
		super();
		setGamma(rGamma, gGamma, bGamma);
		setDACRange(255.0);
		white = inverseDev(Color.white);
		lightGray = inverseDev(Color.lightGray);
		gray = inverseDev(Color.gray);
		darkGray = inverseDev(Color.darkGray);
		black = inverseDev(Color.black);
		red = inverseDev(Color.red);
		yellow = inverseDev(Color.yellow);
		green = inverseDev(Color.green);
		magenta = inverseDev(Color.magenta);
		cyan = inverseDev(Color.cyan);
		blue = inverseDev(Color.blue);
	}

	/**
	 * Set this transform's gamma parameters.
	 * 
	 * @param r
	 *            array containing the red primary's gamma parameters.
	 * @param g
	 *            array containing the green primary's gamma parameters.
	 * @param b
	 *            array containing the blue primary's gamma parameters.
	 */
	public void setGamma(double[] r, double[] g, double[] b) {
		if (r.length < 2) {
			setRedGammaPars(r[0], 1.0);
		} else {
			setRedGammaPars(r[0], r[1]);
		}
		if (g.length < 2) {
			setGreenGammaPars(g[0], 1.0);
		} else {
			setGreenGammaPars(g[0], g[1]);
		}
		if (b.length < 2) {
			setBlueGammaPars(b[0], 1.0);
		} else {
			setBlueGammaPars(b[0], b[1]);
		}
		/*
		 * if (Debug.isActive(Debug.COLDEV)) { System.out.println(this); }
		 */
	}

	/**
	 * Set the maximum output level for a single color channel. This parameter
	 * is needed int order to compute the correct device output value.
	 */
	public void setDACRange(double d) {
		DACRange = d;
	}

	/** Return this device's DAC mapping factor. */
	public double getDACRange() {
		return (DACRange);
	}

	/** Set this device's gamma parameters for the red channel. */
	public void setRedGammaPars(double gamma, double gain) {
		redGamma = gamma;
		redGain = gain;
		redOffset = 1.0 - gain;
	}

	public double[] getRedGammaPars() {
		double[] r = new double[2];
		r[0] = redGamma;
		r[1] = redGain;
		return r;
	}

	/** Set this device's gamma parameters for the green channel. */
	public void setGreenGammaPars(double gamma, double gain) {
		greenGamma = gamma;
		greenGain = gain;
		greenOffset = 1.0 - gain;
	}

	public double[] getGreenGammaPars() {
		double[] r = new double[2];
		r[0] = greenGamma;
		r[1] = greenGain;
		return r;
	}

	/** Set this device's gamma parameters for the blue channel. */
	public void setBlueGammaPars(double gamma, double gain) {
		blueGamma = gamma;
		blueGain = gain;
		blueOffset = 1.0 - gain;
	}

	public double[] getBlueGammaPars() {
		double[] r = new double[2];
		r[0] = blueGamma;
		r[1] = blueGain;
		return r;
	}
	private static final double LO = -0.00001;
	private static final double HI = 1.00001;

	/**
	 * Return the device control values for the XYZ color given as an argument.
	 * Applications should not call this method directly instead they should use
	 * methods of the PxlColor class to access device colors.
	 */
	public Color dev(double[] p) {
		if (devColorTable != null) {
			Object d = devColorTable.get(hashKey(p));
			if (d != null) {
				// System.out.println("ColorDeviceTransform.dev() Color Table Entry "
				// + StringExt.valueOf(p) + " mapped to " + (Color)d);
				return (Color) d;
			}
		}
		// System.out.println("dev(): XYZ = [" + p[0] + ", " + p[1] + ", " +
		// p[2] + "]");
		double[] a = mul(mXYZToRGB, p);
		if (checkColorGamut) {
			if (!((LO <= a[0]) && (a[0] <= HI) && (LO <= a[1]) && (a[1] <= HI)
					&& (LO <= a[2]) && (a[2] <= HI))) {
				String nl = System.getProperty("line.separator");
				System.out.println(nl + "PxlColorDevice.dev(): Invalid Color:");
				PxlColor cc = new PxlColor(p[0], p[1], p[2]);
				System.out.println("  Yxy = " + cc.toString());
				System.out.println("  RGB = ["
						+ PxlColor.colorCoordinate.format(a[0]) + ", "
						+ PxlColor.colorCoordinate.format(a[1]) + ", "
						+ PxlColor.colorCoordinate.format(a[2]) + "]");
			}
		}
		// System.out.println("dev(): RGB = [" + a[0] + ", " + a[1] + ", " +
		// a[2] + "]");
		double r = redInverseDev(a[0] /* /maxLumRed */);
		if (r < 0.0)
			r = 0.0;
		if (r > 1.0)
			r = 1.0;
		double g = greenInverseDev(a[1] /* /maxLumGreen */);
		if (g < 0.0)
			g = 0.0;
		if (g > 1.0)
			g = 1.0;
		double b = blueInverseDev(a[2] /* /maxLumBlue */);
		if (b < 0.0)
			b = 0.0;
		if (b > 1.0)
			b = 1.0;
		// System.out.println("dev(): rgb = [" + r + ", " + g + ", " + b + "]");
		return (new Color((float) r, (float) g, (float) b));
	}

	/**
	 * Control color gamut checking.
	 * 
	 * @param s
	 *            if true then every PxlColor used is checked whether it
	 *            actually is displayable on this device and a warning message
	 *            is printed if this is not the case.
	 */
	public void setCheckColorGamut(boolean s) {
		checkColorGamut = s;
	}

	/**
	 * Get the current state of color gamut checking.
	 * 
	 * @return if true then every PxlColor used is checked whether it actually
	 *         is displayable on this device and a warning message is printed if
	 *         this is not the case.
	 */
	public boolean getCheckColorGamut() {
		return checkColorGamut;
	}

	/**
	 * Return the gamma corrected device control values for the XYZ color given
	 * as an argument. Applications should not call this method directly instead
	 * they should use methods of the PxlColor class to access device colors.
	 * 
	 * @param p
	 *            array of XYZ-coordinates.
	 * @return an array of double values which are device RGB values in the
	 *         range 0.0 <= RGB <= 1.0. Gamma correction has already been
	 *         applied.
	 */
	public double[] devRGB(double[] p) {
		// System.out.println("devRGB(): XYZ = [" + p[0] + ", " + p[1] + ", " +
		// p[2] + "]");
		double[] a = mul(mXYZToRGB, p);
		double[] rgb = new double[3];
		// System.out.println("devRGB(): RGB = [" + a[0] + ", " + a[1] + ", " +
		// a[2] + "]");
		double r = redInverseDev(a[0] /* /maxLumRed */);
		if (r < 0.0)
			r = 0.0;
		if (r > 1.0)
			r = 1.0;
		double g = greenInverseDev(a[1] /* /maxLumGreen */);
		if (g < 0.0)
			g = 0.0;
		if (g > 1.0)
			g = 1.0;
		double b = blueInverseDev(a[2] /* /maxLumBlue */);
		if (b < 0.0)
			b = 0.0;
		if (b > 1.0)
			b = 1.0;
		// System.out.println("devRGB(): rgb = [" + r + ", " + g + ", " + b +
		// "]");
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;
		return (rgb);
	}

	/**
	 * Return the XYZ coordinates of the given device color. Applications should
	 * not call this method driectly instead they should use methods of the
	 * PxlColor class to access device colors.
	 */
	public double[] inverseDev(Color c) {
		double[] a = { c.getRed() / DACRange, c.getGreen() / DACRange,
				c.getBlue() / DACRange };
		// System.out.println("inverseDev(): a = " + Utils.stringOfArray(a));
		double[] b = { redDev(a[0]), greenDev(a[1]), blueDev(a[2]) };
		// System.out.println("inverseDev(): b = " + Utils.stringOfArray(b));
		double[] d = mul(mRGBToXYZ, b);
		// System.out.println("inverseDev(): d = " + Utils.stringOfArray(d));
		return (d);
	}

	/**
	 * Return the red channel's true device output value for a given linear R
	 * value from the device's RGB space.
	 */
	protected double redDev(double x) {
		double y = redGain * x + redOffset;
		return (y > 0.0) ? Math.pow(y, redGamma) : 0.0;
	}

	/**
	 * Return the green channel's true device output value for a given linear G
	 * value from the device's RGB space.
	 */
	protected double greenDev(double x) {
		double y = greenGain * x + greenOffset;
		return (y > 0.0) ? Math.pow(y, greenGamma) : 0.0;
	}

	/**
	 * Return the blue channel's true device output value for a given linear B
	 * value from the device's RGB space.
	 */
	protected double blueDev(double x) {
		double y = blueGain * x + blueOffset;
		return (y > 0.0) ? Math.pow(y, blueGamma) : 0.0;
	}

	/**
	 * Return the red channel's linear R value from its RGB space which must be
	 * used to arrive at the given device output.
	 */
	protected double redInverseDev(double x) {
		return ((Math.pow(x, 1.0 / redGamma) - redOffset) / redGain);
	}

	/**
	 * Return the green channel's linear G value from its RGB space which must
	 * be used to arrive at the given device output.
	 */
	protected double greenInverseDev(double x) {
		return ((Math.pow(x, 1.0 / greenGamma) - greenOffset) / greenGain);
	}

	/**
	 * Return the blue channel's linear B value from its RGB space which must be
	 * used to arrive at the given device output.
	 */
	protected double blueInverseDev(double x) {
		return ((Math.pow(x, 1.0 / blueGamma) - blueOffset) / blueGain);
	}

	protected double devLum(double x, double g, double a, double b) {
		double r = a * x + b;
		return ((r <= 0.0) ? 0.0 : Math.pow(r, g));
	}

	public void printSystemColors() {
		String nl = System.getProperty("line.separator");
		System.out.println(nl + "System colors:");
		for (int i = PxlColor.WHITE; i <= PxlColor.BLUE; i++) {
			ExParExpression x = new ExParExpression(ExParExpression.WHITE + i);
			System.out.println(" " + x.toString(null, null, null) + " = "
					+ PxlColor.systemColor(i));
		}
		System.out.println("");
	}

	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		String nl = System.getProperty("line.separator");
		s.append(nl + " Gamma Functions:");
		s.append(nl + "   red: [gain=" + redGain + ", gamma=" + redGamma + "]");
		s.append(nl + " green: [gain=" + greenGain + ", gamma=" + greenGamma
				+ "]");
		s.append(nl + "  blue: [gain=" + blueGain + ", gamma=" + blueGamma
				+ "]");
		return (new String(s));
	}

	/**
	 * Set a device color table for this device. The device color table file
	 * must contain a single line of text for every color. Every line may
	 * contain arbitrary information which is followed by at least 6 values: The
	 * first three values of these are the CIE XYZ-values of the target color
	 * and the last 3 values are the integer RGB entries for the device color
	 * output. Only the last 6 entries on each line are interpreted, all other
	 * values in a single line are ignored.
	 * 
	 * @param fn
	 *            the file name of the device color table. The file must be in
	 *            the same directory as the design file.
	 */
	public void setDeviceColorTable(String fn) {
		Debug.show(Debug.FILES, "ColorDeviceTransform.setDeviceColorTable("
				+ fn + ")");
		String[] t = FileBase.loadStrings(".", fn);
		if (t != null && t.length > 0) {
			Debug.show(Debug.COLOR_DEVICE,
					"ColorDeviceTransform.setDeviceColorTable() color table file has been read.");
			devColorTable = new HashMap(3 * t.length);
			double[] p = new double[3];
			int[] c = new int[3];
			ArrayList n = new ArrayList(20);
			int count = 0;
			for (int i = 0; i < t.length; i++) {
				StringTokenizer st = new StringTokenizer(t[i]);
				while (st.hasMoreTokens()) {
					n.add(st.nextToken());
				}
				int m = n.size();
				if (m >= 6) {
					for (int j = 0; j < 3; j++) {
						try {
							p[j] = Double.valueOf((String) n.get(m - 6 + j))
									.doubleValue();
						} catch (NumberFormatException nfx) {
							p[j] = 1.0;
						}
					}
					for (int j = 0; j < 3; j++) {
						try {
							c[j] = Integer.valueOf((String) n.get(m - 3 + j))
									.intValue();
						} catch (NumberFormatException nfx) {
							c[j] = 0;
						}
					}
					n.clear();
					devColorTable.put(hashKey(p), new Color(c[0], c[1], c[2]));
					Debug.show(
							Debug.COLOR_DEVICE,
							"ColorDeviceTransform.setDeviceColorTable() entered "
									+ StringExt.valueOf(p) + " -> "
									+ StringExt.valueOf(c));
					count++;
				}
			}
			Debug.show(Debug.COLOR_DEVICE,
					"ColorDeviceTransform.setDeviceColorTable() " + count
							+ " values entered.");
		} else {
			Debug.show(Debug.COLOR_DEVICE,
					"ColorDeviceTransform.setDeviceColorTable() file " + fn
							+ " is empty or can't be opened.");
		}
	}
	private static final double NF = 100.0;
	private static final long SF = 100000L;

	/** Create a hash key for the given XYZ-coordinates. */
	private Object hashKey(double[] x) {
		long k = SF * SF * (long) (NF * x[0]) + SF * (long) (NF * x[1])
				+ (long) (NF * x[2]);
		return new Long(k);
	}
}
