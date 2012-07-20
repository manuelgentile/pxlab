package de.pxlab.pxl.display.editor;

import java.awt.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.gui.ParamControlPanel;

/** Controls the geometric properties of a display object. */
public class GeometryControl extends DisplayPropertyControl {
	// private ExParDescriptor[] geometryPars;
	private ParamControlPanel paramControlPanel;

	public GeometryControl(Component dspp, boolean expMode) {
		super("Geometry");
		paramControlPanel = new ParamControlPanel(this, dspp, expMode);
		// paramControlPanel.setBackground(PXLabGUI.background);
		addContent(paramControlPanel);
	}

	/** Configure this panel for the given display. */
	public void configureFor(Display display) {
		paramControlPanel.configureFor(display, display.getGeometryPars());
		invalidate();
	}
}
