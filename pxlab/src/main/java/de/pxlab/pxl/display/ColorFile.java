package de.pxlab.pxl.display;

import java.io.*;
import java.awt.*;
import de.pxlab.pxl.*;
import java.util.*;

/**
 * Shows the content of a file containing Yxy-coordinates, one color per line.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2004/10/12
 */
public class ColorFile extends Display {
	/** Color file name. */
	public ExPar FileName = new ExPar(STRING, new ExParValue(
			"fi-pb-histogram-uniq-xy.dat"), "Color File Name");
	/**
	 * Color file directory path name.
	 * <p>
	 * Note that the PXLab design file grammar uses the '\'-character in a
	 * string as an escape sequence. This means that strings like 'c:\Images'
	 * are NOT allowed and MUST be written as 'c:/Images'.
	 */
	public ExPar Directory = new ExPar(STRING, new ExParValue(
			"c:/Land/Histogramme"), "Directory Path");
	/** Number of patch columns. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(5),
			"Number of Patches int a Row");
	/** Color patch width. */
	public ExPar PatchWidth = new ExPar(HORSCREENSIZE, new ExParValue(80),
			"Color Patch Width");
	/** Color patch height. */
	public ExPar PatchHeight = new ExPar(VERSCREENSIZE, new ExParValue(80),
			"Color Patch Height");
	/** Horizontal gap between patches. */
	public ExPar HorizontalGap = new ExPar(HORSCREENSIZE, new ExParValue(20),
			"Horizontal Gap Size");
	/** Vertical gap between patches. */
	public ExPar VerticalGap = new ExPar(VERSCREENSIZE, new ExParValue(20),
			"Vertical Gap Size");

	public ColorFile() {
		setTitleAndTopic("Show Color File", COLOR_SPACES_DSP | DEMO);
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
		firstIndex = nextDisplayElementIndex();
		recreatePatches();
		// defaultTiming(0);
		return (firstIndex);
	}

	protected void computeGeometry() {
		// System.out.println("MunsellColorBoard.computeGeometry()");
		// Create the display element list
		recreatePatches();
		// Colors are computed here since they depend on adjustable parameters
		// initMunsellColors();
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

	private void recreatePatches() {
		String[] colors = FileBase.loadStrings(Directory.getString(),
				FileName.getString());
		if (colors != null) {
			int n = colors.length;
			if (n > 0) {
				removeDisplayElements(firstIndex);
				nPatches = n;
				patch = new Bar[nPatches];
				colorTable = new ExPar[nPatches];
				for (int i = 0; i < nPatches; i++) {
					colorTable[i] = new ExPar(DEPCOLOR, new ExParValue(
							new YxyColor(colors[i])), null);
					patch[i] = new Bar(colorTable[i]);
					enterDisplayElement(patch[i], group[0]);
				}
			}
		}
		if (nPatches == 0) {
			removeDisplayElements(firstIndex);
			nPatches = 1;
			patch = new Bar[nPatches];
			colorTable = new ExPar[nPatches];
			for (int i = 0; i < nPatches; i++) {
				colorTable[i] = new ExPar(DEPCOLOR, new ExParValue(
						new ExParExpression(ExParExpression.WHITE)), null);
				patch[i] = new Bar(colorTable[i]);
				enterDisplayElement(patch[i], group[0]);
			}
		}
	}
}
