package de.pxlab.stat;

import java.util.*;

import de.pxlab.util.*;

/**
 * Run an ANOVA from the command line. This is a command line fronthead for the
 * AnovaEngine class.
 * 
 * @version 0.1.0
 * @see AnovaEngine
 */
public class Anova extends StatCommand {
	private boolean numLinOption = false;
	private ArrayList analysisOptions;

	public Anova(String[] args) {
		analysisOptions = new ArrayList(5);
		parseOptions(args);
		StringDataTable d = new StringDataTable(dataFileName, numLinOption);
		FactorialDataTable fd = new FactorialDataTable(d);
		fd.setFactorNames(fd.defaultFactorNames());
		AnovaEngine engine = new AnovaEngine(fd, printOption);
		if (analysisOptions != null && !analysisOptions.isEmpty()) {
			for (Iterator it = analysisOptions.iterator(); it.hasNext();) {
				String s = (String) it.next();
				try {
					engine.addAnalysis(Integer.parseInt(s.substring(0, 1)),
							s.substring(1));
				} catch (NumberFormatException nfx) {
				}
			}
		}
		engine.computeStatistics();
		engine.computeOverallTest();
		engine.computeAnalyses();
		engine.printResult(outputFileName);
		if (createPlotFiles) {
			engine.printPlotData(".", plotFileNamePrefix, plotFileNameExtension);
		}
	}

	public static void main(String[] args) {
		new Anova(args);
	}

	protected String getOptions2() {
		return "r:nc:s:h";
	}

	/** This method is called for every command line option found. */
	public boolean commandLineOption2(char c, String arg) {
		boolean r = true;
		switch (c) {
		case 'n':
			numLinOption = true;
			break;
		case 'r':
			analysisOptions
					.add(String.valueOf(AnovaEngine.RANDOM_FACTOR) + arg);
			break;
		case 'c':
			analysisOptions.add(String
					.valueOf(AnovaEngine.MAIN_EFFECT_CONTRAST) + arg);
			break;
		case 's':
			analysisOptions
					.add(String.valueOf(AnovaEngine.SIMPLE_EFFECT) + arg);
			break;
		case 'h':
			analysisOptions.add(String.valueOf(AnovaEngine.LEVENE_MEANS_TEST)
					+ " ");
			break;
		default:
			r = false;
		}
		return r;
	}

	/** This shows the available command line options. */
	protected void showOptions2() {
		System.out
				.println("   -n       add line number to every line of input data");
		System.out
				.println("   -r name  define factor 'name' as random effects factor");
		System.out
				.println("   -c contrast-definition define a linear contrast like \"B -1 0 1\"");
		System.out
				.println("   -s simple-effect-definition define a simple effect analysis \"B b1\"");
		System.out
				.println("   -h       Test for homogeneity of variances using the Levene means test");
	}
}
