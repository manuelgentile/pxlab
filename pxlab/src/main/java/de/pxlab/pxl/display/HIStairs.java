package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

public class HIStairs extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(253, 253, 253))), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(252, 252, 252))), "Color2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(251, 251, 251))), "Color3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(250, 250, 250))), "Color4");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(249, 249, 249))), "Color5");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(248, 248, 248))), "Color6");
	public ExPar Color7 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(247, 247, 247))), "Color7");
	public ExPar Color8 = new ExPar(COLOR, new ExParValue(new PxlColor(
			new Color(246, 246, 246))), "Color8");

	/** Cunstructor creating the title of the display. */
	public HIStairs() {
		setTitleAndTopic("High Intensity Stairs: Contrast", DISPLAY_TEST_DSP
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

	protected void computeGeometry() {
		Rectangle r1 = centeredRect(width, height, width, height);
		Rectangle r2 = centeredRect(width, height, (int) (3 * width / 4),
				(int) (2 * height / 3));
		int gapX = 30;
		int gapY = 20;
		Rectangle[] r3 = rectPattern(r2, rows, columns, gapX, gapY, 0, 0);
		getDisplayElement(s1).setRect(r1);
		int k = 2;
		for (int i = 0; i < rc; i++) {
			getDisplayElement(k + i).setRect(r3[i].x, r3[i].y, r3[i].width,
					r3[i].height);
		}
	}
}
