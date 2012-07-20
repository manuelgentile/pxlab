package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Desaturation by Adaptation.
 * 
 * <P>
 * Prolonged viewing of a color field leads to desaturation.
 * 
 * <P>
 * Fixate the border for about 30 s and then switch to subgroup 1. Now both
 * fields display the same stimulus but the previously white field has a much
 * higher saturation than the adapting field (Hurvich, 1981).
 * 
 * <P>
 * Also note that there is hardly a change in appearance while one looks at the
 * color field for adaptation. This is a case for color constancy.
 * 
 * <P>
 * Hurvich, L. M. (1981). Color vision. Sunderland, MA: Sinauer.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 11/09/00
 */
public class Desaturation extends Display {
	public Desaturation() {
		setTitleAndTopic("Desaturation by Adaptation", ADAPTATION_DSP | DEMO);
	}
	public ExPar Rect1Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)),
			"Color of the first rectangle");
	public ExPar Rect2Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the second rectangle");
	public ExPar CrossColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Color of the cross");
	private int s1, s2, s3;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Rect1Color), group[0]);
		s2 = enterDisplayElement(new Bar(Rect2Color), group[0]);
		s3 = enterDisplayElement(new Cross(CrossColor), group[0]);
		return (s1);
	}

	protected void computeGeometry() {
		((Bar) getDisplayElement(s1)).setRect(firstSquareOfTwo(width, height,
				false));
		((Bar) getDisplayElement(s2)).setRect(secondSquareOfTwo(width, height,
				false));
		Cross c1 = (Cross) getDisplayElement(s3);
		c1.setRect(new Point(0, 0),
				new Dimension((int) (squareSizeOfTwo(width, height) / 16),
						(int) (squareSizeOfTwo(width, height) / 16)));
	}

	/**
	 * This stepping display is easy since we have only a single step to show.
	 */
	public void showGroup(Graphics g) {
		setGraphicsContext(g);
		// This step makes both areas the same color
		((Bar) getDisplayElement(s1)).show(Rect1Color);
		((Bar) getDisplayElement(s2)).show(Rect1Color);
		((Cross) getDisplayElement(s3)).show(CrossColor);
	}
}
