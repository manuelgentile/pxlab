package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Colors induced by an after image after adaptation.
 * 
 * <P>
 * Adaptation and simultaneous contrast may be combined. First adapt to a
 * certain color which generates a complementary after image after it is
 * removed. If the after image is strong enough then it may induce a color by
 * simultaneous contrast (inspired by Hurvich, 1981).
 * 
 * <P>
 * Hurvich, L. M. (1981). Color vision. Sunderland, MA: Sinauer.
 * 
 * @author E. Reitmayr, H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 05/15/00
 */
public class InductionByAfterImage extends Display {
	public ExPar TargetSize = new ExPar(PROPORT,
			new ExParValue(0.15, 0.0, 1.0), "Relative Size of the Target");
	public ExPar TargetColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Target Color");
	public ExPar SurroundColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.red)), "Surround Color");

	public InductionByAfterImage() {
		setTitleAndTopic("Induction by an After Image", ADAPTATION_DSP | DEMO);
	}
	private int s1, s2, s3;

	protected int create() {
		s1 = enterDisplayElement(new Bar(TargetColor), group[0]);
		s2 = enterDisplayElement(new Bar(SurroundColor), group[1]);
		s3 = enterDisplayElement(new Oval(TargetColor), group[1]);
		return s3;
	}

	protected void computeGeometry() {
		Rectangle r1 = mediumSquare(width, height);
		Rectangle r2 = mediumSquare(width, height);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		Oval circle = (Oval) getDisplayElement(s3);
		double s = TargetSize.getDouble();
		circle.setRect((int) (-s * r2.width / 2), (int) (-s * r2.height / 2),
				(int) (s * r2.width), (int) (s * r2.height));
	}
}
