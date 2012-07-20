package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.Base;
import de.pxlab.pxl.Topics;
import de.pxlab.pxl.gui.BigList;

/**
 * This little Applet is used to start the PXLab Display Editor from a HTML
 * page.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
public class DisplayEditorApplet extends Applet implements ActionListener {
	Button startButton;
	DisplayEditor displayEditor = null;
	String startLabel;
	boolean edit;

	public void init() {
		// Tell the system data base that we are an applet
		Base.setApplet(this);
		String md = getParameter("Mode");
		edit = (md != null) && md.startsWith("Ed");
		startLabel = edit ? "Start Display Editor"
				: "Start Vision Demonstrations";
		// showStatus("init()");
		setLayout(new GridLayout(1, 0));
		startButton = new Button(startLabel);
		startButton.setFont(new Font("SansSerif", Font.BOLD, 24));
		startButton.addActionListener(this);
		add(startButton);
		validate();
	}

	public void start() {
		// showStatus("start()");
	}

	public void stop() {
		// showStatus("stop()");
	}

	public void destroy() {
		if (displayEditor != null)
			displayEditor.dispose();
	}

	public void paint(Graphics g) {
	}

	public void actionPerformed(ActionEvent e) {
		if (displayEditor != null) {
			displayEditor.dispose();
			displayEditor = null;
			startButton.setLabel(startLabel);
		} else {
			de.pxlab.pxl.Base.setApplet(this);
			String options = getParameter("Options");
			String[] args = StringExt.stringArrayOfString(options);
			displayEditor = new DisplayEditor(new BigList(edit ? Topics.EXP
					: Topics.DEMO), args, edit);
			startButton.setLabel("Stop");
		}
	}
}
