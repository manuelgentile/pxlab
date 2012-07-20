package de.pxlab.pxl;

import java.util.*;

import de.pxlab.util.*;
import de.pxlab.stat.IsotonicPMF;

/**
 * A class for controlling adaptive psychophysical procedures.
 * 
 * <p>
 * An adaptive sequence is a sequence of trials within a block which may be
 * mixed with other trials possibly belonging to other adaptive sequences.
 * 
 * <p>
 * Trials belonging to a single sequence are identified by the parameter
 * AdaptiveSequenceID.
 * 
 * <p>
 * The currently active adaptive sequences are stored in a hash map using the
 * value of AdaptiveSequenceID as their access key.
 * 
 * <p>
 * Whenever the runtime controller finishes a trial which belongs to an adaptive
 * sequence it looks up the sequence in the hash map. If the sequence is there
 * then the next trial of the sequence is identified and its adaptive parameter
 * modified according to the rules of the sequence. If the sequence is not yet
 * contained in the hash map then it is entered and the next trial is treated as
 * before.
 * 
 * <p>
 * An adaptive sequence controls (1) the value of the adaptive parameter, (2)
 * the stopping condition, and (3) the method for computing the final result.
 * These properties are derived from parameter values at that point in time when
 * the adaptive sequence is created.
 * 
 * <p>
 * An adaptive sequence stores its history in the state parameter AdaptiveState.
 * The state of trial n is the history of trials 1, ..., (n-1). The state for
 * the next trial is negated if a sequence satisfies its stopping rule.
 * Different types of adaptive procedures are identified by different types of
 * state transition functions. PXLab has several state transition functions for
 * standard adaptive procedures built in. The state transition function maps the
 * current state of a trial and the response to this trial to the state of the
 * next trial of the sequence.
 * 
 * <p>
 * An adaptive sequence also controls the value of the adaptive parameter. The
 * value of the adaptive parameter for the next trial is derived from the
 * current value of the adaptive parameter, the state of the trial and the
 * response. The adaptive parameter control function is defined by the type of
 * the adaptive procedure as it is specified by the parameter AdaptiveProcedure.
 * The predefined control functions handle standard adaptive procedure types by
 * experimental control parameters which can be used to modify the behavior of
 * an adaptive sequence.
 * 
 * <p>
 * This class also provides methods to compute the results of adaptive
 * procedures. This is done using the data tree which is created at runtime.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * To do: Implement some of the other adaptive algorithms
 * 
 * 09/16/01
 * 
 * 06/26/03 observe range limits for the adaptive parameter
 * 
 * 2005/07/26 changed FatalError() to NonFatalError() by default assumptions
 */
public class AdaptiveControl implements AdaptiveProcedureCodes,
		AdaptiveStopCodes, AdaptiveResultCodes {
	private static final int LastAdaptiveProcedureCode = DELAYED_UP_1_DOWN_1;
	private static final int LastAdaptiveStopCode = STOPKEY_RESPONSE;
	/** True if this sequence has reached the minimum step size. */
	private boolean atMinimumStepSize = false;
	/** Adaptive procedure type of this sequence. */
	private int adaptiveProcedure;
	/** Stopping rule of this sequence. */
	private int stoppingRule;
	/** Result computation method of this sequence. */
	private int adaptiveResultComputation;
	/**
	 * This contains the quantiles which should be estimated by the adaptive
	 * procedure results estimation method.
	 */
	private double[] adaptiveQuantiles;
	/** This contains the results of the adaptive procedure. */
	private double[] adaptiveResults;
	/** True if this sequence is not an adaptive procedure. */
	private boolean nonAdaptive;
	/** The ID of this adaptive sequence. */
	private int adaptiveSequenceID;
	/** The name of the adaptive stimulus parameter. */
	private String adaptiveParameterName;
	private boolean limitedRange;
	private double adaptiveParameterLowerLimit;
	private double adaptiveParameterUpperLimit;
	// These are some index values for the experimental parameter
	// value array of the forward trial.
	private int adaptiveSequenceIDIndex;
	private int adaptiveTrialCounterIndex;
	private int adaptiveStateIndex;
	private int adaptiveParameterIndex;
	private int adaptiveStepDivisorIndex;
	private int adaptiveTurnPointCounterIndex;
	private int adaptiveResponseIndex;
	private int adaptiveResultsIndex;
	// The currently defined response key codes
	private int yesKey;
	private int noKey;
	private int stopKey;
	/**
	 * True if the current trial contains an illegal response code.
	 */
	private boolean responseError = false;

	/**
	 * Create an adaptive sequence control object for the given trial. This
	 * should be the first trial of the sequence. The sequence will be
	 * identified by the value of the parameter AdaptiveSequenceID which must be
	 * a local trial parameter if more than a single adaptive sequences are
	 * mixed within the same block.
	 * 
	 * @param currentTrial
	 *            the trial for which the adaptive sequence should be created.
	 */
	public AdaptiveControl(ExDesignNode currentTrial) {
		int a = ExPar.AdaptiveProcedure.getInt();
		if ((a < 0) || (a > LastAdaptiveProcedureCode)) {
			new ParameterValueError("Unknown adaptive procedure type: " + a
					+ ". Setting type 'NonAdaptive'.");
			a = NON_ADAPTIVE;
		}
		adaptiveProcedure = a;
		nonAdaptive = (adaptiveProcedure == NON_ADAPTIVE);
		if (!nonAdaptive) {
			adaptiveSequenceID = ExPar.AdaptiveSequenceID.getInt();
		}
		a = ExPar.AdaptiveStoppingRule.getInt();
		if ((a < 0) || (a > LastAdaptiveStopCode)) {
			new ParameterValueError(
					"Unknown adaptive procedure stopping rule: " + a
							+ ". Setting stopping rule 'DontStop'.");
			a = DONT_STOP;
		}
		stoppingRule = a;
		adaptiveQuantiles = ExPar.AdaptiveQuantiles.getDoubleArray();
		adaptiveResultComputation = ExPar.AdaptiveResultComputation.getInt();
		adaptiveSequenceIDIndex = currentTrial
				.getParIndex("AdaptiveSequenceID");
		adaptiveTrialCounterIndex = currentTrial
				.getParIndex("AdaptiveTrialCounter");
		adaptiveStateIndex = currentTrial.getParIndex("AdaptiveState");
		adaptiveParameterName = ExPar.AdaptiveParameter.getString();
		adaptiveParameterIndex = currentTrial
				.getParIndex(adaptiveParameterName);
		adaptiveStepDivisorIndex = currentTrial
				.getParIndex("AdaptiveStepDivisor");
		adaptiveTurnPointCounterIndex = currentTrial
				.getParIndex("AdaptiveTurnPointCounter");
		adaptiveResponseIndex = currentTrial
				.getParIndex(ExPar.AdaptiveResponseParameter.getString());
		adaptiveResultsIndex = currentTrial.getParIndex("AdaptiveResults");
		Debug.show(Debug.ADAPTIVE, "AdaptiveControl() created.");
		yesKey = ExPar.YesKey.getInt();
		noKey = ExPar.NoKey.getInt();
		stopKey = ExPar.StopKey.getInt();
		Debug.show(Debug.ADAPTIVE, "AdaptiveControl() YesKey = " + yesKey);
		Debug.show(Debug.ADAPTIVE, "AdaptiveControl() NoKey  = " + noKey);
		double[] apl = ExPar.AdaptiveParameterLimits.getDoubleArray();
		limitedRange = (apl.length == 2);
		if (limitedRange) {
			adaptiveParameterLowerLimit = apl[0];
			adaptiveParameterUpperLimit = apl[1];
			Debug.show(Debug.ADAPTIVE, "AdaptiveControl() Parameter limits = "
					+ ExPar.AdaptiveParameterLimits);
		}
	}

	/**
	 * Find the next trial controlled by this adaptive sequence control object
	 * and update its state and its adaptive parameters according to the data of
	 * the current trial.
	 * 
	 * @param currentTrial
	 *            the current trial node which has already been executed.
	 * @return a code which tells the sender whether the fix up was successfull
	 *         or not. A return code of 0 means that the fixup was successful.
	 */
	public int fixForward(ExDesignNode currentTrial) {
		if (nonAdaptive)
			return 0;
		ArrayList trials = ((ExDesignNode) currentTrial.getParent())
				.getChildrenList();
		int currentTrialIndex = trials.indexOf(currentTrial);
		int nextTrialIndex = 0;
		ExParValue[] parValues = null;
		boolean found = false;
		if (adaptiveSequenceIDIndex >= 0) {
			for (nextTrialIndex = currentTrialIndex + 1; nextTrialIndex < trials
					.size(); nextTrialIndex++) {
				parValues = ((ExDesignNode) trials.get(nextTrialIndex))
						.getParValues();
				if (parValues[adaptiveSequenceIDIndex].getInt() == adaptiveSequenceID) {
					found = true;
					// System.out.println("AdaptiveControl.fixForward(): next index = "
					// + nextTrialIndex);
					break;
				}
			}
		} else {
			// Trial does not have an ID
			Debug.show(Debug.ADAPTIVE,
					"AdaptiveControl.fixForward() no adaptive sequence ID in trial.");
			return 1;
		}
		if (!found) {
			// Trial list exhausted!
			Debug.show(Debug.ADAPTIVE,
					"AdaptiveControl.fixForward() Trial list for adaptive sequence ID "
							+ adaptiveSequenceID + " exhausted.");
			return 2;
		}
		// Increase the adaptive trial counter
		setForwardParValue(parValues, adaptiveTrialCounterIndex,
				ExPar.AdaptiveTrialCounter.getInt() + 1);
		int state = ExPar.AdaptiveState.getInt();
		int response = ExPar.get(ExPar.AdaptiveResponseParameter.getString())
				.getInt();
		int nxtState = nextState(state, response);
		setForwardParValue(parValues, adaptiveStateIndex, nxtState);
		if (nxtState >= 0) {
			if (!fixForwardPar(parValues, nxtState)) {
				// Parameter error. We have to copy the current
				// adaptive parameters to the next trial in order to
				// get a copy.
				setForwardParValue(parValues, adaptiveStateIndex, state);
			}
		}
		boolean stopped = stopCondition(response, parValues, nxtState);
		if (stopped) {
			// System.out.println("AdaptiveControl.fixForward(): stopped - removing trials "
			// + nextTrialIndex);
			if (ExPar.AdaptiveRemoveTrailingTrials.getFlag()) {
				for (int i = trials.size() - 1; i > nextTrialIndex; i--) {
					ExParValue[] pv = ((ExDesignNode) trials.get(i))
							.getParValues();
					if (pv[adaptiveSequenceIDIndex].getInt() == adaptiveSequenceID) {
						trials.remove(i);
					}
				}
			}
		}
		if (Debug.isActive(Debug.ADAPTIVE) || ExPar.AdaptiveProtocol.getFlag())
			protocol(parValues);
		return 0;
	}

	/**
	 * Return true if the current trial should actually be executed. A trial is
	 * executed if either it belongs to a non-adaptive sequence or its current
	 * adaptive state is non-negative.
	 * 
	 * @return true if this is not an adaptive procedure or if the adaptive
	 *         procedure has not yet reached its final state.
	 */
	public boolean execTrial() {
		// System.out.println("AdaptiveControl.execTrial(): checking state " +
		// ExPar.AdaptiveState.getInt());
		return nonAdaptive || (ExPar.AdaptiveState.getInt() >= 0);
	}

	/** Set a parameter of the forward trial's argument list. */
	private void setForwardParValue(ExParValue[] pv, int i, int value) {
		if (i < pv.length) {
			pv[i].set(value);
		} else {
			new ParameterValueError("Adaptive parameter error.");
		}
	}

	/** Set a parameter of the forward trial's argument list. */
	private void setForwardParValue(ExParValue[] pv, int i, double value) {
		if (i < pv.length) {
			pv[i].set(value);
		} else {
			new ParameterValueError("Adaptive parameter error.");
		}
	}

	/** Show detailed protocol of the adaptive sequence. */
	private void protocol(ExParValue[] parValues) {
		System.out
				.println("Adaptive Control ---------------------------------------------");
		System.out.println("     Sequence ID = " + adaptiveSequenceID);
		System.out.println("  Procedure Type = " + adaptiveProcedure);
		System.out.println("   Stopping Rule = " + stoppingRule);
		System.out.println("   Trial Counter = "
				+ ExPar.AdaptiveTrialCounter.getInt());
		System.out
				.println("   Current State = " + ExPar.AdaptiveState.getInt());
		System.out.println("        Response = "
				+ ExPar.get(ExPar.AdaptiveResponseParameter.getString())
						.getInt());
		System.out.println("      Next State = "
				+ parValues[adaptiveStateIndex].getInt());
		System.out.println("     Turn Points = "
				+ ExPar.AdaptiveTurnPointCounter.getInt());
		System.out.println("    Step Divisor = "
				+ parValues[adaptiveStepDivisorIndex].getDouble());
		System.out.println("   Next Stimulus = "
				+ parValues[adaptiveParameterIndex].getDouble());
	}

	/**
	 * Get the next state according to the transition matrix of this sequence,
	 * the current state of the adaptive sequence, and the response code for the
	 * current state.
	 * 
	 * @param state
	 *            the current trial's adaptive state
	 * @param response
	 *            the response code for the current trial
	 * @return the state for the next trial in the same adaptive sequence.
	 */
	private int nextState(int state, int response) {
		int stopFactor = 1;
		if (state >= 0) {
			if ((response == stopKey) && (stoppingRule == STOPKEY_RESPONSE)) {
				return -state;
			}
		} else {
			return state;
		}
		if ((response != yesKey) && (response != noKey)) {
			responseError = true;
			return state;
		}
		int[][] stateTransitionMatrix = getStateTransitionMatrix();
		if ((state < 0) || (state >= stateTransitionMatrix.length)) {
			new ParameterValueError("Illegal adaptive state: " + state
					+ ". Setting state 0.");
			state = 0;
		}
		// We get here if response and state are OK
		int nxtState = stateTransitionMatrix[state][(response == yesKey) ? 0
				: 1];
		// System.out.println("AdaptiveControl.nextState(): next state = " +
		// nxtState);
		return nxtState;
	}
	// NOTE that method isTurningPoint() might need a modification if
	// the following state transition tables are being changed. */
	private static final int nonChangingTransform[][] = {
	/* Yes No */
	{ 0, 0 } }; /* Start */
	private static final int up1down1Transform[][] = {
	/* Yes No */
	{ 4, 3 }, /* Start */
	{ 2, 3 }, /* YN */
	{ 4, 1 }, /* NY */
	{ 2, 3 }, /* NN */
	{ 4, 1 } }; /* YY */
	private static final int up1down2Transform[][] = {
	/* Yes No */
	{ 4, 3 }, /* Start */
	{ 6, 3 }, /* YYN */
	{ 7, 1 }, /* YY */
	{ 6, 3 }, /* NN */
	{ 7, 1 }, /* YYYY */
	{ 6, 3 }, /* NYN */
	{ 2, 5 }, /* NY */
	{ 4, 1 } }; /* YYY */
	private static final int up1down3Transform[][] = {
	/* Yes No */
	{ 4, 3 }, /* Start */
	{ 6, 3 }, /* YYYN */
	{ 8, 1 }, /* NYYY */
	{ 6, 3 }, /* NN */
	{ 8, 1 }, /* YYYYYY */
	{ 6, 3 }, /* NYN */
	{ 7, 5 }, /* NY */
	{ 2, 1 }, /* NYY */
	{ 9, 1 }, /* NYYYY */
	{ 4, 1 } }; /* NYYYYY */

	/** Get the state transition matrix for this adaptive control object. */
	private int[][] getStateTransitionMatrix() {
		switch (adaptiveProcedure) {
		case NON_ADAPTIVE:
		case NON_CHANGING:
			return (nonChangingTransform);
		case UP_1_DOWN_1:
		case DELAYED_UP_1_DOWN_1:
			return (up1down1Transform);
		case UP_1_DOWN_2:
			return (up1down2Transform);
		case UP_1_DOWN_3:
			return (up1down3Transform);
		}
		return (up1down1Transform);
	}

	/**
	 * Fix the parameters of the forward trial.
	 * 
	 * @param parValues
	 *            the experimental parameter array of the next trial in this
	 *            adaptive sequence.
	 * @param state
	 *            the state of the next trial in this adaptive sequence.
	 * @return true if a successfull fixup was possible. False if the trial
	 *         should be repeated.
	 */
	private boolean fixForwardPar(ExParValue[] parValues, int state) {
		boolean retVal = true;
		switch (adaptiveProcedure) {
		case NON_ADAPTIVE:
			break;
		case NON_CHANGING:
			fixNoChange(parValues, state);
			break;
		case UP_1_DOWN_1:
			retVal = fixUp1Down1(parValues, state);
			break;
		case UP_1_DOWN_2:
			retVal = fixUp1Down2(parValues, state);
			break;
		case UP_1_DOWN_3:
			retVal = fixUp1Down3(parValues, state);
			break;
		case DELAYED_UP_1_DOWN_1:
			retVal = fixDelayedUp1Down1(parValues, state);
			break;
		}
		return retVal;
	}

	private void fixNoChange(ExParValue[] parValues, int state) {
		parValues[adaptiveParameterIndex].set(ExPar.get(adaptiveParameterName)
				.getDouble());
	}

	private boolean fixParAndStepDiv(ExParValue[] parValues, double step,
			double stepdiv) {
		boolean retVal = true;
		double p_old = ExPar.get(adaptiveParameterName).getDouble();
		double p_new = p_old + step;
		if (limitedRange) {
			if (p_new < adaptiveParameterLowerLimit) {
				retVal = false;
			} else if (p_new > adaptiveParameterUpperLimit) {
				retVal = false;
			}
		}
		// Change forward trial only if fixup was successful
		if (retVal) {
			parValues[adaptiveParameterIndex].set(p_new);
			setForwardParValue(parValues, adaptiveStepDivisorIndex, stepdiv);
		} else {
			parValues[adaptiveParameterIndex].set(p_old);
			setForwardParValue(parValues, adaptiveStepDivisorIndex,
					ExPar.AdaptiveStepDivisor.getDouble());
		}
		return retVal;
	}

	private boolean fixUp1Down1(ExParValue[] parValues, int state) {
		double stepdiv = ExPar.AdaptiveStepDivisor.getDouble();
		if (ExPar.AdaptiveState.getInt() != 0) {
			switch (state) {
			case 1:
			case 2:
				stepdiv += ExPar.AdaptiveStepDivisorIncrement.getDouble();
				break;
			case 3:
			case 4:
				stepdiv = getDecremented(stepdiv);
				break;
			}
		}
		double step = 0.0;
		switch (state) {
		case 1:
			step = ExPar.AdaptiveUpwardStepFactor.getDouble()
					* getStep(stepdiv);
			break;
		case 2:
			step = -getStep(stepdiv);
			break;
		case 3:
			step = ExPar.AdaptiveUpwardStepFactor.getDouble()
					* getStep(stepdiv);
			break;
		case 4:
			step = -getStep(stepdiv);
			break;
		}
		return fixParAndStepDiv(parValues, step, stepdiv);
	}

	private boolean fixUp1Down2(ExParValue[] parValues, int state) {
		double stepdiv = ExPar.AdaptiveStepDivisor.getDouble();
		if (ExPar.AdaptiveState.getInt() != 0) {
			switch (state) {
			case 1:
			case 2:
				stepdiv += ExPar.AdaptiveStepDivisorIncrement.getDouble();
				break;
			case 3:
			case 4:
				stepdiv = getDecremented(stepdiv);
				break;
			default:
				break;
			}
		}
		double step = 0.0;
		switch (state) {
		case 1:
		case 3:
		case 5:
			step = getStep(stepdiv);
			break;
		case 2:
		case 4:
			step = -getStep(stepdiv);
			break;
		case 6:
		case 7:
			step = 0.0;
			break;
		}
		return fixParAndStepDiv(parValues, step, stepdiv);
	}

	private boolean fixUp1Down3(ExParValue[] parValues, int state) {
		double stepdiv = ExPar.AdaptiveStepDivisor.getDouble();
		if (ExPar.AdaptiveState.getInt() != 0) {
			switch (state) {
			case 1:
			case 2:
				stepdiv += ExPar.AdaptiveStepDivisorIncrement.getDouble();
				break;
			case 3:
			case 4:
				stepdiv = getDecremented(stepdiv);
				break;
			default:
				break;
			}
		}
		double step = 0.0;
		switch (state) {
		case 1:
		case 3:
		case 5:
			step = getStep(stepdiv);
			break;
		case 2:
		case 4:
			step = -getStep(stepdiv);
			break;
		case 6:
		case 7:
		case 8:
		case 9:
			step = 0.0;
			break;
		}
		return fixParAndStepDiv(parValues, step, stepdiv);
	}

	private boolean fixDelayedUp1Down1(ExParValue[] parValues, int state) {
		double stepdiv = ExPar.AdaptiveStepDivisor.getDouble();
		if (ExPar.AdaptiveState.getInt() != 0) {
			switch (state) {
			case 1:
			case 2:
				stepdiv = 1.0 + ExPar.AdaptiveStepDivisorIncrement.getDouble()
						* ExPar.AdaptiveTrialCounter.getInt();
				break;
			case 3:
			case 4:
				break;
			}
		}
		double step = 0.0;
		switch (state) {
		case 1:
		case 3:
			step = getStep(stepdiv);
			break;
		case 2:
		case 4:
			step = -getStep(stepdiv);
			break;
		}
		return fixParAndStepDiv(parValues, step, stepdiv);
	}

	/**
	 * Compute the step size. Following a suggestion of M�ltner (1995), the step
	 * size is not computed by s(i)=c/i as usual but is computed according to
	 * the formula:
	 * 
	 * <p>
	 * s(i) = c * (1 + k)/(i + k)
	 * 
	 * <p>
	 * where c is the initial step size and i is the trial counter. The value
	 * for k is set to k = 3 as suggested by M�ltner. This formula has a smaller
	 * initial step size which makes it easier to return after error responses
	 * at the beginning of a sequence.
	 * 
	 * <p>
	 * M�ltner, A. (1995). Sequentielle Verfahren zur Bestimmung von
	 * psychophysikalischen Schwellen in der Interozeption. Disseration,
	 * Universit�t Mannheim.
	 * 
	 * @param stepdiv
	 *            the value of the parameter AdaptiveStepDivisor computed for
	 *            this trial.
	 * @return the step size for changing the adaptive stimulus. Also takes the
	 *         minimum step size into account.
	 */
	private double getStep(double stepdiv) {
		double s, t, retval = 0.0;
		s = ExPar.AdaptiveStepSize.getDouble() / stepdiv;
		t = ExPar.AdaptiveStepSizeMinimum.getDouble();
		if (Math.abs(s) > Math.abs(t)) {
			retval = s;
			atMinimumStepSize = false;
		} else {
			retval = t;
			atMinimumStepSize = true;
		}
		return retval;
	}

	/**
	 * Decrement the step divisor parameter but make sure that the step divisor
	 * does not get smaller than its decrement value.
	 */
	private double getDecremented(double stepdiv) {
		double s = ExPar.AdaptiveStepDivisorDecrement.getDouble();
		stepdiv -= s;
		if (stepdiv < s) {
			stepdiv = s;
		}
		return stepdiv;
	}

	/**
	 * Check whether this trial satisfies the stopping rule.
	 * 
	 * @param response
	 *            the response code for this trial.
	 * @param parValues
	 *            the experimental parameter array of the next trial in this
	 *            sequence.
	 * @param state
	 *            the adaptive procedure state of the next trial in this
	 *            sequence.
	 * @return true if this adaptive sequence satisfies the stopping rule and
	 *         thus is finished.
	 */
	private boolean stopCondition(int response, ExParValue[] parValues,
			int state) {
		if (stoppingRule == DONT_STOP) {
			return false;
		}
		if (state < 0) {
			// This includes the StopKeyResponse case
			return true;
		}
		if (adaptiveTurnPointCounterIndex >= 0) {
			int tc = ExPar.AdaptiveTurnPointCounter.getInt();
			if (isTurnPoint(state)) {
				if (stoppingRule == TURNPOINTS) {
					tc++;
				} else if (stoppingRule == TURNPOINTS_AT_MINIMUM) {
					if (atMinimumStepSize) {
						tc++;
					}
				}
			}
			ExPar.AdaptiveTurnPointCounter.set(tc);
			setForwardParValue(parValues, adaptiveTurnPointCounterIndex, tc);
		}
		boolean stp = false;
		if ((stoppingRule == TURNPOINTS)
				|| (stoppingRule == TURNPOINTS_AT_MINIMUM)) {
			if (ExPar.AdaptiveTurnPointCounter.getInt() >= ExPar.AdaptiveTurnPointLimit
					.getInt()) {
				if (adaptiveProcedure != NON_CHANGING) {
					setForwardParValue(parValues, adaptiveStateIndex, -state);
					stp = true;
				}
			}
		}
		return stp;
	}

	// -----------------------------------------------------------------------------
	// The rest of the file are (static ??) methods which are mainly used
	// for computing the results of adaptive procedures.
	// -----------------------------------------------------------------------------
	/**
	 * Return true if the given state is a turning point. Note that it actually
	 * is the state of the next display that indicates whether the current
	 * display is a turning point. This check relies on the fact that the first
	 * two non-start states of the list of states are the turning points. Note
	 * that this refers to the forward-states, not to the current states!
	 * 
	 * @param state
	 *            the adaptive state of the next trial in this sequence.
	 * @return true if the current trial is a turning point.
	 */
	private boolean isTurnPoint(int state) {
		if (state < 0) {
			state *= -1;
		}
		return ((state == 1) || (state == 2));
	}

	/**
	 * Get the adaptive sequence ID for the given trial node.
	 * 
	 * @param trial
	 *            the trial node whose ID will be returned. Note that this
	 *            requires that the parameter AdaptiveSequenceID must be a trial
	 *            parameter.
	 */
	private int getID(ExDesignNode trial) {
		return trial.getParValue("AdaptiveSequenceID").getInt();
	}

	/**
	 * Compute the adaptive procedure results for all trials with this adaptive
	 * control's ID contained in the given list of trial nodes. The result of
	 * the computation is entered into the parameter AdaptiveResult. If these
	 * are trial parameters then the results are entered into the last trial of
	 * the sequence.
	 * 
	 * @param dataTrials
	 *            a list of trial nodes.
	 */
	public void computeResult(ArrayList dataTrials) {
		if ((dataTrials == null) || (dataTrials.size() == 0))
			return;
		ArrayList pva = new ArrayList(60);
		ExParValue[] pv;
		for (int i = 0; i < dataTrials.size(); i++) {
			pv = ((ExDesignNode) dataTrials.get(i)).getParValues();
			if ((pv != null)
					&& (pv[adaptiveSequenceIDIndex] != null)
					&& (pv[adaptiveSequenceIDIndex].getInt() == adaptiveSequenceID)) {
				pva.add(pv);
			}
		}
		int turnPointLimit = ExPar.AdaptiveTurnPointLimit.getInt();
		int computingPoints = ExPar.AdaptiveComputingPoints.getInt();
		if (computingPoints > turnPointLimit) {
			computingPoints = turnPointLimit;
		}
		int n = 0;
		switch (adaptiveResultComputation) {
		case TURNPOINT_MEAN:
			n = computeTurnPointMean(pva, computingPoints);
			break;
		case TAIL_MEAN:
			n = computeMeanOfStimuli(pva, computingPoints);
			break;
		case LOGISTIC:
		case WEIBULL:
		case GUMBEL:
			n = computePsychometricFunction(adaptiveResultComputation, pva,
					computingPoints);
			break;
		case ISOTONIC_REGRESSION:
			n = computeIsotonicRegression(adaptiveResultComputation, pva);
			break;
		}
		pv = (ExParValue[]) pva.get(pva.size() - 1);
		if (adaptiveResultsIndex >= 0) {
			pv[adaptiveResultsIndex].set(adaptiveResults);
		} else {
			ExPar.AdaptiveResults.set(adaptiveResults);
		}
		if (adaptiveTurnPointCounterIndex >= 0) {
			pv[adaptiveTurnPointCounterIndex].set(n);
		}
	}

	/**
	 * Compute the mean of those stimuli which are turn point stimuli. These are
	 * stimuli where the subject's response differed from the previous response.
	 * 
	 * @param pva
	 *            the list of experimental parameter arrays of all trials
	 *            contained in the adaptive sequence.
	 * @param computingPoints
	 *            the maximum number of turn points to be used for computing the
	 *            result. This number should be even in order to avoid a bias.
	 * @return the number of data points actually used to estimate the results.
	 */
	private int computeTurnPointMean(ArrayList pva, int computingPoints) {
		double s = 0.0;
		double ss = 0.0;
		double x = 0.0;
		int n = 0;
		ExParValue[] pv;
		// Start from the last trial and find all turn points. Note
		// that the state of trial n indicates whether trial n-1 is a
		// turn point!
		for (int i = pva.size() - 1; (i > 0) && (n < computingPoints); i--) {
			pv = (ExParValue[]) pva.get(i);
			int state = Math.abs(pv[adaptiveStateIndex].getInt());
			if (isTurnPoint(state)) {
				x = ((ExParValue[]) pva.get(i - 1))[adaptiveParameterIndex]
						.getDouble();
				n++;
				s += x;
				ss += x * x;
			}
		}
		// Remove the last value if the number of values is odd
		if ((n % 2) != 0) {
			s -= x;
			ss -= (x * x);
			n--;
		}
		adaptiveResults = new double[2];
		if (n > 0) {
			adaptiveResults[0] = s / n;
		} else {
			adaptiveResults[0] = 0.0;
		}
		if (n > 1) {
			adaptiveResults[1] = Math.sqrt((ss - s * s / (double) n)
					/ (double) (n - 1));
		} else {
			adaptiveResults[1] = 0.0;
		}
		return n;
	}

	/**
	 * Compute the mean of those stimuli which were presented last in the
	 * adaptive sequence.
	 * 
	 * @param pva
	 *            the list of experimental parameter arrays of all trials
	 *            contained in the adaptive sequence.
	 * @param computingPoints
	 *            the number of stimuli to be used for computing the result.
	 * @return the number of data points actually used to estimate the results.
	 */
	private int computeMeanOfStimuli(ArrayList pva, int computingPoints) {
		double s = 0.0;
		double ss = 0.0;
		double x;
		int n = 0;
		for (int i = pva.size() - 1; (i > 0) && (n < computingPoints); i--) {
			x = ((ExParValue[]) pva.get(i))[adaptiveParameterIndex].getDouble();
			n++;
			s += x;
			ss += x * x;
		}
		adaptiveResults = new double[2];
		if (n > 0) {
			adaptiveResults[0] = s / n;
		} else {
			adaptiveResults[0] = 0.0;
		}
		if (n > 1) {
			adaptiveResults[1] = Math.sqrt((ss - s * s / (double) n)
					/ (double) (n - 1));
		} else {
			adaptiveResults[1] = 0.0;
		}
		return n;
	}

	/**
	 * Compute estimators for the parameters of a psychometric function using
	 * ALL available data points. The initial values for the parameter estimates
	 * are the turn point means.
	 * 
	 * @param pmfType
	 *            the type of psychometric function which should be assumed.
	 * @param pva
	 *            the list of experimental parameter arrays of all trials
	 *            contained in the adaptive sequence.
	 * @param computingPoints
	 *            the maximum number of turn points to be used for computing the
	 *            turn point mean which is used as an initial value for the
	 *            final parameter estimation procedure.
	 * @return the number of data points actually used to estimate the results.
	 */
	private int computePsychometricFunction(int pmfType, ArrayList pva,
			int computingPoints) {
		int m = computeTurnPointMean(pva, computingPoints);
		double[] tpr = new double[2];
		tpr[0] = adaptiveResults[0];
		tpr[1] = adaptiveResults[1];
		int n = pva.size() - 1;
		double[] x = new double[n];
		boolean[] r = new boolean[n];
		ExParValue[] pv;
		for (int i = 0; i < n; i++) {
			pv = (ExParValue[]) pva.get(i);
			x[i] = pv[adaptiveParameterIndex].getDouble();
			r[i] = (pv[adaptiveResponseIndex].getInt() == yesKey);
		}
		ExPar.AdaptiveMLEstimation.set(0);
		MinimizationFunction lsq = new MinimizationFunction(pmfType, x, r);
		ExPar.AdaptiveMLEstimation.set(1);
		MinimizationFunction mle = new MinimizationFunction(pmfType, x, r);
		Praxis praxis = new Praxis();
		// praxis.setPrintControl(2);
		praxis.setScaling(10.0);
		System.out
				.println("AdaptiveControl.computePsychometricFunction(): \n\n");
		System.out
				.println("AdaptiveControl.computePsychometricFunction(): Initial values: ");
		System.out
				.println("AdaptiveControl.computePsychometricFunction():    c = "
						+ tpr[0]);
		System.out
				.println("AdaptiveControl.computePsychometricFunction():    a = "
						+ tpr[1]);
		System.out
				.println("AdaptiveControl.computePsychometricFunction():  ssq = "
						+ lsq.valueOf(tpr));
		adaptiveResults = new double[2];
		adaptiveResults[0] = tpr[0];
		adaptiveResults[1] = tpr[1];
		praxis.minimize(lsq, adaptiveResults);
		System.out
				.println("AdaptiveControl.computePsychometricFunction(): Estimated values: ");
		System.out
				.println("AdaptiveControl.computePsychometricFunction():    c = "
						+ adaptiveResults[0]);
		System.out
				.println("AdaptiveControl.computePsychometricFunction():    a = "
						+ adaptiveResults[1]);
		System.out
				.println("AdaptiveControl.computePsychometricFunction():  ssq = "
						+ lsq.valueOf(adaptiveResults));
		adaptiveResults[0] = tpr[0];
		adaptiveResults[1] = tpr[1];
		praxis.minimize(mle, adaptiveResults);
		System.out
				.println("AdaptiveControl.computePsychometricFunction(): Estimated values: ");
		System.out
				.println("AdaptiveControl.computePsychometricFunction():    c = "
						+ adaptiveResults[0]);
		System.out
				.println("AdaptiveControl.computePsychometricFunction():    a = "
						+ adaptiveResults[1]);
		System.out
				.println("AdaptiveControl.computePsychometricFunction():  ssq = "
						+ mle.valueOf(adaptiveResults));
		return n;
	}
	/**
	 * This class contains the function which is minimized during parameter
	 * estimation. Two methods are implemented: Least square error minimization
	 * and maximum likelihood estimation. Both methods operate on single
	 * stimulus presentation data.
	 */
	private class MinimizationFunction implements PraxisFunction {
		private double[] stim;
		private boolean[] yes;
		private int n;
		private PsychometricFunction pmf;
		private double guessing;
		private double lapsing;
		private boolean maximumLikelihoodEstimation;

		/**
		 * Create a minimization function.
		 * 
		 * @param type
		 *            the type of psychometric function which should be assumed.
		 *            Currently three function types are supported: the logistic
		 *            function, the Weibull, and the Gumbel function.
		 * @param stim
		 *            an array which contains all stimulus values for the
		 *            adaptive sequence.
		 * @param yes
		 *            an array which contains the responses for the respective
		 *            entries in the stimulus array.
		 */
		public MinimizationFunction(int type, double[] stim, boolean[] yes) {
			this.stim = stim;
			this.yes = yes;
			n = stim.length;
			switch (type) {
			case WEIBULL:
				pmf = new WeibullPMF();
				break;
			case GUMBEL:
				pmf = new GumbelPMF();
				break;
			case LOGISTIC:
			default:
				pmf = new LogisticPMF();
				break;
			}
			maximumLikelihoodEstimation = ExPar.AdaptiveMLEstimation.getFlag();
			guessing = ExPar.AdaptiveGuessingRate.getDouble();
			lapsing = ExPar.AdaptiveLapsingRate.getDouble();
		}

		/**
		 * This function is minimized by the minimization method
		 * (de.pxlab.util.Praxis). If maximumLikelihoodEstimation is false then
		 * we use a least square error minimization procedure. If
		 * maximumLikelihoodEstimation is true then we maximize the likelihood
		 * function for the given series of responses.
		 * 
		 * @param par
		 *            the array of parameters for the psychometric function.
		 *            par[0] ist the point of subjective equality and par[1] is
		 *            the just noticable difference.
		 */
		public double valueOf(double[] par) {
			double pse = par[0];
			double jnd = par[1];
			double p;
			double s = 0.0;
			if (maximumLikelihoodEstimation) {
				s = -1.0;
				for (int i = 0; i < n; i++) {
					p = pmf.valueOf(stim[i], pse, jnd, guessing, lapsing);
					s *= (2.0 * (yes[i] ? p : (1.0 - p)));
				}
			} else {
				double d;
				for (int i = 0; i < n; i++) {
					p = pmf.valueOf(stim[i], pse, jnd, guessing, lapsing);
					d = yes[i] ? (1.0 - p) : p;
					s += d * d;
				}
			}
			return s;
		}
	}
	/**
	 * A logistic psychometric function:
	 * <p>
	 * p(yes|x) = 1/(1+exp((c-x)/a))
	 * <p>
	 * including parameters for guessing and lapsing.
	 */
	private class LogisticPMF implements PsychometricFunction {
		public double valueOf(double x, double pse, double jnd, double lo,
				double hi) {
			return lo + (1.0 - lo - hi)
					* (1.0 / (1.0 + Math.exp((pse - x) / jnd)));
		}
	}
	/**
	 * A Weibull psychometric function:
	 * <p>
	 * p(yes|x) = 1 - exp(-(x/c)**a)
	 * <p>
	 * including parameters for guessing and lapsing.
	 */
	private class WeibullPMF implements PsychometricFunction {
		public double valueOf(double x, double pse, double jnd, double lo,
				double hi) {
			return lo + (1.0 - lo - hi)
					* (1.0 - Math.exp(-Math.pow(x / pse, jnd)));
		}
	}
	/**
	 * A Gumbel psychometric function:
	 * <p>
	 * p(yes|x) = 1 - exp(-exp((x-c)/a))
	 * <p>
	 * including parameters for guessing and lapsing.
	 */
	private class GumbelPMF implements PsychometricFunction {
		public double valueOf(double x, double pse, double jnd, double lo,
				double hi) {
			return lo + (1.0 - lo - hi)
					* (1.0 - Math.exp(-Math.exp((x - pse) / jnd)));
		}
	}

	/**
	 * Compute estimators for the parameters of a psychometric function using
	 * ALL available data points. The initial values for the parameter estimates
	 * are the turn point means.
	 * 
	 * @param pmfType
	 *            the type of psychometric function which should be assumed.
	 * @param pva
	 *            the list of experimental parameter arrays of all trials
	 *            contained in the adaptive sequence.
	 * @param adaptiveStateIndex
	 *            the index of the state parameter in the argument list of a
	 *            trial.
	 * @param adaptiveParameterIndex
	 *            the index of the adaptive stimulus parameter in the argument
	 *            list of a trial.
	 * @param adaptiveResponseIndex
	 *            the index of the response parameter in the argument list of a
	 *            trial.
	 * @param computingPoints
	 *            the maximum number of turn points to be used for computing the
	 *            turn point mean which is used as an initial value for the
	 *            final parameter estimation procedure.
	 * @param results
	 *            this array is used to return the estimates for the pse and the
	 *            jnd.
	 * @return the number of data points actually used to estimate the results.
	 */
	private int computeIsotonicRegression(int pmfType, ArrayList pva) {
		int n = pva.size() - 1;
		System.out.println("AdaptiveControl.computeIsotonicRegression(): " + n
				+ " data points.");
		double[] x = new double[n];
		double[] r = new double[n];
		int yKey = ExPar.YesKey.getInt();
		ExParValue[] pv;
		for (int i = 0; i < n; i++) {
			pv = (ExParValue[]) pva.get(i);
			x[i] = pv[adaptiveParameterIndex].getDouble();
			r[i] = (pv[adaptiveResponseIndex].getInt() == yKey) ? 1.0 : 0.0;
			System.out.println("AdaptiveControl.computeIsotonicRegression(): "
					+ i + ": x=" + x[i] + ", r=" + r[i]);
		}
		IsotonicPMF isr = new IsotonicPMF(x, r);
		System.out
				.println("AdaptiveControl.computeIsotonicRegression(): Estimated values: ");
		System.out
				.println("AdaptiveControl.computeIsotonicRegression():  ssq = "
						+ isr.getChiSquare());
		adaptiveResults = new double[adaptiveQuantiles.length];
		for (int i = 0; i < adaptiveQuantiles.length; i++) {
			adaptiveResults[i] = isr.argumentFor(adaptiveQuantiles[i]);
			System.out
					.println("AdaptiveControl.computeIsotonicRegression(): quantile "
							+ adaptiveQuantiles[i] + ": " + adaptiveResults[i]);
		}
		return n;
	}
}
