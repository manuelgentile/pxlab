package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Activates an external trigger pulse. The trigger may be connected to a serial
 * port output line.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Trigger extends Display {
	public Trigger() {
		setTitleAndTopic("External Trigger Signal", EXTERNAL_DSP | EXP);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	public boolean isGraphic() {
		return false;
	}

	protected int create() {
		enterDisplayElement(new ExternalControlSignal(true, false), group[0]);
		defaultTiming(0);
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
	}
}
