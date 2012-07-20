package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.ExPar;

/**
 * @author M. Hodapp
 * @version 0.2.0
 */
/*
 * 
 * 03/29/01
 */
public class RedSquares extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new YxyColor(80,
			0.307, 0.347)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.red)), "Square Color");
	public ExPar Gap = new ExPar(UNKNOWN, new ExParValue(2), "Width of the gap");

	/** Cunstructor creating the title of the display. */
	public RedSquares() {
		setTitleAndTopic("Red Squares", ASSIMILATION_DSP | DEMO);
	}
	private int k, j;
	private int rc, rc2;
	private int rows = 12;
	private int columns = 12;
	private int s1, s2, s3, s4;

	/** Initialize the display list of the demo. */
	protected int create() {
		rc = rows * columns;
		rc2 = 2 * rc;
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(ExPar.ScreenBackgroundColor));
		for (int i = 0; i < rc2; i++)
			enterDisplayElement(new Bar(Color2));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		int gap = Gap.getInt();
		int xSize = (int) ((r1.width / gap) - 9);
		int ySize = (int) ((r1.height / gap) - 9);
		Rectangle[] r3 = rectPattern(r1, rows, columns, gap, gap, 0, 0);
		Rectangle[] r4 = rectPattern(r2, rows, columns, gap, gap, 0, 0);
		Rectangle r5 = new Rectangle(r3[0].x, r3[0].y, r3[columns - 1].x
				- r3[0].x + r3[columns - 1].width, r3[rc - 1].y
				+ r3[rc - 1].height - r3[0].y);
		getDisplayElement(s1).setRect(r5);
		getDisplayElement(s2).setRect(r2);
		k = 3;
		for (int i = 0; i < rc; i++) {
			getDisplayElement(k + i).setRect(r3[i].x, r3[i].y, r3[i].width,
					r3[i].height);
		}
		j = 3 + rc;
		for (int i = 0; i < rc; i++) {
			getDisplayElement(j + i).setRect(r4[i].x, r4[i].y, r4[i].width,
					r4[i].height);
		}
	}
}
