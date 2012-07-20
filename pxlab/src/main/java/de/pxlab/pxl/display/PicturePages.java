package de.pxlab.pxl.display;

import de.pxlab.pxl.*;
import de.pxlab.util.*;
import java.awt.event.*;

/**
 * Shows a series of pictures and allows the subject to page through. Paging is
 * done in the same way as in TextFile. The sequence of pictures is given in the
 * FileName parameter.
 * 
 * @version 0.1.0
 * @see TextFile
 */
/*
 * 
 * 2006/02/27
 */
public class PicturePages extends Picture {
	public PicturePages() {
		setTitleAndTopic("Picture pages", PICTURE_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.STOP_KEY_TIMER"));
	}
	protected int pageNumber;
	protected String[] fileName;

	protected void computeGeometry() {
		BitMapElement p = (BitMapElement) getDisplayElement(pictIdx);
		p.setLocation(LocationX.getInt(), LocationY.getInt());
		p.setReferencePoint(ReferencePoint.getInt());
		fileName = FileName.getStringArray();
		pageNumber = 0;
		setPage();
	}

	protected void setPage() {
		BitMapElement p = (BitMapElement) getDisplayElement(pictIdx);
		if (StringExt.nonEmpty(fileName[pageNumber])) {
			p.setImage(Directory.getString(), fileName[pageNumber],
					Width.getInt(), Height.getInt());
		}
	}
	protected static int[] forward = { de.pxlab.pxl.KeyCodes.RIGHT_KEY,
			de.pxlab.pxl.KeyCodes.DOWN_KEY, de.pxlab.pxl.KeyCodes.PAGE_DOWN_KEY };
	protected static int[] backward = { de.pxlab.pxl.KeyCodes.LEFT_KEY,
			de.pxlab.pxl.KeyCodes.UP_KEY, de.pxlab.pxl.KeyCodes.PAGE_UP_KEY };

	protected boolean keyResponse(KeyEvent keyEvent) {
		int key = keyEvent.getKeyCode();
		if (StringExt.indexOf(key, forward) >= 0) {
			if (pageNumber < (fileName.length - 1)) {
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
