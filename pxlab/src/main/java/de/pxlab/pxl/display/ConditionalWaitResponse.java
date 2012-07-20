package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Conditionally wait for a subject response if a condition parameter requests
 * it. This object will only then be 'visible' if the parameter named by
 * ConditionParameter has one of the values contained in ConditionCode. This is
 * a just-in-time display. Here is an example how to use it:
 * 
 * <pre>
 *     Trial(SimpleDisk.ResponseCode, SimpleDisk.ResponseTime, 
 * 	ConditionalWaitResponse.ResponseCode, ConditionalWaitResponse.ResponseTime, 
 * 	ResponseCode, ResponseTime) {
 *       ClearScreen:pause() {
 *         Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
 *         Duration = 1000;
 *       }
 *       SimpleDisk() {
 *         Timer = de.pxlab.pxl.TimerCodes.LIMITED_RESPONSE_TIMER 
 * 		+ de.pxlab.pxl.TimerCodes.START_RESPONSE_TIMER_BIT
 * 		+ de.pxlab.pxl.TimerCodes.STOP_RESPONSE_TIMER_BIT;
 * 	Duration = 300;
 *       }
 *       ClearScreen:clear() {
 *         Timer = de.pxlab.pxl.TimerCodes.NO_TIMER;
 *       }
 *       ConditionalWaitResponse() {
 * 	ConditionParameter = "Trial.SimpleDisk.ResponseCode";
 * 	ConditionCode = de.pxlab.pxl.ResponseCodes.TIME_OUT;
 *         Timer = de.pxlab.pxl.TimerCodes.RESPONSE_TIMER + de.pxlab.pxl.TimerCodes.STOP_RESPONSE_TIMER_BIT;
 *         Overlay = de.pxlab.pxl.OverlayCodes.JOIN;
 *       }
 *     }
 * </pre>
 * 
 * This example shows the stimulus for at most 300 ms and then still waits for a
 * response if there was none within this period. The total response time is
 * stored in the global parameter ResponseTime.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ConditionalWaitResponse extends Display {
	/** The name of the parameter whose value should be evaluated. */
	public ExPar ConditionParameter = new ExPar(EXPARNAME, new ExParValue(
			"ResponseCode"), "Condition code parameter name");
	public ExPar ConditionCode = new ExPar(GEOMETRY_EDITOR,
			ResponseCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.ResponseCodes.TIME_OUT"),
			"Condition code value");

	public ConditionalWaitResponse() {
		setTitleAndTopic("Conditional wait for response", CONTROL_DSP);
		JustInTime.set(1);
	}

	protected int create() {
		int s1 = enterDisplayElement(new Clear(), group[0]);
		defaultTiming(0);
		return (s1);
	}

	/**
	 * Depending on the value of ConditionParameter the visiblity of this
	 * display is switched on/off.
	 */
	protected void computeGeometry() {
		int responseCode = 0;
		int[] rCA;
		ExParValue v = ConditionParameter.getValue();
		if (v.getNeedsEvaluation()) {
			rCA = v.getIntArray();
		} else {
			rCA = ExPar.get(v.getString()).getIntArray();
		}
		if ((rCA != null) && (rCA.length >= 1))
			responseCode = rCA[0];
		setVisible(ConditionCode.containsValue(responseCode));
	}
}
