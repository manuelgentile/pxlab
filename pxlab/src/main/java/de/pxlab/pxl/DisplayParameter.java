package de.pxlab.pxl;

/**
 * Describes a 1-dimensional parameter of a display, including its range of
 * acceptable values and its name.
 */
public class DisplayParameter {
	protected double minimum;
	protected double maximum;
	protected double value;
	protected ExPar exPar;

	/** Create a new display parameter with the given properties. */
	public DisplayParameter(double mn, double mx, double v, ExPar n) {
		setRangeParameters(mn, mx, v);
		exPar = n;
	}

	public DisplayParameter(double v) {
		setRangeParameters(0.0, 1.0, v);
	}

	public void setValue(double v) {
		setRangeParameters(minimum, maximum, v);
	}

	public double getValue() {
		return (value);
	}

	public double getMinimum() {
		return (minimum);
	}

	public double getMaximum() {
		return (maximum);
	}

	public double getRange() {
		return (maximum - minimum);
	}

	public String getName() {
		return (exPar.getHint());
	}

	public ExPar getExPar() {
		return (exPar);
	}

	private void setRangeParameters(double mn, double mx, double v) {
		if ((mn <= v) && (v <= mx)) {
			minimum = mn;
			maximum = mx;
			value = v;
		}
	}
}
