package de.pxlab.awtx;

/**
 * This class has been copied from the book
 * 
 * <p>
 * Geary, D. M. (1999). Graphic Java 1.2, Mastering the JFC, Vol I: AWT (3rd
 * ed.). Sun Microsystems Press.
 * 
 * <p>
 * HFILL and VFILL added by H. Irtel
 */
public class Orientation {
	public static final Orientation NORTH = new Orientation();
	public static final Orientation SOUTH = new Orientation();
	public static final Orientation EAST = new Orientation();
	public static final Orientation WEST = new Orientation();
	public static final Orientation CENTER = new Orientation();
	public static final Orientation TOP = new Orientation();
	public static final Orientation LEFT = new Orientation();
	public static final Orientation RIGHT = new Orientation();
	public static final Orientation BOTTOM = new Orientation();
	public static final Orientation HFILL = new Orientation();
	public static final Orientation VFILL = new Orientation();
	public static final Orientation HORIZONTAL = new Orientation();
	public static final Orientation VERTICAL = new Orientation();

	static public Orientation fromString(String s) {
		Orientation o = null;
		if (s.equals("NORTH") || s.equals("north"))
			o = NORTH;
		else if (s.equals("SOUTH") || s.equals("south"))
			o = SOUTH;
		else if (s.equals("EAST") || s.equals("east"))
			o = EAST;
		else if (s.equals("WEST") || s.equals("west"))
			o = WEST;
		else if (s.equals("CENTER") || s.equals("center"))
			o = CENTER;
		else if (s.equals("TOP") || s.equals("top"))
			o = TOP;
		else if (s.equals("LEFT") || s.equals("left"))
			o = LEFT;
		else if (s.equals("RIGHT") || s.equals("right"))
			o = RIGHT;
		else if (s.equals("BOTTOM") || s.equals("bottom"))
			o = BOTTOM;
		else if (s.equals("VERTICAL") || s.equals("vertical"))
			o = VERTICAL;
		else if (s.equals("HORIZONTAL") || s.equals("horizontal"))
			o = HORIZONTAL;
		else if (s.equals("HFILL") || s.equals("hfill"))
			o = HFILL;
		else if (s.equals("VFILL") || s.equals("vfill"))
			o = VFILL;
		return o;
	}

	private Orientation() {
	} // Defeat instantiation
}
