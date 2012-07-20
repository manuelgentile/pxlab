package de.pxlab.pxl;

/**
 * Handles adjustment methods for a Display object.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class AdjustmentControl {
	private Display adjustmentDisplay;
	private int adjustmentStopKey;
	private int adjustmentUpKey;
	private int adjustmentDownKey;
	private double adjustmentStep;
	private ExPar adjustmentResponsePar;
	private ExPar adjustmentExPar;
	private int lastAdjustmentKey;
	private double[] adjustmentRange;
	private boolean limitedRange;

	public AdjustmentControl(Display display) {
		adjustmentDisplay = display;
		adjustmentStopKey = ExPar.AdjustmentStopKey.getInt();
		adjustmentUpKey = ExPar.AdjustmentUpKey.getInt();
		adjustmentDownKey = ExPar.AdjustmentDownKey.getInt();
		adjustmentStep = ExPar.AdjustableStep.getDouble();
		adjustmentRange = ExPar.AdjustableParameterLimits.getDoubleArray();
		limitedRange = (adjustmentRange.length == 2)
				&& (adjustmentRange[0] < adjustmentRange[1]);
		adjustmentResponsePar = adjustmentDisplay.ResponseCode;
		adjustmentExPar = adjustmentDisplay.getDynExPar(); // ExPar.get(ExPar.Adjustable.getString());
		lastAdjustmentKey = 0;
		// System.out.println("AdjustmentControl(): adjustmentExPar = " +
		// adjustmentExPar);
		// System.out.println("AdjustmentControl(): adjustmentStep = " +
		// adjustmentStep);
	}

	public boolean adjust() {
		int key = adjustmentResponsePar.getInt();
		if (key == adjustmentStopKey)
			return false;
		adjust(key);
		return true;
	}

	public void adjust(int key) {
		double x = adjustmentExPar.getDouble();
		if (key == adjustmentUpKey) {
			double xx = x + adjustmentStep;
			if (!limitedRange || (xx <= adjustmentRange[1])) {
				adjustmentExPar.set(xx);
			}
		} else if (key == adjustmentDownKey) {
			double xx = x - adjustmentStep;
			if (!limitedRange || (xx >= adjustmentRange[0])) {
				adjustmentExPar.set(xx);
			}
		} else {
			// System.out.println("AdjustmentControl.adjust(key): illegal key");
		}
		// System.out.println("AdjustmentControl.adjust(key): adjustmentExPar = "
		// + adjustmentExPar.getDouble());
		lastAdjustmentKey = key;
	}

	/*
	 * Hier ist noch ein Fehler, denn wenn statt einem zulassigem key ein
	 * falscher gedrueckt wird, dann wird noch ein weiterer Schritt eingestellt.
	 */
	public boolean doAdjust() {
		boolean adjust = (lastAdjustmentKey != adjustmentStopKey);
		if (!adjust)
			adjustmentDisplay.finished();
		return (adjust);
	}
}
