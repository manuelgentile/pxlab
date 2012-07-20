package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A cross context matching pattern with two center-surround fields. There are
 * two small test fields in two bigger and different surround fields. The two
 * test fields may be adjusted independently. Try to adjust the two test field
 * stimuli until they match in color.
 * 
 * 
 * <p>
 * The special mode shows the two test fields without surrounds. Thus one may
 * see the effect of the surround fields on the adjustment.
 * 
 * <P>
 * An experiment on induction has been conducted by Ware und Cowan (1982). Color
 * induction is discussed by Wyszecki (1986).
 * 
 * @version 0.1.2
 */
/*
 * 02/11/00
 */
public class CrossContextMatching extends Display {
	public ExPar LeftCenterColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.391)), "Left center color");
	public ExPar LeftSurroundColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.25)), "Left surround color");
	public ExPar RightCenterColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.391)), "Right center color");
	public ExPar RightSurroundColor = new ExPar(COLOR, new ExParValue(
			new PxlColor(0.563)), "Right surround color");
	public ExPar CenterSize = new ExPar(PROPORTION, new ExParValue(0.4),
			"Foreground square size");

	/** Cunstructor creating the title of the display. */
	public CrossContextMatching() {
		setTitleAndTopic("Cross Context Matching", COMPLEX_COLOR_MATCHING_DSP);
	}
	private int s1, s2, s3, s4;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(LeftSurroundColor), group[0]);
		s2 = enterDisplayElement(new Bar(RightSurroundColor), group[0]);
		s3 = enterDisplayElement(new Bar(LeftCenterColor), group[0] + group[1]);
		s4 = enterDisplayElement(new Bar(RightCenterColor), group[0] + group[1]);
		// defaultTiming(0);
		return (s4);
	}

	protected void computeGeometry() {
		// get the squares
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
