package de.pxlab.gui;

/**
 * This class implements the logarithmic axis model. Its main features are a
 * log10 transfrom of the slider motion and a multiplicative standard sequence
 * construction.
 * 
 * @author Hans Irtel
 * @version 0.2.0, 03/08/00
 * @see AxisListener
 * @see Slider
 */
public class LogAxisModel extends AxisModel {
	protected double logMinimum;
	protected double logMaximum;

	public LogAxisModel(double mn, double mx, double v) {
		setMinimum(mn);
		setMaximum(mx);
		setValue(v);
	}

	public void setMinimum(double x) {
		minimum = x;
		logMinimum = log10(x);
	}

	public void setMaximum(double x) {
		maximum = x;
		logMaximum = log10(x);
	}

	/** Map a scale value to the range [0.0, 1.0] */
	protected double getMappedValue(double x) {
		return ((log10(x) - logMinimum) / (logMaximum - logMinimum));
	}

	/** Set the scale value from a value in the range [0.0, 1.0] */
	protected double getValueFromMap(double v) {
		return (Math.pow(10.0, (v * (logMaximum - logMinimum) + logMinimum)));
	}

	/**
	 * Return the size of a single step when the range [min, max] is divided by
	 * n steps.
	 */
	protected double getStepSizeValue(int n) {
		return (Math.pow(maximum / minimum, 1.0 / (double) n));
	}

	/**
	 * Get the position on the scale where we arrive when moving from position
	 * start by n steps of size step.
	 */
	protected double getValueForSteps(int n, double step) {
		double start = minimum;
		for (int i = 0; i < n; i++)
			start *= step;
		return (start);
		// return(start * Math.pow(step, (double)n));
	}

	private double log10(double x) {
		return (Math.log(x) / Math.log(10.0));
	}
}
