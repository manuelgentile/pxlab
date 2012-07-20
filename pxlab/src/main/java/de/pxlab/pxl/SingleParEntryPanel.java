package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;

/**
 * A single parameter panel for entering runtime parameter values. This is part
 * of the dialog which pops up immediately before an experimental session is
 * started if the design file defines runtime parameters.
 * 
 * @author H. Irtel
 * @version 0.1.4
 */
/*
 * 
 * 06/14/02 Fixed bug in choice situations with initial parameter values
 * 
 * 2005/01/24 for choice sets whose ExParValue is undefined use the first entry
 * as a default.
 */
public class SingleParEntryPanel extends Panel implements ActionListener,
		ItemListener {
	private String exParName;
	private ExPar exPar;
	private ExParValue exParValue;
	private String[] exParValueSet;
	private TextField textField;
	private Choice choice;
	private int tfw = 30;
	private RuntimeParsDialog controller;
	private Component component;

	/**
	 * Create an entry panel for a single parameter.
	 * 
	 * @param pn
	 *            name of the experimental parameter.
	 * @param values
	 *            an array of possible parameter values. If this is non-null
	 *            then we create a choice object to select a value. If this is
	 *            null then we create a text field to enter the parameter
	 *            directly.
	 * @param controller
	 *            the object which controls this entry panel. This object gets a
	 *            message whenever a parameter value has been entered.
	 */
	public SingleParEntryPanel(String pn, String[] values,
			RuntimeParsDialog controller) {
		exParName = pn;
		exPar = ExPar.get(exParName);
		exParValue = exPar.getValue();
		exParValueSet = values;
		this.controller = controller;
		setLayout(new GridLayout(1, 2, 20, 0));
		Font newFont = new Font("SansSerif", Font.BOLD, 16);
		String lbn = exPar.getHint();
		if (lbn == null)
			lbn = exParName;
		Label lb = new Label(lbn + ":", Label.RIGHT);
		lb.setFont(newFont);
		add(lb);
		// System.out.println("ExParValue at Input: " + exParValue);
		if (exParValueSet == null) {
			String init = exParValue.toString().trim();
			// Remove enclosing quotes if this is a string
			if (init.charAt(0) == '\"') {
				init = init.substring(1, init.length() - 1);
			}
			textField = new TextField(init, tfw);
			textField.setFont(newFont);
			textField.addActionListener(this);
			add(textField);
			component = textField;
		} else {
			choice = new Choice();
			choice.setFont(newFont);
			for (int i = 0; i < exParValueSet.length; i++) {
				choice.add(exParValueSet[i]);
			}
			choice.addItemListener(this);
			if (!exParValue.isUndefined()) {
				// System.out.println("  Selecting: " + exParValue.getString());
				choice.select(exParValue.getString());
			} else {
				choice.select(exParValueSet[0]);
			}
			exParValue.set(choice.getSelectedItem());
			add(choice);
			component = choice;
		}
		addFocusListener(new DebugFocusListener("SingleParEntryPanel"));
		component.addFocusListener(new DebugFocusListener(
				"SingleParEntryPanel." + component.getClass().getName()));
	}

	/** The action listener for text fields. */
	public void actionPerformed(ActionEvent e) {
		ExParValue v = ExParValue.runtimeParameterValue("\""
				+ textField.getText() + "\"");
		if (v != null) {
			exParValue.set(v);
			controller.parameterSet(exParName);
			// System.out.println("SingleParEntryPanel.actionPerformed() " +
			// exParName + "=" + v);
		}
	}

	/** The item listener for choice fields. */
	public void itemStateChanged(ItemEvent e) {
		exParValue.set(choice.getSelectedItem());
		controller.parameterSet(exParName);
		// System.out.println("SingleParEntryPanel.itemStateChanged() " +
		// exParName + "=" + choice.getSelectedItem());
	}

	public ExParValue getExParValue() {
		ExParValue ret = exParValue;
		if (textField != null) {
			ExParValue v = ExParValue.runtimeParameterValue("\""
					+ textField.getText() + "\"");
			if (v != null)
				ret = v;
		}
		// System.out.println("ExParValue at Output: " + ret);
		return ret;
	}

	public void setFocus() {
		component.requestFocus();
	}
}
