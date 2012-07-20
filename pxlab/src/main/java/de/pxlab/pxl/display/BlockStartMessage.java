package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * The default message for starting a block. It show the text "Press any key to
 * go on!" and then waits until a response key is pressed and released again.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class BlockStartMessage extends Message {
	public BlockStartMessage() {
		setTitleAndTopic("Block start message", PROC_MESSAGE_DSP | EXP);
		Text.set(ExParValue
				.getValueForLanguage(
						new ExParValue("Press any key to go on!"),
						new ExParValue(
								"Zum Weitermachen bitte eine beliebige Taste dr�cken!"))
				.getString());
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RELEASE_RESPONSE_TIMER"));
	}
}
