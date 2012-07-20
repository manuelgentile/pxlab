package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
// import Acme.JPM.Encoders.GifEncoder;
import java.util.ArrayList;

import de.pxlab.awtx.ExtendedPopupMenu;
import de.pxlab.pxl.*;

public class EditablePresentationPanel extends Canvas implements DisplayDevice,
/* KeyListener, */MouseListener, MouseMotionListener, ComponentListener {
	/**
	 * The color parameter server which gets a signal when a new active display
	 * Element has been selected.
	 */
	private ColorParServer colorParServer;
	private Display activeDisplay;
	/**
	 * This display panel allows display element selection and this is the
	 * currently active display element.
	 */
	private DisplayElement activeDisplayElement;
	/** The panel has a popup menu for several display related tasks. */
	private ExtendedPopupMenu popupMenu;
	/**
	 * This flag is set to true for editing mode. In editing mode the mouse and
	 * keyboard inputs are treated as editing commands. In non-editing mode the
	 * mouse and keboard listeners of the superclass are activated.
	 */
	private boolean editing = true;
	private Frame owner;
	private PresentationManager presentationManager;
	// private ReturnFromFullScreen retFull;
	/**
	 * Controls this Canvas' painting mode. If false then it draws the complete
	 * list of DisplayElement objects of the current Display object without
	 * looking at the Display's timing groups. If true then it draws only those
	 * elements belonging to the currently active timing group.
	 */
	private boolean observeTimingGroups;

	public EditablePresentationPanel(Frame owner, ColorParServer cps /*
																	 * ,
																	 * ReturnFromFullScreen
																	 * rf
																	 */) {
		this.owner = owner;
		colorParServer = cps;
		// retFull = rf;
		presentationManager = new PresentationManager(owner, this);
		DisplayElement.setValidBounds(true);
		// addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		setBackground(ExPar.ScreenBackgroundColor.getPxlColor().dev());
	}

	/**
	 * Set this panel's popup menu.
	 * 
	 * @param pm
	 *            he popup menu which should be used by this panel.
	 */
	public void setPopupMenu(ExtendedPopupMenu pm) {
		popupMenu = pm;
	}

	/**
	 * Set the currently active Display object.
	 * 
	 * @param dsp
	 *            the Display object which will be shown by this display panel.
	 */
	public void setDisplay(Display dsp) {
		activeDisplay = dsp;
		activeDisplay.recompute(presentationManager);
		activeDisplayElement = activeDisplay.getInitialDisplayElement();
	}

	/**
	 * Set this panel's editing mode.
	 * 
	 * @param mode
	 *            if true then the panel is switched to editing mode. If false
	 *            then the panel is an experimental display panel and all
	 *            keyboard and mouse input is interpreted as a subject's
	 *            response.
	 */
	public void setEditing(boolean mode) {
		editing = mode;
	}

	/**
	 * Get this panel's editing mode.
	 * 
	 * @return true if the panel is in editing mode. false if the panel is an
	 *         experimental display panel and all keyboard and mouse input is
	 *         interpreted as a subject's response.
	 */
	public boolean isEditing() {
		return (editing);
	}

	/** Set this Canvas' painting mode. */
	public void setObserveTimingGroups(boolean t) {
		observeTimingGroups = t;
	}

	public void clear() {
		Graphics g = getGraphics();
		g.setColor(ExPar.ScreenBackgroundColor.getPxlColor().dev());
		g.fillRect(0, 0, getWidth(), getHeight());
		show();
		g.dispose();
	}

	public void showDisplayList(ArrayList displayList) {
		presentationManager.showDisplayList(displayList);
	}

	public void update(Graphics g) {
		// System.out.println("EditablePresentationPanel.update(g)");
		paint(g);
	}

	public void paint(Graphics g) {
		showCurrentDisplay(g);
	}

	/** This is the main method to show the current Display object. */
	private void showCurrentDisplay(Graphics g) {
		// System.out.println("EditablePresentationPanel.showCurrentDisplay(g)");
		// new RuntimeException().printStackTrace();
		/*
		 * if (!isOpen()) { open(); }
		 */
		if (activeDisplay != null) {
			if (activeDisplay.isGraphic()) {
				Graphics mg = getMemoryBufferGraphics();
				if (observeTimingGroups) {
					activeDisplay.showGroup(mg);
				} else {
					activeDisplay.show(mg);
				}
				super.getGraphics().drawImage(memoryBuffer, 0, 0, this);
				mg.dispose();
			} else {
				if (observeTimingGroups) {
					activeDisplay.showGroup();
				} else {
					activeDisplay.show();
				}
			}
		} else {
			// System.out.println("ExperimentalDisplayDevice.EmbeddedCanvas.showCurrentDisplay() - no Display!");
		}
	}

	public void startAnimationPlayer() {
		presentationManager.startAnimationPlayer(activeDisplay, 0);
	}

	public void stopAnimationPlayer() {
		presentationManager.stopAnimationPlayer();
	}

	public void dumpImage(String fn) {
		try {
			BufferedImage img = new Robot().createScreenCapture(new Rectangle(
					getLocationOnScreen(), getSize()));
			try {
				javax.imageio.ImageIO.write(img, "PNG", new File(fn));
			} catch (IOException iox) {
				System.out
						.println("EditablePresentationPanel.dumpImage() Error writing image to file "
								+ fn);
			}
		} catch (AWTException ax) {
			System.out
					.println("EditablePresentationPanel.dumpImage() Can't create Robot for screen capture.");
		}
	}

	// ----------------------------------------------------------------
	// Implementation of the DisplayDevice interface
	// ----------------------------------------------------------------
	public void open() {
		Dimension size = getSize();
		Base.setScreenHeight(size.height);
		Base.setScreenWidth(size.width);
	}

	public void close() {
	}

	public void dispose() {
	}

	public void setActiveScreen(int s) {
	}

	public void setHiddenCursor(boolean s) {
	}

	/**
	 * Get a Graphics context for drawing into the back buffer of the
	 * presentation panel. Method show() has to be called in order to make the
	 * back buffer visible.
	 */
	public Graphics getGraphics() {
		return getMemoryBufferGraphics();
	}

	public Image createMemoryBuffer(TimingElement t) {
		return new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_ARGB);
	}
	/**
	 * The memory drawing buffer which is used for double buffered painting.
	 */
	private Image memoryBuffer;

	/**
	 * Get a Graphics object for the memory buffer. Also check whether the size
	 * of the panel has changed and requires a new memory buffer.
	 * 
	 * @return a Graphics context for painting into the memory buffer.
	 */
	private Graphics getMemoryBufferGraphics() {
		Dimension size = getSize();
		if ((memoryBuffer == null)
				|| (memoryBuffer.getWidth(this) != size.width)
				|| (memoryBuffer.getHeight(this) != size.height)) {
			memoryBuffer = createImage(size.width, size.height);
		}
		return memoryBuffer.getGraphics();
	}

	/**
	 * Show the back buffer of this presentation panel. This method is used by
	 * real time classes like AnimationPlayer.
	 */
	public void show() {
		// System.out.println("EditablePresentationPanel.show()");
		super.getGraphics().drawImage(memoryBuffer, 0, 0, this);
	}

	public Component getComponent() {
		return this;
	}

	// ----------------------------------------------------------------
	// End of Implementation of the DisplayDevice interface
	// ----------------------------------------------------------------
	// ----------------------------------------------------------
	// This is the mouse and mouse motion listener implementation
	// ----------------------------------------------------------
	/**
	 * Releasing the mouse on a DisplayElement selects this element as the
	 * currently active element and this element's color parameter to be the
	 * currently active color parameter.
	 */
	public void mouseReleased(MouseEvent e) {
		// System.out.println("EditablePresentationPanel.mouseReleased() at " +
		// e.getX() + " " + e.getY());
		if (editing) {
			// System.out.println("EditablePresentationPanel.mouseReleased() in editing mode");
			if (e.isPopupTrigger()) {
				// System.out.println("  Showing Popup Menu");
				popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
			} else if (activeDisplay != null) {
				DisplayElement d = activeDisplay.getDisplayElementAt(e.getX(),
						e.getY());
				// System.out.println("  Selection: " + d + " at (" + e.getX() +
				// "," + e.getY() + ")");
				if (d != null) {
					activeDisplayElement = d;
					colorParServer.setColorPar(this, d.getColorPar());
				}
			}
		} else {
			// System.out.println("EditablePresentationPanel.mouseReleased() in non-editing mode");
			presentationManager.getResponseManager().mouseReleased(e);
		}
	}

	/**
	 * If a mouse button is pressed and we are in full screen mode then we go
	 * back to normal.
	 */
	public void mousePressed(MouseEvent e) {
		// System.out.println("EditablePresentationPanel.mousePressed() at " +
		// e.getX() + " " + e.getY());
		if (editing) {
			// System.out.println("EditablePresentationPanel.mousePressed() in editing mode");
			if (e.isPopupTrigger()) {
				// System.out.println("  Showing Popup Menu");
				popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
			} /*
			 * else if (retFull.isFullScreen()) {
			 * retFull.returnFromFullScreen(); }
			 */
		} else {
			// System.out.println("EditablePresentationPanel.mousePressed() in non-editing mode");
			presentationManager.getResponseManager().mousePressed(e);
		}
	}

	/**
	 * Invoked when the mouse has been clicked. This means that a button has
	 * been pressed and released.
	 */
	public void mouseClicked(MouseEvent e) {
		if (!editing)
			presentationManager.getResponseManager().mouseClicked(e);
	}

	/** Invoked when the mouse enters a component. */
	public void mouseEntered(MouseEvent e) {
		if (!editing)
			presentationManager.getResponseManager().mouseEntered(e);
	}

	/** Invoked when the mouse exits a component. */
	public void mouseExited(MouseEvent e) {
		if (!editing)
			presentationManager.getResponseManager().mouseExited(e);
	}

	/** Invoked when the mouse has been moved with a button down. */
	public void mouseDragged(MouseEvent e) {
		if (!editing)
			presentationManager.getResponseManager().mouseDragged(e);
	}

	/** Invoked when the mouse has been moved with no button down. */
	public void mouseMoved(MouseEvent e) {
		if (!editing)
			presentationManager.getResponseManager().mouseMoved(e);
	}

	// ----------------------------------------------------------
	// This is the key listener implementation
	// ----------------------------------------------------------
	/** Invoked when a key has been pressed. */
	/*
	 * public void keyPressed(KeyEvent e) { int keyCode = e.getKeyCode(); if
	 * (e.isAltDown()) { } }
	 */
	/** Invoked when a key has been released. */
	/*
	 * public void keyReleased(KeyEvent e) {
	 * 
	 * int keyCode = e.getKeyCode();
	 * 
	 * if (keyCode == KeyEvent.VK_ESCAPE) { if (retFull.isFullScreen()) {
	 * retFull.returnFromFullScreen(); } } else if ((keyCode >= KeyEvent.VK_0)
	 * && (keyCode <= KeyEvent.VK_9)) { if (retFull.isFullScreen()) {
	 * retFull.activateTimingGroup(keyCode - KeyEvent.VK_0); } } else if
	 * (keyCode == KeyEvent.VK_X) { if (retFull.isFullScreen()) {
	 * retFull.activateTimingGroup(0); } }
	 * 
	 * // e.consume(); }
	 */
	/** Invoked when a key has been typed. */
	// public void keyTyped(KeyEvent e) {}
	// -------------------------------------------------------------
	// The ComponentListener implementation
	// -------------------------------------------------------------
	/**
	 * Whenever this component becomes hidden we disable the display and
	 * property selection menu.
	 */
	public void componentHidden(ComponentEvent e) {
		// System.out.println("EditablePresentationPanel.componentHidden()");
		// controller.setMenuBarEnabled(false);
		// disableDisplayProperty();
	}

	public void componentMoved(ComponentEvent e) {
		// System.out.println("EditablePresentationPanel.componentMoved()");
	}

	public void componentResized(ComponentEvent e) {
		// System.out.println("EditablePresentationPanel.componentResized()");
		if (activeDisplay != null) {
			activeDisplay.recompute(presentationManager);
			invalidate();
			// repaint();
		}
		Dimension size = getSize();
		Base.setScreenHeight(size.height);
		Base.setScreenWidth(size.width);
	}

	/**
	 * Whenever this component becomes visible we enable the display and
	 * property selection menu.
	 */
	public void componentShown(ComponentEvent e) {
		// System.out.println("EditablePresentationPanel.componentShown()");
		// controller.setMenuBarEnabled(true);
		// enableDisplayProperty();
	}
}
