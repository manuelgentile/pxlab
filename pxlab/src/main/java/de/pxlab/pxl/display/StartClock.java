package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Start a named clock. This starts a clock whose name is given as an
 * experimental parameter. The current time of this clock may be retrieved by
 * using Display object ReadClock with the same name. An unlimited number of
 * clocks may be started.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see ReadClock
 */
public class StartClock extends Display {
	/** The name of the clock to be started. */
	public ExPar Name = new ExPar(STRING, new ExParValue("Clock1"),
			"Name of the clock");

	public StartClock() {
		setTitleAndTopic("Start a clock", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		RuntimeRegistry.put(Name.getString(),
				new Long(HiresClock.getTimeNanos()));
	}
}
