package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Increment a named runtime counter which has been created and initialized
 * earlier.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see InitCounter
 */
public class IncrementCounter extends SetCounter {
	/** The increment value. */
	public ExPar Increment = new ExPar(DOUBLE, new ExParValue(1.0),
			"Counter increment value");

	public IncrementCounter() {
		setTitleAndTopic("Increment a counter", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected void computeGeometry() {
		String n = Name.getString();
		Double i = (Double) RuntimeRegistry.get(n);
		if (i == null) {
			new ParameterValueError(
					"IncrementCounter tries to use a counter which has not been initialized: "
							+ n);
		} else {
			double j = i.doubleValue() + Increment.getDouble();
			Value.set(j);
			RuntimeRegistry.put(n, new Double(j));
		}
	}
}
