package de.pxlab.pxl.display.editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import java.util.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.parser.*;

/**
 * A file menu for the display editor.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/24/03
 */
public class FileMenu extends Menu implements ActionListener {
	private MenuItem loadColorDeviceParametersItem;
	private MenuItem exitItem;
	private DisplayEditor parent;

	public FileMenu(DisplayEditor p) {
		super("File");
		parent = p;
		loadColorDeviceParametersItem = new MenuItem(
				"Load Color Device Parameters");
		loadColorDeviceParametersItem.addActionListener(this);
		add(loadColorDeviceParametersItem);
		add(new MenuItem("-"));
		exitItem = new MenuItem("Exit");
		exitItem.addActionListener(this);
		add(exitItem);
	}

	public void actionPerformed(ActionEvent e) {
		MenuItem source = (MenuItem) e.getSource();
		if (source == exitItem) {
			System.exit(0);
		} else if (source == loadColorDeviceParametersItem) {
			GlobalAssignments.load(" Color Device Parameter File");
			// PxlColor.init();
			parent.displayEditorPanel.setDisplay(parent.displayEditorPanel
					.getDisplay());
		}
	}
}
