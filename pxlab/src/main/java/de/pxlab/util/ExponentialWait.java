package de.pxlab.util;

/** Approximately exponentially distributed waiting times. */
public class ExponentialWait {
	private static double maxWait = 10000.0;
	private static final double minR = 0.000001;

	/**
	 * Find an approximately exponentially distributed waiting time with a given
	 * expected value. Be sure not to overflow the log function and take care
	 * that all waiting times are shorter than maxWait.
	 * 
	 * @param expectedValue
	 *            this is the expected value of the exponential distribution we
	 *            generate.
	 */
	public static double instance(double expectedValue) {
		double r, w;
		do {
			do {
				r = Math.random();
			} while (r < minR);
			w = (-expectedValue * Math.log(r));
		} while (w > maxWait);
		return w;
	}

	public static void setMaxWait(double maxWait) {
		ExponentialWait.maxWait = maxWait;
	}
}
