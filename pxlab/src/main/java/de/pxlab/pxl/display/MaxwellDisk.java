package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a computer simulation of James Clerk Maxwell's method of color
 * mixing.
 * 
 * @author H. Irtel
 * @version 0.1.0, 08/18/00
 */
public class MaxwellDisk extends Display {
	public ExPar CenterSize = new ExPar(PROPORT,
			new ExParValue(0.48, 0.0, 1.0), "Center disk size");
	public ExPar SurroundSize = new ExPar(PROPORT,
			new ExParValue(0.9, 0.0, 1.0), "Surround disk size");
	public ExPar TargetColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "Target color");
	public ExPar RedPrimary = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Red primary color");
	public ExPar GreenPrimary = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.GREEN)), "Green primary color");
	public ExPar BluePrimary = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLUE)), "Blue primary color");
	public ExPar Black = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLACK)), "Color");
	public ExPar PrimaryMix = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Primary Mixture");
	public ExPar TargetMix = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Target Mixture");
	public ExPar TargetIntensity = new ExPar(PROPORT, new ExParValue(0.5, 0.0,
			1.0), "Target color sector size");
	public ExPar RedSector = new ExPar(PROPORT, new ExParValue(0.33, 0.0, 1.0),
			"Red primary sector size");
	public ExPar GreenSector = new ExPar(PROPORT,
			new ExParValue(0.5, 0.0, 1.0), "Gren primary sector size");

	public MaxwellDisk() {
		setTitleAndTopic("Maxwell's disk", PHOTOMETRY_DSP | DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7;

	protected int create() {
		s6 = enterDisplayElement(new Oval(PrimaryMix));
		s7 = enterDisplayElement(new Oval(TargetMix));
		s3 = enterDisplayElement(new Sector(RedPrimary));
		s4 = enterDisplayElement(new Sector(GreenPrimary));
		s5 = enterDisplayElement(new Sector(BluePrimary));
		s1 = enterDisplayElement(new Sector(Black));
		s2 = enterDisplayElement(new Sector(TargetColor));
		return (s2);
	}

	protected void computeColors() {
		TargetMix.set(TargetColor.getPxlColor().mix(
				TargetIntensity.getDouble(), Black.getPxlColor()));
		PxlColor a = GreenPrimary.getPxlColor().mix(GreenSector.getDouble(),
				BluePrimary.getPxlColor());
		PrimaryMix.set(RedPrimary.getPxlColor().mix(RedSector.getDouble(), a));
	}

	protected void computeGeometry() {
		computeColors();
		Rectangle r1 = innerRect(firstSquareOfTwo(width, height, false),
				SurroundSize.getDouble());
		Rectangle r2 = innerRect(r1, CenterSize.getDouble());
		getDisplayElement(s6).setRect(r1);
		getDisplayElement(s7).setRect(r2);
		Rectangle r3 = innerRect(secondSquareOfTwo(width, height, false),
				SurroundSize.getDouble());
		Rectangle r4 = innerRect(r3, CenterSize.getDouble());
		double ar, ag, at;
		ar = 360.0 * RedSector.getDouble();
		getDisplayElement(s3).setRect(r3);
		((Sector) getDisplayElement(s3)).setAngles(0, (int) Math.round(ar));
		ag = (360.0 - ar) * GreenSector.getDouble();
		getDisplayElement(s4).setRect(r3);
		((Sector) getDisplayElement(s4)).setAngles((int) Math.round(ar),
				(int) Math.round(ag));
		getDisplayElement(s5).setRect(r3);
		((Sector) getDisplayElement(s5)).setAngles((int) Math.round(ar + ag),
				(int) Math.round(360.0 - ar - ag));
		at = 360.0 * TargetIntensity.getDouble();
		getDisplayElement(s2).setRect(r4);
		((Sector) getDisplayElement(s2)).setAngles(0, (int) Math.round(at));
		getDisplayElement(s1).setRect(r4);
		((Sector) getDisplayElement(s1)).setAngles((int) Math.round(at),
				(int) Math.round(360.0 - at));
	}
}
