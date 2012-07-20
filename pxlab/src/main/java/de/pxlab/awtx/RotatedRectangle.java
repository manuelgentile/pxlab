package de.pxlab.awtx;

import java.awt.*;

/**
 * A polygon which is as rectangle which may be rotated.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class RotatedRectangle extends Polygon {
	/**
	 * Create a polygon which contains a rotated rectangle.
	 * 
	 * @param x
	 *            horizontal center position.
	 * @param y
	 *            vertical center position.
	 * @param width
	 *            rectangle width.
	 * @param height
	 *            rectangle height.
	 * @param angle
	 *            the angle of counter clockwise rotation.
	 */
	public RotatedRectangle(int x, int y, int width, int height, int angle) {
		int w2 = -width / 2;
		int h2 = -height / 2;
		int[] xx = { w2, width + w2, width + w2, w2 };
		int[] yy = { h2, h2, height + h2, height + h2 };
		npoints = 4;
		if (angle == 0) {
			xpoints = xx;
			ypoints = yy;
		} else {
			double a = -Math.PI * angle / 180.0;
			double cos = Math.cos(a);
			double sin = Math.sin(a);
			xpoints = new int[4];
			ypoints = new int[4];
			for (int i = 0; i < 4; i++) {
				xpoints[i] = (int) (cos * xx[i] + sin * yy[i]);
				ypoints[i] = (int) (-sin * xx[i] + cos * yy[i]);
			}
		}
		if ((x != 0) || (y != 0))
			this.translate(x, y);
	}
}
