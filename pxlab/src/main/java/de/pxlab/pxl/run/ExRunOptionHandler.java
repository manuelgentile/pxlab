package de.pxlab.pxl.run;

import java.util.StringTokenizer;
import java.util.ArrayList;
import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * Handles PXLab experimental command line options and arguments. Command line
 * options are single letters which are preceded by a minus sign. Options may
 * have arguments which must be strings without blanks. Options may be followed
 * by commandLine parameter assignments, separated by colons. Here is and
 * example:
 * 
 * <pre>
 *    java Main -x -d default.pxl SubjectCode=\"Tom\"; TrialFactor=7;
 * </pre>
 * 
 * <p>
 * This command line contains two two options: <code>-x</code> tells the program
 * to run in full screen mode and <code>-d default.pxl</code> tells the program
 * to use the design file <code>default.pxl</code>. Following the command line
 * options are two parameter assignments. These are treated as if the following
 * two lines of text were contained at the end of the <code>Context()</code>
 * section of the design file:
 * 
 * <pre>
 * SubjectCode = &quot;Tom&quot;;
 * TrialFactor = 7;
 * </pre>
 * 
 * Note that the quotes must be escaped on the command line in order to prevent
 * the operating system from removing them. Command line assignments are
 * executed after all other assignments in the design file have been executed.
 * 
 * @author H. Irtel
 * @version 0.1.14
 */
/*
 * 
 * 07/24/02 added setDebugOption() to handle debugging options from the command
 * line
 * 
 * 11/17/03 added -F option for single runs
 * 
 * 2004/09/08 fixed -j option handling
 * 
 * 2005/01/04 option -T to echo the design file
 * 
 * 2005/01/04 option -R to set the screen refresh rate
 * 
 * 2005/07/13 option -L
 * 
 * 2005/09/28 option -H
 * 
 * 2005/12/16 option -t, -Z
 * 
 * 2008/03/14 options -K -k
 */
public class ExRunOptionHandler extends RuntimeOptionHandler implements
		CommandLineOptionHandler {
	private String options = "Hh:i:jJk:Ks:g:rxzw:d:f:p:t:D:E:PVR:S:TML:Z?";
	private String[] args = null;
	private String ctrlName = null;
	private String commandLineAssignments = null;
	private StringBuffer rtaBuffer;
	private Object[] nonOptionArgs = null;

	/**
	 * Analyze the given command line arguments.
	 * 
	 * @param cn
	 *            the name of the controlling object. This is used for error and
	 *            usage response messages only.
	 * @param args
	 *            the command line arguments as they are delivered by main().
	 */
	public ExRunOptionHandler(String cn, String[] args) {
		this(cn, args, null);
	}

	/**
	 * Analyze the given command line arguments.
	 * 
	 * @param cn
	 *            the name of the controlling object. This is used for error and
	 *            usage response messages only.
	 * @param cmdln
	 *            a string which contains the complete command line as it may be
	 *            delivered by an applet.
	 */
	public ExRunOptionHandler(String cn, String cmdln) {
		this(cn, null, cmdln);
	}

	/**
	 * Analyze the given command line arguments.
	 * 
	 * @param cn
	 *            the name of the controlling object. This is used for error and
	 *            usage response messages only.
	 * @param args
	 *            the command line arguments as they are delivered by main().
	 * @param cmdln
	 *            a string which contains the complete command line as it may be
	 *            delivered by an applet.
	 */
	public ExRunOptionHandler(String cn, String[] args, String cmdln) {
		if (cmdln == null) {
			this.args = args;
			if ((args != null) && (args.length > 0)
					&& StringExt.nonEmpty(args[0])) {
				StringBuffer cmdLine = new StringBuffer(args[0]);
				for (int i = 1; i < args.length; i++)
					cmdLine.append(" " + args[i]);
				ExPar.CommandLine.set(cmdLine.toString());
			}
		} else {
			ArrayList argList = new ArrayList();
			StringTokenizer st = new StringTokenizer(cmdln);
			while (st.hasMoreTokens()) {
				argList.add(st.nextToken());
			}
			this.args = StringExt.stringArrayOfList(argList);
			ExPar.CommandLine.set(cmdln);
		}
		ctrlName = cn;
		rtaBuffer = new StringBuffer(100);
	}

	public void evaluate() {
		if (args == null)
			return;
		// First we deal with the command line options
		CommandLineParser clp = new CommandLineParser(args, options, this);
		// Debug.show(Debug.PROCEDURE, this, "Options parsed.");
		// Now we check for parameter assignments
		if (clp.hasMoreArgs()) {
			ArrayList noa = new ArrayList();
			// Remaining strings are parameter assignments
			while (clp.hasMoreArgs()) {
				String s = clp.getArg();
				rtaBuffer.append(" " + s);
				noa.add(s);
			}
			// Debug.show(Debug.PROCEDURE, this, "Command line assignments: " +
			// commandLineAssignments);
			nonOptionArgs = noa.toArray();
		}
		commandLineAssignments = rtaBuffer.toString();
	}

	public String[] getNonOptionArgs() {
		if (nonOptionArgs != null) {
			String[] m = new String[nonOptionArgs.length];
			for (int i = 0; i < m.length; i++) {
				m[i] = (String) (nonOptionArgs[i]);
			}
			return m;
		} else {
			return null;
		}
	}

	/**
	 * Return the commandLine parameter assignments which follow the command
	 * line options.
	 * 
	 * @return a string which contains all commandLine parameter assignments
	 *         given on the command line.
	 */
	public String getCommandLineAssignments() {
		// System.out.println(commandLineAssignments);
		return (commandLineAssignments);
	}

	// --------------------------------------------------------------------
	// CommandLineOptionHandler implementation
	// --------------------------------------------------------------------
	/**
	 * This ist the command line option handler which is called for every
	 * command line option found.
	 */
	public void commandLineOption(char c, String arg) {
		// System.out.println("ExRunOptionHandler.commandLineOption(): -" + c +
		// " " + arg);
		switch (c) {
		case 's':
			rtaBuffer.append("SubjectCode=\"" + arg + "\";\n");
			break;
		case 'g':
			rtaBuffer.append("SubjectGroup=\"" + arg + "\";\n");
			break;
		case 'r':
			Base.setDisplayDeviceType(DisplayDevice.FRAMED_WINDOW);
			break;
		case 'x':
			Base.setDisplayDeviceType(DisplayDevice.FULL_SCREEN);
			break;
		case 'z':
			Base.setDisplayDeviceType(DisplayDevice.FULL_SECONDARY_SCREEN);
			break;
		case 'w':
			Base.setScreenWidth(arg);
			break;
		case 'h':
			Base.setScreenHeight(arg);
			break;
		case 'R':
			Base.setDisplayDeviceRefreshRate(arg);
			break;
		case 'S':
			Base.setDisplayDeviceType(arg);
			break;
		case 'T':
			Base.setPrintDesignFile(true);
			break;
		case 'E':
			Base.setEncoding(arg);
			break;
		case 'f':
			ExPar.DesignFileName.set(arg);
			break;
		case 'd':
			rtaBuffer.append("DataFileName=" + StringExt.quote(arg) + ";\n");
			break;
		case 't':
			Base.setProcessedDataFileName(arg);
			break;
		case 'i':
			GlobalAssignments.add(arg);
			break;
		case 'D':
			Debug.add(arg);
			break;
		case 'M':
			Debug.startMemoryMonitor();
			break;
		case 'L':
			Base.setDefaultLanguage(arg);
			break;
		case 'p':
			rtaBuffer
					.append("ProtocolFileName=" + StringExt.quote(arg) + ";\n");
			break;
		case 'P':
			rtaBuffer.append("RuntimeProtocol=1;\n");
			break;
		case 'j':
		case 'J':
			Base.setExportJavaClass(true);
			break;
		case 'k':
			Base.setExportText2(arg);
			break;
		case 'K':
			Base.setExportText(true);
			break;
		case 'H':
			Base.setExportHTML(true);
			break;
		case 'Z':
			Base.setProcessDataFile(true);
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
		System.out.println("PXLab Version " + Version.instance()
				+ " [Build count: " + Version.getBuild() + "]");
	}

	/** This shows the available command line options. */
	private void showOptions() {
		System.out.println("Usage: java " + ctrlName
				+ " [options] [assignments]");
		System.out.println("Options are:");
		if (options.indexOf('s') >= 0)
			System.out.println("   -s code  set subject code to \'code\'");
		if (options.indexOf('g') >= 0)
			System.out
					.println("   -g code  set subject group code to \'code\'");
		if (options.indexOf('r') >= 0)
			System.out.println("   -r       framed window display");
		if (options.indexOf('x') >= 0)
			System.out.println("   -x       full screen non-exclusive display");
		if (options.indexOf('z') >= 0)
			System.out.println("   -z       secondary screen display");
		if (options.indexOf('w') >= 0)
			System.out.println("   -w n     set window width to n pixels");
		if (options.indexOf('h') >= 0)
			System.out.println("   -h n     set window height to n pixels");
		if (options.indexOf('R') >= 0)
			System.out
					.println("   -R n     set display device refresh rate (Hz)");
		if (options.indexOf('S') >= 0)
			System.out
					.println("   -S n     set display device type or mode n (use \'?\' to get list)");
		if (options.indexOf('f') >= 0)
			System.out.println("   -f file  use design file \'file\'");
		if (options.indexOf('d') >= 0)
			System.out
					.println("   -d file  write formatted data to file \'file\'");
		if (options.indexOf('t') >= 0)
			System.out
					.println("   -t file  write processed data to file \'file\'");
		if (options.indexOf('i') >= 0)
			System.out
					.println("   -i file  add file \'file\' to list of initialization files");
		if (options.indexOf('p') >= 0)
			System.out
					.println("   -p file  use protocol output data file \'file\'");
		if (options.indexOf('E') >= 0)
			System.out
					.println("   -E enc   use input file encoding 'enc' (default="
							+ System.getProperty("file.encoding") + ")");
		if (options.indexOf('k') >= 0)
			System.out
					.println("   -k file  pretty print design file to a file");
		if (options.indexOf('K') >= 0)
			System.out.println("   -K       reformat design file");
		if (options.indexOf('J') >= 0)
			System.out
					.println("   -J       convert design file to a Java source file");
		if (options.indexOf('H') >= 0)
			System.out
					.println("   -H       convert design file to a HTML text file");
		if (options.indexOf('D') >= 0)
			System.out
					.println("   -D code  set debug option \'code\' (use \'?\' to get list)");
		if (options.indexOf('L') >= 0)
			System.out
					.println("   -L lang  set the default language ('english', 'deutsch' or something else)");
		if (options.indexOf('M') >= 0)
			System.out
					.println("   -M       start a continuously running memory monitor");
		if (options.indexOf('P') >= 0)
			System.out
					.println("   -P       print runtime node protocol to standard output");
		if (options.indexOf('Z') >= 0)
			System.out.println("   -Z       process data file");
		if (options.indexOf('V') >= 0)
			System.out.println("   -V       show version number only");
		if (options.indexOf('?') >= 0)
			System.out.println("   -?       show this help text");
		System.out
				.println("Assignments are assignments of values to experimental parameters. ");
		System.out.println("Assignments must be closed by a ';'-character.");
		System.out
				.println("Quote characters in assignments must be escaped by the '\\'-character.");
	}
}
