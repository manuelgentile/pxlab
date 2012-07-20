package de.pxlab.pxl.sound;

import java.io.*;
import java.nio.*;
import java.util.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.*;

/**
 * A player for audio data. The player holds an internal play list and when
 * started, runs the play list until it is finished. All audio streams in the
 * play list must have the same AudioFormat.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * 2006/06/19 removed all MediaEvents but CLOSE_MEDIA
 */
public class Player implements LineListener {
	private List playList;
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	private AudioFormat audioFormat;
	private byte[] playBuffer;
	private boolean playing = false;
	private MediaEventListener mediaEventListener;

	/**
	 * Create a Player which uses the given AudioFormat.
	 * 
	 * @param format
	 *            the AudioFormat descriptor.
	 */
	public Player(AudioFormat format) {
		this((AudioInputStream) null, format);
	}

	/**
	 * Create a Player for the given AudioInputStream which uses the given
	 * AudioFormat. The method play() has to be used in order to start playing.
	 * 
	 * @param input
	 *            the first audio input data stream for the Player.
	 * @param format
	 *            the AudioFormat descriptor.
	 */
	public Player(AudioInputStream input, AudioFormat format) {
		playList = Collections.synchronizedList(new LinkedList());
		addInputStream(input, false);
		createLine(format);
		audioFormat = format;
		playBuffer = new byte[16384];
	}

	/**
	 * Create a Player for the given audio data array which uses the given
	 * AudioFormat. The method play() has to be used in order to start playing.
	 * 
	 * @param audioData
	 *            raw audio data.
	 * @param format
	 *            the AudioFormat descriptor.
	 */
	public Player(byte[] audioData, AudioFormat format) {
		InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
		AudioInputStream input = new AudioInputStream(byteArrayInputStream,
				format, audioData.length / format.getFrameSize());
		playList = Collections.synchronizedList(new LinkedList());
		addInputStream(input, false);
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
				format);
		createLine(format);
		audioFormat = format;
		playBuffer = new byte[16384];
	}

	/** Create the SourceDataLine object. */
	private void createLine(AudioFormat format) {
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
				format);
		try {
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.addLineListener(this);
		} catch (LineUnavailableException lux) {
			System.out.println("Player(): Source line unavailable!");
			System.exit(3);
		}
	}

	/** Implementation of the LineListener interface. */
	public void update(LineEvent e) {
		LineEvent.Type t = e.getType();
		if (t == LineEvent.Type.STOP) {
			Debug.time("Audio Data Line stopped:      ");
			/*
			 * if (mediaEventListener != null) {
			 * mediaEventListener.mediaActionPerformed(new
			 * MediaEvent(Player.this, ResponseCodes.STOP_MEDIA, 1000L *
			 * sourceDataLine.getMicrosecondPosition(),
			 * de.pxlab.pxl.HiresClock.getTimeNanos())); }
			 */
		} else if (t == LineEvent.Type.START) {
			Debug.time("Audio Data Line started:      ");
			/*
			 * if (mediaEventListener != null) {
			 * mediaEventListener.mediaActionPerformed(new
			 * MediaEvent(Player.this, ResponseCodes.START_MEDIA, 1000L *
			 * sourceDataLine.getMicrosecondPosition(),
			 * de.pxlab.pxl.HiresClock.getTimeNanos())); }
			 */
		} else if (t == LineEvent.Type.OPEN) {
			Debug.time("Audio Data Line opened:       ");
			/*
			 * if (mediaEventListener != null) {
			 * mediaEventListener.mediaActionPerformed(new
			 * MediaEvent(Player.this, ResponseCodes.OPEN_MEDIA, 1000L *
			 * sourceDataLine.getMicrosecondPosition(),
			 * de.pxlab.pxl.HiresClock.getTimeNanos())); }
			 */
		} else if (t == LineEvent.Type.CLOSE) {
			Debug.time("Audio Data Line closed:       ");
			/*
			 * Moved the MediaEvent.CLOSE event generatioon to the end of the
			 * Player thread's run() method. if (mediaEventListener != null) {
			 * mediaEventListener.mediaActionPerformed(new
			 * MediaEvent(Player.this, MediaEvent.CLOSE, 1000L *
			 * sourceDataLine.getMicrosecondPosition(),
			 * de.pxlab.pxl.HiresClock.getTimeNanos())); }
			 */
		}
	}

	/**
	 * Add an audio input stream to this player's play list.
	 * 
	 * @param input
	 *            the audio stream to be added.
	 * @param start
	 *            if true and the player is not yet playing then it is started.
	 *            If false then the input stream only is added to the player's
	 *            play list.
	 */
	public boolean addInputStream(AudioInputStream input, boolean start) {
		boolean r = false;
		if (input != null) {
			r = playList.add(input);
			if (start) {
				if (!playing) {
					play();
					Debug.time("Audio Data added and started: ");
				} else {
					Debug.time("Audio Data added to list:     ");
				}
			}
		}
		return r;
	}

	public void setMediaEventListener(MediaEventListener p) {
		// System.out.println("Player.addMediaEventListener(): " + p);
		mediaEventListener = p;
	}

	/** Start the player if the play list is nonempty. */
	public void play() {
		if (!playList.isEmpty()) {
			new PlayThread().start();
		}
	}
	/** The Thread which actually plays the sound data. */
	class PlayThread extends Thread {
		public void run() {
			Debug.time("Audio Player Thread started:  ");
			playing = true;
			if (!playList.isEmpty()) {
				try {
					sourceDataLine.open(audioFormat);
					// System.out.println("Player.PlayThread.run(): SourceDataLine opened");
					sourceDataLine.start();
					// System.out.println("Player.PlayThread.run(): SourceDataLine started");
					while (!playList.isEmpty()) {
						audioInputStream = (AudioInputStream) (playList
								.remove(0));
						Debug.time("Audio Player fetching stream: ");
						// System.out.println("Starting audio input stream " +
						// audioInputStream);
						try {
							int cnt;
							while ((cnt = audioInputStream.read(playBuffer, 0,
									playBuffer.length)) != -1) {
								if (cnt > 0) {
									sourceDataLine.write(playBuffer, 0, cnt);
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							System.exit(0);
						}
					}
					// System.out.println("Player.PlayThread.run(): SourceDataLine draining");
					sourceDataLine.drain();
					// System.out.println("Player.PlayThread.run(): SourceDataLine drained");
					// sourceDataLine.stop();
					// System.out.println("Player.PlayThread.run(): SourceDataLine stopped");
					sourceDataLine.close();
					// System.out.println("Player.PlayThread.run(): SourceDataLine closed");
				} catch (LineUnavailableException lux) {
					System.out
							.println("Player.PlayThread.run(): Source line unavailable!");
				}
			}
			playing = false;
			Debug.time("Audio Player Thread finished: ");
			/*
			 * Moved MediaEvent.CLOSE event generation here in order to make
			 * sure that the Player thread is stopped when the CLOSE event is
			 * detected.
			 */
			if (mediaEventListener != null) {
				mediaEventListener.mediaActionPerformed(new MediaEvent(
						Player.this, ResponseCodes.CLOSE_MEDIA,
						1000L * sourceDataLine.getMicrosecondPosition(),
						de.pxlab.pxl.HiresClock.getTimeNanos()));
			}
		}
	}
}
