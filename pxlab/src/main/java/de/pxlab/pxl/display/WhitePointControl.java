package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Set the current color device's white point. According to the CIELAB
 * definition the white point of a color device is that color which shows a
 * perfect reflectance surface illuminated by the current illuminant.
 * 
 * <p>
 * This display object does nothing but change the current
 * PxlColorDeviceTransform object's white point setting.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.pxl.ColorDeviceTransform
 */
public class WhitePointControl extends Display {
	/** Color device white point. */
	public ExPar WhitePoint = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)),
			"Color Device Transform White Point");

	public WhitePointControl() {
		setTitleAndTopic("Color Device Transform White Point Control",
				PARAM_DSP);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
		Execute.set(0);
		setVisible(false);
	}

	protected int create() {
		enterDisplayElement(new Clear(), group[0]);
		defaultTiming(0);
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
		PxlColor.getDeviceTransform().setWhitePoint(
				WhitePoint.getPxlColor().getComponents());
	}
}
