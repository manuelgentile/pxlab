package de.pxlab.pxl;

/** Load a static Scalable Vector Graphics (SVG) file and convert it
 into a bitmap image.

 <p>This file is inspired by the batik demo application
 'org.apache.batik.apps.slideshow' of the Apache Software Foundation.

 <p>See <a
 href="http://www.apache.org/batik">http://www.apache.org/batik</a>
 for more information about the batik project.

 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.batik.bridge.*;
import org.apache.batik.gvt.*;
import org.apache.batik.gvt.renderer.*;
import org.w3c.dom.*;
import org.w3c.dom.svg.*;

public class SVGLoader {
	private StaticRenderer renderer;
	private UserAgent userAgent;
	private DocumentLoader loader;
	private BridgeContext ctx;

	/** Create a loader for SVG files. */
	public SVGLoader() {
		renderer = new StaticRenderer();
		userAgent = new UserAgentAdapter();
		loader = new DocumentLoader(userAgent);
		ctx = new BridgeContext(userAgent, loader);
	}

	/**
	 * Load a SVG file and convert it into a bitmap image.
	 * 
	 * @param fp
	 *            the file path of the SVG file.
	 * @param w
	 *            the with of the image where this file should be rendered into.
	 * @param h
	 *            the height of the image where this file should be rendered
	 *            into.
	 * @return a bitmap which containes the rendered image.
	 */
	public BufferedImage load(String fp, int w, int h) {
		BufferedImage image = null;
		renderer.setDoubleBuffered(true);
		GraphicsNode gvtRoot = null;
		GVTBuilder builder = new GVTBuilder();
		File f = new File(fp);
		// copied from org.apache.batik.apps.slideshow.Main.java;
		w += 2;
		h += 2;
		// -----------------------------------------------------
		try {
			Debug.show(Debug.FILES, "SVGLoader.load() reading " + fp);
			// Document svgDoc = loader.loadDocument(f.toURL().toString());
			Document svgDoc = loader.loadDocument(fp);
			Debug.show(Debug.FILES, "SVGLoader.load() building " + fp);
			gvtRoot = builder.build(ctx, svgDoc);
			Debug.show(Debug.FILES, "SVGLoader.load() rendering " + fp);
			renderer.setTree(gvtRoot);
			Element elt = ((SVGDocument) svgDoc).getRootElement();
			renderer.setTransform(ViewBox.getViewTransform(null, elt, w, h));
			renderer.updateOffScreen(w, h);
			Rectangle r = new Rectangle(0, 0, w, h);
			renderer.repaint(r);
			Debug.show(Debug.FILES, "SVGLoader.load() painting " + fp);
			image = renderer.getOffScreen();
		} catch (Exception ex) {
			System.out
					.println("SVGLoader.load() error when trying to load file "
							+ f);
			// ex.printStackTrace();
		}
		return image;
	}
}
