package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Clear the screen to a given color.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class FillScreen extends ClearScreen {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLACK)), "Fill Color");

	public FillScreen() {
		setTitleAndTopic("Fill Screen", CLEAR_DSP | EXP);
	}

	protected int create() {
		int s1 = enterDisplayElement(new Clear(this.Color), group[0]);
		defaultTiming(0);
		return (s1);
	}
}
