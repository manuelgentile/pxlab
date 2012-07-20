package de.pxlab.pxl;

import de.pxlab.util.Randomizer;
import java.awt.*;

import java.util.*;

/**
 * An array of RandomDot instances for building sparse random dot patterns.
 * 'Sparse' means that the array should only contain a limited number of dots
 * depending on available memory. Usually the limit will be around 5000 to 10000
 * dots.
 * 
 * <p>
 * All dots have a fixed quadratic size in pixels.
 * 
 * <p>
 * Dot positions are rasterized relative to screen pixels and the total pattern
 * size is given in number of horizontal and vertical raster positions. The
 * physical screen pattern width/height is the raster size multiplied by the
 * pattern width and height.
 * 
 * <p>
 * Every dot is an object of class RandomDot.
 * 
 * <p>
 * Dot sampling is described by the following properties:
 * 
 * <ul>
 * <li>The probability for any single raster cell to get a dot of the random dot
 * array. If this value is less or equal to 0 then a fixed number of dots is
 * being sampled.
 * <li>The number of dots to sample. This parameter is evaluated only if the
 * probability parameter is less than or equal to 0.
 * 
 * <li>The number horizontal and vertical raster cells of the dot pattern.
 * 
 * <li>A binary parameter 'circular', if true then the dots are sampled within
 * an elliptical area of the given size. If false then a rectangular area is
 * sampled. This parameter is evaluated only if the probability parameter p is
 * less or equal to 0.
 * </ul>
 * 
 * @version 0.2.1
 * @see RandomDot
 */
/*
 * 
 * 06/10/02
 * 
 * 2005/10/16 use setSize() to set pattern size
 */
public class RandomDotArray extends DisplayElement implements Cloneable {
	/** Holds the array of dots. */
	private ArrayList dotArray;
	/** The pixel width and height of a single dot. */
	private int dotSize = 1;
	/**
	 * The pixel size of a dot position raster cell. If this is larger than 1
	 * then dots will only be positioned at every rasterSize-th pixel.
	 */
	private int rasterSize = 1;
	/**
	 * Number of horizontal and vertical raster cells for this dot pattern. Note
	 * that this parameter together with rasterSize overrides the
	 * DisplayElement's size parameter.
	 */
	private int rasterWidth, rasterHeight;

	/** Return this dot array's number of horizontal raster elements. */
	public int getWidth() {
		return rasterWidth;
	}

	/** Return this dot array's number of vertical raster elements. */
	public int getHeight() {
		return rasterHeight;
	}

	/**
	 * Create a RandomDotArray with the given experimental parameter as its
	 * color parameter.
	 */
	public RandomDotArray(ExPar col) {
		colorPar = col;
		type = DisplayElement.RANDOM_DOTS;
	}

	/**
	 * Returns a full copy of this <tt>RandomDotArray</tt> instance.
	 * 
	 * @return a clone of this <tt>RandomDotArray</tt> instance.
	 */
	public Object clone() {
		try {
			RandomDotArray v = (RandomDotArray) super.clone();
			int n = dotArray.size();
			ArrayList da = new ArrayList(n);
			RandomDot d;
			for (int i = 0; i < n; i++) {
				d = (RandomDot) dotArray.get(i);
				da.add(new RandomDot(d.x, d.y, d.code));
			}
			v.setDotArray(da);
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Sample dots within a raster of given size.
	 * 
	 * @param p
	 *            probability for any single raster cell to get a dot of the
	 *            random dot array. If this value is less or equal to 0 then a
	 *            fixed number of dots is being sampled and the parameter n is
	 *            the number of dots.
	 * @param n
	 *            number of dots to sample. This parameter is evaluated only if
	 *            the parameter p is less than or equal to 0.
	 * @param w
	 *            number horizontal raster cells of the dot pattern.
	 * @param h
	 *            number vertical raster cells of the dot pattern.
	 * @param circular
	 *            if true then the dots are sampled within an elliptical area of
	 *            the given size. If false then a rectangular area is sampled.
	 *            This parameter is evaluated only if the parameter p is less or
	 *            equal to 0.
	 * @return the actual number of dots in the pattern.
	 */
	public int sampleDots(double p, int n, int w, int h, boolean circular) {
		int k = 0;
		if (p <= 0.0) {
			if (circular) {
				k = sampleDotsCircular(n, w, h);
			} else {
				k = sampleDotsRectangular(n, w, h);
			}
		} else {
			k = sampleDotsProb(p, w, h);
		}
		setSize(rasterWidth * rasterSize, rasterHeight * rasterSize);
		// System.out.println("Sampled " + k + " dots of " + (w*h));
		return k;
	}

	/**
	 * Sample dots within a given rectangular raster such that every raster cell
	 * has a fixed probability to get a dot.
	 * 
	 * @param p
	 *            the probability that a single raster cell gets a dot of the
	 *            pattern.
	 * @param w
	 *            number of horizontal dot raster cells of the whole pattern.
	 * @param h
	 *            number of vertical dot raster cells of the whole pattern. Note
	 *            that the actual horizontal and vertical screen size of the
	 *            pattern depends on the number of raster elements and the pixel
	 *            size rasterSize of a single raster element.
	 */
	private int sampleDotsProb(double p, int w, int h) {
		if ((p < 0.0) || (p > 1.0)) {
			throw new IllegalArgumentException(
					"Probability must be between 0.0 and 1.0");
		}
		rasterWidth = w;
		rasterHeight = h;
		int n = w * h;
		dotArray = new ArrayList((int) (n * (p + 0.1)));
		// Randomizer rnd = new Randomizer((seed == 0L)?
		// System.currentTimeMillis(): seed);
		Randomizer rnd = new Randomizer();
		/*
		 * byte[] a = new byte[n]; rnd.nextBytes(a); byte b =
		 * (byte)(Math.round(255.0 * p - 128.0)); int i = 0; for (int y = 0; y <
		 * h; y++) { for (int x = 0; x < w; x++) { if (a[i++] < b) {
		 * dotArray.add(new RandomDot(x, y, RandomDot.BACKGROUND)); } } }
		 */
		float fp = (float) p;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (rnd.nextFloat() < fp) {
					dotArray.add(new RandomDot(x, y, RandomDot.BACKGROUND));
				}
			}
		}
		return dotArray.size();
	}

	/**
	 * Sample a fixed number of dots within a given rectangular raster such that
	 * every raster cell has the same probability to get a dot.
	 * 
	 * @param n
	 *            the number of dots to sample.
	 * @param w
	 *            number of horizontal dot raster cells of the whole pattern.
	 * @param h
	 *            number of vertical dot raster cells of the whole pattern. Note
	 *            that the actual horizontal and vertical screen size of the
	 *            pattern depends on the number of raster elements and the pixel
	 *            size rasterSize of a single raster element.
	 */
	private int sampleDotsRectangular(int n, int w, int h) {
		rasterWidth = w;
		rasterHeight = h;
		dotArray = new ArrayList(n);
		// Randomizer rnd = new Randomizer((seed == 0L)?
		// System.currentTimeMillis(): seed);
		Randomizer rnd = new Randomizer();
		for (int i = 0; i < n; i++) {
			dotArray.add(new RandomDot(rnd.nextInt(w), rnd.nextInt(h),
					RandomDot.BACKGROUND));
		}
		return n;
	}

	/**
	 * Sample a fixed number of dots within a circular raster such that every
	 * raster cell has the same probability to get a dot.
	 * 
	 * @param n
	 *            the number of dots to sample.
	 * @param w
	 *            number of horizontal dot raster cells of the whole pattern.
	 * @param h
	 *            number of vertical dot raster cells of the whole pattern. Note
	 *            that the actual horizontal and vertical screen size of the
	 *            pattern depends on the number of raster elements and the pixel
	 *            size rasterSize of a single raster element.
	 */
	private int sampleDotsCircular(int n, int w, int h) {
		rasterWidth = w;
		rasterHeight = h;
		dotArray = new ArrayList(n);
		// Randomizer rnd = new Randomizer((seed == 0L)?
		// System.currentTimeMillis(): seed);
		Randomizer rnd = new Randomizer();
		int rx, ry;
		long rrx, rry;
		long w2 = w / 2;
		long h2 = h / 2;
		long r2 = w2 * w2 * h2 * h2;
		int i = 0;
		while (i < n) {
			rx = rnd.nextInt(w);
			ry = rnd.nextInt(h);
			rrx = h2 * (long) (rx - w2);
			rry = w2 * (long) (ry - h2);
			if ((rrx * rrx + rry * rry) < r2) {
				dotArray.add(new RandomDot(rx, ry, RandomDot.BACKGROUND));
				i++;
			}
		}
		return n;
	}

	public void setDotSize(int s) {
		dotSize = s;
	}

	public int getDotSize() {
		return (dotSize);
	}

	public void setRasterSize(int s) {
		rasterSize = s;
		setSize(rasterWidth * rasterSize, rasterHeight * rasterSize);
	}

	public int getRasterSize() {
		return (rasterSize);
	}

	public void clear() {
		dotArray.clear();
	}

	/**
	 * Find the number of dots in the array.
	 * 
	 * @return the number of dots in this random array.
	 */
	public int getCount() {
		if (dotArray == null)
			return 0;
		return dotArray.size();
	}

	/**
	 * Find the number of dots with a given code.
	 * 
	 * @param cd
	 *            code value if dots to be counted.
	 * @return the number of dots which have the code value given as an
	 *         argument.
	 */
	public int getCount(int cd) {
		if (dotArray == null)
			return 0;
		int n = dotArray.size();
		int m = 0;
		for (int i = 0; i < n; i++)
			if (((RandomDot) dotArray.get(i)).code == cd)
				m++;
		return m;
	}

	/**
	 * Get a dot from the array.
	 * 
	 * @param i
	 *            index of the dot to be returned.
	 * @return the RandomDot object at the given index.
	 */
	/*
	 * public RandomDot getDot(int i) { return (RandomDot)dotArray.get(i); }
	 */
	/**
	 * Get the array of dots.
	 * 
	 * @return the ArrayList containing all RandomDot objects of this instance.
	 */
	public ArrayList getDotArray() {
		return dotArray;
	}

	/** Set the array of dots. */
	public void setDotArray(ArrayList d) {
		dotArray = d;
	}

	public boolean addDot(int x, int y, int code) {
		return dotArray.add(new RandomDot(x, y, code));
	}

	/*
	 * public boolean setCodeForDot(int i, int code) { RandomDot d =
	 * (RandomDot)dotArray.get(i); d.code = code; return true; }
	 */
	/**
	 * Overrides the superclass DisplayElement's show() method. This method
	 * positions the top left corner of the dot raster at the position given by
	 * the location parameter. Dot positions are scaled by the rasterSize
	 * parameter and dots are dotSize pixels wide and high. The bounding
	 * rectangle is updated.
	 */
	public void show() {
		int n = dotArray.size();
		if (n > 0) {
			int topLeftX = 0, topLeftY = 0, bottomRightX = 0, bottomRightY = 0;
			graphics.setColor(colorPar.getDevColor());
			int x, y;
			int x0 = location.x - rasterWidth * rasterSize / 2;
			int y0 = location.y - rasterHeight * rasterSize / 2;
			RandomDot d;
			for (int i = 0; i < n; i++) {
				d = (RandomDot) dotArray.get(i);
				x = x0 + d.x * rasterSize;
				y = y0 + d.y * rasterSize;
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
			setBounds(topLeftX, topLeftY, bottomRightX - topLeftX + dotSize,
					bottomRightY - topLeftY + dotSize);
		}
	}
}
