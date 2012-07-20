package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This is a simple colored disk.
 * 
 * <P>
 * A simple color field is shown, the color of which may be adjusted in the
 * chromaticity diagram and the luminance scale.
 * <P>
 * Three perceptual attributes of colors may be shown:
 * <P>
 * 1. Brightness. The brightness of a color is the attribute which corresponds
 * to changes in luminance. Use the luminance scale to check it out.
 * 
 * <p>
 * 2. Saturation. To change the saturation of a color stimulus one has to move
 * from the chromaticity diagram's neutral point at (0.31, 0.31) to the borders
 * of the triangle of colors which correspond to the screen phosphors.
 * Saturation is controlled by the content of white in a stimulus.
 * 
 * <p>
 * 3. Hue. Maximal hue changes result from moving along the phosphor triangle's
 * borders.
 * 
 * <P>
 * Wyszecki, G. (1986). Color appearance. In K. R. Boff, L. Kaufman, & J. P.
 * Thomas (Eds.) Handbook of perception and human performance. Vol. I. Sensory
 * processes and perception, Chapter 9. New York: Wiley.
 * 
 * @author H. Irtel
 * @version 0.2.1
 */
/*
 * 
 * 02/14/01
 * 
 * 2005/11/16 allow dithering
 */
public class SimpleDisk extends ColorAdjustableHSB {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.GRAY)), "Disk color");
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(200), "Disk size");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal position");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical position");
	/**
	 * The size of the dithering matrix used to increase color resolution.
	 */
	public ExPar DitheringType = new ExPar(GEOMETRY_EDITOR,
			DitheringCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.DitheringCodes.NO_DITHERING"),
			"Type of dithering");

	public SimpleDisk() {
		setTitleAndTopic("Simple Colored Disk", SIMPLE_GEOMETRY_DSP);
	}
	private int s1;

	protected int create() {
		s1 = enterDisplayElement(new Oval(this.Color), group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeGeometry() {
		DisplayElement disk = getDisplayElement(s1);
		int s = Size.getInt();
		disk.setSize(s, s);
		disk.setLocation(LocationX.getInt() - s / 2, LocationY.getInt() - s / 2);
		disk.setDither(DitheringType.getInt());
	}
}
