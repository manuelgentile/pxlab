package de.pxlab.stat;

import de.pxlab.util.*;
import de.pxlab.pxl.Debug;
import de.pxlab.pxl.Version;

/**
 * An abstract superclass for command line applications in the package
 * de.pxlab.stat.
 */
abstract public class StatCommand implements CommandLineOptionHandler {
	private String options = "k:d:t:pP:D:VM?!";
	protected String dataFileName = null;
	protected String outputFileName = null;
	protected int printOption = de.pxlab.pxl.StatPrintBitCodes.PRINT_DESCRIPTIVE
			| de.pxlab.pxl.StatPrintBitCodes.PRINT_RESULTS;
	private static String defaultPlotFileNamePrefix = "plot";
	protected String plotFileNamePrefix = null;
	protected String plotFileNameExtension = ".pld";
	protected boolean createPlotFiles = false;

	protected void parseOptions(String[] args) {
		if ((args != null) && (args.length > 0)) {
			CommandLineParser clp = new CommandLineParser(args, getOptions2()
					+ options, this);
			if (clp.hasMoreArgs()) {
				dataFileName = clp.getArg();
			}
			if (clp.hasMoreArgs()) {
				outputFileName = clp.getArg();
			}
		}
		if (dataFileName == null) {
			commandLineError(1, "Missing data file name");
		}
		if (plotFileNamePrefix == null) {
			if (outputFileName == null) {
				plotFileNamePrefix = defaultPlotFileNamePrefix;
			} else {
				plotFileNamePrefix = FileExt.getBase(outputFileName);
			}
		}
	}

	abstract protected String getOptions2();

	abstract protected boolean commandLineOption2(char c, String arg);

	abstract protected void showOptions2();

	// --------------------------------------------------------------------
	// Implementation of CommandLineOptionHandler
	// --------------------------------------------------------------------
	/** This method is called for every command line option found. */
	public void commandLineOption(char c, String arg) {
		if (!commandLineOption2(c, arg)) {
			switch (c) {
			case 'd':
				dataFileName = arg;
				break;
			case 't':
				outputFileName = arg;
				break;
			case 'p':
				createPlotFiles = true;
				break;
			case 'P':
				plotFileNamePrefix = arg;
				break;
			case 'k':
				printOption = StringExt.intValue(arg, 1);
				break;
			case 'D':
				Debug.add(arg);
				break;
			case 'M':
				Debug.startMemoryMonitor();
				break;
			case 'V':
				showVersion();
				System.exit(1);
				break;
			case '?':
				showOptions();
				System.exit(1);
				break;
			default:
				commandLineError(1, "Unknown option " + c);
				System.exit(1);
			}
		}
	}

	/**
	 * This method is called whenever an error is found in the command line
	 * options.
	 */
	public void commandLineError(int e, String s) {
		System.out.println("Command line error: " + s);
		showOptions();
	}

	/** This shows the PXLab version and build number. */
	private void showVersion() {
		System.out.println("PXLab Version " + Version.instance());
	}

	/** This shows the available command line options. */
	private void showOptions() {
		System.out.println("Usage: java " + this.getClass().getName()
				+ " [options] datafile");
		System.out.println("Options are:");
		showOptions2();
		if (options.indexOf('d') >= 0)
			System.out.println("   -d file  get data from \'file\'");
		if (options.indexOf('t') >= 0)
			System.out.println("   -t file  write results to \'file\'");
		if (options.indexOf('p') >= 0)
			System.out
					.println("   -p       create plot data files for gnuplot");
		if (options.indexOf('P') >= 0)
			System.out.println("   -P f     use f as plot file name prefix");
		if (options.indexOf('k') >= 0)
			System.out.println("   -k n     set output print level n");
		if (options.indexOf('D') >= 0)
			System.out
					.println("   -D code  set debugging option \'code\' (use \'?\' to get list)");
		if (options.indexOf('M') >= 0)
			System.out
					.println("   -M       start a continuously running memory monitor");
		if (options.indexOf('V') >= 0)
			System.out.println("   -V       show version number only");
		if (options.indexOf('?') >= 0)
			System.out.println("   -?       show this help text");
		if (options.indexOf('!') >= 0)
			System.out.println("   -!       show debug codes");
	}
	// --------------------------------------------------------------------
	// End of CommandLineOptionHandler implementation
	// --------------------------------------------------------------------
}
