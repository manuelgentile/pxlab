package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;

/**
 * A modal dialog which contains a center area for some information display and
 * a bottom row which contains a Cancel and an OK button which are
 * right-aligned. Use <code>add(component,
    BorderLayout.CENTER)</code> to add the center component to
 * this dialog's button panel.
 * 
 * @author Hans Irtel
 * @version 0.1.2
 */
/*
 * 
 * 09/11/02 Fixed constructors to be Java 1.1 compatible
 * 
 * 09/20/02 set cancelled on true in actionListener (er)
 */
abstract public class CancelOKButtonDialog extends ButtonDialog {
	protected Button cancelButton;
	protected Button OKButton;
	protected boolean cancelled = true;

	public CancelOKButtonDialog(Frame parent, String title) {
		super(parent, title);
		ActionListener buttonHandler = new ButtonHandler();
		OKButton = new Button("  OK  ");
		OKButton.addActionListener(buttonHandler);
		rightButtonPanel.add(OKButton);
		cancelButton = new Button("  Cancel  ");
		cancelButton.addActionListener(buttonHandler);
		rightButtonPanel.add(cancelButton);
	}
	/*
	 * Not available under Java 1.1 ------------------------- public
	 * CancelOKButtonDialog(Dialog parent, String title) { super(parent, title);
	 * 
	 * ActionListener buttonHandler = new ButtonHandler();
	 * 
	 * cancelButton = new Button("  Cancel  ");
	 * cancelButton.addActionListener(buttonHandler);
	 * rightButtonPanel.add(cancelButton);
	 * 
	 * OKButton = new Button("  OK  ");
	 * OKButton.addActionListener(buttonHandler);
	 * rightButtonPanel.add(OKButton);
	 * 
	 * }
	 */
	private class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Button b = (Button) e.getSource();
			if (b == cancelButton) {
				cancelled = true;
				closeDialog();
			} else if (b == OKButton) {
				cancelled = false;
				closeOKDialog();
			}
		}
	}

	protected void closeOKDialog() {
		setVisible(false);
	}

	protected void closeDialog() {
		setVisible(false);
	}

	/** This dialog was closed with the OK button. */
	public boolean isOK() {
		return !cancelled;
	}

	/** This dialog was closed with any of the cancel buttons. */
	public boolean isCancelled() {
		return cancelled;
	}
}
