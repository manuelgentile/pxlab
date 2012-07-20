package de.pxlab.pxl.gui;

import java.awt.*;

// import de.pxlab.pxl.gui.ParamControlPanel;
import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/** Controls the geometric properties of a display object. */
public class ParameterDialog extends CancelOKButtonDialog {
	// private ExParDescriptor[] geometryPars;
	private ParamControlPanel paramControlPanel;

	public ParameterDialog(Frame parent) {
		super(parent, " Parameter Dialog");
		paramControlPanel = new ParamControlPanel(/* Container */this, /* Component */
				null, true);
		add(paramControlPanel);
	}

	/** Configure this panel for the given display. */
	public void configureFor(ExParDescriptor[] pars) {
		paramControlPanel.configureFor(null, pars);
		invalidate();
	}
}
