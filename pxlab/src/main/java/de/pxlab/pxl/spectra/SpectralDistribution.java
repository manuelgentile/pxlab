package de.pxlab.pxl.spectra;

import java.io.*;
import java.awt.*;
import java.net.URL; // for loading the Munsell data base
import java.net.MalformedURLException; // for loading the Munsell data base

import java.util.HashMap;

import de.pxlab.pxl.*;
import de.pxlab.gui.Chart; // for paint()

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
public class SpectralDistribution implements Cloneable {
	/** Wavelength of the first entry in the spectrum data array. */
	private int first;
	/** Wavelength of the last entry in the spectrum data array. */
	private int last;
	/** Wavelength step size in the spectrum data array. */
	private int step;
	/** SpectralDistribution data array. */
	private float[] data;

	protected SpectralDistribution() {
		super();
	}

	/**
	 * Create a new SpectralDistribution object from the given range of
	 * wavelength values and the given spectral data array. Note that the data
	 * array is not copied but only its reference is copied. The current
	 * implementation supports only equally spaced spectral data.
	 * 
	 * @param first
	 *            wavelength of the first entry in the data array.
	 * @param last
	 *            wavelength of the last entry in the data array.
	 * @param step
	 *            wavelength step between successive entries in the data array.
	 * @param data
	 *            spectral data of the SpectralDistribution.
	 */
	public SpectralDistribution(int first, int last, int step, float[] data) {
		initData(first, last, step, data);
	}

	public SpectralDistribution(int first, int last, int step, double[] data) {
		float[] fd = new float[data.length];
		for (int i = 0; i < data.length; i++)
			fd[i] = (float) data[i];
		initData(first, last, step, fd);
	}

	/**
	 * Create a new SpectralDistribution object with constant spectral value.
	 * 
	 * @param first
	 *            wavelength of the first entry in the data array.
	 * @param last
	 *            wavelength of the last entry in the data array.
	 * @param step
	 *            wavelength step between successive entries in the data array.
	 * @param value
	 *            data value of the spectral distribution.
	 */
	public SpectralDistribution(int first, int last, int step, float value) {
		initData(first, last, step, value);
	}

	/**
	 * Create a copy of this object.
	 * 
	 * @return a copy of this <code>SpectralDistribution</code> object
	 *         containing its own and newly allocated data array.
	 */
	public Object clone() {
		try {
			SpectralDistribution p = (SpectralDistribution) (super.clone());
			float[] d = new float[data.length];
			System.arraycopy(data, 0, d, 0, data.length);
			p.data = d;
			return (p);
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new RuntimeException("");
		}
	}

	/**
	 * Create a copy of this object.
	 * 
	 * @return a copy of this <code>SpectralDistribution</code> object
	 *         containing its own and newly allocated data array.
	 */
	public SpectralDistribution copy() {
		float[] d = new float[data.length];
		System.arraycopy(data, 0, d, 0, data.length);
		SpectralDistribution p = new SpectralDistribution(first, last, step, d);
		return (p);
	}

	/**
	 * Return the data array of this spectral distribution.
	 * 
	 * @return the float data array.
	 */
	public float[] getData() {
		return (data);
	}

	/** Set the data array of this spectral distribution. */
	public void setData(float[] d) {
		data = d;
	}

	/**
	 * Set this SpectralDistribution object's parameter from the given range of
	 * wavelength values and the given spectral data array. Note that the data
	 * array is not copied but only its reference is copied. The current
	 * implementation supports only equally spaced spectral data.
	 * 
	 * @param first
	 *            wavelength of the first entry in the data array.
	 * @param last
	 *            wavelength of the last entry in the data array.
	 * @param step
	 *            wavelength step between successive entries in the data array.
	 * @param data
	 *            spectral data of the SpectralDistribution.
	 */
	protected void initData(int first, int last, int step, float[] data) {
		this.first = first;
		this.last = last;
		this.step = step;
		this.data = data;
	}

	/**
	 * Set this distribution's data with constant spectral value.
	 * 
	 * @param first
	 *            wavelength of the first entry in the data array.
	 * @param last
	 *            wavelength of the last entry in the data array.
	 * @param step
	 *            wavelength step between successive entries in the data array.
	 * @param value
	 *            data value of the spectral distribution.
	 */
	protected void initData(int first, int last, int step, float value) {
		this.first = first;
		this.last = last;
		this.step = step;
		int n = (last - first) / step + 1;
		data = new float[n];
		for (int i = 0; i < n; i++)
			data[i] = value;
	}

	/**
	 * Copy the data of the given spectral distribution to this
	 * SpectralDistribution object.
	 */
	protected void copyData(SpectralDistribution s) {
		this.first = s.first;
		this.last = s.last;
		this.step = s.step;
		this.data = s.data;
	}

	/**
	 * Return the length of the data array of this spectral distribution.
	 * 
	 * @return the number of entries in the distribution's data array.
	 */
	public int getLength() {
		return (data.length);
	}

	/**
	 * Get the wavelength of the first data element of this spectral
	 * distribution.
	 * 
	 * @return the wavelength.
	 */
	public int getFirst() {
		return (first);
	}

	/**
	 * Get the wavelength of the last data element of this spectral
	 * distribution.
	 * 
	 * @return the wavelength.
	 */
	public int getLast() {
		return (last);
	}

	/**
	 * Get the wavelength step size between successive data elements of this
	 * spectral distribution.
	 * 
	 * @return the wavelength step size.
	 */
	public int getStep() {
		return (step);
	}

	/*
	 * public int getType() { return(type); }
	 */
	/**
	 * Compute this spectrum's value at the given wavelength.
	 * 
	 * @return the spectrum value at the requested wavelength. Values not
	 *         contained in this spectrum's data array are interpolated
	 *         linearly.
	 */
	public double valueAt(int w) {
		double s = 0.0;
		if ((w >= first) && (w <= last)) {
			int wd = (w - first);
			int wdv = wd / step;
			int wmd = wd % step;
			if (wmd == 0) {
				s = (double) data[wdv];
			} else {
				s = ((step - wmd) * data[wdv] + wmd * data[wdv + 1])
						/ (double) step;
			}
		}
		return (s);
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
		if ((w >= first) && (w <= last)) {
			int wd = (w - first);
			int wdv = wd / step;
			int wmd = wd % step;
			if (wmd < (step / 2))
				data[wdv] = (float) s;
			else
				data[wdv + 1] = (float) s;
		}
	}
	/**
	 * Factor for converting radiant flux to luminous flux: luminous efficacy
	 */
	private static final double Km = 683.0;

	/**
	 * Assume that this spectrum is measured in [w/sr/m2/nm] and convert it to
	 * CIE 1931 XYZ-coordinates. Takes about 0.7 ms on my PC.
	 * 
	 * @return a PxlColor object describing the XYZ-coordinates of this energy
	 *         spectrum.
	 */
	public PxlColor toXYZ() {
		return (toXYZ(2));
	}

	/**
	 * Assume that this spectrum is measured in [w/sr/m2/nm] and convert it to
	 * CIE 1931 XYZ-coordinates or CIE 1964 XYZ-coordinates depending on whether
	 * the argument specifies 2 or 10 degree data.
	 * 
	 * @return a PxlColor object describing the XYZ-coordinates of this energy
	 *         spectrum.
	 */
	public PxlColor toXYZ(int deg) {
		double X = 0.0, Y = 0.0, Z = 0.0;
		double d;
		if (deg == 2) {
			// Check wavelength range
			if ((this.first < CIECMF1931.first)
					|| (this.last > CIECMF1931.last)) {
				throw new RuntimeException(
						"Wavelength bounds of spectra must be between 360 and 830 nm");
			}
			int cieIdx = this.first - CIECMF1931.first;
			int iLimit = (this.last - this.first) / this.step + 1;
			for (int i = 0; i < iLimit; i++) {
				d = (double) this.data[i];
				X += ((double) CIECMF1931.x[cieIdx] * d);
				Y += ((double) CIECMF1931.y[cieIdx] * d);
				Z += ((double) CIECMF1931.z[cieIdx] * d);
				cieIdx += this.step;
			}
		} else if (deg == 10) {
			// Check wavelength range
			if ((this.first < CIECMF1964.first)
					|| (this.last > CIECMF1964.last)) {
				throw new RuntimeException(
						"Wavelength bounds of spectra must be between 360 and 830 nm");
			}
			int cieIdx = this.first - CIECMF1964.first;
			int iLimit = (this.last - this.first) / this.step + 1;
			for (int i = 0; i < iLimit; i++) {
				d = (double) this.data[i];
				X += ((double) CIECMF1964.x[cieIdx] * d);
				Y += ((double) CIECMF1964.y[cieIdx] * d);
				Z += ((double) CIECMF1964.z[cieIdx] * d);
				cieIdx += this.step;
			}
		} else {
			throw new RuntimeException(
					"Only 2 or 10 degree CIE color matching data are available.");
		}
		double kmd = Km * (double) this.step;
		return (new PxlColor(kmd * X, kmd * Y, kmd * Z));
	}

	/**
	 * Assume that this spectrum is measured in [w/sr/m2/nm] and compute its
	 * luminance.
	 * 
	 * @return the luminance of this spectral light distribution.
	 */
	public double luminance() {
		float Y = 0.0F;
		// Check wavelength range
		if ((this.first < CIECMF1931.first) || (this.last > CIECMF1931.last)) {
			throw new RuntimeException(
					"Wavelength bounds of spectra must be between 360 and 830 nm");
		}
		int cieIdx = this.first - CIECMF1931.first;
		int iLimit = (this.last - this.first) / this.step + 1;
		for (int i = 0; i < iLimit; i++) {
			Y += (CIECMF1931.y[cieIdx] * this.data[i]);
			cieIdx += this.step;
		}
		float kmd = (float) Km * (float) this.step;
		return (kmd * Y);
	}

	/**
	 * Create a new spectral distribution which contains this spectrum filtered
	 * by the given spectral filter. A new data array is created. The wavelength
	 * range of this spectral distribution must be a subset of the filter's
	 * wavelength range.
	 * 
	 * @param filter
	 *            SpectralDistribution object which describes the filter.
	 * @return a new SpectralDistribution object which contains the filtered
	 *         data array.
	 */
	public SpectralDistribution filteredBy(SpectralDistribution filter) {
		// Check wavelength range
		if ((this.first >= filter.first) && (this.last <= filter.last)
				&& ((this.step % filter.step) == 0)) {
			int f_i = filter.indexOfWavelength(this.first);
			int f_istep = this.step / filter.step;
			int n = this.data.length;
			float[] e = new float[n];
			for (int i = 0; i < n; i++) {
				e[i] = this.data[i] * filter.data[f_i];
				f_i += f_istep;
			}
			return (new SpectralDistribution(this.first, this.last, this.step,
					e));
		} else {
			throw new RuntimeException(
					"Wavelength range is not a subset of the filter's wavelength range.");
		}
	}

	/**
	 * Filter this spectral distribution by the given spectral filter. The
	 * wavelength range of this spectral distribution must be a subset of the
	 * filter's wavelength range.
	 * 
	 * @param flt
	 *            SpectralDistribution object which describes the filter.
	 */
	public void filter(SpectralDistribution flt) {
		// Check wavelength range
		if ((this.first >= flt.first) && (this.last <= flt.last)
				&& ((this.step % flt.step) == 0)) {
			int f_i = flt.indexOfWavelength(this.first);
			int f_istep = this.step / flt.step;
			int n = this.data.length;
			for (int i = 0; i < n; i++) {
				this.data[i] *= flt.data[f_i];
				f_i += f_istep;
			}
		} else {
			throw new RuntimeException(
					"Wavelength range is not a subset of the filter's wavelength range.");
		}
	}

	/**
	 * Add the given spectral distribution to this spectral distribution. The
	 * wavelength range of both must be identical.
	 * 
	 * @param d
	 *            SpectralDistribution to be added.
	 */
	public void add(SpectralDistribution d) {
		// Check wavelength range
		if ((this.first == d.first) && (this.last == d.last)
				&& (this.step == d.step)) {
			int n = this.data.length;
			for (int i = 0; i < n; i++) {
				this.data[i] += d.data[i];
			}
		} else {
			throw new RuntimeException(
					"Wavelength range must be identical for addition.");
		}
	}

	/** Compute the data array index of the given wavelength. */
	private int indexOfWavelength(int w) {
		return ((w - first) / step);
	}

	/**
	 * Assume that this spectrum is measured in [w/sr/m2/nm] and set its energy
	 * level such that the resulting spectrum has the given Y-value or
	 * luminance.
	 * 
	 * @param luminance
	 *            the luminance or Y-value of the resulting SpectralDistribution
	 *            object.
	 */
	public void setLuminance(double luminance) {
		// Check wavelength range
		if ((this.first < CIECMF1931.first) || (this.last > CIECMF1931.last)) {
			throw new RuntimeException(
					"Wavelength bounds of spectra must be between 360 and 830 nm");
		}
		int cieIdx = this.first - CIECMF1931.first;
		int n = this.data.length;
		double Y = 0.0;
		for (int i = 0; i < n; i++) {
			Y += ((double) CIECMF1931.y[cieIdx] * (double) this.data[i]);
			cieIdx += this.step;
		}
		double t = luminance / (Km * (double) this.step * Y);
		for (int i = 0; i < n; i++) {
			this.data[i] *= t;
		}
	}

	/**
	 * Scale this spectrum such that its maximum value is equal to 1.0.
	 */
	public void normalize() {
		int n = data.length;
		float x = 0.0F;
		for (int i = 0; i < n; i++) {
			if (data[i] > x)
				x = data[i];
		}
		for (int i = 0; i < n; i++) {
			data[i] /= x;
		}
	}

	/** Find the maximum value of this spectrum. */
	public double maximumValue() {
		int n = data.length;
		float x = 0.0F;
		for (int i = 0; i < n; i++) {
			if (data[i] > x)
				x = data[i];
		}
		return ((double) x);
	}

	/**
	 * Find the wavelength with the maximum value of this spectrum.
	 */
	public int maximumWavelength() {
		int n = data.length;
		float x = 0.0F;
		int k = 0;
		for (int i = 0; i < n; i++) {
			if (data[i] > x) {
				x = data[i];
				k = i;
			}
		}
		return (k);
	}

	/**
	 * Scale this spectrum such that its value at the given wavelength is equal
	 * to 1.0.
	 */
	public void normalizeAt(int w) {
		int n = data.length;
		float x = (float) valueAt(w);
		for (int i = 0; i < n; i++) {
			data[i] /= x;
		}
	}

	/** Scale this spectrum by the given scaling factor. */
	public void scale(double f) {
		int n = data.length;
		float ff = (float) f;
		for (int i = 0; i < n; i++) {
			data[i] *= ff;
		}
	}

	/**
	 * Create a new spectrum which is scaled by the given factor.
	 * 
	 * @param f
	 *            scaling factor.
	 * @return the new spectral distribution.
	 */
	public SpectralDistribution scaledBy(double f) {
		int n = data.length;
		float[] e = new float[n];
		float ff = (float) f;
		for (int i = 0; i < n; i++) {
			e[i] = ff * data[i];
		}
		return (new SpectralDistribution(this.first, this.last, this.step, e));
	}

	/**
	 * Modify the wavelength range of this spectrum such that it is identical to
	 * the wavelength range of the given spectral distribution. Higher
	 * resolution data are interpolated linearly and range extensions are filled
	 * with zero values.
	 * 
	 * @param s
	 *            the spectral distribution whose wavelength range properties
	 *            should be cloned.
	 */
	public void modifyWavelengthRange(SpectralDistribution s) {
		modifyWavelengthRange(s.first, s.last, s.step);
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
	public void modifyWavelengthRange(int _first, int _last, int _step) {
		// System.out.println("This wavelength range:   " + first + ", " + last
		// + ", " + step);
		// System.out.println("Target wavelength range: " + _first + ", " +
		// _last + ", " + _step);
		// First we create an intermediate array with wavelength steps of 1
		int n = (_last - _first) + 1;
		float[] e = new float[n];
		int data_first_i, data_first_w, e_first_i, data_i, e_last_i, e_i, w;
		// Figure out the first index of data[] and e[] which will be used
		if (_first <= first) {
			data_first_i = 0;
			data_first_w = first;
			e_first_i = first - _first;
		} else {
			data_first_i = 0;
			for (w = first; w < _first; w += step)
				data_first_i++;
			data_first_w = w;
			e_first_i = data_first_w - _first;
		}
		// Now first transfer the existing data points
		data_i = data_first_i;
		e_i = e_first_i;
		for (w = data_first_w; (w <= last) && (w <= _last); w += step) {
			e[e_i] = data[data_i++];
			e_i += step;
		}
		// And then do the interpolation
		e_last_i = e_i - step;
		for (e_i = e_first_i; e_i < e_last_i; e_i += step) {
			for (int h = 1; h < step; h++) {
				e[e_i + h] = (float) (((double) (step - h) * e[e_i] + (double) h
						* e[e_i + step]) / (double) step);
			}
		}
		// At this point we have the required range in steps of 1
		// Now we can fill the target array
		n = (_last - _first) / _step + 1;
		float[] g = new float[n];
		int j = 0;
		for (e_i = _first; e_i <= _last; e_i += _step) {
			// System.out.println("Moving wavelength " + i + ": " +
			// e[i-_first]);
			g[j++] = e[e_i - _first];
		}
		this.data = null;
		e = null;
		// Finally we modify this spectrum's parameter
		this.data = g;
		this.first = _first;
		this.last = _last;
		this.step = _step;
	}
	private static String saveFileDirectory = null;
	private static String saveFileName = null;

	/**
	 * Save this spectral distribution to a file whose name is asked for by a
	 * standard file dialog.
	 */
	public void save() {
		FileDialog fd = new FileDialog(new Frame(),
				"Save Spectral Distribution", FileDialog.SAVE);
		if (saveFileDirectory != null)
			fd.setDirectory(saveFileDirectory);
		if (saveFileName != null)
			fd.setFile(saveFileName);
		fd.show();
		saveFileName = fd.getFile();
		if (saveFileName != null) {
			// System.out.println("Saving spectral distribution to file " +
			// saveFileName);
			saveFileDirectory = fd.getDirectory();
			save(saveFileDirectory, saveFileName);
		}
		fd.dispose();
	}

	/**
	 * Save this spectral distribution to a file.
	 * 
	 * @param fd
	 *            the directory path where the spectral data file is contained.
	 * @param fn
	 *            the name of the data file.
	 */
	public void save(String fd, String fn) {
		File fs = new File(fd, fn);
		try {
			PrintWriter br = new PrintWriter(new FileWriter(fs));
			float[] d = getData();
			int j = 0;
			for (int i = getFirst(); i <= getLast(); i += getStep()) {
				br.println(String.valueOf(i) + " " + String.valueOf(d[j]));
				// System.out.println(String.valueOf(i) + " " +
				// String.valueOf(d[j]));
				j++;
			}
			br.close();
		} catch (IOException ioe) {
			new FileError("Error while writing file " + fs.getAbsolutePath());
		}
	}

	/** Print this spectral distribution. */
	public void print() {
		int j = 0;
		for (int i = first; i <= last; i += step) {
			System.out.println(i + " " + data[j++]);
		}
	}
}
