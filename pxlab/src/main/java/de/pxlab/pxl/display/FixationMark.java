package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A general fixation mark. Supported mark types are cros, dor, and corner
 * marks.
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
/*
 * 
 * 01/26/01 use direct ExPar color access
 * 
 * 2005/09/13 enhanced to support various types of marks.
 */
public class FixationMark extends Display {
	/** Color of the fixation mark. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Color of the fixation mark");
	/** Horizontal position of the fixation mark center. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal location of the fixation mark");
	/** Vertical position of the fixation mark center. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical location of the fixation mark");
	/** The horizontal and vertical size of the fixation mark. */
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(40),
			"Size of the fixation mark");
	/**
	 * The type of the fixation mark.
	 * 
	 * @see de.pxlab.pxl.FixationCodes
	 */
	public ExPar Type = new ExPar(
			GEOMETRY_EDITOR,
			FixationCodes.class,
			new ExParValueConstant("de.pxlab.pxl.FixationCodes.FIXATION_CROSS"),
			"Type of fixation mark");
	/**
	 * The width of the rectangle enclosed by fixation marks of type
	 * CORNER_MARKS.
	 */
	public ExPar EnclosedWidth = new ExPar(SCREENSIZE, new ExParValue(100),
			"Enclosed rectangle width");
	/**
	 * The height of the rectangle enclosed by fixation marks of type
	 * CORNER_MARKS.
	 */
	public ExPar EnclosedHeight = new ExPar(SCREENSIZE, new ExParValue(100),
			"Enclosed rectangle height");
	/** The width of the lines. */
	public ExPar LineWidth = new ExPar(SMALL_SCREENSIZE, new ExParValue(6),
			"Thickness of the fixation mark lines");

	public FixationMark() {
		setTitleAndTopic("Fixation Mark", ATTEND_DSP | EXP);
	}
	private int fixMarkElement;

	protected int create() {
		fixMarkElement = enterDisplayElement(new FixationMarkElement(Color),
				group[0]);
		defaultTiming(0);
		return (fixMarkElement);
	}

	protected void computeGeometry() {
		int s = Size.getInt();
		((FixationMarkElement) getDisplayElement(fixMarkElement))
				.setProperties(LocationX.getInt(), LocationY.getInt(),
						EnclosedWidth.getInt(), EnclosedHeight.getInt(),
						Type.getInt(), s, s, LineWidth.getInt());
	}
}
