package de.pxlab.pxl.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.Base;
import de.pxlab.pxl.DisplayDevice;

/**
 * A menu for setting display device properties.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see DisplayDevice
 */
/*
 * 
 * 2004/10/01
 */
public class DisplayDeviceOptionsMenu extends JMenu implements ItemListener {
	protected JRadioButtonMenuItem framedWindowItem, unframedWindowItem,
			fullScreenItem, fullScreenExclusiveItem, secondaryScreenItem,
			secondaryScreenExclusiveItem, dualFramedWindowItem,
			dualUnframedWindowItem, dualFullScreenItem,
			dualFullScreenExclusiveItem;
	protected ButtonGroup displayDeviceTypeButtonGroup;

	public DisplayDeviceOptionsMenu() {
		super("Display Device");
		// Display Type Options
		displayDeviceTypeButtonGroup = new ButtonGroup();
		framedWindowItem = new JRadioButtonMenuItem("Framed Window",
				Base.getDisplayDeviceType() == DisplayDevice.FRAMED_WINDOW);
		framedWindowItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(framedWindowItem);
		unframedWindowItem = new JRadioButtonMenuItem("Unframed Window",
				Base.getDisplayDeviceType() == DisplayDevice.UNFRAMED_WINDOW);
		unframedWindowItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(unframedWindowItem);
		fullScreenItem = new JRadioButtonMenuItem("Full Screen",
				Base.getDisplayDeviceType() == DisplayDevice.FULL_SCREEN);
		fullScreenItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(fullScreenItem);
		fullScreenExclusiveItem = new JRadioButtonMenuItem(
				"Full Screen Exclusive",
				Base.getDisplayDeviceType() == DisplayDevice.FULL_SCREEN_EXCLUSIVE);
		fullScreenExclusiveItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(fullScreenExclusiveItem);
		secondaryScreenItem = new JRadioButtonMenuItem(
				"Full Secondary Screen",
				Base.getDisplayDeviceType() == DisplayDevice.FULL_SECONDARY_SCREEN);
		secondaryScreenItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(secondaryScreenItem);
		secondaryScreenExclusiveItem = new JRadioButtonMenuItem(
				"Full Secondary Screen Exclusive",
				Base.getDisplayDeviceType() == DisplayDevice.FULL_SECONDARY_SCREEN_EXCLUSIVE);
		secondaryScreenExclusiveItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(secondaryScreenExclusiveItem);
		dualFramedWindowItem = new JRadioButtonMenuItem("Dual Framed Windows",
				Base.getDisplayDeviceType() == DisplayDevice.DUAL_FRAMED_WINDOW);
		dualFramedWindowItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(dualFramedWindowItem);
		dualUnframedWindowItem = new JRadioButtonMenuItem(
				"Dual Unframed Windows",
				Base.getDisplayDeviceType() == DisplayDevice.DUAL_FRAMED_WINDOW);
		dualUnframedWindowItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(dualUnframedWindowItem);
		dualFullScreenItem = new JRadioButtonMenuItem("Full Screen Stereo",
				Base.getDisplayDeviceType() == DisplayDevice.DUAL_FULL_SCREEN);
		dualFullScreenItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(dualFullScreenItem);
		dualFullScreenExclusiveItem = new JRadioButtonMenuItem(
				"Full Screen Exclusive Stereo",
				Base.getDisplayDeviceType() == DisplayDevice.DUAL_FULL_SCREEN_EXCLUSIVE);
		dualFullScreenExclusiveItem.addItemListener(this);
		displayDeviceTypeButtonGroup.add(dualFullScreenExclusiveItem);
		add(framedWindowItem);
		add(unframedWindowItem);
		add(fullScreenItem);
		add(fullScreenExclusiveItem);
		add(secondaryScreenItem);
		add(secondaryScreenExclusiveItem);
		add(dualFramedWindowItem);
		add(dualUnframedWindowItem);
		add(dualFullScreenItem);
		add(dualFullScreenExclusiveItem);
	}

	public void itemStateChanged(ItemEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		// System.out.println(source.getText());
		if (source == framedWindowItem) {
			Base.setDisplayDeviceType(DisplayDevice.FRAMED_WINDOW);
		} else if (source == unframedWindowItem) {
			Base.setDisplayDeviceType(DisplayDevice.UNFRAMED_WINDOW);
		} else if (source == fullScreenItem) {
			Base.setDisplayDeviceType(DisplayDevice.FULL_SCREEN);
		} else if (source == fullScreenExclusiveItem) {
			Base.setDisplayDeviceType(DisplayDevice.FULL_SCREEN_EXCLUSIVE);
		} else if (source == secondaryScreenItem) {
			Base.setDisplayDeviceType(DisplayDevice.FULL_SECONDARY_SCREEN);
		} else if (source == secondaryScreenExclusiveItem) {
			Base.setDisplayDeviceType(DisplayDevice.FULL_SECONDARY_SCREEN_EXCLUSIVE);
		} else if (source == dualFramedWindowItem) {
			Base.setDisplayDeviceType(DisplayDevice.DUAL_FRAMED_WINDOW);
		} else if (source == dualUnframedWindowItem) {
			Base.setDisplayDeviceType(DisplayDevice.DUAL_UNFRAMED_WINDOW);
		} else if (source == dualFullScreenItem) {
			Base.setDisplayDeviceType(DisplayDevice.DUAL_FULL_SCREEN);
		} else if (source == dualFullScreenExclusiveItem) {
			Base.setDisplayDeviceType(DisplayDevice.DUAL_FULL_SCREEN_EXCLUSIVE);
		}
	}

	public void setDualScreenOptionsEnabled(boolean t) {
		dualFramedWindowItem.setEnabled(t);
		dualUnframedWindowItem.setEnabled(t);
		dualFullScreenItem.setEnabled(t);
		dualFullScreenExclusiveItem.setEnabled(t);
	}
}
