package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.display.VisualGammaFields;

/**
 * A dialog for computing gamma function parameters and saving them in a file.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class VisualGammaParsDialog extends ButtonDialog {
	private static NumberFormat dv = NumberFormat.getInstance(Locale.US);
	static {
		dv.setMaximumFractionDigits(6);
		dv.setGroupingUsed(false);
	}
	private double[] r_pars = new double[2];
	private double[] g_pars = new double[2];
	private double[] b_pars = new double[2];
	private Button cancelButton;
	private Button saveButton;
	private Button okButton;

	public VisualGammaParsDialog(Frame parent, VisualGammaFields red,
			VisualGammaFields green, VisualGammaFields blue) {
		super(parent, " Compute gamma parameters from visual matching data");
		ActionListener buttonHandler = new ButtonHandler();
		cancelButton = new Button("  Cancel  ");
		cancelButton.addActionListener(buttonHandler);
		rightButtonPanel.add(cancelButton);
		saveButton = new Button("  Save  ");
		saveButton.addActionListener(buttonHandler);
		rightButtonPanel.add(saveButton);
		if (!Base.isApplication()) {
			saveButton.setEnabled(false);
		}
		okButton = new Button("  Apply  ");
		okButton.addActionListener(buttonHandler);
		rightButtonPanel.add(okButton);
		Panel pep = new Panel(new GridLayout(0, 3, 10, 2));
		pep.add(new Label("Channel"));
		pep.add(new Label("Gamma"));
		pep.add(new Label("Gain"));
		computeGamma(red, r_pars);
		pep.add(new Label("Red:", Label.RIGHT));
		pep.add(new Label(dv.format(r_pars[0])));
		pep.add(new Label(dv.format(r_pars[1])));
		computeGamma(green, g_pars);
		pep.add(new Label("Green:", Label.RIGHT));
		pep.add(new Label(dv.format(g_pars[0])));
		pep.add(new Label(dv.format(g_pars[1])));
		computeGamma(blue, b_pars);
		pep.add(new Label("Blue:", Label.RIGHT));
		pep.add(new Label(dv.format(b_pars[0])));
		pep.add(new Label(dv.format(b_pars[1])));
		add(pep, BorderLayout.CENTER);
		pack();
	}

	private void computeGamma(VisualGammaFields d, double[] pars) {
		double[] a = new double[6];
		double[] b = new double[6];
		int c = d.getChannel();
		a[0] = channel(c, d.PrimaryColor);
		a[1] = channel(c, d.Step1Color);
		a[2] = channel(c, d.Step2Color);
		a[3] = channel(c, d.Step3Color);
		a[4] = channel(c, d.Step4Color);
		a[5] = channel(c, d.Step5Color);
		b[0] = a[1];
		b[1] = a[2];
		b[2] = a[3];
		b[3] = a[4];
		b[4] = a[5];
		b[5] = channel(c, d.Step6Color);
		pars[0] = 2.4;
		pars[1] = 1.0;
		/*
		 * System.out.println("VisualGammaParsDialog.computeGamma() for channel "
		 * + c); System.out.println("Data: "); for (int i = 0; i < 6; i++) {
		 * System.out.println("a[] = " + a[i] + "  b[] = " + b[i]); }
		 */
		new VisualGammaEstimator().estimate(a, b, pars);
	}

	private int channel(int d, ExPar p) {
		Color rgb = p.getDevColor();
		if (d == 0)
			return rgb.getRed();
		else if (d == 1)
			return rgb.getGreen();
		return rgb.getBlue();
	}
	private class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Button b = (Button) e.getSource();
			if (b == cancelButton) {
				closeDialog();
			} else if (b == saveButton) {
				saveEstimates();
			} else if (b == okButton) {
				closeOKDialog();
			}
		}
	}

	private void closeOKDialog() {
		ColorDeviceTransform d = PxlColor.getDeviceTransform();
		d.setRedGammaPars(r_pars[0], r_pars[1]);
		d.setGreenGammaPars(g_pars[0], g_pars[1]);
		d.setBlueGammaPars(b_pars[0], b_pars[1]);
		closeDialog();
	}

	protected void closeDialog() {
		setVisible(false);
		dispose();
	}

	private void saveEstimates() {
		SaveFileDialog fd = new SaveFileDialog(" Save parameter estimates",
				"gammapars.pxd", "pxd");
		String fp = fd.getPath();
		try {
			PrintWriter p = new PrintWriter(new FileOutputStream(fp), true);
			print(p, r_pars, g_pars, b_pars);
			p.close();
		} catch (IOException iox) {
			new FileError("Error while writing parameters to file " + fp);
		}
	}

	private void print(PrintWriter p, double[] r, double[] g, double[] b) {
		p.println("\t// Measured on " + new java.util.Date());
		p.println("\tRedGammaPars = [" + r[0] + ", " + r[1] + ";");
		p.println("\tGreenGammaPars = [" + g[0] + ", " + g[1] + ";");
		p.println("\tBlueGammaPars = [" + b[0] + ", " + b[1] + ";");
	}
}
