package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * An animated Plaid pattern as a mixture of two colors. This is the sum of two
 * complex sinusoidal wave patterns multiplied by a Gaussian envelope function.
 * The sinusoids are cosines with phase, amplitude and orientation being
 * defined. Harmonic components my be added by using array valued parameters.
 * The two wave patterns are named 'vertical' and 'horizontal', but their
 * orientation is arbitrary. The wave pattern is animated while the Gaussian
 * envelope is static.
 * 
 * <p>
 * Note that the program must actually create as many animation frames as the
 * smallest common multiple of VerticalFramesPerCycle and
 * HorizontalFramesPerCycle requires. Thus be careful when choosing these
 * parameter values.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
public class PlaidPatternAnimation extends PlaidPattern {
	/**
	 * Number of animation frames of the horizontal wave per horizontal pattern
	 * cycle. This number determines the motion speed in combination with
	 * FrameDuration and HorizontalFrequency. Note that the program must
	 * actually create as many frames as the smallest common multiple of
	 * VerticalFramesPerCycle and HorizontalFramesPerCycle requires. Thus be
	 * careful when choosing these parameter values.
	 */
	public ExPar HorizontalFramesPerCycle = new ExPar(SMALL_INT,
			new ExParValue(24), "Horizontal pattern frames per cycle");
	/**
	 * Number of animation frames of the vertical wave per vertical pattern
	 * cycle. This number determines the motion speed in combination with
	 * FrameDuration and VerticalFrequency. Note that the program must actually
	 * create as many frames as the smallest common multiple of
	 * VerticalFramesPerCycle and HorizontalFramesPerCycle requires. Thus be
	 * careful when choosing these parameter values.
	 */
	public ExPar VerticalFramesPerCycle = new ExPar(SMALL_INT, new ExParValue(
			24), "Vertical pattern frames per cycle");
	/** Modulation type: phase modulation (0) or amplitude modulation (1). */
	public ExPar AmplitudeModulation = new ExPar(FLAG, new ExParValue(0),
			"Amplitude modulation flag");
	/**
	 * Timer for the single animation frames. This should always be a clock
	 * timer which is synchronized to the vertical retrace.
	 */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValue(TimerCodes.VS_CLOCK_TIMER), "Frame Timer");
	/**
	 * Duration of a single animation frame. This duration determines the motion
	 * speed in combination with VerticalFramesPerCycle, VerticalFrequency,
	 * HorizontalFramesPerCycle, and HorizontalFrequency.
	 */
	public ExPar FrameDuration = new ExPar(DURATION, new ExParValue(20),
			"Single frame duration");

	public boolean isAnimated() {
		return (true);
	}

	/** Create an animated Plaid pattern. */
	public PlaidPatternAnimation() {
		setTitleAndTopic("Animated Complex Plaid Pattern", GRATING_DSP);
		Duration.set(500);
	}
	protected ConvexMixtureElement[] gratings;
	protected int horFramesPerCycle;
	protected int verFramesPerCycle;
	protected int framesPerCycle = 0;

	protected int create() {
		grating = new ConvexMixtureElement(LowColor, HighColor, FixationColor);
		int s = enterDisplayElement(grating, group[0]);
		int t = enterTiming(FrameTimer, FrameDuration, 0);
		return s;
	}

	protected void computeColors() {
		super.computeColors();
		if (gratings != null) {
			for (int i = 0; i < gratings.length; i++) {
				gratings[i].computeColors();
			}
		}
	}

	protected void computeGeometry() {
		super.computeGeometry();
		horFramesPerCycle = HorizontalFramesPerCycle.getInt();
		verFramesPerCycle = VerticalFramesPerCycle.getInt();
		int fpc = de.pxlab.util.MathExt.kgV(horFramesPerCycle,
				verFramesPerCycle);
		if (fpc != framesPerCycle) {
			framesPerCycle = fpc;
			gratings = new ConvexMixtureElement[framesPerCycle];
		}
		setFramesPerCycle(framesPerCycle);
		if (AmplitudeModulation.getFlag()) {
			// amplitudeAnimation();
			phaseAnimation();
		} else {
			phaseAnimation();
		}
	}

	private void phaseAnimation() {
		double verShift = 0.0;
		double verShiftStep = 2.0 * Math.PI / verFramesPerCycle;
		double horShift = 0.0;
		double horShiftStep = 2.0 * Math.PI / horFramesPerCycle;
		for (int i = 0; i < framesPerCycle; i++) {
			gratings[i] = new ConvexMixtureElement(LowColor, HighColor,
					FixationColor);
			gratings[i].setLocation(LocationX.getInt(), LocationY.getInt());
			gratings[i].setSize(Width.getInt(), Height.getInt());
			gratings[i].setFixation(FixationType.getInt(),
					FixationSize.getInt());
			gratings[i].setGaussianStandardDeviation(
					HorizontalGaussianStandardDeviation.getDouble(),
					VerticalGaussianStandardDeviation.getDouble());
			gratings[i].setPlaid(VerticalFrequency.getDouble(),
					VerticalHarmonics.getIntArray(),
					VerticalPhase.getDoubleArray(),
					VerticalAmplitude.getDoubleArray(),
					VerticalOrientation.getDouble(), verShift,
					HorizontalFrequency.getDouble(),
					HorizontalHarmonics.getIntArray(),
					HorizontalPhase.getDoubleArray(),
					HorizontalAmplitude.getDoubleArray(),
					HorizontalOrientation.getDouble(), horShift);
			if (!getFullRecompute()) {
				gratings[i].computeColors();
			}
			verShift += verShiftStep;
			horShift += horShiftStep;
		}
	}

	public void computeAnimationFrame(int frame) {
		if (frame < framesPerCycle) {
			grating.setImage(gratings[frame].getImage());
		}
	}

	protected void destroy() {
		for (int i = 0; i < framesPerCycle; i++) {
			if (gratings[i] != null)
				gratings[i].destroy();
		}
		super.destroy();
	}
}
