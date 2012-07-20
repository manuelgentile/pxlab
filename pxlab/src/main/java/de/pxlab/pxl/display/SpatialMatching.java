package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/*  @author M. Hodapp
 @version 0.1.0 
 @date 05/29/00 */
public class SpatialMatching extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new YxyColor(6.6,
			0.64, 0.33)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new YxyColor(21.3,
			0.29, 0.6)), "Color2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color3");

	/** Cunstructor creating the title of the display. */
	public SpatialMatching() {
		setTitleAndTopic("Matching and spatial mixture", PHOTOMETRY_DSP | DEMO);
	}
	private int c1, c2, c3;
	private int s1, s2, s3, s4;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color3));
		s2 = enterDisplayElement(new HorStripedBar(Color1));
		s3 = enterDisplayElement(new HorStripedBar(Color2));
		return (s1);
	}

	protected void computeColors() {
		Color3.set(Color1.getPxlColor().mix(Color2.getPxlColor()));
	}

	protected void computeGeometry() {
		computeColors();
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		getDisplayElement(s1).setRect(r1);
		HorStripedBar hb1 = (HorStripedBar) getDisplayElement(s2);
		hb1.setLocation(r2.x, r2.y);
		hb1.setSize(r2.width, r2.height);
		hb1.setPhase(2, 1);
		HorStripedBar hb2 = (HorStripedBar) getDisplayElement(s3);
		hb2.setLocation(r2.x, r2.y + 1);
		hb2.setSize(r2.width, r2.height);
		hb2.setPhase(2, 1);
	}
}
