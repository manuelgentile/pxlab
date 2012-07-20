package de.pxlab.pxl;

/**
 * A filled rectangle which can use dithering in order to get higher color
 * resolution.
 * 
 * @author H. Irtel
 * @version 0.3.3
 */
/*
 * 
 * 01/26/01 use an ExPar for color
 * 
 * 02/07/02 added dithering.
 * 
 * 07/21/02 always setBounds() !!
 * 
 * 11/03/03 show selection
 */
public class Bar extends DisplayElement {
	public Bar(ExPar i) {
		type = DisplayElement.BAR;
		colorPar = i;
	}

	public Bar(ExPar i, int x, int y, int w, int h) {
		type = DisplayElement.BAR;
		colorPar = i;
		setLocation(x, y);
		setSize(w, h);
	}

	/**
	 * Show this Bar. The display context has been set in the static area of
	 * class DisplayElement. This method can show a dithered version of this Bar
	 * if dithering has been set.
	 */
	public void show() {
		if ((size.width > 0) && (size.height > 0)) {
			if (dither != null) {
				dither.setColor(colorPar.getPxlColor());
				drawDitheredBar(location.x, location.y, size.width, size.height);
			} else {
				graphics.setColor(colorPar.getDevColor());
				graphics.fillRect(location.x, location.y, size.width,
						size.height);
			}
			setBounds(location.x, location.y, size.width, size.height);
			showSelection();
		}
	}
}
