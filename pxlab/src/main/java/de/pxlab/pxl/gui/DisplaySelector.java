package de.pxlab.pxl.gui;

import de.pxlab.pxl.Display;

/** Describes objects which can select Displays. */
public interface DisplaySelector {
	/** Select the given display for further action. */
	public void selectDisplay(Display dsp);
}
