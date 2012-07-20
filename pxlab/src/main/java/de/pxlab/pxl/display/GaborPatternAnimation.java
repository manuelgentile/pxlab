package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * An animated Gabor patch as a mixture of two colors. This is a complex
 * sinusoidal wave pattern multiplied by a Gaussian envelope function. The
 * sinusoid is a cosine with phase, amplitude and orientation being defined.
 * Harmonic components my be added by using array valued parameters. The wave
 * pattern is animated while the Gaussian envelope is static.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
public class GaborPatternAnimation extends GaborPattern {
	/**
	 * Number of animation frames per pattern cycle. This number determines the
	 * motion speed in combination with FrameDuration and VerticalFrequency.
	 */
	public ExPar FramesPerCycle = new ExPar(SMALL_INT, new ExParValue(16),
			"Animation frames per cycle");
	/** Modulation type: phase modulation (0) or amplitude modulation (1). */
	public ExPar AmplitudeModulation = new ExPar(FLAG, new ExParValue(0),
			"Amplitude modulation flag");
	/**
	 * Timer for the single animation frames. This should always be a clock
	 * timer which is synchronized to the vertical retrace.
	 */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"Frame timer");
	/**
	 * Duration of a single animation frame. This duration determines the motion
	 * speed in combination with FramesPerCycle and VerticalFrequency.
	 */
	public ExPar FrameDuration = new ExPar(DURATION, new ExParValue(20),
			"Single frame duration");

	public boolean isAnimated() {
		return (true);
	}

	/** Create an animated Gabor pattern. */
	public GaborPatternAnimation() {
		setTitleAndTopic("Animated Complex Gabor Pattern", GRATING_DSP);
		Duration.set(500);
	}
	protected ConvexMixtureElement[] gratings;
	protected int framesPerCycle = 0;
	protected int framesPerCycle2;

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
		int fpc = FramesPerCycle.getInt();
		if (fpc != framesPerCycle) {
			framesPerCycle = FramesPerCycle.getInt();
			framesPerCycle2 = (framesPerCycle + 1) / 2;
			gratings = new ConvexMixtureElement[framesPerCycle];
		}
		setFramesPerCycle(framesPerCycle);
		if (AmplitudeModulation.getFlag()) {
			amplitudeAnimation();
		} else {
			phaseAnimation();
		}
	}

	private void amplitudeAnimation() {
		double amp = 0.0;
		double ampStep = 2.0 * Math.PI / framesPerCycle;
		for (int i = 0; i < framesPerCycle2; i++) {
			gratings[i] = new ConvexMixtureElement(LowColor, HighColor,
					FixationColor);
			gratings[i].setLocation(LocationX.getInt(), LocationY.getInt());
			gratings[i].setSize(Width.getInt(), Height.getInt());
			gratings[i].setFixation(FixationType.getInt(),
					FixationSize.getInt());
			gratings[i].setGaussianStandardDeviation(
					HorizontalGaussianStandardDeviation.getInt(),
					VerticalGaussianStandardDeviation.getInt());
			double a = -Math.cos(amp) / 2.0 + 0.5;
			double[] amplitude = VerticalAmplitude.getDoubleArray();
			double[] amplitude2 = new double[amplitude.length];
			for (int j = 0; j < amplitude.length; j++)
				amplitude2[j] = a * amplitude[j];
			gratings[i].setGabor(VerticalFrequency.getDouble(),
					VerticalHarmonics.getIntArray(),
					VerticalPhase.getDoubleArray(), amplitude2,
					VerticalOrientation.getDouble());
			if (!getFullRecompute()) {
				gratings[i].computeColors();
			}
			amp += ampStep;
		}
		if ((framesPerCycle % 2) == 0) {
			gratings[framesPerCycle2] = grating;
			for (int i = 1; i < framesPerCycle2; i++) {
				gratings[framesPerCycle2 + i] = gratings[framesPerCycle2 - i];
			}
		} else {
			for (int i = 1; i < framesPerCycle2; i++) {
				gratings[framesPerCycle2 + i - 1] = gratings[framesPerCycle2
						- i];
			}
		}
	}

	private void phaseAnimation() {
		double shift = 0.0;
		double shiftStep = 2.0 * Math.PI / framesPerCycle;
		for (int i = 0; i < framesPerCycle; i++) {
			gratings[i] = new ConvexMixtureElement(LowColor, HighColor,
					FixationColor);
			gratings[i].setLocation(LocationX.getInt(), LocationY.getInt());
			gratings[i].setSize(Width.getInt(), Width.getInt());
			gratings[i].setFixation(FixationType.getInt(),
					FixationSize.getInt());
			gratings[i].setGaussianStandardDeviation(
					HorizontalGaussianStandardDeviation.getInt(),
					VerticalGaussianStandardDeviation.getInt());
			gratings[i].setGabor(VerticalFrequency.getDouble(),
					VerticalHarmonics.getIntArray(),
					VerticalPhase.getDoubleArray(),
					VerticalAmplitude.getDoubleArray(),
					VerticalOrientation.getDouble(), shift);
			if (!getFullRecompute()) {
				gratings[i].computeColors();
			}
			shift += shiftStep;
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
