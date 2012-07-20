package de.pxlab.pxl;

import java.awt.Polygon;

/**
 * A filled polygon.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 01/26/01 use an ExPar for color
 * 
 * 2005/09/01 fixed bug which forgot to set the bounds
 */
public class FilledPolygon extends PolygonElement {
	public FilledPolygon(ExPar i) {
		type = DisplayElement.FILLED_POLYGON;
		colorPar = i;
	}

	public FilledPolygon(Polygon p) {
		type = DisplayElement.FILLED_POLYGON;
		polygon = p;
	}

	public FilledPolygon(ExPar i, Polygon p) {
		type = DisplayElement.FILLED_POLYGON;
		colorPar = i;
		polygon = p;
	}

	/**
	 * Show this PolyArea. The display context has been set in the static area
	 * of class DisplayElement.
	 */
	public void show() {
		graphics2D.setColor(colorPar.getDevColor());
		if (isTransformed) {
			Polygon p = transformed();
			graphics2D.fillPolygon(p);
			setBounds(p.getBounds());
		} else {
			graphics2D.fillPolygon(polygon);
			setBounds(polygon.getBounds());
		}
	}
}
