package de.pxlab.pxl.sound;

import javax.sound.sampled.*;

import de.pxlab.pxl.Debug;

/** An abstract sound recorder super class. */
abstract public class Recorder implements LineListener {
	protected AudioFormat audioFormat;
	protected TargetDataLine targetDataLine;
	protected Thread capture;

	/** Create the TargetDataLine object. */
	protected void createLine(AudioFormat format) {
		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,
				format);
		try {
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.addLineListener(this);
		} catch (LineUnavailableException lux) {
			System.out.println("Player(): Source line unavailable!");
			System.exit(3);
		}
	}

	abstract public void start();

	/** Implementation of the LineListener interface. */
	public void update(LineEvent e) {
		LineEvent.Type t = e.getType();
		if (t == LineEvent.Type.STOP) {
			Debug.showTime(Debug.MEDIA_TIMING, this,
					"Recorder.update(): Line stopped.");
		} else if (t == LineEvent.Type.START) {
			Debug.showTime(Debug.MEDIA_TIMING, this,
					"Recorder.update(): Line started.");
		} else if (t == LineEvent.Type.OPEN) {
			Debug.showTime(Debug.MEDIA_TIMING, this,
					"Recorder.update(): Line opened.");
		} else if (t == LineEvent.Type.CLOSE) {
			Debug.showTime(Debug.MEDIA_TIMING, this,
					"Recorder.update(): Line closed.");
		}
	}
}
