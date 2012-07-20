package de.pxlab.pxl;

import java.awt.*;

/**
 * A text input field with a label.
 * 
 * @version 0.1.2
 */
/*
 * 
 * 2001/07/11
 * 
 * 2007/06/14 fixed bug when clearing a character in antialiasing mode. This
 * kept the character outline. So we have to clear the complete string
 * rectangle. This should also be more efficient than repainting the character
 * with the background color.
 */
public class TextInputElement extends TextElement {
	protected String label = "";
	protected boolean focus = false;
	protected int gap = 0;

	public TextInputElement(ExPar i) {
		super(i);
	}

	public TextInputElement(ExPar i, String lb) {
		super(i);
		setLabel(lb);
	}

	public TextInputElement(ExPar i, String lb, String s) {
		super(i, s);
		setLabel(lb);
	}

	public void setLabel(String lb) {
		label = lb;
	}

	public String getLabel() {
		return label;
	}

	public void setFocus(boolean b) {
		focus = b;
	}

	public void setGap(int g) {
		gap = g;
	}
	private String recentLabel = "";
	private String recentText = "";
	private PxlColor pc;

	public void show() {
		clear(recentLabel, location.x - gap, location.y,
				PositionReferenceCodes.BASE_RIGHT);
		clear(focus ? (recentText + "_") : recentText, location.x, location.y,
				PositionReferenceCodes.BASE_LEFT);
		colorPar.set(colorPar.getPxlColor());
		show(label, location.x - gap, location.y,
				PositionReferenceCodes.BASE_RIGHT);
		recentLabel = label;
		show(focus ? (text + "_") : text, location.x, location.y,
				PositionReferenceCodes.BASE_LEFT);
		recentText = text;
	}

	/** Delete the last character of the text string if it exists. */
	public void deleteLastChar() {
		int n = text.length();
		if (n > 0) {
			text = text.substring(0, n - 1);
		}
	}

	/** Append the given character to the text string. */
	public void append(int c) {
		text = text + String.valueOf((char) c);
	}

	/** Return the text string as a double value. */
	public double getDoubleValue() {
		double x = 0.0;
		try {
			x = Double.valueOf(text).doubleValue();
		} catch (NumberFormatException nex) {
		}
		return x;
	}

	/** Return the text string as an integer value. */
	public int getIntegerValue() {
		int x = 0;
		try {
			x = Integer.valueOf(text).intValue();
		} catch (NumberFormatException nex) {
		}
		return x;
	}
}
