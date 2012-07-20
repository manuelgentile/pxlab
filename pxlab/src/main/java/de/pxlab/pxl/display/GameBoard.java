package de.pxlab.pxl.display;

// import java.awt.*;
import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

abstract public class GameBoard extends Display {
	/**
	 * Indicates that the response does not constitute a move on the game board.
	 */
	public static final int NO_MOVE = -1;
	/**
	 * Indicates that the move is not admissible in the current state of the
	 * game.
	 */
	public static final int ILLEGAL_MOVE = -2;
	/** Game not yet over. */
	public static final int RUNNING = 0;
	/** Game over but no winner. */
	public static final int NO_PLAYER_WINS = 3;
	/** Game over and first player wins. */
	public static final int FIRST_PLAYER_WINS = 1;
	/** Game over and second player wins. */
	public static final int SECOND_PLAYER_WINS = 2;
	/** First player position code. */
	public static final int FIRST_PLAYER = 1;
	/** Second player position code. */
	public static final int SECOND_PLAYER = 2;
	/** Background color of the game board. */
	public ExPar BoardColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)), "Game Board Color");
	/**
	 * The current state of the game. The default value (-1) results in the most
	 * common initial state of the game. Otherwise the various games have their
	 * private methods to store their state.
	 */
	public ExPar CurrentState = new ExPar(STRING, new ExParValue(-1),
			"Current State of the Game");
	/**
	 * The goal state of the problem. If any of the entries is (-1) then the
	 * respective state is ignored.
	 */
	public ExPar GoalState = new ExPar(STRING, new ExParValue(-1),
			"Goal State of the Game");
	/**
	 * Describes the most recent move made by any one of the players. The
	 * various games use different methods to code their moves.
	 */
	public ExPar CurrentMove = new ExPar(RTDATA, new ExParValue(-1),
			"Most Recent Move");
	/**
	 * The current winning state of the game. This parameter may be used to
	 * figure out who one the game.
	 */
	public ExPar WinningState = new ExPar(RTDATA, new ExParValue(RUNNING),
			"Current Winning State");
	/** Board width. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(400),
			"Board Width");
	/** Board height if it applies. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(100),
			"Board Height");
	/** Simulate a second player for 2-person games. */
	public ExPar SimulateSecondPlayer = new ExPar(FLAG, new ExParValue(0),
			"Simulate a Second Player");
	/** Display element index of the background game board. */
	protected int boardIdx;
	/** Used for random player move simulation. */
	protected Randomizer randomizer;

	protected int create() {
		boardIdx = enterDisplayElement(new Bar(BoardColor), group[0]);
		defaultTiming(0);
		DisplayElement.setValidBounds(true);
		return (boardIdx);
	}

	/**
	 * Return the current state of the board.
	 * 
	 * @return a parameter value which contains the current state of the board.
	 */
	abstract protected ExParValue getState();

	/**
	 * Compute a move from the response coordinates given as arguments. Usually
	 * this method is called when a response is finished. It returns a code
	 * which in principle can be a move but whose admissibility for the current
	 * state of the game has not yet been checked.
	 * 
	 * @param downX
	 *            horizontal coordinate of button down response.
	 * @param downY
	 *            vertical coordinate of button down response.
	 * @param upX
	 *            horizontal coordinate of button up response.
	 * @param upY
	 *            vertical coordinate of button up response.
	 * @return a code for the intended move or NO_MOVE if the button was
	 *         released outside of the game board.
	 */
	abstract protected ExParValue getMove(int downX, int downY, int upX, int upY);

	/**
	 * Check whether the given parameter contains the code for a non-move
	 * response. This is true if the response was outside of the game board.
	 * 
	 * @param m
	 *            parameter containing the move code as it is returned by
	 *            getMove().
	 * @return true if the move code indicates a non-move response.
	 */
	protected boolean noMove(ExParValue m) {
		return m.getInt() == NO_MOVE;
	}

	/**
	 * Check whether the given move is admissible for the current state of the
	 * board.
	 * 
	 * @param m
	 *            a code for the intended move.
	 * @return true if according to the rules the move is admissible and false
	 *         otherwise.
	 */
	abstract protected boolean admissibleMove(ExParValue m);

	/**
	 * Execute an admissible move. If there are more than one players involved
	 * then the move should be made by the first player and makeMove(ExParValue,
	 * int) should also be implemented.
	 * 
	 * @param m
	 *            a code for the intended move. Note that this MUST be an
	 *            admissible move.
	 */
	abstract protected void makeMove(ExParValue m);

	/**
	 * Execute an admissible move by the given player.
	 * 
	 * @param m
	 *            a code for the intended move. Note that this MUST be an
	 *            admissible move.
	 * @param p
	 *            the code of the player who should make the move.
	 */
	protected void makeMove(ExParValue m, int p) {
		makeMove(m);
	}

	/**
	 * Return a code for the move of a second player. This is only used in
	 * 2-person games.
	 */
	protected ExParValue getSecondPlayerMove() {
		return new ExParValue(NO_MOVE);
	}

	/**
	 * Check whether the game is in a win state.
	 * 
	 * @return a win state code. The code value RUNNING means that the game ist
	 *         not yet finished. Otherwise either one of the players wins or we
	 *         have a draw where no player wins.
	 */
	abstract protected int winState();

	/** Simulate a second player's move. */
	private void simulateSecondPlayerMove() {
		if (randomizer == null)
			randomizer = new Randomizer();
		ExParValue m = getSecondPlayerMove();
		if (!noMove(m)) {
			makeMove(m, SECOND_PLAYER);
			CurrentState.getValue().set(getState());
		}
	}

	/**
	 * A move is finished after the mouse button has been released. Thus we
	 * execute them here. This is a hook into the mouse button released event
	 * handler.
	 */
	protected boolean pointerReleased() {
		ExParValue mv = getMove(pointerActivationX, pointerActivationY,
				pointerReleaseX, pointerReleaseY);
		// Ignore 'moves' which are not within the area of the game board.
		if (noMove(mv))
			return false;
		CurrentMove.getValue().set(mv);
		// System.out.println("GameBoard.pointerReleased(): CurrentMove = " +
		// CurrentMove);
		if (admissibleMove(mv)) {
			makeMove(mv);
			CurrentState.getValue().set(getState());
			int win = winState();
			if (win != RUNNING) {
				ExPar.BlockState.set(StateCodes.BREAK);
				ExPar.TrialState.set(StateCodes.EXECUTE);
			} else {
				if (SimulateSecondPlayer.getFlag()) {
					simulateSecondPlayerMove();
					win = winState();
					if (win != RUNNING) {
						ExPar.BlockState.set(StateCodes.BREAK);
						ExPar.TrialState.set(StateCodes.EXECUTE);
					} else {
						ExPar.BlockState.set(StateCodes.EXECUTE);
						ExPar.TrialState.set(StateCodes.COPY);
					}
				} else {
					ExPar.BlockState.set(StateCodes.EXECUTE);
					ExPar.TrialState.set(StateCodes.COPY);
				}
			}
			WinningState.set(win);
		} else {
			ExPar.BlockState.set(StateCodes.EXECUTE);
			ExPar.TrialState.set(StateCodes.COPY);
		}
		// System.out.println("GameBoard.pointerReleased(): state = " +
		// CurrentState);
		return true;
	}
}
