package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Show and set the current color device's white point. According to the CIELAB
 * definition the white point of a color device is that color which shows a
 * perfect reflectance surface illuminated by the current illuminant.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.display.WhitePointControl
 */
public class ShowWhitePoint extends SimpleBar {
	public ShowWhitePoint() {
		setTitleAndTopic("Show Color Device White Point", COLOR_SPACES_DSP);
		Color.set(new ExParValueFunction(ExParExpression.WHITE));
	}

	protected void computeColors() {
		PxlColor.getDeviceTransform().setWhitePoint(
				Color.getPxlColor().getComponents());
		super.computeColors();
	}
}
