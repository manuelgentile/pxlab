package de.pxlab.pxl.display;

import java.awt.event.KeyEvent;
import de.pxlab.pxl.*;

/**
 * Shows a rectangular pattern of color patches which contains a CIELab color
 * sample of constant lightness and allows for selection of a subset of color
 * patches.
 * 
 * <p>
 * This display object's timer should alwas be set to
 * (de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER |
 * de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER_BIT) in order to track mouse
 * buttons and to use the StopKey for stopping selection.
 * 
 * <p>
 * Keyboard Navigation: If a STOP_KEY_TIMER is used then keyboard navigation in
 * the CIELab plane is possible. The cursor left/right/up/down keys move the
 * center color within a plane of constant lightness. The page up/down keys
 * change the lightness level.
 * 
 * @version 0.1.3
 * @see MunsellColorSampleSelection
 */
/*
 * 
 * 2003/03/11
 * 
 * 2006/10/24 use ExPar.CIEWhitePoint
 */
public class CIELabColorSampleSelection extends ColorSampleSelection {
	/*
	 * Nominal white reference color used to compute the CIELAB coordinates. The
	 * CIELAB reference color is defined to be a perfect white reflector
	 * illuminated by the general scene illuminant which should be D65 or some
	 * other standard illuminant.
	 */
	/*
	 * replaced by ExPar.CIEWhitePoint public ExPar WhiteReferenceColor = new
	 * ExPar(COLOR, new ExParValue(new ExParExpression(ExParExpression.WHITE)),
	 * "Nominal White Reference Color");
	 */
	/** Center color of the selection set. */
	public ExPar SelectionCenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)),
			"Selection Set Center Color");
	/** Selection set chroma step between patches. */
	public ExPar SelectionChromaStep = new ExPar(SMALL_INT, new ExParValue(10),
			"Chroma Step Between Selection Patches");
	/** Selection set chroma step between patches. */
	public ExPar SelectionLightnessStep = new ExPar(SMALL_INT, new ExParValue(
			10), "Lightness Step Between Selection Patches");
	/** Selection set chroma step between patches. */
	public ExPar SelectionHueAngleStep = new ExPar(SMALL_INT,
			new ExParValue(10), "Hue Angle Step Between Selection Patches");
	public ExPar SelectionPlaneType = new ExPar(SMALL_INT, new ExParValue(0),
			"CIELab Plane Type");

	public CIELabColorSampleSelection() {
		setTitleAndTopic("CIELAB Color Selection", COLOR_SPACES_DSP);
		NumberOfSelectionColumns.set(20);
		NumberOfSelectionRows.set(6);
		SelectionPatchWidth.set(32);
		SelectionPatchHeight.set(32);
		SelectionGapSize.set(2);
	}

	/** Initialize the display list of the demo. */
	protected int create() {
		colorSampleView = new CIELabColorSampleView(SelectionCenterColor);
		disk = enterDisplayElement(new Oval(DiskColor), group[0]);
		int i = enterDisplayElement(colorSampleView, group[0]);
		defaultTiming(0);
		return (i);
	}

	/*
	 * protected void computeColors() { super.computeColors();
	 * PxlColor.getDeviceTransform
	 * ().setWhitePoint(WhiteReferenceColor.getPxlColor().getComponents()); }
	 */
	protected void computeGeometry() {
		super.computeGeometry();
		((CIELabColorSampleView) colorSampleView)
				.setPlaneType(SelectionPlaneType.getInt());
		((CIELabColorSampleView) colorSampleView)
				.setCIELabLightnessStep(SelectionLightnessStep.getDouble());
		((CIELabColorSampleView) colorSampleView)
				.setCIELabChromaticityStep(SelectionChromaStep.getDouble());
		((CIELabColorSampleView) colorSampleView)
				.setCIELabHueAngleStep(SelectionHueAngleStep.getDouble());
	}

	/**
	 * This method is here to be overridden by display objects which want to
	 * respond to single key strokes which do NOT stop the display timer. The
	 * display object may change its appearance and the currently active timing
	 * group will be redrawn afterwards.
	 */
	protected boolean keyResponse(KeyEvent keyEvent) {
		// System.out.println("CIELabColorSampleSelection.keyResponse(): " +
		// keyEvent.getKeyCode());
		((CIELabColorSampleView) colorSampleView).adjustCenterColor(keyEvent
				.getKeyCode());
		setDiskColor();
		return true;
	}
}
