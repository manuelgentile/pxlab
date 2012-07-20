package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Create and initialize a named runtime counter. An unlimted number of counters
 * may be created.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see IncrementCounter
 */
public class InitCounter extends Display {
	/** The name of the counter to be used. */
	public ExPar Name = new ExPar(STRING, new ExParValue("Counter1"),
			"Name of the counter");
	/** The initial counter value. */
	public ExPar InitialValue = new ExPar(DOUBLE, new ExParValue(0.0),
			"Initial counter value");

	public InitCounter() {
		setTitleAndTopic("Initialize a counter", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		RuntimeRegistry.put(Name.getString(),
				new Double(InitialValue.getDouble()));
	}
}
