package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Looking Glass Suite, No. 9.
 * 
 * <P>
 * This is after a lithograph by Peter Sedgley, entitled "Looking Glass Suite,
 * No. 9". It has been published by Lindsay & Norman (1977). Try to fixate the
 * center cross. After some time you will see the ring disappear. Since the
 * pattern is approximately isoluminant and there is no high frequency contour
 * local adaptation leads to disappearance of the ring and a filling in with the
 * surround color.
 * 
 * <P>
 * Lindsay, P. H. & Norman, D. A. (1977). Human information processing: An
 * introduction to psychology. New York: Academic Press.
 * 
 * <P>
 * Livingstone, M. S., & Hubel, D. H. (1988). Segregation of form, color,
 * movement, and depth: Anatomy, physiology, and perception. Science, 240,
 * 740-749.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 
 * 05/15/00
 */
public class LookingGlass extends Display {
	public ExPar SurroundColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			50.0, 0.449, 0.449)), "Surround color");
	public ExPar CircleColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			48.0, 0.405, 0.301)), "Circle color");
	public ExPar CrossColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Cross color");
	public ExPar TransitionRegion = new ExPar(PROPORTION, new ExParValue(0.5),
			"Transition Region");
	public ExPar Size = new ExPar(PROPORTION, new ExParValue(0.6),
			"Inner Circel Size");

	/** Create the LookingGlass display. */
	public LookingGlass() {
		setTitleAndTopic("Looking Glass Suite, No. 9", COLOR_CONTRAST_DSP
				| DEMO);
	}
	private int s1, s3;
	private int nLines;
	private int x1, y1, size;

	protected int create() {
		s1 = enterDisplayElement(new Oval(SurroundColor));
		s3 = enterDisplayElement(new Cross(CrossColor));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle r = largeSquare(width, height);
		nLines = 2 * ((int) Math.round(r.width / 8
				* TransitionRegion.getDouble())) / 2;
		size = (int) (r.width * Size.getDouble());
		x1 = (int) (r.x + r.width / 2 - size / 2);
		y1 = (int) (r.y + r.height / 2 - size / 2);
		getDisplayElement(s1).setRect(r);
		getDisplayElement(s3).setRect(0, 0, size / 16, size / 16);
	}

	/**
	 * This display has its own paint method for drawing the non-selectable
	 * transition area.
	 */
	public void show(Graphics g) {
		// First draw the background ellipse.
		super.show(g);
		// Now draw the transition area. Positions have already been computed.
		if (nLines > 0) {
			int nLines2 = (nLines + nLines);
			PxlColor[] ramp = SurroundColor.getPxlColor().sinusoidalRampTo(
					CircleColor.getPxlColor(), nLines + 1);
			int size2 = size;
			int xx1 = x1, yy1 = y1;
			for (int i = 0; i < nLines; i++) {
				g.setColor(ramp[i + 1].dev());
				g.fillOval(xx1++, yy1++, size2--, size2--);
			}
			int k = nLines - 1;
			int xx2 = x1 + nLines, yy2 = y1 + nLines;
			for (int i = 0; i < nLines; i++) {
				g.setColor(ramp[k].dev());
				g.fillOval(xx2++, yy2++, size2--, size2--);
				k -= 1;
			}
		}
		((Cross) getDisplayElement(s3)).show();
	}
}
