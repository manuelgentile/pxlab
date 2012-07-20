package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Read the state of a named clock which has been started earlier by running
 * StartClock().
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see StartClock
 */
public class ReadClock extends StartClock {
	/** This is the time interval read from the clock given in milliseconds. */
	public ExPar Time = new ExPar(RTDATA, new ExParValue(0),
			"Time read in milliseconds");
	/** This is the time interval read from the clock given in seconds. */
	public ExPar TimeSeconds = new ExPar(RTDATA, new ExParValue(0),
			"Time read in seconds");

	public ReadClock() {
		setTitleAndTopic("Read a clock", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		String n = Name.getString();
		Long s = (Long) RuntimeRegistry.get(n);
		if (s == null) {
			new ParameterValueError(
					"ReadClock tries to read a clock which has not been started: "
							+ n);
		} else {
			double ms = HiresClock.ms(s.longValue(), HiresClock.getTimeNanos());
			Time.set(ms);
			TimeSeconds.set(ms / 1000.0);
		}
	}
}
