package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A target center field with a surround of up to 8 different subfields.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 08/12/01
 */
public class ComplexColorContextAnimated extends ComplexColorContext {
	/** Number of frames per animation cycle. */
	public ExPar FramesPerCycle = new ExPar(1, 300, new ExParValue(80),
			"Animation frames per cycle");
	/** Timer for single animation frames. Should always be a clock timer. */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"Frame timer");
	/** Duration of a single animation frame. */
	public ExPar FrameDuration = new ExPar(DURATION, new ExParValue(30),
			"Single frame duration");

	public boolean isAnimated() {
		return (true);
	}

	public ComplexColorContextAnimated() {
		setTitleAndTopic("Complex color context animated",
				COMPLEX_COLOR_MATCHING_DSP);
	}

	protected int create() {
		int r = super.create();
		TimingElement te = getTiming(0);
		te.setTimerPar(FrameTimer);
		te.setIntendedDurationPar(FrameDuration);
		return r;
	}
	private int framesPerCycle;

	protected void computeGeometry() {
		super.computeGeometry();
		framesPerCycle = FramesPerCycle.getInt();
		setFramesPerCycle(framesPerCycle);
	}

	public void computeAnimationFrame(int frame) {
		contrast = 0.5 * (1.0 + Math.cos(2.0 * Math.PI
				* (double) (frame % framesPerCycle) / (double) framesPerCycle));
		super.computeColors();
	}
}
