package de.pxlab.pxl;

import java.awt.*;
import java.awt.geom.*;

/**
 * An outlined Ellipse.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 01/26/01 use an ExPar for color
 */
public class Ellipse extends DisplayElement {
	public Ellipse(ExPar i) {
		type = DisplayElement.ELLIPSE;
		colorPar = i;
	}

	/*
	 * public Ellipse(int x, int y, int w, int h) { type =
	 * DisplayElement.ELLIPSE; setLocation(x, y); setSize(w, h); }
	 */
	public Ellipse(ExPar i, int x, int y, int w, int h) {
		type = DisplayElement.ELLIPSE;
		colorPar = i;
		setLocation(x, y);
		setSize(w, h);
	}

	public void show() {
		graphics2D.setPaint(colorPar.getDevColor());
		graphics2D.setStroke(new BasicStroke((float) lineWidth));
		graphics2D.drawOval(location.x, location.y, size.width - 1,
				size.height - 1);
		setBounds(new Rectangle(location, size));
	}
}
