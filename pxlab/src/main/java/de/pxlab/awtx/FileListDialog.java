package de.pxlab.awtx;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFileChooser;

import de.pxlab.util.FileList;
import de.pxlab.util.DirectoryFilter;

/**
 * A modal dialog which contains a center area for some information display and
 * a bottom row which contains a panel for adding buttons which are
 * right-aligned. Use <code>add(component,
    BorderLayout.CENTER)</code> to add the center component
 * and use <code>buttonPanel.add(button)</code> to add a button to this dialog's
 * button panel.
 * 
 * @author Hans Irtel
 * @version 0.1.1
 */
public class FileListDialog extends CancelOKButtonDialog {
	private Button dirButton;
	private Frame parent;
	private File directory;
	private FileList files;
	private List list;

	public FileListDialog(Frame parent, String title) {
		super(parent, title);
		this.parent = parent;
		ActionListener dirButtonHandler = new DirButtonHandler();
		dirButton = new Button(" Directory ");
		dirButton.addActionListener(dirButtonHandler);
		leftButtonPanel.add(dirButton);
		// list = new List();
		pack();
	}
	private class DirButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Button b = (Button) e.getSource();
			if (b == dirButton) {
				getDirectory();
			}
		}
	}

	private void getDirectory() {
		JFileChooser fd = new JFileChooser();
		fd.showOpenDialog(parent);
		fd.setMultiSelectionEnabled(true);
		File[] fls = fd.getSelectedFiles();
		for (int i = 0; i < fls.length; i++) {
			System.out.println("Selected: " + fls[i].getName());
		}
		/*
		 * if (fd.getFile() != null) { String pfn = fd.getDirectory() +
		 * fd.getFile(); directory = new File(pfn); setTitle(" Directory: " +
		 * pfn); try { files = new FileList(directory.getPath(), "pxd"); } catch
		 * (FileNotFoundException fnf) { } } fd.dispose();
		 */
	}
	/*
	 * private void getDirectory() { FileDialog fd = new FileDialog(parent,
	 * " Data Directory", FileDialog.LOAD); fd.setFilenameFilter(new
	 * DirectoryFilter()); fd.show(); if (fd.getFile() != null) { String pfn =
	 * fd.getDirectory() + fd.getFile(); directory = new File(pfn);
	 * setTitle(" Directory: " + pfn); try { files = new
	 * FileList(directory.getPath(), "pxd"); } catch (FileNotFoundException fnf)
	 * { } } fd.dispose(); }
	 */
}
