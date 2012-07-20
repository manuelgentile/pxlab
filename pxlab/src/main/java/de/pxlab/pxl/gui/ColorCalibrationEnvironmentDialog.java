package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.awtx.*;
import de.pxlab.util.*;
import de.pxlab.pxl.*;

public class ColorCalibrationEnvironmentDialog extends CloseButtonDialog {
	private Frame parent;
	private Panel infoPanel;

	public ColorCalibrationEnvironmentDialog(Frame f) {
		super(f, "Color Calibration Environment");
		parent = f;
		infoPanel = new Panel(new GridLayout(0, 2, 0, 0));
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.add(new Label("Current CIE White Point:", Label.RIGHT));
		ExParValue v = ExPar.CIEWhitePoint.getValue();
		infoPanel.add(new Label((v.length == 3) ? new PxlColor(v
				.getDoubleArray()).toString() : "not set", Label.LEFT));
		int type = Base.getDisplayDeviceType();
		if (!ExperimentalDisplayDevice.isFullSecondaryScreen(type)) {
			populatePanel(DisplayDevice.PRIMARY_SCREEN);
			if (ExperimentalDisplayDevice.isDualScreen(type)) {
				populatePanel(DisplayDevice.SECONDARY_SCREEN);
			}
		} else {
			populatePanel(DisplayDevice.SECONDARY_SCREEN);
		}
		pack();
		setVisible(true);
	}

	private void populatePanel(int t) {
		ColorDeviceTransform cdt = new ScreenColorTransform(t);
		String m = (t == DisplayDevice.PRIMARY_SCREEN) ? "Primary Screen"
				: "Secondary Screen";
		infoPanel.add(new Label(m, Label.RIGHT));
		infoPanel.add(new Label(" ", Label.LEFT));
		infoPanel.add(new Label("Device White Point:", Label.RIGHT));
		infoPanel.add(new Label(new PxlColor(cdt.getWhitePoint()).toString(),
				Label.LEFT));
		infoPanel.add(new Label("Red Primary:", Label.RIGHT));
		infoPanel.add(new Label(new PxlColor(cdt.getRedBasis()).toString(),
				Label.LEFT));
		infoPanel.add(new Label("Green Primary:", Label.RIGHT));
		infoPanel.add(new Label(new PxlColor(cdt.getGreenBasis()).toString(),
				Label.LEFT));
		infoPanel.add(new Label("Blue Primary:", Label.RIGHT));
		infoPanel.add(new Label(new PxlColor(cdt.getBlueBasis()).toString(),
				Label.LEFT));
		infoPanel.add(new Label("Red Gamma & Gain:", Label.RIGHT));
		infoPanel.add(new Label(StringExt.valueOf(cdt.getRedGammaPars(), 4),
				Label.LEFT));
		infoPanel.add(new Label("Green Gamma & Gain:", Label.RIGHT));
		infoPanel.add(new Label(StringExt.valueOf(cdt.getGreenGammaPars(), 4),
				Label.LEFT));
		infoPanel.add(new Label("Blue Gamma & Gain:", Label.RIGHT));
		infoPanel.add(new Label(StringExt.valueOf(cdt.getBlueGammaPars(), 4),
				Label.LEFT));
	}
}
