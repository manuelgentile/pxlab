package de.pxlab.pxl.calib;

import de.pxlab.util.Praxis;
import de.pxlab.util.PraxisFunction;
import de.pxlab.util.PraxisFunction1;

/**
 * Estimate parameters of a monitor gamma function from pairs of values with
 * G(a[i])/2=G(b[i]) where the '=' is a visual intensity match.
 * 
 * <p>
 * The gamma function used here is from: Berns, R. S., Motta, R. J., &
 * Gorzynski, M. E. (1993). CRT Colorimetry. Part I: Theory and Practice. Colour
 * Research and Application, 18, 299--314. It looks like this: G(x) = (gain * x
 * + offset) ** gamma, if ((gain * x + offset) > 0.0) and G(x) = 0.0 otherwise.
 * 
 * <p>
 * The estimation process uses a principal axis estimation method which does not
 * calculate derivatives (PRAXIS).
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class GammaEstimator {
	private static final int GAMMAPAR = 0;
	private static final int GAINPAR = 1;
	private static final double SMALLFLOAT = 0.000001;
	/** Number of data points */
	private int ndata;
	/** DAC output values. */
	private double[] a;
	/** Relative luminance values. */
	private double[] b;
	private double a_scale, b_scale;
	/** Parameters of the gamma function */
	private int npar;
	private double[] gpar;

	/**
	 * Estimate parameters. The input data are arrays aa[] and bb[] of device
	 * intensity values in the range of 0, .., 255 which represent visual
	 * matches such that the luminance produced by bb[i] visually matches half
	 * of the luminance produced by aa[i]. The 'half luminance' condition can be
	 * created by writing only every second video line of a patch with intensity
	 * aa[i].
	 * 
	 * @param data
	 *            an array of data arrays. Each single data array has to contain
	 *            the DAC output value at index 0 and the measured luminance
	 *            value at index 1. The first data array MUST contain the
	 *            maximum DAC output value and the maximum luminance of the
	 *            respective channel.
	 * @param g
	 *            an array of up to 3 double values which contain the initial
	 *            parameter estimates and is used to return the final estimates.
	 * @return the sum of the squared deviations between the data and the
	 *         estimated gamma function or -1.0 in case of an error.
	 */
	public double estimate(double[][] data, double[] g) {
		double mn;
		// System.out.println("data.length = " + data.length);
		// System.out.println("data[0].length = " + data[0].length);
		ndata = data.length;
		npar = g.length;
		a = new double[ndata];
		b = new double[ndata];
		gpar = new double[npar];
		/* move data into local data area */
		System.out.println("GammaEstimator.estimate() Data:");
		a_scale = data[0][0];
		b_scale = data[0][1];
		for (int i = 0; i < ndata; i++) {
			a[i] = data[i][0] / a_scale;
			b[i] = data[i][1];
			System.out.println("  " + String.valueOf(a[i]) + " "
					+ String.valueOf(b[i]));
		}
		System.out
				.println("GammaEstimator.estimate(): Initial parameter values:");
		for (int i = 0; i < npar; i++) {
			gpar[i] = g[i];
			System.out.println("  " + String.valueOf(i) + " "
					+ String.valueOf(gpar[i]));
		}
		Praxis praxis = new Praxis();
		// Initialize parameters of the estimation process */
		/* step = 0.1; /* 1.0 +/- */
		/* scbd, /* 1.0, ..., 10.0 */
		/* tol; /* depends on machine */
		/* ktm = 1; /* 1, ..., 4 */
		/* prin = 0; /* 0, ..., 3 */
		/* illc = 0; /* 0/1 */
		/* maxfun = 500; /* 0 = no limit, ... */
		// praxis.setPrintControl(2);
		if (npar == 1) {
			mn = praxis.minimize(new GammaFunction1(), gpar, 0.8, 3.0);
		} else {
			mn = praxis.minimize(new GammaFunction(), gpar);
		}
		System.out.println("GammaEstimator.estimate(): Parameter estimates:");
		for (int i = 0; i < npar; i++) {
			System.out.println("  " + gpar[i]);
			g[i] = gpar[i];
		}
		return mn;
	}
	/**
	 * This uses a simple 1-parameter gamma function. We know that G(a)/2=G(b),
	 * so we compute G(a)-2*G(b). This difference is squared and summed up for
	 * all data points.
	 */
	private class GammaFunction1 implements PraxisFunction1 {
		public double valueOf(double p) {
			double s, ra, rb;
			s = 0.0;
			for (int i = 0; i < ndata; i++) {
				ra = b_scale * Math.pow(a[i], p);
				rb = b[i];
				// minimize the squared deviation
				s += (ra - rb) * (ra - rb);
			}
			return s;
		}
	}
	/**
	 * This uses the Berns, Motta, & Gorzynski () form of the gamma function. We
	 * know that G(a)/2=G(b), so we compute G(a)-2*G(b). This difference is
	 * squared and summed up for all data points. In order to prevent the
	 * function to leave the interval [0.0, 1.0] we assume that none of the data
	 * points has G(x)=0.0 and punish parameter values which result in 0.0
	 * values for G().
	 */
	private class GammaFunction implements PraxisFunction {
		public double valueOf(double p[]) {
			double s, ra, rb;
			int i;
			s = 0.0;
			for (i = 0; i < ndata; i++) {
				ra = p[GAINPAR] * (a[i] - 1.0) + 1.0;
				if (ra <= SMALLFLOAT) {
					ra = 0.0;
					continue;
				} else {
					ra = Math.pow(ra, p[GAMMAPAR]);
				}
				ra *= b_scale;
				rb = b[i];
				// System.out.println("ra = " + ra + "  rb = " + rb);
				// minimize the squared deviation
				s += (ra - rb) * (ra - rb);
			}
			return s;
		}
	}

	/**
	 * This uses the Berns, Motta, & Gorzynski () form of the gamma function.
	 * Calling this function requires that gpar[] has been set previously.
	 */
	private double gammaFun(double x) {
		double r;
		r = gpar[GAINPAR] * (x - 1.0) + 1.0;
		return ((r <= 0.0) ? 0.0 : Math.pow(r, gpar[GAMMAPAR]));
	}
}
