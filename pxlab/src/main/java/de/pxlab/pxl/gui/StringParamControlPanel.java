package de.pxlab.pxl.gui;

import java.awt.*;
import java.awt.event.*;

import de.pxlab.awtx.*;
import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * A control panel for experimental parameters which have string values.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 07/28/01
 * 
 * 02/15/02 introduced closeOK() method
 */
public class StringParamControlPanel extends ExpressionParamControlPanel {
	public StringParamControlPanel(Component dspp, Display dsp,
			ExParDescriptor exd, boolean cnt) {
		super(dspp, dsp, exd, cnt);
	}

	/**
	 * Transform the value of the experimental parameter to a string which will
	 * be entered into the editing text field. THis mainly removes the enclosing
	 * quotes from the result of the ExParValue.toString() method.
	 */
	protected String stringOf(ExParValue v) {
		String s = v.toString();
		/*
		 * can't happen if (s == null) return "";
		 */
		int k = s.length();
		if (k < 2)
			return s;
		if (k == 2)
			return "";
		return s.substring(1, s.length() - 1);
	}

	/**
	 * Transform the string contained in the text field to a string which can be
	 * the value of the experimental parameter.
	 */
	protected ExParValue valueOf(String v) {
		String s = StringExt.unquote("\"" + v + "\"");
		return new ExParValue(s);
	}
}
