package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A smiley face for feedback.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/10/21
 */
public class Smiley extends Display {
	/** Face color. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.YELLOW)), "Smiley color");
	/** Line color. */
	public ExPar LineColor = new ExPar(COLOR, new ExParValue(new YxyColor(
			21.952, 0.203, 0.217)), "Line color");
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(200), "Disk size");
	/** Horizontal center location. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal center position");
	/** Vertical center location. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical center position");
	/** The type of face which should be shown. */
	public ExPar Mood = new ExPar(GEOMETRY_EDITOR, ResponseCodes.class,
			new ExParValueConstant("de.pxlab.pxl.ResponseCodes.CORRECT"),
			"Mood code");

	public Smiley() {
		setTitleAndTopic("Smiley", FEEDBACK_DSP);
		JustInTime.set(1);
	}
	private int s1;

	protected int create() {
		s1 = enterDisplayElement(new SmileyElement(this.Color, this.LineColor),
				group[0]);
		defaultTiming(0);
		return (s1);
	}

	protected void computeGeometry() {
		SmileyElement disk = (SmileyElement) getDisplayElement(s1);
		int s = Size.getInt();
		disk.setSize(s, s);
		disk.setLocation(LocationX.getInt() - s / 2, LocationY.getInt() - s / 2);
		disk.setLineWidth(s / 12);
		disk.setMood(Mood.getInt());
	}
}
