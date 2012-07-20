package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A series of color patches connecting two points in color space.
 * 
 * <P>
 * The color line contains convex mixtures of the two colors at the end points.
 * These may be selected and adjusted.
 * 
 * <P>
 * Setting one of the endpoints to white allows demonstration of the
 * Abney-Effect: In a mixture of white with a highly saturated color one may
 * find hues, which are not contained in the mixture components. Lines of
 * constant hue are not always straight in color space, but may be curved. Thus
 * the color line may contain hues that are different from the end points.
 * 
 * <P>
 * Setting one of the end points to black may be used to demonstrate the
 * Bezold-Br�cke hue shift: Changing a color stimulus' intensity level may also
 * change its hue (cf. "Plain Color Field", Wyszecki, 1986).
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 05/18/00
 */
public class ColoredLine extends Display {
	public ColoredLine() {
		setTitleAndTopic("Colored Line", LATERAL_INHIBITION_DSP | DEMO);
	}
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.YELLOW)), "First Color of the colored line");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLUE)), "Last Color of the colored line");
	public ExPar Color3 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 2 of the colored line");
	public ExPar Color4 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 3 of the colored line");
	public ExPar Color5 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 4 of the colored line");
	public ExPar Color6 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 5 of the colored line");
	public ExPar Color7 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 6 of the colored line");
	public ExPar Color8 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 7 of the colored line");
	public ExPar Color9 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 8 of the colored line");
	public ExPar Color10 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 9 of the colored line");
	public ExPar Color11 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 10 of the colored line");
	public ExPar Color12 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 11 of the colored line");
	private int s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1), group[0]);
		s2 = enterDisplayElement(new Bar(Color3), group[0]);
		s3 = enterDisplayElement(new Bar(Color4), group[0]);
		s4 = enterDisplayElement(new Bar(Color5), group[0]);
		s5 = enterDisplayElement(new Bar(Color6), group[0]);
		s6 = enterDisplayElement(new Bar(Color7), group[0]);
		s7 = enterDisplayElement(new Bar(Color8), group[0]);
		s8 = enterDisplayElement(new Bar(Color9), group[0]);
		s9 = enterDisplayElement(new Bar(Color10), group[0]);
		s10 = enterDisplayElement(new Bar(Color11), group[0]);
		s11 = enterDisplayElement(new Bar(Color12), group[0]);
		s12 = enterDisplayElement(new Bar(Color2), group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeColors() {
		PxlColor[] ramp = Color1.getPxlColor().linearRampTo(
				Color2.getPxlColor(), 12);
		Color3.set(ramp[1]);
		Color4.set(ramp[2]);
		Color5.set(ramp[3]);
		Color6.set(ramp[4]);
		Color7.set(ramp[5]);
		Color8.set(ramp[6]);
		Color9.set(ramp[7]);
		Color10.set(ramp[8]);
		Color11.set(ramp[9]);
		Color12.set(ramp[10]);
	}

	protected void computeGeometry() {
		computeColors();
		// get the squares
		int columns = 1;
		int rows = 12;
		Rectangle r = centeredRect((int) (width), (int) (height),
				(int) (4 * width / 5.0), (int) (12 * height / 18.0));
		Rectangle[] r1 = rectPattern(r, columns, rows, 0, 0, 0, 0);
		getDisplayElement(s1).setRect(r1[0]);
		getDisplayElement(s2).setRect(r1[1]);
		getDisplayElement(s3).setRect(r1[2]);
		getDisplayElement(s4).setRect(r1[3]);
		getDisplayElement(s5).setRect(r1[4]);
		getDisplayElement(s6).setRect(r1[5]);
		getDisplayElement(s7).setRect(r1[6]);
		getDisplayElement(s8).setRect(r1[7]);
		getDisplayElement(s9).setRect(r1[8]);
		getDisplayElement(s10).setRect(r1[9]);
		getDisplayElement(s11).setRect(r1[10]);
		getDisplayElement(s12).setRect(r1[11]);
	}
}
