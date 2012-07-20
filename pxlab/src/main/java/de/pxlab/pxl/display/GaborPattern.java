package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * A Gabor patch as a mixture of two colors. This is a complex sinusoidal wave
 * pattern multiplied by a Gaussian envelope function. The sinusoid is a cosine
 * with phase, amplitude and orientation being defined. Harmonic components my
 * be added by using array valued parameters.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 
 * 12/05/03 made this a subclass of ConvexMixturePattern
 */
public class GaborPattern extends ConvexMixturePattern {
	/**
	 * Vertical sinusoid frequency. Scaling is such that 1 Hz corresponds to a
	 * single period per image window.
	 */
	public ExPar VerticalFrequency = new ExPar(SMALL_DOUBLE,
			new ExParValue(8.0), "Vertical base frequency");
	/**
	 * Harmonic components. The default is 1. If this is an integer array then
	 * the respective harmonics are added to the base frequency. Phase and
	 * amplitude arrays are created if only single scalar values are defined.
	 */
	public ExPar VerticalHarmonics = new ExPar(SMALL_INT, new ExParValue(1),
			"Array of harmonics");
	/** Vertical sinusoid phase shift. */
	public ExPar VerticalPhase = new ExPar(0.0, 2.0 * Math.PI, new ExParValue(
			0.0), "Vertical phase shift");
	/** Vertical sinusoid amplitude. */
	public ExPar VerticalAmplitude = new ExPar(-1.0, 1.0, new ExParValue(1.0),
			"Amplitude factor");
	/**
	 * Orientation of 'vertical' sinusoid. An orientation of 0 corresponds to a
	 * vertical sinusoid.
	 */
	public ExPar VerticalOrientation = new ExPar(ANGLE, new ExParValue(0),
			"Orientation of Sinusoid");
	/** Horizontal standard deviation of the Gaussian envelope. */
	public ExPar HorizontalGaussianStandardDeviation = new ExPar(SCREENSIZE,
			new ExParValue(64),
			"Horizontal standard deviation of gaussian envelope");
	/** Vertical standard deviation of the Gaussian envelope. */
	public ExPar VerticalGaussianStandardDeviation = new ExPar(SCREENSIZE,
			new ExParValue(64),
			"Vertical standard deviation of gaussian envelope");

	public GaborPattern() {
		setTitleAndTopic("Complex Gabor Pattern", GRATING_DSP);
	}

	protected void computeGeometry() {
		// System.out.println("GaborPattern.computeGeometry()");
		grating.setLocation(LocationX.getInt(), LocationY.getInt());
		grating.setSize(Width.getInt(), Height.getInt());
		grating.setFixation(FixationType.getInt(), FixationSize.getInt());
		grating.setGaussianStandardDeviation(
				HorizontalGaussianStandardDeviation.getInt(),
				VerticalGaussianStandardDeviation.getInt());
		grating.setGabor(VerticalFrequency.getDouble(),
				VerticalHarmonics.getIntArray(),
				VerticalPhase.getDoubleArray(),
				VerticalAmplitude.getDoubleArray(),
				VerticalOrientation.getDouble());
		if (!getFullRecompute()) {
			grating.computeColors();
		}
	}
}
