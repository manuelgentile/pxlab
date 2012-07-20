package de.pxlab.pxl.display;

import java.awt.*;
import java.util.Random;
import de.pxlab.util.StringExt;
import de.pxlab.pxl.*;

// import de.pxlab.util.Randomizer;
/**
 * An abstract superclass for objects which have an adjustable color. Color
 * adjustment is done by transforming a tracking response into color coordinate
 * changes. Computation is done using the HSB coordinate system.
 * 
 * <p>
 * Adjustment devices may be the mouse, a gamepad or joystick or a SpaceMouse
 * decice.
 * 
 * <p>
 * Moving the mouse left/right while the left button is being pressed changes
 * the hue.
 * 
 * <p>
 * Moving the mouse up/down changes brightness or saturation depending on
 * whether the left or the right mouse button is down.
 * 
 * <p>
 * A key press on the keyboard should be used to finish adjustment.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2004/11/23 created HSB version
 */
abstract public class ColorAdjustableHSB extends Display implements
		ColorAdjustBitCodes {
	/**
	 * The runtime instance name of the color parameter which should be
	 * adjusted.
	 */
	public ExPar AdjustableColorPar = new ExPar(EXPARNAME, new ExParValue(
			"ScreenBackgroundColor"), "Name of the Adjustable Color Parameter");
	/** Allows the selection of single coordinates for adjustment. */
	public ExPar AdjustableCoordinates = new ExPar(GEOMETRY_EDITOR,
			ColorAdjustBitCodes.class,
			new ExParValue(LIGHTNESS | HUE | CHROMA), "Adjustable coordinates");
	/**
	 * Axes used for adjustment if it is done by an external device which may
	 * have more than a single axis. The entries MUST be meaningful for the
	 * currently used device. There must be exactly 3 entries for HSB. If one of
	 * the entries is (-1) then this coordinate is not adjusted.
	 */
	public ExPar AxesUsed = new ExPar(SMALL_INT, new ExParValue(0, 1, 2),
			"External device axes used");
	/**
	 * One pixel mouse motion corresponds to this amount of change in CIELAB L*
	 * value.
	 */
	public ExPar LightnessStep = new ExPar(PROPORT, new ExParValue(0.001),
			"CIELab L*-Step per Screen Pixel");
	/**
	 * One pixel mouse motion corresponds to this amount of change in CIELAB
	 * hue. Note that hue angles are computed in radians here.
	 */
	public ExPar HueStep = new ExPar(PROPORT, new ExParValue(0.001),
			"CIELab Hue Radiant Angle Step per Screen Pixel");
	/**
	 * One pixel mouse motion corresponds to this amount of change in CIELAB
	 * chroma.
	 */
	public ExPar ChromaStep = new ExPar(PROPORT, new ExParValue(0.001),
			"CIELab Chroma Step per Screen Pixel");
	/**
	 * If nonzero then this parameter creates an inital randomization of the
	 * adjustable color. The parameter value x means that the initial color
	 * value is moved +- x steps from the given parameter value.
	 */
	public ExPar InitialRandomSteps = new ExPar(SMALL_INT, new ExParValue(0),
			"Random Range of Initial Color Value");
	private ExPar adjustableColorPar;
	private double h_step;
	private double s_step;
	private double b_step;
	private double min_h = 0.000001;
	private double max_h = 1.0;
	private double min_s = 0.000001;
	private double max_s = 1.0;
	private double min_b = 0.000001;
	private double max_b = 1.0;
	private Random rnd = new Random();
	private int[] ax;

	protected void computeColors() {
		// System.out.println("ColorAdjustableHSB.computeColors()");
		// Create parameters for this display
		// System.out.println("ColorAdjustableHSB.computeColors() AdjustableColorPar = "
		// + AdjustableColorPar.getString());
		adjustableColorPar = ExPar.get(AdjustableColorPar.getString());
		int selection = AdjustableCoordinates.getInt();
		h_step = ((selection & HUE) != 0) ? HueStep.getDouble() : 0.0;
		s_step = ((selection & CHROMA) != 0) ? ChromaStep.getDouble() : 0.0;
		b_step = ((selection & LIGHTNESS) != 0) ? LightnessStep.getDouble()
				: 0.0;
		// System.out.println("ColorAdjustableHSB.computeColors(): steps = [" +
		// h_step + "," + s_step + "," + b_step + "]");
		ax = AxesUsed.getIntArray();
		// Create randomized initial color value if requested
		int rn = InitialRandomSteps.getInt();
		if (rn > 0) {
			PxlColor vr = new PxlColor(new Color(rnd.nextInt(0x1000000)));
			adjustableColorPar.set(vr);
			// System.out.println("ColorAdjustableHSB.computeColors(): random start value = "
			// + new YxyColor(vr));
		}
	}
	private double h;
	private double s;
	private double b;
	// Last position used for computation
	private int pointerRecentX, pointerRecentY;

	private void setHSB() {
		ExParValue v = adjustableColorPar.getValue();
		PxlColor p = v.getPxlColor();
		Color c = adjustableColorPar.getValue().getPxlColor().dev();
		float[] hsb = c.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		h = hsb[0];
		s = hsb[1];
		b = hsb[2];
		// System.out.println("ColorAdjustableHSB.setHSB(): [" + h + "," + s +
		// "," + b + "]");
	}

	protected boolean axisDeltasChanged() {
		// System.out.println("ColorAdjustableHSB.axisDeltasChanged()");
		setHSB();
		// System.out.println("  axisDeltas = " +
		// StringExt.valueOf(axisDeltas));
		// System.out.println("          ax = " + StringExt.valueOf(ax));
		if (axisDeltas.length >= 3 && ax.length == 3) {
			adjust((ax[0] < 0) ? 0.0 : axisDeltas[ax[0]], (ax[1] < 0) ? 0.0
					: axisDeltas[ax[1]], (ax[2] < 0) ? 0.0 : axisDeltas[ax[2]]);
			adjustableColorPar.set(new PxlColor(Color.getHSBColor((float) h,
					(float) s, (float) b)));
		} else if (axisDeltas.length == 1) {
			if (h_step > 0.0)
				adjust(axisDeltas[0], 0.0, 0.0);
			else if (s_step > 0.0)
				adjust(0.0, axisDeltas[0], 0.0);
			else if (b_step > 0.0)
				adjust(0.0, 0.0, axisDeltas[0]);
			adjustableColorPar.set(new PxlColor(Color.getHSBColor((float) h,
					(float) s, (float) b)));
		}
		return true;
	}
	private double stepSizeScale = 1.0;

	protected void adjust(double dh, double ds, double db) {
		/*
		 * System.out.println("ColorAdjustableHSB.adjust()");
		 * System.out.println("    hsb = [" + h + ", " + s + ", " + b + "]");
		 * System.out.println("      d = [" + dh + ", " + ds + ", " + db + "]");
		 * System.out.println("  steps = [" + h_step + "," + s_step + "," +
		 * b_step + "]");
		 */
		// System.out.println("ColorAdjustableHSB.adjust(): stepSizeScale = " +
		// stepSizeScale);
		double hs = dh * stepSizeScale * h_step;
		double ss = ds * stepSizeScale * s_step;
		double bs = db * stepSizeScale * b_step;
		// System.out.println("  steps = [" + hs + "," + ss + "," + bs + "]");
		h += hs;
		if (h > max_h)
			h = h % max_h;
		if (h < min_h)
			h += max_h;
		s += ss;
		if (s > max_s)
			s = max_s;
		if (s < min_s)
			s = min_s;
		b += bs;
		if (b > max_b)
			b = max_b;
		if (b < min_b)
			b = min_b;
		// System.out.println(" >> hsb = [" + h + ", " + s + ", " + b + "]");
	}

	/** Called by the superclass if the pointer button is pressed. */
	protected boolean pointerActivated() {
		setHSB();
		pointerRecentX = pointerActivationX;
		pointerRecentY = pointerActivationY;
		return true;
	}

	/**
	 * Called by the superclass if the pointer has been dragged with button
	 * down.
	 */
	protected boolean pointerDragged() {
		setHSB();
		int dx = pointerCurrentX - pointerRecentX;
		int dy = pointerCurrentY - pointerRecentY;
		pointerRecentX = pointerCurrentX;
		pointerRecentY = pointerCurrentY;
		// System.out.println("Adjustable color par is " +
		// AdjustableColorPar.getString());
		// Lab = adjustableColorPar.getPxlColor().toLabLChStar();
		double h_new = h, s_new = s, b_new = b;
		int adx = Math.abs(dx);
		int ady = Math.abs(dy);
		// System.out.println("dx = " + dx + ", dy = " + dy + " AButton = " +
		// pointerActivationButton + " CButton = " + pointerCurrentButton);
		// System.out.print("Changing ");
		if (adx > ady) {
			// Change hue
			double dh = (h_step * dx);
			h_new = h + dh;
			// System.out.println("ColorAdjustable.pointerDragged(): new Hue = "
			// + h_new);
		} else if (pointerActivationButton == KeyCodes.LEFT_BUTTON) {
			// Left button, change L*
			// System.out.println("ColorAdjustable.pointerDragged(): Lightness");
			double b_new_try = b - (b_step * dy);
			if ((b_new_try >= min_b) && (b_new_try <= max_b)) {
				b_new = b_new_try;
			}
			// System.out.println("ColorAdjustable.pointerDragged(): new Lightness = "
			// + b_new);
		} else {
			// Right button, change chroma
			// System.out.println("ColorAdjustable.pointerDragged(): Chroma");
			double s_new_try = s - (s_step * dy);
			if ((s_new_try >= min_s) && (s_new_try <= max_s)) {
				s_new = s_new_try;
			}
			// System.out.println("ColorAdjustable.pointerDragged(): new Saturation = "
			// + s_new);
		}
		/*
		 * System.out.println("ColorAdjustable.pointerDragged() L: " + L +
		 * " -> " + L_new);
		 * System.out.println("ColorAdjustable.pointerDragged() C: " + C +
		 * " -> " + C_new);
		 * System.out.println("ColorAdjustable.pointerDragged() h: " + h +
		 * " -> " + h_new); System.out.println(""); /*
		 */
		adjustableColorPar.set(new PxlColor(Color.getHSBColor((float) h_new,
				(float) s_new, (float) b_new)));
		// System.out.println("ColorAdjustableHSB().pointerDragged() set [" +
		// h_new + "," + s_new + "," + b_new + "]");
		Color ccc = adjustableColorPar.getValue().getPxlColor().dev();
		// System.out.println("ColorAdjustableHSB.pointerDragged() Color = " +
		// ccc.toString());
		return true;
	}
}
