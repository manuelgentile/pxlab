package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Open a media player for a given file but do not yet start to play. No timer
 * is involved.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/05/23
 */
public class MediaPlayerOpen extends MediaPlayer {
	public MediaPlayerOpen() {
		setTitleAndTopic("Open media player", MEDIA_DSP | EXP);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}
}
