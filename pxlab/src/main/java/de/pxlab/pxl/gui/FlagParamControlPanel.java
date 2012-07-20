package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * A control panel for experimental parameters which have string values.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 07/28/01
 */
public class FlagParamControlPanel extends SingleParamControlPanel implements
		ItemListener {
	private Checkbox flag;

	/**
	 * Create a StringParamControlPanel which sends its update messages to the
	 * given Component object and can adjust the ExPar object of the given
	 * Display.
	 * 
	 * @param dspp
	 *            this is the Component which shows the Display object and which
	 *            receives the repaint() messages whenver a parameter value
	 *            changes. This ExParControlPanel object sends a repaint message
	 *            to this Component whenever the parameter has changed.
	 * @param dsp
	 *            the Display object whose parameter is controlled here.
	 * @param exd
	 *            the ExParDescriptor object which describes the ExPar parameter
	 *            to be controlled.
	 */
	public FlagParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd) {
		super(dspp, dsp, exd);
		flag = new Checkbox(getParName(), exPar.getFlag());
		flag.addItemListener(this);
		add(flag, BorderLayout.NORTH);
	}

	/** The ItemListener object of this panel's flag input field. */
	public void itemStateChanged(ItemEvent e) {
		// System.out.println("StringParamControlPanel.actionPerformed(): " +
		// textField.getText());
		exParValue.set(flag.getState() ? 1 : 0);
		updateDisplay();
	}

	public void updateControl(ExParValue v) {
		flag.setState(v.getInt() == 1);
	}
}
