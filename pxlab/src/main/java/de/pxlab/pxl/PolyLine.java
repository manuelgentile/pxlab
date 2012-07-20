package de.pxlab.pxl;

import java.awt.*;
import java.awt.geom.*;

/**
 * A series of connected lines.
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
/*
 * 
 * 01/26/01 use an ExPar for color
 * 
 * 2005/02/16 moved the whole story to Graphics2D
 */
public class PolyLine extends PolygonElement {
	public PolyLine(ExPar i) {
		type = POLYLINE;
		colorPar = i;
	}

	public PolyLine(Polygon p) {
		type = POLYLINE;
		polygon = p;
	}

	public PolyLine(ExPar i, Polygon p) {
		type = POLYLINE;
		colorPar = i;
		polygon = p;
	}

	/**
	 * Show this PolyLine. The display context has been set in the static area
	 * of class DisplayElement.
	 */
	public void show() {
		if ((polygon.npoints > 1)
				&& ((polygon.xpoints[0] != polygon.xpoints[1]) || (polygon.ypoints[0] != polygon.ypoints[1]))) {
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setPaint(colorPar.getDevColor());
			graphics2D.setStroke(new BasicStroke((float) lineWidth));
			Polygon p = isTransformed ? transformed() : polygon;
			GeneralPath gp = new GeneralPath();
			gp.moveTo((float) (p.xpoints[0]), (float) (p.ypoints[0]));
			for (int i = 1; i < p.npoints; i++) {
				gp.lineTo((float) (p.xpoints[i]), (float) (p.ypoints[i]));
			}
			graphics2D.draw(gp);
			if (validBounds) {
				setBounds(gp.getBounds());
			}
		}
	}
}
