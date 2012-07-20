package de.pxlab.stat;

import java.util.*;

import de.pxlab.util.*;
import de.pxlab.pxl.MinimizationFunctionCodes;
import de.pxlab.pxl.LearningModelCodes;

/**
 * Estimate parameters of a statistical learning model in the tradition of
 * stimulus sampling theory (Estes, 1950). Currently only the one element model
 * (Bower, 1961) is supported.
 * 
 * <p>
 * Input format is a FactorialDataTable with these columns:
 * <ol>
 * <li>Random Factor
 * <li>Item-Code
 * <li>Data: 1 for error and 0 for correct response
 * </ol>
 */
public class LearningModelEngine extends StatEngine implements
		LearningModelCodes, MinimizationFunctionCodes {
	public static final int CHISQUARE_LIMIT = 3;
	private int modelType = AON_MODEL;
	private String modelTypeName;
	private double guessingRate = 0.5;
	private double alpha;
	private int nItems;
	private int nTrials;
	private int[] t;
	private int[] t1;
	private int[] t2;
	private int[] sE;
	private double[] pE;
	private double[] est_pE;
	private int totalE;
	private double meanE;
	private int minimizationFunction;
	private String minimizationFunctionName;
	protected DataTable data;

	/**
	 * Create an estimation engine for the given factorial frequency table.
	 * 
	 * @param dt
	 *            the factorial data table.
	 * @param mT
	 *            the type of model to be estimated.
	 * @param mf
	 *            the minimization method to be used.
	 * @param printOption
	 *            the output print option code.
	 */
	public LearningModelEngine(FactorialDataTable dt, int mT, int mf,
			String title, int printOption) {
		super(title, printOption);
		data = dt.repeatedMeasuresTable();
		modelType = mT;
		if (modelType == AON_MODEL) {
			modelTypeName = "All-or-Nothing Model";
		} else {
			System.out
					.println("Unknown type of learning model! Using AoN Model.");
			modelTypeName = "All-or-Nothing Model";
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
	}

	public void computeStatistics() {
		int nItems = data.getRows();
		int nTrials = data.getColumns() - 1;
		int[][] d = new int[nItems][nTrials];
		for (int j = 0; j < nTrials; j++) {
			double[] c = data.doubleColumn(j + 1);
			for (int i = 0; i < nItems; i++) {
				d[i][j] = (int) Math.round(c[i]);
			}
		}
		t = new int[nItems];
		t1 = new int[nItems];
		t2 = new int[nItems];
		for (int i = 0; i < nItems; i++) {
			t[i] = 0;
			t1[i] = 0;
			t2[i] = 0;
		}
		sE = new int[nTrials];
		for (int i = 0; i < nTrials; i++)
			sE[i] = 0;
		int totalE = 0;
		int sC1 = 0, sC2 = 0;
		int sE1 = 0, sE2 = 0;
		for (int i = 0; i < nItems; i++) {
			int idx = -1;
			for (int j = 0; j < nTrials; j++) {
				t[i] += d[i][j];
				sE[j] += d[i][j];
				totalE += d[i][j];
				if (d[i][j] == 1) {
					idx = j;
				}
			}
			// idx = number of trials before the last error
			int n1, n2;
			if (idx > 0) {
				n1 = n2 = idx / 2;
				if ((idx % 2) == 0) {
					// even
				} else {
					// odd, add one to one of the counts
					if ((i % 2) == 0)
						n1++;
					else
						n2++;
				}
				int k = 0;
				for (int j = 0; j < n1; j++) {
					if (d[i][k++] == 0) {
						sC1++;
					} else {
						sE1++;
						t1[i] = t1[i] + 1;
					}
				}
				for (int j = 0; j < n2; j++) {
					if (d[i][k++] == 0) {
						sC2++;
					} else {
						sE2++;
						t2[i] = t2[i] + 1;
					}
				}
			}
		}
		double y1 = sC1 - guessingRate * (sC1 + sE1);
		double y2 = sC2 - guessingRate * (sC2 + sE2);
		double chiSquareVincent = y1 * y1
				/ (guessingRate * (1.0 - guessingRate) * (sC1 + sE1)) + y2 * y2
				/ (guessingRate * (1.0 - guessingRate) * (sC2 + sE2));
		double probVincent = ChiSquareDist.probabilityOf(chiSquareVincent, 2);
		meanE = (double) totalE / (double) nItems;
		alpha = guessingRate / meanE;
		pE = new double[nTrials];
		est_pE = new double[nTrials];
		for (int j = 0; j < nTrials; j++) {
			pE[j] = (double) sE[j] / (double) nItems;
			est_pE[j] = guessingRate * Math.pow((1.0 - alpha), j);
			addPlotDataLine("curve", String.valueOf(j + 1) + " " + pE[j] + " "
					+ est_pE[j]);
		}
		double chiSquare = 0.0;
		int df = 0;
		double b = 0.0;
		double w = 0.0;
		double dbw;
		for (int j = nTrials - 1; j >= 0; j--) {
			b += sE[j];
			w += (nItems * est_pE[j]);
			if (w >= CHISQUARE_LIMIT) {
				dbw = b - w;
				chiSquare += (dbw * dbw / w);
				df++;
				b = 0.0;
				w = 0.0;
			}
		}
		if (w > 0.0) {
			dbw = b - w;
			chiSquare += (dbw * dbw / w);
			df++;
		}
		double prob = ChiSquareDist.probabilityOf(chiSquare, df - 1);
		/*
		 * console("T[] = ", t); console("sE[] = ", sE);
		 * System.out.println("Mean Error Rate = " + meanE);
		 * System.out.println("alpha = " + alpha); console("pE[] = ", pE);
		 * console("est_pE[] = ", est_pE);
		 */
		if (dPr) {
			beginTable("Empirical Data and Estimated Learning Function");
			Object[] c0 = data.column(0);
			String[] item = new String[nItems];
			for (int i = 0; i < nItems; i++) {
				item[i] = (String) (c0[i]);
			}
			append(nl + "<tr><td>&nbsp;</td><td align=\"center\" colspan=\""
					+ nTrials + "\">Trial</td></tr>");
			append(nl + "<tr><td>Item</td>");
			for (int j = 0; j < nTrials; j++)
				append("<td align=\"right\">" + (j + 1) + "</td>");
			append("<td>T(i)</td><td>T<sub>1</sub>(i)</td><td>T<sub>2</sub>(i)</td></tr>");
			append(nl + "<tr><td colspan=\"" + (nTrials + 4)
					+ "\"><hr></td></tr>");
			for (int i = 0; i < nItems; i++) {
				append(nl + "<tr><td align=\"right\">" + item[i] + "</td>");
				for (int j = 0; j < nTrials; j++)
					append("<td align=\"right\">" + d[i][j] + "</td>");
				append("<td align=\"right\">" + t[i] + "</td>");
				append("<td align=\"right\">" + t1[i] + "</td>");
				append("<td align=\"right\">" + t2[i] + "</td>");
				append("</tr>");
			}
			append(nl + "<tr><td colspan=\"" + (nTrials + 4)
					+ "\"><hr></td></tr>");
			append(nl + "<tr><td>Errors</td>");
			for (int j = 0; j < nTrials; j++)
				append("<td align=\"right\">" + sE[j] + "</td>");
			append("<td align=\"right\">" + totalE + "</td>");
			append("<td align=\"right\">" + sE1 + "</td>");
			append("<td align=\"right\">" + sE2 + "</td>");
			append("</tr>");
			append(nl + "<tr><td>f(t)</td>");
			for (int j = 0; j < nTrials; j++)
				append("<td align=\"right\">" + dfm2(pE[j]) + "</td>");
			append("<td>&nbsp;</td></tr>");
			append(nl + "<tr><td>F(t)</td>");
			for (int j = 0; j < nTrials; j++)
				append("<td align=\"right\">" + dfm2(est_pE[j]) + "</td>");
			append("<td>&nbsp;</td></tr>");
			append(nl + "<tr><td>N*F(t)</td>");
			for (int j = 0; j < nTrials; j++)
				append("<td align=\"right\">" + dfm2(nTrials * est_pE[j])
						+ "</td>");
			append("<td>&nbsp;</td></tr>");
			endTable();
		}
		if (rPr) {
			beginTable("Estimated Learning parameters");
			append(nl + "<tr>");
			append("<td align=\"right\">Items</td>");
			append("<td align=\"right\">Trials</td>");
			append("<td align=\"right\">Mean Errors</td>");
			append("<td align=\"right\">Guessing Rate</td>");
			append("<td align=\"right\">Learning Rate</td>");
			append("</tr>");
			append(nl + "<tr><td colspan=\"5\"><hr></td></tr>");
			append(nl + "<tr>");
			append("<td align=\"right\">" + nItems + "</td>");
			append("<td align=\"right\">" + nTrials + "</td>");
			append("<td align=\"right\">" + dfm2(meanE) + "</td>");
			append("<td align=\"right\">" + dfm2(guessingRate) + "</td>");
			append("<td align=\"right\">" + dfm2(alpha) + "</td>");
			append("</tr>");
			endTable();
		}
		if (rPr) {
			beginTable("Goodness of Fit Test for Learning Curve");
			append(nl + "<tr>");
			append("<td align=\"right\">Items</td>");
			append("<td align=\"right\">Trials</td>");
			append("<td align=\"right\">Learning Rate</td>");
			append("<td align=\"right\">Test Categories</td>");
			append("<td align=\"right\">df</td>");
			append("<td align=\"right\">&chi;<sup>2</sup></td>");
			append("<td align=\"right\">p</td>");
			append("</tr>");
			append(nl + "<tr><td colspan=\"7\"><hr></td></tr>");
			append(nl + "<tr>");
			append("<td align=\"right\">" + nItems + "</td>");
			append("<td align=\"right\">" + nTrials + "</td>");
			append("<td align=\"right\">" + dfm2(alpha) + "</td>");
			append("<td align=\"right\">" + df + "</td>");
			append("<td align=\"right\">" + (df - 1) + "</td>");
			append("<td align=\"right\">" + dfm(chiSquare) + "</td>");
			append("<td align=\"right\">" + dfm(prob) + "</td>");
			append("</tr>");
			endTable();
		}
		if (rPr) {
			beginTable("Vincent Test Results");
			append(nl
					+ "<tr><td colspan=\"3\" align=\"center\">Part 1</td><td colspan=\"3\" align=\"center\">Part 2</td></tr>");
			append(nl + "<tr>");
			append("<td align=\"right\">Trials</td>");
			append("<td align=\"right\">Correct</td>");
			append("<td align=\"right\">Errors</td>");
			append("<td align=\"right\">Trials</td>");
			append("<td align=\"right\">Correct</td>");
			append("<td align=\"right\">Errors</td>");
			append("<td align=\"right\">df</td>");
			append("<td align=\"right\">&chi;<sup>2</sup></td>");
			append("<td align=\"right\">p</td>");
			append("</tr>");
			append(nl + "<tr><td colspan=\"9\"><hr></td></tr>");
			append(nl + "<tr>");
			append("<td align=\"right\">" + (sC1 + sE1) + "</td>");
			append("<td align=\"right\">" + sC1 + "</td>");
			append("<td align=\"right\">" + sE1 + "</td>");
			append("<td align=\"right\">" + (sC2 + sE2) + "</td>");
			append("<td align=\"right\">" + sC2 + "</td>");
			append("<td align=\"right\">" + sE2 + "</td>");
			append("<td align=\"right\">" + 2 + "</td>");
			append("<td align=\"right\">" + dfm(chiSquareVincent) + "</td>");
			append("<td align=\"right\">" + dfm(probVincent) + "</td>");
			append("</tr>");
			endTable();
		}
		/*
		 * chiSquare = 0.0; double csq; for (int i = 0; i < k; i++) { p_dat[i] =
		 * (double)y[i]/(double)n[i]; p_est[i] = f.valueOf(x[i], par[0], par[1],
		 * fixedPar[0], fixedPar[1]); double e = n[i]*p_est[i]; double d = y[i]
		 * - e; double dd = d * d; csq = dd / (e * (1.0 - p_est[i])); //
		 * System.out.println("   " + i + "  " + x[i] + "  " + y[i] + "  " + e +
		 * "  " + dd + "  " + csq); chiSquare += csq; if (pr) append(nl +
		 * "<tr><td>" + x[i] + "</td><td>" + n[i] + "</td><td>" + dfm(p_dat[i])
		 * + "</td><td>" + dfm(p_est[i]) + "</td></tr>");
		 * addPlotDataLine(fixedConditions + "_data", String.valueOf(x[i]) + " "
		 * + String.valueOf(p_dat[i])); // addPlotDataLine(fixedConditions +
		 * "_est", String.valueOf(x[i]) + " " + String.valueOf(p_est[i])); }
		 * 
		 * int nx = 50; double xx = x[0]; double xstep = (x[k-1] - x[0])/(nx-1);
		 * for (int i = 0; i < nx; i++) { double px = f.valueOf(xx, par[0],
		 * par[1], fixedPar[0], fixedPar[1]); addPlotDataLine(fixedConditions +
		 * "_est", String.valueOf(xx) + " " + String.valueOf(px)); xx += xstep;
		 * }
		 * 
		 * if (pr) endTable();
		 * 
		 * if (pr) { int df = k - 2;
		 * beginTable("Parameter Estimates Using Minimization of " +
		 * minimizationFunctionName); append(nl +
		 * "<tr><td colspan=\"3\" align=\"left\">Estimates</td><td align=\"right\">&chi;<sup>2</sup></td><td align=\"right\">df</td><td align=\"right\">p</td></tr>"
		 * ); append(nl + "<tr><td colspan=\"6\"><hr></td></tr>"); double prob =
		 * ChiSquareDist.probabilityOf(chiSquare, k-2); append(nl +
		 * "<tr><td>c</td><td>=</td><td align=\"right\">" + dfm(par[0]) +
		 * "</td><td align=\"right\">"+ dfm(chiSquare) +
		 * "</td><td align=\"right\">"+ df + "</td><td align=\"right\">" +
		 * dfm(prob) + "</td></tr>"); append(nl +
		 * "<tr><td>a</td><td>=</td><td align=\"right\">" + dfm(par[1]) +
		 * "</td></tr>"); append(nl +
		 * "<tr><td>g</td><td>=</td><td align=\"right\">" + dfm(fixedPar[0]) +
		 * "</td></tr>"); append(nl +
		 * "<tr><td>l</td><td>=</td><td align=\"right\">" + dfm(fixedPar[1]) +
		 * "</td></tr>"); endTable(); addPlotDataLine("pars",
		 * fixedConditions.replace('_', ' ') + " " + dfm(par[0]) + " " +
		 * dfm(par[1]) + " " + dfm(fixedPar[0]) + " " + dfm(fixedPar[1])); }
		 */
	}
}
