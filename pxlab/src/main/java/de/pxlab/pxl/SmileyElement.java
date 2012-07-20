package de.pxlab.pxl;

import java.awt.*;
import java.awt.geom.*;

/**
 * A Smiley showing various mood states.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/10/21
 */
public class SmileyElement extends Ellipse {
	private ExPar borderColorPar;
	private int mood = ResponseCodes.CORRECT;

	public SmileyElement(ExPar i, ExPar j) {
		super(i);
		borderColorPar = j;
	}

	public void setMood(int m) {
		mood = m;
	}

	public void show() {
		if (size.width > 0 && size.height > 0) {
			graphics2D.setPaint(colorPar.getDevColor());
			graphics2D.fillOval(location.x, location.y, size.width - 1,
					size.height - 1);
			graphics2D.setPaint(borderColorPar.getDevColor());
			graphics2D.setStroke(new BasicStroke((float) lineWidth));
			graphics2D.drawOval(location.x, location.y, size.width - 1,
					size.height - 1);
			int da = 7 * size.width / 24;
			int sa = size.width / 6;
			int wm = size.width / 3;
			int wm2 = 5 * wm / 4;
			int x = location.x + size.width / 2 - da / 2 - sa / 2;
			int y = location.y + size.height / 3 - sa / 2;
			graphics2D.fillOval(x, y, sa, sa);
			graphics2D.fillOval(x + da, y, sa, sa);
			switch (mood) {
			case ResponseCodes.CORRECT:
				graphics2D.drawArc(location.x + size.width / 2 - wm2 / 2,
						location.y + size.height / 2 - wm / 3, wm2, wm2, 210,
						120);
				break;
			case ResponseCodes.FALSE:
				graphics2D.drawArc(location.x + size.width / 2 - wm2 / 2,
						location.y + size.height / 2 + wm / 2, wm2, wm2, 30,
						120);
				break;
			default:
				graphics2D.drawLine(location.x + size.width / 2 - wm / 2,
						location.y + 2 * size.height / 3, location.x
								+ size.width / 2 + wm / 2, location.y + 2
								* size.height / 3);
			}
			setBounds(new Rectangle(location, size));
		}
	}
}
