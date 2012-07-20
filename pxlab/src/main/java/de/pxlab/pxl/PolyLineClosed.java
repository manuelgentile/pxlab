package de.pxlab.pxl;

import java.awt.*;

/**
 * A series of connected lines.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 01/26/01 use an ExPar for color
 */
public class PolyLineClosed extends PolygonElement {
	public PolyLineClosed(ExPar i) {
		type = DisplayElement.POLYLINECLOSED;
		colorPar = i;
	}

	public PolyLineClosed(Polygon p) {
		type = DisplayElement.POLYLINECLOSED;
		polygon = p;
	}

	public PolyLineClosed(ExPar i, Polygon p) {
		type = DisplayElement.POLYLINECLOSED;
		colorPar = i;
		polygon = p;
	}

	/**
	 * Show this PolyLineClosed. The display context has been set in the static
	 * area of class DisplayElement.
	 */
	public void show() {
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setPaint(colorPar.getDevColor());
		graphics2D.setStroke(new BasicStroke((float) lineWidth));
		if (isTransformed) {
			Polygon p = transformed();
			graphics2D.drawPolygon(p);
			if (validBounds) {
				setBounds(p.getBounds());
			}
		} else {
			graphics2D.drawPolygon(polygon);
			if (validBounds) {
				setBounds(polygon.getBounds());
			}
		}
	}
}
