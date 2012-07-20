package de.pxlab.pxl;

import java.awt.*;

/**
 * An outlined rectangle with variable line width.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 
 * 01/26/01 use an ExPar for color
 * 
 * 07/04/03 added variable line width
 */
public class Rect extends DisplayElement {
	public Rect(ExPar i) {
		type = DisplayElement.RECT;
		colorPar = i;
	}

	public Rect(ExPar i, int x, int y, int w, int h) {
		type = DisplayElement.RECT;
		colorPar = i;
		setLocation(x, y);
		setSize(w, h);
	}

	public void show() {
		if ((size.width == 0) || (size.height == 0) || (lineWidth == 0)) {
			// nothing to draw
		} else {
			graphics.setColor(colorPar.getDevColor());
			if (lineWidth > 1) {
				graphics.fillRect(location.x, location.y, size.width, lineWidth);
				graphics.fillRect(location.x, location.y + size.height
						- lineWidth, size.width, lineWidth);
				graphics.fillRect(location.x, location.y + lineWidth,
						lineWidth, size.height - 2 * lineWidth);
				graphics.fillRect(location.x + size.width - lineWidth,
						location.y + lineWidth, lineWidth, size.height - 2
								* lineWidth);
			} else {
				graphics.drawRect(location.x, location.y, size.width - 1,
						size.height - 1);
			}
		}
		setBounds(new Rectangle(location, size));
	}
}
