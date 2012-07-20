package de.pxlab.stat;

import java.util.*;

import de.pxlab.util.*;

/**
 * Run descriptive statistics from the command line. This is a command line
 * fronthead for the StatisticsEngine class.
 * 
 * @version 0.1.0
 * @see StatisticsEngine
 */
public class Statistics extends StatCommand implements de.pxlab.pxl.StatCodes {
	private ArrayList analysisOptions;
	private int requestedStats = DEFAULT_STATS;
	private int column;

	public Statistics(String[] args) {
		analysisOptions = new ArrayList(5);
		parseOptions(args);
		StringDataTable d = new StringDataTable(dataFileName);
		if (d.getColumns() > 1) {
			int[] slct = new int[1];
			slct[0] = column;
			d = d.subTable(slct);
		}
		// System.out.println(d);
		StatisticsEngine engine = new StatisticsEngine(d.doubleTable(),
				requestedStats, printOption);
		/*
		 * if (analysisOptions != null && !analysisOptions.isEmpty()) { for
		 * (Iterator it = analysisOptions.iterator(); it.hasNext(); ) { String s
		 * = (String)it.next(); try {
		 * engine.addAnalysis(Integer.parseInt(s.substring(0, 1)),
		 * s.substring(1)); } catch (NumberFormatException nfx) {} } }
		 */
		engine.computeStatistics();
		if (createPlotFiles) {
			engine.printPlotData(".", plotFileNamePrefix, plotFileNameExtension);
		}
		engine.printResult(outputFileName);
	}

	public static void main(String[] args) {
		new Statistics(args);
	}

	protected String getOptions2() {
		return "c:s:S";
	}

	/** This method is called for every command line option found. */
	public boolean commandLineOption2(char c, String arg) {
		boolean r = true;
		switch (c) {
		case 'c':
			column = StringExt.intValue(arg, 0);
			break;
		case 's':
			setRequestedStats(arg);
			break;
		default:
			r = false;
		}
		return r;
	}

	/** This shows the available command line options. */
	protected void showOptions2() {
		System.out.println("   -c n      use column n of input data table");
		System.out.println("   -s t      add a statistic to be computed:");
		System.out.println("      n");
		System.out.println("      min");
		System.out.println("      max");
		System.out.println("      sum");
		System.out.println("      ssq");
		System.out.println("      mean");
		System.out.println("      geomean");
		System.out.println("      harmean");
		System.out.println("      mod");
		System.out.println("      q25");
		System.out.println("      med");
		System.out.println("      q50");
		System.out.println("      q75");
		System.out.println("      range");
		System.out.println("      avdev");
		System.out.println("      varco");
		System.out.println("      var");
		System.out.println("      std");
		System.out.println("      se");
		System.out.println("      skew");
		System.out.println("      kurt");
		System.out.println("      cum");
	}

	private void setRequestedStats(String arg) {
		if (arg.startsWith("n"))
			requestedStats = requestedStats | (N);
		else if (arg.startsWith("min"))
			requestedStats = requestedStats | (MINIMUM);
		else if (arg.startsWith("max"))
			requestedStats = requestedStats | (MAXIMUM);
		else if (arg.startsWith("sum"))
			requestedStats = requestedStats | (SUM);
		else if (arg.startsWith("ssq"))
			requestedStats = requestedStats | (SUM_OF_SQUARES);
		else if (arg.startsWith("mean"))
			requestedStats = requestedStats | (MEAN);
		else if (arg.startsWith("geomean"))
			requestedStats = requestedStats | (GEOMETRIC_MEAN);
		else if (arg.startsWith("harmean"))
			requestedStats = requestedStats | (HARMONIC_MEAN);
		else if (arg.startsWith("mod"))
			requestedStats = requestedStats | (MODE);
		else if (arg.startsWith("q25"))
			requestedStats = requestedStats | (QUANTILE_25);
		else if (arg.startsWith("med"))
			requestedStats = requestedStats | (MEDIAN);
		else if (arg.startsWith("q50"))
			requestedStats = requestedStats | (QUANTILE_50);
		else if (arg.startsWith("q75"))
			requestedStats = requestedStats | (QUANTILE_75);
		else if (arg.startsWith("range"))
			requestedStats = requestedStats | (RANGE);
		else if (arg.startsWith("avdev"))
			requestedStats = requestedStats | (AV_DEV);
		else if (arg.startsWith("varco"))
			requestedStats = requestedStats | (VAR_COEFF);
		else if (arg.startsWith("var"))
			requestedStats = requestedStats | (VARIANCE);
		else if (arg.startsWith("std"))
			requestedStats = requestedStats | (STDDEV);
		else if (arg.startsWith("se"))
			requestedStats = requestedStats | (STDERR);
		else if (arg.startsWith("skew"))
			requestedStats = requestedStats | (SKEW);
		else if (arg.startsWith("kurt"))
			requestedStats = requestedStats | (KURTOSIS);
		else if (arg.startsWith("cum"))
			requestedStats = requestedStats | (CUMULATIVE);
	}
}
