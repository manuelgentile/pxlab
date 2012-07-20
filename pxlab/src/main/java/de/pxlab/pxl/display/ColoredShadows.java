package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Simultaes the colored shadows induction effect.
 * 
 * <P>
 * The background light comes from two sources such that the light reflected
 * from the surround is the sum of the two illuminants. A fictous object casts
 * two shadows on the background such that each shadow field reflects only light
 * from a single source. One of the sources is white light and the other is
 * chromatic. Note the induced color in the shadow which is filled with white
 * light. Should be described in every textbook.
 * 
 * <P>
 * Johann Wolfgang von Goethe (1810). Zur Farbenlehre. T�bingen.
 * 
 * @version 0.1.1
 */
/*
 * 05/15/00
 */
public class ColoredShadows extends Display {
	public ExPar BarWidth = new ExPar(PROPORTION, new ExParValue(0.25),
			"Width of the bars");
	public ExPar BarHeight = new ExPar(PROPORTION, new ExParValue(0.7),
			"Height of the bars");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Color of the first light source");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(20.0, 0.281, 0.606),
			"Color of the second light source");
	public ExPar Color3 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Additive mixture of the two light sources");

	public ColoredShadows() {
		setTitleAndTopic("Colored Shadows", COLOR_CONTRAST_DSP | DEMO);
	}
	private int s1, s2, s3;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color3));
		s2 = enterDisplayElement(new Bar(Color1));
		s3 = enterDisplayElement(new Bar(Color2));
		defaultTiming(0);
		return (s2);
	}

	protected void computeColors() {
		Color1.getPxlColor().setY(80.0);
		Color3.set(Color1.getPxlColor().add(Color2.getPxlColor()));
	}

	protected void computeGeometry() {
		computeColors();
		Rectangle r1 = largeSquare(width, height);
		int barW = (int) (0.5 * r1.width * BarWidth.getDouble());
		int barH = (int) (r1.height * BarHeight.getDouble());
		Rectangle r2 = new Rectangle((int) (-0.25 * r1.width - 0.5 * barW),
				-barH / 2, barW, barH);
		Rectangle r3 = new Rectangle((int) (0.25 * r1.width - 0.5 * barW),
				-barH / 2, barW, barH);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
	}
}
