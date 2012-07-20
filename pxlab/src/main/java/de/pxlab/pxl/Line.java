package de.pxlab.pxl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A simple line.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 01/26/01 use an ExPar for color
 */
public class Line extends DisplayElement {
	public Line(ExPar i) {
		type = LINE;
		colorPar = i;
	}

	public Line(int x, int y, int u, int v) {
		type = LINE;
		setLocation(new Point(x, y));
		setLocation2(new Point(u, v));
	}

	public Line(ExPar i, int x, int y, int u, int v) {
		type = LINE;
		colorPar = i;
		setLocation(new Point(x, y));
		setLocation2(new Point(u, v));
	}

	public void show() {
		graphics2D.setColor(colorPar.getDevColor());
		graphics2D.drawLine(location.x, location.y, location2.x, location2.y);
		Point p = null;
		Dimension s = null;
		if (location.x <= location2.x) {
			if (location.y <= location2.y) {
				p = location;
				s = new Dimension(location2.x - location.x + 1, location2.y
						- location.y + 1);
			} else {
				p = new Point(location.x, location2.y);
				s = new Dimension(location2.x - location.x + 1, location.y
						- location2.y + 1);
			}
		} else {
			if (location.y <= location2.y) {
				p = new Point(location2.x, location.y);
				s = new Dimension(location.x - location2.x + 1, location2.y
						- location.y + 1);
			} else {
				p = new Point(location2.x, location2.y);
				s = new Dimension(location.x - location2.x + 1, location.y
						- location2.y + 1);
			}
		}
		setBounds(new Rectangle(p, s));
	}
	/** The 2nd location of this object on the screen. */
	protected Point location2;

	/** Set an object's 2nd location on the screen. */
	public void setLocation2(Point l) {
		location2 = l;
	}

	/** Set an object's 2nd location on the screen. */
	public void setLocation2(int x, int y) {
		location2 = new Point(x, y);
	}

	/** Get an object's 2nd location on the screen. */
	public Point getLocation2() {
		return (location2);
	}
}
