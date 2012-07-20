package de.pxlab.pxl;

import java.io.*;
import java.util.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.parser.*;

/**
 * Static methods to read and execute files containing global parameter
 * assignments. These don't work for applets.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 07/29/03
 * 
 * 08/14/03 added handling of command line assignments, should work for applets
 * also.
 * 
 * 2004/11/10 lots of changes/fixes
 */
public class GlobalAssignments {
	private static String globalSystemAssignmentsFile = "system.pxd";
	private static ArrayList glf = new ArrayList(5);
	private static String commandLineOptions = null;

	/**
	 * Open a file dialog and load and execute a file containing global
	 * experimental parameter assignments.
	 * 
	 * @param title
	 *            is the title of the file dialog box.
	 */
	public static void load(String title) {
		if (Base.isApplication()) {
			LoadFileDialog fd = new LoadFileDialog(title, "pxd");
			String fn = fd.getPath();
			// System.out.println("GlobalAssignments.load() file " + fn);
			fd.dispose();
			if (fn != null)
				exec(fn);
		}
	}

	/**
	 * Execute all registered files containing global experimental parameter
	 * assignments. This method is called by ExPar.reset() whenever the global
	 * experimental parameter values are to be initialized.
	 */
	public static void exec() {
		// System.out.println("GlobalAssignments.exec()");
		if (Base.isApplication()) {
			execInit("pxlab.home", globalSystemAssignmentsFile);
			execInit("pxlab.local", globalSystemAssignmentsFile);
			String[] sp = ExPar.ScreenParameterFile.getStringArray();
			for (int i = 0; i < sp.length; i++)
				execInit("pxlab.local", sp[i]);
			for (Iterator it = glf.iterator(); it.hasNext();) {
				exec((String) (it.next()));
			}
		}
	}

	/**
	 * Execute initializing assignments contained in the given file.
	 * 
	 * @param property
	 *            the name of the PXLab property which contains the directory
	 *            where the file to be executed should be contained.
	 * @param fileName
	 *            the name of the file which contains the assignments we have to
	 *            execute.
	 */
	private static void execInit(String property, String fileName) {
		String dirName = Base.getProperty(property);
		if (dirName != null) {
			File f = new File(dirName + System.getProperty("file.separator")
					+ fileName);
			if (f.canRead()) {
				exec(f.getPath());
			}
		}
	}

	/**
	 * Add the given file to the list of global assignment files. It is not
	 * allowed to add a file twice.
	 * 
	 * @param path
	 *            the full path name of a file containing valid experimental
	 *            parameter assignments.
	 */
	public static void add(String path) {
		if (!(glf.contains(path)))
			glf.add(path);
	}

	/**
	 * Add a string which contains all those experimental parameter assignments
	 * which have been given in the command line or which have been generated
	 * from command line options. These will be executed by the
	 * execCommandLine() method.
	 * 
	 * @param p
	 *            contains a sequence of experimental parameter assignments.
	 */
	public static void addCommandLine(String p) {
		commandLineOptions = p;
	}

	/**
	 * Execeute the parameter assignments which have been added previously by
	 * the addCommandLine() method.
	 */
	public static void execCommandLine() {
		if (commandLineOptions == null)
			return;
		exec("Command Line", new StringReader(commandLineOptions));
	}

	/**
	 * Remove the given file from the list of global assignment files. It is not
	 * possible to remove 'system.pxd' from this list. The only way to not
	 * execute 'system.pxd' is to delete the file.
	 * 
	 * @param path
	 *            the full path name of a file containing valid experimental
	 *            parameter assignments.
	 */
	public static void remove(String path) {
		int i = glf.indexOf(path);
		if (i >= 0)
			glf.remove(i);
	}

	/**
	 * Execute the global experimental parameter assignments contained in the
	 * given file.
	 * 
	 * @param fn
	 *            the full path name of a file containing valid experimental
	 *            parameter assignments.
	 */
	public static void exec(String fn) {
		// System.out.println("GlobalAssignments.exec(String): " + fn);
		if (Base.isApplication()) {
			try {
				FileReader fr = new FileReader(fn);
				exec(fn, fr);
				fr.close();
			} catch (IOException iox) {
				new FileError("Can't open parameter file " + fn);
			}
		}
	}

	/**
	 * Execute the global experimental parameter assignments delivered by the
	 * given file reader.
	 * 
	 * @param src
	 *            a source description for the reader. This parameter is only
	 *            used for protocol and error messages.
	 * @param fr
	 *            a Reader which delivers the assignments using standard PXLab
	 *            parameter assignment syntax.
	 */
	public static void exec(String src, Reader fr) {
		// System.out.println("GlobalAssignments.exec(String, Reader)");
		Debug.show(Debug.FILES,
				"GlobalAssignments.exec(String, Reader): Read file " + src);
		// if (Base.isApplication()) {
		// ExDesignTreeParser pp = new ExDesignTreeParser(fr,
		// Base.getEncoding());
		ExDesignTreeParser pp = new ExDesignTreeParser(fr);
		try {
			ExDesignNode nd = new ExDesignNode(
					ExDesignNode.AssignmentGroupNode, 10);
			pp.createListOfAssignments(nd);
			Debug.show(Debug.FILES,
					"GlobalAssignments.exec(String, Reader): Closed file "
							+ src);
			ArrayList n = nd.getChildrenList();
			for (int i = 0; i < n.size(); i++) {
				// System.out.println("GlobalAssignments.exec() " +
				// (ExDesignNode)n.get(i));
				((ExDesignNode) n.get(i)).doAssignment();
			}
		} catch (ParseException e) {
			// System.out.println(e.getMessage());
			new SyntaxError(fixedParserErrorMessage(e, src));
		} catch (TokenMgrError tme) {
			// System.out.println(e.getMessage());
			new SyntaxError(fixedParserErrorMessage(tme, src));
		}
		// }
	}

	/**
	 * Create a more useful error message based on the parser's error message.
	 */
	private static String fixedParserErrorMessage(Throwable pex, String fn) {
		String m = pex.getMessage();
		String nl = System.getProperty("line.separator");
		if (m == null) {
			return "Unknown Syntax Error in File " + fn;
		} else {
			return "Syntax Error in File " + fn + nl
					+ m.replace((char) 13, ' ').replace((char) 10, ' ');
		}
	}
}
