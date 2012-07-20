package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

public class LIStairs extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(0, 0, 0))), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(1, 1, 1))), "Color2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(2, 2, 2))), "Color3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(3, 3, 3))), "Color4");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(4, 4, 4))), "Color5");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(5, 5, 5))), "Color6");
	public ExPar Color7 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(6, 6, 6))), "Color7");
	public ExPar Color8 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(7, 7, 7))), "Color8");
	public ExPar Color9 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Background Color");

	/** Cunstructor creating the title of the display. */
	public LIStairs() {
		setTitleAndTopic("Low Intensity Stairs: Brightness", DISPLAY_TEST_DSP
				| DEMO);
	}
	private int rc;
	private int rows = 2;
	private int columns = 8;
	private int s1, s2, s3, s4;

	/** Initialize the display list of the demo. */
	protected int create() {
		rc = rows * columns;
		s1 = enterDisplayElement(new Bar(Color1));
		enterDisplayElement(new Bar(Color1));
		enterDisplayElement(new Bar(Color2));
		enterDisplayElement(new Bar(Color3));
		enterDisplayElement(new Bar(Color4));
		enterDisplayElement(new Bar(Color5));
		enterDisplayElement(new Bar(Color6));
		enterDisplayElement(new Bar(Color7));
		enterDisplayElement(new Bar(Color8));
		enterDisplayElement(new Bar(Color8));
		enterDisplayElement(new Bar(Color7));
		enterDisplayElement(new Bar(Color6));
		enterDisplayElement(new Bar(Color5));
		enterDisplayElement(new Bar(Color4));
		enterDisplayElement(new Bar(Color3));
		enterDisplayElement(new Bar(Color2));
		enterDisplayElement(new Bar(Color1));
		return (s1);
	}

	protected void computeColors() {
		ExPar.ScreenBackgroundColor.set(Color9.getPxlColor());
	}

	protected void computeGeometry() {
		Rectangle r1 = centeredRect(width, height, width, height);
		Rectangle r2 = centeredRect(width, height, (int) (3 * width / 4),
				(int) (2 * height / 3));
		int gapX = 30;
		int gapY = 20;
		Rectangle[] r3 = rectPattern(r2, rows, columns, gapX, gapY, 0, 0);
		// getDisplayElement(s1).setRect(r1);
		int k = 2;
		for (int i = 0; i < rc; i++) {
			getDisplayElement(k + i).setRect(r3[i].x, r3[i].y, r3[i].width,
					r3[i].height);
		}
	}
}
