package de.pxlab.pxl;

import java.util.StringTokenizer;

/**
 * This class allows us to create a PxlColor from its Yxy-coordinates. It does
 * not add any fields to the PxlColor class since the PxlColor class already
 * contains the xy-chromaticities of any PxlColor object. The YxyColor subclass
 * is only used to have a convenient way to create PxlColor objects from
 * Yxy-coordinates.
 * 
 * @author H. Irtel
 * @version 0.2.3
 */
/*
 * 04/17/00
 * 
 * 11/05/03 use superclass toString() method
 * 
 * 2004/10/12 added constructor YxyColor(String);
 */
public class YxyColor extends PxlColor {
	/**
	 * Create a PxlColor from its Yxy-coordinates.
	 * 
	 * @param Y
	 *            the luminance of the color.
	 * @param x
	 *            the chromaticity coordinate x of the color.
	 * @param y
	 *            the chromaticity coordinate y of the color.
	 */
	public YxyColor(double Y, double x, double y) {
		super(x * Y / y, Y, (1.0 - x - y) * Y / y);
	}

	/**
	 * Create a PxlColor from its Yxy-coordinates in a double array.
	 * 
	 * @param a
	 *            a double array containing the Yxy-coordinates.
	 */
	public YxyColor(double[] a) {
		super(a[1] * a[0] / a[2], a[0], (1.0 - a[1] - a[2]) * a[0] / a[2]);
	}

	/**
	 * Create a new Yxycolor object from a PxlColor object.
	 * 
	 * @param c
	 *            the PxlColor onject which should be cloned.
	 */
	public YxyColor(PxlColor c) {
		this.X = c.X;
		this.Y = c.Y;
		this.Z = c.Z;
		this.x = c.x;
		this.y = c.y;
	}

	/**
	 * Create a new Yxycolor object from a String containing Y, x, and y values.
	 * If the input string is invalid then the black color is created.
	 * 
	 * @param s
	 *            the data string.
	 */
	public YxyColor(String s) {
		this(PxlColor.systemColor(PxlColor.BLACK));
		String sY = null, sx = null, sy = null;
		StringTokenizer st = new StringTokenizer(s, " \t,[]");
		if (st.hasMoreTokens()) {
			sY = st.nextToken();
			if (st.hasMoreTokens()) {
				sx = st.nextToken();
				if (st.hasMoreTokens()) {
					sy = st.nextToken();
					try {
						Y = Double.valueOf(sY).doubleValue();
						x = Double.valueOf(sx).doubleValue();
						y = Double.valueOf(sy).doubleValue();
						X = x * Y / y;
						Z = (1.0 - x - y) * Y / y;
					} catch (NumberFormatException nfx) {
					}
				}
			}
		}
	}
}
