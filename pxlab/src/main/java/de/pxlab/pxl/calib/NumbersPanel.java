package de.pxlab.pxl.calib;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;

public class NumbersPanel extends JPanel {
	protected JLabel[][] label;
	protected NumberFormat df;

	public NumbersPanel() {
		df = NumberFormat.getInstance(Locale.US);
		df.setMaximumFractionDigits(4);
		df.setGroupingUsed(false);
	}

	protected void setProperties(String[] rowLabel, String[] columnLabel) {
		setLayout(new GridLayout(rowLabel.length + 1, columnLabel.length + 1,
				10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// setBackground(Color.LIGHT_GRAY);
		label = new JLabel[rowLabel.length][columnLabel.length];
		add(new JLabel(" ", JLabel.LEFT));
		for (int i = 0; i < columnLabel.length; i++) {
			add(new JLabel(columnLabel[i], JLabel.CENTER));
		}
		for (int j = 0; j < rowLabel.length; j++) {
			add(new JLabel(rowLabel[j], JLabel.LEFT));
			for (int i = 0; i < columnLabel.length; i++) {
				add(label[j][i] = new JLabel("----------", JLabel.CENTER));
				label[j][i].setOpaque(true);
				label[j][i].setBackground(Color.WHITE);
			}
		}
	}
}
