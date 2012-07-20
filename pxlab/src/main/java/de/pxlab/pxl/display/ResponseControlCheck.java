package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Check whether spurious responses have been found during the most recent
 * spurious response collection interval. Spurious response checking depends on
 * the experimental parameter Type.
 * 
 * <p>
 * If Type is nonzero then we check for a certain response event. The event is
 * defined by the parameters Direction, Device, and Code. Type == LAST_RESPONSE
 * checks for the last response in the checking interval. Type == FIRST_RESPONSE
 * checks for the first response code in the checked interval. If the response
 * event is present then displayListControlState() returns true and the message
 * text is not shown. If the event is not present then it returns false and the
 * message text is shown.
 * 
 * <p>
 * If the Type parameter is ANY_RESPONSE we check whether there is any spurious
 * response. If there is one then displayListControlState() returns false and
 * the message text becomes visible. Otherwise it returns true and the message
 * text is not shown.
 * 
 * <p>
 * If the method displayListControlState() returns false then the global
 * parameter TrialState is set to StateCodes.ERROR.
 * 
 * @author H. Irtel
 * @version 0.3.0
 * @see ResponseControlStart
 * @see ResponseControlStop
 */
public class ResponseControlCheck extends Message {
	/**
	 * If nonzero then we have to check for a certain response event. If zero we
	 * have to check whether there is any spurious response.
	 */
	public ExPar Type = new ExPar(GEOMETRY_EDITOR, ResponseControlCodes.class,
			new ExParValueConstant(
					"de.pxlab.pxl.ResponseControlCodes.ANY_RESPONSE"),
			"Response to check");
	/** Direction code from class de.pxlab.pxl.TimerCodes. */
	public ExPar Direction = new ExPar(GEOMETRY_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"),
			"Response direction bit");
	/** Device code from class de.pxlab.pxl.TimerCodes. */
	public ExPar Device = new ExPar(GEOMETRY_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"),
			"Response device bit");
	/** Response code value. */
	public ExPar Codes = new ExPar(GEOMETRY_EDITOR, KeyCodes.class,
			new ExParValueNotSet(), "Response Code");

	public ResponseControlCheck() {
		super();
		setTitleAndTopic("Check for Spurious Responses", CONTROL_DSP | EXP);
		setDisplayListControl();
		Text.set("Response Error!");
		Duration.set(2000);
		JustInTime.set(1);
	}

	public boolean displayListControlState(ResponseController rc) {
		boolean r = true;
		ResponseEventGroup expected;
		ResponseEvent found = null;
		setVisible(false);
		int tp = Type.getInt();
		if (tp == ResponseControlCodes.ANY_RESPONSE) {
			r = !rc.hasSpuriousResponse();
		} else {
			expected = new ResponseEventGroup(Device.getInt(),
					Direction.getInt(), Codes.getValue().isNotSet() ? null
							: Codes.getIntArray());
			if (tp == ResponseControlCodes.LAST_RESPONSE) {
				found = rc.getLastSpuriousResponse();
			} else if (tp == ResponseControlCodes.FIRST_RESPONSE) {
				found = rc.getFirstSpuriousResponse();
			}
			if (found != null) {
				// System.out.println("ResponseControlCheck.displayListControlState(): Found "
				// + found);
				int cd = expected.getCode(found);
				ResponseCode.set(cd);
				ResponseTime.set(found.getTime());
				// System.out.println("ResponseControlCheck.displayListControlState(): code = "
				// + cd);
				r = (cd >= 0);
			} else {
				r = false;
			}
		}
		if (r) {
			Debug.show(Debug.DSP_PROPS,
					"ResponseControlCheck.displayListControlState(): returns true.");
		} else {
			setVisible(true);
			ExPar.TrialState.set(StateCodes.ERROR);
			Debug.show(Debug.DSP_PROPS,
					"ResponseControlCheck.displayListControlState(): TrialState set to "
							+ ExPar.TrialState.getInt());
		}
		return r;
	}
}
