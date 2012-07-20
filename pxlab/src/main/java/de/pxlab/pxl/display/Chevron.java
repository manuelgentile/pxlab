package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A Chevron type of circularly arranged wedges.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 08/14/01
 * 
 * 07/09/02 fixed bug for repeated display
 */
public class Chevron extends Display {
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(8.62, 0.313, 0.329)), "Background color");
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
	public ExPar HoleSize = new ExPar(SCREENSIZE, new ExParValue(200),
			"Center hole size");
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(500), "Size");
	public ExPar GapSize = new ExPar(PROPORT, new ExParValue(0.1),
			"Relative gap size");
	public ExPar FirstAngle = new ExPar(ANGLE, new ExParValue(90),
			"Location of first patch");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position");
	public ExPar NumberOfFields = new ExPar(1, 8, new ExParValue(8),
			"Number of fields");
	private int maxFields = 8;
	private int firstField;
	private int nFields;

	public Chevron() {
		setTitleAndTopic("Chevron", COLOR_DISCRIMINATION_DSP);
	}

	protected int create() {
		setBackgroundColorPar(BackgroundColor);
		firstField = nextDisplayElementIndex();
		nFields = 0;
		checkFields();
		defaultTiming(0);
		return (firstField);
	}

	protected void computeColors() {
	}

	protected void computeGeometry() {
		boolean modified = checkFields();
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		int r1 = HoleSize.getInt() / 2;
		int r2 = Size.getInt() / 2;
		double gap = GapSize.getDouble();
		double a, astep, adelta, xp1, xp2, yp1, yp2;
		Polygon p;
		astep = 2.0 * Math.PI / (double) nFields;
		adelta = (gap > 0.0) ? (astep * (1.0 - gap)) : astep;
		a = Math.PI * FirstAngle.getDouble() / 360.0 - adelta / 2.0;
		// System.out.println("firstField = " + firstField);
		// System.out.println("   nFields = " + nFields);
		for (int i = 0; i < nFields; i++, a += astep) {
			p = ((FilledPolygon) getDisplayElement(firstField + i))
					.getPolygon();
			xp1 = Math.cos(a);
			yp1 = Math.sin(a);
			xp2 = Math.cos(a + adelta);
			yp2 = Math.sin(a + adelta);
			p.xpoints[0] = x
					+ ((r1 > 0.0) ? (int) Math.round((double) r1 * xp1) : 0);
			p.ypoints[0] = y
					+ ((r1 > 0.0) ? (int) Math.round((double) r1 * yp1) : 0);
			p.xpoints[1] = x + (int) Math.round((double) r2 * xp1);
			p.ypoints[1] = y + (int) Math.round((double) r2 * yp1);
			p.xpoints[2] = x + (int) Math.round((double) r2 * xp2);
			p.ypoints[2] = y + (int) Math.round((double) r2 * yp2);
			p.xpoints[3] = x
					+ ((r1 > 0) ? (int) Math.round((double) r1 * xp2) : 0);
			p.ypoints[3] = y
					+ ((r1 > 0) ? (int) Math.round((double) r1 * yp2) : 0);
		}
	}

	private boolean checkFields() {
		int n = NumberOfFields.getInt();
		if (n > maxFields)
			n = maxFields;
		boolean r = false;
		int[] x = new int[4];
		int[] y = new int[4];
		// System.out.println("nFields = " + nFields);
		// System.out.println("      n = " + n);
		if (n != nFields) {
			removeDisplayElements(firstField);
			if (n > 0)
				enterDisplayElement(new FilledPolygon(Field1Color, new Polygon(
						x, y, 4)), group[0]);
			if (n > 1)
				enterDisplayElement(new FilledPolygon(Field2Color, new Polygon(
						x, y, 4)), group[0]);
			if (n > 2)
				enterDisplayElement(new FilledPolygon(Field3Color, new Polygon(
						x, y, 4)), group[0]);
			if (n > 3)
				enterDisplayElement(new FilledPolygon(Field4Color, new Polygon(
						x, y, 4)), group[0]);
			if (n > 4)
				enterDisplayElement(new FilledPolygon(Field5Color, new Polygon(
						x, y, 4)), group[0]);
			if (n > 5)
				enterDisplayElement(new FilledPolygon(Field6Color, new Polygon(
						x, y, 4)), group[0]);
			if (n > 6)
				enterDisplayElement(new FilledPolygon(Field7Color, new Polygon(
						x, y, 4)), group[0]);
			if (n > 7)
				enterDisplayElement(new FilledPolygon(Field8Color, new Polygon(
						x, y, 4)), group[0]);
			nFields = n;
			r = true;
		}
		return r;
	}
}
