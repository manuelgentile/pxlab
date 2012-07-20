package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * The superclass of all panels to define the value of an experimental
 * parameter.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 07/30/01
 * 
 * 02/13/02 allow hints to be switched on/off
 */
abstract public class SingleParamControlPanel extends InsetPanel {
	private Component displayPanel;
	private Display display;
	protected ExPar exPar;
	protected int exParType;
	protected String exParName;
	protected ExParValue exParValue;

	/**
	 * Create an ExParControlPanel which sends its update messages to the given
	 * Component object and can control the ExPar object which is described by
	 * the given ExParDescriptor for the given Display object.
	 * 
	 * @param dspp
	 *            this is the Component which shows the Display object and which
	 *            receives the repaint() messages whenver a parameter value
	 *            changes. This ExParControlPanel object sends a repaint message
	 *            to this Component whenever the parameter has changed. This
	 *            parameter may be null if no visible Display is to be modified.
	 * @param dsp
	 *            the Display object whose parameter is controlled here. This
	 *            parameter may be null if no visible Display is to be modified.
	 * @param exd
	 *            the ExParDescriptor object which describes the ExPar parameter
	 *            to be controlled.
	 */
	public SingleParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd) {
		super(new BorderLayout(), new Insets(PXLabGUI.smallInternalElementGap,
				0 /* PXLabGUI.internalBorder */, 0 /*
												 * PXLabGUI.smallInternalElementGap
												 */, 0 /*
														 * PXLabGUI.internalBorder
														 */));
		displayPanel = dspp;
		display = dsp;
		exPar = exd.getValue();
		exParName = exd.getName();
		exParValue = exPar.getValue();
		exParType = exPar.getType();
		if (Debug.layout())
			setBackground(Color.green);
	}

	/** Update the visual control to new parameter values. */
	abstract public void updateControl(ExParValue v);

	public String getParName() {
		return exParName;
	}

	public ExParValue getParValue() {
		return exParValue;
	}

	protected void updateDisplay() {
		// System.out.println("ExParControlPanel.updateDisplay() " + exParName +
		// "=" + exPar.toString());
		// new RuntimeException().printStackTrace();
		if (display != null) {
			display.recomputeGeometry();
			display.recomputeTiming();
		}
		if (displayPanel != null) {
			displayPanel.repaint();
		}
	}

	/** Remove all components from this panel. */
	public void clearPanel() {
		removeAll();
	}

	public void closeOK() {
	}

	public void closeCancel() {
	}
}
