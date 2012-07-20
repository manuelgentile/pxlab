package de.pxlab.util;

import java.io.*;

/** A file name filter for file extensions. */
public class FilenameExtensionFilter implements FilenameFilter {
	private String extension;

	/**
	 * Create a file name filter for the extension given as an argument.
	 * 
	 * @param extension
	 *            an extension string like 'pxd'.
	 */
	public FilenameExtensionFilter(String extension) {
		this.extension = "." + extension;
	}

	public boolean accept(File dir, String name) {
		boolean s = name.endsWith(extension);
		// System.out.println("Testing " + name + (s? " OK": " no"));
		return s;
	}
}
