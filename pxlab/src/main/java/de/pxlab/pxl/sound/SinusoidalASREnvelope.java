package de.pxlab.pxl.sound;

/**
 * A sinusoidal attack, sustain, and release envelope. The sinusoidal attack is
 * a sinusoidal increase in intensity over the duration of the attack period.
 * The sustain period always has a constant value equal to the gain value. And
 * the sinusoidal release period also is a sinusoidal decrease in intensity.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class SinusoidalASREnvelope extends ASRSoundEnvelope {
	/**
	 * Create an envelope with sinusoidal attack, constant release and
	 * sinusoidal decay.
	 * 
	 * @param d
	 *            total envelope duration in milliseconds. Setting the duration
	 *            implicitly defines the sustain period. The sustain period is
	 *            the total duration minus the quiet, minus the attack and minus
	 *            the release duration.
	 * @param q
	 *            quiet lead duration in milliseconds.
	 * @param a
	 *            attack duration in milliseconds.
	 * @param r
	 *            release duration in milliseconds.
	 */
	public SinusoidalASREnvelope(double d, double q, double a, double r) {
		super(d, q, a, r);
	}

	/**
	 * Compute an envelope function value for the attack period.
	 * 
	 * @param t
	 *            the time argument of the envelope function with t = 0.0 at the
	 *            start of the attack period.
	 */
	protected double attackValueOf(double t) {
		return gain * (1.0 - Math.cos(t / attack * Math.PI)) / 2.0;
	}

	/**
	 * Compute an envelope function value for the release period.
	 * 
	 * @param t
	 *            the time argument of the envelope function with t = 0.0 at the
	 *            start of the release period.
	 */
	protected double releaseValueOf(double t) {
		return gain * (1.0 + Math.cos(t / release * Math.PI)) / 2.0;
	}
}
