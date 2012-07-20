package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * A dialog which shows the current gamma function parameters.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ShowGammaParsDialog extends OKButtonDialog {
	private static NumberFormat dv = NumberFormat.getInstance(Locale.US);
	static {
		dv.setMaximumFractionDigits(6);
		dv.setGroupingUsed(false);
	}

	public ShowGammaParsDialog(Frame parent) {
		super(parent, " Current Gamma Parameters");
		Panel pep = new Panel(new GridLayout(0, 4, 10, 2));
		pep.add(new Label("Channel"));
		pep.add(new Label("Gamma"));
		pep.add(new Label("Gain"));
		pep.add(new Label("Offset"));
		ColorDeviceTransform device = PxlColor.getDeviceTransform();
		double[] r_pars = device.getRedGammaPars();
		pep.add(new Label("Red:", Label.RIGHT));
		pep.add(new Label(dv.format(r_pars[0])));
		pep.add(new Label(dv.format(r_pars[1])));
		pep.add(new Label(dv.format(r_pars[2])));
		double[] g_pars = device.getGreenGammaPars();
		pep.add(new Label("Green:", Label.RIGHT));
		pep.add(new Label(dv.format(g_pars[0])));
		pep.add(new Label(dv.format(g_pars[1])));
		pep.add(new Label(dv.format(g_pars[2])));
		double[] b_pars = device.getBlueGammaPars();
		pep.add(new Label("Blue:", Label.RIGHT));
		pep.add(new Label(dv.format(b_pars[0])));
		pep.add(new Label(dv.format(b_pars[1])));
		pep.add(new Label(dv.format(b_pars[2])));
		add(pep, BorderLayout.CENTER);
		pack();
	}
}
