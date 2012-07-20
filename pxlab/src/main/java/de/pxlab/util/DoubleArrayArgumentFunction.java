package de.pxlab.util;

/**
 * A double valued function with a double array argument.
 * 
 * @version 0.1.0
 */
public interface DoubleArrayArgumentFunction {
	/**
	 * The function value.
	 * 
	 * @param x
	 *            the argument.
	 * @return the value of the function at x.
	 */
	public double valueOf(double[] x);
}
