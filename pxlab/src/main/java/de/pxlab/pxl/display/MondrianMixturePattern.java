package de.pxlab.pxl.display;

import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * Simulation of a Mondrian pattern made up from 1, 2, or 3 light source
 * mixtures.
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
/*
 * 
 * 07/23/02
 */
public class MondrianMixturePattern extends Display {
	/**
	 * First component of the convext mixture for computing the TargetColor.
	 */
	public ExPar AColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.RED)), "First Light Source Color");
	/**
	 * Second component of the convext mixture for computing the TargetColor.
	 */
	public ExPar BColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GREEN)), "Second Light Source Color");
	/** First distractor color. */
	public ExPar CColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLUE)), "Third Light Source Color");
	/** Number of point lights to show. */
	public ExPar NumberOfLights = new ExPar(1, 3, new ExParValue(1),
			"Number of Light Sources to Use");
	/** Image width. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(300),
			"Pattern width");
	/** Image height. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(300),
			"Pattern height");
	/** Number of columns in the Mondrian pattern. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(7),
			"Number of Columns");
	/** Number of rows in the Mondrian pattern. */
	public ExPar NumberOfRows = new ExPar(SMALL_INT, new ExParValue(5),
			"Number of Rows");
	/** Size of the gap between adjacent patches. */
	public ExPar GapSize = new ExPar(SCREENSIZE, new ExParValue(10),
			"Gap Size between Patches");

	public MondrianMixturePattern() {
		setTitleAndTopic("Mondrian mixture pattern", PATTERN_IMAGE_DSP);
	}
	protected LightDistribution lightDist;
	protected int nLights;
	protected int ld_idx;
	protected Randomizer rnd;

	protected int create() {
		lightDist = new LightDistribution(AColor);
		nLights = 1;
		ld_idx = enterDisplayElement(lightDist);
		defaultTiming(0);
		rnd = new Randomizer();
		return (ld_idx);
	}

	protected void computeColors() {
		// System.out.println("PointLights.computeColors()");
		lightDist.computeColors();
	}

	protected void computeGeometry() {
		// System.out.println("MondrianMixturePattern.computeGeometry()");
		int n = NumberOfLights.getInt();
		if ((n != nLights) && (n >= 1) && (n <= 3)) {
			removeDisplayElements(ld_idx);
			lightDist = createLightDistribution(n);
			enterDisplayElement(lightDist);
			nLights = n;
		}
		setPointLights(lightDist, nLights, NumberOfColumns.getInt(),
				NumberOfRows.getInt(), GapSize.getInt());
	}

	protected LightDistribution createLightDistribution(int n) {
		switch (n) {
		case 1:
			return new LightDistribution(AColor);
		case 2:
			return new LightDistribution(AColor, BColor);
		case 3:
			return new LightDistribution(AColor, BColor, CColor);
		}
		return null;
	}

	protected void setPointLights(LightDistribution lightDist, int n,
			int columns, int rows, int gap) {
		lightDist.setColorPar(AColor);
		switch (n) {
		case 1:
			lightDist.set1ColorRandomMondrian(Width.getInt(), Height.getInt(),
					columns, rows, gap);
			break;
		case 2:
			lightDist.setColorPar2(BColor);
			lightDist.set2ColorRandomMondrian(Width.getInt(), Height.getInt(),
					columns, rows, gap);
			break;
		case 3:
			lightDist.setColorPar2(BColor);
			lightDist.setColorPar3(CColor);
			lightDist.set3ColorRandomMondrian(Width.getInt(), Height.getInt(),
					columns, rows, gap);
			break;
		}
	}

	protected void destroy() {
		if (lightDist != null)
			lightDist.destroy();
		super.destroy();
	}
}
