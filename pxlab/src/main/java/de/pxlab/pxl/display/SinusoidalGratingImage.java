package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;

import de.pxlab.pxl.*;

/**
 * A sinusoidal grating of two colors which is created in an image buffer and
 * animated by shifting the image horizontally behind a clipping window.
 * 
 * <p>
 * Moving sinewave color gratings have been used byte Cavanagh, Tyler, and
 * Favreau (1984) to show that isoluminant gratings appear to move much slower
 * than heteroluminant gratings.
 * 
 * <p>
 * Cavanagh, P., Tyler, C. W., & Favreau, O. (1984). Perceived velocity of
 * moving chromatic gratings. JOSA A, 1, 893-899.
 * <P>
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 
 * 03/23/00
 * 
 * 06/27/02 Fixed bug with reference point.
 * 
 * 2005/02/23 use BufferedImage now
 */
public class SinusoidalGratingImage extends FrameAnimation {
	public ExPar AColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.red)), "Color A");
	public ExPar BColor = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.yellow)), "Color B");
	/** Display field width. */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(300),
			"Image Width");
	/** Display field height. */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(200),
			"Image Height");
	public ExPar FrameIncrement = new ExPar(SMALL_INT, new ExParValue(3),
			"Frame Counter Increment");

	public SinusoidalGratingImage() {
		setTitleAndTopic("Moving Sinusoidal Grating Image", GRATING_DSP | DEMO);
		FrameDuration.set(300);
	}
	private int pictElement;

	protected int create() {
		BitMapElement pict = new BitMapElement();
		pict.setReferencePoint(PositionReferenceCodes.TOP_LEFT);
		pict.setColorPar(AColor);
		pictElement = enterDisplayElement(pict, group[0]);
		enterTiming(FrameTimer, FrameDuration, 0);
		return (pictElement);
	}

	protected void computeColors() {
		computeGeometry();
	}
	private BufferedImage imageBuffer = null;
	private int xLoc, yLoc;
	private int pictWidth, pictHeight;
	private int pictHalfWidth;

	protected void computeGeometry() {
		pictHalfWidth = Width.getInt() / 2;
		pictWidth = pictHalfWidth + pictHalfWidth - 1;
		pictHeight = Height.getInt();
		xLoc = -pictHalfWidth;
		yLoc = -pictHeight / 2;
		setFramesPerCycle(pictWidth - 1);
		setFrameIncrement(FrameIncrement.getInt());
		// System.out.println("width = " + pictWidth);
		// System.out.println("    x = " + xLoc);
		// System.out.println("    y = " + yLoc);
		PxlColor[] ramp = AColor.getPxlColor().sinusoidalRampTo(
				BColor.getPxlColor(), pictHalfWidth);
		// System.out.println("w=" + (4*pictHalfWidth-1) + ", h=" + pictHeight);
		int ibWidth = 4 * pictHalfWidth - 1;
		if ((imageBuffer == null) || (imageBuffer.getWidth() != ibWidth)
				|| (imageBuffer.getHeight() != pictHeight)) {
			imageBuffer = new BufferedImage(ibWidth, pictHeight,
					BufferedImage.TYPE_INT_RGB);
		}
		Graphics g = imageBuffer.getGraphics();
		for (int x = 0; x < pictHalfWidth; x++) {
			g.setColor(ramp[x].dev());
			g.drawLine(x, 0, x, pictHeight - 1);
			if (x < (pictHalfWidth - 1))
				g.drawLine(pictWidth - x - 1, 0, pictWidth - x - 1,
						pictHeight - 1);
			if (x > 0)
				g.drawLine(pictWidth + x - 1, 0, pictWidth + x - 1,
						pictHeight - 1);
			if ((x > 0) && (x < (pictHalfWidth - 1)))
				g.drawLine(4 * pictHalfWidth - 4 - x, 0, 4 * pictHalfWidth - 4
						- x, pictHeight - 1);
		}
		g.dispose();
		BitMapElement p = (BitMapElement) getDisplayElement(pictElement);
		p.setImage(imageBuffer);
		p.setLocation(xLoc, yLoc);
		p.setClipRect(xLoc, yLoc, pictWidth, pictHeight);
	}

	/**
	 * Move the image horizontally according to frame position. Since the
	 * clipping rectangle remains unchanged the visible part of the image seems
	 * to change.
	 */
	public void computeAnimationFrame(int frame) {
		((BitMapElement) getDisplayElement(pictElement)).setLocation(xLoc
				- frame, yLoc);
	}
}
