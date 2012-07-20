package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.*;

/**
 * A board of Munsell color patches with two illuminants. One of the illuminants
 * applies to most of the board including the background while the other is
 * restricted to a target patch. Thus the target patch may be modified
 * independently of the other patches. Colors are defined by their Munsell names
 * and simulate the respective reflectance functions under the given
 * illuminants.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * 12/02/02
 * 
 * 2004/10/12 Bugfix
 */
public class MunsellColorBoard extends SpectralColorDisplay {
	/**
	 * This illuminant covers the whole board and all patches except the target
	 * patch. It is a standard CIE D illuminant and the parameter gives its
	 * color temperature.
	 */
	public ExPar AmbientIlluminant = new ExPar(1800.0, 26000.0, new ExParValue(
			6500.0), "Ambient Illuminant Color Temperature");
	/**
	 * Luminance of the Ambient Illuminant. This corresponds to the luminance of
	 * a perfect (100 %) reflector illuminated by the respective illuminant.
	 */
	public ExPar AmbientIlluminantLuminance = new ExPar(0.0, 2000.0,
			new ExParValue(40.0), "Luminance of the Ambient Illuminant");
	/**
	 * This illuminant covers only the target patch. It is a standard CIE D
	 * illuminant and the parameter gives its color temperature.
	 */
	public ExPar TargetIlluminant = new ExPar(1800.0, 26000.0, new ExParValue(
			6500.0), "Target Illuminant Color Temperature");
	/**
	 * Luminance of the Target Illuminant. This corresponds to the luminance of
	 * a perfect (100 %) reflector illuminated by the respective illuminant.
	 */
	public ExPar TargetIlluminantLuminance = new ExPar(0.0, 2000.0,
			new ExParValue(40.0), "Luminance of the Target Illuminant");
	/** Index of the target patch. The top left patch has index 0. */
	public ExPar Target = new ExPar(SMALL_INT, new ExParValue(9),
			"Index of the Target Patch");
	/** Number of patch columns. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(5),
			"Number of Patches int a Row");
	/** Color patch width. */
	public ExPar PatchWidth = new ExPar(HORSCREENSIZE, new ExParValue(64),
			"Color Patch Width");
	/** Color patch height. */
	public ExPar PatchHeight = new ExPar(VERSCREENSIZE, new ExParValue(100),
			"Color Patch Height");
	/** Horizontal gap between patches. */
	public ExPar HorizontalGap = new ExPar(HORSCREENSIZE, new ExParValue(40),
			"Horizontal Gap Size");
	/** Vertical gap between patches. */
	public ExPar VerticalGap = new ExPar(VERSCREENSIZE, new ExParValue(40),
			"Vertical Gap Size");
	/**
	 * This array initializes the color board with proper Munsell color names.
	 * Note that the first entry is the name of the background color. These are
	 * approximately those values used by Speigle & Brainard.
	 */
	private static final String[] colorName = {
			"N 7.5/", // background
			"5G 6/6", "5R 4/4", "N 9.5/", "5R 6/6", "5G 6/6", "2.5YR 6/4",
			"5PB 5/6", "5Y 6/6", "5B 4/4", "N 3/", "N 3.5/", "2.5YR 4/4",
			"5Y 4/4", "N 5/", "5R 4/4" };
	/**
	 * Contains the Munsell color names of all patches in the display including
	 * the background which must be the first entry.
	 */
	public ExPar MunsellColors = new ExPar(STRING, new ExParValue(colorName),
			"Munsell Color Names");
	/** Contains the resulting coordinates of the target patch's color. */
	public ExPar TargetColor = new ExPar(DEPCOLOR, new ExParValue(new PxlColor(
			1.0)), "Resulting Target Color");

	public MunsellColorBoard() {
		setTitleAndTopic("Munsell Color Board", COLOR_SPACES_DSP);
	}
	// public boolean canShowSpectralDistributions() {return(true);}
	// Index of the display element which contains the top left patch
	private int firstIndex;
	// private int nRows;
	// private int nColumns;
	private int nPatches = 0;
	private int nPatches1 = 0;
	private Bar[] patch;
	private ExPar[] colorTable;

	/** Initialize the display list of the demo. */
	protected int create() {
		// System.out.println("MunsellColorBoard.create()");
		firstIndex = nextDisplayElementIndex();
		nPatches = 0;
		recreatePatches();
		defaultTiming(0);
		return (firstIndex);
	}

	protected void computeColors() {
		// System.out.println("MunsellColorBoard.computeColors()");
		initMunsellColors();
		// Don't forget the background color
	}

	protected void computeGeometry() {
		// System.out.println("MunsellColorBoard.computeGeometry()");
		// Create the display element list
		recreatePatches();
		// Colors are computed here since they depend on adjustable parameters
		initMunsellColors();
		int nc = NumberOfColumns.getInt();
		int nr = (nPatches + nc - 1) / nc;
		int w = PatchWidth.getInt();
		int h = PatchHeight.getInt();
		int gw = HorizontalGap.getInt();
		int gh = VerticalGap.getInt();
		int left_x = -(nc * w + (nc - 1) * gw) / 2;
		int top_y = -(nr * h + (nr - 1) * gh) / 2;
		int y = top_y;
		int x = left_x;
		int i = 0;
		for (int k = 0; k < nPatches; k++) {
			patch[k].setRect(x, y, w, h);
			x += (w + gw);
			i++;
			if (i >= nc) {
				x = left_x;
				y += (h + gh);
				i = 0;
			}
		}
	}

	/**
	 * Get the name of the illuminant spectral distribution function for the
	 * display element at the given index.
	 * 
	 * @return the name of the illuminant spectrum or null if unknown.
	 */
	public String getIlluminantName(int i) {
		// All patches but the target have the same Illuminant
		int t = Target.getInt() + firstIndex;
		return ("D" + String.valueOf(((i == t) ? TargetIlluminant
				: AmbientIlluminant).getInt()));
	}

	/**
	 * Get the name of the reflectance function of the display element at the
	 * given index.
	 * 
	 * @return the name of the reflectance spectrum or null if unknown.
	 */
	public String getReflectanceName(int idx) {
		String[] m = MunsellColors.getStringArray();
		return ((idx < m.length) ? m[idx] : null);
	}

	private void recreatePatches() {
		// Figure out the number of color patches
		int n = MunsellColors.getValue().length - 1;
		// System.out.println("MunsellColorBoard.recreatePatches(): Creating " +
		// n + " color patches");
		// Recreate patches if necessary
		if ((n > 0) && (n != nPatches)) {
			removeDisplayElements(firstIndex);
			nPatches = n;
			nPatches1 = n + 1;
			patch = new Bar[nPatches];
			colorTable = new ExPar[nPatches1];
			// First set the background color
			colorTable[0] = new ExPar(DEPCOLOR, new ExParValue(new PxlColor()),
					null);
			getDisplayElement(backgroundFieldIndex).setColorPar(colorTable[0]);
			for (int i = 1; i < nPatches1; i++) {
				colorTable[i] = new ExPar(DEPCOLOR, new ExParValue(
						new PxlColor()), null);
				patch[i - 1] = new Bar(colorTable[i]);
				enterDisplayElement(patch[i - 1], group[0]);
			}
		}
	}

	protected void initMunsellColors() {
		// Display element index of the target is equal to the target
		// color index int the color table
		int t = Target.getInt() + firstIndex;
		try {
			// Get the illuminant spectral distribution
			SpectralDistribution ambLight = SpectralDistributionFactory
					.instance(getIlluminantName(0));
			SpectralDistribution targLight = SpectralDistributionFactory
					.instance(getIlluminantName(t));
			// Set the illuminant luminance to 100 cd/qm
			ambLight.setLuminance(AmbientIlluminantLuminance.getDouble());
			targLight.setLuminance(TargetIlluminantLuminance.getDouble());
			String[] m = MunsellColors.getStringArray();
			for (int i = 0; i < nPatches1; i++) {
				// System.out.println("MunsellColorBoard.initMunsellColors(): "
				// + colorName[i]);
				SpectralDistribution r = SpectralDistributionFactory
						.instance(m[i]);
				if (r == null)
					r = SpectralDistributionFactory.instance("N 3/");
				// Filter the light by the reflectance
				r.filter((i == t) ? targLight : ambLight);
				// and set the color table entry
				PxlColor c = r.toXYZ();
				// System.out.println(i + " " + nc + " " + c.getY() + " " +
				// c.getx() + " " + c.gety());
				colorTable[i].set(c);
				if (i == t) {
					// System.out.println("target = " + c);
					TargetColor.set(c);
				}
			}
		} catch (SpectrumNotFoundException snf) {
			new ParameterValueError(snf.getMessage());
			for (int i = 0; i < nPatches1; i++) {
				colorTable[i].set(PxlColor.systemColor(PxlColor.BLACK));
			}
		}
	}
}
