package de.pxlab.pxl;

import java.awt.*;
import java.awt.geom.*;

import de.pxlab.awtx.Painter;

/**
 * An arbitrary geometric path element defined by a description string.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.display.GeometricPath
 * @see de.pxlab.awtx.Painter
 */
/*
 * 
 * 2007/05/10
 */
public class PathElement extends DisplayElement {
	protected String[] pathString = null;
	protected double scale = 1.0;
	protected boolean doFill = false;
	protected ExPar colorLinePar;

	/**
	 * Create a new path element.
	 * 
	 * @param i
	 *            the outline color parameter
	 * @param j
	 *            the fill color parameter
	 */
	public PathElement(ExPar i, ExPar j) {
		type = POLYLINE;
		colorLinePar = i;
		colorPar = j;
	}

	/**
	 * Set this path element's properties.
	 * 
	 * @param x
	 *            a horizontal shift of the path
	 * @param y
	 *            a vertical shift of the path
	 * @param s
	 *            a scaling factor for all coordinates of the path
	 * @param lw
	 *            the line width for drawing the path's outline
	 * @param fill
	 *            if true then the path is filled
	 * @param p
	 *            the command string defining the path.
	 */
	public void setProperties(int x, int y, double s, int lw, boolean fill,
			String[] p) {
		setLocation(x, y);
		scale = s;
		setLineWidth(lw);
		doFill = fill;
		pathString = p;
	}

	public void show() {
		Rectangle bounds = Painter.paint((Graphics2D) graphics, location.x,
				location.y, scale, lineWidth, colorLinePar.getDevColor(),
				colorPar.getDevColor(), doFill, pathString);
		if (validBounds) {
			setBounds(bounds);
		}
	}
}
