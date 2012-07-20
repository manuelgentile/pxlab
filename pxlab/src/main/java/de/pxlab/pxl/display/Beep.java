package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A simple audio beep signal with fixed pitch and duration. The actual sound
 * will be operating system dependent. Under Windows this corresponds to the
 * "ding" standard sound as it is defined by the system control panel. Note that
 * the timing parameters do not affect the sound but the time interval reserved
 * for the sound. You should not set this Display's Duration parameter to 0
 * since it will not be shown in this case.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 06/14/02
 */
public class Beep extends Display {
	/** Constructor creating the title of the display. */
	public Beep() {
		setTitleAndTopic("Audio Beep", AUDIO_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"));
		Duration.set(1);
	}

	protected int create() {
		int s = enterDisplayElement(new AudioBeep(), group[0]);
		defaultTiming(0);
		return (s);
	}

	protected void computeGeometry() {
	}
}
