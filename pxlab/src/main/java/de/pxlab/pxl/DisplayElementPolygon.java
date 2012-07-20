package de.pxlab.pxl;

import java.awt.Polygon;

/**
 * This class represents polygon type display objects.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 05/04/00
 */
abstract public class DisplayElementPolygon extends DisplayElement {
	/** Polygon types store their points here. */
	protected Polygon polygon;

	public void setPolygon(Polygon p) {
		polygon = p;
	}

	public Polygon getPolygon() {
		return (polygon);
	}
	protected double rotationSine = 0.0;
	protected double rotationCosine = 1.0;
	protected double rotation = 0.0;
	protected int mX = 0;
	protected int mY = 0;
	protected int translationX = 0;
	protected int translationY = 0;
	protected boolean isTransformed = false;

	/**
	 * This polygon should be rotated around the origin by the given angle
	 * before it is shown.
	 * 
	 * @param angle
	 *            angle of rotation in degrees.
	 */
	public void setRotation(double angle) {
		rotation = Math.PI * angle / 180.0;
		rotationSine = Math.sin(rotation);
		rotationCosine = Math.cos(rotation);
		isTransformed = true;
	}

	/**
	 * This polygon should be rotated around the given point by the given angle
	 * before it is shown.
	 * 
	 * @param angle
	 *            angle of rotation in degrees.
	 * @param x
	 *            horizontal center of rotation.
	 * @param y
	 *            vertical center of rotation.
	 */
	public void setRotation(double angle, int x, int y) {
		setRotation(angle);
		mX = x;
		mY = y;
	}

	/**
	 * Shift every point of this polygon by the given x- and y-value after it is
	 * rotated by its rotation and before it is shown.
	 * 
	 * @param x
	 *            horizontal shift.
	 * @param y
	 *            vertical shift.
	 */
	public void setTranslation(int x, int y) {
		translationX = x;
		translationY = y;
		isTransformed = true;
	}

	/**
	 * Create a new polygon which is transformed by this polygon's rotation and
	 * translation parameters.
	 */
	protected Polygon transformed() {
		int n = polygon.npoints;
		int[] xp = new int[n];
		int[] yp = new int[n];
		int xmm, ymm;
		for (int i = 0; i < n; i++) {
			if (rotation != 0.0) {
				xmm = polygon.xpoints[i] - mX;
				ymm = polygon.ypoints[i] - mY;
				xp[i] = (int) Math.round(rotationCosine * xmm + rotationSine
						* ymm + mX + translationX);
				yp[i] = (int) Math.round(-rotationSine * xmm + rotationCosine
						* ymm + mY + translationY);
			} else {
				xp[i] = polygon.xpoints[i] + translationX;
				yp[i] = polygon.ypoints[i] + translationY;
			}
		}
		return (new Polygon(xp, yp, n));
	}
}
