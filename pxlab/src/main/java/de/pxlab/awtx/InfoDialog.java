package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import de.pxlab.util.StringExt;

/**
 * A non-modal dialog which contains a center area for some information display.
 * 
 * @author Hans Irtel
 * @version 0.1.0
 */
/*
 * 
 * 09/03/03
 */
public class InfoDialog extends Dialog {
	protected int fieldGapSize = 16;
	protected int borderSize = 20;

	/**
	 * Create a dialog with the given parameters.
	 * 
	 * @param parent
	 *            the parent frame of this dialog.
	 * @param title
	 *            the title string for the dialog's frame.
	 * @param message
	 *            the message to show.
	 */
	public InfoDialog(Frame parent, String title, String message) {
		super((parent != null) ? parent : new Frame(), title, false);
		// System.out.println("InfoDialog(): " + title);
		setLayout(new BorderLayout(fieldGapSize, fieldGapSize));
		InsetPanel messagePanel = new InsetPanel(new ColumnLayout(2));
		messagePanel.setInsets(new Insets(30, 40, 30, 40));
		ArrayList a = StringExt.getTextParagraph(message);
		for (int i = 0; i < a.size(); i++) {
			messagePanel.add(new Label((String) a.get(i)));
		}
		add(messagePanel, BorderLayout.CENTER);
		pack();
		if ((parent != null) && (parent.isVisible())) {
			Point p = parent.getLocationOnScreen();
			if (p != null) {
				setLocation(p.x + 120, p.y + 80);
			}
		}
		setResizable(false);
		// setResizable(true);
		/*
		 * addWindowListener( new WindowAdapter(){ public void
		 * windowClosing(WindowEvent evt) { closeDialog(); } } );
		 */
	}

	public Insets getInsets() {
		Insets i = super.getInsets();
		return (new Insets(i.top + borderSize, i.left + borderSize, i.bottom
				+ borderSize, i.right + borderSize));
	}
}
