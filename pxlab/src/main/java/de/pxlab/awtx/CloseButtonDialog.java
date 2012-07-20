package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;

/**
 * A modal dialog which contains a center area for some information display and
 * a single button for closing the dialog. Use
 * <code>add(component, BorderLayout.CENTER)</code> to add the center component.
 * 
 * @author Hans Irtel
 * @version 0.1.1
 */
/*
 * 
 * 2005/05/11 made closeDialog() public in order to be able to override it form
 * other packages.
 */
public class CloseButtonDialog extends SingleButtonDialog {
	/**
	 * Create a dialog for the given frame parameters containing a single close
	 * button.
	 * 
	 * @param parent
	 *            the parent frame of this dialog.
	 * @param title
	 *            the title string for the dialog's frame.
	 */
	public CloseButtonDialog(Frame parent, String title) {
		super(parent, title, "    Close    ");
	}

	public void closeDialog() {
		setVisible(false);
		dispose();
	}
}
