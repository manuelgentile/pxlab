package de.pxlab.pxl.spectra;

/**
 * This class provides static data for some spectral light distributions. The
 * methods of this class should not be accessed directly but only via instances
 * of class <class>SpectralDistribution</class>.
 * 
 * <p>
 * These light distributions are currently available:
 * <ol>
 * <li>RedLight
 * <li>GreenLight
 * <li>SunLight4500
 * <li>BlueSky16200
 * <li>CIE standard light source A
 * <li>CIE standard light source B
 * <li>CIE standard light source C
 * <li>MagentaLight;
 * </ol>
 */
public class SpectralLightDistribution {
	private static final float[][] gt = {
			// Light: RedLight from file red1
			{ 17.0F, 13.0F, 17.0F, 14.0F, 14.0F, 18.0F, 14.0F, 15.0F, 18.0F,
					15.0F, 15.0F, 16.0F, 16.0F, 16.0F, 16.0F, 14.0F, 15.0F,
					15.0F, 16.0F, 17.0F, 17.0F, 18.0F, 19.0F, 19.0F, 20.0F,
					21.0F, 22.0F, 23.0F, 25.0F, 25.0F, 27.0F, 29.0F, 31.0F,
					34.0F, 57.0F, 62.0F, 66.0F, 69.0F, 72.0F, 75.0F, 77.0F,
					79.0F, 81.0F, 83.0F, 85.0F, 88.0F, 91.0F, 93.0F, 96.0F,
					98.0F, 95.0F, 96.0F, 96.0F, 97.0F, 97.0F, 97.0F, 97.0F,
					97.0F, 97.0F, 97.0F, 97.0F, 97.0F, 97.0F, 97.0F, 97.0F,
					97.0F, 97.0F, 97.0F, 98.0F, },
			// Light: GreenLight from file green1
			{ 17.0F, 18.0F, 19.0F, 20.0F, 22.0F, 23.0F, 24.0F, 25.0F, 27.0F,
					30.0F, 34.0F, 38.0F, 41.0F, 47.0F, 51.0F, 56.0F, 63.0F,
					73.0F, 77.0F, 79.0F, 84.0F, 87.0F, 89.0F, 93.0F, 96.0F,
					97.0F, 97.0F, 96.0F, 96.0F, 95.0F, 93.0F, 91.0F, 90.0F,
					87.0F, 80.0F, 76.0F, 74.0F, 71.0F, 68.0F, 65.0F, 62.0F,
					60.0F, 58.0F, 55.0F, 53.0F, 51.0F, 49.0F, 47.0F, 46.0F,
					44.0F, 41.0F, 38.0F, 36.0F, 35.0F, 33.0F, 32.0F, 31.0F,
					30.0F, 28.0F, 27.0F, 25.0F, 23.0F, 22.0F, 21.0F, 20.0F,
					19.0F, 18.0F, 17.0F, 17.0F, },
			// Light: SunLight4500 from file sunlite1
			{ 0.218280F, 0.238900F, 0.259717F, 0.330121F, 0.433517F, 0.478822F,
					0.515736F, 0.543798F, 0.561238F, 0.552911F, 0.567926F,
					0.623656F, 0.685549F, 0.747640F, 0.792552F, 0.821925F,
					0.847364F, 0.854642F, 0.871230F, 0.894899F, 0.905586F,
					0.887359F, 0.911356F, 0.931025F, 0.922109F, 0.935025F,
					0.934173F, 0.912470F, 0.925780F, 0.947023F, 0.964988F,
					0.972725F, 0.971938F, 0.979609F, 0.986362F, 0.976987F,
					0.967480F, 0.955940F, 0.946761F, 0.949384F, 0.970627F,
					0.965382F, 0.952269F, 0.959022F, 0.965644F, 0.969906F,
					0.961448F, 0.960858F, 0.960136F, 0.939483F, 0.941909F,
					0.964988F, 0.980855F, 0.967873F, 0.959218F, 0.956268F,
					0.984789F, 0.999148F, 0.997115F, 0.997836F, 0.970102F,
					0.880868F, 0.871099F, 0.918961F, 0.940467F, 0.950105F,
					0.931943F, 0.864805F, 0.819302F, },
			// Light: BlueSky16200 from file bluesky1
			{ 0.634475F, 0.659939F, 0.676886F, 0.772526F, 0.950945F, 0.989738F,
					0.995316F, 0.986076F, 0.950945F, 0.887966F, 0.858116F,
					0.890606F, 0.931698F, 0.962868F, 0.971300F, 0.957375F,
					0.940385F, 0.909768F, 0.882473F, 0.863609F, 0.836484F,
					0.785386F, 0.772185F, 0.757196F, 0.718447F, 0.700690F,
					0.674246F, 0.634006F, 0.617612F, 0.608074F, 0.598961F,
					0.584739F, 0.565151F, 0.545818F, 0.533555F, 0.511838F,
					0.489695F, 0.468404F, 0.450264F, 0.439022F, 0.432465F,
					0.418668F, 0.401720F, 0.392437F, 0.383751F, 0.372892F,
					0.362034F, 0.352069F, 0.342531F, 0.326350F, 0.319366F,
					0.320686F, 0.313916F, 0.303313F, 0.293987F, 0.285045F,
					0.287004F, 0.286152F, 0.277806F, 0.271759F, 0.257367F,
					0.229646F, 0.222705F, 0.229433F, 0.228922F, 0.226197F,
					0.219894F, 0.202351F, 0.191279F, },
			// Light: CIE_A from file cie_a.tab
			{ 9.80F, 10.90F, 12.09F, 13.35F, 14.71F, 16.15F, 17.68F, 19.29F,
					20.99F, 22.79F, 24.67F, 26.64F, 28.70F, 30.85F, 33.09F,
					35.41F, 37.81F, 40.30F, 42.87F, 45.52F, 48.24F, 51.04F,
					53.91F, 56.85F, 59.86F, 62.93F, 66.06F, 69.25F, 72.50F,
					75.79F, 79.13F, 82.52F, 85.95F, 89.41F, 92.91F, 96.44F,
					100.00F, 103.58F, 107.18F, 110.80F, 114.44F, 118.08F,
					121.73F, 125.39F, 129.04F, 132.70F, 136.35F, 139.99F,
					143.62F, 147.24F, 150.84F, 154.42F, 157.98F, 161.52F,
					165.03F, 168.51F, 171.96F, 175.38F, 178.77F, 182.12F,
					185.43F, 188.70F, 191.93F, 195.12F, 198.26F, 201.36F,
					204.41F, 207.41F, 210.36F, },
			// Light: CIE_B from file cie_b.tab
			{ 22.40F, 26.85F, 31.30F, 36.18F, 41.30F, 46.62F, 52.10F, 57.70F,
					63.20F, 68.37F, 73.10F, 77.31F, 80.80F, 83.44F, 85.40F,
					86.88F, 88.30F, 90.08F, 92.00F, 93.75F, 95.20F, 96.23F,
					96.50F, 95.71F, 94.20F, 92.37F, 90.70F, 89.65F, 89.50F,
					90.43F, 92.20F, 94.46F, 96.90F, 99.16F, 101.00F, 102.20F,
					102.80F, 102.92F, 102.60F, 101.90F, 101.00F, 100.07F,
					99.20F, 98.44F, 98.00F, 98.08F, 98.50F, 99.06F, 99.70F,
					100.36F, 101.00F, 101.56F, 102.20F, 103.05F, 103.90F,
					104.59F, 105.00F, 105.08F, 104.90F, 104.55F, 103.90F,
					102.84F, 101.60F, 100.38F, 99.10F, 97.70F, 96.20F, 94.60F,
					92.90F, },
			// Light: CIE_C from file cie_c.tab
			{ 33.00F, 39.92F, 47.40F, 55.17F, 63.30F, 71.81F, 80.60F, 89.53F,
					98.10F, 105.80F, 112.40F, 117.75F, 121.50F, 123.45F,
					124.00F, 123.60F, 123.10F, 123.30F, 123.80F, 124.09F,
					123.90F, 122.92F, 120.70F, 116.90F, 112.10F, 106.98F,
					102.30F, 98.81F, 96.90F, 96.78F, 98.00F, 99.94F, 102.10F,
					103.95F, 105.20F, 105.67F, 105.30F, 104.11F, 102.30F,
					100.15F, 97.80F, 95.43F, 93.20F, 91.22F, 89.70F, 88.83F,
					88.40F, 88.19F, 88.10F, 88.06F, 88.00F, 87.86F, 87.80F,
					87.99F, 88.20F, 88.20F, 87.90F, 87.22F, 86.30F, 85.30F,
					84.00F, 82.21F, 80.20F, 78.24F, 76.30F, 74.36F, 72.40F,
					70.40F, 68.30F, },
			// Light: MagentaLight from file magenta1
			{ 96.0F, 99.0F, 99.0F, 98.0F, 99.0F, 98.0F, 97.0F, 95.0F, 93.0F,
					91.0F, 89.0F, 86.0F, 82.0F, 76.0F, 72.0F, 55.0F, 40.0F,
					39.0F, 31.0F, 26.0F, 25.0F, 25.0F, 23.0F, 23.0F, 23.0F,
					23.0F, 24.0F, 24.0F, 24.0F, 25.0F, 28.0F, 32.0F, 34.0F,
					38.0F, 43.0F, 51.0F, 59.0F, 67.0F, 74.0F, 78.0F, 83.0F,
					86.0F, 88.0F, 90.0F, 90.0F, 91.0F, 92.0F, 93.0F, 93.0F,
					93.0F, 94.0F, 94.0F, 95.0F, 95.0F, 95.0F, 96.0F, 96.0F,
					96.0F, 96.0F, 96.0F, 97.0F, 97.0F, 97.0F, 97.0F, 97.0F,
					97.0F, 96.0F, 96.0F, 94.0F, }, };

	/**
	 * A spectral light 'exists' if it is contained in our data table.
	 * 
	 * @param n
	 *            the name of the spectral light distribution.
	 * @return true if the argument descibes a light distribution which is
	 *         contained in our data set. protected static boolean exists(String
	 *         n) { return(SpectralDistributionFactory.getIndex(n) != null); }
	 */
	/**
	 * Create a spectral light distribution with the given name.
	 * 
	 * @param n
	 *            the spectral distribution name.
	 * @return a spectral distribution function if the name is known or null if
	 *         the spectrum is not contained in our data base.
	 */
	protected static SpectralDistribution instance(String n) {
		SpectralDistribution d = null;
		int i = SpectralDistributionFactory.getIndex(n);
		if ((i >= SpectralDistributionFactory.LIGHTS_FIRST)
				&& (i < SpectralDistributionFactory.lightsLimit)) {
			d = new SpectralDistribution(380, 720, 5, (float[]) (gt[i
					- SpectralDistributionFactory.LIGHTS_FIRST].clone()));
		}
		return (d);
	}

	/**
	 * Create a spectral light distribution whose name is contained in the name
	 * table at the given index position.
	 * 
	 * @param i
	 *            index of the distribution in this class's internal name table.
	 * @return a spectral distribution function if the name is known or null if
	 *         the spectrum is not contained in our data base.
	 */
	protected static SpectralDistribution instance(int i) {
		return (new SpectralDistribution(380, 720, 5, (float[]) (gt[i].clone())));
	}
}
