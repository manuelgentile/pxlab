package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;
import java.util.Random;

/**
 * A spatial distribution of additive mixtures of up to 4 light sources. The
 * mixture components are defined by a PxlColor value and an associated 2D
 * intensity weight function.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see GratingFactory
 */
/*
 * 
 * 01/10/03 added background light
 * 
 * 07/18/03 we now have three types of distributions: a simple disk, a
 * defocussed disk and a simulated point light source at a certain distance.
 * 
 * 07/27/04 allow for additive and convex mixtures.
 */
public class LightDistribution extends BitMapElement implements
		LightDistributionCodes, LightMixtureCodes {
	private static final int LAST_TYPE = DISTANT_POINT_LIGHT;
	protected float[][][] pattern = null;
	protected PxlColor[] colors = null;
	protected ExPar colorPar2, colorPar3, colorPar4;
	protected ExPar backgroundColorPar = null;
	protected boolean unmasked = true;
	/** Pattern: DISK, DEFOCUSSED_DISK, or DISTANT_POINT_LIGHT */
	protected int distributionType = DISK;
	/** Mixture type: ADDITIVE_MIXTURE or CONVEX_MIXTURE */
	protected int mixtureType = ADDITIVE_MIXTURE;

	/**
	 * Destroy all resources associated with this light distribution.
	 */
	public void destroy() {
		if (pattern != null) {
			int n = pattern.length;
			if (pattern[0] != null) {
				int h = pattern[0].length;
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < h; j++) {
						pattern[i][j] = null;
					}
					pattern[i] = null;
				}
			}
			pattern = null;
		}
		colors = null;
		flush();
		System.gc();
	}

	/** Create a light distribution with a single light source. */
	public LightDistribution(ExPar c1) {
		colorPar = c1;
	}

	/** Create a light distribution with two light sources. */
	public LightDistribution(ExPar c1, ExPar c2) {
		colorPar = c1;
		colorPar2 = c2;
	}

	/** Create a light distribution with three light sources. */
	public LightDistribution(ExPar c1, ExPar c2, ExPar c3) {
		colorPar = c1;
		colorPar2 = c2;
		colorPar3 = c3;
	}

	/** Create a light distribution with four light sources. */
	public LightDistribution(ExPar c1, ExPar c2, ExPar c3, ExPar c4) {
		colorPar = c1;
		colorPar2 = c2;
		colorPar3 = c3;
		colorPar4 = c4;
	}

	public void setColorPar2(ExPar c2) {
		colorPar2 = c2;
	}

	public void setColorPar3(ExPar c3) {
		colorPar3 = c3;
	}

	public void setColorPar4(ExPar c4) {
		colorPar4 = c4;
	}

	public void setDistributionType(int t) {
		if ((t >= 0) && (t <= LAST_TYPE))
			distributionType = t;
	}

	/**
	 * Set the mixtue type to be used for computing this light distribution.
	 * 
	 * @param t
	 *            is either ADDITIVE_MIXTURE (default) or CONVEX_MIXTURE.
	 */
	public void setMixtureType(int t) {
		if ((t == ADDITIVE_MIXTURE) || (t == CONVEX_MIXTURE))
			mixtureType = t;
	}

	/**
	 * Signal a change of the color coordinates of one of the light sources.
	 */
	public void computeColors() {
		int n = colors.length;
		colors[0] = colorPar.getPxlColor();
		if (n > 1)
			colors[1] = colorPar2.getPxlColor();
		if (n > 2)
			colors[2] = colorPar3.getPxlColor();
		if (n > 3)
			colors[3] = colorPar4.getPxlColor();
		if (pattern != null) {
			setImage(pattern, colors);
		}
	}

	/**
	 * Control the masking flag.
	 * 
	 * @param m
	 *            if true then a radial mask is used such that the pattern
	 *            appears to be an oval cut-out. If false then no mask is used
	 *            and the pattern fills the whole rectangular area.
	 */
	public void setMasking(boolean m) {
		unmasked = !m;
	}

	/**
	 * Set the experimental parameter which describes the background pedestal
	 * color.
	 */
	public void setBackgroundColorPar(ExPar p) {
		backgroundColorPar = p;
	}

	/**
	 * Set the geometric parameters for simulating a single point light source
	 * behind a matte projection screen in darkness.
	 * 
	 * @param rel_x
	 *            relative horizontal position of the light source.
	 * @param rel_y
	 *            relative vertical position of the light source.
	 * @param amp
	 *            amplitude factor. Should satisfy 0 <= amp <= 1.0.
	 * @param inner
	 *            inner distribution diameter.
	 * @param outer
	 *            distribution parameter.
	 */
	public void setPointLight(int width, int height, double rel_x,
			double rel_y, double amp, double inner, double outer) {
		pattern = new float[1][][];
		float[][] p = null;
		p = radialPattern(width, height, (int) (width * rel_x),
				(int) (height * rel_y), amp, inner, outer, p);
		pattern[0] = p;
		colors = new PxlColor[1];
		colors[0] = colorPar.getPxlColor();
		setImage(pattern, colors);
	}

	/**
	 * Set the geometric parameters for simulating two point light sources
	 * behind a matte projection screen in darkness.
	 * 
	 * @param rel_x_1
	 *            relative horizontal position of the 1st light source.
	 * @param rel_y_1
	 *            relative vertical position of the 1st light source.
	 * @param rel_x_2
	 *            relative horizontal position of the 2nd light source.
	 * @param rel_y_2
	 *            relative vertical position of the 2nd light source.
	 * @param amp
	 *            amplitude factor. Should satisfy 0 <= amp <= 1.0.
	 * @param inner
	 *            inner section diameter.
	 * @param outer
	 *            outer section diameter. screen.
	 */
	public void setPointLights(int width, int height, double rel_x_1,
			double rel_y_1, double rel_x_2, double rel_y_2, double amp,
			double inner, double outer) {
		pattern = new float[2][][];
		float[][] p1 = null;
		p1 = radialPattern(width, height, (int) (width * rel_x_1),
				(int) (height * rel_y_1), amp, inner, outer, p1);
		float[][] p2 = null;
		p2 = radialPattern(width, height, (int) (width * rel_x_2),
				(int) (height * rel_y_2), amp, inner, outer, p2);
		pattern[0] = p1;
		pattern[1] = p2;
		colors = new PxlColor[2];
		colors[0] = colorPar.getPxlColor();
		colors[1] = colorPar2.getPxlColor();
		setImage(pattern, colors);
	}

	/**
	 * Set the geometric parameters for simulating three point light sources
	 * behind a matte projection screen in darkness.
	 * 
	 * @param rel_x_1
	 *            relative horizontal position of the 1st light source.
	 * @param rel_y_1
	 *            relative vertical position of the 1st light source.
	 * @param rel_x_2
	 *            relative horizontal position of the 2nd light source.
	 * @param rel_y_2
	 *            relative vertical position of the 2nd light source.
	 * @param rel_x_3
	 *            relative horizontal position of the 3rd light source.
	 * @param rel_y_3
	 *            relative vertical position of the 3rd light source.
	 * @param amp
	 *            amplitude factor. Should satisfy 0 <= amp <= 1.0.
	 * @param inner
	 *            inner section diameter.
	 * @param outer
	 *            outer section diameter.
	 */
	public void setPointLights(int width, int height, double rel_x_1,
			double rel_y_1, double rel_x_2, double rel_y_2, double rel_x_3,
			double rel_y_3, double amp, double inner, double outer) {
		pattern = new float[3][][];
		float[][] p1 = null;
		p1 = radialPattern(width, height, (int) (width * rel_x_1),
				(int) (height * rel_y_1), amp, inner, outer, p1);
		float[][] p2 = null;
		p2 = radialPattern(width, height, (int) (width * rel_x_2),
				(int) (height * rel_y_2), amp, inner, outer, p2);
		float[][] p3 = null;
		p3 = radialPattern(width, height, (int) (width * rel_x_3),
				(int) (height * rel_y_3), amp, inner, outer, p3);
		pattern[0] = p1;
		pattern[1] = p2;
		pattern[2] = p3;
		colors = new PxlColor[3];
		colors[0] = colorPar.getPxlColor();
		colors[1] = colorPar2.getPxlColor();
		colors[2] = colorPar3.getPxlColor();
		setImage(pattern, colors);
	}

	/**
	 * Set the geometric parameters for simulating three point light sources
	 * behind a matte projection screen in darkness.
	 * 
	 * @param rel_x_1
	 *            relative horizontal position of the 1st light source.
	 * @param rel_y_1
	 *            relative vertical position of the 1st light source.
	 * @param rel_x_2
	 *            relative horizontal position of the 2nd light source.
	 * @param rel_y_2
	 *            relative vertical position of the 2nd light source.
	 * @param rel_x_3
	 *            relative horizontal position of the 3rd light source.
	 * @param rel_y_3
	 *            relative vertical position of the 3rd light source.
	 * @param rel_x_4
	 *            relative horizontal position of the 4th light source.
	 * @param rel_y_4
	 *            relative vertical position of the 4th light source.
	 * @param amp
	 *            amplitude factor. Should satisfy 0 <= amp <= 1.0.
	 * @param inner
	 *            inner section diameter.
	 * @param outer
	 *            outer section diameter.
	 */
	public void setPointLights(int width, int height, double rel_x_1,
			double rel_y_1, double rel_x_2, double rel_y_2, double rel_x_3,
			double rel_y_3, double rel_x_4, double rel_y_4, double amp,
			double inner, double outer) {
		pattern = new float[4][][];
		float[][] p1 = null;
		p1 = radialPattern(width, height, (int) (width * rel_x_1),
				(int) (height * rel_y_1), amp, inner, outer, p1);
		float[][] p2 = null;
		p2 = radialPattern(width, height, (int) (width * rel_x_2),
				(int) (height * rel_y_2), amp, inner, outer, p2);
		float[][] p3 = null;
		p3 = radialPattern(width, height, (int) (width * rel_x_3),
				(int) (height * rel_y_3), amp, inner, outer, p3);
		float[][] p4 = null;
		p4 = radialPattern(width, height, (int) (width * rel_x_4),
				(int) (height * rel_y_4), amp, inner, outer, p4);
		pattern[0] = p1;
		pattern[1] = p2;
		pattern[2] = p3;
		pattern[3] = p4;
		colors = new PxlColor[4];
		colors[0] = colorPar.getPxlColor();
		colors[1] = colorPar2.getPxlColor();
		colors[2] = colorPar3.getPxlColor();
		colors[3] = colorPar4.getPxlColor();
		setImage(pattern, colors);
	}

	public void set1ColorRandomMondrian(int width, int height, int columns,
			int rows, int gap) {
		pattern = new float[1][][];
		float[][] p1 = null;
		p1 = randomMondrian(width, height, columns, rows, gap, p1);
		pattern[0] = p1;
		colors = new PxlColor[1];
		colors[0] = colorPar.getPxlColor();
		setImage(pattern, colors);
	}

	public void set2ColorRandomMondrian(int width, int height, int columns,
			int rows, int gap) {
		pattern = new float[2][][];
		float[][] p1 = null;
		p1 = randomMondrian(width, height, columns, rows, gap, p1);
		float[][] p2 = null;
		p2 = randomMondrian(width, height, columns, rows, gap, p2);
		pattern[0] = p1;
		pattern[1] = p2;
		colors = new PxlColor[2];
		colors[0] = colorPar.getPxlColor();
		colors[1] = colorPar2.getPxlColor();
		setImage(pattern, colors);
	}

	public void set3ColorRandomMondrian(int width, int height, int columns,
			int rows, int gap) {
		pattern = new float[3][][];
		float[][] p1 = null;
		p1 = randomMondrian(width, height, columns, rows, gap, p1);
		float[][] p2 = null;
		p2 = randomMondrian(width, height, columns, rows, gap, p2);
		float[][] p3 = null;
		p3 = randomMondrian(width, height, columns, rows, gap, p3);
		pattern[0] = p1;
		pattern[1] = p2;
		pattern[2] = p3;
		colors = new PxlColor[3];
		colors[0] = colorPar.getPxlColor();
		colors[1] = colorPar2.getPxlColor();
		colors[2] = colorPar3.getPxlColor();
		setImage(pattern, colors);
	}

	public float[][] radialPattern(int width, int height, int center_x,
			int center_y, double amplitude, double inner_diameter,
			double outer_diameter, float[][] p) {
		if (distributionType == DEFOCUSSED_DISK) {
			return defocussedDisk(width, height, center_x, center_y, amplitude,
					inner_diameter, outer_diameter, p);
		} else if (distributionType == DISTANT_POINT_LIGHT) {
			return distantPointLight(width, height, center_x, center_y,
					amplitude, outer_diameter, p);
		}
		return disk(width, height, center_x, center_y, amplitude,
				outer_diameter, p);
	}

	/**
	 * Create a disk pattern.
	 * 
	 * @param width
	 *            number horizontal pixel points.
	 * @param height
	 *            number vertical pixel points.
	 * @param center_x
	 *            horizontal center position of the disk given in pixels
	 *            relative to the top left corner of the pattern.
	 * @param center_y
	 *            vertical center position of the disk given in pixels relative
	 *            to the top left corner of the pattern.
	 * @param amplitude
	 *            amplitude. Should satisfy 0 <= amp <= 1.0.
	 * @param diameter
	 *            the diameter of the disk relative to the pattern size.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given width and height
	 *            then a new matrix is created.
	 * @return a matrix of size width * height containing the weight function.
	 */
	public float[][] disk(int width, int height, int center_x, int center_y,
			double amplitude, double diameter, float[][] p) {
		if ((p == null) || (p[0].length != width) || (p.length != height)) {
			p = new float[height][width];
		}
		double xx;
		double yy, yy2;
		double rxy2;
		double d2 = diameter * diameter / 4.0;
		double nx1 = (double) (width - 1);
		double ny1 = (double) (height - 1);
		if (amplitude == 0.0) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					p[y][x] = 0.0F;
				}
			}
		} else {
			for (int y = 0; y < height; y++) {
				yy = (double) (y - center_y) / ny1;
				yy2 = yy * yy;
				for (int x = 0; x < width; x++) {
					xx = (double) (x - center_x) / nx1;
					rxy2 = xx * xx + yy2;
					p[y][x] = (rxy2 < d2) ? (float) amplitude : 0.0F;
				}
			}
		}
		// fprintf(logstream, "frequency range: %f-%f\n", exp(_f_min)/PI2,
		// exp(f-f_step)/PI2);
		return (p);
	}

	/**
	 * Create a pattern which describes a defocussed disk with a sinusoidal
	 * transition phase between the inner and outer diameter.
	 * 
	 * @param width
	 *            number horizontal pixel points.
	 * @param height
	 *            number vertical pixel points.
	 * @param center_x
	 *            horizontal center position of the disk given in pixels
	 *            relative to the top left corner of the pattern.
	 * @param center_y
	 *            vertical center position of the disk given in pixels relative
	 *            to the top left corner of the pattern.
	 * @param amplitude
	 *            amplitude. Should satisfy 0 <= amp <= 1.0.
	 * @param diameter
	 *            the diameter of the disk center relative to the pattern size.
	 * @param size
	 *            the diameter of outer disk area relative to the pattern size.
	 *            The ring between 'diameter' and 'size' constitutes the
	 *            sinusoidal transition phase.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given width and height
	 *            then a new matrix is created.
	 * @return a matrix of size width * height containing the weight function.
	 */
	public float[][] defocussedDisk(int width, int height, int center_x,
			int center_y, double amplitude, double diameter, double size,
			float[][] p) {
		if ((p == null) || (p[0].length != width) || (p.length != height)) {
			p = new float[height][width];
		}
		double xx;
		double yy, yy2;
		double rxy2;
		double s = size / 2.0;
		double s2 = s * s;
		double d = diameter / 2.0;
		double d2 = d * d;
		double sd = s - d;
		double nx1 = (double) (width - 1);
		double ny1 = (double) (height - 1);
		if (amplitude == 0.0) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					p[y][x] = 0.0F;
				}
			}
		} else {
			for (int y = 0; y < height; y++) {
				yy = (double) (y - center_y) / ny1;
				yy2 = yy * yy;
				for (int x = 0; x < width; x++) {
					xx = (double) (x - center_x) / nx1;
					rxy2 = xx * xx + yy2;
					if (rxy2 < d2) {
						p[y][x] = (float) amplitude;
					} else if (rxy2 < s2) {
						p[y][x] = (float) (amplitude
								* (Math.cos(Math.PI * (Math.sqrt(rxy2) - d)
										/ (sd)) + 1.0) / 2.0);
					} else {
						p[y][x] = 0.0F;
					}
				}
			}
		}
		// fprintf(logstream, "frequency range: %f-%f\n", exp(_f_min)/PI2,
		// exp(f-f_step)/PI2);
		return (p);
	}

	/**
	 * Create a pattern which simulates the radial diffusion of a point light
	 * source behind a projection screen.
	 * 
	 * @param width
	 *            number horizontal pixel points.
	 * @param height
	 *            number vertical pixel points.
	 * @param center_x
	 *            horizontal position of the point light source behind the
	 *            screen given in pixels relative to the top left corner of the
	 *            pattern.
	 * @param center_y
	 *            vertical position of the point light source behind the screen
	 *            given in pixels relative to the top left corner of the
	 *            pattern.
	 * @param amplitude
	 *            amplitude factor. Should satisfy 0 <= amp <= 1.0.
	 * @param distance
	 *            the distance of the point light source to the screen relative
	 *            to the screen size.
	 * @param p
	 *            a matrix which stores the output values. If this is null or
	 *            the dimensions of p do not fit to the given width and height
	 *            then a new matrix is created.
	 * @return a matrix of size width * height containing the weight function.
	 */
	public float[][] distantPointLight(int width, int height, int center_x,
			int center_y, double amplitude, double distance, float[][] p) {
		if ((p == null) || (p[0].length != width) || (p.length != height)) {
			// System.out.println("LightDistribution.distantPointLight() creating p: w="
			// + width + ", h=" + height);
			p = new float[height][width];
		}
		// System.out.println("LightDistribution.distantPointLight() w=" + width
		// + ", h=" + height);
		// System.out.println("LightDistribution.distantPointLight() x=" +
		// center_x + ", y=" + center_y);
		// System.out.println("LightDistribution.distantPointLight() amplitude ="
		// + amplitude);
		double xx;
		double yy, yy2;
		double rxy2;
		double d2 = distance * distance;
		double d2a = d2 * amplitude;
		double nx1 = (double) (width - 1);
		double ny1 = (double) (height - 1);
		if (d2a == 0.0) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					p[y][x] = 0.0F;
				}
			}
		} else {
			for (int y = 0; y < height; y++) {
				yy = (double) (y - center_y) / ny1;
				yy2 = yy * yy;
				for (int x = 0; x < width; x++) {
					xx = (double) (x - center_x) / nx1;
					rxy2 = xx * xx + yy2;
					p[y][x] = (float) (d2a / (d2 + rxy2));
				}
			}
		}
		// fprintf(logstream, "frequency range: %f-%f\n", exp(_f_min)/PI2,
		// exp(f-f_step)/PI2);
		return (p);
	}
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
			double r = rnd.nextDouble();
			/*
			 * double r; do { r = rnd.nextGaussian()/4.0 + 0.5; } while ((r <
			 * 0.0) || (r > 1.0));
			 */
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
	 * Create the color mixture image according to the current mixtureType
	 * parameter value of this object.
	 * 
	 * @param pix
	 *            an integer array which can hold the image pixel data. The
	 *            array must have at least as many entries as there are points
	 *            in the pattern data matrix.
	 * @param pattern
	 *            is an array of matrices of image pixel color weights. The
	 *            entries must be values in the range [0.0, 1.0].
	 * @param color
	 *            the array of colors which are to be mixed.
	 * @return an image containing the given data.
	 */
	private void setImage(float[][][] pattern, PxlColor[] color) {
		if (mixtureType == ADDITIVE_MIXTURE) {
			setAdditiveMixtureImage(pattern, color);
		} else {
			setConvexMixtureImage(pattern, color);
		}
	}

	/**
	 * Create an image with the given color mixture pattern and use the given
	 * integer array to store the image data. Every color in the image is a
	 * mixture of the input colors containt in the input color array. The set of
	 * pattern matrices holds the respective mixture weigts. Thus for every
	 * point p(x,y) we have a color
	 * 
	 * <p>
	 * c = ScreenBackgroundColor + Sum_over_i ( pattern[i][y][x] * color[i] ).
	 * 
	 * @param pix
	 *            an integer array which can hold the image pixel data. The
	 *            array must have at least as many entries as there are points
	 *            in the pattern data matrix.
	 * @param pattern
	 *            is an array of matrices of image pixel color weights. The
	 *            entries must be values in the range [0.0, 1.0].
	 * @param color
	 *            the array of colors which are to be added.
	 * @return an image containing the given data.
	 */
	private void setAdditiveMixtureImage(float[][][] pattern, PxlColor[] color) {
		int n = pattern.length;
		if (n != color.length) {
			new RuntimeException(
					"Number of pattern slices and number of colors unequal!");
		}
		int w = pattern[0][0].length;
		int h = pattern[0].length;
		if ((image == null) || (image.getWidth() != w)
				|| (image.getHeight() != h)) {
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		}
		setSize(w, h);
		long w2 = w / 2;
		long h2 = h / 2;
		long r2 = w2 * w2 * h2 * h2;
		long xx, yy;
		PxlColor c;
		PxlColor bgColor = ExPar.ScreenBackgroundColor.getPxlColor();
		if (backgroundColorPar != null) {
			bgColor = backgroundColorPar.getPxlColor();
		}
		int background = bgColor.dev().getRGB();
		for (int y = 0; y < h; y++) {
			yy = w2 * (long) (y - h2);
			for (int x = 0; x < w; x++) {
				xx = h2 * (long) (x - w2);
				if (unmasked || ((xx * xx + yy * yy) < r2)) {
					c = bgColor.add(color[0].scaled(pattern[0][y][x]));
					for (int i = 1; i < n; i++) {
						c = c.add(color[i].scaled(pattern[i][y][x]));
					}
					image.setRGB(x, y, c.dev().getRGB());
				} else {
					image.setRGB(x, y, background);
				}
			}
		}
	}

	/**
	 * Create an image which is a convex mixture of the current background color
	 * and the given target colors. The weight pattern is stored in the given
	 * pattern matrix. Use the given integer array to store the image data.
	 * Every color in the image is a convex mixture of the input color and the
	 * background color. The pattern matrix holds the respective mixture weigts.
	 * Thus for every point p(x,y) we have a color
	 * 
	 * <p>
	 * c = pattern[y][x] * input_color + (1-pattern[x,y]) * background_color.
	 * 
	 * <p>
	 * In order to speed up computation a weight pattern is used to modify the
	 * background only if its value is non-zero. This also makes it possible to
	 * use up to four planes of weight patterns for different colors. The
	 * requirement is that at any pixel point only a single weight pattern must
	 * be non-zero.
	 * 
	 * @param pix
	 *            an integer array which can hold the image pixel data. The
	 *            array must have at least as many entries as there are points
	 *            in the pattern data matrix.
	 * @param pattern
	 *            is a matrix of image pixel color weights. The entries must be
	 *            values in the range [0.0, 1.0].
	 * @param color
	 *            the mixture color.
	 * @return an image containing the given data.
	 */
	private void setConvexMixtureImage(float[][][] pattern, PxlColor[] color) {
		// System.out.println("Convex Mixture");
		int n = pattern.length;
		if (n != color.length) {
			new RuntimeException(
					"Number of pattern slices and number of colors unequal!");
		}
		int w = pattern[0][0].length;
		int h = pattern[0].length;
		if ((image == null) || (image.getWidth() != w)
				|| (image.getHeight() != h)) {
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		}
		setSize(w, h);
		long w2 = w / 2;
		long h2 = h / 2;
		long r2 = w2 * w2 * h2 * h2;
		long xx, yy;
		PxlColor bgColor = ExPar.ScreenBackgroundColor.getPxlColor();
		if (backgroundColorPar != null) {
			bgColor = backgroundColorPar.getPxlColor();
		}
		int background = bgColor.dev().getRGB();
		float p;
		int pix;
		for (int y = 0; y < h; y++) {
			yy = w2 * (long) (y - h2);
			for (int x = 0; x < w; x++) {
				xx = h2 * (long) (x - w2);
				pix = background;
				if (unmasked || ((xx * xx + yy * yy) < r2)) {
					for (int i = 0; i < n; i++) {
						if (pattern[i][x][y] > 0.0000001F) {
							pix = (color[i].mix(pattern[i][x][y], bgColor))
									.dev().getRGB();
						}
					}
				}
				image.setRGB(x, y, pix);
			}
		}
	}
}
