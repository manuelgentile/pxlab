package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Start spurious response collection. This Display object does not show
 * anything on the screen but only starts a mechanism to watch for spurious
 * response events. These are response events which happen while no other
 * Display objects are watching the response device. This is the case for time
 * intervals which are stopped by pure clock timers. The spurious response
 * collection is stopped by the ResponseControlStop Display object.
 * 
 * @author H. Irtel
 * @version 0.1.2
 * @see ResponseControlStop
 * @see ResponseControlCheck
 */
public class ResponseControlStart extends Display {
	public ResponseControlStart() {
		setTitleAndTopic("Start spurious response checking", CONTROL_DSP | EXP);
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
		rc.setWatchSpuriousResponses(true);
		return (true);
	}
}
