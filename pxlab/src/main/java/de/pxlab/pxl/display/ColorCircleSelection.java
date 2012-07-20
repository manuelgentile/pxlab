package de.pxlab.pxl.display;

import java.awt.event.KeyEvent;
import de.pxlab.pxl.*;

/**
 * Shows a circular color sample and allows for selection of a subset of color
 * patches.
 * 
 * @version 0.1.1
 * @see CIELabPlane
 */
/*
 * 
 * 11/03/03
 */
public class ColorCircleSelection extends Display {
	private double[] sample = { 70.0, 40.0, 00.0, 70.0, 40.0, 30.0, 70.0, 40.0,
			60.0, 70.0, 40.0, 90.0, 70.0, 40.0, 120.0, 70.0, 40.0, 150.0, 70.0,
			40.0, 180.0, 70.0, 40.0, 210.0, 70.0, 40.0, 240.0, 70.0, 40.0,
			270.0, 70.0, 40.0, 300.0, 70.0, 40.0, 330.0 };
	/**
	 * Contains all color coordinates of the color sample to be shown.
	 */
	public ExPar ColorSample = new ExPar(UNKNOWN, new ExParValue(sample),
			"Color Sample");
	/** The color space code of the coordinates of the color sample. */
	public ExPar ColorSampleSpace = new ExPar(UNKNOWN, new ExParValue(
			PxlColor.CS_LabLChStar), "Color Sample Space");
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
	public ExPar NumberOfSelectionPatches = new ExPar(SMALL_INT,
			new ExParValue(12), "Number of Selection Patches");
	/** Color patch width. */
	public ExPar SelectionPatchSize = new ExPar(SCREENSIZE, new ExParValue(50),
			"Color Selection Patch Size");
	/** Color patch height. */
	public ExPar SelectionCircleSize = new ExPar(SCREENSIZE,
			new ExParValue(400), "Color Selection Circle Size");
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

	public ColorCircleSelection() {
		setTitleAndTopic("Color Circle Selection", COLOR_SPACES_DSP);
	}
	protected ColorCircleView colorCircleView;
	protected int disk;
	protected int nPatches;

	/** Initialize the display list of the demo. */
	protected int create() {
		colorCircleView = new ColorCircleView(UnusedColor);
		colorCircleView.setColorSamplePar(ColorSample);
		colorCircleView.setColorSpace(ColorSampleSpace.getInt());
		disk = enterDisplayElement(new Oval(DiskColor), group[0]);
		int i = enterDisplayElement(colorCircleView, group[0]);
		defaultTiming(0);
		return (disk);
	}

	protected void computeColors() {
		PxlColor c = colorCircleView.getSelectedColor();
		if (c != null)
			DiskColor.set(c);
		else
			DiskColor.set(ExPar.ScreenBackgroundColor.getPxlColor());
	}

	protected void computeGeometry() {
		int s = DiskSize.getInt();
		getDisplayElement(disk).setRect(DiskLocationX.getInt() - s / 2,
				DiskLocationY.getInt() - s / 2, s, s);
		colorCircleView.setProperties(SelectionLocationX.getInt(),
				SelectionLocationY.getInt(),
				nPatches = NumberOfSelectionPatches.getInt(),
				SelectionCircleSize.getInt(), SelectionPatchSize.getInt(), 90,
				SingleSelection.getFlag());
	}

	/**
	 * This method is called whenever the pointer button has been pressed. This
	 * method is activated by setting the
	 * de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER_BIT flag of this object's
	 * timer.
	 */
	protected boolean pointerActivated() {
		colorCircleView.select(pointerActivationX, pointerActivationY);
		PxlColor c = colorCircleView.getSelectedColor();
		if (c != null)
			DiskColor.set(c);
		else
			DiskColor.set(ExPar.ScreenBackgroundColor.getPxlColor());
		return true;
	}

	/**
	 * This method is here to be overridden by display objects which want to
	 * respond to single key strokes which do NOT stop the display timer. The
	 * display object may change its appearance and the currently actice timing
	 * group will be redrawn afterwards.
	 */
	protected boolean keyResponse(KeyEvent keyEvent) {
		int idx = colorCircleView.getSelectedColorIndex();
		if (idx >= 0) {
			int next_idx = idx + 1;
			if (next_idx >= nPatches)
				next_idx -= nPatches;
			int prev_idx = idx - 1;
			if (prev_idx < 0)
				prev_idx += nPatches;
			int key = keyEvent.getKeyCode();
			// System.out.println("ColorCircleSelection.keyResponse(): " + key);
			if (key == ExPar.AdjustmentDownKey.getInt()) {
			} else if (key == ExPar.AdjustmentUpKey.getInt()) {
			}
		}
		return true;
	}

	/**
	 * This method is called when the display of the currently active timing
	 * group has been finished.
	 * 
	 * @param group
	 *            the index of the timing group which has been run.
	 */
	protected void timingGroupFinished(int group) {
		Selection.set(colorCircleView.getSelection());
		SelectionIndex.set(colorCircleView.getSelectionIndex());
		// SelectionBinPattern.set(colorCircleView.getSelectionBinPattern());
		SelectionBinPattern.getValue().set(
				colorCircleView.getSelectionBinPattern());
		SelectionBinPattern.getValue()
				.setTypeConjecture(ExParValue.TYPE_STRING);
		// System.out.println(colorCircleView.getSelection());
	}
}
