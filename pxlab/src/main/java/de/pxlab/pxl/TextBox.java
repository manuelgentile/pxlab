package de.pxlab.pxl;

import java.awt.*;

/**
 * A single line of text framed in a background box which adapts to the size of
 * the text.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/02
 */
public class TextBox extends TextElement {
	ExPar boxColorPar;

	public TextBox(ExPar i, ExPar j) {
		super(i);
		boxColorPar = j;
	}

	public void show() {
		// Don't show anything if there is no text or the size is 0
		if (text.length() == 0)
			return;
		int dx = referencePoint / 3;
		int dy = referencePoint % 3;
		if (font == null) {
			// System.out.println("TextBox.show(): font == null");
			setFont("Sans", Font.PLAIN, 32);
		}
		graphics2D.setFont(font);
		FontMetrics fontMetrics = graphics2D.getFontMetrics(font);
		int sw = fontMetrics.stringWidth(text);
		int sh = fontMetrics.getAscent();
		int sd = fontMetrics.getDescent();
		int x = location.x;
		int y = location.y;
		if ((dx != 0) || (dy != 0)) {
			if (dx > 0) {
				dx = -sw / 2 * dx;
			}
			if (dy > 0) {
				dy = sh / 2 * dy;
			}
			x += dx;
			y += dy;
		}
		graphics2D.setColor(boxColorPar.getDevColor());
		graphics2D.fillRect(x - 20, y - sh - 10, sw + 40, sh + sd + 20);
		graphics2D.setColor(colorPar.getDevColor());
		graphics2D.drawString(text, x, y);
		setBounds(new Point(x, y - sh), new Dimension(sw, sh + sd));
	}
}
