package de.pxlab.stat;

import java.io.*;
import java.text.*;
import java.util.*;

import de.pxlab.pxl.Debug;

/**
 * A superclass for statistical analysis engine classes. This class unifies
 * result output, plot data generation and print options.
 * 
 * @version 0.2.1
 */
/*
 * 
 * 2006/11/02 fixed bug with dform2
 */
public class StatEngine {
	protected static final double FZERO = 0.0000000001;
	protected static final double MAXF = 99999.0;
	protected static String nl = System.getProperty("line.separator");
	/** Main header of a data analysis output file. */
	public static String header1 = "H2";
	/** Header introducing the outout of a single data analysis object. */
	public static String header2 = "H3";
	/** Header introducing a new section or table within a data analysis object. */
	public static String header3 = "H4";
	protected HashMap plotData = null;
	/** True if anything formatted is to be printed. */
	protected boolean aPr = false;
	/** True if descriptive statistics are to be printed. */
	protected boolean dPr = false;
	/** True if estimation or test results are to be printed. */
	protected boolean rPr = false;
	/** True if extremely detailed descriptive statistics are to be printed. */
	protected boolean xdPr = false;
	/**
	 * True if all descriptive terms should be added which are necessary for the
	 * computations of test statistics.
	 */
	protected boolean xxdPr = false;
	/** True if very detailed estimation or test results are to be printed. */
	protected boolean xrPr = false;
	protected int printOption;
	protected String title;

	protected StatEngine(String title, int printOption) {
		this.printOption = printOption;
		if ((printOption & de.pxlab.pxl.StatPrintBitCodes.PRINT_DESCRIPTIVE) != 0) {
			dPr = true;
		}
		if ((printOption & de.pxlab.pxl.StatPrintBitCodes.PRINT_RESULTS) != 0) {
			rPr = true;
		}
		if ((printOption & de.pxlab.pxl.StatPrintBitCodes.PRINT_DETAILS) != 0) {
			xrPr = rPr;
			xdPr = dPr;
		}
		if ((printOption & de.pxlab.pxl.StatPrintBitCodes.PRINT_TERMS) != 0) {
			xxdPr = xdPr = dPr;
		}
		aPr = rPr || dPr;
		this.title = title;
		rb = new StringBuffer(2000);
		if (aPr && (title != null))
			append("\n<" + header2 + ">" + title + "</" + header2 + ">\n");
	}

	/**
	 * Add a line of data to a plot file.
	 * 
	 * @param fn
	 *            the file name of the plot data file.
	 * @param data
	 *            the data content of the line to be added.
	 */
	protected void addPlotDataLine(String fn, String data) {
		if (plotData == null) {
			plotData = new HashMap(30);
		}
		if (!plotData.containsKey(fn)) {
			plotData.put(fn, new ArrayList(20));
		}
		ArrayList a = (ArrayList) plotData.get(fn);
		a.add(data);
	}
	/** The output string buffer */
	protected StringBuffer rb;

	/** Append a string to the output data buffer. */
	protected void append(String s) {
		rb.append(s);
	}

	protected void beginTable(String label) {
		rb.append(nl + "<" + header3 + ">" + label + "</" + header3 + ">");
		rb.append(nl + "<table cellpadding=\"4\">");
	}

	protected void endTable() {
		rb.append(nl + "</table>");
	}

	/** Return the output data buffer. */
	public String getResult() {
		return rb.toString();
	}

	public void printResult() {
		Debug.show(Debug.FILES, "StatEngine.printResult(): Print to stdout");
		System.out.print(rb.toString());
	}

	public void printResult(String fn) {
		if (fn == null) {
			Debug.show(Debug.FILES,
					"StatEngine.printResult(): No file name - print to stdout");
			System.out.print(rb.toString());
		} else {
			try {
				Debug.show(Debug.FILES,
						"StatEngine.printResult(): Open output file " + fn);
				PrintWriter pw = new PrintWriter(fn);
				pw.print(rb.toString());
				pw.close();
				Debug.show(Debug.FILES,
						"StatEngine.printResult(): Closed output file " + fn);
			} catch (FileNotFoundException fnfx) {
				Debug.show(Debug.FILES,
						"StatEngine.printResult(): File error - print to stdout");
				System.out.print(rb.toString());
			}
		}
	}

	/**
	 * Print plotting data generated during engine execution.
	 * 
	 * @param dir
	 *            the directory where the data file is to be stored.
	 * @param fnp
	 *            a prefix for the data file name. The complete data file name
	 *            is generated from the prefix and the plot data key.
	 * @param fnx
	 *            file name extension.
	 */
	public void printPlotData(String dir, String fnp, String fnx) {
		if (plotData == null)
			return;
		for (Iterator it = plotData.keySet().iterator(); it.hasNext();) {
			String f = (String) it.next();
			ArrayList a = (ArrayList) plotData.get(f);
			String fn = fnp + "_" + f + fnx;
			try {
				File fl = new File(dir, fn);
				Debug.show(
						Debug.FILES,
						"StatEngine.printPlotData(): Open output file "
								+ fl.getPath());
				PrintWriter pw = new PrintWriter(fl);
				for (Iterator jt = a.iterator(); jt.hasNext();) {
					pw.println((String) jt.next());
				}
				pw.close();
				Debug.show(
						Debug.FILES,
						"StatEngine.printPlotData(): Closed output file "
								+ fl.getPath());
			} catch (FileNotFoundException fnfx) {
				Debug.show(Debug.FILES,
						"StatEngine.printPlotData(): File error");
			}
		}
	}

	/**
	 * Invert the given matrix.
	 * 
	 * @param matrix
	 *            the matrix to be inverted.
	 * @return the determinant of the input matrix.
	 */
	protected static double invert(double[][] matrix) {
		int size = matrix.length;
		double pivot;
		double temp;
		double det = 1.0;
		for (int j = 0; j < size; j++) {
			pivot = matrix[j][j];
			det *= pivot;
			matrix[j][j] = 1.0;
			for (int k = 0; k < size; k++)
				if (!almostZero(pivot))
					matrix[j][k] /= pivot;
				else
					return (0.0);
			for (int k = 0; k < size; k++)
				if (k != j) {
					temp = matrix[k][j];
					matrix[k][j] = 0.0;
					for (int l = 0; l < size; l++)
						matrix[k][l] -= matrix[j][l] * temp;
				}
		}
		return (det);
	}

	protected static boolean almostZero(double x) {
		return Math.abs(x) < FZERO;
	}
	static DecimalFormat dform = (DecimalFormat) (NumberFormat
			.getNumberInstance(Locale.US));
	static {
		dform.applyPattern("##########.0000");
	}
	static DecimalFormat dform2 = (DecimalFormat) (NumberFormat
			.getNumberInstance(Locale.US));
	static {
		dform2.applyPattern("##########.00");
	}

	protected static String dfm(DecimalFormat p, double x) {
		int k = 10;
		String s = p.format(x);
		int i = s.indexOf('.');
		int m = k - i;
		if (m > 0) {
			char[] b = new char[m];
			for (int j = 0; j < m; j++)
				b[j] = ' ';
			s = new String(b) + s;
		}
		return s;
	}

	protected static String dfm(double x) {
		return dfm(dform, x);
	}

	protected static String dfm2(double x) {
		return dfm(dform2, x);
	}

	protected static String localName(String n) {
		int i = n.lastIndexOf('.');
		return (i > 0) ? n.substring(i + 1) : n;
	}

	protected void console(String p, String[] a) {
		int m = a.length;
		for (int j = 0; j < m; j++) {
			System.out.println("   " + p + "[" + j + "] = " + a[j]);
		}
	}

	protected void console(String p, double[] a) {
		int m = a.length;
		for (int j = 0; j < m; j++) {
			System.out.println("   " + p + "[" + j + "] = " + a[j]);
		}
	}

	protected void console(String p, int[] a) {
		System.out.print("   " + p + "[] = [ " + a[0]);
		int m = a.length;
		for (int j = 1; j < m; j++) {
			System.out.print(", " + a[j]);
		}
		System.out.println("]");
	}

	protected void console(String p, double[][] a) {
		int n = a.length;
		for (int i = 0; i < n; i++) {
			int m = a[i].length;
			for (int j = 0; j < m; j++) {
				System.out.println("   " + p + "[" + i + "][" + j + "] = "
						+ a[i][j]);
			}
		}
	}

	protected void console(String p, int[][] a) {
		int n = a.length;
		for (int i = 0; i < n; i++) {
			int m = a[i].length;
			for (int j = 0; j < m; j++) {
				System.out.println("   " + p + "[" + i + "][" + j + "] = "
						+ a[i][j]);
			}
		}
	}
}
