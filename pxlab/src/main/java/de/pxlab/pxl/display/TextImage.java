package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import de.pxlab.pxl.*;

/**
 * Shows a string which may be mirror transformed or rotated as an image.
 * 
 * @version 0.2.1
 */
/*
 * 
 * 08/06/03 fixed background color bug
 * 
 * 2005/02/23 use BufferedImage
 * 
 * 2008/01/24 fixed bug for mirror case at certain rotations
 */
public class TextImage extends TextDisplay {
	public ExPar Rotation = new ExPar(ANGLE, new ExParValue(0),
			"Angle of rotation");
	public ExPar Mirror = new ExPar(FLAG, new ExParValue(0),
			"Mirror image flag");

	public TextImage() {
		setTitleAndTopic("Text Image", PATTERN_IMAGE_DSP);
		Text.set("R");
		FontSize.set(128);
	}
	private BitMapElement bitMap;

	protected int create() {
		bitMap = new BitMapElement(Color);
		int t = enterDisplayElement(bitMap, group[0]);
		defaultTiming(0);
		return (t);
	}

	protected void computeColors() {
		computeGeometry();
	}

	protected void computeGeometry() {
		String txt = Text.getString();
		Font font = new Font(FontFamily.getString(), FontStyle.getInt(),
				FontSize.getInt());
		Graphics g = displayComponent.getGraphics();
		FontMetrics fm = g.getFontMetrics(font);
		int txtHeight = fm.getDescent() + fm.getAscent();
		int txtWidth = fm.stringWidth(txt);
		int txtSize = (txtHeight > txtWidth) ? txtHeight : txtWidth;
		int imgSize = (int) (2 * txtSize);
		// System.out.println("Image Size: " + imgSize);
		BufferedImage imgBuffer = new BufferedImage(imgSize, imgSize,
				BufferedImage.TYPE_INT_RGB);
		// System.out.println("Buffer Size: " + imgBuffer.getWidth() + "x" +
		// imgBuffer.getHeight());
		Graphics2D ig = (Graphics2D) imgBuffer.getGraphics();
		ig.setColor(ExPar.ScreenBackgroundColor.getDevColor());
		// ig.setColor(java.awt.Color.gray);
		ig.fillRect(0, 0, imgSize, imgSize);
		ig.setColor(Color.getDevColor());
		ig.setFont(font);
		ig.transform(AffineTransform.getTranslateInstance(imgSize / 2,
				imgSize / 2));
		Point2D.Float p1 = new Point2D.Float();
		double angle = -Rotation.getDouble() * Math.PI / 180.0;
		AffineTransform rot = AffineTransform.getRotateInstance(angle);
		boolean mirror = Mirror.getFlag();
		Point2D.Float p0 = new Point2D.Float(mirror ? txtWidth / 2
				: -txtWidth / 2, 2 * txtHeight / 7);
		rot.transform(p0, p1);
		ig.transform(AffineTransform.getTranslateInstance(p1.x, p1.y));
		ig.transform(rot);
		if (mirror)
			ig.transform(AffineTransform.getScaleInstance(-1.0, 1.0));
		ig.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		ig.drawString(txt, 0, 0);
		ig.dispose();
		g.dispose();
		// bitMap.setReferencePoint(de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER);
		bitMap.setImage(imgBuffer);
		bitMap.setLocation(LocationX.getInt(), LocationY.getInt());
	}
}
