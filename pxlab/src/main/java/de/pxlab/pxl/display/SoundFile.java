package de.pxlab.pxl.display;

import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.*;
import de.pxlab.util.*;
import de.pxlab.pxl.sound.*;

/**
 * Plays a sound file.
 * 
 * <p>
 * For efficiency reasons a single instance of this SoundFile object can only
 * play files having the same AudioFormat.
 * 
 * <p>
 * This object always has to use a timer which has the
 * TimerCodes.END_OF_MEDIA_TIMER_BIT set.
 * 
 * @author H. Irtel
 * @version 0.2.2
 * @see de.pxlab.pxl.sound.Player
 */
/*
 * 
 * 2005/03/11
 * 
 * 2005/05/03 fixed sound event handling to use TimerCodes.END_OF_MEDIA_TIMER
 */
public class SoundFile extends SoundDisplay {
	public SoundFile() {
		setTitleAndTopic("Sound file player", AUDIO_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.END_OF_MEDIA_TIMER"));
	}
	private SoundFileElement soundElement;

	protected int create() {
		soundElement = new SoundFileElement();
		enterDisplayElement(soundElement, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		check4MediaTimer();
		soundElement
				.setProperties(Directory.getString(), FileName.getString(),
						Volume.getDouble(),
						(MediaEventListener) (presentationManager
								.getResponseManager()));
	}

	public AudioFormat getAudioFormat() {
		return soundElement.getAudioFormat();
	}

	public ShortBuffer getAudioDataAsShort() {
		return soundElement.getAudioDataAsShort();
	}

	public byte[] getAudioData() {
		return soundElement.getAudioData();
	}
}
