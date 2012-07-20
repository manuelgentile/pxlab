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
 * int color space. The Stanrad field' center patch may have a smooth transition
 * area from the background to the center color.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 10/28/03
 */
public class CrossContextMatchingRectMixed extends CrossContextMatchingMixed {
	/**
	 * Defines the variable surround field color. If we have a single variable
	 * surround element then this parameter is set from the parameter
	 * VariableSurroundColor. If we have more than a single surround element
	 * then this is an array of colors which is created randomly.
	 */
	public ExPar VariableSurroundColorSet = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)),
			"Variable Surround Color Set");
	private double[] rcl_default = { 0.0, 100.0, -120.0, 120.0, -120.0, 120.0 };
	public ExPar RandomColorLimits = new ExPar(DOUBLE, new ExParValue(
			rcl_default), "Random Color Sampling Space in CIELAB");
	/** Number of patch columns. */
	public ExPar VariableSurroundColumns = new ExPar(SMALL_INT, new ExParValue(
			1), "Number of Columns in Variable Surround");
	/** Number of patch rows. */
	public ExPar VariableSurroundRows = new ExPar(SMALL_INT, new ExParValue(1),
			"Number of Rows in Variable Surround");

	/** Cunstructor creating the title of the display. */
	public CrossContextMatchingRectMixed() {
		setTitleAndTopic("Cross Context Matching Rectangles with Mixed Colors",
				COMPLEX_COLOR_MATCHING_DSP);
	}
	private boolean patternDirty;

	/** Initialize the display list of the demo. */
	protected int create() {
		stdS = enterDisplayElement(new Bar(StandardSurroundColor), group[0]);
		stdC = enterDisplayElement(new Bar(StandardCenterColor), group[0]);
		varS = enterDisplayElement(new BarPattern(VariableSurroundColor,
				VariableSurroundColorSet), group[0]);
		varC = enterDisplayElement(new Bar(VariableCenterColor), group[0]);
		fixMarkElement = enterDisplayElement(new Cross(FixationMarkColor),
				group[0]);
		defaultTiming(0);
		patternDirty = true;
		return (stdC);
	}
	private RandomColor rc;

	protected void computeColors() {
		super.computeColors();
		int n = VariableSurroundRows.getInt()
				* VariableSurroundColumns.getInt();
		if ((n > 1) && patternDirty) {
			if (rc == null)
				rc = new RandomColor(PxlColor.CS_LabStar);
			double[] rcl = new double[6];
			System.arraycopy(rcl_default, 0, rcl, 0, 6);
			double[] rcl_d = RandomColorLimits.getDoubleArray();
			for (int i = 0; (i < rcl_d.length) && (i < 6); i++)
				rcl[i] = rcl_d[i];
			rc.setLimits(rcl);
			PxlColor[] c = rc.nextPxlColor(n);
			double[] a = new double[n + n + n];
			for (int i = 0; i < n; i++) {
				// System.out.println("CrossContextMatchingRectMixed.computeColors(): "
				// + c[i]);
				a[i + i + i + 0] = c[i].getY();
				a[i + i + i + 1] = c[i].getx();
				a[i + i + i + 2] = c[i].gety();
			}
			VariableSurroundColorSet.set(a);
			patternDirty = false;
		}
		if (n == 1) {
			VariableSurroundColorSet.set(VariableSurroundColor.getPxlColor());
		}
	}

	protected void finished() {
		patternDirty = true;
	}

	protected void computeGeometry() {
		computeColors();
		getDisplayElement(stdS).setCenterAndSize(
				-HorizontalCenterDistance.getInt() / 2, 0,
				StandardSurroundSize.getInt(), StandardSurroundSize.getInt());
		getDisplayElement(varS).setCenterAndSize(
				HorizontalCenterDistance.getInt() / 2, 0,
				VariableSurroundSize.getInt(), VariableSurroundSize.getInt());
		((BarPattern) getDisplayElement(varS))
				.setRowsColumns(VariableSurroundRows.getInt(),
						VariableSurroundColumns.getInt());
		getDisplayElement(stdC).setCenterAndSize(
				-(HorizontalCenterDistance.getInt()) / 2, 0,
				StandardCenterSize.getInt(), StandardCenterSize.getInt());
		getDisplayElement(varC).setCenterAndSize(
				(HorizontalCenterDistance.getInt()) / 2, 0,
				VariableCenterSize.getInt(), VariableCenterSize.getInt());
		Cross fixMark = (Cross) getDisplayElement(fixMarkElement);
		fixMark.setLocation(this.FixationMarkLocationX.getInt(),
				this.FixationMarkLocationY.getInt());
		int fixMarkSize = this.FixationMarkSize.getInt();
		fixMark.setSize(fixMarkSize, fixMarkSize);
		fixMark.setLineWidth(this.FixationMarkLineWidth.getInt());
	}
}
