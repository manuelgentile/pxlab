package de.pxlab.pxl.display.editor;

import de.pxlab.util.*;

import de.pxlab.pxl.*;

/**
 * Handles display editor command line options and arguments. Command line
 * options are single letters which are preceded by a minus sign. Options may
 * have arguments which must be strings without blanks. Options may be followed
 * by a runtime parameter which specifies the initial display to be shown.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public class DisplayEditorOptionHandler implements CommandLineOptionHandler {
	private String options = "Di:ML:V?";
	private String ctrlName = null;
	private String initialDisplay = null;

	/**
	 * Analyze the given command line arguments.
	 * 
	 * @param cn
	 *            the name of the controlling object. This is used for error and
	 *            usage response messages only.
	 * @param args
	 *            the command line arguments as they are delivered by main().
	 */
	public DisplayEditorOptionHandler(String cn, String[] args) {
		// System.out.println("DisplayEditorOptionHandler()");
		ctrlName = cn;
		// First we deal with the command line options
		CommandLineParser clp = new CommandLineParser(args, options, this);
		// Debug.show(Debug.PROCEDURE, this, "Options parsed.");
		// Now we check for additional parameters
		if (clp.hasMoreArgs()) {
			// Remaining string ist initial display object name
			// while (clp.hasMoreArgs()) {
			initialDisplay = clp.getArg();
			// }
			// Debug.show(Debug.PROCEDURE, this, "Command line assignments: " +
			// runtimeAssignments);
		}
	}

	public String getInitialDisplay() {
		return initialDisplay;
	}

	// --------------------------------------------------------------------
	// CommandLineOptionHandler implementation
	// --------------------------------------------------------------------
	/**
	 * This ist the command line option handler which is called for every
	 * command line option found.
	 */
	public void commandLineOption(char c, String arg) {
		// System.out.println("DisplayEditorOptionHandler.commandLineOption(): -"
		// + c + " " + arg);
		switch (c) {
		case 'i':
			GlobalAssignments.add(arg);
			break;
		case 'D':
			Debug.setDebugLayout(true);
			break;
		case 'M':
			Debug.startMemoryMonitor();
			break;
		case 'L':
			Base.setDefaultLanguage(arg);
			break;
		case 'V':
			showVersion();
			System.exit(1);
			break;
		case '?':
			showOptions();
			System.exit(1);
			break;
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

	// --------------------------------------------------------------------
	// End of CommandLineOptionHandler implementation
	// --------------------------------------------------------------------
	/** This shows the PXLab version and build number. */
	private void showVersion() {
		System.out.println("PXLab Version " + Version.instance());
	}

	/** This shows the available command line options. */
	private void showOptions() {
		System.out.println("Usage: java " + ctrlName
				+ " [options] [initial display]");
		System.out.println("Options are:");
		System.out
				.println("   -i file  add file \'file\' to list of initialization files");
		System.out.println("   -L lang  set the default language");
		System.out
				.println("   -M       start a continuously running memory monitor");
		System.out.println("   -V       show version number only");
		System.out.println("   -?       show this help text");
		System.out
				.println("\"initial display\" is the Java class name of the first display to be shown. ");
	}
}
