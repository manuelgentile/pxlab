package de.pxlab.pxl.display;

import java.awt.*;
import java.util.Random;

import de.pxlab.pxl.*;

/**
 * A color discrimination pattern made up of random tiles with a Landoldt type
 * ring whose gap orientation has to be detected. The colors of ring gap and
 * background are controlled independently.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/01/12
 */
public class RandomTilesColorDiscrimination extends RandomTilesColorVisionTest {
	/** The color of the background elements. */
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(20.0, 0.34, 0.44)), "Background Color");

	/** Create the pattern. */
	public RandomTilesColorDiscrimination() {
		setTitleAndTopic("Color discrimination with random tiles",
				COLOR_DISCRIMINATION_DSP);
	}

	protected int create() {
		rtp = new RandomTilesColorDiscriminationPattern(Color, DistractorColor,
				BackgroundColor);
		int s1 = enterDisplayElement(rtp, group[0]);
		defaultTiming();
		return (s1);
	}
}
