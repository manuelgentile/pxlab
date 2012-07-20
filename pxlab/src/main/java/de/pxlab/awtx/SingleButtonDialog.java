package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;

/**
 * A modal dialog which contains a center area for some information display and
 * a single button for closing the dialog. Use
 * <code>add(component, BorderLayout.CENTER)</code> to add the center component.
 * This class is abstract since it does not define the closing behaviour. The
 * button calls the closeDialog() method when pressed.
 * 
 * @author Hans Irtel
 * @version 0.1.0
 */
abstract public class SingleButtonDialog extends ButtonDialog {
	protected Button button;

	/**
	 * Create a dialog with the given parameters.
	 * 
	 * @param parent
	 *            the parent frame of this dialog.
	 * @param title
	 *            the title string for the dialog's frame.
	 * @param label
	 *            the text of the button label.
	 */
	public SingleButtonDialog(Frame parent, String title, String label) {
		super(parent, title);
		button = new Button(label);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		rightButtonPanel.add(button);
	}

	public void setButtonLabel(String label) {
		button.setLabel(label);
	}
}
