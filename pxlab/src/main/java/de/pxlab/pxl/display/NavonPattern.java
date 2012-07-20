package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A rectangular pattern whose pattern elements are letters. This may be used to
 * define patterns like those which have been used in the paper
 * 
 * <p>
 * Navon, D. (1977). Forrest before trees: The precedence of global features in
 * visual perception. Cognitive Psychology, 9, 353-383.
 * 
 * <p>
 * to study the processing of local and global aspects of stimuli.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 01/27/03
 */
public class NavonPattern extends FontDisplay {
	private int[] p = { 17, 9, 5, 3, 5, 9, 17 };
	/**
	 * The pattern of letters which is shown. This is an array of integer values
	 * which define the pattern to be shown. Each number defines a single
	 * horizontal line of the pattern. The first entry is the top line of the
	 * pattern. The integer numbers are interpreted as binary pattern codes. If
	 * a bit is set then the respective position contains an elementary letter
	 * and if a bit is not set then the respective position is empty. Bits are
	 * interpreted such that the lowest value bit correspond to the leftmost
	 * column of the pattern. The number of columns used is defined by parameter
	 * NumberOfColumns. As an example consider the array [17, 9, 5, 3, 5, 9, 17]
	 * which makes up the pattern 'K'.
	 */
	public ExPar Pattern = new ExPar(INTEGER, new ExParValue(p), "Pattern");
	/**
	 * Letter element making up the pattern. This is a string which contains a
	 * single letter. This letter is used to paint the pattern.
	 */
	public ExPar Letter = new ExPar(STRING, new ExParValue("O"), "Letter");
	/** Number of columns for the letter pattern. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(7),
			"Number of columns");
	/** Horizontal center location. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical center location. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");

	public NavonPattern() {
		setTitleAndTopic("Navon Letter Pattern", LETTER_MATRIX_DSP);
	}
	private int s;

	protected int create() {
		s = enterDisplayElement(new SingleCharPattern(Color), group[0]);
		enterTiming(this.Timer, this.Duration, 0, this.ResponseTime,
				this.ResponseCode);
		return (s);
	}

	protected void computeGeometry() {
		int[] pt = Pattern.getIntArray();
		SingleCharPattern cp = (SingleCharPattern) getDisplayElement(s);
		cp.setProperties(pt, (int) (Letter.getString().charAt(0)), pt.length,
				NumberOfColumns.getInt(), LocationX.getInt(),
				LocationY.getInt(), FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt());
	}
}
