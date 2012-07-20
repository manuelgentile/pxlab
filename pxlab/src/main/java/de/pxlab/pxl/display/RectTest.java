package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a simple center surround display where the center field's size may be
 * adjusted by the user.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 15/03/00
 */
public class RectTest extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.red)), "Color 2");

	/** Cunstructor creating the title of the display. */
	public RectTest() {
		setTitleAndTopic("Bounds of Rectangular Objects", DISPLAY_TEST_DSP
				| DEMO);
	}
	private int s1, s2, s3, s4, s5;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Line(Color1), group[0]);
		s2 = enterDisplayElement(new Line(Color1), group[0]);
		s3 = enterDisplayElement(new Line(Color1), group[0]);
		s4 = enterDisplayElement(new Line(Color1), group[0]);
		s5 = enterDisplayElement(new Ellipse(Color2), group[0]);
		return (s5);
	}

	protected void computeGeometry() {
		Rectangle r1 = mediumSquare(width, height);
		int d = r1.width / 4;
		getDisplayElement(s1).setLocation(r1.x - d, r1.y);
		((Line) getDisplayElement(s1)).setLocation2(r1.x + r1.width + d, r1.y);
		getDisplayElement(s2).setLocation(r1.x - d, r1.y + r1.height - 1);
		((Line) getDisplayElement(s2)).setLocation2(r1.x + r1.width + d, r1.y
				+ r1.height - 1);
		getDisplayElement(s3).setLocation(r1.x, r1.y - d);
		((Line) getDisplayElement(s3)).setLocation2(r1.x, r1.y + r1.height + d);
		getDisplayElement(s4).setLocation(r1.x + r1.width - 1, r1.y - d);
		((Line) getDisplayElement(s4)).setLocation2(r1.x + r1.width - 1, r1.y
				+ r1.height + d);
		getDisplayElement(s5).setRect(r1);
	}
}
