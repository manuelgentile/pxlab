package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.*;

/**
 * Macbeth Color Checker Colors.
 * 
 * <P>
 * Shows 24 test colors which are widely used for checking reproduction quality
 * of photographic film.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 06/15/00
 * 
 * 06/0801 updated to new color schema
 */
public class MacbethColorChecker extends SpectralColorDisplay {
	public ExPar Illuminant = new ExPar(1800.0, 26000.0,
			new ExParValue(6500.0), "Illuminant Color Temperature");
	public ExPar NumberOfComponents = new ExPar(SMALL_INT, new ExParValue(12,
			1, 12), "Number of Basic Components for Reflectance Simulation");
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(90.0, 0.321, 0.348)), "Background Color");
	private static final String[] colorName = {
			"N 3/", // background
			"3YR 3.7/3.2", "2.2YR 6.47/4.1", "4.3PB 4.95/5.5", "6.7GY 4.2/4.1",
			"9.7PB 5.47/6.7", "2.5BG 7.0/6", "5YR 6.0/11", "7.5PB 4.0/10.7",
			"2.5R 5.0/10", "5P 3.0/7", "5GY 7.1/9.1", "10YR 7.0/10.5",
			"7.5PB 2.9/12.7", "0.25G 5.4/8.65", "5R 4.0/12", "5Y 8.0/11.1",
			"2.5RP 5.0/12", "5B 5.0/8", "N 9.5/", "N 8.0/", "N 6.5/", "N 5.0/",
			"N 3.5/", "N 2.0/" };
	private ExPar[] colorTable;

	public MacbethColorChecker() {
		setTitleAndTopic("Macbeth ColorChecker", COLOR_SPACES_DSP | DEMO);
	}

	public boolean canShowSpectralDistributions() {
		return (true);
	}
	private int background, firstIndex;
	private int nRows;
	private int nColumns;
	private int n;
	private int n1;
	private double gap = 0.16;
	private Rectangle[] p;

	/** Initialize the display list of the demo. */
	protected int create() {
		getDisplayElement(backgroundFieldIndex).setColorPar(BackgroundColor);
		nRows = 4;
		nColumns = 6;
		n = nRows * nColumns;
		n1 = n + 1;
		colorTable = new ExPar[n1];
		for (int i = 0; i < n1; i++)
			colorTable[i] = new ExPar(DEPCOLOR, new ExParValue(new PxlColor()),
					null);
		background = enterDisplayElement(new Bar(colorTable[0]));
		firstIndex = nextDisplayElementIndex();
		for (int i = 1; i < n1; i++)
			enterDisplayElement(new Bar(colorTable[i]));
		return (firstIndex);
	}

	protected void computeColors() {
		initMunsellColors();
	}

	protected void computeGeometry() {
		// Color are computed here since they depend on adjustable parameters
		initMunsellColors();
		Rectangle rr = new Rectangle(-width / 2, -height / 2, width, height);
		Rectangle r = innerRect(rr, 44.0 / 50.0);
		p = rectPattern(r, nRows, nColumns, gap, gap, 1.0);
		int hgap = p[1].x - (p[0].x + p[0].width);
		int vgap = p[nColumns].y - (p[0].y + p[0].height);
		getDisplayElement(background).setRect(p[0].x - hgap, p[0].y - vgap,
				nColumns * p[0].width + (nColumns + 1) * hgap,
				nRows * p[0].height + (nRows + 1) * vgap);
		for (int i = 0; i < n; i++)
			getDisplayElement(firstIndex + i).setRect(p[i].x, p[i].y,
					p[i].width, p[i].height);
	}

	public String getIlluminantName(int i) {
		return ("D" + String.valueOf(Illuminant.getInt()));
	}

	public String getReflectanceName(int idx) {
		int i = idx - background;
		return ((i < colorName.length) ? colorName[i] : null);
	}

	protected void initMunsellColors() {
		int nc = NumberOfComponents.getInt();
		try {
			// Get the illuminant spectral distribution
			SpectralDistribution light = SpectralDistributionFactory
					.instance(getIlluminantName(0));
			// Set the illuminant luminance to 100 cd/qm
			light.setLuminance(100.0);
			for (int i = 0; i < n1; i++) {
				// System.out.println("MacbethColorChecker.initMunsellColors(): "
				// + colorName[i]);
				SpectralDistribution r = SpectralDistributionFactory
						.reducedMunsellReflectance(colorName[i], nc);
				if (r == null)
					r = SpectralDistributionFactory.instance(colorName[i]);
				// Filter the light by the reflectance
				r.filter(light);
				// and set the color table entry
				PxlColor c = r.toXYZ();
				// System.out.println(i + " " + nc + " " + c.getY() + " " +
				// c.getx() + " " + c.gety());
				colorTable[i].set(c);
			}
		} catch (SpectrumNotFoundException snf) {
			new ParameterValueError(snf.getMessage());
			for (int i = 0; i < n1; i++) {
				colorTable[i].set(PxlColor.systemColor(PxlColor.BLACK));
			}
		}
	}
}
