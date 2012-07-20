package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import de.pxlab.pxl.*;
import de.pxlab.pxl.gui.*;

/**
 * This class creates a panel containing several selectable coordinates
 * representations of a color in different color spaces.
 * 
 * @author H. Irtel
 * @version 0.2.2
 */
/*
 * 02/06/01 added setEnabled()
 * 
 * 09/17/02 make button panel adjust the number of rows according to how many
 * buttons are needed.
 */
public class ChartPanel extends Panel implements ActionListener {
	Panel buttonPanel;
	Panel cardPanel;
	ArrayList capList = new ArrayList();

	/**
	 * Create a color charts display panel containing charts for the color
	 * systems listed in colorSystems and using the ColorServer cs.
	 */
	public ChartPanel(int[] colorSystems, ColorServer cs, ColorClipBoard cb) {
		cardPanel = new Panel(new CardLayout());
		buttonPanel = new Panel(new GridLayout(0, 4));
		ColorAdjustmentPanel cap;
		for (int i = 0; i < colorSystems.length; i++) {
			cap = ColorAdjustmentPanel.instanceOf(colorSystems[i], cs);
			if (cap != null) {
				if ((cb != null) && (cap instanceof ColorAdjustmentChart))
					cb.addColorClipBoardListener((ColorAdjustmentChart) cap);
				addChart(cap, cs);
			}
		}
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(cardPanel, BorderLayout.CENTER);
		setBackground(PXLabGUI.background);
	}

	private void addChart(ColorAdjustmentPanel cap, ColorServer cs) {
		// System.out.println("Adding system " + cap.getName());
		cs.addColorChangeListener(cap);
		cardPanel.add(cap, cap.getName());
		Button b = new Button(cap.getName());
		b.addActionListener(this);
		buttonPanel.add(b);
		capList.add(cap);
	}

	public void setEnabled(boolean e) {
		for (int i = 0; i < capList.size(); i++)
			((ColorAdjustmentPanel) capList.get(i)).setEnabled(e);
	}

	/**
	 * This ActionListener responds to color system selection menu item
	 * checking.
	 */
	public void actionPerformed(ActionEvent e) {
		CardLayout cl = (CardLayout) cardPanel.getLayout();
		cl.show(cardPanel, e.getActionCommand());
	}
}
