package de.pxlab.gui;

/**
 * This is a model for a linear axis.
 * 
 * @author H. Irtel
 * @version 0.2.0, 03/08/00
 * @see AxisListener
 * @see Slider
 */
public class LinearAxisModel extends AxisModel {
	public LinearAxisModel(double mn, double mx, double v) {
		minimum = mn;
		maximum = mx;
		value = v;
	}

	/** Map a scale value to the range [0.0, 1.0] */
	protected double getMappedValue(double x) {
		return ((x - minimum) / (maximum - minimum));
	}

	/** Get a scale value from a value in the range [0.0, 1.0] */
	protected double getValueFromMap(double v) {
		return (v * (maximum - minimum) + minimum);
	}

	/**
	 * Return the size of a single step when the range [min, max] is divided by
	 * n steps.
	 */
	protected double getStepSizeValue(int n) {
		return ((maximum - minimum) / n);
	}

	/**
	 * Get the position on the scale where we arrive when moving from position
	 * start by n steps of size step.
	 */
	protected double getValueForSteps(int n, double step) {
		return (minimum + n * step);
	}
}
