package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This display simulates Josef Albers' Homage to the Square series.
 * 
 * @author H. Irtel
 * @version 0.3.2
 */
/*
 * 02/13/01
 */
public class VisionDemonstrationsLogo extends Display {
	public ExPar Square1Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			82.5, 0.405, 0.498)), "First square color");
	public ExPar Square2Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			72.0, 0.424, 0.49)), "Second square color");
	public ExPar Square3Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			61.2, 0.444, 0.473)), "Third square color");
	public ExPar Square4Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			52.0, 0.467, 0.460)), "Fourth square color");
	public ExPar TextColor = new ExPar(COLOR,
			new ExParValue(new PxlColor(0.55)), "Text color");

	public VisionDemonstrationsLogo() {
		setTitleAndTopic("Vision Demonstrations Logo", INTRO_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Square1Color));
		s2 = enterDisplayElement(new Bar(Square2Color));
		s3 = enterDisplayElement(new Bar(Square3Color));
		s4 = enterDisplayElement(new Bar(Square4Color));
		s5 = enterDisplayElement(new TextElement(TextColor, "Vision"));
		s6 = enterDisplayElement(new TextElement(TextColor, "Demonstrations"));
		ExPar.ScreenBackgroundColor.set(new PxlColor(0.25));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle bg = mediumSquare(width, height);
		bg.y -= bg.height / 4;
		Rectangle f1 = innerRect(bg, 0.8);
		f1.y = (int) (bg.y + (bg.height - f1.height) * 0.75);
		Rectangle ce = innerRect(f1, 6.0 / 8.0);
		ce.y = (int) (f1.y + (f1.height - ce.height) * 0.75);
		Rectangle f2 = innerRect(ce, 2.0 / 3.0);
		f2.y = (int) (ce.y + (ce.height - f2.height) * 0.75);
		getDisplayElement(s1).setRect(bg.x, bg.y, bg.width, bg.height);
		getDisplayElement(s2).setRect(f1.x, f1.y, f1.width, f1.height);
		getDisplayElement(s3).setRect(ce.x, ce.y, ce.width, ce.height);
		getDisplayElement(s4).setRect(f2.x, f2.y, f2.width, f2.height);
	}

	public void show(Graphics g) {
		// System.out.println("Showing ...");
		Bar b = (Bar) getDisplayElement(s1);
		Rectangle r = new Rectangle(b.getLocation(), b.getSize());
		r.y += r.height;
		Rectangle r2 = ((TextElement) getDisplayElement(s5)).fitToRect(g,
				"SansSerif", Font.PLAIN, r);
		r2.y += r2.height;
		((TextElement) getDisplayElement(s6)).fitToRect(g, "SansSerif",
				Font.PLAIN, r2);
		super.show(g);
	}
}
