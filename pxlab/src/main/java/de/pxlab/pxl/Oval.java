package de.pxlab.pxl;

/**
 * A filled Ellipse with dithering being possible.
 * 
 * @author H. Irtel
 * @version 0.3.2
 */
/*
 * 
 * 01/26/01 use an ExPar for color
 * 
 * 02/07/02 added dithering
 * 
 * 11/03/03 moved dithered drawing to the super class
 */
public class Oval extends DisplayElement {
	public Oval(ExPar i) {
		type = DisplayElement.OVAL;
		colorPar = i;
	}

	public Oval(ExPar i, int x, int y, int w, int h) {
		type = DisplayElement.OVAL;
		colorPar = i;
		setLocation(x, y);
		setSize(w, h);
	}

	public void show() {
		if ((size.width > 0) && (size.height > 0)) {
			if (dither != null) {
				dither.setColor(colorPar.getPxlColor());
				drawDitheredOval(location.x + size.width / 2, location.y
						+ size.height / 2, size.width / 2, size.height / 2);
			} else {
				graphics2D.setColor(colorPar.getDevColor());
				graphics2D.fillOval(location.x, location.y, size.width,
						size.height);
			}
			setBounds(location.x, location.y, size.width, size.height);
			showSelection();
		}
	}
}
