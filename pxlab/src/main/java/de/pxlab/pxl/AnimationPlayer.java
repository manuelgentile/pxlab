package de.pxlab.pxl;

import java.awt.Graphics;

/**
 * Plays frame animated display objects. The player shows single frames of
 * animated display objects. Frames are prepared by calling the Display object's
 * computeAnimationFrame() method with the proper frame number as an argument.
 * At each frame all timing groups of a display are run using the respective
 * timing parameters. However, only clock timers are evaluated, response
 * dependent timers for the timing groups are ignored. Frames are painted by
 * calling the Display object's showGroup(Graphics) method with the proper
 * timing group set. If a Display object is adjustable then its recompute
 * methods for geometry, timing, and color are called after the last timing
 * group has been shown and before the computeAnimationFrame() method is called.
 * The player also asks the active display for the FrameIncrement and for the
 * FramesPerCycle parameter values for controlling frame counting. Frame
 * presentation is synchronized to the vertical blanking interval if the
 * respective frame timer has the TimerCodes.VIDEO_SYNCHRONIZATION_BIT set. If
 * the timer type is END_OF_MEDIA_TIMER, then the player stops after a cycle is
 * completed.
 * 
 * @version 0.5.1
 */
/*
 * 
 * 2005/07/03 adapted to nanosecond timing
 * 
 * 2005/07/19 Optimized timing: Write the next timing group into the back buffer
 * immediately after a frame has been shown.
 * 
 * 2005/09/19 If the timer type is END_OF_MEDIA_TIMER, then the player stops
 * after a cycle is completed.
 */
public class AnimationPlayer extends Thread {
	/**
	 * This flag is set by calling PresentationManager.startAnimationPlayer()
	 * and inicates that the animation thread has been started. It is cleared by
	 * calling PresentationManager.stopAnimationPlayer() and forces the
	 * animation thread to stop.
	 */
	private boolean playing = false;
	/**
	 * If this is true then the animation player will stop after a single cycle
	 * of frames has been shown.
	 */
	private boolean stopAfterCycle;
	private Display activeDisplay;
	private DisplayDevice displayDevice;
	private MediaEventListener mediaEventListener;
	private WaitLock waitLock;

	public AnimationPlayer(Display activeDisplay, DisplayDevice displayDevice,
			int timerType, MediaEventListener mediaEventListener) {
		this.activeDisplay = activeDisplay;
		this.displayDevice = displayDevice;
		this.mediaEventListener = mediaEventListener;
		setPriority(Thread.MIN_PRIORITY);
		stopAfterCycle = ((timerType & TimerBitCodes.END_OF_MEDIA_TIMER_BIT) != 0);
		waitLock = new WaitLock();
	}

	/**
	 * Set the playing flag for the player. The player only runs if the playing
	 * flag is true. The player stops and finishes the thread when the playing
	 * flag becomes false.
	 */
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	/** This starts the thread which runs the animation. */
	public void run() {
		// System.out.println("AnimationPlayer.run()");
		int frame = 0;
		int frameIncrement = activeDisplay.getFrameIncrement();
		int framesPerCycle = activeDisplay.getFramesPerCycle();
		TimingElement timing;
		boolean syncToVB;
		int currentGroup;
		int lastGroup;
		long intendedDuration;
		long startTime = 0L;
		long recentShowTime;
		long nextShowTime;
		boolean first;
		boolean adjustable = activeDisplay.getAdjustable();
		Graphics g;
		Debug.time("AnimationPlayer started: ---> ");
		displayDevice.setActiveScreen(activeDisplay.Screen.getInt());
		lastGroup = activeDisplay.getLastTimingGroupIndex();
		activeDisplay.clearTimingGroup();
		currentGroup = 0;
		activeDisplay.nextTimingGroup();
		activeDisplay.computeAnimationFrame(frame);
		timing = activeDisplay.getTiming(currentGroup);
		intendedDuration = timing.getIntendedDuration();
		syncToVB = (timing.getTimerType() & TimerBitCodes.VIDEO_SYNCHRONIZATION_BIT) != 0;
		activeDisplay.clearBoundingBox();
		g = displayDevice.getGraphics();
		activeDisplay.showGroup(g);
		g.dispose();
		nextShowTime = 0L;
		first = true;
		while (playing) {
			waitLock.waitUntilNanos(nextShowTime);
			if (syncToVB)
				VideoSystem.waitForBeginOfVerticalBlank();
			displayDevice.show();
			recentShowTime = HiresClock.getTimeNanos();
			if (first) {
				first = false;
				startTime = recentShowTime;
			}
			Debug.time("Frame [" + frame + "," + currentGroup + "] ");
			nextShowTime = recentShowTime + intendedDuration;
			if (currentGroup == lastGroup) {
				if (adjustable) {
					activeDisplay.recomputeGeometry();
					activeDisplay.recomputeTiming();
					activeDisplay.recomputeColors();
					lastGroup = activeDisplay.getLastTimingGroupIndex();
				}
				frame += frameIncrement;
				if (frame >= framesPerCycle) {
					frame = 0;
					if (stopAfterCycle) {
						if (mediaEventListener != null) {
							mediaEventListener
									.mediaActionPerformed(new MediaEvent(this,
											ResponseCodes.CLOSE_MEDIA,
											recentShowTime - startTime,
											de.pxlab.pxl.HiresClock
													.getTimeNanos()));
						}
					}
				}
				if (playing) {
					activeDisplay.computeAnimationFrame(frame);
					activeDisplay.clearTimingGroup();
					currentGroup = 0;
				}
			} else {
				currentGroup++;
			}
			if (playing) {
				activeDisplay.nextTimingGroup();
				activeDisplay.clearBoundingBox();
				g = displayDevice.getGraphics();
				activeDisplay.showGroup(g);
				g.dispose();
				intendedDuration = activeDisplay.getTiming(currentGroup)
						.getIntendedDuration();
			}
		}
		Debug.time("AnimationPlayer finished:     ");
	}
}
