package de.pxlab.awtx;

import java.awt.*;

/**
 * Shows information about the operating system and the Java system.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
public class SystemPropertiesDialog extends CloseButtonDialog {
	public SystemPropertiesDialog(Frame f) {
		super(f, "System Information");
		Panel infoPanel = new Panel(new GridLayout(0, 1, 0, 0));
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.add(new Label(System.getProperty("java.vendor")
				+ " Java VM Version " + System.getProperty("java.version"),
				Label.LEFT));
		infoPanel.add(new Label("Java Installation Directory: "
				+ System.getProperty("java.home"), Label.LEFT));
		infoPanel.add(new Label("Java Class Path: "
				+ System.getProperty("java.class.path"), Label.LEFT));
		// infoPanel.add(new Label("Java Load Library Path: " +
		// System.getProperty("java.library.path"), Label.LEFT));
		infoPanel.add(new Label(System.getProperty("os.name") + " Version "
				+ System.getProperty("os.version"), Label.LEFT));
		pack();
		setVisible(true);
	}
}
