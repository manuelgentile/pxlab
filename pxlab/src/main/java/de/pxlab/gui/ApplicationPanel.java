package de.pxlab.gui;

import java.awt.*;

/**
 * This ist the super class of main control panels. These panels have an action
 * bar on the top and are stacked onto the main control frame.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see ApplicationFrame
 */
public class ApplicationPanel extends Panel {
	protected String title = null;
	protected String shortTitle = null;
	protected ActionBar actionBar = null;

	/**
	 * Create a new control panel with an action bar in its north field.
	 */
	public ApplicationPanel() {
		super(new BorderLayout());
	}

	/**
	 * Set this control panel's ActionBar.
	 * 
	 * @param bb
	 *            the ActionBar object which should be set.
	 */
	public void setActionBar(ActionBar bb) {
		if (bb != actionBar) {
			if (actionBar != null) {
				this.remove(actionBar);
			}
			if (bb != null)
				this.add(bb, BorderLayout.NORTH);
			actionBar = bb;
		}
	}

	/**
	 * Get this control panel's ActionBar.
	 * 
	 * @return the ActionBar object of this control panel.
	 */
	public ActionBar getActionBar() {
		return (actionBar);
	}

	/**
	 * Create this panel's options menu.
	 * 
	 * @return a Menu object which contains this panel's options.
	 */
	public Menu getOptionsMenu() {
		Menu menu = new Menu("Unknown");
		return (menu);
	}

	/**
	 * Get this panel's title.
	 * 
	 * @return a String object which ist this panel's title.
	 */
	public String getTitle() {
		return (title);
	}

	/**
	 * Get this panel's short title.
	 * 
	 * @return a String object which ist this panel's shorttitle.
	 */
	public String getShortTitle() {
		return (shortTitle);
	}
}
