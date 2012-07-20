package de.pxlab.pxl.display;

import java.awt.*;
import java.util.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.SpectralDistribution;
import de.pxlab.pxl.spectra.SpectralDistributionFactory;

/**
 * This display shows a constant Hue page of the Munsell Book of Colors. The Hue
 * value and the illuminant's color temperature are adjustable.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 10/25/00
 */
public class MunsellConstantHue extends MunsellPage {
	public ExPar HueIndex = new ExPar(1.0, 40.0, new ExParValue(1.0),
			"Munsell Hue Index");
	public ExPar Illuminant = new ExPar(1800.0, 26000.0,
			new ExParValue(6500.0), "Illuminant Color Temperature");
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(90.0, 0.313, 0.329)), "Background Color");
	public ExPar ShowLabels = new ExPar(FLAG, new ExParValue(1),
			"Flag to Show Scales and Labels");

	public String getIlluminantName(int i) {
		return ("D" + String.valueOf(Illuminant.getInt()));
	}

	/** Cunstructor creating the title of the display. */
	public MunsellConstantHue() {
		setTitleAndTopic("Munsell Book of Colors: Constant Hue Page",
				COLOR_SPACES_DSP | DEMO);
	}

	/** Initialize the display list of the demo. */
	protected void createPage() {
		nRows = 9;
		nColumns = 8;
		getDisplayElement(backgroundFieldIndex).setColorPar(BackgroundColor);
	}

	protected String xLabel(int i) {
		return (String.valueOf(MunsellBookOfColors.getChromaLevel(i)));
	}

	protected String yLabel(int i) {
		return (MunsellBookOfColors.getValueName(8 - i + 1) + "/");
	}

	protected void computeMunsellColors(SpectralDistribution light)
			throws SpectrumNotFoundException {
		showLabels = ShowLabels.getFlag();
		// Get the Hue value and make sure that it is valid: 1, ..., 40 are
		// valid
		int hue = HueIndex.getInt();
		if (hue < 1)
			hue = 1;
		if (hue > 40)
			hue = 40;
		// Now get the constant Hue iterator through the Book of Colors
		Iterator it = MunsellBookOfColors.constantHueIterator(hue);
		while (it.hasNext()) {
			// Get the next Munsell color of this page
			MunsellColor mc = (MunsellColor) it.next();
			// Compute the row and column of the patch
			int row = 9 - mc.getValueIndex();
			int col = mc.getChromaIndex() / 2;
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
