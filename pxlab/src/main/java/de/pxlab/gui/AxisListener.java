package de.pxlab.gui;

/**
 * An AxisListener responds to changes in an AxisModel.
 * 
 * @version 0.2.0
 * @see AxisModel
 * @see Slider
 */
public interface AxisListener {
	/**
	 * This tells the listener that the axis value has been changed. The
	 * listener returns true if it is willing to accept the new value. It
	 * returns false if the new value is not acceptable for the axis model.
	 */
	public boolean axisValueChanged(double x);
}
