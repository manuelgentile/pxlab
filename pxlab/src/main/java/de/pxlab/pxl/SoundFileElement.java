package de.pxlab.pxl;

import java.io.*;
import java.net.*;
import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.sound.*;

/**
 * Plays a sound file. A single instance of this SoundFileElement can only play
 * files having the same AudioFormat.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see de.pxlab.pxl.sound.Player
 */
public class SoundFileElement extends DisplayElement {
	protected AudioInputStream audioInputStream = null;
	protected AudioFormat audioFormat;
	protected String fileName = null;
	protected File soundFile = null;
	protected Player player = null;

	public SoundFileElement() {
		type = DisplayElement.AUDIO_BEEP;
		setColorPar(ExPar.ScreenBackgroundColor);
	}

	public void setProperties(String dir, String fn, double volume,
			MediaEventListener mediaEventListener) {
		// System.out.println("SoundFileElement.setProperties(): fn = " + fn);
		URL url = FileBase.url(dir, fn);
		if (url != null) {
			try {
				Debug.show(Debug.FILES, "Open audio stream from " + url);
				audioInputStream = AudioSystem.getAudioInputStream(url);
				audioFormat = audioInputStream.getFormat();
				if (player == null) {
					player = new Player(audioInputStream, audioFormat);
					player.setMediaEventListener(mediaEventListener);
				} else {
					player.addInputStream(audioInputStream, false);
				}
			} catch (UnsupportedAudioFileException uaf) {
				System.out
						.println("SoundFileElement.setProperties(): Unsupported audio format: "
								+ fn);
			} catch (IOException iox) {
				System.out
						.println("SoundFileElement.setProperties(): I/O Error: "
								+ fn);
			}
		}
	}

	public void show() {
		if (player != null)
			player.play();
	}

	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public ShortBuffer getAudioDataAsShort() {
		return (ShortBuffer) null;
	}

	public byte[] getAudioData() {
		return (byte[]) null;
	}
}
