package de.pxlab.pxl;

import java.awt.*;

/**
 * A simple audio beep.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 06/14/02
 */
public class AudioBeep extends DisplayElement {
	public AudioBeep() {
		type = DisplayElement.AUDIO_BEEP;
	}

	/** Present this audio beep signal. */
	public void show() {
		Toolkit.getDefaultToolkit().beep();
	}
}
