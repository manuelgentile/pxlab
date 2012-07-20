package de.pxlab.pxl;

import java.awt.*;

import java.util.ArrayList;
import de.pxlab.util.StringExt;

/**
 * A paragraph of text. The text is shown in single line mode if it is only a
 * single line of text and in paragraph mode if the text contains multiple
 * lines. Single line mode is used if the text object contains only a single
 * string, the string does not contain any newline characters, and line wrapping
 * is turned off. In all other cases the text is shown in paragraph mode. In
 * single line mode the text reference point codes refer to the single line. In
 * paragraph mode the text reference point codes refer to the whole paragraph.
 * In this case the line width parameter must be defined.
 * 
 * @version 0.3.1
 */
/*
 * 
 * 01/26/01 use an ExPar for color 03/15/01 made reference point and lineskip
 * factor object variables 03/23/01 added line breaking features
 * 
 * 2007/06/16 allow pixel reference positioning for internal access
 */
// public class TextParagraphElement extends DisplayElement implements
// PositionReferenceCodes {
public class TextParagraphElement extends DisplayElement {
	/** Text content of this text element. */
	protected String[] text = new String[1];
	/** The Font for this text object. */
	protected Font font = null;
	/** Emphasized font style. */
	protected int emFontStyle = Font.BOLD;
	/** The emphasized Font for this text object. */
	protected Font emFont = null;
	/** If true then the first line of the text is emphasized. */
	protected boolean emphasizeFirstLine = false;
	/**
	 * If false then single lines of text are treated differently in vertical
	 * alignment than paragraphs. If true then single lines are treated like
	 * paragraphs. False by default.
	 */
	protected boolean strictParagraphMode = false;

	public void setStrictParagraphMode(boolean m) {
		strictParagraphMode = m;
	}
	/** Reference point for text positioning. */
	protected int referencePoint = PositionReferenceCodes.BASE_CENTER;
	/**
	 * True if the vertical position reference should refer to pixels instead of
	 * text lines.
	 */
	private boolean pixelPositionReference = false;
	/** Alignment of lines within a multiline paragraph. */
	protected int alignment = AlignmentCodes.CENTER;
	/**
	 * The actual lineskip is the font's natural lineskip multiplied by this
	 * factor.
	 */
	protected double lineSkipFactor = 1.0;
	/** If true then automatic line wrapping is done. */
	protected boolean wrapLines = false;

	public TextParagraphElement(ExPar i) {
		type = DisplayElement.TEXT;
		colorPar = i;
		setSize(10000, 1);
	}
	FontMetrics fontMetrics;

	public void show() {
		if (text == null)
			return;
		graphics2D.setColor(colorPar.getDevColor());
		graphics2D.setFont(font);
		fontMetrics = graphics2D.getFontMetrics(font);
		ArrayList par = new ArrayList(30);
		if (Debug.isActive(Debug.TEXTPOS)) {
			graphics2D.drawLine(location.x - 200, location.y, location.x + 200,
					location.y);
			graphics2D.drawLine(location.x, location.y - 200, location.x,
					location.y + 200);
		}
		if (wrapLines) {
			// Explicit lines are kept
			ArrayList pl = new ArrayList(5);
			for (int i = 0; i < text.length; i++) {
				pl.addAll(StringExt.getTextParagraph(text[i]));
				// System.out.println("Current size of pl: " + pl.size());
				// for (int j = 0; j < pl.size(); j++) System.out.println("  "
				// +j+": " + (String)pl.get(j));
			}
			for (int i = 0; i < pl.size(); i++) {
				par.addAll(StringExt.getTextParagraph((String) pl.get(i),
						fontMetrics, size.width));
				// System.out.println("Current size of par: " + par.size());
				// for (int j = 0; j < par.size(); j++) System.out.println("  "
				// +j+": " + (String)par.get(j));
			}
		} else {
			// Do not break lines but look for '\n' characters and
			// respect every single string as a single line
			for (int i = 0; i < text.length; i++) {
				// System.out.println("TextParagraphElement.show() text[" + i +
				// "] = " + text[i]);
				par.addAll(StringExt.getTextParagraph(text[i]));
			}
		}
		if (par.size() > 1 || strictParagraphMode) {
			showMultipleLines(par);
		} else if (par.size() == 1) {
			showSingleLine((String) (par.get(0)));
		}
		if (Debug.isActive(Debug.TEXTPOS)) {
			Rectangle bounds = getBounds();
			graphics2D
					.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	private void showSingleLine(String txt) {
		// System.out.println("TextParagraphElement.showSingleLine(): " + txt);
		int x = locX(referencePoint, location.x, fontMetrics.stringWidth(txt));
		int y = textLocY(referencePoint, location.y, fontMetrics.getAscent());
		graphics2D.drawString(txt, x, y);
		setBounds(x, y - fontMetrics.getAscent(), fontMetrics.stringWidth(txt),
				fontMetrics.getAscent() + fontMetrics.getDescent());
	}

	private void showMultipleLines(ArrayList textPar) {
		// System.out.println("TextParagraphElement.showMultipleLines(): " +
		// textPar.size());
		// lineskip is the number of screen lines per line of text
		int lineskip = (int) (fontMetrics.getHeight() * lineSkipFactor + 0.5);
		int n = textPar.size();
		// These are the x- and y-shifts for every line of text
		int[] dxp = new int[n];
		int[] dyp = new int[n];
		// width of the widest line
		int maxWidth = 0;
		// Compute the y-position of the first line. The default is
		// dy==2 and the first line is at the reference point. yy is
		// initialized to the position of the first line of text
		// relative to the reference point
		int yy = 0;
		// System.out.println("referencePoint="+referencePoint+", alignment="+alignment+", dx="+dx+", dy="+dy);
		int dx = referencePoint / 3;
		int dy = referencePoint % 3;
		if (pixelPositionReference) {
			// System.out.println("Pixel wise reference");
			if (dy == 0) {
				// The last line is at the reference point
				yy = -(n - 1) * lineskip;
			} else if (dy == 1) {
				// The middle line is at the reference point. Odd
				// numbered paragraphs round down
				yy = -((n - 1) * lineskip - fontMetrics.getAscent()) / 2;
			} else if (dy == 2) {
				yy = fontMetrics.getAscent();
			}
		} else {
			if (dy == 0) {
				// The last line is at the reference point
				yy = -(n - 1) * lineskip;
			} else if (dy == 1) {
				// The middle line is at the reference point. Odd
				// numbered paragraphs round down
				yy = -(n / 2) * lineskip;
			}
		}
		int boundy = location.y + yy - lineskip;
		for (int i = 0; i < n; i++) {
			int sw = fontMetrics.stringWidth((String) textPar.get(i));
			dxp[i] = (alignment <= 0) ? 0 : (-sw / 2 * alignment);
			if (sw > maxWidth)
				maxWidth = sw;
			dyp[i] = yy;
			// System.out.println("yy = " + yy + ", lineskip = " + lineskip);
			yy += lineskip;
		}
		int ref_dx = -dx * (maxWidth / 2);
		int alg_dx = alignment * (maxWidth / 2);
		int xp = location.x + ref_dx + alg_dx;
		if (emphasizeFirstLine) {
			graphics2D.setFont(emFont);
			graphics2D.drawString((String) textPar.get(0), xp + dxp[0],
					location.y + dyp[0]);
			graphics2D.setFont(font);
			for (int i = 1; i < n; i++) {
				graphics2D.drawString((String) textPar.get(i), xp + dxp[i],
						location.y + dyp[i]);
			}
		} else {
			for (int i = 0; i < n; i++) {
				graphics2D.drawString((String) textPar.get(i), xp + dxp[i],
						location.y + dyp[i]);
			}
		}
		setBounds(location.x + ref_dx, boundy, maxWidth, n * lineskip);
	}

	public void setProperties(String[] txt, String ff, int fst, int fs, int x,
			int y, int w, int ref, int align, boolean em1st, boolean wrp,
			double lskp) {
		/*
		 * System.out.println("Text = " + txt[0]); System.out.println("Font = "
		 * + ff); System.out.println("Ref = " + ref);
		 * System.out.println("Ali = " + align); System.out.println("x = " + x +
		 * "   y = " + y);
		 */
		setText(txt);
		setFont(ff, fst, fs);
		setLocation(x, y);
		setSize(w, 1);
		setReferencePoint(ref);
		alignment = align;
		wrapLines = wrp;
		emphasizeFirstLine = em1st;
		if (emphasizeFirstLine) {
			setEmFont(ff, fs);
		}
		lineSkipFactor = lskp;
	}

	public void setWrapLines(boolean w) {
		wrapLines = w;
	}

	/** Set a TEXT object's text string. */
	public void setText(String[] t) {
		text = t;
	}

	/** Set a TEXT object's text string. */
	public void setText(String t) {
		text = new String[1];
		text[0] = t;
	}

	/** Get a TEXT object's text string. */
	public String[] getText() {
		return (text);
	}

	/** Set the text paragraph's reference point. */
	public void setReferencePoint(int p) {
		referencePoint = Math.abs(p) % 9;
	}

	/**
	 * Set the text paragraph's reference point. If this method is called then
	 * the vertical reference is computed relative to the paragraph's pixel size
	 * instead of with reference to its text lines.
	 * 
	 * @param p
	 *            the position reference code.
	 */
	public void setPixelReferencePoint(int p) {
		pixelPositionReference = true;
		referencePoint = Math.abs(p) % 9;
	}

	/** Get a TEXT object's reference position code. */
	public int getReferencePoint() {
		return (referencePoint);
	}

	/** Set a TEXT object's reference position code. */
	public void setAlignment(int p) {
		alignment = Math.abs(p) % 3;
	}

	/** Get a TEXT object's reference position code. */
	public int getAlignment() {
		return (alignment);
	}

	/**
	 * Set the line skip factor which is used to multiply the font's character
	 * height for finding the actual line skip.
	 */
	public void setLineSkipFactor(double d) {
		lineSkipFactor = d;
	}

	/** Get the line skip factor. */
	public double getLineSkipFactor() {
		return (lineSkipFactor);
	}

	/** Set a TEXT object's font. */
	public void setFont(Font fnt) {
		font = fnt;
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
		}
	}

	public void setEmFont(String fn, int fs) {
		if ((emFont == null) || (!(emFont.getName().equals(fn)))
				|| (emFont.getStyle() != emFontStyle)
				|| (emFont.getSize() != fs)) {
			// System.out.println("Setting font family " + fn + " using style "
			// + ft + " at size " + fs + "pt");
			emFont = new Font(fn, emFontStyle, fs);
		}
	}

	/** Get a TEXT object's font. */
	public Font getFont() {
		return (font);
	}
}
