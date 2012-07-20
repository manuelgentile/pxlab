package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * An image pattern which is a convex mixture of two colors.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 2005/09/20
 */
abstract public class ConvexMixturePattern extends Display {
	/**
	 * Mean amplitude color. This is the color corresponding to a pattern
	 * amplitude of 0.5. It is only used if the parameter MeanColorFixed is
	 * true.
	 */
	public ExPar MeanColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Mean amplitude color");
	/**
	 * Maximum amplitude color. This is the color corresponding to a pattern
	 * amplitude of 1.0.
	 */
	public ExPar HighColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Maximum amplitude color");
	/**
	 * Minimum amplitude color. This is the color corresponding to a pattern
	 * amplitude of 0.0. If this parameter is used then MeanColor is computed as
	 * the convex mixture of LowColor and HighColor. This parameter is used if
	 * MeanColorFixed is false.
	 */
	public ExPar LowColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Minimum amplitude color");
	/**
	 * If true then the mean color is fixed and the low color is computed. If
	 * false then the low color is fixed and the mean color is computed.
	 */
	public ExPar MeanColorFixed = new ExPar(FLAG, new ExParValue(1),
			"Mean color is fixed, low color is computed");
	/** Pattern width. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(256),
			"Pattern width");
	/** Pattern height. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(256),
			"Pattern height");
	/** Horizontal center position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical center position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/**
	 * Type of fixation mark to be shown.
	 * 
	 * @see de.pxlab.pxl.FixationCodes
	 */
	public ExPar FixationType = new ExPar(GEOMETRY_EDITOR, FixationCodes.class,
			new ExParValueConstant("de.pxlab.pxl.FixationCodes.NO_FIXATION"),
			"Type of fixation mark");
	/** Color of an optional fixation mark. */
	public ExPar FixationColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Fixation mark color");
	/** Size of a fixation marks. */
	public ExPar FixationSize = new ExPar(VERSCREENSIZE, new ExParValue(10),
			"Size of fixation mark");
	protected ConvexMixtureElement grating;

	protected int create() {
		grating = new ConvexMixtureElement(LowColor, HighColor, FixationColor);
		int s = enterDisplayElement(grating);
		defaultTiming(0);
		return s;
	}

	protected void computeColors() {
		PxlColor high = HighColor.getPxlColor();
		if (MeanColorFixed.getFlag()) {
			double[] m = MeanColor.getPxlColor().getComponents();
			double[] h = high.getComponents();
			LowColor.set(new PxlColor(2 * m[0] - h[0], 2 * m[1] - h[1], 2
					* m[2] - h[2]));
		} else {
			PxlColor low = LowColor.getPxlColor();
			MeanColor.set(high.mix(low));
		}
		/*
		 * System.out.println("ConvexMixturePattern.computeColors():  LowColor = "
		 * + LowColor);
		 * System.out.println("ConvexMixturePattern.computeColors(): MeanColor = "
		 * + MeanColor);
		 * System.out.println("ConvexMixturePattern.computeColors(): HighColor = "
		 * + HighColor);
		 */
		grating.computeColors();
	}

	protected void destroy() {
		if (grating != null)
			grating.destroy();
		super.destroy();
	}
}
