package de.pxlab.pxl.display.editor;

import java.awt.Frame;
import java.awt.MenuBar;

/**
 * Describes objects which are able to control a DisplayEditorPanel.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public interface DisplayEditorPanelController {
	/**
	 * Return the Frame object which is the editor's parent in the container
	 * hierarchy.
	 */
	public Frame getFrame();

	/**
	 * Get the controller's menu bar if it has one.
	 * 
	 * @return the controller's menu bar or null if this controller does not
	 *         have a menu bar.
	 */
	public MenuBar getMenuBar();

	/**
	 * Set the enabled stet of the controller's menu bar.
	 * 
	 * @param state
	 *            if true then the menu bar items are enabled if false then they
	 *            are disabled
	 */
	public void setMenuBarEnabled(boolean state);
}
