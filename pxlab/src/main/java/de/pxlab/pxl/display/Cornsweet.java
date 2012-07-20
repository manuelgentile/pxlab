package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Simulates the Craik-O'Brian-Cornsweet pattern.
 * 
 * <P>
 * The inner and outer area have the same luminance. The ring is a luminance
 * gradient which creates the impression of the inner area being darker than the
 * outer area. This demonstrates the role of luminance gradients for the
 * brightness of an enclosed area.
 * 
 * <P>
 * Cornsweet, T. N. (1979). Visual Perception. New York: Academic Press.
 * 
 * <P>
 * Craik, K. J. W. (1966). The nature of psychology. A selection of papers,
 * essays, and other writings by the late K. J. W. Craik. S. L. Sherwood (Ed.),
 * Cambridge: Cambridge Universitiy Press.
 * 
 * <P>
 * O'Brien, V. (1958). Contour perception, illusion and reality. JOSA, 48,
 * 112-119.
 * 
 * <P>
 * See also: Spillmann, L. & Werner, John S. (Eds.) (1990). Visual Perception:
 * The Neurophysiological Foundations. New York: Academic Press.
 * 
 * @version 0.1.1
 */
/*
 * 05/15/00
 */
public class Cornsweet extends Display {
	public ExPar TransitionRegion = new ExPar(PROPORTION, new ExParValue(0.4),
			"Transition Region");
	public ExPar Size = new ExPar(PROPORTION, new ExParValue(0.7),
			"Inner Circel Size");
	public ExPar CircleColor = new ExPar(COLOR, new ExParValue(21.952, 0.307,
			0.347), "Color of the circle");
	public ExPar RingColor1 = new ExPar(COLOR, new ExParValue(23.05, 0.307,
			0.347), "First Ring Color");
	public ExPar RingColor2 = new ExPar(COLOR, new ExParValue(20.854, 0.307,
			0.347), "Second Ring Color");

	/** Create the Cornsweet demo. */
	public Cornsweet() {
		setTitleAndTopic("Craik - O'Brien - Cornsweet - Pattern",
				COLOR_CONTRAST_DSP | DEMO);
	}
	private int s1;
	int nLines;
	int x1, y1, size;

	public int create() {
		s1 = enterDisplayElement(new Oval(CircleColor));
		defaultTiming(0);
		return (s1);
	}

	/**
	 * The shapes are derived from double squares. The transition area size is
	 * an adjustible parameter.
	 */
	protected void computeGeometry() {
		double trans = TransitionRegion.getDouble();
		double s = Size.getDouble();
		Rectangle r = largeSquare(width, height);
		nLines = 2 * ((int) Math.round(r.width / 8 * trans)) / 2;
		size = (int) (r.width * s);
		x1 = (int) (r.x + r.width / 2 - size / 2);
		y1 = (int) (r.y + r.height / 2 - size / 2);
		getDisplayElement(s1).setRect(r);
	}

	/**
	 * This demo has its own paint method for drawing the non-selectable
	 * transition area.
	 */
	public void show(Graphics g) {
		// First draw the background ellipse.
		super.show(g);
		// Now draw the transition area. Positions have already been computed.
		if (nLines > 0) {
			PxlColor[] ramp = CircleColor.getPxlColor().sinusoidalRampTo(
					RingColor1.getPxlColor(), nLines + 1);
			PxlColor[] ramp2 = RingColor2.getPxlColor().sinusoidalRampTo(
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
				g.setColor(ramp2[i + 1].dev());
				g.fillOval(xx2++, yy2++, size2--, size2--);
				k -= 1;
			}
		}
	}
}
