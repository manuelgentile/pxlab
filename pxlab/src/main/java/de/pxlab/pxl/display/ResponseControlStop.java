package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Stop spurious response collection.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see ResponseControlStart
 * @see ResponseControlCheck
 */
public class ResponseControlStop extends Display {
	public ResponseControlStop() {
		setTitleAndTopic("Stop spurious response checking", CONTROL_DSP | EXP);
		setDisplayListControl();
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
	}

	public boolean displayListControlState(ResponseController rc) {
		rc.setWatchSpuriousResponses(false);
		return (true);
	}
}
