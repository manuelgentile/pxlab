package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A target center field with a surround of up to 8 different subfields. The
 * context is partitioned by radial sections into 2, 4, or 8 subfields.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * NOTE: Es scheint dass die Reihenfolge der Argumente in
 * colorTable[0].set(Field1Color.getPxlColor().mix(contrast, ec)); falsch
 * ist!!!! Es muss heissen: colorTable[0].set(ec.getPxlColor().mix(contrast,
 * Field1Color));
 * 
 * Seems that the above note is wrong since contrast==0 correctly leads to
 * showing the EqivalentContextColor only.
 * 
 * 08/12/01
 * 
 * 07/09/02 fixed bug for repeated displays.
 * 
 * 11/16/02 made it color adjustable
 */
public class ComplexColorContext extends ColorAdjustableHSB {
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(8.62, 0.313, 0.329)), "Background color");
	public ExPar TargetColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			31.62, 0.313, 0.329)), "Target field color");
	public ExPar Field1Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			11.20, 0.313, 0.329)), "Color of context field 1");
	public ExPar Field2Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			17.78, 0.313, 0.329)), "Color of context field 2");
	public ExPar Field3Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			19.95, 0.313, 0.329)), "Color of context field 3");
	public ExPar Field4Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			28.18, 0.313, 0.329)), "Color of context field 4");
	public ExPar Field5Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			35.48, 0.313, 0.329)), "Color of context field 5");
	public ExPar Field6Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			50.12, 0.313, 0.329)), "Color of context field 6");
	public ExPar Field7Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			56.23, 0.313, 0.329)), "Color of context field 7");
	public ExPar Field8Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			89.12, 0.313, 0.329)), "Color of context field 8");
	public ExPar EquivalentContextColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(31.62, 0.313, 0.329)), "Equivalent context color");
	public ExPar EquivalentContextLuminance = new ExPar(DOUBLE, 0.0, 100.0,
			new ExParValue(40.0), "Equivalent Context Luminance");
	public ExPar LuminanceFlag = new ExPar(FLAG, new ExParValue(0),
			"Restriction to Luminance");
	public ExPar FieldSize = new ExPar(SCREENSIZE, new ExParValue(500),
			"Context field size");
	public ExPar TargetSize = new ExPar(SCREENSIZE, new ExParValue(200),
			"Target field size");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position");
	public ExPar Contrast = new ExPar(PROPORT, new ExParValue(1.0),
			"Contrast factor");
	public ExPar NumberOfContextFields = new ExPar(1, 8, new ExParValue(8),
			"Number of context fields");
	protected int firstField;
	protected int target = 0;
	protected int nFields = 0;
	protected int locX = -1, locY = -1, fieldSize = 0, targetSize = 0;
	protected ExPar[] colorTable;
	protected double contrast = -1.0;

	public ComplexColorContext() {
		setTitleAndTopic("Complex color context", COMPLEX_COLOR_MATCHING_DSP);
	}

	protected int create() {
		// System.out.println("ComplexColorContext.create()");
		setBackgroundColorPar(BackgroundColor);
		colorTable = new ExPar[8];
		for (int i = 0; i < 8; i++) {
			colorTable[i] = new ExPar(DEPCOLOR, new ExParValueNotSet(), "");
		}
		firstField = nextDisplayElementIndex();
		nFields = 0;
		defaultTiming(0);
		return (firstField);
	}

	protected void computeColors() {
		super.computeColors();
		PxlColor ec = EquivalentContextColor.getPxlColor();
		if (LuminanceFlag.getFlag()) {
			double eLum = EquivalentContextLuminance.getDouble();
			if (eLum < 0.0)
				eLum = 0.0;
			ec.setY(eLum);
		}
		// System.out.println("ComplexColorContext.computeColors() ec = " + ec);
		colorTable[0].set(Field1Color.getPxlColor().mix(contrast, ec));
		// System.out.println("ComplexColorContext.computeColors() contrast = "
		// + contrast);
		// System.out.println("ComplexColorContext.computeColors() c1 = " +
		// colorTable[0]);
		if (nFields > 1) {
			colorTable[1].set(Field2Color.getPxlColor().mix(contrast, ec));
			if (nFields > 2) {
				colorTable[2].set(Field3Color.getPxlColor().mix(contrast, ec));
				colorTable[3].set(Field4Color.getPxlColor().mix(contrast, ec));
				if (nFields > 4) {
					colorTable[4].set(Field5Color.getPxlColor().mix(contrast,
							ec));
					colorTable[5].set(Field6Color.getPxlColor().mix(contrast,
							ec));
					colorTable[6].set(Field7Color.getPxlColor().mix(contrast,
							ec));
					colorTable[7].set(Field8Color.getPxlColor().mix(contrast,
							ec));
				}
			}
		}
	}

	protected void computeGeometry() {
		boolean modified = checkContextFields();
		double c = Contrast.getDouble();
		if (c != contrast) {
			contrast = c;
			computeColors();
		}
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		int s = FieldSize.getInt() / 2;
		int t = TargetSize.getInt();
		if (modified || (x != locX) || (y != locY) || (s != fieldSize)
				|| (t != targetSize)) {
			switch (nFields) {
			case 1:
				getDisplayElement(firstField).setRect(x - s + 1, y - s + 1,
						s + s, s + s);
				break;
			case 2:
				getDisplayElement(firstField).setRect(x - s + 1, y - s + 1, s,
						s + s);
				getDisplayElement(firstField + 1).setRect(x + 1, y - s + 1, s,
						s + s);
				break;
			case 4:
				getDisplayElement(firstField).setRect(x - s + 1, y - s + 1, s,
						s);
				getDisplayElement(firstField + 1).setRect(x + 1, y - s + 1, s,
						s);
				getDisplayElement(firstField + 2).setRect(x + 1, y + 1, s, s);
				getDisplayElement(firstField + 3).setRect(x - s + 1, y + 1, s,
						s);
				break;
			case 8:
				getDisplayElement(firstField + 0).setRect(x + 1, y - s + 1, s,
						s);
				getDisplayElement(firstField + 1).setRect(x + 1, y - s + 1, s,
						s);
				getDisplayElement(firstField + 2).setRect(x + 1, y + 1, s, s);
				getDisplayElement(firstField + 3).setRect(x + 1, y + 1, s, s);
				getDisplayElement(firstField + 4).setRect(x - s + 1, y + 1, s,
						s);
				getDisplayElement(firstField + 5).setRect(x - s + 1, y + 1, s,
						s);
				getDisplayElement(firstField + 6).setRect(x - s + 1, y - s + 1,
						s, s);
				getDisplayElement(firstField + 7).setRect(x - s + 1, y - s + 1,
						s, s);
				break;
			}
			getDisplayElement(target).setCenterAndSize(x, y, t, t);
			locX = x;
			locY = y;
			fieldSize = s;
			targetSize = t;
		}
	}

	private boolean checkContextFields() {
		int n = NumberOfContextFields.getInt();
		boolean r = false;
		// System.out.println("nFields = " + nFields);
		// System.out.println("      n = " + n);
		if (n != nFields) {
			// number of fields has changed
			removeDisplayElements(firstField);
			r = true;
			if (n <= 1) {
				enterDisplayElement(new Bar(colorTable[0]), group[0]);
				nFields = 1;
			} else if (n <= 2) {
				enterDisplayElement(new Bar(colorTable[0]), group[0]);
				enterDisplayElement(new Bar(colorTable[1]), group[0]);
				nFields = 2;
			} else if (n <= 4) {
				enterDisplayElement(new Bar(colorTable[0]), group[0]);
				enterDisplayElement(new Bar(colorTable[1]), group[0]);
				enterDisplayElement(new Bar(colorTable[2]), group[0]);
				enterDisplayElement(new Bar(colorTable[3]), group[0]);
				nFields = 4;
			} else {
				enterDisplayElement(new Wedge(colorTable[0], Wedge.TOPLEFT),
						group[0]);
				enterDisplayElement(
						new Wedge(colorTable[1], Wedge.BOTTOMRIGHT), group[0]);
				enterDisplayElement(new Wedge(colorTable[2], Wedge.TOPRIGHT),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[3], Wedge.BOTTOMLEFT),
						group[0]);
				enterDisplayElement(
						new Wedge(colorTable[4], Wedge.BOTTOMRIGHT), group[0]);
				enterDisplayElement(new Wedge(colorTable[5], Wedge.TOPLEFT),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[6], Wedge.BOTTOMLEFT),
						group[0]);
				enterDisplayElement(new Wedge(colorTable[7], Wedge.TOPRIGHT),
						group[0]);
				nFields = 8;
			}
			target = enterDisplayElement(new Oval(TargetColor), group[0]);
		}
		return r;
	}
}
