package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Two Gaussian patches, one for adaptation and the other one for matching it to
 * the afterimage. May be shown continuously.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/06/30
 */
// public class AfterEffectImage extends ColorAdjustableHSB {
public class AfterEffectImage extends Display {
	/** Pattern background. */
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(40.075, 0.309, 0.329)), "Background color");
	/** Color of the adaptation patch. */
	public ExPar AdaptationColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(18.26, 0.64, 0.33)), "Adaptation color");
	/** Color of the test patch. */
	public ExPar MatchingColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			35.3, 0.226, 0.292)), "Test color");
	/** Horizontal pattern center position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical pattern center position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/** Number of horizontal pixels in the pattern. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(384),
			"Pattern width in number of pixels");
	/** Number of vertical pixels in the pattern. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(256),
			"Pattern height in number of pixels");
	/**
	 * Number of horizontal pixels which the patch centers are shifted from the
	 * pattern center.
	 */
	public ExPar ShiftX = new ExPar(HORSCREENSIZE, new ExParValue(32),
			"Horizontal test patch shift");
	/** Number of vertical pixels which the test patch centers are shifted. */
	public ExPar ShiftY = new ExPar(HORSCREENSIZE, new ExParValue(0),
			"Vertical test ppatch shift");
	/** Standard deviation of the Gaussian in degrees of visual angle. */
	public ExPar GaussianStandardDeviation = new ExPar(SMALL_VISUAL_ANGLE,
			new ExParValue(0.2), "Standard deviation of Gaussian");
	/**
	 * Number of pixels per degree visual angle. Used for all metric
	 * calculations.
	 */
	public ExPar PixelPerDegree = new ExPar(SCREENSIZE, new ExParValue(64),
			"Pixel per degree visual angle");
	/**
	 * Timer for controlling the ON and OFF periods. This should always be set
	 * to de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER.
	 */
	public ExPar OnOffTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"Internal cycle interval timer");
	/** Adaptation interval duration. */
	public ExPar AdaptationDuration = new ExPar(DURATION, 0, 300,
			new ExParValue(3000), "Adaptation interval duration");
	/** Test interval duration. */
	public ExPar MatchingDuration = new ExPar(DURATION, 0, 500, new ExParValue(
			600), "Test interval duration");
	/**
	 * Type of fixation mark to be shown.
	 * 
	 * @see de.pxlab.pxl.FixationCodes
	 */
	public ExPar FixationMarkType = new ExPar(GEOMETRY_EDITOR,
			FixationCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.FixationCodes.FIXATION_CROSS"),
			"Type of fixation mark");
	/** Color of an optional fixation mark. */
	public ExPar FixationMarkColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Fixation mark color");
	/** Size of a fixation marks. */
	public ExPar FixationMarkSize = new ExPar(VERSCREENSIZE,
			new ExParValue(10), "Size of fixation mark");
	/** The width of the lines. */
	public ExPar FixationMarkLineWidth = new ExPar(SMALL_SCREENSIZE,
			new ExParValue(6), "Thickness of the fixation mark lines");

	public boolean isAnimated() {
		return (true);
	}
	protected ConvexMixtureElement grating;

	public AfterEffectImage() {
		setTitleAndTopic("Color after effect image", ADAPTATION_DSP);
	}
	protected ConvexMixtureElement adapPattern, testPattern;

	protected int create() {
		adapPattern = new ConvexMixtureElement(BackgroundColor,
				AdaptationColor, FixationMarkColor);
		testPattern = new ConvexMixtureElement(BackgroundColor, MatchingColor,
				FixationMarkColor);
		int s = enterDisplayElement(adapPattern, group[0]);
		enterDisplayElement(testPattern, group[1]);
		int t = enterTiming(OnOffTimer, AdaptationDuration, 0);
		enterTiming(OnOffTimer, MatchingDuration, t);
		setFramesPerCycle(1);
		return s;
	}

	protected void computeColors() {
		// System.out.println("AfterEffectImage.computeColors()");
		// super.computeColors();
		adapPattern.computeColors();
		testPattern.computeColors();
	}

	protected void computeGeometry() {
		adapPattern.setLocation(LocationX.getInt(), LocationY.getInt());
		adapPattern.setSize(Width.getInt(), Height.getInt());
		adapPattern.setFixation(FixationMarkType.getInt(),
				FixationMarkSize.getInt());
		adapPattern.setPixelPerDegree(PixelPerDegree.getInt());
		adapPattern.setGaussianStandardDeviation(
				GaussianStandardDeviation.getDouble(),
				GaussianStandardDeviation.getDouble());
		adapPattern.setGaussianPattern(Width.getInt() / 2 - ShiftX.getInt(),
				Height.getInt() / 2 - ShiftY.getInt());
		testPattern.setLocation(LocationX.getInt(), LocationY.getInt());
		testPattern.setSize(Width.getInt(), Height.getInt());
		testPattern.setFixation(FixationMarkType.getInt(),
				FixationMarkSize.getInt());
		testPattern.setPixelPerDegree(PixelPerDegree.getInt());
		testPattern.setGaussianStandardDeviation(
				GaussianStandardDeviation.getDouble(),
				GaussianStandardDeviation.getDouble());
		testPattern.setGaussianPattern(Width.getInt() / 2 + ShiftX.getInt(),
				Height.getInt() / 2 + ShiftY.getInt());
		if (!getFullRecompute()) {
			adapPattern.computeColors();
			testPattern.computeColors();
		}
	}

	protected void destroy() {
		if (adapPattern != null)
			adapPattern.destroy();
		if (testPattern != null)
			testPattern.destroy();
		super.destroy();
	}
}
