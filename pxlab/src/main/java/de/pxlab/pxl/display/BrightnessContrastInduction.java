package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Brightness and Color Contrast Induction. Shows that the contrast of a
 * surround field affects the perceived contrast of a center field.
 * 
 * <P>
 * Singer, B. & D'Zmura, M. (1993). Color Contrast Induction. Vision Research,
 * 34, 3111-3126.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 14/08/00
 */
public class BrightnessContrastInduction extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.white)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.darkGray)), "Color 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color 1");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color 2");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color 1");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color 2");

	public BrightnessContrastInduction() {
		setTitleAndTopic("Brightness Contrast Induction", COLOR_CONTRAST_DSP
				| DEMO);
	}
	private int s1, s2, s3, s4;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color6));
		for (int k = 0; k < 50; k++) {
			enterDisplayElement(new Bar(Color2));
		}
		for (int l = 0; l < 50; l++) {
			enterDisplayElement(new Bar(Color5));
		}
		s3 = enterDisplayElement(new Bar(Color3));
		s4 = enterDisplayElement(new Bar(Color3));
		for (int m = 0; m < 28; m++) {
			enterDisplayElement(new Bar(Color4));
		}
		defaultTiming(0);
		return (s1);
	}

	protected void computeColors() {
		Color3.set(Color1.getPxlColor().mix(0.8, Color2.getPxlColor()));
		Color4.set(Color1.getPxlColor().mix(0.2, Color2.getPxlColor()));
		Color6.set(Color1.getPxlColor().mix(0.6, Color2.getPxlColor()));
		Color5.set(Color1.getPxlColor().mix(0.4, Color2.getPxlColor()));
	}

	protected void computeGeometry() {
		computeColors();
		// get the 2 big squares
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		int size = (int) (r1.width / 10);
		r1.width = size * 10;
		r1.height = size * 10;
		int size2 = (int) (r2.width / 10);
		r2.width = size2 * 10;
		r2.height = size2 * 10;
		int x1 = (int) (r1.x);
		int x3 = (int) (r2.x);
		int y = (int) (r1.y);
		int y2 = (int) (r2.y);
		for (int i = 1; i < 6; i++) {
			getDisplayElement(2 + i).setRect(new Rectangle(x1, y, size, size));
			getDisplayElement(7 + i).setRect(
					new Rectangle(x1, y + 2 * size, size, size));
			getDisplayElement(12 + i).setRect(
					new Rectangle(x1, y + 4 * size, size, size));
			getDisplayElement(17 + i).setRect(
					new Rectangle(x1, y + 6 * size, size, size));
			getDisplayElement(22 + i).setRect(
					new Rectangle(x1, y + 8 * size, size, size));
			x1 += 2 * size;
			getDisplayElement(52 + i).setRect(
					new Rectangle(x3, y2, size2, size2));
			getDisplayElement(57 + i).setRect(
					new Rectangle(x3, y2 + 2 * size2, size2, size2));
			getDisplayElement(62 + i).setRect(
					new Rectangle(x3, y2 + 4 * size2, size2, size2));
			getDisplayElement(67 + i).setRect(
					new Rectangle(x3, y2 + 6 * size2, size2, size2));
			getDisplayElement(72 + i).setRect(
					new Rectangle(x3, y2 + 8 * size2, size2, size2));
			x3 += 2 * size;
		}
		int x2 = (int) (r1.x + size);
		int x4 = (int) (r2.x + size2);
		for (int j = 1; j < 6; j++) {
			getDisplayElement(27 + j).setRect(
					new Rectangle(x2, y + size, size, size));
			getDisplayElement(32 + j).setRect(
					new Rectangle(x2, y + 3 * size, size, size));
			getDisplayElement(37 + j).setRect(
					new Rectangle(x2, y + 5 * size, size, size));
			getDisplayElement(42 + j).setRect(
					new Rectangle(x2, y + 7 * size, size, size));
			getDisplayElement(47 + j).setRect(
					new Rectangle(x2, y + 9 * size, size, size));
			x2 += 2 * size2;
			getDisplayElement(77 + j).setRect(
					new Rectangle(x4, y2 + size2, size2, size2));
			getDisplayElement(82 + j).setRect(
					new Rectangle(x4, y2 + 3 * size2, size2, size2));
			getDisplayElement(87 + j).setRect(
					new Rectangle(x4, y2 + 5 * size2, size2, size2));
			getDisplayElement(92 + j).setRect(
					new Rectangle(x4, y2 + 7 * size2, size2, size2));
			getDisplayElement(97 + j).setRect(
					new Rectangle(x4, y2 + 9 * size2, size2, size2));
			x4 += 2 * size2;
		}
		// get the inner squares
		Rectangle r3 = innerRect(r1, 0.4);
		Rectangle r4 = innerRect(r2, 0.4);
		r3.width = 6 * r3.width / 6;
		r3.height = 6 * r3.height / 6;
		r4.width = 6 * r4.width / 6;
		r4.height = 6 * r4.height / 6;
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
		int size3 = (int) (r3.width / 4);
		int size4 = (int) (r3.width / 4);
		int x5 = (int) (r3.x);
		int x6 = (int) (r3.x + size3);
		int x7 = (int) (r4.x);
		int x8 = (int) (r4.x + size4);
		int y3 = (int) (r3.y);
		int y4 = (int) (r3.y + size3);
		int y5 = (int) (r4.y);
		int y6 = (int) (r4.y + size4);
		for (int n = 1; n < 3; n++) {
			getDisplayElement(104 + n).setRect(
					new Rectangle(x5, y3, size3, size3));
			getDisplayElement(106 + n).setRect(
					new Rectangle(x5, y3 + 2 * size3, size3, size3));
			x5 += 2 * size2;
			getDisplayElement(108 + n).setRect(
					new Rectangle(x6, y4, size3, size3));
			getDisplayElement(110 + n).setRect(
					new Rectangle(x6, y4 + 2 * size3, size3, size3));
			x6 += 2 * size2;
		}
		for (int n = 1; n < 3; n++) {
			getDisplayElement(112 + n).setRect(
					new Rectangle(x7, y5, size4, size4));
			getDisplayElement(114 + n).setRect(
					new Rectangle(x7, y5 + 2 * size4, size4, size4));
			x7 += 2 * size2;
			getDisplayElement(116 + n).setRect(
					new Rectangle(x8, y6, size4, size4));
			getDisplayElement(118 + n).setRect(
					new Rectangle(x8, y6 + 2 * size4, size4, size4));
			x8 += 2 * size2;
		}
	}
}
