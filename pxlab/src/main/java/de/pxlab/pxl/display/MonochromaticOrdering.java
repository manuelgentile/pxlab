package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.*;

/*  @author H. Irtel
 @version 0.2.1
 */
/*
 06/15/00 

 06/0801  updated to new color schema
 */
public class MonochromaticOrdering extends SpectralColorDisplay {
	public ExPar LowTempIlluminant = new ExPar(1800.0, 26000.0, new ExParValue(
			5000.0), "Illuminant with Low Color Temperature");
	public ExPar HighTempIlluminant = new ExPar(1800.0, 26000.0,
			new ExParValue(5000.0), "Illuminant with High Color Temperature");
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(90.0, 0.321, 0.348)), "Background Color");
	private static final String[] colorName = {
			"N 3/", // background
			"5P 6/8", "5RP 6/10", "5R 6/14", "5YR 6/12", "5Y 6/8", "5GY 6/8",
			"5G 6/10", "5BG 6/8", "5B 6/8", "5PB 6/10", };
	private ExPar[] colorTable;

	public MonochromaticOrdering() {
		setTitleAndTopic("Monochromatic Ordering", COLOR_SPACES_DSP | DEMO);
	}

	public boolean canShowSpectralDistributions() {
		return (true);
	}
	private int background, firstIndex;
	private int nRows;
	private int nColumns;
	private int n;
	private int n1;
	private double gap = 0.0; // 0.16;
	private Rectangle[] p;

	/** Initialize the display list of the demo. */
	protected int create() {
		getDisplayElement(backgroundFieldIndex).setColorPar(BackgroundColor);
		nRows = 4;
		nColumns = 10;
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
		if (i <= (2 * nColumns))
			return ("D" + String.valueOf(LowTempIlluminant.getInt()));
		else
			return ("D" + String.valueOf(HighTempIlluminant.getInt()));
	}

	public String getReflectanceName(int i) {
		int cni = (i <= nColumns) ? i : (((i - nColumns - 1) % nColumns) + 1);
		return (colorName[cni]);
	}

	protected void initMunsellColors() {
		try {
			// Get the illuminant spectral distribution
			SpectralDistribution light1 = SpectralDistributionFactory
					.instance("D" + String.valueOf(LowTempIlluminant.getInt()));
			SpectralDistribution light2 = SpectralDistributionFactory
					.instance("D" + String.valueOf(HighTempIlluminant.getInt()));
			// Set the illuminant luminance to 100 cd/qm
			light1.setLuminance(100.0);
			light2.setLuminance(100.0);
			for (int i = 0; i < n1; i++) {
				int cni = (i <= nColumns) ? i
						: (((i - nColumns - 1) % nColumns) + 1);
				// System.out.println("MacbethColorChecker.initMunsellColors(): "
				// + colorName[i]);
				SpectralDistribution r = SpectralDistributionFactory
						.instance(colorName[cni]);
				// Filter the light by the reflectance
				r.filter((i <= (2 * nColumns)) ? light1 : light2);
				// and set the color table entry
				PxlColor c = r.toXYZ();
				// System.out.println(i + " " + nc + " " + c.getY() + " " +
				// c.getx() + " " + c.gety());
				if ((i > nColumns) && (i <= (3 * nColumns))) {
					colorTable[i].set(new YxyColor(c.getY(), 0.313, 0.329));
				} else {
					colorTable[i].set(c);
				}
			}
		} catch (SpectrumNotFoundException snf) {
			new ParameterValueError(snf.getMessage());
			for (int i = 0; i < n1; i++) {
				colorTable[i].set(PxlColor.systemColor(PxlColor.BLACK));
			}
		}
	}
}
