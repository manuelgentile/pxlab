package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A feedback message text which may be modified by data from the same display
 * list. The message string is defined by the parameter <code>Text</code>.
 * 
 * <p>
 * This <code>CheckTimeOut</code> option is here to avoid checking response
 * codes which actually do not exist. It is not meant to handle go/nogo
 * conditions. If the flag <code>CheckTimeOut</code> is set then the Feedback
 * object first checks whether the response has been terminated by a time out
 * signal and sets the feedback text to <code>TimeOutText</code> and the
 * parameter <code>Response</code> to the value
 * <code>ResponseCodes.TIME_OUT</code> in case there is a time out.
 * 
 * <p>
 * If there is no time out code or the respective flag has not been set then
 * feedback text modification is done according to the parameter
 * <code>Evaluation</code>. These evaluation methods are currently available:
 * 
 * <ul>
 * <li><code>EvaluationCodes.NO_EVALUATION</code>: No modification of the
 * feedback text ist made. This makes it possible to use the Feedback display as
 * a standard text display with the additional property of being computed just
 * in time.
 * 
 * <li><code>EvaluationCodes.COMPARE_CODE</code>: The current value of the
 * parameter whose name is given by the value of <code>ResponseParameter</code>
 * is compared to the content of parameter <code>CorrectCode</code>. If
 * <code>CorrectCode</code> contains the value of <code>ResponseParameter</code>
 * then the parameter <code>Response</code> is set to
 * <code>ResponseCodes.CORRECT</code> and the parameter <code>Text</code> is set
 * to the value of parameter <code>CorrectText</code>. If the value of the
 * response parameter is not equal to the value of <code>CorrectCode</code> then
 * the parameter <code>Response</code> is set to
 * <code>ResponseCodes.FALSE</code> and the parameter <code>Text</code> is set
 * to the value of parameter <code>FalseText</code>. In this case the global
 * parameter <code>ResponseErrorCount</code> is incremented by 1 and
 * <code>TrialState</code> is set to <code>StateCodes.ERROR</code>. Parameter
 * <code>ResponseParameter</code> usually will be the name of an experimental
 * parameter which contains the response code to be evaluated. However,
 * <code>ResponseParameter</code> may also be an expression. In this case the
 * expression value itself is the target value which is being evaluated. This
 * means that the following two definitions are equivalent:
 * 
 * <pre>
 * ResponseParameter = Trial.SimpleDisk.ResponseCode;
 * ResponseParameter = &quot;Trial.SimpleDisk.ResponseCode&quot;;
 * </pre>
 * 
 * <li><code>EvaluationCodes.CHECK_NOGO</code>: This is used to detect valid
 * nogo-trials. It is checked whether the current value of the parameter whose
 * name is given by the value of parameter <code>ResponseParameter</code> is
 * contained in the value of parameter <code>CorrectCode</code>. If yes then we
 * assume a valid nogo-trial and the parameter <code>Visible</code> is set to 0
 * such that the feedback text will not be shown. The parameter
 * <code>Response</code> is set to <code>ResponseCodes.CORRECT</code> in this
 * case. If we do not have a valid nogo trial then we assume a false go trial
 * and the feedback text is set to <code>FalseText</code> and the parameter
 * <code>Response</code> is set to <code>ResponseCodes.FALSE</code> in this case
 * and TrialState is set to StateCodes.ERROR. The parameter <code>Visible</code>
 * is set to 1 in this case.
 * 
 * 
 * <li><code>EvaluationCodes.SELECT_ONE</code>: (not yet implemented)
 * 
 * <li><code>EvaluationCodes.LETTER</code>: (not yet implemented)
 * 
 * <li><code>EvaluationCodes.TEXTLINE</code>: (not yet implemented)
 * 
 * <li><code>EvaluationCodes.CIELAB_COLOR_DISTANCE</code>: Evaluate the distance
 * of a target color value defined by the parameter Feedback.TargetParameter
 * with a response color parameter defined by Feedback.ResponseParameter. An
 * error/false response is defined to have a resulting value which is larger
 * than Feedback.CorrectCode.
 * 
 * <li><code>EvaluationCodes.CIELAB_AB_DISTANCE</code>: Same as
 * CIELAB_COLOR_DISTANCE but using only the chromaticity coordinates for
 * evaluation.
 * 
 * <li><code>EvaluationCodes.POSITION</code>: Check whether the response
 * position parameter whose name is given in ResponseParameter is sufficiently
 * near to the position value of the parameter whose name is given in
 * TargetParameter. The distance which is 'sufficiently near' is defined in
 * parameter CorrectCode. The actual distance is stored in parameter Response.
 * Sufficiently near values are treated like correct and all others like false
 * responses.
 * 
 * </ul>
 * 
 * The display text is only shown if the flag parameter <code>Visible</code> is
 * true. Also note that this display's properties are compiled immediately
 * before it is shown such that real time substitutions are possible.
 * 
 * @version 0.2.0
 */
/*
 * 05/30/01 make this a justInTime display.
 * 
 * 02/17/01 added time out checking
 * 
 * 11/27/02 added color distance evaluation
 * 
 * 06/28/04 Set ExPar.TrialState on 'false' response trials
 * 
 * 2005/02/24 enable debug options
 * 
 * 2005/05/31 fixed bug with sequence of parameter update.
 * 
 * 2005/08/04 allow ResponseParameter to be an expression whose value is the
 * value which will be evaluated.
 * 
 * 2005/08/23 allow multiple entries in CorrectCode
 * 
 * 2005/09/07 use TrialState to return the result. TrialReturnCode does no
 * longer exist
 */
public class Feedback extends Message implements EvaluationCodes, ResponseCodes {
	/** The name of the parameter whose value should be evaluated. */
	public ExPar ResponseParameter = new ExPar(EXPARNAME, new ExParValue(
			"ResponseCode"), "Response code parameter name");
	/**
	 * The name of the parameter whose value is the reference for correct
	 * responses.
	 */
	public ExPar TargetParameter = new ExPar(EXPARNAME, new ExParValue(
			"ResponseCode"), "Target code parameter name");
	/** The resulting response value after evaluation. */
	public ExPar Response = new ExPar(RTDATA, new ExParValueNotSet(),
			"Evaluated response");
	/** The response code for responses which are considered to be 'correct'. */
	public ExPar CorrectCode = new ExPar(1, 255, new ExParValue(255),
			"Code for Correct Responses");
	/** The feedback text for 'correct' responses. */
	public ExPar CorrectText = new ExPar(STRING, new ExParValue("Correct!"),
			"Feedback text for correct responses");
	/** The feedback text for 'false' responses. */
	public ExPar FalseText = new ExPar(STRING, new ExParValue("False!"),
			"Feedback text for false responses");
	/** The feedback text for time out 'responses'. */
	public ExPar TimeOutText = new ExPar(STRING, new ExParValue("Time out!"),
			"Feedback text for time out responses");
	/** The set of response codes from which response selection is done. */
	public ExPar SelectionSet = new ExPar(UNKNOWN, new ExParValue(0, 1),
			"Selection set of codes");
	/** The type of evaluation which should be done. */
	public ExPar Evaluation = new ExPar(
			GEOMETRY_EDITOR,
			EvaluationCodes.class,
			new ExParValueConstant("de.pxlab.pxl.EvaluationCodes.NO_EVALUATION"),
			"Response code evaluation method");
	/** Include a check for time out responses. */
	public ExPar CheckTimeOut = new ExPar(FLAG, new ExParValue(0),
			"Check for time out responses");
	/** Flag to make this display visible or to hide it. */
	public ExPar Visible = new ExPar(FLAG, new ExParValue(1),
			"Flag to indicate a visible feedback message");
	/** Flag to show an online protocol. */
	public ExPar Protocol = new ExPar(FLAG, new ExParValue(0),
			"Online Protocol Flag");

	public Feedback() {
		setTitleAndTopic("Feedback Message", FEEDBACK_DSP);
		Text.set("OK!");
		Duration.set(1000);
		JustInTime.set(1);
	}

	protected void computeGeometry() {
		int responseCode = 0;
		int[] rCA;
		ExParValue v = ResponseParameter.getValue();
		if (v.getNeedsEvaluation()) {
			rCA = v.getIntArray();
		} else {
			rCA = ExPar.get(v.getString()).getIntArray();
		}
		if ((rCA != null) && (rCA.length >= 1))
			responseCode = rCA[0];
		Debug.show(Debug.DSP_PROPS, "Feedback() 'ResponseParameter' = "
				+ responseCode);
		// First check for time out responses if necessary.
		// if (CheckTimeOut.getFlag())
		// System.out.println("Feedback.computeGeometry() Checking for time out response ...");
		if (CheckTimeOut.getFlag() && (responseCode == ResponseCodes.TIME_OUT)) {
			// System.out.println("Feedback.computeGeometry() Time out response found");
			Response.set(ResponseCodes.TIME_OUT);
			this.Text.set(TimeOutText.getString());
			Debug.show(Debug.DSP_PROPS, "Feedback() Response = TIME_OUT");
		} else {
			int evaluation = Evaluation.getInt();
			// No time out so check the code
			switch (evaluation) {
			case NO_EVALUATION:
				break;
			case COMPARE_CODE:
				/*
				 * int cc = ExPar.get(getInstanceName() +
				 * ".CorrectCode").getInt();
				 * System.out.println("Feedback.computeGeometry() Response code: "
				 * + ExPar.get(ResponseParameter.getString()).getInt());
				 * System.out
				 * .println("Feedback.computeGeometry() Correct code: " +
				 * CorrectCode.getInt() + "( " + CorrectCode + " )");
				 * System.out.println("Feedback.computeGeometry() cc: " + cc);
				 */
				Debug.show(Debug.DSP_PROPS, "Feedback() CorrectCode = "
						+ CorrectCode);
				if (CorrectCode.containsValue(responseCode)) {
					Response.set(CORRECT);
					this.Text.set(CorrectText.getString());
				} else {
					Response.set(FALSE);
					ExPar.ResponseErrorCount.set(ExPar.ResponseErrorCount
							.getInt() + 1);
					ExPar.TrialState.set(StateCodes.ERROR);
					this.Text.set(FalseText.getString());
				}
				if (Debug.isActive(Debug.DSP_PROPS)) {
					System.out.println("Feedback() Response = "
							+ Response.getInt());
					System.out.println("Feedback() ResponseErrorCount = "
							+ ExPar.ResponseErrorCount.getInt());
					System.out.println("Feedback() TrialState = "
							+ ExPar.TrialState.getInt());
					System.out.println("TrialCounter = "
							+ ExPar.TrialCounter.getInt());
				}
				break;
			case CHECK_NOGO:
				Debug.show(Debug.DSP_PROPS, "Feedback() CorrectCode = "
						+ CorrectCode.getInt());
				if (CorrectCode.containsValue(responseCode)) {
					Response.set(CORRECT);
					Visible.set(0);
				} else {
					Response.set(FALSE);
					ExPar.TrialState.set(StateCodes.ERROR);
					Visible.set(1);
					this.Text.set(FalseText.getString());
				}
				if (Debug.isActive(Debug.DSP_PROPS)) {
					System.out.println("Feedback() Response = "
							+ Response.getInt());
					System.out.println("Feedback() TrialState = "
							+ ExPar.TrialState.getInt());
				}
				break;
			case SELECT_ONE:
			case LETTER:
			case TEXTLINE:
				break;
			case CIELAB_COLOR_DISTANCE:
			case CIELAB_AB_DISTANCE:
				Debug.show(Debug.DSP_PROPS, "Feedback() CorrectCode = "
						+ CorrectCode.getDouble());
				PxlColor a = ExPar.get(TargetParameter.getString())
						.getPxlColor();
				double[] aa = a.toLabStar();
				PxlColor b = ExPar.get(ResponseParameter.getString())
						.getPxlColor();
				double[] bb = b.toLabStar();
				double dL = aa[0] - bb[0];
				double da = aa[1] - bb[1];
				double db = aa[2] - bb[2];
				double dE = da * da + db * db;
				if (Evaluation.getInt() == CIELAB_COLOR_DISTANCE) {
					dE += (dL * dL);
				}
				dE = Math.sqrt(dE);
				Response.set(dE);
				if (dE <= CorrectCode.getDouble()) {
					this.Text.set(CorrectText.getString());
				} else {
					ExPar.ResponseErrorCount.set(ExPar.ResponseErrorCount
							.getInt() + 1);
					this.Text.set(FalseText.getString());
				}
				if (Debug.isActive(Debug.DSP_PROPS)) {
					System.out.println("Feedback() Response = "
							+ Response.getDouble());
					System.out.println("Feedback() ResponseErrorCount = "
							+ ExPar.ResponseErrorCount.getInt());
				}
				break;
			case STRING_TO_COLOR:
				Response.set(new PxlColor(ExPar.get(
						ResponseParameter.getString()).getString()));
				Debug.show(Debug.DSP_PROPS,
						"Feedback() Response = " + Response.getPxlColor());
				break;
			case POSITION:
			case POSITION_HIT:
				Debug.show(Debug.DSP_PROPS, "Feedback() CorrectCode = "
						+ CorrectCode.getDouble());
				aa = ExPar.get(TargetParameter.getString()).getDoubleArray();
				bb = ExPar.get(ResponseParameter.getString()).getDoubleArray();
				da = aa[0] - bb[0];
				db = aa[1] - bb[1];
				dE = da * da + db * db;
				dE = Math.sqrt(dE);
				if (evaluation == POSITION)
					Response.set(dE);
				if (dE <= CorrectCode.getDouble()) {
					if (evaluation == POSITION_HIT)
						Response.set(CORRECT);
					this.Text.set(CorrectText.getString());
				} else {
					if (evaluation == POSITION_HIT)
						Response.set(FALSE);
					ExPar.ResponseErrorCount.set(ExPar.ResponseErrorCount
							.getInt() + 1);
					this.Text.set(FalseText.getString());
				}
				if (Debug.isActive(Debug.DSP_PROPS)) {
					System.out.println("Feedback() Response = "
							+ Response.getDouble());
					System.out.println("Feedback() ResponseErrorCount = "
							+ ExPar.ResponseErrorCount.getInt());
				}
				break;
			default:
				break;
			}
		}
		setVisible(Visible.getInt() != 0);
		super.computeGeometry();
	}
}
