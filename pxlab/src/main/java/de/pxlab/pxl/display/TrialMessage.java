package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * The default message for a trial. It show the trial number and then waits
 * until a response key is pressed.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public class TrialMessage extends Message {
	public TrialMessage() {
		setTitleAndTopic("Trial message", PROC_MESSAGE_DSP | EXP);
		Text.set("Trial %TrialCounter%");
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RESPONSE_TIMER"));
	}
}
