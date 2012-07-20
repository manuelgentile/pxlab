package de.pxlab.pxl;

import java.awt.*;

/**
 * A matrix of letters.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 03/26/01
 */
public class CharPattern extends DisplayElement {
	/** Text content of text objects. */
	protected String letters;
	/** The Font for this text object. */
	protected Font font = null;
	protected int rows = 1;
	protected int columns = 1;
	protected int cursorRow = -1;
	protected int cursorCol = -1;

	public CharPattern(ExPar i) {
		colorPar = i;
		setSize(300, 300);
		setColumns(3);
		type = DisplayElement.CHAR_PATTERN;
	}

	public void setCursor(int row, int col) {
		cursorRow = row;
		cursorCol = col;
		modified = true;
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
	public void setLetters(String t) {
		letters = t;
		modified = true;
	}

	/** Get a TEXT object's text string. */
	public String getLetters() {
		return (letters);
	}

	public void setProperties(String lt, int rws, int cls, int x, int y, int w,
			int h, String ff, int fst, int fs) {
		setLetters(lt);
		setRows(rws);
		setColumns(cls);
		setLocation(x, y);
		setSize(w, h);
		setFont(ff, fst, fs);
		// prepareShow();
	}
	protected FontMetrics fm;
	protected int fma;
	protected int n;
	protected char[] letterChars;
	protected int[] xshift;
	protected int dx;
	protected int dy;
	protected int w;
	protected int h;
	protected int x0;
	protected int y0;
	protected int nrc;
	protected int[] xp;
	protected int[] yp;

	protected void prepareShow() {
		graphics2D.setFont(font);
		fm = graphics2D.getFontMetrics(font);
		fma = fm.getAscent();
		letterChars = letters.toCharArray();
		n = letterChars.length;
		xshift = new int[n];
		for (int i = 0; i < n; i++)
			xshift[i] = fm.charWidth(letterChars[i]) / 2;
		if (columns == 1) {
			dx = 0;
		} else {
			dx = (size.width) / (columns - 1);
		}
		dy = (size.height) / (rows);
		w = dx * (columns - 1);
		h = dy * rows;
		x0 = location.x - w / 2;
		y0 = location.y - (dy * ((rows + 1) / 2)) + dy;
		nrc = rows * columns;
		xp = new int[nrc];
		yp = new int[nrc];
		int x;
		int y = y0;
		int k = 0;
		for (int i = 0; i < rows; i++) {
			x = x0;
			for (int j = 0; j < columns; j++) {
				xp[k] = x;
				yp[k++] = y;
				x += dx;
			}
			y += dy;
		}
		// graphics2D.drawLine(location.x-200, location.y, location.x+200,
		// location.y);
		// graphics2D.drawLine(location.x, location.y-200, location.x,
		// location.y+200);
		modified = false;
	}

	public void show() {
		// System.out.println("CharPattern.show()");
		if (modified)
			prepareShow();
		graphics2D.setColor(colorPar.getDevColor());
		graphics2D.setFont(font);
		int mx = (nrc < n) ? nrc : n;
		for (int i = 0; i < mx; i++) {
			graphics2D.drawChars(letterChars, i, 1, xp[i] - xshift[i], yp[i]);
		}
		followShow();
	}

	protected void followShow() {
		// System.out.println("Showing " + getClass().getName());
		if (cursorRow >= 0) {
			// System.out.println("   Cursor ");
			char[] crs = new char[1];
			crs[0] = '_';
			graphics2D.drawChars(crs, 0, 1,
					x0 + cursorCol * dx - fm.charWidth(crs[0]) / 2, y0
							+ cursorRow * dy);
		}
		if (fm == null)
			fm = graphics2D.getFontMetrics();
		int fw = font.getSize() / 2;
		setBounds(new Point(x0 - fw, y0 - dy), new Dimension(w + fw + fw, h));
	}

	public Point getLetterLocation(int row, int column) {
		int i = row * columns + column;
		// System.out.println("x="+xp[i]+", y="+yp[i]);
		if ((xp == null) || (yp == null) || (xp.length < (i + 1)))
			prepareShow();
		return (new Point(xp[i], yp[i]));
	}

	public int getAscent() {
		if ((xp == null) || (yp == null))
			prepareShow();
		return fma;
	}
}
