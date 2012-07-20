package de.pxlab.pxl.gui;

import java.awt.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

public class TimingEnvironmentDialog extends CloseButtonDialog {
	private Frame parent;

	public TimingEnvironmentDialog(Frame f) {
		super(f, "Timing Environment");
		parent = f;
		Panel infoPanel = new Panel(new GridLayout(0, 2, 0, 0));
		add(infoPanel, BorderLayout.CENTER);
		infoPanel.add(new Label("PXLab High Resolution Timer:", Label.RIGHT));
		infoPanel.add(new Label(HiresClock.getTimerName(), Label.LEFT));
		int cpg = HiresClock.getChecksPerTimerGranularityStep();
		infoPanel.add(new Label("Resolution:", Label.RIGHT));
		infoPanel.add(new Label(((cpg > 1) ? "better than " : "")
				+ String.valueOf(HiresClock.getTimerGranularity()) + " ms",
				Label.LEFT));
		infoPanel.add(new Label("Checks per Step:", Label.RIGHT));
		infoPanel.add(new Label(String.valueOf(cpg), Label.LEFT));
		infoPanel.add(new Label("Java Sleep Timer:", Label.RIGHT));
		infoPanel.add(new Label("java.lang.System.currentTimeMillis()",
				Label.LEFT));
		infoPanel.add(new Label("Resolution:", Label.RIGHT));
		infoPanel.add(new Label(String.valueOf(HiresClock
				.getSleepTimerGranularity()) + " ms", Label.LEFT));
		double r = VideoSystem.getFrameDuration();
		if (r > 0.0) {
			infoPanel.add(new Label("Video Cycle Duration:", Label.RIGHT));
			infoPanel.add(new Label(String.valueOf(r) + " ms", Label.LEFT));
			infoPanel.add(new Label("Video Refresh Rate:", Label.RIGHT));
			infoPanel.add(new Label(String.valueOf(1000.0 / r) + " Hz",
					Label.LEFT));
		}
		pack();
		setVisible(true);
	}
}
