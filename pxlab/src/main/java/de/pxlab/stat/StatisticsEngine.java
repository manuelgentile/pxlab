package de.pxlab.stat;

import java.util.ArrayList;
import de.pxlab.pxl.StatCodes;

/**
 * Describe the given array of data. Computes lots of descriptive statistics.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class StatisticsEngine extends StatEngine {
	private double sum_, sumOfSquares_, mean_, geometricMean_, harmonicMean_,
			variance_, standardDeviation_, standardError_,
			variationCoefficient_, skew_, kurtosis_, minimum_, maximum_,
			range_, averageDeviation_, quantile25_, quantile50_, quantile75_;
	private int m;
	private ArrayList cumulative_;
	private int stats;
	private DataTable data;

	public StatisticsEngine(DataTable dt, int requestedStats, String title,
			int printOption) {
		super(title, printOption);
		data = dt;
		stats = requestedStats;
	}

	public StatisticsEngine(DataTable dt, int requestedStats, int printOption) {
		this(dt, requestedStats, "Descriptive Statistics", printOption);
	}

	public void computeStatistics() {
		double x;
		double xx;
		double sx = 0.0;
		double sxx = 0.0;
		double sxxx = 0.0;
		double sxxxx = 0.0;
		double gMean = 0.0;
		double hMean = 0.0;
		boolean allGreaterZero = true;
		int column = data.getColumns() - 1;
		double[] a = data.doubleColumn(column);
		m = a.length;
		for (int i = 0; i < m; i++) {
			x = a[i];
			sx += x;
			xx = x * x;
			sxx += xx;
			sxxx += xx * x;
			sxxxx += xx * xx;
			if (allGreaterZero && x > 0.0) {
				gMean += Math.log(x);
				hMean += 1.0 / x;
			} else {
				allGreaterZero = false;
			}
		}
		sum_ = sx;
		sumOfSquares_ = sxx;
		mean_ = sx / m;
		geometricMean_ = allGreaterZero ? Math.exp(gMean / m) : 0.0;
		harmonicMean_ = allGreaterZero ? m / hMean : 0.0;
		variance_ = (sxx - sx * sx / m) / (m - 1.0);
		standardDeviation_ = Math.sqrt(variance_);
		standardError_ = Math.sqrt(variance_ / m);
		variationCoefficient_ = (mean_ != 0.0) ? standardDeviation_ / mean_
				: 0.0;
		if (variance_ > 0.0) {
			skew_ = (sxxx - 3.0 * mean_ * sxx + 3.0 * mean_ * mean_ * sx - mean_
					* mean_ * sx)
					/ (m * variance_ * standardDeviation_);
			kurtosis_ = (sxxxx - 4.0 * mean_ * sxxx + 6.0 * mean_ * mean_ * sxx
					- 4.0 * mean_ * mean_ * mean_ * sx + m * mean_ * mean_
					* mean_ * mean_)
					/ (m * variance_ * variance_);
		} else {
			skew_ = sxxx / m;
			kurtosis_ = sxxxx / m - 3.0;
		}
		double[] b = new double[m];
		System.arraycopy(a, 0, b, 0, m);
		java.util.Arrays.sort(b);
		int i25 = m / 4;
		int i50 = m / 2;
		int i75 = (3 * m) / 4;
		cumulative_ = new ArrayList(m);
		minimum_ = b[0];
		maximum_ = b[m - 1];
		range_ = maximum_ - minimum_;
		double hx = minimum_;
		double histo = 0.0;
		averageDeviation_ = 0.0;
		for (int i = 0; i < m; i++) {
			averageDeviation_ += Math.abs(a[i] - mean_);
			if (b[i] > hx) {
				double[] w = new double[2];
				w[0] = hx;
				w[1] = histo;
				cumulative_.add(w);
			}
			hx = b[i];
			histo = (double) (i + 1) / (double) m;
		}
		double[] w = new double[2];
		w[0] = hx;
		w[1] = histo;
		cumulative_.add(w);
		/*
		 * for (int i = 0; i < cumulative_.size(); i++) { double[] p =
		 * (double[])cumulative_.get(i); System.out.println(i + " " + p[0] + " "
		 * + p[1]); }
		 */
		averageDeviation_ /= m;
		quantile25_ = b[i25];
		quantile50_ = b[i50];
		quantile75_ = b[i75];
		createHTMLResults(data.getColumnNames()[column]);
		for (int i = 0; i < cumulative_.size(); i++) {
			double[] cum = (double[]) cumulative_.get(i);
			addPlotDataLine("cumulative", i + " " + cum[0] + " " + cum[1]);
		}
	}

	private void createHTMLResults(String v) {
		beginTable("Statistics for Variable " + v);
		if ((stats & StatCodes.N) != 0)
			append(nl + "<tr><td align=\"right\">n</td><td>=</td><td>"
					+ this.n() + "</td></tr>");
		if ((stats & StatCodes.MINIMUM) != 0)
			append(nl + "<tr><td align=\"right\">minimum</td><td>=</td><td>"
					+ this.minimum() + "</td></tr>");
		if ((stats & StatCodes.MAXIMUM) != 0)
			append(nl + "<tr><td align=\"right\">maximum</td><td>=</td><td>"
					+ this.maximum() + "</td></tr>");
		if ((stats & StatCodes.SUM) != 0)
			append(nl + "<tr><td align=\"right\">sum</td><td>=</td><td>"
					+ this.sum() + "</td></tr>");
		if ((stats & StatCodes.MEAN) != 0)
			append(nl + "<tr><td align=\"right\">mean</td><td>=</td><td>"
					+ this.mean() + "</td></tr>");
		if ((stats & StatCodes.GEOMETRIC_MEAN) != 0)
			append(nl
					+ "<tr><td align=\"right\">geometric mean</td><td>=</td><td>"
					+ this.geometricMean() + "</td></tr>");
		if ((stats & StatCodes.HARMONIC_MEAN) != 0)
			append(nl
					+ "<tr><td align=\"right\">harmonic mean</td><td>=</td><td>"
					+ this.harmonicMean() + "</td></tr>");
		if ((stats & StatCodes.QUANTILE_25) != 0)
			append(nl
					+ "<tr><td align=\"right\">25 % quantile</td><td>=</td><td>"
					+ this.quantile25() + "</td></tr>");
		if ((stats & StatCodes.MEDIAN) != 0)
			append(nl + "<tr><td align=\"right\">median</td><td>=</td><td>"
					+ this.median() + "</td></tr>");
		if ((stats & StatCodes.QUANTILE_75) != 0)
			append(nl
					+ "<tr><td align=\"right\">75 % quantile</td><td>=</td><td>"
					+ this.quantile75() + "</td></tr>");
		if ((stats & StatCodes.RANGE) != 0)
			append(nl + "<tr><td align=\"right\">range</td><td>=</td><td>"
					+ this.range() + "</td></tr>");
		if ((stats & StatCodes.SUM_OF_SQUARES) != 0)
			append(nl
					+ "<tr><td align=\"right\">sum of squares</td><td>=</td><td>"
					+ this.sumOfSquares() + "</td></tr>");
		if ((stats & StatCodes.VARIANCE) != 0)
			append(nl + "<tr><td align=\"right\">var</td><td>=</td><td>"
					+ this.variance() + "</td></tr>");
		if ((stats & StatCodes.STDDEV) != 0)
			append(nl + "<tr><td align=\"right\">sd</td><td>=</td><td>"
					+ this.standardDeviation() + "</td></tr>");
		if ((stats & StatCodes.AV_DEV) != 0)
			append(nl
					+ "<tr><td align=\"right\">average deviation</td><td>=</td><td>"
					+ this.averageDeviation() + "</td></tr>");
		if ((stats & StatCodes.VAR_COEFF) != 0)
			append(nl
					+ "<tr><td align=\"right\">coeff of variation</td><td>=</td><td>"
					+ this.variationCoefficient() + "</td></tr>");
		if ((stats & StatCodes.STDERR) != 0)
			append(nl + "<tr><td align=\"right\">se</td><td>=</td><td>"
					+ this.standardError() + "</td></tr>");
		if ((stats & StatCodes.SKEW) != 0)
			append(nl + "<tr><td align=\"right\">skew</td><td>=</td><td>"
					+ this.skew() + "</td></tr>");
		if ((stats & StatCodes.KURTOSIS) != 0)
			append(nl + "<tr><td align=\"right\">kurtosis</td><td>=</td><td>"
					+ this.kurtosis() + "</td></tr>");
		endTable();
		if ((stats & StatCodes.CUMULATIVE) != 0) {
			beginTable("Cumulative Distribution");
			ArrayList a = this.cumulative();
			for (int i = 0; i < a.size(); i++) {
				double[] cum = (double[]) a.get(i);
				append(nl + "<tr><td>" + i + "</td><td align=\"right\">"
						+ cum[0] + "</td><td align=\"right\">" + cum[1]
						+ "</td></tr>");
			}
			endTable();
		}
	}

	public int n() {
		return m;
	}

	public double sum() {
		return sum_;
	}

	public double sumOfSquares() {
		return sumOfSquares_;
	}

	public double mean() {
		return mean_;
	}

	public double geometricMean() {
		return geometricMean_;
	}

	public double harmonicMean() {
		return harmonicMean_;
	}

	public double variance() {
		return variance_;
	}

	public double standardDeviation() {
		return standardDeviation_;
	}

	public double standardError() {
		return standardError_;
	}

	public double variationCoefficient() {
		return variationCoefficient_;
	}

	public double skew() {
		return skew_;
	}

	public double kurtosis() {
		return kurtosis_;
	}

	public double minimum() {
		return minimum_;
	}

	public double maximum() {
		return maximum_;
	}

	public double range() {
		return range_;
	}

	public double averageDeviation() {
		return averageDeviation_;
	}

	public double quantile25() {
		return quantile25_;
	}

	public double quantile50() {
		return quantile50_;
	}

	public double median() {
		return quantile50_;
	}

	public double quantile75() {
		return quantile75_;
	}

	public ArrayList cumulative() {
		return cumulative_;
	}
}
