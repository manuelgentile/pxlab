package de.pxlab.pxl.sound;

/**
 * A constant sound envelope function.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ConstantEnvelope extends SoundEnvelope {
	/**
	 * Create a constant envelope.
	 * 
	 * @param q
	 *            quiet lead duration in milliseconds.
	 */
	public ConstantEnvelope(double q) {
		super(q);
	}

	protected double valueOfEnvelope(double t) {
		return gain;
	}
}
