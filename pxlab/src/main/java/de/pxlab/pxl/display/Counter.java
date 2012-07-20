package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * An animated display showing a counter. The parameter Text will contain the
 * counter value after it has bee shown.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 06/27/01
 */
public class Counter extends TextDisplay {
	public ExPar Step = new ExPar(SMALL_INT, new ExParValue(1),
			"Counter Step Size");
	public ExPar InitialCount = new ExPar(0, 30000, new ExParValue(0),
			"Initial Counter Value");
	/** Timer for single animation frames. Should always be a clock timer. */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"Frame timer");
	/** Duration of a single animation frame. */
	public ExPar FrameDuration = new ExPar(DURATION, 0, 5000, new ExParValue(
			500), "Single Frame Duration");

	public Counter() {
		setTitleAndTopic("Dynamic Overlay Counter", ATTEND_DSP | EXP);
	}

	public boolean isAnimated() {
		return (true);
	}
	private TextElement clearText;
	private TextElement showText;
	private String previousText = "";
	private int count;
	private int step;

	protected int create() {
		// removeDisplayElements(0);
		int i = enterDisplayElement(clearText = new TextElement(
				ExPar.ScreenBackgroundColor), group[0]);
		enterDisplayElement(showText = new TextElement(Color), group[0]);
		int t = enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(-1);
		return (i);
	}

	protected void computeGeometry() {
		count = InitialCount.getInt();
		step = Step.getInt();
		// System.out.println("TwoStrings.computeGeometry()");
		String fnn = FontFamily.getString();
		int fnt = FontStyle.getInt();
		int fs = FontSize.getInt();
		clearText.setFont(fnn, fnt, fs);
		clearText.setLocation(LocationX.getInt(), LocationY.getInt());
		clearText.setText("");
		showText.setFont(fnn, fnt, fs);
		showText.setLocation(LocationX.getInt(), LocationY.getInt());
		previousText = String.valueOf(count);
		showText.setText(previousText);
		Text.set(previousText);
	}

	public void computeAnimationFrame(int frame) {
		clearText.setText(previousText);
		count += step;
		previousText = String.valueOf(count);
		showText.setText(previousText);
		// System.out.println("Counter.computeAnimationFrame() count = " +
		// count);
	}
}
