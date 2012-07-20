package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Two patches, one for adaptation and the other one for matching it to the
 * afterimage. May be shown continuously.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/06/30
 */
public class AfterEffectPatch extends ColorAdjustableHSB {
	/** Color of the adaptation patch. */
	public ExPar AdaptationColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(21.26, 0.64, 0.33)), "Adaptation color");
	/** Color of the test patch. */
	public ExPar MatchingColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			48.3, 0.226, 0.292)), "Test color");
	/** Horizontal pattern center position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical pattern center position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	public ExPar Size = new ExPar(VERSCREENSIZE, new ExParValue(32),
			"Patch size");
	/**
	 * Number of horizontal pixels which the patch centers are shifted from the
	 * pattern center.
	 */
	public ExPar ShiftX = new ExPar(HORSCREENSIZE, new ExParValue(48),
			"Horizontal test patch shift");
	/** Number of vertical pixels which the test patch centers are shifted. */
	public ExPar ShiftY = new ExPar(HORSCREENSIZE, new ExParValue(0),
			"Vertical test ppatch shift");
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
			new ExParValue(16), "Size of fixation mark");
	/** The width of the lines. */
	public ExPar FixationMarkLineWidth = new ExPar(SMALL_SCREENSIZE,
			new ExParValue(6), "Thickness of the fixation mark lines");

	public boolean isAnimated() {
		return (true);
	}

	public AfterEffectPatch() {
		setTitleAndTopic("Color after effect patch", ADAPTATION_DSP);
	}
	protected Oval adapPattern, testPattern;
	protected FixationMarkElement fixMark;

	protected int create() {
		adapPattern = new Oval(AdaptationColor);
		testPattern = new Oval(MatchingColor);
		int s = enterDisplayElement(adapPattern, group[0]);
		enterDisplayElement(testPattern, group[1]);
		int t = enterTiming(OnOffTimer, AdaptationDuration, 0);
		enterTiming(OnOffTimer, MatchingDuration, t);
		fixMark = new FixationMarkElement(FixationMarkColor);
		enterDisplayElement(fixMark, group[0] + group[1]);
		setFramesPerCycle(1);
		return s;
	}

	protected void computeColors() {
	}

	protected void computeGeometry() {
		int w = Size.getInt();
		int h = Size.getInt();
		int sx = ShiftX.getInt();
		int sy = ShiftY.getInt();
		adapPattern.setRect(LocationX.getInt() - w / 2 - sx, LocationY.getInt()
				- h / 2 - sy, w, h);
		testPattern.setRect(LocationX.getInt() - w / 2 + sx, LocationY.getInt()
				- h / 2 + sy, w, h);
		int s = FixationMarkSize.getInt();
		fixMark.setProperties(LocationX.getInt(), LocationY.getInt(), 1, 1,
				FixationMarkType.getInt(), s, s, FixationMarkLineWidth.getInt());
	}
}
