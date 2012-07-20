package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A filled rectangle centered at its location.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * 07/25/01
 * 
 * 2005/11/16 allow dithering
 */
public class SimpleBar extends ColorAdjustableHSB {
	/** The color of the bar. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Bar color");
	/** The width of the bar. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(100),
			"Bar width");
	/** The height of the bar. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(100),
			"Bar height");
	/** The horizontal center location of the bar. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** The vertical center location of the bar. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/**
	 * The size of the dithering matrix used to increase color resolution.
	 */
	public ExPar DitheringType = new ExPar(GEOMETRY_EDITOR,
			DitheringCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.DitheringCodes.NO_DITHERING"),
			"Type of dithering");

	public SimpleBar() {
		setTitleAndTopic("Simple Colored Bar", SIMPLE_GEOMETRY_DSP);
	}
	protected int bar_idx;

	protected int create() {
		bar_idx = enterDisplayElement(new Bar(this.Color), group[0]);
		defaultTiming(0);
		return (bar_idx);
	}

	protected void computeGeometry() {
		DisplayElement bar = getDisplayElement(bar_idx);
		int w = Width.getInt();
		int h = Height.getInt();
		bar.setRect(LocationX.getInt() - w / 2, LocationY.getInt() - h / 2, w,
				h);
		bar.setDither(DitheringType.getInt());
	}
}
