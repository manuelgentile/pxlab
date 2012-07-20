package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A letter matrix followed by a clear screen interval followed by an optical
 * cue.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
public class CuedLetterMatrix extends LetterMatrix {
	/** Cue color. */
	public ExPar CueColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)), "Cue color");
	/** Row or column position of the cue. */
	public ExPar CuePosition = new ExPar(SMALL_INT, new ExParValue(0),
			"Row/Column number of cue");
	/** Distance between letters and cue. */
	public ExPar CueDistance = new ExPar(SCREENSIZE, new ExParValue(50),
			"Distance between letters and cue");
	/** Cue size. */
	public ExPar CueSize = new ExPar(SCREENSIZE, new ExParValue(0), "Cue size");
	/**
	 * Flag to indicate whether a row (1) or a column (0) should be cued.
	 */
	public ExPar CueOrientation = new ExPar(FLAG, new ExParValue(1),
			"Row/Column flag for cue");
	/**
	 * Cue delay timer. Should be a CLOCK_TIMER. The cue delay is the duration
	 * of the clear screen interval between letter offset and cue onset.
	 */
	public ExPar CueDelayTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Timer for cue onset delay");
	/**
	 * Intended duration of the clear screen interval between letter offset and
	 * cue onset.
	 */
	public ExPar CueDelayDuration = new ExPar(DURATION, 0, 3000,
			new ExParValue(100), "Cue onset delay to mask");
	/**
	 * Actual duration of the clear screen interval between letter offset and
	 * cue onset as measured by the PXLab control timer.
	 */
	public ExPar CueDelayTime = new ExPar(RTDATA, new ExParValue(0),
			"Actual cue onset delay duration");
	/** Code that indicates which event stopped the cue delay interval. */
	public ExPar CueDelayStopCode = new ExPar(RTDATA, new ExParValue(0),
			"Cue stop code");
	/** Visibility duration timer for the cue. */
	public ExPar CueTimer = new ExPar(GEOMETRY_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Cue duration timer");
	/** Visibility duration of the cue. */
	public ExPar CueDuration = new ExPar(DURATION, 0, 3000,
			new ExParValue(100), "Cue duration");

	/** Constructor creating the title of the display. */
	public CuedLetterMatrix() {
		setTitleAndTopic("Letter Matrix Followed by a Cue", LETTER_MATRIX_DSP);
		NumberOfRows.set(2);
	}
	private int cue1, cue2, clear;

	/** Initialize the display list of the demo. */
	protected int create() {
		letters = enterDisplayElement(new CharPattern(Color), group[0]);
		clear = enterDisplayElement(new Clear(), group[1]);
		cue1 = enterDisplayElement(new Bar(CueColor), group[2]);
		cue2 = enterDisplayElement(new Bar(CueColor), group[2]);
		// group[0]: Letters only
		enterTiming(Timer, Duration, 0, ResponseTime, ResponseCode);
		// group[1]: Clear screen
		enterTiming(CueDelayTimer, CueDelayDuration, 1, CueDelayTime,
				CueDelayStopCode);
		// group[2]: Cue
		enterTiming(CueTimer, CueDuration, 2);
		return (letters);
	}

	protected void computeGeometry() {
		super.computeGeometry();
		CharPattern cpm = (CharPattern) getDisplayElement(letters);
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
