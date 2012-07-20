package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * A control panel for numerical experimental parameters which can have a given
 * range of values.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 07/28/01
 * 
 * 05/21/02 fixed bugs when using magic numbers
 * 
 * 12/17/02 ignore limits when using text input
 */
public class NumericalRangeParamControlPanel extends SingleParamControlPanel
		implements AxisListener, TextListener {
	protected Slider slider;
	protected TextField textField;
	protected int tfw = 6;
	private double lowerLimit;
	private double upperLimit;

	/**
	 * Create a NumericalRangeParamControlPanel which sends its update messages
	 * to the given Component object and can adjust the ExPar object of the
	 * given Display.
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
	public NumericalRangeParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd) {
		this(dspp, dsp, exd, 0.0, 0.0);
	}

	/**
	 * Create a NumericalRangeParamControlPanel which sends its update messages
	 * to the given Component object and can adjust the ExPar object of the
	 * given Display.
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
	 * @param min
	 *            minimum value for the numerical range.
	 * @param max
	 *            maximum value for the numerical range.
	 */
	public NumericalRangeParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd, double min, double max) {
		super(dspp, dsp, exd);
		if (max == min) {
			upperLimit = exPar.getMaxValue();
			lowerLimit = exPar.getMinValue();
		} else if (max == (double) MagicNumber.screenBottomY) {
			upperLimit = MagicNumber.limit(MagicNumber.screenBottomY);
			lowerLimit = MagicNumber.limit(MagicNumber.screenTopY);
		} else if (max == (double) MagicNumber.screenRightX) {
			upperLimit = MagicNumber.limit(MagicNumber.screenRightX);
			lowerLimit = MagicNumber.limit(MagicNumber.screenLeftX);
		} else if (max == (double) MagicNumber.screenHeight) {
			upperLimit = MagicNumber.limit(MagicNumber.screenHeight);
		} else if (max == (double) MagicNumber.screenWidth) {
			upperLimit = MagicNumber.limit(MagicNumber.screenWidth);
		} else {
			upperLimit = max;
			lowerLimit = min;
		}
		double value = exPar.getDouble();
		slider = new Slider(Slider.HORIZONTAL, new LinearAxisModel(
				MagicNumber.limit(lowerLimit), MagicNumber.limit(upperLimit),
				value), 0, 0);
		slider.setAxisListener(this);
		slider.setPreferredSpacing(24, 200, 24);
		add(slider, BorderLayout.CENTER);
		Panel tfp = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		textField = new TextField(String.valueOf(value), tfw);
		textField.addTextListener(this);
		tfp.add(textField);
		add(tfp, BorderLayout.WEST);
		// setBackground(Color.blue);
	}

	/**
	 * Clear this panel by removing its listener objects and its sliders. This
	 * method must be called by this panel's parent before it clears itself.
	 */
	public void clearPanel() {
		// System.out.println("NumericalRangeParamControlPanel.clearPanel(): " +
		// exParName);
		if (slider != null) {
			slider.removeAxisListener();
		}
		removeAll();
		slider = null;
	}

	/** The AxisListener method of this panel's Slider object. */
	public boolean axisValueChanged(double x) {
		// System.out.println("NumericalRangeParameControlPanel.axisValueChanged(): "
		// + x);
		exParValue.set(x);
		textField.setText(String.valueOf((int) (x * 1000) / 1000.0));
		updateDisplay();
		// System.out.println("axis displayPanel = " + displayPanel);
		return (true);
	}

	/* 07/13/2004 (pf) */
	public void textValueChanged(TextEvent e) {
		try {
			TextComponent tc = (TextComponent) e.getSource();
			double x = Double.valueOf(tc.getText()).doubleValue();
			// System.out.println("NumericalRangeParameControlPanel.textValueChanged(): "
			// + x);
			if ((x >= lowerLimit) && (x <= upperLimit)) {
				exParValue.set(x);
				slider.setValue(x);
				updateDisplay();
			}
		} catch (NumberFormatException ee) {
		}
	}

	/**
	 * The ActionListener object of this panel's numeric input field.
	 */
	/*
	 * public void actionPerformed(ActionEvent e) { //
	 * System.out.println("NumericalRangeParamControlPanel.actionPerformed(): "
	 * + textField.getText()); try { double x =
	 * Double.valueOf(textField.getText()).doubleValue(); // if ((x >=
	 * lowerLimit) && (x <= upperLimit)) { exParValue.set(x);
	 * slider.setValue(x); updateDisplay(); // } } catch (NumberFormatException
	 * ee) { } }
	 */
	/** Update the visual control to new parameter value. */
	public void updateControl(ExParValue v) {
		double x = v.getDouble();
		textField.setText(String.valueOf((int) (x * 1000) / 1000.0));
		slider.setValue(x);
	}
}
