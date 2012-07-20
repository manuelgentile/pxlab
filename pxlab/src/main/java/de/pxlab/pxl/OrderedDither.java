package de.pxlab.pxl;

import java.awt.Color;

/**
 * This class creates an ordered dither object for approximating target colors
 * which may not be available as device colors by spatial mixture of two
 * available device colors. The spatial mixture is described by a nxn dither
 * matrix such that dithering may result in a loss of spatial resolution. Its
 * advantage, however, is to increase color resolution.
 * 
 * <p>
 * Here is how color dithering works: Suppose we want to display a certain
 * intensity X which cannot be shown on the display because of its digital
 * resolution limits. We approximate X by a convex mixture X' of a intensity
 * C_lo and a intensity C_hi which both are near to X. Thus we have to use the
 * mixture
 * 
 * <p>
 * X' = (p/k)*C_hi + ((k-p)/k)*C_lo
 * 
 * <p>
 * with 0<=p<=k being the mixture weight. To create the mixture weights p we use
 * the spatial pixels in a nxn-matrix which gives us n*n+1 mixture levels: p =
 * 0, 1, ..., k=(n*n).
 * 
 * <p>
 * In order to properly distribute the two intensities C_lo and C_hi within the
 * nxn matrix of pixels we use the "dither matrix". This is a matrix of the
 * numbers 0, ..., n*n-1 distributed such that a proper distribution of
 * intensities results from the following rule:
 * 
 * <p>
 * If the weight p is larger than the entry in the dither matrix at a certain
 * dither control index, then the intensity C_hi is drawn for that pixel and if
 * not then the intensity C_lo is drawn. The dither control index i ist computed
 * from the pixel position (x, y) by the following rule:
 * 
 * <p>
 * i = n * y%n + x%n
 * 
 * <p>
 * Thus we have the rule: Draw the pixel at (x,y) using intensity C_hi if p >
 * D[i] and use intensity C_lo if not. Lets call the level index "dither control
 * index". It ranges from 0 to n*n.
 * 
 * <p>
 * The above considerations are applied to the R, G, and B channel of every
 * device color independently.
 * 
 * <p>
 * What is the increase in resolution gatherd by dithering? It depends on the
 * size of the dither matrix. A matrix of size 2x2 gives 3 additional steps of
 * color which corresponds to an effective increase of color resolution by 1.59
 * bit. A dither matrix size of 3x3 gives 8 additional steps corresponding to 3
 * additional bits of color resolution, and a dither matrix of 4x4 adds 15 color
 * steps corresponding 3.91 bits. Note, however, that these values are only
 * theoretical maxima. Actual resolutions will be less because the effective
 * step size of a monitor's luminance output is not equal for all intensities
 * because of monitor gamma.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see RandomDither
 */
/*
 * 
 * 02/13/02
 */
public class OrderedDither extends Dither {
	private static final int D4[] = { 0, 2, 3, 1 };
	private static final int D9[] = { 6, 8, 4, 1, 0, 3, 5, 2, 7 };
	private static final int D16[] = { 0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9,
			15, 7, 13, 5 };
	private int[] D;
	private int n;
	private int k;
	private Color[] C;

	/**
	 * Create an ordered dither object.
	 * 
	 * @param n
	 *            is the number of rows and columns in the dither matrix.
	 *            Currently only n = 2, 3, 4 is allowed.
	 */
	public OrderedDither(int n) {
		this.n = n;
		k = n * n;
		if (n == 4) {
			D = D16;
		} else if (n == 3) {
			D = D9;
		} else {
			D = D4;
		}
		C = new Color[k];
		setColor(PxlColor.systemColor(PxlColor.WHITE));
	}

	/** Return the size of the dither matrix. */
	public int getType() {
		return n;
	}

	/**
	 * Get the device color for the given screen position. The color returned
	 * approximates the currently active target color using the dither matrix of
	 * this dithering object.
	 * 
	 * @param x
	 *            horizontal position.
	 * @param y
	 *            vertical position.
	 */
	public Color colorAt(int x, int y) {
		int i = n * (Math.abs(y) % n) + (Math.abs(x) % n);
		return C[i];
	}

	/**
	 * Set the target color for this dithering object.
	 * 
	 * @param c
	 *            the color which should be approximated by the next dither
	 *            operation.
	 */
	public void setColor(PxlColor c) {
		double[] rgb = c.devRGB();
		for (int i = 0; i < 3; i++)
			rgb[i] *= 255.0;
		// System.out.println("r=" + rgb[0] + ", g=" + rgb[1] + ", b=" +
		// rgb[2]);
		int r_lo = (int) (rgb[0]);
		int g_lo = (int) (rgb[1]);
		int b_lo = (int) (rgb[2]);
		int r_hi = (int) (rgb[0] + 0.9999999);
		int g_hi = (int) (rgb[1] + 0.9999999);
		int b_hi = (int) (rgb[2] + 0.9999999);
		// System.out.println("rl=" + r_lo + ", gl=" + g_lo + ", bl=" + b_lo);
		// System.out.println("rh=" + r_hi + ", gh=" + g_hi + ", bh=" + b_hi);
		int w_r = weight(rgb[0], r_lo, r_hi);
		int w_g = weight(rgb[1], g_lo, g_hi);
		int w_b = weight(rgb[2], b_lo, b_hi);
		// System.out.println("wr=" + w_r + ", wg=" + w_g + ", wb=" + w_b);
		for (int i = 0; i < k; i++) {
			C[i] = new Color(((w_r > D[i]) ? r_hi : r_lo), ((w_g > D[i]) ? g_hi
					: g_lo), ((w_b > D[i]) ? b_hi : b_lo));
			// System.out.println(C[i]);
		}
	}

	/** Compute the weight w such that x = w*x_hi + (1-w)*x_lo. */
	private int weight(double x, int x_lo, int x_hi) {
		if (x_lo == x_hi)
			return 0;
		return (int) ((x - (double) x_lo) * (double) k + 0.5);
	}
}
