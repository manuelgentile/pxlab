package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.gui.*;
import de.pxlab.pxl.*;

/**
 * A control panel for numerical experimental parameters which can have a given
 * range of integer values.
 * 
 * @author H. Irtel
 * @version 0.1.3
 */
/*
 * 07/28/01
 * 
 * 05/21/02 fixed bugs when using magic numbers
 * 
 * 09/17/02 do not only show integer values but also set parameter value to an
 * integer
 * 
 * 12/17/02 ignore limits when using the text input field
 * 
 * 06/04/04 implements TextListener (pf)
 */
public class IntegerRangeParamControlPanel extends
		NumericalRangeParamControlPanel implements TextListener {
	private int lowerLimit;
	private int upperLimit;

	/**
	 * Create an IntegerRangeParamControlPanel which sends its update messages
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
	public IntegerRangeParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd) {
		this(dspp, dsp, exd, 0, 0);
	}

	/**
	 * Create a IntegerRangeParamControlPanel which sends its update messages to
	 * the given Component object and can adjust the ExPar object of the given
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
	 * @param min
	 *            minimum value for the numerical range.
	 * @param max
	 *            maximum value for the numerical range.
	 */
	public IntegerRangeParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd, int min, int max) {
		super(dspp, dsp, exd);
		if (max == min) {
			upperLimit = (int) exPar.getMaxValue();
			lowerLimit = (int) exPar.getMinValue();
		} else if (max == MagicNumber.screenBottomY) {
			upperLimit = (int) MagicNumber.limit(MagicNumber.screenBottomY);
			lowerLimit = (int) MagicNumber.limit(MagicNumber.screenTopY);
		} else if (max == MagicNumber.screenRightX) {
			upperLimit = (int) MagicNumber.limit(MagicNumber.screenRightX);
			lowerLimit = (int) MagicNumber.limit(MagicNumber.screenLeftX);
		} else if (max == MagicNumber.screenHeight) {
			upperLimit = (int) MagicNumber.limit(MagicNumber.screenHeight);
		} else if (max == (double) MagicNumber.screenWidth) {
			upperLimit = (int) MagicNumber.limit(MagicNumber.screenWidth);
		} else {
			upperLimit = max;
			lowerLimit = min;
		}
		int value = exPar.getInt();
		slider = new Slider(Slider.HORIZONTAL, new LinearAxisModel(
				MagicNumber.limit(lowerLimit), MagicNumber.limit(upperLimit),
				value), 0, 0);
		slider.setAxisListener(this);
		slider.setPreferredSpacing(24, 200, 24);
		slider.setLabelPrecision(0);
		add(slider, BorderLayout.CENTER);
		Panel tfp = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		textField = new TextField(String.valueOf(value), tfw);
		// textField.addActionListener(this);
		textField.addTextListener(this);
		// tfp.setBackground(Color.red);
		tfp.add(textField);
		add(tfp, BorderLayout.WEST);
	}

	/** The AxisListener method of this panel's Slider object. */
	public boolean axisValueChanged(double x) {
		int ix = (int) Math.round(x);
		exParValue.set(ix);
		textField.setText(String.valueOf(ix));
		updateDisplay();
		// System.out.println("axis displayPanel = " + displayPanel);
		return (true);
	}

	/* 06/04/2004 (pf) */
	/** The TextListener object of this panel's numeric input field. */
	public void textValueChanged(TextEvent e) {
		try {
			TextComponent tc = (TextComponent) e.getSource();
			int x = Integer.valueOf(tc.getText()).intValue();
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
	 * public void actionPerformed(ActionEvent e) {
	 * System.out.println("IntegerRangeParamControlPanel.actionPerformed(): " +
	 * textField.getText()); try { int x =
	 * Integer.valueOf(textField.getText()).intValue(); // if ((x >= lowerLimit)
	 * && (x <= upperLimit)) { exParValue.set(x); slider.setValue(x);
	 * updateDisplay(); // } } catch (NumberFormatException ee) { } }
	 */
	/** Update the visual control to new parameter value. */
	public void updateControl(ExParValue v) {
		// System.out.println("IntegerRangeParamControlPanel.updateControl() v= "
		// + v.toString());
		int x = v.getInt();
		textField.setText(String.valueOf(x));
		slider.setValue(x);
	}
}
