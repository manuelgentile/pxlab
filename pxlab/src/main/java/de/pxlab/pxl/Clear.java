package de.pxlab.pxl;

/**
 * This display element clears the screen.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 01/26/01 use an ExPar for color
 * 
 * 06/18/01 Fixed bug with coordinate positions
 */
public class Clear extends DisplayElement {
	public Clear() {
		type = DisplayElement.CLEAR;
		colorPar = ExPar.ScreenBackgroundColor;
	}

	public Clear(ExPar i) {
		type = DisplayElement.CLEAR;
		colorPar = i;
	}

	public void show() {
		graphics.setColor(colorPar.getDevColor());
		graphics.fillRect(-displayWidth / 2, -displayHeight / 2, displayWidth,
				displayHeight);
		setBounds(-displayWidth / 2, -displayHeight / 2, displayWidth,
				displayHeight);
	}
}
