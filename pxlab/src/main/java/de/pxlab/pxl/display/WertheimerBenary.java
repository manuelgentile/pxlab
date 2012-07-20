package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * The Wertheimer-Benary cross shows an object dependent brightness induction
 * effect.
 * 
 * <P>
 * Benary, W. (1924). Beobachtungen zu einem Experiment �ber den
 * Helligkeitskontrast. Psychologische Forschung, 5, 131-142.
 * 
 * @author E. Reitmayr
 * @version 0.2.0
 */
/*
 * 05/12/00
 */
public class WertheimerBenary extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Color1");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new YxyColor(30.739,
			0.307, 0.347)), "Color4");
	public ExPar Color3 = new ExPar(COLOR,
			new ExParValue(30.739, 0.307, 0.347), "Color3");
	public ExPar Color2 = new ExPar(COLOR,
			new ExParValue(15.366, 0.307, 0.347), "Color2");

	/** Cunstructor creating the title of the display. */
	public WertheimerBenary() {
		setTitleAndTopic("Wertheimer-Benary Cross", ASSIMILATION_DSP | DEMO);
	}
	private int s1, s2, s3, s4;

	protected int create() {
		int[] xcr = new int[12];
		int[] ycr = new int[12];
		int[] xt1 = new int[3];
		int[] yt1 = new int[3];
		int[] xt2 = new int[3];
		int[] yt2 = new int[3];
		// background
		s1 = enterDisplayElement(new Bar(Color1));
		// cross
		s2 = enterDisplayElement(new FilledPolygon(Color2, new Polygon(xcr,
				ycr, 12)));
		// triangles
		s3 = enterDisplayElement(new FilledPolygon(Color3, new Polygon(xt1,
				yt1, 3)), group[0]);
		s4 = enterDisplayElement(new FilledPolygon(Color4, new Polygon(xt2,
				yt2, 3)), group[0]);
		return (s3);
	}

	protected void computeGeometry() {
		Rectangle bigr = largeSquare(width, height);
		getDisplayElement(s1).setRect(bigr);
		// get the cross
		FilledPolygon cross = (FilledPolygon) getDisplayElement(s2);
		Polygon cr = cross.getPolygon();
		cr.xpoints[0] = (int) (2 * bigr.width / 16);
		cr.ypoints[0] = (int) (6 * bigr.height / 16);
		cr.xpoints[1] = (int) (2 * bigr.width / 16);
		cr.ypoints[1] = (int) (2 * bigr.height / 16);
		cr.xpoints[2] = (int) (6 * bigr.width / 16);
		cr.ypoints[2] = (int) (2 * bigr.height / 16);
		cr.xpoints[3] = (int) (6 * bigr.width / 16);
		cr.ypoints[3] = (int) (-(2 * bigr.height / 16));
		cr.xpoints[4] = (int) (2 * bigr.width / 16);
		cr.ypoints[4] = (int) (-(2 * bigr.height / 16));
		cr.xpoints[5] = (int) (2 * bigr.width / 16);
		cr.ypoints[5] = (int) (-(6 * bigr.height / 16));
		cr.xpoints[6] = (int) (-(2 * bigr.width / 16));
		cr.ypoints[6] = (int) (-(6 * bigr.height / 16));
		cr.xpoints[7] = (int) (-(2 * bigr.width / 16));
		cr.ypoints[7] = (int) (-(2 * bigr.height / 16));
		cr.xpoints[8] = (int) (-(6 * bigr.width / 16));
		cr.ypoints[8] = (int) (-(2 * bigr.height / 16));
		cr.xpoints[9] = (int) (-(6 * bigr.width / 16));
		cr.ypoints[9] = (int) (2 * bigr.height / 16);
		cr.xpoints[10] = (int) (-(2 * bigr.width / 16));
		cr.ypoints[10] = (int) (2 * bigr.height / 16);
		cr.xpoints[11] = (int) (-(2 * bigr.width / 16));
		cr.ypoints[11] = (int) (6 * bigr.height / 16);
		// get the left triangle
		FilledPolygon ltri = (FilledPolygon) getDisplayElement(s3);
		Polygon lt = ltri.getPolygon();
		lt.xpoints[0] = (int) (-(2 * bigr.width / 16));
		lt.ypoints[0] = (int) (-(2 * bigr.height / 16));
		lt.xpoints[1] = (int) (-(6 * bigr.width / 16));
		lt.ypoints[1] = (int) (-(2 * bigr.height / 16));
		lt.xpoints[2] = (int) (-(4 * bigr.width / 16));
		lt.ypoints[2] = 0;
		// get the right triangle
		FilledPolygon rtri = (FilledPolygon) getDisplayElement(s4);
		Polygon rt = rtri.getPolygon();
		rt.xpoints[0] = (int) (4.83 * bigr.width / 16);
		rt.ypoints[0] = (int) (-(2 * bigr.height / 16));
		rt.xpoints[1] = (int) (2 * bigr.width / 16);
		rt.ypoints[1] = (int) (-(2 * bigr.height / 16));
		rt.xpoints[2] = (int) (2 * bigr.width / 16);
		rt.ypoints[2] = (int) (-(4.83 * bigr.height / 16));
	}
}
