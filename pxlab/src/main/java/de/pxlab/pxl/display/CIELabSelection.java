package de.pxlab.pxl.display;

import java.awt.event.KeyEvent;
import de.pxlab.pxl.*;

/**
 * Shows a rectangular pattern of color patches which contains a CIELab color
 * sample of constant lightness and allows for selection of a subset of color
 * patches. Selected colors are indicated by their circular form.
 * 
 * <p>
 * This display object's timer should alwas be set to
 * (de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER |
 * de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER_BIT) in order to track mouse
 * buttons and to use the StopKey for stopping selection.
 * 
 * <p>
 * Keyboard Navigation: If a STOP_KEY_TIMER is used, then keyboard navigation in
 * the CIELab plane is possible. The cursor left/right/up/down keys move the
 * center color within a plane of constant lightness. The page up/down keys
 * change the lightness level.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.CIELabColorSampleView
 */
/*
 * 
 * 11/03/03
 */
public class CIELabSelection extends Display {
	/**
	 * Nominal white reference color used to compute the CIELAB coordinates. The
	 * CIELAB reference color is defined to be a perfect white reflector
	 * illuminated by the general scene illuminant which should be D65 or some
	 * other standard illuminant.
	 */
	public ExPar WhiteReferenceColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)),
			"Nominal White Reference Color");
	/** Center color of the selection set. */
	public ExPar SelectionCenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Selection Set Center Color");
	/** Selection set chroma step between patches. */
	public ExPar SelectionChromaStep = new ExPar(SMALL_INT, new ExParValue(10),
			"Chroma Step Between Selection Patches");
	/** Horizontal center position of the selection set. */
	public ExPar SelectionLocationX = new ExPar(HORSCREENPOS,
			new ExParValue(0), "Selection Set Horizontal Center Position");
	/** Vertical center position of the selection set. */
	public ExPar SelectionLocationY = new ExPar(VERSCREENPOS,
			new ExParValue(0), "Selection Set Vertical Center Position");
	/** Number of patch columns. */
	public ExPar NumberOfSelectionColumns = new ExPar(SMALL_INT,
			new ExParValue(9), "Number of Selection Patches in a Row");
	/** Number of patch rows. */
	public ExPar NumberOfSelectionRows = new ExPar(SMALL_INT,
			new ExParValue(9), "Number of Selection Patches in a Column");
	/** Color patch size. */
	public ExPar SelectionPatchWidth = new ExPar(SCREENSIZE,
			new ExParValue(24), "Color Selection Patch Size");
	/** Color patch size. */
	public ExPar SelectionPatchHeight = new ExPar(SCREENSIZE,
			new ExParValue(24), "Color Selection Patch Size");
	/** Gap between patches. */
	public ExPar SelectionGapSize = new ExPar(SCREENSIZE, new ExParValue(6),
			"Selection Set Gap Size");
	/** If true then only a single object may be selected. */
	public ExPar SingleSelection = new ExPar(FLAG, new ExParValue(0),
			"Single Selection Flag");
	/** Stores the set of selected CIELab values. */
	public ExPar SelectionSet = new ExPar(RTDATA, new ExParValue(""),
			"Set of Selected Colors");

	public CIELabSelection() {
		setTitleAndTopic("CIELAB Color Selection Set", COLOR_SPACES_DSP | DEMO);
	}
	protected CIELabColorSampleView colorSampleView;

	/** Initialize the display list of the demo. */
	protected int create() {
		colorSampleView = new CIELabColorSampleView(SelectionCenterColor);
		int i = enterDisplayElement(colorSampleView, group[0]);
		defaultTiming(0);
		return (i);
	}

	protected void computeColors() {
		PxlColor.getDeviceTransform().setWhitePoint(
				WhiteReferenceColor.getPxlColor().getComponents());
	}

	protected void computeGeometry() {
		colorSampleView.setProperties(SelectionLocationX.getInt(),
				SelectionLocationY.getInt(), NumberOfSelectionColumns.getInt(),
				NumberOfSelectionRows.getInt(), SelectionPatchWidth.getInt(),
				SelectionPatchHeight.getInt(), SelectionGapSize.getInt(),
				SingleSelection.getFlag());
		colorSampleView.setCIELabChromaticityStep(SelectionChromaStep
				.getDouble());
	}

	/**
	 * This method is called whenever the pointer button has been pressed. This
	 * method is activated by setting the
	 * de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER_BIT flag of this object's
	 * timer.
	 */
	protected boolean pointerActivated() {
		colorSampleView.select(pointerActivationX, pointerActivationY);
		return true;
	}

	/**
	 * This method is here to be overridden by display objects which want to
	 * respond to single key strokes which do NOT stop the display timer. The
	 * display object may change its appearance and the currently actice timing
	 * group will be redrawn afterwards.
	 */
	protected boolean keyResponse(KeyEvent keyEvent) {
		colorSampleView.adjustCenterColor(keyEvent.getKeyCode());
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
		SelectionSet.set(colorSampleView.getSelection());
		// System.out.println(colorSampleView.getSelection());
	}
}
