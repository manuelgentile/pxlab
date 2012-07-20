package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.ArrayList;

/**
 * The DisplayPanel shows a Display objects which is an element of a stimulus.
 * The DisplayPanel has two different modes for showing the Display: A simple
 * drawing mode which ignores the timing of display elements and the timing mode
 * which observes the timing groups of Display objects. The simple drawing mode
 * is used for demonstrations and Display editing while the timing mode is used
 * for real time experimental displays.
 * 
 * <p>
 * The DisplayPanel class is able to use double buffered painting of Display
 * objects. Note, however, that it is not recommended to use double buffering
 * for real time displays since double buffering takes time.
 * 
 * <p>
 * The DisplayPanel also has a simple animation player for running Display
 * objects which are animated. These Display objects have a method for showing a
 * certain animation frame. The animation player runs in a separate thread, the
 * Animation Thread, and can be started and stopped by method calls. This makes
 * it possible to edit Display properties while the animation is running.
 * 
 * <p>
 * The DisplayPanel is able to write its content to a GIF file.
 * 
 * @author H. Irtel
 * @version 0.4.4
 * @see Display
 * @see TimingElement
 */
/*
 * 02/13/01 clear display object stepping state when attaching it to the display
 * panel
 * 
 * 07/25/01 switch off double buffered mode by default
 * 
 * 
 * 08/13/01 enable the animation player to run in double buffered mode.
 */
public class DisplayPanel extends de.pxlab.awtx.BufferedCanvas implements
		ComponentListener, FocusListener {
	/** The container which owns this display panel. */
	protected DisplayPanelContainer container;

	/**
	 * After this DisplayPanel has been moved into a proper container it will
	 * send this reference to the DisplayPanel.
	 */
	public void setContainer(DisplayPanelContainer container) {
		this.container = container;
	}
	/**
	 * This is a reference to the current Display object we have to show.
	 */
	protected Display display;
	/**
	 * The display mode for the subsequent drawing operation. If true then the
	 * DisplayPanel uses the timing groups of DisplayElement objects of a
	 * Display in order to provide real time properties for stimuli. If false
	 * then the DisplayPanel draws the complete list of DisplayElement objects
	 * of the current Display object without looking at the Display's timing
	 * groups.
	 */
	private boolean observeTimingGroups;
	private boolean paintingDisabled = false;

	public void setPaintingDisabled(boolean a) {
		paintingDisabled = a;
	}
	/** The default size for this DisplayPanel. */
	private Dimension preferredSize = new Dimension(600, 600);
	/**
	 * The memory drawing buffer which is used for double buffered painting.
	 */
	protected Image memoryBuffer;
	/**
	 * If this flag is set to true then this DisplayPanel uses double buffered
	 * painting. It is recommended to NOT use double buffered painting in real
	 * time applications but only in interactive applications. Double buffering
	 * is off by default.
	 */
	protected boolean doubleBuffered = false;
	/**
	 * By default the animation player does not use double buffering. If this
	 * flag is set then it does!
	 */
	protected boolean doubleBufferedAnimation = false;
	/** Animation displays run in their own private Thread. */
	// private AnimationThread animationThread;
	/**
	 * This flag is set by calling startAnimation() and inicates that the
	 * animation thread has been started. It is cleared by calling
	 * stopAnimation() and forces the animation thread to stop.
	 */
	private boolean isMoving = false;

	/* The time when the last painting operation was finished. */
	// private long lastPaintingTime = 0;
	/** Create a new DisplayPanel object. */
	public DisplayPanel() {
		setBackground(ExPar.ScreenBackgroundColor.getDevColor());
		// setBackground(Color.green);
		addFocusListener(this);
		addComponentListener(this);
	}

	/** Set this DisplayPanel's preferred size. */
	public void setPreferredSize(Dimension s) {
		preferredSize = s;
	}

	/** Get this DisplayPanel's preferred size. */
	public Dimension getPreferredSize() {
		return (preferredSize);
	}

	/**
	 * Set the active Display object of this DisplayPanel. The Display object
	 * must already be prepared to run on this DisplayPanel. This means that its
	 * geometry must have been computed for this DisplayPanel.
	 */
	public void setDisplay(Display x) {
		display = x;
		// System.out.println("Display is " + display.getTitle());
	}

	/** Get the current Display object of this DisplayPanel. */
	public Display getDisplay() {
		return (display);
	}

	/**
	 * Set this DisplayPanel's timing mode. If false the it draws the complete
	 * list of DisplayElement objects of the current Display object without
	 * looking at the Display's timing groups. If true then it uses a real time
	 * display of the current Display objet's timing groups.
	 */
	public void setObserveTimingGroups(boolean t) {
		observeTimingGroups = t;
	}

	/** Get this DisplayPanel's timing mode. */
	public boolean getObserveTimingGroups() {
		return (observeTimingGroups);
	}

	/**
	 * Set the double buffering flag of this DisplayPanel. If this flag is set
	 * to true then this DisplayPanel uses double buffered painting.
	 * <p>
	 * It is recommended to NOT use double buffered painting in real time
	 * applications but only in interactive applications. Immediate mode
	 * painting is much faster than double buffered painting. On my 1 GHz
	 * Pentium III it takes 44 ms (with the Sun 1.4.0-beta Java VM) or 48 ms
	 * (with the Microsoft 1.1.4 Java VM) to blit a 1600x1200 pixel back buffer
	 * to the screen. And this must be done after any drawing operation if
	 * double buffering is used.
	 * 
	 * <p>
	 * Double buffering is off by default.
	 */
	public void setDoubleBuffered(boolean b) {
		doubleBuffered = b;
	}

	/**
	 * Check whether this DisplayPanel is running in double buffered mode.
	 */
	public boolean getDoubleBuffered() {
		return (doubleBuffered);
	}

	/** Set the animation player's double buffering mode. */
	public void setDoubleBufferedAnimation(boolean state) {
		doubleBufferedAnimation = state;
	}

	/** Get the animation player's double buffering mode. */
	public boolean getDoubleBufferedAnimation() {
		return doubleBufferedAnimation;
	}

	// ------------------------------------------------------------------------
	// Double Buffered Painting Support
	// ------------------------------------------------------------------------
	/**
	 * Request an immediate painting operation which is done within the
	 * currently running thread.
	 */
	public void repaintImmediately() {
		// System.out.println("DisplayPanel.repaintImmediately()");
		Graphics g = getGraphics();
		paint(g);
		g.dispose();
	}

	/**
	 * Get a Graphics object for the memory buffer. Also check whether the size
	 * of the panel has changed and requires a new memory buffer.
	 * 
	 * @return a Graphics context for painting into the memory buffer.
	 */
	protected Graphics getMemoryBufferGraphics() {
		Dimension size = getSize();
		if ((memoryBuffer == null)
				|| (memoryBuffer.getWidth(this) != size.width)
				|| (memoryBuffer.getHeight(this) != size.height)) {
			memoryBuffer = createImage(size.width, size.height);
		}
		return (memoryBuffer.getGraphics());
	}

	/**
	 * Update the DisplayPanel without clearing the background first. This
	 * method is needed here since the system's repaint() method calls it and we
	 * do not want to have the default Panel's method update() clearing the
	 * background before calling paint().
	 */
	public void update(Graphics g) {
		// System.out.println("DisplayPanel.update(Graphics)");
		if (paintingDisabled) {
			return;
		}
		paint(g);
	}

	/**
	 * This is the main paint() method of this DisplayPanel object. It has 2
	 * modes: use timing groups or ignore them. Experiments must observe timing
	 * groups while display editors will usually ignore them.
	 */
	public void paint(Graphics g) {
		// System.out.println("DisplayPanel: paint(Graphics)");
		// new RuntimeException("").printStackTrace();
		if (paintingDisabled) {
			return;
		}
		if (display != null) {
			// System.out.println(display.getTitle());
			if (observeTimingGroups) {
				// System.out.println("DisplayPanel: paint(Graphics) with timing");
				if (doubleBuffered) {
					Graphics mg = getMemoryBufferGraphics();
					display.showGroup(mg);
					g.drawImage(memoryBuffer, 0, 0, this);
					mg.dispose();
				} else {
					display.showGroup(g);
				}
			} else {
				// System.out.println("DisplayPanel: paint(Graphics) no timing");
				if (doubleBuffered) {
					Graphics mg = getMemoryBufferGraphics();
					display.show(mg);
					// long t1 = pc.getCount();
					g.drawImage(memoryBuffer, 0, 0, this);
					// long t2 = pc.getCount();
					mg.dispose();
					// System.out.println("DisplayPanel.paint(): " +
					// pc.msec(t2-t1));
					// System.out.println("DisplayPanel.paint(): double buffered");
				} else {
					display.show(g);
				}
			}
		} else {
			clear(g);
		}
	}

	private void clear(Graphics g) {
		// setBackground(Color.red);
		Dimension s = getSize();
		setBackground(ExPar.ScreenBackgroundColor.getDevColor());
		g.clearRect(0, 0, s.width, s.height);
	}

	/** Clear this display panel to its background color. */
	public void clear() {
		Graphics g = getGraphics();
		clear(g);
		g.dispose();
	}

	/**
	 * Return the time when the last painting operation was finished.
	 */
	/*
	 * public long getLastPaintingTime() { return(lastPaintingTime); }
	 */
	// ------------------------------------------------------------------------
	// Animation support
	// ------------------------------------------------------------------------
	/**
	 * This method is called when the user has requested to start animation.
	 */
	/*
	 * public void startAnimation() { isMoving = true; animationThread = new
	 * AnimationThread(); animationThread.setPriority(Thread.MIN_PRIORITY);
	 * animationThread.start(); }
	 */
	/**
	 * This method is called when the user has requested to stop animation.
	 */
	/*
	 * public void stopAnimation() { isMoving = false; if (animationThread !=
	 * null) { try { animationThread.join(); } catch (InterruptedException e) {}
	 * animationThread = null; } }
	 */
	/**
	 * This class runs the animated displays. The animation thread runs as long
	 * the boolean signal isMoving is true. The thread manages a frame counter,
	 * a frame counter increment and a delay period between successive frames.
	 * An animated display has a certain number of frames per cycle. Whenever a
	 * certain frame should be shown the animation thread sends a
	 * showAnimationFrame() message to the currently active display. This draws
	 * the requested frame of the Display. private class AnimationThread extends
	 * Thread { public void run() { int n = 0; while (isMoving) { Graphics g =
	 * getGraphics(); display.showAnimationFrame(g, n); g.dispose(); n +=
	 * display.getFrameIncrement(); if (n >= display.getFramesPerCycle()) n = 0;
	 * int fd = display.getFrameDelay(); if (fd > 0) { try { Thread.sleep(fd); }
	 * catch (InterruptedException e) {} } } } }
	 */
	// ------------------------------------------------------------------------
	// Screen Dump Support
	// ------------------------------------------------------------------------
	/**
	 * Dump the current content of the display panel to a file.
	 * 
	 * @param out
	 *            the output stream where to dump the file to.
	 * @throws <tt>IOException</tt> if there is an error while writing to the
	 *         output stream.
	 */
	public void dump(OutputStream out) throws IOException {
		/*
		 * boolean db = getDoubleBuffered(); setDoubleBuffered(true);
		 * repaintImmediately(); GifEncoder ge = new GifEncoder(memoryBuffer,
		 * out); ge.encode(); setDoubleBuffered(db);
		 */
	}

	// -------------------------------------------------------------
	// The FocusListener implementation
	// -------------------------------------------------------------
	public void focusGained(FocusEvent e) {
		Debug.show(Debug.FOCUS, this, "gained focus.");
	}

	public void focusLost(FocusEvent e) {
		Debug.show(Debug.FOCUS, this, "lost focus.");
	}

	// -------------------------------------------------------------
	// The ComponentListener implementation
	// -------------------------------------------------------------
	/**
	 * Component Listener for detecting size changes of the DisplayPanel which
	 * require a recomputation of the display.
	 */
	public void componentResized(ComponentEvent e) {
		// System.out.println("displayPanel.componentResized() ...");
		if (display != null) {
			Dimension s = getSize();
			display.recomputeGeometry(s.width, s.height);
		}
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	/** Return a verbal description of this DisplayPanel object. */
	public String toString() {
		Dimension s = getSize();
		return (getClass().getName() + "[" + s.width + "x" + s.height + "]");
	}
}
