package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This demo shows a sinusoidal grating of two colors which is painted directly
 * onto the DisplayPanel.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 03/23/00
 */
public class SinusoidalGrating extends Display {
	public ExPar SpatialFrequency = new ExPar(UNKNOWN, new ExParValue(10.0),
			"Spatial frequency");
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.gray)), "Color 1");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color 2");

	/** Create the grating demo. */
	public SinusoidalGrating() {
		setTitleAndTopic("Sinusoidal Grating", GRATING_DSP | DEMO);
	}
	private int s1;
	private int nLines;
	private int x1, x2, y1, y2;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		return (s1);
	}

	protected void computeColors() {
	}

	/**
	 * We actually do not have shapes here but simple compute the starting and
	 * ending points of the line grating pattern.
	 */
	protected void computeGeometry() {
		Rectangle r = largeSquare(width, height);
		r.y += r.height / 4;
		r.height /= 2;
		nLines = (int) (r.width / SpatialFrequency.getDouble());
		x1 = r.x;
		x2 = x1 + r.width;
		y1 = r.y;
		y2 = r.y + r.height - 1;
	}

	/**
	 * This demo has its own paint method for drawing line grating pattern.
	 */
	public void show(Graphics g) {
		// First draw the background rectangle.
		setGraphicsContext(g);
		((DisplayElement) getDisplayElement(0)).show();
		// Now we have to recompute the colors since these depend on
		// the number of lines in one grating period
		PxlColor[] ramp = Color1.getPxlColor().sinusoidalRampTo(
				Color2.getPxlColor(), nLines);
		Color[] ramp_c = new Color[nLines];
		for (int i = 0; i < nLines; i++)
			ramp_c[i] = ramp[i].dev();
		// Now we ar ready to do the drawing
		int di = 1;
		int i = 0;
		for (int x = x1; x < x2; x++) {
			g.setColor(ramp_c[i]);
			g.drawLine(x, y1, x, y2);
			i += di;
			if (i == nLines) {
				i -= 2;
				di = -di;
			} else if (i < 0) {
				i = 1;
				di = -di;
			}
		}
	}
}
