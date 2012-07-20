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
public class MunsellConstantChroma extends MunsellPage {
	public ExPar Chroma = new ExPar(1.0, 14.0, new ExParValue(8.0),
			"Munsell Chroma");
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
	public MunsellConstantChroma() {
		setTitleAndTopic("Munsell Book of Colors: Constant Chroma Page",
				COLOR_SPACES_DSP | DEMO);
	}

	/** Initialize the display list of the demo. */
	protected void createPage() {
		nRows = 9;
		nColumns = 40;
		getDisplayElement(backgroundFieldIndex).setColorPar(BackgroundColor);
	}

	protected String xLabel(int i) {
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
		// Get the Chroma value and make sure that it is valid: 1, 2, 4, 6, ...,
		// 14 are valid
		int chroma = Chroma.getInt();
		if (chroma < 2) {
			chroma = 1;
		} else {
			chroma = 2 * ((chroma + 1) / 2);
		}
		// Now get the constant Chroma iterator through the Book of Colors
		Iterator it = MunsellBookOfColors
				.constantChromaIterator((double) chroma);
		while (it.hasNext()) {
			// Get the next Munsell color of this page
			MunsellColor mc = (MunsellColor) it.next();
			// Compute the row and column of the patch
			int row = 9 - mc.getValueIndex();
			int col = mc.getHueIndex();
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
		}
	}
}
