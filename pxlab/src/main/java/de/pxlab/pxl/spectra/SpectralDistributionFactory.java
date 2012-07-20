package de.pxlab.pxl.spectra;

import java.io.*;
import java.awt.*;
import java.net.URL; // for loading the Munsell data base
import java.net.MalformedURLException; // for loading the Munsell data base

import java.util.HashMap;

import de.pxlab.pxl.*;
import de.pxlab.gui.*; // for paint()

/**
 * A spectral disptribution is a float valued function tabulated over a certain
 * wavelength range. The limits and step size of the wavelength range are
 * included. Only constant step size spectra are supported.
 * 
 * <p>
 * Spectral distributions generally are described by names. These prefix
 * characters are used to identify the type of the spectral distribution:
 * 
 * <ul>
 * 
 * <li>D for CIE Daylight spectra like D65 or D6500 for CIE daylight at 6500 K.
 * 
 * <li>E for equal energy spectra like E1 or E0.5
 * 
 * <li>MG for one of 12 metameric gray reflectance functions: MG1, ..., MG12
 * 
 * <li>N for Munsell neutral notations like N 9.5/
 * 
 * <li>all other names are searched in the internal name table which contains
 * the Munsell Book of Colors, matte collection remittance functions and some
 * additional light spectra.
 * 
 * </ul>
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 08/16/00
 * 
 * 12/05/01 make sure that we use the Munsell.data file instead of the
 * MunsellData.class if we are running as an applet.
 */
public class SpectralDistributionFactory {
	/**
	 * This is the index set for spectral distributions in our data base.
	 */
	private static HashMap index = new HashMap(1500);
	/** First index for Munsell Book of Colors spectral data. */
	protected static final int MUNSELL_FIRST = 0;
	/** Index limit for Munsell Book of Colors spectral data. */
	protected static int munsellLimit;
	/** First index for light spectral data. */
	protected static final int LIGHTS_FIRST = 1400;
	/** Index Limit for light spectral data. */
	protected static int lightsLimit;
	private static int nMunsellColors;
	private static int nMunsellBases;
	private static int nMunsellWaves;
	private static float[][] munsellBasis;
	private static float[][] munsellWeight;
	private static String[] munsellNames;
	private static final String[] light = { "RedLight", "GreenLight",
			"SunLight4500", "SkyLight16200", "A", "B", "C", "MagentaLight" };
	static {
		readMunsellData();
		int n = light.length;
		for (int i = 0; i < n; i++) {
			register(light[i], i + LIGHTS_FIRST);
		}
		lightsLimit = LIGHTS_FIRST + n;
	}

	private static void register(String n, int i) {
		index.put(n, new Integer(i));
	}

	/**
	 * Find the index of a given name of a spectral distribution i our internal
	 * table of names of known spectra.
	 * 
	 * @param n
	 *            name of the spectral distribution to be searched.
	 * @return the index of the spectrum name in our internal name table.
	 */
	protected static int getIndex(String n) {
		Integer idx = (Integer) index.get(n);
		return ((idx == null) ? (-1) : idx.intValue());
	}

	/**
	 * Find out whether this class knows the given name of a spectral
	 * distribution.
	 * 
	 * @param n
	 *            name of the spectral distribution to be checked.
	 * @return true if our internal table contains spectrum name and false
	 *         otherwise.
	 */
	public static boolean contains(String n) {
		return (index.get(n) != null);
	}

	/**
	 * Create the named spectral distribution. A named spectral distribution can
	 * only be generated if it is contained in our table of distributions or may
	 * be computed from parameters.
	 * 
	 * @param n
	 *            name of the spectral distribution to generate.
	 */
	public static SpectralDistribution instance(String n)
			throws SpectrumNotFoundException {
		// System.out.println("Looking for spectral distribution " + n);
		SpectralDistribution d = null;
		if (n.startsWith("D")) {
			d = CIEDaylightDistribution.instance(n);
		} else if (n.startsWith("E")) {
			d = new SpectralDistribution(380, 720, 5, energy(n));
		} else if (n.startsWith("MG")) {
			d = MetamericGrayDistribution.instance(n);
		} else if (n.startsWith("N")) {
			d = getMunsellNeutralReflectance(n);
		} else {
			int i = getIndex(n);
			// System.out.println("Index of spectral distribution " + n + " is "
			// + i);
			if ((i >= MUNSELL_FIRST) && (i < munsellLimit)) {
				d = new SpectralDistribution(380, 720, 5,
						munsellChromaticReflectance(i - MUNSELL_FIRST));
			} else if ((i >= LIGHTS_FIRST) && (i < lightsLimit)) {
				d = SpectralLightDistribution.instance(i - LIGHTS_FIRST);
			}
		}
		if (d == null) {
			// d = getMunsellNeutralReflectance("N 5");
			throw new SpectrumNotFoundException(n);
		}
		return d;
	}

	/**
	 * Create a Munsell spectral reflectance which uses only the first m basis
	 * vectors.
	 */
	public static SpectralDistribution reducedMunsellReflectance(String n, int m)
			throws SpectrumNotFoundException {
		SpectralDistribution d = null;
		int i = getIndex(n);
		// System.out.println("Index of spectral distribution " + n + " is " +
		// i);
		if ((i >= MUNSELL_FIRST) && (i < munsellLimit)) {
			d = new SpectralDistribution(380, 720, 5,
					reducedMunsellChromaticReflectance(i - MUNSELL_FIRST, m));
		} else {
			// System.out.println(" ---> not in data base: " + n);
		}
		return (d);
	}

	/**
	 * Convert an equal energy specification like 'E1' or 'E0.5' to a float with
	 * the respective value.
	 * 
	 * @param n
	 *            the energy specification which is a captial letter 'E'
	 *            followed by a floating point number.
	 * @return the float value corresponding to the energy.
	 */
	private static float energy(String n) {
		float t = 0.0F;
		try {
			t = Float.valueOf(n.substring(1)).floatValue();
		} catch (NumberFormatException e) {
			new ParameterValueError("Invalid energy specification: " + n);
		}
		return (t);
	}

	/**
	 * Get a single component of the CIE 1931 2-degree color matching functions.
	 * The function is tabulated from 360 to 830 nm with 1 nm steps.
	 * 
	 * @return a SpectralDistribution object describing the x-component of the
	 *         color matching function.
	 */
	public static SpectralDistribution xCMF31() {
		return (new SpectralDistribution(CIECMF1931.first, CIECMF1931.last,
				CIECMF1931.step, CIECMF1931.x));
	}

	/**
	 * Get a single component of the CIE 1931 2-degree color matching functions.
	 * The function is tabulated from 360 to 830 nm with 1 nm steps.
	 * 
	 * @return a SpectralDistribution object describing the y-component of the
	 *         color matching function.
	 */
	public static SpectralDistribution yCMF31() {
		return (new SpectralDistribution(CIECMF1931.first, CIECMF1931.last,
				CIECMF1931.step, CIECMF1931.y));
	}

	/**
	 * Get a single component of the CIE 1931 2-degree color matching functions.
	 * The function is tabulated from 360 to 830 nm with 1 nm steps.
	 * 
	 * @return a SpectralDistribution object describing the z-component of the
	 *         color matching function.
	 */
	public static SpectralDistribution zCMF31() {
		return (new SpectralDistribution(CIECMF1931.first, CIECMF1931.last,
				CIECMF1931.step, CIECMF1931.z));
	}

	/**
	 * Get a single component of the CIE 1964 10-degree color matching
	 * functions. The function is tabulated from 360 to 830 nm with 1 nm steps.
	 * 
	 * @return a SpectralDistribution object describing the x-component of the
	 *         color matching function.
	 */
	public static SpectralDistribution xCMF64() {
		return (new SpectralDistribution(CIECMF1964.first, CIECMF1964.last,
				CIECMF1964.step, CIECMF1964.x));
	}

	/**
	 * Get a single component of the CIE 1964 10-degree color matching
	 * functions. The function is tabulated from 360 to 830 nm with 1 nm steps.
	 * 
	 * @return a SpectralDistribution object describing the y-component of the
	 *         color matching function.
	 */
	public static SpectralDistribution yCMF64() {
		return (new SpectralDistribution(CIECMF1964.first, CIECMF1964.last,
				CIECMF1964.step, CIECMF1964.y));
	}

	/**
	 * Get a single component of the CIE 1964 10-degree color matching
	 * functions. The function is tabulated from 360 to 830 nm with 1 nm steps.
	 * 
	 * @return a SpectralDistribution object describing the z-component of the
	 *         color matching function.
	 */
	public static SpectralDistribution zCMF64() {
		return (new SpectralDistribution(CIECMF1964.first, CIECMF1964.last,
				CIECMF1964.step, CIECMF1964.z));
	}
	private static String loadFileDirectory = null;
	private static String loadFileName = null;
	private static String saveFileDirectory = null;
	private static String saveFileName = null;

	/**
	 * Load a spectral distribution from a file whose name is asked for by a
	 * standard file dialog.
	 * 
	 * @return the spectral distribution whose data are contained in the file
	 *         set by the file dialog.
	 */
	public static SpectralDistribution load() {
		SpectralDistribution sd = null;
		FileDialog fd = new FileDialog(new Frame(),
				"Load Spectral Distribution", FileDialog.LOAD);
		if (loadFileDirectory != null)
			fd.setDirectory(loadFileDirectory);
		if (loadFileName != null)
			fd.setFile(loadFileName);
		fd.show();
		loadFileName = fd.getFile();
		if (loadFileName != null) {
			// System.out.println("Loading spectral distribution from file " +
			// loadFileName);
			loadFileDirectory = fd.getDirectory();
			sd = load(loadFileDirectory, loadFileName);
		}
		fd.dispose();
		return (sd);
	}

	/**
	 * Load a spectral distribution from a file.
	 * 
	 * @param fn
	 *            the name of the data file.
	 * @return the spectral distribution whose data are contained in the given
	 *         file.
	 */
	public static SpectralDistribution load(String fn) {
		return load(new File(fn));
	}

	/**
	 * Load a spectral distribution from a file.
	 * 
	 * @param fd
	 *            the directory path where the spectral data file is contained.
	 * @param fn
	 *            the name of the data file.
	 * @return the spectral distribution whose data are contained in the given
	 *         file.
	 */
	public static SpectralDistribution load(String fd, String fn) {
		return load(new File(fd, fn));
	}

	/**
	 * Load a spectral distribution from a file.
	 * 
	 * @param fs
	 *            the File object to read the data from.
	 * @return the spectral distribution whose data are contained in the given
	 *         file.
	 */
	public static SpectralDistribution load(File fs) {
		SpectralDistribution r = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fs));
			int[] w = new int[1000];
			float[] e = new float[1000];
			String ln, wn, en;
			int n = 0;
			try {
				while ((ln = br.readLine()) != null) {
					int bi = ln.indexOf(' ');
					try {
						w[n] = Integer.valueOf(ln.substring(0, bi)).intValue();
						e[n] = Float.valueOf(ln.substring(bi)).floatValue();
						n++;
					} catch (NumberFormatException ne) {
						new FileError("Error while reading from file "
								+ fs.getAbsolutePath());
						return (null);
					}
				}
				br.close();
				if (n < 1)
					return (null);
				float[] ee = new float[n];
				int dw = w[1] - w[0];
				boolean valid = true;
				for (int i = 1; i < n; i++) {
					// System.out.println(i + ": " + w[i] + " " + e[i]);
					if (w[i] - w[i - 1] != dw) {
						valid = false;
						break;
						/*
						 * new FileError("Unequal wavelength steps in file " +
						 * fs.getAbsolutePath()); return(null);
						 */
					}
				}
				if (valid) {
					System.arraycopy(e, 0, ee, 0, n);
					r = new SpectralDistribution(w[0], w[n - 1], dw, ee);
				} else {
					r = new SpectralDistribution(380, 720, 5,
							regularWavelengthRange(w, e, 380, 720, 5));
				}
			} catch (IOException ioe) {
				new FileError("Error while reading from file "
						+ fs.getAbsolutePath());
				return (null);
			}
		} catch (FileNotFoundException fnf) {
			new FileError("Can't open file " + fs.getAbsolutePath());
			return (null);
		}
		return (r);
	}

	/**
	 * Modify the wavelength range of this spectrum such that it spans the given
	 * range. Higher resolution data are interpolated linearly and range
	 * extensions are filled with zero values.
	 * 
	 * @param _first
	 *            lowest wavelength of the new range
	 * @param _last
	 *            highest wavelength of the new range
	 * @param _step
	 *            wavelength step size of the new range
	 */
	public static float[] regularWavelengthRange(int[] wv, float[] data,
			int _first, int _last, int _step) {
		// System.out.println("This wavelength range:   " + first + ", " + last
		// + ", " + step);
		// System.out.println("Target wavelength range: " + _first + ", " +
		// _last + ", " + _step);
		/*
		 * System.out.println(
		 * "SpectralDistributionFactory.regularWavelengthRange() Input: "); for
		 * (int i = 0; i < wv.length; i++) if (wv[i] > 0)
		 * System.out.println("  " + wv[i] + " " + data[i]);
		 */
		// First we create an intermediate array with wavelength steps of 1
		int n = (_last - _first) + 1;
		float[] e = new float[n];
		for (int i = 0; i < n; i++)
			e[i] = 0.0F;
		// Transfer the first data element
		int idx = wv[0] - _first;
		if ((idx >= 0) && (idx < n)) {
			e[idx] = data[0];
		}
		// Now transfer the following data elements
		for (int i = 1; (i < wv.length) && (wv[i] > 0); i++) {
			int idx1 = wv[i - 1] - _first;
			int idx2 = wv[i] - _first;
			int midx = idx2 - idx1;
			// check whether we have to interpolate something
			if (midx > 1) {
				for (int j = 1; j < midx; j++) {
					float a = (float) (midx - j) / (float) midx;
					e[idx1 + j] = a * data[i - 1] + (1.0F - a) * data[i];
				}
			}
			e[idx2] = data[i];
		}
		// At this point we have the required range in steps of 1
		// Now we can fill the target array
		n = (_last - _first) / _step + 1;
		float[] g = new float[n];
		int j = 0;
		for (int e_i = _first; e_i <= _last; e_i += _step) {
			// System.out.println("Moving wavelength " + i + ": " +
			// e[i-_first]);
			g[j++] = e[e_i - _first];
		}
		/*
		 * System.out.println(
		 * "SpectralDistributionFactory.regularWavelengthRange() Output: "); j =
		 * 0; for (int i = _first; i <= _last; i += _step)
		 * System.out.println("  " + i + " " + g[j++]);
		 */
		return g;
	}

	/**
	 * Extract the number from a Munsell Neutral reflectance specification like
	 * 'N 5.5/'.
	 * 
	 * @param s
	 *            the Munsell Neutral color specification.
	 * @return the numeric value contained in the string.
	 */
	private static double munsellNeutralValue(String s) {
		int i = s.indexOf("/");
		String ss = (i < 0) ? s.substring(2) : s.substring(2, i);
		double r = 0.0;
		try {
			r = Double.valueOf(ss).doubleValue();
		} catch (NumberFormatException e) {
			new ParameterValueError(
					"Invalid Munsell neutral number specification: " + s);
		}
		return (r);
	}

	private static SpectralDistribution getMunsellNeutralReflectance(String s) {
		double v = SpectralDistributionFactory.munsellNeutralValue(s);
		if ((v >= 2.0) && (v <= 9.5)) {
			int i = getIndex(s);
			if ((i >= MUNSELL_FIRST) && (i < munsellLimit)) {
				return new SpectralDistribution(380, 720, 5,
						SpectralDistributionFactory
								.munsellChromaticReflectance(i - MUNSELL_FIRST));
			} else {
				return new SpectralDistribution(380, 720, 5,
						SpectralDistributionFactory
								.munsellNeutralReflectanceValue(v));
			}
		}
		return new SpectralDistribution(380, 720, 5,
				SpectralDistributionFactory.munsellNeutralReflectanceValue(v));
	}

	/**
	 * Convert a Munsell Neutral value to a remittance value.
	 * 
	 * @param v
	 *            the Munsell Neutral value.
	 * @return the corresponding remittance value.
	 */
	private static float munsellNeutralReflectanceValue(double v) {
		double v2, v3, v4, v5;
		double r = 0.0;
		if (v > 9.9) {
			r = 1.0;
		} else if (v > 0.0) {
			v2 = v * v;
			v3 = v2 * v;
			v4 = v3 * v;
			v5 = v4 * v;
			r = ((1.2219 * v - 0.23111 * v2 + 0.23951 * v3 - 0.021009 * v4 + 0.0008404 * v5) / 100.0);
		}
		return ((float) r);
	}

	/**
	 * Convert a Munsell chromatic color specification into a spectral
	 * remittance function.
	 * 
	 * @param m
	 *            the Munsell chromatic color specification. It must be
	 *            contained in the Munsell Book of Colors, matte collection.
	 * @return a float array containing the respective remittance values.
	 */
	private static float[] munsellChromaticReflectance(int m) {
		return (reducedMunsellChromaticReflectance(m, 12));
	}

	/**
	 * Convert a Munsell chromatic color specification into a spectral
	 * remittance function which uses a reduced set of basis vectors.
	 * 
	 * @param m
	 *            the Munsell chromatic color specification. It must be
	 *            contained in the Munsell Book of Colors, matte collection.
	 * @param b
	 *            the number of basis vectors to use.
	 * @return a float array containing the respective remittance values.
	 */
	private static float[] reducedMunsellChromaticReflectance(int m, int b) {
		// System.out.println("Computing reflectance number " + m);
		float s;
		float[] r = new float[nMunsellWaves];
		if (b > 12)
			b = 12;
		for (int w = 0; w < nMunsellWaves; w++) {
			s = 0.0F;
			for (int i = 0; i < b; i++)
				s += munsellWeight[m][i] * munsellBasis[w][i];
			r[w] = s;
		}
		return (r);
	}

	/**
	 * Get a Munsell basis vector.
	 * 
	 * @param b
	 *            the number of the basis vector to return.
	 * @return a float array containing the respective basis vector.
	 */
	public static SpectralDistribution munsellBasisVector(int b) {
		float[] r = new float[nMunsellWaves];
		if (b > 12)
			b = 12;
		for (int w = 0; w < nMunsellWaves; w++) {
			r[w] = munsellBasis[w][b];
		}
		return (new SpectralDistribution(380, 720, 5, r));
	}

	/** Read the Munsell spectral remittance data file. */
	private static void readMunsellData() {
		String fn = "/de/pxlab/pxl/spectra/Munsell.data";
		InputStream ins = null;
		DataInputStream ds;
		Debug.show(Debug.FILES,
				"SpectralDistributionFactory.readMunsellData() Trying to read "
						+ fn);
		URL url = SpectralDistributionFactory.class.getResource(fn);
		Debug.show(Debug.FILES,
				"SpectralDistributionFactory.readMunsellData() URL = " + url);
		if (url != null) {
			try {
				ins = url.openStream();
			} catch (IOException uiox) {
				Debug.show(Debug.FILES,
						"SpectralDistributionFactory.readMunsellData(): Can't open stream for "
								+ url);
			}
		}
		if (ins != null) {
			ds = new DataInputStream(ins);
			Debug.show(Debug.FILES,
					"SpectralDistributionFactory.readMunsellData(): Data file opened.");
			try {
				nMunsellColors = ds.readInt();
				nMunsellBases = ds.readInt();
				nMunsellWaves = ds.readInt();
				for (int i = 0; i < nMunsellColors; i++) {
					register(new String(ds.readUTF()), i + MUNSELL_FIRST);
				}
				munsellLimit = nMunsellColors - MUNSELL_FIRST;
				munsellBasis = new float[nMunsellWaves][nMunsellBases];
				for (int i = 0; i < nMunsellWaves; i++) {
					for (int j = 0; j < nMunsellBases; j++) {
						munsellBasis[i][j] = ds.readFloat();
					}
				}
				munsellWeight = new float[nMunsellColors][nMunsellBases];
				for (int i = 0; i < nMunsellColors; i++) {
					for (int j = 0; j < nMunsellBases; j++) {
						munsellWeight[i][j] = ds.readFloat();
					}
				}
				ds.close();
				Debug.show(Debug.FILES,
						"SpectralDistributionFactory.readMunsellData(): Data read.");
			} catch (IOException e2) {
				new FileError("Error while reading Munsell data from file "
						+ fn);
			}
		} else {
			Debug.show(Debug.FILES,
					"SpectralDistributionFactory.readMunsellData(): Can't load Munsell data from "
							+ fn);
			/*
			 * Debug.show(Debug.MUNSELL_DATA,
			 * "Trying to load static class MunsellData."); nMunsellColors =
			 * MunsellData.nColors; nMunsellBases = MunsellData.nBases;
			 * nMunsellWaves = MunsellData.nWaves; for (int i = 0; i <
			 * nMunsellColors; i++) { register(MunsellData.notation[i], i +
			 * MUNSELL_FIRST); } munsellLimit = nMunsellColors - MUNSELL_FIRST;
			 * munsellBasis = MunsellData.basis; munsellWeight =
			 * MunsellData.weight; Debug.show(Debug.MUNSELL_DATA,
			 * "OK. Static class MunsellData loaded.");
			 */
		}
	}
}
