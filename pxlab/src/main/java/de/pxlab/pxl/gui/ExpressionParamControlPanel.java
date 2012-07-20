package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import de.pxlab.awtx.*;
import de.pxlab.util.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.parser.*;

/**
 * A control panel for experimental parameters which have an expression value
 * including array values.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 2007/01/26
 */
public class ExpressionParamControlPanel extends SingleParamControlPanel
		implements ActionListener, TextListener {
	private TextField textField;
	private boolean continuous;

	/**
	 * Create a ExpressionParamControlPanel which sends its update messages to
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
	 */
	public ExpressionParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd, boolean cnt) {
		super(dspp, dsp, exd);
		Panel tfp = new Panel(new BorderLayout());
		// tfp.setBackground(Color.yellow);
		textField = new TextField(stringOf(exPar.getValue()));
		textField.addActionListener(this);
		textField.addTextListener(this);
		continuous = cnt;
		tfp.add(textField, BorderLayout.NORTH);
		add(tfp, BorderLayout.CENTER);
		// setBackground(Color.green);
	}

	/**
	 * Tell this panel that it will be closed immediately with an OK button.
	 */
	public void closeOK() {
		// System.out.println("StringParamControlPanel.closeOK(): " +
		// textField.getText());
		exParValue.set(valueOf(textField.getText()));
		updateDisplay();
	}

	/**
	 * The ActionListener object of this panel's text input field. This is
	 * called whenever the RETURN key is pressed in the text field.
	 */
	public void actionPerformed(ActionEvent e) {
		// System.out.println("ExpressionParamControlPanel.actionPerformed(): "
		// + textField.getText());
		exParValue.set(valueOf(textField.getText()));
		updateDisplay();
	}

	/**
	 * The TextListener object of this panel's text input field. This is called
	 * whenever the text has been changed.
	 */
	public void textValueChanged(TextEvent e) {
		if (continuous) {
			// System.out.println("ExpressionParamControlPanel.textValueChanged(): "
			// + textField.getText());
			exParValue.set(valueOf(textField.getText()));
			updateDisplay();
		}
	}

	/** Update the visual control to new parameter value. */
	public void updateControl(ExParValue v) {
		textField.setText(stringOf(v));
	}

	/**
	 * Transform the value of the experimental parameter to a string which will
	 * be entered into the editing text field. THis mainly removes the enclosing
	 * quotes from the result of the ExParValue.toString() method.
	 */
	protected String stringOf(ExParValue v) {
		return v.toString();
	}

	/**
	 * Transform the string contained in the text field to a string which can be
	 * the value of the experimental parameter.
	 */
	protected ExParValue valueOf(String text) {
		ExParValue v = null;
		try {
			// ExDesignTreeParser parser = new ExDesignTreeParser(new
			// StringReader(text.getText()), Base.getEncoding());
			ExDesignTreeParser parser = new ExDesignTreeParser(
					new StringReader(text));
			v = parser.assignableParameterValue();
		} catch (ParseException pex) {
			new de.pxlab.pxl.NonFatalError(pex.getMessage());
		} catch (TokenMgrError tex) {
			new de.pxlab.pxl.NonFatalError(tex.getMessage());
		}
		return v;
	}
}
