package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.DisplaySupport;
import de.pxlab.pxl.Topics;

/**
 * Creates a hierarchical menu to select a Display object. When a Display object
 * is selected then the DisplaySelectionListener is sent the selected Display.
 * 
 * @version 0.1.2
 * @see DisplaySelectionListener
 */
/*
 * 
 * 2006/03/08 adapted to also handle data processing display objects.
 */
public class DisplaySelectionMenu extends Menu implements ActionListener {
	private DisplaySelectionListener displaySelector;
	private DisplayCollection bigList;

	public DisplaySelectionMenu(String title, DisplaySelectionListener ds) {
		this(title, new BigList(), ds);
	}

	/**
	 * Instantiate a multi-level menu which contains all groups of possible
	 * displays as submenus.
	 */
	public DisplaySelectionMenu(String title, DisplayCollection bl,
			DisplaySelectionListener ds) {
		super(title);
		bigList = bl;
		displaySelector = ds;
		if ((bigList.getTopicMask() & Topics.DATA) != 0) {
			// Assume we have data processing display objects which do
			// not have subgroups
			for (int i = 0; i < bigList.size(); i++) {
				DisplaySupport d = (DisplaySupport) bigList.get(i);
				MenuItem mi = new MenuItem(d.getTitle());
				mi.addActionListener(this);
				add(mi);
			}
		} else {
			// Assume we have experimental display objects and create
			// submenus with titles
			Menu[] subMenu = new Menu[Topics.topicDescription.length];
			for (int i = 0; i < Topics.topicDescription.length; i++) {
				subMenu[i] = new Menu(Topics.topicDescription[i]);
			}
			// Now create the menu items for the available displays and
			// enter them into their respective submenu
			for (int i = 0; i < bigList.size(); i++) {
				DisplaySupport d = (DisplaySupport) bigList.get(i);
				MenuItem mi = new MenuItem(d.getTitle());
				mi.addActionListener(this);
				subMenu[d.getTopic() % Topics.DEMO].add(mi);
			}
			for (int i = 0; i < Topics.topicDescription.length; i++) {
				if (subMenu[i].getItemCount() > 0)
					add(subMenu[i]);
			}
		}
	}

	/**
	 * Our ActionListener implementation. It is called whenever the user selects
	 * an item from the menu of available displays.
	 */
	public void actionPerformed(ActionEvent e) {
		MenuItem source = (MenuItem) e.getSource();
		String item = source.getLabel();
		for (int i = 0; i < bigList.size(); i++) {
			DisplaySupport dsp = (DisplaySupport) bigList.get(i);
			if (item.equals(dsp.getTitle())) {
				displaySelector.selectDisplay(dsp);
				// System.out.println("Select display " + i + ": " + item);
				break;
			}
		}
	}
}
