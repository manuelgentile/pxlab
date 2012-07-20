package de.pxlab.gui;

import java.awt.*;

/**
 * A horizontal bar which shows the progress of some task.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ProgressBar extends Canvas {
	private long min;
	private long max;
	private long value = 0L;
	private Insets insets = new Insets(10, 10, 10, 10);

	/** Create a progress bar with the range [0, 1000]. */
	public ProgressBar() {
		this.min = 0L;
		this.max = 1000L;
		value = min;
	}

	/** Create a progress bar with the given range. */
	public ProgressBar(long min, long max) {
		this.min = min;
		this.max = max;
		value = min;
	}

	/**
	 * Set this progress bar's value and update the display immediately. Note
	 * that the progress bar updates its display not from within the event queue
	 * but does an immediate update from within the current thread.
	 */
	public void setValue(long v) {
		value = v;
		showStatus();
	}

	/** Set this progress bar's range of values. */
	public void setRange(long min, long max) {
		this.min = min;
		this.max = max;
		// show();
	}

	/**
	 * Update the display immediately. Note that the progress bar updates its
	 * display not from within the event queue but does an immediate update from
	 * within the current thread.
	 */
	private void showStatus() {
		Graphics g = getGraphics();
		paint(g);
		g.dispose();
	}

	/** Return this bar's insets. */
	public Insets getInsets() {
		return insets;
	}

	/** Set this bar's insets. */
	public void setInsets(Insets ins) {
		insets = (Insets) ins.clone();
	}

	public Dimension getPreferredSize() {
		return new Dimension(200 + insets.left + insets.right, 16 + insets.top
				+ insets.bottom);
	}

	public Dimension getMinimumSize() {
		return new Dimension(100 + insets.left + insets.right, 16 + insets.top
				+ insets.bottom);
	}

	/** Paint this progress bar to the given graphics context. */
	public void paint(Graphics g) {
		Dimension sz = getSize();
		int fw = sz.width - insets.left - insets.right - 2;
		int fh = sz.height - insets.top - insets.bottom - 2;
		g.setColor(SystemColor.control);
		g.fillRect(0, 0, sz.width, sz.height);
		g.fill3DRect(insets.left, insets.top, fw + 2, fh + 2, false);
		int w = (int) (fw * ((double) value / (double) (max - min)));
		if (w > 0) {
			g.setColor(Color.blue);
			g.fillRect(insets.left + 1, insets.top + 1, w, fh);
		}
		g.setColor(getBackground());
		g.fillRect(insets.left + w + 1, insets.top + 1, fw - w, fh);
	}
}
