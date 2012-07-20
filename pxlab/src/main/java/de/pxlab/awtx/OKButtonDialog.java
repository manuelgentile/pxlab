package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;

/**
 * A modal dialog which contains a center area for some information display and
 * a single button for closing the dialog. Use
 * <code>add(component, BorderLayout.CENTER)</code> to add the center component.
 * 
 * @author Hans Irtel
 * @version 0.1.0
 */
public class OKButtonDialog extends SingleButtonDialog {
	/**
	 * Create a dialog for the given frame parameters containing a single close
	 * button.
	 * 
	 * @param parent
	 *            the parent frame of this dialog.
	 * @param title
	 *            the title string for the dialog's frame.
	 */
	public OKButtonDialog(Frame parent, String title) {
		super(parent, title, "      OK      ");
	}

	protected void closeDialog() {
		setVisible(false);
		dispose();
	}
}
