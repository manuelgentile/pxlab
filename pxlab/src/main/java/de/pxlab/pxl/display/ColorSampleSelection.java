package de.pxlab.pxl.display;

import java.awt.event.KeyEvent;
import de.pxlab.pxl.*;

/**
 * Shows a rectangular color sample and allows for selection of a subset of
 * color patches. Selected colors are indicated by their circular form. Also may
 * contain a separate circular patch which always shows the selected color if
 * the SingleSelection flag is set.
 * 
 * <p>
 * This display object's timer should always have the
 * de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER_BIT set in order to track mouse
 * buttons for selection.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * @see CIELabPlane
 */
/*
 * 
 * 11/03/03
 * 
 * 2006/01/16 allow timer stops only if a color is selected.
 */
public class ColorSampleSelection extends Display {
	private double[] sample = { 10.71, 0.408, 0.506, 10.71, 0.469, 0.457,
			8.715, 0.576, 0.38, 8, 0.63, 0.329, 7.715, 0.579, 0.296, 7.715,
			0.513, 0.273, 9.415, 0.287, 0.59, 8.839, 0.583, 0.336, 10.71,
			0.235, 0.387, 6.692, 0.256, 0.224, 6.494, 0.406, 0.415, 28.8,
			0.464, 0.464, 6.692, 0.59, 0.324, 2.187, 0.301, 0.17, 9.781, 0.219,
			0.301, 9.781, 0.476, 0.406, 9.781, 0.24, 0.415, 6.463, 0.541,
			0.275, 10.00, 0.4, 0.4, 10.00, 0.4, 0.4 };
	/*
	 * 29.67, 0.381, 0.340, 29, 0.386, 0.403, 30.19, 0.371, 0.471, 29.15, 0.289,
	 * 0.416, 31.13, 0.26, 0.321, 29.9, 0.243, 0.258, 29.95, 0.291, 0.259,
	 * 31.44, 0.333, 0.276, 11.18, 0.567, 0.312, 58.78, 0.434, 0.471, 19.83,
	 * 0.253, 0.422, 19.83, 0.353, 0.422, 57.5, 0.373, 0.363, 11.76, 0.356,
	 * 0.448, 19.58, 0.310, 0.328, 75.78, 0.313, 0.327};
	 */
	/**
	 * Contains all color coordinates of the color sample to be shown.
	 */
	public ExPar ColorSample = new ExPar(UNKNOWN, new ExParValue(sample),
			"Color Sample");
	/** Dummy color parameter. */
	public ExPar UnusedColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Unused Color");
	/** Horizontal center position of the selection set. */
	public ExPar SelectionLocationX = new ExPar(HORSCREENPOS,
			new ExParValue(0), "Selection Set Horizontal Center Position");
	/** Vertical center position of the selection set. */
	public ExPar SelectionLocationY = new ExPar(VERSCREENPOS,
			new ExParValue(0), "Selection Set Vertical Center Position");
	/** Number of patch columns. */
	public ExPar NumberOfSelectionColumns = new ExPar(SMALL_INT,
			new ExParValue(4), "Number of Selection Patches in a Row");
	/** Number of patch rows. */
	public ExPar NumberOfSelectionRows = new ExPar(SMALL_INT,
			new ExParValue(4), "Number of Selection Patches in a Column");
	/** Color patch width. */
	public ExPar SelectionPatchWidth = new ExPar(SCREENSIZE,
			new ExParValue(64), "Color Selection Patch Width");
	/** Color patch height. */
	public ExPar SelectionPatchHeight = new ExPar(SCREENSIZE,
			new ExParValue(64), "Color Selection Patch Height");
	/** Gap between patches. */
	public ExPar SelectionGapSize = new ExPar(SCREENSIZE, new ExParValue(12),
			"Selection Set Gap Size");
	/** If true then only a single object may be selected. */
	public ExPar SingleSelection = new ExPar(FLAG, new ExParValue(0),
			"Single Selection Flag");
	/**
	 * Stores the set of selected color values by their coordinates.
	 */
	public ExPar Selection = new ExPar(RTDATA, new ExParValue(""),
			"Set of Selected Colors");
	/**
	 * Stores the set of selected colors by their index in the display pattern.
	 */
	public ExPar SelectionIndex = new ExPar(RTDATA, new ExParValue(""),
			"Set of Selected Colors by Index");
	/** Stores the binary pattern of selected colors. */
	public ExPar SelectionBinPattern = new ExPar(RTDATA, new ExParValue(""),
			"Binary selection pattern");
	/** Selection Color Field Color. */
	public ExPar DiskColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Selection Color Disk");
	public ExPar DiskSize = new ExPar(SCREENSIZE, new ExParValue(0),
			"Selection Color Disk Size");
	public ExPar DiskLocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Selection Color Disk Position");
	public ExPar DiskLocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Selection Color Disk Position");

	public ColorSampleSelection() {
		setTitleAndTopic("Color Sample Selection", COLOR_SPACES_DSP);
	}
	protected ColorSampleView colorSampleView;
	protected int disk;

	/** Initialize the display list of the demo. */
	protected int create() {
		colorSampleView = new ColorSampleView(UnusedColor);
		colorSampleView.setColorSamplePar(ColorSample);
		disk = enterDisplayElement(new Oval(DiskColor), group[0]);
		int i = enterDisplayElement(colorSampleView, group[0]);
		defaultTiming(0);
		return (disk);
	}

	protected void computeColors() {
		setDiskColor();
	}

	protected void computeGeometry() {
		int s = DiskSize.getInt();
		getDisplayElement(disk).setRect(DiskLocationX.getInt() - s / 2,
				DiskLocationY.getInt() - s / 2, s, s);
		colorSampleView.setProperties(SelectionLocationX.getInt(),
				SelectionLocationY.getInt(), NumberOfSelectionRows.getInt(),
				NumberOfSelectionColumns.getInt(),
				SelectionPatchWidth.getInt(), SelectionPatchHeight.getInt(),
				SelectionGapSize.getInt(), SingleSelection.getFlag());
	}

	/**
	 * This method is called whenever the pointer button has been pressed. This
	 * method is activated by setting the
	 * de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER_BIT flag of this object's
	 * timer.
	 */
	protected boolean pointerActivated() {
		colorSampleView.select(pointerActivationX, pointerActivationY);
		setDiskColor();
		return true;
	}

	protected void setDiskColor() {
		PxlColor c = colorSampleView.getSelectedColor();
		if (c != null) {
			DiskColor.set(c);
			// System.out.println("ColorSampleSelection.setDiskColor() Disk = "
			// + c);
		} else {
			DiskColor.set(ExPar.ScreenBackgroundColor.getPxlColor());
			// System.out.println("ColorSampleSelection.setDiskColor() Disk cleared.");
		}
	}

	/**
	 * Alllow timer stops only if a color is currently selected.
	 * 
	 * @return true if currently at least one color is being selected.
	 */
	public boolean getAllowTimerStop(int rc) {
		return colorSampleView.getSelectedColor() != null;
	}

	/**
	 * This method is called when the display of the currently active timing
	 * group has been finished.
	 * 
	 * @param group
	 *            the index of the timing group which has been run.
	 */
	protected void timingGroupFinished(int group) {
		if (SingleSelection.getFlag()) {
			double[] a = colorSampleView.getSingleSelection();
			if (a != null) {
				Selection.set(a);
			}
		} else
			Selection.set(colorSampleView.getSelection());
		SelectionIndex.set(colorSampleView.getSelectionIndex());
		// SelectionBinPattern.set(colorSampleView.getSelectionBinPattern());
		SelectionBinPattern.getValue().set(
				colorSampleView.getSelectionBinPattern());
		SelectionBinPattern.getValue()
				.setTypeConjecture(ExParValue.TYPE_STRING);
		// System.out.println(colorSampleView.getSelection());
	}
}
