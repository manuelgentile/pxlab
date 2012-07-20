package de.pxlab.pxl.gui;

import de.pxlab.pxl.data.*;

/**
 * The list of all available data processing objects.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/03/08
 */
public class BigDataProcessingList extends DisplayCollection {
	public BigDataProcessingList() {
		super(de.pxlab.pxl.Topics.DATA);
		add(new Anova());
		add(new EllipseEstimation());
		add(new Export());
		add(new TransformColors());
		add(new PsychometricFunction());
		add(new Regression());
		add(new Statistics());
		add(new VisualGammaEstimation());
		trimToSize();
	}
}
