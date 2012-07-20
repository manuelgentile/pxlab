package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * The default message for starting a session. It show the text
 * "Press any key to start!" and then waits until a response key is pressed and
 * released again.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class SessionStartMessage extends Message {
	public SessionStartMessage() {
		setTitleAndTopic("Session start message", PROC_MESSAGE_DSP | EXP);
		Text.set(ExParValue.getValueForLanguage(
				new ExParValue("Press any key to start!"),
				new ExParValue(
						"Zum Starten bitte eine beliebige Taste dr�cken!"))
				.getString());
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RELEASE_RESPONSE_TIMER"));
	}
}
