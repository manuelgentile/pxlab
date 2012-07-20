package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Shows a clear screen for a certain duration.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 01/26/01 use ExPar color for DisplayElement objects
 * 
 * 06/23/01 use ScreenBackgroundColor as color parameter. It is important that
 * all experimental Display objects use the same background color since
 * otherwise we have to set the background color for every Display object of a
 * DisplayList.
 */
public class ClearScreen extends Display {
	public ClearScreen() {
		setTitleAndTopic("Clear Screen", CLEAR_DSP | EXP);
	}

	protected int create() {
		int s1 = enterDisplayElement(new Clear(), group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeGeometry() {
	}
}
