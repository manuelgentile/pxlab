package de.pxlab.pxl;

/** Exception thrown if a required spectrum is not found. */
public class SpectrumNotFoundException extends Exception {
	public SpectrumNotFoundException(String n) {
		super("Can't find spectral distribution " + n);
	}
}
