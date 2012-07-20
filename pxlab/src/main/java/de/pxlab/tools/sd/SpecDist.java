package de.pxlab.tools.sd;

import java.io.*;
import java.util.*;
import de.pxlab.util.*;
import de.pxlab.stat.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.spectra.*;

/**
 * Takes the spectral data files given as arguments and then applies
 * successively all the spectral operations requested on the command line. It
 * then prints the resulting spectrals distribution and computes the requested
 * coordinates.
 * 
 * @version 0.2.0
 */
public class SpecDist implements CommandLineOptionHandler {
	private ArrayList nameList = new ArrayList(10);
	private ArrayList fileList = new ArrayList(10);
	private ArrayList workList = new ArrayList(10);
	private ArrayList argList = new ArrayList(10);
	private DataTable spectralDataTable;
	private StringDataTable spectralDataNameTable;
	private String extension = "dat";
	/**
	 * Default file type. Contains two columns where the first column is the
	 * wavelength and the second column is the spectral data value.
	 */
	private static final int FILE_TYPE_1 = 1;
	/**
	 * Contains multiple spectra with a single spectrum per input row and the
	 * first column contains the name of the spectrum.
	 */
	private static final int FILE_TYPE_2 = 2;
	/**
	 * Contains multiple spectra with a single spectrum per input row. No names
	 * are present.
	 */
	private static final int FILE_TYPE_3 = 3;
	private int fileType = FILE_TYPE_1;
	private boolean compute_Yxy = false;
	private boolean compute_LabStar = false;
	private boolean compute_LabLChStar = false;
	private boolean compute_LMS = false;
	// These data are used for files which do not contain wavelength information
	private int waveMin = 0, waveMax = 0, waveStep = 0;
	private int firstWavelength = 380;
	private int wavelengthStep = 10;
	private int lastWavelength = 730;
	private int observer = 2;
	private String outputFileName = null;
	private String whitePointName = null;
	private double whitePoint_Y = 100.0;
	private double whitePoint_x = 0.0;
	private double whitePoint_y = 0.0;
	private static final String NORMALIZE = "nor";
	private static final String SETLUM = "lum";
	private static final String SCALE = "scal";
	private static final String MULTIPLY = "mul";
	private static final String ADD = "add";

	public SpecDist(String[] args) {
		CommandLineParser cp = new CommandLineParser(args, options, this);
		if (cp.hasMoreArgs()) {
			while (cp.hasMoreArgs()) {
				String path = null;
				try {
					path = cp.getArg();
					fileList.addAll(new FileList(path, extension));
				} catch (FileNotFoundException e) {
					System.out
							.println("Directory/File " + path + " not found!");
					System.exit(3);
				}
			}
		}
		// System.out.println("Found " + fileList.size() +
		// " spectral data files.");
		if (nameList.isEmpty() && fileList.isEmpty()) {
			System.out.println("No working spectra specified!");
			showUsage();
			System.exit(3);
		}
		if (!nameList.isEmpty()) {
			for (Iterator it = nameList.iterator(); it.hasNext();) {
				String fn = (String) it.next();
				try {
					SpectralDistribution sd = SpectralDistributionFactory
							.instance(fn);
					transform(fn, sd);
				} catch (SpectrumNotFoundException snf) {
				}
			}
		}
		if (!fileList.isEmpty()) {
			if (fileType == FILE_TYPE_1) {
				for (Iterator it = fileList.iterator(); it.hasNext();) {
					String fn = (String) it.next();
					SpectralDistribution sd = SpectralDistributionFactory
							.load(fn);
					transform(fn, sd);
				}
			} else if ((fileType == FILE_TYPE_2) || (fileType == FILE_TYPE_3)) {
				createDataTable(fileList);
				int nc = spectralDataTable.getColumns();
				lastWavelength = firstWavelength + (nc - 1) * wavelengthStep;
				int n = spectralDataTable.getRows();
				for (int i = 0; i < n; i++) {
					double[] d = spectralDataTable.doubleRow(i);
					SpectralDistribution sd = new SpectralDistribution(
							firstWavelength, lastWavelength, wavelengthStep, d);
					transform(spectralDataNameTable.stringEntry(i, 0), sd);
				}
			}
		}
		System.exit(0);
	}

	private void createDataTable(ArrayList f) {
		boolean hasNames = fileType == FILE_TYPE_2;
		int nc = 0;
		spectralDataTable = new DataTable(100);
		if (hasNames)
			spectralDataNameTable = new StringDataTable(100);
		StringDataTable collect = new StringDataTable(100);
		for (Iterator it = f.iterator(); it.hasNext();) {
			String fn = (String) it.next();
			StringDataTable sdt = new StringDataTable(fn);
			int m = sdt.getColumns();
			if (nc > 0) {
				if (m != nc) {
					System.out
							.println("Data files with an unequal number of columns found.");
					System.out
							.println("Number of columns must be the same for all input files.");
					System.exit(3);
				}
			} else {
				nc = m;
			}
			collect.addAll(sdt);
		}
		if (hasNames) {
			int[] slct = new int[nc - 1];
			for (int i = 0; i < slct.length; i++)
				slct[i] = i + 1;
			spectralDataTable.addAll(collect.subTable(slct).doubleTable());
			slct = new int[1];
			slct[0] = 0;
			spectralDataNameTable.addAll(collect.subTable(slct));
		} else {
			spectralDataTable.addAll(collect.doubleTable());
		}
	}

	/** 
    */
	private void transform(String fn, SpectralDistribution sd) {
		if (waveMin != 0 && waveMax != 0 && waveStep != 0) {
		}
		if (!workList.isEmpty()) {
			int k = 0;
			for (Iterator it = workList.iterator(); it.hasNext();) {
				String cmd = (String) it.next();
				if (cmd.equals(NORMALIZE)) {
					sd.normalize();
				} else if (cmd.equals(SCALE)) {
					sd.scale(StringExt.doubleValue((String) argList.get(k++),
							1.0));
				} else if (cmd.equals(SETLUM)) {
					sd.setLuminance(StringExt.doubleValue(
							(String) argList.get(k++), 100.0));
				} else if (cmd.equals(MULTIPLY)) {
					SpectralDistribution fd = getSpectralDistribution((String) argList
							.get(k++));
					if (fd != null) {
						sd.filter(fd);
					}
				} else if (cmd.equals(ADD)) {
					SpectralDistribution fd = getSpectralDistribution((String) argList
							.get(k++));
					if (fd != null) {
						sd.add(fd);
					}
				}
			}
		}
		if (outputFileName != null) {
			sd.save(".", outputFileName);
		}
		if (compute_Yxy || compute_LabStar || compute_LabLChStar || compute_LMS) {
			PxlColor c = sd.toXYZ();
			System.out.print(fn);
			if (compute_Yxy) {
				System.out.print("  " + c);
			}
			if (compute_LMS) {
				System.out.print("  "
						+ PxlColor.colorToString(c.transform(PxlColor.CS_LMS)));
			}
			if (compute_LabStar || compute_LabLChStar) {
				setWhitePoint();
				if (compute_LabStar) {
					System.out.print("  "
							+ PxlColor.colorToString(c
									.transform(PxlColor.CS_LabStar)));
				}
				if (compute_LabLChStar) {
					System.out.print("  "
							+ PxlColor.colorToString(c
									.transform(PxlColor.CS_LabLChStar)));
				}
			}
			System.out.println("");
		} else if (outputFileName == null) {
			// No coordinate transform so create spectral output
			sd.print();
		}
	}

	private void setWhitePoint() {
		PxlColor wp = PxlColor.D65;
		if (whitePoint_x != 0.0 && whitePoint_y != 0.0) {
			wp = new YxyColor(whitePoint_Y, whitePoint_x, whitePoint_y);
		}
		if (whitePointName != null) {
			SpectralDistribution sd = getSpectralDistribution(whitePointName);
			if (sd != null) {
				wp = sd.toXYZ();
				wp.setY(whitePoint_Y);
			}
		}
		PxlColor.getDeviceTransform().setWhitePoint(wp.getComponents());
	}

	private SpectralDistribution getSpectralDistribution(String n) {
		SpectralDistribution fd = null;
		try {
			fd = SpectralDistributionFactory.instance(n);
		} catch (SpectrumNotFoundException snf) {
			fd = SpectralDistributionFactory.load(n);
		}
		return fd;
	}
	private String options = "xahle:1:2:3:w:v:s:f:d:NL:S:M:A:p:?";

	/** This method is called for every command line option found. */
	public void commandLineOption(char c, String arg) {
		switch (c) {
		case 'x':
			compute_Yxy = true;
			break;
		case 'a':
			compute_LabStar = true;
			break;
		case 'h':
			compute_LabLChStar = true;
			break;
		case 'l':
			compute_LMS = true;
			break;
		case 'e':
			whitePointName = arg;
			break;
		case '1':
			whitePoint_Y = StringExt.doubleValue(arg, 100.0);
			break;
		case '2':
			whitePoint_x = StringExt.doubleValue(arg, 0.313);
			break;
		case '3':
			whitePoint_y = StringExt.doubleValue(arg, 0.329);
			break;
		case 'w':
			waveMin = StringExt.intValue(arg, 380);
			break;
		case 'v':
			waveMax = StringExt.intValue(arg, 720);
			break;
		case 's':
			waveStep = StringExt.intValue(arg, 5);
			break;
		case 'f':
			fileType = StringExt.intValue(arg, FILE_TYPE_1);
			break;
		case 'd':
			nameList.add(arg);
			break;
		case 'N':
			workList.add(NORMALIZE);
			break;
		case 'S':
			workList.add(SCALE);
			argList.add(arg);
			break;
		case 'L':
			workList.add(SETLUM);
			argList.add(arg);
			break;
		case 'M':
			workList.add(MULTIPLY);
			argList.add(arg);
			break;
		case 'A':
			workList.add(ADD);
			argList.add(arg);
			break;
		case 'p':
			outputFileName = arg;
			break;
		case '?':
			showUsage();
			System.exit(0);
			break;
		}
	}

	public void showUsage() {
		System.out.println("Usage: java " + this.getClass().getName()
				+ " [options] data-file");
		System.out.println("Options are:");
		System.out
				.println("  -x     compute CIE 1931 chromaticities for the 2 deg observer [Y,x,y]");
		System.out
				.println("  -a     compute CIE 1971 L*a*b* coordinates [L*,a*,b*]");
		System.out
				.println("  -h     compute CIE 1971 L*a*b* coordinates [L*,C*,h*]");
		System.out
				.println("  -l     compute Smith & Pokorny fundamental sensitivity values [L,M,S]");
		System.out.println("  -e n   set CIE white point name");
		System.out.println("  -1 Y   set CIE white point Y-coordinate to Y");
		System.out.println("  -2 x   set CIE white point x-coordinate to x");
		System.out.println("  -3 y   set CIE white point y-coordinate to y");
		System.out.println("  -w m   set wavelength minimum value to m nm");
		System.out.println("  -v m   set wavelength maximum value to m nm");
		System.out.println("  -s m   set wavelength step size value to m nm");
		System.out.println("  -f t   set file type");
		System.out.println("  -d n   use named spectrum n as input data");
		System.out
				.println("  -N     normalize spectra such that the maximum is at 1.0");
		System.out.println("  -S x   scalar multiplication of spectrum by x");
		System.out.println("  -L x   set luminance to x cd/qm");
		System.out
				.println("  -M n   spectral multiplication by data in file n");
		System.out.println("  -A n   add spectral data in file n");
		System.out
				.println("  -p n   write result of spectral operations to file n");
		System.out.println("  -?     show this help text");
		System.out
				.println("Options -e, -M and -A accept data file names and also accept named spectra");
		System.out
				.println("like D65, \"5G 6/10\", or E1. Option -d also uses named spectra. See");
		System.out
				.println("class de.pxlab.pxl.spectra.SpectralDistribution for available named spectra.");
	}

	public void commandLineError(int e, String s) {
		System.out.println("Command line error: " + s);
		showUsage();
		System.exit(3);
	}

	public static void main(String[] args) {
		new SpecDist(args);
	}
}
