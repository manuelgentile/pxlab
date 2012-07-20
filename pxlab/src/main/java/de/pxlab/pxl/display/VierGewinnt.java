package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;
import de.pxlab.util.Randomizer;

/*

 The 'Vier Gewinnt' game as a problem solving task. 

 <p>Note that this Display automaticaly turns on double buffered
 display mode in order to avoid strong flickering during mouse
 movement.

 @author H. Irtel
 @version 0.1.0 

 */
/*

 07/13/02

 */
public class VierGewinnt extends GameBoard {
	/** Empty position code. */
	private static final byte NO_PLAYER_CODE = (byte) '.';
	/** First player position code. */
	private static final byte FIRST_PLAYER_CODE = (byte) 'X';
	/** Second player position code. */
	private static final byte SECOND_PLAYER_CODE = (byte) 'O';
	/** First player chips color. */
	public ExPar FirstPlayerColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)),
			"'Red' Player Chips Color");
	/** Yellow player chips color. */
	public ExPar SecondPlayerColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)),
			"'Yellow' Player Chips Color");
	/** Number of rows in the board. */
	public ExPar NumberOfRows = new ExPar(SMALL_INT, new ExParValue(6),
			"Number of Rows");
	/** Number of columns in the board. */
	public ExPar NumberOfColumns = new ExPar(SMALL_INT, new ExParValue(7),
			"Number of Columns");
	/** Number of columns in the board. */
	public ExPar WinCount = new ExPar(SMALL_INT, new ExParValue(4),
			"Length of Winning Series");

	public VierGewinnt() {
		setTitleAndTopic("Vier Gewinnt", PROBLEM_SOLVING_DSP);
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
	private byte[] board = null;
	/** Width of a single chip column. */
	private int chipSize;
	/** Width of the gap between chips. */
	private int gapSize;

	protected void computeGeometry() {
		int r = NumberOfRows.getInt();
		int c = NumberOfColumns.getInt();
		if ((rows != r) || (columns != c)) {
			rows = r;
			columns = c;
			board = new byte[rows * columns];
			int k = 0;
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					board[k++] = NO_PLAYER_CODE;
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
		int board_h = rows * dcg + gapSize;
		int board_x = -board_w / 2;
		int board_y = -board_h / 2;
		getDisplayElement(boardIdx).setRect(board_x, board_y, board_w, board_h);
		setState(CurrentState.getValue());
	}

	/** Overrides the Display class show() method. */
	public void show() {
		super.show();
		showGroup();
	}

	/** Overrides the Display class showGroup() method. */
	public void showGroup() {
		super.showGroup();
		Rectangle b = getDisplayElement(boardIdx).getBounds();
		int dcg = chipSize + gapSize;
		int left_x = b.x + gapSize;
		int bottom_y = b.y + b.height - dcg;
		int xx;
		int yy = bottom_y;
		int k = 0;
		// System.out.println("VierGewinnt.showGroup(): state = " + new
		// String(board));
		for (int r = 0; r < rows; r++) {
			xx = left_x;
			for (int c = 0; c < columns; c++) {
				byte bk = board[k];
				if (bk == FIRST_PLAYER_CODE) {
					graphics.setColor(FirstPlayerColor.getDevColor());
				} else if (bk == SECOND_PLAYER_CODE) {
					graphics.setColor(SecondPlayerColor.getDevColor());
				} else {
					graphics.setColor(ExPar.ScreenBackgroundColor.getDevColor());
				}
				k++;
				graphics.fillOval(xx, yy, chipSize, chipSize);
				xx += dcg;
			}
			yy -= dcg;
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
		int rc = rows * columns;
		for (int i = 0; i < rc; i++)
			board[i] = NO_PLAYER_CODE;
		String ss = "";
		if (state.getInt() != (-1)) {
			ss = state.getString();
		} else {
			CurrentState.getValue().set(getState());
		}
		byte[] s = ss.getBytes();
		int k = 0;
		for (int i = 0; (i < s.length) && (i < rc); i++) {
			board[k++] = s[i];
		}
	}

	/**
	 * Return the current state of the board.
	 * 
	 * @return a parameter value which contains the current state of the board.
	 */
	protected ExParValue getState() {
		return new ExParValue(new String(board));
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
		int m = NO_MOVE;
		Rectangle b = getDisplayElement(boardIdx).getBounds();
		// System.out.println("VierGewinnt.getMove(): board size = " + b);
		if (b.contains(upX, upY)) {
			int x = b.x + gapSize;
			for (int c = 0; c < columns; c++) {
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
		// System.out.println("VierGewinnt.getMove(): " + m);
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
		return firstFreePositionInColumn(mv) >= 0;
	}

	/**
	 * Execute a move if it is admissible. Also update the experimental
	 * parameters which store the move and the state of the game.
	 * 
	 * @param m
	 *            the column corresponding to the subject's response.
	 * @param p
	 *            the player who makes the move.
	 */
	protected void makeMove(ExParValue m, int p) {
		int mv = m.getInt();
		board[firstFreePositionInColumn(mv) * columns + mv] = (p == FIRST_PLAYER) ? FIRST_PLAYER_CODE
				: SECOND_PLAYER_CODE;
	}

	protected void makeMove(ExParValue m) {
		makeMove(m, FIRST_PLAYER);
	}

	/**
	 * Find the first free position in a column.
	 * 
	 * @param column
	 *            the column where we have to look for the first free position.
	 * @return the row number of the first free position in the given column or
	 *         (-1) if there is none.
	 */
	private int firstFreePositionInColumn(int column) {
		int p = -1;
		for (int i = 0; i < rows; i++) {
			if (board[i * columns + column] == NO_PLAYER_CODE) {
				p = i;
				break;
			}
		}
		return p;
	}

	/** Simulate a second player's move. */
	public ExParValue getSecondPlayerMove() {
		int mv = randomizer.nextInt(columns);
		while (firstFreePositionInColumn(mv) < 0) {
			mv = randomizer.nextInt(columns);
		}
		return new ExParValue(mv);
	}

	/**
	 * Check out the possible win states. The code value RUNNING means that the
	 * game ist not yet finished. Otherwise either one of the players wins or we
	 * have a draw where no player wins.
	 * 
	 * @return a win state code.
	 */
	protected int winState() {
		int count = 0;
		for (int column = 0; column < columns; column++) {
			for (int row = 0; row < rows; row++) {
				if (!chipAtIs(row, column, NO_PLAYER_CODE)) {
					count++;
					if (winRow(row, column, 1, 0, FIRST_PLAYER_CODE))
						return FIRST_PLAYER_WINS;
					if (winRow(row, column, 1, 0, SECOND_PLAYER_CODE))
						return SECOND_PLAYER_WINS;
					if (winRow(row, column, 1, 1, FIRST_PLAYER_CODE))
						return FIRST_PLAYER_WINS;
					if (winRow(row, column, 1, 1, SECOND_PLAYER_CODE))
						return SECOND_PLAYER_WINS;
					if (winRow(row, column, 0, 1, FIRST_PLAYER_CODE))
						return FIRST_PLAYER_WINS;
					if (winRow(row, column, 0, 1, SECOND_PLAYER_CODE))
						return SECOND_PLAYER_WINS;
					if (winRow(row, column, -1, 1, FIRST_PLAYER_CODE))
						return FIRST_PLAYER_WINS;
					if (winRow(row, column, -1, 1, SECOND_PLAYER_CODE))
						return SECOND_PLAYER_WINS;
				}
			}
		}
		if (count == (rows * columns))
			return NO_PLAYER_WINS;
		return RUNNING;
	}

	/**
	 * Check for a winning row starting at the given position.
	 * 
	 * @param x
	 *            horizontal start position.
	 * @param y
	 *            vertical start position.
	 * @param dx
	 *            column increment for subsequent positions in the row.
	 * @param dy
	 *            row increment for subsequent positions in the row.
	 * @param p
	 *            player code for which to check.
	 */
	private boolean winRow(int x, int y, int dx, int dy, int p) {
		boolean win = true;
		int winCount = WinCount.getInt();
		for (int i = 0; win && (i < winCount); i++) {
			win = win && chipAtIs(x, y, p);
			x += dx;
			y += dy;
		}
		return win;
	}

	/**
	 * Check whether the chip at the given row/column position belongs to the
	 * given player.
	 */
	private boolean chipAtIs(int r, int c, int p) {
		return (r >= 0) && (r < rows) && (c >= 0) && (c < columns)
				&& (board[r * columns + c] == p);
	}
}
