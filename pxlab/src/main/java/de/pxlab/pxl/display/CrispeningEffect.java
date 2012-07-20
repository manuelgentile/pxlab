package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A display to investigate the crispening effect of brightness contrast.
 * 
 * @author E. Reitmayr
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 01/24/01 removed call to enterColor(PxlColor) (H. Irtel) 01/26/01 use ExPar
 * color access methods 05/04/01 added FiaxtionMark
 */
public class CrispeningEffect extends Display {
	// Note that the first center/surround pair is the variable
	// stimulus and the second pair is the standard
	public ExPar CenterSquareSize = new ExPar(PROPORT, new ExParValue(0.6),
			"Size of the center squares");
	public ExPar BorderSize = new ExPar(PROPORT, new ExParValue(0.05),
			"Size of the border");
	public ExPar VariableSurroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)),
			"Variable surround color");
	public ExPar StandardSurroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)),
			"Standard surround color");
	public ExPar BorderColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Border color");
	public ExPar StandardCenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Standard center color");
	public ExPar VariableCenterHigherLimitColor = new ExPar(COLOR,
			new ExParValue(new ExParExpression(ExParExpression.WHITE)),
			"Higher limit of the variable center color");
	public ExPar VariableCenterLowerLimitColor = new ExPar(COLOR,
			new ExParValue(new ExParExpression(ExParExpression.DARK_GRAY)),
			"Lower limit of the variable center color");
	public ExPar VariableCenterColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Variable center color");
	public ExPar VariableCenterColorWeight = new ExPar(PROPORT, new ExParValue(
			0.20), "Mixture weight of the variable center color");
	public ExPar FixationMarkColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the fixation mark");
	public ExPar FixationMarkLocationX = new ExPar(HORSCREENPOS,
			new ExParValue(0), "Horizontal location of the fixation mark");
	public ExPar FixationMarkLocationY = new ExPar(VERSCREENPOS,
			new ExParValue(0), "Vertical location of the fixation mark");
	public ExPar FixationMarkSize = new ExPar(SCREENSIZE, new ExParValue(40),
			"Size of the fixation mark");
	public ExPar FixationMarkLineWidth = new ExPar(SMALL_SCREENSIZE,
			new ExParValue(3), "Thickness of the fixation mark lines");

	/** Cunstructor creating the title of the display. */
	public CrispeningEffect() {
		setTitleAndTopic("Crispening Effect", COMPLEX_COLOR_MATCHING_DSP);
	}
	private int s1, s2, s3, s4, s5, s6;
	private int fixMarkElement;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(StandardSurroundColor), group[0]);
		s2 = enterDisplayElement(new Bar(VariableSurroundColor), group[0]);
		s3 = enterDisplayElement(new Bar(BorderColor), group[0]);
		s4 = enterDisplayElement(new Bar(BorderColor), group[0]);
		s5 = enterDisplayElement(new Bar(StandardCenterColor), group[0]);
		s6 = enterDisplayElement(new Bar(VariableCenterColor), group[0]);
		fixMarkElement = enterDisplayElement(new Cross(FixationMarkColor),
				group[0]);
		defaultTiming(0);
		return (s2);
	}

	protected void computeColors() {
		double a = VariableCenterColorWeight.getDouble();
		VariableCenterColor.set(VariableCenterHigherLimitColor.getPxlColor()
				.mix(a, VariableCenterLowerLimitColor.getPxlColor()));
		// System.out.println(a);
	}

	protected void computeGeometry() {
		computeColors();
		// get the squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		Rectangle r3 = innerRect(r1, CenterSquareSize.getDouble());
		Rectangle r4 = innerRect(r2, CenterSquareSize.getDouble());
		Rectangle r5 = innerRect(r3, (1.0 - BorderSize.getDouble()));
		Rectangle r6 = innerRect(r4, (1.0 - BorderSize.getDouble()));
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		getDisplayElement(s5).setRect(r5);
		getDisplayElement(s6).setRect(r6);
		Cross fixMark = (Cross) getDisplayElement(fixMarkElement);
		fixMark.setLocation(this.FixationMarkLocationX.getInt(),
				this.FixationMarkLocationY.getInt());
		int fixMarkSize = this.FixationMarkSize.getInt();
		fixMark.setSize(fixMarkSize, fixMarkSize);
		fixMark.setLineWidth(this.FixationMarkLineWidth.getInt());
	}
}
