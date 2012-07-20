package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Start the media player. The player must be opened before it may be started.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/05/23
 */
public class MediaPlayerStart extends MediaPlayer {
	/**
	 * The delay between the show() method and the actual start of the media
	 * player.
	 */
	public ExPar StartDelay = new ExPar(RTDATA, new ExParValue(0),
			"Start delay duration");

	public MediaPlayerStart() {
		setTitleAndTopic("Start media player", MEDIA_DSP | EXP);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected void computeGeometry() {
		// check4MediaTimer();
	}

	public void show(java.awt.Graphics g) {
		// System.out.println("MediaPlayerStart.show(Graphics): " +
		// staticMediaPlayer);
		show();
	}

	public void show() {
		// System.out.println("MediaPlayerStart.show(): " + staticMediaPlayer);
	}

	public void showGroup() {
		staticMediaPlayer.start();
		StartDelay.set(HiresClock.ms(staticMediaPlayer.getStartDelay()));
	}

	public void showGroup(java.awt.Graphics g) {
		// System.out.println("MediaPlayerStart.showGroup(Graphics): " +
		// staticMediaPlayer);
		showGroup();
	}

	protected void timingGroupFinished(int group) {
		int t = Timer.getInt();
		if (t == TimerCodes.NO_TIMER) {
			// Nothing to do here: ResponseCode == 0
			// System.out.println("MediaPlayerStart.timingGroupFinished() no action.");
		} else if ((t & TimerBitCodes.END_OF_MEDIA_TIMER_BIT) != 0) {
			// System.out.println("MediaPlayerStart.timingGroupFinished() remove player.");
			// Player has finished, so remove it
			RuntimeRegistry.remove(mpKey);
			// staticMediaPlayer.dispose();
		} else {
		}
		Debug.show(Debug.EVENTS, this.toString() + " timing group " + group
				+ " finished with code " + ResponseCode.getString());
	}
}
