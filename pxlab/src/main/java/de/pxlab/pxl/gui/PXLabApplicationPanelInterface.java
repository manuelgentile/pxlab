package de.pxlab.pxl.gui;

import java.awt.Menu;
import de.pxlab.gui.ActionBar;
import de.pxlab.pxl.ExDesign;

/**
 * This interface makes sure that PXLabApplicationPanel and
 * PXLabApplicationJPanel objects implement the same basic methods.
 */
public interface PXLabApplicationPanelInterface {
	/**
	 * Set this control panel's ActionBar.
	 * 
	 * @param bb
	 *            the ActionBar object which should be set.
	 */
	public void setActionBar(ActionBar bb);

	/**
	 * Create this panel's options menu.
	 * 
	 * @return a Menu object which contains this panel's options.
	 */
	public Menu getOptionsMenu();

	/**
	 * Get this panel's title.
	 * 
	 * @return a String object which ist this panel's title.
	 */
	public String getTitle();

	/**
	 * Get this panel's short title.
	 * 
	 * @return a String object which ist this panel's shorttitle.
	 */
	public String getShortTitle();

	/**
	 * Set this control panel according to the content of the given experimental
	 * design.
	 * 
	 * @param exd
	 *            the new ExDesign object which should be used as experimental
	 *            design tree.
	 */
	public void setExDesign(ExDesign exd);
}
