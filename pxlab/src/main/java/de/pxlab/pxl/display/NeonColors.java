package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.ExPar;

/**
 * "Neon" Colors
 * 
 * <P>
 * Neon colors are a special case of assimilation. They are created by thin
 * colored lines on a background. The background assumes the colors of the
 * lines. The effect also works for dark or bright lines.
 * 
 * <P>
 * The demonstration allows making a match between the two background areas such
 * that one can "measure" the amount of assimilation.
 * 
 * <P>
 * Van Tuijl, H.F. (1975). A new visual illusion: Neonlike color spreading and
 * complementary color induction between subjective contours. Acta Psychologica,
 * 39, 441-445.
 * 
 * @author M. Hodapp
 * @version 0.2.1
 */
/*
 * 
 * 09/06/00
 */
public class NeonColors extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new YxyColor(37.426,
			0.244, 0.225)), "Color2");
	public ExPar BackGround = ExPar.ScreenBackgroundColor;

	/** Cunstructor creating the title of the display. */
	public NeonColors() {
		setTitleAndTopic("'Neon' Colors", ASSIMILATION_DSP | DEMO);
	}
	private int rc;
	private int s1, s2, s3, s4;
	private int rows = 25;
	private int columns = 25;
	private Rectangle r2, r3;
	private Oval e1;

	protected int create() {
		rc = rows * columns;
		s4 = enterDisplayElement(new Bar(BackGround));
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Oval(Color2));
		s3 = enterDisplayElement(new Oval(BackGround));
		for (int i = 0; i < rc; i++)
			enterDisplayElement(new Bar(Color1));
		return (s2);
	}

	protected void computeGeometry() {
		Rectangle r = largeSquare(width, height);
		r2 = centeredSquare(width, height, (int) (r.width * 0.5));
		r3 = centeredSquare(width, height, (int) (r.width * 0.2));
		int k = 5;
		Rectangle[] rp = rectPattern(r, rows, columns, 1, 1, 0, 0);
		Rectangle r1 = new Rectangle(rp[0].x, rp[0].y, rp[columns - 1].x
				+ rp[columns - 1].width - rp[0].x, rp[rc - 1].y
				+ rp[rc - 1].height - rp[columns - 1].y);
		getDisplayElement(s4).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		for (int i = 0; i < rc; i++)
			getDisplayElement(k + i).setRect(rp[i].x, rp[i].y, rp[i].width,
					rp[i].height);
	}
}
