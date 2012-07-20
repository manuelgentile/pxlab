package de.pxlab.awtx;

import java.awt.Frame;
import java.awt.FileDialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;

public class SaveFileDialog extends FileDialog implements FilenameFilter {
	private String extension;

	public SaveFileDialog(String title, String fn, String ext) {
		super(new Frame(), title, FileDialog.SAVE);
		// pack();
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		// Dimension ds = getSize();
		// System.out.println(ds);
		setLocation(ss.width / 4, ss.height / 4);
		extension = "." + ext;
		setFilenameFilter(this);
		setFile(fn);
		show();
		// System.out.println("LoadFileDialog() Found: " + getPath());
	}

	public String getPath() {
		String fn = getFile();
		String fd = getDirectory();
		if (fn == null) {
			return null;
		} else {
			if (fd != null) {
				return fd + fn;
			} else {
				return fn;
			}
		}
	}

	public boolean accept(File dir, String name) {
		return (name.endsWith(extension));
	}
}
