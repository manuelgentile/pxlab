package de.pxlab.pxl.spectra;

public interface SpectralLightListener {
	/**
	 * This tells the listener that the whole spectrum has been changed.
	 */
	public void spectrumChanged(Object o);

	/**
	 * This tells the listener that a single value of the spectrum has been
	 * changed.
	 */
	public void spectrumChanged(Object o, int w);

	/**
	 * This tells the listener that the luminance of a light source has been
	 * changed.
	 */
	public void luminanceChanged(Object o);

	public String getName();
}
