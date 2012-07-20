package de.pxlab.stat;

import java.util.*;

import de.pxlab.util.*;
import de.pxlab.pxl.PsychometricFunctionCodes;
import de.pxlab.pxl.MinimizationFunctionCodes;

/**
 * Run psychometric function parameter estimation from the command line. This is
 * a command line fronthead for the PsychometricFunctionEngine class.
 * 
 * @version 0.1.0
 * @see PsychometricFunctionEngine
 */
public class PMF extends StatCommand {
	private ArrayList analysisOptions;
	private int functionType = PsychometricFunctionCodes.LOGISTIC;
	private int minimizationType = MinimizationFunctionCodes.CHI_SQUARE;
	private String yesCode = "2";
	private double[] fixedPar;
	private double[] setPar;

	public PMF(String[] args) {
		setPar = new double[2];
		setPar[0] = -1.0;
		setPar[1] = -1.0;
		fixedPar = new double[2];
		fixedPar[0] = 0.0;
		fixedPar[1] = 0.0;
		parseOptions(args);
		StringDataTable d = new StringDataTable(dataFileName);
		FactorialFrequencyTable fd = new FactorialFrequencyTable(d);
		fd.setFactorNames(fd.defaultFactorNames());
		// System.out.println(fd.toString());
		PsychometricFunctionEngine engine = new PsychometricFunctionEngine(fd,
				functionType, minimizationType, yesCode, fixedPar, printOption);
		/*
		 * if (analysisOptions != null && !analysisOptions.isEmpty()) { for
		 * (Iterator it = analysisOptions.iterator(); it.hasNext(); ) { String s
		 * = (String)it.next(); try {
		 * engine.addAnalysis(Integer.parseInt(s.substring(0, 1)),
		 * s.substring(1)); } catch (NumberFormatException nfx) {} } }
		 */
		if ((setPar[0] >= 0.0) && (setPar[1] >= 0.0)) {
			engine.setResults(setPar, true);
		} else {
			engine.estimate();
		}
		engine.printResult(outputFileName);
		if (createPlotFiles) {
			engine.printPlotData(".", plotFileNamePrefix, plotFileNameExtension);
		}
	}

	public static void main(String[] args) {
		new PMF(args);
	}

	protected String getOptions2() {
		return "a:c:g:l:y:f:m:";
	}

	/** This method is called for every command line option found. */
	public boolean commandLineOption2(char c, String arg) {
		boolean r = true;
		switch (c) {
		case 'c':
			setPar[0] = StringExt.doubleValue(arg, -1.0);
			break;
		case 'a':
			setPar[1] = StringExt.doubleValue(arg, -1.0);
			break;
		case 'g':
			fixedPar[0] = StringExt.doubleValue(arg, 0.0);
			break;
		case 'l':
			fixedPar[1] = StringExt.doubleValue(arg, 0.0);
			break;
		case 'y':
			yesCode = arg;
			break;
		case 'f':
			functionType = funType(arg);
			break;
		case 'm':
			minimizationType = minType(arg);
			break;
		default:
			r = false;
		}
		return r;
	}

	private int funType(String s) {
		String n = s.toLowerCase();
		int t = PsychometricFunctionCodes.LOGISTIC;
		if (n.startsWith("l")) {
		} else if (n.startsWith("w")) {
			t = PsychometricFunctionCodes.WEIBULL;
		} else if (n.startsWith("g")) {
			t = PsychometricFunctionCodes.GUMBEL;
		}
		return t;
	}

	private int minType(String s) {
		String n = s.toLowerCase();
		int t = MinimizationFunctionCodes.CHI_SQUARE;
		if (n.startsWith("c")) {
		} else if (n.startsWith("li")) {
			t = MinimizationFunctionCodes.LOG_LIKELIHOOD;
		} else if (n.startsWith("lo")) {
			t = MinimizationFunctionCodes.SQUARED_DEV_LOGIT;
		} else if (n.startsWith("sq")) {
			t = MinimizationFunctionCodes.SQUARED_DEV;
		}
		return t;
	}

	/** This shows the available command line options. */
	protected void showOptions2() {
		System.out.println("   -g x     set guessing rate to x");
		System.out.println("   -l x     set lapsing rate to x ");
		System.out.println("   -y s     assume s as the \'yes\' response code");
		System.out
				.println("   -f s     use function type s (\'Logistic\', \'Weibull\', \'Gumbel\')");
		System.out
				.println("   -m s     use minimization method s (\'chisquare\', \'likelihood\', \'logit\')");
		System.out
				.println("   -c x     don\'t estimate, but set shift parameter to x");
		System.out
				.println("   -a x     don\'t estimate, but set steepness parameter to x");
	}
}
