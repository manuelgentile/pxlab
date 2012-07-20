package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Simultanous brightness contrast demonstration.
 * 
 * @version 0.1.1
 */
public class SimultanousBrightnessContrast extends Display {
	public ExPar CenterSize = new ExPar(PROPORTION, new ExParValue(0.4),
			"Inner square size");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.darkGray)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color 2");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.gray)), "Color 3");

	/** Cunstructor creating the title of the display. */
	public SimultanousBrightnessContrast() {
		setTitleAndTopic("Simultanous Brightness Contrast", COLOR_CONTRAST_DSP
				| DEMO);
	}
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
