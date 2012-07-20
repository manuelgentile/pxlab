package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This is a dithering test image with a dithered center and a normal surround
 * both showing the same color.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class DitherTest extends Display {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Field color");
	public ExPar CenterSize = new ExPar(PROPORT, new ExParValue(0.33),
			"Foreground square size");
	public ExPar SurroundSize = new ExPar(PROPORT, new ExParValue(0.8),
			"Background square size");
	public ExPar DitheringType = new ExPar(GEOMETRY_EDITOR,
			DitheringCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.DitheringCodes.ORDERED_DITHERING_4X4"),
			"Type of Dithering");

	public DitherTest() {
		setTitleAndTopic("Dither Test", DISPLAY_TEST_DSP | DEMO);
	}
	private int s1, s2;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color), group[0]);
		s2 = enterDisplayElement(new Bar(Color), group[0] + group[1]);
		/*
		 * enterTiming(this.Timer, this.Duration, this.ResponseSet, 0,
		 * this.ResponseTime, this.ResponseCode); enterTiming(this.Timer,
		 * this.Duration, this.ResponseSet, 1, this.ResponseTime,
		 * this.ResponseCode);
		 */
		return s2;
	}

	protected void computeGeometry() {
		int size1 = relSquareSize(width, height, SurroundSize.getDouble());
		int size2 = relSquareSize(size1, size1, CenterSize.getDouble());
		getDisplayElement(s1).setRect(centeredSquare(width, height, size1));
		getDisplayElement(s2).setDither(DitheringType.getInt());
		getDisplayElement(s2).setRect(centeredSquare(width, height, size2));
	}
}
