package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A convenience superclass for frame animated Display objects. These are
 * objects which present a sequence of homogenous animation frames.
 * 
 * <p>
 * Note that not all Display objects which use frame animation can be subclasses
 * of this class since Java does not allow multiple inheritance.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/22
 */
abstract public class FrameAnimation extends Display {
	/**
	 * Timer for animation frames. This should always be a raw clock timer using
	 * vertical retrace synchronization.
	 */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"Frame timer");
	/**
	 * Duration of a single animation frame. The default is 40 ms which
	 * corresponds to 3 video frames on a 75 Hz display.
	 */
	public ExPar FrameDuration = new ExPar(DURATION, 0, 200,
			new ExParValue(40), "Single animation frame duration");
	/** Number of animation frames per display cycle. */
	public ExPar FramesPerCycle = new ExPar(SMALL_INT, new ExParValue(17),
			"Animation frames per cycle");

	public boolean isAnimated() {
		return (true);
	}
}
