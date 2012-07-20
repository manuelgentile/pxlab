package de.pxlab.pxl;

import java.io.*;

/**
 * Describes an experiment by the value of parameter ExperimentName in the
 * design file.
 * 
 * @version 0.1.2
 */
public class ExperimentDescriptor {
	private String path;
	private String designFile;
	private String label;

	/**
	 * Create a descriptor of the experiment with the given design file.
	 * 
	 * @param fd
	 *            the design file of the experiment.
	 */
	public ExperimentDescriptor(File fd) {
		path = fd.getParent();
		designFile = fd.getName();
		try {
			ExDesign design = new ExDesign(fd.getAbsolutePath(), null);
			design.initRuntimeContext();
			label = ExPar.ExperimentTitle.getString();
			if (label.equals("PXLab Experiment")) {
				label = designFile;
			}
		} catch (Exception ex) {
		}
	}

	public String getPath() {
		return path;
	}

	public String getFullPath() {
		return path + System.getProperty("file.separator") + designFile;
	}

	public String designFile() {
		return designFile;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return path + designFile;
	}
}
