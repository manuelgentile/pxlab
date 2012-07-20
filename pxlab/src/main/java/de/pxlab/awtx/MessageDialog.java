package de.pxlab.awtx;

import java.awt.*;
import java.util.ArrayList;
import de.pxlab.util.StringExt;

/**
 * This is a modal dialog which presents a message to the user and contains a
 * single close button.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
public class MessageDialog extends CloseButtonDialog {
	/**
	 * Create a dialog for the given frame parameters containing a single close
	 * button and showning the given message.
	 * 
	 * @param parent
	 *            the parent frame of this dialog.
	 * @param title
	 *            the title string for the dialog's frame.
	 * @param message
	 *            the text shown by the message dialog.
	 */
	public MessageDialog(Frame parent, String title, String message) {
		super(parent, title);
		InsetPanel messagePanel = new InsetPanel(new ColumnLayout(2));
		messagePanel.setInsets(new Insets(30, 40, 30, 40));
		ArrayList a = StringExt.getTextParagraph(message);
		for (int i = 0; i < a.size(); i++) {
			messagePanel.add(new Label((String) a.get(i)));
		}
		add(messagePanel, BorderLayout.CENTER);
		pack();
	}
}
