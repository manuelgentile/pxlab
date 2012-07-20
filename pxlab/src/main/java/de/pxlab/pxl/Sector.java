package de.pxlab.pxl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * An outlined Sector.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 01/26/01 use an ExPar for color
 */
public class Sector extends ArcElement {
	public Sector(ExPar i) {
		type = DisplayElement.SECTOR;
		colorPar = i;
	}

	public Sector(int x, int y, int w, int h, int sa, int aa) {
		type = DisplayElement.SECTOR;
		setLocation(new Point(x, y));
		setSize(new Dimension(w, h));
		startAngle = sa;
		arcAngle = aa;
	}

	public Sector(ExPar i, int x, int y, int w, int h, int sa, int aa) {
		type = DisplayElement.SECTOR;
		colorPar = i;
		setLocation(new Point(x, y));
		setSize(new Dimension(w, h));
		startAngle = sa;
		arcAngle = aa;
	}

	public void show() {
		graphics.setColor(colorPar.getDevColor());
		graphics.fillArc(location.x, location.y, size.width - 1,
				size.height - 1, startAngle, arcAngle);
		setBounds(new Rectangle(location, size));
	}
}
