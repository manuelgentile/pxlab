package de.pxlab.pxl;

import java.io.*;
import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.sound.*;

/**
 * Records from a sound source until a certain threshold value is passed.
 * 
 * @version 0.1.2
 * @see de.pxlab.pxl.display.VoiceKey
 */
/*
 * 
 * 2005/03/04
 * 
 * 2008/02/28 observe flag stopImmediately
 */
public class VoiceKeyElement extends DisplayElement {
	protected AudioFormat audioFormat;
	protected VoiceKeyRecorder recorder;

	public VoiceKeyElement() {
		setColorPar(ExPar.ScreenBackgroundColor);
	}

	public void setProperties(int ms, double volume, double sampleRate,
			double threshold, String fn, boolean stopImmediately,
			MediaEventListener listener) {
		int srt = (int) Math.round(sampleRate);
		if ((srt == 44100) || (srt == 22050) || (srt == 16000)
				|| (srt == 11025) || (srt == 8000)) {
			audioFormat = new AudioFormat(srt, 16, 1, true, true);
		} else {
			new ParameterValueError(
					"SoundRecorderElement.computeRecorder(): Illegal sample rate "
							+ srt + " Hz, changed to 11025 Hz.");
			audioFormat = new AudioFormat(11025, 16, 1, true, true);
		}
		recorder = new VoiceKeyRecorder(ms * 1000000L, volume, threshold, fn,
				audioFormat);
		recorder.setStopImmediately(stopImmediately);
		recorder.setMediaEventListener(listener);
	}

	/** Start recording. */
	public void show() {
		recorder.start();
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
