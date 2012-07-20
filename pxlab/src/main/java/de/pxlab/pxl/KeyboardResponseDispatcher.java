package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;

/**
 * Dispatches keyboard events which arrive at the current KeyboardFocusManager
 * while an experiment is running. Break events are sent to the
 * ExDesignThreadStarter and all other keyboard events are sent to the
 * KeyboardResponseListener. This class is needed since frameless windows can
 * never get the keyboard focus and thus regular keyboard events are never sent
 * to full screen windows. Therefore we have to intercept all keyboard input
 * while an experiment is running.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see PresentationManager
 * @see KeyboardResponseListener
 * @see java.awt.KeyboardFocusManager
 */
/*
 * 
 * 2004/10/28
 */
public class KeyboardResponseDispatcher implements KeyEventDispatcher {
	/**
	 * The destination of keyboard break signals. These are used to immediately
	 * stop a running experiment.
	 */
	private ExDesignThreadStarter threadStarter;
	/** The mask which must be activated in order to accept the Break key. */
	// private int breakMask = 0;
	private int altBreakMask = (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
	/** The key code of the Break key. */
	private int breakKeyCode = KeyEvent.VK_PAUSE;
	private int altBreakKeyCode = KeyEvent.VK_ESCAPE;
	/** The destination of keyboard response events. */
	private KeyboardResponseListener keyboardResponseListener;

	/**
	 * Create a keyboard response dispatcher.
	 * 
	 * @param threadStarter
	 *            the destination of Break signals which may be used to stop a
	 *            running experiment.
	 * @param keyboardResponseListener
	 *            the destination of all keyboard response events.
	 */
	public KeyboardResponseDispatcher(ExDesignThreadStarter threadStarter,
			KeyboardResponseListener keyboardResponseListener) {
		this.threadStarter = threadStarter;
		this.keyboardResponseListener = keyboardResponseListener;
		// System.out.println("KeyboardResponseDispatcher()");
	}

	/* ------------------------------------------------------- */
	/* Implements the KeyEventDispatcher interface */
	/* ------------------------------------------------------- */
	/** Dispatch the given keyboard event to the proper destination. */
	public boolean dispatchKeyEvent(KeyEvent e) {
		/*
		 * System.out.println("KeyboardResponseDispatcher.dispatchKeyEvent(): ");
		 * System.out.println("  Event ID = " + e.getID());
		 * System.out.println("  Source = " +
		 * e.getSource().getClass().getName()); System.out.println("  Char = " +
		 * e.getKeyChar() + " [" + (int)e.getKeyChar() + "]");
		 * System.out.println("  Code = " + e.getKeyCode());
		 * System.out.println("  Text = " +
		 * KeyEvent.getKeyText(e.getKeyCode()));
		 * System.out.println("  Location = " + e.getKeyLocation());
		 * System.out.println("  Modifiers Mask = " + e.getModifiers());
		 * System.out.println("  ModifiersText = " +
		 * KeyEvent.getKeyModifiersText(e.getModifiers()));
		 * System.out.println("  Alt = " + (e.isAltDown()? "down": "up"));
		 * System.out.println("  AltGraph = " + (e.isAltGraphDown()? "down":
		 * "up")); System.out.println("  Control = " + (e.isControlDown()?
		 * "down": "up")); System.out.println("  Meta = " + (e.isMetaDown()?
		 * "down": "up")); System.out.println("  Shift = " + (e.isShiftDown()?
		 * "down": "up"));
		 * 
		 * /*
		 */
		int id = e.getID();
		if (id == KeyEvent.KEY_PRESSED) {
			if (isBreak(e)) {
				threadStarter.stopExperiment();
			} else {
				keyboardResponseListener.keyPressed(e);
			}
		} else if (id == KeyEvent.KEY_RELEASED) {
			keyboardResponseListener.keyReleased(e);
		} else if (id == KeyEvent.KEY_TYPED) {
			// ignore
		}
		return true;
	}

	private boolean isBreak(KeyEvent e) {
		/* int modifier = e.getModifiersEx(); */
		int keyCode = e.getKeyCode();
		return /* (((modifier & breakMask) == modifier) && */(keyCode == breakKeyCode) /* ) */
		/*
		 * || ((keyCode == altBreakKeyCode) && ((modifier & altBreakMask) ==
		 * modifier))
		 */;
	}
	/**
	 * Set the keyboard key combination which is used as a Break signal.
	 * 
	 * @param mask
	 *            defines the keyboard mask of Ctrl/Alt/Shift keys to be used.
	 * @param keyCode
	 *            is the code of the Break key.
	 */
	/*
	 * public void setBreakCode(int mask, int keyCode) { breakMask = mask;
	 * breakKeyCode = keyCode; }
	 */
}
