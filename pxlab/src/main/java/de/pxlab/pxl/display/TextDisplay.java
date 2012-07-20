package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * An abstract superclass for Display objects which presents some kind of text.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/04/19
 */
abstract public class TextDisplay extends FontDisplay {
	/** Horizontal text position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Location of the Reference Point");
	/** Vertical text position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Location of the Reference Point");
	/**
	 * Reference point for the whole paragraph. See
	 * <code>PositionReferenceCodes</code> for a description.
	 */
	public ExPar ReferencePoint = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER"),
			"Text Reference Point");
	/** Text content. */
	public ExPar Text = new ExPar(STRING, new ExParValue(""), "Text");
}
