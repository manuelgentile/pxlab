package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import de.pxlab.pxl.*;

/**
 * A row of letters which which constitute a correct word if put into the
 * correct sequence. If the mouse pointer is released on any of the letters then
 * it is moved into a second display row at the first empty position.
 * 
 * @author E. Reitmayr
 * @version 0.1.2
 */
/*
 * 
 * 01/29/03 Fixed update bug in pointerReleased() method
 */
public class Anagram extends LetterMatrix {
	/**
	 * Number of letters in the current anagram. If undefined then this
	 * parameter is derived from the actual value of Anagram.
	 */
	public ExPar NumberOfLetters = new ExPar(SMALL_INT, new ExParValue(10),
			"Number of Letters in Anagram");
	/** Anagram string to be shown. */
	public ExPar Anagram = new ExPar(STRING, new ExParValue("MEINPETREX"),
			"Anagram");
	/** Solution string created by the subject. */
	public ExPar Solution = new ExPar(RTDATA, new ExParValue(""),
			"Solution string");

	/*
	 * public ExPar CharMatrix = new ExPar(RTDATA, new ExParValue(' '),
	 * "Position of Strings during one trial");
	 */
	public Anagram() {
		setTitleAndTopic("Anagrams", PROBLEM_SOLVING_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_KEY_TIMER"));
	}
	private char[] charMatrix, probe;
	private int Rows;
	private int Columns;
	private boolean newTrial;
	private CharPattern charPattern;

	/** Initialize the display list of the demo. */
	protected int create() {
		int a = enterDisplayElement(new CharPattern(Color), group[0]);
		defaultTiming(0);
		DisplayElement.setValidBounds(true);
		charPattern = (CharPattern) getDisplayElement(a);
		return (a);
	}

	protected void computeGeometry() {
		charPattern.setSize(Width.getInt(), Height.getInt());
		charPattern.setLocation(LocationX.getInt(), LocationY.getInt());
		charPattern.setFont(FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt());
		newTrial = false;
		if (probe == null || String.valueOf(probe) != Anagram.getString()) {
			newTrial = true;
			String cAnagram = Anagram.getString();
			int al = cAnagram.length();
			probe = new char[al];
			int probePosition = 0;
			for (int i = 0; i < al; i++) {
				probe[i] = cAnagram.charAt(i);
			}
			if (al != NumberOfLetters.getInt()) {
				NumberOfLetters.set(al);
			}
		}
		Rows = 2;
		Columns = NumberOfLetters.getInt();
		charPattern.setRows(Rows);
		charPattern.setColumns(Columns);
		if (charMatrix == null || newTrial) {
			charMatrix = new char[(Rows) * (Columns)];
			int k = 0;
			for (int i = 0; i < Rows; i++) {
				if (i == 0) {
					for (int j = 0; j < Columns; j++) {
						charMatrix[k++] = probe[j];
					}
				}
				if (i == 1) {
					charMatrix[k++] = '_';
					for (int j = 1; j < Columns; j++) {
						charMatrix[k++] = ' ';
					}
				}
			}
		}
		Letters.set(String.valueOf(charMatrix));
		charPattern.setLetters(Letters.getString());
		charPattern.setSize(FontSize.getInt() * Columns, FontSize.getInt() * 2);
		setResult();
	}

	/** Ignore timing steps since we have only a single group. */
	public void showGroup(Graphics g) {
		show(g);
	}

	/** This is a hook into the mouse button released event handler. */
	protected boolean pointerReleased() {
		for (int i = 0; i < Rows; i++) {
			for (int j = 0; j < Columns; j++) {
				Point p = charPattern.getLetterLocation(i, j);
				if (pointerActivationX >= (p.x - (FontSize.getInt() / 2))
						&& pointerActivationX < (p.x + (FontSize.getInt() / 2))
						&& pointerActivationY <= p.y
						&& pointerActivationY > p.y - FontSize.getInt()) {
					if (pointerReleaseX >= (p.x - (FontSize.getInt() / 2))
							&& pointerReleaseX < (p.x + (FontSize.getInt() / 2))
							&& pointerReleaseY <= p.y
							&& pointerReleaseY > p.y - FontSize.getInt()) {
						// System.out.println("Punkt entspricht Buchstabe " + i
						// + ", " + j);
						if (i == 0) {
							for (int k = Columns; k < Columns * 2; k++) {
								if (moveToTestRowAllowed(j, k)) {
									charMatrix[k] = charMatrix[j];
									charMatrix[j] = ' ';
									if (k < (2 * Columns - 1))
										charMatrix[k + 1] = '_';
									break;
								}
							}
							break;
						}
						if (i == 1) {
							if (moveFromTestRowAllowed(Columns + j)) {
								for (int k = 0; k < Columns; k++) {
									if (probe[k] == charMatrix[Columns + j]
											&& charMatrix[k] == ' ') {
										charMatrix[k] = charMatrix[Columns + j];
										if (j < Columns) {
											charMatrix[Columns + j] = '_';
											if (j < Columns - 1) {
												charMatrix[Columns + j + 1] = ' ';
											}
										}
										break;
									}
								}
							}
							break;
						}
					}
				}
			}
		}
		Letters.set(String.valueOf(charMatrix));
		charPattern.setLetters(Letters.getString());
		setResult();
		return true;
	}

	// pointer release is in probe row
	// check if probe is valid and if position in test row is valid
	private boolean moveToTestRowAllowed(int posProbe, int posTest) {
		if (charMatrix[posProbe] == ' ')
			return false;
		if (charMatrix[posTest] == '_')
			return true;
		return false;
	}

	// pointer release in test row
	private boolean moveFromTestRowAllowed(int pos) {
		if (pos == (2 * Columns - 1))
			return true;
		if (charMatrix[pos + 1] == '_')
			return true;
		return false;
	}

	private void setResult() {
		int resultSize = 0;
		for (int i = Columns; i < 2 * Columns; i++) {
			if (charMatrix[i] == '_' || charMatrix[i] == ' ') {
				break;
			}
			resultSize++;
		}
		char[] result = new char[resultSize];
		for (int i = Columns; i < resultSize + Columns; i++) {
			result[i - Columns] = charMatrix[i];
		}
		Solution.set(String.valueOf(result));
	}
}
