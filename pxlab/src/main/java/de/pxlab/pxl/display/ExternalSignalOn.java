package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Switch on an external signal. This Display is used to switch on an external
 * signal like a LED connected to a serial port output control line.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * @see ExternalSignalOff
 */
/*
 * 
 * 08/22/03 do not set the Overlay property by default.
 */
public class ExternalSignalOn extends Display {
	/** Code number of the external signal. */
	public ExPar Code = new ExPar(
			GEOMETRY_EDITOR,
			ExternalSignalCodes.class,
			new ExParValueConstant("de.pxlab.pxl.ExternalSignalCodes.SIGNAL_1"),
			"Code of the external signal");

	public ExternalSignalOn() {
		setTitleAndTopic("Switch on an external signal", EXTERNAL_DSP | EXP);
	}

	public boolean isGraphic() {
		return false;
	}
	protected ExternalControlSignal s;

	protected int create() {
		s = new ExternalControlSignal(false, true);
		enterDisplayElement(s, group[0]);
		defaultTiming(0);
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
		s.setCode(Code.getInt());
	}
}
