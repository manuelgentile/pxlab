package de.pxlab.pxl.display;

import java.awt.*;
import java.util.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.SpectralDistribution;
import de.pxlab.pxl.spectra.SpectralDistributionFactory;

/**
 * This display shows a constant Chroma page of the Munsell Book of Colors. The
 * Chroma value and the illuminant's color temperature are adjustable.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 10/25/00
 */
public class MunsellMaximumChroma extends MunsellPage {
	/** Number of the Hue which is shown in the leftmost column. */
	public ExPar HueNumber = new ExPar(1.0, 40.0, new ExParValue(1.0),
			"Munsell First Hue Index");
	public ExPar Illuminant = new ExPar(1800.0, 26000.0,
			new ExParValue(6500.0), "Illuminant Color Temperature");
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(90.0, 0.321, 0.348)), "Background Color");
	public ExPar ShowLabels = new ExPar(FLAG, new ExParValue(1),
			"Flag to Show Scales and Labels");

	public String getIlluminantName(int i) {
		return ("D" + String.valueOf(Illuminant.getInt()));
	}

	/** Cunstructor creating the title of the display. */
	public MunsellMaximumChroma() {
		setTitleAndTopic("Munsell Book of Colors: Maximum Chroma Page",
				COLOR_SPACES_DSP | DEMO);
	}

	/** Initialize the display list of the demo. */
	protected void createPage() {
		nRows = 9;
		nColumns = 40;
		getDisplayElement(backgroundFieldIndex).setColorPar(BackgroundColor);
	}

	/** Get the label for the given column. */
	protected String xLabel(int i) {
		i = (i + HueNumber.getInt() - 1) % 40;
		String r = null;
		if ((i % 4 == 1))
			r = "5" + MunsellBookOfColors.getBasicHueName(i / 4);
		return (r);
	}

	protected String yLabel(int i) {
		return (MunsellBookOfColors.getValueName(8 - i + 1) + "/");
	}

	protected void computeMunsellColors(SpectralDistribution light)
			throws SpectrumNotFoundException {
		showLabels = ShowLabels.getFlag();
		// Get the Hue value and make sure that it is valid: 1, ..., 40 are
		// valid
		int hue = HueNumber.getInt();
		if (hue < 1)
			hue = 1;
		if (hue > 40)
			hue = 40;
		// Now get the constant Chroma iterator through the Book of Colors
		Iterator it = MunsellBookOfColors.maximumChromaIterator();
		while (it.hasNext()) {
			// Get the next Munsell color of this page
			MunsellColor mc = (MunsellColor) it.next();
			// Compute the row and column of the patch
			int row = 9 - mc.getValueIndex();
			int col = (mc.getHueIndex() - (hue - 1) + 40) % 40;
			// and its index in the color table
			int idx = row * nColumns + col;
			// Now get its spectral reflectance
			String mcn = mc.toString();
			SpectralDistribution r = SpectralDistributionFactory.instance(mcn);
			// Filter the light by the reflectance
			r.filter(light);
			// and set the color table entry
			colorTable[idx].set(r.toXYZ());
			colorName[idx] = mcn;
			// System.out.println("Row = " + row + ", Column = " + col + " : " +
			// mcn);
		}
	}
}
