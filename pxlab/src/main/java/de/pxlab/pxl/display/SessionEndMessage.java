package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * The default message for ending a session. It shows the text "END" for a fixed
 * duration of 2 seconds.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class SessionEndMessage extends Message {
	public SessionEndMessage() {
		setTitleAndTopic("Session end message", PROC_MESSAGE_DSP | EXP);
		Text.set(ExParValue.getValueForLanguage(new ExParValue("E N D"),
				new ExParValue("E N D E")).getString());
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"));
		Duration.set(2000);
	}
}
