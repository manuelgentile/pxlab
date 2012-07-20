package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Does not show anything but is 'visible' which means that its show() methods
 * are called!. It may be used to preserve screen content and provide a new set
 * of Display object parameters. This MUST be JustInTime in order to prevent
 * back buffer creation.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/05/04
 */
public class Nothing extends Display {
	public Nothing() {
		setTitleAndTopic("Empty Display", CLEAR_DSP | EXP);
		JustInTime.set(1);
	}

	public boolean isGraphic() {
		return false;
	}

	protected int create() {
		int s1 = enterDisplayElement(new EmptyDisplayElement(), group[0]);
		defaultTiming(0);
		JustInTime.set(1);
		return s1;
	}

	protected void computeGeometry() {
	}
}
