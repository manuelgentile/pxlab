package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A field containing a series of vertical stripes on a colored background and a
 * homogenous matching field.
 * 
 * <P>
 * This image is similar to the one used by Ware and Cowan (1982). It allows
 * measurement of induction effects. The color stripes may be adjusted. in Ware
 * and Cowan's experiment the large comparison field had to be matched with the
 * stripes in the induction field.
 * 
 * <P>
 * Ware, C., & Cowan, W. B. (1982). Changes in perceived color due to chromatic
 * interactions. Vision Research, 22, 1353-1362.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 05/18/00
 */
public class InductionMatching extends Display {
	public ExPar StripeSize = new ExPar(PROPORT, new ExParValue(0.333),
			"Stripe size");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new YxyColor(22.87,
			0.265, 0.371)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.yellow)), "Color 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new YxyColor(22.87,
			0.265, 0.371)), "Color 3");

	public InductionMatching() {
		setTitleAndTopic("Induction and Matching", COMPLEX_COLOR_MATCHING_DSP);
	}
	private int s1, s2, s3, s4, s5, s6, s7, s8;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1), group[0]);
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color3), group[0]);
		s4 = enterDisplayElement(new Bar(Color3), group[0]);
		s5 = enterDisplayElement(new Bar(Color3), group[0]);
		s6 = enterDisplayElement(new Bar(Color3), group[0]);
		s7 = enterDisplayElement(new Bar(Color3), group[0]);
		s8 = enterDisplayElement(new Bar(Color3), group[0]);
		defaultTiming(0);
		return (s3);
	}

	protected void computeGeometry() {
		// get the squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		// get the stripes
		double par = StripeSize.getDouble();
		int stripesize = (int) (par * r2.width / 6);
		int redX = (int) (par * r2.width / 18);
		Rectangle r3 = new Rectangle(r2.x + r2.width / 18 - redX, r2.y,
				stripesize, r2.height);
		Rectangle r4 = new Rectangle(r2.x + r2.width / 18 * 4 - redX, r2.y,
				stripesize, r2.height);
		Rectangle r5 = new Rectangle(r2.x + r2.width / 18 * 7 - redX, r2.y,
				stripesize, r2.height);
		Rectangle r6 = new Rectangle(r2.x + r2.width / 18 * 10 - redX, r2.y,
				stripesize, r2.height);
		Rectangle r7 = new Rectangle(r2.x + r2.width / 18 * 13 - redX, r2.y,
				stripesize, r2.height);
		Rectangle r8 = new Rectangle(r2.x + r2.width / 18 * 16 - redX, r2.y,
				stripesize, r2.height);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		getDisplayElement(s5).setRect(r5);
		getDisplayElement(s6).setRect(r6);
		getDisplayElement(s7).setRect(r7);
		getDisplayElement(s8).setRect(r8);
	}
}
