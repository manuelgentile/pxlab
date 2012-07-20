package de.pxlab.pxl;

import java.awt.*;
import java.util.ArrayList;

/**
 * A rectangular sample of color patches. The single patches may be selected by
 * subjects. Selected patches are shown as ovals while unselected patches are
 * shown as rectangles.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 11/04/03
 */
public class ColorSampleView extends DisplayElement {
	protected int rows = 0;
	protected int columns = 0;
	protected int patchWidth = 24;
	protected int patchHeight = 24;
	protected int gapSize = 8;
	protected boolean singleSelection = false;
	protected Rectangle[] patch;
	protected boolean[] selection;
	protected PxlColor[] colorTable;
	protected ExPar colorSamplePar;

	public ColorSampleView(ExPar i) {
		colorPar = i;
		type = DisplayElement.BAR_PATTERN;
		setRowsColumns(9, 9);
	}

	/**
	 * Set this color sample's geometric properties.
	 * 
	 * @param x
	 *            horizontal center position
	 * @param y
	 *            vertical center position
	 * @param rws
	 *            number of rows
	 * @param cls
	 *            number of columns
	 * @param psw
	 *            patch width
	 * @param psh
	 *            patch height
	 * @param gs
	 *            gap size
	 * @param ss
	 *            true if single selection is requested and false if multiple
	 *            selection is allowed.
	 */
	public void setProperties(int x, int y, int rws, int cls, int psw, int psh,
			int gs, boolean ss) {
		setLocation(x, y);
		setRowsColumns(rws, cls);
		patchWidth = psw;
		patchHeight = psh;
		gapSize = gs;
		singleSelection = ss;
		clearSelection();
	}

	/**
	 * Set the color sample parameter.
	 * 
	 * @param cs
	 *            this experimental parameter contains the color coordinates of
	 *            all colors in the sample to be shown.
	 */
	public void setColorSamplePar(ExPar cs) {
		colorSamplePar = cs;
	}

	public void setRowsColumns(int r, int c) {
		if ((r != rows) || (c != columns)) {
			rows = r;
			columns = c;
			int n = rows * columns;
			patch = new Rectangle[n];
			selection = new boolean[n];
			colorTable = new PxlColor[n];
			for (int i = 0; i < n; i++) {
				patch[i] = null;
				selection[i] = false;
				colorTable[i] = ExPar.ScreenBackgroundColor.getPxlColor();
			}
		}
	}

	public void show() {
		double[] cd = colorSamplePar.getDoubleArray();
		int nc = cd.length / 3;
		int k = 0;
		int n = rows * columns;
		for (int i = 0; (i < nc) && (i < n); i++) {
			colorTable[i] = new YxyColor(cd[k], cd[k + 1], cd[k + 2]);
			k += 3;
		}
		int left_x = -(columns * patchWidth + (columns - 1) * gapSize) / 2
				+ location.x;
		int top_y = -(rows * patchHeight + (rows - 1) * gapSize) / 2
				+ location.y;
		int x;
		int y = top_y;
		int dsx = patchWidth + gapSize;
		int dsy = patchHeight + gapSize;
		k = 0;
		for (int i = 0; i < rows; i++) {
			x = left_x;
			for (int j = 0; j < columns; j++) {
				PxlColor c = colorTable[k];
				if (c.isDisplayable()) {
					patch[k] = new Rectangle(x, y, patchWidth, patchHeight);
					graphics.setColor(c.dev());
					if (selection[k]) {
						graphics.fillOval(x, y, patchWidth, patchHeight);
					} else {
						graphics.fillRect(x, y, patchWidth, patchHeight);
					}
				} else {
					patch[k] = null;
				}
				k++;
				x += dsx;
			}
			y += dsy;
		}
		setBounds(left_x, top_y,
				columns * patchWidth + (columns - 1) * gapSize, rows
						* patchHeight + (rows - 1) * gapSize);
	}

	/** Clear the selection set. */
	public void clearSelection() {
		int n = rows * columns;
		for (int i = 0; i < n; i++) {
			selection[i] = false;
		}
	}

	/**
	 * Shift the selection positions.
	 * 
	 * @param da
	 *            number of rows to shift
	 * @param db
	 *            number of columns to shift.
	 */
	public void shiftSelection(int da, int db) {
		int n = rows * columns;
		boolean[] ns = new boolean[n];
		for (int i = 0; i < n; i++)
			ns[i] = false;
		int k = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (selection[k]) {
					int ni = i + db;
					int nj = j + da;
					if ((ni >= 0) && (ni < rows) && (nj >= 0) && (nj < columns)) {
						ns[ni * columns + nj] = true;
					}
				}
				k++;
			}
		}
		for (int i = 0; i < n; i++)
			selection[i] = ns[i];
	}

	/**
	 * Switch the selection state of the color patch at the given position. No
	 * action is taken if the selected position does not show a displayable
	 * color.
	 * 
	 * @param x
	 *            the horizontal pixel coordinate
	 * @param y
	 *            the vertical pixel coordinate of the patch to be selected.
	 */
	public void select(int x, int y) {
		if (this.contains(x, y)) {
			int n = rows * columns;
			int idx = -1;
			for (int i = 0; i < n; i++) {
				if ((patch[i] != null) && patch[i].contains(x, y)) {
					idx = i;
					break;
				}
			}
			if (idx >= 0) {
				PxlColor c = colorTable[idx];
				if (c.isDisplayable()) {
					for (int i = 0; i < n; i++) {
						if (i == idx) {
							selection[i] = !selection[i];
						} else {
							if (singleSelection) {
								selection[i] = false;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Get the selected colors.
	 * 
	 * @return a string which contains the coordinates of all selected colors.
	 */
	public String getSelection() {
		StringBuffer b = new StringBuffer(100);
		int n = rows * columns;
		for (int i = 0; i < n; i++) {
			if (selection[i]) {
				b.append(" " + colorTable[i].toString());
			} else {
			}
		}
		return (b.toString());
	}

	/**
	 * Get the single selected color.
	 * 
	 * @return a double array which contains the XYZ-coordinates of the selected
	 *         color or null if no color is selected.
	 */
	public double[] getSingleSelection() {
		int n = rows * columns;
		for (int i = 0; i < n; i++) {
			if (selection[i]) {
				return colorTable[i].getComponents();
			}
		}
		return null;
	}

	/**
	 * Get the selected colors.
	 * 
	 * @return an array of integer index values which point to the selected
	 *         colors.
	 */
	public int[] getSelectionIndex() {
		int n = rows * columns;
		ArrayList sil = new ArrayList(n);
		for (int i = 0; i < n; i++) {
			if (selection[i]) {
				sil.add(new Integer(i));
			} else {
			}
		}
		int m = sil.size();
		int[] si = new int[m];
		for (int i = 0; i < m; i++)
			si[i] = ((Integer) (sil.get(i))).intValue();
		return si;
	}

	/**
	 * Get the pattern of selected colors.
	 * 
	 * @return a string which contains '1' for every selected color and '0'
	 *         otherwise.
	 */
	public String getSelectionBinPattern() {
		int n = rows * columns;
		byte[] b = new byte[n + n];
		for (int i = 0; i < n; i++) {
			b[i + i] = ' ';
			b[i + i + 1] = (byte) (selection[i] ? '1' : '0');
		}
		return new String(b);
	}

	/**
	 * Get the selected color.
	 * 
	 * @return the coordinates of the selected color.
	 */
	public PxlColor getSelectedColor() {
		PxlColor c = null;
		int n = rows * columns;
		for (int i = 0; i < n; i++) {
			if (selection[i]) {
				c = colorTable[i];
				break;
			}
		}
		return c;
	}
}
