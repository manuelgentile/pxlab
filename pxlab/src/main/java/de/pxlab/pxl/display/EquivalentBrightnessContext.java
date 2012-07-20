package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Does every complex surround have an equivalent homogenous surround which
 * creates the same induction effect as the complex surround?
 * 
 * @version 0.1.1
 */
/*
 * 05/15/00
 */
public class EquivalentBrightnessContext extends Display {
	public ExPar CenterSize = new ExPar(PROPORTION, new ExParValue(0.4),
			"Inner square size");
	public ExPar Square1Color1 = new ExPar(COLOR, new ExParValue(8.781, 0.307,
			0.347), "Color of field 1 and 3 in square 1");
	public ExPar Square1Color2 = new ExPar(COLOR, new ExParValue(48.294, 0.307,
			0.347), "Color of field 2 and 3 in square 1");
	public ExPar CenterSquareColor = new ExPar(COLOR, new ExParValue(21.952,
			0.307, 0.347), "Center square Color");
	public ExPar Square2Color = new ExPar(COLOR, new ExParValue(26.342, 0.307,
			0.347), "Color of Square 2");

	/** Cunstructor creating the title of the display. */
	public EquivalentBrightnessContext() {
		setTitleAndTopic("Equivalent Brightness Context",
				COMPLEX_COLOR_MATCHING_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Square1Color1));
		s2 = enterDisplayElement(new Bar(Square2Color));
		s3 = enterDisplayElement(new Bar(Square1Color2));
		s4 = enterDisplayElement(new Bar(Square1Color2));
		s5 = enterDisplayElement(new Bar(CenterSquareColor), group[0]);
		s6 = enterDisplayElement(new Bar(CenterSquareColor), group[0]);
		return (s3);
	}

	protected void computeGeometry() {
		// get the 2 background squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		// get the 2 light grey squares on the left
		Rectangle r3 = new Rectangle((int) (r1.x + (0.5 * r1.width)),
				(int) (r1.y), (int) (r1.width / 2.0), (int) (r1.height / 2.0));
		Rectangle r4 = new Rectangle((int) (r1.x),
				(int) (r1.y + (0.5 * r1.height)), (int) (r1.width / 2.0),
				(int) (r1.height / 2.0));
		// get the inner squares
		Rectangle r5 = innerRect(r1, CenterSize.getDouble());
		Rectangle r6 = innerRect(r2, CenterSize.getDouble());
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		getDisplayElement(s5).setRect(r5);
		getDisplayElement(s6).setRect(r6);
	}
}
