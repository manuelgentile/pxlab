package de.pxlab.pxl.display.editor;

import java.awt.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.gui.ParamControlPanel;

public class TimingControl extends DisplayPropertyControl {
	private ParamControlPanel paramControlPanel;

	public TimingControl(Component dspp, boolean expMode) {
		super("Timing");
		paramControlPanel = new ParamControlPanel(this, dspp, expMode);
		addContent(paramControlPanel);
	}

	/** Configure this panel for the given display. */
	public void configureFor(Display display) {
		paramControlPanel.configureFor(display, display.getTimingPars());
		invalidate();
	}
}
