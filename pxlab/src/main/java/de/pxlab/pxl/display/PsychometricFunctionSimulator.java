package de.pxlab.pxl.display;

import java.awt.*;
import java.util.Random;

import de.pxlab.pxl.*;

/**
 * Simulates yes/no responses according to a logistic psychometric function.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 07/25/01
 */
public class PsychometricFunctionSimulator extends Display {
	/**
	 * The point of subjective equality of the psychometric function. This
	 * corresponds to the argument for a function value of 0.5.
	 */
	public ExPar pse = new ExPar(0.0, 10000.0, new ExParValue(100),
			"Point of subjective equality");
	/**
	 * The just noticable difference of the psychometric function. This
	 * corresponds to the difference between the arguments for function values
	 * of 0.5 and 0.73.
	 */
	public ExPar jnd = new ExPar(0.0, 200.0, new ExParValue(1.0),
			"Just noticable difference");
	/**
	 * The guessing rate. This depends on the experimental paradigm simultated.
	 * A yes/no design should have a guessing rate of 0, while a forced choice
	 * paradigm will have a guessing rate of 1/n where n is the number of
	 * alternatives.
	 */
	public ExPar GuessingRate = new ExPar(PROPORT, new ExParValue(0.0),
			"Guessing rate");
	/**
	 * The lapsing rate. This is the rate at which the subject will respond 'no'
	 * while his/her intention was to respond 'yes'.
	 */
	public ExPar LapsingRate = new ExPar(PROPORT, new ExParValue(0.0),
			"Lapsing rate");
	/**
	 * The experimental parameter which holds the stimulus value for this
	 * psychometric function simulation. Usually this will be a parameter of
	 * some other display object.
	 */
	public ExPar StimulusParameter = new ExPar(EXPARNAME, new ExParValue(
			"ResponseCode"), "Name of stimulus parameter");
	/** This is the simulated response code. */
	public ExPar Response = new ExPar(RTDATA, new ExParValue(0),
			"Response parameter");

	public PsychometricFunctionSimulator() {
		setTitleAndTopic("Psychometric function simulator", PARAM_DSP | EXP);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.CLOCK_TIMER"));
		Duration.set(1);
	}
	private Random rand;

	protected int create() {
		enterDisplayElement(new EmptyDisplayElement(), group[0]);
		defaultTiming(0);
		rand = new Random();
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
		double stim = ExPar.get(StimulusParameter.getString()).getDouble();
		double gs = GuessingRate.getDouble();
		double lp = LapsingRate.getDouble();
		double p = gs + (1.0 - gs - lp)
				/ (1.0 + Math.exp(-(stim - pse.getDouble()) / jnd.getDouble()));
		Response.set((rand.nextDouble() < p) ? ExPar.YesKey.getInt()
				: ExPar.NoKey.getInt());
	}
}
