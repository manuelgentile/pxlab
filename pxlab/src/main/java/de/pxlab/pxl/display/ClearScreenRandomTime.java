package de.pxlab.pxl.display;

import de.pxlab.util.ExponentialWait;
import de.pxlab.pxl.*;

/**
 * Shows a clear screen for a random duration. The distribution of the random
 * time interval is exponential within certain limits.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class ClearScreenRandomTime extends ClearScreen {
	/*
	 * Distribution type for the random time interval. Currently only the
	 * exponential distribution (2) is implemented.
	 * 
	 * public ExPar RandomDistribution = new ExPar(INTEGER_0_2, new
	 * ExParValue(2), "Random distribution type");
	 */
	/** The expected value of the random distribution. */
	public ExPar ExpectedWait = new ExPar(DURATION, 0, 10000,
			new ExParValue(0), "Expected waiting time");
	/**
	 * The upper limit of the waiting time. Random samples are drawn from the
	 * underlying distribution until a value is found which is less than this
	 * value.
	 */
	public ExPar WaitingTimeLimit = new ExPar(DURATION, 0, 10000,
			new ExParValue(10000), "Waiting time limit");

	public ClearScreenRandomTime() {
		setTitleAndTopic("Clear Screen with Random Delay", CLEAR_DSP | EXP);
	}

	public void computeTiming() {
		/*
		 * int distribution = RandomDistribution.getInt(); switch (distribution)
		 * { case 0: break; case 1: case 2:
		 */
		ExponentialWait.setMaxWait(WaitingTimeLimit.getDouble());
		Duration.set(ExponentialWait.instance(ExpectedWait.getDouble()));
		/*
		 * break; default: break; }
		 */
	}
}
