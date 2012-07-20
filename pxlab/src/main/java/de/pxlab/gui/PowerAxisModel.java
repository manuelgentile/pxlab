package de.pxlab.gui;

/**
 * This class implements the power function AxisModel. Its main feature is a
 * power transfrom of the slider motion.
 * 
 * @author Hans Irtel
 * @version 0.2.0, 03/08/00
 * @see AxisListener
 * @see Slider
 */
public class PowerAxisModel extends AxisModel {
	protected double powerValue;

	public PowerAxisModel(double p, double mn, double mx, double v) {
		powerValue = p;
		setMinimum(mn);
		setMaximum(mx);
		setValue(v);
	}

	/** Map a scale value to the range [0.0, 1.0] */
	protected double getMappedValue(double x) {
		return (power((x - minimum) / (maximum - minimum)));
	}

	/** Get a scale value from a value in the range [0.0, 1.0] */
	protected double getValueFromMap(double v) {
		return (inversePower(v) * (maximum - minimum) + minimum);
	}

	/**
	 * Return the size of a single step when the range [min, max] is divided by
	 * n steps.
	 */
	protected double getStepSizeValue(int n) {
		return (inversePower(1.0 / (double) n) * (maximum - minimum) + minimum);
	}

	/**
	 * Get the position on the scale where we arrive when moving from position
	 * start by n steps of size step.
	 */
	protected double getValueForSteps(int n, double step) {
		return (inversePower((double) n) * (step - minimum) + minimum);
	}

	private double power(double x) {
		return (Math.pow(x, powerValue));
	}

	private double inversePower(double x) {
		return (Math.pow(x, 1.0 / powerValue));
	}
}
