package de.pxlab.pxl;

import java.util.ArrayList;

import de.pxlab.util.StringExt;

/**
 * A linear color space transformation. The color space is defined by setting
 * its basis and its white point.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 12/13/03
 */
public class LinearColorTransform {
	/**
	 * This matrix handles the linear part of the conversion from device RGB
	 * coordinates to the CIE XYZ tristimulus values.
	 */
	protected double[][] mRGBToXYZ;
	/**
	 * This matrix handles the linear part of the conversion from CIE XYZ
	 * tristimulus values to device RGB coordinates.
	 */
	protected double[][] mXYZToRGB;
	/**
	 * The maximum luminance which can be produced by the red channel.
	 */
	protected double maxLumRed;
	/**
	 * The maximum luminance which can be produced by the green channel.
	 */
	protected double maxLumGreen;
	/**
	 * The maximum luminance which can be produced by the blue channel.
	 */
	protected double maxLumBlue;
	/**
	 * The CIE 1931 XYZ chromaticity coordinates of the white point of this
	 * device.
	 */
	protected double[] whitePoint;
	private static double[][] sRGBPhosphors = { { 21.26, 0.640, 0.330 },
			{ 71.52, 0.290, 0.600 }, { 7.22, 0.150, 0.060 } };

	/**
	 * Create a linear color space transformation to a default device. The
	 * default device is a RGB device whose primaries correspond to the
	 * primaries of sRGB space and whose white point creates a luminance of 100
	 * cd/qm.
	 */
	public LinearColorTransform() {
		setPrimaries(sRGBPhosphors);
	}

	/**
	 * Create a linear color space transformation to a device having the given
	 * primaries.
	 * 
	 * @param m
	 *            a matrix which contains the device's primaries as its row
	 *            vectors.
	 */
	public LinearColorTransform(double[][] m) {
		setPrimaries(m);
	}

	/**
	 * Create a linear color space transformation to a device having the given
	 * primaries.
	 * 
	 * @param r
	 *            an array which contains the device red primary coordinates.
	 * @param g
	 *            an array which contains the device green primary coordinates.
	 * @param b
	 *            an array which contains the device blue primary coordinates.
	 */
	public LinearColorTransform(double[] r, double[] g, double[] b) {
		double[][] m = new double[3][];
		m[0] = r;
		m[1] = g;
		m[2] = b;
		setPrimaries(m);
	}

	/**
	 * Set the primaries of this transform. Implicitly also sets the white point
	 * and the maximum luminance values for each primary.
	 * 
	 * @param p
	 *            a matrix containing the CIE Yxy-coordinates of the device's
	 *            RGB-primaries. Ordering is such that p[0] contains the
	 *            Yxy-coordinates of the red, p[1] the green and p[2] the blue
	 *            primary vector. The Y-coordinate should define the maximum
	 *            luminance of each primary and the three basis vectors should
	 *            add to the device's white point.
	 */
	public void setPrimaries(double[][] p) {
		/*
		 * if (Debug.isActive(Debug.COLDEV)) {
		 * System.out.println("\nLinearColorTransform.setPrimaries()");
		 * System.out.println("Input Matrix:" ); showMatrix(p); }
		 */
		double[] r = XYZfromYxy(p[0]);
		double[] g = XYZfromYxy(p[1]);
		double[] b = XYZfromYxy(p[2]);
		mRGBToXYZ = createColumnMatrix(r, g, b);
		mXYZToRGB = new double[3][3];
		invertMatrix(mRGBToXYZ, mXYZToRGB);
		maxLumRed = p[0][0];
		maxLumGreen = p[1][0];
		maxLumBlue = p[2][0];
		double[] wp = { 1.0, 1.0, 1.0 };
		whitePoint = mul(mRGBToXYZ, wp);
		/*
		 * if (Debug.isActive(Debug.COLDEV)) {
		 * System.out.println("New white point is " +
		 * StringExt.valueOf(whitePoint)); }
		 */
	}

	/**
	 * Return this color transform's linear RGB representation of the given XYZ
	 * coordinates.
	 * 
	 * @param XYZ
	 *            the CIE XYZ coordinates of a color.
	 * @return this transform's coordinates of the argument color.
	 */
	public double[] toLinearRGB(double[] XYZ) {
		return (mul(mXYZToRGB, XYZ));
	}

	/**
	 * Return the XYZ coordinates of the linear RGB representation given as an
	 * argument.
	 * 
	 * @param RGB
	 *            a color's coordinates as defined by this transform.
	 * @return the CIE XYZ coordinates of the argument color.
	 */
	public double[] fromLinearRGB(double[] RGB) {
		return (mul(mRGBToXYZ, RGB));
	}

	/** Return the red channel's basis vector in XYZ coordinates. */
	public double[] getRedBasis() {
		double[] p = new double[3];
		p[0] = mRGBToXYZ[0][0];
		p[1] = mRGBToXYZ[1][0];
		p[2] = mRGBToXYZ[2][0];
		return (p);
	}

	/** Return the green channel's basis vector in XYZ coordinates. */
	public double[] getGreenBasis() {
		double[] p = new double[3];
		p[0] = mRGBToXYZ[0][1];
		p[1] = mRGBToXYZ[1][1];
		p[2] = mRGBToXYZ[2][1];
		return (p);
	}

	/** Return the blue channel's basis vector in XYZ coordinates. */
	public double[] getBlueBasis() {
		double[] p = new double[3];
		p[0] = mRGBToXYZ[0][2];
		p[1] = mRGBToXYZ[1][2];
		p[2] = mRGBToXYZ[2][2];
		return (p);
	}

	/**
	 * Return the device's white point coordinates array in XYZ space.
	 */
	public double[] getWhitePoint() {
		return (whitePoint);
	}

	/**
	 * Set the device's white point coordinates array in XYZ space.
	 */
	public void setWhitePoint(double[] wp) {
		whitePoint[0] = wp[0];
		whitePoint[1] = wp[1];
		whitePoint[2] = wp[2];
	}

	public double getMaxLumRed() {
		return maxLumRed;
	}

	public double getMaxLumGreen() {
		return maxLumGreen;
	}

	public double getMaxLumBlue() {
		return maxLumBlue;
	}

	/**
	 * Check whether the given color can be displayed on this device. Returns
	 * true if it can.
	 */
	public boolean canDisplay(PxlColor c) {
		double[] a = mul(mXYZToRGB, c.getComponents());
		return ((0.0 <= a[0]) && (a[0] <= 1.0) && (0.0 <= a[1])
				&& (a[1] <= 1.0) && (0.0 <= a[2]) && (a[2] <= 1.0));
	}
	/**
	 * A list of vertices which describe the convex region of valid colors in
	 * XYZ space with respect to a certain luminance value.
	 */
	private ArrayList validRegion = new ArrayList(6);

	/**
	 * Enter a vertex from RGB space into the validRegion array after
	 * transforming it to XYZ space.
	 */
	private void enterVertex(double r, double g, double b) {
		double[] p = { r / maxLumRed, g / maxLumGreen, b / maxLumBlue };
		validRegion.add(new PxlColor(mul(mRGBToXYZ, p)));
	}

	/**
	 * Get the vertices which span the subregion of the color space which
	 * actually can be displayed by this device at the luminance level given as
	 * an argument.
	 */
	public ArrayList getValidRegionVertices(double Y) {
		validRegion.clear();
		if (Y <= maxLumBlue) {
			enterVertex(Y, 0.0, 0.0);
			enterVertex(0.0, Y, 0.0);
			enterVertex(0.0, 0.0, Y);
		} else if (Y <= maxLumRed) {
			enterVertex(Y, 0.0, 0.0);
			enterVertex(0.0, Y, 0.0);
			enterVertex(0.0, Y - maxLumBlue, maxLumBlue);
			enterVertex(Y - maxLumBlue, 0.0, maxLumBlue);
		} else if (Y < (maxLumRed + maxLumBlue)) {
			enterVertex(maxLumRed, Y - maxLumRed, 0.0);
			enterVertex(0.0, Y, 0.0);
			enterVertex(0.0, Y - maxLumBlue, maxLumBlue);
			enterVertex(Y - maxLumBlue, 0.0, maxLumBlue);
			enterVertex(maxLumRed, 0.0, Y - maxLumRed);
		} else if (Y <= maxLumGreen) {
			enterVertex(maxLumRed, Y - maxLumRed, 0.0);
			enterVertex(0.0, Y, 0.0);
			enterVertex(0.0, Y - maxLumBlue, maxLumBlue);
			enterVertex(maxLumRed, Y - maxLumRed - maxLumBlue, maxLumBlue);
		} else if (Y < (maxLumGreen + maxLumBlue)) {
			enterVertex(maxLumRed, Y - maxLumRed, 0.0);
			enterVertex(Y - maxLumGreen, maxLumGreen, 0.0);
			enterVertex(0.0, maxLumGreen, Y - maxLumGreen);
			enterVertex(0.0, Y - maxLumBlue, maxLumBlue);
			enterVertex(maxLumRed, Y - maxLumRed - maxLumBlue, maxLumBlue);
		} else if (Y < (maxLumRed + maxLumGreen)) {
			enterVertex(maxLumRed, Y - maxLumRed, 0.0);
			enterVertex(Y - maxLumGreen, maxLumGreen, 0.0);
			enterVertex(Y - maxLumGreen - maxLumBlue, maxLumGreen, maxLumBlue);
			enterVertex(maxLumRed, Y - maxLumRed - maxLumBlue, maxLumBlue);
		} else if (Y < (maxLumRed + maxLumGreen + maxLumBlue)) {
			enterVertex(maxLumRed, maxLumGreen, Y - maxLumRed - maxLumGreen);
			enterVertex(Y - maxLumGreen - maxLumBlue, maxLumGreen, maxLumBlue);
			enterVertex(maxLumRed, Y - maxLumRed - maxLumBlue, maxLumBlue);
		}
		return (validRegion);
	}

	/**
	 * Compute the color with the same chromaticity as this one and the maximum
	 * possible luminance for this color device. This is done by transforming
	 * the XYZ coordinates into linear RGB coordinates and finding that channel
	 * which has the highest value. This channel limits the luminance range of
	 * this color. Thus the maximum luminance requires that this channel has
	 * value 1.0 and the other channels are set accordingly.
	 */
	public double[] max(double[] p) {
		// double[] a = linearRGB(p);
		double[] a = mul(mXYZToRGB, p);
		// System.out.println("max of XYZ=" + Utils.stringOfArray(p) + " RGB=" +
		// Utils.stringOfArray(a));
		double f = 1.0;
		if (a[2] > a[0]) {
			// B > R
			if (a[2] > a[1]) {
				// B > (R,G)
				f = 1.0 / a[2];
			} else {
				// G > B > R
				f = 1.0 / a[1];
			}
		} else {
			// R > B
			if (a[0] > a[1]) {
				// R > (B,G)
				f = 1.0 / a[0];
			} else {
				// G > R > B
				f = 1.0 / a[1];
			}
		}
		a[0] *= f;
		a[1] *= f;
		a[2] *= f;
		double[] b = mul(mRGBToXYZ, a);
		// System.out.println("Max of " + Utils.stringOfArray(p));
		// System.out.println("    is " + Utils.stringOfArray(b));
		return (b);
	}

	/** A locally used transformation form Yxy to XYZ coordinates. */
	protected static double[] XYZfromYxy(double[] a) {
		double[] p = new double[3];
		p[1] = a[0];
		p[0] = a[1] * p[1] / a[2];
		p[2] = (1.0 - a[1] - a[2]) * p[1] / a[2];
		return (p);
	}

	/**
	 * Clip this color into the valid color polytope. The algorithm used is from
	 * Foley & van Dam: the Sutherland algorithm for 3D-clipping. Note that the
	 * point to be clipped to must be located within the clipping cube. Thus its
	 * luminance must be less or equal to maxLum().
	 */
	public double[] clipped(double[] c) {
		double CubeRMin = 0.0;
		double CubeRMax = maxLumRed;
		double CubeGMin = 0.0;
		double CubeGMax = maxLumGreen;
		double CubeBMin = 0.0;
		double CubeBMax = maxLumBlue;
		// Find the clipping reference. Preferable this is that point on
		// the gray axis, which has the same luminance as the color to
		// be clipped. However, we have to make sure that the clipping
		// reference is within the valid cube.
		// First get our white point chromaticity
		// PxlColor wp = new PxlColor(device.getWhitePoint());
		// Now set the white point luminance equal to the target if
		// possible
		// if (this.Y < wp.Y) wp.setY(this.Y);
		double[] w = whitePoint;
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
		// We are done
		double[] d = { r, g, b };
		return d;
	}

	protected static void invertMatrix(double[][] M, double[][] invM) {
		int i, j;
		double det, prod1, prod2;
		double[][] P = new double[3][6];
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 3; ++j) {
				P[i][j] = M[i][j];
				P[i][j + 3] = P[i][j];
			}
		}
		det = 0.0;
		for (i = 0; i < 3; ++i) {
			prod1 = prod2 = 1.0;
			for (j = 0; j < 3; ++j) {
				prod1 *= P[j][i + j];
				prod2 *= P[j][5 - j - i];
			}
			det += prod1 - prod2;
		}
		invM[0][0] = (M[1][1] * M[2][2] - M[1][2] * M[2][1]) / det;
		invM[0][1] = -(M[0][1] * M[2][2] - M[0][2] * M[2][1]) / det;
		invM[0][2] = (M[0][1] * M[1][2] - M[0][2] * M[1][1]) / det;
		invM[1][0] = -(M[1][0] * M[2][2] - M[1][2] * M[2][0]) / det;
		invM[1][1] = (M[0][0] * M[2][2] - M[0][2] * M[2][0]) / det;
		invM[1][2] = -(M[0][0] * M[1][2] - M[0][2] * M[1][0]) / det;
		invM[2][0] = (M[1][0] * M[2][1] - M[1][1] * M[2][0]) / det;
		invM[2][1] = -(M[0][0] * M[2][1] - M[0][1] * M[2][0]) / det;
		invM[2][2] = (M[0][0] * M[1][1] - M[0][1] * M[1][0]) / det;
	}

	protected static double[][] createColumnMatrix(double[] a, double[] b,
			double[] c) {
		double[][] m = new double[3][3];
		for (int i = 0; i < 3; i++) {
			m[i][0] = a[i];
			m[i][1] = b[i];
			m[i][2] = c[i];
		}
		return (m);
	}

	/**
	 * Premultiply the 3-dimensional vector p by the 3x3-matrix m and return the
	 * resulting 3-dimensional vector.
	 */
	protected static double[] mul(double[][] m, double[] p) {
		double[] x = new double[3];
		x[0] = m[0][0] * p[0] + m[0][1] * p[1] + m[0][2] * p[2];
		x[1] = m[1][0] * p[0] + m[1][1] * p[1] + m[1][2] * p[2];
		x[2] = m[2][0] * p[0] + m[2][1] * p[1] + m[2][2] * p[2];
		return (x);
	}

	/** Create a string representation of the given array. */
	public static String stringOfArray(double[] a) {
		if ((a == null) || (a.length == 0))
			return ("[]");
		StringBuffer s = new StringBuffer("[" + String.valueOf(a[0]));
		for (int i = 1; i < a.length; i++)
			s.append(", " + String.valueOf(a[i]));
		s.append("]");
		return (new String(s));
	}

	/** Create a string representation of the given array. */
	public static String stringOfArray(float[] a) {
		if ((a == null) || (a.length == 0))
			return ("[]");
		StringBuffer s = new StringBuffer("[" + String.valueOf(a[0]));
		for (int i = 1; i < a.length; i++)
			s.append(", " + String.valueOf(a[i]));
		s.append("]");
		return (new String(s));
	}

	/** Create a string representation of the given array. */
	public static String stringOfArray(int[] a) {
		if ((a == null) || (a.length == 0))
			return ("[]");
		StringBuffer s = new StringBuffer("[" + String.valueOf(a[0]));
		for (int i = 1; i < a.length; i++)
			s.append(", " + String.valueOf(a[i]));
		s.append("]");
		return (new String(s));
	}

	/** Create a string representation of the given matrix. */
	public static String stringOfMatrix(String prefix, double[][] m) {
		StringBuffer s = new StringBuffer(prefix + "[" + stringOfArray(m[0]));
		String nl = System.getProperty("line.separator");
		for (int i = 1; i < m[0].length; i++) {
			s.append(nl + prefix + stringOfArray(m[i]));
		}
		s.append("]");
		return (new String(s));
	}

	public static void showMatrix(double[][] p) {
		System.out.println(stringOfArray(p[0]));
		System.out.println(stringOfArray(p[1]));
		System.out.println(stringOfArray(p[2]));
	}

	public String toString() {
		StringBuffer s = new StringBuffer(3000);
		String nl = System.getProperty("line.separator");
		s.append(nl + "Color Transformation:");
		s.append(nl + " Transform from XYZ to RGB = " + nl
				+ stringOfMatrix(" ", mXYZToRGB));
		s.append(nl + " Transform from RGB to XYZ = " + nl
				+ stringOfMatrix(" ", mRGBToXYZ));
		s.append(nl + " White Point = " + (new PxlColor(whitePoint)));
		s.append(nl + "             = " + StringExt.valueOf(whitePoint));
		return (new String(s));
	}
}
