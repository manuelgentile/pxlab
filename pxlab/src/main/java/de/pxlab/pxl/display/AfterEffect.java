package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * An abstract superclass for after effect matching of two patches, one for
 * adaptation and the other one for matching it to the afterimage. May be shown
 * continuously.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/06/30
 */
abstract public class AfterEffect extends Display {
	/** Pattern background. */
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Background color");
	/** Color of the adaptation patch. */
	public ExPar AdaptationColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.CYAN)), "Adaptation color");
	/** Color of the test patch. */
	public ExPar MatchingColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Test color");
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
	public ExPar FixationType = new ExPar(
			GEOMETRY_EDITOR,
			FixationCodes.class,
			new ExParValueConstant("de.pxlab.pxl.FixationCodes.FIXATION_CROSS"),
			"Type of fixation mark");
	/** Color of an optional fixation mark. */
	public ExPar FixationColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Fixation mark color");
	/** Size of a fixation marks. */
	public ExPar FixationSize = new ExPar(VERSCREENSIZE, new ExParValue(10),
			"Size of fixation mark");

	public boolean isAnimated() {
		return (true);
	}
}
