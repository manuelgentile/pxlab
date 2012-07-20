package de.pxlab.pxl;

import java.io.*;
import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.sound.*;

/**
 * Records a sound clip.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/03/04
 */
public class SoundRecorderElement extends DisplayElement {
	protected AudioFormat audioFormat;
	protected String soundFile;
	protected FileRecorder recorder;

	public SoundRecorderElement() {
		setColorPar(ExPar.ScreenBackgroundColor);
	}

	public void setProperties(int channels, double volume, double sampleRate,
			String fn) {
		soundFile = fn;
		if (channels < 1)
			channels = 1;
		else if (channels > 2)
			channels = 2;
		int srt = (int) Math.round(sampleRate);
		if ((srt == 44100) || (srt == 22050) || (srt == 16000)
				|| (srt == 11025) || (srt == 8000)) {
			audioFormat = new AudioFormat(srt, 16, channels, true, true);
		} else {
			new ParameterValueError(
					"SoundRecorderElement.computeRecorder(): Illegal sample rate "
							+ srt + " Hz, changed to 11025 Hz.");
			audioFormat = new AudioFormat(11025, 16, channels, true, true);
		}
		recorder = new FileRecorder(soundFile, audioFormat);
	}

	/** Start recording. */
	public void show() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "SoundRecorderElement.show()");
		recorder.start();
	}

	public void pause() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "SoundRecorderElement.pause()");
		recorder.pause();
	}

	public void cont() {
		Debug.showTime(Debug.MEDIA_TIMING, this,
				"SoundRecorderElement.continue()");
		recorder.cont();
	}

	public void stop() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "SoundRecorderElement.stop()");
		recorder.stop();
	}

	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public ShortBuffer getAudioDataAsShort() {
		return null;
	}

	public byte[] getAudioData() {
		return null;
	}
}
