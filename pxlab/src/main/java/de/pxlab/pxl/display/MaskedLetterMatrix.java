package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A letter matrix followed by a mask followed by an optical cue. Three timing
 * intervals are involved here:
 * 
 * <ol>
 * <li>The letter only interval controlled by parameters Timer and Duration.
 * 
 * <li>Then follows a mask only interval. This is controlled by the timer
 * CueDelayTimer and the duration CueDelayDuration since it actually defines the
 * SOA between mask and cue.
 * 
 * <li>Finally we have the mask and cue both being visible. This is controlled
 * by timer MaskTimer and duration parameter MaskDuration.
 * 
 * </ol>
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
public class MaskedLetterMatrix extends LetterMatrix {
	/**
	 * Letters used for masking. The mask is created by superposition of all
	 * letters contained in this string.
	 */
	public ExPar MaskLetters = new ExPar(STRING,
			new ExParValue("ASDFGHJKMDEW"), "Mask letters");
	/** Color of the masking letters. */
	public ExPar MaskColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)), "Letter color");
	/** Timer for that timing interval which shows both mask and cue. */
	public ExPar MaskTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Mask and cue interval timer");
	/**
	 * Time interval where the mask is shown together with the cue. The actual
	 * mask duration is MaskDuration + CueDelayDuration.
	 */
	public ExPar MaskDuration = new ExPar(DURATION, new ExParValue(1000),
			"Mask and cue visibility duration");
	/**
	 * Actual mask and cue display duration as measured by the PXLab timer.
	 */
	public ExPar MaskResponseTime = new ExPar(RTDATA, new ExParValue(0),
			"Actual mask and cue display duration or response time");
	/** Event code which stopped the mask duration interval. */
	public ExPar MaskResponseCode = new ExPar(RTDATA, new ExParValue(0),
			"Mask and cue duration time interval stop code");
	/** Cue color. */
	public ExPar CueColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)), "Cue color");
	/** Row or column position of the cue. */
	public ExPar CuePosition = new ExPar(SMALL_INT, new ExParValue(0),
			"Row/column number of cue");
	/** Distance between letters and cue. */
	public ExPar CueDistance = new ExPar(SCREENSIZE, new ExParValue(50),
			"Distance between letters and cue");
	/** Cue size. */
	public ExPar CueSize = new ExPar(SCREENSIZE, new ExParValue(0), "Cue size");
	/**
	 * Flag to indicate whether a row (1) or a column (0) should be cued.
	 */
	public ExPar CueOrientation = new ExPar(FLAG, new ExParValue(1),
			"Row/column flag for cue");
	/**
	 * Cue delay timer. Should be a CLOCK_TIMER. The cue delay is the duration
	 * of the interval between mask onset and and cue onset.
	 */
	public ExPar CueDelayTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Timer for cue onset delay to mask");
	/**
	 * Time interval where the mask is shown without the cue. This corresponds
	 * to the onset asynchrony between mask and cue.
	 */
	public ExPar CueDelayDuration = new ExPar(DURATION, new ExParValue(100),
			"Cue onset delay to mask");
	/** Actual duration of the SOA between mask onset and cue onset. */
	public ExPar CueDelayTime = new ExPar(RTDATA, new ExParValue(0),
			"Actual cue SOA to mask");
	/** Code that indicates which event stopped the cue delay interval. */
	public ExPar CueDelayStopCode = new ExPar(RTDATA, new ExParValue(0),
			"Cue delay stop code");

	/** Constructor creating the title of the display. */
	public MaskedLetterMatrix() {
		setTitleAndTopic("Masked Letter Matrix with Cue", LETTER_MATRIX_DSP);
		NumberOfRows.set(2);
	}
	private int mask, cue1, cue2;

	/** Initialize the display list of the demo. */
	protected int create() {
		letters = enterDisplayElement(new CharPattern(Color), group[0]);
		mask = enterDisplayElement(new CharPatternMask(MaskColor), group[1]
				+ group[2]);
		cue1 = enterDisplayElement(new Bar(CueColor), group[2]);
		cue2 = enterDisplayElement(new Bar(CueColor), group[2]);
		// group[0]: Letters only
		enterTiming(Timer, Duration, 0, ResponseTime, ResponseCode);
		// group[1]: Mask only
		enterTiming(CueDelayTimer, CueDelayDuration, 1, CueDelayTime,
				CueDelayStopCode);
		// group[2]: Mask and Cue
		enterTiming(MaskTimer, MaskDuration, 2, MaskResponseTime,
				MaskResponseCode);
		return (letters);
	}

	protected void computeGeometry() {
		super.computeGeometry();
		CharPatternMask cpm = (CharPatternMask) getDisplayElement(mask);
		cpm.setProperties(MaskLetters.getString(), rows, columns, topLeftX,
				topLeftY, matrixWidth, matrixHeight, fontFamily, fontStyle,
				fontSize);
		int cuePosition = CuePosition.getInt();
		Bar cu1 = (Bar) getDisplayElement(cue1);
		Bar cu2 = (Bar) getDisplayElement(cue2);
		int cueSize = CueSize.getInt();
		cu1.setSize(cueSize, cueSize);
		cu2.setSize(cueSize, cueSize);
		int cueDist = CueDistance.getInt();
		if (CueOrientation.getFlag()) {
			// cue marks a row
			Point lp1 = cpm.getLetterLocation(cuePosition, 0);
			Point lp2 = cpm.getLetterLocation(cuePosition, columns - 1);
			// fs/3 is an ad hoc compromise
			int py = lp1.y - cueSize / 2 - fontSize / 3;
			cu1.setLocation(lp1.x - cueDist - cueSize, py);
			cu2.setLocation(lp2.x + cueDist, py);
		} else {
			// cue marks a column
			Point lp1 = cpm.getLetterLocation(0, cuePosition);
			Point lp2 = cpm.getLetterLocation(rows - 1, cuePosition);
			int asc = 7 * cpm.getAscent() / 10;
			int px = lp1.x - cueSize / 2;
			cu1.setLocation(px, lp1.y - asc - cueDist - cueSize);
			cu2.setLocation(px, lp2.y + cueDist);
		}
	}
}
