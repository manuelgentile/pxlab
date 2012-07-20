package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;

/**
 * Handles a subject's keyboard response events while an experiment is running.
 * The response events are detected by the keyboard response dispatcher while an
 * experiment is running.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see ResponseManager
 * @see PresentationManager
 * @see KeyboardResponseDispatcher
 */
/*
 * 
 * 2004/10/28
 */
public interface KeyboardResponseListener {
	/** The subject has pressed a response key on the keyboard. */
	public void keyPressed(KeyEvent e);

	/** The subject has released a response key on the keyboard. */
	public void keyReleased(KeyEvent e);
}
