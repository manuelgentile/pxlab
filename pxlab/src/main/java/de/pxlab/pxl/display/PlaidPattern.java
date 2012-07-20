package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * A Plaid pattern as a mixture of two colors. This is the sum of two complex
 * sinusoidal wave patterns multiplied by a Gaussian envelope function. The
 * sinusoids are cosines with phase, amplitude and orientation being defined.
 * Harmonic components my be added by using array valued parameters. The two
 * wave patterns are named 'vertical' and 'horizontal', but their orientation is
 * arbitrary.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class PlaidPattern extends GaborPattern {
	/**
	 * Horizontal sinusoid frequency. Scaling is such that 1 Hz corresponds to a
	 * single period per image window.
	 */
	public ExPar HorizontalFrequency = new ExPar(SMALL_DOUBLE, new ExParValue(
			4.0), "Horizontal base frequency");
	/**
	 * Harmonic components. The default is 1. If this is an integer array then
	 * the respective harmonics are added to the base frequency. Phase and
	 * amplitude arrays are created if only single scalar values are defined.
	 */
	public ExPar HorizontalHarmonics = new ExPar(SMALL_INT, new ExParValue(1),
			"Array of harmonics");
	/** Horizontal sinusoid phase shift. */
	public ExPar HorizontalPhase = new ExPar(0.0, 2.0 * Math.PI,
			new ExParValue(0.0), "Horizontal phase shift");
	/** Amplitude array. */
	public ExPar HorizontalAmplitude = new ExPar(-1.0, 1.0,
			new ExParValue(0.5), "Amplitude factor");
	/**
	 * Orientation of the 'horizontal' component. An orientation of 0
	 * corresponds to a vertical sinusoid.
	 */
	public ExPar HorizontalOrientation = new ExPar(ANGLE, new ExParValue(90),
			"Orientation of sinusoid");

	public PlaidPattern() {
		setTitleAndTopic("Complex Plaid Pattern", GRATING_DSP);
		VerticalAmplitude.set(0.5);
	}

	protected void computeGeometry() {
		// System.out.println("PlaidPattern.computeGeometry()");
		grating.setLocation(LocationX.getInt(), LocationY.getInt());
		grating.setSize(Width.getInt(), Height.getInt());
		grating.setFixation(FixationType.getInt(), FixationSize.getInt());
		grating.setGaussianStandardDeviation(
				HorizontalGaussianStandardDeviation.getInt(),
				VerticalGaussianStandardDeviation.getInt());
		grating.setPlaid(VerticalFrequency.getDouble(),
				VerticalHarmonics.getIntArray(),
				VerticalPhase.getDoubleArray(),
				VerticalAmplitude.getDoubleArray(),
				VerticalOrientation.getDouble(),
				HorizontalFrequency.getDouble(),
				HorizontalHarmonics.getIntArray(),
				HorizontalPhase.getDoubleArray(),
				HorizontalAmplitude.getDoubleArray(),
				HorizontalOrientation.getDouble());
		if (!getFullRecompute()) {
			grating.computeColors();
		}
	}
}
