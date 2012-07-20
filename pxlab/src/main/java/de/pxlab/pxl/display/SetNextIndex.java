package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Set a parameter to the current index of an index array and increment the
 * index. After this Display has been executed the parameter Index will contain
 * the current index value and the internal index counter will be incremented
 * for the next call.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2008/04/15
 */
public class SetNextIndex extends InitIndexArray {
	/** The value of the next index. */
	public ExPar Index = new ExPar(RTDATA, new ExParValue(0), "Next index");

	public SetNextIndex() {
		setTitleAndTopic("Set next index in an index array", PARAM_DSP | EXP);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected void computeGeometry() {
		String n = Name.getString();
		int[] a = (int[]) RuntimeRegistry.get(n);
		if (a == null) {
			new ParameterValueError(
					"SetNextIndex tries to use an index array which has not been initialized: "
							+ n);
		} else {
			Integer next = (Integer) RuntimeRegistry.get(n + NEXT_INDEX);
			int i = next.intValue();
			RuntimeRegistry.put(n + NEXT_INDEX, new Integer(i + 1));
			Index.set(a[i]);
			// System.out.println("SetNextIndex: next index is " +
			// Index.getInt());
		}
	}
}
