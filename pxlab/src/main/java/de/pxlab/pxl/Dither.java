package de.pxlab.pxl;

import java.awt.Color;

/**
 * This class describes dithering objects for approximating target colors which
 * may not be available as device colors by spatial mixture of two available
 * device colors.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 02/13/02
 */
abstract public class Dither {
	/**
	 * Get the device color for the given screen position. The color returned
	 * approximates the currently active target color using the dithering method
	 * of this dithering object.
	 * 
	 * @param x
	 *            horizontal screen position.
	 * @param y
	 *            vertical screen position.
	 */
	abstract public Color colorAt(int x, int y);

	/**
	 * Set the target color for this dithering object.
	 * 
	 * @param c
	 *            the color which should be approximated by the next call to the
	 *            colorAt() method.
	 */
	abstract public void setColor(PxlColor c);

	/**
	 * Return the unique identifying type code of this Dither's method.
	 */
	abstract public int getType();
}
