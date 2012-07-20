package de.pxlab.pxl;

/**
 * A psychometric function which gives the probability of a YES-response to the
 * given stimulus in a psychometric experiment.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public interface PsychometricFunction {
	/**
	 * Returns the function value for the given parameters and the given
	 * stimulus.
	 * 
	 * @param x
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
	public double valueOf(double x, double pse, double jnd, double lo, double hi);
}
