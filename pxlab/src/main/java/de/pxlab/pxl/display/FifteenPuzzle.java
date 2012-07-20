package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/**
 * The fifteen tiels puzzle as invented by Sam Loyd.
 * 
 * <p>
 * Here is a description copied from <a
 * href="http://www.cut-the-knot.com/pythagoras/fifteen.shtml">
 * http://www.cut-the-knot.com/pythagoras/fifteen.shtml</a>
 * 
 * <p>
 * As probably every one knows, the purpose of the puzzle is to get the original
 * ordering of the counters after they have been randomly reshuffled. The only
 * allowed moves are sliding counters into the empty square. The puzzle's theory
 * says that there are two groups of starting configurations. Configurations in
 * the first could eventually be solved whereas configurations in the second are
 * unsolvable. The difference between the two is that configurations in the
 * former group can be obtained by acting backwards - starting with the target
 * ordering and just randomly sliding the counters. Configurations of the
 * unsolvable group are obtained when, in addition, two neighboring counters are
 * physically lifted and their positions swapped.
 * 
 * <p>
 * Note that this Display automaticaly turns on double buffered display mode in
 * order to avoid strong flickering during mouse movement.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/13/02
 */
public class FifteenPuzzle extends GameBoard {
	private static final int EMPTY_POSITION = 0;
	/** Tile color. */
	public ExPar TileColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.DARK_GRAY)), "Tile Color");
	/** Letter/digit color. */
	public ExPar LetterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "Letter/Digit Color");
	/** Number of rows in the board. */
	public ExPar NumberOfRows = new ExPar(SMALL_INT, new ExParValue(4),
			"Number of Rows");
	/** Number of columns in the board. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(4),
			"Number of Columns");
	/** Number of columns in the board. */
	public ExPar ScrambleCount = new ExPar(SMALL_INT, new ExParValue(100),
			"Board Scrambling Count");

	public FifteenPuzzle() {
		setTitleAndTopic("Fifteen Puzzle", PROBLEM_SOLVING_DSP);
	}
	/**
	 * Number of rows during the most recent call to computeGeometry().
	 */
	protected int rows;
	/**
	 * Number of columns during the most recent call to computeGeometry().
	 */
	protected int columns;
	/** Holds the board state. */
	private int[] board = null;
	/** Holds the goal state. */
	private int[] goal = null;
	/** Width of a single chip column. */
	private int chipSize;
	/** Width of the gap between chips. */
	private int borderSize;

	protected void computeGeometry() {
		int r = NumberOfRows.getInt();
		int c = NumberOfColumns.getInt();
		int rc = r * c;
		if ((rows != r) || (columns != c)) {
			rows = r;
			columns = c;
			board = new int[rc];
			setState(new ExParValue(-1));
			goal = new int[rc];
		}
		// define the gap between disks
		int relGap = 1;
		int relDiv = 10;
		// intended width
		int sw = Width.getInt();
		// diameter of a single disk
		chipSize = relDiv * sw / (columns * relDiv + 2 * relGap);
		borderSize = relGap * chipSize / relDiv;
		// effective width/height of the board
		int board_w = columns * chipSize + 2 * borderSize;
		int board_h = rows * chipSize + 2 * borderSize;
		int board_x = -board_w / 2;
		int board_y = -board_h / 2;
		getDisplayElement(boardIdx).setRect(board_x, board_y, board_w, board_h);
		setState(CurrentState.getValue());
		int[] g = GoalState.getIntArray();
		if (g[0] == -1) {
			for (int i = 0; i < (rc - 1); i++) {
				goal[i] = i + 1;
			}
			goal[rc - 1] = EMPTY_POSITION;
		} else {
			for (int i = 0; (i < rc) && (i < g.length); i++)
				goal[i] = g[i];
		}
	}

	/** Overrides the Display class show() method. */
	public void show() {
		super.show();
		showGroup();
	}

	/** Overrides the Display class showGroup() method. */
	public void showGroup() {
		// System.out.println("FifteenPuzzle.showGroup()");
		super.showGroup();
		graphics2D.setFont(new Font("SansSerif", Font.BOLD, chipSize / 2));
		FontMetrics fm = graphics2D.getFontMetrics();
		Rectangle b = getDisplayElement(boardIdx).getBounds();
		int left_x = b.x + borderSize;
		int xx;
		int yy = b.y + borderSize;
		int k = 0;
		// System.out.println("FifteenPuzzle.showGroup(): state = " + new
		// String(board));
		for (int r = 0; r < rows; r++) {
			xx = left_x;
			for (int c = 0; c < columns; c++) {
				graphics2D.setColor(TileColor.getDevColor());
				graphics2D.fill3DRect(xx, yy, chipSize, chipSize, true);
				int t = board[k];
				if (t != EMPTY_POSITION) {
					String s = String.valueOf(t);
					int fw = fm.stringWidth(s);
					graphics2D.setColor(LetterColor.getDevColor());
					graphics2D.drawString(s, xx + (chipSize - fw) / 2, yy
							+ (chipSize + fm.getAscent()) / 2);
				}
				k++;
				xx += chipSize;
			}
			yy += chipSize;
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
	private void setState(ExParValue state) {
		// System.out.println("FifteenPuzzle.setState() state = " + state);
		int rc = rows * columns;
		int rc1 = rc - 1;
		if (state.getInt() == (-1)) {
			for (int i = 0; i < rc1; i++)
				board[i] = i + 1;
			board[rc1] = EMPTY_POSITION;
			scrambleBoard(ScrambleCount.getInt());
			CurrentState.getValue().set(getState());
		} else {
			int[] s = state.getIntArray();
			for (int i = 0; (i < s.length) && (i < rc); i++) {
				board[i] = s[i];
			}
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
	 * Compute a move from the response coordinates given as arguments.
	 * 
	 * @param downX
	 *            horizontal coordinate of button down response.
	 * @param downY
	 *            vertical coordinate of button down response.
	 * @param upX
	 *            horizontal coordinate of button up response.
	 * @param upY
	 *            vertical coordinate of button up response.
	 * @return the column where the button has been released. The parameter
	 *         value is NO_MOVE if the button was released outside of the game
	 *         board.
	 */
	protected ExParValue getMove(int downX, int downY, int upX, int upY) {
		Rectangle bb = getDisplayElement(boardIdx).getBounds();
		Rectangle b = new Rectangle(bb.x + borderSize, bb.y + borderSize,
				bb.width - 2 * borderSize, bb.height - 2 * borderSize);
		// System.out.println("FifteenPuzzle.getMove(): board size = " + b);
		int m = NO_MOVE;
		if (b.contains(upX, upY)) {
			int r = (upY - b.y) / chipSize;
			int c = (upX - b.x) / chipSize;
			m = r * columns + c;
		}
		// System.out.println("FifteenPuzzle.getMove(): " + m);
		return new ExParValue(m);
	}

	/**
	 * Check whether the given move is admissible for the current state of the
	 * board.
	 * 
	 * @param mv
	 *            a code for the intended move.
	 * @return true if according to the rules the move is admissible and false
	 *         otherwise.
	 */
	protected boolean admissibleMove(ExParValue mv) {
		int rc = rows * columns;
		int p = emptyPosition();
		if (p < 0)
			return false;
		int rp = p / columns;
		int cp = p % columns;
		int m = mv.getInt();
		int r = m / columns;
		int c = m % columns;
		if (cp == c) {
			if ((r > 0) && (rp == (r - 1)))
				return true;
			if ((r < (rows - 1)) && (rp == (r + 1)))
				return true;
		} else if (rp == r) {
			if ((c > 0) && (cp == (c - 1)))
				return true;
			if ((c < (columns - 1)) && (cp == (c + 1)))
				return true;
		}
		return false;
	}

	private int emptyPosition() {
		int rc = rows * columns;
		for (int i = 0; i < rc; i++) {
			if (board[i] == EMPTY_POSITION) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Execute a move if it is admissible. Also update the experimental
	 * parameters which store the move and the state of the game.
	 * 
	 * @param mv
	 *            the column corresponding to the subject's response.
	 * @param player
	 *            the player who makes the move.
	 */
	protected void makeMove(ExParValue mv, int player) {
		int m = mv.getInt();
		int p = emptyPosition();
		board[p] = board[m];
		board[m] = EMPTY_POSITION;
	}

	protected void makeMove(ExParValue m) {
		makeMove(m, FIRST_PLAYER);
	}

	/**
	 * Check out the possible win states. The code value RUNNING means that the
	 * game ist not yet finished. Otherwise either one of the players wins or we
	 * have a draw where no player wins.
	 * 
	 * @return a win state code.
	 */
	protected int winState() {
		int rc = rows * columns;
		boolean win = true;
		for (int i = 0; i < rc; i++) {
			win = win && (board[i] == goal[i]);
		}
		return (win ? FIRST_PLAYER_WINS : RUNNING);
	}

	private void scrambleBoard(int k) {
		if (randomizer == null)
			randomizer = new Randomizer();
		int[] m = new int[4];
		for (int i = 0; i < k; i++) {
			int n = possibleMoves(m);
			int mv = m[randomizer.nextInt(n)];
			makeMove(new ExParValue(mv));
		}
	}

	private int possibleMoves(int[] m) {
		int p = emptyPosition();
		int r = p / columns;
		int c = p % columns;
		int k = 0;
		if (r > 0)
			m[k++] = (r - 1) * columns + c;
		if (r < (rows - 1))
			m[k++] = (r + 1) * columns + c;
		if (c > 0)
			m[k++] = r * columns + c - 1;
		if (c < (columns - 1))
			m[k++] = r * columns + c + 1;
		return k;
	}
}
