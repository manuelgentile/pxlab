package de.pxlab.pxl;

import java.awt.*;
import java.util.ArrayList;

/**
 * A circular arrangement of color patches. The single patches may be selected
 * by subjects. Selected patches are magnified.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 12/03/03
 */
public class ColorCircleView extends DisplayElement {
	protected int numberOfPatches = 0;
	protected int patchSize = 24;
	protected int circleSize = 400;
	protected boolean singleSelection = true;
	protected double positionOne = Math.PI / 2.0;
	protected Rectangle[] patch;
	protected boolean[] selection;
	protected PxlColor[] colorTable;
	protected int colorSpace = PxlColor.CS_Yxy;
	protected ExPar colorSamplePar;

	/**
	 * Create a circular color patch view.
	 * 
	 * @param i
	 *            this argument is here for compatibility only. its value is
	 *            ignored.
	 */
	public ColorCircleView(ExPar i) {
		colorPar = i;
		type = DisplayElement.BAR_PATTERN;
		setNumberOfPatches(16);
	}

	/**
	 * Set this color circle's geometric properties.
	 * 
	 * @param x
	 *            horizontal center position
	 * @param y
	 *            vertical center position
	 * @param n
	 *            number of patches
	 * @param cs
	 *            circle diameter
	 * @param p1
	 *            angular position of the first patch (degrees)
	 * @param ps
	 *            patch diameter
	 * @param ss
	 *            true if single selection is requested and false if multiple
	 *            selection is allowed.
	 */
	public void setProperties(int x, int y, int n, int cs, int ps, int p1,
			boolean ss) {
		setLocation(x, y);
		setNumberOfPatches(n);
		circleSize = cs;
		patchSize = ps;
		positionOne = p1 / 180.0 * Math.PI;
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

	/**
	 * Set the color space of the color sample parameter.
	 * 
	 * @param cs
	 *            the color space used for the coordindates in the color sample
	 *            parameter.
	 */
	public void setColorSpace(int cs) {
		colorSpace = cs;
	}

	/**
	 * Set the number of color patches to be used.
	 * 
	 * @param n
	 *            number of color patches.
	 */
	public void setNumberOfPatches(int n) {
		if (n != numberOfPatches) {
			numberOfPatches = n;
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
		double ccd[] = new double[3];
		for (int i = 0; (i < nc) && (i < numberOfPatches); i++) {
			System.arraycopy(cd, k, ccd, 0, 3);
			colorTable[i] = PxlColor.instance(colorSpace, ccd);
			k += 3;
		}
		int x, xx;
		int y, yy;
		int d = patchSize / 20;
		PxlColor bgc = ExPar.ScreenBackgroundColor.getPxlColor();
		PxlColor c;
		double a = positionOne;
		double aStep = 2.0 * Math.PI / (double) (numberOfPatches);
		double radius = circleSize / 2.0;
		k = 0;
		for (int i = 0; i < numberOfPatches; i++) {
			c = (colorTable[k] != null) ? colorTable[k] : bgc;
			if (colorTable[k] != null) {
				xx = (int) (location.x - radius * Math.cos(a));
				yy = (int) (location.y - radius * Math.sin(a));
				x = xx - patchSize / 2;
				y = yy - patchSize / 2;
				patch[k] = new Rectangle(x, y, patchSize, patchSize);
				graphics2D.setColor(c.dev());
				if (selection[k]) {
					graphics2D.fillOval(x - d, y - d, patchSize + d + d,
							patchSize + d + d);
				} else {
					graphics2D.fillOval(x, y, patchSize, patchSize);
				}
			} else {
				patch[k] = null;
			}
			k++;
			a += aStep;
		}
		setBounds(location.x - (circleSize + patchSize) / 2, location.y
				- (circleSize + patchSize) / 2, circleSize + patchSize,
				circleSize + patchSize);
	}

	/** Clear the selection set. */
	public void clearSelection() {
		for (int i = 0; i < numberOfPatches; i++) {
			selection[i] = false;
		}
	}

	/**
	 * Switch the selection state of the color patch at the given position.
	 * 
	 * @param x
	 *            the horizontal pixel coordinate
	 * @param y
	 *            the vertical pixel coordinate of the patch to be selected.
	 */
	public void select(int x, int y) {
		// System.out.println("ColorCircleView.select() x="+x+", y="+y);
		if (this.contains(x, y)) {
			for (int i = 0; i < numberOfPatches; i++) {
				if (patch[i] != null) {
					// System.out.println("  i="+i+": " + patch[i]);
					if (patch[i].contains(x, y)) {
						selection[i] = !selection[i];
						// System.out.println("  selected!");
					} else {
						if (singleSelection) {
							selection[i] = false;
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
		for (int i = 0; i < numberOfPatches; i++) {
			if (selection[i]) {
				b.append(" " + colorTable[i].toString());
			} else {
			}
		}
		return (b.toString());
	}

	/**
	 * Get the selected colors.
	 * 
	 * @return an array of integer index values which point to the selected
	 *         colors.
	 */
	public int[] getSelectionIndex() {
		ArrayList sil = new ArrayList(numberOfPatches);
		for (int i = 0; i < numberOfPatches; i++) {
			if (selection[i]) {
				sil.add(new Integer(i));
			} else {
			}
		}
		int m = sil.size();
		if (m == 0)
			return null;
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
		byte[] b = new byte[2 * numberOfPatches];
		for (int i = 0; i < numberOfPatches; i++) {
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
		for (int i = 0; i < numberOfPatches; i++) {
			if (selection[i]) {
				c = colorTable[i];
				break;
			}
		}
		return c;
	}

	/**
	 * Get the index of the selected color.
	 * 
	 * @return the index of the selected color or -1 if none is selected.
	 */
	public int getSelectedColorIndex() {
		for (int i = 0; i < numberOfPatches; i++) {
			if (selection[i]) {
				return i;
			}
		}
		return -1;
	}
}
