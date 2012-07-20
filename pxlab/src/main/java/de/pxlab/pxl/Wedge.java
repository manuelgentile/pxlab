package de.pxlab.pxl;

import java.awt.Polygon;

/**
 * A filled half or quarter rectangle with 8 possible orientations. TOPRIGHT,
 * BOTTOMRIGHT, BOTTOMLEFT, and TOPLEFT orientations are half rectangles with a
 * single rectangular corner corresponding to the orientation and one diagonal
 * as its base. TOP, BOTTOM, LEFT, and RIGHT are quarter rectangles which
 * together make up a complete rectangle and each one constitutes the respective
 * part of the full rectangle. Location is defined by the top left corner of the
 * corresponding rectangle and sizes correspond to the respective rectangle's
 * width and heigt.
 * 
 * Note that we do not allow that a wedge is created without its orientation
 * being defined!
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 08/11/01 use an ExPar for color 02/11/03 added TOP,...
 */
public class Wedge extends FilledPolygon {
	/** A wedge with its rectangular corner at the top right. */
	public static final int TOPRIGHT = 0;
	/** A wedge with its rectangular corner at the bottom right. */
	public static final int BOTTOMRIGHT = 1;
	/** A wedge with its rectangular corner at the bottom left. */
	public static final int BOTTOMLEFT = 2;
	/** A wedge with its rectangular corner at the top left. */
	public static final int TOPLEFT = 3;
	/** A wedge with its rectangular corner at the top right. */
	public static final int TOP = 4;
	/** A wedge with its rectangular corner at the bottom right. */
	public static final int BOTTOM = 5;
	/** A wedge with its rectangular corner at the bottom left. */
	public static final int LEFT = 6;
	/** A wedge with its rectangular corner at the top left. */
	public static final int RIGHT = 7;
	/** The orientation of the rectangular corner of this wedge. */
	protected int orientation = TOPRIGHT;

	/**
	 * Create a wedge at position (0,0) with size (1,1) with the given color
	 * parameter and the given orientation.
	 */
	public Wedge(ExPar i, int orient) {
		this(i, 0, 0, 1, 1, orient);
	}

	/**
	 * Create a wedge at position (x,y) with size (w,h) with the given color
	 * parameter and the given orientation.
	 */
	public Wedge(ExPar i, int x, int y, int w, int h, int orient) {
		super(i);
		type = WEDGE;
		orientation = orient;
		setRect(x, y, w, h);
	}

	/** Set this wedge's orientation. */
	public void setOrientation(int orient) {
		orientation = orient;
	}

	/** Get this wedge's orientation. */
	public int getOrientation() {
		return orientation;
	}

	public void show() {
		// System.out.println("Wedge.show() Orientation= " + orientation +
		// ", Position = " + location);
		int[] xp = new int[3];
		int[] yp = new int[3];
		switch (orientation) {
		case TOPRIGHT:
			xp[0] = location.x;
			yp[0] = location.y;
			xp[1] = location.x + size.width - 0;
			yp[1] = location.y;
			xp[2] = xp[1];
			yp[2] = location.y + size.height - 0;
			;
			break;
		case BOTTOMRIGHT:
			xp[0] = location.x + size.width - 0;
			yp[0] = location.y;
			xp[1] = xp[0];
			yp[1] = location.y + size.height - 0;
			xp[2] = location.x;
			yp[2] = yp[1];
			break;
		case BOTTOMLEFT:
			xp[0] = location.x;
			yp[0] = location.y;
			xp[1] = location.x + size.width - 0;
			yp[1] = location.y + size.height - 0;
			xp[2] = location.x;
			yp[2] = yp[1];
			break;
		case TOPLEFT:
			xp[0] = location.x;
			yp[0] = location.y;
			xp[1] = location.x + size.width - 0;
			yp[1] = location.y;
			xp[2] = location.x;
			yp[2] = location.y + size.height - 0;
			;
			break;
		case TOP:
			xp[0] = location.x;
			yp[0] = location.y;
			xp[1] = xp[0] + size.width;
			yp[1] = location.y;
			xp[2] = location.x + size.width / 2;
			yp[2] = location.y + size.height / 2;
			break;
		case RIGHT:
			xp[0] = location.x + size.width;
			yp[0] = location.y;
			xp[1] = xp[0];
			yp[1] = location.y + size.height;
			xp[2] = location.x + size.width / 2;
			yp[2] = location.y + size.height / 2;
			break;
		case BOTTOM:
			xp[0] = location.x + size.width / 2;
			yp[0] = location.y + size.height / 2;
			xp[1] = location.x + size.width;
			yp[1] = location.y + size.height;
			xp[2] = location.x;
			yp[2] = yp[1];
			break;
		case LEFT:
			xp[0] = location.x;
			yp[0] = location.y;
			xp[1] = location.x + size.width / 2;
			yp[1] = location.y + size.height / 2;
			xp[2] = xp[0];
			yp[2] = yp[0] + size.height;
			break;
		}
		polygon = new Polygon(xp, yp, 3);
		super.show();
	}
}
