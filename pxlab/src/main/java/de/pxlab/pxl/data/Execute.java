package de.pxlab.pxl.data;

import java.util.ArrayList;
import java.io.*;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.*;

/**
 * Execute an external command. If this object's data table is non-empty then it
 * is sent to the external command's standard input. The external command's
 * standard output is stored in this DataDisplay's output data file if it is
 * non-empty.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/06/12
 */
public class Execute extends DataDisplay {
	/** The external command to be executed. */
	public ExPar Command = new ExPar(STRING, new ExParValue(""),
			"External command to execute");
	/** An array of arguments to the command. */
	public ExPar Arguments = new ExPar(STRING, new ExParValue(""),
			"Arguments for the command");
	/** The working directory for the external command. */
	public ExPar WorkingDirectory = new ExPar(STRING, new ExParValue("/"),
			"Working directory for the command");
	/** Returns the exit value of the external command. */
	public ExPar ExitValue = new ExPar(RTDATA, new ExParValue(0),
			"Exit value of the process");
	/**
	 * If true then the external command's error stream is added to the output
	 * file. May be used for debugging.
	 */
	public ExPar ShowErrorStream = new ExPar(FLAG, new ExParValue(0),
			"Add error stream to output");

	public Execute() {
		setTitleAndTopic("Execute external command", Topics.DATA);
	}
	private ProcessBuilder processBuilder;
	private String data;
	private String result;

	protected int processDataTable(ExDesign exDesign, String[] p,
			ExParValueTable grandTable) {
		ExParValueTable table = null;
		data = null;
		result = null;
		if (grandTable.size() > 0) {
			if (p != null && p.length != 0) {
				// System.out.println("p.length = " + p.length);
				table = new ExParValueTable(grandTable.subTable(p));
			} else {
				table = grandTable;
			}
		}
		if ((table != null) && (table.size() > 0)) {
			data = table.toString();
		}
		execute(Command.getStringArray(), Arguments.getStringArray(),
				WorkingDirectory.getString());
		if (StringExt.nonEmpty(result)) {
			showResults(result);
		}
		return 0;
	}

	/**
	 * Check whether the given table is a valid data table for this analysis
	 * object.
	 * 
	 * @return always true since this method overruns the superclass data table
	 *         checking.
	 */
	protected boolean valid(ExParValueTable t) {
		return true;
	}

	private void execute(String[] cmd, String[] arg, String wd) {
		processBuilder = null;
		ArrayList a = new ArrayList();
		if (StringExt.nonEmpty(cmd[0])) {
			for (int i = 0; i < cmd.length; i++)
				if (StringExt.nonEmpty(cmd[i]))
					a.add(cmd[i]);
			for (int i = 0; i < arg.length; i++)
				if (StringExt.nonEmpty(arg[i]))
					a.add(arg[i]);
			processBuilder = new ProcessBuilder(a);
			if (StringExt.nonEmpty(wd)) {
				File dir = new File(wd);
				if (dir.isDirectory()) {
					processBuilder.directory(dir);
				}
			}
			if (ShowErrorStream.getFlag())
				processBuilder.redirectErrorStream(true);
		}
		if (processBuilder != null) {
			int exitValue = 0;
			StringBuilder s = new StringBuilder();
			try {
				Process process = processBuilder.start();
				// System.out.println("Execute.execute() External command started.");
				if (data != null) {
					PrintStream out = new PrintStream(process.getOutputStream());
					out.println(data);
					out.close();
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					s.append(line + "\n");
				}
				try {
					// System.out.print("Execute.run() waiting ...");
					exitValue = process.waitFor();
					// System.out.println("Finished!");
				} catch (InterruptedException iex) {
				}
			} catch (IOException iox) {
			}
			ExitValue.set(exitValue);
			result = s.toString();
		}
	}
}
