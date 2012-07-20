package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A Benham color mixer simulation.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/02/16
 */
public class BenhamColorMixer extends FrameAnimation {
	public ExPar SquareColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)),
			"Background square color");
	public ExPar BarColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Target bar color");
	private ExPar DesaturationColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "Desaturation color");
	private ExPar Color1 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)), "");
	private ExPar Color2 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "");
	public ExPar BarSize = new ExPar(PROPORT, new ExParValue(0.2),
			"Relative size of bar");

	public BenhamColorMixer() {
		setTitleAndTopic("Benham color mixer", PHOTOMETRY_DSP | DEMO);
		FramesPerCycle.setType(RTDATA);
	}
	private int s1, s2;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1), group[0]);
		s2 = enterDisplayElement(new Bar(Color2), group[0]);
		enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(2);
		return s1;
	}

	protected void computeColors() {
		Color1.set((PxlColor) (SquareColor.getPxlColor().clone()));
		Color2.set((PxlColor) (BarColor.getPxlColor().clone()));
	}

	protected void computeGeometry() {
		Rectangle r1 = largeSquare(width, height);
		Rectangle r2 = innerRect(r1, BarSize.getDouble());
		r2.x = r1.x;
		r2.width = r1.width;
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
	}

	public void computeAnimationFrame(int frame) {
		if ((frame % 2) == 0) {
			Color1.set(SquareColor.getPxlColor());
			Color2.set(BarColor.getPxlColor());
		} else {
			Color1.set(DesaturationColor.getPxlColor());
			Color2.set(DesaturationColor.getPxlColor());
		}
	}
}
