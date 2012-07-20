package de.pxlab.pxl;

/**
 * Some static support methods for the CIELabStar color system.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class CIELabStar {
	private static final double searchLimit = 1.0;
	private static final int countLimit = 10;

	/**
	 * Create a rectangular constant lightness plane of CIELab colors. The
	 * resulting array contains an entry for every position in the plane.
	 * However, colors which are not displayable on the current device cointain
	 * null entries.
	 * 
	 * @param c
	 *            the CIE Yxy-coordinates of the color which corresponds to the
	 *            center of the color plane.
	 * @param aStep
	 *            the a-chromaticity step size.
	 * @param bStep
	 *            the b-chromaticity step size.
	 * @param columns
	 *            the number of columns in the resulting rectangular plane.
	 * @param rows
	 *            the number of rows in the resulting rectangular plane.
	 * @param colorTable
	 *            the output table. This contains an array of PxlColor values
	 *            from the top left to the bottom right color in the constant
	 *            lightness plane. Positions which would contain colors which
	 *            are not displayable on the current device cointain null
	 *            entries.
	 * @return the resulting color table. Identical to the last argument if this
	 *         fits the requested table size.
	 */
	public static PxlColor[] constantLightnessPlane(PxlColor c, double aStep,
			double bStep, int columns, int rows, PxlColor[] colorTable) {
		if ((colorTable == null) || (colorTable.length != (columns * rows)))
			colorTable = new PxlColor[columns * rows];
		double[] cc = c.transform(PxlColor.CS_LabStar);
		// System.out.println("CIELabStar.constantLightnessPlane(): [" + cc[0] +
		// "," + cc[1] + "," + cc[2] + "]");
		double c_left_x = cc[1]
				- ((columns / 2) - (((columns % 2) == 0) ? 0.5 : 0.0)) * aStep;
		double c_top_y = cc[2] + ((rows / 2) - (((rows % 2) == 0) ? 0.5 : 0.0))
				* bStep;
		double c_x;
		double c_y = c_top_y;
		int k = 0;
		int ndc = 0;
		for (int i = 0; i < rows; i++) {
			c_x = c_left_x;
			cc[2] = c_y;
			for (int j = 0; j < columns; j++) {
				cc[1] = c_x;
				PxlColor pc = PxlColor.fromLabStar(cc);
				if (pc.isDisplayable()) {
					colorTable[k] = pc;
				} else {
					colorTable[k] = null;
					ndc++;
				}
				k++;
				c_x += aStep;
			}
			c_y -= bStep;
		}
		// System.out.println("CIELabStar.constantLightnessPlane(): " + ndc +
		// " of " + (rows*columns) + " not displayable.");
		return colorTable;
	}

	/**
	 * Create a rectangular plane of maximum chroma CIELab colors. The resulting
	 * array contains an entry for every position in the plane.
	 * 
	 * @param c
	 *            the CIE Yxy-coordinates of the color which corresponds to the
	 *            center of the color plane.
	 * @param hStep
	 *            the hue angle step size.
	 * @param LStep
	 *            the lightness step size.
	 * @param columns
	 *            the number of columns in the resulting rectangular plane.
	 * @param rows
	 *            the number of rows in the resulting rectangular plane.
	 * @param colorTable
	 *            the output table. This contains an array of PxlColor values
	 *            from the top left to the bottom right color in the maximum
	 *            chroma plane.
	 * @return the resulting color table. Identical to the last argument if this
	 *         fits the requested table size.
	 */
	public static PxlColor[] maximumChromaPlane(PxlColor c, double hStep,
			double LStep, int columns, int rows, PxlColor[] colorTable) {
		if ((colorTable == null) || (colorTable.length != (columns * rows)))
			colorTable = new PxlColor[columns * rows];
		double[] cc = c.transform(PxlColor.CS_LabLChStar);
		double c_left_x = cc[2]
				- ((columns / 2) - (((columns % 2) == 0) ? 0.5 : 0.0)) * hStep;
		double c_top_y = cc[0] + ((rows / 2) - (((rows % 2) == 0) ? 0.5 : 0.0))
				* LStep;
		double c_x;
		double c_y = c_top_y;
		int k = 0;
		for (int i = 0; i < rows; i++) {
			c_x = c_left_x;
			cc[0] = c_y;
			for (int j = 0; j < columns; j++) {
				cc[2] = c_x;
				cc[1] = CIELabStar.maxChroma(c_y, 0.0, c_x);
				colorTable[k] = PxlColor.instance(PxlColor.CS_LabLChStar, cc);
				k++;
				c_x += hStep;
			}
			c_y -= LStep;
		}
		return colorTable;
	}

	/**
	 * Create a rectangular plane of maximum chroma CIELab colors. The resulting
	 * array contains an entry for every position in the plane.
	 * 
	 * @param c
	 *            the CIE Yxy-coordinates of the first color on the circle.
	 * @param columns
	 *            the number of points in the resulting color circle.
	 * @param colorTable
	 *            the output table. This contains an array of PxlColor values.
	 *            Positions which would contain colors which are not displayable
	 *            on the current device cointain null entries.
	 * @return the resulting color table. Identical to the last argument if this
	 *         fits the requested table size.
	 */
	public static PxlColor[] colorCircle(PxlColor c, int columns,
			PxlColor[] colorTable) {
		if ((colorTable == null) || (colorTable.length != (columns)))
			colorTable = new PxlColor[columns];
		double[] cc = c.transform(PxlColor.CS_LabLChStar);
		double hStep = 360.0 / columns;
		double c_x = cc[2];
		for (int j = 0; j < columns; j++) {
			cc[2] = c_x;
			PxlColor pc = PxlColor.instance(PxlColor.CS_LabLChStar, cc);
			if (pc.isDisplayable()) {
				colorTable[j] = pc;
			} else {
				colorTable[j] = null;
			}
			c_x += hStep;
		}
		return colorTable;
	}

	/**
	 * Find the maximum CIELabStar Chroma value such that the resulting color
	 * can be displayed on the current color devive.
	 * 
	 * @param L
	 *            the Lightness value of the target color.
	 * @param C
	 *            the initial Chroma value of the target color.
	 * @param h
	 *            the hue angle of the target color.
	 * @return the maximum Chroma value such that the resulting color can be
	 *         shown on the current output device and has the same lightness and
	 *         hue as the input argumen.
	 */
	public static double maxChroma(double L, double C, double h) {
		double s = 20;
		int count1, count2;
		// if (C == 0.0) return 0.0;
		double[] LCh = new double[3];
		LCh[0] = L;
		LCh[1] = C;
		LCh[2] = h;
		if (!validLCh(LCh))
			return 0.0;
		count1 = 0;
		while ((count1 < countLimit) && (s > searchLimit)) {
			count2 = 0;
			do {
				LCh[1] += s;
				count2++;
			} while ((count2 < countLimit) && validLCh(LCh));
			LCh[1] -= s;
			s /= 2;
			count1++;
		}
		if (count1 >= countLimit)
			System.out.println("x");
		return LCh[1];
	}

	/** Check whether the given LCh color can be displayed. */
	private static boolean validLCh(double[] LCh) {
		return PxlColor.instance(PxlColor.CS_LabLChStar, LCh).isDisplayable();
	}

	public static double deltaE(double[] a, double[] b) {
		double E = 0.0;
		if ((a.length == 3) && (b.length == 3)) {
			double d0 = a[0] - b[0];
			double d1 = a[1] - b[1];
			double d2 = a[2] - b[2];
			E = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
		}
		return E;
	}
}
