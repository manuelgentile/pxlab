package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Tritanopic Confusion Colors.
 * 
 * <P>
 * The hues of the three columns of this pattern are indiscriminable for
 * Tritanopes. They lie on the tritanopic confusion line.
 * 
 * <P>
 * Only the top left and top right colors are adjustable. All other fields are
 * computed. The center column is a mixture of the left and right column.
 * 
 * <P>
 * Pokorny, J. & Smith, V. C. (1986). Colorimetry and color discrimination. In
 * K. R. Boff, L. Kaufman & J. P. Thomas (Eds.) Handbook of perception and human
 * performance. Vol. I. Sensory processes and perception, Chapter 8. New York:
 * Wiley.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 05/15/00
 */
public class TritanopicConfusionColors extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(56.85, 0.270, 0.225),
			"Color of the field 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(33.65, 0.270, 0.225),
			"Color of the field 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(20.42, 0.270, 0.225),
			"Color of the field 3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(74.86, 0.307, 0.307),
			"Color of the field 4");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(45.96, 0.307, 0.307),
			"Color of the field 5");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(27.05, 0.307, 0.307),
			"Color of the field 6");
	public ExPar Color7 = new ExPar(COLOR, new ExParValue(94.11, 0.344, 0.400),
			"Color of the field 7");
	public ExPar Color8 = new ExPar(COLOR, new ExParValue(56.93, 0.344, 0.400),
			"Color of the field 8");
	public ExPar Color9 = new ExPar(COLOR, new ExParValue(33.52, 0.344, 0.400),
			"Color of the field 9");

	public TritanopicConfusionColors() {
		setTitleAndTopic("Tritanopic confusion colors",
				COLOR_DISCRIMINATION_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7, s8, s9;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color3));
		s4 = enterDisplayElement(new Bar(Color4));
		s5 = enterDisplayElement(new Bar(Color5));
		s6 = enterDisplayElement(new Bar(Color6));
		s7 = enterDisplayElement(new Bar(Color7));
		s8 = enterDisplayElement(new Bar(Color8));
		s9 = enterDisplayElement(new Bar(Color9));
		return (s2);
	}

	protected void computeGeometry() {
		int msize = largeSquareSize(width, height);
		Rectangle ms = largeSquare(width, height);
		Rectangle r1 = new Rectangle(ms.x, ms.y, msize / 14 * 4, msize / 14 * 4);
		Rectangle r2 = new Rectangle(ms.x, ms.y + msize / 14 * 5,
				msize / 14 * 4, msize / 14 * 4);
		Rectangle r3 = new Rectangle(ms.x, ms.y + msize / 14 * 10,
				msize / 14 * 4, msize / 14 * 4);
		Rectangle r4 = new Rectangle(ms.x + msize / 14 * 5, ms.y,
				msize / 14 * 4, msize / 14 * 4);
		Rectangle r5 = new Rectangle(ms.x + msize / 14 * 5, ms.y + msize / 14
				* 5, msize / 14 * 4, msize / 14 * 4);
		Rectangle r6 = new Rectangle(ms.x + msize / 14 * 5, ms.y + msize / 14
				* 10, msize / 14 * 4, msize / 14 * 4);
		Rectangle r7 = new Rectangle(ms.x + msize / 14 * 10, ms.y,
				msize / 14 * 4, msize / 14 * 4);
		Rectangle r8 = new Rectangle(ms.x + msize / 14 * 10, ms.y + msize / 14
				* 5, msize / 14 * 4, msize / 14 * 4);
		Rectangle r9 = new Rectangle(ms.x + msize / 14 * 10, ms.y + msize / 14
				* 10, msize / 14 * 4, msize / 14 * 4);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		getDisplayElement(s5).setRect(r5);
		getDisplayElement(s6).setRect(r6);
		getDisplayElement(s7).setRect(r7);
		getDisplayElement(s8).setRect(r8);
		getDisplayElement(s9).setRect(r9);
	}
}
