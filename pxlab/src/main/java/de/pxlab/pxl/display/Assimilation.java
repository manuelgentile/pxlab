package de.pxlab.pxl.display;

import de.pxlab.pxl.Display;
import de.pxlab.pxl.*;
import java.awt.*;

/**
 * A checkerboard pattern which shows assimilation of colors.
 * 
 * <P>
 * The color of the squares on the diagonals is affected by the surrounding
 * squares. Since these are rather small, the main effect is not contrast
 * enhancement but assimilation. The diagonal squares approach their surrounding
 * squares in color. The extent of assimilation depends on retinal image size.
 * The smaller the retinal image, the more assimilation. This can be seen best
 * when observation distance is increased by stepping back from the monitor. The
 * larger the distance, the more different become the two diagonals. A closeup
 * view of the monitor, however, shows that both diagonals comprise the same
 * stimulus.
 * 
 * <p>
 * Pattern created after R�hler (1995).
 * 
 * <P>
 * Bezold, W. v. (1874). Die Farbenlehre im Hinblick auf Kunst und Kunstgewerbe.
 * Braunschweig: Verlag von George Westermann.
 * 
 * <P>
 * Helson, H. (1963). Studies of anomalous contrast and assimilation. Journal of
 * the Optical Society of America, 53, 179-184.
 * 
 * <P>
 * R�hler, R. (1995). Sehen und Erkennen: Psychophysik des Gesichtssinnes.
 * Heidelberg: Springer-Verlag.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 05/15/00
 */
public class Assimilation extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.YELLOW)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLUE)), "Color 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.RED)), "Color 3");
	public ExPar Size = new ExPar(PROPORT, new ExParValue(0.5, 0, 1),
			"Square size");

	public Assimilation() {
		setTitleAndTopic("Assimilation", ASSIMILATION_DSP | DEMO);
	}

	public boolean canStep() {
		return (false);
	}
	private int s1;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		for (int k = 0; k < 100; k++) {
			enterDisplayElement(new Bar(Color2));
		}
		for (int k = 0; k < 20; k++) {
			enterDisplayElement(new Bar(Color3));
		}
		defaultTiming();
		return (s1);
	}

	protected void computeGeometry() {
		double par = Size.getDouble();
		int bigsize;
		if (height > width) {
			bigsize = (int) (width / 7.0 * 5.0 * par);
		} else {
			bigsize = (int) (height / 7 * 5.0 * par);
		}
		bigsize = (bigsize / 10) * 10;
		Rectangle r1 = centeredSquare(width, height, bigsize);
		double size = r1.width / 10;
		double y = r1.y;
		double y2 = r1.y;
		double y3 = r1.y;
		double y4 = r1.y;
		int k = 1;
		int m = 1;
		int l = 1;
		int n = 1;
		getDisplayElement(s1).setRect(r1);
		for (int j = 1; j < 6; j++) {
			double x1 = r1.x;
			for (int i = 1; i < 6; i++) {
				getDisplayElement(1 + k).setRect(
						new Rectangle((int) (x1), (int) (y), (int) (size),
								(int) (size)));
				x1 += 2 * size;
				k += 1;
			}
			y += 2 * size;
		}
		for (int j = 1; j < 6; j++) {
			double x2 = r1.x + size;
			for (int i = 1; i < 6; i++) {
				getDisplayElement(51 + m).setRect(
						new Rectangle((int) (x2), (int) (y2) + (int) (size),
								(int) (size), (int) (size)));
				x2 += 2 * size;
				m += 1;
			}
			y2 += 2 * size;
		}
		double x3 = r1.x;
		for (int i = 1; i < 11; i++) {
			getDisplayElement(101 + l).setRect(
					new Rectangle((int) (x3), (int) (y3), (int) (size),
							(int) (size)));
			x3 += size;
			l += 1;
			y3 += size;
		}
		double x4 = r1.x + 9 * size;
		for (int i = 1; i < 11; i++) {
			getDisplayElement(111 + n).setRect(
					new Rectangle((int) (x4), (int) (y4), (int) (size),
							(int) (size)));
			x4 -= size;
			n += 1;
			y4 += size;
		}
	}
}
