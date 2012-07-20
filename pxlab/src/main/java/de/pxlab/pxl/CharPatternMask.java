package de.pxlab.pxl;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * A mask built of a matrix of letters.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 03/26/01
 */
public class CharPatternMask extends CharPattern {
	public CharPatternMask(ExPar i) {
		super(i);
		type = DisplayElement.CHAR_PATTERN_MASK;
	}

	public void show() {
		if (modified)
			prepareShow();
		graphics2D.setColor(colorPar.getDevColor());
		graphics2D.setFont(font);
		for (int i = 0; i < nrc; i++) {
			for (int k = 0; k < n; k++) {
				graphics2D.drawChars(letterChars, k, 1, xp[i] - xshift[k],
						yp[i]);
			}
		}
		followShow();
	}
}
