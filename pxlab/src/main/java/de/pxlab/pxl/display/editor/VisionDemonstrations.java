package de.pxlab.pxl.display.editor;

/**
 * The vision demonstrations version of the PXLab display editor.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 2006/11/22
 */
public class VisionDemonstrations {
	// ---------------------------------------------------------------
	// Application entry point
	// ---------------------------------------------------------------
	public static void main(String[] args) {
		new DisplayEditor(
				new de.pxlab.pxl.gui.BigList(de.pxlab.pxl.Topics.DEMO), args,
				false);
	}
}
