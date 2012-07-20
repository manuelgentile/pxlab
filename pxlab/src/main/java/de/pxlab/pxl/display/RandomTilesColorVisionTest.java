package de.pxlab.pxl.display;

import java.awt.*;
import java.util.Random;

import de.pxlab.pxl.*;

/**
 * A color vision test pattern made up of random tiles with a Landoldt type ring
 * whose gap orientation has to be detected.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 2005/11/14 allow the ring color to be a convex mixture and use dithering.
 */
public class RandomTilesColorVisionTest extends Display {
	/**
	 * The color of the Landoldt ring elements. If the flag ConvexMixture is set
	 * then this color is computed as a convex mixture of LowerLimitColor and
	 * HigherLimitColor.
	 */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new YxyColor(20.0,
			0.44, 0.34)), "Foreground Color");
	/**
	 * Lower limit color for the case where the parameter Color is computed as a
	 * convex mixture of LowerLimitColor and HigherLimitColor.
	 */
	public ExPar LowerLimitColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(20.0, 0.44, 0.34)),
			"Lower limit of the convex color mixture");
	/**
	 * Upper limit color for the case where the parameter Color is computed as a
	 * convex mixture of LowerLimitColor and HigherLimitColor.
	 */
	public ExPar HigherLimitColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(20.0, 0.44, 0.34)),
			"Higher limit of the convex color mixture");
	/**
	 * Mixture weight for the case where the parameter Color is computed as a
	 * convex mixture of HigherLimitColor and LowerLimitColor: C = w*HLC +
	 * (1-w)*LLC.
	 */
	public ExPar Weight = new ExPar(PROPORT, new ExParValue(0.50),
			"Convext mixture weight");
	/** The color of the distractor elements. */
	public ExPar DistractorColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(20.0, 0.34, 0.44)), "Distractor Color");
	/** The color of the background elements. */
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(20.0, 0.34, 0.44)), "Background Color");
	/** Range of random luminance variation. */
	public ExPar LuminanceRange = new ExPar(0.0, 100.0, new ExParValue(20.0),
			"Range of Random Luminance Variation");
	/** The orientation of the gap in the Landoldt ring. */
	public ExPar Orientation = new ExPar(0, 7, new ExParValue(0),
			"Orientation of Opening");
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
	public ExPar RasterSize = new ExPar(SMALL_SCREENSIZE, new ExParValue(5),
			"Raster Size in Pixels");
	/**
	 * Maximum horizontal and vertical size of a single tile given in raster
	 * units.
	 */
	public ExPar MaxTileSize = new ExPar(SMALL_SCREENSIZE, new ExParValue(2),
			"Maximum Tile Size in Raster Units");
	/** Half of the size of the joint between two adjacent tiles. */
	public ExPar JointSize = new ExPar(SMALL_SCREENSIZE, new ExParValue(1),
			"Joint Size between Tiles");
	/**
	 * If true then the parameter Color is computed as a convex mixture of
	 * HigherLimitColor and LowerLimitColor.
	 */
	public ExPar ConvexMixture = new ExPar(FLAG, new ExParValue(0),
			"Flag to use convex mixture color");
	/**
	 * The size of the dithering matrix used to increase color resolution.
	 */
	public ExPar DitheringType = new ExPar(GEOMETRY_EDITOR,
			DitheringCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.DitheringCodes.NO_DITHERING"),
			"Type of dithering");

	/** Create the pattern. */
	public RandomTilesColorVisionTest() {
		setTitleAndTopic("Color Vision Test", COLOR_DISCRIMINATION_DSP);
	}
	protected RandomTilesColorDiscriminationPattern rtp;

	protected int create() {
		rtp = new RandomTilesColorDiscriminationPattern(DistractorColor, Color,
				DistractorColor);
		int s1 = enterDisplayElement(rtp, group[0]);
		defaultTiming();
		return (s1);
	}

	protected void computeColors() {
		if (ConvexMixture.getFlag()) {
			double a = Weight.getDouble();
			Color.set(HigherLimitColor.getPxlColor().mix(a,
					LowerLimitColor.getPxlColor()));
			// System.out.println(a);
		}
	}

	protected void computeGeometry() {
		if (ConvexMixture.getFlag()) {
			computeColors();
		}
		int w = Width.getInt();
		int h = Height.getInt();
		rtp.setProperties(LocationX.getInt() - w / 2, LocationY.getInt() - h
				/ 2, w, h, RasterSize.getInt(), MaxTileSize.getInt(),
				JointSize.getInt());
		rtp.setOrientation(Orientation.getInt());
		rtp.setLuminanceRange(LuminanceRange.getDouble());
		rtp.setDither(DitheringType.getInt());
	}
}
