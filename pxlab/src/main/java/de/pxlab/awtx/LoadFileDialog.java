package de.pxlab.awtx;

import java.awt.Frame;
import java.awt.FileDialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;

public class LoadFileDialog extends FileDialog implements FilenameFilter {
	private String extension;

	public LoadFileDialog(String title, String ext) {
		super(new Frame(), title, FileDialog.LOAD);
		// pack();
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		// Dimension ds = getSize();
		// System.out.println(ds);
		setLocation(ss.width / 4, ss.height / 4);
		extension = "." + ext;
		setFilenameFilter(this);
		show();
		// System.out.println("LoadFileDialog() Found: " + getPath());
	}

	public String getPath() {
		return (getDirectory() + getFile());
	}

	public boolean accept(File dir, String name) {
		return (name.endsWith(extension));
	}
}
