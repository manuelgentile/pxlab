package de.pxlab.pxl;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import de.pxlab.awtx.Painter;

/**
 * A scalable geometric representation of the Self-Assessment Manikin.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 2007/05/03
 * 
 * 2008/02/21 allow 7-point scale
 */
public class SAMImageFactory implements SAMScaleCodes {
	/**
	 * Width of the image drawing lines. This is an absolute width independent
	 * of scaling.
	 */
	private double lineWidth = 1.5;
	/**
	 * Width of the frame lines. This is an absolute width independent of
	 * scaling.
	 */
	private double frameLineWidth = 1.8;
	/** If true then the Valence scale is shown in portrait mode. */
	private boolean valencePortrait = true;
	/**
	 * If true then the images use a small border only. Should only be set if
	 * the Dominance scale is not used since the Dominance scale needs a wide
	 * border.
	 */
	private boolean narrowBorder = true;
	/**
	 * If true then separate selection points are drawn below the images for
	 * steps 1, 3, 5, 7, and 9 and the steps 2, 4, 6, and 8 have a selection
	 * point only but no images. Should only be set for 9-point scales.
	 */
	private boolean selectionPoints = false;
	/** Image line color. */
	private Color lineColor = Color.black;
	/** Frame line color. */
	private Color frameColor = Color.red;
	/** Background color. */
	private Color bgColor = Color.white;
	/** Background color for the selected state. */
	private Color selectedColor = Color.lightGray;
	// --------------------------------------------------------------
	// The remaining parameters are not modifyable in order to keep
	// them constant across studies.
	// --------------------------------------------------------------
	private static final int levelResolution = 100;
	private static final int centerLevel = 5 * levelResolution;
	private static final int minLevel = 1 * levelResolution;
	private static final int maxLevel = 9 * levelResolution;
	private static final double frameWidth = 540.0;
	private static final double frameHeight = 580.0;
	private static final double designWidth = frameWidth + 80.0;
	private static final double designHeight = frameHeight + 80.0;
	private static final double narrowBorderFactor = 1.1;
	private static final double frameLeftX = -frameWidth / 2.0;
	private static final double frameRightX = frameWidth / 2.0;
	private static final double frameTopY = -frameHeight / 2.0;
	private static final double frameBottomY = frameHeight / 2.0;
	private static final double[] bodyPoints = {
	/* b01 */-90.0, -210.0,
	/* b02 */-90.0, -127.0,
	/* b03 */-60.0, -127.0,
	/* b04 */-60.0, -70.0,
	/* b05 */-180.0, -70.0,
	/* b06 */-180.0, 40.0,
	/* b07 */-122.0, 40.0,
	/* b08 */-122.0, 155.0,
	/* b09 */-180.0, 155.0,
	/* b10 */-180.0, 210.0,
	/* b11 */-60.0, 210.0,
	/* b12 */-60.0, 155.0,
	/* b13 */-60.0, -180.0,
	/* b14 */-122.0, 0.0 };
	private static final double valencePortraitScaleFactor = 1.7;
	private static final double valencePortraitCenterShiftY = 86.0;
	private static final double selectionPointY = frameBottomY + 130.0;
	private static final double selectionPointSize = 140.0;
	private static final double d_scale_b = 1.4;
	private static final double dx1_scale = 0.36;
	private static final double dx9_scale = 1.66;
	private static final double k1 = Math.pow(dx1_scale / dx9_scale,
			1.0 / d_scale_b);
	private static final double dx_scale_m = ((double) minLevel - k1
			* (double) maxLevel)
			/ (1.0 - k1);
	private static final double dx_scale_a = dx9_scale
			/ Math.pow((double) maxLevel - dx_scale_m, d_scale_b);
	private static final double d_yx_ratio = ((designHeight / 2.0) / bodyPoints[1])
			/ ((designWidth / 2.0) / bodyPoints[8]);
	private static final double dy1_scale = dx1_scale / d_yx_ratio;
	private static final double dy9_scale = dx9_scale * d_yx_ratio;
	private static final double k2 = Math.pow(dy1_scale / dy9_scale,
			1.0 / d_scale_b);
	private static final double dy_scale_m = ((double) minLevel - k2
			* (double) maxLevel)
			/ (1.0 - k2);
	private static final double dy_scale_a = dy9_scale
			/ Math.pow((double) maxLevel - dy_scale_m, d_scale_b);
	// These are the available arousal values
	private static final int[] arousalLevelValue = { 100, 200, 233, 300, 366,
			400, 500, 600, 633, 700, 766, 800, 900 };
	// This is for debugging purposes only
	private boolean showGrid = false;
	private boolean debug = false;

	public SAMImageFactory() {
		// System.out.println("SAMImageFactory()");
		// new RuntimeException().printStackTrace();
	}

	/**
	 * Define whether the Valence scale is shown in portrait mode or in full
	 * body mode.
	 * 
	 * @param s
	 *            if true then the Valence scale is shown as a portrait image.
	 *            If false then the same full body image is used for the Valence
	 *            scale as it is used for the other scales.
	 */
	public void setValencePortrait(boolean s) {
		valencePortrait = s;
	}

	/**
	 * Set the line width for image drawing.
	 * 
	 * @param w
	 *            the line width. The line width is invariant against image
	 *            scaling.
	 */
	public void setLineWidth(double w) {
		lineWidth = w;
	}

	/**
	 * Set the line width for drawing the frame.
	 * 
	 * @param w
	 *            the frame line width. The line width is invariant against
	 *            image scaling.
	 */
	public void setFrameLineWidth(double w) {
		frameLineWidth = w;
	}

	/**
	 * Define whether a wide or a narrow border should be used. A narrow border
	 * should only be used if the Dominance scale is not used since the
	 * Dominance scale needs a wide border for its maximum value level.
	 * 
	 * @param s
	 *            if true then a narrow border is used. The narrow border is
	 *            apropriate for the Valence and the Arousal scale it is not
	 *            apropriate for the Dominance scale.
	 */
	public void setNarrowBorder(boolean s) {
		narrowBorder = s;
	}

	/**
	 * Make this a scale which has a special selection point below its images
	 * and which does not show any images at the even level positions but only
	 * shows the selection point at these positions.
	 */
	public void setSelectionPoints(boolean s) {
		selectionPoints = s;
	}

	/** Show a background grid. */
	public void setShowGrid(boolean s) {
		showGrid = s;
	}

	/**
	 * Set the drawing colors.
	 * 
	 * @param l
	 *            the drawing line color
	 * @param f
	 *            the frame line color
	 * @param s
	 *            the background color for the selected state
	 * @param bg
	 *            the background color
	 */
	public void setColors(Color l, Color f, Color s, Color bg) {
		lineColor = l;
		frameColor = f;
		selectedColor = s;
		bgColor = bg;
	}

	/**
	 * Create a single Self-Assessment Manikin image. The image type and value
	 * level are described in a description string. If the description string
	 * matches one of the strings "-valence-", "-arousal-", or "-dominance-"
	 * then the respective scale is used. The value level is defined by the
	 * description string matching one of "-1", "-2", ..., "-9". The selected
	 * state of a value level is created if the description string matches the
	 * string "-selected". Thus as an example if the description string is
	 * "SAM-arousal-4" then the unselected level 4 of the Arousal scale will be
	 * drawn. If the description is "SAM-arousal-4-selected" then the respective
	 * selected state is created.
	 * 
	 * @param desc
	 *            the image description string.
	 * @param w
	 *            the requested image width
	 * @param h
	 *            the requested image height (this parameter is currently not
	 *            used)
	 * @return an image for the requested scale and value level.
	 */
	public BufferedImage instance(String desc, int w, int h) {
		// System.out.println("SAMImageFactory.instance(): " + desc);
		int type = VALENCE;
		if (desc.matches(".*-valence-.*")) {
			type = VALENCE;
		} else if (desc.matches(".*-arousal-.*")) {
			type = AROUSAL;
		} else if (desc.matches(".*-dominance-.*")) {
			type = DOMINANCE;
		}
		int level = 5;
		if (desc.matches(".*-1.*")) {
			level = 1;
		} else if (desc.matches(".*-2.*")) {
			level = 2;
		} else if (desc.matches(".*-3.*")) {
			level = 3;
		} else if (desc.matches(".*-4.*")) {
			level = 4;
		} else if (desc.matches(".*-5.*")) {
			level = 5;
		} else if (desc.matches(".*-6.*")) {
			level = 6;
		} else if (desc.matches(".*-7.*")) {
			level = 7;
		} else if (desc.matches(".*-8.*")) {
			level = 8;
		} else if (desc.matches(".*-9.*")) {
			level = 9;
		}
		boolean selected = desc.matches(".*-selected.*");
		return instance(type, level, selected, w);
	}

	/**
	 * Create a single Self-Assessment Manikin image.
	 * 
	 * @param type
	 *            scale type. Must be one of SAMScaleCodes.VALENCE,
	 *            SAMScaleCodes.AROUSAL, SAMScaleCodes.DOMINANCE.
	 * @param lev
	 *            scale value level: 1, ..., 9. Only integer values are used for
	 *            most scale properties.
	 * @param selected
	 *            indicates whether this is a normal or a selected image.
	 * @param w
	 *            intended image width. The actually resulting image size may be
	 *            slightly larger.
	 * @return a BufferedImage containing the requested scale value level.
	 */
	public BufferedImage instance(int type, double lev, boolean selected, int w) {
		int level = (int) Math.round(levelResolution * lev);
		// compute the scaling factor for fitting the designWidth into
		// the requested image width
		double sf = ((double) w) / designWidth;
		if (debug)
			System.out.println("Scaling factor = " + sf);
		// scale the line width to a fixed size
		double lw = lineWidth / sf;
		double flw = frameLineWidth / sf;
		int lwi = (int) Math.ceil(lw);
		// compute image width and height
		int iw = w;
		int ih = (int) Math.ceil((double) w * designHeight / designWidth);
		if (debug)
			System.out.println("Image size = " + iw + "x" + ih);
		// true if this is a selection point only
		boolean spe = false;
		int sph = 0;
		if (selectionPoints) {
			sph = ih / 3;
			if ((lowResLevel(level) % 2) == 0) {
				iw = w / 3;
				spe = true;
			}
		}
		// create an image buffer
		if (iw <= 0)
			iw = 1;
		if (ih <= 0)
			ih = 1;
		BufferedImage imgBuffer = new BufferedImage(iw, ih + sph,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = imgBuffer.createGraphics();
		// set some general painting properties
		g.setBackground(bgColor);
		g.clearRect(0, 0, iw, ih + sph);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// create the transform
		if (narrowBorder)
			sf *= narrowBorderFactor;
		AffineTransform tr = AffineTransform.getTranslateInstance(
				(double) iw / 2.0, (double) ih / 2.0);
		tr.concatenate(AffineTransform.getScaleInstance(sf, sf));
		g.setTransform(tr);
		if (selectionPoints) {
			g.setPaint(lineColor);
			g.setStroke(new BasicStroke((float) lw));
			selectionPoint(g, selected);
		}
		if (!spe) {
			// not selection point only, so draw the image
			Rectangle bounds = frame(g, flw, selected);
			g.setPaint(lineColor);
			g.setStroke(new BasicStroke((float) lw));
			if ((type == VALENCE) && valencePortrait) {
				// this is a Valence scale image in portrait mode
				g.setClip(bounds.x + lwi, bounds.y + lwi, bounds.width - 2
						* lwi, bounds.height - 2 * lwi);
				sf *= valencePortraitScaleFactor;
				tr = AffineTransform.getTranslateInstance((double) iw / 2.0,
						(double) (ih / 2.0));
				tr.concatenate(AffineTransform.getScaleInstance(sf, sf));
				tr.concatenate(AffineTransform.getTranslateInstance(0.0,
						valencePortraitCenterShiftY));
				g.setTransform(tr);
				lw = lineWidth / sf;
				g.setStroke(new BasicStroke((float) (lw)));
			}
			if ((type == DOMINANCE)) {
				/*
				 * System.out.println("d_scale_b = " + d_scale_b);
				 * System.out.println("dx_scale_m = " + dx_scale_m);
				 * System.out.println("dx_scale_a = " + dx_scale_a);
				 * System.out.println("dy_scale_m = " + dy_scale_m);
				 * System.out.println("dy_scale_a = " + dy_scale_a);
				 */
				double dsx = dx_scale_a
						* Math.pow((double) level - dx_scale_m, d_scale_b);
				double dsy = dy_scale_a
						* Math.pow((double) level - dy_scale_m, d_scale_b);
				// System.out.println("dsx = " + dsx);
				// System.out.println("dsy = " + dsy);
				tr = AffineTransform.getTranslateInstance((double) iw / 2.0,
						(double) ih / 2.0);
				tr.concatenate(AffineTransform.getScaleInstance(sf * dsx, sf
						* dsy));
				g.setTransform(tr);
				lw = lineWidth / (sf * dsx);
				g.setStroke(new BasicStroke((float) (lw)));
				// System.out.println("sfx = " + sf*dsx);
				// System.out.println("sfy = " + sf*dsy);
			}
			// System.out.println("sf = " + sf);
			if (showGrid) {
				grid(g);
				g.setPaint(lineColor);
				g.setStroke(new BasicStroke((float) lw));
			}
			body(g, bodyPoints);
			mouth(g, type, level);
			nose(g, type, level);
			eyes(g, type, level);
			eyeBrows(g, type, level);
			if (type == AROUSAL) {
				arousalBeats(g, level, lw);
			}
		}
		g.dispose();
		return imgBuffer;
	}

	private void selectionPoint(Graphics2D g, boolean selected) {
		double x = -selectionPointSize / 2.0;
		double y = selectionPointY - selectionPointSize / 2.0;
		Shape e = new Ellipse2D.Double(x, y, selectionPointSize,
				selectionPointSize);
		if (selected) {
			g.setPaint(selectedColor);
			g.fill(e);
		}
		g.setPaint(lineColor);
		g.draw(e);
	}

	private Rectangle frame(Graphics2D g, double frLw, boolean selected) {
		Path2D.Double p = new Path2D.Double();
		p.moveTo(frameLeftX, frameTopY);
		p.lineTo(frameRightX, frameTopY);
		p.lineTo(frameRightX, frameBottomY);
		p.lineTo(frameLeftX, frameBottomY);
		p.closePath();
		if (selected && !selectionPoints) {
			g.setPaint(selectedColor);
			g.fill(p);
		}
		g.setStroke(new BasicStroke((float) frLw));
		g.setPaint(frameColor);
		g.draw(p);
		return p.getBounds();
	}

	private void body(Graphics2D g, double[] p) {
		Path2D.Double path = new Path2D.Double();
		path.moveTo(p[0], p[1]);
		for (int i = 1; i <= 11; i++)
			path.lineTo(p[i + i], p[i + i + 1]);
		for (int i = 11; i >= 0; i--)
			path.lineTo(-p[i + i], p[i + i + 1]);
		path.closePath();
		if (!showGrid) {
			g.setPaint(bgColor);
			g.fill(path);
		}
		g.setPaint(lineColor);
		g.draw(path);
		path.reset();
		// hair line
		path.moveTo(p[4], p[5]);
		path.lineTo(p[24], p[25]);
		path.lineTo(-p[24], p[25]);
		path.lineTo(-p[4], p[5]);
		// left arm pit
		path.moveTo(p[12], p[13]);
		path.lineTo(p[26], p[27]);
		// right arm pit
		path.moveTo(-p[12], p[13]);
		path.lineTo(-p[26], p[27]);
		g.draw(path);
	}
	private static final double v1_mouth_y = -63.3;
	private static final double v1_mouth_w = 55.0;
	private static final double v1_mouth_dy = -50.0;
	private static final double v5_mouth_y = -94.0;
	private static final double v5_mouth_w = 52.0;
	private static final double v9_mouth_y = -104;
	private static final double v9_mouth_w = 56.7;
	private static final double v9_mouth_dy = 50.0;
	private static final double v9_smile_dy = 8.0;
	private static final double v9_smile_dx = 16.0;
	private static final double v9_smile_size = 5.0;
	private static final double a_mouth_y = -92.3;
	private static final double a_mouth_w = 57.0;
	private static final double d1_mouth_y = -95.0;
	private static final double d5_mouth_y = -85.3;
	private static final double d5_mouth_w = 60.0;
	private static final double d9_mouth_y = -90.3;
	private static final double d9_mouth_w = 56.0;

	private void mouth(Graphics2D g, int type, int level) {
		switch (type) {
		case VALENCE:
			double w = valueAt(level, v1_mouth_w, v5_mouth_w, v9_mouth_w);
			double y = valueAt(level, v1_mouth_y, v5_mouth_y, v9_mouth_y);
			mouth(g, y, w, valueAt(level, v1_mouth_dy, 0.0, v9_mouth_dy));
			if (lowResLevel(level) >= 8) {
				double sy = y - v9_smile_dy;
				double sx = -w / 2.0 - v9_smile_dx;
				double s = (lowResLevel(level) > 8) ? v9_smile_size
						: v9_smile_size / 2.0;
				double s2 = s / 2.0;
				g.fill(new Ellipse2D.Double(sx - s2, sy - s2, s, s));
				g.fill(new Ellipse2D.Double(-sx - s2, sy - s2, s, s));
			}
			break;
		case AROUSAL:
			mouth(g, a_mouth_y, a_mouth_w, 0.0);
			break;
		case DOMINANCE:
			mouth(g, valueAt(level, d1_mouth_y, d5_mouth_y, d9_mouth_y),
					upValueAt(level, d5_mouth_w, d9_mouth_w), 0.0);
			break;
		}
	}

	private void mouth(Graphics2D g, double y, double w, double dy) {
		Path2D.Double p = new Path2D.Double();
		p.moveTo(-w / 2.0, y);
		if (dy != 0.0) {
			double tension = 0.5;
			double tx = tension * w / 2.0;
			double ty = tension * dy;
			p.curveTo(-w / 2.0 + tx, y + ty, w / 2.0 - tx, y + ty, w / 2.0, y);
		} else {
			p.lineTo(w / 2.0, y);
		}
		g.draw(p);
	}
	private static final double v5_nose_w = 14.0;
	private static final double v5_nose_h = 7.0;
	private static final double v5_nose_y = -112.0;
	private static final double a1_nose_w = 17.5;
	private static final double a1_nose_h = 7.0;
	private static final double a1_nose_y = -110.5;
	private static final double a5_nose_w = 15.6;
	private static final double a5_nose_h = 7.0;
	private static final double a5_nose_y = -112.0;
	private static final double a9_nose_w = 13.2;
	private static final double a9_nose_h = 7.0;
	private static final double a9_nose_y = -114.0;
	private static final double d1_nose_w = 19.0;
	private static final double d1_nose_h = 7.0;
	private static final double d1_nose_y = -116.0;
	private static final double d5_nose_w = 19.0;
	private static final double d5_nose_h = 6.0;
	private static final double d5_nose_y = -105.0;
	private static final double d9_nose_w = 14.0;
	private static final double d9_nose_h = 10.0;
	private static final double d9_nose_y = -112.0;

	private void nose(Graphics2D g, int type, int level) {
		switch (type) {
		case VALENCE:
			nose(g, v5_nose_y, v5_nose_w, v5_nose_h);
			break;
		case AROUSAL:
			nose(g, valueAt(level, a1_nose_y, a5_nose_y, a9_nose_y),
					valueAt(level, a1_nose_w, a5_nose_w, a9_nose_w),
					valueAt(level, a1_nose_h, a5_nose_h, a9_nose_h));
			break;
		case DOMINANCE:
			nose(g, valueAt(level, d1_nose_y, d5_nose_y, d9_nose_y),
					valueAt(level, d1_nose_w, d5_nose_w, d9_nose_w),
					valueAt(level, d1_nose_h, d5_nose_h, d9_nose_h));
			break;
		}
	}

	private void nose(Graphics2D g, double y, double w, double h) {
		Path2D.Double p = new Path2D.Double();
		double w2 = w / 2.0;
		p.moveTo(-w2, y - h);
		p.lineTo(-w2, y);
		p.lineTo(w2, y);
		p.lineTo(w2, y - h);
		g.draw(p);
	}
	private static final double v5_eye_d = 34.0;
	private static final double v5_eye_y = -128.0;
	private static final double v5_eye_w = 20.0;
	private static final double v5_eye_h = 16.0;
	private static final double v9_eye_y = -132.0;
	private static final double v9_eye_h = 12.0;
	private static final double a1_eye_d = 30.4;
	private static final double a1_eye_w = 24.0;
	private static final double a1_eye_h = 9.4;
	private static final double a1_eye_dy = a1_eye_h;
	private static final double a5_eye_d = 32.6;
	private static final double a5_eye_y = -128.0;
	private static final double a5_eye_w = 19.0;
	private static final double a5_eye_h = 14.0;
	private static final double a5_eye_dy = 0.0;
	private static final double a9_eye_d = 32.6;
	private static final double a9_eye_y = -131.0;
	private static final double a9_eye_h = 18.0;
	private static final double d1_eye_y = -135.5;
	private static final double d1_eye_h = 14.2;
	private static final double d5_eye_d = 35.0;
	private static final double d5_eye_y = -123.0;
	private static final double d5_eye_w = 20.4;
	private static final double d5_eye_h = 15.3;
	private static final double d9_eye_d = 32.6;
	private static final double d9_eye_y = -124.5;
	private static final double d9_eye_h = 20.4;
	private static final double pupilSize = 7.0;

	private void eyes(Graphics2D g, int type, int level) {
		switch (type) {
		case VALENCE:
			eyes(g, v5_eye_d, upValueAt(level, v5_eye_y, v9_eye_y), v5_eye_w,
					upValueAt(level, v5_eye_h, v9_eye_h), 0.0,
					downValueAt(level, (v5_eye_h - pupilSize) / 2.0, 0.0));
			break;
		case AROUSAL:
			eyes(g, downValueAt(level, a1_eye_d, a5_eye_d),
					upValueAt(level, a5_eye_y, a9_eye_y),
					downValueAt(level, a1_eye_w, a5_eye_w),
					valueAt(level, a1_eye_h, a5_eye_h, a9_eye_h),
					downValueAt(level, a1_eye_dy, a5_eye_dy), 0.0);
			break;
		case DOMINANCE:
			eyes(g, upValueAt(level, d5_eye_d, d9_eye_d),
					valueAt(level, d1_eye_y, d5_eye_y, d9_eye_y), d5_eye_w,
					valueAt(level, d1_eye_h, d5_eye_h, d9_eye_h), 0.0, 0.0);
			break;
		}
	}

	private void eyes(Graphics2D g, double d, double y, double w, double h,
			double dy, double dp) {
		Path2D.Double p = new Path2D.Double();
		double ps2 = pupilSize / 2.0;
		double d2 = d / 2.0;
		double py = (y + y - h + dy) / 2.0 + dp;
		if (dy > 0.0) {
			if (h - dy > 0.0) {
				// need to clip the pupils
				Rectangle clip = g.getClipBounds();
				Rectangle2D.Double bounds = new Rectangle2D.Double(-d2 - w, y
						- h + dy, w, h - dy);
				g.setClip(bounds);
				g.fill(new Ellipse2D.Double(-d2 - w / 2.0 - ps2, py - ps2,
						pupilSize, pupilSize));
				bounds = new Rectangle2D.Double(d2, y - h + dy, w, h - dy);
				g.setClip(bounds);
				g.fill(new Ellipse2D.Double(d2 + w / 2.0 - ps2, py - ps2,
						pupilSize, pupilSize));
				g.setClip(clip);
			}
		} else {
			g.fill(new Ellipse2D.Double(-d2 - w / 2.0 - ps2, py - ps2,
					pupilSize, pupilSize));
			g.fill(new Ellipse2D.Double(d2 + w / 2.0 - ps2, py - ps2,
					pupilSize, pupilSize));
		}
		p.moveTo(-d2, y - h);
		p.lineTo(-d2, y);
		p.lineTo(-d2 - w, y);
		p.lineTo(-d2 - w, y - h);
		if (dy > 0) {
			if (h - dy > 0.0) {
				p.moveTo(-d2, y - h + dy);
				p.lineTo(-d2 - w, y - h + dy);
			}
		} else {
			p.closePath();
		}
		p.moveTo(d2, y - h);
		p.lineTo(d2, y);
		p.lineTo(d2 + w, y);
		p.lineTo(d2 + w, y - h);
		if (dy > 0) {
			if (h - dy > 0.0) {
				p.moveTo(d2, y - h + dy);
				p.lineTo(d2 + w, y - h + dy);
			}
		} else {
			p.closePath();
		}
		g.draw(p);
	}
	private static final double v1_eyeBrow_y = -167.8;
	private static final double v1_eyeBrow_w = 16.0;
	private static final double v1_eyeBrow_dy = 11.5;
	private static final double v5_eyeBrow_d = 38.0;
	private static final double v5_eyeBrow_y = -156.5;
	private static final double v5_eyeBrow_w = 17.0;
	private static final double v9_eyeBrow_y = -167.8;
	private static final double v9_eyeBrow_w = 27.0;
	private static final double a1_eyeBrow_d = 33.2;
	private static final double a1_eyeBrow_w = 23.0;
	private static final double a5_eyeBrow_d = 32.6;
	private static final double a5_eyeBrow_y = -151.0;
	private static final double a5_eyeBrow_w = 20.3;
	private static final double a9_eyeBrow_d = 25.0;
	private static final double a9_eyeBrow_y = -173.0;
	private static final double a9_eyeBrow_w = 25.5;
	private static final double d1_eyeBrow_d = 36.0;
	private static final double d1_eyeBrow_w = 22.0;
	private static final double d5_eyeBrow_d = 40.0;
	private static final double d5_eyeBrow_y = -153.0;
	private static final double d5_eyeBrow_w = 16.0;
	private static final double d9_eyeBrow_d = 38.0;
	private static final double d9_eyeBrow_w = 23.0;

	private void eyeBrows(Graphics2D g, int type, int level) {
		switch (type) {
		case VALENCE:
			eyeBrows(g, v5_eyeBrow_d,
					valueAt(level, v1_eyeBrow_y, v5_eyeBrow_y, v9_eyeBrow_y),
					upValueAt(level, v5_eyeBrow_w, v9_eyeBrow_w),
					downValueAt(level, v1_eyeBrow_dy, 0.0));
			break;
		case AROUSAL:
			eyeBrows(g,
					valueAt(level, a1_eyeBrow_d, a5_eyeBrow_d, a9_eyeBrow_d),
					upValueAt(level, a5_eyeBrow_y, a9_eyeBrow_y),
					valueAt(level, a1_eyeBrow_w, a5_eyeBrow_w, a9_eyeBrow_w),
					0.0);
			break;
		case DOMINANCE:
			eyeBrows(g,
					valueAt(level, d1_eyeBrow_d, d5_eyeBrow_d, d9_eyeBrow_d),
					d5_eyeBrow_y,
					valueAt(level, d1_eyeBrow_w, d5_eyeBrow_w, d9_eyeBrow_w),
					0.0);
			break;
		}
	}

	private void eyeBrows(Graphics2D g, double d, double y, double w, double dy) {
		Path2D.Double p = new Path2D.Double();
		double d2 = d / 2.0;
		p.moveTo(-d2, y);
		p.lineTo(-d2 - w, y + dy);
		p.moveTo(d2, y);
		p.lineTo(d2 + w, y + dy);
		g.draw(p);
	}

	private void arousalBeats(Graphics2D g, int lev, double lnWidth) {
		int level = arousalLevel(lev);
		int ldx = 0;
		int ldy = 0;
		double lsc = 1.0;
		if (level == 100) {
			String[] ps = { "M -2 34 ", "C 2 38 5 37 6 36 ",
					"C 10 34 11 38 6 40 ", "C 0 43 -1 42 -6 38 ",
					"C -9 34 -6 30 -2 34 Z" };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 200) {
			String[] ps = { "M -4 34 ", "C -0 35 4 51 14 38 ",
					"C 17 33 10 30 10 24 ", "C 9 17 13 17 12 14 ",
					"C 11 12 8 13 4 12 ", "C 0 11 -2 5 -7 8 ",
					"C -12 11 -7 14 -8 18 ", "C -9 22 -14 25 -16 29 ",
					"C -17 32 -8 33 -4 34 Z", "M 13 59 ",
					"C 20 59 26 54 28 45 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 233) {
			String[] ps = { "M -4 34 ", "C -0 35 4 51 14 38 ",
					"C 17 33 10 30 10 24 ", "C 9 17 13 17 12 14 ",
					"C 11 12 8 13 4 12 ", "C 0 11 -2 5 -7 8 ",
					"C -12 11 -7 14 -8 18 ", "C -9 22 -14 25 -16 29 ",
					"C -17 32 -8 33 -4 34 Z", "M -34 30 ",
					"C -33 41 -34 57 -9 57 ", "M 8 -10 ", "C 21 -14 30 -5 30 7" };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 300) {
			String[] ps = { "M 5 -0 ", "C -2 -0 1 7 -2 11 ",
					"C -6 15 -14 15 -18 19 ", "C -22 23 -14 26 -14 30 ",
					"C -14 34 -33 50 -25 53 ", "C -18 57 -10 46 -2 42 ",
					"C 5 38 5 49 13 45 ", "C 20 41 9 29 17 26 ",
					"C 25 24 24 23 24 19 ", "C 25 16 21 14 17 11 ",
					"C 12 8 13 -0 5 -0 Z", "M -45 46 ",
					"C -49 57 -45 73 -25 73 ", "M 13 -20 ",
					"C 26 -24 32 -11 36 -7 ", "C 40 -3 49 0 49 12 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 366) {
			String[] ps = { "M -34 9 ", "C -32 4 -23 8 -22 8 ",
					"C -20 9 -9 16 -7 14 ", "C -5 13 -5 -2 6 -5 ",
					"C 16 -7 11 13 14 14 ", "C 16 16 30 7 36 13 ",
					"C 39 17 35 22 34 23 ", "C 31 25 23 29 22 32 ",
					"C 21 35 21 34 23 41 ", "C 26 47 28 48 27 54 ",
					"C 26 60 21 65 14 65 ", "C 4 66 -2 44 -9 44 ",
					"C -17 44 -21 54 -30 50 ", "C -38 46 -22 38 -24 29 ",
					"C -26 21 -41 23 -34 9 Z", "M 1 33 ", "C 11 29 8 21 2 22 ",
					"C -4 22 5 30 -7 27 ", "C -18 25 -7 37 1 33 Z",
					"M -21 -10 ", "C -27 -8 -45 -10 -50 10 ", "M 7 81 ",
					"C 23 81 35 79 44 62 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 400) {
			String[] ps = { "M -34 9 ", "C -32 4 -23 8 -22 8 ",
					"C -20 9 -9 16 -7 14 ", "C -5 13 -5 -2 6 -5 ",
					"C 16 -7 11 13 14 14 ", "C 16 16 30 7 36 13 ",
					"C 39 17 35 22 34 23 ", "C 31 25 23 29 22 32 ",
					"C 21 35 21 34 23 41 ", "C 26 47 28 48 27 54 ",
					"C 26 60 21 65 14 65 ", "C 4 66 -2 44 -9 44 ",
					"C -17 44 -21 54 -30 50 ", "C -38 46 -22 38 -24 29 ",
					"C -26 21 -41 23 -34 9 Z", "M 1 33 ", "C 11 29 8 21 2 22 ",
					"C -4 22 5 30 -7 27 ", "C -18 25 -7 37 1 33 Z", "M 7 81 ",
					"C 23 81 35 79 44 62 ", "M 21 -24 ",
					"C -1 -24 -5 -13 -13 -9 ", "C -22 -5 -39 -10 -45 0 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 500) {
			String[] ps = { "M -7 -0 ", "C -13 -0 -27 -35 -41 -18 ",
					"C -56 -0 -24 3 -35 13 ", "C -45 23 -62 6 -62 31 ",
					"C -62 54 -35 34 -31 41 ", "C -27 48 -35 69 -24 69 ",
					"C -13 69 -10 54 -3 54 ", "C 4 54 21 87 35 72 ",
					"C 49 58 28 54 31 44 ", "C 35 34 56 41 56 27 ",
					"C 56 13 35 13 28 10 ", "C 21 6 21 -12 14 -12 ",
					"C 8 -12 0 -0 -7 -0 ", "Z ", "M 4 13 ",
					"C 0 13 -3 17 -7 21 ", "C -10 23 -13 23 -13 27 ",
					"C -13 31 -10 31 -7 34 ", "C -3 38 0 41 4 41 ",
					"C 8 41 10 34 10 31 ", "C 10 27 10 27 10 23 ",
					"C 10 21 8 13 4 13 ", "Z ", "M -7 -25 ",
					"C 4 -18 18 -28 25 -25 ", "C 31 -22 35 -4 45 -4 ",
					"M -41 69 ", "C -35 76 -37 76 -31 79 ",
					"C -24 83 -22 82 -17 79 ", "M -48 83 ",
					"C -45 87 -41 93 -35 93 ", "C -31 93 -31 93 -27 89 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 600) {
			String[] ps = { "M -49 -36 ", "C -36 -45 -9 3 -2 -3 ",
					"C 5 -7 14 -32 29 -30 ", "C 45 -27 26 12 33 11 ",
					"C 41 10 61 -7 63 8 ", "C 66 23 42 29 49 35 ",
					"C 53 39 70 49 60 61 ", "C 48 76 24 46 16 52 ",
					"C 9 57 29 104 5 97 ", "C -16 91 -13 45 -22 50 ",
					"C -25 51 -50 65 -62 52 ", "C -74 39 -35 25 -38 13 ",
					"C -40 2 -63 -27 -49 -36 ", "Z ", "M -22 16 ",
					"C -19 10 -5 19 -2 16 ", "C -0 13 3 3 8 5 ",
					"C 12 7 9 20 11 24 ", "C 13 27 22 24 22 28 ",
					"C 22 32 14 32 11 34 ", "C 9 37 10 48 5 48 ",
					"C -0 49 -0 35 -3 34 ", "C -5 33 -13 45 -18 40 ",
					"C -22 35 -14 32 -15 28 ", "C -15 24 -26 22 -22 16 ", "Z ",
					"M -12 -66 ", "L -5 -30 ", "M 0 23 ",
					"C -6 21 -8 28 -1 28 ", "C 6 28 6 24 0 23 ", "Z ",
					"M 71 -18 ", "C 76 -15 81 -13 83 -2 ", "M -84 49 ",
					"C -81 64 -75 68 -70 70 ", "M -89 -32 ",
					"C -90 -47 -86 -52 -79 -56 ", "M -64 -47 ",
					"C -69 -43 -73 -34 -71 -27 ",
					/*
					 * "M 0 115 ", "C 5 117 15 117 20 115 ",
					 */
					"M 95 70 ", "C 94 79 90 85 80 90 ", "M 83 55 ",
					"C 83 69 78 78 65 83 " };
			ldy = 10;
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 633) {
			String[] ps = { "M -49 -36 ", "C -36 -45 -9 3 -2 -3 ",
					"C 5 -7 14 -32 29 -30 ", "C 45 -27 26 12 33 11 ",
					"C 41 10 61 -7 63 8 ", "C 66 23 42 29 49 35 ",
					"C 53 39 70 49 60 61 ", "C 48 76 24 46 16 52 ",
					"C 9 57 29 104 5 97 ", "C -16 91 -13 45 -22 50 ",
					"C -25 51 -50 65 -62 52 ", "C -74 39 -35 25 -38 13 ",
					"C -40 2 -63 -27 -49 -36 ", "Z ", "M -22 16 ",
					"C -19 10 -5 19 -2 16 ", "C -0 13 3 3 8 5 ",
					"C 12 7 9 20 11 24 ", "C 13 27 22 24 22 28 ",
					"C 22 32 14 32 11 34 ", "C 9 37 10 48 5 48 ",
					"C -0 49 -0 35 -3 34 ", "C -5 33 -13 45 -18 40 ",
					"C -22 35 -14 32 -15 28 ", "C -15 24 -26 22 -22 16 ", "Z ",
					"M -12 -66 ", "L -5 -30 ", "M 0 23 ",
					"C -6 21 -8 28 -1 28 ", "C 6 28 6 24 0 23 ", "Z ",
					"M 71 -18 ", "C 76 -15 81 -13 83 -2 ", "M -84 49 ",
					"C -81 64 -75 68 -70 70 ", "M -89 -32 ",
					"C -90 -47 -86 -52 -79 -56 ", "M -64 -47 ",
					"C -69 -43 -73 -34 -71 -27 ", "M 95 70 ",
					"C 94 79 90 85 80 90 ", "M 83 55 ", "C 83 69 78 78 65 83 ",
					"M -50 10 ", "L -80 -8 ", "M -179 -99 ",
					"C -193 -92 -201 -81 -204 -67 " };
			ldy = 10;
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 700) {
			String[] ps = { "M 23 -4 ", "C 8 3 -16 -61 -41 -43 ",
					"C -62 -28 -27 -11 -34 7 ", "C -41 23 -77 27 -87 53 ",
					"C -94 70 -56 57 -48 63 ", "C -34 74 -48 145 -20 141 ",
					"C 5 138 -6 84 8 77 ", "C 23 70 30 127 58 109 ",
					"C 86 92 55 74 55 60 ", "C 55 49 79 53 79 38 ",
					"C 79 24 62 31 58 14 ", "C 55 -4 74 -16 62 -29 ",
					"C 51 -40 41 -13 23 -4 ", "Z D R ", "M 19 17 ",
					"C 12 17 -2 -1 -6 3 ", "C -9 7 -2 21 -6 24 ",
					"C -9 28 -31 25 -30 28 ", "C -30 31 -16 35 -13 38 ",
					"C -9 42 -16 45 -12 49 ", "C -9 52 4 54 8 56 ",
					"C 16 60 30 67 33 63 ", "C 37 60 26 46 26 38 ",
					"C 26 31 40 14 37 10 ", "C 33 7 23 17 19 17 ", "Z D R ",
					"M 19 38 ", "C 26 42 37 42 37 46 ", "C 37 49 30 56 23 56 ",
					"C 16 56 5 67 1 63 ", "C -2 60 -2 49 -2 46 ",
					"C -2 42 1 31 5 31 ", "C 8 31 12 33 19 38 ", "Z P R ",
					"M -23 -64 ", "C -38 -64 -45 -61 -52 -54 ", "M -101 38 ",
					"C -105 60 -101 74 -84 81 ", "M -112 74 ",
					"C -116 70 -108 81 -101 84 ", "M 76 -36 ",
					"C 69 -43 69 -43 62 -47 ", "M 76 -54 ",
					"C 83 -50 83 -50 86 -47 ", "M 83 70 ",
					"C 86 81 86 84 83 95 ", "M 62 123 ",
					"C 55 131 37 127 33 123 ", "M 76 113 ",
					"C 79 116 83 123 86 127 ", "M 72 7 ",
					"C 83 -1 94 -11 97 -18 ", "M -23 14 ",
					"C -34 7 -73 -15 -80 -18 ", "M 200 200 ",
					"C 200 218 193 228 175 239 ", "M -179 -89 ",
					"C -193 -82 -201 -71 -204 -57 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps);
		} else if (level == 766) {
			String[] ps1 = { "M -202 -65 ", "C -202 -69 -202 -80 -189 -84 ",
					"M -96 25 ", "C -86 19 -81 -2 -94 -23 ", "M 101 -17 ",
					"L 142 -55 ", "M -114 62 ", "C -115 68 -110 80 -99 82 ",
					"M 116 59 ", "C 113 63 115 70 116 76 ",
					"C 116 79 119 90 118 92 ", "C 117 100 112 108 95 110 ",
					"M -218 -74 ", "C -218 -80 -213 -91 -205 -93 ",
					"M -94 127 ", "C -90 136 -83 142 -74 146 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps1);
			String[] ps2 = { "M -101 -89 ", "C -101 -89 -10 -6 -7 -8 ",
					"C -4 -11 -7 -49 1 -47 ", "C 10 -45 22 -24 27 -25 ",
					"C 32 -26 82 -59 82 -59 ", "L 59 16 ",
					"C 59 16 106 13 107 16 ", "C 108 19 72 48 72 52 ",
					"C 72 57 105 83 98 91 ", "C 91 99 50 79 44 86 ",
					"C 38 92 52 166 52 166 ", "L -1 103 ", "L -14 138 ",
					"C -14 138 -23 110 -25 108 ", "C -26 107 -54 138 -70 123 ",
					"C -86 108 -36 74 -44 65 ", "C -53 56 -95 75 -101 58 ",
					"C -107 41 -52 33 -53 24 ", "C -53 15 -101 -89 -101 -89 ",
					"Z " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, true,
					ps2);
			String[] ps3 = { "M -36 12 ", "L 1 21 ", "L 26 1 ", "L 29 29 ",
					"L 52 41 ", "L 25 54 ", "L 28 71 ", "L 7 63 ", "L 8 87 ",
					"L -11 58 ", "L -31 57 ", "L -19 32 ", "L -36 12 ", "Z ",
					"M 4 35 ", "C 6 31 18 19 18 21 ", "C 18 24 15 34 15 34 ",
					"C 15 34 29 42 28 44 ", "C 28 45 17 41 15 43 ",
					"C 14 45 18 57 16 56 ", "C 11 55 3 50 2 45 ",
					"C 1 41 -21 50 -9 39 ", "C -1 32 3 37 4 35 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps3);
		} else if (level == 800) {
			String[] ps1 = { "M -202 -65 ", "C -202 -69 -202 -80 -189 -84 ",
					"M -96 25 ", "C -86 19 -81 -2 -94 -23 ", "M -2 -58 ",
					"C 23 -65 32 -74 41 -75 ", "M 54 -64 ",
					"C 48 -65 31 -55 20 -51 ", "M 101 -17 ", "L 142 -55 ",
					"M -114 62 ", "C -115 68 -110 80 -99 82 ", "M 116 59 ",
					"C 113 63 115 70 116 76 ", "C 116 79 119 90 118 92 ",
					"C 117 100 112 108 95 110 ", "M 66 136 ", "L 87 156 ",
					"M 58 152 ", "L 64 160 ", "M 191 57 ",
					"C 196 55 200 54 202 44 ", "M 203 71 ",
					"C 211 70 218 60 219 53 ", "M -218 -74 ",
					"C -218 -80 -213 -91 -205 -93 ", "M 115 -212 ",
					"C 116 -220 114 -230 95 -233 ", "M 116 -246 ",
					"C 128 -244 135 -238 135 -226 ", "M 189 231 ",
					"C 207 225 207 210 206 201 ", "M -186 233 ",
					"C -200 228 -203 219 -203 205 ", "M -94 127 ",
					"C -90 136 -83 142 -74 146 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps1);
			String[] ps2 = { "M -101 -89 ", "C -101 -89 -10 -6 -7 -8 ",
					"C -4 -11 -7 -49 1 -47 ", "C 10 -45 22 -24 27 -25 ",
					"C 32 -26 82 -59 82 -59 ", "L 59 16 ",
					"C 59 16 106 13 107 16 ", "C 108 19 72 48 72 52 ",
					"C 72 57 105 83 98 91 ", "C 91 99 50 79 44 86 ",
					"C 38 92 52 166 52 166 ", "L -1 103 ", "L -14 138 ",
					"C -14 138 -23 110 -25 108 ", "C -26 107 -54 138 -70 123 ",
					"C -86 108 -36 74 -44 65 ", "C -53 56 -95 75 -101 58 ",
					"C -107 41 -52 33 -53 24 ", "C -53 15 -101 -89 -101 -89 ",
					"Z " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, true,
					ps2);
			String[] ps3 = { "M -36 12 ", "L 1 21 ", "L 26 1 ", "L 29 29 ",
					"L 52 41 ", "L 25 54 ", "L 28 71 ", "L 7 63 ", "L 8 87 ",
					"L -11 58 ", "L -31 57 ", "L -19 32 ", "L -36 12 ", "Z ",
					"M 4 35 ", "C 6 31 18 19 18 21 ", "C 18 24 15 34 15 34 ",
					"C 15 34 29 42 28 44 ", "C 28 45 17 41 15 43 ",
					"C 14 45 18 57 16 56 ", "C 11 55 3 50 2 45 ",
					"C 1 41 -21 50 -9 39 ", "C -1 32 3 37 4 35 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps3);
		} else if (level == 900) {
			String[] ps1 = { "M -12 -79 ", "C -21 -79 -26 -78 -34 -73 ",
					"M -115 -238 ", "C -122 -231 -126 -223 -126 -212 ",
					"M -96 -227 ", "C -107 -220 -107 -208 -107 -205 ",
					"M  93 -238 ", "C 108 -238 115 -231 108 -220 ",
					"M 100 -249 ", "C 115 -249 122 -242 122 -234 ",
					"M -8 -68 ", "C 7 -61 37 -76 51 -65 ", "M 143 24 ",
					"C 144 19 158 -39 118 -50 ", "M 162 13 ",
					"C 162 -9 169 -54 125 -65 ", "M 200 -46 ",
					"C 200 -32 200 -6 200 5 ", "M 215 -43 ",
					"C 215 -61 215 -21 215 -6 ", "M 158 68 ",
					"C 162 49 162 42 158 35 ", "M 151 61 ",
					"C 151 79 147 79 143 83 ", "M 110 123 ",
					"C 103 131 99 134 92 138 ", "M 118 134 ",
					"C 110 142 103 145 99 149 ", "M 95 116 ",
					"C 92 119 92 119 88 123 ", "M 48 175 ",
					"C 33 186 3 189 -15 189 ", "M 37 193 ",
					"C 22 197 11 204 3 204 ", "M -30 153 ",
					"C -45 145 -48 149 -56 149 ", "C -63 149 -70 145 -78 134 ",
					"M -19 142 ", "C -30 134 -37 134 -52 138 ",
					"C -67 142 -78 119 -81 112 ", "M -177 236 ",
					"C -192 236 -196 231 -199 213 ", "M -192 251 ",
					"C -210 243 -210 240 -214 225 ", "M -92 49 ",
					"C -96 57 -92 68 -92 68 ", "M -104 49 ",
					"C -107 57 -104 64 -104 68 ", "M -89 -24 ",
					"C -96 -13 -96 -9 -100 -2 ", "M -96 -32 ",
					"C -104 -24 -104 -17 -104 -13 ", "M -196 -43 ",
					"C -196 -28 -196 -9 -196 2 ", "M -210 -32 ",
					"C -210 -21 -210 -13 -210 -2 " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps1);
			String[] ps2 = { "M 7 -32 ", "L 11 -76 ", "L 62 -32 ",
					"L 158 -120 ", "L 99 2 ", "L 132 38 ", "L 92 38 ",
					"L 180 142 ", "L 44 86 ", "L 70 142 ", "L 22 101 ",
					"L 7 226 ", "L -33 105 ", "L -185 142 ", "L -59 61 ",
					"L -110 27 ", "L -59 -13 ", "L -144 -94 ", "L 7 -32 ", "Z " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, true,
					ps2);
			String[] ps3 = { "M 2 27 ", "C 13 14 -1 -12 10 -18 ",
					"C 20 -23 21 23 47 12 ", "C 83 -3 92 18 73 28 ",
					"C 20 54 65 66 53 74 ", "C 43 83 39 67 21 64 ",
					"C 4 61 -32 78 -38 65 ", "C -43 52 -6 36 2 27 ", "Z " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, false,
					ps3);
			String[] ps4 = { "M 3 5 ", "C 14 5 33 -21 37 -17 ",
					"C 40 -13 18 13 18 20 ", "C 18 27 37 42 33 46 ",
					"C 29 49 15 36 7 38 ", "C -4 42 -15 83 -19 83 ",
					"C -22 83 -8 46 -15 35 ", "C -22 24 -41 24 -37 20 ",
					"C -33 16 -11 24 -8 20 ", "C -4 16 -15 2 -11 -2 ",
					"C -8 -6 -0 5 3 5 ", "Z " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, true,
					ps4);
			String[] ps5 = { "M 2 18 ", "C -9 29 7 29 7 24 ",
					"C 7 18 4 16 2 18 ", "Z " };
			Painter.paint(g, ldx, ldy, lsc, lnWidth, lineColor, bgColor, true,
					ps5);
		}
	}

	// --------------------------------------------------------
	// Helper methods
	// --------------------------------------------------------
	private double valueAt(int level, double x1, double x5, double x9) {
		double x = x5;
		if (level < centerLevel) {
			double a = levelWeightDown(level);
			x = a * x1 + (1.0 - a) * x5;
		} else if (level > centerLevel) {
			double a = levelWeightUp(level);
			x = a * x9 + (1.0 - a) * x5;
		}
		return x;
	}

	private double downValueAt(int level, double x1, double x5) {
		double x = x5;
		if (level < centerLevel) {
			double a = levelWeightDown(level);
			x = a * x1 + (1.0 - a) * x5;
		}
		return x;
	}

	private double upValueAt(int level, double x5, double x9) {
		double x = x5;
		if (level > centerLevel) {
			double a = levelWeightUp(level);
			x = a * x9 + (1.0 - a) * x5;
		}
		return x;
	}

	private int arousalLevel(int level) {
		for (int i = 1; i < arousalLevelValue.length; i++) {
			if (level < (arousalLevelValue[i - 1] + arousalLevelValue[i]) / 2) {
				return arousalLevelValue[i - 1];
			}
		}
		return arousalLevelValue[arousalLevelValue.length - 1];
	}

	private int lowResLevel(int level) {
		return (level + (levelResolution - 1) / 2) / levelResolution;
	}

	private double levelWeight(int level) {
		return (double) (level - minLevel) / (double) (maxLevel - minLevel);
	}

	private double levelWeightDown(int level) {
		return (double) (level - centerLevel)
				/ (double) (minLevel - centerLevel);
	}

	private double levelWeightUp(int level) {
		return (double) (level - centerLevel)
				/ (double) (maxLevel - centerLevel);
	}

	private void grid(Graphics2D g) {
		double d = 10.0;
		g.setPaint(Color.lightGray);
		g.setStroke(new BasicStroke(1F));
		for (double x = frameLeftX + d; x < frameRightX; x += d) {
			g.drawLine((int) x, (int) frameTopY, (int) x, (int) frameBottomY);
		}
		for (double y = frameTopY + d; y < frameBottomY; y += d) {
			g.drawLine((int) frameLeftX, (int) y, (int) frameRightX, (int) y);
		}
	}
}
