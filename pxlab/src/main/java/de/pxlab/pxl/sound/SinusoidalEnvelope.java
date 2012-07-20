package de.pxlab.pxl.sound;

/**
 * A Sinusoidal sound envelope function.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class SinusoidalEnvelope extends SoundEnvelope {
	private double tms;

	/**
	 * Create a sinusoidal envelope.
	 * 
	 * @param d
	 *            total duration of the envelope in milliseconds.
	 * @param q
	 *            quiet lead duration in milliseconds.
	 */
	public SinusoidalEnvelope(double d, double q) {
		super(q);
		tms = 2.0 * Math.PI / ((d - q) / 1000.0);
	}

	protected double valueOfEnvelope(double t) {
		return gain * (1.0 - Math.cos(tms * t)) / 2.0;
	}
}
