package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Stripe Induction.
 * 
 * <p>
 * Simultanous contrast mainly works locally. Thus the two levels of brightness
 * in the wide dark and bright stripes induce different levels of brightness in
 * the small gray level stripes.
 * 
 * <P>
 * Foley, J. M. & McCourt, M. E. (1985). Visual grating induction. JOSA, A, 2,
 * 1220-1230.
 * 
 * <P>
 * McCourt, M. E. (1982). A spatial frequency dependent grating induction
 * effect. Vision Research, 22, 119-134.
 * 
 * @author E. Reitmayr
 * @version 0.2.1
 */
/*
 * 
 * 05/20/00
 */
public class StripeInduction extends Display {
	public ExPar BarSize = new ExPar(1.0, 7.5, new ExParValue(1.0),
			"Size of the horizontal bars");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.gray)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color2");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.darkGray)), "Color3");

	/** Cunstructor creating the title of the display. */
	public StripeInduction() {
		setTitleAndTopic("Stripe Induction", LATERAL_INHIBITION_DSP | DEMO);
	}
	private int c1, c2, c3;
	private int nBars = 4;

	protected int create() {
		for (int i = 0; i < nBars; i++) {
			enterDisplayElement(new Bar(Color1));
			enterDisplayElement(new Bar(Color2));
		}
		enterDisplayElement(new Bar(Color1));
		enterDisplayElement(new Bar(Color3), group[0]);
		enterDisplayElement(new Bar(Color3), group[0]);
		enterDisplayElement(new Bar(Color3), group[0]);
		enterDisplayElement(new Bar(Color3), group[0]);
		defaultTiming(0);
		return (c3);
	}

	protected void computeGeometry() {
		Rectangle r = largeSquare(width, height);
		int s = largeSquareSize(width, height);
		// Make sure that every bar has the same number of video lines
		int d = (int) (s / ((2 * nBars) + 1));
		int x = r.x;
		int y = r.y;
		for (int i = 0; i < nBars; i++) {
			getDisplayElement(1 + i + i).setRect(x, y, d, s);
			x += d;
			getDisplayElement(1 + i + i + 1).setRect(x, y, d, s);
			x += d;
		}
		getDisplayElement(1 + nBars + nBars).setRect(x, y, d, s);
		int x1 = r.x;
		int y1 = r.y;
		double s1 = 9.0 * d;
		double par = BarSize.getDouble();
		double barSize = s * par * 1 / 44;
		double distSize = s * ((44 - (4 * par * 1)) / 5) / 44;
		getDisplayElement(10).setRect((int) (x1), (int) (y1 + distSize),
				(int) (s1), (int) (barSize));
		y1 += barSize;
		getDisplayElement(11).setRect((int) (x1), (int) (y1 + 2.0 * distSize),
				(int) (s1), (int) (barSize));
		y1 += barSize;
		getDisplayElement(12).setRect((int) (x1), (int) (y1 + 3.0 * distSize),
				(int) (s1), (int) (barSize));
		y1 += barSize;
		getDisplayElement(13).setRect((int) (x1), (int) (y1 + 4.0 * distSize),
				(int) (s1), (int) (barSize));
	}
}
