package de.pxlab.pxl.display;

import de.pxlab.pxl.*;
import java.awt.*;
import java.util.*;
import de.pxlab.pxl.spectra.SpectralDistribution;
import de.pxlab.pxl.spectra.SpectralDistributionFactory;

/**
 * Shows a rectangular pattern of color patches which contains a sample from the
 * Munsell Book of Colors with maximum Chroma and allows for selection of a
 * subset of color patches.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see CIELabColorSampleSelection
 */
/*
 * 
 * 11/03/03
 * 
 * 2006/02/23 adjust Chroma only as long as the respective spectral data are
 * available.
 */
public class MunsellColorSampleSelection extends ColorSampleSelection {
	/** Number of the first Hue section to be shown. */
	public ExPar FirstHueNumber = new ExPar(1, 40, new ExParValue(1),
			"Munsell First Hue Number");
	/** First Value level to be shown. */
	public ExPar FirstValue = new ExPar(1, 9, new ExParValue(1),
			"Munsell First Value Number");
	/**
	 * Color temperature of the CIE daylight illuminator which is used to
	 * simulate the Munsell colors from their reflectance spectra.
	 */
	public ExPar Illuminant = new ExPar(1800.0, 26000.0,
			new ExParValue(6500.0), "Illuminant Color Temperature");
	/**
	 * Luminance of a perfect white reflector illuminated by the simulated
	 * illuminant of the color patches.
	 */
	public ExPar Luminance = new ExPar(0.0, 300.0, new ExParValue(100.0),
			"White Reference Luminance");
	/**
	 * If this flag is not set then colors with the maximum Chroma values in the
	 * actual Munsell Book of Colors which cannot be shown on the current device
	 * are missing. If the flag is set then the Chroma of these colors is
	 * reduced in Chroma steps of the Munsell Book of Colors until a Color is
	 * generated which can be shown on the current color device.
	 */
	public ExPar ModifyChroma = new ExPar(FLAG, new ExParValue(1),
			"Modify Chroma for non-displayable colors");
	// Munsell space is 9 rows and 40 columns
	protected int mRows = 9;
	protected int mColumns = 40;

	public MunsellColorSampleSelection() {
		setTitleAndTopic("Munsell Color Selection", COLOR_SPACES_DSP);
		NumberOfSelectionRows.set(mRows);
		NumberOfSelectionColumns.set(mColumns);
		SelectionPatchWidth.set(12);
		SelectionPatchHeight.set(12);
		SelectionGapSize.set(2);
	}
	private PxlColor[] colorTable;
	private String[] colorName;
	private int[] midx;

	protected void computeGeometry() {
		int n = mRows * mColumns;
		colorTable = new PxlColor[n];
		colorName = new String[n];
		PxlColor bgc = ExPar.ScreenBackgroundColor.getPxlColor();
		for (int i = 0; i < n; i++) {
			colorTable[i] = bgc;
			colorName[i] = "";
		}
		// Get the Hue value and make sure that it is valid: 1, ..., 40 are
		// valid
		int hue = FirstHueNumber.getInt();
		if (hue < 1)
			hue = 1;
		if (hue > 40)
			hue = 40;
		SpectralDistribution light;
		boolean modChr = ModifyChroma.getFlag();
		try {
			// Get the illuminant temperature and spectral distribution
			light = SpectralDistributionFactory.instance("D"
					+ String.valueOf(Illuminant.getInt()));
		} catch (SpectrumNotFoundException snf) {
			new ParameterValueError(snf.getMessage());
			light = new SpectralDistribution(380, 720, 5, 1.0F);
		}
		// Set the illuminant luminance to 100 cd/qm
		light.setLuminance(Luminance.getDouble());
		// Now get the Chroma iterator through the Book of Colors
		Iterator it = MunsellBookOfColors.maximumChromaIterator();
		while (it.hasNext()) {
			// Get the next Munsell color of this page
			MunsellColor mc = (MunsellColor) it.next();
			// Now get its spectral reflectance
			String mcn = mc.toString();
			// System.out.println("mcn =  " + mcn);
			SpectralDistribution r = null;
			try {
				r = SpectralDistributionFactory.instance(mcn);
			} catch (SpectrumNotFoundException snf) {
				new ParameterValueError(snf.getMessage());
				r = new SpectralDistribution(380, 720, 5, 1.0F);
			}
			// Filter the light by the reflectance
			r.filter(light);
			PxlColor c = r.toXYZ();
			if (modChr) {
				boolean canModify = true;
				while (canModify && !c.isDisplayable()) {
					canModify = mc.decrementChroma();
					if (canModify) {
						mcn = mc.toString();
						try {
							r = SpectralDistributionFactory.instance(mcn);
						} catch (SpectrumNotFoundException snf) {
							new ParameterValueError(snf.getMessage());
							r = new SpectralDistribution(380, 720, 5, 1.0F);
						}
						r.filter(light);
						c = r.toXYZ();
					}
				}
			}
			// and set the color table entry
			// Compute the row and column of the patch
			int row = 9 - mc.getValueIndex();
			int col = (mc.getHueIndex() - (hue - 1) + 40) % 40;
			// and its index in the color table
			int idx = row * mColumns + col;
			colorTable[idx] = c;
			colorName[idx] = mcn;
			// System.out.println("Row = " + row + ", Column = " + col + " : " +
			// mcn);
		}
		int nsr = NumberOfSelectionRows.getInt();
		int nsc = NumberOfSelectionColumns.getInt();
		int m = nsr * nsc;
		double[] a = new double[3 * m];
		midx = new int[m];
		int mr = MunsellBookOfColors.findValueIndex(FirstValue.getDouble());
		if (mr < 1)
			mr = 1;
		if (mr > 9)
			mr = 9;
		mr = (mr + nsr - 1);
		if (mr > 9)
			mr = 9;
		int mc;
		int h = 0, idx;
		for (int i = 0; i < nsr; i++) {
			mc = 0;
			for (int j = 0; j < nsc; j++) {
				idx = (9 - mr) * mColumns + mc;
				if (idx < n) {
					a[h + h + h + 0] = colorTable[idx].getY();
					a[h + h + h + 1] = colorTable[idx].getx();
					a[h + h + h + 2] = colorTable[idx].gety();
					midx[h] = idx;
				} else {
					a[h + h + h + 0] = bgc.getY();
					a[h + h + h + 1] = bgc.getx();
					a[h + h + h + 2] = bgc.gety();
				}
				h++;
				mc++;
			}
			mr--;
		}
		ColorSample.set(a);
		super.computeGeometry();
	}

	protected void timingGroupFinished(int group) {
		int[] si = colorSampleView.getSelectionIndex();
		int nsr = NumberOfSelectionRows.getInt();
		int nsc = NumberOfSelectionColumns.getInt();
		StringBuffer b = new StringBuffer(100);
		// System.out.println("MunsellColorSampleSelection.timingGroupFinished():");
		for (int i = 0; i < si.length; i++) {
			int idx = midx[si[i]];
			if (i > 0)
				b.append(", ");
			b.append(colorName[idx]);
			// System.out.println("  " + colorName[idx]);
		}
		Selection.set(b.toString());
		// System.out.println(b);
	}
}
