package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * An uncorrelated 2-dimensional Gaussian pattern with a temporal Gaussian
 * envelope.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/19
 */
public class GaussianFest extends ModelFest {
	public GaussianFest() {
		setTitleAndTopic("ModelFest Gaussian Pattern", GRATING_DSP);
	}

	protected void computeGeometry() {
		super.computeGeometry();
		double[] a = Amplitude.getDoubleArray();
		grating.setGaussian(a[0]);
		if (!getFullRecompute()) {
			grating.computeColors();
		}
	}
}
