package de.pxlab.pxl;

import java.util.Random;

/**
 * A source for random colors. Colors are sampled froma uniform distribution in
 * a color space which is defined by the constructor of this class. Note that
 * output space and sampling space are independent. Thus the sampling space to
 * be used is defined by the constructor of this class while output coordinates
 * are defined by the method used to retrieve the samples.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 11/12/03
 */
public class RandomColor {
	protected int colorSpace;
	protected int distribution = 0;
	protected double[] range = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	private PxlColor lastColor;
	private Random rnd = new Random();
	private long tryCount = 0;
	private long successCount = 0;

	/** Create a random color source for the CIELAB color space. */
	public RandomColor() {
		this(PxlColor.CS_LabStar);
	}

	/**
	 * Create a random color source for the given color space.
	 * 
	 * @param cs
	 *            the color space which is used as a sample space for the random
	 *            colors.
	 */
	public RandomColor(int cs) {
		colorSpace = cs;
		setDefaultLimits();
	}

	public void setDefaultLimits() {
		switch (colorSpace) {
		case PxlColor.CS_Yxy:
			range[0] = 0.0;
			range[1] = 100.0;
			range[2] = 0.0;
			range[3] = 1.0;
			range[4] = 0.0;
			range[5] = 1.0;
			break;
		case PxlColor.CS_LabStar:
			range[0] = 0.0;
			range[1] = 100.0;
			range[2] = -120.0;
			range[3] = 120.0;
			range[4] = -120.0;
			range[5] = 120.0;
			break;
		case PxlColor.CS_Dev:
			range[0] = 0.0;
			range[1] = 255.0;
			range[2] = 0.0;
			range[3] = 255.0;
			range[4] = 0.0;
			range[5] = 255.0;
			break;
		default:
			new ParameterValueError(
					"Illegal Color Space for RandomColor class!");
		case PxlColor.CS_RGB:
			range[0] = 0.0;
			range[1] = 1.0;
			range[2] = 0.0;
			range[3] = 1.0;
			range[4] = 0.0;
			range[5] = 1.0;
			break;
		}
	}

	/** Set the limits for random sampling. */
	public void setLimits(double min1, double max1, double min2, double max2,
			double min3, double max3) {
		if (max1 >= min1) {
			range[0] = min1;
			range[1] = max1;
		}
		if (max2 >= min2) {
			range[2] = min2;
			range[3] = max2;
		}
		if (max3 >= min3) {
			range[4] = min3;
			range[5] = max3;
		}
	}

	/** Set the limits for random sampling. */
	public void setLimits(double[] m) {
		setLimits(m[0], m[1], m[2], m[3], m[4], m[5]);
	}

	/** Set the limits of the first color coordinate for random sampling. */
	public void setLimits1(double min1, double max1) {
		if (max1 >= min1) {
			range[0] = min1;
			range[1] = max1;
		}
	}

	/** Set the limits of the seconds color coordinate for random sampling. */
	public void setLimits2(double min2, double max2) {
		if (max2 >= min2) {
			range[2] = min2;
			range[3] = max2;
		}
	}

	/** Set the limits of the third color coordinate for random sampling. */
	public void setLimits3(double min3, double max3) {
		if (max3 >= min3) {
			range[4] = min3;
			range[5] = max3;
		}
	}

	/**
	 * Get a single color sample.
	 * 
	 * @return a double array which contains the coordinates of the sampled
	 *         color in the color space which is used as a sample space.
	 */
	public double[] next() {
		double[] a = new double[3];
		do {
			a[0] = sample(0);
			a[1] = sample(1);
			a[2] = sample(2);
			lastColor = PxlColor.instance(colorSpace, a);
			tryCount++;
		} while (!(lastColor.isDisplayable()));
		successCount++;
		// System.out.println("RandomColor.next() try = " + tryCount +
		// ", success = " + successCount +
		// ", efficiency = " + ((double)successCount/(double)tryCount));
		return a;
	}

	/**
	 * Get the sampling efficiency of this random color source.
	 * 
	 * @return the sampling efficiency. This is the relative frequency of cases
	 *         where a sampling trial results in a color which can be displayed
	 *         on the current output device. The efficiency will mostly depend
	 *         on proper sampling limits as defined by the setLimits() method.
	 */
	public double getEfficiency() {
		return (double) successCount / (double) tryCount;
	}

	/**
	 * Get a series of color samples.
	 * 
	 * @param n
	 *            the number of colors to sample.
	 * @return a double array which contains the coordinates of the sampled
	 *         colors in the color space which is used as a sample space.
	 */
	public double[] next(int n) {
		double[] a;
		double[] c = new double[n + n + n];
		for (int i = 0; i < n; i++) {
			a = next();
			c[i + i + i + 0] = a[0];
			c[i + i + i + 1] = a[1];
			c[i + i + i + 2] = a[2];
		}
		return c;
	}

	/**
	 * Get a single color sample with coordinates given in CIE Yxy color space.
	 * Note that output space and sampling space are independent. Thus the
	 * sampling space to be used is defined by the constructor of this class
	 * while output coordinates of this method always are Yxy coordinates.
	 * 
	 * @return a random PxlColor object.
	 */
	public PxlColor nextPxlColor() {
		next();
		return lastColor;
	}

	/**
	 * Get a seriesof color samples with coordinates given in CIE Yxy color
	 * space.
	 * 
	 * @param n
	 *            the number of colors to sample.
	 * @return an array of random PxlColor objects.
	 */
	public PxlColor[] nextPxlColor(int n) {
		PxlColor[] a = new PxlColor[n];
		for (int i = 0; i < n; i++) {
			a[i] = nextPxlColor();
		}
		return a;
	}

	private double sample(int a) {
		double s = 0.0;
		a *= 2;
		if (range[a] == range[a + 1]) {
			s = range[a];
		} else {
			s = range[a] + rnd.nextDouble() * (range[a + 1] - range[a]);
		}
		return s;
	}
}
