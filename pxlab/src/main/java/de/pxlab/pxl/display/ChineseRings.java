package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * The 'Chinese Rings' puzzle as a problem solving task. The puzzle contains a
 * horizontal series of binary chips which may be switched on or off. The
 * initial state usually is all chips being switched on and the task is to
 * switch off all the chips according to the rules.
 * 
 * <p>
 * These are the rules:
 * 
 * <p>
 * The puzzle usually starts with all chips switched on.
 * 
 * <p>
 * The task is to get all chips switched off.
 * 
 * <p>
 * A chip may be switched on or off by pointing at it and pressing a mouse
 * button.
 * 
 * <p>
 * A chip, however, can only then change its state if
 * 
 * <ol>
 * <li>the chip is the rightmost chip, or
 * 
 * <li>all but the next chip to the right are switched off.
 * </ol>
 * 
 * <p>
 * This is all for the rules.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/13/02
 */
public class ChineseRings extends GameBoard {
	/** First player chips color. */
	public ExPar RingColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)),
			"'Red' Player Chips Color");
	/** Number of rings in the board. */
	public ExPar NumberOfRings = new ExPar(SMALL_INT, new ExParValue(5),
			"Number of Rings");

	public ChineseRings() {
		setTitleAndTopic("Chinese Rings", PROBLEM_SOLVING_DSP);
		GoalState.set(0);
	}
	/**
	 * Number of rings during the most recent call to computeGeometry().
	 */
	private int columns;
	/** Holds the board state as a binary pattern. */
	private int board = 0;
	/** Powers of two used as a mask. */
	private int[] p2;
	/** Width of a single chip column. */
	private int chipSize;
	/** Width of the gap between chips. */
	private int gapSize;

	protected int create() {
		int m = 1;
		p2 = new int[31];
		for (int i = 0; i < 31; i++) {
			p2[i] = m;
			m = m << 1;
		}
		return super.create();
	}

	/**
	 * Compute the geometry of the background board and of the single rings.
	 */
	protected void computeGeometry() {
		int c = NumberOfRings.getInt();
		if (columns != c) {
			columns = c;
			if (columns > 31)
				columns = 31;
			board = 0;
		}
		// define the gap between disks
		int relGap = 1;
		int relDiv = 10;
		// intended width
		int sw = Width.getInt();
		// diameter of a single disk
		chipSize = relDiv * sw / (columns * (relDiv + relGap) + relGap);
		gapSize = relGap * chipSize / relDiv;
		int dcg = chipSize + gapSize;
		// effective width/height of the board
		int board_w = columns * dcg + gapSize;
		int board_h = dcg + gapSize;
		int board_x = -board_w / 2;
		int board_y = -board_h / 2;
		// Set background board size
		getDisplayElement(boardIdx).setRect(board_x, board_y, board_w, board_h);
		// Set current game state
		setState(CurrentState.getValue());
	}

	/** Overrides the Display class show() method. */
	public void show() {
		super.show();
		showGroup();
	}

	/**
	 * Overrides the Display class showGroup() method. We don't need to show the
	 * background board but only have to paint the rings at their current
	 * states.
	 */
	public void showGroup() {
		super.showGroup();
		// System.out.println("ChineseRings.show() board = " + board);
		Rectangle b = getDisplayElement(boardIdx).getBounds();
		int dcg = chipSize + gapSize;
		int bottom_y = b.y + b.height - dcg;
		int xx = b.x + gapSize;
		for (int c = columns - 1; c >= 0; c--) {
			if ((board & p2[c]) != 0) {
				graphics.setColor(RingColor.getDevColor());
			} else {
				graphics.setColor(ExPar.ScreenBackgroundColor.getDevColor());
			}
			graphics.fillOval(xx, bottom_y, chipSize, chipSize);
			xx += dcg;
			// System.out.println("ChineseRings.show() column " + c);
		}
	}

	/**
	 * Set the current state of the game. Usually this method will be called by
	 * computeGeometry() in order to set the local state variable to the value
	 * of the parameter CurrentState. Note that by convention if CurrentState is
	 * equal to (-1) then the board is set to a default starting state.
	 * 
	 * @param state
	 *            an experimental parameter value which defines the state of the
	 *            game board.
	 */
	protected void setState(ExParValue state) {
		if (board == -1) {
			board = p2[NumberOfRings.getInt() % 32] - 1;
			CurrentState.set(board);
		} else {
			board = state.getInt();
		}
	}

	/**
	 * Return the current state of the board.
	 * 
	 * @return a parameter value which contains the current state of the board.
	 */
	protected ExParValue getState() {
		return new ExParValue(board);
	}

	/**
	 * Compute a move from the response coordinates given as arguments. Usually
	 * this method is called when a response is finished.
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
	protected ExParValue getMove(int downX, int downY, int upX, int upY) {
		int m = NO_MOVE;
		Rectangle b = getDisplayElement(boardIdx).getBounds();
		if (b.contains(upX, upY)) {
			int x = b.x + gapSize;
			for (int c = columns - 1; c >= 0; c--) {
				if ((upX > x) && (upX < (x + chipSize))) {
					m = c;
					break;
				}
				x += (chipSize + gapSize);
			}
			if ((m < 0) || (m >= columns)) {
				m = NO_MOVE;
			}
		}
		return new ExParValue(m);
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
	protected boolean admissibleMove(ExParValue m) {
		int mv = m.getInt();
		return (mv == 0) || ((board % p2[mv]) == p2[mv - 1]);
	}

	/**
	 * Execute an admissible move. Also update the experimental parameters which
	 * store the move and the state of the game.
	 * 
	 * @param m
	 *            a code for the intended move. Note that this MUST be an
	 *            admissible move.
	 */
	protected void makeMove(ExParValue m) {
		int mv = m.getInt();
		if ((board & p2[mv]) != 0) {
			board = board & ~p2[mv];
		} else {
			board = board | p2[mv];
		}
	}

	/**
	 * Check out the possible win states. The code value RUNNING means that the
	 * game ist not yet finished. Otherwise either one of the players wins or we
	 * have a draw where no player wins.
	 * 
	 * @return a win state code.
	 */
	protected int winState() {
		return (board == GoalState.getInt()) ? FIRST_PLAYER_WINS : RUNNING;
	}
}
