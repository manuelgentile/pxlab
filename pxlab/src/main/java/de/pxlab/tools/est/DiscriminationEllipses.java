package de.pxlab.tools.est;

import java.awt.*;
import java.io.*;
import java.util.*;

import de.pxlab.util.*;
import de.pxlab.awtx.*;

/**
 * An application for estimating the parameters of color discrimination
 * ellipses. This class reads data from an input file and estimates the
 * parameters of an ellipse. The input file may contain data points which refer
 * to a fixed center or may contain data points which do not refer to a fixed
 * center of the ellipse. If the data points refer to a fixed center then the
 * center coordinates have to be contained in each input line. If the ellipse's
 * center is not fixed then the center coordinates are estimated from the data
 * by calculating the arithmetic mean of all points for a single ellipse. In
 * this case the data file need only contain the data point coordinates. For
 * fixed center ellipses the data file has 5 columns and for free center
 * ellipses the data file has 3 columns:
 * 
 * <p>
 * 1: An ID code which identifies a data point as belonging to a single ellipse.
 * All points with the same ID code are considered to be points of the same
 * ellipse.
 * 
 * <p>
 * 2, 3: For a fixed center ellipse this are the coordinates of the ellipse's
 * center. For free center ellipses these are the coordinates of a point on the
 * ellipse.
 * 
 * <p>
 * 4, 5: For fixed center ellipses these are the coordinates of a data point on
 * the ellipse.
 * 
 * <p>
 * A single argument must be given on the command line when starting this
 * program: the name of the data file.
 * 
 * <p>
 * If the input coordinates are in the range of 0.0 <= x,y <= 1.0 then CIE 1931
 * xy chromaticity coordinates are shown in the results display. If the input
 * coordinates are outside of this range then CIELab coordinates are shown.
 */
public class DiscriminationEllipses extends CloseableFrame {
	private DiscriminationChart chart = null;
	private String currentFileName = null;

	public DiscriminationEllipses(String[] args) {
		super(" Discrimination Ellipses");
		String fn = null;
		String ct = "xy";
		if (args.length == 1) {
			fn = args[0];
		} else if (args.length == 2) {
			ct = args[0];
			fn = args[1];
		} else {
			System.out
					.println("Illegal number of arguments. Only a single file name allowed!");
			System.exit(3);
		}
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					new FileInputStream(fn)));
			currentFileName = fn;
			String line;
			int previousCode = -1;
			int code = 0;
			double x = 0.0, y = 0.0;
			double[] xp = new double[200];
			double[] yp = new double[200];
			double xx = 0.0, yy = 0.0, cx = 0.0, cy = 0.0;
			int n = 0;
			int k = 0;
			boolean fixedCenter = false;
			while ((line = rd.readLine()) != null) {
				n++;
				StringTokenizer st = new StringTokenizer(line);
				if (st.hasMoreTokens()) {
					code = Integer.parseInt(st.nextToken());
				} else {
					System.out.println("Input error in line " + n);
					System.exit(3);
				}
				if (st.hasMoreTokens()) {
					x = Double.valueOf(st.nextToken()).doubleValue();
				} else {
					System.out.println("Input error in line " + n);
					System.exit(3);
				}
				if (st.hasMoreTokens()) {
					y = Double.valueOf(st.nextToken()).doubleValue();
				} else {
					System.out.println("Input error in line " + n);
					System.exit(3);
				}
				if (st.hasMoreTokens()) {
					xx = Double.valueOf(st.nextToken()).doubleValue();
					if (st.hasMoreTokens()) {
						yy = Double.valueOf(st.nextToken()).doubleValue();
						fixedCenter = true;
					}
				}
				if ((code == previousCode) || (previousCode == -1)) {
					xp[k] = fixedCenter ? xx : x;
					yp[k] = fixedCenter ? yy : y;
					if (fixedCenter) {
						cx = x;
						cy = y;
					}
					k++;
					previousCode = code;
				} else {
					if (fixedCenter)
						runEstimation(previousCode, k, cx, cy, xp, yp);
					else
						runEstimation(previousCode, k, xp, yp);
					k = 0;
					xp[k] = fixedCenter ? xx : x;
					yp[k] = fixedCenter ? yy : y;
					if (fixedCenter) {
						cx = x;
						cy = y;
					}
					k++;
					previousCode = code;
				}
				// System.out.println("input: " + code + " " + x + " " + y);
			}
			if (fixedCenter)
				runEstimation(previousCode, k, cx, cy, xp, yp);
			else
				runEstimation(previousCode, k, xp, yp);
			rd.close();
		} catch (IOException iox) {
			System.out.println("Input read error.");
			System.exit(3);
		}
	}

	private void runEstimation(int code, int n, double[] x, double[] y) {
		double[] px = new double[n];
		double[] py = new double[n];
		// for (int i = 0; i < n; i++) System.out.println(x[i] + " " + y[i]);
		System.arraycopy(x, 0, px, 0, n);
		System.arraycopy(y, 0, py, 0, n);
		EllipseEstimator est = new EllipseEstimator();
		double[] p = est.estimate(px, py);
		est.printResults(code);
		showResults(px, py, p);
	}

	private void runEstimation(int code, int n, double cx, double cy,
			double[] x, double[] y) {
		double[] px = new double[n];
		double[] py = new double[n];
		// for (int i = 0; i < n; i++) System.out.println(x[i] + " " + y[i]);
		System.arraycopy(x, 0, px, 0, n);
		System.arraycopy(y, 0, py, 0, n);
		EllipseEstimator est = new EllipseEstimator();
		double[] p = est.estimate(cx, cy, px, py);
		est.printResults(code);
		showResults(px, py, p);
		/*
		 * p[0] = 5.04; p[1] = 7.83; p[2] = 0.4589; System.out.println(p[0] +
		 * " " + p[1] + " " + p[2] + " " + est.valueOf(p)); p[0] = 5.04; p[1] =
		 * 8.00; p[2] = 0.4589; System.out.println(p[0] + " " + p[1] + " " +
		 * p[2] + " " + est.valueOf(p)); p[0] = 5.04; p[1] = 9.83; p[2] =
		 * 0.4589; System.out.println(p[0] + " " + p[1] + " " + p[2] + " " +
		 * est.valueOf(p)); p[0] = 5.04; p[1] = 10.83; p[2] = 0.4589;
		 * System.out.println(p[0] + " " + p[1] + " " + p[2] + " " +
		 * est.valueOf(p));
		 */
	}

	private void showResults(double[] px, double[] py, double[] p) {
		if (chart == null) {
			double x_min = Double.MAX_VALUE, x_max = Double.MIN_VALUE;
			double y_min = Double.MAX_VALUE, y_max = Double.MIN_VALUE;
			for (int i = 0; i < px.length; i++) {
				if (px[i] < x_min)
					x_min = px[i];
				if (px[i] > x_max)
					x_max = px[i];
			}
			for (int i = 0; i < py.length; i++) {
				if (py[i] < y_min)
					y_min = py[i];
				if (py[i] > y_max)
					y_max = py[i];
			}
			double dx = x_max - x_min;
			double dy = y_max - y_min;
			setTitle(" Discrimination Ellipses " + currentFileName);
			setSize(800, 800);
			if (dx > 1.0 && dy > 1.0) {
				chart = new DiscriminationChart("ab", -30.0, 30.0, 10.0, -30.0,
						30.0, 10.0);
			} else {
				chart = new DiscriminationChart("xy", 0.27, 0.33, 0.01, 0.21,
						0.27, 0.01);
				// chart = new DiscriminationChart("xy", 0.1, 0.5, 0.01, 0.1,
				// 0.5, 0.01);
			}
			add(chart);
			setVisible(true);
		}
		chart.addPars(p);
		chart.addData(px, py);
		chart.repaint();
	}

	public static void main(String[] args) {
		new DiscriminationEllipses(args);
	}
}
