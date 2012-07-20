package de.pxlab.pxl;

/**
 * A filled patch which is shown as an oval if it is being selected and is shown
 * as a bar if not. Dithering is possible.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 01/26/01 use an ExPar for color
 * 
 * 02/07/02 added dithering
 * 
 * 11/03/03 moved dithered drawing to the super class
 */
public class Patch extends DisplayElement {
	public Patch(ExPar i) {
		type = DisplayElement.OVAL;
		colorPar = i;
	}

	public Patch(ExPar i, int x, int y, int w, int h) {
		type = DisplayElement.OVAL;
		colorPar = i;
		setLocation(x, y);
		setSize(w, h);
	}

	public void show() {
		if ((size.width > 0) && (size.height > 0)) {
			if (dither != null) {
				dither.setColor(colorPar.getPxlColor());
				if (isSelected()) {
					drawDitheredOval(location.x + size.width / 2, location.y
							+ size.height / 2, size.width / 2, size.height / 2);
				} else {
					drawDitheredBar(location.x, location.y, size.width,
							size.height);
				}
			} else {
				graphics.setColor(colorPar.getDevColor());
				if (isSelected()) {
					graphics.fillOval(location.x, location.y, size.width,
							size.height);
				} else {
					graphics.fillRect(location.x, location.y, size.width,
							size.height);
				}
			}
			setBounds(location.x, location.y, size.width, size.height);
		}
	}
}
