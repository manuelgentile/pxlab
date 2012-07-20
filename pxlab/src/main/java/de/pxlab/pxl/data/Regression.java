package de.pxlab.pxl.data;

import de.pxlab.pxl.*;
import de.pxlab.stat.*;
import de.pxlab.util.*;

/**
 * Compute correlations and a linear regression. The first column of the data
 * table is considered to be the dependent variable (regressor) and all other
 * columns are considered to be independent variables (predictors).
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see de.pxlab.stat.LinearRegressionEngine
 */
/*
 * 
 * 2005/12/08
 */
public class Regression extends DataDisplay implements StatCodes {
	/**
	 * Defines the statistics which should be computed. public ExPar Statistics
	 * = new ExPar(GEOMETRY_EDITOR, StatCodes.class, new
	 * ExParValueConstant("de.pxlab.pxl.StatCodes.LINREG"),
	 * "Statistics to be computed");
	 */
	/**
	 * Contains the pairwise correlations between all variables. Index sequence
	 * is (1,2), (1,3), (2,3), (1,4), (2,4), (3,4), ...
	 */
	public ExPar Correlations = new ExPar(RTDATA, new ExParValue(0),
			"Correlations");
	/**
	 * Contains the multiplicative coefficients of the regression equation,
	 * starting with the second parameter which is the first predictor.
	 */
	public ExPar Coefficients = new ExPar(RTDATA, new ExParValue(0),
			"Regression coefficients");
	/** Contains the intercept of the regression equation. */
	public ExPar Intercept = new ExPar(RTDATA, new ExParValue(0),
			"Intercept of regression equation");
	/** Name of the regression function. Used for plot purpose only. */
	public ExPar FunctionName = new ExPar(STRING, new ExParValue("Reg"),
			"Regression function name");

	/** Constructor creating the title of the display. */
	public Regression() {
		setTitleAndTopic("Linear regression", Topics.DATA);
		Header.set("Linear Regression");
	}

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable grandTable) {
		ExParValueTable table = null;
		if (p != null && p.length != 0) {
			table = new ExParValueTable(grandTable.subTable(p));
		} else {
			table = grandTable;
		}
		LinearRegressionEngine engine = new LinearRegressionEngine(table,
				FunctionName.getString(), Header.getString(),
				PrintLevel.getInt());
		engine.computeStatistics();
		engine.computeRegression();
		Correlations.set(engine.getCorrelations());
		Coefficients.set(engine.getCoefficients());
		Intercept.set(engine.getIntercept());
		storePlotData(engine);
		showResults(engine.getResult());
		return 0;
	}
}
