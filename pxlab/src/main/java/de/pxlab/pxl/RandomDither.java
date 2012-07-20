package de.pxlab.pxl;

import java.awt.Color;

import de.pxlab.util.Randomizer;

/**
 * A random dither operator for approximating target colors which may not be
 * available as device colors by spatial mixture of two available device colors.
 * The spatial mixture is created by a random distribution of two mixture
 * components.
 * 
 * <p>
 * Here is how random color dithering works: Suppose we want to display a
 * certain intensity X which cannot be shown on the display because of its
 * digital resolution limits. We approximate X by a convex mixture X' of a set
 * of pixels having intensity C_lo and another set of pixels having intensity
 * C_hi which both are near to X. Thus we have to use the mixture
 * 
 * <p>
 * X' = p*C_hi + (1-p)*C_lo
 * 
 * <p>
 * where 0<=p<=1 is the proportion of pixels having intensity C_hi and (1-p) is
 * the proportion of pixels having intensity C_lo.
 * 
 * <p>
 * In order to properly distribute the two intensities C_lo and C_hi we compute
 * the required proportions for each color channel separately and distribute the
 * pixels accordingly. These considerations are applied to the R, G, and B
 * channel of every device color independently.
 * 
 * <p>
 * What is the increase in color resolution gathered by random dithering? Sorry,
 * I don't know how to compute it! For large enough fields on the screen it
 * should be rather high. For small fields the optimum might be described by the
 * properties of an ordered dither.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see OrderedDither
 */
/*
 * 
 * 02/13/02
 */
public class RandomDither extends Dither {
	private int r_lo;
	private int g_lo;
	private int b_lo;
	private int r_hi;
	private int g_hi;
	private int b_hi;
	private double w_r;
	private double w_g;
	private double w_b;
	private Randomizer rand;

	/** Create a random dither object. */
	public RandomDither() {
		rand = new Randomizer();
		setColor(PxlColor.systemColor(PxlColor.WHITE));
	}

	/** Return the identifying type this dither method. */
	public int getType() {
		return 1;
	}

	/**
	 * Get the device color for the given screen position. The color returned
	 * approximates the currently active target color using random dithering.
	 * 
	 * @param x
	 *            (ignored).
	 * @param y
	 *            (ignored).
	 */
	public Color colorAt(int x, int y) {
		Color c = new Color(((rand.nextDouble() < w_r) ? r_hi : r_lo),
				((rand.nextDouble() < w_g) ? g_hi : g_lo),
				((rand.nextDouble() < w_b) ? b_hi : b_lo));
		return c;
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
		r_lo = (int) (rgb[0]);
		g_lo = (int) (rgb[1]);
		b_lo = (int) (rgb[2]);
		r_hi = (int) (rgb[0] + 0.9999999);
		g_hi = (int) (rgb[1] + 0.9999999);
		b_hi = (int) (rgb[2] + 0.9999999);
		// System.out.println("rl=" + r_lo + ", gl=" + g_lo + ", bl=" + b_lo);
		// System.out.println("rh=" + r_hi + ", gh=" + g_hi + ", bh=" + b_hi);
		w_r = weight(rgb[0], r_lo, r_hi);
		w_g = weight(rgb[1], g_lo, g_hi);
		w_b = weight(rgb[2], b_lo, b_hi);
		// System.out.println("wr=" + w_r + ", wg=" + w_g + ", wb=" + w_b);
	}

	/** Compute the weight w such that x = w*x_hi + (1-w)*x_lo. */
	private double weight(double x, int x_lo, int x_hi) {
		if (x_lo == x_hi)
			return 0.0;
		return (x - (double) x_lo);
	}
}
