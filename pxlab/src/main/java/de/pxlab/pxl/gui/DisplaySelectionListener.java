package de.pxlab.pxl.gui;

import de.pxlab.pxl.DisplaySupport;

/**
 * Describes objects which listen to Display selection from the
 * DisplaySelectionMenu.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see DisplaySelectionMenu
 */
public interface DisplaySelectionListener {
	/** Select the given display for further action. */
	public void selectDisplay(DisplaySupport dsp);
}
