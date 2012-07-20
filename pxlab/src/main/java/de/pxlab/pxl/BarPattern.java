package de.pxlab.pxl;

/**
 * A series of filled rectangles.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 11/12/03
 */
public class BarPattern extends DisplayElement {
	protected int rows = 1;
	protected int columns = 1;
	protected ExPar colorSamplePar;

	public BarPattern(ExPar i) {
		type = DisplayElement.BAR;
		colorPar = i;
	}

	public BarPattern(ExPar i, ExPar s) {
		type = DisplayElement.BAR;
		colorPar = i;
		colorSamplePar = s;
	}

	public void setRowsColumns(int rws, int cls) {
		rows = rws;
		columns = cls;
	}

	public void setColorSamplePar(ExPar cs) {
		colorSamplePar = cs;
	}

	public void show() {
		double[] cd = colorPar.getDoubleArray();
		if (colorSamplePar != null) {
			double[] csp = colorSamplePar.getDoubleArray();
			if (csp.length >= 3) {
				cd = csp;
			}
		}
		PxlColor c;
		int n = rows * columns;
		int dx = size.width / columns;
		int dy = size.height / rows;
		int left_x = location.x;
		int top_y = location.y;
		int x;
		int y = top_y;
		int k = 0;
		for (int i = 0; i < rows; i++) {
			x = left_x;
			for (int j = 0; j < columns; j++) {
				if (cd.length >= ((k + 3))) {
					c = new YxyColor(cd[k], cd[k + 1], cd[k + 2]);
					// System.out.println("# " + c);
				} else {
					c = ExPar.ScreenBackgroundColor.getPxlColor();
					// System.out.println("o");
				}
				graphics.setColor(c.dev());
				graphics.fillRect(x, y, dx, dy);
				k += 3;
				x += dx;
			}
			y += dy;
		}
		setBounds(left_x, top_y, columns * dx, rows * dy);
	}
}
