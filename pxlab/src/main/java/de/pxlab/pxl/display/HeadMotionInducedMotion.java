package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a strange pattern which induces apparent circular motion when moving
 * the head up to or away from the screen.
 * 
 * @version 0.1.1
 */
public class HeadMotionInducedMotion extends Display {
	public ExPar Radius = new ExPar(SCREENSIZE, new ExParValue(142),
			"Radius of inner circle");
	public ExPar Distance = new ExPar(SCREENSIZE, new ExParValue(36),
			"Distance of circles");
	public ExPar InnerShear = new ExPar(SIGNED_PROPORTION,
			new ExParValue(0.37), "Shear of inner circle lines");
	public ExPar OuterShear = new ExPar(SIGNED_PROPORTION,
			new ExParValue(-0.37), "Shear of outer circle lines");
	public ExPar Width = new ExPar(SCREENSIZE, new ExParValue(17),
			"Width of bars");
	public ExPar Height = new ExPar(SCREENSIZE, new ExParValue(16),
			"Height of lines");
	public ExPar NumberOfBars = new ExPar(SMALL_INT, new ExParValue(30),
			"Number of bars");
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Color of bars");

	/** Constructor creating the title of the display. */
	public HeadMotionInducedMotion() {
		setTitleAndTopic("Motion Induced by Head Motion", COMPLEX_GEOMETRY_DSP
				| DEMO);
	}

	protected int create() {
		int s1 = enterDisplayElement(new Cross(this.Color), group[0]);
		return (s1);
	}
	private int shear1;
	private int shear2;
	private double angleStep;
	private int numberOfBars;
	private int innerRadius;
	private int outerRadius;
	private int w2;
	private int h2;

	protected void computeGeometry() {
		numberOfBars = NumberOfBars.getInt();
		angleStep = (2 * Math.PI) / (numberOfBars - 1);
		innerRadius = Radius.getInt();
		outerRadius = innerRadius + Distance.getInt();
		w2 = Width.getInt() / 2;
		h2 = Height.getInt() / 2;
		shear1 = (int) (InnerShear.getDouble() * w2);
		shear2 = (int) (OuterShear.getDouble() * w2);
		((DisplayElement) displayElementList.get(backgroundFieldIndex + 1))
				.setRect(0, 0, w2, w2);
	}

	public void show(Graphics g) {
		setGraphicsContext(g);
		((DisplayElement) displayElementList.get(backgroundFieldIndex)).show();
		((DisplayElement) displayElementList.get(backgroundFieldIndex + 1))
				.show();
		double[] x = new double[4];
		double[] y = new double[4];
		double[] x1 = new double[4];
		double[] y1 = new double[4];
		double[] x2 = new double[4];
		double[] y2 = new double[4];
		int[] xx1 = new int[4];
		int[] yy1 = new int[4];
		int[] xx2 = new int[4];
		int[] yy2 = new int[4];
		x1[0] = -w2 + shear1;
		x1[1] = w2 + shear1;
		x1[2] = w2 - shear1;
		x1[3] = -w2 - shear1;
		y1[0] = y1[1] = -h2 - innerRadius;
		y1[2] = y1[3] = h2 - innerRadius;
		x2[0] = -w2 + shear2;
		x2[1] = w2 + shear2;
		x2[2] = w2 - shear2;
		x2[3] = -w2 - shear2;
		y2[0] = y2[1] = -h2 - outerRadius;
		y2[2] = y2[3] = h2 - outerRadius;
		graphics.setColor(this.Color.getDevColor());
		double cosR, sinR;
		double angle = 0.0;
		for (int j = 0; j < numberOfBars; j++) {
			cosR = Math.cos(angle);
			sinR = Math.sin(angle);
			angle += angleStep;
			for (int i = 0; i < 4; i++) {
				xx1[i] = (int) (cosR * x1[i] + sinR * y1[i]);
				yy1[i] = (int) (-sinR * x1[i] + cosR * y1[i]);
				xx2[i] = (int) (cosR * x2[i] + sinR * y2[i]);
				yy2[i] = (int) (-sinR * x2[i] + cosR * y2[i]);
			}
			graphics.fillPolygon(xx1, yy1, 4);
			graphics.fillPolygon(xx2, yy2, 4);
		}
	}
}
