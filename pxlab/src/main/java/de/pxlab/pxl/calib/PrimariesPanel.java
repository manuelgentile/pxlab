package de.pxlab.pxl.calib;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;

public class PrimariesPanel extends NumbersPanel {
	public PrimariesPanel() {
		super();
		String[] rowLabel = { "Red", "Green", "Blue" };
		String[] columnLabel = { "Y", "x", "y" };
		setProperties(rowLabel, columnLabel);
	}

	/**
	 * Set this graph's internal data array. Called by the object which created
	 * this graph.
	 */
	public void setGammaData(ArrayList gammaData) {
		Object[] d;
		double[] data;
		if (gammaData.size() == 3) {
			for (int j = 0; j < 3; j++) {
				d = (Object[]) (gammaData.get(j));
				data = (double[]) (d[0]);
				for (int i = 0; i < 3; i++) {
					label[j][i].setText(df.format(data[i + 1]));
				}
			}
			repaint();
		}
	}
}
