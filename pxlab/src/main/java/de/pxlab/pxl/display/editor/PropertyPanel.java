package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import de.pxlab.awtx.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.gui.*;

/**
 * Collects the property panels for color, geometry, and timing properties in a
 * CardLayout.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class PropertyPanel extends Panel implements ActionListener {
	/** This panel controls the color properties of a display. */
	private ColorControl colorControlPanel;
	/** This panel controls the geometric properties of a display. */
	private GeometryControl geometryControlPanel;
	/** This panel controls the timing properties of a display. */
	private TimingControl timingControlPanel;
	/** The names of the property panels. */
	private String[] propertyLabel = { "Color", "Geometry", "Timing" };

	public PropertyPanel(Component displayPanel, ColorParServer colorParServer,
			boolean expMode) {
		super(new CardLayout());
		// Create the property panels
		colorControlPanel = new ColorControl(colorParServer, expMode);
		add(colorControlPanel, propertyLabel[0]);
		geometryControlPanel = new GeometryControl(displayPanel, expMode);
		add(geometryControlPanel, propertyLabel[1]);
		timingControlPanel = new TimingControl(displayPanel, expMode);
		add(timingControlPanel, propertyLabel[2]);
	}

	/**
	 * ActionListener implementation for selecting the active property.
	 */
	public void actionPerformed(ActionEvent e) {
		Button source = (Button) e.getSource();
		String item = source.getLabel();
		// System.out.println("Activate System " + item);
		show(item);
	}

	/** Show the panel whose label is given as an argument. */
	public void show(String item) {
		((CardLayout) getLayout()).show(this, item);
	}

	/** Configure the property panels for the given display object. */
	public void configureFor(Display dsp) {
		colorControlPanel.configureFor(dsp);
		geometryControlPanel.configureFor(dsp);
		timingControlPanel.configureFor(dsp);
	}

	/**
	 * Return the collected file menu entries of the property panels.
	 */
	public MenuItem[] getFileMenuItems() {
		return (colorControlPanel.getFileMenuItems());
	}

	/**
	 * Get the color control panel. This is called by a FileMenu object in order
	 * to write the color board content to a file.
	 */
	public ColorControl getColorControlPanel() {
		return (colorControlPanel);
	}

	/** Return the label of the color property panel. */
	public String getColorPropertyLabel() {
		return propertyLabel[0];
	}

	/** Return the label of the geometry property panel. */
	public String getGeometryPropertyLabel() {
		return propertyLabel[1];
	}

	/** Return the label of the timing property panel. */
	public String getTimingPropertyLabel() {
		return propertyLabel[2];
	}
	/*
	 * public void paint(Graphics g) {
	 * System.out.println("PropertyPanel.paint() Panel is " + (isValid()?
	 * "valid": "invalid")); super.paint(g); }
	 * 
	 * public void invalidate() {
	 * System.out.println("PropertyPanel.invalidate()"); super.invalidate(); }
	 */
}
