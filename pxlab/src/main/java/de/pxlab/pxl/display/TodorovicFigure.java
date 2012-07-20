package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Todorovic Figure.
 * 
 * <P>
 * Why don't the two circles look equally bright? Note that the length of the
 * common border for circle and covering squares is larger than for circle and
 * background.
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
public class TodorovicFigure extends Display {
	public ExPar CircleSize = new ExPar(PROPORT, new ExParValue(0.4),
			"Size of the circles");
	public ExPar SquareSize = new ExPar(PROPORT, new ExParValue(0.7, 0.0, 1.0),
			"Size of the little squares");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.gray)), "Color1");
	public ExPar Color3 = new ExPar(COLOR,
			new ExParValue(30.739, 0.307, 0.347), "Color2");
	public ExPar Color2 = new ExPar(COLOR,
			new ExParValue(15.366, 0.307, 0.347), "Color3");

	/** Cunstructor creating the title of the display. */
	public TodorovicFigure() {
		setTitleAndTopic("Todorovic Figure", ASSIMILATION_DSP | DEMO);
	}
	private int c1, c2, c3;
	private int s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Oval(Color3), group[0]);
		s4 = enterDisplayElement(new Oval(Color3), group[0]);
		s5 = enterDisplayElement(new Bar(Color2));
		s6 = enterDisplayElement(new Bar(Color2));
		s7 = enterDisplayElement(new Bar(Color2));
		s8 = enterDisplayElement(new Bar(Color2));
		s9 = enterDisplayElement(new Bar(Color1));
		s10 = enterDisplayElement(new Bar(Color1));
		s11 = enterDisplayElement(new Bar(Color1));
		s12 = enterDisplayElement(new Bar(Color1));
		return (s3);
	}

	protected void computeGeometry() {
		// get the squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		// get the circles
		double par1 = CircleSize.getDouble();
		Rectangle r3 = innerRect(r1, par1);
		Rectangle r4 = innerRect(r2, par1);
		Oval c1 = (Oval) getDisplayElement(s3);
		c1.setRect(r3.x, r3.y, r3.width, r3.height);
		Oval c2 = (Oval) getDisplayElement(s4);
		c2.setRect(r4.x, r4.y, r4.width, r4.height);
		// compute the size of the little squares and their distance from each
		// other
		double par2 = SquareSize.getDouble();
		int n = 2;
		int lsS = (int) ((1 - par2) * r1.width / n);
		Rectangle[] lr1 = rectPattern(r1, n, n, lsS, lsS, lsS, lsS);
		Rectangle[] lr2 = rectPattern(r2, n, n, lsS, lsS, lsS, lsS);
		getDisplayElement(s5).setRect(lr1[0]);
		getDisplayElement(s6).setRect(lr1[1]);
		getDisplayElement(s7).setRect(lr1[2]);
		getDisplayElement(s8).setRect(lr1[3]);
		getDisplayElement(s9).setRect(lr2[0]);
		getDisplayElement(s10).setRect(lr2[1]);
		getDisplayElement(s11).setRect(lr2[2]);
		getDisplayElement(s12).setRect(lr2[3]);
	}
}
