package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * DIN 6169 Video display test colors.
 * 
 * <P>
 * Shows 17 test colors which are used in DIN 6169 for color displays.
 * 
 * <P>
 * Richter, K. (1996). Computergrafik und Farbmetrik. Berlin: VDE-Verlag.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 05/18/00
 */
public class DINVideo2 extends Display {
	public ExPar ColorG = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.gray)), "Color");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new YxyColor(29.67,
			0.381, 0.340)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new YxyColor(29,
			0.386, 0.403)), "Color2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new YxyColor(30.19,
			0.371, 0.471)), "Color3");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new YxyColor(29.15,
			0.289, 0.416)), "Color4");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(new YxyColor(0.001,
			0.313, 0.329)), "Color5");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(new YxyColor(31.13,
			0.26, 0.321)), "Color6");
	public ExPar Color7 = new ExPar(COLOR, new ExParValue(new YxyColor(29.9,
			0.243, 0.258)), "Color7");
	public ExPar Color8 = new ExPar(COLOR, new ExParValue(new YxyColor(29.95,
			0.291, 0.259)), "Color8");
	public ExPar Color9 = new ExPar(COLOR, new ExParValue(new YxyColor(31.44,
			0.333, 0.276)), "Color9");
	public ExPar Color10 = new ExPar(COLOR, new ExParValue(new YxyColor(0.001,
			0.313, 0.329)), "Color10");
	public ExPar Color11 = new ExPar(COLOR, new ExParValue(new YxyColor(11.18,
			0.567, 0.312)), "Color11");
	public ExPar Color12 = new ExPar(COLOR, new ExParValue(new YxyColor(58.78,
			0.434, 0.471)), "Color12");
	public ExPar Color13 = new ExPar(COLOR, new ExParValue(new YxyColor(19.83,
			0.253, 0.422)), "Color13");
	public ExPar Color14 = new ExPar(COLOR, new ExParValue(new YxyColor(6.75,
			0.172, 0.160)), "Color14");
	public ExPar Color15 = new ExPar(COLOR, new ExParValue(new YxyColor(0.001,
			0.313, 0.329)), "Color15");
	public ExPar Color16 = new ExPar(COLOR, new ExParValue(new YxyColor(57.5,
			0.373, 0.363)), "Color16");
	public ExPar Color17 = new ExPar(COLOR, new ExParValue(new YxyColor(11.76,
			0.356, 0.448)), "Color17");
	public ExPar Color18 = new ExPar(COLOR, new ExParValue(new YxyColor(3.69,
			0.316, 0.337)), "Color18");
	public ExPar Color19 = new ExPar(COLOR, new ExParValue(new YxyColor(19.58,
			0.310, 0.328)), "Color19");
	public ExPar Color20 = new ExPar(COLOR, new ExParValue(new YxyColor(75.78,
			0.313, 0.327)), "Color20");

	public DINVideo2() {
		setTitleAndTopic("DIN 6169 Video: Display Test Colors 2",
				DISPLAY_TEST_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14,
			s15, s16, s17, s18, s19, s20, s21;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(ColorG));
		s2 = enterDisplayElement(new Bar(Color1));
		s3 = enterDisplayElement(new Bar(Color2));
		s4 = enterDisplayElement(new Bar(Color3));
		s5 = enterDisplayElement(new Bar(Color4));
		s6 = enterDisplayElement(new Bar(Color5));
		s7 = enterDisplayElement(new Bar(Color6));
		s8 = enterDisplayElement(new Bar(Color7));
		s9 = enterDisplayElement(new Bar(Color8));
		s10 = enterDisplayElement(new Bar(Color9));
		s11 = enterDisplayElement(new Bar(Color10));
		s12 = enterDisplayElement(new Bar(Color11));
		s13 = enterDisplayElement(new Bar(Color12));
		s14 = enterDisplayElement(new Bar(Color13));
		s15 = enterDisplayElement(new Bar(Color14));
		s16 = enterDisplayElement(new Bar(Color15));
		s17 = enterDisplayElement(new Bar(Color16));
		s18 = enterDisplayElement(new Bar(Color17));
		s19 = enterDisplayElement(new Bar(Color18));
		s20 = enterDisplayElement(new Bar(Color19));
		s21 = enterDisplayElement(new Bar(Color20));
		return (s3);
	}

	protected void computeGeometry() {
		// get the squares
		int rows = 4;
		int columns = 5;
		int gap = 5;
		int vb = 5;
		int hb = 5;
		int k = 2;
		int n = rows * columns;
		Rectangle r = centeredRect(width, height, (int) (3 * width / 4),
				(int) (2 * height / 3));
		Rectangle[] p = rectPattern(r, rows, columns, gap, gap, vb, hb);
		getDisplayElement(s1).setRect(r);
		for (int i = 0; i < n; i++) {
			getDisplayElement(k + i).setRect(p[i]);
		}
	}
}
