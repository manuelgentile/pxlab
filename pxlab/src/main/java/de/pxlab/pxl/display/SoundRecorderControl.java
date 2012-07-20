package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Controls a sound recorder across multiple display objects.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/03/04
 */
public class SoundRecorderControl extends SoundRecorder {
	/**
	 * Sound recorder command code.
	 * 
	 * @see de.pxlab.pxl.RecorderCodes
	 */
	public ExPar Command = new ExPar(GEOMETRY_EDITOR, RecorderCodes.class,
			new ExParValueConstant("de.pxlab.pxl.RecorderCodes.REC_START"),
			"Command code");

	public SoundRecorderControl() {
		setTitleAndTopic("Static sound recorder", AUDIO_DSP | EXP);
		Directory.set(".");
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}
	private String srKey = "SoundRecorderElement";
	private SoundRecorderElement staticRecorder = null;

	/** Override the media timer check. */
	protected void check4MediaTimer() {
	}

	protected int create() {
		staticRecorder = (SoundRecorderElement) RuntimeRegistry.get(srKey);
		if (staticRecorder == null) {
			staticRecorder = new SoundRecorderElement();
			RuntimeRegistry.put(srKey, staticRecorder);
		}
		enterDisplayElement(staticRecorder, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		int cmd = Command.getInt();
		if (cmd == RecorderCodes.REC_START) {
			staticRecorder.setProperties(Channels.getInt(), Volume.getDouble(),
					SampleRate.getDouble(), soundOutputFileName());
		}
	}

	public void show(java.awt.Graphics g) {
		System.out.println("SoundRecorderControl.show(Graphics): "
				+ staticRecorder);
	}

	public void show() {
		System.out.println("SoundRecorderControl.show(): " + staticRecorder);
	}

	/** Override the display's showGroup(). */
	public void showGroup() {
		// System.out.println("SoundRecorderControl.showGroup(): " +
		// staticRecorder);
		int cmd = Command.getInt();
		switch (cmd) {
		case RecorderCodes.REC_START:
			staticRecorder.show();
			break;
		case RecorderCodes.REC_PAUSE:
			staticRecorder.pause();
			break;
		case RecorderCodes.REC_CONTINUE:
			staticRecorder.cont();
			break;
		case RecorderCodes.REC_STOP:
			staticRecorder.stop();
			break;
		}
	}

	/**
	 * This method is not used for non-graphic display objects.
	 */
	public void showGroup(java.awt.Graphics g) {
		// System.out.println("SoundRecorderControl.showGroup(Graphics): " +
		// staticRecorder);
	}

	/** Must restore the original timingGroupFinished() method. */
	protected void timingGroupFinished(int group) {
		// System.out.println("SoundRecorderControl.timingGroupFinished() Command = "
		// + Command);
	}
}
