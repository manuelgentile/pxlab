package de.pxlab.pxl;

import java.awt.*;
import java.awt.geom.*;

/**
 * An outlined Arc.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 01/26/01 use an ExPar for color
 */
public class Arc extends ArcElement {
	public Arc(ExPar i) {
		type = DisplayElement.ARC;
		colorPar = i;
	}

	public Arc(int x, int y, int w, int h, int sa, int aa) {
		type = DisplayElement.ARC;
		setLocation(new Point(x, y));
		setSize(new Dimension(w, h));
		startAngle = sa;
		arcAngle = aa;
	}

	public Arc(ExPar i, int x, int y, int w, int h, int sa, int aa) {
		type = DisplayElement.ARC;
		colorPar = i;
		setLocation(new Point(x, y));
		setSize(new Dimension(w, h));
		startAngle = sa;
		arcAngle = aa;
	}

	public void show() {
		graphics2D.setPaint(colorPar.getDevColor());
		graphics2D.setStroke(new BasicStroke((float) lineWidth));
		graphics2D.drawArc(location.x, location.y, size.width - 1,
				size.height - 1, startAngle, arcAngle);
		setBounds(new Rectangle(location, size));
	}
}
