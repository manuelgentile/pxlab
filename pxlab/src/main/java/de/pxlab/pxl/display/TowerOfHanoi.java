package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * The Tower of Hanoi problem. Some information on its history and its
 * mathematics are to be found at <a
 * href="http://www.cut-the-knot.com/recurrence/hanoi.shtml">
 * http://www.cut-the-knot.com/recurrence/hanoi.shtml</a>
 * 
 * 
 * <p>
 * The subject's task is to move a set of ordered disks from the start position
 * to the goal position. The rules are:
 * 
 * <ol>
 * <li>Within each step only a single disk may be moved.
 * 
 * <li>One must not put a larger disk on top of a smaller one.
 * </ol>
 * 
 * The rules are strictly enforced. The three available positions are indicated
 * by poles and the disks are shown in side view. The disks may be moved using a
 * mouse pointer.
 * 
 * <p>
 * The problem is defined by the following parameters:
 * <ul>
 * <li><code>NumberOfDisks</code> gives the number of disks.
 * 
 * <li><code>StartPosition</code> gives the initial position of the set of
 * disks.
 * 
 * <li><code>GoalPosition</code> gives the target position. The problem is
 * solved if all disks have been moved to the goal position.
 * 
 * <li><code>ProblemState</code> contains the position of each disk at a single
 * step of the problem solving sequence. Position codes are 1, 2, or 3 for the
 * left, middle, and right pole respectively.
 * 
 * <li><code>Move</code> is the number of the disk which is moved in the current
 * trial.
 * 
 * </ul>
 * 
 * The state of the problem is completely stored in the parameters for every
 * trial. Within a trial the drawing is done by a hook into the system's mouse
 * response methods. If a problem is solved then the global experimental
 * parameter BlockState is set to FINISHED. This will end the block and possibly
 * start a new sequence.
 * 
 * <p>
 * Note that this Display automaticaly turns on double buffered display mode in
 * order to avoid strong flickering during mouse movement.
 * 
 * @author H. Irtel
 * @version 0.2.8
 */
/*
 * 01/26/01 use ExPar color and unified timing
 * 
 * 05/31/01 moved pointerMoved() herein to get computeGeometry()
 * 
 * 12/13/01 changed pointerMoved() to pointerDragged()
 * 
 * 11/14/02 fixed update while dragging
 */
public class TowerOfHanoi extends MoveProblem {
	/** Color of the position poles. */
	public ExPar PoleColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			12.618, 0.404, 0.448)), "Pole color");
	/** Basement color. */
	public ExPar BaseColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			18.367, 0.249, 0.282)), "Base color");
	/** Disk color. */
	public ExPar DiskColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			45.654, 0.404, 0.448)), "Disk color");
	/** Number of disks to be used. */
	public ExPar NumberOfDisks = new ExPar(INTEGER, 1, 12, new ExParValue(6),
			"Number of disks");

	public TowerOfHanoi() {
		setTitleAndTopic("Tower of Hanoi", PROBLEM_SOLVING_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_TIMER"));
		startPosition = 0;
		goalPosition = 2;
	}
	private int base, firstPole, firstDisk;
	private int nDisks;

	protected int create() {
		dropRect = new Rectangle[3];
		nDisks = 0;
		textBox = enterDisplayElement(new TextBox(MessageColor, BoxColor),
				group[0]);
		base = enterDisplayElement(new Bar3D(BaseColor), group[0]);
		firstPole = nextDisplayElementIndex();
		for (int i = 0; i < 3; i++) {
			enterDisplayElement(new Bar3D(PoleColor), group[0]);
			dropRect[i] = new Rectangle();
		}
		firstDisk = nextDisplayElementIndex();
		defaultTiming(0);
		return base;
	}

	protected void computeGeometry() {
		// System.out.println("TowerOfHanoi.computeGeometry()");
		// System.out.println("  ProblemState = " + ProblemState);
		int n = NumberOfDisks.getInt();
		int cellW = width / 32;
		int cellH = 3 * cellW / 2;
		// Check whether the number of disks has changed
		if (n != nDisks) {
			nDisks = n;
			removeDisplayElements(firstDisk);
			DisplayElement disk;
			for (int i = 0; i < nDisks; i++) {
				disk = new Bar3D(DiskColor);
				disk.setSize(8 * cellW - (i * 6 * cellW) / (nDisks - 1), cellH);
				enterDisplayElement(disk, group[0]);
			}
			problemState = new int[nDisks];
			setStartState();
			TextBox tb = ((TextBox) getDisplayElement(textBox));
			tb.setText("");
			tb.setLocation(0, -height / 2 + 200);
		}
		// base rectangle
		getDisplayElement(base).setRect(-15 * cellW, height / 2 - 4 * cellH,
				30 * cellW, 2 * cellH);
		// poles
		int poleHeight = (nDisks + 1) * cellH;
		for (int i = 0; i < 3; i++) {
			DisplayElement pole = getDisplayElement(firstPole + i);
			pole.setRect(-9 * cellW + i * 9 * cellW - cellW / 2, height / 2 - 4
					* cellH - poleHeight, cellW, poleHeight);
			dropRect[i] = new Rectangle(-13 * cellW + i * 9 * cellW, height / 2
					- 4 * cellH - poleHeight, 8 * cellW, poleHeight);
		}
		// disks are numbered from 0 to nDisks-1, where 0 is the largest
		// and (nDisks-1) ist the smallest disk
		// System.out.println("moving disk = " + currentMove);
		int[] diskY = new int[3];
		for (int i = 0; i < 3; i++)
			diskY[i] = height / 2 - 4 * cellH - cellH;
		for (int i = 0; i < nDisks; i++) {
			DisplayElement disk = getDisplayElement(firstDisk + i);
			int w = disk.getSize().width;
			int p = problemState[i];
			int x = -9 * cellW + p * 9 * cellW - w / 2;
			// System.out.println("Computing position of disk " + i);
			// System.out.println("First disk element: " + firstDisk);
			if (currentMove == i) {
				disk.setLocation(x + pointerCurrentX - pointerActivationX,
						diskY[p] + pointerCurrentY - pointerActivationY);
			} else {
				disk.setLocation(x, diskY[p]);
			}
			diskY[p] -= cellH;
		}
		// System.out.println("--------------------");
	}

	/** This is a hook into the mouse button pressed event handler. */
	protected boolean pointerActivated() {
		((TextBox) getDisplayElement(textBox)).setText("");
		for (int i = 0; i < nDisks; i++) {
			if (getDisplayElement(firstDisk + i).getBounds().contains(
					pointerActivationX, pointerActivationY)) {
				currentMove = i;
				// System.out.println("moving disk = " + currentMove);
				int p = problemState[currentMove];
				if (currentMove != topDisk(p)) {
					currentMove = -1;
				}
				// System.out.println("    yes really moving disk = " +
				// currentMove);
			}
		}
		return true;
	}

	/** This is a hook into the mouse button released event handler. */
	protected boolean pointerReleased() {
		// System.out.println("TowerOfHanoi.pointerReleased()");
		boolean move = false;
		if (currentMove >= 0) {
			for (int i = 0; i < 3; i++) {
				if (dropRect[i].contains(pointerReleaseX, pointerReleaseY)) {
					move = moveDisk(problemState[currentMove], i);
					break;
				}
			}
		}
		finishMove(move);
		return true;
	}

	/**
	 * Find the number of the topmost disk on the given pole.
	 * 
	 * @return the number of the disk on the given position or (-1) if there is
	 *         no disk on that position.
	 */
	private int topDisk(int pole) {
		int t = -1;
		// This loop returns the last/smallest disk whose position is equal to
		// pole
		for (int i = 0; i < nDisks; i++) {
			if (pole == problemState[i]) {
				t = i;
			}
		}
		return t;
	}

	/**
	 * Move the top disk from fromPole to the toPole.
	 * 
	 * @return true if the move is valid, false otherwise.
	 */
	private boolean moveDisk(int fromPole, int toPole) {
		boolean valid = false;
		// First check whether this move is valid. The move is valid if
		// the from-pole is nonempty and its top disk is smaller than the
		// top disk at the to-pole. Note that bigger disks have
		// smaller index values
		int d1 = topDisk(fromPole);
		int d2 = topDisk(toPole);
		// System.out.println("Moving disk " + d1 + " on pole " + fromPole +
		// " onto " + d2 + " on pole " + toPole);
		if ((d1 >= 0) && (d2 < d1)) {
			problemState[d1] = toPole;
			valid = true;
		}
		return valid;
	}
}
