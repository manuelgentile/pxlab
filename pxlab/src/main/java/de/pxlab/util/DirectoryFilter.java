package de.pxlab.util;

import java.io.*;

public class DirectoryFilter implements FilenameFilter {
	public boolean accept(File dir, String fn) {
		String pfn = dir.getPath() + fn;
		System.out.println("DirectoryFilter.accept() " + pfn + " ?");
		File fd = new File(pfn);
		return fd.isDirectory();
	}
}
