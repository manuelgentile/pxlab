package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.SystemPropertiesDialog;
import de.pxlab.awtx.GraphicsEnvironmentDialog;
import de.pxlab.pxl.gui.AboutDialog;

public class HelpMenu extends Menu implements ActionListener {
	private MenuItem aboutItem;
	private MenuItem systemPropertiesItem;
	private MenuItem graphicsEnvironmentItem;
	private Frame parent;

	public HelpMenu(Frame p) {
		super("Help");
		parent = p;
		systemPropertiesItem = new MenuItem("Java System");
		systemPropertiesItem.addActionListener(this);
		add(systemPropertiesItem);
		graphicsEnvironmentItem = new MenuItem("Graphics Environment");
		graphicsEnvironmentItem.addActionListener(this);
		add(graphicsEnvironmentItem);
		aboutItem = new MenuItem("About PXLab");
		aboutItem.addActionListener(this);
		add(aboutItem);
	}

	public void actionPerformed(ActionEvent e) {
		MenuItem source = (MenuItem) e.getSource();
		if (source == aboutItem) {
			new AboutDialog();
		} else if (source == systemPropertiesItem) {
			new SystemPropertiesDialog(parent);
		} else if (source == graphicsEnvironmentItem) {
			new GraphicsEnvironmentDialog(parent);
		}
	}
}
