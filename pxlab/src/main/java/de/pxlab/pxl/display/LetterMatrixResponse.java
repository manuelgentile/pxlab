package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.event.KeyEvent;
import de.pxlab.pxl.*;

/**
 * Collects a letter matrix response and checks which and how many of the
 * letters are correct.
 * 
 * @version 0.1.2
 */
/*
 * 
 * 2007/04/25 removed iitialization of StopKey
 */
public class LetterMatrixResponse extends LetterMatrix {
	public ExPar UpperCase = new ExPar(FLAG, new ExParValue(1),
			"Transform input characters to upper case");
	public ExPar ResponseRows = new ExPar(SMALL_INT, new ExParValue(3),
			"Number of rows in response subset");
	public ExPar ResponseColumns = new ExPar(SMALL_INT, new ExParValue(4),
			"Number of columns in response subset");
	public ExPar FirstResponseRow = new ExPar(SMALL_INT, new ExParValue(0),
			"First row in response subset");
	public ExPar FirstResponseColumn = new ExPar(SMALL_INT, new ExParValue(0),
			"First column in response subset");
	/**
	 * This is the sequence of letters which is considered to be the correct
	 * sequence.
	 */
	public ExPar CorrectLetters = new ExPar(STRING, new ExParValue(""),
			"Correct letters");
	/**
	 * Indicates the positions where the response string 'Letters' is identical
	 * to 'CorrectLetters'.
	 */
	public ExPar CorrectResponsePositions = new ExPar(STRING,
			new ExParValue(""), "Correct response positions");
	/**
	 * The number of letters which are identical in 'Letters' and
	 * 'CorrectLetters'.
	 */
	public ExPar NumberCorrect = new ExPar(RTDATA, new ExParValue(0),
			"Number of correct letters in response");

	public LetterMatrixResponse() {
		setTitleAndTopic("Letter matrix response", LETTER_MATRIX_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER"));
		JustInTime.set(1);
	}
	private char[] chars;
	private int cRow;
	private int cCol;
	private int minRow;
	private int minCol;
	private int maxRow;
	private int maxCol;
	private CharPattern charPattern;
	private boolean upperCase;

	/** Initialize the display list of the demo. */
	protected int create() {
		int lts = enterDisplayElement(new CharPattern(Color), group[0]);
		defaultTiming();
		charPattern = (CharPattern) getDisplayElement(lts);
		return (lts);
	}

	protected void computeGeometry() {
		charPattern.setSize(Width.getInt(), Height.getInt());
		charPattern.setLocation(LocationX.getInt(), LocationY.getInt());
		charPattern.setFont(FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt());
		int letterRows = NumberOfRows.getInt();
		int letterColumns = NumberOfColumns.getInt();
		charPattern.setRows(letterRows);
		charPattern.setColumns(letterColumns);
		chars = new char[(letterRows) * (letterColumns)];
		int k = 0;
		for (int i = 0; i < letterRows; i++)
			for (int j = 0; j < letterColumns; j++)
				chars[k++] = ' ';
		Letters.set(String.valueOf(chars));
		charPattern.setLetters(Letters.getString());
		upperCase = (UpperCase.getInt() != 0);
		// System.out.println(FirstResponseRow);
		cRow = FirstResponseRow.getInt();
		cCol = FirstResponseColumn.getInt();
		minRow = cRow;
		minCol = cCol;
		maxRow = minRow + ResponseRows.getInt() - 1;
		maxCol = minCol + ResponseColumns.getInt() - 1;
		charPattern.setCursor(cRow, cCol);
		NumberCorrect.set(0);
	}

	protected boolean keyResponse(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		int keyChar = keyEvent.getKeyChar();
		boolean validKey = true;
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			cs_left();
			break;
		case KeyEvent.VK_RIGHT:
			cs_right();
			break;
		case KeyEvent.VK_UP:
			cRow--;
			if (cRow < minRow)
				cRow = minRow;
			break;
		case KeyEvent.VK_DOWN:
			cRow++;
			if (cRow > maxRow)
				cRow = maxRow;
			break;
		case KeyEvent.VK_END:
			cCol = maxCol;
			cRow = maxRow;
			break;
		case KeyEvent.VK_HOME:
			cCol = minRow;
			cRow = minCol;
			break;
		case KeyEvent.VK_BACK_SPACE:
			cs_left();
			chars[cRow * (maxCol + 1) + cCol] = ' ';
			break;
		case KeyEvent.VK_DELETE:
			chars[cRow * (maxCol + 1) + cCol] = ' ';
			break;
		default:
			if (keyChar != KeyEvent.CHAR_UNDEFINED) {
				chars[cRow * (maxCol + 1) + cCol] = upperCase ? Character
						.toUpperCase((char) keyChar) : (char) keyChar;
				cs_right();
			} else {
				validKey = false;
			}
			break;
		}
		if (validKey) {
			Letters.set(String.valueOf(chars));
			// Seems we don't need it
			// ExPar.ResponseStore.set(Letters.getValue());
			charPattern.setLetters(Letters.getString());
			charPattern.setCursor(cRow, cCol);
			setNumberCorrect(chars);
			/*
			 * System.out.println("LetterMatrixResponse.keyResponse(): code=" +
			 * keyCode + ", char = " + (char)keyChar);
			 * System.out.println("LetterMatrixResponse.keyResponse(): Cursor at("
			 * + cRow + "," + cCol + ")");
			 */
		}
		// System.out.println("LetterMatrixResponse.keyResponse(): Letters = " +
		// Letters.getString());
		return true;
	}

	private void cs_left() {
		cCol--;
		if (cCol < minCol) {
			cRow--;
			if (cRow < minRow) {
				cCol = minCol;
				cRow = minRow;
			} else {
				cCol = maxCol;
			}
		}
	}

	private void cs_right() {
		cCol++;
		if (cCol > maxCol) {
			cRow++;
			if (cRow > maxRow) {
				cCol = maxCol;
				cRow = maxRow;
			} else {
				cCol = minCol;
			}
		}
	}

	private void setNumberCorrect(char[] rs) {
		String cs = CorrectLetters.getString();
		char[] b = new char[cs.length()];
		for (int i = 0; i < b.length; i++)
			b[i] = '-';
		if (cs.length() != rs.length)
			return;
		int n = 0;
		for (int i = 0; i < rs.length; i++) {
			if (cs.charAt(i) == rs[i]) {
				b[i] = '#';
				n++;
			}
		}
		NumberCorrect.set(n);
		CorrectResponsePositions.set(new String(b));
	}
}
