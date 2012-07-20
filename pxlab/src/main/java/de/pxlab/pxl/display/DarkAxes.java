package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * An extension of Chevreul's stairs with induced dark diagonals.
 * 
 * <P>
 * This demonstration is similiar to Chevreul's stairs of gray. Here contrast
 * enhancement is especially high at the corners since these have two contours
 * surrounding them. Thus the cornes appear darker than the other parts.
 * Filtering out the high spatial frequencies by nearly closing the eyes
 * enhances the "glowing" axes.
 * 
 * <P>
 * The image is drawn after "Arcturus" by Victor Vasarely.
 * 
 * <P>
 * Hurvich, L. M. (1981). Color vision. Sunderland, MA: Sinauer.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 05/18/00
 */
public class DarkAxes extends Display {
	public DarkAxes() {
		setTitleAndTopic("Dark Axes", LATERAL_INHIBITION_DSP | DEMO);
	}
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "First Color of the dark axes");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Last Color of the dark axes");
	public ExPar Color3 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 2 of the dark axes");
	public ExPar Color4 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 3 of the dark axes");
	public ExPar Color5 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 4 of the dark axes");
	public ExPar Color6 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 5 of the dark axes");
	public ExPar Color7 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 6 of the dark axes");
	public ExPar Color8 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color 7 of the dark axes");
	private int s1, s2, s3, s4, s5, s6, s7, s8;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1), group[0] + group[1]
				+ group[2] + group[3] + group[4] + group[5] + group[6]
				+ group[7]);
		s2 = enterDisplayElement(new Bar(Color3), group[1] + group[2]
				+ group[3] + group[4] + group[5] + group[6] + group[7]);
		s3 = enterDisplayElement(new Bar(Color4), group[2] + group[3]
				+ group[4] + group[5] + group[6] + group[7]);
		s4 = enterDisplayElement(new Bar(Color5), group[3] + group[4]
				+ group[5] + group[6] + group[7]);
		s5 = enterDisplayElement(new Bar(Color6), group[4] + group[5]
				+ group[6] + group[7]);
		s6 = enterDisplayElement(new Bar(Color7), group[5] + group[6]
				+ group[7]);
		s7 = enterDisplayElement(new Bar(Color8), group[6] + group[7]);
		s8 = enterDisplayElement(new Bar(Color2), group[7]);
		return (s1);
	}

	protected void computeColors() {
		PxlColor[] ramp = Color1.getPxlColor().linearRampTo(
				Color2.getPxlColor(), 8);
		Color3.set(ramp[1]);
		Color4.set(ramp[2]);
		Color5.set(ramp[3]);
		Color6.set(ramp[4]);
		Color7.set(ramp[5]);
		Color8.set(ramp[6]);
	}

	protected void computeGeometry() {
		int size1 = 32 * ((width < height) ? width : height) / 36;
		int size2 = 28 * ((width < height) ? width : height) / 36;
		int size3 = 24 * ((width < height) ? width : height) / 36;
		int size4 = 20 * ((width < height) ? width : height) / 36;
		int size5 = 16 * ((width < height) ? width : height) / 36;
		int size6 = 12 * ((width < height) ? width : height) / 36;
		int size7 = 8 * ((width < height) ? width : height) / 36;
		int size8 = 4 * ((width < height) ? width : height) / 36;
		Rectangle r1 = centeredSquare(width, height, size1);
		Rectangle r2 = centeredSquare(width, height, size2);
		Rectangle r3 = centeredSquare(width, height, size3);
		Rectangle r4 = centeredSquare(width, height, size4);
		Rectangle r5 = centeredSquare(width, height, size5);
		Rectangle r6 = centeredSquare(width, height, size6);
		Rectangle r7 = centeredSquare(width, height, size7);
		Rectangle r8 = centeredSquare(width, height, size8);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		getDisplayElement(s5).setRect(r5);
		getDisplayElement(s6).setRect(r6);
		getDisplayElement(s7).setRect(r7);
		getDisplayElement(s8).setRect(r8);
	}
}
