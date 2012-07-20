package de.pxlab.util;

import java.text.*;
import java.util.Locale;

public class FixedWidthFormat {
	private String space;
	private int width;
	private NumberFormat form;

	/**
	 * Create a pattern of the form "######.000".
	 * 
	 * @param width
	 *            total width of the format pattern.
	 * @param fract
	 *            number of fractional digits.
	 */
	public FixedWidthFormat(int width, int fract) {
		this.width = width;
		char[] n = new char[width];
		for (int i = 0; i < (width - fract - 1); i++)
			n[i] = '#';
		if (fract > 0) {
			n[width - fract - 1] = '.';
			for (int i = 0; i < fract; i++)
				n[width - 1 - i] = '0';
			form = NumberFormat.getNumberInstance(Locale.US);
			if (form instanceof DecimalFormat) {
				((DecimalFormat) form).applyPattern(new String(n));
			}
		} else {
			n[width - fract - 1] = '#';
			form = NumberFormat.getNumberInstance(Locale.US);
			if (form instanceof DecimalFormat) {
				((DecimalFormat) form).applyPattern(new String(n));
			}
		}
		n = new char[width];
		for (int i = 0; i < width; i++)
			n[i] = ' ';
		space = new String(n);
	}

	/**
	 * Use this format to create a String representation of the given double.
	 * 
	 * @param x
	 *            the number which should be formatted.
	 * @return a formatted string.
	 */
	public String format(double x) {
		String s = space + form.format(x);
		int k = s.length();
		return s.substring(k - width);
	}
}
