package de.pxlab.pxl;

import java.util.ArrayList;

/**
 * This implements a ColorServer which stores its color in an ExPar object.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ColorParServer extends ColorServer {
	private ExPar colorPar = new ExPar(ExParTypeCodes.COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "");

	/**
	 * Set this color server's ExPar object which is used to store the server's
	 * active color.
	 * 
	 * @param source
	 *            the source object which sets the color parameter.
	 * @param param
	 *            the ExPar object which contains the color to be served.
	 */
	public void setColorPar(Object source, ExPar param) {
		setColorPar(source, true, param);
	}

	/**
	 * Set this color server's ExPar object which is used to store the server's
	 * active color.
	 * 
	 * @param source
	 *            the source object which sets the color parameter.
	 * @param param
	 *            the ExPar object which contains the color to be served.
	 */
	public void setColorPar(Object source, boolean adjustible, ExPar param) {
		colorPar = param;
		// System.out.println("Parameter set: " + p.getHint());
		fireColorChangeEvent(new ColorChangeEvent(source,
				adjustible ? ColorChangeEvent.COLOR_SET
						: ColorChangeEvent.COLOR_SET_PASSIVE,
				colorPar.getPxlColor()));
	}

	/**
	 * Return this server's ExPar color parameter.
	 * 
	 * @return the ExPar object which stores this server's color.
	 */
	public ExPar getColorPar() {
		return (colorPar);
	}

	/**
	 * This method is called by clients which manage color coordinates and wish
	 * to tell the server that they have changed the coordinates of the
	 * currently active color. This method sends the given ColorChangeEvent to
	 * all registered listeners.
	 * 
	 * @param source
	 *            the source object of the change.
	 * @param color
	 *            the color coordinates of the new color.
	 */
	public void colorAdjusted(Object source, PxlColor color) {
		colorPar.set(color);
		// System.out.println("Setting current color to " + c);
		fireColorChangeEvent(new ColorChangeEvent(source,
				ColorChangeEvent.COLOR_ADJUSTED, color));
	}

	/**
	 * Set the current color of this server to the given color. This method
	 * creates and fires ColorChangeEvents of type COLOR_SET.
	 * 
	 * @param source
	 *            the source object of the change.
	 * @param adjustible
	 *            if true than the active color is adjustible if false then it
	 *            is passive and for display only.
	 * @param color
	 *            the color coordinates of the new color.
	 */
	public void setActiveColor(Object source, boolean adjustible, PxlColor color) {
		colorPar.set(color);
		// System.out.println("Setting current color to " + c);
		fireColorChangeEvent(new ColorChangeEvent(source,
				adjustible ? ColorChangeEvent.COLOR_SET
						: ColorChangeEvent.COLOR_SET_PASSIVE, color));
	}

	/**
	 * Return this server's active color coordinates.
	 * 
	 * @return a color object describing the current color of this server.
	 */
	public PxlColor getActiveColor() {
		return (colorPar.getPxlColor());
	}
}
