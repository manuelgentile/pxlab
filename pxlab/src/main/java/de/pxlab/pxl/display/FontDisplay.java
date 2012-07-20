package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * An abstract superclass for Display objects which presents some kind of
 * letters.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
/*
 * 
 * 2005/04/19
 * 
 * 2005/10/18 made FontSize default depend on screen size.
 */
abstract public class FontDisplay extends Display {
	/** Letter color. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Letter color");
	/** Font family name. */
	public ExPar FontFamily = new ExPar(FONTNAME, new ExParValue("SansSerif"),
			"Font family name");
	/** Font style. */
	public ExPar FontStyle = new ExPar(GEOMETRY_EDITOR, FontStyleCodes.class,
			new ExParValueConstant("de.pxlab.pxl.FontStyleCodes.PLAIN"),
			"Font style");
	/** Font size. */
	public ExPar FontSize = new ExPar(
			VERSCREENSIZE,
			// new ExParValue(32),
			new ExParValue(new ExParExpression(ExParExpression.IDIV),
					new ExParValue(new ExParExpression(
							ExParExpression.SCREEN_HEIGHT)), new ExParValue(28)),
			"Font size");
}
