package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * One and the same color stimulus is shown in two different surround fields.
 * The color stimulus looks different in the two surrounds.
 * <P>
 * Color contrast effects are discussed by Hurvich (1981).
 * <P>
 * Hurvich, L. M. (1981). Color vision. Sunderland, MA: Sinauer.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 05/12/00
 */
public class SimultanousColorContrast extends Display {
	public ExPar CenterSize = new ExPar(PROPORTION, new ExParValue(0.4),
			"Inner square size");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new YxyColor(44.44,
			0.22, 0.29)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new YxyColor(75.8,
			0.39, 0.51)), "Color 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new YxyColor(71.5,
			0.33, 0.45)), "Color 3");

	/** Cunstructor creating the title of the display. */
	public SimultanousColorContrast() {
		setTitleAndTopic("Simultanous Color Contrast", COLOR_CONTRAST_DSP
				| DEMO);
	}
	private int c1, c2, c3;
	private int s1, s2, s3, s4;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color3), group[0]);
		s4 = enterDisplayElement(new Bar(Color3), group[0]);
		return (s3);
	}

	protected void computeGeometry() {
		Rectangle r1 = firstSquareOfTwo(width, height, true);
		Rectangle r2 = secondSquareOfTwo(width, height, true);
		Rectangle r3 = innerRect(r1, CenterSize.getDouble());
		Rectangle r4 = innerRect(r2, CenterSize.getDouble());
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		getDisplayElement(s3).setRect(r3);
		getDisplayElement(s4).setRect(r4);
	}
}
