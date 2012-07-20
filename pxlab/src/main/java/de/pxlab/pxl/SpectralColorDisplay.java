package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

import de.pxlab.pxl.spectra.*;

/**
 * A Display object which can contain spectral color definitions.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 07/03/02 removed these methods and fields from class Display in order to get
 * better modularity
 */
abstract public class SpectralColorDisplay extends Display {
	/**
	 * The list of spectral light and filter distributions used to control this
	 * display's spectral colors.
	 */
	private ArrayList spectralLightDistributions = new ArrayList();
	/**
	 * The list of spectral color distributions which actually are associated
	 * with surfaces and thus have associated color parameters.
	 */
	private ArrayList spectralColorDistributions = new ArrayList();
	/**
	 * This is the list of dependent color parameters of this display. This is
	 * currently only used by spectral demos which compute their colors from
	 * spectral distributions.
	 */
	private ArrayList spectralColorPars = new ArrayList();

	/**
	 * Get the list of spectral light and filter distributions used to control
	 * this display's spectral colors.
	 */
	public ArrayList getSpectralLightDistributions() {
		return (spectralLightDistributions);
	}

	/**
	 * Get the list of spectral color distributions which actually are
	 * associated with surfaces and thus have associated color parameters.
	 */
	public ArrayList getSpectralColorDistributions() {
		return (spectralColorDistributions);
	}

	/**
	 * Check whether this Display object has a spectral color distribution to
	 * show in a spectral color distribution panel.
	 * 
	 * @return true if the display has a spectral color distribution to show
	 *         such that the editor should open a spectral color distribution
	 *         panel for it.
	 */
	public boolean hasSpectralDistributions() {
		return ((spectralLightDistributions.size() != 0) || (spectralColorDistributions
				.size() != 0));
	}

	/**
	 * Recompute the dependent colors because one of the independent colors has
	 * been changed.
	 */
	public void recomputeColors() {
		recomputeSpectralColors();
		computeColors();
	}

	/** Recompute the spectral colors. */
	protected void recomputeSpectralColors() {
		// System.out.println("Display.recomputeSpectralColors()");
		int n = spectralColorDistributions.size();
		for (int i = 0; i < n; i++) {
			FilteredSpectralLight fsl = (FilteredSpectralLight) spectralColorDistributions
					.get(i);
			// fsl.recompute();
			ExParDescriptor xpd = (ExParDescriptor) spectralColorPars.get(i);
			xpd.getValue().set(fsl.toXYZ());
		}
	}

	// -----------------------------------------------------------------
	// Destroying and Updating a Display
	// -----------------------------------------------------------------
	/**
	 * Destroy this instance of the display because the user has selected
	 * another one to view. Clear the color table if requested and also give the
	 * display a chance to do some cleaning up.
	 */
	public void destroyInstance() {
		super.destroyInstance();
		// Clear the list of spectral color distributions and their parameters.
		spectralLightDistributions.clear();
		spectralColorDistributions.clear();
		spectralColorPars.clear();
		// System.out.println("destroyInstance(): " +
		// ((spectralDistributionPanel == null)? "no ": "") +
		// "  spectralDistributionPanel exists.");
	}

	/**
	 * Check whether this display can tell us its illuminant name and the name
	 * of the reflectance functions of its display elements.
	 * 
	 * @return true if the display has an illuminant and has reflectance
	 *         functions, false otherwise.
	 */
	public boolean canShowSpectralDistributions() {
		return (true);
	}

	/**
	 * Get the name of the illuminant spectral distribution function for the
	 * display element at the given index.
	 * 
	 * @return the name of the illuminant spectrum or null if unknown.
	 */
	public String getIlluminantName(int i) {
		return (null);
	}

	public String getIlluminantName(DisplayElement d) {
		return ((d == null) ? null : getIlluminantName(getIndexOf(d)));
	}

	/**
	 * Get the name of the reflectance function of the display element at the
	 * given index.
	 * 
	 * @return the name of the reflectance spectrum or null if unknown.
	 */
	public String getReflectanceName(int i) {
		return (null);
	}

	public String getReflectanceName(DisplayElement d) {
		return ((d == null) ? null : getReflectanceName(getIndexOf(d)));
	}

	// -----------------------------------------------------------------
	// Spectral Distribution List Access Methods
	// -----------------------------------------------------------------
	/**
	 * Enter a light source distribution to the spectral distribution panel.
	 * 
	 * @param nPar
	 *            name of the spectral distribution parameter.
	 * @param lumPar
	 *            parameter for the luminance of a perfect white reflectance
	 *            standard when this light source illuminates it.
	 */
	public SpectralLightSource getLightSource(ExPar nPar, ExPar lumPar) {
		// System.out.println("Display.getLightSource(): " + nPar.getString());
		SpectralLightSource source = null;
		try {
			source = new SpectralLightSource(nPar, lumPar);
			spectralLightDistributions.add(source);
		} catch (SpectrumNotFoundException snfe) {
			new ParameterValueError(snfe.getMessage());
			source = new SpectralLightSource(lumPar);
			spectralLightDistributions.add(source);
		}
		return source;
	}

	/**
	 * Enter a spectral light filter to the spectral distribution panel.
	 * 
	 * @param nPar
	 *            experimental parameter giving the name of the spectral
	 *            distribution.
	 */
	public SpectralLightFilter getLightFilter(ExPar nPar) {
		// System.out.println("Display.getLightFilter(): " + nPar.getString());
		SpectralLightFilter filter = null;
		try {
			filter = new SpectralLightFilter(nPar);
			spectralLightDistributions.add(filter);
		} catch (SpectrumNotFoundException snfe) {
			new ParameterValueError(snfe.getMessage());
			filter = new SpectralLightFilter(1.0F);
			spectralLightDistributions.add(filter);
		}
		return filter;
	}

	/**
	 * Enter a spectral light to the spectral distribution panel.
	 * 
	 * @param n
	 *            description of this light.
	 * @param ls
	 *            spectral light source distribution.
	 * @param f1
	 *            spectral distribution of first filter layer.
	 * @return the color index which is associated with this filter stack's
	 *         output light.
	 */
	public ExPar enterLight(String n, SpectralLightSource ls,
			SpectralLightFilter f1) {
		return (enterLight(new FilteredSpectralLight(ls, f1, n)));
	}

	/**
	 * Enter a spectral light to the spectral distribution panel.
	 * 
	 * @param n
	 *            description of this light.
	 * @param ls
	 *            spectral light source distribution.
	 * @param f1
	 *            spectral distribution of first filter layer.
	 * @param f2
	 *            spectral distribution of second filter layer.
	 * @return an experimental parameter which stores the color of this filter
	 *         stack's output light.
	 */
	public ExPar enterLight(String n, SpectralLightSource ls,
			SpectralLightFilter f1, SpectralLightFilter f2) {
		return (enterLight(new FilteredSpectralLight(ls, f1, f2, n)));
	}

	public ExPar enterLight(String n, SpectralLightSource ls,
			SpectralLightFilter f1, SpectralLightFilter f2,
			SpectralLightFilter f3) {
		return (enterLight(new FilteredSpectralLight(ls, f1, f2, f3, n)));
	}

	public ExPar enterLight(String n, SpectralLightSource ls,
			SpectralLightFilter f1, SpectralLightFilter f2,
			SpectralLightFilter f3, SpectralLightFilter f4) {
		return (enterLight(new FilteredSpectralLight(ls, f1, f2, f3, f4, n)));
	}

	/**
	 * Enter a spectral light to the spectral distribution panel.
	 * 
	 * @param sfs
	 *            spectral filter stack of this light.
	 * @return an experimental parameter which stores the color of this filter
	 *         stack's output light.
	 */
	public ExPar enterLight(FilteredSpectralLight sfs) {
		// System.out.println("Display.enterLight(): " + sfs.getName());
		// Create an ExPar object for this dependent color and add it
		// to the list of color pars
		String n = sfs.getName();
		ExPar cp = new ExPar(DEPCOLOR, new ExParValue(new PxlColor()),
				"Color of " + n);
		spectralColorPars.add(new ExParDescriptor(n, cp,
				ExParDescriptor.COLOR_EDIT));
		// System.out.println("Index for " + n + ": " + ci);
		// FilteredSpectralLightChart chart = new FilteredSpectralLight(sfs, cp,
		// displayPanel, n);
		sfs.setColorPar(cp);
		spectralColorDistributions.add(sfs);
		return (cp);
	}

	/**
	 * Return the color parameters of this display, including the depenendent
	 * parameters which have been created during instance creation.
	 */
	public ExParDescriptor[] getColorPars() {
		ArrayList cp = new ArrayList();
		for (int i = 0; i < exParFields.length; i++) {
			if (exParFields[i].getEditorType() == ExParDescriptor.COLOR_EDIT)
				cp.add(exParFields[i]);
		}
		int n1 = cp.size();
		int n2 = spectralColorPars.size();
		ExParDescriptor[] cpf = new ExParDescriptor[n1 + n2];
		int i = 0;
		for (int j = 0; j < n1; j++)
			cpf[i++] = (ExParDescriptor) cp.get(j);
		for (int j = 0; j < n2; j++)
			cpf[i++] = (ExParDescriptor) spectralColorPars.get(j);
		return (cpf);
	}
}
