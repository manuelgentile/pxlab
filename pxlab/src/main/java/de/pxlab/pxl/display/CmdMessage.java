package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a paragraph of text which is shown on the command line window which
 * started the PXLab application. By default this object has the NO_TIMER as its
 * timer and has the JustInTime flag set.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/10/27
 */
public class CmdMessage extends Display {
	/** Text to be shown on the command line. */
	public ExPar Text = new ExPar(STRING,
			new ExParValue("Trial %TrialCounter%"),
			"Command line window message");

	public CmdMessage() {
		setTitleAndTopic("Command line window message", TEXT_PAR_DSP | EXP);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
		JustInTime.set(1);
	}

	public boolean isGraphic() {
		return false;
	}

	protected int create() {
		int s1 = enterDisplayElement(new EmptyDisplayElement(), group[0]);
		defaultTiming(0);
		return s1;
	}

	protected void computeGeometry() {
	}

	public void showGroup() {
		String[] s = Text.getStringArray();
		for (int i = 0; i < s.length; i++) {
			System.out.println(s[i]);
		}
	}

	public void showGroup(Graphics g) {
		showGroup();
	}

	public void show(Graphics g) {
	}

	public void show() {
	}
}
