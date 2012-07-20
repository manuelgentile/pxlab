package de.pxlab.pxl.display;

import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.sound.*;
import de.pxlab.pxl.*;
import de.pxlab.util.StringExt;

/**
 * A voice key which records sound from the microphone and works as a response
 * switch. The recorded sound can be stored into a sound file whose name is
 * created automatically.
 * 
 * <ul>
 * <li>Microphone recording level has to be set externally by the operating
 * system controls.
 * 
 * <li>The Duration parameter defines the maximum time to wait for a sound
 * signal passing threshold.
 * 
 * <li>The Threshold parameter defines the threshold for the response switch.
 * 
 * <li>This object always requires a TimerCodes.END_OF_MEDIA_TIMER.
 * 
 * <li>The recorded sound is written to a file if the parameter Directory is
 * nonempty.
 * 
 * </ul>
 * 
 * @version 0.1.2
 */
/*
 * 
 * 2005/03/04
 * 
 * 2008/02/28 observe flag StopImmediately
 */
public class VoiceKey extends SoundRecorder {
	/** Voice key threshold value. */
	public ExPar Threshold = new ExPar(INTEGER, new ExParValueVar(
			"VoiceKeyThreshold"), "Voice key threshold value");
	/**
	 * Voice key flag to stop recording immediately when the threshold has been
	 * passed.
	 */
	public ExPar StopImmediately = new ExPar(FLAG, new ExParValue(0),
			"Flag to stop recording when threshold is passed");

	public VoiceKey() {
		setTitleAndTopic("Voice key", AUDIO_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.END_OF_MEDIA_TIMER"));
		Directory.set("");
	}
	private VoiceKeyElement recorder;

	protected int create() {
		recorder = new VoiceKeyElement();
		enterDisplayElement(recorder, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		check4MediaTimer();
		recorder.setProperties(Duration.getInt(), Volume.getDouble(),
				SampleRate.getDouble(), Threshold.getDouble(),
				soundOutputFileName(), StopImmediately.getFlag(),
				(MediaEventListener) (presentationManager.getResponseManager()));
	}

	/** This method is here to re-define the respective superclass method. */
	protected void timingGroupFinished(int group) {
	}

	public AudioFormat getAudioFormat() {
		return recorder.getAudioFormat();
	}

	public ShortBuffer getAudioDataAsShort() {
		return recorder.getAudioDataAsShort();
	}

	public byte[] getAudioData() {
		return recorder.getAudioData();
	}
}
