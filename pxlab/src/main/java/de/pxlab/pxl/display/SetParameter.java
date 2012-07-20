package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Set an experimental parameter's value at runtime. This Display object may be
 * used to set an arbitray experimental parameter's value at runtime and from
 * within a display procedure. This Display object does not create any output
 * and has its JustInTime parameter set by default.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class SetParameter extends Display {
	/** This is the full instance name of the parameter which should be set. */
	public ExPar Parameter = new ExPar(EXPARNAME, new ExParValue("BlockState"),
			"Experimental parameter name");
	/** The value we want to set. */
	public ExPar Value = new ExPar(UNKNOWN, new ExParValue(0),
			"Parameter value");

	public SetParameter() {
		setTitleAndTopic("Set experimental parameter", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		String[] n = Parameter.getStringArray();
		ExParValue v = Value.getValue().getValue();
		// System.out.println("SetParameter.computeGeometry() Execute = " +
		// Execute.getValue() + " [" + Execute.getInt() + "]");
		// System.out.println("SetParameter.computeGeometry() Parameter = " +
		// n[0]);
		// System.out.println("SetParameter.computeGeometry() Value = " + v);
		if ((n.length > 1) && (n.length == v.length)) {
			for (int i = 0; i < v.length; i++) {
				ExPar.get(n[i]).getValue().set(v.getValueAt(i));
			}
		} else {
			ExPar.get(n[0]).getValue().set(v);
		}
	}
}
