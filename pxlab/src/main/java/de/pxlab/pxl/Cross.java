package de.pxlab.pxl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * An cross mark built from a filled vertical and horizontal rectangle.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 01/26/01 use an ExPar for color 05/04/01 do not draw if size or lineWidth is
 * 0
 */
public class Cross extends DisplayElement {
	public Cross(ExPar i) {
		type = CROSS;
		colorPar = i;
	}

	public Cross(ExPar i, int x, int y, int w, int h) {
		type = CROSS;
		colorPar = i;
		setLocation(x, y);
		setSize(2 * (w / 2) + 1, 2 * (h / 2) + 1);
	}

	public void show() {
		if ((size.width == 0) || (size.height == 0) || (lineWidth == 0)) {
			// nothing to draw
		} else {
			graphics.setColor(colorPar.getDevColor());
			if (lineWidth > 1) {
				int lw2 = lineWidth / 2;
				// System.out.println("Cross at (" + location.x + "," +
				// location.y + ")");
				graphics.fillRect(location.x - size.width / 2,
						location.y - lw2, size.width, lineWidth);
				graphics.fillRect(location.x - lw2, location.y - size.height
						/ 2, lineWidth, size.height);
			} else {
				graphics.drawLine(location.x - size.width / 2, location.y,
						location.x + size.width / 2, location.y);
				graphics.drawLine(location.x, location.y - size.height / 2,
						location.x, location.y + size.height / 2);
			}
		}
		setBounds(new Point(location.x - size.width / 2, location.y
				- size.height / 2), size);
	}
}
