package de.pxlab.pxl;

import java.awt.Toolkit;
import java.awt.Dimension;

/**
 * This class contains some magic numbers which represent certain variables in
 * ExParValue objects.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 07/19/00
 * 
 * 07/28/01 added magic numbers for width and height, refer to the screen and
 * not to the Component
 */
public class MagicNumber {
	private static final int magicBase = 1234567; // Integer.MAX_VALUE;
	/**
	 * Whenever this number is used as an x-coordinate value it is replaced by
	 * the current screen's leftmost x-coordinate.
	 */
	public static final int screenLeftX = magicBase - 1;
	/**
	 * Whenever this number is used as an x-coordinate value it is replaced by
	 * the current screen's rightmost x-coordinate.
	 */
	public static final int screenRightX = magicBase - 2;
	/**
	 * Whenever this number is used as an y-coordinate value it is replaced by
	 * the current screen's topmost y-coordinate.
	 */
	public static final int screenTopY = magicBase - 3;
	/**
	 * Whenever this number is used as an y-coordinate value it is replaced by
	 * the current screen's bottommost y-coordinate.
	 */
	public static final int screenBottomY = magicBase - 4;
	/**
	 * Whenever this number is used as an y-coordinate value it is replaced by
	 * the current screen's bottommost y-coordinate.
	 */
	public static final int screenWidth = magicBase - 5;
	/**
	 * Whenever this number is used as an y-coordinate value it is replaced by
	 * the current screen's bottommost y-coordinate.
	 */
	public static final int screenHeight = magicBase - 6;
	/** The limit for all MagicNumber objects we have. */
	public static final int lowerLimit = magicBase - 7;

	/**
	 * Check whether the argument x is a MagicNumber and convert it to a screen
	 * border limit if necessary.
	 * 
	 * @param x
	 *            the number which possibly is a MagicNumber
	 * @return the double value which corresponds to the screen border
	 *         coordinate which is represented by this MagicNumber or the
	 *         unmodified argument x if it is not a MagicNumber.
	 */
	public static double limit(double x) {
		double r = x;
		// System.out.println("limit: " + x);
		if ((x < (double) (magicBase)) && (x > (double) (lowerLimit))) {
			Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
			int w = s.width;
			int h = s.height;
			switch ((int) (x + 0.5)) {
			case screenLeftX:
				r = -w / 2;
				break;
			case screenRightX:
				r = -w / 2 + w - 1;
				break;
			case screenTopY:
				r = -h / 2;
				break;
			case screenBottomY:
				r = -h / 2 + h - 1;
				break;
			case screenWidth:
				r = w;
				break;
			case screenHeight:
				r = h;
				break;
			}
		}
		return (r);
	}
}
