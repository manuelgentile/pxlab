package de.pxlab.awtx;

/**
 * A Canvas which uses double buffering transparently. This class exists only
 * because it is needed for Java 1.1 in order to simulate the buffering strategy
 * of Java 1.4 for RealTimeDisplayPanel objects.
 */
public class BufferedCanvas extends java.awt.Canvas {
	public BufferedCanvas() {
		super();
	}

	public BufferedCanvas(java.awt.GraphicsConfiguration config) {
		super(config);
	}
}
