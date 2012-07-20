package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Shows an image contained in an external file followed by a masking image from
 * another file. The masking image must have the same geometry as the target
 * image. The default timing parameters control the time of the mask. The time
 * of the picture alone is controlled by SOATimer and SOADuration. The parameter
 * SOATimer should always be set to de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 11/13/01
 */
public class PictureMasked extends Picture {
	/** Masking Image file name. */
	public ExPar MaskFileName = new ExPar(STRING, new ExParValue(""),
			"Masking image file name");
	/**
	 * Timer for controlling the duration of the interval where only the picture
	 * is visible. This is the stimulus onset asynchrony between picture and
	 * mask.
	 */
	public ExPar SOATimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Picture to mask SOA timer");
	/**
	 * Duration of the interval where only the picture is visible. This is the
	 * stimulus onset asynchrony between picture and mask.
	 */
	public ExPar SOADuration = new ExPar(DURATION, new ExParValue(1000),
			"Picture to mask SOA duration");

	public PictureMasked() {
		setTitleAndTopic("Masked picture display", PICTURE_DSP);
	}
	private BitMapElement pict;
	private BitMapElement mask;

	protected int create() {
		pict = new BitMapElement();
		mask = new BitMapElement();
		enterDisplayElement(pict, group[0]);
		enterDisplayElement(mask, group[1]);
		// group[0]: target picture only
		enterTiming(SOATimer, SOADuration, 0);
		// group[1]: Mask only
		enterTiming(Timer, Duration, ResponseSet, 1, ResponseTime, ResponseCode);
		// We need a dummy color parameter for the display editor
		pict.setColorPar(ExPar.ScreenBackgroundColor);
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
		String fn = FileName.getString();
		if ((fn != null) && (fn.length() != 0)) {
			pict.setImage(Directory.getString(), fn);
			pict.setLocation(LocationX.getInt(), LocationY.getInt());
			pict.setReferencePoint(ReferencePoint.getInt());
		}
		fn = MaskFileName.getString();
		if ((fn != null) && (fn.length() != 0)) {
			mask.setImage(Directory.getString(), fn);
			mask.setLocation(LocationX.getInt(), LocationY.getInt());
			mask.setReferencePoint(ReferencePoint.getInt());
		}
	}
}
