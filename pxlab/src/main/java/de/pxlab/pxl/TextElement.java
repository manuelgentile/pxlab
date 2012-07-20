package de.pxlab.pxl;

import java.awt.*;

import java.util.ArrayList;
import de.pxlab.util.StringExt;

/**
 * A single line of text.
 * 
 * @version 0.3.1
 */
/*
 * 
 * 01/26/01 use an ExPar for color 03/15/01 made reference point and lineskip
 * factor object variables 03/23/01 added line breaking features
 * 
 * 2007/06/14 added clear() for clearing a piece of text.
 */
public class TextElement extends DisplayElement implements
		PositionReferenceCodes {
	/**
	 * Text content of this text element. This is defined to be an array here in
	 * order to be able to extend it to the TextParagraphElement.
	 */
	protected String text;
	/** The Font for this text object. */
	protected Font font = null;
	/** Reference point for text positioning. */
	protected int referencePoint = BASE_CENTER;

	public TextElement(ExPar i) {
		colorPar = i;
	}

	public TextElement(ExPar i, String s) {
		colorPar = i;
		text = s;
	}

	public void show() {
		show(text, location.x, location.y, referencePoint);
	}

	protected void show(String txt, int x, int y, int refPoint) {
		// System.out.println("TextElement.show() " + txt);
		Point p = position(txt, x, y, refPoint);
		graphics2D.setColor(colorPar.getDevColor());
		graphics2D.setFont(font);
		if (Debug.isActive(Debug.TEXTPOS)) {
			graphics2D.drawLine(p.x - 200, p.y, p.x + 200, p.y);
			graphics2D.drawLine(p.x, p.y - 200, p.x, p.y + 200);
		}
		graphics2D.drawString(txt, p.x, p.y);
		if (Debug.isActive(Debug.TEXTPOS)) {
			Rectangle bounds = getBounds();
			graphics2D
					.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	protected void clear(String txt, int x, int y, int refPoint) {
		// System.out.println("TextElement.clear() " + txt);
		Point p = position(txt, x, y, refPoint);
		Rectangle bounds = getBounds();
		graphics2D.setColor(ExPar.ScreenBackgroundColor.getDevColor());
		graphics2D.fillRect(bounds.x - 3, bounds.y, bounds.width + 3,
				bounds.height);
	}

	private Point position(String txt, int x, int y, int refPoint) {
		if (font == null) {
			System.out.println("TextElement.baseLeftPosition(): font == null");
		}
		FontMetrics fontMetrics = graphics2D.getFontMetrics(font);
		int sw = fontMetrics.stringWidth(txt);
		int sh = fontMetrics.getAscent();
		x = locX(refPoint, x, sw);
		y = textLocY(refPoint, y, sh);
		setBounds(x, y - sh, sw, sh + fontMetrics.getDescent());
		// System.out.println("TextElement.position() bounds = " + getBounds());
		return new Point(x, y);
	}

	/** Set a TEXT object's font. */
	public void setFont(Font fnt) {
		// System.out.println("Asking for font " + font);
		font = fnt;
	}

	/**
	 * Set the font of this text element if the new font properties are
	 * different from the current font.
	 */
	public void setFont(String fn, int ft, int fs) {
		// System.out.println("Asking for font family " + fn + " using style " +
		// ft + " at size " + fs + "pt");
		if ((font == null) || (!(font.getName().equals(fn)))
				|| (font.getStyle() != ft) || (font.getSize() != fs)) {
			// System.out.println("Setting font family " + fn + " using style "
			// + ft + " at size " + fs + "pt");
			font = new Font(fn, ft, fs);
		}
	}

	/** Get a TEXT object's font. */
	public Font getFont() {
		return (font);
	}

	/** Set a TEXT object's text string. */
	public void setText(String t) {
		text = t;
	}

	/** Get a TEXT object's text string. */
	public String getText() {
		return (text);
	}

	/** Set a TEXT object's reference position code. */
	public void setReferencePoint(int p) {
		referencePoint = Math.abs(p) % 9;
	}

	/** Get a TEXT object's reference position code. */
	public int getReferencePoint() {
		return (referencePoint);
	}

	/**
	 * Adjust the font size if this Text object such that it fits into the top
	 * left area of the given rectangle. Also sets the reference point to
	 * BASE_LEFT.
	 */
	public Rectangle fitToRect(Graphics g, String fn, int fs, Rectangle r) {
		int fns = 100;
		Font fnt = new Font(fn, fs, fns);
		FontMetrics fm = g.getFontMetrics(fnt);
		int fns2 = (int) ((double) r.width / (double) fm.stringWidth(text) * (double) fns);
		Rectangle ret;
		if ((font == null) || (font.getSize() != fns2)) {
			font = new Font(fn, fs, fns2);
			FontMetrics fm2 = g.getFontMetrics(font);
			setLocation(r.x + fns2 / 20, r.y + fm2.getAscent());
			setSize(r.width, fm2.getAscent());
			fnt = null;
			fm = null;
			// int rp = getReferencePoint();
			setReferencePoint(BASE_LEFT);
			ret = new Rectangle(r.x, r.y, r.width, fm2.getAscent());
		} else {
			ret = new Rectangle(location, size);
		}
		return (ret);
	}
}
