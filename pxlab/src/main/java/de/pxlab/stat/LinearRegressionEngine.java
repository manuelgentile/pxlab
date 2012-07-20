package de.pxlab.stat;

import java.text.*;
import java.util.Locale;

/**
 * Compute correlations and multiple linear regression. The first column of the
 * data table is the dependent variable and all other columns are predictors.
 * 
 * @version 0.1.0
 */
public class LinearRegressionEngine extends StatEngine {
	/** column number of regressor */
	private static int REG = 0;
	/** Name of the regression function for plot output. */
	private String funcName;
	/** names of variables */
	private String[] varName;
	/** number of variables */
	private int nVar;
	/** number of cases */
	private int nCases;
	/* correlation matrix */
	private double[][] correl;
	/* covariance matrix */
	private double[][] covar;
	/* sum of scores for each variable */
	private double[] sum;
	private double[] coeff;
	private double intercept;
	/** The inout data table. */
	private DataTable data;

	public LinearRegressionEngine(DataTable dt, String fN, String title,
			int printOption) {
		super(title, printOption);
		data = dt;
		nVar = data.getColumns();
		String[] vn = data.getColumnNames();
		varName = new String[nVar];
		for (int i = 0; i < nVar; i++)
			varName[i] = localName(vn[i]);
		nCases = data.getRows();
		funcName = fN;
	}

	public LinearRegressionEngine(DataTable dt, String fN, int printOption) {
		this(dt, fN, "Linear Regression", printOption);
	}

	public void computeStatistics() {
		computeCovarianceMatrix();
		computeCorrelationMatrix();
	}

	private void computeCovarianceMatrix() {
		sum = new double[nVar];
		covar = new double[nVar][nVar];
		double[] minx = new double[nVar];
		double[] maxx = new double[nVar];
		for (int row = 0; row < nVar; row++) {
			minx[row] = Double.MAX_VALUE;
			maxx[row] = Double.MIN_VALUE;
		}
		double[] temp;
		for (int i = 0; i < nCases; i++) {
			temp = data.doubleRow(i);
			for (int row = 0; row < nVar; row++) {
				sum[row] += temp[row];
				if (temp[row] < minx[row])
					minx[row] = temp[row];
				if (temp[row] > maxx[row])
					maxx[row] = temp[row];
				for (int col = 0; col <= row; col++) {
					covar[row][col] += temp[row] * temp[col];
				}
			}
		}
		append(nl + "<h4>Regression Analysis for " + nCases + " Cases of "
				+ nVar + " Variables</h4>");
		if (xdPr)
			printMatrix(covar, "Raw SS Matrix");
		for (int row = 0; row < nVar; row++) {
			for (int col = 0; col <= row; col++) {
				covar[row][col] -= sum[row] * sum[col] / nCases;
			}
		}
		if (dPr) {
			beginTable("Descriptive Statistics");
			printNames();
			printVector(minx, "Min");
			printVector(maxx, "Max");
			printVector(sum, "Sum");
			temp = new double[nVar];
			for (int row = 0; row < nVar; row++)
				temp[row] = sum[row] / nCases;
			printVector(temp, "Mean");
			for (int row = 0; row < nVar; row++)
				temp[row] = Math.sqrt(covar[row][row] / (nCases - 1));
			printVector(temp, "sd");
			endTable();
		}
		if (xdPr)
			printMatrix(covar, "Covariance Matrix");
	}

	private void computeCorrelationMatrix() {
		correl = new double[nVar][nVar];
		double denom;
		for (int row = 0; row < nVar; row++) {
			for (int col = 0; col < row; col++) {
				if (!almostZero(denom = covar[row][row] * covar[col][col])) {
					correl[row][col] = covar[row][col] / Math.sqrt(denom);
				} else {
					correl[row][col] = 0.0;
				}
			}
			correl[row][row] = 1.0;
		}
		if (dPr)
			printMatrix(correl, "Correlation Matrix");
	}

	public void computeRegression() {
		/* inverse correlation matrix */
		double[][] invcor = new double[nVar][nVar];
		/* partial correlations */
		double[][] partial = new double[nVar - 1][nVar - 1];
		/** standardized regression weights */
		double[] beta = new double[nVar];
		/** regression coefficients slopes */
		double[] b = new double[nVar];
		/** intercept in regression equation */
		double a;
		coeff = new double[nVar - 1];
		/** F ratio */
		double F;
		/** squared correlation coefficient */
		double Rsq;
		/** squared SE estimate */
		double SEestsq;
		/** number of predictors */
		int nPred = nVar - 1;
		/** degrees of freedom for error */
		int dfError = nCases - nVar;
		/* char Fline[20]; /* holds the F ratio line */
		if (nPred < 1) {
			System.out
					.println("LinearRegressionEngine.computeRegression(): No predictor variables for regression");
			return;
		}
		if (dfError < 1) {
			System.out
					.println("LinearRegressionEngine.computeRegression(): Not enough degrees of freedom for regression");
			return;
		}
		// make correlation matrix symmetric
		for (int row = 0; row < nVar; row++)
			for (int col = 0; col < row; col++)
				correl[col][row] = correl[row][col];
		// copy for inverse and partial correlation
		for (int row = 0; row < nVar; row++)
			for (int col = 0; col < nVar; col++) {
				invcor[row][col] = correl[row][col]; /* copy */
				if (row != 0 && col != 0)
					partial[row - 1][col - 1] = correl[row][col];
			}
		// invert partial
		if (almostZero(invert(partial))) {
			System.out
					.println("LinearRegressionEngine.computeRegression(): These predictors seem to be non-orthogonal\nSingular partial correlation matrix");
			return;
		}
		// invert correlation matrix
		if (almostZero(invert(invcor))) {
			Rsq = 1.0;
		} else if (almostZero(invcor[REG][REG])) {
			Rsq = 0.0;
		} else {
			Rsq = 1.0 - 1.0 / invcor[REG][REG];
		}
		for (int var = 1; var < nVar; var++) {
			beta[var] = b[var] = 0.0;
			for (int col = 1; col < nVar; col++)
				beta[var] += partial[var - 1][col - 1] * correl[REG][col];
		}
		a = sum[REG];
		for (int var = 1; var < nVar; var++)
			if (!almostZero(covar[var][var])) {
				b[var] = beta[var]
						* Math.sqrt(covar[REG][REG] / covar[var][var]);
				a -= b[var] * sum[var];
			}
		a /= nCases;
		intercept = a;
		for (int var = 1; var < nVar; var++)
			coeff[var - 1] = b[var];
		if (rPr) {
			StringBuffer sb = new StringBuffer(100);
			beginTable("Regression Equation for " + varName[REG]);
			append(nl + "<tr><td><p>" + varName[REG] + " = ");
			sb.append(varName[REG] + " = ");
			for (int var = 1; var < nVar; var++) {
				append(dfm(b[var]) + " * " + varName[var] + " + ");
				sb.append(dfm(b[var]) + " * " + varName[var] + " + ");
			}
			append(dfm(a) + "</p></td></tr>\n");
			sb.append(dfm(a));
			endTable();
		}
		StringBuffer sa = new StringBuffer(funcName + "(");
		StringBuffer sb = new StringBuffer(") = ");
		for (int var = 1; var < nVar; var++) {
			if (var > 1)
				sa.append(", ");
			sa.append(varName[var]);
			sb.append(dfm(b[var]) + " * " + varName[var] + " + ");
		}
		sb.append(dfm(a));
		addPlotDataLine("reg_eqn", sa.toString() + sb.toString());
		if (almostZero(Rsq - 1.0))
			F = MAXF;
		else
			F = Rsq * dfError / (nPred * (1.0 - Rsq));
		SEestsq = covar[REG][REG] * (1.0 - Rsq) / dfError;
		if (rPr) {
			beginTable("Significance Test for Prediction of " + varName[REG]);
			append(nl
					+ "<tr><td align=\"left\">Mult-R</td><td align=\"right\">R-Squared</td>");
			append("<td align=\"right\">SEest</td><td align=\"right\">F("
					+ nPred);
			append("," + dfError
					+ ")</td><td align=\"right\">prob(F)</td></tr>");
			append(nl + "<tr><td align=\"right\">" + dfm(Math.sqrt(Rsq))
					+ "</td><td align=\"right\">" + dfm(Rsq) + "</td>");
			append("<td align=\"right\">" + dfm(Math.sqrt(SEestsq)));
			append("</td><td align=\"right\">" + dfm(F) + "</td>");
			append("<td align=\"right\">"
					+ dfm(FDist.probabilityOf(F, nPred, dfError))
					+ "</td></tr>");
			endTable();
		}
		if (xrPr) {
			beginTable("Significance Test(s) for Predictor(s) of "
					+ varName[REG]);
			append(nl + "<tr><td align=\"left\">Predictor</td>");
			append("<td align=\"right\">beta</td>");
			append("<td align=\"right\">b</td>");
			append("<td align=\"right\">Rsq</td>");
			append("<td align=\"right\">se</td>");
			append("<td align=\"right\">t(" + dfError + ")</td>");
			append("<td align=\"right\">p</td></tr>");
			for (int var = 1; var < nVar; var++) {
				double rsq = 0.0;
				double SEsq;
				double Fvar = 0.0;
				double p;
				double SSres;
				if (!almostZero(partial[var - 1][var - 1]))
					rsq = 1.0 - 1.0 / partial[var - 1][var - 1];
				else
					rsq = 0.0;
				SSres = covar[var][var] * (1.0 - rsq);
				if (SSres > FZERO && SEestsq > FZERO) {
					SEsq = SEestsq / SSres;
					Fvar = b[var] * b[var] / SEsq;
					p = FDist.probabilityOf(Fvar, 1, dfError);
				} else {
					SEsq = 0.0;
					Fvar = MAXF;
					p = 0.0;
				}
				append(nl + "<tr><td align=\"left\">" + varName[var] + "</td>");
				append("<td align=\"right\">" + dfm(beta[var]) + "</td>");
				append("<td align=\"right\">" + dfm(b[var]) + "</td>");
				append("<td align=\"right\">" + dfm(rsq) + "</td>");
				append("<td align=\"right\">" + dfm(Math.sqrt(SEsq)) + "</td>");
				append("<td align=\"right\">" + dfm(Math.sqrt(Fvar)) + "</td>");
				append("<td align=\"right\">" + dfm(p) + "</td></tr>");
			}
			endTable();
		}
	}

	private void printMatrix(double[][] mat, String label) {
		beginTable(label);
		printNames();
		for (int row = 0; row < nVar; row++) {
			append(nl + "<tr><td align=\"left\">" + varName[row] + "</td>");
			for (int col = 0; col < nVar; col++) {
				if (col <= row) {
					append("<td align=\"right\">" + dfm(mat[row][col])
							+ "</td>");
				} else {
					append("<td align=\"right\">&nbsp;</td>");
				}
			}
			append("</tr>");
		}
		endTable();
	}

	private void printNames() {
		append(nl + "<tr><td align=\"left\">&nbsp;</td>");
		for (int col = 0; col < nVar; col++)
			append("<td align=\"right\">" + varName[col] + "</td>");
		append("</tr>");
	}

	private void printVector(double[] var, String label) {
		if (label == null)
			label = "&nbsp;";
		append(nl + "<tr><td align=\"left\">" + label + "</td>");
		for (int col = 0; col < nVar; col++)
			append("<td align=\"right\">" + dfm(var[col]) + "</td>");
		append("</tr>");
	}

	public double[] getCoefficients() {
		return coeff;
	}

	public double getIntercept() {
		return intercept;
	}

	public double[] getCorrelations() {
		int n = (nVar * nVar - nVar) / 2;
		double[] c = new double[n];
		int i = 0;
		for (int row = 1; row < nVar; row++) {
			for (int col = 0; col < row; col++) {
				c[i++] = correl[row][col];
			}
		}
		return c;
	}
}
