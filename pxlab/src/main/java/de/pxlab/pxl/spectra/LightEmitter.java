package de.pxlab.pxl.spectra;

public interface LightEmitter {
	/**
	 * Add a spectral light listener. The listener is called whenever the
	 * spectral distribution or intensity of the LightEmitter has been changed.
	 */
	public void addSpectralLightListener(SpectralLightListener a);

	/** Remove the given spectral light listener from this emitter. */
	public void removeSpectralLightListener(SpectralLightListener a);
}
