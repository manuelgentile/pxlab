package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * @author E. Reitmayr
 * @version 0.2.0
 */
/*
 * 
 * 05/15/00
 */
public class Induction extends Display {
	public ExPar CircleSize = new ExPar(PROPORT,
			new ExParValue(0.15, 0.0, 1.0), "Size of the circle");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.red)), "Color 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Color 3");

	/** Cunstructor creating the title of the display. */
	public Induction() {
		setTitleAndTopic("Induction", ADAPTATION_DSP | DEMO);
	}
	private int s1, s2, s3;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1), group[0]);
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Oval(Color3));
		return (s3);
	}

	protected void computeGeometry() {
		Rectangle r1 = mediumSquare(width, height);
		Rectangle r2 = mediumSquare(width, height);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		Oval circle = (Oval) getDisplayElement(s3);
		double s = CircleSize.getDouble();
		circle.setRect(new Point((int) (-s * r2.width / 2), (int) (-s
				* r2.height / 2)), new Dimension((int) (s * r2.width),
				(int) (s * r2.height)));
	}
}
