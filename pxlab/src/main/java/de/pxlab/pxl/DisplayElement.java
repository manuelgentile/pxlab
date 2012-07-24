package de.pxlab.pxl;

import java.awt.*;

/*

 To Do:

 - Seems that the static part of this class has to be initialized
 before any experiment starts. Otherwise something like
 'validBounds' may propagate its state from one experiment to the
 next.

 - remove object type codes

 - remove replication**

 */
/**
 * This abstract class represents primitive graphic objects with a color
 * parameter and a screen location and size. The class is abstract since each
 * subclass has its own drawing method.
 * 
 * @author H. Irtel
 * @version 0.4.4
 */
/*
 * 01/26/01 use an ExPar for color
 * 
 * 05/10/01 added modified field
 * 
 * 07/30/01 added syncToVerticalBlanking flag
 * 
 * 02/07/02 added dithering (currently for class Bar only).
 * 
 * 08/01/02 added getRect()
 * 
 * 11/11/02 enable visible selection
 * 
 * 11/03/03 moved all dithered drawing into this class
 * 
 * 12/10/03 getColorAt()
 * 
 * 31/01/04 load() can load objects of this class at runtime.
 * 
 * 2005/06/18 removed syncToVerticalBlanking flag. This is done by the
 * PresentationManager now.
 * 
 * 2005/07/21 convertPoint() added.
 */
abstract public class DisplayElement {
	/** These object types are known. */
	public static final int EMPTY = 0;
	public static final int CLEAR = 1;
	public static final int BAR = 2;
	public static final int OVAL = 3;
	public static final int FILLED_POLYGON = 4;
	public static final int SECTOR = 5;
	public static final int LINE = 10;
	public static final int RECT = 11;
	public static final int ELLIPSE = 12;
	public static final int POLYLINE = 13;
	public static final int POLYLINECLOSED = 14;
	public static final int ARC = 15;
	public static final int CROSS = 16;
	public static final int BAR_PATTERN = 17;
	public static final int VERSTRIPES = 18;
	public static final int TEXT = 20;
	public static final int TEXTPARAGRAPH = 21;
	public static final int CHAR_PATTERN = 22;
	public static final int CHAR_PATTERN_MASK = 23;
	public static final int SCALE = 24;
	public static final int BITMAP = 30;
	public static final int PICTURE = 31;
	public static final int GRATING = 32;
	public static final int WEDGE = 33;
	public static final int RANDOM_DOTS = 34;
	public static final int RANDOM_TILES = 35;
	public static final int EXTERNAL_SIGNAL = 36;
	public static final int ILLUMINATION_CONTROL = 37;
	// public static final int SCALEDPICTURE = 31;
	// public static final int SUBPICTURE = 32;
	public static final int AUDIO_BEEP = 50;
	public static final int DELAY = 60;
	/** Selected objects are marked by a frame. */
	public static final int SELECTION_FRAME = 0;
	/** Selected objects are marked by a central dot. */
	public static final int SELECTION_DOT = 1;
	/**
	 * This is the Graphics context where instances of this DisplayElement class
	 * will be shown. Note that this is a static context and thus applies to all
	 * instances of this class.
	 */
	protected static Graphics graphics;
	/**
	 * This is a Graphics2D context for those elements which want to use the
	 * Graphics2D properties. By default this context has set antialiasing ON.
	 */
	protected static Graphics2D graphics2D;
	/**
	 * This is the width of the display panel which is needed in order to
	 * translate the coordinate system such that the origin is at the display
	 * panel's center.
	 */
	protected static int displayWidth;
	/**
	 * This is the height of the display panel which is needed in order to
	 * translate the coordinate system such that the origin is at the display
	 * panel's center.
	 */
	protected static int displayHeight;
	/**
	 * If true then this object's geometry has been modified since the last
	 * drawing operation.
	 */
	protected boolean modified = true;
	// protected static View3 view3;
	/**
	 * This is the ColorTable which will be used to display this DisplayElement.
	 */
	// protected static ColorTable colorTable;
	/** Tells us what type of geometry this object is. */
	protected int type;
	/** The location of this object on the screen. */
	protected Point location = new Point();
	/** The width and height of this object on the screen. */
	protected Dimension size = new Dimension();
	/**
	 * With of lines. Currently only implemented for some of the line drawing
	 * objects and only for horizontal and vertical lines.
	 */
	protected int lineWidth = 1;
	/**
	 * The color parameter which should be used to draw this object.
	 */
	protected ExPar colorPar;
	/**
	 * Some DisplayElements can use ordered dithering to increase color
	 * precision. These need an instance of class Dither to manage their color.
	 */
	protected Dither dither = null;
	/**
	 * Set this to true to turn on bounding box updating on painting. This is
	 * useful for object selection.
	 */
	protected static boolean validBounds = false;
	/**
	 * This is the bounding box of each object AFTER it has been drawn!
	 */
	private Rectangle bounds = new Rectangle();
	/** This object's selection state. */
	protected boolean selected = false;
	/** Color for showing selection frames. */
	protected static Color selectionColor = Color.white;
	protected static int selectionShowType = SELECTION_FRAME;
	protected static int selectionDotSize = 3;

	/** Set the color for showing selection frames. */
	public static void setSelectionColor(Color s) {
		selectionColor = s;
	}

	/** Set the way how selection is indicated. */
	public static void setSelectionShowType(int t) {
		selectionShowType = t;
	}

	/** Set the size of the selection marker or frame. */
	public static void setSelectionDotSize(int t) {
		selectionDotSize = t;
	}
	/**
	 * Indicates to which timing groups this shape element belongs. A timing
	 * group is a group of display elements which have the same timing
	 * properties. Each bit in the group pattern corresponds to a timing step.
	 * If a bit is on then the timing group is visible during the respective
	 * timing step.
	 */
	private long timingGroupPattern;
	/**
	 * Display starting time relative to the reference object's display time.
	 */
	// private int onsetTime = 0;
	/**
	 * Index of this element's onset time reference object in the display list.
	 */
	// private int onsetRefObject = 0;
	/** Reference type of this element's onset time reference. */
	// private int onsetTimeRefType = Display.CLOCK_TIMER;
	/**
	 * Display offset time relative to the reference object's display time. Note
	 * that the offset time is not used for optic display elements since these
	 * do not have an offset time. They disappear because they are hidden by
	 * other optic display elements.
	 */
	// private int offsetTime = 0;
	/**
	 * Index of this element's offset time reference object in the display list.
	 */
	// private int offsetRefObject = 0;
	/** Reference type of this element's offset time reference. */
	// private int offsetTimeRefType = Display.CLOCK_TIMER;
	/** Drawing replication factor. */
	private int replicationFactor = 0;
	/** x-shift for repeated drawing. */
	private int replicationXShift;
	/** y-shift for repeated drawing. */
	private int replicationYShift;

	/**
	 * Set the display context for drawing a sequence of display elements. This
	 * method also establishes the user coordinate space for object positioning.
	 * The user coordinate system has its origin at the center of the display
	 * and its axis units correspond to pixels of the drawing device. The
	 * horizontal axis is oriented from left to right and the vertical axis is
	 * oriented from top to bottom.
	 * 
	 * @param g
	 *            a valid graphics context. It must be safe to use this graphics
	 *            context for later drawing operations.
	 * @param w
	 *            the width of the graphics device.
	 * @param h
	 *            the height of the graphics device.
	 */
	public static void setGraphicsContext(Graphics g, int w, int h) {
		graphics = g;
		graphics.translate(w / 2, h / 2);
		graphics2D = (Graphics2D) graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		displayWidth = w;
		displayHeight = h;
	}

	public static int[] convertPoint(Point p) {
		int[] v = new int[2];
		if (p != null) {
			v[0] = p.x - displayWidth / 2;
			v[1] = p.y - displayHeight / 2;
		} else {
			System.out
					.println("DisplayElement.convertPoint(Point p): null pointer for position!");
			v[0] = -displayWidth / 2;
			v[1] = -displayHeight / 2;
		}
		return v;
	}

	/** Set an object's geometry type. */
	public void setType(int t) {
		type = t;
	}

	/** Get an object's geometry type. */
	public int getType() {
		return (type);
	}

	/**
	 * Calling this method with a true argument activates bounding box updating
	 * while objects of this class are painted. The bounding box may be used for
	 * object selection. The bounding box update flag is a class variable.
	 */
	public static void setValidBounds(boolean b) {
		validBounds = b;
	}

	/**
	 * Returns the bounding box update flag for this class. The bounding box may
	 * be used for object selection.
	 */
	public static boolean getValidBounds() {
		return (validBounds);
	}

	/**
	 * Returns the bounding box of this shape object. Note that this method
	 * returns a valid bounding box only after the shape has been painted.
	 */
	public Rectangle getBounds() {
		return (bounds);
	}

	/**
	 * Set the bounding box of this object AFTER it has been shown on the
	 * screen.
	 */
	protected void setBounds(Rectangle b) {
		setBounds(b.x, b.y, b.width, b.height);
	}

	/**
	 * Set the bounding box of this object AFTER it has been shown on the
	 * screen.
	 */
	protected void setBounds(Point p, Dimension s) {
		setBounds(p.x, p.y, s.width, s.height);
	}

	/**
	 * Set the bounding box of this object immediately AFTER it has been shown
	 * on the screen.
	 */
	protected void setBounds(int x, int y, int width, int height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
		// graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * Returns true if this shape's bounding box contains the given point. Note
	 * that this method must not be called before the shape has been painted.
	 * 
	 * @param p
	 *            the point in user space which should be tested.
	 * @return true if this shape's bounding box contains the given point and
	 *         false otherwise.
	 */
	public boolean contains(Point p) {
		return (bounds.contains(p));
	}

	/**
	 * Returns true if this shape's bounding box contains the given point. Note
	 * that this method must not be called before the shape has been painted.
	 * 
	 * @param x
	 *            horizontal coordinate in user space of the point which should
	 *            be tested.
	 * @param y
	 *            vertical coordinate in user space of the point which should be
	 *            tested.
	 * @return true if this shape's bounding box contains the given point and
	 *         false otherwise.
	 */
	public boolean contains(int x, int y) {
		return (bounds.contains(x, y));
	}

	/** Set this object's selection state. */
	public void setSelected(boolean s) {
		selected = s;
	}

	/** Get this object's selection state. */
	public boolean isSelected() {
		return selected;
	}

	/** Set an object's location in user space. */
	public void setLocation(Point l) {
		setLocation(l.x, l.y);
	}

	/** Set an object's location in user space. */
	public void setLocation(int x, int y) {
		location.x = x;
		location.y = y;
		modified = true;
	}

	/** Get an object's location in user space. */
	public Point getLocation() {
		return (location);
	}

	/** Set an object's size in user space. */
	public void setSize(Dimension s) {
		setSize(s.width, s.height);
	}

	/** Set an object's size in user space. */
	public void setSize(int width, int height) {
		size.width = width;
		size.height = height;
		modified = true;
	}

	/** Get an object's size in user space. */
	public Dimension getSize() {
		return (size);
	}

	/** Set an object's top left location and size in user space. */
	public void setRect(Rectangle r) {
		setRect(r.x, r.y, r.width, r.height);
	}

	/** Set an object's top left location and size in user space. */
	public void setRect(Point p, Dimension s) {
		setRect(p.x, p.y, s.width, s.height);
	}

	/** Set an object's top left location and size in user space. */
	public void setRect(int x, int y, int width, int height) {
		location.x = x;
		location.y = y;
		size.width = width;
		size.height = height;
		modified = true;
	}

	/** Get an object's top left location and size in user space. */
	public Rectangle getRect() {
		return new Rectangle(location, size);
	}

	/** Set an object's center location and size in user space. */
	public void setCenterAndSize(int x, int y, int width, int height) {
		setRect(x - width / 2, y - height / 2, width, height);
	}

	/** Set a LINE object's line width. */
	public void setLineWidth(int w) {
		lineWidth = w;
		modified = true;
	}

	/** Get a LINE object's line width. */
	public int getLineWidth() {
		return (lineWidth);
	}

	/** Set this object's replication factor. */
	public void setReplicationFactor(int a) {
		replicationFactor = a;
		modified = true;
	}

	/** Set this object's replication shifts. */
	public void setReplicationShift(int dx, int dy) {
		replicationXShift = dx;
		replicationYShift = dy;
		modified = true;
	}

	/** Set this object's replication parameters. */
	public void setReplication(int a, int dx, int dy) {
		replicationFactor = a;
		replicationXShift = dx;
		replicationYShift = dy;
		modified = true;
	}

	/**
	 * Set this object's group pattern. The group pattern indicates to which
	 * timing groups this shape element belongs. A timing group is a group of
	 * display elements which have the same timing properties. Each bit in the
	 * group pattern corresponds to a timing step. If a bit is on then the
	 * timing group is visible during the respective timing step.
	 */
	public void setTimingGroupPattern(long groups) {
		timingGroupPattern = groups;
	}

	/** Return this object's group pattern. */
	public long getTimingGroupPattern() {
		return (timingGroupPattern);
	}

	/** Set this object's color parameter. */
	public void setColorPar(ExPar c) {
		colorPar = c;
	}

	/** Return this object's color parameter. */
	public ExPar getColorPar() {
		return (colorPar);
	}

	/**
	 * Get this element's color at the given relative position. This method may
	 * be used with images in order to get the color at some special pixel of
	 * the image. Most display elements will ignore this request.
	 * 
	 * @param x
	 *            horizontal position
	 * @param y
	 *            vertical position.
	 */
	public PxlColor getColorAt(int x, int y) {
		return colorPar.getPxlColor();
	}

	/**
	 * Set this DisplayElement to use dithering.
	 * 
	 * @param n
	 *            dithering method code. If 0 then dithering is switched off. If
	 *            1 then random dithering is used. Otherwise n may be 2, 3, or 4
	 *            to define the dither matrix size of ordered dithering.
	 */
	public void setDither(int n) {
		if (n > 0) {
			if ((dither == null) || (dither.getType() != n)) {
				if (n == 1) {
					dither = new RandomDither();
				} else {
					dither = new OrderedDither(n);
				}
			}
		} else {
			dither = null;
		}
	}

	/**
	 * Compute an object's left edge position with respect to a position
	 * reference code and the reference point's horizontal position.
	 * 
	 * @param referenceCode
	 *            a position reference code from interface
	 *            PositionReferenceCodes which defines the object's position
	 *            reference point.
	 * @param x
	 *            the horizontal position of the reference point
	 * @param w
	 *            the width of the object.
	 * @return the left edge position of the object to be located.
	 */
	public static int locX(int referenceCode, int x, int w) {
		int dx = referenceCode / 3;
		if (dx > 0) {
			x -= w / 2 * dx;
		}
		return x;
	}

	/**
	 * Compute a text object's drawing position with respect to a position
	 * reference code. Text objects have their drawing position defined by the
	 * base left corner.
	 * 
	 * @param referenceCode
	 *            a position reference code from interface
	 *            PositionReferenceCodes which defines the object's position
	 *            reference point. This may be BASE (0), MIDDLE (1), or TOP (2).
	 * @param y
	 *            the vertical position of the reference point
	 * @param h
	 *            the height of the object.
	 * @return the vertical drawing position for the text object.
	 */
	public static int textLocY(int referenceCode, int y, int h) {
		// System.out.println("DisplayElement.textLocY() y = " + y + ", h = " +
		// h);
		int dy = referenceCode % 3;
		System.out.println("DY "+dy+"\t"+y);
		// System.out.println("  DisplayElement.textLocY() dy = " + dy);
		if (dy > 0) {
			y += h / 2 * dy;
		}
		// System.out.println("  DisplayElement.textLocY() y = " + y);
		return y;
	}

	/**
	 * Compute a rectangle object's drawing position with respect to a position
	 * reference code. Rectangular graphic objects have their drawing position
	 * defined by the top left corner.
	 * 
	 * @param referenceCode
	 *            a position reference code from interface
	 *            PositionReferenceCodes which defines the object's position
	 *            reference point. This may be BASE (0), MIDDLE (1), or TOP (2).
	 * @param y
	 *            the vertical position of the reference point
	 * @param h
	 *            the height of the object.
	 * @return the vertical drawing position for the rectangular graphics
	 *         object.
	 */
	public static int rectLocY(int referenceCode, int y, int h) {
		// System.out.println("DisplayElement.rectLocY() y = " + y + ", h = " +
		// h);
		int dy = 2 - referenceCode % 3;
		// System.out.println("  DisplayElement.rectLocY() dy = " + dy);
		if (dy > 0) {
			y -= h / 2 * dy;
		}
		// System.out.println("  DisplayElement.rectLocY() y = " + y);
		return y;
	}

	/*
	 * public static void setView3(View3 v) { view3 = v; }
	 * 
	 * public static View3 getView3() { return(view3); }
	 */
	/**
	 * Show this object using the given color parameter i instead of its own.
	 */
	public void show(ExPar i) {
		ExPar cis = colorPar;
		colorPar = i;
		show();
		colorPar = cis;
	}

	/**
	 * Show this object in the currently defined display context. The display
	 * context must have been set by the method setGraphicsContext(Graphics g,
	 * int w, int h). This method also has to set the object's bounds if the
	 * validBounds flag is set.
	 */
	abstract public void show();

	/**
	 * Show a frame around the display element if this element is selected. The
	 * frame is within the bounds of the display element. Bounds must be valid
	 * in order to correctly show the selection frame.
	 */
	protected void showSelection() {
		// System.out.print("DisplayElement.showSelection(): ");
		if (validBounds) {
			if (isSelected()) {
				// System.out.println(" selected");
				Color storedColor = graphics.getColor();
				graphics.setColor(selectionColor);
				if (selectionShowType == SELECTION_FRAME) {
					// Draw bounds to indicate selection
					// System.out.println(" Width = " + bounds.width);
					for (int i = 0; i < selectionDotSize; i++) {
						graphics.drawRect(bounds.x + i, bounds.y + i,
								bounds.width - 1 - i - i, bounds.height - 1 - i
										- i);
					}
				} else {
					int x = bounds.x + bounds.width / 2;
					int y = bounds.y + bounds.height / 2;
					graphics.fillRect(x - selectionDotSize / 2, y
							- selectionDotSize / 2, selectionDotSize,
							selectionDotSize);
				}
				graphics.setColor(storedColor);
			} else {
				// System.out.println("not selected");
			}
		}
	}

	/**
	 * Draw fixation marks into the given graphics context.
	 * 
	 * @param g
	 *            the Graphics context to be used for drawing.
	 * @param x
	 *            the horizontal center position relative to the graphics
	 *            context.
	 * @param y
	 *            the vertical center position relative to the graphics context.
	 * @param w
	 *            the width of the graphics context or the object which should
	 *            be marked.
	 * @param h
	 *            the height of the graphics context or the object which should
	 *            be marked.
	 * @param fixType
	 *            the type of fixation mark to draw.
	 * @param fixWidth
	 *            the width of the fixation mark to draw.
	 * @param fixHeight
	 *            the height of the fixation mark to draw.
	 * @param lineWidth
	 *            the line width of the fixation mark to draw.
	 * @param c
	 *            the device color of the fixation mark to draw.
	 */
	protected static void drawFixationMark(Graphics g, int x, int y, int w,
			int h, int fixType, int fixWidth, int fixHeight, int lineWidth,
			Color c) {
		if (g == null)
			g = graphics;
		if ((fixType != FixationCodes.NO_FIXATION) && (fixHeight > 0)
				&& (fixWidth > 0)) {
			g.setColor(c);
			if ((fixType == FixationCodes.FIXATION_CROSS)
					|| (fixType == FixationCodes.FIXATION_DOT)) {
				if (lineWidth > 1) {
					int lw2 = lineWidth / 2;
					g.fillRect(x - fixWidth / 2, y - lw2, fixWidth, lineWidth);
					g.fillRect(x - lw2, y - fixHeight / 2, lineWidth, fixHeight);
				} else {
					g.drawLine(x - fixWidth / 2, y, x + fixWidth / 2, y);
					g.drawLine(x, y - fixHeight / 2, x, y + fixHeight / 2);
				}
			} else if (fixType == FixationCodes.CORNER_MARKS) {
				if (lineWidth > 1) {
					g.fillRect(x - w / 2, y - h / 2, lineWidth, fixHeight);
					g.fillRect(x - w / 2, y - h / 2, fixWidth, lineWidth);
					g.fillRect(x - w / 2 + w - lineWidth, y - h / 2, lineWidth,
							fixHeight);
					g.fillRect(x - w / 2 + w - fixWidth, y - h / 2, fixWidth,
							lineWidth);
					g.fillRect(x - w / 2, y - h / 2 + h - fixHeight, lineWidth,
							fixHeight);
					g.fillRect(x - w / 2, y - h / 2 + h - lineWidth, fixWidth,
							lineWidth);
					g.fillRect(x - w / 2 + w - lineWidth, y - h / 2 + h
							- fixHeight, lineWidth, fixHeight);
					g.fillRect(x - w / 2 + w - fixWidth, y - h / 2 + h
							- lineWidth, fixWidth, lineWidth);
				} else {
					g.drawLine(x - w / 2, y - h / 2, x - w / 2 + fixWidth - 1,
							y - h / 2);
					g.drawLine(x - w / 2, y - h / 2, x - w / 2, y - h / 2
							+ fixHeight - 1);
					g.drawLine(x - w / 2 + w - 1, y - h / 2, x - w / 2 + w
							- fixWidth, y - h / 2);
					g.drawLine(x - w / 2 + w - 1, y - h / 2, x - w / 2 + w - 1,
							y - h / 2 + fixHeight);
					g.drawLine(x - w / 2, y - h / 2 + h - 1, x - w / 2
							+ fixWidth - 1, y - h / 2 + h - 1);
					g.drawLine(x - w / 2, y - h / 2 + h - 1, x - w / 2, y - h
							/ 2 + h - fixHeight);
					g.drawLine(x - w / 2 + w - 1, y - h / 2 + h - 1, x - w / 2
							+ w - fixWidth, y - h / 2 + h - 1);
					g.drawLine(x - w / 2 + w - 1, y - h / 2 + h - 1, x - w / 2
							+ w - 1, y - h / 2 + h - fixHeight);
				}
			}
		}
	}

	/**
	 * Draw a horizontal line using the current dither matrix. This method must
	 * only be called when dithering is enabled and there exists a valid Dither
	 * object.
	 */
	protected void drawDitheredHorizontalLine(int x1, int y, int x2) {
		if (x2 < x1) {
			int x = x2;
			x2 = x1;
			x1 = x;
		}
		for (int x = x1; x <= x2; x++) {
			graphics.setColor(dither.colorAt(x, y));
			graphics.drawLine(x, y, x, y);
		}
	}

	/**
	 * Draw a filled rectangle using the current dither matrix. This method must
	 * only be called when dithering is enabled and there exists a valid Dither
	 * object.
	 */
	protected void drawDitheredBar(int x, int y, int w, int h) {
		int x2 = x + w - 1;
		int y2 = y + h;
		for (int yy = y; yy < y2; yy++) {
			drawDitheredHorizontalLine(x, yy, x2);
		}
	}

	/**
	 * Draw a filled ellipse using the current dither matrix. This method must
	 * only be called when dithering is enabled and there exists a valid Dither
	 * object.
	 */
	protected void drawDitheredOval(int xc, int yc, int a0, int b0) {
		int x = 0;
		int y = b0;
		boolean drw = true;
		int a = a0;
		int b = b0;
		int Asquared = a * a;
		int TwoAsquared = 2 * Asquared;
		int Bsquared = b * b;
		int TwoBsquared = 2 * Bsquared;
		int d = Bsquared - Asquared * b + Asquared / 4;
		int dx = 0;
		int dy = TwoAsquared * b;
		while (dx < dy) {
			if (drw) {
				drawDitheredHorizontalLine(xc - x, yc - y, xc + x);
				drawDitheredHorizontalLine(xc - x, yc + y, xc + x);
			}
			if (d > 0L) {
				--y;
				dy -= TwoAsquared;
				d -= dy;
				drw = true;
			} else {
				drw = false;
			}
			++x;
			dx += TwoBsquared;
			d += Bsquared + dx;
		}
		d += (3L * (Asquared - Bsquared) / 2L - (dx + dy)) / 2L;
		while (y > 0) {
			drawDitheredHorizontalLine(xc - x, yc - y, xc + x);
			drawDitheredHorizontalLine(xc - x, yc + y, xc + x);
			if (d < 0L) {
				++x;
				dx += TwoBsquared;
				d += dx;
			}
			--y;
			dy -= TwoAsquared;
			d += Asquared - dy;
		}
		drawDitheredHorizontalLine(xc - x, yc, xc + x);
	}

	/**
	 * Load and instantiate a DisplayElement object of the class whose name is
	 * given as an argument.
	 * 
	 * @param className
	 *            the name of the class to be instantiated.
	 * @return the object which has been instantiated.
	 */
	public static Object load(String className) {
		// System.out.println("Trying to load class " + className + "...");
		Class p = null;
		Object d = null;
		try {
			p = Class.forName(className);
			// System.out.println("Class " + className + " loaded.");
		} catch (ClassNotFoundException cnf) {
			/*
			 * String dexp = ExPar.DisplayExtensionPackage.getString(); if
			 * (dexp.length() > 0) { try { p = Class.forName(dexp + "." +
			 * className); } catch (ClassNotFoundExceptio cnfx) { new
			 * DisplayError("Class " + dexp + "." + className + " not found.");
			 * } } else {
			 */
			new DisplayError("Class " + className + " not found.");
			/*
			 * }
			 */
		}
		try {
			d = p.newInstance();
		} catch (InstantiationException ie) {
			new DisplayError("Class " + p.getName()
					+ " could not be instantiated.");
		} catch (IllegalAccessException ia) {
			new DisplayError("Illegal access to class " + p.getName() + ".");
		}
		return d;
	}
}
