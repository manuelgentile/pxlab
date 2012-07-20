package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * @version 0.2.1
 */
/*
 * 
 * 05/19/00
 */
public class FramedSquares extends Display {
	public ExPar SquareSize = new ExPar(PROPORTION, new ExParValue(0.6),
			"Size of the medium squares");
	public ExPar CenterSquareSize = new ExPar(PROPORTION, new ExParValue(0.6),
			"Size of the inner squares");
	public ExPar Color1 = new ExPar(COLOR,
			new ExParValue(21.952, 0.307, 0.347), "First Color");
	public ExPar Color2 = new ExPar(COLOR,
			new ExParValue(28.538, 0.307, 0.347), "Second Color");
	public ExPar Color3 = new ExPar(COLOR,
			new ExParValue(15.366, 0.307, 0.347), "Third Color");

	/** Cunstructor creating the title of the display. */
	public FramedSquares() {
		setTitleAndTopic("Framed Squares", COMPLEX_COLOR_MATCHING_DSP | DEMO);
	}
	private int c1, c2, c3;
	private int s1, s2, s3, s4, s5, s6;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color3));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color2));
		s4 = enterDisplayElement(new Bar(Color3));
		s5 = enterDisplayElement(new Bar(Color1), group[0]);
		s6 = enterDisplayElement(new Bar(Color1), group[0]);
		return (s5);
	}

	protected void computeGeometry() {
		// get the squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		Rectangle r3 = innerRect(r1, SquareSize.getDouble());
		Rectangle r4 = innerRect(r2, SquareSize.getDouble());
		Rectangle r5 = innerRect(r3, CenterSquareSize.getDouble());
		Rectangle r6 = innerRect(r4, CenterSquareSize.getDouble());
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		getDisplayElement(s5).setRect(r5);
		getDisplayElement(s6).setRect(r6);
	}
}
