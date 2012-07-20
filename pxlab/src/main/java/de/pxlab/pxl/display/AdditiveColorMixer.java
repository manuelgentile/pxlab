package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Simulates an additive color mixer by projecting three light sources on top of
 * each other. Intensities and colors of the light sources may be adjusted and
 * the mixture lights are computed automatically.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 05/03/00
 */
public class AdditiveColorMixer extends Display {
	/** Red source color. */
	public ExPar Square1Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)),
			"Color of the First Square");
	/** Green source color. */
	public ExPar Square2Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)),
			"Color of the Second Square");
	/** Blue source color. */
	public ExPar Square3Color = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)),
			"Color of the Third Square");
	/** First and second source mixture color. */
	public ExPar Dep1Color = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"First Dependent Color");
	/** Second and third source mixture color. */
	public ExPar Dep2Color = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Second Dependent Color");
	/** First and third source mixture color. */
	public ExPar Dep3Color = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Third Dependent Color");
	/** All sources mixture color. */
	public ExPar Dep4Color = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Fourth Dependent Color");

	public AdditiveColorMixer() {
		setTitleAndTopic("Additive color mixer", PHOTOMETRY_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Square1Color), group[0]);
		s2 = enterDisplayElement(new Bar(Square2Color), group[0]);
		s3 = enterDisplayElement(new Bar(Square3Color), group[0]);
		s4 = enterDisplayElement(new Bar(Dep1Color), group[0]);
		s5 = enterDisplayElement(new Bar(Dep2Color), group[0]);
		s6 = enterDisplayElement(new Bar(Dep3Color), group[0]);
		s7 = enterDisplayElement(new Bar(Dep4Color), group[0]);
		defaultTiming();
		return (s1);
	}

	protected void computeColors() {
		Dep1Color.set(Square1Color.getPxlColor()
				.add(Square2Color.getPxlColor()));
		Dep2Color.set(Square2Color.getPxlColor()
				.add(Square3Color.getPxlColor()));
		Dep3Color.set(Square3Color.getPxlColor()
				.add(Square1Color.getPxlColor()));
		Dep4Color.set(Dep1Color.getPxlColor().add(Square3Color.getPxlColor()));
	}

	protected void computeGeometry() {
		computeColors();
		int s = mediumSquareSize(width, height);
		int d = s / 6;
		s = 6 * d;
		int x = -5 * d;
		int y = -5 * d;
		((Bar) getDisplayElement(s1)).setRect(x, y, s, s);
		((Bar) getDisplayElement(s2)).setRect(x + 4 * d, y + 2 * d, s, s);
		((Bar) getDisplayElement(s3)).setRect(x + 2 * d, y + 4 * d, s, s);
		((Bar) getDisplayElement(s4)).setRect(x + 4 * d, y + 2 * d, 2 * d,
				2 * d);
		((Bar) getDisplayElement(s5)).setRect(x + 4 * d, y + 4 * d, 4 * d,
				4 * d);
		((Bar) getDisplayElement(s6)).setRect(x + 2 * d, y + 4 * d, 2 * d,
				2 * d);
		((Bar) getDisplayElement(s7)).setRect(x + 4 * d, y + 4 * d, 2 * d,
				2 * d);
	}
}
