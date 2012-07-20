package de.pxlab.pxl.spectra;

import java.util.ArrayList;
import de.pxlab.pxl.*;

/**
 * A filtered spectral light contains a light source distribution and an array
 * of filter distributions. It also contains an output light distribution. The
 * light source distribution and filter layers are preserved and my be accessed
 * separately. The filtered light usually feeds its output light chromaticity
 * coordinates into a color parameter. The filtered light responds to changes of
 * its light source and filter distributions by recomputing its output spectrum
 * and chromaticity.
 */
public class FilteredSpectralLight implements SpectralLightListener,
		LightEmitter {
	/**
	 * This filtered light's list of spectral distributions. The first entry in
	 * this array is the light source of this filtered light. The second entry
	 * is the output light distribution of the whole emitted light. The
	 * remaining entries are spectral distributions of filter layers.
	 */
	protected ArrayList distributions = new ArrayList(4);
	protected ArrayList spectralLightListeners = new ArrayList();
	private String name;
	private ExPar colorPar;

	/**
	 * Create a new filtered light object which initially contains only the
	 * light source distribution and has no filter applied.
	 */
	public FilteredSpectralLight(SpectralLightSource light, String name) {
		this.name = name;
		distributions.add(light);
		light.addSpectralLightListener(this);
		distributions.add((SpectralDistribution) light.copy());
		recompute();
	}

	/**
	 * Create a new filtered light object which initially has one filter applied
	 * to its light source.
	 */
	public FilteredSpectralLight(SpectralLightSource light,
			SpectralLightFilter flt, String name) {
		this.name = name;
		distributions.add(light);
		light.addSpectralLightListener(this);
		distributions.add((SpectralDistribution) light.copy());
		distributions.add(flt);
		flt.addSpectralLightListener(this);
		recompute();
	}

	/**
	 * Create a new filtered light object which initially has two filters
	 * applied to its light source.
	 */
	public FilteredSpectralLight(SpectralLightSource light,
			SpectralLightFilter flt1, SpectralLightFilter flt2, String name) {
		this.name = name;
		distributions.add(light);
		light.addSpectralLightListener(this);
		distributions.add((SpectralDistribution) light.copy());
		distributions.add(flt1);
		distributions.add(flt2);
		flt1.addSpectralLightListener(this);
		flt2.addSpectralLightListener(this);
		recompute();
	}

	/**
	 * Create a new filtered light object which initially has three filters
	 * applied to its light source.
	 */
	public FilteredSpectralLight(SpectralLightSource light,
			SpectralLightFilter flt1, SpectralLightFilter flt2,
			SpectralLightFilter flt3, String name) {
		this.name = name;
		distributions.add(light);
		light.addSpectralLightListener(this);
		distributions.add((SpectralDistribution) light.copy());
		distributions.add(flt1);
		distributions.add(flt2);
		distributions.add(flt3);
		flt1.addSpectralLightListener(this);
		flt2.addSpectralLightListener(this);
		flt3.addSpectralLightListener(this);
		recompute();
	}

	/**
	 * Create a new filtered light object which initially has four filters
	 * applied to its light source.
	 */
	public FilteredSpectralLight(SpectralLightSource light,
			SpectralLightFilter flt1, SpectralLightFilter flt2,
			SpectralLightFilter flt3, SpectralLightFilter flt4, String name) {
		this.name = name;
		distributions.add(light);
		light.addSpectralLightListener(this);
		distributions.add((SpectralDistribution) light.copy());
		distributions.add(flt1);
		distributions.add(flt2);
		distributions.add(flt3);
		distributions.add(flt4);
		flt1.addSpectralLightListener(this);
		flt2.addSpectralLightListener(this);
		flt3.addSpectralLightListener(this);
		flt4.addSpectralLightListener(this);
		recompute();
	}

	/**
	 * Add the given spectral distribution as a filter to this filtered light
	 * object.
	 */
	/*
	 * public void addSpectralLightFilter(SpectralLightFilter f) {
	 * distributions.add(f); recompute(); }
	 */
	/**
	 * Remove the given spectral distribution from the list of filters of this
	 * filtered light object.
	 */
	/*
	 * public void removeSpectralLightFilter(SpectralLightFilter f) {
	 * distributions.remove(f); recompute(); }
	 */
	/**
	 * Return the number of filter layers of this filtered light. All spectral
	 * distributions but the first two in the distributions array are considered
	 * to be filters.
	 */
	public int getFilterCount() {
		return (distributions.size() - 2);
	}

	/** Return the number of distributions. */
	public int getCount() {
		return (distributions.size());
	}

	/**
	 * Return the k-th filter layer of this emitted light. Note that the first
	 * filter layer has index 2. The 0-th filter layer returns the spectral
	 * distribution of the light source and the 1st filter layer returns the
	 * emitted light spectrum.
	 */
	public SpectralDistribution getDistribution(int k) {
		return ((SpectralDistribution) distributions.get(k));
	}

	private double getLightSourceLuminance() {
		return ((SpectralLightSource) distributions.get(0)).getLuminance();
	}

	public String getName() {
		return (name);
	}

	public void setColorPar(ExPar cp) {
		colorPar = cp;
	}

	public ExPar getColorPar() {
		return (colorPar);
	}

	// ---------------------------------------------------------------
	// This is the SpectralLightListener implementation
	// ---------------------------------------------------------------
	/**
	 * This tells the listener that the whole spectrum has been changed.
	 */
	public void spectrumChanged(Object o) {
		recompute();
	}

	/**
	 * This tells the listener that a single value of the spectrum has been
	 * changed.
	 */
	public void spectrumChanged(Object o, int w) {
		recompute(w);
	}

	/**
	 * This tells the listener that the luminance of a light source has been
	 * changed.
	 */
	public void luminanceChanged(Object o) {
		recompute();
	}

	/**
	 * Recompute the spectral distribution of the emitted light from its light
	 * source and filter layers.
	 */
	public void recompute() {
		// System.out.println("FilteredSpectralLight.recompute() " + getName());
		SpectralDistribution ss = getDistribution(0);
		SpectralDistribution sd = getDistribution(1);
		System.arraycopy(ss.getData(), 0, sd.getData(), 0, ss.getLength());
		int n = distributions.size();
		for (int i = 2; i < n; i++) {
			sd.filter(getDistribution(i));
		}
		int m = spectralLightListeners.size();
		for (int i = 0; i < m; i++) {
			((SpectralLightListener) spectralLightListeners.get(i))
					.spectrumChanged(getDistribution(1));
		}
	}

	/**
	 * Recompute the spectral distribution of the emitted light from its light
	 * source and filter layers for a single given wavelength.
	 */
	public void recompute(int w) {
		// System.out.println("FilteredSpectralLight.recompute() " + getName() +
		// " at " + w + " nm");
		double s = getDistribution(0).valueAt(w);
		int n = distributions.size();
		for (int i = 2; i < n; i++) {
			s *= getDistribution(i).valueAt(w);
		}
		// System.out.println("FilteredSpectralLight.recompute() of " +
		// getName());
		getDistribution(1).setValueAt(w, s);
		int m = spectralLightListeners.size();
		for (int i = 0; i < m; i++) {
			// System.out.println("FilteredSpectralLight.recompute(): calling Listener "
			// + i);
			((SpectralLightListener) spectralLightListeners.get(i))
					.spectrumChanged(getDistribution(1), w);
		}
	}

	/**
	 * Add a spectral light listener. The listener is called whenever the
	 * spectral distribution or intensity of the LightEmitter has been changed.
	 */
	public void addSpectralLightListener(SpectralLightListener a) {
		// System.out.println("FilteredSpectralLight[" + getName() +
		// "]: adding listener " + a.getName());
		spectralLightListeners.add(a);
	}

	/** Remove the given spectral light listener from this emitter. */
	public void removeSpectralLightListener(SpectralLightListener a) {
		spectralLightListeners.remove(a);
	}

	/**
	 * Find that spectral distribution within this emitted light which is
	 * closest to the given value at the given wavelength.
	 */
	public int nearestSpectrum(int w, double d) {
		double dist = Double.MAX_VALUE;
		int n = distributions.size();
		int nearest = 0;
		for (int i = 0; i < n; i++) {
			if (Math.abs(d
					- ((SpectralDistribution) distributions.get(i)).valueAt(w)) < dist)
				nearest = i;
		}
		return (nearest);
	}

	/** Compute the CIE 1931 XYZ color coordinates for this emitted light. */
	public PxlColor toXYZ() {
		return (toXYZ(2));
	}

	/**
	 * Compute the CIE XYZ color coordinates for this emitted light and for one
	 * of the CIE standard observers (2 or 10 degree).
	 * 
	 * @param degree
	 *            either 2 or 10 for one of the CIE standard observers.
	 * @return the XYZ color coordinates for this emitted light.
	 */
	public PxlColor toXYZ(int degree) {
		double sLum = getDistribution(0).luminance();
		PxlColor c = getDistribution(1).toXYZ(degree);
		c.setY(getLightSourceLuminance() * c.getY() / sLum);
		return (c);
	}
	/**
	 * Compute the additive mixture of this emitted light with the given
	 * filtered light.
	 * 
	 * @param f
	 *            filtered light which should be added to this filtered light.
	 * @return a Light object which is the sum of both lights. public
	 *         FilteredSpectralLight addedTo(FilteredSpectralLight fb) {
	 *         FilteredSpectralLight s =
	 *         (FilteredSpectralLight)((FilteredSpectralLight
	 *         )getLight()).clone(); s.add(fb.getLight()); return(s); }
	 */
}
