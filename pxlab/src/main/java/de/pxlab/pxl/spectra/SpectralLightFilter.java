package de.pxlab.pxl.spectra;

import java.util.ArrayList;
import de.pxlab.pxl.*;

/**
 * A spectral light filter has a spectral distribution function and a parameter
 * which holds the name of this distribution.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class SpectralLightFilter extends SpectralDistribution implements
		LightEmitter {
	protected ArrayList spectralLightListeners = new ArrayList();
	protected ExPar spectrumPar;

	/**
	 * Create a new spectral light filter which has the spectral distribution
	 * named by the given experimental parameter.
	 */
	public SpectralLightFilter(ExPar specPar) throws SpectrumNotFoundException {
		spectrumPar = specPar;
		copyData(SpectralDistributionFactory.instance(specPar.getString()));
	}

	/**
	 * Create a neutral density filter.
	 * 
	 * @param d
	 *            the filter transmission factor.
	 */
	public SpectralLightFilter(float d) {
		spectrumPar = new ExPar(ExParTypeCodes.STRING, new ExParValue(
				"Neutral density filter"), "");
		copyData(new SpectralDistribution(380, 720, 5, d));
	}

	public ExPar getSpectrumPar() {
		return (spectrumPar);
	}

	public String getName() {
		return (spectrumPar.getString());
	}

	/** Set the data array of this spectral distribution. */
	public void setData(float[] d) {
		setData(d, this);
	}

	/** Set the data array of this spectral distribution. */
	public void setData(float[] d, Object source) {
		super.setData(d);
		int n = spectralLightListeners.size();
		for (int i = 0; i < n; i++) {
			((SpectralLightListener) spectralLightListeners.get(i))
					.spectrumChanged(source);
		}
	}

	/**
	 * Set this spectrum's value at the wavelength nearest to the given
	 * argument.
	 * 
	 * @param w
	 *            wavelength of the spectrum which should be changed.
	 * @param s
	 *            new spectrum value.
	 */
	public void setValueAt(int w, double s) {
		setValueAt(w, s, this);
	}
	private static int counter = 0;

	/**
	 * Set this spectrum's value at the wavelength nearest to the given
	 * argument.
	 * 
	 * @param w
	 *            wavelength of the spectrum which should be changed.
	 * @param s
	 *            new spectrum value.
	 */
	public void setValueAt(int w, double s, Object source) {
		// counter++;
		// if (counter > 5) throw new RuntimeException("Exit");
		super.setValueAt(w, s);
		// System.out.println("SpectralLightFilter.setValueAt() of " +
		// getName());
		int n = spectralLightListeners.size();
		for (int i = 0; i < n; i++) {
			// System.out.println("SpectralLightFilter.setValueAt(): calling Listener "
			// +
			// ((SpectralLightListener)spectralLightListeners.get(i)).getName());
			((SpectralLightListener) spectralLightListeners.get(i))
					.spectrumChanged(source, w);
		}
	}

	/**
	 * Add a spectral light listener. The listener is called whenever the
	 * spectral distribution or intensity of the LightEmitter has been changed.
	 */
	public void addSpectralLightListener(SpectralLightListener a) {
		// System.out.println("SpectralLightFilter [" + getName() +
		// "]: adding listener " + a.getName());
		spectralLightListeners.add(a);
	}

	/** Remove the given spectral light listener from this emitter. */
	public void removeSpectralLightListener(SpectralLightListener a) {
		spectralLightListeners.remove(a);
	}
}
