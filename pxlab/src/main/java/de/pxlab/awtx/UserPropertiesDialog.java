package de.pxlab.awtx;

import java.awt.*;

/**
 * Shows information about the current user.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class UserPropertiesDialog extends CloseButtonDialog {
	public UserPropertiesDialog(Frame f) {
		super(f, "User Information");
		Panel infoPanel = new Panel(new GridLayout(0, 1, 0, 0));
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.add(new Label("Current User Name:  "
				+ System.getProperty("user.name"), Label.LEFT));
		infoPanel.add(new Label("User Home Directory:  "
				+ System.getProperty("user.home"), Label.LEFT));
		infoPanel.add(new Label("Current Working Directory:  "
				+ System.getProperty("user.dir"), Label.LEFT));
		pack();
		setVisible(true);
	}
}
