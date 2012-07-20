package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/*
 import de.pxlab.util.*;
 import de.pxlab.pxl.sound.*;
 */
/**
 * Shows a movie or any other time based audiovisual media data file. This
 * object always uses a END_OF_MEDIA_TIMER such that its duration is identical
 * to the duration of the media file.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 2005/05/06
 * 
 * 2006/02/23
 */
public class Movie extends MediaPlayer {
	public Movie() {
		setTitleAndTopic("Movie", MEDIA_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.END_OF_MEDIA_TIMER"));
	}

	protected void timingGroupFinished(int group) {
		int t = Timer.getInt();
		if ((t & TimerBitCodes.END_OF_MEDIA_TIMER_BIT) != 0) {
			// Player has finished, so remove it
			RuntimeRegistry.remove(mpKey);
		}
		Debug.show(Debug.EVENTS, this.toString() + " timing group " + group
				+ " finished with code " + ResponseCode.getString());
	}
}
