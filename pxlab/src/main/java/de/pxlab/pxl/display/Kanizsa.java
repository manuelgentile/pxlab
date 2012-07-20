package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Kanizsa's Figure.
 * 
 * <P>
 * The visual system is able to complete contours with gaps to simple figures.
 * The guiding principle is to identify simple objects. This results in the
 * subjective contours of Kanizsa's figure. Colored borders may induce colored
 * areas created by subjective contours.
 * 
 * <P>
 * Kanizsa, G. (1976). Subjective contours. Scientific American, 234, 48-52.
 * 
 * @author E. Reitmayr
 * @version 0.2.0
 */
/*
 * 
 * 05/15/00
 */
public class Kanizsa extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(0.8)),
			"Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color 2");

	/** Cunstructor creating the title of the display. */
	public Kanizsa() {
		setTitleAndTopic("Kanizsa's Figure", COMPLEX_GEOMETRY_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7;

	/** Initialize the display list of the demo. */
	protected int create() {
		int[] x = new int[4];
		int[] y = new int[4];
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Oval(Color2));
		s3 = enterDisplayElement(new Oval(Color2));
		s4 = enterDisplayElement(new Oval(Color2));
		s5 = enterDisplayElement(new Oval(Color2));
		s6 = enterDisplayElement(new PolyLineClosed(Color2,
				new Polygon(x, y, 4)));
		s7 = enterDisplayElement(new Bar(Color1));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle r1 = largeSquare(width, height);
		getDisplayElement(s1).setRect(r1);
		int w = r1.width;
		int h = r1.height;
		Oval c1 = (Oval) getDisplayElement(s2);
		c1.setRect(new Point((int) (-4 * w / 12), (int) (-4 * h / 12)),
				new Dimension((int) (2 * w / 12), (int) (2 * h / 12)));
		Oval c2 = (Oval) getDisplayElement(s3);
		c2.setRect(new Point((int) (2 * w / 12), (int) (-4 * h / 12)),
				new Dimension((int) (2 * w / 12), (int) (2 * h / 12)));
		Oval c3 = (Oval) getDisplayElement(s4);
		c3.setRect(new Point((int) (2 * w / 12), (int) (2 * h / 12)),
				new Dimension((int) (2 * w / 12), (int) (2 * h / 12)));
		Oval c4 = (Oval) getDisplayElement(s5);
		c4.setRect(new Point((int) (-4 * w / 12), (int) (2 * h / 12)),
				new Dimension((int) (2 * w / 12), (int) (2 * h / 12)));
		PolyLineClosed l = (PolyLineClosed) getDisplayElement(s6);
		Polygon lt = l.getPolygon();
		lt.xpoints[0] = 0;
		lt.ypoints[0] = (int) (4 * h / 12);
		lt.xpoints[1] = (int) (4 * w / 12);
		lt.ypoints[1] = 0;
		lt.xpoints[2] = 0;
		lt.ypoints[2] = (int) (-4 * h / 12);
		lt.xpoints[3] = (int) (-4 * w / 12);
		lt.ypoints[3] = 0;
		Rectangle r2 = centeredSquare(width, height, (int) (6 * w / 12));
		getDisplayElement(s7).setRect(r2);
	}
}
