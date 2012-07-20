package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Switch off an external signal. This Display is used to switch off an external
 * signal like a LED connected to a serial port output control line.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * @see ExternalSignalOn
 */
/*
 * 
 * 08/22/03 do not set the Overlay property by default.
 */
public class ExternalSignalOff extends ExternalSignalOn {
	public ExternalSignalOff() {
		setTitleAndTopic("Switch off an external signal", EXTERNAL_DSP | EXP);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		s = new ExternalControlSignal(false, false);
		enterDisplayElement(s, group[0]);
		defaultTiming(0);
		return (backgroundFieldIndex);
	}
}
