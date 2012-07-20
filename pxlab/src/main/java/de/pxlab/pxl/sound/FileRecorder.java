package de.pxlab.pxl.sound;

import java.io.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.Debug;

/** A sound recorder which stores the sound data into a WAV-file. */
public class FileRecorder extends Recorder {
	protected AudioFileFormat.Type fileType = null;
	protected File audioFile = null;

	/**
	 * Create a Recorder which uses the given AudioFormat. The method start()
	 * must be used to start recording.
	 * 
	 * @param fileName
	 *            the name of the data file. MUST have extension 'wav'.
	 * @param format
	 *            the AudioFormat descriptor.
	 */
	public FileRecorder(String fileName, AudioFormat format) {
		createLine(format);
		audioFormat = format;
		fileType = AudioFileFormat.Type.WAVE;
		if (fileName != null) {
			audioFile = new File(fileName);
			Debug.show(Debug.FILES, "AudioFile = " + audioFile.getPath());
		}
	}

	/** Start recording. */
	public void start() {
		capture = new CaptureThread();
		capture.start();
		Debug.showTime(Debug.MEDIA_TIMING, this, "FileRecorder.start()");
	}

	/** Pause recording. */
	public void pause() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "FileRecorder.pause()");
		if (targetDataLine.isOpen() && targetDataLine.isActive()) {
			targetDataLine.stop();
		}
	}

	/** Continue recording. */
	public void cont() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "FileRecorder.cont()");
		if (targetDataLine.isOpen() && !targetDataLine.isActive()) {
			targetDataLine.start();
		}
	}

	/** Stop recording. */
	public void stop() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "FileRecorder.stop()");
		if (targetDataLine.isOpen()) {
			targetDataLine.stop();
			targetDataLine.close();
		}
	}
	/** Recording thread. */
	private class CaptureThread extends Thread {
		public void run() {
			try {
				targetDataLine.open(audioFormat);
				targetDataLine.start();
				try {
					AudioSystem.write(new AudioInputStream(targetDataLine),
							fileType, audioFile);
				} catch (IOException iox) {
					iox.printStackTrace();
				}
			} catch (LineUnavailableException lux) {
				System.out
						.println("Recorder.CaptureThread.run(): Target line unavailable!");
			}
		}
	}
}
