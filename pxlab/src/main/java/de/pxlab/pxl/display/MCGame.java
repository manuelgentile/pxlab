package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.event.KeyEvent;
import de.pxlab.pxl.*;

/**
 * The Missionaries and Cannibals problem. The rules are:
 * 
 * <ol>
 * 
 * <li>The number of cannibals on one side of the river or in the boat must
 * never be larger than the number of missionaries.
 * 
 * <li>Only a fixed number of subjects can be moved from one side to the other
 * during one trial.
 * 
 * <li>Subjects are moved by dragging them from their position to the target
 * position with the mouse button down. The boat is moved by clicking on the
 * boat's triangular front region.
 * 
 * </ol>
 * 
 * 
 * <p>
 * The problem is defined by the following parameters:
 * 
 * <ul>
 * 
 * <li><code>NumberOfMissionaries</code> gives the number of Missionaries.
 * 
 * <li><code>NumberOfCannibals</code> gives the number of cannibals.
 * 
 * <li><code>BoatCapacity</code> gives the maximum number of subjects to be
 * moved.
 * 
 * <li>StartState gives the initial state and may be empty for the default start
 * position where all objects are on the left side of the river.
 * 
 * <li>GoalState gives the goal state. The problem is solved if all missionaries
 * and cannibals have been moved to the goal position. This parameter may be
 * empty for the default goal state where all objects are on the right side of
 * the river.
 * 
 * <li>ProblemState contains the positions of boat, missionaries, and cannibals
 * at each move. Position codes are 0 for the left, 1 for the right side of the
 * river, and 2 for the boat, .
 * 
 * <li>Move is the id number of the object moved in the most recent trial. The
 * boat is id=0, the missionaries have id numbers 1, ..., NumberOfMissionaries,
 * and the cannibals have id numbers larger than NumberOfMissionaries.
 * 
 * </ul>
 * 
 * @version 0.3.0
 */
/*
 * 
 * 12/13/01 changed pointerMoved() to pointerDragged()
 */
public class MCGame extends MoveProblem {
	public ExPar BoatColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Boat color");
	public ExPar CannibalsColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Color of cannibals");
	public ExPar MissionariesColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)),
			"Color of missionaries");
	public ExPar RiverColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)), "River color");
	public ExPar NumberOfMissionaries = new ExPar(1, 6, new ExParValue(3),
			"Number of missionaries");
	public ExPar NumberOfCannibals = new ExPar(1, 6, new ExParValue(3),
			"Number of cannibals");
	public ExPar BoatCapacity = new ExPar(1, 4, new ExParValue(2),
			"Maximum number of subjects to be moved");
	// Don't change these since we use them as indices
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int IN_BOAT = 2;

	public MCGame() {
		setTitleAndTopic("Missionaries and Cannibals Problem",
				PROBLEM_SOLVING_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER"));
		startPosition = LEFT;
		goalPosition = RIGHT;
	}
	private int river, boatFront, boatBody, firstSubject;
	private int nMissionaries, nCannibals, nSubjects;
	private int pointer_dx, pointer_dy;

	public int create() {
		dropRect = new Rectangle[3];
		nMissionaries = 0;
		nCannibals = 0;
		river = enterDisplayElement(new Bar(RiverColor), group[0]);
		textBox = enterDisplayElement(new TextBox(MessageColor, BoxColor),
				group[0]);
		boatBody = enterDisplayElement(new Bar(BoatColor), group[0]);
		// the boatfront must be the last element before the
		// missionaries/cannibals
		boatFront = enterDisplayElement(new FilledPolygon(BoatColor), group[0]);
		firstSubject = nextDisplayElementIndex();
		defaultTiming(0);
		return river;
	}

	public void computeGeometry() {
		int cellW = width / 32;
		int cellH = 2 * cellW;
		int topY = -height / 2;
		int n = NumberOfMissionaries.getInt();
		int m = NumberOfCannibals.getInt();
		if ((n != nMissionaries) || (m != nCannibals)) {
			// System.out.println("MCGame.computeGeometry(): number of subjects has changed.");
			nMissionaries = n;
			nCannibals = m;
			nSubjects = nMissionaries + nCannibals;
			removeDisplayElements(firstSubject);
			TextElement te;
			Font font = new Font("SansSerif", Font.BOLD, cellH);
			for (int i = 0; i < nSubjects; i++) {
				te = new TextElement((i < nMissionaries) ? MissionariesColor
						: CannibalsColor, (i < nMissionaries) ? "M" : "C");
				te.setFont(font);
				te.setReferencePoint(PositionReferenceCodes.BASE_CENTER);
				enterDisplayElement(te, group[0]);
			}
			problemState = new int[nSubjects + 1];
			setStartState();
			TextBox tb = ((TextBox) getDisplayElement(textBox));
			tb.setText("");
			tb.setLocation(0, -topY - 200);
		}
		// river
		int riverWidth = 8 * cellW;
		int riverX = -4 * cellW;
		getDisplayElement(river).setRect(riverX, topY, riverWidth, height);
		// the boat is a rectangle and a triangle depending on the boat's
		// position
		int[] xPoints = new int[3], yPoints = new int[3];
		Rectangle boat;
		if (leftSideActive()) {
			boat = new Rectangle(-4 * cellW, -cellH, 4 * cellW, 2 * cellH);
			xPoints[0] = (0);
			xPoints[1] = (2 * cellW);
			xPoints[2] = (0);
		} else {
			boat = new Rectangle(0, -cellH, 4 * cellW, 2 * cellH);
			xPoints[0] = (0);
			xPoints[1] = (-2 * cellW);
			xPoints[2] = (0);
		}
		yPoints[0] = (-cellH);
		yPoints[1] = (0);
		yPoints[2] = (cellH);
		((FilledPolygon) (getDisplayElement(boatFront)))
				.setPolygon(new Polygon(xPoints, yPoints, 3));
		(getDisplayElement(boatBody)).setRect(boat);
		// the dropRects are used as drop areas for pointerReleased()
		// left shore
		dropRect[0] = new Rectangle(-16 * cellW, topY, 12 * cellW, height);
		// right shore
		dropRect[1] = new Rectangle(4 * cellW, topY, 12 * cellW, height);
		// boat/river
		dropRect[2] = new Rectangle(-4 * cellW, topY, 8 * cellW, height);
		// Set the subjects' locations
		int leftMX = -4 * cellW - cellW;
		int leftCX = leftMX;
		int rightMX = 4 * cellW + cellW;
		int rightCX = rightMX;
		int inBoat = 0;
		int base = cellH / 8;
		for (int i = 0; i < nSubjects; i++) {
			DisplayElement subject = getDisplayElement(firstSubject + i);
			int p = problemState[i + 1];
			if ((i + 1) == currentMove) {
				subject.setLocation(pointerCurrentX + pointer_dx,
						pointerCurrentY + pointer_dy);
			} else if (i < nMissionaries) {
				if (p == LEFT) {
					subject.setLocation(leftMX, -base);
					leftMX -= (2 * cellW);
				} else if (p == RIGHT) {
					subject.setLocation(rightMX, -base);
					rightMX += (2 * cellW);
				} else if (p == IN_BOAT) {
					if (leftSideActive()) {
						switch (inBoat) {
						case 0:
							subject.setLocation(boat.x + 3 * cellW, -base);
							break;
						case 1:
							subject.setLocation(boat.x + 3 * cellW, cellH
									- base);
							break;
						case 2:
							subject.setLocation(boat.x + cellW, -base);
							break;
						default:
							subject.setLocation(boat.x + cellW, cellH - base);
							break;
						}
					} else {
						switch (inBoat) {
						case 0:
							subject.setLocation(boat.x + cellW, -base);
							break;
						case 1:
							subject.setLocation(boat.x + cellW, cellH - base);
							break;
						case 2:
							subject.setLocation(boat.x + 3 * cellW, -base);
							break;
						default:
							subject.setLocation(boat.x + 3 * cellW, cellH
									- base);
							break;
						}
					}
					inBoat++;
				}
			} else {
				if (p == LEFT) {
					subject.setLocation(leftCX, cellH - base);
					leftCX -= 2 * cellW;
				} else if (p == RIGHT) {
					subject.setLocation(rightCX, cellH - base);
					rightCX += 2 * cellW;
				} else if (p == IN_BOAT) {
					if (leftSideActive()) {
						switch (inBoat) {
						case 0:
							subject.setLocation(boat.x + 3 * cellW, -base);
							break;
						case 1:
							subject.setLocation(boat.x + 3 * cellW, cellH
									- base);
							break;
						case 2:
							subject.setLocation(boat.x + cellW, -base);
							break;
						default:
							subject.setLocation(boat.x + cellW, cellH - base);
							break;
						}
					} else {
						switch (inBoat) {
						case 0:
							subject.setLocation(boat.x + cellW, -base);
							break;
						case 1:
							subject.setLocation(boat.x + cellW, cellH - base);
							break;
						case 2:
							subject.setLocation(boat.x + 3 * cellW, -base);
							break;
						default:
							subject.setLocation(boat.x + 3 * cellW, cellH
									- base);
							break;
						}
					}
					inBoat++;
				}
			}
		}
	}

	private boolean leftSideActive() {
		return problemState[0] == LEFT;
	}

	/** Returns the active Side at the actual State of the game */
	private int activeSide() {
		return (problemState[0]);
	}

	/**
	 * When the mouse button is pressed then we check whether this is within the
	 * bounds of one of the movable objects. These are the boat, the
	 * missionaries, and the cannibals. The variable currentMove stores the
	 * index of the object in the problemState array.
	 */
	protected boolean pointerActivated() {
		((TextBox) getDisplayElement(textBox)).setText("");
		for (int i = -1; i < nSubjects; i++) {
			if (getDisplayElement(firstSubject + i).getBounds().contains(
					pointerActivationX, pointerActivationY)) {
				currentMove = i + 1;
				int p = problemState[currentMove];
				if ((p != activeSide()) && (p != IN_BOAT)) {
					currentMove = -1;
					((TextBox) getDisplayElement(textBox))
							.setText("Illegal pick!");
				} else {
					if (i >= 0) {
						Point pnt = getDisplayElement(firstSubject + i)
								.getLocation();
						pointer_dx = pnt.x - pointerActivationX;
						pointer_dy = pnt.y - pointerActivationY;
					}
				}
				// System.out.println("MCGame.pointerActivated(): currentMove = "
				// + currentMove);
				break;
			}
		}
		return true;
	}

	/**
	 * If the button is released and the moving object was a subject, then we
	 * check whether any of the drop rectangles contains the pointer position
	 * and do the move if it is allowed. If the pointer is released on the boat
	 * front and the boat is being moved then this is done. If the final state
	 * is reached then we set the state parameters accordingly.
	 */
	protected boolean pointerReleased() {
		boolean move = false;
		if (currentMove > 0 && currentMove <= nSubjects) {
			if (dropRect[LEFT].contains(pointerReleaseX, pointerReleaseY)) {
				move = moveSubject(currentMove, problemState[currentMove], LEFT);
			}
			if (dropRect[RIGHT].contains(pointerReleaseX, pointerReleaseY)) {
				move = moveSubject(currentMove, problemState[currentMove],
						RIGHT);
			}
			if (dropRect[IN_BOAT].contains(pointerReleaseX, pointerReleaseY)) {
				move = moveSubject(currentMove, problemState[currentMove],
						IN_BOAT);
			}
		}
		if (currentMove == 0) {
			if (getDisplayElement(boatFront).contains(pointerReleaseX,
					pointerReleaseY)) {
				move = moveBoat();
			}
		}
		finishMove(move);
		return true;
	}

	private int subjectsInBoat() {
		int n = 0;
		for (int i = 1; i <= nSubjects; i++) {
			if (problemState[i] == IN_BOAT) {
				n++;
			}
		}
		return n;
	}

	/**
	 * Move a subject from the shore to the boat or from the boat back to the
	 * shore.
	 * 
	 * @return true if the move is valid, false otherwise.
	 */
	private boolean moveSubject(int movedSub, int from, int to) {
		// First check whether this move is valid. The move is valid if
		// the from-place is the active side or the boat and if the to-place
		// is the active side or the boat and if there is free space in the boat
		int side = activeSide();
		if (from != activeSide() && from != IN_BOAT) {
			// System.out.println("moveSubject = false wegen fromside ");
			return false;
		}
		if (to != side && to != IN_BOAT) {
			// System.out.println("moveSubject = false wegen toside ");
			return false;
		}
		if ((to == IN_BOAT) && (subjectsInBoat() >= BoatCapacity.getInt())) {
			// System.out.println("moveSubject = false wegen boot voll ");
			return false;
		}
		problemState[movedSub] = to;
		// System.out.println("moveSubject = true ");
		return true;
	}

	/**
	 * Move the Boat from one side to another.
	 * 
	 * @return true if the move is valid, false otherwise.
	 */
	private boolean moveBoat() {
		// Check whether the move is valid. The move is valid if
		// on both sides and int the boat the number of Cannibals will be less
		// or equal
		// the number of Missionaries
		int fromSide = activeSide();
		int toSide = (fromSide == LEFT) ? RIGHT : LEFT;
		int fromSideMiss = 0;
		int fromSideCann = 0;
		int toSideMiss = 0;
		int toSideCann = 0;
		int boatMiss = 0;
		int boatCann = 0;
		for (int i = 1; i <= nMissionaries; i++) {
			if (problemState[i] == fromSide) {
				fromSideMiss++;
			} else if (problemState[i] == toSide) {
				toSideMiss++;
			} else if (problemState[i] == IN_BOAT) {
				toSideMiss++;
				boatMiss++;
			}
		}
		for (int i = nMissionaries + 1; i <= nSubjects; i++) {
			if (problemState[i] == fromSide) {
				fromSideCann++;
			} else if (problemState[i] == toSide) {
				toSideCann++;
			} else if (problemState[i] == IN_BOAT) {
				toSideCann++;
				boatCann++;
			}
		}
		if ((fromSideMiss != 0) && (fromSideCann > fromSideMiss)) {
			// System.out.println("moveBoat false wegen startseite");
			return false;
		}
		if (((boatMiss != 0) && (boatCann > boatMiss))
				|| ((boatCann + boatMiss) == 0)) {
			// System.out.println("moveBoat false wegen boot");
			return false;
		}
		if ((toSideMiss != 0) && (toSideCann > toSideMiss)) {
			// System.out.println("moveBoat false wegen zielseite");
			return false;
		}
		for (int i = 1; i <= nSubjects; i++) {
			if (problemState[i] == IN_BOAT) {
				problemState[i] = toSide;
			}
		}
		problemState[0] = toSide;
		// System.out.println("moveBoat true");
		return true;
	}
}
