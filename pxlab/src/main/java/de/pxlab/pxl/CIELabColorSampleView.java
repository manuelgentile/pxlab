package de.pxlab.pxl;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A sample of color patches which contain a view onto a CIELab plane of
 * constant lightness or contains a view onto a maximum chroma sheet of the
 * CIELab space. The single patches may be selected by subjects.
 * 
 * <p>
 * Keyboard Navigation: If a STOP_KEY_TIMER is used, then keyboard navigation in
 * the CIELab plane is possible. The cursor left/right/up/down keys move the
 * center color within a plane of constant lightness. The page up/down keys
 * change the lightness level.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 11/04/03
 */
public class CIELabColorSampleView extends ColorSampleView {
	protected double cieLabChromaticityStep = 10.0;
	protected double cieLabHueAngleStep = 10.0;
	protected double cieLabLightnessStep = 3.0;
	public static final int CONSTANT_LIGHTNESS_PLANE = 0;
	public static final int CONSTANT_CHROMA_PLANE = 1;
	public static final int CONSTANT_HUE_PLANE = 2;
	public static final int CONSTANT_SATURATION_PLANE = 3;
	public static final int MAXIMUM_CHROMA_PLANE = 4;
	protected int planeType = CONSTANT_LIGHTNESS_PLANE;

	public CIELabColorSampleView(ExPar i) {
		super(i);
	}

	public CIELabColorSampleView(ExPar i, int m) {
		super(i);
		setPlaneType(m);
	}

	public void setPlaneType(int m) {
		if ((m == CONSTANT_LIGHTNESS_PLANE) || (m == MAXIMUM_CHROMA_PLANE))
			planeType = m;
	}

	public void setCIELabChromaticityStep(double cs) {
		cieLabChromaticityStep = cs;
	}

	public void setCIELabHueAngleStep(double cs) {
		cieLabHueAngleStep = cs;
	}

	public void setCIELabLightnessStep(double cs) {
		cieLabLightnessStep = cs;
	}

	public void show() {
		// System.out.println("CIELabColorSampleView.show(): colorPar = " +
		// colorPar.getPxlColor());
		if (planeType == CONSTANT_LIGHTNESS_PLANE)
			CIELabStar.constantLightnessPlane(colorPar.getPxlColor(),
					cieLabChromaticityStep, cieLabChromaticityStep, columns,
					rows, colorTable);
		else if (planeType == MAXIMUM_CHROMA_PLANE)
			CIELabStar.maximumChromaPlane(colorPar.getPxlColor(),
					cieLabHueAngleStep, cieLabLightnessStep, columns, rows,
					colorTable);
		else
			CIELabStar.constantLightnessPlane(colorPar.getPxlColor(),
					cieLabChromaticityStep, cieLabChromaticityStep, columns,
					rows, colorTable);
		int left_x = -(columns * patchWidth + (columns - 1) * gapSize) / 2
				+ location.x;
		int top_y = -(rows * patchHeight + (rows - 1) * gapSize) / 2
				+ location.y;
		int x;
		int y = top_y;
		int dsx = patchWidth + gapSize;
		int dsy = patchHeight + gapSize;
		PxlColor bgc = ExPar.ScreenBackgroundColor.getPxlColor();
		PxlColor c;
		int k = 0;
		for (int i = 0; i < rows; i++) {
			x = left_x;
			for (int j = 0; j < columns; j++) {
				c = (colorTable[k] != null) ? colorTable[k] : bgc;
				if (colorTable[k] != null) {
					patch[k] = new Rectangle(x, y, patchWidth, patchHeight);
				} else {
					patch[k] = null;
				}
				// Its necessary to always paint the patch since
				// otherwise the view does not work for overlays
				graphics.setColor(c.dev());
				if (selection[k]) {
					graphics.fillOval(x, y, patchWidth, patchHeight);
				} else {
					graphics.fillRect(x, y, patchWidth, patchHeight);
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
	protected static NumberFormat lightnessValue = NumberFormat
			.getInstance(Locale.US);
	protected static NumberFormat chromaticityCoordinate = NumberFormat
			.getInstance(Locale.US);
	static {
		lightnessValue.setMaximumFractionDigits(3);
		lightnessValue.setGroupingUsed(false);
		chromaticityCoordinate.setMaximumFractionDigits(2);
		chromaticityCoordinate.setGroupingUsed(false);
	}

	/**
	 * Get the selected colors.
	 * 
	 * @return a string which contains the CIELab coordinates of all selected
	 *         colors.
	 */
	public String getSelection() {
		StringBuffer b = new StringBuffer(100);
		int n = rows * columns;
		for (int i = 0; i < n; i++) {
			if (selection[i]) {
				double[] c = colorTable[i].toLabStar();
				String cs = " [" + lightnessValue.format(c[0]) + ","
						+ chromaticityCoordinate.format(c[1]) + ","
						+ chromaticityCoordinate.format(c[2]) + "]";
				b.append(cs);
			} else {
			}
		}
		return (b.toString());
	}

	/**
	 * Get the selected color.
	 * 
	 * @return an array which contains the CIELab coordinates of the selected
	 *         color or null if none is selected.
	 */
	public double[] getSingleSelection() {
		double[] a = super.getSingleSelection();
		if (a != null) {
			PxlColor sc = new PxlColor(a);
			a = sc.toLabStar();
		}
		return a;
	}

	public void adjustCenterColor(int key) {
		double[] cc = colorPar.getPxlColor().transform(PxlColor.CS_LabStar);
		// System.out.println("CIELabColorSampleView.adjustCenterColor(): in = "
		// + colorPar.getPxlColor());
		// System.out.println("CIELabColorSampleView.adjustCenterColor(): in = ["
		// + cc[0] + "," + cc[1] + "," + cc[2] + "]");
		switch (key) {
		case KeyCodes.LEFT_KEY:
			cc[1] += cieLabChromaticityStep;
			shiftSelection(-1, 0);
			break;
		case KeyCodes.RIGHT_KEY:
			cc[1] -= cieLabChromaticityStep;
			shiftSelection(1, 0);
			break;
		case KeyCodes.UP_KEY:
			cc[2] -= cieLabChromaticityStep;
			shiftSelection(0, -1);
			break;
		case KeyCodes.DOWN_KEY:
			cc[2] += cieLabChromaticityStep;
			shiftSelection(0, 1);
			break;
		case KeyCodes.PAGE_UP_KEY:
			if ((cc[0] + cieLabLightnessStep) < 100.0) {
				cc[0] += cieLabLightnessStep;
				clearSelection();
			} else {
				return;
			}
			break;
		case KeyCodes.PAGE_DOWN_KEY:
			if ((cc[0] - cieLabLightnessStep) > 0.0) {
				cc[0] -= cieLabLightnessStep;
				clearSelection();
			} else {
				return;
			}
			break;
		}
		// System.out.println("CIELabColorSampleView.adjustCenterColor(): out = ["
		// + cc[0] + "," + cc[1] + "," + cc[2] + "]");
		colorPar.set(PxlColor.instance(PxlColor.CS_LabStar, cc));
		// System.out.println("CIELabColorSampleView.adjustCenterColor(): out = "
		// + colorPar.getPxlColor());
	}
}
