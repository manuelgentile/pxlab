package de.pxlab.stat;

import java.util.*;

import de.pxlab.util.*;
import de.pxlab.pxl.PsychometricFunctionCodes;
import de.pxlab.pxl.MinimizationFunctionCodes;
import de.pxlab.pxl.PsychometricFunction;

/**
 * Compute parameters of psychometric functions.
 * 
 * <p>
 * Input format is a FactorialFrequencyTable with these columns:
 * 
 * <p>
 * 1. Random Factor
 * <p>
 * 2. Factor level code
 * <p>
 * . Factor level code
 * <p>
 * n-2. Independent variable of a physical intensity.
 * <p>
 * n-1. Response code
 * <p>
 * n. Frequency of cases for factor level combination
 * 
 * <p>
 * This class also must know which response code value (column n-1) is the
 * 'yes'-response.
 * 
 * <p>
 * The class also computes a chi square goodness of fit test. For this purpose a
 * chi square test data table is created which builds groups of successive data
 * points such that the following two restrictions on the expected frequencies
 * for each response category are observed: (1) the expected frequencies for
 * each of the two response categories in each group are at least 1, and (2) the
 * expected frequency for one of the two response categories is at least 5. If
 * this condition can't be met than no chi square test is computed. Note that
 * the actual degrees of freedom are the number of groups built minus 2 because
 * we estimate 2 arameters.
 * 
 * @version 0.2.0
 */
public class PsychometricFunctionEngine extends StatEngine implements
		PsychometricFunctionCodes, MinimizationFunctionCodes {
	private int functionType;
	private String functionTypeName;
	/**
	 * The factor level of the response factor which corresponds to the 'yes'
	 * response.
	 */
	private String yesCode;
	private double[] fixedPar = { 0.0, 0.0 };
	private double[] par;
	private String fixedConditions = "";
	/**
	 * The number of factors in the data table. This is one less than the number
	 * of columns in the input table.
	 */
	protected FactorialFrequencyTable data;
	protected int nFactors;
	protected int nCondFactors;
	protected int[] nFactorLevels;
	protected boolean doMinimization = true;
	protected double[] setPar;
	protected double[] x;
	protected int[] n;
	protected int[] y;
	protected double[] p_dat;
	protected double[] p_est;
	protected double chiSquare;
	protected int minimizationFunction;
	protected String minimizationFunctionName;

	/**
	 * Create an estimation engine for the given factorial frequency table.
	 * 
	 * @param grandData
	 *            the factorial frequency table.
	 * @param fT
	 *            the type of function to be estimated. Available types are
	 *            listed in class de.pxlab.pxl.PsychometricFunctionCodes.
	 * @param mf
	 *            the minimization method to be used. Available methods are
	 *            listed in class de.pxlab.pxl.MinimizationFunctionCodes.
	 * @param yC
	 *            the 'YES'-code factor level in the frequency table.
	 * @param fP
	 *            array of fixed parameters of the psychometric function.
	 * @param printOption
	 *            the output print option code.
	 */
	public PsychometricFunctionEngine(FactorialFrequencyTable grandData,
			int fT, int mf, String yC, double[] fP, String title,
			int printOption) {
		super(title, printOption);
		data = grandData;
		nFactors = data.numberOfFactors();
		nCondFactors = nFactors - 3;
		nFactorLevels = data.numberOfFactorLevels();
		functionType = fT;
		if (functionType == GUMBEL) {
			functionTypeName = "Gumbel";
		} else if (functionType == WEIBULL) {
			functionTypeName = "Weibull";
		} else if (functionType == LOGISTIC) {
			functionTypeName = "Logistic";
		} else if (functionType == ISOTONIC) {
			functionTypeName = "Isotonic";
		} else {
			System.out
					.println("Unknown type of psychometric function! Using logistic.");
			functionTypeName = "Logistic";
		}
		minimizationFunction = mf;
		if (minimizationFunction == CHI_SQUARE) {
			minimizationFunctionName = "Sum of &chi;<sup>2</sup>";
		} else if (minimizationFunction == LOG_LIKELIHOOD) {
			minimizationFunctionName = "Negative Log Likelihood";
		} else if (minimizationFunction == SQUARED_DEV_LOGIT) {
			minimizationFunctionName = "Sum of squared deviations of Logit transforms";
		} else {
			System.out
					.println("Unknown type of minimization function! Using chi square.");
			minimizationFunction = CHI_SQUARE;
			minimizationFunctionName = "Sum of &chi;<sup>2</sup>";
		}
		yesCode = yC;
		if (fP != null)
			for (int i = 0; i < fP.length; i++)
				fixedPar[i] = fP[i];
		/*
		 * System.out.println("Psychometric function estimation");
		 * System.out.println("  Function type: " + functionType);
		 * System.out.println("  'Yes' code: " + yesCode); for (int i = 0; i <
		 * fixedPar.length; i++) { System.out.println("  fixed Parameter [" + i
		 * + "] = " + fixedPar[i]); }
		 * 
		 * System.out.println("  Grand data table");
		 * System.out.println("   Number of factors: " + nFactors);
		 * System.out.println("   Number of condition factors: " +
		 * nCondFactors);
		 * 
		 * System.out.println("   Random Factor: " +
		 * grandData.getFactorName(0)); for (int i = 0; i < nCondFactors; i++) {
		 * System.out.println("   Condition Factor: " +
		 * grandData.getFactorName(1+i)); for (int j = 0; j <
		 * nFactorLevels[1+i]; j++) { System.out.println("      Level: " +
		 * grandData.factorLevelName(1+i, j)); } }
		 * System.out.println("   Physical Intensity Factor: " +
		 * grandData.getFactorName(nCondFactors + 1)); for (int j = 0; j <
		 * nFactorLevels[nCondFactors + 1]; j++) {
		 * System.out.println("      Level: " +
		 * grandData.factorLevelName(nCondFactors + 1, j)); }
		 * System.out.println("   Response Factor: " +
		 * grandData.getFactorName(nCondFactors + 2)); for (int j = 0; j <
		 * nFactorLevels[nCondFactors + 2]; j++) {
		 * System.out.println("      Level: " +
		 * grandData.factorLevelName(nCondFactors + 2, j)); } /*
		 */
	}

	public PsychometricFunctionEngine(FactorialFrequencyTable grandData,
			int fT, int mf, String yC, double[] fP, int printOption) {
		this(grandData, fT, mf, yC, fP,
				"Psychometric Function Parameter Estimation", printOption);
	}

	public void setResults(double[] sP, boolean pr) {
		setPar = sP;
		doMinimization = false;
		estimate();
	}

	public void estimate() {
		int[] m = new int[nFactors];
		for (int i = 0; i < nFactors; i++)
			m[i] = -1;
		for (int i = 0; i < nCondFactors; i++)
			m[1 + i] = 0;
		for (Iterator it = data.subSectionIterator(m); it.hasNext();) {
			// Create the table subsection
			FactorialDataTable ftb = (FactorialDataTable) it.next();
			// System.out.println(ftb.toString());
			// System.out.println(ftb.describe());
			// ftb.print(System.out);
			if (aPr) {
				append(nl + "<h3>" + functionTypeName
						+ " Psychometric Function</h3>");
			}
			// Describe table subsection by its fixed conditions
			if (aPr)
				beginTable("Fixed Conditions");
			fixedConditions = "";
			Integer key = (Integer) (ftb.keySet().toArray()[0]);
			int[] idx = ftb.factorLevelOfKey(key);
			for (int i = 0; i < nCondFactors; i++) {
				if (aPr)
					append(nl + "<tr><td>" + ftb.getFactorName(1 + i)
							+ "</td><td>=</td><td>"
							+ ftb.factorLevelName(1 + i, /* idx[1+i] */0)
							+ "</td></tr>");
				fixedConditions = (i == 0 ? "" : (fixedConditions + "_"))
						+ ftb.factorLevelName(1 + i, 0);
			}
			if (aPr)
				endTable();
			// Estmate parameters
			estimatePMF(ftb, functionType, yesCode, fixedPar);
		}
	}

	private void estimatePMF(FactorialDataTable data, int functionType,
			String yesCode, double[] fixedPar) {
		Object[] key = data.keySet().toArray();
		int[] m = data.factorLevelOfKey((Integer) key[0]);
		int nFactors = data.numberOfFactors();
		int kFactor = nFactors - 2;
		int k = data.numberOfFactorLevels()[kFactor];
		x = new double[k];
		n = new int[k];
		y = new int[k];
		p_dat = new double[k];
		p_est = new double[k];
		// Create data arrays x[], n[], and y[]
		for (int i = 0; i < k; i++) {
			y[i] = 0;
			n[i] = 0;
			try {
				x[i] = Double.valueOf(data.factorLevelName(kFactor, i))
						.doubleValue();
			} catch (NumberFormatException nfx) {
				x[i] = 0.0;
			}
		}
		for (int j = 0; j < key.length; j++) {
			int[] idx = data.factorLevelOfKey((Integer) key[j]);
			int h = idx[kFactor];
			int d = data.intValueFor(key[j]);
			n[h] += d;
			if (yesCode.equals(data.factorLevelName(kFactor + 1,
					idx[kFactor + 1])))
				y[h] += d;
		}
		double[] es = initialEstimates();
		/*
		 * System.out.println("Estimate PMF for "); for (int i = 0; i < k; i++)
		 * { System.out.println("\t" + x[i] + "\t" + n[i] + "\t" + y[i]); }
		 * System.out.println("Initial estimates:  c=" + es[0] + "  a=" +
		 * es[1]); /*
		 */
		// Run minimization procedure
		PsychometricFunction f = null;
		par = new double[2];
		if (functionType == ISOTONIC) {
			IsotonicPMF isoPMF = new IsotonicPMF(x, n, y);
			par[0] = isoPMF.argumentFor(0.5);
			par[1] = (isoPMF.argumentFor(0.75) - isoPMF.argumentFor(0.25)) / 2.0;
			f = isoPMF;
		} else {
			if (functionType == GUMBEL) {
				f = new GumbelPMF();
			} else if (functionType == WEIBULL) {
				f = new WeibullPMF();
			} else {
				f = new LogisticPMF();
			}
			par[0] = es[0];
			par[1] = es[1];
			if (doMinimization) {
				Praxis praxis = new Praxis();
				praxis.setPrintControl(xrPr ? 3 : 0);
				// praxis.setScaling(10.0);
				double minSquare = praxis.minimize(new MinimizationFunction(f,
						fixedPar[0], fixedPar[1]), par);
			} else {
				if ((setPar != null) && (setPar.length == 2)) {
					par[0] = setPar[0];
					par[1] = setPar[1];
				}
			}
		}
		// Ready to print the output
		if (rPr) {
			beginTable("Empirical Data and Estimated Probabilities");
			append(nl
					+ "<tr><td align=\"center\">x</td><td align=\"center\">N</td><td align=\"center\">p</td><td align=\"center\">P</td></tr>");
		}
		chiSquare = 0.0;
		double csq;
		for (int i = 0; i < k; i++) {
			p_dat[i] = (double) y[i] / (double) n[i];
			p_est[i] = f
					.valueOf(x[i], par[0], par[1], fixedPar[0], fixedPar[1]);
			double e = n[i] * p_est[i];
			double d = y[i] - e;
			double dd = d * d;
			csq = dd / (e * (1.0 - p_est[i]));
			// System.out.println("   " + i + "  " + x[i] + "  " + y[i] + "  " +
			// e + "  " + dd + "  " + csq);
			chiSquare += csq;
			if (rPr)
				append(nl + "<tr><td>" + x[i] + "</td><td>" + n[i]
						+ "</td><td>" + dfm(p_dat[i]) + "</td><td>"
						+ dfm(p_est[i]) + "</td></tr>");
			addPlotDataLine(fixedConditions + "_data", String.valueOf(x[i])
					+ " " + String.valueOf(p_dat[i]));
			// addPlotDataLine(fixedConditions + "_est", String.valueOf(x[i]) +
			// " " + String.valueOf(p_est[i]));
		}
		if (rPr)
			endTable();
		int chiClassIndex = 0;
		double[] expFrqY = new double[k];
		double[] expFrqN = new double[k];
		double[] obsFrqY = new double[k];
		double[] obsFrqN = new double[k];
		int[] lastDataIndex = new int[k];
		double eFY = 0.0;
		double oFY = 0.0;
		double eFN = 0.0;
		double oFN = 0.0;
		boolean hasValidChiSquare = true;
		for (int i = 0; i < k; i++) {
			eFY += n[i] * p_est[i];
			eFN += n[i] * (1.0 - p_est[i]);
			oFY += n[i] * p_dat[i];
			oFN += n[i] * (1.0 - p_dat[i]);
			if ((eFY >= 1.0) && (eFN >= 1.0) && ((eFY >= 5.0) || (eFN >= 5.0))) {
				expFrqY[chiClassIndex] = eFY;
				obsFrqY[chiClassIndex] = oFY;
				expFrqN[chiClassIndex] = eFN;
				obsFrqN[chiClassIndex] = oFN;
				lastDataIndex[chiClassIndex] = i;
				eFY = 0.0;
				oFY = 0.0;
				eFN = 0.0;
				oFN = 0.0;
				chiClassIndex++;
			}
		}
		if (chiClassIndex == 0) {
			hasValidChiSquare = false;
		} else {
			if (lastDataIndex[chiClassIndex - 1] != (k - 1)) {
				// last class is too small
				expFrqY[chiClassIndex - 1] += eFY;
				obsFrqY[chiClassIndex - 1] += oFY;
				expFrqN[chiClassIndex - 1] += eFN;
				obsFrqN[chiClassIndex - 1] += oFN;
				lastDataIndex[chiClassIndex - 1] = k - 1;
			}
		}
		double chiSquareCat = 0.0;
		double probChiSquareCat = 0.0;
		int chiSquaredf = 0;
		if (hasValidChiSquare) {
			if (rPr) {
				beginTable("Chi Square Test Data");
				append(nl
						+ "<tr><td align=\"center\">x]</td><td align=\"center\">N</td><td align=\"center\">Observed</td><td align=\"center\">Expected</td></tr>");
			}
			for (int i = 0; i < chiClassIndex; i++) {
				double d = obsFrqY[i] - expFrqY[i];
				double dd = d * d;
				double sm = dd / expFrqY[i];
				chiSquareCat += sm;
				// System.out.println("   " + i + "  " + x[i] + "  " + y[i] +
				// "  " + e + "  " + dd + "  " + csq);
				d = obsFrqN[i] - expFrqN[i];
				dd = d * d;
				sm = dd / expFrqN[i];
				chiSquareCat += sm;
				int nn = (int) Math.round(obsFrqY[i] + obsFrqN[i]);
				// System.out.println("   " + i + "  " + x[i] + "  " + y[i] +
				// "  " + e + "  " + dd + "  " + csq);
				if (rPr)
					append(nl + "<tr><td>" + x[lastDataIndex[i]] + "</td><td>"
							+ nn + "</td><td>" + ((int) Math.round(obsFrqY[i]))
							+ "</td><td>" + dfm(expFrqY[i]) + "</td></tr>");
			}
			if (rPr)
				endTable();
			if (chiClassIndex > 2) {
				chiSquaredf = chiClassIndex - 2;
				probChiSquareCat = ChiSquareDist.probabilityOf(chiSquareCat,
						chiSquaredf);
				/*
				 * if (rPr) { beginTable("Chi Square Test Result"); append(nl +
				 * "<tr><td align=\"center\">&chi;<sup>2</sup></td><td align=\"center\">df</td><td align=\"center\">p</td></tr>"
				 * ); }
				 * 
				 * 
				 * if (rPr) append(nl + "<tr><td>" + dfm(chiSquareCat) +
				 * "</td><td>" + (chiClassIndex-2) + "</td><td>" +
				 * dfm(probChiSquareCat) + "</td><td>");
				 * 
				 * if (rPr) endTable();
				 */
			}
		}
		int nx = 50;
		double xx = x[0];
		double xstep = (x[k - 1] - x[0]) / (nx - 1);
		for (int i = 0; i < nx; i++) {
			double px = f.valueOf(xx, par[0], par[1], fixedPar[0], fixedPar[1]);
			addPlotDataLine(fixedConditions + "_est", String.valueOf(xx) + " "
					+ String.valueOf(px));
			xx += xstep;
		}
		if (rPr) {
			int df = k - 2;
			beginTable("Parameter Estimates Using Minimization of "
					+ minimizationFunctionName);
			append(nl
					+ "<tr><td colspan=\"3\" align=\"left\">Estimates</td><td align=\"right\">&chi;<sup>2</sup></td><td align=\"right\">df</td><td align=\"right\">p</td></tr>");
			append(nl + "<tr><td colspan=\"6\"><hr></td></tr>");
			// double prob = ChiSquareDist.probabilityOf(chiSquare, k-2);
			append(nl + "<tr><td>c</td><td>=</td><td align=\"right\">"
					+ dfm(par[0]) + "</td><td align=\"right\">"
					+ dfm(chiSquareCat) + "</td><td align=\"right\">"
					+ chiSquaredf + "</td><td align=\"right\">"
					+ dfm(probChiSquareCat) + "</td></tr>");
			append(nl + "<tr><td>a</td><td>=</td><td align=\"right\">"
					+ dfm(par[1]) + "</td></tr>");
			append(nl + "<tr><td>g</td><td>=</td><td align=\"right\">"
					+ dfm(fixedPar[0]) + "</td></tr>");
			append(nl + "<tr><td>l</td><td>=</td><td align=\"right\">"
					+ dfm(fixedPar[1]) + "</td></tr>");
			endTable();
			addPlotDataLine("pars", fixedConditions.replace('_', ' ') + " "
					+ dfm(par[0]) + " " + dfm(par[1]) + " " + dfm(fixedPar[0])
					+ " " + dfm(fixedPar[1]));
		}
	}

	/**
	 * Compute initial estimates for the psychometric function parameters. These
	 * are computed according to the Berkson () method.
	 */
	private double[] initialEstimates() {
		// System.out.println("Initial Estimates:");
		int k = x.length;
		double[] par = new double[2];
		double sNw = 0.0;
		double sNwx = 0.0;
		double sNwy = 0.0;
		double sNwx2 = 0.0;
		double sNwxy = 0.0;
		for (int i = 0; i < k; i++) {
			double p = (double) y[i] / (double) n[i];
			double logp = Math.log((y[i] + 0.5) / (n[i] - y[i] + 0.5));
			double w = p * (1.0 - p);
			// System.out.println(" x[i] = " + x[i] + ",  logp = " + logp);
			double Nw = n[i] * w;
			sNw += Nw;
			double Nwx = Nw * x[i];
			sNwx += Nwx;
			double Nwy = Nw * logp;
			sNwy += Nwy;
			double Nwx2 = Nwx * x[i];
			sNwx2 += Nwx2;
			double Nwxy = Nwx * logp;
			sNwxy += Nwxy;
		}
		double mx = sNwx / sNw;
		double my = sNwy / sNw;
		double Sx2 = sNwx2 - mx * sNwx;
		double Sxy = sNwxy - mx * sNwy;
		/*
		 * System.out.println("  sNwx = " + sNwx);
		 * System.out.println("  sNwy = " + sNwy);
		 * System.out.println(" sNwx2 = " + sNwx2);
		 * System.out.println(" sNwxy = " + sNwxy);
		 * System.out.println("   sNw = " + sNw); System.out.println("    mx = "
		 * + mx); System.out.println("    my = " + my);
		 * System.out.println("   Sx2 = " + Sx2); System.out.println("   Sxy = "
		 * + Sxy);
		 */
		if ((Sx2 > 0.0001) && (Sxy > 0.0001)) {
			par[1] = Sx2 / Sxy;
			par[0] = mx - par[1] * my;
		} else {
			par[0] = x[k / 2];
			par[1] = x[k / 2 + 1] - par[0];
		}
		return par;
	}
	private class MinimizationFunction implements PraxisFunction {
		private PsychometricFunction f;
		private double guessing;
		private double lapsing;

		public MinimizationFunction(PsychometricFunction f, double guessing,
				double lapsing) {
			this.f = f;
			this.guessing = guessing;
			this.lapsing = lapsing;
		}

		public double valueOf(double[] p) {
			if (minimizationFunction == CHI_SQUARE) {
				return valueOfChiSquare(p);
			} else if (minimizationFunction == LOG_LIKELIHOOD) {
				return valueOfLogLikelihood(p);
			} else if (minimizationFunction == SQUARED_DEV_LOGIT) {
				return valueOfSquaredDevLogit(p);
			}
			return valueOfChiSquare(p);
		}

		public double valueOfSquaredDevLogit(double[] p) {
			double chsq = 0.0;
			double lg_w, lg_y, d, w;
			for (int i = 0; i < x.length; i++) {
				w = f.valueOf(x[i], p[0], p[1], guessing, lapsing);
				lg_w = Math.log(w / (1.0 - w));
				lg_y = Math.log((y[i] + 0.5) / (n[i] - y[i] + 0.5));
				d = lg_y - lg_w;
				chsq += n[i] * w * (1.0 - w) * d * d;
				// System.out.println("x[i]=" + x[i] + " y[i]=" + y[i] + " e=" +
				// e);
			}
			// System.out.println("p[0]=" + p[0] + " p[1]=" + p[1] + " chsq=" +
			// chsq);
			return chsq;
		}

		public double valueOfChiSquare(double[] p) {
			double chsq = 0.0;
			double e, d, w;
			for (int i = 0; i < x.length; i++) {
				w = f.valueOf(x[i], p[0], p[1], guessing, lapsing);
				e = n[i] * w;
				d = y[i] - e;
				chsq += d * d / (e * (1.0 - w));
				// System.out.println("x[i]=" + x[i] + " y[i]=" + y[i] + " e=" +
				// e);
			}
			// System.out.println("p[0]=" + p[0] + " p[1]=" + p[1] + " chsq=" +
			// chsq);
			return chsq;
		}

		public double valueOfLogLikelihood(double[] p) {
			double chsq = 0.0;
			double d, w, lw, lv;
			double logBinomialCoeff = 0.0;
			for (int i = 0; i < x.length; i++) {
				w = f.valueOf(x[i], p[0], p[1], guessing, lapsing);
				lw = Math.log(w);
				lv = Math.log(1.0 - w);
				d = y[i] * lw + (n[i] - y[i]) * lv;
				// logBinomialCoeff = MathExt.binom(n[i], y[i]);
				chsq -= (logBinomialCoeff + d);
				// System.out.println("x[i]=" + x[i] + " y[i]=" + y[i] + " e=" +
				// e);
			}
			// System.out.println("p[0]=" + p[0] + " p[1]=" + p[1] + " chsq=" +
			// chsq);
			return chsq;
		}
	}
	/**
	 * A logistic psychometric function:
	 * <p>
	 * p(yes|x) = 1/(1+exp((c-x)/a))
	 * <p>
	 * including parameters for guessing and lapsing.
	 */
	private class LogisticPMF implements PsychometricFunction {
		public double valueOf(double x, double pse, double jnd, double lo,
				double hi) {
			return lo + (1.0 - lo - hi)
					* (1.0 / (1.0 + Math.exp((pse - x) / jnd)));
		}
	}
	/**
	 * A Weibull psychometric function:
	 * <p>
	 * p(yes|x) = 1 - exp(-(x/c)**a)
	 * <p>
	 * including parameters for guessing and lapsing.
	 */
	private class WeibullPMF implements PsychometricFunction {
		public double valueOf(double x, double pse, double jnd, double lo,
				double hi) {
			return lo + (1.0 - lo - hi)
					* (1.0 - Math.exp(-Math.pow(x / pse, jnd)));
		}
	}
	/**
	 * A Gumbel psychometric function:
	 * <p>
	 * p(yes|x) = 1 - exp(-exp((x-c)/a))
	 * <p>
	 * including parameters for guessing and lapsing.
	 */
	private class GumbelPMF implements PsychometricFunction {
		public double valueOf(double x, double pse, double jnd, double lo,
				double hi) {
			return lo + (1.0 - lo - hi)
					* (1.0 - Math.exp(-Math.exp((x - pse) / jnd)));
		}
	}
}
