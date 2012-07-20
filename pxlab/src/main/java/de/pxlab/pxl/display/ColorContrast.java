package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Demonstrates a color contrast effect.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ColorContrast extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(41.4,
			34.6, 12.4)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(51.2,
			60.3, 10.7)), "Color2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(40.9,
			41.8, 13.5)), "Color3");
	public ExPar LineThickness = new ExPar(PROPORTION, new ExParValue(0.5),
			"Line thickness");

	/** Cunstructor creating the title of the display. */
	public ColorContrast() {
		setTitleAndTopic("Simultanous Color Contrast", COLOR_CONTRAST_DSP
				| DEMO);
	}
	private int c1, c2, c3;
	private int s1, s2, s3, s4, s5, s6, s7, s8, s9;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color3), group[0]);
		s4 = enterDisplayElement(new Bar(Color3), group[0]);
		s5 = enterDisplayElement(new Bar(Color3), group[0]);
		s6 = enterDisplayElement(new Bar(Color3), group[0]);
		s7 = enterDisplayElement(new Bar(Color3), group[0]);
		s8 = enterDisplayElement(new Bar(Color3), group[0]);
		s9 = enterDisplayElement(new Bar(Color3), group[0]);
		defaultTiming(0);
		return (s3);
	}

	protected void computeGeometry() {
		Rectangle r = centeredRect(width, height, 2 * width / 3, 3 * height / 4);
		Rectangle r1 = new Rectangle(r.x, r.y, r.width / 2, r.height);
		Rectangle r2 = new Rectangle(r.x + r1.width, r.y, r1.width, r1.height);
		double par = LineThickness.getDouble();
		int x1 = r1.width / 8;
		int xx1 = 8 * x1;
		int xSize = xx1 / 8;
		int y1 = r1.height / 10;
		int yy1 = y1 * 10;
		int ySize = yy1 / 10;
		Rectangle r3 = new Rectangle(r1.x + 2 * xSize, r1.y + 2 * ySize,
				(int) (xSize * par), 6 * ySize);
		Rectangle r5 = new Rectangle(
				(int) (r1.x + 5 * xSize + xSize - (xSize * par)), r3.y,
				r3.width, r3.height);
		Rectangle r4 = new Rectangle(r3.x + r3.width,
				(int) (r3.y + r3.height - (int) (xSize * par)), r5.x - r3.x
						- r3.width, (int) (xSize * par));
		Rectangle r7 = new Rectangle(r2.x + (r2.x - r5.x - r5.width), r2.y + 2
				* ySize, (int) (xSize * par), 6 * ySize);
		Rectangle r6 = new Rectangle(r5.x + r5.width, r5.y, r7.x - r5.x
				- r5.width, (int) (xSize * par));
		Rectangle r9 = new Rectangle(
				(int) (r2.x + 5 * xSize + xSize - (xSize * par)), r7.y,
				r7.width, r7.height);
		Rectangle r8 = new Rectangle(r7.x + r7.width,
				(int) (r7.y + r7.height - (int) (xSize * par)), r9.x - r7.x
						- r7.width, (int) (xSize * par));
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		getDisplayElement(s5).setRect(r5);
		getDisplayElement(s6).setRect(r6);
		getDisplayElement(s7).setRect(r7);
		getDisplayElement(s8).setRect(r8);
		getDisplayElement(s9).setRect(r9);
	}
}
