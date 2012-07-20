package de.pxlab.pxl;

import java.awt.Component;

/**
 * The DefaultColorServer owns a single color object which may be set and
 * retrieved by clients. Clients must register as ColorChangeListerns in order
 * to get a message whenever the color object has been changed.
 * 
 * @author H. Irtel
 * @version 0.2.2
 * @see ColorServer
 */
/*
 * 06/12/00
 */
public class DefaultColorServer extends ColorServer {
	private PxlColor color = PxlColor.systemColor(PxlColor.GRAY);

	/**
	 * This method is called by clients which manage color coordinates and wish
	 * to tell the server that they have changed the coordinates of the
	 * currently active color.
	 * 
	 * @param source
	 *            the object which generated the color change.
	 * @param x
	 *            the new color value.
	 */
	public void colorAdjusted(Object source, PxlColor x) {
		color = x;
		fireColorChangeEvent(new ColorChangeEvent(source,
				ColorChangeEvent.COLOR_ADJUSTED, x));
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
		this.color = color;
		fireColorChangeEvent(new ColorChangeEvent(source,
				adjustible ? ColorChangeEvent.COLOR_SET
						: ColorChangeEvent.COLOR_SET_PASSIVE, color));
	}

	/**
	 * Return the currently active color.
	 * 
	 * @return a color object describing the current color of this server.
	 */
	public PxlColor getActiveColor() {
		return (color);
	}
}
