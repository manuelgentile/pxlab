package de.pxlab.util;

import de.pxlab.pxl.NonFatalError;

/**
 * A double valued function with a double array argument.
 * 
 * @version 0.1.0
 */
public class LinearWeightFunction implements DoubleArrayArgumentFunction {
	private double[] w;

	public LinearWeightFunction(double[] w) {
		this.w = w;
	}

	/**
	 * The function value.
	 * 
	 * @param x
	 *            the argument.
	 * @return the value of the function at x.
	 */
	public double valueOf(double[] x) {
		double r = 0.0;
		int n = x.length;
		if (x.length == w.length) {
			for (int i = 0; i < n; i++)
				r += w[i] * x[i];
		} else {
			new NonFatalError(
					"LinearWeightFunction.valueOf() Length of argument and length og weight array not equal.");
		}
		return r;
	}
}
