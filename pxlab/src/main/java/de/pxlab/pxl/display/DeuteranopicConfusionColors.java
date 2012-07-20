package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Deuteranopic Confusion Colors.
 * 
 * <P>
 * The hues of the three columns of this pattern are indiscriminable for
 * deuteranopes. They lie on the deuteranopic confusion line.
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
public class DeuteranopicConfusionColors extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(38.11, 0.390, 0.257),
			"Color of the field 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(22.90, 0.388, 0.256),
			"Color of the field 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(14.06, 0.388, 0.262),
			"Color of the field 3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(58.25, 0.298, 0.323),
			"Color of the field 4");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(34.70, 0.298, 0.322),
			"Color of the field 5");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(20.53, 0.294, 0.318),
			"Color of the field 6");
	public ExPar Color7 = new ExPar(COLOR, new ExParValue(76.48, 0.230, 0.364),
			"Color of the field 7");
	public ExPar Color8 = new ExPar(COLOR, new ExParValue(46.05, 0.231, 0.366),
			"Color of the field 8");
	public ExPar Color9 = new ExPar(COLOR, new ExParValue(27.28, 0.231, 0.366),
			"Color of the field 9");

	public DeuteranopicConfusionColors() {
		setTitleAndTopic("Deuteranopic confusion colors",
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
		defaultTiming(0);
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
