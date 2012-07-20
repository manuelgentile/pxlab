package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A letter matrix and a feedback message which may contain information about
 * the response to a previously shown letter matrix.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
public class LetterMatrixFeedback extends LetterMatrix {
	/** Letters which have been recorded as a response. */
	public ExPar ResponseLetters = new ExPar(STRING, new ExParValue(""),
			"Response letters");
	/**
	 * Data parameter which stores the number of letters which are identical in
	 * Letters and ResponseLetters.
	 */
	public ExPar NumberCorrect = new ExPar(RTDATA, new ExParValue(0),
			"Number of Correct Letters");
	/** Feedback text string. */
	public ExPar Text = new ExPar(STRING, new ExParValue(""), "Feedback Text");

	/** Constructor creating the title of the display. */
	public LetterMatrixFeedback() {
		setTitleAndTopic("Letter Matrix with Message Text", LETTER_MATRIX_DSP);
		JustInTime.set(1);
	}
	private int feedback;

	/** Initialize the display list of the demo. */
	protected int create() {
		letters = enterDisplayElement(new CharPattern(Color), group[0]);
		feedback = enterDisplayElement(new TextParagraphElement(Color),
				group[0]);
		defaultTiming();
		return letters;
	}

	protected void computeGeometry() {
		super.computeGeometry();
		String s1 = Letters.getString();
		int n1 = s1.length();
		String s2 = ResponseLetters.getString();
		int n2 = s2.length();
		int m = (n1 < n2) ? n1 : n2;
		int n = 0;
		if (s1 != "" && s2 != "") {
			for (int i = 0; i < m; i++) {
				if (s1.charAt(i) == s2.charAt(i))
					n++;
			}
		}
		NumberCorrect.set(n);
		TextParagraphElement txt = (TextParagraphElement) getDisplayElement(feedback);
		txt.setFont(FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt() / 2);
		txt.setReferencePoint(PositionReferenceCodes.MIDDLE_CENTER);
		txt.setLocation(0, LocationY.getInt() + Height.getInt());
		txt.setText(this.Text.getStringArray());
	}
}
