package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * Superclass for displays which allow for visual gamma function measurement.
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
 * Pairs of adjacent color fields are shown. The right fields have to be
 * adjusted such that they are equal in brightness to the left fields. Procede
 * from top to bottom and use a large observation distance. Since the left
 * fields use only each second screen line we thus get equations of the form
 * G(x)/2=G(y). From these we can estimate the parameters of the gamma functions
 * by some minimization algorithm. This is done after selection of the "Compute
 * Gamma Parameters" option int the "Colors" submenu of the options menu. The
 * results are shown int a dialog. Results become valid only after all three
 * color channels have been adjusted.
 * 
 * <P>
 * Berns, R. S., Motta, R. J., & Gorzynski, M. E. (1993). CRT Colorimetry. Part
 * I: Theory and Practice. Colour Research and Application, 18, 299--314.
 * <P>
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 03/08/01
 */
public class VisualGammaFields extends Display {
	private int nFields = 6;
	protected int channel = 0;
	public ExPar PrimaryColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.red)), "PrimaryColor");
	public ExPar Step1Color = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color step 1");
	public ExPar Step2Color = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color step 2");
	public ExPar Step3Color = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color step 3");
	public ExPar Step4Color = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color step 4");
	public ExPar Step5Color = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color step 5");
	public ExPar Step6Color = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Color step 6");
	private int firstBar, firstStripes;

	/** Initialize the display list of the demo. */
	protected int create() {
		firstBar = enterDisplayElement(new Bar(Step1Color), group[0]);
		enterDisplayElement(new Bar(Step2Color), group[0]);
		enterDisplayElement(new Bar(Step3Color), group[0]);
		enterDisplayElement(new Bar(Step4Color), group[0]);
		enterDisplayElement(new Bar(Step5Color), group[0]);
		enterDisplayElement(new Bar(Step6Color), group[0]);
		firstStripes = enterDisplayElement(new HorStripedBar(PrimaryColor),
				group[0]);
		enterDisplayElement(new HorStripedBar(Step1Color), group[0]);
		enterDisplayElement(new HorStripedBar(Step2Color), group[0]);
		enterDisplayElement(new HorStripedBar(Step3Color), group[0]);
		enterDisplayElement(new HorStripedBar(Step4Color), group[0]);
		enterDisplayElement(new HorStripedBar(Step5Color), group[0]);
		defaultTiming(0);
		return (firstBar);
	}

	protected void initColorPars() {
		PxlColor black = new PxlColor(Color.black);
		Step1Color.set(PrimaryColor.getPxlColor().mix(black));
		Step2Color.set(Step1Color.getPxlColor().mix(black));
		Step3Color.set(Step2Color.getPxlColor().mix(black));
		Step4Color.set(Step3Color.getPxlColor().mix(black));
		Step5Color.set(Step4Color.getPxlColor().mix(black));
		Step6Color.set(Step5Color.getPxlColor().mix(black));
	}

	public int getChannel() {
		return channel;
	}

	protected void computeGeometry() {
		Rectangle r = centeredRect(width, height, width / 2, 3 * height / 4);
		int sizeW = r.width / 2;
		int h = r.height / nFields;
		int gap = h / 4;
		int sizeH = 2 * ((3 * h / 4) / 2);
		int y = r.y + gap / 2;
		for (int i = 0; i < nFields; i++) {
			getDisplayElement(firstBar + i).setRect(r.x, y, sizeW, sizeH);
			HorStripedBar hb = (HorStripedBar) getDisplayElement(firstStripes
					+ i);
			hb.setRect(r.x + sizeW, y, sizeW, sizeH);
			hb.setPhase(2, 1);
			y += (sizeH + gap);
		}
	}
}
