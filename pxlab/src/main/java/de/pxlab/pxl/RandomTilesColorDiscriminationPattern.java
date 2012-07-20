package de.pxlab.pxl;

import java.awt.*;

/**
 * A pattern for testing color discrimination. The pattern is made up from
 * random tiles containing a Landoldt-type ring with a gap. The ring and the gap
 * colors are to be discriminated while the background serves as an adaptation
 * color. The task is to detect the orientation of the gap in the ring.
 * 
 * @version 0.2.0
 */
public class RandomTilesColorDiscriminationPattern extends RandomTiles {
	protected ExPar colorPar3;
	protected int orientation = 0;
	protected double luminanceRange = 0.0;

	/**
	 * Create a new color discrimination test pattern.
	 * 
	 * @param tc
	 *            target/gap pattern color parameter.
	 * @param dc
	 *            distractor/ring pattern color parameter.
	 * @param bc
	 *            background color parameter.
	 */
	public RandomTilesColorDiscriminationPattern(ExPar tc, ExPar dc, ExPar bc) {
		super(tc, dc);
		colorPar3 = bc;
	}

	/**
	 * Set this color test pattern's gap orientation. This is the orientation of
	 * the gap of the Landoldt type ring.
	 * 
	 * @param d
	 *            the orientation code. Valid code values are 0, ..., 7.
	 */
	public void setOrientation(int d) {
		orientation = Math.abs(d) % 8;
	}

	/**
	 * Set this color test pattern's random luminance range. The actual
	 * luminance of a single tile my be randomized. If the random luminance
	 * range is nonzero then the actual luminance of a tile is Y' = Y + rand(d)
	 * - d/2 where rand(d) is a uniformly distributed random number x with 0 <=
	 * x < d.
	 * 
	 * @param d
	 *            the range of random luminance which is added to each tile.
	 */
	public void setLuminanceRange(double d) {
		luminanceRange = d;
	}

	/**
	 * Get the device color of the given tile.
	 * 
	 * @param t
	 *            the tile whose color is requested.
	 */
	protected Color getColorOf(Tile t) {
		return getPxlColorOf(t).dev();
	}
	private static final int BG = 1;
	private static final int RING = 2;
	private static final int GAP = 3;

	/**
	 * Get the XYZ-color of the given tile.
	 * 
	 * @param t
	 *            the tile whose color is requested.
	 */
	protected PxlColor getPxlColorOf(Tile t) {
		int cc = RING;
		int x = t.x + t.w / 2;
		int y = t.y + t.h / 2;
		int dx = x - columns / 2;
		int dy = y - rows / 2;
		double a = Math.atan2(-dy, dx) + Math.PI;
		// System.out.println("a=" + a);
		double r1 = (((2 * (8 - orientation) + 11) % 16) / 8.0) * Math.PI;
		double r2 = (((2 * (8 - orientation) + 13) % 16) / 8.0) * Math.PI;
		if (orientation == 6) {
			if (((r1 < a)) || ((a < r2)))
				cc = GAP;
		} else {
			if ((r1 < a) && (a < r2))
				cc = GAP;
		}
		int rr = dx * dx + dy * dy;
		if ((rr > (rows / 6) * (columns / 6))
				&& (rr < (2 * rows / 6) * (2 * columns / 6))) {
		} else {
			cc = BG;
		}
		PxlColor c = (cc == BG) ? colorPar3.getPxlColor()
				: ((cc == RING) ? colorPar2.getPxlColor() : colorPar
						.getPxlColor());
		if (luminanceRange > 0.0) {
			double r = rand.nextDouble() * luminanceRange - luminanceRange
					/ 2.0;
			c.setY(c.getY() + r);
		}
		return c;
	}
}
