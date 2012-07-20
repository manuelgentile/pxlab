package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A pattern for visual gamma measurement by adjustment.
 * 
 * <P>
 * The relation between monitor input voltage and luminance output is called
 * "gamma function". Berns, Motta, and Gorzinsky (1993) have shown that gamma
 * functions follow the equation G(x) = (a*x + s)^g. Knowledge of the video
 * tube's gamma function is necessary for correctly reproducing defined
 * chromaticities on a monitor. Usually it has to be measured with a photometer.
 * We use a visual adjustment method to estimate the gamma function parameters.
 * 
 * <P>
 * A pair of adjacent color fields are shown. The right field has to be adjusted
 * such that it is equal in brightness to the left fields. Since the left fields
 * use only every second screen line we thus get equations of the form
 * G(x)/2=G(y). From these we can estimate the parameters of the gamma functions
 * by some minimization algorithm.
 * 
 * <P>
 * Berns, R. S., Motta, R. J., & Gorzynski, M. E. (1993). CRT Colorimetry. Part
 * I: Theory and Practice. Colour Research and Application, 18, 299--314.
 * <P>
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 
 * 03/08/01
 * 
 * 2005/11/29 Draw multiple horizontal cycles for better visiblity.
 */
public class VisualGammaTarget extends Display implements
		CalibrationChannelBitCodes {
	public ExPar Channel = new ExPar(GEOMETRY_EDITOR,
			CalibrationChannelBitCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.CalibrationChannelBitCodes.RED_CHANNEL"),
			"Color calibration channel");
	public ExPar HighDeviceValue = new ExPar(0, 255, new ExParValue(255),
			"High device value");
	public ExPar LowDeviceValue = new ExPar(0, 255, new ExParValue(192),
			"Low device value");
	public ExPar HighColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "High value color");
	public ExPar LowColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Low value color");
	public ExPar AdjustedPar = new ExPar(STRING, new ExParValue(""),
			"Parameter storing the adjustment result");
	public ExPar ReferencePar = new ExPar(STRING, new ExParValue(""),
			"Parameter storing the reference value");
	public ExPar NumberOfCycles = new ExPar(SMALL_INT, new ExParValue(1),
			"Number of horizontal cycles");
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(300),
			"Pattern width");
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(300),
			"Pattern height");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");

	public VisualGammaTarget() {
		setTitleAndTopic("Single visual gamma adjustment target",
				VISUAL_GAMMA_DSP);
	}
	int s;

	/** Initialize the display list of the demo. */
	protected int create() {
		s = enterDisplayElement(new VisualGammaPattern(HighColor, LowColor),
				group[0]);
		defaultTiming(0);
		return (s);
	}

	protected void computeColors() {
		int[] p1 = { 0, 0, 0 };
		int[] p2 = { 0, 0, 0 };
		int c = Channel.getInt();
		int hi = HighDeviceValue.getInt();
		int lo = LowDeviceValue.getInt();
		int devMax = 255;
		if (hi < 0)
			hi = 0;
		if (hi > devMax)
			hi = devMax;
		if (lo < 0)
			lo = 0;
		if (lo > devMax)
			lo = devMax;
		if (c == RED_CHANNEL) {
			p1[0] = hi;
			p2[0] = lo;
		} else if (c == GREEN_CHANNEL) {
			p1[1] = hi;
			p2[1] = lo;
		} else if (c == BLUE_CHANNEL) {
			p1[2] = hi;
			p2[2] = lo;
		}
		HighColor.set(new PxlColor(new Color(p1[0], p1[1], p1[2])));
		LowColor.set(new PxlColor(new Color(p2[0], p2[1], p2[2])));
		/*
		 * System.out.println("VisualGammaTarget.computeColors() hi =        " +
		 * hi);
		 * System.out.println("VisualGammaTarget.computeColors() lo =        " +
		 * lo);
		 * System.out.println("VisualGammaTarget.computeColors() HighColor = " +
		 * HighColor.getPxlColor());
		 * System.out.println("VisualGammaTarget.computeColors() LowColor =  " +
		 * LowColor.getPxlColor());
		 */
	}

	protected void computeGeometry() {
		computeColors();
		VisualGammaPattern bar = (VisualGammaPattern) getDisplayElement(s);
		int w = Width.getInt();
		int h = Height.getInt();
		bar.setRect(LocationX.getInt() - w / 2, LocationY.getInt() - h / 2, w,
				h);
		bar.setProperties(NumberOfCycles.getInt());
	}

	/**
	 * After the adjustment is finished we store the result in a global
	 * parameter. We also make sure that the device value parameters contain
	 * primitive values since otherwise the parameter estimation procedure from
	 * de.pxlab.pxl.data.VisualGammaEstimation does not work. The reason is that
	 * HighDeviceValue may be initialized to an expression and it is not changed
	 * here. This means that the data analysis procedure will see the expression
	 * which may involve a global parameter which is not modified by trial
	 * results. Thus it may be constant across trials.
	 */
	protected void finished() {
		String n = ReferencePar.getString();
		if (de.pxlab.util.StringExt.nonEmpty(n)) {
			int hdv = HighDeviceValue.getInt();
			ExPar.get(n).getValue().set(hdv);
			HighDeviceValue.set(hdv);
		}
		n = AdjustedPar.getString();
		if (de.pxlab.util.StringExt.nonEmpty(n)) {
			int ldv = LowDeviceValue.getInt();
			ExPar.get(n).getValue().set(ldv);
			LowDeviceValue.set(ldv);
		}
	}
}
