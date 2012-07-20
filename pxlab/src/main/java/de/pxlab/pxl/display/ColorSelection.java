package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This display contains a small number of circular color patches and the
 * subject's task is to select one of them like in the odd man out paradigm.
 * 
 * @author H. Irtel
 * @version 0.2.2
 */
/*
 * 01/26/01 use ExPar color for DisplayElement objects
 * 
 * 08/13/01 have my own background color.
 * 
 * 02/07/02 added dithering.
 * 
 * 02/09/02 made this a subclass of ColorDiscrimination
 */
public class ColorSelection extends ColorDiscrimination {
	/** First distractor color. */
	public ExPar CColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.YELLOW)), "Distractor Color C");
	/** Second distractor color. */
	public ExPar DColor = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.YELLOW)), "Distractor Color D");
	/**
	 * Color position string. This string contains one of the letters T, A, B,
	 * C, D for every test patch on the screen. The letter defines which of the
	 * colors TargetColor, AColor, BColor, CColor, or DColor is shown at the
	 * respective position. The length of the string implicitly defines the
	 * number of patches in the test pattern.
	 */
	public ExPar Positions = new ExPar(STRING, new ExParValue("TCCC"),
			"Color Position String");
	/** Diameter of the whole arrangement of test patches. */
	public ExPar ConfigSize = new ExPar(SCREENSIZE, new ExParValue(300.0),
			"Diameter of Patch Arrangement");
	/** Position angle of the first test patch. */
	public ExPar FirstAngle = new ExPar(ANGLE, new ExParValue(90),
			"Location of First Patch");

	public ColorSelection() {
		setTitleAndTopic("Color Selection and Discriminiation",
				COLOR_DISCRIMINATION_DSP);
	}
	private int s1;
	private int nPatches;
	private String positions;

	protected int create() {
		setBackgroundColorPar(BackgroundColor);
		positions = Positions.getString();
		nPatches = positions.length();
		s1 = nextDisplayElementIndex();
		for (int i = 0; i < nPatches; i++) {
			enterDisplayElement(new Oval(TargetColor), group[0]);
		}
		defaultTiming(0);
		return (s1);
	}

	protected void computeColors() {
		TargetColor.set(AColor.getPxlColor().mix(WeightOfA.getDouble(),
				BColor.getPxlColor()));
	}

	protected void computeGeometry() {
		computeColors();
		positions = Positions.getString();
		if (positions.length() != nPatches) {
			throw new RuntimeException(
					"Number of patches in the display must not change.");
		}
		double angle = Math.PI * FirstAngle.getDouble() / 180.0;
		double aStep = (2.0 * Math.PI) / nPatches;
		int xp, yp;
		double cr = ConfigSize.getDouble() / 2.0;
		int ps = PatchSize.getInt();
		int dms = DitheringType.getInt();
		for (int i = 0; i < nPatches; i++) {
			Oval d = (Oval) getDisplayElement(s1 + i);
			d.setDither(dms);
			char c = positions.charAt(i);
			if (c == 'T') {
				d.setColorPar(TargetColor);
			} else if (c == 'A') {
				d.setColorPar(AColor);
			} else if (c == 'B') {
				d.setColorPar(BColor);
			} else if (c == 'C') {
				d.setColorPar(CColor);
			} else if (c == 'D') {
				d.setColorPar(DColor);
			} else {
				throw new RuntimeException("Illegal character in 'Positions'.");
			}
			xp = (int) Math.round(cr * Math.cos(angle));
			yp = -(int) Math.round(cr * Math.sin(angle));
			angle += aStep;
			d.setCenterAndSize(xp, yp, ps, ps);
		}
	}
}
