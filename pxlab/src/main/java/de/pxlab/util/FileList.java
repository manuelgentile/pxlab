package de.pxlab.util;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;

/**
 * A list of files or directories starting from a given root. The directory tree
 * is recursively traversed such that all subdirectories are searched.
 * 
 * @version 0.2.0
 */
public class FileList extends ArrayList {
	private String[] extension = null;
	private long sizeLimit = 0L;
	public static final int DIR_LIST = 1;
	public static final int FILE_LIST = 2;
	private int fileType;

	public FileList(String path, String ext) throws FileNotFoundException {
		this(path, FILE_LIST, extensionArray(ext), 0L);
	}

	public FileList(String path, String[] ext) throws FileNotFoundException {
		this(path, FILE_LIST, ext, 0L);
	}

	public FileList(String path, int ft, String ext, long slmt)
			throws FileNotFoundException {
		this(path, ft, extensionArray(ext), slmt);
	}

	/**
	 * Create the list of files rooted in the given path and having the given
	 * extension or create a list of directories which contain these files.
	 * 
	 * @param path
	 *            the root path for all directories searched.
	 * @param ft
	 *            the type of file searched: directory (DIR_LIST) or data file
	 *            (FILE_LIST). If the file type is DIR_LIST then only those
	 *            directories are added to the list which contain at least a
	 *            single files which fits the extension.
	 * @param ext
	 *            an array of extensions of acceptable files.
	 * @param slmt
	 *            the minimum size of a file to be acceptable.
	 */
	public FileList(String path, int ft, String[] ext, long slmt)
			throws FileNotFoundException {
		fileType = (ft == FILE_LIST) ? FILE_LIST : ((ft == DIR_LIST) ? DIR_LIST
				: FILE_LIST);
		if ((ext != null) && (ext.length > 0) && (ext[0] != null))
			for (int i = 0; i < ext.length; i++)
				if (!ext[i].startsWith("."))
					ext[i] = "." + ext[i].toLowerCase();
		extension = ext;
		sizeLimit = slmt;
		File dir = new File(path);
		if (!dir.exists()) {
			throw new FileNotFoundException(path);
		}
		if (dir.isDirectory()) {
			// This is a directory so create its file list.
			new Finder(path);
		} else {
			// This is not a directory so check for a single file
			if (dir.isFile()) {
				if ((fileType == FILE_LIST) && isAcceptable(dir.getName())) {
					add(dir.getName());
				}
			}
		}
	}

	private static String[] extensionArray(String e) {
		String[] ext = null;
		if (e != null) {
			ext = new String[1];
			ext[0] = "." + e.toLowerCase();
		}
		return ext;
	}
	class Finder {
		public Finder(String path) {
			File dir = new File(path);
			String[] dirList = dir.list();
			String fSep = System.getProperty("file.separator");
			boolean lc;
			for (int i = 0; i < dirList.length; i++) {
				File fs = new File(dir, dirList[i]);
				if (fs.isFile()) {
					lc = (sizeLimit > 0) ? (fs.length() >= sizeLimit) : true;
					if (lc && isAcceptable(dirList[i])) {
						// System.out.println("Adding " + dirList[i]);
						if (fileType == DIR_LIST) {
							add(dir.getPath());
							break;
						} else {
							add(dir.getPath() + fSep + dirList[i]);
						}
					}
				}
			}
			for (int i = 0; i < dirList.length; i++) {
				File fs = new File(dir, dirList[i]);
				if (fs.isDirectory()) {
					new Finder(fs.getPath());
				}
			}
		}
	}

	private boolean isAcceptable(String fn) {
		if (extension == null || extension.length == 0 || extension[0] == null)
			return true;
		String fns = fn.toLowerCase();
		for (int i = 0; i < extension.length; i++)
			if (fns.length() >= extension[i].length())
				if (fns.endsWith(extension[i]))
					return true;
		return false;
	}
}
