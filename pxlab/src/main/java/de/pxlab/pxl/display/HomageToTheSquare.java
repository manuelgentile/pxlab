package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Can create samples of Josef Albers' Homage to the Square series.
 * 
 * @version 0.3.0
 */
public class HomageToTheSquare extends Display {
	/**
	 * Number of missing square. May be 0, 2, 3, or 4. The value 1 is treated as
	 * if it were 0.
	 */
	public ExPar MissingSquare = new ExPar(INTEGER, 0, 4, new ExParValue(0),
			"Number of missing square");
	/** Size of outer square. */
	public ExPar Size = new ExPar(VERSCREENSIZE, new ExParValue(
			new ExParExpression(ExParExpression.DIV_OP), new ExParValue(
					new ExParExpression(ExParExpression.SCREEN_HEIGHT)),
			new ExParValue(2.0)), "Square size");
	/** The horizontal center location. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** The vertical center location. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/** Color of the first and largest square. */
	public ExPar Square1Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			83.0, 0.407, 0.498)), "First square color");
	/** Color of the second square. */
	public ExPar Square2Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			72.0, 0.426, 0.493)), "Second square color");
	/** Color of the third square. */
	public ExPar Square3Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			61.2, 0.444, 0.473)), "Third square color");
	/** Color of the fourth square. */
	public ExPar Square4Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			52.0, 0.467, 0.460)), "Fourth square color");

	public HomageToTheSquare() {
		setTitleAndTopic("Homage to the Square for Painters",
				LATERAL_INHIBITION_DSP);
	}
	protected int s1, s2, s3, s4;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Square1Color));
		s2 = enterDisplayElement(new Bar(Square2Color));
		s3 = enterDisplayElement(new Bar(Square3Color));
		s4 = enterDisplayElement(new Bar(Square4Color));
		defaultTiming(0);
		return s1;
	}

	protected void computeGeometry() {
		int d = Size.getInt() / 20;
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		int m = MissingSquare.getInt();
		getDisplayElement(s1).setRect(-10 * d + x, -10 * d + y, 20 * d, 20 * d);
		getDisplayElement(s2).setRect(-8 * d + x, -7 * d + y,
				(m == 2) ? 0 : 16 * d, (m == 2) ? 0 : 16 * d);
		getDisplayElement(s3).setRect(-6 * d + x, -4 * d + y,
				(m == 3) ? 0 : 12 * d, (m == 3) ? 0 : 12 * d);
		getDisplayElement(s4).setRect(-4 * d + x, -1 * d + y,
				(m == 4) ? 0 : 8 * d, (m == 4) ? 0 : 8 * d);
	}
}
