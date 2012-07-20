package de.pxlab.pxl;

import java.awt.*;

/**
 * A pattern made up of a single letter. The pattern rows are defined by the
 * binary code in an integer array. Positioning works fine for capital letter
 * characters.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 01/24/03
 */
public class SingleCharPattern extends DisplayElement {
	/** Binary pattern rows. */
	protected int[] pattern = { 14, 17, 10, 4 };
	protected int rows = 4;
	protected int columns = 5;
	/** The letter used to draw the pattern. */
	protected char[] letter = { 'O' };
	/** The Font for this text object. */
	protected Font font = null;

	public SingleCharPattern(ExPar i) {
		colorPar = i;
		type = DisplayElement.CHAR_PATTERN;
	}

	public void setColumns(int n) {
		columns = n;
		modified = true;
	}

	public int getColumns() {
		return (columns);
	}

	public void setRows(int n) {
		rows = n;
		modified = true;
	}

	public int getRows() {
		return (rows);
	}

	/** Set a TEXT object's font. */
	public void setFont(Font fnt) {
		font = fnt;
		modified = true;
	}

	/**
	 * Set the font of this text element if the new font properties are
	 * different from the current font.
	 */
	public void setFont(String fn, int ft, int fs) {
		if ((font == null) || (!(font.getName().equals(fn)))
				|| (font.getStyle() != ft) || (font.getSize() != fs)) {
			// System.out.println("Setting font family " + fn + " using style "
			// + ft + " at size " + fs + "pt");
			font = new Font(fn, ft, fs);
			modified = true;
		}
	}

	/** Get a TEXT object's font. */
	public Font getFont() {
		return (font);
	}

	/** Set a TEXT object's text string. */
	public void setPattern(int[] p) {
		pattern = p;
		modified = true;
	}

	/** Set a TEXT object's text string. */
	public void setLetter(int t) {
		letter[0] = (char) t;
		modified = true;
	}

	/** Get a TEXT object's text string. */
	public int getLetter() {
		return ((int) letter[0]);
	}

	public void setProperties(int[] p, int lt, int rws, int cls, int x, int y,
			String ff, int fst, int fs) {
		setPattern(p);
		setLetter(lt);
		setRows(rws);
		setColumns(cls);
		setLocation(x, y);
		setFont(ff, fst, fs);
	}

	public void show() {
		graphics2D.setColor(colorPar.getDevColor());
		graphics2D.setFont(font);
		FontMetrics fm = graphics2D.getFontMetrics(font);
		int fma = fm.getAscent();
		int fmh = fma /* + fm.getDescent() */;
		int cw = fm.charWidth(letter[0]);
		int w = cw * columns;
		int h = fmh * rows;
		int x0 = location.x - w / 2;
		int y0 = location.y - h / 2 + fma;
		int x;
		int y = y0;
		for (int i = 0; i < rows; i++) {
			int m = 1;
			int p = pattern[i];
			x = x0;
			for (int j = 0; j < columns; j++) {
				if ((m & p) != 0) {
					graphics2D.drawChars(letter, 0, 1, x, y);
				}
				x += cw;
				m = (m << 1);
			}
			y += fmh;
		}
		setBounds(x0, y0 - fma, w, h);
	}
}
