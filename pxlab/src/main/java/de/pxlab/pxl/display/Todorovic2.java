package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Simplyfied Todorovic Figure.
 * 
 * <P>
 * Why don't the two center rectangles look equally bright? Note that the length
 * of the common border for the center rectangle and the covering rectangles is
 * larger than for the center and the background.
 * 
 * <P>
 * Todorovic, D. (1996). Lightness and junctions. ZiF-Report No. 26/96.
 * Bielefeld: Zentrum f�r interdisziplin�re Forschung.
 * 
 * @author E. Reitmayr
 * @version 0.2.1
 */
/*
 * 
 * 05/26/00
 */
public class Todorovic2 extends Display {
	public ExPar BarSize = new ExPar(PROPORT, new ExParValue(0.3),
			"Size of the bars");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.gray)), "Color1");
	public ExPar Color2 = new ExPar(COLOR,
			new ExParValue(30.739, 0.307, 0.347), "Color2");
	public ExPar Color1 = new ExPar(COLOR,
			new ExParValue(15.366, 0.307, 0.347), "Color3");

	/** Cunstructor creating the title of the display. */
	public Todorovic2() {
		setTitleAndTopic("Simplified Todorovic Figure", ASSIMILATION_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7, s8;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color3), group[0]);
		s4 = enterDisplayElement(new Bar(Color3), group[0]);
		s5 = enterDisplayElement(new Bar(Color2));
		s6 = enterDisplayElement(new Bar(Color2));
		s7 = enterDisplayElement(new Bar(Color1));
		s8 = enterDisplayElement(new Bar(Color1));
		return (s3);
	}

	protected void computeGeometry() {
		double par = BarSize.getDouble();
		// get the squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		Rectangle r3 = inner3rdRect(r1);
		Rectangle r4 = inner3rdRect(r2);
		Rectangle r5 = new Rectangle((int) (r1.x + r1.width / 4),
				(int) (r1.y + (r1.height / 3 * (1 - par))),
				(int) (0.5 * r1.width), (int) (r1.height / 3 * par));
		Rectangle r6 = new Rectangle((int) (r1.x + r1.width / 4),
				(int) (r1.y + (2 * r1.height / 3)), (int) (r1.width / 2),
				(int) (r1.height / 3 * par));
		Rectangle r7 = new Rectangle((int) (r2.x + r2.width / 4),
				(int) (r2.y + (r2.height / 3 * (1 - par))),
				(int) (0.5 * r2.width), (int) (r2.height / 3 * par));
		Rectangle r8 = new Rectangle((int) (r2.x + r2.width / 4),
				(int) (r2.y + (2 * r2.height / 3)), (int) (r2.width / 2),
				(int) (r2.height / 3 * par));
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
