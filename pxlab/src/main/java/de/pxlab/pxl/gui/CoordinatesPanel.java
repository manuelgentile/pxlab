package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import de.pxlab.pxl.*;
import de.pxlab.gui.*;

/**
 * This class creates a panel containing several coordinate representations of a
 * color in different color spaces. It also provides a way to enter numeric
 * coordinates in the different color spaces and creates a menu which contains
 * checkable items for adding and removing color systems.
 * 
 * @author H. Irtel
 * @version 0.3.0
 * @see ColorCoordinatesPanel
 * @see ColorServer
 */
/*
 * 02/04/01 only a label change
 */
public class CoordinatesPanel extends Panel implements ItemListener {
	private Menu menu;
	private ArrayList systems;
	private Container controlPanel;

	/**
	 * Create a coordinates display panel which may contain all those systems
	 * indicated in colorSystems. The ColorServer is sent to the single
	 * coordinate panels to allow them to send color changes to the ColorServer
	 * and to register themselves as ColorChangeListeners. The CoordinatesPanel
	 * may be reconfigured by checking items in the menu generated here. After
	 * the panel has been reconfigured the Container which contains this panel
	 * has to be layed out.
	 */
	public CoordinatesPanel(int[] colorSystems, ColorServer cs, Container cp) {
		setLayout(new GridLayout(0, 1, 0, PXLabGUI.smallInternalElementGap));
		setBackground(PXLabGUI.background);
		// setBackground(Color.green);
		controlPanel = cp;
		menu = new Menu("Numerical coordinates fields");
		systems = new ArrayList();
		for (int i = 0; i < colorSystems.length; i++) {
			systems.add(new ColorCoordinatesPanel(colorSystems[i],
					colorSystems[i] == PxlColor.CS_Yxy
							|| colorSystems[i] == PxlColor.CS_LabStar, cs));
		}
		CheckboxMenuItem item;
		for (int i = 0; i < systems.size(); i++) {
			ColorCoordinatesPanel p = (ColorCoordinatesPanel) systems.get(i);
			// System.out.println("Adding " + p.getName());
			item = new CheckboxMenuItem(p.getName(), p.getInitialState());
			item.addItemListener(this);
			menu.add(item);
			if (p.getInitialState())
				add((ColorCoordinatesPanel) systems.get(i));
		}
	}

	public Menu getOptionsMenu() {
		return (menu);
	}

	/**
	 * This ItemListener responds to color system selection menu item checking.
	 */
	public void itemStateChanged(ItemEvent e) {
		CheckboxMenuItem source = (CheckboxMenuItem) e.getSource();
		String item = source.getLabel();
		// System.out.println("Activate System " + item);
		for (int i = 0; i < systems.size(); i++) {
			if (item.equals(((ColorCoordinatesPanel) systems.get(i)).getName())) {
				// System.out.println("Found System (" + i + "): " + item);
				if (source.getState()) {
					add((ColorCoordinatesPanel) systems.get(i));
				} else {
					remove((ColorCoordinatesPanel) systems.get(i));
				}
				// Force the container which holds this panel to
				// validate its layout
				controlPanel.validate();
				controlPanel.repaint();
				break;
			}
		}
	}
}
