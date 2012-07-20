package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.SpectralDistribution;
import de.pxlab.pxl.spectra.SpectralDistributionFactory;

/**
 * This display shows a constant attribute page of the Munsell Book of Colors.
 * The attribute value and the illuminant's color temperature are adjustable.
 * 
 * @author H. Irtel
 * @version 0.2.2
 */
/*
 * 10/25/00
 * 
 * 06/07/01 use private background color
 * 
 * 10/08/01 changed font size
 */
abstract class MunsellPage extends SpectralColorDisplay {
	public boolean canShowSpectralDistributions() {
		return (true);
	}
	protected int firstIndex;
	protected int nRows;
	protected int nColumns;
	protected int n;
	protected int gap = 3;
	protected Rectangle[] p;
	protected ExPar[] colorTable;
	protected String[] colorName;
	protected boolean showLabels = true;

	/**
	 * Create the display elements of the constant attribute page. We always
	 * create nRows*nColumns patches, even if not all of these are used. This
	 * makes it possible to also create a fixed size table of color parameters
	 * and color names from which to retrieve the colors and their names by the
	 * respective display element index.
	 */
	protected int create() {
		createPage();
		n = nRows * nColumns;
		colorTable = new ExPar[n];
		colorName = new String[n];
		for (int i = 0; i < n; i++)
			colorTable[i] = new ExPar(DEPCOLOR, new ExParValue(new PxlColor()),
					null);
		firstIndex = nextDisplayElementIndex();
		for (int i = 0; i < n; i++)
			enterDisplayElement(new Bar(colorTable[i]));
		return (firstIndex);
	}

	protected void destroy() {
		super.destroy();
		for (int i = 0; i < n; i++) {
			colorTable[i] = null;
			colorName[i] = null;
		}
		colorTable = null;
		colorName = null;
	}

	protected abstract void createPage();

	protected void computeColors() {
		initMunsellColors();
	}

	protected void computeGeometry() {
		// Color are computed here since they depend on adjustable parameters
		initMunsellColors();
		Rectangle rr = new Rectangle(-width / 2, -height / 2, width, height);
		Rectangle r = innerRect(rr, 42.0 / 50.0);
		p = rectPattern(r, nRows, nColumns, gap, gap, 0, 0, 1.4);
		for (int i = 0; i < n; i++)
			getDisplayElement(firstIndex + i).setRect(p[i].x, p[i].y,
					p[i].width, p[i].height);
	}

	protected void initMunsellColors() {
		// First clear all patches to the background color
		PxlColor bg = getDisplayElement(0).getColorPar().getPxlColor();
		for (int i = 0; i < n; i++) {
			colorTable[i].set(bg);
			colorName[i] = null;
		}
		try {
			// Get the illuminant temperature and spectral distribution
			SpectralDistribution light = SpectralDistributionFactory
					.instance(getIlluminantName(0));
			// Set the illuminant luminance to 100 cd/qm
			light.setLuminance(100.0);
			computeMunsellColors(light);
		} catch (SpectrumNotFoundException snf) {
			new ParameterValueError(snf.getMessage());
		}
	}

	protected abstract void computeMunsellColors(SpectralDistribution light)
			throws SpectrumNotFoundException;;

	public void show(Graphics g) {
		super.show(g);
		if (!showLabels)
			return;
		Rectangle p00 = p[nColumns * (nRows - 1)];
		Rectangle p10 = p[0];
		Rectangle p01 = p[nColumns * nRows - 1];
		int w2 = p00.width / 2;
		int h2 = p00.height / 2;
		int xb1 = p00.x + w2;
		int xb2 = p01.x + w2;
		int yb = p00.y + h2 + h2 + 3 * gap;
		int xl = p10.x - 3 * gap;
		int yl1 = p00.y + h2;
		int yl2 = p10.y + h2;
		int fh = (nColumns < 40) ? h2 : h2 + h2;
		g.setFont(new Font("SansSerif", Font.PLAIN, fh));
		g.setColor(Color.black);
		g.drawLine(xb1, yb, xb2, yb);
		g.drawLine(xl, yl1, xl, yl2);
		int x, y;
		y = yb - gap;
		for (int i = 0; i < nColumns; i++) {
			x = p[i].x + w2;
			g.drawLine(x, y, x, yb);
			drawXLabel(g, xLabel(i), x, yb);
		}
		x = xl + gap;
		for (int i = 0; i < nRows; i++) {
			y = p[i * nColumns].y + h2;
			g.drawLine(xl, y, x, y);
			drawYLabel(g, yLabel(i), xl, y);
		}
	}

	protected void drawXLabel(Graphics g, String lb, int x, int y) {
		if (lb != null) {
			FontMetrics fm = g.getFontMetrics();
			int w = fm.stringWidth(lb);
			int h = fm.getHeight();
			g.drawString(lb, x - w / 2, y + h);
		}
	}

	protected void drawYLabel(Graphics g, String lb, int x, int y) {
		if (lb != null) {
			FontMetrics fm = g.getFontMetrics();
			int w = fm.stringWidth(lb);
			int h = fm.getAscent();
			int cw = fm.charWidth('H');
			g.drawString(lb, x - w - cw, y + h / 2);
		}
	}

	protected abstract String xLabel(int i);

	protected abstract String yLabel(int i);

	public String getReflectanceName(int idx) {
		int i = idx - firstIndex;
		return ((i < colorName.length) ? colorName[i] : null);
	}
}
