package de.pxlab.awtx;

import java.awt.*;

public class ExtendedPopupMenu extends PopupMenu {
	private int x, y;

	public ExtendedPopupMenu() {
		super();
	}

	public void show(Component origin, int x, int y) {
		this.x = x;
		this.y = y;
		super.show(origin, x, y);
	}

	public Point getActivationPoint() {
		return (new Point(x, y));
	}
}
