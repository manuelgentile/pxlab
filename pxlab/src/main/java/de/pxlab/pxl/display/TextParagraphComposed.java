package de.pxlab.pxl.display;

import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * This is a paragraph of text whose text elements are composed of themes and
 * segments defined in a text cache file. The display creates a hash map from a
 * text segment file and then substitutes segment codes by segments from the
 * hash map. Here is an example from a text segment file:
 * 
 * <pre>
 *    &#064;R01A=Als sich Sabine in der N�he der Felsen aufhielt, 
 *    trat sie auf eine Glasscherbe
 *    &#064;R01B=. 
 *    &#064;R01C= und schnitt sich in den Fu�.
 *    &#064;R01D= Sie hat gerade ihre Armbanduhr gesucht, 
 *    die sie verlegt hatte, w�hrend sie auf dem Felsen sa�.
 *    &#064;R01E= Sie rief verzweifelt um Hilfe, aber es gab Niemanden 
 *    in der Gegend, der sie h�ren konnte.
 *    &#064;R01F=Als sich Sabine in der N�he der Felsen aufhielt, 
 *    fand sie eine Glasscherbe mit einer ungew�hnlichen Aufschrift. 
 *    Sie brachte sie in das nahegelegene Museum, um es untersuchen zu lassen.
 *    &#064;T01E=Sabine schnitt sich in den Fu�.
 * </pre>
 * 
 * <p>
 * Each segment has a key which is introduced by the '&#064;'-character and
 * which does not belong to the key. The key and the text are separated by the
 * '='-character. The text for a key is everthing between the '='-character and
 * the next '&#064;'-character or the end of the file. Trailing blanks are
 * removed and line breaks are replaced by a single space character. Thus in the
 * previuos example the first key is 'R01A' and its text value is 'Als sich
 * Sabine in der N�he der Felsen aufhielt, trat sie auf eine Glasscherbe'. The
 * second key is 'R01B' and its string value is '.' which is only the period
 * character. Each key is composed of a theme code which can contain several
 * letters and a segment code which may only be a single letter. The theme code
 * is defined by the parameter <code>TextTheme</code> and the single letter
 * segment code sequence is defined by the parameter <code>TextSegments</code>.
 * Thus in order to produce a single sentence from the above examples we can
 * set:
 * 
 * <pre>
 * TextTheme = &quot;R01&quot;;
 * TextSegments = &quot;AB&quot;;
 * </pre>
 * 
 * This creates the sentence 'Als sich Sabine in der N�he der Felsen aufhielt,
 * trat sie auf eine Glasscherbe.' including the period character from the
 * second key.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see de.pxlab.util.TextCache
 */
public class TextParagraphComposed extends TextParagraph {
	/** Code for the text theme. */
	public ExPar TextTheme = new ExPar(STRING, new ExParValue("T"),
			"Text theme code");
	/**
	 * A list of text segment codes which are combined to make up the text.
	 */
	public ExPar TextSegments = new ExPar(STRING, new ExParValue("A"),
			"List of text segment codes");
	/** Path name of the file containing the text segments. */
	public ExPar TextSegmentFile = new ExPar(STRING, new ExParValue(""),
			"Name of the text segment file");

	public TextParagraphComposed() {
		setTitleAndTopic("Text segment composition", TEXT_PAR_DSP);
		FontSize.set(32.0, 6.0, 300.0);
	}
	private TextCache textCache = null;

	/*
	 * protected int create() {
	 * 
	 * s1 = enterDisplayElement(new TextParagraphElement(this.Color), group[0]);
	 * defaultTiming(0);
	 * 
	 * return(s1); }
	 */
	protected void computeGeometry() {
		String fn = TextSegmentFile.getString();
		// Check whether the text cache exists or has been changed
		if ((textCache == null) || !(textCache.getFileName().equals(fn))) {
			if (StringExt.nonEmpty(fn)) {
				textCache = new TextCache(fn);
			}
		}
		if (textCache != null) {
			StringBuffer b = new StringBuffer();
			String segments = TextSegments.getString();
			char[] segCode = new char[1];
			for (int i = 0; i < segments.length(); i++) {
				segCode[0] = segments.charAt(i);
				String key = TextTheme.getString() + new String(segCode);
				b.append(textCache.getTextSegment(key));
			}
			this.Text.set(b.toString());
		} else {
			this.Text.set(TextSegments.getString());
		}
		super.computeGeometry();
	}
}
