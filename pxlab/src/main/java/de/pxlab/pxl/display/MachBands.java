package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Mach Bands - A smooth transition from a dark to a bright field shows light
 * and dark bands at the start and end of the transition.
 * 
 * <P>
 * Look at the points where the brighter/darker area enters the smooth
 * transition zone. There is a brightness enhancement at the region where the
 * bright area becomes darker and there is a darker band where the dark area
 * becomes brighter. These have first been described by the Austrian physicist
 * Ernst Mach.
 * 
 * <P>
 * Mach, E. (1865). �ber die Wirkung der r�umlichen Vertheilung des Lichtreizes
 * auf die Netzhaut. Akademie der Wissenschaften, Wien, mathematisch-
 * naturwissenschaftliche Klasse, 52, 303-322.
 * 
 * <P>
 * Ratliff, F. (1965). Mach bands: Quantitative studies on neural networks in
 * the retina. San Francisco: Holden Day.
 * 
 * <p>
 * This demo has two color rectangles which are connected by a smooth transition
 * area. It shows how computed colors may be used without color parameters.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 03/20/00
 */
public class MachBands extends Display {
	public ExPar TransitionRegion = new ExPar(PROPORT, new ExParValue(0.15),
			"Size of the transition region");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.gray)), "Color1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color2");

	/** Create the Mach band demo. */
	public MachBands() {
		setTitleAndTopic("Mach Bands", LATERAL_INHIBITION_DSP | DEMO);
	}
	private int s1, s2;
	private int nLines;
	private boolean hor;
	private int x1, x2, y1, y2;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color2));
		return (s1);
	}

	/**
	 * The shapes are derived from double squares. The transition area size is
	 * an adjustible parameter.
	 */
	protected void computeGeometry() {
		Rectangle r1 = firstSquareOfTwo(width, height, false);
		Rectangle r2 = secondSquareOfTwo(width, height, false);
		nLines = 2 * ((int) Math.round(r1.width * TransitionRegion.getDouble())) / 2;
		hor = (r1.x < r2.x);
		if (hor) {
			// We are horizontal
			r1.width -= nLines;
			r2.x += nLines;
			r2.width -= nLines;
			x1 = x2 = r1.x + r1.width;
			y1 = r1.y;
			y2 = r1.y + r1.height - 1;
		} else {
			// We are vertical
			r1.height -= nLines;
			r2.y += nLines;
			r2.height -= nLines;
			x1 = r1.x;
			x2 = r1.x + r1.width - 1;
			y1 = y2 = r1.y + r1.height;
		}
		getDisplayElement(s1).setRect(r1);
		getDisplayElement(s2).setRect(r2);
	}

	/**
	 * This demo has its own show method for drawing the non-selectable
	 * transition area.
	 */
	public void show(Graphics g) {
		// First draw the bounding rectangles.
		super.show(g);
		// Now draw the transition area. Positions have already been computed.
		if (nLines > 0) {
			int nLines2 = (nLines + nLines);
			PxlColor[] ramp = Color1.getPxlColor().linearRampTo(
					Color2.getPxlColor(), nLines2 + 2);
			int xx1 = x1, xx2 = x2, yy1 = y1, yy2 = y2;
			for (int i = 0; i < nLines2; i++) {
				g.setColor(ramp[i + 1].dev());
				if (hor) {
					g.drawLine(xx1++, yy1, xx2++, yy2);
				} else {
					g.drawLine(xx1, yy1++, xx2, yy2++);
				}
			}
		}
	}
}
