package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;
import java.util.Random;

/**
 * A factory for grating pattern images. A grating is a pattern which at any
 * point (x,y) in its area contains a convex mixture C(x,y) of two colors A and
 * B:
 * 
 * <p>
 * C(x,y) = w(x,y)*A + [1-w(x,y)]*B
 * 
 * <p>
 * with 0 <= w(x,y) <= 1. We call it a 'grating' since the weight function w()
 * will usually be periodic in one or both of its argument dimensions.
 * 
 * @version 0.2.1
 * @author H. Irtel
 */
public class GratingFactory {
	private static final double Pi2 = Math.PI + Math.PI;
	private Random rnd = null;

	public float[][] randomMondrian(int width, int height, int columns,
			int rows, int gap, float[][] p) {
		int col_width = (width + columns - 1) / columns;
		// width = columns * col_width;
		int row_height = (height + rows - 1) / rows;
		// height = rows * row_height;
		if ((p == null) || (p[0].length != width) || (p.length != height)) {
			p = new float[height][width];
		}
		// Fill random array of mixture weights
		if (rnd == null)
			rnd = new Random();
		int ncr = columns * rows;
		float[] a = new float[ncr];
		for (int i = 0; i < ncr; i++) {
			double r;
			do {
				r = rnd.nextGaussian() / 4.0 + 0.5;
			} while ((r < 0.0) || (r > 1.0));
			a[i] = (float) r;
		}
		int row = 0;
		int row_count = 0;
		for (int y = 0; y < height; y++) {
			int col = 0;
			int col_count = 0;
			int rc = row * columns;
			for (int x = 0; x < width; x++) {
				p[y][x] = a[rc + col];
				col_count++;
				if (col_count >= col_width) {
					col_count = 0;
					col++;
				}
			}
			row_count++;
			if (row_count >= row_height) {
				row_count = 0;
				row++;
			}
		}
		return p;
	}

	/**
	 * Create an uncorrelated 2-dimensional Gaussian envelope. The envelope
	 * function is
	 * 
	 * <p>
	 * g(x,y) = exp( -( (x-s/2)**2/(2*sh**2) + (y-s/2)**2/(2*sv**2) ) )
	 * 
	 * <p>
	 * with s being the pattern size and sh and sv being the horizontal and
	 * vertical standard deviation.
	 * 
	 * <p>
	 * Envelope function values satisfy 0.0 < g <= 1.0. The envelope maximum is
	 * at (center_x, center_y).
	 * 
	 * @param center_x
	 *            horizontal center pixel position within pattern frame.
	 * @param center_y
	 *            vertical center pixel position within pattern frame.
	 * @param width
	 *            number horizontal pixel points.
	 * @param height
	 *            number vertical pixel points.
	 * @param horGaussianStdDev
	 *            horizontal standard deviation of Gaussian envelope. Scale is
	 *            in pixel points.
	 * @param verGaussianStdDev
	 *            vertical standard deviation of Gaussian envelope. Scale is in
	 *            pixel points.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the Gaussian envelope.
	 */
	public float[][] gaussian(int center_x, int center_y, int width,
			int height, double horGaussianStdDev, double verGaussianStdDev,
			float[][] p) {
		if ((p == null) || (p[0].length != width) || (p.length != height)) {
			p = new float[height][width];
		}
		double horGaussianVariance = 2.0 * horGaussianStdDev
				* horGaussianStdDev;
		double verGaussianVariance = 2.0 * verGaussianStdDev
				* verGaussianStdDev;
		int x, y, xx, yy;
		float min = 10.0F, max = -10.0F;
		float z;
		for (y = 0; y < height; y++) {
			yy = y - center_y;
			for (x = 0; x < width; x++) {
				xx = x - center_x;
				z = (float) (Math
						.exp(-((xx * xx) / horGaussianVariance + (yy * yy)
								/ verGaussianVariance)));
				p[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.gaussian(): " + min + " to " +
		// max);
		return p;
	}

	public float[][] gaussian(int width, int height, double horGaussianStdDev,
			double verGaussianStdDev, float[][] p) {
		return gaussian(width / 2, height / 2, width, height,
				horGaussianStdDev, verGaussianStdDev, p);
	}

	public float[][] gaussian(int size, double horGaussianStdDev,
			double verGaussianStdDev, float[][] p) {
		return gaussian(size / 2, size / 2, size, size, horGaussianStdDev,
				verGaussianStdDev, p);
	}

	/**
	 * Create a Gabor pattern of weights in the range [0.0, 1.0]. The weight
	 * function is a Gaussian with horizontal and vertical standard deviations
	 * being independent.
	 * 
	 * @param width
	 *            number horizontal pixel points.
	 * @param height
	 *            number vertical pixel points.
	 * @param frequency
	 *            frequency of the sinusoid base wave.
	 * @param harmonics
	 *            array of harmonics to include. The array contains the integer
	 *            frequency multiples which should be included.
	 * @param phase
	 *            phase array for all harmonics.
	 * @param amplitude
	 *            amplitude for all harmonics.
	 * @param maxAmp
	 *            maximum amplitude which may result from adding all harmonics.
	 *            Component amplitudes are scaled such that the total amplitude
	 *            is not larger than this.
	 * @param orientation
	 *            counter-clockwise angle of rotation.
	 * @param shift
	 *            a shift value along the orientation axis. May be used for
	 *            grating motion stimuli.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given width and height
	 *            then a new matrix is created.
	 * @return a matrix of size width * height containing the Gabor pattern.
	 *         values.
	 */
	public float[][] complexSinusoid(int width, int height, double frequency,
			int[] harmonics, double[] phase, double[] amplitude, double maxAmp,
			double orientation, double shift, float[][] p) {
		if ((p == null) || (p[0].length != width) || (p.length != height)) {
			p = new float[height][width];
		}
		double[] a = new double[amplitude.length];
		double sa = 0.0;
		for (int i = 0; i < amplitude.length; i++)
			sa += amplitude[i];
		sa = Math.abs(sa);
		if (sa > maxAmp) {
			sa /= maxAmp;
			for (int i = 0; i < amplitude.length; i++)
				a[i] = amplitude[i] / sa;
		} else {
			for (int i = 0; i < amplitude.length; i++)
				a[i] = amplitude[i];
		}
		int center_x = width / 2;
		int center_y = height / 2;
		double nx = (double) (width - 1);
		double ny = (double) (height - 1);
		double pi2f = Pi2 * frequency;
		double asin = Math.sin(Pi2 * orientation / 360.0);
		double acos = Math.cos(Pi2 * orientation / 360.0);
		int x, y;
		double xx, yy, yyy;
		float min = 10.0F, max = -10.0F;
		float z;
		for (y = 0; y < height; y++) {
			yy = (double) (y - center_y) / ny;
			for (x = 0; x < width; x++) {
				xx = (double) (x - center_x) / nx;
				// xxx = acos * xx - asin * yy;
				yyy = asin * xx + acos * yy;
				z = cosine(pi2f, yyy, harmonics, phase, a, shift);
				p[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.complexSinusoid(): " + min +
		// " to " + max);
		return (p);
	}

	private float cosine(double pi2f, double x, int[] harmonics,
			double[] phase, double[] amplitude, double shift) {
		double s;
		s = amplitude[0]
				* Math.cos(harmonics[0] * (pi2f * x - shift) - phase[0]);
		if (harmonics.length > 1) {
			for (int i = 1; i < harmonics.length; i++) {
				s += amplitude[i]
						* Math.cos(harmonics[i] * (pi2f * x - shift) - phase[i]);
			}
		}
		return (float) s;
	}

	/**
	 * Compute a sinusoidal grating pattern. The function used is a cosine with
	 * a maximum at the pattern's center. Frequency is relative to the pattern
	 * size such that 1 Hz corresponds to a period identical to the pattern
	 * size.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param frequency
	 *            the pattern frequency. This is the number of periods in the
	 *            pattern.
	 * @param phase
	 *            the phase angle in radians.
	 * @param amplitude
	 *            the amplitude factor for the sinusoid. This factor should be
	 *            less or equal to 1.0.
	 * @param orientation
	 *            the orientation of the sinusoid in degrees. An orientation of
	 *            0 is a vertical sinusoid.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the sinusoidal pattern.
	 */
	public float[][] sinusoid(int size, double frequency, double phase,
			double amplitude, double orientation, float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		int center = size / 2;
		double n = size - 1.0;
		double pi2f = Pi2 * frequency;
		double asin = Math.sin(Pi2 * orientation / 360.0);
		double acos = Math.cos(Pi2 * orientation / 360.0);
		int x, y;
		double xx, xxx, yy, yyy;
		float min = 10.0F, max = -10.0F;
		float z;
		for (y = 0; y < size; y++) {
			yy = (double) (y - center) / n;
			for (x = 0; x < size; x++) {
				xx = (double) (x - center) / n;
				// xxx = acos * xx - asin * yy;
				yyy = asin * xx + acos * yy;
				z = (float) (amplitude * Math.cos(pi2f * yyy - phase));
				p[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.sinusoid(): " + min + " to " +
		// max);
		return p;
	}

	/**
	 * Compute a concentric sinusoidal grating pattern. The function used is a
	 * cosine with a maximum at the pattern's center. Frequency is relative to
	 * the pattern size such that 1 Hz corresponds to a period identical to the
	 * pattern size.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param frequency
	 *            the pattern frequency. This is the number of periods in the
	 *            pattern.
	 * @param phase
	 *            the phase angle in radians.
	 * @param amplitude
	 *            the amplitude factor for the sinusoid. This factor should be
	 *            less or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the sinusoidal pattern.
	 */
	public float[][] concentricSinusoid(int size, double frequency,
			double phase, double amplitude, float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		int center = size / 2;
		double n = size - 1.0;
		double pi2f = Pi2 * frequency;
		int x, y;
		double xx, xxx, yy, yyyy, r;
		float min = 10.0F, max = -10.0F;
		float z;
		for (y = 0; y < size; y++) {
			yy = (double) (y - center) / n;
			yyyy = yy * yy;
			for (x = 0; x < size; x++) {
				xx = (double) (x - center) / n;
				// xxx = acos * xx - asin * yy;
				r = Math.sqrt(xx * xx + yyyy);
				z = (float) (amplitude * Math.cos(pi2f * r - phase));
				p[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.sinusoid(): " + min + " to " +
		// max);
		return p;
	}

	/**
	 * Compute a Bessel grating pattern. The function used is j0 with a maximum
	 * at the pattern's center. Frequency is relative to the pattern size such
	 * that 1 Hz corresponds to a period identical to the pattern size.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param frequency
	 *            the pattern frequency. This is the number of periods in the
	 *            pattern.
	 * @param amplitude
	 *            the amplitude factor for the sinusoid. This factor should be
	 *            less or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the Bessel pattern.
	 */
	public float[][] bessel(int size, double frequency, double amplitude,
			float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		int center = size / 2;
		double n = size - 1.0;
		double pi2f = Pi2 * frequency;
		int x, y;
		double xx, xxx, yy, yyyy, r;
		float min = 10.0F, max = -10.0F;
		float z;
		for (y = 0; y < size; y++) {
			yy = (double) (y - center) / n;
			yyyy = yy * yy;
			for (x = 0; x < size; x++) {
				xx = (double) (x - center) / n;
				// xxx = acos * xx - asin * yy;
				r = Math.sqrt(xx * xx + yyyy);
				z = (float) (amplitude * j0(pi2f * r));
				p[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.bessel(): " + min + " to " + max);
		return p;
	}

	/**
	 * Compute a horizontal edge. The function values satisfy -amplitude <= f <=
	 * amplitude.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param amplitude
	 *            the amplitude factor for the edge. This factor should be less
	 *            or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] horizontalEdge(int size, double amplitude, float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		float a = (float) amplitude;
		float b = -a;
		int center = size / 2;
		int x, y;
		for (y = 0; y < center; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = a;
			}
		}
		for (; y < size; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = b;
			}
		}
		return p;
	}

	public float[][] zero(int size, float[][] p) {
		return constant(size, 0.0, p);
	}

	/**
	 * Compute a pattern with constant value everywhere.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param a
	 *            the pattern value.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] constant(int size, double a, float[][] p) {
		return constant(size, size, a, p);
	}

	public float[][] constant(int width, int height, double a, float[][] p) {
		if ((p == null) || (p[0].length != width) || (p.length != height)) {
			p = new float[height][width];
		}
		int x, y;
		float af = (float) a;
		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				p[y][x] = af;
			}
		}
		return p;
	}

	/**
	 * Compute a horizontal line pattern. The function values satisfy 0.0 <= f
	 * <= amplitude.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param w
	 *            the width of the line in pixels.
	 * @param amplitude
	 *            the amplitude factor for the line. This factor should be less
	 *            or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] horizontalLine(int size, int w, double amplitude,
			float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		float a = (float) amplitude;
		float b = 0.0F;
		int y1 = (size - w) / 2;
		if (y1 >= size)
			y1 = size;
		int y2 = y1 + w;
		if (y2 >= size)
			y2 = size;
		int x, y;
		for (y = 0; y < y1; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = b;
			}
		}
		for (; y < y2; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = a;
			}
		}
		for (; y < size; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = b;
			}
		}
		return p;
	}

	/**
	 * Compute a horizontal dipole pattern. The function values satisfy
	 * -amplitude <= f <= amplitude.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param w
	 *            the width of the non-zero dipole region in pixels.
	 * @param amplitude
	 *            the amplitude factor for the dipole. This factor should be
	 *            less or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] horizontalDipole(int size, int w, double amplitude,
			float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		int w3 = w / 3;
		if (w3 < 1)
			w3 = 1;
		w = 3 * w3;
		float a = (float) amplitude;
		float b = 0.0F;
		float c = -a;
		int y1 = (size - w) / 2;
		if (y1 >= size)
			y1 = size;
		int y2 = y1 + w3;
		if (y2 >= size)
			y2 = size;
		int y3 = y2 + w3;
		if (y3 >= size)
			y3 = size;
		int y4 = y3 + w3;
		if (y4 >= size)
			y4 = size;
		int x, y;
		for (y = 0; y < y1; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = b;
			}
		}
		for (; y < y2; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = a;
			}
		}
		for (; y < y3; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = b;
			}
		}
		for (; y < y4; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = c;
			}
		}
		for (; y < size; y++) {
			for (x = 0; x < size; x++) {
				p[y][x] = b;
			}
		}
		return p;
	}

	/**
	 * Compute a centered disk pattern. The function values satisfy 0.0 <= f <=
	 * amplitude.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param w
	 *            the diameter of the disk in pixels.
	 * @param amplitude
	 *            the amplitude factor for the disk. This factor should be less
	 *            or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] disk(int size, int w, double amplitude, float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		float a = (float) amplitude;
		float b = 0.0F;
		int center = size / 2;
		int x, y, dx, dy, dx2, dy2;
		int r2 = w * w / 4;
		for (y = 0; y < size; y++) {
			dy = y - center;
			dy2 = dy * dy;
			for (x = 0; x < size; x++) {
				dx = x - center;
				dx2 = dx * dx;
				p[y][x] = ((dx2 + dy2) <= r2) ? a : b;
			}
		}
		return p;
	}

	/**
	 * Compute a checkerboard pattern. The function values satisfy -amplitude <=
	 * f <= amplitude.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param w
	 *            the horizontal width of the checkerboard raster in pixels.
	 * @param amplitude
	 *            the amplitude factor for the checkerboard. This factor should
	 *            be less or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] checkerboard(int size, int w, double amplitude, float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		float a = (float) amplitude;
		float b = -a;
		int d = Math.abs(w);
		int d2 = d / 2;
		int ss = d * ((size / d) + 1);
		int yshift = d - ((size + d) / 2) % d;
		int xshift = yshift;
		int xys = xshift + yshift;
		int xyt = xshift - yshift;
		// System.out.println("GratingFactory.checkerboard(): d = " + d);
		int x, y, xx, yy;
		int c = size / 2;
		int s, t;
		float z;
		float min = 10.0F, max = -10.0F;
		for (y = 0; y < size; y++) {
			yy = y - c;
			for (x = 0; x < size; x++) {
				xx = x - c;
				s = (xx + yy + ss) / d;
				t = (x - y + ss) / d;
				if ((s % 2) == 0) {
					z = ((t % 2) == 0) ? b : a;
				} else {
					z = ((t % 2) == 0) ? a : b;
				}
				p[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.checkerboard(): " + min + " to " +
		// max);
		return p;
	}

	/**
	 * Compute a binary white noise pattern. The function values satisfy f =
	 * +/-amplitude.
	 * 
	 * @param size
	 *            the number of horizontal and vertical pixels in the pattern.
	 * @param dotSize
	 *            the size of a single random dot in pixels.
	 * @param amplitude
	 *            the amplitude factor for the pattern. This factor should be
	 *            less or equal to 1.0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] whiteNoise(int size, int dotSize, double amplitude,
			float[][] p) {
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		int d = Math.abs(dotSize);
		if (d > 1)
			while ((size % d) != 0)
				d--;
		int n = size / d;
		float a = (float) amplitude;
		float b = -a;
		Random rnd = new Random();
		int x, y;
		float s;
		for (y = 0; y < size; y += d) {
			for (x = 0; x < size; x += d) {
				s = rnd.nextBoolean() ? a : b;
				for (int dy = 0; dy < d; dy++) {
					int ydy = y + dy;
					for (int dx = 0; dx < d; dx++) {
						p[ydy][x + dx] = s;
					}
				}
			}
		}
		return p;
	}

	/**
	 * Compute a pattern from an image. Only the blue image channel is used. The
	 * image pixel values are transformed such that the given reference
	 * luminance corresponds to a pattern value of 0.
	 * 
	 * @param amplitude
	 *            the amplitude factor for the output pattern.
	 * @param ip
	 *            an image which is used as the pattern. The image must be
	 *            square sized.
	 * @param lm
	 *            luminance proportion which corresponds to pattern value 0.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given size then a new
	 *            matrix is created.
	 * @return a float matrix containing the pattern.
	 */
	public float[][] imagePattern(double amplitude, BufferedImage ip,
			double lm, float[][] p) {
		int size = ip.getWidth();
		if ((p == null) || (p[0].length != size) || (p.length != size)) {
			p = new float[size][size];
		}
		float a0 = (float) (amplitude / lm);
		float a1 = (float) (amplitude / (1.0 - lm));
		int x, y;
		float min = 10.0F, max = -10.0F;
		float b, z;
		for (y = 0; y < size; y++) {
			for (x = 0; x < size; x++) {
				b = (float) (Math
						.pow((double) (new Color(ip.getRGB(x, y)).getBlue()) / 255.0,
								2.4) - lm);
				z = b * ((b > 0.0F) ? a1 : a0);
				p[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.imagePattern(): " + min + " to " +
		// max);
		return p;
	}

	/**
	 * Multiply the elements of a pattern by a constant factor: s[][] = a *
	 * p[][].
	 * 
	 * @param a
	 *            the scalar factor.
	 * @param p
	 *            the pattern to be scaled.
	 * @return the product pattern.
	 */
	public float[][] product(double a, float[][] p) {
		int width;
		int height;
		float[][] s = null;
		float min = 10.0F, max = -10.0F;
		width = p[0].length;
		height = p.length;
		s = new float[height][width];
		int x, y;
		float aa = (float) a;
		float z;
		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				z = aa * p[y][x];
				s[y][x] = z;
				if (z < min)
					min = z;
				if (z > max)
					max = z;
			}
		}
		// System.out.println("GratingFactory.product(): " + min + " to " +
		// max);
		return s;
	}

	/**
	 * Compute the product of two patterns: s[][] = p[][] * q[][].
	 * 
	 * @param p
	 *            the first component pattern.
	 * @param q
	 *            the second component pattern.
	 * @return the product pattern.
	 */
	public float[][] product(float[][] p, float[][] q) {
		int width;
		int height;
		float[][] s = null;
		float min = 10.0F, max = -10.0F;
		if ((p[0].length == q[0].length) && (p.length == q.length)) {
			width = p[0].length;
			height = p.length;
			s = new float[height][width];
			int x, y;
			float z;
			for (y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
					z = p[y][x] * q[y][x];
					s[y][x] = z;
					if (z < min)
						min = z;
					if (z > max)
						max = z;
				}
			}
		}
		// System.out.println("GratingFactory.product(): " + min + " to " +
		// max);
		return s;
	}

	/**
	 * Insert pattern a[][] at position (px, py) of a target pattern.
	 * 
	 * @param a
	 *            the pattern to be inserted. Its size must be such that it
	 *            completely fits into the target pattern at the intended
	 *            position.
	 * @param px
	 *            the horizontal coordinate of the top left position of the
	 *            insertion pattern in the target.
	 * @param py
	 *            the vertical coordinate of the top left position of the
	 *            insertion pattern in the target.
	 * @param p
	 *            the pattern where insertion should be done.
	 * @return the insertion pattern.
	 */
	public float[][] insert(float[][] a, int px, int py, float[][] p) {
		int width = a[0].length;
		int height = a.length;
		if ((p[0].length >= (px + width)) && (p.length >= py + height)) {
			int x, y;
			float z;
			for (y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
					p[py + y][px + x] = a[y][x];
				}
			}
		}
		return p;
	}

	/**
	 * Compute the sum of two patterns: s[][] = p[][] + q[][].
	 * 
	 * @param p
	 *            the first component pattern.
	 * @param q
	 *            the second component pattern.
	 * @return the summation pattern.
	 */
	public float[][] sum(float[][] p, float[][] q) {
		int width;
		int height;
		float[][] s = null;
		float min = 10.0F, max = -10.0F;
		if ((p[0].length == q[0].length) && (p.length == q.length)) {
			width = p[0].length;
			height = p.length;
			s = new float[height][width];
			int x, y;
			float z;
			for (y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
					z = p[y][x] + q[y][x];
					s[y][x] = z;
					if (z < min)
						min = z;
					if (z > max)
						max = z;
				}
			}
		}
		// System.out.println("GratingFactory.product(): " + min + " to " +
		// max);
		return s;
	}

	private static double j0(double x) {
		double ax;
		if ((ax = Math.abs(x)) < 8.0) {
			double y = x * x;
			double ans1 = 57568490574.0
					+ y
					* (-13362590354.0 + y
							* (651619640.7 + y
									* (-11214424.18 + y
											* (77392.33017 + y * (-184.9052456)))));
			double ans2 = 57568490411.0
					+ y
					* (1029532985.0 + y
							* (9494680.718 + y
									* (59272.64853 + y
											* (267.8532712 + y * 1.0))));
			return ans1 / ans2;
		} else {
			double z = 8.0 / ax;
			double y = z * z;
			double xx = ax - 0.785398164;
			double ans1 = 1.0
					+ y
					* (-0.1098628627e-2 + y
							* (0.2734510407e-4 + y
									* (-0.2073370639e-5 + y * 0.2093887211e-6)));
			double ans2 = -0.1562499995e-1
					+ y
					* (0.1430488765e-3 + y
							* (-0.6911147651e-5 + y
									* (0.7621095161e-6 - y * 0.934935152e-7)));
			return Math.sqrt(0.636619772 / ax)
					* (Math.cos(xx) * ans1 - z * Math.sin(xx) * ans2);
		}
	}
}
