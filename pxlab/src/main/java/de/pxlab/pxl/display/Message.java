package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * The Message display shows a text, preferably a single line, centered on the
 * screen. Font properties, text color and timing are defined by experimental
 * parameters. The text may be defined by an array of strings. If the array
 * contains a single string and the string contains carriage return characters
 * ('\n') then the text is broken into multiple lines. If the text is an array
 * of strings then these are shown as multiple lines also. Multiple lines of
 * text are always centered on the screen and the horizontal position reference
 * is the center of the text line or paragraph while the vertical position
 * reference is the base line of a bottom line of text.
 * 
 * <p>
 * This display object is most usefule for single line messages. Multiple line
 * messages or text should use the super class TextParagraph.
 * 
 * @version 0.4.6
 */
/*
 * 
 * 01/19/01 allow multiple lines of text 01/26/01 use ExPar color, unififed
 * timing 03/15/01 simplify parameters
 * 
 * 2005/10/18 made FontSize depend on screen height
 * 
 * 2006/02/03 changed default reference point to BASE_CENTER.
 */
public class Message extends TextParagraph {
	public Message() {
		setTitleAndTopic("Text message display", TEXT_PAR_DSP);
		FontSize.set(new ExParValueFunction(ExParExpression.IDIV,
				new ExParValue(new ExParExpression(
						ExParExpression.SCREEN_HEIGHT)), new ExParValue(16)));
		Alignment.set(new ExParValueConstant(
				"de.pxlab.pxl.AlignmentCodes.CENTER"));
		ReferencePoint.set(new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.BASE_CENTER"));
	}
}
