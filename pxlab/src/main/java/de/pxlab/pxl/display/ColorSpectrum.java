package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.*;

/**
 * A smooth color spectrum computed to wavelength distributions.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/01/21
 */
public class ColorSpectrum extends Display {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Dummy bar color");

	public ColorSpectrum() {
		setTitleAndTopic("Smooth Color Spectrum", DISPLAY_TEST_DSP | DEMO);
	}
	private int nLines;
	private int s1, x1, x2, y1, y2;
	private Color[] spectrum = new Color[(700 - 400) + 1];

	protected int create() {
		s1 = enterDisplayElement(new Bar(Color));
		SpectralDistribution sd;
		SpectralDistribution CIEy = SpectralDistributionFactory.yCMF31();
		int k = 0;
		for (int i = 400; i <= 700; i++) {
			sd = new SpectralDistribution(400, 701, 1, 0.0F);
			sd.setValueAt(i, 1.0);
			PxlColor c = sd.toXYZ();
			c.setY(CIEy.valueAt(i) * 80.0);
			spectrum[k++] = c.dev();
		}
		return s1;
	}

	protected void computeGeometry() {
		Rectangle r = centeredRect(width, height, (int) (4 * width / 5),
				(int) (height / 4));
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
			int xx1 = x1;
			int xx2 = x2;
			for (int i = 0; i < nLines; i++) {
				g.setColor(spectrum[(int) ((double) ((spectrum.length - 1)) * i / (double) (nLines - 1))]);
				g.drawLine(xx1++, y1, xx2++, y2);
			}
		}
	}
}
