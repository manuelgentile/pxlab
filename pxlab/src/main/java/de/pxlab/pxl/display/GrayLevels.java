package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A smooth transition of gray levels.
 * 
 * @author M. Hodapp
 * @version 0.1.0
 */
/*
 * 05/15/00
 */
public class GrayLevels extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLACK)), "First Color of the gray levels");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.WHITE)), "Last Color of the gray levels");

	/** Create the GrayLevels display. */
	public GrayLevels() {
		setTitleAndTopic("Smooth Gray Levels: Contrast", DISPLAY_TEST_DSP
				| DEMO);
	}
	private int nLines;
	private int s1, x1, x2, y1, y2;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle r = centeredRect(width, height, (int) (4 * width / 5),
				(int) (height / 3));
		nLines = r.width;
		x1 = r.x;
		x2 = r.x;
		y1 = r.y;
		y2 = r.y + r.height;
	}

	/**
	 * This display has its own paint method for drawing the non-selectable
	 * transition area.
	 */
	public void show(Graphics g) {
		super.show(g);
		// Now draw the transition area. Positions have already been computed.
		if (nLines > 0) {
			PxlColor[] ramp = Color1.getPxlColor().sinusoidalRampTo(
					Color2.getPxlColor(), nLines + 1);
			int xx1 = x1;
			int xx2 = x2;
			for (int i = 0; i < nLines; i++) {
				g.setColor(ramp[i + 1].dev());
				g.drawLine(xx1++, y1, xx2++, y2);
			}
		}
	}
}
