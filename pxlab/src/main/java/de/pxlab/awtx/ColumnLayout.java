package de.pxlab.awtx;

import java.awt.*;

/**
 * This class has been copied from the book
 * 
 * <p>
 * Geary, D. M. (1999). Graphic Java 1.2, Mastering the JFC, Vol I: AWT (3rd
 * ed.). Sun Microsystems Press.
 */
public class ColumnLayout implements LayoutManager {
	static private int _defaultGap = 5;
	private int gap;
	private Orientation horizontalOrientation;
	private Orientation verticalOrientation;

	public ColumnLayout() {
		this(Orientation.CENTER, Orientation.CENTER, _defaultGap);
	}

	public ColumnLayout(int gap) {
		this(Orientation.CENTER, Orientation.CENTER, gap);
	}

	public ColumnLayout(Orientation horizontalOrient, Orientation verticalOrient) {
		this(horizontalOrient, verticalOrient, _defaultGap);
	}

	public ColumnLayout(Orientation horizontalOrient,
			Orientation verticalOrient, int gap) {
		if (gap < 0
				|| (horizontalOrient != Orientation.LEFT
						&& horizontalOrient != Orientation.CENTER
						&& horizontalOrient != Orientation.RIGHT && horizontalOrient != Orientation.HFILL)
				|| (verticalOrient != Orientation.TOP
						&& verticalOrient != Orientation.CENTER && verticalOrient != Orientation.BOTTOM)) {
			throw new IllegalArgumentException("bad gap or orientation");
		}
		this.gap = gap;
		this.verticalOrientation = verticalOrient;
		this.horizontalOrientation = horizontalOrient;
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container target) {
		Insets insets = target.getInsets();
		Dimension dim = new Dimension(0, 0);
		int ncomponents = target.getComponentCount();
		Component comp;
		Dimension d;
		for (int i = 0; i < ncomponents; i++) {
			comp = target.getComponent(i);
			if (comp.isVisible()) {
				d = comp.getPreferredSize();
				if (i > 0)
					dim.height += gap;
				dim.height += d.height;
				dim.width = Math.max(d.width, dim.width);
			}
		}
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		return dim;
	}

	public Dimension minimumLayoutSize(Container target) {
		Insets insets = target.getInsets();
		Dimension dim = new Dimension(0, 0);
		int ncomponents = target.getComponentCount();
		Component comp;
		Dimension d;
		for (int i = 0; i < ncomponents; i++) {
			comp = target.getComponent(i);
			if (comp.isVisible()) {
				d = comp.getMinimumSize();
				dim.width = Math.max(d.width, dim.width);
				dim.height += d.height;
				if (i > 0)
					dim.height += gap;
			}
		}
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		return dim;
	}

	public void layoutContainer(Container target) {
		Insets insets = target.getInsets();
		int top = insets.top;
		int left = 0;
		int width = 0;
		int ncomponents = target.getComponentCount();
		Dimension preferredSize = target.getPreferredSize();
		Dimension targetSize = target.getSize();
		Component comp;
		Dimension ps;
		if (verticalOrientation == Orientation.CENTER)
			top += (targetSize.height / 2) - (preferredSize.height / 2);
		else if (verticalOrientation == Orientation.BOTTOM)
			top = targetSize.height - preferredSize.height + insets.top;
		for (int i = 0; i < ncomponents; i++) {
			comp = target.getComponent(i);
			left = insets.left;
			if (comp.isVisible()) {
				ps = comp.getPreferredSize();
				width = ps.width;
				if (horizontalOrientation == Orientation.CENTER)
					left = (targetSize.width / 2) - (ps.width / 2);
				else if (horizontalOrientation == Orientation.RIGHT) {
					left = targetSize.width - ps.width - insets.right;
				} else if (horizontalOrientation == Orientation.HFILL) {
					width = targetSize.width - insets.left - insets.right;
				}
				comp.setBounds(left, top, width, ps.height);
				top += ps.height + gap;
			}
		}
	}
}
