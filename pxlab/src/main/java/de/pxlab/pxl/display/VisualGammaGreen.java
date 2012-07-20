package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A display pattern for finding the green gamma function of your monitor by
 * visual adjustment.
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
public class VisualGammaGreen extends VisualGammaFields {
	public VisualGammaGreen() {
		setTitleAndTopic("Visual gamma function measurement: green",
				VISUAL_GAMMA_DSP | DEMO);
		PrimaryColor.set(new PxlColor(Color.green));
		channel = 1;
		initColorPars();
	}
}
