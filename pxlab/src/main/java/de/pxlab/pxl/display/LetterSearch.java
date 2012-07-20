package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a random arrangement of distractor and target letters for visual
 * search.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 06/13/01
 */
public class LetterSearch extends SearchPattern {
	/**
	 * Target and distractor letters. The first letter of this string is the
	 * target letter and the second letter is the distractor letter.
	 */
	public ExPar Letters = new ExPar(STRING, new ExParValue("PR"),
			"Target and distractor letters");

	public LetterSearch() {
		setTitleAndTopic("Letter Search", SEARCH_DSP);
		TargetColor.set(new ExParValueFunction(ExParExpression.GRAY));
		DistractorColor.set(new ExParValueFunction(ExParExpression.GRAY));
	}
	protected Font font;
	protected int fontSize = -1;
	protected String targetLetter;
	protected String distractorLetter;

	protected void computeGeometry() {
		super.computeGeometry();
		String s = Letters.getString();
		targetLetter = s.substring(0, 1);
		distractorLetter = s.substring(1);
		if (itemSize != fontSize) {
			fontSize = itemSize;
			font = new Font("SansSerif", Font.PLAIN, fontSize);
		}
	}

	protected void showDistractorAt(Point p) {
		graphics2D.setFont(font);
		graphics2D.drawString(distractorLetter, p.x, p.y + itemSize);
	}

	protected void showTargetAt(Point p) {
		graphics2D.setFont(font);
		graphics2D.drawString(targetLetter, p.x, p.y + itemSize);
	}
}
