package de.pxlab.pxl.calib;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;

public class NumberPanel extends JPanel {
	private JLabel rY, rx, ry, rg, ra;
	private JLabel gY, gx, gy, gg, ga;
	private JLabel bY, bx, by, bg, ba;
	private NumberFormat df;

	public NumberPanel() {
		super(new GridLayout(4, 6, 10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		df = NumberFormat.getInstance(Locale.US);
		df.setMaximumFractionDigits(4);
		df.setGroupingUsed(false);
		// setBackground(Color.LIGHT_GRAY);
		add(new JLabel(" ", JLabel.LEFT));
		add(new JLabel("Y", JLabel.CENTER));
		add(new JLabel("x", JLabel.CENTER));
		add(new JLabel("y", JLabel.CENTER));
		add(new JLabel("Gamma", JLabel.CENTER));
		add(new JLabel("Shift", JLabel.CENTER));
		add(new JLabel("Red", JLabel.LEFT));
		add(rY = new JLabel("-------", JLabel.RIGHT));
		add(rx = new JLabel("-------", JLabel.RIGHT));
		add(ry = new JLabel("-------", JLabel.RIGHT));
		add(rg = new JLabel("-------", JLabel.RIGHT));
		add(ra = new JLabel("-------", JLabel.RIGHT));
		add(new JLabel("Green", JLabel.LEFT));
		add(gY = new JLabel("-------", JLabel.RIGHT));
		add(gx = new JLabel("-------", JLabel.RIGHT));
		add(gy = new JLabel("-------", JLabel.RIGHT));
		add(gg = new JLabel("-------", JLabel.RIGHT));
		add(ga = new JLabel("-------", JLabel.RIGHT));
		add(new JLabel("Blue", JLabel.LEFT));
		add(bY = new JLabel("-------", JLabel.RIGHT));
		add(bx = new JLabel("-------", JLabel.RIGHT));
		add(by = new JLabel("-------", JLabel.RIGHT));
		add(bg = new JLabel("-------", JLabel.RIGHT));
		add(ba = new JLabel("-------", JLabel.RIGHT));
	}

	/**
	 * Set this graph's internal data array. Called by the object which created
	 * this graph.
	 */
	public void setGammaData(ArrayList gammaData) {
		Object[] d;
		double[] data;
		if (gammaData.size() == 3) {
			d = (Object[]) (gammaData.get(0));
			data = (double[]) (d[0]);
			rY.setText(df.format(data[1]));
			rx.setText(df.format(data[2]));
			ry.setText(df.format(data[3]));
			d = (Object[]) (gammaData.get(1));
			data = (double[]) (d[0]);
			gY.setText(df.format(data[1]));
			gx.setText(df.format(data[2]));
			gy.setText(df.format(data[3]));
			d = (Object[]) (gammaData.get(2));
			data = (double[]) (d[0]);
			bY.setText(df.format(data[1]));
			bx.setText(df.format(data[2]));
			by.setText(df.format(data[3]));
			repaint();
		}
	}

	/**
	 * Set this graph's internal gamma parameter value array. Called by the
	 * object which created this graph.
	 */
	public void setGammaPars(ArrayList gammaPars) {
		double[] pars;
		if (gammaPars.size() == 3) {
			pars = (double[]) (gammaPars.get(0));
			rg.setText(df.format(pars[0]));
			ra.setText(df.format(pars[1]));
			pars = (double[]) (gammaPars.get(1));
			gg.setText(df.format(pars[0]));
			ga.setText(df.format(pars[1]));
			pars = (double[]) (gammaPars.get(2));
			bg.setText(df.format(pars[0]));
			ba.setText(df.format(pars[1]));
			repaint();
		}
	}
}
