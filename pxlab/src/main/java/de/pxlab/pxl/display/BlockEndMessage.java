package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * The default message for ending a block. It show the text "PAUSE" for a fixed
 * duration of 2 seconds.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class BlockEndMessage extends Message {
	public BlockEndMessage() {
		setTitleAndTopic("Block end message", PROC_MESSAGE_DSP | EXP);
		Text.set(ExParValue.getValueForLanguage(new ExParValue("PAUSE"),
				new ExParValue("PAUSE")).getString());
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"));
		Duration.set(2000);
	}
}
