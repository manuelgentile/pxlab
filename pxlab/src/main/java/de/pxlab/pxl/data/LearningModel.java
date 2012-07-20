package de.pxlab.pxl.data;

import de.pxlab.pxl.*;
import de.pxlab.stat.*;
import de.pxlab.util.*;

/**
 * Create a data table from all trial data of the node which contains this
 * DataAnalysis object and compute factor level statistics and an analysis of
 * variance (ANOVA). Only trials which satisfy the Include condition and do not
 * satisfy the Exclude condition are entered into the data table.
 * 
 * <p>
 * The first column of the data table must be the random factor and the last
 * column must be the dependent variable. All other columns are considered to be
 * factors and their values are factor levels. The factor type is determined
 * automatically.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/12/08
 */
public class LearningModel extends DataDisplay implements StatCodes {
	/**
	 * The type of the psychometric function to be estimated: Possible values
	 * are defined in class de.pxlab.pxl.LearningModelCodes.
	 */
	public ExPar ModelType = new ExPar(GEOMETRY_EDITOR,
			LearningModelCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.LearningModelCodes.AON_MODEL"),
			"Type of learning model");
	/**
	 * The type of the psychometric function to be estimated: Possible values
	 * are defined in class de.pxlab.pxl.LearningModelCodes.
	 */
	public ExPar MinimizationFunction = new ExPar(GEOMETRY_EDITOR,
			MinimizationFunctionCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.MinimizationFunctionCodes.CHI_SQUARE"),
			"Type of minimization function");
	/**
	 * Defines values for fixed parameters like guessing rates or asymptotes.
	 */
	public ExPar FixedParameters = new ExPar(PROPORT, new ExParValue(0.0),
			"Fixed parameter values");
	/**
	 * Code for 'yes' responses.
	 * 
	 * @see de.pxlab.pxl.KeyCodes
	 */
	public static ExPar YesCode = new ExPar(KEYCODE, new ExParValueConstant(
			"de.pxlab.pxl.KeyCodes.LEFT_KEY"), "Code for 'yes' responses");

	/** Constructor creating the title of the display. */
	public LearningModel() {
		setTitleAndTopic("Lerning model parameter estimation", Topics.DATA);
		Header.set("Learning Model Parameter Estimation");
	}

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable grandTable) {
		ExParValueTable table = null;
		if (p != null && p.length != 0) {
			table = new ExParValueTable(grandTable.subTable(p));
		} else {
			table = grandTable;
		}
		// table.printColumnNames(System.out);
		StringDataTable sdt = table.stringTable();
		// sdt.printColumnNames(System.out);
		FactorialDataTable fdt = new FactorialDataTable(sdt);
		// ft.printFactorLevelNames(System.out);
		LearningModelEngine engine = new LearningModelEngine(fdt,
				ModelType.getInt(), MinimizationFunction.getInt(),
				Header.getString(), PrintLevel.getInt());
		engine.computeStatistics();
		storePlotData(engine);
		showResults(engine.getResult());
		return 0;
	}
}
