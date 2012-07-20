package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;

/**
 * A modal dialog which contains a top and central area for some information
 * display and a bottom row which contains three panels for adding buttons. One
 * of these is left aligned, one is centered, and one is right aligned. Use
 * <code>add(component, BorderLayout.CENTER)</code> to add the top component and
 * use <code>leftButtonPanel.add(button)</code>,
 * <code>centerButtonPanel.add(button)</code> or
 * <code>rightButtonPanel.add(button)</code> to add a button to this dialog's
 * button panels.
 * 
 * @author Hans Irtel
 * @version 0.1.2
 */
/*
 * 
 * 09/11/02 Fixed constructors to be Java 1.1 compatible
 */
abstract public class ButtonDialog extends Dialog {
	protected int buttonGapSize = 0;
	protected int fieldGapSize = 16;
	protected int borderSize = 20;
	protected Panel rightButtonPanel;
	protected Panel leftButtonPanel;
	protected Panel centerButtonPanel;

	/**
	 * Create a dialog with the given parameters.
	 * 
	 * @param parent
	 *            the parent frame of this dialog.
	 * @param title
	 *            the title string for the dialog's frame.
	 */
	public ButtonDialog(Frame parent, String title) {
		super((parent != null) ? parent : new Frame(), title, true);
		// System.out.println("ButtonDialog(): " + title);
		setLayout(new BorderLayout(fieldGapSize, fieldGapSize));
		if ((parent != null) && (parent.isVisible())) {
			Point p = parent.getLocationOnScreen();
			if (p != null) {
				setLocation(p.x + 120, p.y + 80);
			}
		}
		// setResizable(false);
		// setResizable(true);
		InsetPanel buttonPanelContainer = new InsetPanel(new BorderLayout());
		buttonPanelContainer
				.setInsets(new Insets((2 * borderSize) / 3, 0, 0, 0));
		leftButtonPanel = new Panel(new GridLayout(1, 0, buttonGapSize, 0));
		rightButtonPanel = new Panel(new GridLayout(1, 0, buttonGapSize, 0));
		centerButtonPanel = new Panel(new GridLayout(1, 0, buttonGapSize, 0));
		buttonPanelContainer.add(leftButtonPanel, BorderLayout.WEST);
		buttonPanelContainer.add(rightButtonPanel, BorderLayout.EAST);
		buttonPanelContainer.add(centerButtonPanel, BorderLayout.CENTER);
		Panel bottomPanel = new Panel(new BorderLayout());
		bottomPanel.add(new Separator(), BorderLayout.NORTH);
		bottomPanel.add(buttonPanelContainer, BorderLayout.SOUTH);
		add(bottomPanel, BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				closeDialog();
			}
		});
	}

	/**
	 * This method responds to a closing request which comes from pressing the
	 * close button at the top right window corner. It should remove the dialog
	 * from the screen but should dispose of all resources only if no
	 * information will be requested from the dialog by the calling instance.
	 */
	abstract protected void closeDialog();

	public Insets getInsets() {
		Insets i = super.getInsets();
		return (new Insets(i.top + borderSize, i.left + borderSize, i.bottom
				+ borderSize, i.right + borderSize));
	}
}
