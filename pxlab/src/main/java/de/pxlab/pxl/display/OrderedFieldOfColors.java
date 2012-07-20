package de.pxlab.pxl.display;

import java.awt.*;
import java.util.*;
import de.pxlab.util.Randomizer;
import de.pxlab.pxl.*;

/**
 * This is an ordered field of color mixtures. Look at the gamut and compare
 * this with a randomized display of the same colors.
 * 
 * <P>
 * After Jan Koenderink and Astrid Kappers.
 * 
 * <P>
 * Koenderink, J. & Kappers, A. (1996). Color Space. Part of the course: The
 * structure of matter. ZiF-Report Nr. 16/96. Bielefeld: Zentrum f�r
 * interdisziplin�re Forschung.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class OrderedFieldOfColors extends Display {
	public ExPar FirstColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			20.0, 0.235, 0.399)), "First Mixture Color");
	public ExPar SecondColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			20.0, 0.621, 0.34)), "Second Mixture Color");
	public ExPar DarkColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.darkGray)), "Dark Gray Color");

	/** Cunstructor creating the title of the display. */
	public OrderedFieldOfColors() {
		setTitleAndTopic("Ordered Field of Colors", COMPLEX_COLOR_MATCHING_DSP
				| DEMO);
	}
	private int n = 12, nn;
	private int firstIndex;
	private ExPar[] colorTable;

	/** Initialize the display list of the demo. */
	public int create() {
		nn = n * n;
		colorTable = new ExPar[nn];
		firstIndex = nextDisplayElementIndex();
		for (int i = 0; i < nn; i++) {
			colorTable[i] = new ExPar(DEPCOLOR, new ExParValue(new PxlColor()),
					null);
			enterDisplayElement(new Bar(colorTable[i]), group[0]);
		}
		return (firstIndex);
	}

	protected void destroy() {
		colorTable = null;
	}

	protected void computeColors() {
		PxlColor p = DarkColor.getPxlColor();
		PxlColor a = FirstColor.getPxlColor();
		PxlColor b = SecondColor.getPxlColor();
		double kx, ky, kz;
		PxlColor a1, b1;
		int i = 0;
		for (int iy = 0; iy < n; iy++) {
			ky = (double) iy / (double) (n - 1.0);
			for (int ix = 0; ix < n; ix++) {
				kx = (double) ix / (double) (n - 1.0);
				a1 = a.mix(kx, p);
				b1 = b.mix(ky, p);
				kz = (kx - ky + 1.0) / 2.0;
				colorTable[i++].set(a1.mix(kz, b1));
			}
		}
	}

	protected void computeGeometry() {
		Rectangle r = largeSquare(width, height);
		Rectangle[] p = rectPattern(r, n, n, 0, 0, 0, 0);
		for (int i = 0; i < nn; i++)
			getDisplayElement(firstIndex + i).setRect(p[i].x, p[i].y,
					p[i].width, p[i].height);
	}

	/**
	 * This stepping display is easy since we have only a single step to show.
	 */
	public void showGroup() {
		int[] a = new int[nn];
		for (int i = 0; i < nn; i++)
			a[i] = i;
		Randomizer rnd = new Randomizer();
		rnd.randomize(a);
		for (int i = 0; i < nn; i++) {
			((Bar) getDisplayElement(firstIndex + i)).show(colorTable[a[i]]);
		}
	}
}
