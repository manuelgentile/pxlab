package de.pxlab.pxl.data;

import java.util.ArrayList;

import de.pxlab.pxl.*;
import de.pxlab.util.StringExt;
import de.pxlab.stat.*;

/**
 * Shows descriptive statistics of a data array. Creates a data table from all
 * trial data of the node which contains this object and computes descriptive
 * statistics.
 * 
 * <p>
 * Only trials which satisfy the Include condition and do not satisfy the
 * Exclude condition are entered into the data table.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/12/08
 */
public class Statistics extends DataDisplay implements StatCodes {
	/** Defines the statistics which should be computed. */
	public ExPar Stats = new ExPar(GEOMETRY_EDITOR, StatCodes.class,
			new ExParValueConstant("de.pxlab.pxl.StatCodes.MEAN"),
			"Statistics to be computed");
	/**
	 * A format string for showing the results of data processing if parameter
	 * HTMLFormat is not set. By default this string is empty and the data
	 * processing objects generate HTML-formatted output. If this string is
	 * non-empty then it is used for formatting the output.
	 */
	public ExPar ResultFormat = new ExPar(STRING, new ExParValue(""),
			"Result format");
	public ExPar TotalN = new ExPar(RTDATA, new ExParValue(0),
			"Total number of entries in the input data node");
	public ExPar IncludedN = new ExPar(RTDATA, new ExParValue(0),
			"Number of entries included");
	public ExPar ExcludedN = new ExPar(RTDATA, new ExParValue(0),
			"Number of entries excluded");
	public ExPar N = new ExPar(RTDATA, new ExParValue(0),
			"Number of entries used for computation");
	public ExPar Sum = new ExPar(RTDATA, new ExParValue(0), "Sum of values");
	public ExPar SumOfSquares = new ExPar(RTDATA, new ExParValue(0),
			"Sum of squared values");
	public ExPar Mean = new ExPar(RTDATA, new ExParValue(0), "Arithmetic mean");
	public ExPar GeometricMean = new ExPar(RTDATA, new ExParValue(0),
			"Geometric mean");
	public ExPar HarmonicMean = new ExPar(RTDATA, new ExParValue(0),
			"Harmonic mean");
	public ExPar Minimum = new ExPar(RTDATA, new ExParValue(0), "Minimum value");
	public ExPar Maximum = new ExPar(RTDATA, new ExParValue(0), "Maximum value");
	public ExPar Median = new ExPar(RTDATA, new ExParValue(0), "Median");
	public ExPar Quantile25 = new ExPar(RTDATA, new ExParValue(0),
			"25 % Quantile");
	public ExPar Quantile75 = new ExPar(RTDATA, new ExParValue(0),
			"75 % Quantile");
	public ExPar Variance = new ExPar(RTDATA, new ExParValue(0), "Variance");
	public ExPar StandardDeviation = new ExPar(RTDATA, new ExParValue(0),
			"Standard deviation");
	public ExPar StandardError = new ExPar(RTDATA, new ExParValue(0),
			"Standard error of the mean");
	public ExPar CoefficientOfVariation = new ExPar(RTDATA, new ExParValue(0),
			"Coefficient of variation");
	public ExPar Skew = new ExPar(RTDATA, new ExParValue(0), "Skew");
	public ExPar Kurtosis = new ExPar(RTDATA, new ExParValue(0), "Kurtosis");
	public ExPar Range = new ExPar(RTDATA, new ExParValue(0), "Range of values");
	public ExPar AverageDeviation = new ExPar(RTDATA, new ExParValue(0),
			"Average Deviation");

	public Statistics() {
		setTitleAndTopic("Descriptive statistics", Topics.DATA);
		Header.set("Descriptive Statistics");
	}

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable grandTable) {
		ExParValueTable table = null;
		if (p != null && p.length != 0) {
			table = new ExParValueTable(grandTable.subTable(p));
		} else {
			table = grandTable;
		}
		String clm = table.getColumnNames()[table.getColumns() - 1];
		StatisticsEngine s = new StatisticsEngine(table, Stats.getInt(),
				Header.getString(), PrintLevel.getInt());
		s.computeStatistics();
		TotalN.set(total_N);
		IncludedN.set(included_N);
		ExcludedN.set(excluded_N);
		N.set(s.n());
		Sum.set(s.sum());
		SumOfSquares.set(s.sumOfSquares());
		Mean.set(s.mean());
		GeometricMean.set(s.geometricMean());
		HarmonicMean.set(s.harmonicMean());
		Minimum.set(s.minimum());
		Maximum.set(s.maximum());
		Median.set(s.median());
		Quantile25.set(s.quantile25());
		Quantile75.set(s.quantile75());
		Median.set(s.median());
		Variance.set(s.variance());
		StandardDeviation.set(s.standardDeviation());
		StandardError.set(s.standardError());
		CoefficientOfVariation.set(s.variationCoefficient());
		Skew.set(s.skew());
		Kurtosis.set(s.kurtosis());
		Range.set(s.range());
		AverageDeviation.set(s.averageDeviation());
		storePlotData(s);
		String f = ResultFormat.getString();
		if (StringExt.nonEmpty(f)) {
			// nothing to do here, since getString() includes formatting
		} else {
			StringBuffer b = new StringBuffer();
			String nl = System.getProperty("line.separator");
			int stats = Stats.getInt();
			b.append(nl + "<font face=\"Arial,Helvetica\">");
			b.append(nl + "<h3>Descriptive Statistics for " + clm + "</h3>");
			b.append(nl + "<table cellpadding=\"4\">");
			if ((stats & StatCodes.TOTAL_N) != 0)
				b.append(nl
						+ "<tr><td align=\"right\">total n</td><td>=</td><td>"
						+ total_N + "</td></tr>");
			if ((stats & StatCodes.INCLUDED_N) != 0) {
				b.append(nl
						+ "<tr><td align=\"right\">Include</td><td>=</td><td>"
						+ Include.getValue().toString() + "</td></tr>");
				b.append(nl
						+ "<tr><td align=\"right\">included</td><td>=</td><td>"
						+ included_N + "</td></tr>");
			}
			if ((stats & StatCodes.EXCLUDED_N) != 0) {
				b.append(nl
						+ "<tr><td align=\"right\">Exclude</td><td>=</td><td>"
						+ Exclude.getValue().toString() + "</td></tr>");
				b.append(nl
						+ "<tr><td align=\"right\">excluded</td><td>=</td><td>"
						+ excluded_N + "</td></tr>");
			}
			b.append(nl + "</table></font>" + nl);
			f = b.toString() + s.getResult();
		}
		showResults(f);
		return 0;
	}
}
