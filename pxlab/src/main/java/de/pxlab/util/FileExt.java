package de.pxlab.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import de.pxlab.util.StringExt;

/**
 * Some file name utility methods.
 * 
 * @author Hans Irtel
 * @version 0.1.1
 */
public class FileExt {
	/**
	 * Rename the given file to a backup file. If a file with the same name
	 * already exists then it is deleted.
	 * 
	 * @param f
	 *            the file which should become a backup file.
	 */
	public static void backupFile(File f) {
		// System.out.println("Util.backupFile() getPath(): " + f.getPath());
		// System.out.println("Util.backupFile() getName(): " + f.getName());
		File bf = new File(getBase(f.getPath()) + ".bak");
		if (bf.exists()) {
			bf.delete();
		}
		// System.out.println("Util.backupFile() renames " + f + " to " + bf);
		f.renameTo(bf);
	}

	/**
	 * Get the file name extension of the given file name.
	 * 
	 * @param fn
	 *            the file name whose extension should be analyzed.
	 * @return a string containing the file name extension. If the file name
	 *         does not have an extension then null is returned.
	 */
	public static String getExtension(String fn) {
		int idx = fn.lastIndexOf('.');
		if (idx < 0)
			return null;
		return fn.substring(idx + 1);
	}

	/**
	 * Get the file name base of the given file name without extension.
	 * 
	 * @param fn
	 *            the file name whose extension should be removed.
	 * @return a string containing the file name without extension. If the file
	 *         name does not have an extension then the argument string is
	 *         returned.
	 */
	public static String getBase(String fn) {
		int idx = fn.lastIndexOf('.');
		if (idx < 0)
			return fn;
		return fn.substring(0, idx);
	}

	/**
	 * Find a file in a set of directories specified by a path list.
	 * 
	 * @param fn
	 *            the name of the file to find.
	 * @param pl
	 *            a string containing a path list like PATH or CLASSPATH.
	 * @return the full path of the file.
	 */
	public static File findFile(String fn, String pl) {
		ArrayList cp = de.pxlab.util.StringExt.pathList(pl);
		for (Iterator it = cp.iterator(); it.hasNext();) {
			String pn = ((String) (it.next()))
					+ System.getProperty("file.separator") + fn;
			File f = new File(pn);
			try {
				if (f.exists()) {
					return f;
				}
			} catch (SecurityException sex) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Get the directory part of the given file. We search for '\' and '/'
	 * characters here since we do not know whether the given string follows the
	 * correct operating system convention.
	 * 
	 * @param fn
	 *            the file name whose directory part should be returned.
	 * @return a string containing the directory part of the given file name. If
	 *         the file does not have a directory prefix then the string './' is
	 *         returned.
	 */
	public static String getDirectory(String fn) {
		int idx = fn.lastIndexOf('/');
		if (idx < 0)
			idx = fn.lastIndexOf('\\');
		if (idx < 0)
			return "./";
		return fn.substring(0, idx + 1);
	}
}
