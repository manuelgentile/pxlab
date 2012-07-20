package de.pxlab.pxl.sound;

/**
 * An envelope which has an initial quiet and attack period, followed by a
 * constant sustain and a final release period. The attack and release parts are
 * computed by subclass methods. The sustain period value is constant and equal
 * to the gain value which satisfies 0.0 <= gain <= 1.0.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
abstract public class ASRSoundEnvelope extends SoundEnvelope {
	/**
	 * Total envelope duration, including the quiet lead period, given in
	 * seconds.
	 */
	protected double duration;
	/** The attack period duration in seconds. */
	protected double attack;
	/** The release period duration in seconds. */
	protected double release;
	/**
	 * The point in the time argument of valueOfEnvelope() when the attack
	 * period ends.
	 */
	private double endOfAttackArg;
	/**
	 * The point in the time argument of valueOfEnvelope() when the sustain
	 * period ends.
	 */
	private double endOfSustainArg;

	/**
	 * Create an envelope with an initial quiet period, an attack, a constant
	 * release and a decay period.
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
	 * @param r
	 *            release duration in milliseconds.
	 */
	public ASRSoundEnvelope(double d, double q, double a, double r) {
		super(q);
		duration = d / 1000.0;
		attack = a / 1000.0;
		release = r / 1000.0;
		endOfAttackArg = attack;
		endOfSustainArg = duration - release - quietLead;
		if (duration < (quietLead + attack + release)) {
			System.out
					.println("ASRSoundEnvelope.setDuration(): Duration less than (Q+A+R)");
		}
		/*
		 * System.out.println("ASRSoundEnvelope()");
		 * System.out.println("    duration = " + duration);
		 * System.out.println("   quietLead = " + quietLead);
		 * System.out.println("      attack = " + attack);
		 * System.out.println("     release = " + release);
		 * System.out.println(" endOfAttack = " + endOfAttackArg);
		 * System.out.println("endOfSustain = " + endOfSustainArg);
		 */
	}
	static int iii = 0;

	/**
	 * Compute an envelope function value for the given point in time. If time
	 * points after the value set as duration are requested then 0.0 is
	 * returned.
	 * 
	 * @param t
	 *            the time argument of the envelope function in seconds. Time
	 *            starts with t = 0.0 at the beginning of the attack period.
	 */
	protected double valueOfEnvelope(double t) {
		double x = 0.0;
		if (t < endOfAttackArg) {
			x = attackValueOf(t);
		} else if (t < endOfSustainArg) {
			x = gain;
		} else if (t < duration) {
			x = releaseValueOf(t - endOfSustainArg);
		}
		return x;
	}

	/**
	 * Compute the envelope for the attack period.
	 * 
	 * @param t
	 *            is the time with t = 0.0 at the start of the attack period.
	 */
	abstract protected double attackValueOf(double t);

	/**
	 * Compute the envelope for the release period.
	 * 
	 * @param t
	 *            is the time with t = 0.0 at the start of the release period.
	 */
	abstract protected double releaseValueOf(double t);
}
