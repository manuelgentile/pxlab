package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/*

 The 'Water Jugs' problem as used by Luchins (1942).

 <p>Luchins, A. S. (1942). Mechanization in problem
 solving. Psychological Monographs, 54, Nr. 248.

 <p>The task is to fill the jugs such that a certain constellation is
 arrived at. The default problem starts with 8-0-0 and has 4-4-0 as a
 goal.

 <p>The problem is attributed to Sim�on Denis Poisson
 (1781-1840). You may find more information about it at <a
 href="http://www.cut-the-knot.com/water.shtml">
 http://www.cut-the-knot.com/water.shtml</a> which also includes some
 //mathematical background.

 @author H. Irtel
 @version 0.1.0 

 */
/*

 07/15/02

 */
public class WaterJugs extends GameBoard {
	/** Size of first water jug. */
	public ExPar Jug1 = new ExPar(SMALL_INT, new ExParValue(8),
			"Size of First Jug");
	/** Size of second water jug. */
	public ExPar Jug2 = new ExPar(SMALL_INT, new ExParValue(5),
			"Size of Second Jug");
	/** Size of third water jug. */
	public ExPar Jug3 = new ExPar(SMALL_INT, new ExParValue(3),
			"Size of Third Jug");
	/** Color of jugs. */
	public ExPar JugColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "Jug Color");
	/** Color of water. */
	public ExPar WaterColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)), "Water Color");

	public WaterJugs() {
		setTitleAndTopic("Water Jugs", PROBLEM_SOLVING_DSP);
		int[] state = { 8, 0, 0 };
		CurrentState.set(state);
		BoardColor.set(new ExParValueFunction(ExParExpression.GRAY));
		int[] goalstate = { 4, 4, -1 };
		GoalState.set(goalstate);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER"));
	}
	/** Current fill state of the jugs. */
	private int[] fillState;
	/** Size of the jugs. */
	private int[] jugs;
	private int jugWidth, gapSize, wallSize, waterStore, waterUnit;

	protected int create() {
		fillState = new int[3];
		jugs = new int[3];
		return super.create();
	}

	/**
	 * Compute the geometry of the background board and of the single rings.
	 */
	protected void computeGeometry() {
		jugs[0] = Jug1.getInt();
		jugs[1] = Jug2.getInt();
		jugs[2] = Jug3.getInt();
		// intended width
		int sw = Width.getInt();
		int sh = Height.getInt();
		// diameter of a single disk
		wallSize = sw / 32;
		jugWidth = 6 * wallSize;
		gapSize = 2 * wallSize;
		waterStore = Jug1.getInt();
		waterUnit = (sh - 2 * gapSize - wallSize) / waterStore;
		// effective width/height of the board
		int board_w = 32 * wallSize;
		int board_h = waterStore * waterUnit + 2 * gapSize + wallSize;
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
		// System.out.println("WaterJugs.show() board = " + board);
		Rectangle b = getDisplayElement(boardIdx).getBounds();
		showJugAt(b.x + gapSize, b.y + b.height - gapSize, Jug1.getInt(),
				fillState[0]);
		showJugAt(b.x + 2 * gapSize + 2 * wallSize + jugWidth, b.y + b.height
				- gapSize, Jug2.getInt(), fillState[1]);
		showJugAt(b.x + 3 * gapSize + 4 * wallSize + 2 * jugWidth, b.y
				+ b.height - gapSize, Jug3.getInt(), fillState[2]);
	}

	/**
	 * Show single water jug at the given position.
	 * 
	 * @param x
	 *            bottom left horizontal point.
	 * @param y
	 *            bottom left vertical point.
	 * @param s
	 *            size of the jug in water units.
	 * @param f
	 *            fill state of the jug in water units.
	 */
	private void showJugAt(int x, int y, int s, int f) {
		int dy = wallSize + s * waterUnit;
		graphics.setColor(JugColor.getDevColor());
		graphics.fillRect(x, y - dy, wallSize, dy);
		graphics.fillRect(x + wallSize + jugWidth, y - dy, wallSize, dy);
		graphics.fillRect(x, y - wallSize, jugWidth + 2 * wallSize, wallSize);
		graphics.setColor(WaterColor.getDevColor());
		dy = f * waterUnit;
		graphics.fillRect(x + wallSize, y - wallSize - dy, jugWidth, dy);
		graphics.setColor(JugColor.getDevColor());
		int mx = x + wallSize;
		for (int i = 0; i < s; i++) {
			graphics.fillRect(mx, y - (wallSize + (i + 1) * waterUnit),
					wallSize, wallSize / 4);
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
		int[] s = state.getIntArray();
		if ((s[0] != -1) && (s.length >= 3)) {
			fillState[0] = s[0];
			fillState[1] = s[1];
			fillState[2] = s[2];
		} else {
			fillState[0] = Jug1.getInt();
			fillState[1] = 0;
			fillState[2] = 0;
			CurrentState.getValue().set(fillState);
		}
		// System.out.println("WaterJugs.setState(): " + state);
	}

	/**
	 * Return the current state of the board.
	 * 
	 * @return a parameter value which contains the current state of the board.
	 */
	protected ExParValue getState() {
		return new ExParValue(fillState);
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
		if (b.contains(downX, downY) && b.contains(upX, upY)) {
			int[] fromTo = new int[2];
			fromTo[0] = findJugAt(downX, downY, b.x + wallSize + gapSize);
			fromTo[1] = findJugAt(upX, upY, b.x + wallSize + gapSize);
			if ((fromTo[0] != NO_MOVE) && (fromTo[1] != NO_MOVE)) {
				// System.out.println("WaterJugs.getMove(): from " + fromTo[0] +
				// " to " + fromTo[1]);
				return new ExParValue(fromTo);
			}
		}
		return new ExParValue(m);
	}

	private int findJugAt(int x, int y, int bx) {
		if ((x > bx) && (x < (bx + jugWidth)))
			return 0;
		bx += (2 * wallSize + gapSize + jugWidth);
		if ((x > bx) && (x < (bx + jugWidth)))
			return 1;
		bx += (2 * wallSize + gapSize + jugWidth);
		if ((x > bx) && (x < (bx + jugWidth)))
			return 2;
		return NO_MOVE;
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
		int[] mv = m.getIntArray();
		if (((mv[0] == 0) || (mv[0] == 1) || (mv[0] == 2))
				&& ((mv[1] == 0) || (mv[1] == 1) || (mv[1] == 2))
				&& (mv[0] != mv[1])) {
			return true;
		} else {
			return false;
		}
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
		int[] mv = m.getIntArray();
		int source = mv[0];
		int target = mv[1];
		// How much space is in the target jug?
		int targetSpace = jugs[target] - fillState[target];
		// possibilities are:
		if (targetSpace >= fillState[source]) {
			// target space strictly larger than or equal to content of source
			fillState[target] += fillState[source];
			fillState[source] = 0;
		} else {
			// target space strictly smaller than content of source
			fillState[source] -= targetSpace;
			fillState[target] = jugs[target];
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
		// System.out.println("WaterJugs.setState():    GoalState = " +
		// GoalState);
		// System.out.println("WaterJugs.setState(): CurrentState = " +
		// CurrentState);
		int[] ws = GoalState.getIntArray();
		if (ws.length >= 3) {
			if (((ws[0] == -1) || (ws[0] == fillState[0]))
					&& ((ws[1] == -1) || (ws[1] == fillState[1]))
					&& ((ws[2] == -1) || (ws[2] == fillState[2]))) {
				return FIRST_PLAYER;
			}
		}
		return RUNNING;
	}
}
