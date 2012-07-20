package de.pxlab.pxl;

import java.awt.*;

/**
 * Displays a filled rectangle with a label which may be used as a screen
 * button. A screen button is an extension of a mouse button and can be used by
 * mouse tracking timers to stop the tracking interval.
 * 
 * @version 0.1.0
 * @see de.pxlab.pxl.display.RatingScale
 */
/*
 * 
 * 2007/06/19
 */
public class ScreenButtonElement extends DisplayElement {
	private Bar bar;
	private TextElement label;
	private boolean active = false;

	/** Create a screen button. */
	public ScreenButtonElement(ExPar bc, ExPar tc) {
		bar = new Bar(bc);
		label = new TextElement(tc);
	}

	/**
	 * Set the screen button's properties.
	 * 
	 * @param p
	 *            an array of position information. If it is a single number
	 *            then it is a position code from class PositionReferenceCodes.
	 *            If the array contains two numbers then they are the x and y
	 *            coordinate of the button's center.
	 * @param a
	 *            an array of size parameters. If this array contains a single
	 *            number then it is the button's height. If the array contains
	 *            two numbers then these are the button's width and height
	 *            respectively.
	 * @param t
	 *            the text label on the button.
	 * @return the bounding rectangle of the button or null if the button's size
	 *         is 0.
	 * @see PositionReferenceCodes
	 */
	public Rectangle setProperties(int[] p, int[] a, String t) {
		Rectangle b = null;
		int h = 0, w = 0;
		if (a.length > 1) {
			w = a[0];
			h = a[1];
		} else {
			h = a[0];
			w = h * 4;
		}
		if (h > 0) {
			int x = 0, y = 0;
			if (p.length > 1) {
				x = locX(PositionReferenceCodes.MIDDLE_CENTER, p[0], w);
				y = rectLocY(PositionReferenceCodes.MIDDLE_CENTER, p[1], h);
			} else {
				int position = p[0];
				int d = h / 2;
				switch (position / 3) {
				case 0:
					x = -displayWidth / 2 + d;
					break;
				case 1:
					x = -w / 2;
					break;
				case 2:
					x = displayWidth / 2 - w - d;
					break;
				}
				switch (position % 3) {
				case 0:
					y = displayHeight / 2 - h - d;
					break;
				case 1:
					y = -h / 2;
					break;
				case 2:
					y = -displayHeight / 2 + d;
					break;
				}
			}
			bar.setRect(x, y, w, h);
			label.setReferencePoint(PositionReferenceCodes.BASE_CENTER);
			label.setFont("SansSerif", Font.PLAIN, h / 2);
			label.setText(t);
			label.setLocation(x + w / 2, y + 11 * h / 16);
			b = bar.getRect();
			active = true;
		} else {
			active = false;
		}
		return b;
	}

	public void show() {
		if (active) {
			bar.show();
			label.show();
			setBounds(bar.getBounds());
		}
	}
}
