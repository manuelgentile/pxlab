package de.pxlab.pxl;

import java.awt.*;

/**
 * A series of lines across the display screen which can be used as a ruler to
 * measure screen size.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class RulerElement extends DisplayElement {
	protected boolean horizontal;
	protected int unitSize = 10;
	protected ExPar centerColorPar;

	public RulerElement(ExPar i) {
		type = LINE;
		colorPar = i;
		centerColorPar = i;
		horizontal = true;
	}

	public RulerElement(ExPar i, ExPar ci, boolean p) {
		type = LINE;
		colorPar = i;
		centerColorPar = ci;
		horizontal = p;
	}

	public void setHorizontal(boolean a) {
		horizontal = a;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void setUnitSize(int s) {
		unitSize = s;
	}

	public int getUnitSize() {
		return unitSize;
	}

	public void show() {
		int x, y;
		if (horizontal) {
			x = 0;
			y = -displayHeight / 2;
			graphics.setColor(centerColorPar.getDevColor());
			graphics.drawLine(x, y, x, y + displayHeight - 1);
			x += unitSize;
			graphics.setColor(colorPar.getDevColor());
			while (x < displayWidth / 2) {
				graphics.drawLine(x, y, x, y + displayHeight - 1);
				x += unitSize;
			}
			x = -unitSize;
			while (x > -displayWidth / 2) {
				graphics.drawLine(x, y, x, y + displayHeight - 1);
				x -= unitSize;
			}
		} else {
			x = -displayWidth / 2;
			y = 0;
			graphics.setColor(centerColorPar.getDevColor());
			graphics.drawLine(x, y, x + displayWidth - 1, y);
			y += unitSize;
			graphics.setColor(colorPar.getDevColor());
			while (y < displayHeight / 2) {
				graphics.drawLine(x, y, x + displayWidth - 1, y);
				y += unitSize;
			}
			y = -unitSize;
			while (y > -displayHeight / 2) {
				graphics.drawLine(x, y, x + displayWidth - 1, y);
				y -= unitSize;
			}
		}
		setBounds(-displayWidth / 2, -displayHeight / 2, displayWidth,
				displayHeight);
	}
}
