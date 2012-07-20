package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Stop a currently running media player.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public class MediaPlayerStop extends MediaPlayer {
	public MediaPlayerStop() {
		setTitleAndTopic("Stop a media player", MEDIA_DSP | EXP);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected void computeGeometry() {
		// check4MediaTimer();
	}

	public void show(java.awt.Graphics g) {
		// System.out.println("MediaPlayerSync.show(Graphics): " +
		// staticMediaPlayer);
	}

	public void show() {
		// System.out.println("MediaPlayerSync.show(): " + staticMediaPlayer);
	}

	/** Here is where the music plays. */
	public void showGroup() {
		staticMediaPlayer.stop();
		RuntimeRegistry.remove(mpKey);
		// staticMediaPlayer.dispose();
	}

	/**
	 * This method is not used for non-graphic display objects.
	 */
	public void showGroup(java.awt.Graphics g) {
		// System.out.println("MediaPlayerSync.showGroup(Graphics): " +
		// staticMediaPlayer);
		showGroup();
	}
}
