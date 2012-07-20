package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This works as a superclass for computer monitor adaptations of E G
 * Heinemann's classical experiment on brightness induction.
 * 
 * <p>
 * Heinemann, E. G. (1955). Simultanous brightness induction as a function of
 * inducing- and test-field luminance. Journal of Experimental Psychology, 50,
 * 89-96.
 * 
 * @author H. Irtel
 * @version 0.4.0
 */
abstract public class BrightnessInduction extends Display {
	// Note that the first center/surround pair is the variable
	// stimulus and the second pair is the standard
	public ExPar StandardSurroundSize = new ExPar(SCREENSIZE, new ExParValue(
			240), "Standard surround field size");
	public ExPar StandardCenterSize = new ExPar(PROPORT, new ExParValue(0.3),
			"Standard center patch size");
	public ExPar VariableSurroundSize = new ExPar(SCREENSIZE, new ExParValue(
			240), "Variable surround field size");
	public ExPar VariableCenterSize = new ExPar(PROPORT, new ExParValue(0.3),
			"Variable center patch size");
	public ExPar SurroundDistance = new ExPar(SCREENSIZE, new ExParValue(50),
			"Distance between surround fields");
	public ExPar StandardSurroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)),
			"Standard surround color");
	public ExPar StandardCenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Standard center color");
	public ExPar VariableSurroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)),
			"Variable surround color");
	public ExPar VariableCenterHigherLimitColor = new ExPar(COLOR,
			new ExParValue(new ExParExpression(ExParExpression.WHITE)),
			"Higher limit of the variable center color");
	public ExPar VariableCenterLowerLimitColor = new ExPar(COLOR,
			new ExParValue(new ExParExpression(ExParExpression.DARK_GRAY)),
			"Lower limit of the variable center color");
	public ExPar VariableCenterColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Computed variable center color");
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
	protected int s1, s2, s3, s4, fixMarkElement;

	protected void computeColors() {
		VariableCenterColor.set(VariableCenterHigherLimitColor.getPxlColor()
				.mix(VariableCenterColorWeight.getDouble(),
						VariableCenterLowerLimitColor.getPxlColor()));
	}

	protected void computeGeometry() {
		computeColors();
		int d = SurroundDistance.getInt();
		int sss = StandardSurroundSize.getInt();
		Rectangle r1 = centeredSquare(width, height, sss);
		r1.x = -sss - d / 2;
		Rectangle r2 = innerRect(r1, StandardCenterSize.getDouble());
		int vss = VariableSurroundSize.getInt();
		Rectangle r3 = centeredSquare(width, height, vss);
		r3.x = d / 2;
		Rectangle r4 = innerRect(r3, VariableCenterSize.getDouble());
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		Cross fixMark = (Cross) getDisplayElement(fixMarkElement);
		fixMark.setLocation(this.FixationMarkLocationX.getInt(),
				this.FixationMarkLocationY.getInt());
		int fixMarkSize = this.FixationMarkSize.getInt();
		fixMark.setSize(fixMarkSize, fixMarkSize);
		fixMark.setLineWidth(this.FixationMarkLineWidth.getInt());
	}
}
