package de.pxlab.util;

import java.text.NumberFormat;
import java.util.Locale;

import de.pxlab.util.*;
import de.pxlab.stat.Stats;

/**
 * Estimate parameters of an ellipse which best describes a set of given points.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see de.pxlab.util.Praxis
 * @see de.pxlab.util.PraxisFunction
 */
/*
 * 
 * 2005/11/18 fixed serious bug in function dist() which computes the distance
 * values.
 */
public class EllipseEstimator implements PraxisFunction {
	private double[] x_data;
	private double[] y_data;
	private double[] res;
	private double[] statistics = new double[5];

	/**
	 * Estimate the parameters of an ellipse which best describes the given
	 * points.
	 * 
	 * @param x
	 *            an array of x-coordinates for the sample points.
	 * @param y
	 *            an array of y-coordinates for the sample points. Must be the
	 *            same size as x.
	 * @return an array containing the estimated parameters. (p[0], p[1])
	 *         contain the mean values of x and y. This is used as an estimate
	 *         of the ellipse's center position. p[2] is the first half axis,
	 *         p[3] is the second half axis, and p[4] is the angle of rotation
	 *         of the main axis. p[5] contains the final result of the deviation
	 *         criterion.
	 */
	public double[] estimate(double[] x, double[] y) {
		double mx = Stats.mean(x);
		double my = Stats.mean(y);
		return estimate(mx, my, x, y);
	}

	/**
	 * Estimate the parameters of an ellipse which best describes the given
	 * points.
	 * 
	 * @param xc
	 *            the horizontal center position of the ellipse.
	 * @param yc
	 *            the vertical center position of the ellipse.
	 * @param x
	 *            an array of x-coordinates for the sample points.
	 * @param y
	 *            an array of y-coordinates for the sample points. Must be the
	 *            same size as x.
	 * @return an array containing the estimated parameters. (p[0], p[1])
	 *         contain the values of xc and yc. p[2] is the first half axis,
	 *         p[3] is the second half axis, and p[4] is the angle of rotation
	 *         of the main axis. p[5] contains the final result of the deviation
	 *         criterion.
	 */
	public double[] estimate(double xc, double yc, double[] x, double[] y) {
		int n = x.length;
		x_data = new double[n];
		y_data = new double[n];
		double x_center = xc;
		double y_center = yc;
		// compute deviation values
		for (int i = 0; i < n; i++) {
			x_data[i] = (x[i] - x_center);
			y_data[i] = (y[i] - y_center);
			// System.out.println(x_data[i] + " " + y_data[i]);
		}
		// Thats where our initial parameter estimates go
		double[] p = new double[3];
		// prepare sensible initial estimates
		double sx = Stats.standardDev(x_data);
		double sy = Stats.standardDev(y_data);
		double r = Stats.correlation(x_data, y_data);
		double d = r * sy / sx;
		double da = Math.atan(d);
		double r2 = (1.0 - r * r);
		double g11 = 1.0 / (sx * sx * r2);
		double g12 = r / (sx * sy * r2);
		double g22 = 1.0 / (sy * sy * r2);
		double wt = Math.atan2(g11 - g12, 2.0 * g12) / 2.0;
		double wtd = Math.toDegrees(wt);
		double wa = Math.sqrt(1.0 / (g22 + g12 / Math.tan(wt)));
		double wb = Math.sqrt(1.0 / (g11 - g12 / Math.tan(wt)));
		/*
		 * System.out.println("EllipseEstimator.estimate() WS-Estimates: g11=" +
		 * g11 + " g12=" + g12 + " g22=" + g22);
		 * System.out.println("EllipseEstimator.estimate() WS-Estimates: " + wa
		 * + " " + wb + " " + wt + " " + wtd);
		 * 
		 * System.out.println(" mean = [" + x_center + ", " + y_center + "]");
		 * System.out.println(" stddev = [" + sx + ", " + sy + "]");
		 * System.out.println(" corr = " + r); System.out.println(" beta = " +
		 * d); System.out.println(" angle = " + da + " [= " + Math.toDegrees(da)
		 * + " deg]");
		 */
		p[0] = sx;
		p[1] = sy;
		p[2] = da;
		Praxis praxis = new Praxis();
		praxis.setPrintControl(0);
		praxis.setScaling(10.0);
		double minSquare = praxis.minimize(this, p);
		/*
		 * System.out.println("Estimated values: "); System.out.println(" x = "
		 * + x_center); System.out.println(" y = " + y_center);
		 * System.out.println(" a = " + p[0]); System.out.println(" b = " +
		 * p[1]); System.out.println(" d = " + p[2] + " [= " +
		 * (p[2]/Math.PI*180.0) + " degrees]");
		 * System.out.println(" minSquare = " + minSquare);
		 * System.out.println("Estimated values: "); System.out.println(" x = "
		 * + x_center); System.out.println(" y = " + y_center);
		 * System.out.println(" a = " + p[0]); System.out.println(" b = " +
		 * p[1]); System.out.println(" d = " + p[2] + " [= " +
		 * (p[2]/Math.PI*180.0) + " degrees]");
		 * System.out.println(" minSquare = " + minSquare);
		 */
		res = new double[6];
		res[0] = x_center;
		res[1] = y_center;
		res[2] = p[0];
		res[3] = p[1];
		res[4] = p[2];
		res[5] = minSquare;
		getTestStats();
		double[] mc = new double[3];
		mc[0] = 2.3;
		mc[1] = 0.9;
		mc[2] = Math.toRadians(58.0);
		double mcd = dist(x_data, y_data, mc);
		return res;
	}

	/**
	 * Implementation of the interface PraxisFunction. This method is called by
	 * Praxis to get the minimization criterion.
	 */
	public double valueOf(double[] p) {
		return dist(x_data, y_data, p);
	}

	/**
	 * Compute the distance of a single point from the ellipse with the
	 * parameters given in the argument.
	 * 
	 * @param xx
	 *            array of x-coordinates of the sample points.
	 * @param yy
	 *            array of y-coordinates of the sample points.
	 * @param p
	 *            parameters of the ellipse.
	 */
	private double dist(double[] xx, double[] yy, double[] p) {
		double p11 = Math.cos(p[2]);
		double p12 = Math.sin(p[2]);
		double p21 = -Math.sin(p[2]);
		double p22 = Math.cos(p[2]);
		double xxx, yyy, th, ex, ey, dsx, dsy, sqdist;
		double sum = 0.0;
		// Get the number of data points
		int n = xx.length;
		for (int i = 0; i < n; i++) {
			// Now rotate the point from the original ellipse to its
			// normal form. This is a rotation equivalent to the
			// rotation of the ellipse but into the opposite direction.
			xxx = p11 * xx[i] + p12 * yy[i];
			yyy = p21 * xx[i] + p22 * yy[i];
			// Now compute the point's running parameter value which
			// corresponds to its angle with the x-axis.
			th = Math.atan2(yyy / p[1], xxx / p[0]);
			// Then we can compute that point of the ellipse which has
			// the same running parameter angle as the data point.
			ex = p[0] * Math.cos(th);
			ey = p[1] * Math.sin(th);
			// System.out.println("EllipseEstimator.dist()" + xx[i] + " " +
			// yy[i] + " " + xxx + " " + yyy + " " + Math.toDegrees(th) + " " +
			// ex + " " + ey);
			// Compute the point's squared distance to our rotated data point
			dsx = xxx - ex;
			dsy = yyy - ey;
			sqdist = (dsx * dsx + dsy * dsy);
			// sum += Math.sqrt(sqdist);
			sum += sqdist;
		}
		// System.out.println("EllipseEstimator.dist() p[] = [" + p[0] + ", " +
		// p[1] + ", " + p[2] + " (" + Math.toDegrees(p[2]) + "deg)]  sum = " +
		// sum);
		return sum;
	}

	public double[] getTestStats() {
		double[] p = new double[3];
		p[0] = res[2];
		p[1] = res[3];
		p[2] = res[4];
		int df1 = 2 * x_data.length - 2;
		int df2 = 2 * x_data.length - 2 - 2 - 1;
		double SS_radius = sumOfSquaredDist(x_data, y_data, p);
		double F_value = (SS_radius / (df1)) / (res[5] / (df2));
		statistics[0] = SS_radius;
		statistics[1] = df1;
		statistics[2] = res[5];
		statistics[3] = df2;
		statistics[4] = F_value;
		return statistics;
	}
	private static NumberFormat d = NumberFormat.getInstance(Locale.US);
	static {
		d.setMaximumFractionDigits(8);
		d.setGroupingUsed(false);
	}
	private static NumberFormat i = NumberFormat.getIntegerInstance(Locale.US);

	public void printResults(int code) {
		System.out.println(code + " " + d.format(res[0]) + " "
				+ d.format(res[1]) + " " + d.format(res[2]) + " "
				+ d.format(res[3]) + " " + d.format(res[4]) + " "
				+ d.format(res[5]) + " " + d.format(Math.PI * res[2] * res[3])
				+ " " + d.format(statistics[0]) + " " + i.format(statistics[1])
				+ " " + d.format(statistics[2]) + " " + i.format(statistics[3])
				+ " " + d.format(statistics[4]));
	}

	/*
	 * public void printResults() { System.out.println((res[0]) + " " + (res[1])
	 * + " " + (res[2]) + " " + (res[3]) + " " + (res[4]) + " " + (res[5]) + " "
	 * + (statistics[0]) + " " + (statistics[1]) + " " + (statistics[2]) + " " +
	 * (statistics[3]) + " " + (statistics[4])); }
	 */
	/**
	 * Compute the distance of a single point from the ellipse with those
	 * parameters given int the argument.
	 */
	private double sumOfSquaredDist(double[] xx, double[] yy, double[] p) {
		double dsx, dsy, sqdist;
		double sum = 0.0;
		// Get the number of data points
		int n = xx.length;
		for (int i = 0; i < n; i++) {
			// Compute every point's squared distance to the center
			dsx = xx[i];
			dsy = yy[i];
			sqdist = (dsx * dsx + dsy * dsy);
			sum += sqdist;
		}
		return sum;
	}
}
