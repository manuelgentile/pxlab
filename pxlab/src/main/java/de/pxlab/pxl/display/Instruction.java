package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * This is almost the same as TextParagraph.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Instruction extends TextParagraph {
	public Instruction() {
		setTitleAndTopic("Instruction text", TEXT_PAR_DSP);
		String[] p = { "Instruction" };
		Text.set(p);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RELEASE_RESPONSE_TIMER"));
		EmphasizeFirstLine.set(1);
	}
}
