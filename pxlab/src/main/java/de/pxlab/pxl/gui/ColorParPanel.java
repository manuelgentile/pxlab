package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import de.pxlab.pxl.*;

/**
 * The color parameter panel shows a display's set of color parameters. It may
 * also be used to select the active color.
 * 
 * @version 0.3.0
 */
/*
 * 
 * 2006/11/28 changed to Swing
 */
public class ColorParPanel extends ColorButtonPanel implements
		ColorChangeListener {
	private ColorParServer colorServer;
	private ExParDescriptor[] exPars;

	/**
	 * Create a view onto a display's set of color parameters. This is an
	 * alternative way to select the display's active color. This is why we need
	 * to have access to the color server.
	 * 
	 * @param cs
	 *            the currently active color server.
	 */
	public ColorParPanel(ColorParServer cs) {
		super(2);
		colorServer = cs;
		colorServer.addColorChangeListener(this);
	}

	/**
	 * Configure the view for a given display.
	 * 
	 * @param dsp
	 *            the currently active display whose color parameters should be
	 *            shown.
	 */
	public void configureFor(Display dsp) {
		exPars = dsp.getColorPars();
		removeAll();
		ExPar cp = colorServer.getColorPar();
		for (int i = 0; i < exPars.length; i++) {
			ExPar p = exPars[i].getValue();
			String n = exPars[i].getName();
			if (p.getType() != ExParTypeCodes.COLOR)
				n = "[" + n + "]";
			addButton(exPars[i], p.getPxlColor(), n, p.equals(cp));
		}
		// System.out.println("ColorParPanel.configureFor() " + dsp.getTitle());
		// for (int i = 0; i < exPars.length; i++) System.out.println("exPars["
		// + i + "] = " + exPars[i]);
	}

	/** Implements the ColorAdjustmentListener interface. */
	public void colorChanged(ColorChangeEvent e) {
		updateColors();
	}

	private void updateColors() {
		if (exPars != null && exPars.length > 0) {
			ExPar cp = colorServer.getColorPar();
			for (int i = 0; i < exPars.length; i++) {
				ExPar p = exPars[i].getValue();
				setButtonColor(i, p.getPxlColor(), p.equals(cp));
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		int i = indexOfButton(source);
		if (i >= 0) {
			colorServer.setColorPar(this, exPars[i].getValue());
		}
		updateColors();
	}
	/*
	 * public void paint(Graphics g) {
	 * System.out.println("ColorParPanel.paint() Panel is " + (isValid()?
	 * "valid": "invalid")); super.paint(g); }
	 * 
	 * 
	 * public void invalidate() {
	 * System.out.println("ColorParPanel.invalidate()"); super.invalidate(); }
	 */
}
