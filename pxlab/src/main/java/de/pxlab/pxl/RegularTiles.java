package de.pxlab.pxl;

import java.awt.*;
import java.util.*;
import de.pxlab.util.*;

/**
 * A regular pattern of tiles. The tiles have raster elements as their basic
 * size units. Rows and columns are counted by the raster elements. Raster
 * elements are described by their pixel sizes.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class RegularTiles extends RandomTiles {
	protected int centerSize = 0;
	protected double luminanceRange = 0.0;

	/**
	 * Create a new random tiles pattern.
	 * 
	 * @param fc
	 *            foreground pattern color parameter.
	 * @param bc
	 *            background color parameter.
	 */
	public RegularTiles(ExPar fc, ExPar bc) {
		super(fc, bc);
		maxTileWidth = 1;
		maxTileHeight = 1;
	}

	/**
	 * Create a new random tiles pattern.
	 * 
	 * @param fc
	 *            pattern color parameter.
	 */
	public RegularTiles(ExPar fc) {
		super(fc, fc);
		maxTileWidth = 1;
		maxTileHeight = 1;
	}

	/**
	 * Overrides the superclass's method in order to make sure that tile size
	 * always is 1 raster unit.
	 * 
	 * @param s
	 *            ignored.
	 */
	public void setTileSize(int s) {
		maxTileWidth = 1;
		maxTileHeight = 1;
	}

	/**
	 * Set this color pattern's random luminance range. The actual luminance of
	 * a single tile my be randomized. If the random luminance range is nonzero
	 * then the actual luminance of a tile is Y' = Y + rand(d) - d/2 where
	 * rand(d) is a uniformly distributed random number x with 0 <= x < d.
	 * 
	 * @param d
	 *            the range of random luminance which is added to each tile.
	 */
	public void setLuminanceRange(double d) {
		luminanceRange = d;
	}

	public void setCenterSize(int n) {
		centerSize = n;
	}

	/**
	 * Decide whether a single tile belongs to the foreground pattern or not.
	 * 
	 * @param t
	 *            the tile which should be tested.
	 */
	protected boolean isForeground(Tile t) {
		int x = (columns - centerSize) / 2;
		int y = (rows - centerSize) / 2;
		return ((t.x >= x) && (t.x < (x + centerSize)) && (t.y >= y) && (t.y < (y + centerSize)));
	}

	/**
	 * Get the color of the given tile.
	 * 
	 * @param t
	 *            the tile whose color is requested.
	 */
	protected Color colorOf(Tile t) {
		if (isForeground(t)) {
			return colorPar.getPxlColor().dev();
		}
		PxlColor c = colorPar2.getPxlColor();
		if (luminanceRange > 0.0) {
			double r = rand.nextDouble() * luminanceRange - luminanceRange
					/ 2.0;
			c.setY(c.getY() + r);
		}
		return c.dev();
	}
}
