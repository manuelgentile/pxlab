package de.pxlab.stat;

import java.text.*;
import java.util.*;

import de.pxlab.util.*;

/**
 * Analysis of variance for factorial data sets.
 * 
 * This code is derived from the ingenious algorithms used in the program
 * ANOVA.C of the <a href="http://www1.acm.org/perlman/stat/">|STAT</a> package
 * written by Gary Perlman. Results have been verified against some of the
 * examples in the following book:
 * 
 * <p>
 * Keppel, G. & Wickens, Th. D. (2004). Design and Analysis: A Researcher's
 * Handbook (4th ed.). New Jersey: Pearson.
 * 
 * @see FactorialDataTable
 * @version 0.3.0
 */
/*
 * 
 * To do: Things don't seem to work when a contrast is tested on a simple effect
 * subset. See computeSimpleEffect()
 */
public class AnovaEngine extends StatEngine {
	protected static final int RANDOM = 0;
	protected static final int BETWEEN = 1;
	protected static final int WITHIN = 2;
	protected static final int DATA = 3;
	protected static final int FIXED = 4;
	/**
	 * A simple effect is an effect observed at a fixed factor level combination
	 * of some of the factors involved. It is computed by extracting the
	 * subtable at the respective factor level combination and then computing a
	 * main effect analysis for the subtable.
	 */
	public static final int SIMPLE_EFFECT = 1;
	/**
	 * A main effect contrast is a linear contrast of the main effect for a
	 * single factor. Its computation depends on whether the respective factor
	 * is a between or a within subjects factor.
	 */
	public static final int MAIN_EFFECT_CONTRAST = 2;
	/**
	 * Run a Levene means test. This is for checking homogeneity of variances in
	 * all factorial subgroups. Currently only the arithmetic means test can be
	 * run.
	 */
	public static final int LEVENE_MEANS_TEST = 3;
	/** Define one of the factors as a random factor. */
	public static final int RANDOM_FACTOR = 4;
	/** The input data table. */
	private FactorialDataTable data;
	/** Number of factors in the input data table. */
	private int nFactors;
	private String[] shortFactorName = { "R", "A", "B", "C", "D", "E", "F" };
	private String[] factorName;
	/** Contains the number of factor levels for each factor. */
	private int[] nFactorLevels;
	/** Contains the factor type (RANDOM, BETWEEN, WITHIN) for each factor. */
	private int[] factorType;
	/** Contains the sampling type (RANDOM, FIXED) for each factor. */
	private int[] factorSamplingType;
	/**
	 * The number of possible sources of variability in the data file. This is
	 * 2^nFactors.
	 */
	private int nSources;
	/**
	 * These are the 'bracket terms' of Keppel & Wickens (2004). They are
	 * computed by a call to computeStatistics().
	 */
	private double[] bracket;
	private double[] _ssEffect;
	private double[] _ssError;
	private int[] _dfEffect;
	private int[] _dfError;
	/**
	 * This array contains the bracket terms for every factor at every factor
	 * level.
	 */
	private double[][] bracketAt;
	/**
	 * This array contains the mean for every factor at every factor level.
	 */
	private double[][] meanAt;
	/**
	 * This array contains the number of terms for every factor at every factor
	 * level.
	 */
	private int[][] countAt;
	private int[] sumCount;
	/** Stores the cell means for the Levene Means Test. */
	private FactorialDataTable cellMeans;
	/** Contains all between factors. */
	private BitSet betweenFactors = new BitSet();
	private int betweenLevels;
	private int withinLevels;
	private int effectCount;
	/**
	 * The factor with index 0 is the sampling factor. It generates the data
	 * collection units. In most cases this will be subjects.
	 */
	private static final int samplingFactor = 0;
	private BitSet randomFactors;
	private BitSet factors;
	/**
	 * True if the bracket terms for the current factorial data table have
	 * already been computed.
	 */
	private boolean statisticsComputed = false;
	private ArrayList analysisList = null;
	private ArrayList randomFactorList = null;

	/**
	 * Prepare the ANOVA engine for the given factorial data table. No
	 * computations are done during instantiation.
	 */
	public AnovaEngine(FactorialDataTable dt) {
		this(dt, "ANOVA Results", de.pxlab.pxl.StatPrintBitCodes.PRINT_NOTHING);
	}

	/**
	 * Prepare an ANOVA for the given factorial data table. No computations are
	 * done during instantiation.
	 */
	public AnovaEngine(FactorialDataTable dt, int printOption) {
		this(dt, "ANOVA Results", printOption);
	}

	public AnovaEngine(FactorialDataTable dt, String title, int printOption) {
		super(title, printOption);
		data = dt;
		nFactors = data.numberOfFactors();
		factorName = data.getFactorNames();
		nFactorLevels = data.numberOfFactorLevels();
		nSources = (1 << nFactors);
		factors = new BitSet(nSources - 1);
		randomFactors = new BitSet();
		/*
		 * System.out.println("AnovaEngine() Instantiated.");
		 * System.out.println("     title = " + title);
		 * System.out.println("     print = " + printOption);
		 * System.out.println("   Factors = " + StringExt.valueOf(factorName));
		 * System.out.println("  nFactors = " + nFactors);
		 * System.out.println("  nSources = " + nSources);
		 * System.out.println("   factors = " + factors); /*
		 * System.out.println("  data:\n" + data.toString()); /*
		 */
	}

	public int[] getFactorType() {
		if (!statisticsComputed) {
			if (!computeStatistics()) {
				return null;
			}
		}
		return factorType;
	}

	/**
	 * Compute the cell statistics. This essentially computes the bracket term
	 * for every possible source and also identifies which factors are between
	 * and which are within factors.
	 */
	public boolean computeStatistics() {
		int[] sourceMask = new int[nFactors];
		int[] non_sourceMask = new int[nFactors];
		int[] idx = new int[nFactors];
		// Contains the number of terms summed for this factor. This
		// must be equal to the number of within-levels of a factor.
		sumCount = new int[nFactors];
		boolean validData = true;
		bracketAt = new double[nFactors][];
		meanAt = new double[nFactors][];
		countAt = new int[nFactors][];
		for (int i = 0; i < nFactors; i++) {
			sumCount[i] = 0;
			bracketAt[i] = new double[nFactorLevels[i]];
			meanAt[i] = new double[nFactorLevels[i]];
			countAt[i] = new int[nFactorLevels[i]];
		}
		bracket = new double[nSources];
		double d;
		double sum = 0.0, sumsq = 0.0;
		int count = 0;
		String[] cms;
		StringDataTable cellMeans0 = new StringDataTable(200);
		if (dPr) {
			beginTable("Cell Statistics");
		}
		// we now cycle through all possible sources in order to compute the
		// bracket terms.
		for (int source = 0; source < nSources; source++) {
			bracket[source] = 0.0;
			BitSet sourceSet = new BitSet(source);
			int nTerms = sourceSet.size();
			// System.out.println("         source set [" + source + "] = " +
			// sourceSet);
			if (dPr)
				if (xxdPr || !sourceSet.containsElement(samplingFactor))
					printCellHeader(sourceSet);
			for (int i = 0; i < nFactors; i++) {
				if (sourceSet.containsElement(i)) {
					sourceMask[i] = -1;
					non_sourceMask[i] = 0;
				} else {
					sourceMask[i] = 0;
					non_sourceMask[i] = -1;
				}
			}
			// for all levels of all sources
			for (MaskedExpansionIterator si = new MaskedExpansionIterator(
					nFactorLevels, sourceMask); si.hasNext();) {
				// for all levels of non-'source'
				int[] sIdx = (int[]) (si.next());
				sum = 0.0;
				sumsq = 0.0;
				count = 0;
				// System.out.print("       source level: ");
				// printIndexArray(sIdx);
				for (MaskedExpansionIterator nsi = new MaskedExpansionIterator(
						nFactorLevels, non_sourceMask); nsi.hasNext();) {
					int[] nsIdx = (int[]) (nsi.next());
					// System.out.print("   non-source level: ");
					// printIndexArray(nsIdx);
					for (int i = 0; i < nFactors; i++) {
						idx[i] = sourceSet.containsElement(i) ? sIdx[i]
								: nsIdx[i];
					}
					// System.out.print("              level: ");
					// printIndexArray(idx);
					if (data.containsFactorLevel(idx)) {
						d = data.getFactorLevel(idx);
						sum += d;
						sumsq += d * d;
						count++;
					}
				}
				if (count > 0) {
					bracket[source] += sum * sum / count;
				} else {
					if ((nTerms == 2)
							&& (sourceSet.containsElement(samplingFactor))) {
						// count == 0 and nTerms == 2, thus there
						// exist levels of the sampling factor (==
						// subjects) which did not generate data for
						// the current level of this non-Random
						// factor. This means that this MUST be a
						// between factor
						betweenFactors = betweenFactors.union(new BitSet(
								source - 1));
					}
				}
				for (int i = samplingFactor; i < nFactors; i++) {
					if (sourceSet.containsElement(i) && nTerms == 1) {
						countAt[i][idx[i]] = count;
						sumCount[i] += count;
						if (count > 0) {
							meanAt[i][idx[i]] = sum / count;
							bracketAt[i][idx[i]] = sum * sum / count;
							// System.out.println("       bracketAt [" + i +
							// "][" + idx[i] + "] = " + bracketAt[i][idx[i]]);
						}
					}
				}
				if (!sourceSet.containsElement(samplingFactor)) {
					addPlotData(sourceSet, sIdx, count, sum, sumsq);
				}
				if (dPr) {
					if ((xxdPr && (count > 1))
							|| !sourceSet.containsElement(samplingFactor)) {
						append(nl + "<tr>");
						int f1 = samplingFactor + (xxdPr ? 0 : 1);
						for (int i = f1; i < nFactors; i++) {
							if (sourceSet.containsElement(i)) {
								append("<td>" + data.factorLevelName(i, idx[i])
										+ "</td>");
							} else {
								append("<td>&nbsp;</td>");
							}
						}
						printCellStatistics(count, sum, sumsq, sum * sum
								/ count);
					}
				}
				// System.out.println("AnovaEngine.computeStatistics() source = "
				// + sourceSet);
				if ((nSources > 2) && (source == nSources - 2)) {
					cms = new String[nFactors];
					for (int i = samplingFactor + 1; i < nFactors; i++) {
						cms[i - 1] = data.factorLevelName(i, idx[i]);
					}
					cms[nFactors - 1] = String.valueOf(sum / count);
					cellMeans0.add(cms);
					// System.out.println("AnovaEngine.computeStatistics() cms = "
					// + StringExt.valueOf(cms));
				}
			}
			// System.out.println("            bracket [" + source + "] = " +
			// bracket[source]);
			if (xdPr) {
				if (xxdPr || !sourceSet.containsElement(samplingFactor)) {
					printBracketStatistics(bracket[source]);
				}
			}
		}
		if (nSources > 2) {
			// System.out.println("AnovaEngine.computeStatistics() cellMeans StringDataTable: \n"
			// + cellMeans0);
			cellMeans = new FactorialDataTable(cellMeans0);
			// System.out.println("AnovaEngine.computeStatistics() cellMeans FactorialDataTable: \n"
			// + cellMeans);
		}
		if (dPr)
			endTable();
		/*
		 * console("countAt", countAt); console("meanAt", meanAt);
		 * console("bracketAt", bracketAt); console("sumCount", sumCount);
		 * console("bracket", bracket);
		 */
		withinLevels = 1;
		betweenLevels = 1;
		effectCount = nFactorLevels[samplingFactor];
		for (int factor = 0; factor < nFactors; factor++) {
			// System.out.println("AnovaEngine factor = " + factor);
			if (nFactorLevels[factor] <= 1) {
				if (aPr)
					append(nl + "<p>Too few levels of factor " + factor
							+ "</p>" + nl);
				System.out.println("Too few levels of factor " + factor);
				validData = validData && false;
			} else {
				if (betweenFactors.containsElement(factor)) {
					betweenLevels *= nFactorLevels[factor];
					// System.out.println("AnovaEngine betweenLevels = " +
					// betweenLevels + " at factor " + factor);
				} else {
					withinLevels *= nFactorLevels[factor];
					// System.out.println("AnovaEngine withinLevels = " +
					// withinLevels + " at factor " + factor);
				}
			}
		}
		factorType = new int[nFactors + 1];
		factorSamplingType = new int[nFactors + 1];
		factorType[samplingFactor] = RANDOM;
		factorSamplingType[samplingFactor] = RANDOM;
		for (int factor = samplingFactor + 1; factor < nFactors; factor++) {
			factorType[factor] = betweenFactors.containsElement(factor) ? BETWEEN
					: WITHIN;
			factorSamplingType[factor] = FIXED;
			if (sumCount[factor] != withinLevels) {
				if (aPr)
					append(nl + "<p>Unbalanced factor: " + factor + "</p>" + nl);
				System.out.println("Unbalanced factor: " + factor);
				validData = validData && false;
			}
		}
		if (randomFactorList != null && randomFactorList.size() > 0) {
			for (int i = 0; i < randomFactorList.size(); i++) {
				String f = (String) randomFactorList.get(i);
				int factor = StringExt.indexOf(f, factorName);
				if (factor >= 0) {
					factorSamplingType[factor] = RANDOM;
					randomFactors.enter(factor);
				}
			}
			System.out.println("  randomFactors = " + randomFactors);
		}
		factorType[nFactors] = DATA;
		factorSamplingType[nFactors] = DATA;
		if (dPr)
			summarizeDesign(withinLevels);
		statisticsComputed = true;
		return validData;
	}

	public void computeOverallTest() {
		if (!statisticsComputed) {
			if (!computeStatistics()) {
				return;
			}
		}
		boolean errorFound = false;
		_ssEffect = new double[nSources];
		_ssError = new double[nSources];
		_dfEffect = new int[nSources];
		_dfError = new int[nSources];
		if (rPr) {
			beginTable("Test Results");
			printTestResultHeader();
		}
		for (int source = 0; (source < nSources) && !errorFound; source += 2) {
			BitSet sourceSet = new BitSet(source);
			int nTerms = sourceSet.size();
			/*
			 * System.out.println("Source: " + source);
			 * System.out.println("  SourceSet: " + sourceSet);
			 * System.out.println("  Influences:");
			 */
			double ssEffect = 0.0;
			for (int subSource = 0; subSource <= source; subSource += 2) {
				BitSet subSourceSet = new BitSet(subSource);
				if (subSourceSet.subsetOf(sourceSet)) {
					double s = signedTerm(nTerms, subSourceSet,
							bracket[subSource]);
					ssEffect += s;
					// System.out.println("    " + subSourceSet + "  + " + s);
				}
			}
			if (almostZero(ssEffect)) {
				ssEffect = 0.0;
			}
			if (ssEffect < 0.0) {
				if (rPr)
					append(nl + "<tr><td>Invalid SS effect: " + ssEffect
							+ "</td></tr>" + nl);
				System.out.println("Invalid SS effect: " + ssEffect);
				errorFound = true;
			}
			_ssEffect[source] = ssEffect;
			int dfEffect = 1;
			boolean containsWithinFactor = false;
			for (int factor = samplingFactor + 1; factor < nFactors; factor++) {
				if (sourceSet.containsElement(factor)) {
					dfEffect *= (nFactorLevels[factor] - 1);
					containsWithinFactor = containsWithinFactor
							| (factorType[factor] == WITHIN);
				}
			}
			if (dfEffect <= 0) {
				if (rPr)
					append(nl + "<tr><td>Invalid df effect: " + dfEffect
							+ "</td></tr>" + nl);
				System.out.println("Invalid df effect: " + dfEffect);
				errorFound = true;
			}
			_dfEffect[source] = dfEffect;
			BitSet errorSet = new BitSet();
			// System.out.println("  ErrorSet . : " + errorSet);
			if (randomFactors.isEmpty()) {
				errorSet.enter(samplingFactor);
				// System.out.println("  ErrorSet .. : " + errorSet);
				errorSet = errorSet.union(betweenFactors);
				// System.out.println("  ErrorSet ... : " + errorSet);
			} else {
				errorSet.enter(samplingFactor);
				// System.out.println("  ErrorSet .. : " + errorSet);
				errorSet = errorSet.union(randomFactors);
				// System.out.println("  ErrorSet ... : " + errorSet);
				errorSet = errorSet.union(betweenFactors);
				// System.out.println("  ErrorSet .... : " + errorSet);
			}
			errorSet = errorSet.union(sourceSet);
			int nError = errorSet.size();
			/*
			 * System.out.println("  ErrorSet: " + errorSet);
			 * System.out.println("  Influences:");
			 */
			double ssError = 0.0;
			for (int subSource = 0; subSource < nSources; subSource++) {
				BitSet subSourceSet = new BitSet(subSource);
				if (subSourceSet.subsetOf(errorSet)) {
					if (betweenFactors.subsetOf(subSourceSet)) {
						double s = signedTerm(nError, subSourceSet,
								bracket[subSource]);
						ssError += s;
						// System.out.println("    " + subSourceSet + "  + " +
						// s);
					}
				}
			}
			if (ssError < 0.00000000001) {
				if (rPr)
					append(nl + "<tr><td>Invalid SS error: " + ssError
							+ "</td></tr>" + nl);
				System.out.println("Invalid SS error: " + ssError);
				errorFound = true;
			}
			_ssError[source] = ssError;
			int dfError = nFactorLevels[samplingFactor] - betweenLevels;
			for (int factor = samplingFactor + 1; factor < nFactors; factor++) {
				if (sourceSet.containsElement(factor)
						&& !betweenFactors.containsElement(factor)) {
					dfError *= (nFactorLevels[factor] - 1);
				}
			}
			if (dfError <= 0) {
				if (rPr)
					append(nl + "<tr><td>Invalid df error: " + dfError
							+ "</td><tr>" + nl);
				System.out.println("Invalid df error: " + dfError);
				errorFound = true;
			}
			_dfError[source] = dfError;
			if (errorFound)
				return;
			if (rPr)
				printTestResult(sourceSet, bracket[source], ssEffect, dfEffect,
						errorSet, bracket[source + 1], ssError, dfError,
						containsWithinFactor ? 0 : effectCount);
		}
		if (rPr)
			endTable();
	}

	public void computeAnalyses() {
		if (analysisList != null) {
			for (Iterator it = analysisList.iterator(); it.hasNext();) {
				Analysis analysis = (Analysis) it.next();
				// System.out.println("Analysis: " + analysis.toString());
				if (analysis.type == MAIN_EFFECT_CONTRAST) {
					if (betweenFactors.containsElement(analysis.factor)) {
						computeBetweenSubjectContrast(analysis);
					} else {
						computeWithinSubjectContrast(analysis);
					}
				} else if (analysis.type == SIMPLE_EFFECT) {
					computeSimpleEffect(analysis);
				} else if (analysis.type == LEVENE_MEANS_TEST) {
					if (nSources > 2) {
						computeLeveneMeansTest();
					} else {
						System.out
								.println("AnovaEngine: Can't run the Levene Means Test. It requires at least one non-random factor.");
					}
				} else {
					System.out.println("AnovaEngine: illegal analysis type "
							+ analysis.type);
				}
			}
		}
	}

	private void computeBetweenSubjectContrast(Analysis contrast) {
		if (rPr) {
			beginTable("Results for between subjects contrast on factor "
					+ contrast.factorName + ": "
					+ StringExt.valueOf(contrast.c));
			printTestResultHeader();
		}
		int error = nSources - 2;
		double ssEffect = 0.0;
		double ssc = 0.0;
		int k = nFactorLevels[contrast.factor];
		for (int i = 0; i < k; i++) {
			double c = contrast.c[i];
			ssc += c * c / countAt[contrast.factor][i];
			ssEffect += c * meanAt[contrast.factor][i];
		}
		ssEffect = ssEffect * ssEffect / ssc;
		// System.out.println("Error value for factor " + contrast.fn + " = " +
		// msError + "(df = " + dfError + ")");
		if (rPr) {
			printTestResult(BitSet.singleton(contrast.factor), ssEffect,
					ssEffect, 1, new BitSet(error), _ssError[error],
					_ssError[error], _dfError[error], 2 * effectCount / k);
			endTable();
		}
	}

	/**
	 * Here is how it is done: For every subject we compute the value of the
	 * contrast variable. < This is done by first computing for every level of
	 * the contrast (within) factor the mean over all non-contrast within
	 * factors. And from these means we compute the contrast for the respective
	 * subject.
	 */
	private void computeWithinSubjectContrast(Analysis contrast) {
		FactorialDataTable contrastData = data.derivedTable(contrast.factor,
				new LinearWeightFunction(contrast.c));
		// System.out.println(contrastData.toString());
		AnovaEngine ave = new AnovaEngine(contrastData,
				"Results for within subjects contrast on factor "
						+ contrast.factorName + ": "
						+ StringExt.valueOf(contrast.c), printOption);
		ave.computeStatistics();
		ave.computeOverallTest();
		if (rPr)
			append(ave.getResult());
	}

	private void computeSimpleEffect(Analysis simple) {
		FactorialDataTable simpleData = data.subTable(simple.mask);
		// console("factorNames", simpleData.getFactorNames());
		AnovaEngine ave = new AnovaEngine(simpleData,
				"Results for simple effects on factor " + simple.factorName
						+ " at level " + simple.factorLevelName, printOption);
		ave.computeStatistics();
		int e = nSources - 1;
		ave.computeSimpleTest(bracket[e], _dfError[e - 1], _ssError[e - 1]);
		/*
		 * if (analysisList != null) { for (Iterator it =
		 * analysisList.iterator(); it.hasNext(); ) { Analysis analysis =
		 * (Analysis)it.next(); // System.out.println("Analysis: " +
		 * analysis.toString()); if (analysis.type == MAIN_EFFECT_CONTRAST) { if
		 * (analysis.factor != simple.factor) { if
		 * (ave.betweenFactors.containsElement(analysis.factor)) {
		 * ave.computeBetweenSubjectContrast(analysis); } else {
		 * ave.computeWithinSubjectContrast(analysis); } } } } } /*
		 */
		if (rPr)
			append(ave.getResult());
	}

	public void computeSimpleTest(double brError, int dfError, double ssError) {
		if (rPr) {
			beginTable("Test Results");
			printTestResultHeader();
		}
		_ssEffect = new double[nSources];
		_ssError = new double[nSources];
		_dfEffect = new int[nSources];
		_dfError = new int[nSources];
		for (int source = 0; source < nSources; source += 2) {
			BitSet sourceSet = new BitSet(source);
			int nTerms = sourceSet.size();
			double ssEffect = 0.0;
			for (int subSource = 0; subSource <= source; subSource += 2) {
				BitSet subSourceSet = new BitSet(subSource);
				if (subSourceSet.subsetOf(sourceSet)) {
					ssEffect += signedTerm(nTerms, subSourceSet,
							bracket[subSource]);
				}
			}
			if (almostZero(ssEffect)) {
				ssEffect = 0.0;
			}
			_ssEffect[source] = ssEffect;
			int dfEffect = 1;
			boolean containsWithinFactor = false;
			for (int factor = samplingFactor + 1; factor < nFactors; factor++) {
				if (sourceSet.containsElement(factor)) {
					dfEffect *= (nFactorLevels[factor] - 1);
					containsWithinFactor = containsWithinFactor
							| (factorType[factor] == WITHIN);
				}
			}
			_dfEffect[source] = dfEffect;
			BitSet errorSet = new BitSet();
			errorSet.enter(samplingFactor);
			errorSet = errorSet.union(betweenFactors);
			errorSet = errorSet.union(sourceSet);
			_ssError[source] = ssError;
			_dfError[source] = dfError;
			if (rPr)
				printTestResult(sourceSet, bracket[source], ssEffect, dfEffect,
						errorSet, bracket[source + 1], ssError, dfError,
						containsWithinFactor ? 0 : effectCount);
		}
		if (rPr)
			endTable();
	}

	public void computeLeveneMeansTest() {
		int[] m = new int[nFactors];
		for (int i = 0; i < nFactors; i++)
			m[i] = -1;
		FactorialDataTable dataCopy = data.subSection(m);
		// System.out.println(dataCopy);
		int[] cm = new int[nFactors - 1];
		for (MaskedExpansionIterator si = new MaskedExpansionIterator(
				nFactorLevels, m); si.hasNext();) {
			int[] idx = (int[]) si.next();
			if (dataCopy.containsFactorLevel(idx)) {
				for (int j = 1; j < nFactors; j++)
					cm[j - 1] = idx[j];
				// System.out.println(StringExt.valueOf(cm));
				double x = Math.abs(dataCopy.getFactorLevel(idx)
						- cellMeans.getFactorLevel(cm));
				dataCopy.put(dataCopy.keyOf(idx), x);
			}
		}
		// System.out.println(dataCopy);
		AnovaEngine ave = new AnovaEngine(dataCopy,
				"Levene Means Test for Homogeneity of Variance",
				rPr ? de.pxlab.pxl.StatPrintBitCodes.PRINT_RESULTS
						: de.pxlab.pxl.StatPrintBitCodes.PRINT_NOTHING);
		ave.computeStatistics();
		ave.computeOverallTest();
		if (rPr)
			append(ave.getResult());
	}

	/** Only called if xPr is true. */
	private void printTestResultHeader() {
		append(nl + "<tr>");
		append("<td align=\"left\">Source</td>");
		if (xrPr)
			append("<td align=\"right\">[&nbsp;]</td>");
		append("<td align=\"right\">SS</td>" + "<td align=\"right\">df</td>"
				+ "<td align=\"right\">MS</td>" + "<td align=\"right\">F</td>"
				+ "<td align=\"right\">p</td>"
				+ "<td align=\"right\">R<sup>2</sup><sub>p</sub></td>"
				+ "<td align=\"right\">&omega;<sup>2</sup><sub>p</sub></td>"
				+ "</tr>" + nl);
		append(nl + "<tr><td colspan=\"");
		append(xrPr ? "9" : "8");
		append("\"><hr></td></tr>" + nl);
	}

	/** Only called if xPr is true. */
	private void printTestResult(BitSet sourceSet, double brEffect,
			double ssEffect, int dfEffect, BitSet errorSet, double brError,
			double ssError, int dfError, int effCnt) {
		double msEffect = ssEffect / dfEffect;
		double msError = ssError / dfError;
		double Fvalue = msEffect / msError;
		double p = FDist.probabilityOf(Fvalue, dfEffect, dfError);
		double partialEffectSize = ssEffect / (ssEffect + ssError);
		String oss = "&nbsp;";
		if (effCnt > 0) {
			double omegaSquared = (dfEffect * (Fvalue - 1.0))
					/ ((dfEffect * (Fvalue - 1.0)) + effCnt);
			if (omegaSquared < 0)
				omegaSquared = 0.0;
			oss = dfm(omegaSquared) /* + " [" + effCnt + "]" */;
		}
		// System.out.println(ssEffect + " " + _msError[error] + " " + Fvalue +
		// " " + p);
		append(nl + "<tr>");
		printSourceName(sourceSet);
		if (xrPr)
			append("<td align=\"right\">" + dfm(brEffect) + "</td>");
		append("<td align=\"right\">" + dfm(ssEffect) + "</td>"
				+ "<td align=\"right\">" + dfEffect + "</td>"
				+ "<td align=\"right\">" + dfm(msEffect) + "</td>"
				+ "<td align=\"right\">" + dfm(Fvalue) + "</td>"
				+ "<td align=\"right\">" + dfm(p) + "</td>"
				+ "<td align=\"right\">" + dfm(partialEffectSize) + "</td>"
				+ "<td align=\"right\">" + oss + "</td>" + "</tr>");
		append(nl + "<tr>");
		printErrorName(errorSet);
		if (xrPr)
			append("<td align=\"right\">" + dfm(brError) + "</td>");
		append("<td align=\"right\">" + dfm(ssError) + "</td>"
				+ "<td align=\"right\">" + dfError + "</td>"
				+ "<td align=\"right\">" + dfm(msError) + "</td>" + "</tr>");
		append(nl + "<tr><td>&nbsp;</td></tr>");
	}

	private double signedTerm(int n, BitSet sub, double x) {
		return (((n - sub.size()) % 2) == 0) ? x : -x;
	}

	private void printSourceName(BitSet sourceSet) {
		if (sourceSet.size() == 0) {
			append("<td>T</td>");
		} else {
			append("<td>");
			for (int factor = 1; factor < nFactors; factor++) {
				if (sourceSet.containsElement(factor)) {
					append(shortFactorName[factor]);
				}
			}
			append("</td>");
		}
	}

	private void printErrorName(BitSet errorSet) {
		append("<td>");
		for (int factor = 1; factor < nFactors; factor++) {
			if (errorSet.containsElement(factor)
					&& !betweenFactors.containsElement(factor)) {
				append(shortFactorName[factor]);
			}
		}
		append(shortFactorName[samplingFactor] + "/");
		for (int factor = 1; factor < nFactors; factor++) {
			if (errorSet.containsElement(factor)
					&& betweenFactors.containsElement(factor)) {
				append(shortFactorName[factor]);
			}
		}
		append("</td>");
	}

	private boolean nonProp() {
		int maxLevels = 0;
		for (int factor = 0; factor < nFactors; factor++) {
			if (nFactorLevels[factor] > maxLevels)
				maxLevels = nFactorLevels[factor];
		}
		int[][] count = new int[maxLevels][maxLevels];
		int[] level = new int[nFactors];
		int[] sourceMask = new int[nFactors];
		int[] non_sourceMask = new int[nFactors];
		int[] idx = new int[nFactors];
		if (betweenFactors.size() <= 1) {
			return false;
		}
		for (int source = 0; source < nSources; source += 2) {
			BitSet sourceSet = new BitSet(source);
			if (!sourceSet.subsetOf(betweenFactors) || (sourceSet.size() != 2)) {
				continue;
			}
			int fact1 = 0;
			int fact2 = 0;
			for (int factor = 0; factor < nFactors; factor++) {
				level[factor] = 0;
				if (sourceSet.containsElement(factor)) {
					if (fact1 != 0) {
						fact2 = factor;
					} else {
						fact1 = factor;
					}
				}
			}
			for (int lev1 = 0; lev1 < nFactorLevels[fact1]; lev1++) {
				for (int lev2 = 0; lev2 < nFactorLevels[fact2]; lev2++) {
					count[lev1][lev2] = 0;
				}
			}
			int k = 1;
			for (int i = 0; i < nFactors; i++) {
				if ((source & k) != 0) {
					sourceMask[i] = -1;
					non_sourceMask[i] = 0;
				} else {
					sourceMask[i] = 0;
					non_sourceMask[i] = -1;
				}
				k = (k << 1);
			}
			for (MaskedExpansionIterator si = new MaskedExpansionIterator(
					nFactorLevels, sourceMask); si.hasNext();) {
				int[] sIdx = (int[]) (si.next());
				for (MaskedExpansionIterator nsi = new MaskedExpansionIterator(
						nFactorLevels, non_sourceMask); nsi.hasNext();) {
					int[] nsIdx = (int[]) (nsi.next());
					k = 1;
					for (int i = 0; i < nFactors; i++) {
						idx[i] = ((source & k) != 0) ? sIdx[i] : nsIdx[i];
						k = (k << 1);
					}
					// System.out.print("              level: ");
					// printIndexArray(idx);
					if (data.containsFactorLevel(idx)) {
						count[level[fact1]][level[fact2]]++;
					}
				}
			}
			for (int lev1 = 0; lev1 < nFactorLevels[fact1]; lev1++) {
				double coeff = (double) count[lev1][0] / (double) count[0][0];
				for (int lev2 = 0; lev2 < nFactorLevels[fact2]; lev2++) {
					double test = (double) count[lev1][lev2]
							/ (double) count[0][lev2];
					if (!almostZero(test - coeff))
						return true;
				}
			}
		}
		return false;
	}

	/** ONly called if dPr is true. */
	private void summarizeDesign(int ndata) {
		beginTable("Design Information");
		append(nl + "<tr><td>Factor:</td>");
		for (int factor = 0; factor <= nFactors; factor++)
			append("<td>" + localName(factorName[factor]) + "</td>");
		append("</tr>");
		append(nl + "<tr><td>Levels:</td>");
		for (int factor = 0; factor < nFactors; factor++)
			append("<td>" + nFactorLevels[factor] + "</td>");
		append("<td>" + ndata + "</td>");
		append("</tr>");
		append(nl + "<tr><td>Effect:</td>");
		for (int factor = 0; factor <= nFactors; factor++) {
			switch (factorSamplingType[factor]) {
			case RANDOM:
				append("<td>Random</td>");
				break;
			case FIXED:
				append("<td>Fixed</td>");
				break;
			case DATA:
				append("<td>Data</td>");
				break;
			}
		}
		append("</tr>");
		append(nl + "<tr><td>Type:</td>");
		for (int factor = 0; factor <= nFactors; factor++) {
			switch (factorType[factor]) {
			case RANDOM:
				append("<td>Random</td>");
				break;
			case BETWEEN:
				append("<td>Between</td>");
				break;
			case WITHIN:
				append("<td>Within</td>");
				break;
			case DATA:
				append("<td>Data</td>");
				break;
			}
		}
		append("</tr>");
		endTable();
	}

	/*
	 * private void printIndexArray(int[] a) { System.out.print("["); for (int i
	 * = 0; i < a.length; i++) System.out.print(" " + a[i]);
	 * System.out.println("]"); }
	 */
	/** Only called if dPr is true. */
	private void printCellHeader(BitSet sourceSet) {
		StringBuffer src = new StringBuffer();
		// spans at least 5 columns
		append(nl + "<tr><td colspan=\"5\">Source:");
		int f1 = samplingFactor + (xxdPr ? 0 : 1);
		if (sourceSet.size() == 0) {
			append(" T");
			src.append("T");
		} else {
			for (int factor = f1; factor < nFactors; factor++) {
				if (sourceSet.containsElement(factor)) {
					append(" " + localName(factorName[factor]));
					src.append(localName(factorName[factor]));
				}
			}
		}
		append("</td></tr>");
		append(nl + "<tr>");
		for (int factor = f1; factor < nFactors; factor++) {
			append("<td>" + localName(factorName[factor]) + "</td>");
		}
		append("<td align=\"right\">N</td>");
		if (xdPr) {
			append("<td align=\"right\">Sum</td><td align=\"right\">SumSQ</td><td align=\"right\">["
					+ src + "]</td>");
		}
		append("<td align=\"right\">Mean</td><td align=\"right\">SD</td><td align=\"right\">SE</td></tr>"
				+ nl);
	}

	/** Only called if dPr is true. */
	private void printCellStatistics(int count, double sum, double sumsq,
			double bracket) {
		if (count > 0) {
			append("<td align=\"right\">" + count + "</td>");
			if (xdPr) {
				append("<td align=\"right\">" + dfm(sum)
						+ "</td><td align=\"right\">" + dfm(sumsq)
						+ "</td><td align=\"right\">" + dfm(bracket) + "</td>");
			}
			append("<td align=\"right\">" + dfm(sum / count) + "</td>");
			if (count > 1) {
				double sd = Math.sqrt((sumsq - sum * sum / count)
						/ (count - 1.0));
				double se = sd / Math.sqrt((double) count);
				append("<td align=\"right\">" + dfm(sd)
						+ "</td><td align=\"right\">" + dfm(se) + "</td>");
			} else {
				append("<td>Can't compute standard deviation</td>");
			}
		} else {
			append("<td>Empty cells are not allowed!</td>");
		}
		append("</tr>" + nl);
	}

	private void addPlotData(BitSet sourceSet, int[] sIdx, int count,
			double sum, double sumsq) {
		// StringBuffer fn = new StringBuffer(sourceSet.toString(nFactors) +
		// "_");
		StringBuffer fn = new StringBuffer(20);
		int lastFactor = 0;
		for (int factor = samplingFactor + 1; factor < nFactors; factor++) {
			if (sourceSet.containsElement(factor)) {
				lastFactor = factor;
			}
		}
		for (int factor = (samplingFactor + 1); factor < nFactors; factor++) {
			if (factor > (samplingFactor + 1))
				fn.append("_");
			if (factor == lastFactor) {
				fn.append("x");
			} else {
				if (sourceSet.containsElement(factor)) {
					fn.append(String.valueOf(sIdx[factor]));
				} else {
					fn.append("n");
				}
			}
		}
		StringBuffer d = new StringBuffer();
		d.append(String.valueOf(sIdx[lastFactor]));
		d.append(" "
				+ String.valueOf(data.factorLevelName(lastFactor,
						sIdx[lastFactor])));
		if (count > 0) {
			d.append(" " + dfm(sum / count));
			// System.out.println("   \'" + sum/count + "\' -> \'" +
			// dfm(sum/count) + "\'");
		}
		if (count > 1) {
			double sd = Math.sqrt((sumsq - sum * sum / count) / (count - 1.0));
			double se = sd / Math.sqrt((double) count);
			d.append(" " + dfm(sd) + " " + dfm(se));
		}
		addPlotDataLine(fn.toString(), d.toString());
		// System.out.println(" -> " + fn + ": " + d);
	}

	/** Only called of xrPr is true. */
	private void printBracketStatistics(double bracket) {
		append(nl + "<tr>");
		int f1 = samplingFactor + (xxdPr ? 0 : 1);
		for (int i = f1; i < nFactors; i++) {
			append("<td>&nbsp;</td>");
		}
		append("<td></td><td></td><td></td>");
		append("<td align=\"right\">" + dfm(bracket) + "</td></tr>");
	}

	/**
	 * Define the factor whose name is given as an argument to be a random
	 * effects factor instead of a fixed effects factor.
	 */
	private void setRandomFactor(String f) {
		if (randomFactorList == null) {
			randomFactorList = new ArrayList();
		}
		randomFactorList.add(f);
	}

	public void addAnalysis(int type, String a) {
		String f = "";
		StringTokenizer st = new StringTokenizer(a, " ,");
		if (type == MAIN_EFFECT_CONTRAST) {
			if (st.hasMoreTokens()) {
				f = st.nextToken();
			}
			ArrayList w = new ArrayList(10);
			try {
				while (st.hasMoreTokens()) {
					w.add(new Double(st.nextToken()));
				}
			} catch (NumberFormatException nfx) {
				System.out
						.println("Illegal contrast definition: \"" + a + "\"");
				System.out
						.println("  Correct format is : \"factorname c1 c2 ... cn\"");
				return;
			}
			int n = w.size();
			double[] c = new double[n];
			for (int i = 0; i < n; i++) {
				c[i] = ((Double) w.get(i)).doubleValue();
			}
			addLinearContrast(type, f, c);
		} else if (type == SIMPLE_EFFECT) {
			String fl = "";
			if (st.hasMoreTokens()) {
				f = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				fl = st.nextToken();
			}
			addSimpleEffect(type, f, fl);
		} else if (type == RANDOM_FACTOR) {
			if (st.hasMoreTokens()) {
				f = st.nextToken();
			}
			setRandomFactor(f);
		} else if (type == LEVENE_MEANS_TEST) {
			if (analysisList == null) {
				analysisList = new ArrayList(10);
			}
			analysisList.add(new Analysis(type));
		}
	}

	private void addLinearContrast(int type, String factor, double[] c) {
		int f = 0;
		if (type == MAIN_EFFECT_CONTRAST) {
			f = StringExt.indexOf(factor, factorName);
			if (f < 0) {
				System.out
						.println("AnovaEngine.addContrast() Unknown factor name: "
								+ factor + ". Can't compute contrast.");
				return;
			}
			if (f == 0) {
				System.out.println("AnovaEngine.addContrast() Factor name: "
						+ factor
						+ ". Can't compute contrast for random factor.");
				return;
			}
			int k = nFactorLevels[f];
			if (c.length != k) {
				System.out
						.println("AnovaEngine.addContrast() Length of contrast array ("
								+ c.length
								+ ") not equal to number of factor levels ("
								+ k + "). Can't compute contrast.");
				return;
			}
			double sc = 0;
			for (int i = 0; i < c.length; i++)
				sc += c[i];
			if (!almostZero(sc)) {
				System.out
						.println("AnovaEngine.addContrast() Illegal contrast array: "
								+ StringExt.valueOf(c)
								+ " does not add up to 0");
				return;
			}
			if (analysisList == null) {
				analysisList = new ArrayList(10);
			}
			analysisList.add(new Analysis(type, factor, f, c));
		}
	}

	private void addSimpleEffect(int type, String factor, String factorLevel) {
		if (type == SIMPLE_EFFECT) {
			int f = StringExt.indexOf(factor, factorName);
			if (f < 0) {
				System.out
						.println("AnovaEngine.addSimpleEffect() Unknown factor name: "
								+ factor + ". Can't compute simple effect.");
				return;
			}
			if (f == 0) {
				System.out
						.println("AnovaEngine.addSimpleEffect() Factor name: "
								+ factor
								+ ". Can't compute simple effect for random factor.");
				return;
			}
			int k = StringExt.indexOf(factorLevel,
					data.getFactorLevelNames()[f]);
			if (k < 0) {
				System.out.println("AnovaEngine.addSimpleEffect() Factor "
						+ factor + " does not have level " + factorLevel);
				return;
			}
			int[] mask = new int[nFactors];
			for (int i = 0; i < nFactors; i++)
				mask[i] = -1;
			mask[f] = k;
			if (analysisList == null) {
				analysisList = new ArrayList(10);
			}
			analysisList
					.add(new Analysis(type, factor, f, factorLevel, k, mask));
		} else {
			System.out
					.println("AnovaEngine.addSimpleEffect() Unknown effect type: "
							+ type);
			return;
		}
	}
	public class Analysis {
		protected int type;
		protected int factor;
		protected int factorLevel;
		protected String factorName;
		protected String factorLevelName;
		protected double[] c;
		protected int[] mask;

		public Analysis(int type) {
			this.type = type;
		}

		public Analysis(int type, String fn, int factor, double[] c) {
			this.type = type;
			this.factorName = fn;
			this.factor = factor;
			this.c = c;
		}

		public Analysis(int type, String fn, int factor, String fln,
				int factorLevel, int[] mask) {
			this.type = type;
			this.factorName = fn;
			this.factorLevelName = fln;
			this.factor = factor;
			this.factorLevel = factorLevel;
			this.mask = mask;
		}

		public String toString() {
			String s = "";
			switch (type) {
			case MAIN_EFFECT_CONTRAST:
				s = "[Main Effect Contrast: Factor=" + factorName + ", c[]="
						+ StringExt.valueOf(c) + "]";
				break;
			case SIMPLE_EFFECT:
				s = "[Simple Effect: Factor=" + factorName + " at level="
						+ factorLevelName + "]";
				break;
			case LEVENE_MEANS_TEST:
				s = "[Levene Means Test]";
				break;
			}
			return s;
		}
	}
}
