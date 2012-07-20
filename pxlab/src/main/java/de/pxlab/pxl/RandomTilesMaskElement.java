package de.pxlab.pxl;

import java.awt.*;

// import de.pxlab.util.*;
/**
 * A pattern of random tiles with two colors.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class RandomTilesMaskElement extends RandomTiles {
	/**
	 * Create a new random tiles pattern.
	 * 
	 * @param fc
	 *            foreground pattern color parameter.
	 * @param bc
	 *            background color parameter.
	 */
	public RandomTilesMaskElement(ExPar fc, ExPar bc) {
		super(fc, bc);
	}

	/**
	 * Get the device color of the given tile. This method is used when
	 * dithering is OFF.
	 * 
	 * @param t
	 *            the tile whose color is requested.
	 */
	protected Color getColorOf(Tile t) {
		return rand.nextBoolean() ? colorPar.getDevColor() : colorPar2
				.getDevColor();
	}

	/**
	 * Get the XYZ-color of the given tile. This method is used when dithering
	 * is ON.
	 * 
	 * @param t
	 *            the tile whose color is requested.
	 */
	protected PxlColor getPxlColorOf(Tile t) {
		return rand.nextBoolean() ? colorPar.getPxlColor() : colorPar2
				.getPxlColor();
	}
}
