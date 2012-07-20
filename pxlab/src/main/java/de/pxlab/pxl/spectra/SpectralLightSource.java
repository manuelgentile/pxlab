package de.pxlab.pxl.spectra;

import de.pxlab.pxl.*;

/**
 * A spectral light source has a spectral distribution function and a luminance.
 * The luminance is that luminance which would result from a perfect white
 * reflector illuminated by this light source. The light source also contains
 * experimental parameters for the name of the spectral distribution and the
 * luminance value.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class SpectralLightSource extends SpectralLightFilter {
	protected ExPar luminancePar;

	/**
	 * Create a new spectral light source.
	 * 
	 * @param specPar
	 *            the experimental parameter which contains the name of the
	 *            spectral distribution.
	 * @param lmp
	 *            the experimental parameter whose value is the luminance of the
	 *            light source.
	 */
	public SpectralLightSource(ExPar specPar, ExPar lmp)
			throws SpectrumNotFoundException {
		super(specPar);
		normalize();
		spectrumPar = specPar;
		luminancePar = lmp;
	}

	/**
	 * Create an equal energy light source.
	 * 
	 * @param lmp
	 *            the experimental parameter whose value is the luminance of the
	 *            light source.
	 */
	public SpectralLightSource(ExPar lmp) {
		super(1.0F);
		luminancePar = lmp;
	}

	/**
	 * Get this light source's luminance parameter.
	 * 
	 * @return the experimental parameter which holds the intended luminance of
	 *         this light source.
	 */
	public ExPar getLuminancePar() {
		return (luminancePar);
	}

	/**
	 * Get the value of this light source's luminance parameter.
	 * 
	 * @return the intended luminance of this light source.
	 */
	public double getLuminance() {
		return (luminancePar.getDouble());
	}

	/** Set the data array of this spectral distribution. */
	public void setLuminance(double lum) {
		setLuminance(lum, this);
	}

	/** Set the data array of this spectral distribution. */
	public void setLuminance(double lum, Object source) {
		luminancePar.set(lum);
		int n = spectralLightListeners.size();
		for (int i = 0; i < n; i++) {
			((SpectralLightListener) spectralLightListeners.get(i))
					.luminanceChanged(source);
		}
	}
}
