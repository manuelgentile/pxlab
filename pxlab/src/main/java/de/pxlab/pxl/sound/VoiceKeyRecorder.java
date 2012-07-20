package de.pxlab.pxl.sound;

import java.io.*;
import java.nio.*;
import java.util.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.*;

/**
 * A sound recorder which looks at the recorded sound and checks whether it
 * passes a given threshold. If this is the case then a response event is
 * generated. The recorder also can store the sound data into a WAV-file.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public class VoiceKeyRecorder extends FileRecorder {
	private static int recordingBufferSize = 44;
	private boolean stopImmediately = false;
	private int voiceKeyThreshold;
	private boolean voiceKey;
	private MediaEventListener mediaEventListener;
	private boolean recordFile;
	private int audioDataArraySize;
	private int numberOfChunks;
	private double volume;
	private ByteBuffer byteBuffer;
	private ShortBuffer shortBuffer;
	private byte[] audioData;

	/**
	 * Set the size of the recording buffer. This affects the delay when
	 * threshold passing is detected. It does not affect the resulting response
	 * time.
	 * 
	 * @param b
	 *            recording buffer size. Must be an integer multiple of 4.
	 *            Proper values may be 32, ..., 88.
	 */
	public static void setRecordingBufferSize(int b) {
		recordingBufferSize = 4 * (b / 4);
	}

	/**
	 * Set the flag which controls whether recording stops when the threshold
	 * has been passed or whether recording goes on until the time out interval
	 * has passed. Recording may be set to proceed after threshold passing in
	 * order to store the recorded sound into a sound file and analyze its
	 * content later.
	 * 
	 * @param s
	 *            must be true if the full duration is to be recorded or false
	 *            if recording stops after threshold passing.
	 */
	public void setStopImmediately(boolean s) {
		stopImmediately = s;
	}

	/**
	 * Create a Recorder which uses the given AudioFormat. The method start()
	 * must be used to start recording.
	 * 
	 * @param duration
	 *            the maximum recording duration in nanoseconds.
	 * @param volume
	 *            recording volume level (currently not used).
	 * @param threshold
	 *            the voice key threshold level.
	 * @param fileName
	 *            the name of the file for storing the recorded data. If null
	 *            then data are not stored.
	 * @param format
	 *            the AudioFormat descriptor.
	 */
	public VoiceKeyRecorder(long duration, double volume, double threshold,
			String fileName, AudioFormat format) {
		super(fileName, format);
		this.volume = volume;
		recordFile = (audioFile != null);
		// System.out.println("VoiceKeyRecorder():" + (recordFile? " ": " not ")
		// + "recording.");
		voiceKeyThreshold = (int) Math.round(threshold);
		audioDataArraySize = recordingBufferSize
				* (int) ((double) duration / 1000000000.0
						* audioFormat.getFrameRate()
						* audioFormat.getFrameSize() / recordingBufferSize);
		numberOfChunks = audioDataArraySize / recordingBufferSize;
	}

	public void setMediaEventListener(MediaEventListener p) {
		mediaEventListener = p;
	}

	/** Start recording. */
	public void start() {
		capture = new CaptureThread();
		capture.start();
	}

	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public ShortBuffer getAudioDataAsShort() {
		return shortBuffer;
	}

	public byte[] getAudioData() {
		return audioData;
	}
	private class CaptureThread extends Thread {
		private byte[] recordingBuffer;
		private int bytesRead;

		public CaptureThread() {
			recordingBuffer = new byte[recordingBufferSize];
			byteBuffer = ByteBuffer.wrap(recordingBuffer);
			shortBuffer = byteBuffer.asShortBuffer();
			audioData = new byte[audioDataArraySize];
		}

		public void run() {
			int maxValue = 0, m = 0, thresholdIndex = 0;
			int stoppingValue = 0;
			long startTime = 0L;
			long stopTime = 0L;
			double byteDelayFactor = 1000000000.0 / audioFormat.getFrameSize()
					/ audioFormat.getFrameRate();
			Debug.time("Voice key recorder startet:   ");
			Debug.show(Debug.VOICE_KEY, "Starting voice key recorder.");
			try {
				targetDataLine.open(audioFormat);
				targetDataLine.start();
				// Does not work! Controls.setVolume(targetDataLine, volume);
				/*
				 * if (mediaEventListener != null) {
				 * mediaEventListener.mediaActionPerformed(new
				 * MediaEvent(VoiceKeyRecorder.this, ResponseCodes.START_MEDIA,
				 * 1000L * targetDataLine.getMicrosecondPosition(),
				 * de.pxlab.pxl.HiresClock.getTimeNanos())); }
				 */
				int audioDataIndex = 0;
				boolean passedThreshold = false;
				boolean stoppedRecording = false;
				try {
					startTime = de.pxlab.pxl.HiresClock.getTimeNanos();
					while (!stoppedRecording
							&& (audioDataIndex < audioDataArraySize)) {
						bytesRead = targetDataLine.read(recordingBuffer, 0,
								recordingBuffer.length);
						if (bytesRead > 0) {
							shortBuffer.rewind();
							int n = 0;
							while (shortBuffer.remaining() > 0) {
								m = Math.abs(shortBuffer.get());
								if (!passedThreshold && (m > voiceKeyThreshold)) {
									Debug.time("Voice key threshold found:    ");
									Debug.show(Debug.VOICE_KEY,
											"Voice key threshold found.");
									stoppingValue = m;
									passedThreshold = true;
									thresholdIndex = audioDataIndex + n + n;
									stopTime = startTime
											+ Math.round(thresholdIndex
													* byteDelayFactor);
									if (mediaEventListener != null) {
										mediaEventListener
												.mediaActionPerformed(new MediaEvent(
														VoiceKeyRecorder.this,
														ResponseCodes.CLOSE_MEDIA,
														1000L * targetDataLine
																.getMicrosecondPosition(),
														stopTime));
									}
									stoppedRecording = stopImmediately;
								}
								if (m > maxValue)
									maxValue = m;
								n++;
							}
							byteBuffer.rewind();
							byteBuffer
									.get(audioData, audioDataIndex, bytesRead);
							audioDataIndex += bytesRead;
						}
					}
				} catch (Exception ex) {
					System.out.println(ex);
					System.exit(0);
				}
				targetDataLine.stop();
				targetDataLine.close();
				if (!passedThreshold) {
					stopTime = de.pxlab.pxl.HiresClock.getTimeNanos();
					if (mediaEventListener != null) {
						mediaEventListener
								.mediaActionPerformed(new MediaEvent(
										VoiceKeyRecorder.this,
										ResponseCodes.CLOSE_MEDIA,
										1000L * targetDataLine
												.getMicrosecondPosition(),
										stopTime));
					}
				}
				if (recordFile) {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(
							audioData);
					AudioInputStream audioStream = new AudioInputStream(
							inputStream, audioFormat, audioData.length
									/ audioFormat.getFrameSize());
					try {
						AudioSystem.write(audioStream, fileType, audioFile);
					} catch (IOException iox) {
						System.out
								.println("VoiceKeyRecorder.CaptureThread.run(): Error writing audio data file "
										+ audioFile.getPath());
					}
				}
			} catch (LineUnavailableException lux) {
				System.out
						.println("Recorder.CaptureThread.run(): Target line unavailable!");
			}
			Debug.time("Voice key recorder finished   ");
			if (Debug.isActive(Debug.VOICE_KEY)) {
				System.out.println("VoiceKeyRecorder: Maximum level was "
						+ maxValue);
				System.out.println("                  Stopping value was "
						+ stoppingValue);
			}
		}
	}
}
