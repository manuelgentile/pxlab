package de.pxlab.pxl.sound;

/**
 * A Gaussian sound envelope function.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class GaussianEnvelope extends SoundEnvelope {
	private double m;
	private double v;

	/**
	 * Create a Gaussian envelope which has its maximum at an arbitrary point in
	 * its non-quiet interval.
	 * 
	 * @param q
	 *            quiet lead duration in milliseconds.
	 * @param m
	 *            time where the Gaussian's maximum is reached relative to its
	 *            non-quiet interval.
	 * @param s
	 *            standard deviation of the Gaussian in milliseconds.
	 */
	public GaussianEnvelope(double q, double m, double s) {
		super(q);
		this.m = m / 1000.0;
		s /= 1000.0;
		v = s * s;
	}

	/**
	 * Compute an envelope function value.
	 * 
	 * @param t
	 *            the time argument of the envelope function starting with t =
	 *            0.0 at the start of the non-quiet period.
	 */
	protected double valueOfEnvelope(double t) {
		double x = t - m;
		return gain * Math.exp(-(x * x / v));
	}
}
