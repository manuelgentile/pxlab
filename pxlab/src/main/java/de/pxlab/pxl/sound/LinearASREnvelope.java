package de.pxlab.pxl.sound;

/**
 * A linear attack, sustain, and release envelope. The linear attack is a linear
 * increase in intensity over the duration of the attack period. The sustain
 * period always has a constant value equal to the gain value. And the linear
 * release period also is a linear decrease in intensity.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class LinearASREnvelope extends ASRSoundEnvelope {
	/**
	 * Create an envelope with linear attack, constant release and linear decay.
	 * 
	 * @param d
	 *            total envelope duration in milliseconds. Setting the duration
	 *            implicitly defines the sustain period. The sustain period is
	 *            the total duration minus the attack and minus the release
	 *            duration.
	 * @param q
	 *            quiet lead duration in milliseconds.
	 * @param a
	 *            attack duration in milliseconds.
	 * @param r
	 *            release duration in milliseconds.
	 */
	public LinearASREnvelope(double d, double q, double a, double r) {
		super(d, q, a, r);
	}

	/**
	 * Compute an envelope function value for the attack period.
	 * 
	 * @param t
	 *            the time argument of the envelope function.
	 */
	protected double attackValueOf(double t) {
		return gain * t / attack;
	}

	/**
	 * Compute an envelope function value for the release period.
	 * 
	 * @param t
	 *            the time argument of the envelope function with t = 0.0 at the
	 *            start of the release period.
	 */
	protected double releaseValueOf(double t) {
		return gain * (1.0 - t / release);
	}
}
