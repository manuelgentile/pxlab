package de.pxlab.pxl;

import de.pxlab.util.Randomizer;
import java.awt.*;

/**
 * An array of binary random dots with arbitrary density and correlation.
 * 
 * <p>
 * Note that the location parameter of this DisplayElement always describes the
 * center of the pattern!
 * 
 * @author H. Irtel
 * @version 0.2.1
 * @see RandomDotArray
 */
/*
 * 
 * 06/10/02
 * 
 * 2005/10/16 use setSize() to set pattern size
 */
public class BinaryRandomDotArray extends DisplayElement implements Cloneable {
	/** Holds the array of dots. */
	private int[][] dotArray;
	/** The pixel width and height of a single dot. */
	private int dotSize = 1;
	/** Number of storage units needed for onel horizontal line. */
	private int horizontalPieces;
	/**
	 * Number of horizontal and vertical raster units for this dot pattern. Note
	 * that this parameter together with rasterSize overrides the
	 * DisplayElement's size parameter.
	 */
	private int rasterWidth, rasterHeight;
	private int[] mask = new int[32];

	/** Return this dot array's number of horizontal raster elements. */
	public int getWidth() {
		return rasterWidth;
	}

	/** Return this dot array's number of vertical raster elements. */
	public int getHeight() {
		return rasterHeight;
	}

	/**
	 * Create a BinaryRandomDotArray with the given experimental parameter as
	 * its color parameter.
	 */
	public BinaryRandomDotArray(ExPar col) {
		colorPar = col;
		type = DisplayElement.RANDOM_DOTS;
		for (int b = 0; b < 32; b++)
			mask[b] = (1 << b);
	}

	/**
	 * Returns a full copy of this <tt>BinaryRandomDotArray</tt> instance.
	 * 
	 * @return a clone of this <tt>BinaryRandomDotArray</tt> instance.
	 */
	public Object clone() {
		try {
			BinaryRandomDotArray v = (BinaryRandomDotArray) super.clone();
			int h = dotArray.length;
			int[][] da = new int[h][horizontalPieces];
			for (int row = 0; row < h; row++) {
				for (int k = 0; k < horizontalPieces; k++) {
					da[row][k] = dotArray[row][k];
				}
			}
			v.dotArray = da;
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Sample dots within a given rectangular raster such that every raster
	 * element has a fixed probability to become a dot.
	 * 
	 * @param p
	 *            the probability that a single raster element becomes a dot of
	 *            the pattern.
	 * @param w
	 *            number of horizontal dot raster elements of the whole pattern.
	 * @param h
	 *            number of vertical dot raster elements of the whole pattern.
	 *            Note that the actual horizontal and vertical screen size of
	 *            the pattern depends on the number of raster elements and the
	 *            pixel size rasterSize of a single raster element.
	 */
	public int sampleDots(double p, int w, int h) {
		if ((p < 0.0) || (p > 1.0)) {
			throw new IllegalArgumentException(
					"Probability must be between 0.0 and 1.0");
		}
		rasterWidth = w;
		rasterHeight = h;
		horizontalPieces = (w + 31) / 32;
		dotArray = new int[h][horizontalPieces];
		// Randomizer rnd = new Randomizer((seed == 0L)?
		// System.currentTimeMillis(): seed);
		Randomizer rnd = new Randomizer();
		float fp = (float) p;
		int m;
		int s = 0;
		int col;
		for (int row = 0; row < h; row++) {
			col = 0;
			for (int k = 0; k < horizontalPieces; k++) {
				m = 0;
				for (int b = 0; (b < 32) && (col < rasterWidth); b++) {
					if (rnd.nextFloat() < fp) {
						m = (m |= mask[b]);
						s++;
					}
					col++;
				}
				dotArray[row][k] = m;
			}
		}
		setSize(rasterWidth * dotSize, rasterHeight * dotSize);
		return s;
	}

	/**
	 * Sample dots within a given rectangular raster such that every raster
	 * element has a fixed probability to become a dot.
	 * 
	 * @param p
	 *            the probability that a single raster element becomes a dot of
	 *            the pattern.
	 * @param corr
	 *            the correlation between horizontal neighbours. This changes
	 *            the conditional probability of a dot being set depending on
	 *            the state of its horizontal neighbour. If the previous dot is
	 *            set then its right neigbour has probability (p + (1-p)*corr)
	 *            that it becomes set and if a dot is not set then its neighbour
	 *            has probability (p - p*corr) of being set.
	 * @param w
	 *            number of horizontal dot raster elements of the whole pattern.
	 * @param h
	 *            number of vertical dot raster elements of the whole pattern.
	 *            Note that the actual horizontal and vertical screen size of
	 *            the pattern depends on the number of raster elements and the
	 *            pixel size rasterSize of a single raster element.
	 */
	public int sampleDots(double p, double corr, int w, int h) {
		if ((p < 0.0) || (p > 1.0)) {
			throw new IllegalArgumentException(
					"Probability must be between 0.0 and 1.0");
		}
		if ((corr < 0.0) || (corr > 1.0)) {
			throw new IllegalArgumentException(
					"Correlation must be between 0.0 and 1.0");
		}
		rasterWidth = w;
		rasterHeight = h;
		horizontalPieces = (w + 31) / 32;
		dotArray = new int[h][horizontalPieces];
		// Randomizer rnd = new Randomizer((seed == 0L)?
		// System.currentTimeMillis(): seed);
		Randomizer rnd = new Randomizer();
		float fp = (float) p;
		float fpp = fp + (1.0F - fp) * (float) corr;
		float fpn = fp - fp * (float) corr;
		boolean active = false;
		int m;
		int s = 0;
		int col;
		for (int row = 0; row < h; row++) {
			col = 0;
			for (int k = 0; k < horizontalPieces; k++) {
				m = 0;
				for (int b = 0; (b < 32) && (col < rasterWidth); b++) {
					active = (rnd.nextFloat() < (active ? fpp : fpn));
					if (active) {
						m = (m |= mask[b]);
						s++;
					}
					col++;
				}
				dotArray[row][k] = m;
			}
		}
		setSize(rasterWidth * dotSize, rasterHeight * dotSize);
		return s;
	}

	public void setDotSize(int s) {
		dotSize = s;
		setSize(rasterWidth * dotSize, rasterHeight * dotSize);
	}

	public int getDotSize() {
		return (dotSize);
	}

	/**
	 * Overrides the superclass DisplayElement's show() method. This method
	 * positions the top left corner of the dot raster at the position given by
	 * the location parameter. Dot positions are scaled by the rasterSize
	 * parameter and dots are dotSize pixels wide and high. The bounding
	 * rectangle is updated.
	 */
	public void show() {
		int topLeftX = 0, topLeftY = 0, bottomRightX = 0, bottomRightY = 0;
		graphics.setColor(colorPar.getDevColor());
		int x, y;
		int x0 = location.x - rasterWidth * dotSize / 2;
		int y0 = location.y - rasterHeight * dotSize / 2;
		int col;
		for (int row = 0; row < rasterHeight; row++) {
			y = y0 + row * dotSize;
			col = 0;
			for (int k = 0; k < horizontalPieces; k++) {
				int m = dotArray[row][k];
				for (int b = 0; (b < 32) && (col < rasterWidth); b++) {
					if ((m & mask[b]) != 0) {
						x = x0 + col * dotSize;
						graphics.fillRect(x, y, dotSize, dotSize);
						if (x < topLeftX)
							topLeftX = x;
						else if (x > bottomRightX)
							bottomRightX = x;
						if (y < topLeftY)
							topLeftY = y;
						else if (y > bottomRightY)
							bottomRightY = y;
					}
					col++;
				}
			}
		}
		setBounds(topLeftX, topLeftY, bottomRightX - topLeftX + dotSize,
				bottomRightY - topLeftY + dotSize);
	}
}
