package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Single frame black bars on a white background help to evaluate the decay time
 * of display.
 * 
 * @author H. Irtel
 * @version 0.5.0
 */
/*
 * 2005/09/22
 */
public class BlackSwitching extends WhiteSwitching {
	public BlackSwitching() {
		setTitleAndTopic("Single Frame Black Bars", DISPLAY_TEST_DSP | DEMO);
		BackgroundColor.set(new ExParValueFunction(ExParExpression.WHITE));
		Color.set(new ExParValueFunction(ExParExpression.BLACK));
	}
}
