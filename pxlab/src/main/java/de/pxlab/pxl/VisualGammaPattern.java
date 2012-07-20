package de.pxlab.pxl;

/**
 * A pattern for visual gamma measurement. It contains three adjacent
 * rectangles. The center rectangle is such that every odd line is colored and
 * every even line is blank. The left rectangle is simply a filled box. The
 * observer's task is to adjust the filled box's color such that both rectangles
 * appear equal and there is no visible border.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 2005/11/29 modification to paint 3 fields with the outer fields being
 * homogenous and the center field being striped.
 * 
 * 08/05/03
 */
public class VisualGammaPattern extends DisplayElement {
	protected ExPar colorPar2;
	protected int horizontalCycles = 1;

	public VisualGammaPattern(ExPar a, ExPar b) {
		colorPar = a;
		colorPar2 = b;
	}

	public void setProperties(int hc) {
		horizontalCycles = (hc > 0) ? hc : 1;
	}

	public void show() {
		// number of horizontal lines must be odd
		int h = 2 * (size.height / 2) + 1;
		int hc1 = 2 * horizontalCycles + 1;
		int w = hc1 * (size.width / hc1);
		int ws = w / hc1;
		int x = location.x;
		int y = location.y;
		graphics.setColor(colorPar2.getDevColor());
		for (int j = 0; j < hc1; j += 2) {
			graphics.fillRect(x + j * ws, y, ws, h);
		}
		graphics.setColor(colorPar.getDevColor());
		for (int j = 1; j < hc1; j += 2) {
			int xs = x + j * ws;
			int xr = xs + ws - 1;
			for (int i = 0; i < h; i += 2) {
				int yy = y + i;
				graphics.drawLine(xs, yy, xr, yy);
			}
		}
		if (validBounds) {
			setBounds(location.x, location.y, w, h);
		}
	}
}
