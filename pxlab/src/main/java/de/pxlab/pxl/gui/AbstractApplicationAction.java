package de.pxlab.pxl.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import de.pxlab.pxl.Debug;

abstract public class AbstractApplicationAction extends AbstractAction {
	public AbstractApplicationAction(String text, String iconfile,
			String description, char key) {
		super(text);
		if (iconfile != null) {
			ImageIcon icon = null;
			Debug.show(Debug.FILES, "Search for icon file " + iconfile);
			URL url = getClass().getResource(iconfile);
			if (url != null) {
				Debug.show(Debug.FILES, "Search for file at URL " + url);
				try {
					// icon = new
					// ImageIcon(AbstractApplicationFrame.class.getResource(iconfile));
					icon = new ImageIcon(url);
				} catch (RuntimeException ex) {
					Debug.show(Debug.FILES, "Search for URL " + url
							+ " failed.");
				}
			} else {
				Debug.show(Debug.FILES, "Search for icon file " + iconfile
						+ " failed.");
			}
			if (icon != null) {
				putValue(SMALL_ICON, icon);
			}
		}
		putValue(SHORT_DESCRIPTION, description);
		putValue(MNEMONIC_KEY, new Integer(key));
	}
}
