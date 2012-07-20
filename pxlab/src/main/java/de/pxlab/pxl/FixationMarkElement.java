package de.pxlab.pxl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * An fixation mark.
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
public class FixationMarkElement extends DisplayElement {
	private int type = FixationCodes.FIXATION_CROSS;
	private int enclosedWidth = 0;
	private int enclosedHeight = 0;

	public FixationMarkElement(ExPar i) {
		colorPar = i;
	}

	public void setProperties(int x, int y, int ew, int eh, int t, int w,
			int h, int lw) {
		setLocation(x, y);
		setSize(w, h);
		setLineWidth(lw);
		enclosedWidth = ew;
		enclosedHeight = eh;
		type = t;
	}

	public void show() {
		drawFixationMark(graphics, location.x, location.y, enclosedWidth,
				enclosedHeight, type, size.width, size.height, lineWidth,
				colorPar.getDevColor());
		if (type == FixationCodes.CORNER_MARKS) {
			setBounds(new Point(location.x - enclosedWidth / 2, location.y
					- enclosedHeight / 2), size);
		} else {
			setBounds(new Point(location.x - size.width / 2, location.y
					- size.height / 2), size);
		}
	}
}
