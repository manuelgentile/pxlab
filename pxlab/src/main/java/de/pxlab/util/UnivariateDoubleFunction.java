package de.pxlab.util;

/**
 * A double valued function with a single double argument.
 * 
 * @version 0.1.0
 */
public interface UnivariateDoubleFunction {
	/**
	 * The function value.
	 * 
	 * @param x
	 *            the argument.
	 * @return the value of the function at x.
	 */
	public double valueOf(double x);

	/**
	 * The function value with 0 as argument. This should be calculated much
	 * faster than for any other argument.
	 * 
	 * @return the value of the function at 0.
	 */
	public double valueOfZero();
}
