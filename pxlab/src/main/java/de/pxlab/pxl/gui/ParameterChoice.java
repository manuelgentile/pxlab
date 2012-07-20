package de.pxlab.pxl.gui;

import java.awt.Choice;

import de.pxlab.pxl.ExPar;

public class ParameterChoice extends Choice {
	private String[] allParNames;
	private int n;
	private static final String EMPTY = "";

	public ParameterChoice(String initial) {
		super();
		allParNames = ExPar.getAllParNames();
		int n = allParNames.length;
		for (int i = 0; i < n; i++) {
			add(allParNames[i]);
		}
		add(EMPTY);
		select((initial == null) ? EMPTY : initial);
	}

	public String getSelectedItem() {
		String slct = super.getSelectedItem();
		return slct.equals(EMPTY) ? null : slct;
	}
}
