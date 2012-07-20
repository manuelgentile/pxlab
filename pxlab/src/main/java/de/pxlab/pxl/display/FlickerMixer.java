package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A color mixer which uses photometric flickering.
 * 
 * @author E. Reitmayr
 * @version 0.2.0
 */
/*
 * 06/02/00
 */
public class FlickerMixer extends FrameAnimation {
	public ExPar FirstColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)), "First Color");
	public ExPar SecondColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)), "Second Color");
	public ExPar MixtureColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Mixture of Color 1 and 2");
	private ExPar Color1 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "");
	private ExPar Color2 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "");

	public FlickerMixer() {
		setTitleAndTopic("Flicker photometric color mixer", PHOTOMETRY_DSP
				| DEMO);
		FramesPerCycle.setType(RTDATA);
	}
	private int s1, s2, s3;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1), group[0]);
		s2 = enterDisplayElement(new Bar(Color2), group[0]);
		s3 = enterDisplayElement(new Bar(MixtureColor), group[0]);
		enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(2);
		return s1;
	}

	protected void computeColors() {
		Color1.set((PxlColor) (FirstColor.getPxlColor().clone()));
		Color2.set((PxlColor) (SecondColor.getPxlColor().clone()));
		MixtureColor.set(FirstColor.getPxlColor()
				.mix(SecondColor.getPxlColor()));
	}

	protected void computeGeometry() {
		// computeColors();
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect((int) (r1.x + r1.width / 2), r1.y,
				(int) (r1.width / 2), r1.height);
		getDisplayElement(s3).setRect(r2);
	}

	public void computeAnimationFrame(int frame) {
		if ((frame % 2) == 0) {
			Color1.set(FirstColor.getPxlColor());
			Color2.set(FirstColor.getPxlColor());
		} else {
			Color1.set(SecondColor.getPxlColor());
			Color2.set(SecondColor.getPxlColor());
		}
	}
}
