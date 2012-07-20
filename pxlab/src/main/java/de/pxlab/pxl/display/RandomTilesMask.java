package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A pattern of random tiles using two fioxed colors.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2008/03/06
 */
public class RandomTilesMask extends Display {
	/** The first color. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Foreground Color");
	/** The color of the distractor elements. */
	public ExPar DistractorColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)), "Distractor Color");
	/** Horizontal pattern size in pixels. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(400),
			"Horizontal pattern size");
	/** Vertical pattern size in pixels. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(400),
			"Vertical pattern size");
	/** Horizontal center position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Position of Center Point");
	/** Vertical center position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Position of Center Point");
	/** Number of pixels making up a single pattern raster element. */
	public ExPar RasterSize = new ExPar(SMALL_SCREENSIZE, new ExParValue(20),
			"Raster Size in Pixels");
	/**
	 * Maximum horizontal and vertical size of a single tile given in raster
	 * units.
	 */
	public ExPar MaxTileSize = new ExPar(SMALL_SCREENSIZE, new ExParValue(1),
			"Maximum Tile Size in Raster Units");
	/** Half of the size of the joint between two adjacent tiles. */
	public ExPar JointSize = new ExPar(SMALL_SCREENSIZE, new ExParValue(0),
			"Joint Size between Tiles");

	/** Create the pattern. */
	public RandomTilesMask() {
		setTitleAndTopic("Random Tiles Mask", RANDOM_DOT_DSP);
	}
	protected RandomTilesMaskElement rt;

	protected int create() {
		rt = new RandomTilesMaskElement(Color, DistractorColor);
		int s1 = enterDisplayElement(rt, group[0]);
		defaultTiming();
		return s1;
	}

	protected void computeGeometry() {
		int w = Width.getInt();
		int h = Height.getInt();
		rt.setProperties(LocationX.getInt() - w / 2,
				LocationY.getInt() - h / 2, w, h, RasterSize.getInt(),
				MaxTileSize.getInt(), JointSize.getInt());
	}
}
