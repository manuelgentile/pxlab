package de.pxlab.pxl;

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
public class VisualGammaEstimator {
	private static final int GAMMAPAR = 0;
	private static final int GAINPAR = 1;
	private static final double SMALLFLOAT = 0.000001;
	/** Data points */
	private int ndata;
	private double[] a;
	private double[] b;
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
	 * @param aa
	 *            an array of device intensity values.
	 * @param bb
	 *            an array of device intensity values such that the visual
	 *            matching condition G(aa[i])/2 = G(bb[i]) holds for every i.
	 * @param g
	 *            an array of 2 double values for returning the parameter
	 *            estimates.
	 * @return the sum of the squared deviations between the data and the
	 *         estimated gamma function or -1.0 in case of an error.
	 */
	public double estimate(double aa[], double bb[], double g[]) {
		int i;
		double mn;
		ndata = aa.length;
		npar = g.length;
		a = new double[ndata];
		b = new double[ndata];
		gpar = new double[npar];
		/* move data into local data area */
		for (i = 0; i < ndata; i++) {
			a[i] = aa[i] / 255.0;
			b[i] = bb[i] / 255.0;
		}
		for (i = 0; i < npar; i++) {
			gpar[i] = g[i];
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
		for (i = 0; i < npar; i++) {
			// System.out.println("Parameter: " + gpar[i]);
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
				// first compute G(a) and 2*G(b)
				ra = Math.pow(a[i], p);
				rb = 2.0 * Math.pow(b[i], p);
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
				// first compute G(a) and 2*G(b)
				ra = p[GAINPAR] * (a[i] - 1.0) + 1.0;
				if (ra <= SMALLFLOAT) {
					s += ra * ra;
					ra = 0.0;
					continue;
				} else {
					ra = Math.pow(ra, p[GAMMAPAR]);
				}
				rb = p[GAINPAR] * (b[i] - 1.0) + 1.0;
				if (rb <= SMALLFLOAT) {
					s += rb * rb;
					rb = 0.0;
					continue;
				} else {
					rb = 2.0 * Math.pow(rb, p[GAMMAPAR]);
				}
				// minimize the squared deviation
				s += (ra - rb) * (ra - rb);
			}
			// Add deviation Gamma(1.0)-1.0 in order to fix Gamma(1.0)=1.0
			/*
			 * ra = p[GAINPAR] + p[OFFSETPAR]; ra = (ra <= 0.0)? 0.0:
			 * Math.pow(ra, p[GAMMAPAR]); s += (ra-1.0) * (ra-1.0);
			 */
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
