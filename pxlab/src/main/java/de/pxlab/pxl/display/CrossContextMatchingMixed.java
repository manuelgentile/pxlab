package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A cross context color matching display where the adjustable matching color is
 * a mixture of two arbitrary colors. There are two center surround fields: one
 * is the Standard field and the other is the Variable field. Usually the
 * Standard field's colors are constant and the Variable field's center color is
 * adjustable. The Variable field's center color is a convex mixture of two
 * arbitrary colors thus that the mixture may be adjusted along a single line
 * int color space. The Standard field center patch may have a smooth transition
 * area from the background to the center color.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 10/28/03
 */
public class CrossContextMatchingMixed extends Display {
	public ExPar StandardSurroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)),
			"Standard surround color");
	public ExPar StandardCenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Standard center color");
	public ExPar VariableSurroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)),
			"Variable surround color");
	public ExPar VariableCenterLowerLimitColor = new ExPar(COLOR,
			new ExParValue(new ExParExpression(ExParExpression.DARK_GRAY)),
			"Lower limit of the variable center color");
	public ExPar VariableCenterHigherLimitColor = new ExPar(COLOR,
			new ExParValue(new ExParExpression(ExParExpression.WHITE)),
			"Higher limit of the variable center color");
	/**
	 * Contains the actually presented Variable center color which is computed
	 * on-line.
	 */
	public ExPar VariableCenterColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Variable center color");
	public ExPar VariableCenterColorWeight = new ExPar(PROPORT, new ExParValue(
			0.20), "Mixture weight of the variable center color");
	// Note that the first center/surround pair is the variable
	// stimulus and the second pair is the standard
	public ExPar VariableSurroundSize = new ExPar(SCREENSIZE, new ExParValue(
			300), "Variable Field Surround Size");
	public ExPar StandardSurroundSize = new ExPar(SCREENSIZE, new ExParValue(
			300), "Standard Field Surround Size");
	public ExPar VariableCenterSize = new ExPar(SCREENSIZE,
			new ExParValue(100), "Variable Field Center Size");
	public ExPar StandardCenterSize = new ExPar(SCREENSIZE,
			new ExParValue(100), "Standard Field Center Size");
	public ExPar StandardCenterTransitionRegion = new ExPar(SCREENSIZE,
			new ExParValue(0), "Standard Field Center Transition Region Size");
	public ExPar HorizontalCenterDistance = new ExPar(SCREENSIZE,
			new ExParValue(400), "Horizontal Field Center Distance");
	public ExPar HorizontalCenterDisparity = new ExPar(SCREENPOS,
			new ExParValue(0), "Horizontal Center Field Disparity");
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
	public CrossContextMatchingMixed() {
		setTitleAndTopic("Cross Context Matching with Mixed Colors",
				COMPLEX_COLOR_MATCHING_DSP);
	}
	protected int stdS, stdC, varS, varC;
	protected int fixMarkElement;

	/** Initialize the display list of the demo. */
	protected int create() {
		stdS = enterDisplayElement(new Oval(StandardSurroundColor), group[0]);
		stdC = enterDisplayElement(new SmoothDisk(StandardCenterColor,
				StandardSurroundColor), group[0]);
		varS = enterDisplayElement(new Oval(VariableSurroundColor), group[0]);
		varC = enterDisplayElement(new Oval(VariableCenterColor), group[0]);
		fixMarkElement = enterDisplayElement(new Cross(FixationMarkColor),
				group[0]);
		defaultTiming(0);
		return (stdC);
	}

	protected void computeColors() {
		double a = VariableCenterColorWeight.getDouble();
		VariableCenterColor.set(VariableCenterHigherLimitColor.getPxlColor()
				.mix(a, VariableCenterLowerLimitColor.getPxlColor()));
		// System.out.println(a);
	}

	protected void computeGeometry() {
		computeColors();
		setPositions(-HorizontalCenterDistance.getInt() / 2,
				StandardSurroundSize.getInt(),
				HorizontalCenterDistance.getInt() / 2,
				VariableSurroundSize.getInt(),
				-HorizontalCenterDistance.getInt() / 2,
				StandardCenterSize.getInt(),
				HorizontalCenterDistance.getInt() / 2,
				VariableCenterSize.getInt());
		((SmoothDisk) getDisplayElement(stdC))
				.setTransitionRegion(StandardCenterTransitionRegion.getInt());
		Cross fixMark = (Cross) getDisplayElement(fixMarkElement);
		fixMark.setLocation(this.FixationMarkLocationX.getInt(),
				this.FixationMarkLocationY.getInt());
		int fixMarkSize = this.FixationMarkSize.getInt();
		fixMark.setSize(fixMarkSize, fixMarkSize);
		fixMark.setLineWidth(this.FixationMarkLineWidth.getInt());
	}

	private void setPositions(int stdS_cx, int stdS_wh, int varS_cx,
			int varS_wh, int stdC_cx, int stdC_wh, int varC_cx, int varC_wh) {
		getDisplayElement(stdS).setCenterAndSize(stdS_cx, 0, stdS_wh, stdS_wh);
		getDisplayElement(varS).setCenterAndSize(varS_cx, 0, varS_wh, varS_wh);
		getDisplayElement(stdC).setCenterAndSize(stdC_cx, 0, stdC_wh, stdC_wh);
		getDisplayElement(varC).setCenterAndSize(varC_cx, 0, varC_wh, varC_wh);
	}

	protected void computeStereographicGeometry(int s) {
		if (s == STEREO_NONE)
			return;
		// A left shift for the right eye image and a right shift for
		// the left eye image moves the object nearer than the
		// fixation point.
		int d = HorizontalCenterDisparity.getInt();
		if (s == STEREO_RIGHT) {
			setPositions((HorizontalCenterDistance.getInt()) / 2,
					StandardSurroundSize.getInt(),
					-(HorizontalCenterDistance.getInt()) / 2,
					VariableSurroundSize.getInt(),
					(HorizontalCenterDistance.getInt() - d) / 2,
					StandardCenterSize.getInt(),
					-(HorizontalCenterDistance.getInt() + d) / 2,
					VariableCenterSize.getInt());
		} else {
			setPositions(-(HorizontalCenterDistance.getInt()) / 2,
					StandardSurroundSize.getInt(),
					(HorizontalCenterDistance.getInt()) / 2,
					VariableSurroundSize.getInt(),
					-(HorizontalCenterDistance.getInt() + d) / 2,
					StandardCenterSize.getInt(),
					(HorizontalCenterDistance.getInt() - d) / 2,
					VariableCenterSize.getInt());
		}
	}
}
