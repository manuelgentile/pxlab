package de.pxlab.pxl;

import java.awt.*;
import java.util.*;
import de.pxlab.util.*;

/**
 * A pattern looking like a floor of random sized tiles. The tiles have raster
 * elements as their basic size units. The position and size of a single tile is
 * described by raster element counts and not by pixel counts. Only raster
 * elements are described by their pixel sizes.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class RandomTiles extends DisplayElement {
	protected int rows;
	protected int columns;
	protected ExPar colorPar2;
	protected int maxTileWidth;
	protected int maxTileHeight;
	protected int rasterWidth;
	protected int rasterHeight;
	protected int joint = 0;
	private ArrayList tiles;
	protected static Randomizer rand = new Randomizer();

	/**
	 * Create a new random tiles pattern.
	 * 
	 * @param fc
	 *            foreground pattern color parameter.
	 * @param bc
	 *            background color parameter.
	 */
	public RandomTiles(ExPar fc, ExPar bc) {
		colorPar = fc;
		colorPar2 = bc;
		type = DisplayElement.RANDOM_TILES;
		modified = true;
	}

	/**
	 * Create a new random tiles pattern.
	 * 
	 * @param fc
	 *            pattern color parameter.
	 */
	public RandomTiles(ExPar fc) {
		this(fc, fc);
	}

	/**
	 * Set this pattern's maximum tile size in raster element units.
	 * 
	 * @param s
	 *            the maximum number of raster units contained in the width and
	 *            height of a single tile.
	 */
	public void setTileSize(int s) {
		maxTileWidth = s;
		maxTileHeight = s;
		modified = true;
	}

	/**
	 * Set this pattern's raster unit size in pixels.
	 * 
	 * @param s
	 *            the number of pixels making up a single raster element's width
	 *            and height.
	 */
	public void setRasterSize(int s) {
		rasterWidth = s;
		rasterHeight = s;
		rows = size.height / rasterHeight;
		columns = size.width / rasterWidth;
		modified = true;
	}

	/**
	 * Set this pattern's joint size in pixels.
	 * 
	 * @param s
	 *            half the number of pixels making up a joint between two tiles.
	 */
	public void setJoint(int s) {
		joint = s;
	}

	/**
	 * Simultanously set all geometric properties of this random tile pattern.
	 */
	public void setProperties(int x, int y, int w, int h, int rs, int ts, int sh) {
		setLocation(x, y);
		setSize(w, h);
		setRasterSize(rs);
		setTileSize(ts);
		setJoint(sh);
		createPattern();
	}

	/**
	 * Decide whether a single tile belongs to the foreground pattern or not.
	 * Subclasses which want to create a foreground pattern should override this
	 * method.
	 * 
	 * @param t
	 *            the tile which should be tested.
	 */
	protected boolean isForeground(Tile t) {
		return true;
	}

	/**
	 * Get the device color of the given tile. This method is used when
	 * dithering is OFF.
	 * 
	 * @param t
	 *            the tile whose color is requested.
	 */
	protected Color getColorOf(Tile t) {
		return isForeground(t) ? colorPar.getDevColor() : colorPar2
				.getDevColor();
	}

	/**
	 * Get the XYZ-color of the given tile. This method is used when dithering
	 * is ON.
	 * 
	 * @param t
	 *            the tile whose color is requested.
	 */
	protected PxlColor getPxlColorOf(Tile t) {
		return isForeground(t) ? colorPar.getPxlColor() : colorPar2
				.getPxlColor();
	}

	private void createPattern() {
		rows = size.height / rasterHeight;
		columns = size.width / rasterWidth;
		boolean[][] a = new boolean[rows][columns];
		tiles = new ArrayList(rows * columns / 4);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns;) {
				if (!a[row][col]) {
					// This cell is not yet occupied
					int ww = 1, tw = 0;
					int hh = 1, th = 0;
					while ((col + ww < columns) && (ww < maxTileWidth)
							&& !a[row][col + ww])
						ww++;
					while ((row + hh < rows) && (hh < maxTileHeight)
							&& !a[row + hh][col])
						hh++;
					if (ww > 1)
						tw = rand.nextInt(ww);
					if (hh > 1)
						th = rand.nextInt(hh);
					for (int i = 0; i <= th; i++) {
						for (int j = 0; j <= tw; j++) {
							a[row + i][col + j] = true;
						}
					}
					tiles.add(new Tile(col, row, tw + 1, th + 1));
					// System.out.println(row + "." + col + ": [" + (tw+1) + "x"
					// + (th+1) + "]");
					col += (tw + 1);
				} else {
					// System.out.println(row + "." + col + ": x");
					// This cell is already occupied
					col++;
				}
			}
		}
		modified = false;
	}

	/** Show this pattern within the current graphics context. */
	public void show() {
		if (modified) {
			createPattern();
		}
		int n = tiles.size();
		int xj = location.x + joint;
		int yj = location.y + joint;
		int jj = joint + joint;
		for (int i = 0; i < n; i++) {
			Tile t = (Tile) tiles.get(i);
			int x = xj + t.x * rasterWidth;
			int y = yj + t.y * rasterHeight;
			int w = t.w * rasterWidth - jj;
			int h = t.h * rasterHeight - jj;
			if (dither == null) {
				graphics.setColor(getColorOf(t));
				graphics.fillRect(x, y, w, h);
			} else {
				dither.setColor(getPxlColorOf(t));
				drawDitheredBar(x, y, w, h);
			}
		}
	}

	/** Return the number of rows of this tile pattern. */
	public int getRows() {
		return rows;
	}

	/** Return the number of columns of this tile pattern. */
	public int getColumns() {
		return columns;
	}
	/**
	 * Describes a single tile. The position and size of a tile is described by
	 * raster element counts and not by pixel counts.
	 */
	protected class Tile {
		public int x, y;
		public short w, h;

		/**
		 * Create a new tile.
		 * 
		 * @param x
		 *            horizontal raster element count of this tile.
		 * @param y
		 *            vertical raster element count of this tile.
		 * @param w
		 *            number of raster elements making up the width of this
		 *            tile.
		 * @param h
		 *            number of raster elements making up the height of this
		 *            tile.
		 */
		public Tile(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = (short) w;
			this.h = (short) h;
		}
	}
}
