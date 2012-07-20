package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Controls the parameter SessionState. Currently these state control mechanisms
 * are implemented:
 * 
 * <ul>
 * <li>StateControlCodes.NO_CONTROL: does nothing.
 * 
 * <li>StateControlCodes.STOP_ON_CRITERION: If the experimental parameter whose
 * name is given by the value of ControlParameter is equal to the value of the
 * parameter Criterion then the parameter SessionState is set to
 * SessionState.BREAK. This will stop the currently running session after the
 * currently running block has been finished.
 * 
 * <li>
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class SessionStateControl extends Display {
	/**
	 * This is the name of the parameter which should be used for session state
	 * control.
	 */
	public ExPar ControlParameter = new ExPar(EXPARNAME, new ExParValue(
			"ResponseErrorCount"), "Session control parameter");
	/** The criterion which should be checked. */
	public ExPar Criterion = new ExPar(SMALL_INT, new ExParValue(0),
			"Criterion value");
	/** The type of checking we do. */
	public ExPar Type = new ExPar(
			GEOMETRY_EDITOR,
			StateControlCodes.class,
			new ExParValueConstant("de.pxlab.pxl.StateControlCodes.NO_CONTROL"),
			"Control type");

	public SessionStateControl() {
		setTitleAndTopic("Session State Control", PARAM_DSP | EXP);
		JustInTime.set(1);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		switch (Type.getInt()) {
		case StateControlCodes.NO_CONTROL:
			break;
		case StateControlCodes.STOP_ON_CRITERION:
			if (ExPar.get(ControlParameter.getString()).getInt() == Criterion
					.getInt()) {
				ExPar.SessionState.set(StateCodes.BREAK);
			}
			break;
		default:
			break;
		}
		if (Debug.isActive(Debug.DSP_PROPS | Debug.STATE_CTRL)) {
			System.out.println("SessionStateControl() 'ControlParameter' = "
					+ ExPar.get(ControlParameter.getString()).getInt());
			System.out.println("SessionStateControl() Criterion = "
					+ Criterion.getInt());
			System.out.println("SessionStateControl() SessionState = "
					+ ExPar.SessionState.getInt());
		}
	}
}
