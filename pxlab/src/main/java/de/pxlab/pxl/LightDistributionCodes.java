package de.pxlab.pxl;

/**
 * Light distribution type codes for simulation of light mixtures.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public interface LightDistributionCodes {
	/** A simple homogenous disk in a constant background. */
	public static final int DISK = 0;
	/**
	 * A disk with a sinusoidal transition phase to the background.
	 */
	public static final int DEFOCUSSED_DISK = 1;
	/**
	 * Simulates a point light source at a certain distance from the projection
	 * screen.
	 */
	public static final int DISTANT_POINT_LIGHT = 2;
}
