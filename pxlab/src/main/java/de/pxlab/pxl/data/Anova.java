package de.pxlab.pxl.data;

import de.pxlab.pxl.*;
import de.pxlab.stat.*;
import de.pxlab.util.*;

/**
 * Create a data table from all argument data of the node which contains this
 * DataAnalysis object and compute factor level statistics and an analysis of
 * variance (ANOVA).
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
public class Anova extends DataDisplay implements StatCodes {
	/* Defines the statistics which should be computed. */
	/*
	 * public ExPar Statistics = new ExPar(GEOMETRY_EDITOR, StatCodes.class, new
	 * ExParValueConstant("de.pxlab.pxl.StatCodes.ANOVA"),
	 * "Statistics to be computed");
	 */
	public ExPar MainEffectContrast = new ExPar(STRING, new ExParValue(""),
			"Main effect contrast definition(s)");
	public ExPar SimpleEffect = new ExPar(STRING, new ExParValue(""),
			"Simple effect analysis definition(s)");
	public ExPar LeveneMeansTest = new ExPar(FLAG, new ExParValue(0),
			"Compute Levene means test for homogeneity");

	/** Constructor creating the title of the display. */
	public Anova() {
		setTitleAndTopic("Factor level statistics and ANOVA", Topics.DATA);
		Header.set("ANOVA Results");
	}

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable grandTable) {
		ExParValueTable table = null;
		if (p != null && p.length != 0) {
			table = new ExParValueTable(grandTable.subTable(p));
		} else {
			table = grandTable;
		}
		FactorialExParValueTable ft = new FactorialExParValueTable(table);
		AnovaEngine engine = new AnovaEngine(ft, Header.getString(),
				PrintLevel.getInt());
		String[] a = MainEffectContrast.getStringArray();
		for (int i = 0; i < a.length; i++) {
			if (StringExt.nonEmpty(a[i])) {
				engine.addAnalysis(
						de.pxlab.stat.AnovaEngine.MAIN_EFFECT_CONTRAST, a[i]);
			}
		}
		a = SimpleEffect.getStringArray();
		for (int i = 0; i < a.length; i++) {
			if (StringExt.nonEmpty(a[i])) {
				engine.addAnalysis(de.pxlab.stat.AnovaEngine.SIMPLE_EFFECT,
						a[i]);
			}
		}
		if (LeveneMeansTest.getFlag()) {
			engine.addAnalysis(de.pxlab.stat.AnovaEngine.LEVENE_MEANS_TEST, " ");
		}
		engine.computeStatistics();
		engine.computeOverallTest();
		engine.computeAnalyses();
		storePlotData(engine);
		showResults(engine.getResult());
		return 0;
	}
}
