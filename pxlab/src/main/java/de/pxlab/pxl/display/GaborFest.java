package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A Gabor patch of two colors with a temporal Gaussian envelope.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/19
 */
public class GaborFest extends ModelFest {
	/**
	 * Spatial frequency of the underlaying sinusoid. Scaling is such that 1 Hz
	 * corresponds to 1 cycle per visual angle. The visual angle is defined by
	 * parameter PixelPerDegree.
	 */
	public ExPar Frequency = new ExPar(SMALL_DOUBLE, new ExParValue(4.0),
			"Frequency of the sinusoid");
	/** Spatial sinusoid phase shift in radians. */
	public ExPar Phase = new ExPar(0.0, 2.0 * Math.PI, new ExParValue(0.0),
			"Phase shift of the sinusoid");
	/**
	 * Orientation of the spatial sinusoid in degrees. A value of 0 corresponds
	 * to a vertical sinusoidal wave.
	 */
	public ExPar Orientation = new ExPar(ANGLE, new ExParValue(0),
			"Orientation of the sinusoid");
	/**
	 * If true then a concentric pattern is shown. Otherwise a linear pattern is
	 * shown.
	 */
	public ExPar Concentric = new ExPar(FLAG, new ExParValue(0),
			"Concentric pattern flag");
	/**
	 * If true then a concentric Bessel function pattern is shown instead of a
	 * sinusoid.
	 */
	public ExPar Bessel = new ExPar(FLAG, new ExParValue(0),
			"Bessel pattern flag");

	public GaborFest() {
		setTitleAndTopic("ModelFest Gabor Pattern", GRATING_DSP);
	}

	protected void computeGeometry() {
		// System.out.println("Gabor.computeGeometry()");
		super.computeGeometry();
		double[] f = Frequency.getDoubleArray();
		double[] p = Phase.getDoubleArray();
		double[] a = Amplitude.getDoubleArray();
		if (Bessel.getFlag()) {
			// System.out.println("Gabor.computeGeometry(): Bessel");
			grating.setBessel(f[0], a[0]);
		} else {
			if (Concentric.getFlag()) {
				// System.out.println("Gabor.computeGeometry(): Concentric Gabor");
				grating.setConcentricGabor(f[0], p[0], a[0]);
			} else {
				double[] o = Orientation.getDoubleArray();
				if (f.length == 1) {
					// System.out.println("Gabor.computeGeometry(): Gabor");
					grating.setGabor(f[0], p[0], a[0], o[0]);
				} else if (f.length == 2) {
					// System.out.println("Gabor.computeGeometry(): Plaid");
					grating.setPlaid(f[0], p[0], a[0], o[0], f[1],
							(p.length == 2) ? p[1] : p[0],
							(a.length == 2) ? a[1] : a[0],
							(o.length == 2) ? o[1] : o[0]);
				}
			}
		}
		if (!getFullRecompute()) {
			grating.computeColors();
		}
	}
}
