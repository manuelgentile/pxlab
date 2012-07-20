package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Show and set the current CIE white point. According to the CIELab definition
 * the white point of the CIELab transform is that color which shows a perfect
 * reflectance surface illuminated by the current illuminant.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 2006/10/24
 */
public class ShowCIEWhitePoint extends SimpleBar {
	public ShowCIEWhitePoint() {
		setTitleAndTopic("Show/Set current CIE White Point", COLOR_SPACES_DSP);
		Color.set(PxlColor.getCIEWhitePoint());
	}

	protected void computeColors() {
		ExPar.CIEWhitePoint.set(Color.getPxlColor());
		super.computeColors();
	}
}
