package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Filling In of Colors.
 * <P>
 * If you fixate the image center then the smooth contours will disappear
 * completely and the center area will be filled with the surround color.
 * 
 * An isoluminant center with a low spatial frequency border to its surround
 * creates filling in of colors from the surround.
 * 
 * <P>
 * Krauskopf, J. (1963). Effect of retinal image stabilization in the appearance
 * of heterochromatic targets. Journal of the Optical Society of America, 53,
 * 741-743.
 * 
 * @version 0.2.1
 */
/*
 * 
 * 05/15/00
 * 
 * 10/28/03 Modifications to use SmoothDisk class
 */
public class FillingIn extends Display {
	public ExPar TransitionRegion = new ExPar(PROPORTION, new ExParValue(0.6),
			"Transition region");
	public ExPar Size = new ExPar(PROPORTION, new ExParValue(0.15),
			"Inner circle size");
	public ExPar SurroundColor = new ExPar(COLOR, new ExParValue(34.98, 0.449,
			0.449), "Surround color");
	public ExPar DiskColor = new ExPar(COLOR, new ExParValue(34.38, 0.405,
			0.301), "Disk color");

	/** Create the FillingIn display. */
	public FillingIn() {
		setTitleAndTopic("Filling In", COLOR_CONTRAST_DSP | DEMO);
	}
	private int s1, s2;

	protected int create() {
		s1 = enterDisplayElement(new Oval(SurroundColor));
		s2 = enterDisplayElement(new SmoothDisk(DiskColor, SurroundColor));
		return (s2);
	}

	protected void computeGeometry() {
		Rectangle r1 = largeSquare(width, height);
		Rectangle r2 = innerRect(r1, Size.getDouble());
		int nLines = (int) Math.round((r1.width - r2.width) / 2
				* TransitionRegion.getDouble());
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
		((SmoothDisk) getDisplayElement(s2)).setTransitionRegion(nLines);
	}
}
