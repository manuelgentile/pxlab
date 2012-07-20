package de.pxlab.stat;

import java.util.*;

import de.pxlab.util.*;

/**
 * Run a linear regression from the command line. This is a command line
 * fronthead for the LinearRegressionEngine class.
 * 
 * @version 0.1.0
 * @see LinearRegressionEngine
 */
public class Regression extends StatCommand {
	private ArrayList analysisOptions;
	private String functionName = "Reg";

	public Regression(String[] args) {
		analysisOptions = new ArrayList(5);
		parseOptions(args);
		StringDataTable d = new StringDataTable(dataFileName);
		LinearRegressionEngine engine = new LinearRegressionEngine(
				d.doubleTable(), functionName, printOption);
		/*
		 * if (analysisOptions != null && !analysisOptions.isEmpty()) { for
		 * (Iterator it = analysisOptions.iterator(); it.hasNext(); ) { String s
		 * = (String)it.next(); try {
		 * engine.addAnalysis(Integer.parseInt(s.substring(0, 1)),
		 * s.substring(1)); } catch (NumberFormatException nfx) {} } }
		 */
		engine.computeStatistics();
		engine.computeRegression();
		engine.printResult(outputFileName);
		if (createPlotFiles) {
			engine.printPlotData(".", plotFileNamePrefix, plotFileNameExtension);
		}
	}

	public static void main(String[] args) {
		new Regression(args);
	}

	protected String getOptions2() {
		return "r:";
	}

	/** This method is called for every command line option found. */
	public boolean commandLineOption2(char c, String arg) {
		boolean r = true;
		switch (c) {
		case 'r':
			functionName = arg;
			break;
		default:
			r = false;
		}
		return r;
	}

	/** This shows the available command line options. */
	protected void showOptions2() {
		System.out.println("   -r name  set output function name to 'name'");
	}
}
