package de.pxlab.gui;

/**
 * This class describes a very general model for a bounded range of numbers
 * which may be viewed by a Axis. An essential property of the model is that it
 * does not fix the scale type and the corresponding operation which is used to
 * construct standard sequences on the scale. The mapping of the model to the
 * axis view is a two-step process: First the model's range is mapped onto the
 * interval [0.0, 1.0] and then this interval is mapped onto the view. This is a
 * graphic illustration of the the mapping of the axis model:
 * 
 * <pre>
 * Model         Minimum ------------ Value ------------Maximum
 *                  |                   |                  |
 *                  V                   V                  V
 * MappedScale     0.0 ---------------------------------- 1.0
 *                  |                   |                  |
 *                  V                   V                  V
 * View          ScaleX --------------- x ---------- (ScaleX + RangeX)
 * 
 * 
 * getMappedValue(x):  Model -> MappedScale
 * 
 * setValueFromMap(x): MappedScale -> Model
 * </pre>
 * 
 * @version 0.2.0
 * @see AxisListener
 * @see Slider
 */
abstract public class AxisModel {
	protected double minimum;
	protected double maximum;
	protected double value;
	protected AxisListener axisListener = null;

	/** Set the lower bound of the range of values. */
	public void setMinimum(double x) {
		minimum = x;
	}

	/** Return the lower bound of the axis model. */
	public double getMinimum() {
		return (minimum);
	}

	/** Set the upper bound of the axis model. */
	public void setMaximum(double x) {
		maximum = x;
	}

	/** Return the upper bound of the axis model. */
	public double getMaximum() {
		return (maximum);
	}

	/** Return the current value of the axis model. */
	public double getValue() {
		return (value);
	}

	/**
	 * Set the current value of the axis model. This method is used to position
	 * the model indenpendent of the axis listener. It usually requires the the
	 * model's view be repainted in order to correctly reflect the model's
	 * state.
	 */
	public void setValue(double v) {
		value = v;
	}

	/**
	 * Map a value from the axis model into the range [0.0, 1.0] on the
	 * MappedScale
	 */
	abstract protected double getMappedValue(double x);

	/**
	 * Set the model's value from a value on the MappedScale in the range [0.0,
	 * 1.0].
	 */
	protected boolean setValueFromMap(double v) {
		double vnew = getValueFromMap(v);
		boolean valid;
		if (axisListener != null) {
			valid = axisListener.axisValueChanged(vnew);
		} else {
			valid = true;
		}
		if (valid)
			value = vnew;
		return (valid);
	}

	public void setAxisListener(AxisListener ax) {
		axisListener = ax;
	}

	public void removeAxisListener() {
		axisListener = null;
	}

	/**
	 * Get a value on the model's scale from a value on the MappedScale in the
	 * range [0.0, 1.0]
	 */
	abstract protected double getValueFromMap(double v);

	/**
	 * Return the size of a single step on the model's scale when the range
	 * [min, max] is divided by n equally spaced steps.
	 */
	abstract protected double getStepSizeValue(int n);

	/**
	 * Get the position on the scale where we arrive when moving from position
	 * start by n steps of size step.
	 */
	abstract protected double getValueForSteps(int n, double step);
}
