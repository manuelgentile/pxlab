package de.pxlab.pxl.display;

import java.awt.Rectangle;

import de.pxlab.pxl.*;

/**
 * A superclass for move problems. These are problems which may be defined by a
 * small number of state parameters and which are to be solved by a series of
 * moves.
 * 
 * <p>
 * After every non-final move the global experimental parameter BlockState is
 * set to StateCodes.EXECUTE and the global experimental parameter TrialState is
 * set to StateCodes.COPY.
 * 
 * <p>
 * After the goal has been detected the global experimental parameter BlockState
 * is set to StateCodes.BREAK and the global experimental parameter TrialState
 * is set to StateCodes.EXECUTE.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public abstract class MoveProblem extends Display {
	/**
	 * This parameter may be used to define an arbitrary start state of the
	 * problem. If it is empty then the default start state will be used.
	 */
	public ExPar StartState = new ExPar(INTEGER, new ExParValue(0),
			"Start state of the game");
	/**
	 * This parameter may be used to define an arbitrary goal state of the
	 * problem. If it is empty then the default goal state will be used.
	 */
	public ExPar GoalState = new ExPar(INTEGER, new ExParValue(0),
			"Goal state of the game");
	/**
	 * Describes the state of the problem after the most recent move has been
	 * made. This parameter is set by the program and must not be set in the
	 * design file.
	 */
	public ExPar ProblemState = new ExPar(RTDATA, new ExParValue(0),
			"Current state of the game");
	/**
	 * Describes the most recent move which has been made. This parameter is set
	 * by the program and must not be set in the design file.
	 */
	public ExPar Move = new ExPar(RTDATA, new ExParValue(-1),
			"Most recent move");
	/** Color of an information text box background. */
	public ExPar BoxColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)), "Message box color");
	/** Color of information text. */
	public ExPar MessageColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Message text color");
	protected int textBox;
	protected Rectangle[] dropRect;
	protected int[] problemState;
	protected int currentMove;
	// Default start and goal positions
	protected int startPosition;
	protected int goalPosition;

	/** Set the problem state to the start state. */
	protected void setStartState() {
		int[] startPositions = StartState.getIntArray();
		if (startPositions.length == problemState.length) {
			// we have an initial state being defined, so use it
			for (int i = 0; i < problemState.length; i++) {
				problemState[i] = startPositions[i];
			}
		} else {
			// no initial state is defined, so use the default start state
			for (int i = 0; i < problemState.length; i++) {
				problemState[i] = startPosition;
			}
		}
		currentMove = -1;
	}

	/**
	 * Finish the current move. This sets the problem state and the move
	 * parameter and also sets the global parameters BlockState and TrialState
	 * according to whether the move reached the goal state or not. If the move
	 * was invalid then a warning text box is shown.
	 * 
	 * @param validMove
	 *            must be true if the move was valid and false otherwise.
	 */
	protected void finishMove(boolean validMove) {
		if (validMove) {
			Move.set(currentMove);
			((TextBox) getDisplayElement(textBox)).setText("");
		} else {
			((TextBox) getDisplayElement(textBox)).setText("Illegal move!");
		}
		ProblemState.set(problemState);
		currentMove = -1;
		if (isFinal()) {
			computeGeometry();
			ExPar.BlockState.set(StateCodes.BREAK);
			ExPar.TrialState.set(StateCodes.EXECUTE);
			setStartState();
		} else {
			ExPar.BlockState.set(StateCodes.EXECUTE);
			ExPar.TrialState.set(StateCodes.COPY);
		}
	}

	/**
	 * Check whether the current problem state is the final goal state.
	 * 
	 * @return true if the current state is the final goal state.
	 */
	protected boolean isFinal() {
		int[] goalState = GoalState.getIntArray();
		if (goalState.length == problemState.length) {
			for (int i = 0; i < problemState.length; i++) {
				if (problemState[i] != goalState[i]) {
					return false;
				}
			}
		} else {
			for (int i = 0; i < problemState.length; i++) {
				if (problemState[i] != goalPosition) {
					return false;
				}
			}
		}
		return true;
	}

	protected boolean pointerDragged() {
		computeGeometry();
		return true;
	}
}
