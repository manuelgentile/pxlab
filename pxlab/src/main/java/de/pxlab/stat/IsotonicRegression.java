package de.pxlab.stat;

/**
 * Computes the isotonic regression of a monotone and discrete function. The
 * algorithm used is the pool-adjacent-violators algorithm. The isotonic
 * regression is described in the book:
 * 
 * <p>
 * Barlow, R. E., Bartholomew, D. J., Bremner, J. M., and Brunk, H. D. (1972).
 * Statistical inference under order restrictions. London: Wiley.
 * 
 * <p>
 * Known bugs: The class requires that all data points have the same weight
 * (number of observations).
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 09/17/01
 * 
 * 09/12/03 moved to newly created package de.pxlab.stat
 */
public class IsotonicRegression {
	private int n;
	private double[] f = null;
	private double[] x = null;
	private double chiSquare;

	/**
	 * Compute the isotonic regression for the given range and function values.
	 * This method works correctly only if all data points have the same weight.
	 * 
	 * @param xx
	 *            the array of points for which the function is defined. The
	 *            array need not be sorted.
	 * @param rr
	 *            an array of 0s and 1s corresponding to empirical observations
	 *            (1 = yes, 0 = no).
	 */
	public IsotonicRegression(double[] xx, double[] rr) {
		int m = xx.length;
		double[] xxx = new double[m];
		System.arraycopy(xx, 0, xxx, 0, m);
		double[] rrr = new double[m];
		System.arraycopy(rr, 0, rrr, 0, m);
		int[] www = new int[m];
		n = sort(xxx, rrr, www);
		x = new double[n];
		System.arraycopy(xxx, 0, x, 0, n);
		double[] r = new double[n];
		System.arraycopy(rrr, 0, r, 0, n);
		double[] g = new double[n];
		int[] s = new int[n];
		int[] w = new int[n];
		for (int i = 0; i < n; i++) {
			g[i] = r[i]; /* function value */
			s[i] = 1; /* pool size */
			w[i] = www[i]; /* weight */
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
		chiSquare = 0.0;
		double d;
		for (int i = 0; i < n; i++) {
			d = r[i] - f[i];
			chiSquare += (d * d);
		}
		for (int i = 0; i < n; i++) {
			System.out.println(i + ": x=" + x[i] + ", r=" + r[i] + ", f="
					+ f[i]);
		}
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

	/*
	 * public double[] getFunction() { return f; }
	 * 
	 * 
	 * public double[] getArguments() { return x; }
	 * 
	 * 
	 * public int size() { return n; }
	 */
	/**
	 * Return the function value for the given argument.
	 * 
	 * @param y
	 *            argument value.
	 * @return function value for the given argument.
	 */
	public double valueOf(double y) {
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
	 * method does a linear interpolatio between the neares grid points.
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
			System.out.println("p=" + p + ":  x1=" + x1 + ", f1=" + f1
					+ ",  x2=" + x2 + ", f2=" + f2);
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
			System.out.println("  r=" + r);
			return r;
		}
		return 0.0;
	}
}
