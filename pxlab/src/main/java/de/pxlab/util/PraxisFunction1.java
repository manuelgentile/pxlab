package de.pxlab.util;

/**
 * A single argument function which can be minimized by Praxis.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface PraxisFunction1 {
	/**
	 * The function value.
	 * 
	 * @param x
	 *            the argument of the function which should be minimized.
	 * @return the value of the function at x.
	 */
	public double valueOf(double x);
}
