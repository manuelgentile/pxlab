package de.pxlab.pxl.calib;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;

public class ParametersPanel extends NumbersPanel {
	public ParametersPanel() {
		super();
		String[] rowLabel = { "Red", "Green", "Blue" };
		String[] columnLabel = { "gamma", "scale" };
		setProperties(rowLabel, columnLabel);
	}

	/**
	 * Set this graph's internal gamma parameter value array. Called by the
	 * object which created this graph.
	 */
	public void setGammaPars(ArrayList gammaPars) {
		double[] pars;
		if (gammaPars.size() == 3) {
			for (int j = 0; j < 3; j++) {
				pars = (double[]) (gammaPars.get(j));
				for (int i = 0; i < 2; i++) {
					label[j][i].setText(df.format(pars[i]));
				}
			}
			repaint();
		}
	}
}
