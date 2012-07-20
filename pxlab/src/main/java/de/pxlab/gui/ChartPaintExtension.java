package de.pxlab.gui;

import java.awt.Graphics;

/**
 * This interface provides a hook into the paint method of a Chart object.
 * 
 * @author H. Irtel
 * @version 0.1.0, 06/12/00
 */
public interface ChartPaintExtension {
	/** This method is called whenever the chart has been painted. */
	public void extendedPaint(Graphics g);
	/**
	 * This method is called whenever the mouse button has been pressed within
	 * the chart. It gets the models' x,y-coordinates as arguments.
	 */
	// public void extendedMousePressed(MouseEvent e, double x, double y);
	/**
	 * This method is called whenever the mouse button has been released within
	 * the chart. It gets the models' x,y-coordinates as arguments.
	 */
	// public void extendedMouseReleased(MouseEvent e, double x, double y);
}
