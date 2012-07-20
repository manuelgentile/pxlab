package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Get a named runtime counter's value.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see InitCounter
 */
public class GetCounter extends SetCounter {
	public GetCounter() {
		setTitleAndTopic("Get a counter value", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected void computeGeometry() {
		String n = Name.getString();
		Double i = (Double) RuntimeRegistry.get(n);
		if (i == null) {
			new ParameterValueError(
					"GetCounter tries to use a counter which has not been initialized: "
							+ n);
		} else {
			Value.set(i.doubleValue());
		}
	}
}
