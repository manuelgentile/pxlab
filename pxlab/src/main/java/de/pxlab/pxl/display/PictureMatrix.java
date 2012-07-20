package de.pxlab.pxl.display;

import java.util.*;
import java.awt.Dimension;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.*;

/**
 * Shows a matrix of images. If this object's timer is a
 * MOUSE_TRACKING_RELEASE_KEY_TIMER, then button presses on any of the images
 * results in image 'selection'. Selected images are marked by a frame or are
 * replaced by selected images as named in FileNameSelected and (de-)selection
 * times are stored in the parameter SelectionTimes. The display is closed after
 * a keyboard key has been released.
 * 
 * @author H. Irtel
 * @version 0.5.0
 */
/*
 * 
 * 11/12/02
 * 
 * 2004/10/21 added SingleSelection flag
 * 
 * 2004/11/24 allow for selected images: FileNameSelected
 * 
 * 2005/08/04 added Selection parameter
 * 
 * 2005/08/05 added DefaultSelection
 * 
 * 2006/01/16 added DisableNonSelection to disable the timer as long as no
 * picture is selected.
 * 
 * 2006/02/21 enable ReferencePoint for the whole matrix
 * 
 * 2007/05/16 replaced SingleSelection by SelectionType and enabled row overlay
 * positioning
 */
public class PictureMatrix extends Picture implements SelectionTypeCodes {
	/**
	 * Alternative image file names for the selected states. These are images
	 * which may be shown during the selected state of an image object. This
	 * parameter must be empty or must have the same number of entries as the
	 * parameter FileName.
	 */
	public ExPar FileNameSelected = new ExPar(STRING, new ExParValue(""),
			"Selected image file names");
	/** Number of image columns. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(10),
			"Number of image columns");
	/** Horizontal gap between images. */
	public ExPar HorizontalGap = new ExPar(HORSCREENSIZE, new ExParValue(10),
			"Horizontal gap size");
	/** Vertical gap between images. */
	public ExPar VerticalGap = new ExPar(VERSCREENSIZE, new ExParValue(10),
			"Vertical gap size");
	/**
	 * Stores the state and times when selection events are detected. Correct
	 * computation of these timing events requires that the
	 * START_RESPONSE_TIMER_BIT has been activated for one of the timers
	 * preceding the current display element.
	 */
	public ExPar SelectionTimes = new ExPar(RTDATA, new ExParValue(""),
			"Selection times");
	/**
	 * Stores the set of selected objects as a string of the form '_0_3_6' which
	 * contains no blank characters. Objects are identified by their index in
	 * the FileName array.
	 */
	public ExPar SelectionSet = new ExPar(RTDATA, new ExParValue(""),
			"Set of selected objects as string");
	/**
	 * Stores the set of selected objects. Objects are identified by their index
	 * in the FileName array.
	 */
	public ExPar Selection = new ExPar(RTDATA, new ExParValue(""),
			"Selected object numbers");
	/** The color of the selection frame. */
	public ExPar SelectionColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)),
			"Color of the Selection Frame");
	/**
	 * Defines how selection is controlled. Defaults to allowing selection of a
	 * single element only.
	 */
	public ExPar SelectionType = new ExPar(GEOMETRY_EDITOR,
			SelectionTypeCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.SelectionTypeCodes.SINGLE_SELECTION"),
			"Selection type");
	/**
	 * If true then stopping this display is only allowed if at least one
	 * picture is selected.
	 */
	public ExPar DisableNonSelection = new ExPar(FLAG, new ExParValue(1),
			"Disable stopping without selection");

	public PictureMatrix() {
		setTitleAndTopic("Selectable picture matrix", PICTURE_DSP);
		ReferencePoint.set(new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER"));
	}
	protected int pictIdx;
	protected int teIdx;
	protected int nPics = 1;
	protected BitMapElement[] pics = new BitMapElement[1];
	/**
	 * If this is true then successive rows have the same vertical position.
	 * This is here such that subclasses can change ist.
	 */
	protected boolean rowOverlay = false;

	protected int create() {
		// By default we create a single picture element only
		pics[0] = new BitMapElement();
		pictIdx = enterDisplayElement(pics[0], group[0]);
		// We need a dummy color parameter for the display editor
		pics[0].setColorPar(ExPar.ScreenBackgroundColor);
		teIdx = defaultTiming(0);
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
		// Selection requires valid bounds
		DisplayElement.setValidBounds(true);
		DisplayElement.setSelectionColor(SelectionColor.getPxlColor().dev());
		DisplayElement.setSelectionShowType(DisplayElement.SELECTION_FRAME);
		DisplayElement.setSelectionDotSize(3);
		// Check out how many pictures we have and fix the display
		// element list if necessary
		String[] fn = FileName.getStringArray();
		int np = fn.length;
		if ((np != nPics) && (np > 0)) {
			removeDisplayElements(pictIdx);
			pics = new BitMapElement[np];
			for (int i = 0; i < np; i++) {
				pics[i] = new BitMapElement();
				pics[i].setColorPar(ExPar.ScreenBackgroundColor);
				enterDisplayElement(pics[i], group[0]);
			}
			nPics = np;
		}
		setImages();
		int hg = HorizontalGap.getInt();
		int vg = VerticalGap.getInt();
		int nc = NumberOfColumns.getInt();
		int nr = (nPics + nc - 1) / nc;
		// Get image widths and heights and compute the width of every
		// row and the height of the total matrix
		int[] wp = new int[nPics];
		int[] wr = new int[nr];
		int[] hr = new int[nr];
		int h = 0;
		int j = 0;
		for (int row = 0; j < nPics; row++) {
			wr[row] = 0;
			hr[row] = 0;
			for (int col = 0; (j < nPics) && (col < nc); col++) {
				pics[j].setReferencePoint(de.pxlab.pxl.PositionReferenceCodes.TOP_LEFT);
				pics[j].setSelected(false);
				Dimension size = pics[j].getSize();
				wp[j] = size.width;
				wr[row] += (size.width + hg);
				if (size.height > hr[row])
					hr[row] = size.height;
				j++;
			}
			wr[row] -= hg;
			h += (hr[row] + vg);
		}
		h -= vg;
		if (rowOverlay)
			h = hr[0];
		// Set the pictures and their positions relative to the top left corner
		int[] xp = new int[nPics];
		int[] yp = new int[nPics];
		j = 0;
		int y = 0;
		for (int row = 0; j < nPics; row++) {
			xp[j] = 0;
			yp[j] = y;
			j++;
			for (int col = 1; (j < nPics) && (col < nc); col++) {
				xp[j] = xp[j - 1] + wp[j - 1] + hg;
				yp[j] = y;
				j++;
			}
			if (!rowOverlay)
				y += (hr[row] + vg);
		}
		// System.out.println("PictureMatrix.computeGeometry() w = " + w +
		// ", h = " + h);
		// Compute position shift according to the reference code
		int ref = ReferencePoint.getInt();
		int dx = ref / 3;
		int dy = ref % 3;
		int[] xs = new int[nr];
		for (int row = 0; row < nr; row++) {
			// System.out.println("PictureMatrix.computeGeometry() x = " + x +
			// ", y = " + y);
			if (dx != 0) {
				int sdx = 0;
				if (dx > 0) {
					sdx = -dx * wr[row] / 2;
				}
				xs[row] = LocationX.getInt() + sdx;
			} else {
				xs[row] = LocationX.getInt();
			}
		}
		int sdy = 0;
		if (dy != 2) {
			sdy = -(2 - dy) * h / 2;
		}
		int ys = LocationY.getInt() + sdy;
		// System.out.println("PictureMatrix.computeGeometry() ReferencePoint = "
		// + ReferencePoint);
		// System.out.println("PictureMatrix.computeGeometry() dx = " + dx +
		// ", dy = " + dy);
		// System.out.println("PictureMatrix.computeGeometry() x = " + x +
		// ", y = " + y);
		// Set the absolute picture positions
		j = 0;
		for (int row = 0; j < nPics; row++) {
			for (int col = 0; (j < nPics) && (col < nc); col++) {
				pics[j].setLocation(xs[row] + xp[j], ys + yp[j]);
				// System.out.println("PictureMatrix.computeGeometry() i=" + i +
				// ", xp=" + xp[i] + ", yp=" + yp[i]);
				// System.out.println("PictureMatrix.computeGeometry() i=" + i +
				// ", x=" + (x+xp[i]) + ", y=" + (y+yp[i]));
				j++;
			}
		}
		SelectionTimes.set("");
	}

	protected void setImages() {
		String[] fn = FileName.getStringArray();
		int np = fn.length;
		if ((np != nPics) && (np > 0)) {
			removeDisplayElements(pictIdx);
			pics = new BitMapElement[np];
			for (int i = 0; i < np; i++) {
				pics[i] = new BitMapElement();
				pics[i].setColorPar(ExPar.ScreenBackgroundColor);
				enterDisplayElement(pics[i], group[0]);
			}
			nPics = np;
		}
		String[] sfn = FileNameSelected.getStringArray();
		boolean s = (sfn != null) && (sfn.length == nPics);
		for (int i = 0; i < nPics; i++) {
			pics[i].setImage(Directory.getString(), fn[i], Width.getInt(),
					Height.getInt());
			if (s)
				pics[i].setSelectedImage(Directory.getString(), sfn[i],
						Width.getInt(), Height.getInt());
		}
	}

	/**
	 * This method is called whenever the pointer button has been pressed. We
	 * figure out whether the pointer points to an actual image and then switch
	 * its selection state. This method is activated by setting the
	 * de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER_BIT flag of this object's
	 * timer.
	 */
	protected boolean pointerActivated() {
		int st = SelectionType.getInt();
		for (int i = 0; i < nPics; i++) {
			// System.out.print("Object " + i + " is ");
			BitMapElement p = pics[i];
			if ((pics[i].getTimingGroupPattern() & activeTimingGroup) != 0) {
				if (p.contains(pointerActivationX, pointerActivationY)) {
					// System.out.println("Element " + i + " contains (" +
					// pointerActivationX + "," + pointerActivationY + ")");
					int si = i;
					if (st == SINGLE_SELECTION) {
						// Single selection means that all selections must be
						// cleared before selection changes
						for (int j = 0; j < nPics; j++) {
							if (j != i) {
								pics[j].setSelected(false);
							}
						}
					} else if (st == SINGLE_IN_ROW_SELECTION) {
						// Single selection within a single image row
						int nc = NumberOfColumns.getInt();
						int m = (i / nc) * nc;
						for (int j = m; j < (m + nc); j++) {
							if (j != i) {
								pics[j].setSelected(false);
							}
						}
						si = i % nc;
					}
					p.setSelected(!p.isSelected());
					SelectionTimes.set(SelectionTimes.getString() + "__"
							+ (p.isSelected() ? "s" : "u") + "_" + si + "_"
							+ Math.round(HiresClock.ms(pointerEventTime)));
					// System.out.println("selected");
					// System.out.println("Selection state = " +
					// (pics[i].isSelected()? "On": "Off"));
				}
			}
		}
		return true;
	}

	/**
	 * Allow timer stops only if a picture in the currently active timing group
	 * is currently selected.
	 * 
	 * @return true if currently at least one picture int the currently active
	 *         timing group is selected.
	 */
	public boolean getAllowTimerStop(int rc) {
		if (DisableNonSelection.getFlag()) {
			for (int i = 0; i < nPics; i++) {
				long tgp = pics[i].getTimingGroupPattern();
				if (((tgp & activeTimingGroup) != 0) && pics[i].isSelected()) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	protected void timingGroupFinished(int group) {
		int[] slct = getSelection();
		Selection.set(slct);
		if (slct.length > 0) {
			StringBuffer ss = new StringBuffer(String.valueOf(slct[0]));
			for (int i = 1; i < slct.length; i++) {
				ss.append("_" + slct[i]);
			}
			SelectionSet.set(ss.toString());
		} else {
			SelectionSet.set("");
		}
	}

	protected int[] getSelection() {
		ArrayList s = new ArrayList(5);
		for (int i = 0; i < nPics; i++) {
			if (pics[i].isSelected()) {
				s.add(new Integer(i));
			}
		}
		int n = s.size();
		int[] k = new int[n];
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				k[i] = ((Integer) s.get(i)).intValue();
			}
		}
		return k;
	}
}
