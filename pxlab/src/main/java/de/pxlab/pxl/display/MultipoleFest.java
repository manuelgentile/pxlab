package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A subgroup of grating (ModelFest) stimuli which includes the zero contrast
 * stimulus, edge, line, and dipole, disk and checkerboard pattern, and dynamic
 * and static white noise. Presentation may be dynamic and a spatial and
 * temporal Gaussian weight function is used.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/09/19
 */
public class MultipoleFest extends ModelFest {
	/**
	 * Size of pattern subelement measured in visual angle. For multipoles this
	 * is the width of the transition region. For disk and checkerboard stimuli
	 * this is the size of the respective stimulus element, and for the noise
	 * pattern this is the size of a noise super-pixel.
	 */
	public ExPar SubSize = new ExPar(SMALL_VISUAL_ANGLE, new ExParValue(
			1.0 / 60.0), "Subelement size");
	/**
	 * Stimulus type, including zero contrast stimulus, edge, line, and dipole,
	 * disk and checkerboard pattern, and dynamic and static white noise.
	 * 
	 * @see de.pxlab.pxl.MultipoleCodes
	 */
	public ExPar Type = new ExPar(GEOMETRY_EDITOR, MultipoleCodes.class,
			new ExParValueConstant("de.pxlab.pxl.MultipoleCodes.EDGE_PATTERN"),
			"Multipole type");

	public MultipoleFest() {
		setTitleAndTopic("ModelFest Multipole Pattern", GRATING_DSP);
	}
	private boolean firstNoiseInstance;

	protected void computeGeometry() {
		super.computeGeometry();
		double w = SubSize.getDouble();
		double a = Amplitude.getDouble();
		switch (Type.getInt()) {
		case MultipoleCodes.ZERO_PATTERN:
			grating.setZero();
			break;
		case MultipoleCodes.EDGE_PATTERN:
			grating.setEdge(a);
			break;
		case MultipoleCodes.LINE_PATTERN:
			grating.setLine(w, a);
			break;
		case MultipoleCodes.DIPOLE_PATTERN:
			grating.setDipole(w, a);
			break;
		case MultipoleCodes.DISK_PATTERN:
			grating.setDisk(w, a);
			break;
		case MultipoleCodes.CHECKERBOARD_PATTERN:
			grating.setCheckerboard(w, a);
			break;
		case MultipoleCodes.WHITE_NOISE_PATTERN:
			grating.setWhiteNoise(w, a);
			break;
		case MultipoleCodes.STATIC_WHITE_NOISE_PATTERN:
			String n = getInstanceName() + "WhiteNoise";
			Object p = RuntimeRegistry.get(n);
			if (p == null) {
				grating.setWhiteNoise(w, a);
				RuntimeRegistry.put(n, grating.getPattern());
			} else {
				grating.setPattern((float[][]) p);
			}
			break;
		}
		if (!getFullRecompute()) {
			grating.computeColors();
		}
	}
}
