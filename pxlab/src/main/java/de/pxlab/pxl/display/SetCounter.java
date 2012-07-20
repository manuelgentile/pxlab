package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Set a named runtime counter which has been created and initialized earlier.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see InitCounter
 */
public class SetCounter extends InitCounter {
	/** The counter value after it has been set. */
	public ExPar Value = new ExPar(DOUBLE, new ExParValue(0.0), "Counter value");

	public SetCounter() {
		setTitleAndTopic("Set a counter", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected void computeGeometry() {
		String n = Name.getString();
		Double i = (Double) RuntimeRegistry.get(n);
		if (i == null) {
			new ParameterValueError(
					"SetCounter tries to use a counter which has not been initialized: "
							+ n);
		} else {
			RuntimeRegistry.put(n, new Double(Value.getDouble()));
		}
	}
}
