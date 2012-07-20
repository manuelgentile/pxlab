package de.pxlab.pxl.data;

import de.pxlab.pxl.*;
import de.pxlab.stat.*;
import de.pxlab.util.*;

// import de.pxlab.pxl.tools.est.*;
/**
 * Estimate parameters of discrimination ellipses in the CIE xy-chromaticity
 * diagram. The input data table must contain the following columns:
 * 
 * <ol>
 * 
 * <li>ID code. A code which identifies the ellipse.
 * 
 * <li>The distractor color which is identical to the center position of the
 * ellipse.
 * 
 * <li>The mixture color B.
 * 
 * <li>The mixture color A.
 * 
 * <li>A weight value k which is such that (k*A + (1-k)*B) is a color on the
 * ellipse.
 * 
 * </ol>
 * 
 * <p>
 * The estimation procedure estimates a single ellipse from all data points with
 * the same ID code.
 * 
 * <p>
 * The ellipse center is the arithmetic mean of all distractor colors in column
 * 2.
 * 
 * <p>
 * The estimation procedure computes the mixture color for every data point and
 * then estimates the ellipse such that the sum of the squares of the radial
 * distances between the empirical data and the ellipse becomes minimal.
 * 
 * <p>
 * Only the chromaticity coordinates x, y are used for estimation. Her is an
 * example design file for estimation:
 * 
 * <pre>
 * Experiment() 
 * {
 *   Context()
 *   {
 *     ProcedureData(SubjectCode,
 * 	  Trial.RandomTilesColorVisionTest.DistractorColor, 
 * 	  Trial.RandomTilesColorVisionTest.LowerLimitColor, 
 * 	  Trial.RandomTilesColorVisionTest.HigherLimitColor, 
 * 	  AdaptiveResults) {
 *       EllipseEstimation() {
 *         Include = AdaptiveState == -2 || AdaptiveState == -1;
 *         ResultFormat = "%SubjectCode% %ProcedureData.EllipseEstimation.AxisA% %ProcedureData.EllipseEstimation.AxisB% %ProcedureData.EllipseEstimation.Rotation%\n";
 *       }
 *     }
 *   }
 * }
 * </pre>
 * 
 * <p>
 * This file uses the results of an adaptive procedure with the display object
 * RandomTilesColorVisionTest to collect the data.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/12/08
 */
public class EllipseEstimation extends DataDisplay {
	/** Holds the result for the gamma value. */
	public ExPar CenterX = new ExPar(RTDATA, new ExParValue(0.0),
			"Estimated horizontal position");
	/** Holds the result for the gain value. */
	public ExPar CenterY = new ExPar(RTDATA, new ExParValue(0.0),
			"Estimated vertical position");
	/** Holds the result for the gain value. */
	public ExPar AxisA = new ExPar(RTDATA, new ExParValue(0.0),
			"Estimated a-axis");
	/** Holds the result for the gain value. */
	public ExPar AxisB = new ExPar(RTDATA, new ExParValue(0.0),
			"Estimated b-axis");
	/** Holds the result for the gain value. */
	public ExPar Rotation = new ExPar(RTDATA, new ExParValue(0.0),
			"Estimated angle of rotation");
	/** Final goodness of fit statistic. */
	public ExPar GoodnessOfFit = new ExPar(RTDATA, new ExParValue(0.0),
			"Final goodness of fit statistic");
	/**
	 * A format string for showing the results of data processing if parameter
	 * HTMLFormat is not set. By default this string is empty and the data
	 * processing objects generate HTML-formatted output. If this string is
	 * non-empty then it is used for formatting the output.
	 */
	public ExPar ResultFormat = new ExPar(STRING, new ExParValue(""),
			"Result format");

	public EllipseEstimation() {
		setTitleAndTopic("Ellipse parameter estimation", Topics.DATA);
	}
	private static final int ID_IDX = 0;
	private static final int DISTRACTOR_IDX = 1;
	private static final int LOW_COL_IDX = 2;
	private static final int HIGH_COL_IDX = 3;
	private static final int WEIGHT_IDX = 4;

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable table) {
		/*
		 * if (p == null) { System.out.println(
		 * "EllipseEstimation.processDataTable(): missing parameter names."); }
		 */
		Object[] distractor = table.column(p[DISTRACTOR_IDX]);
		Object[] lowCol = table.column(p[LOW_COL_IDX]);
		Object[] highCol = table.column(p[HIGH_COL_IDX]);
		double[] weight = table.doubleColumn(table.columnIndex(p[WEIGHT_IDX]));
		int[] id = table.intColumn(table.columnIndex(p[ID_IDX]));
		int n = weight.length;
		double mx = 0.0;
		double my = 0.0;
		double[] x = new double[n];
		double[] y = new double[n];
		int prevID = 1324354657;
		int k = 0;
		for (int j = 0; j < n; j++) {
			if (id[j] != prevID) {
				if (k > 0) {
					estimate(prevID, k, mx, my, x, y);
				}
				k = 0;
			}
			prevID = id[j];
			PxlColor d = ((ExParValue) distractor[j]).getPxlColor();
			mx = d.getx();
			my = d.gety();
			PxlColor lo = ((ExParValue) lowCol[j]).getPxlColor();
			PxlColor hi = ((ExParValue) highCol[j]).getPxlColor();
			PxlColor t = hi.mix(weight[j], lo);
			x[k] = t.getx();
			y[k] = t.gety();
			Debug.show(Debug.DATA, "  " + k + " " + prevID + " " + mx + " "
					+ my + " " + x[k] + " " + y[k]);
			k++;
		}
		if (k > 0) {
			estimate(prevID, k, mx, my, x, y);
		}
		return 0;
	}

	private void estimate(int id, int n, double mx, double my, double[] x,
			double[] y) {
		double[] px = new double[n];
		double[] py = new double[n];
		System.arraycopy(x, 0, px, 0, n);
		System.arraycopy(y, 0, py, 0, n);
		EllipseEstimator est = new EllipseEstimator();
		double[] p = est.estimate(mx, my, px, py);
		CenterX.set(p[0]);
		CenterY.set(p[1]);
		AxisA.set(p[2]);
		AxisB.set(p[3]);
		Rotation.set(180.0 * p[4] / Math.PI);
		GoodnessOfFit.set(p[5]);
		String f = ResultFormat.getString();
		if (StringExt.nonEmpty(f)) {
		} else {
			StringBuffer b = new StringBuffer();
			String nl = System.getProperty("line.separator");
			b.append(nl + "<" + StatEngine.header3 + ">Parameters for Ellipse "
					+ id + "</" + StatEngine.header3 + ">" + nl);
			b.append("<table cellpadding=\"4\">" + nl);
			b.append("<tr><td align=\"right\">x<td>=<td>" + p[0] + "</tr>" + nl);
			b.append("<tr><td align=\"right\">y<td>=<td>" + p[1] + "</tr>" + nl);
			b.append("<tr><td align=\"right\">a<td>=<td>" + p[2] + "</tr>" + nl);
			b.append("<tr><td align=\"right\">b<td>=<td>" + p[3] + "</tr>" + nl);
			b.append("<tr><td align=\"right\">angle<td>=<td>"
					+ Rotation.getDouble() + "</tr>" + nl);
			b.append("<tr><td align=\"right\">fit<td>=<td>" + p[5] + "</tr>"
					+ nl);
			b.append("</table>" + nl);
			f = b.toString();
		}
		showResults(f);
	}
}
