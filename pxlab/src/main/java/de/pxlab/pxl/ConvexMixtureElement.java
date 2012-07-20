package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;

/**
 * A bitmap which contains an ModelFest stimulus pattern. This implementation
 * strictly separates 4 aspects of each stimulus: the base pattern, the spatial
 * envelope, the temporal envelope, and the colors. At each point in space and
 * time the pattern value is considered to be a mixture weight for the convex
 * mixture of two colors. It thus is easy to extend the patterns from its
 * original monochrome versions to arbitrary color 'lines' in CIE
 * xy-chromaticity space or any of its linear transformations.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.display.ModelFest
 */
public class ConvexMixtureElement extends BitMapElement {
	protected float[][] pattern = null;
	protected float[][] gaussian = null;
	protected GratingFactory gratingFactory = null;
	protected ExPar altColorPar = null;
	protected ExPar fixationColorPar = null;
	protected int fixationMarkType;
	protected int fixationMarkSize;
	protected double horGaussianSD;
	protected double verGaussianSD;
	protected int temporalGaussianSD;
	protected int duration;
	protected int pixelPerDegree;

	public float[][] getPattern() {
		return pattern;
	}

	public float[][] getGaussian() {
		return gaussian;
	}

	public ConvexMixtureElement(ExPar lowCol, ExPar highCol, ExPar fixCol) {
		gratingFactory = new GratingFactory();
		setColorPars(lowCol, highCol, fixCol);
	}

	public void setColorPars(ExPar lowCol, ExPar highCol, ExPar fixCol) {
		colorPar = lowCol;
		altColorPar = highCol;
		fixationColorPar = fixCol;
	}

	public void setPixelPerDegree(int ppd) {
		pixelPerDegree = ppd;
	}

	public void setGaussianStandardDeviation(double horSD, double verSD) {
		horGaussianSD = horSD;
		verGaussianSD = verSD;
	}

	public void setAnimationProperties(int d, int tempSD, int fpc) {
		duration = d;
		temporalGaussianSD = tempSD;
		setMovieLength(fpc);
	}

	public void setFixation(int t, int s) {
		fixationMarkType = t;
		fixationMarkSize = s;
	}

	public void computeColors() {
		// System.out.println("ConvexMixtureElement.computeColors():  LowColor = "
		// + colorPar);
		// System.out.println("ConvexMixtureElement.computeColors(): HighColor = "
		// + altColorPar);
		setImage();
	}

	public void setPattern(float[][] p) {
		// System.out.println("ConvexMixtureElement.setPattern()");
		pattern = p;
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setGabor(double frequency, double phase, double amplitude,
			double orientation) {
		// System.out.println("ConvexMixtureElement.setGabor()");
		pattern = gratingFactory.sinusoid(size.width, frequency
				* (double) (size.width) / (double) pixelPerDegree, phase,
				amplitude, orientation, pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setGabor(double frequency, int[] harmonics, double[] phase,
			double[] amplitude, double orientation) {
		// System.out.println("ConvexMixtureElement.setGabor() [complex]");
		pattern = getComplexSinusoid(frequency, harmonics, phase, amplitude,
				1.0, orientation, 0.0, pattern);
		gaussian = gratingFactory.gaussian(size.width, size.height,
				horGaussianSD, verGaussianSD, gaussian);
	}

	public void setGabor(double frequency, int[] harmonics, double[] phase,
			double[] amplitude, double orientation, double shift) {
		// System.out.println("ConvexMixtureElement.setGabor() [complex]");
		pattern = getComplexSinusoid(frequency, harmonics, phase, amplitude,
				1.0, orientation, shift, pattern);
		gaussian = gratingFactory.gaussian(size.width, size.height,
				horGaussianSD, verGaussianSD, gaussian);
	}

	public void setPlaid(double frequency1, int[] harmonics1, double[] phase1,
			double[] amplitude1, double orientation1, double frequency2,
			int[] harmonics2, double[] phase2, double[] amplitude2,
			double orientation2) {
		float[][] p1 = getComplexSinusoid(frequency1, harmonics1, phase1,
				amplitude1, 0.5, orientation1, 0.0, null);
		float[][] p2 = getComplexSinusoid(frequency2, harmonics2, phase2,
				amplitude2, 0.5, orientation2, 0.0, null);
		pattern = gratingFactory.sum(p1, p2);
		gaussian = gratingFactory.gaussian(size.width, size.height,
				horGaussianSD, verGaussianSD, gaussian);
	}

	public void setPlaid(double frequency1, int[] harmonics1, double[] phase1,
			double[] amplitude1, double orientation1, double shift1,
			double frequency2, int[] harmonics2, double[] phase2,
			double[] amplitude2, double orientation2, double shift2) {
		float[][] p1 = getComplexSinusoid(frequency1, harmonics1, phase1,
				amplitude1, 0.5, orientation1, shift1, null);
		float[][] p2 = getComplexSinusoid(frequency2, harmonics2, phase2,
				amplitude2, 0.5, orientation2, shift2, null);
		pattern = gratingFactory.sum(p1, p2);
		gaussian = gratingFactory.gaussian(size.width, size.height,
				horGaussianSD, verGaussianSD, gaussian);
	}

	private float[][] getComplexSinusoid(double frequency, int[] harmonics,
			double[] phase, double[] amplitude, double maxAmp,
			double orientation, double shift, float[][] p) {
		if (harmonics.length == 1) {
			p = gratingFactory.complexSinusoid(size.width, size.height,
					frequency, harmonics, phase, amplitude, maxAmp,
					orientation, shift, p);
		} else {
			double[] phase2 = new double[harmonics.length];
			double[] amplitude2 = new double[harmonics.length];
			if (phase.length < harmonics.length)
				for (int i = 0; i < harmonics.length; i++)
					phase2[i] = phase[0];
			else
				for (int i = 0; i < harmonics.length; i++)
					phase2[i] = phase[i];
			if (amplitude.length < harmonics.length)
				for (int i = 0; i < harmonics.length; i++)
					amplitude2[i] = amplitude[0];
			else
				for (int i = 0; i < harmonics.length; i++)
					amplitude2[i] = amplitude[i];
			p = gratingFactory.complexSinusoid(size.width, size.height,
					frequency, harmonics, phase2, amplitude2, maxAmp,
					orientation, shift, p);
		}
		return p;
	}

	public void setConcentricGabor(double frequency, double phase,
			double amplitude) {
		// System.out.println("ConvexMixtureElement.setConcentricGabor()");
		pattern = gratingFactory.concentricSinusoid(size.width, frequency
				* (double) (size.width) / (double) pixelPerDegree, phase,
				amplitude, pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setBessel(double frequency, double amplitude) {
		// System.out.println("ConvexMixtureElement.setBessel()");
		pattern = gratingFactory.bessel(size.width, frequency
				* (double) (size.width) / (double) pixelPerDegree, amplitude,
				pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setPlaid(double frequency1, double phase1, double amplitude1,
			double orientation1, double frequency2, double phase2,
			double amplitude2, double orientation2) {
		// System.out.println("ConvexMixtureElement.setGabor()");
		float[][] p1 = gratingFactory.sinusoid(size.width, frequency1
				* (double) (size.width) / (double) pixelPerDegree, phase1,
				amplitude1, orientation1, null);
		float[][] p2 = gratingFactory.sinusoid(size.width, frequency2
				* (double) (size.width) / (double) pixelPerDegree, phase2,
				amplitude2, orientation2, null);
		pattern = gratingFactory.sum(p1, p2);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setCollinearGabors(int n, double frequency, double phase1,
			double phase2, double amplitude, double orientation) {
		// System.out.println("ConvexMixtureElement.setCollinearGabors()");
		int w = size.width / (n + 1);
		pattern = gratingFactory.zero(size.width, pattern);
		gaussian = gratingFactory.zero(size.width, gaussian);
		float[][] gs = gratingFactory.gaussian(w, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, null);
		float[][] a1 = gratingFactory
				.sinusoid(w,
						frequency * (double) (w) / (double) pixelPerDegree,
						phase1, amplitude, orientation, null);
		float[][] a2 = gratingFactory
				.sinusoid(w,
						frequency * (double) (w) / (double) pixelPerDegree,
						phase2, amplitude, orientation, null);
		int x = size.width / 2 - n * w / 2;
		int y = size.width / 2 - w / 2;
		for (int i = 0; i < n; i++) {
			// System.out.println("ConvexMixtureElement.setCollinearGabors(): Insert at "
			// + x + ", " + y);
			gratingFactory.insert((i % 2 == 0) ? a1 : a2, x, y, pattern);
			gratingFactory.insert(gs, x, y, gaussian);
			x += w;
		}
	}

	public void setEdge(double amplitude) {
		// System.out.println("ConvexMixtureElement.setEdge()");
		pattern = gratingFactory.horizontalEdge(size.width, amplitude, pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setLine(double width, double amplitude) {
		// System.out.println("ConvexMixtureElement.setEdge()");
		pattern = gratingFactory.horizontalLine(size.width,
				(int) Math.round(width * (double) pixelPerDegree), amplitude,
				pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setDipole(double width, double amplitude) {
		// System.out.println("ConvexMixtureElement.setDipole()");
		pattern = gratingFactory.horizontalDipole(size.width,
				(int) Math.round(width * (double) pixelPerDegree), amplitude,
				pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setDisk(double width, double amplitude) {
		// System.out.println("ConvexMixtureElement.setDisk()");
		pattern = gratingFactory.disk(size.width,
				(int) Math.round(width * (double) pixelPerDegree), amplitude,
				pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setCheckerboard(double width, double amplitude) {
		// System.out.println("ConvexMixtureElement.setCheckerboard()");
		pattern = gratingFactory.checkerboard(size.width,
				(int) Math.round(width * (double) pixelPerDegree), amplitude,
				pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setWhiteNoise(double width, double amplitude) {
		// System.out.println("ConvexMixtureElement.setWhiteNoise()");
		pattern = gratingFactory.whiteNoise(size.width,
				(int) Math.round(width * (double) pixelPerDegree), amplitude,
				pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setImagePattern(double amplitude, BufferedImage ip, double lm) {
		// System.out.println("ConvexMixtureElement.setImagePattern()");
		pattern = gratingFactory.imagePattern(amplitude, ip, lm, pattern);
		gaussian = gratingFactory.gaussian(ip.getWidth(), horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setGaussian(double amplitude) {
		// System.out.println("ConvexMixtureElement.setGaussian()");
		pattern = gratingFactory.constant(size.width, amplitude, pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	/*
	 * public void setShiftedGaussianPattern(int x, int y) { pattern =
	 * gratingFactory.constant(size.width, size.height, 1.0, pattern); gaussian
	 * = gratingFactory.gaussian(x, y, size.width, size.height, horGaussianSD *
	 * (double)pixelPerDegree, verGaussianSD * (double)pixelPerDegree,
	 * gaussian); }
	 */
	public void setGaussianPattern(int x, int y) {
		pattern = gratingFactory.gaussian(x, y, size.width, size.height,
				horGaussianSD * (double) pixelPerDegree, verGaussianSD
						* (double) pixelPerDegree, pattern);
		gaussian = null;
	}

	public void setZero() {
		// System.out.println("ConvexMixtureElement.setZero()");
		pattern = gratingFactory.zero(size.width, pattern);
		gaussian = gratingFactory.gaussian(size.width, horGaussianSD
				* (double) pixelPerDegree, verGaussianSD
				* (double) pixelPerDegree, gaussian);
	}

	public void setRandomMondrian(int columns, int rows, int gap) {
		pattern = gratingFactory.randomMondrian(size.width, size.height,
				columns, rows, gap, pattern);
		gaussian = null;
	}

	/**
	 * Create an image with the given color mixture pattern. Every color in the
	 * image is a mixture of the two input colors: c = k*a + (1-k)*b with 0 <= k
	 * <= 1 and k being any point in the mixture pattern.
	 * 
	 * @param pattern
	 *            is a matrix of image pixel values. The entries must be values
	 *            in the range [0.0, 1.0].
	 * @param lowColor
	 *            the color which corresponds to pattern values of 0.0.
	 * @param highColor
	 *            the color which corresponds to pattern values of 1.0.
	 */
	private void setImage() {
		// System.out.println("ConvexMixtureElement.setImage()");
		int nSteps = 300;
		int[] ramp = linearRamp(colorPar.getPxlColor(),
				altColorPar.getPxlColor(), nSteps + 1);
		int w = pattern[0].length;
		int h = pattern.length;
		float[][] weight;
		if (movie == null) {
			if (gaussian == null) {
				weight = pattern;
				image = createImage(weight, 0.0F, (float) (ramp.length - 1),
						ramp, image);
			} else {
				weight = gratingFactory.product(gaussian, pattern);
				image = createImage(weight, ramp, image);
			}
			// System.out.println("ConvexMixtureElement.setImage(): single image");
		} else {
			int n = movie.length;
			int ts = duration / n;
			int n2 = (n + 1) / 2;
			int d2 = duration / 2;
			double v = 2.0 * temporalGaussianSD * temporalGaussianSD;
			int t = 0;
			int i;
			for (i = 0; i < n2; i++) {
				int x = t - d2;
				double s = Math.exp(-(((double) (x * x)) / v));
				float[][] g = gratingFactory.product(s, gaussian);
				weight = gratingFactory.product(g, pattern);
				BufferedImage im = createImage(weight, ramp, null);
				setMovieFrame(im, i);
				t += ts;
			}
			int k = ((n % 2) == 1) ? i - 2 : i - 1;
			for (; i < n; i++) {
				setMovieFrame(getMovieFrame(k--), i);
			}
			// System.out.println("ConvexMixtureElement.setImage(): movie");
		}
	}

	private BufferedImage createImage(float[][] weight, int[] ramp,
			BufferedImage img) {
		return createImage(weight, 1.0F, (float) ((ramp.length - 1) / 2), ramp,
				img);
	}

	private BufferedImage createImage(float[][] weight, float m, float a,
			int[] ramp, BufferedImage img) {
		int w = weight[0].length;
		int h = weight.length;
		if ((img == null) || (img.getWidth() != w) || (img.getHeight() != h)) {
			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		}
		int index = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				index = (int) (a * (weight[y][x] + m));
				/*
				 * if (index < 0 || index > nSteps) {
				 * System.out.println("ConvexMixtureElement.setImage() weight["
				 * + y + "][" + x + "] = " + weight[y][x]); }
				 */
				img.setRGB(x, y, ramp[index]);
			}
		}
		addFixation(img);
		return img;
	}

	private void addFixation(BufferedImage img) {
		if (fixationMarkType != FixationCodes.NO_FIXATION) {
			Graphics g = img.getGraphics();
			drawFixationMark(g, img.getWidth() / 2, img.getHeight() / 2,
					img.getWidth(), img.getHeight(), fixationMarkType,
					fixationMarkSize, fixationMarkSize, fixationMarkSize / 5,
					fixationColorPar.getDevColor());
			g.dispose();
		}
	}

	/**
	 * Create a linear ramp of mixture colors. Every color in the ramp is a
	 * mixture of the two input colors: c = k*a + (1-k)*b with 0 <= k <= 1.
	 * Index 0 of the output array corresponds to k = 1.0 and index (n-1)
	 * korresponds to k = 0.0.
	 * 
	 * @param a
	 *            first color of the mixture array.
	 * @param b
	 *            last color of the mixture array.
	 * @param n
	 *            number of elements in the array.
	 * @return an integer array of device colors corresponding to the mixtures.
	 */
	private int[] linearRamp(PxlColor a, PxlColor b, int n) {
		PxlColor[] pc = a.linearRampTo(b, n);
		int[] m = new int[n];
		for (int i = 0; i < n; i++)
			m[i] = pc[i].dev().getRGB();
		return m;
	}

	/**
	 * Destroy all resources associated with this light distribution.
	 */
	public void destroy() {
		dispose(pattern);
		pattern = null;
		dispose(gaussian);
		gaussian = null;
		flush();
		System.gc();
	}

	private void dispose(float[][] p) {
		if (p != null) {
			int n = p.length;
			if (p[0] != null) {
				int h = p[0].length;
				for (int i = 0; i < n; i++) {
					p[i] = null;
				}
			}
		}
	}
}
