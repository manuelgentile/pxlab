package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.pxl.*;

/**
 * This class creates a panel for the display of numeric color coordinates in 3
 * text fields and which also may be used to enter coordinates values.
 * 
 * @version 0.3.0
 */
/*
 * 
 * 04/02/00
 * 
 * 2006/10/15 allow for non-editable text fields.
 */
public class ColorCoordinatesPanel extends Panel implements
		ColorChangeListener, ActionListener {
	protected boolean editable;
	private TextField aNumericField, bNumericField, cNumericField;
	private int tfw = 6;
	private int csType;
	private String name;
	private boolean initialState;
	private ColorServer colorServer;

	/**
	 * Create a coordinates panel which handles color coordinates related to the
	 * XYZ space by the given transform.
	 * 
	 * @param cs
	 *            the color space id which relates this panel's coordinates to
	 *            the XYZ-space.
	 * @param istate
	 *            the initial state flag of this panel. If this is true then
	 *            this panel should be shown initially.
	 * @param ct
	 *            the ColorServer object which communicates with this panel.
	 */
	public ColorCoordinatesPanel(int cs, boolean istate, ColorServer ct) {
		setLayout(new GridLayout(1, 4, PXLabGUI.smallInternalElementGap, 0));
		csType = cs;
		name = PxlColor.getShortName(csType);
		initialState = istate;
		colorServer = ct;
		editable = (colorServer != null);
		setBackground(PXLabGUI.background);
		Label label = new Label(name, Label.RIGHT);
		aNumericField = new TextField(tfw);
		aNumericField.addActionListener(this);
		aNumericField.setEditable(editable);
		bNumericField = new TextField(tfw);
		bNumericField.addActionListener(this);
		bNumericField.setEditable(editable);
		cNumericField = new TextField(tfw);
		cNumericField.addActionListener(this);
		cNumericField.setEditable(editable);
		add(label);
		add(aNumericField);
		add(bNumericField);
		add(cNumericField);
		if (colorServer != null)
			colorServer.addColorChangeListener(this);
	}

	/**
	 * Return the name of the transformation displayed in this panel.
	 */
	public String getName() {
		return (name);
	}

	/**
	 * Return the initial state of this panel. If the initial state is true then
	 * this panel is displayed on startup.
	 */
	public boolean getInitialState() {
		return (initialState);
	}

	/**
	 * This implementation of the ColorAdjustmentListener interface updates the
	 * numeric panels whenever the current color has changed.
	 */
	public void colorChanged(ColorChangeEvent e) {
		int id = e.getID();
		if (id == ColorChangeEvent.COLOR_SET_PASSIVE) {
			aNumericField.setEnabled(false);
			bNumericField.setEnabled(false);
			cNumericField.setEnabled(false);
		} else if (id == ColorChangeEvent.COLOR_SET) {
			aNumericField.setEnabled(true);
			bNumericField.setEnabled(true);
			cNumericField.setEnabled(true);
		}
		// System.out.println(e);
		double[] a = e.getColor().transform(csType);
		aNumericField.setText(numericLabel(a[0], 3));
		bNumericField.setText(numericLabel(a[1], 3));
		cNumericField.setText(numericLabel(a[2], 3));
	}

	/**
	 * When any of the color coordinate fields is changed then the current color
	 * is updated.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			double a = Double.valueOf(aNumericField.getText()).doubleValue();
			double b = Double.valueOf(bNumericField.getText()).doubleValue();
			double c = Double.valueOf(cNumericField.getText()).doubleValue();
			double[] x = { a, b, c };
			PxlColor d = PxlColor.instance(csType, x);
			if (d.isDisplayable() && editable)
				colorServer.colorAdjusted(this, d);
		} catch (NumberFormatException ex) {
		}
	}

	/**
	 * Create a string representation of x with at most n decimal digits.
	 */
	private String numericLabel(double x, int n) {
		String s = null;
		if (n == 0) {
			s = String.valueOf(Math.round(x));
		} else {
			int precisionFactor = 1;
			for (int i = 0; i < n; i++)
				precisionFactor *= 10;
			// System.out.println(x + " pf= " + precisionFactor);
			x = (double) Math.round(precisionFactor * x)
					/ (double) precisionFactor;
			s = String.valueOf(x);
		}
		return (s);
	}
}
