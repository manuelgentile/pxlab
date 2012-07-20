package de.pxlab.util;

/**
 * A function which can be minimized by Praxis. This is a double valued function
 * with an array argument of double values.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface PraxisFunction {
	/**
	 * The function value.
	 * 
	 * @param x
	 *            a double array which usually contains the parameters of the
	 *            function which should be minimized as argument values.
	 * @return the value of the function at x.
	 */
	public double valueOf(double[] x);
}
