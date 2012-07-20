package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This display shows an inverted T figure to demonstrate the
 * horizontal-vertical illusion. The location refers to the top middle point of
 * the base line. Rotations are done around this point.
 * 
 * @author H. Irtel
 * @version 0.3.3
 */
/*
 * 01/26/01 use ExPar color access and unified Timing 05/16/01 allow 4 different
 * orientations
 */
public class HorizontalVerticalIllusion extends Display {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.WHITE)), "Color of lines");
	public ExPar BaseLine = new ExPar(SCREENSIZE, new ExParValue(300),
			"Length of base line");
	public ExPar CutLine = new ExPar(SCREENSIZE, new ExParValue(300),
			"Length of cutting line");
	public ExPar CutRatio = new ExPar(PROPORT, new ExParValue(0.5),
			"Cutting point ratio");
	public ExPar LineWidth = new ExPar(SMALL_SCREENSIZE, new ExParValue(3),
			"Width of lines");
	public ExPar Orientation = new ExPar(ANGLE, new ExParValue(0),
			"Orientation of figure");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position");

	public HorizontalVerticalIllusion() {
		setTitleAndTopic("Horizontal vertical illusion", COMPLEX_GEOMETRY_DSP);
	}
	private int baseLine, cutLine;

	protected int create() {
		baseLine = enterDisplayElement(new Bar(this.Color), group[0]);
		cutLine = enterDisplayElement(new Bar(this.Color), group[0]);
		defaultTiming(0);
		return (baseLine);
	}

	protected void computeGeometry() {
		int bl = BaseLine.getInt();
		int cl = CutLine.getInt();
		int w = LineWidth.getInt();
		if (w < 1)
			w = 1;
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		int cp = (int) Math.round(CutRatio.getDouble() * (bl - w));
		switch (((Orientation.getInt() + 44) / 90) * 90) {
		case 0:
		case 360:
		default:
			((Bar) getDisplayElement(baseLine)).setRect(x - bl / 2, y, bl, w);
			((Bar) getDisplayElement(cutLine)).setRect(x - bl / 2 + cp, y - cl,
					w, cl);
			break;
		case 90:
			((Bar) getDisplayElement(baseLine)).setRect(x, y - bl / 2, w, bl);
			((Bar) getDisplayElement(cutLine)).setRect(x - cl, y + bl / 2 - w
					- cp, cl, w);
			break;
		case 180:
			((Bar) getDisplayElement(baseLine)).setRect(x - bl / 2, y - w, bl,
					w);
			((Bar) getDisplayElement(cutLine)).setRect(x + bl / 2 - cp - w, y,
					w, cl);
			break;
		case 270:
			((Bar) getDisplayElement(baseLine)).setRect(x - w, y - bl / 2, w,
					bl);
			((Bar) getDisplayElement(cutLine)).setRect(x, y - bl / 2 + cp, cl,
					w);
			break;
		}
	}
}
