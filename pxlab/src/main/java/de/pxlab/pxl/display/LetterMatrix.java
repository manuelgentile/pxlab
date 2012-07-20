package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.util.Randomizer;
import de.pxlab.pxl.*;

/**
 * A rectangular field of letters with number of rows/columns and spacing
 * controlled by experimental parameters.
 * 
 * @author M. Hodapp
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 01/26/01 many modifications (H. Irtel)
 */
public class LetterMatrix extends FontDisplay {
	/**
	 * The set of possible letters to be used for automatic selection of
	 * letters.
	 */
	public ExPar SetOfLetters = new ExPar(STRING, new ExParValue(
			"BCDFGHJKLMNPQRSTVWXYZ"), "Set of Possible Letters");
	/** Actual letter sequence to be shown. */
	public ExPar Letters = new ExPar(STRING, new ExParValue(""),
			"Display letters");
	/** Number of columns for the letter matrix. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(4),
			"Number of columns");
	/** Number of rows for the letter matrix. */
	public ExPar NumberOfRows = new ExPar(SMALL_INT, new ExParValue(3),
			"Number of rows");
	/** Total width of the letter matrix field. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(200),
			"Width of letter matrix");
	/** Total height of the letter matrix field. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(200),
			"Height of letter matrix");
	/** Horizontal center location. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical center location. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");

	public LetterMatrix() {
		setTitleAndTopic("Letter Matrix", LETTER_MATRIX_DSP);
	}
	protected int letters;
	protected int rows;
	protected int columns;
	protected int matrixWidth;
	protected int matrixHeight;
	protected int topLeftX;
	protected int topLeftY;
	protected String fontFamily;
	protected int fontStyle;
	protected int fontSize;

	protected int create() {
		letters = enterDisplayElement(new CharPattern(Color), group[0]);
		enterTiming(this.Timer, this.Duration, 0, this.ResponseTime,
				this.ResponseCode);
		return (letters);
	}

	protected void computeGeometry() {
		rows = NumberOfRows.getInt();
		columns = NumberOfColumns.getInt();
		topLeftX = LocationX.getInt();
		topLeftY = LocationY.getInt();
		matrixWidth = Width.getInt();
		matrixHeight = Height.getInt();
		fontFamily = FontFamily.getString();
		fontStyle = FontStyle.getInt();
		fontSize = FontSize.getInt();
		CharPattern cp = (CharPattern) getDisplayElement(letters);
		// System.out.println("LetterMatrix.computeGeometry() Letters = " +
		// Letters);
		if (Letters.getValue().isEmpty()) {
			// System.out.println("LetterMatrix.computeGeometry(): sampling");
			sampleLetters(rows * columns);
		}
		// System.out.println("LetterMatrix.computeGeometry(): Letters = " +
		// Letters.getString());
		cp.setProperties(Letters.getString(), rows, columns, topLeftX,
				topLeftY, matrixWidth, matrixHeight, fontFamily, fontStyle,
				fontSize);
	}
	private static Randomizer rnd = null;

	private void sampleLetters(int n) {
		char[] lts = new char[n];
		String set = SetOfLetters.getString();
		int s = set.length();
		if (rnd == null)
			rnd = new Randomizer();
		for (int i = 0; i < n; i++) {
			int r = rnd.nextInt(s);
			// System.out.println("sampleLetters() r = " + r);
			lts[i] = set.charAt(r);
		}
		Letters.set(String.valueOf(lts));
		// System.out.println("sampleLetters() Letters = " + Letters);
	}
}
