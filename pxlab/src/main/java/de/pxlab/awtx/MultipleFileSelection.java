package de.pxlab.awtx;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A modal dialog which allows multiple file selection.
 * 
 * <p>
 * Known Bugs: This dialog needs a directory selection mechanism which also
 * works when file filters are used. Currently using file filters makes it
 * impossible to klick-select a directory since directories usually do not
 * satisfy the file filter.
 * 
 * <p>
 * Work around: do not use file filters or manually enter the directory name to
 * change the directory.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class MultipleFileSelection extends CancelOKButtonDialog implements
		ActionListener, FilenameFilter {
	private TextField directory;
	private Button upDir;
	private Choice fileType;
	private List list;
	private File currentDirectory;

	/**
	 * Create a multiple file selection dialog within the given initial path.
	 * 
	 * @param parent
	 *            the parent frame for this dialog.
	 * @param title
	 *            the frame title of the dialog.
	 * @param path
	 *            the initial directory to be shown.
	 */
	public MultipleFileSelection(Frame parent, String title, String path) {
		this(parent, title, path, null);
	}

	/**
	 * Create a multiple file selection dialog within the given initial path.
	 * 
	 * @param parent
	 *            the parent frame for this dialog.
	 * @param title
	 *            the frame title of the dialog.
	 * @param path
	 *            the initial directory to be shown.
	 * @param ext
	 *            if this is non-null then only files having this extension are
	 *            shown.
	 */
	public MultipleFileSelection(Frame parent, String title, String path,
			String ext) {
		super(parent, title);
		// setResizable(true);
		currentDirectory = new File(path);
		if (!currentDirectory.isDirectory()) {
			new RuntimeException("Not a directory: " + path);
		}
		directory = new TextField(currentDirectory.getPath(), 60);
		directory.addActionListener(this);
		fileType = new Choice();
		// fileType.addActionListener(this);
		if (ext != null) {
			String item = "*." + ext;
			fileType.add(item);
			fileType.select(item);
		}
		upDir = new Button("  Up  ");
		upDir.addActionListener(this);
		list = new List(18, true);
		list.addActionListener(this);
		updateList(path);
		// Create layout
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		Panel panel = new Panel(gbl);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		// Directory row
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridwidth = 1;
		panel.add(new Label(" Directory: "), gbc);
		panel.add(directory, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(0, 10, 10, 0);
		panel.add(upDir, gbc);
		// File type row
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridwidth = 1;
		panel.add(new Label(" Type: "), gbc);
		panel.add(fileType, gbc);
		// The list
		gbc.gridx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(list, gbc);
		add(panel, BorderLayout.CENTER);
		pack();
	}

	/** Update the file list for the given path. */
	private boolean updateList(String path) {
		File f = new File(path);
		if (f.isDirectory()) {
			currentDirectory = f;
			String[] files = currentDirectory.list(this);
			list.removeAll();
			for (int i = 0; i < files.length; i++) {
				File fd = new File(f, files[i]);
				String fn = fd.isDirectory() ? (files[i] + File.separator)
						: files[i];
				list.add(fn);
			}
			// setTitle(" " + path);
			directory.setText(path);
			return true;
		} else {
			return false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == upDir) {
			String parent = currentDirectory.getParent();
			if (parent != null) {
				updateList(parent);
			}
		} else if (source == directory) {
			String s = directory.getText();
			updateList(s);
		} else if (source == fileType) {
			// String s = fileType.getText();
			// updateList(s);
		} else if (source == list) {
			String s = e.getActionCommand();
			updateList(currentDirectory.getPath() + File.separator + s);
		}
	}

	public void addExtension(String ext) {
		String item = "*." + ext;
		fileType.add(item);
		fileType.select(item);
	}

	public boolean accept(File dir, String name) {
		String ext = fileType.getSelectedItem();
		if (ext == null)
			return true;
		ext = ext.substring(1);
		return name.endsWith(ext);
	}

	/** Return the selected file objects. */
	public File[] getSelectedFiles() {
		String[] names = list.getSelectedItems();
		File[] files = new File[names.length];
		for (int i = 0; i < names.length; i++) {
			files[i] = new File(currentDirectory, names[i]);
		}
		return files;
	}
}
