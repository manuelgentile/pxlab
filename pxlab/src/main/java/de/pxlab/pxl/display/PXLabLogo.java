package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * This display simulates Josef Albers' Homage to the Square series.
 * 
 * @author H. Irtel
 * @version 0.1.3
 */
/*
 * 02/28/01
 */
public class PXLabLogo extends Display {
	public ExPar Square1Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			83.0, 0.407, 0.498)), "First square color");
	public ExPar Square2Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			72.0, 0.426, 0.493)), "Second square color");
	public ExPar Square3Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			61.2, 0.444, 0.473)), "Third square color");
	public ExPar Square4Color = new ExPar(COLOR, new ExParValue(new YxyColor(
			52.0, 0.467, 0.460)), "Fourth square color");
	public ExPar TextColor = new ExPar(COLOR,
			new ExParValue(new PxlColor(0.55)), "Text color");

	public PXLabLogo() {
		setTitleAndTopic("Psychological Experiments Laboratory Logo", INTRO_DSP
				| EXP);
	}
	private int s1, s2, s3, s4, s5, s6, s7, s8;

	protected int create() {
		s1 = enterDisplayElement(new Bar(Square1Color));
		s2 = enterDisplayElement(new Bar(Square2Color));
		s3 = enterDisplayElement(new Bar(Square3Color));
		s4 = enterDisplayElement(new Bar(Square4Color));
		s5 = enterDisplayElement(new TextElement(TextColor, "Psychological"));
		s6 = enterDisplayElement(new TextElement(TextColor, "Experiments"));
		s7 = enterDisplayElement(new TextElement(TextColor, "Laboratory"));
		s8 = enterDisplayElement(new TextElement(TextColor, "Hans Irtel ("
				+ Version.year() + ")"));
		ExPar.ScreenBackgroundColor.set(new PxlColor(0.25));
		return (s1);
	}
	private Rectangle cRect;
	private Rectangle tRect;
	private boolean horizontal;

	protected void computeGeometry() {
		cRect = firstSquareOfTwo(width, height, false);
		tRect = secondSquareOfTwo(width, height, false);
		horizontal = (width > height);
		if (horizontal) {
			cRect.x -= cRect.width / 40;
			tRect.x += tRect.width / 40;
		}
	}
	private TextElement t1, t2, t3, t4;

	public void show(Graphics g) {
		t1 = (TextElement) getDisplayElement(s5);
		Rectangle r2 = t1.fitToRect(g, "SansSerif", Font.PLAIN, tRect);
		int lineSkip = r2.height;
		Font fnt = t1.getFont();
		t2 = (TextElement) getDisplayElement(s6);
		t2.setFont(fnt);
		t3 = (TextElement) getDisplayElement(s7);
		t3.setFont(fnt);
		t4 = (TextElement) getDisplayElement(s8);
		t4.setFont(new Font(fnt.getFamily(), fnt.getStyle(), fnt.getSize() / 2));
		t1.setReferencePoint(PositionReferenceCodes.BASE_LEFT);
		t2.setReferencePoint(PositionReferenceCodes.BASE_LEFT);
		t3.setReferencePoint(PositionReferenceCodes.BASE_LEFT);
		t4.setReferencePoint(PositionReferenceCodes.BASE_LEFT);
		if (horizontal) {
			int th = 7 * lineSkip / 2;
			setColorSquares(new Rectangle(cRect.x + cRect.width - 4 * th / 3,
					cRect.y + cRect.height - 4 * th / 3, th, th));
			setTextPosition(tRect.x - th / 3, tRect.y - th / 3, lineSkip);
		} else {
			setColorSquares(cRect);
			setTextPosition(tRect.x, tRect.y, lineSkip);
		}
		super.show(g);
	}

	private void setColorSquares(Rectangle cRect) {
		Rectangle cRect1 = innerRect(cRect, 0.8);
		cRect1.y = (int) (cRect.y + (cRect.height - cRect1.height) * 0.75);
		Rectangle cRect2 = innerRect(cRect1, 6.0 / 8.0);
		cRect2.y = (int) (cRect1.y + (cRect1.height - cRect2.height) * 0.75);
		Rectangle cRect3 = innerRect(cRect2, 2.0 / 3.0);
		cRect3.y = (int) (cRect2.y + (cRect2.height - cRect3.height) * 0.75);
		getDisplayElement(s1).setRect(cRect);
		getDisplayElement(s2).setRect(cRect1);
		getDisplayElement(s3).setRect(cRect2);
		getDisplayElement(s4).setRect(cRect3);
	}

	private void setTextPosition(int px, int py, int lineSkip) {
		int p5 = py, p6 = py + lineSkip, p7 = py + 2 * lineSkip, p8 = py + 11
				* lineSkip / 4;
		int d = lineSkip;
		if (horizontal) {
			d = (py + tRect.height - p8);
		}
		getDisplayElement(s5).setLocation(px, p5 + d);
		getDisplayElement(s6).setLocation(px, p6 + d);
		getDisplayElement(s7).setLocation(px, p7 + d);
		getDisplayElement(s8).setLocation(px, p8 + d);
	}
}
