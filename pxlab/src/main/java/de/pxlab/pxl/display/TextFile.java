package de.pxlab.pxl.display;

import java.awt.event.*;
import de.pxlab.pxl.*;
import de.pxlab.util.StringExt;

/**
 * Shows the content of a text file and allows paging by keyboard responses. The
 * file may contain arbitrary text. Pages are separatet by the PageSeparator
 * string at the beginnig of an input line. The rest of this line is igored.
 * Empty lines at the beginning of a page also are ignored. Automatic line
 * wrapping is on but line breaks in the input file are preserved.
 * 
 * <p>
 * Paging is achieved by setting the Timer to
 * de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER, which is the default and using the
 * cursor right/down or the page down key for paging forward and the cursor
 * left/up or the page up key for paging backwards. Paging is stopped by the
 * current StopKey.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/02/26
 */
public class TextFile extends TextParagraph {
	/**
	 * Text file directory path name.
	 * 
	 * @see de.pxlab.pxl.FileBase
	 */
	public ExPar Directory = new ExPar(STRING, new ExParValue("."),
			"Text file directory path");
	/**
	 * Text file name.
	 * 
	 * @see de.pxlab.pxl.FileBase
	 */
	public ExPar FileName = new ExPar(STRING, new ExParValue(""),
			"Text file name");
	/**
	 * If this string is found at the beginnig of a line in the input file the
	 * it is used as a new page indicator. The rest of this line is ignored.
	 */
	public ExPar PageSeparator = new ExPar(STRING, new ExParValue("@"),
			"Page separator string");

	public TextFile() {
		setTitleAndTopic("Text file pages", TEXT_PAR_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER"));
		ReferencePoint.set(new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.TOP_LEFT"));
		LocationX.getValue().set(new ExParValueNotSet());
		LocationY.getValue().set(new ExParValueNotSet());
	}
	protected String[] text;
	protected int[] pageIndex;
	protected int pageNumber = 0;
	protected String[] page;

	protected void computeGeometry() {
		text = FileBase
				.loadStrings(Directory.getString(), FileName.getString());
		pageIndex = StringExt.getPageIndex(text, PageSeparator.getString());
		/*
		 * System.out.println("StringExt.getPageIndex():"); for (int j = 0; j <
		 * pageIndex.length; j += 2) System.out.println("  " + pageIndex[j] +
		 * " " + pageIndex[j+1]);
		 */
		pageNumber = 0;
		if (LocationX.getValue().isNotSet()) {
			// LocationX = -Session.TextFile.Width/2;
			LocationX.getValue().set(
					new ExParValue(new ExParExpression(ExParExpression.NEG_OP),
							new ExParValue(new ExParExpression(
									ExParExpression.DIV_OP), new ExParValueVar(
									getInstanceName() + ".Width"),
									new ExParValue(2))));
		}
		if (LocationY.getValue().isNotSet()) {
			// LocationY = screenTop() + Session.TextFile.FontSize*4;
			LocationY.getValue().set(
					new ExParValue(new ExParExpression(ExParExpression.ADD_OP),
							new ExParValue(new ExParExpression(
									ExParExpression.SCREEN_TOP)),
							new ExParValue(new ExParExpression(
									ExParExpression.MUL_OP), new ExParValueVar(
									getInstanceName() + ".FontSize"),
									new ExParValue(4))));
		}
		setPage();
	}

	protected boolean setPage() {
		boolean r = true;
		int p2 = pageNumber + pageNumber;
		if (pageIndex.length >= (p2 + 2)) {
			int first = pageIndex[p2];
			int last = pageIndex[p2 + 1];
			page = new String[last - first + 1];
			int j = 0;
			for (int i = first; i <= last; i++) {
				page[j++] = text[i];
			}
			double w = Width.getDouble();
			textpar.setProperties(page, FontFamily.getString(),
					FontStyle.getInt(), FontSize.getInt(), LocationX.getInt(),
					LocationY.getInt(),
					(w < 1.0) ? (int) (width * w) : (int) w,
					ReferencePoint.getInt(), Alignment.getInt(),
					EmphasizeFirstLine.getFlag() && (pageNumber == 0),
					Wrapping.getFlag(), LineSkipFactor.getDouble());
		} else {
			r = false;
		}
		return r;
	}
	protected static int[] forward = { de.pxlab.pxl.KeyCodes.RIGHT_KEY,
			de.pxlab.pxl.KeyCodes.DOWN_KEY, de.pxlab.pxl.KeyCodes.PAGE_DOWN_KEY };
	protected static int[] backward = { de.pxlab.pxl.KeyCodes.LEFT_KEY,
			de.pxlab.pxl.KeyCodes.UP_KEY, de.pxlab.pxl.KeyCodes.PAGE_UP_KEY };

	protected boolean keyResponse(KeyEvent keyEvent) {
		int key = keyEvent.getKeyCode();
		if (StringExt.indexOf(key, forward) >= 0) {
			if (pageNumber < (pageIndex.length / 2 - 1)) {
				pageNumber++;
			}
		} else if (StringExt.indexOf(key, backward) >= 0) {
			if (pageNumber > 0) {
				pageNumber--;
			}
		}
		// System.out.println("TextFile.keyResponse(): key = " + key +
		// " pageNumber = " + pageNumber);
		setPage();
		return true;
	}
}
