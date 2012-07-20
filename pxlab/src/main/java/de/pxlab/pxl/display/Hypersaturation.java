package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Hypersaturation by Successive Contrast.
 * 
 * <P>
 * Adaptation to complementary colors may increase saturation after successive
 * viewing.
 * 
 * <P>
 * Three centered squares of different sizes are shown. Fixate and adapt for
 * about 20 seconds. Then select subgroup 2 showing a homogenous yellow square.
 * If the small square's color is complementary to the big square's color, then
 * that part of the big square where the small one has been during adaptation
 * has a much higher saturation than the rest of the field (after Schiffman,
 * 1990). Adaptation is treated extensively by Wyszecki (1986).
 * 
 * <P>
 * Schiffman, H. R. (1990). Sensation and Perception. An integrated approach
 * (3rd. ed.). New York: Wiley.
 * 
 * @author E. Reitmayr
 * @version 0.2.1
 */
/*
 * 
 * 11/09/00
 */
public class Hypersaturation extends Display {
	public ExPar SquareSize = new ExPar(PROPORT, new ExParValue(0.6, 0.0, 1.0),
			"Size of the medium square");
	public ExPar CenterSquareSize = new ExPar(PROPORT, new ExParValue(0.6, 0.0,
			1.0), "Size of the inner square");
	public ExPar LargeColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)),
			"Color of the large square");
	public ExPar MediumColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the medium square");
	public ExPar InnerColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)),
			"Color of the inner square");
	public ExPar FixationMarkColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)),
			"Color of the fixation mark");

	/** Cunstructor creating the title of the display. */
	public Hypersaturation() {
		setTitleAndTopic("Hypersaturation", ADAPTATION_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(LargeColor), group[0] + group[1]);
		s2 = enterDisplayElement(new Bar(MediumColor), group[0]);
		s3 = enterDisplayElement(new Bar(InnerColor));
		s4 = enterDisplayElement(new Cross(FixationMarkColor), group[0]
				+ group[1]);
		return s1;
	}

	protected void computeGeometry() {
		Rectangle r1 = largeSquare(width, height);
		Rectangle r2 = innerRect(r1, SquareSize.getDouble());
		Rectangle r3 = innerRect(r2, CenterSquareSize.getDouble());
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		Cross c1 = (Cross) getDisplayElement(s4);
		c1.setRect(new Point(0, 0), new Dimension((int) (r1.width / 16),
				(int) (r1.height / 16)));
	}

	public void showGroup(Graphics g) {
		setGraphicsContext(g);
		if (activeTimingGroup == 1) {
			((Bar) getDisplayElement(s1)).show(MediumColor);
			((Bar) getDisplayElement(s2)).show(InnerColor);
			((Cross) getDisplayElement(s4)).show(FixationMarkColor);
		}
		if (activeTimingGroup == 2) {
			((Bar) getDisplayElement(s1)).show(LargeColor);
			((Cross) getDisplayElement(s4)).show(FixationMarkColor);
		}
	}
}
