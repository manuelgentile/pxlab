package de.pxlab.stat;

import de.pxlab.pxl.*;

public class IsotonicPMF implements PsychometricFunction {
	private double[] x;
	private int[] w;
	private double[] g;
	private double[] f;
	private int n;
	private double chiSquare;

	public IsotonicPMF(double[] xxx, int[] www, int[] yyy) {
		n = xxx.length;
		x = new double[n];
		System.arraycopy(xxx, 0, x, 0, n);
		w = new int[n];
		System.arraycopy(www, 0, w, 0, n);
		for (int i = 1; i < n; i++) {
			if (x[i] <= x[i - 1])
				throw new RuntimeException(
						"Illegal Argument of IsotonicPMF: x-values must be strictly ordered.");
		}
		g = new double[n];
		for (int i = 0; i < n; i++) {
			g[i] = (double) yyy[i] / (double) w[i];
		}
		computeIsotonicRegression();
	}

	/**
	 * Compute the isotonic regression psychometric function for the given set
	 * of binary response data. This constructor assumes that every entry of the
	 * input array has the same weight.
	 * 
	 * @param xx
	 *            the array of points for which the function is defined. The
	 *            array need not be sorted.
	 * @param rr
	 *            an array of function values. Must contain a value for each
	 *            entry in xx. The numbers may be 0s and 1s corresponding to
	 *            empirical observations (1 = yes, 0 = no).
	 */
	public IsotonicPMF(double[] xx, double[] rr) {
		int m = xx.length;
		double[] xxxx = new double[m];
		System.arraycopy(xx, 0, xxxx, 0, m);
		double[] rrrr = new double[m];
		System.arraycopy(rr, 0, rrrr, 0, m);
		int[] wwww = new int[m];
		n = sort(xxxx, rrrr, wwww);
		x = new double[n];
		System.arraycopy(xxxx, 0, x, 0, n);
		g = new double[n];
		w = new int[n];
		for (int i = 0; i < n; i++) {
			w[i] = wwww[i]; /* weight */
			g[i] = rrrr[i] / (double) w[i]; /* function value */
		}
		computeIsotonicRegression();
	}

	private void computeIsotonicRegression() {
		/*
		 * System.out.println("\nIsotonicPMF Input Data: x, w, g:"); for (int i
		 * = 0; i < n; i++) { System.out.println(x[i] + " " + w[i] + " " +
		 * g[i]); }
		 */
		int[] s = new int[n];
		for (int i = 0; i < n; i++) {
			s[i] = 1;
		}
		int current = 0;
		int next = current + s[current];
		while (next < n) {
			if (g[current] > g[next]) {
				int wnew = w[current] + w[next];
				g[current] = ((double) w[current] * g[current] + (double) w[next]
						* g[next])
						/ wnew;
				w[current] = wnew;
				s[current] += s[next];
				current = 0; /* not optimal, but efficient */
			} else {
				current += s[current];
			}
			next = current + s[current];
		}
		f = new double[n];
		current = 0;
		int k = 0;
		while (current < n) {
			for (int j = 0; j < s[current]; j++) {
				f[k++] = g[current];
			}
			current += s[current];
		}
		/*
		 * System.out.println("\nIsotonicPMF Estimation Result: x, w, s, g, f:");
		 * for (int i = 0; i < n; i++) { System.out.println(x[i] + " " + w[i] +
		 * " " + s[i] + " " + g[i] + " " + f[i]); }
		 */
		chiSquare = 0.0;
		/*
		 * double d; for (int i = 0; i < n; i++) { d = r[i] - f[i]; chiSquare +=
		 * (d * d); }
		 * 
		 * for (int i = 0; i < n; i++) { System.out.println(i + ": x=" + x[i] +
		 * ", r=" + r[i] + ", f=" + f[i]); }
		 */
	}

	public String toString() {
		StringBuffer s = new StringBuffer(1000);
		for (int i = 0; i < n; i++) {
			s.append(x[i] + "\t" + g[i] + "\t" + f[i] + "\n");
		}
		return s.toString();
	}

	/**
	 * Sort the input data and pool points of equal stimulus values.
	 * 
	 * @param x
	 *            array of stimulus values. On return this array contains the
	 *            sorted stimulus values.
	 * @param r
	 *            array of response values (0 or 1). On return this array
	 *            contains the relative frequencies for the respective stimulus
	 *            value.
	 * @param m
	 *            on return this contains the weight for the respective stimulus
	 *            value.
	 * @return the number of valid entries in the output arrays. This
	 *         corresponds to the number of different stimulus levels in the
	 *         input data.
	 */
	private int sort(double[] x, double[] r, int[] m) {
		double t;
		int pos;
		// First sort the input arrays according to stimulus size
		for (int j = 0; j < x.length; j++) {
			pos = j;
			for (int i = j + 1; i < x.length; i++) {
				if (x[i] < x[pos]) {
					pos = i;
				}
			}
			t = x[j];
			x[j] = x[pos];
			x[pos] = t;
			t = r[j];
			r[j] = r[pos];
			r[pos] = t;
		}
		// Now pool points of equal stimulus values and create the
		// corresponding weights
		int k = 0;
		for (int j = 0; j < x.length; j++) {
			x[k] = x[j];
			r[k] = r[j];
			m[k] = 1;
			if (j < x.length - 1) {
				for (int i = j + 1; (i < x.length) && (x[i] == x[j]); i++) {
					r[k] += r[i];
					m[k]++;
				}
				if (m[k] > 1) {
					r[k] /= m[k];
					j += (m[k] - 1);
				}
			}
			k++;
		}
		return k;
	}

	public double getChiSquare() {
		return chiSquare;
	}

	/**
	 * Returns the function value for the given parameters and the given
	 * stimulus.
	 * 
	 * @param y
	 *            the stimulus value.
	 * @param pse
	 *            the point of subjective equality or threshold.
	 * @param jnd
	 *            the just noticable difference or slope of the psychometric
	 *            function.
	 * @param lo
	 *            the left/lower asymptotic value of the function.
	 * @param hi
	 *            the right/higher asymptote value.
	 * @return the function value.
	 */
	public double valueOf(double y, double pse, double jnd, double lo, double hi) {
		if (f != null) {
			for (int i = 0; i < n; i++) {
				if (y <= x[i]) {
					return (f[i]);
				}
			}
			return f[n - 1];
		}
		return 0.0;
	}

	/**
	 * Return the nearest function argument for the given function value. This
	 * method does a linear interpolation between the nearest grid points.
	 * 
	 * @param p
	 *            function value whose inverse is searched.
	 * @return that argument which has p as its function value.
	 */
	public double argumentFor(double p) {
		double x1 = 0.0, x2 = 0.0;
		double f1 = 0.0, f2 = 0.0;
		if (f != null) {
			for (int i = 0; i < n; i++) {
				if (f[i] >= p) {
					x1 = x[i];
					f1 = f[i];
					break;
				}
			}
			for (int i = n - 1; i >= 0; i--) {
				if (f[i] <= p) {
					x2 = x[i];
					f2 = f[i];
					break;
				}
			}
			// System.out.println("p=" + p + ":  x1=" + x1 + ", f1=" + f1 +
			// ",  x2=" + x2 + ", f2=" + f2);
			if (f1 == f2) {
				return (x1 + x2) / 2;
			}
			if (x2 < x1) {
				double t;
				t = x2;
				x2 = x1;
				x1 = t;
				t = f2;
				f2 = f1;
				f1 = t;
			}
			double r = x1 + (p - f1) / (f2 - f1) * (x2 - x1);
			// System.out.println("  r=" + r);
			return r;
		}
		return 0.0;
	}
}
