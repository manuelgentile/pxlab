package de.pxlab.pxl.sound;

/**
 * An envelope function is a single argument function which satisfies 0.0 <=
 * f(t) <= gain <= 1.0 at any point in time. Every envelope function may have an
 * initial quiet lead period which might be necessary in order to get a noise
 * free signal start on the respective hardware.
 * 
 * <p>
 * The time argument for computing envelope function values are seconds.
 * Durations are given in milliseconds.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
abstract public class SoundEnvelope {
	/** The gain value is the maximum level of the envelope function. */
	protected double gain = 1.0;
	/**
	 * An initial quiet period which may be necessary to get a noise free signal
	 * start.
	 */
	protected double quietLead = 0.0;

	/**
	 * Create an envelope function with an initial quiet period which may be
	 * necessary to get a noise free signal start.
	 * 
	 * @param ms
	 *            quiet lead period in milliseconds.
	 */
	public SoundEnvelope(double ms) {
		quietLead = ms / 1000.0;
	}

	/**
	 * Set the gain value of this envelope.
	 * 
	 * @param g
	 *            the gain value. Must be non-negative and less or equal to 1.0.
	 */
	public void setGain(double g) {
		if (g < 0.0) {
			gain = 0.0;
		} else if (g > 1.0) {
			gain = 1.0;
		} else {
			gain = g;
		}
	}

	/** Get the current gain value. */
	public double getGain() {
		return gain;
	}

	/**
	 * Compute an envelope function value for the given point in time.
	 * 
	 * @param t
	 *            the time argument of the envelope function given in seconds.
	 */
	public double valueOf(double t) {
		double x = 0.0;
		if (t > quietLead) {
			x = valueOfEnvelope(t - quietLead);
		}
		return x;
	}

	/**
	 * Compute the nonquiet part of an envelope function.
	 * 
	 * @param t
	 *            the time argument of the envelope function in seconds relative
	 *            to the start of the nonquiet period.
	 */
	abstract protected double valueOfEnvelope(double t);
}
