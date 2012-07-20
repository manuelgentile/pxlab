package de.pxlab.pxl.sound;

/**
 * A Gaussian attack, constant sustain, and Gaussian release envelope. The
 * sustain period always has a constant value equal to the gain value.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class GaussianASREnvelope extends ASRSoundEnvelope {
	/** Attack duration variance. */
	private double attack_v;
	/** Release duration variance. */
	private double release_v;

	/**
	 * Create an envelope with Gaussian attack, constant release and Gaussian
	 * decay.
	 * 
	 * @param d
	 *            total envelope duration in milliseconds. Setting the duration
	 *            implicitly defines the sustain period. The sustain period is
	 *            the total duration minus the quiet, minus the attack, and
	 *            minus the release duration.
	 * @param q
	 *            quiet lead duration in milliseconds.
	 * @param a
	 *            attack duration in milliseconds.
	 * @param as
	 *            attack period standard deviation in milliseconds.
	 * @param r
	 *            release duration in milliseconds.
	 * @param rs
	 *            release period standard deviation in milliseconds.
	 */
	public GaussianASREnvelope(double d, double q, double a, double as,
			double r, double rs) {
		super(d, q, a, r);
		as /= 1000.0;
		attack_v = as * as;
		rs /= 1000.0;
		release_v = rs * rs;
	}

	/**
	 * Compute an envelope function value for the attack period.
	 * 
	 * @param t
	 *            the time argument of the envelope function with t = 0.0 at the
	 *            start of the attack period.
	 */
	protected double attackValueOf(double t) {
		double x = t - attack;
		return gain * Math.exp(-(x * x / attack_v));
	}

	/**
	 * Compute an envelope function value for the release period.
	 * 
	 * @param t
	 *            the time argument of the envelope function with t = 0.0 at the
	 *            start of the release period.
	 */
	protected double releaseValueOf(double t) {
		return gain * Math.exp(-(t * t / release_v));
	}
}
