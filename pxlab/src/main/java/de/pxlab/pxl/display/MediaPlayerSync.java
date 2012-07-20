package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Synchronize to the media time of a media data file played by the media
 * player. Two types of synchronization are possible: (1) wait until a certain
 * media time is reached and (2) wait until the end of the media data file has
 * been found. In order to wait for the end of the media data stream the Timer
 * parameter of this object must be set to END_OF_MEDIA_TIMER. If the
 * synchronization object should wait until a certain media time has been
 * reached its Timer must be set to SYNC_TO_MEDIA_TIMER and the parameter
 * MediaTime has to be set to the respective media time. The media time is given
 * in milliseconds relative to the start of the media stream. Thus in order to
 * synchronize to the start of the 10th frame of a 25 Hz video stream MediaTime
 * must be set to 9*40 = 360 ms.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public class MediaPlayerSync extends MediaPlayer {
	/**
	 * The media synchronization time in milliseconds. This is the media time
	 * relative to the start of the media which is currently playing.
	 */
	public ExPar MediaTime = new ExPar(DURATION, new ExParValue(500),
			"Media synchronization time");

	public MediaPlayerSync() {
		setTitleAndTopic("Sync to media player", MEDIA_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.SYNC_TO_MEDIA_TIMER"));
	}

	protected void computeGeometry() {
		check4MediaTimer();
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
		int t = Timer.getInt();
		if ((t & TimerBitCodes.SYNC_TO_MEDIA_TIMER_BIT) != 0) {
			staticMediaPlayer.setSyncAt(MediaTime.getInt() * 1000000L);
		}
	}

	/**
	 * This method is not used for non-graphic display objects.
	 */
	public void showGroup(java.awt.Graphics g) {
		// System.out.println("MediaPlayerSync.showGroup(Graphics): " +
		// staticMediaPlayer);
		showGroup();
	}

	protected void timingGroupFinished(int group) {
		int t = Timer.getInt();
		if (((t & TimerBitCodes.END_OF_MEDIA_TIMER_BIT) != 0)
				&& (ResponseCode.getInt() == ResponseCodes.CLOSE_MEDIA)) {
			// Player has finished, so remove it
			RuntimeRegistry.remove(mpKey);
		}
		Debug.show(Debug.EVENTS, this.toString() + " timing group " + group
				+ " finished with code " + ResponseCode.getString());
	}
}
