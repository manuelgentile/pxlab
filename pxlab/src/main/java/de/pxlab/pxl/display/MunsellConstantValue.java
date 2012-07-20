package de.pxlab.pxl.display;

import java.awt.*;
import java.util.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.SpectralDistribution;
import de.pxlab.pxl.spectra.SpectralDistributionFactory;

/**
 * This display shows a constant Value page of the Munsell Book of Colors. The
 * Value level and the illuminant's color temperature are adjustable.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 10/25/00
 */
public class MunsellConstantValue extends MunsellPage {
	public ExPar Value = new ExPar(2.5, 9.0, new ExParValue(5.0),
			"Munsell Value");
	public ExPar Illuminant = new ExPar(1800.0, 26000.0,
			new ExParValue(6500.0), "Illuminant Color Temperature");
	public ExPar ShowLabels = new ExPar(FLAG, new ExParValue(1),
			"Flag to Show Scales and Labels");
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new YxyColor(90.0, 0.321, 0.348)), "Background Color");

	public String getIlluminantName(int i) {
		return ("D" + String.valueOf(Illuminant.getInt()));
	}

	/** Cunstructor creating the title of the display. */
	public MunsellConstantValue() {
		setTitleAndTopic("Munsell Book of Colors: Constant Value Page",
				COLOR_SPACES_DSP | DEMO);
	}

	/** Initialize the display list of the demo. */
	protected void createPage() {
		nRows = 8;
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
		return (String.valueOf(MunsellBookOfColors.getChromaLevel((nRows - 1)
				- i)));
	}

	protected void computeMunsellColors(SpectralDistribution light)
			throws SpectrumNotFoundException {
		showLabels = ShowLabels.getFlag();
		// Get the Value level
		double val = Value.getDouble();
		// Now get the constant Value iterator through the Book of Colors
		Iterator it = MunsellBookOfColors.constantValueIterator(val);
		while (it.hasNext()) {
			// Get the next Munsell color of this page
			MunsellColor mc = (MunsellColor) it.next();
			// Compute the row and column of the patch
			int row = 7 - mc.getChromaIndex() / 2;
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
