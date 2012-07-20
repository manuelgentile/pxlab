package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Shows a constant lightness plane in CIELAB space and allows for selection of
 * a subset of color patches.
 * 
 * @version 0.1.2
 * @see CIELabSelection
 */
/*
 * 
 * 11/03/03
 */
public class CIELabPlane extends Display {
	public ExPar CenterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Plane Center Color");
	/** Lightness (L*) level of the CIELAB plane. */
	public ExPar LightnessLevel = new ExPar(0.0, 200.0, new ExParValue(60.0),
			"Lightness Level of CIELab Plane");
	/** a*-coordinate of the plane's center hue. */
	public ExPar CenterHueA = new ExPar(-200, 200, new ExParValue(0),
			"CIELab Plane Center Hue a*-coordinate");
	/** b*-coordinate of the plane's center hue. */
	public ExPar CenterHueB = new ExPar(-200, 200, new ExParValue(0),
			"CIELab Plane Center Hue b*-coordinate");
	public ExPar ChromaStep = new ExPar(SMALL_INT, new ExParValue(10),
			"Chroma Step Between Patches");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Center Position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Center Position");
	/** Number of patch columns. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(9),
			"Number of Patches in a Row");
	/** Number of patch columns. */
	public ExPar NumberOfRows = new ExPar(SMALL_INT, new ExParValue(9),
			"Number of Patches in a Column");
	/** Color patch width. */
	public ExPar PatchWidth = new ExPar(HORSCREENSIZE, new ExParValue(32),
			"Color Patch Width");
	/** Color patch height. */
	public ExPar PatchHeight = new ExPar(VERSCREENSIZE, new ExParValue(32),
			"Color Patch Height");
	/** Horizontal gap between patches. */
	public ExPar HorizontalGap = new ExPar(HORSCREENSIZE, new ExParValue(12),
			"Horizontal Gap Size");
	/** Vertical gap between patches. */
	public ExPar VerticalGap = new ExPar(VERSCREENSIZE, new ExParValue(12),
			"Vertical Gap Size");
	/** If true then only a single object may be selected. */
	public ExPar SingleSelection = new ExPar(FLAG, new ExParValue(0),
			"Single Selection Flag");
	/** Stores the set of selected CIELab values. */
	public ExPar SelectionSet = new ExPar(RTDATA, new ExParValue(""),
			"Set of Selected Colors");

	public CIELabPlane() {
		setTitleAndTopic("CIELAB Plane", COLOR_SPACES_DSP | DEMO);
	}
	private int firstIndex;
	private int nPatches = 0;
	private Patch[] patch;
	private ExPar[] colorTable;
	private int bg;

	/** Initialize the display list of the demo. */
	protected int create() {
		bg = enterDisplayElement(new Bar(ExPar.ScreenBackgroundColor), group[0]);
		firstIndex = nextDisplayElementIndex();
		recreatePatches();
		defaultTiming(0);
		return (backgroundFieldIndex);
	}

	protected void computeColors() {
		double[] lc = new double[3];
		lc[0] = LightnessLevel.getDouble();
		lc[1] = CenterHueA.getDouble();
		lc[2] = CenterHueB.getDouble();
		PxlColor cc = PxlColor.fromLabStar(lc);
		PxlColor cc2 = CenterColor.getPxlColor();
		if (!cc.equals(cc2)) {
			double[] ccn = cc2.toLabStar();
			LightnessLevel.set(ccn[0]);
			CenterHueA.set(ccn[1]);
			CenterHueB.set(ccn[2]);
		}
		computeGeometry();
	}

	protected void computeGeometry() {
		// Create the display element list
		recreatePatches();
		int nc = NumberOfColumns.getInt();
		int nr = NumberOfRows.getInt();
		int w = PatchWidth.getInt();
		int h = PatchHeight.getInt();
		int gw = HorizontalGap.getInt();
		int gh = VerticalGap.getInt();
		int left_x = -(nc * w + (nc - 1) * gw) / 2 + LocationX.getInt();
		int top_y = -(nr * h + (nr - 1) * gh) / 2 + LocationY.getInt();
		int x;
		int y = top_y;
		int k = 0;
		double chrs = ChromaStep.getDouble();
		double a = 0.0, b = 0.0, a_left = 0.0, b_top = 0.0;
		double[] Lab = new double[3];
		Lab[0] = LightnessLevel.getDouble();
		a_left = CenterHueA.getDouble()
				- ((nc / 2) - (((nc % 2) == 0) ? 0.5 : 0.0)) * chrs;
		b_top = CenterHueB.getDouble()
				+ ((nr / 2) - (((nr % 2) == 0) ? 0.5 : 0.0)) * chrs;
		b = b_top;
		for (int i = 0; i < nr; i++) {
			x = left_x;
			a = a_left;
			for (int j = 0; j < nc; j++) {
				patch[k].setRect(x, y, w, h);
				Lab[1] = a;
				Lab[2] = b;
				PxlColor c = PxlColor.fromLabStar(Lab);
				if (c.isDisplayable()) {
					colorTable[k].set(c);
				} else {
					colorTable[k]
							.set(ExPar.ScreenBackgroundColor.getPxlColor());
				}
				// System.out.println(colorTable[k]);
				k++;
				x += (w + gw);
				a += chrs;
			}
			y += (h + gh);
			b -= chrs;
		}
		getDisplayElement(bg).setRect(left_x, top_y, nc * w + (nc - 1) * gw,
				nr * h + (nr - 1) * gh);
	}

	private void recreatePatches() {
		int n = NumberOfRows.getInt() * NumberOfColumns.getInt();
		// Recreate patches only if necessary
		if ((n > 0) && (n != nPatches)) {
			removeDisplayElements(firstIndex);
			nPatches = n;
			patch = new Patch[nPatches];
			colorTable = new ExPar[nPatches];
			for (int i = 0; i < nPatches; i++) {
				colorTable[i] = new ExPar(DEPCOLOR, new ExParValue(
						new PxlColor()), null);
				patch[i] = new Patch(colorTable[i]);
				patch[i].setSelected(false);
				enterDisplayElement(patch[i], group[0]);
			}
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
		for (int i = 0; i < nPatches; i++) {
			// System.out.print("Object " + i + " is ");
			Patch p = patch[i];
			if (p.contains(pointerActivationX, pointerActivationY)) {
				p.setSelected(!p.isSelected());
			} else {
				if (SingleSelection.getFlag()) {
					p.setSelected(false);
				}
			}
			// System.out.println("selected");
			// System.out.println("Selection state = " + (pics[i].isSelected()?
			// "On": "Off"));
		}
		return true;
	}

	/**
	 * This method is called when the display of the currently active timing
	 * group has been finished.
	 * 
	 * @param group
	 *            the index of the timing group which has been run.
	 */
	protected void timingGroupFinished(int group) {
		StringBuffer b = new StringBuffer(100);
		for (int i = 0; i < nPatches; i++) {
			if (patch[i].isSelected()) {
				double[] c = colorTable[i].getPxlColor().toLabStar();
				String cs = " [" + c[0] + "," + Math.round(c[1]) + ","
						+ Math.round(c[2]) + "]";
				b.append(cs);
			} else {
			}
		}
		SelectionSet.set(b.toString());
		// System.out.println(b);
	}
}
