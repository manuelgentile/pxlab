package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.sound.*;

// import de.pxlab.pxl.gui.*;
public class SystemInfoMenu extends JMenu implements ActionListener {
	private JMenuItem userPropertiesItem;
	private JMenuItem systemPropertiesItem;
	private JMenuItem graphicsEnvironmentItem;
	private JMenuItem soundEnvironmentItem;
	private JMenuItem timingEnvironmentItem;
	private JMenuItem colorCalibrationItem;
	private JFrame parent;

	public SystemInfoMenu(JFrame p) {
		super("Info");
		parent = p;
		userPropertiesItem = new JMenuItem("User");
		userPropertiesItem.addActionListener(this);
		add(userPropertiesItem);
		systemPropertiesItem = new JMenuItem("Java System");
		systemPropertiesItem.addActionListener(this);
		add(systemPropertiesItem);
		graphicsEnvironmentItem = new JMenuItem("Graphics");
		graphicsEnvironmentItem.addActionListener(this);
		add(graphicsEnvironmentItem);
		colorCalibrationItem = new JMenuItem("Color");
		colorCalibrationItem.addActionListener(this);
		add(colorCalibrationItem);
		soundEnvironmentItem = new JMenuItem("Sound");
		soundEnvironmentItem.addActionListener(this);
		add(soundEnvironmentItem);
		timingEnvironmentItem = new JMenuItem("Timing");
		timingEnvironmentItem.addActionListener(this);
		add(timingEnvironmentItem);
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) e.getSource();
		if (source == userPropertiesItem) {
			new UserPropertiesDialog(parent);
		} else if (source == systemPropertiesItem) {
			new SystemPropertiesDialog(parent);
		} else if (source == graphicsEnvironmentItem) {
			new GraphicsEnvironmentDialog(parent);
		} else if (source == colorCalibrationItem) {
			new ColorCalibrationEnvironmentDialog(parent);
		} else if (source == soundEnvironmentItem) {
			new SoundEnvironmentDialog(parent);
		} else if (source == timingEnvironmentItem) {
			new TimingEnvironmentDialog(parent);
		}
	}
}
