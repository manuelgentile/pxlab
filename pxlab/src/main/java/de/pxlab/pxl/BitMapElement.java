package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;

import java.util.*;

/**
 * A rectangular bitmap based on the BufferedImage class. This may be a computed
 * bitmap or it may be an image contained in a file.
 * 
 * @author H. Irtel
 * @version 0.3.0
 * @see java.awt.image.BufferedImage
 */
/*
 * 
 * 11/22/01 allow URLs as image directories
 * 
 * 11/26/01 ignore null images
 * 
 * 2004/11/24 added selectedImage
 * 
 * 2005/05/30 added createMessage()
 * 
 * 2006/02/10 added support for scalable vector graphics images.
 * 
 * 2007/05/12 moved all methods from the class PictureElement into this file.
 */
public class BitMapElement extends DisplayElement implements
		PositionReferenceCodes {
	/** The main image reference. */
	protected BufferedImage image = null;
	/** The image reference for the selected image version. */
	protected BufferedImage selectedImage = null;
	/** This is the clipping rectangle for showing the bitmap. */
	protected Rectangle clipRect = null;
	/** Reference point for image positioning. */
	protected int referencePoint = MIDDLE_CENTER;
	/** Indicates that this image must be scaled. */
	private boolean scaling = false;
	/**
	 * If not equal to 1f then the image has to be scaled before being drawn.
	 * Only non-clipped images may be scaled.
	 */
	private float scalingFactor = 1f;
	private String filePath = null;
	/** An array of images for a movie. */
	protected BufferedImage[] movie = null;

	public BitMapElement() {
	}

	public BitMapElement(ExPar i) {
		colorPar = i;
	}

	public void show() {
		show((isSelected() && selectedImage != null) ? selectedImage : image);
	}

	protected void show(BufferedImage image) {
		if (image == null)
			return;
		int dx = referencePoint / 3;
		int dy = referencePoint % 3;
		if ((dx != 0) || (dy != 0)) {
			if (dx > 0) {
				dx = -size.width / 2 * dx;
			}
			if (dy > 0) {
				dy = size.height / 2 * dy;
			}
		}
		int x = location.x + dx;
		int y = location.y + dy - size.height;
		// System.out.println("BitMapElement.show() at " + x + " " + y);
		if (clipRect != null) {
			// System.out.println("BitMapElement.show(): showing clipped image.");
			Rectangle cr = graphics2D.getClipBounds();
			graphics2D.setClip(clipRect.x, clipRect.y, clipRect.width,
					clipRect.height);
			graphics2D.drawImage(image, new AffineTransform(scalingFactor, 0f,
					0f, scalingFactor, x, y), null);
			if (cr != null)
				graphics2D.setClip(cr.x, cr.y, cr.width, cr.height);
			else
				graphics2D.setClip(null);
			if (validBounds) {
				setBounds(clipRect);
			}
		} else {
			graphics2D.drawImage(image, new AffineTransform(scalingFactor, 0f,
					0f, scalingFactor, x, y), null);
			if (validBounds) {
				setBounds(new Rectangle(x, y, size.width, size.height));
				// System.out.println("BitMapElement.show(): bounds = " +
				// getBounds());
			}
		}
		if (isSelected() && selectedImage == null)
			showSelection();
	}

	public void setImage(BufferedImage i) {
		image = i;
		setSize(image.getWidth(), image.getHeight());
		setScalingFactor(1f);
	}

	public void setSelectedImage(BufferedImage i) {
		selectedImage = i;
	}

	/**
	 * Set this element's image file.
	 * 
	 * @param dir
	 *            the directory where the image may be found. This can be null.
	 *            In this case the parameter fn must contain the complete file
	 *            path.
	 * @param fn
	 *            the name of the image file.
	 */
	public void setImage(String dir, String fn) {
		setImage(dir, fn, 0, 0);
	}

	/**
	 * Set this element's image file.
	 * 
	 * @param dir
	 *            the directory where the image may be found. This can be null.
	 *            In this case the parameter fn must contain the complete file
	 *            path.
	 * @param fn
	 *            the name of the image file.
	 * @param w
	 *            intended width of the image on screen.
	 * @param h
	 *            intended height of the image on screen.
	 */
	public void setImage(String dir, String fn, int w, int h) {
		boolean isSVG = fn.toLowerCase().endsWith(".svg");
		int ww = (isSVG && (w <= 0)) ? displayWidth : w;
		int hh = (isSVG && (h <= 0)) ? displayHeight : h;
		BufferedImage img = FileBase.loadImage(dir, fn, ww, hh);
		if (img == null) {
			img = createMessage("Not found:  " + dir + "/" + fn);
		}
		if (img != null) {
			setImage(img);
			filePath = fn;
		}
	}

	/**
	 * Set this element's selected version image file.
	 * 
	 * @param dir
	 *            the directory where the image may be found. This can be null.
	 *            In this case the parameter fn must contain the complete file
	 *            path.
	 * @param fn
	 *            the name of the image file.
	 */
	public void setSelectedImage(String dir, String fn) {
		// System.out.println("PictureElement.setSelectedImage(): " + fn);
		if (fn != null) {
			BufferedImage img = FileBase.loadImage(dir, fn);
			if (img != null) {
				setSelectedImage(img);
				// System.out.println("PictureElement.setSelectedImage(): " +
				// fn);
			}
		}
	}

	/**
	 * Set this element's selected version image file.
	 * 
	 * @param dir
	 *            the directory where the image may be found. This can be null.
	 *            In this case the parameter fn must contain the complete file
	 *            path.
	 * @param fn
	 *            the name of the image file.
	 * @param w
	 *            intended width of the image on screen.
	 * @param h
	 *            intended height of the image on screen.
	 */
	public void setSelectedImage(String dir, String fn, int w, int h) {
		if (fn != null) {
			if (w == 0 && h == 0) {
				w = displayWidth;
				h = displayHeight;
			}
			BufferedImage img = FileBase.loadImage(dir, fn, w, h);
			if (img != null) {
				setSelectedImage(img);
			}
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public BufferedImage getImage() {
		return (image);
	}

	public void setMovieLength(int n) {
		if (n != 0) {
			if ((movie == null) || (movie.length != n)) {
				movie = new BufferedImage[n];
			}
		} else {
			if (movie != null) {
				for (int i = 0; i < movie.length; i++) {
					movie[i].flush();
				}
			}
			movie = null;
		}
	}

	public void setMovieFrame(BufferedImage i, int n) {
		setImage(i);
		if (movie != null) {
			if (n < movie.length) {
				movie[n] = i;
			}
		}
	}

	public BufferedImage getMovieFrame(int n) {
		BufferedImage i = null;
		if (movie != null) {
			if (n < movie.length) {
				i = movie[n];
			}
		}
		return i;
	}

	public void setActiveMovieFrame(int n) {
		if (movie != null) {
			if (n < movie.length) {
				image = movie[n];
			}
		}
	}

	public HashMap getColorHistogram() {
		HashMap hm = new HashMap(1000);
		int w = image.getWidth();
		int h = image.getHeight();
		Integer key, cnt;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				key = new Integer(image.getRGB(x, y));
				if (hm.containsKey(key)) {
					cnt = (Integer) hm.get(key);
					hm.put(key, new Integer(cnt.intValue() + 1));
				} else {
					hm.put(key, new Integer(1));
				}
			}
		}
		return hm;
	}

	/**
	 * Find the color at the given image point.
	 * 
	 * @param x
	 *            horizontal position relative to the element's bounding box
	 * @param y
	 *            vertical position relative to the element's bounding box
	 */
	public PxlColor getColorAt(int x, int y) {
		// System.out.println("BitMapElement.getColorAt() x="+x+", y="+y);
		PxlColor c = new PxlColor(new Color(image.getRGB(x, y)));
		// System.out.println("BitMapElement.getColorAt() x="+x+", y="+y+", c="
		// + c);
		return c;
	}

	/*
	 * public void setColorParFrom(int x, int y) { colorPar.set(getColorAt(x,
	 * y)); }
	 */
	public static void printColorHistogram(HashMap hm) {
		for (Iterator it = hm.keySet().iterator(); it.hasNext();) {
			Integer key = (Integer) it.next();
			int cnt = ((Integer) (hm.get(key))).intValue();
			PxlColor pc = new PxlColor(new Color(key.intValue()));
			System.out.println(pc.toString() + ": " + cnt);
		}
	}

	public void setScalingFactor(double f) {
		scalingFactor = (float) f;
		// System.out.println("BitMapElement.setScalingFactor() = " +
		// scalingFactor);
		setSize((int) (f * image.getWidth() + 0.5),
				(int) (f * image.getHeight() + 0.5));
	}

	public void setClipRect(int x, int y, int w, int h) {
		clipRect = new Rectangle(x, y, w, h);
	}

	public Rectangle getClipRect() {
		return (clipRect);
	}

	/** Set a BitMapElement object's reference position code. */
	public void setReferencePoint(int p) {
		referencePoint = Math.abs(p) % 9;
	}

	/** Get a BitMapElement object's reference position code. */
	public int getReferencePoint() {
		return (referencePoint);
	}

	public void flush() {
		if (image != null)
			image.flush();
	}

	/**
	 * Create an image which contains a message. This may be used to create al
	 * alternative image for the case where the intended image is not found.
	 */
	public static BufferedImage createMessage(String txt) {
		int w = 1024;
		int h = 48;
		BufferedImage imgBuffer = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		Graphics ig = imgBuffer.getGraphics();
		Font font = new Font("Sans", Font.PLAIN, 18);
		ig.setColor(Color.blue);
		ig.fillRect(0, 0, w, h);
		ig.setColor(Color.yellow);
		ig.setFont(font);
		FontMetrics fm = ig.getFontMetrics(font);
		int txtWidth = fm.stringWidth(txt);
		ig.drawString(txt, (w - txtWidth) / 2, (h + fm.getAscent()) / 2);
		ig.dispose();
		return imgBuffer;
	}
}
