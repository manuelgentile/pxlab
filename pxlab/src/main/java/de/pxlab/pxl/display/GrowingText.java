package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A single line of text which grows from a minimum to a maximum size.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 06/25/03
 */
public class GrowingText extends TextDisplay {
	/** Maximum Font size for text. */
	public ExPar MaxFontSize = new ExPar(VERSCREENSIZE, new ExParValue(45),
			"Maximum Font Size");
	/** Maximum Font size for text. */
	public ExPar StopSize = new ExPar(RTDATA, new ExParValue(0),
			"Stopping Size");
	/** Minimum Font size for text. */
	public ExPar MinFontSize = new ExPar(VERSCREENSIZE, new ExParValue(5),
			"Minimum Font Size");
	/** Timer for single animation frames. Should always be a clock timer. */
	public ExPar FrameTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"),
			"Frame Timer");
	/** Duration of a single animation frame. */
	public ExPar FrameDuration = new ExPar(DURATION, 0, 300,
			new ExParValue(50), "Single Frame Duration");
	/** Background field size for erasing the text between frames. */
	public ExPar FieldWidth = new ExPar(HORSCREENSIZE, new ExParValue(300),
			"Maximum Width of the Text");
	/** Background field size for erasing the text between frames. */
	public ExPar FieldHeight = new ExPar(VERSCREENSIZE, new ExParValue(100),
			"Maximum Height of the Text");

	public GrowingText() {
		setTitleAndTopic("Growing Text Animation", APPARENT_MOTION_DSP);
		Text.set("Growing Text");
	}

	public boolean isAnimated() {
		return (true);
	}
	protected Bar bar;
	protected TextElement txt;

	protected int create() {
		bar = new Bar(ExPar.ScreenBackgroundColor);
		enterDisplayElement(bar, group[0]);
		txt = new TextElement(Color);
		int b = enterDisplayElement(txt, group[0]);
		enterTiming(FrameTimer, FrameDuration, 0);
		return (b);
	}
	protected Font[] fnt;
	protected int minFontSize;
	protected int pointStepSize = 1;
	protected int nSteps;

	protected void computeGeometry() {
		int x = LocationX.getInt();
		int y = LocationY.getInt();
		int w = FieldWidth.getInt();
		int h = FieldHeight.getInt();
		bar.setRect(x - w / 2, y - h / 2, w, h);
		txt.setReferencePoint(PositionReferenceCodes.BASE_CENTER);
		txt.setText(Text.getString());
		txt.setLocation(x, y);
		minFontSize = MinFontSize.getInt();
		nSteps = (MaxFontSize.getInt() - minFontSize + pointStepSize)
				/ pointStepSize;
		if (nSteps < 0)
			nSteps = 1;
		fnt = new Font[nSteps];
		int fs = minFontSize;
		for (int i = 0; i < nSteps; i++) {
			fnt[i] = new Font(FontFamily.getString(), FontStyle.getInt(), fs);
			fs += pointStepSize;
		}
		txt.setFont(fnt[0]);
		// Make sure that the animation driver requests more than nSteps frames
		setFramesPerCycle(nSteps + 1);
	}

	public void computeAnimationFrame(int frame) {
		// Set the font according to the frame number as long as we
		// have fonts available. If the maximum size is reached then
		// always use the largest font.
		Font f = fnt[(frame < nSteps) ? frame : (nSteps - 1)];
		txt.setFont(f);
		StopSize.set(f.getSize());
	}

	protected void destroy() {
		for (int i = 0; i < nSteps; i++) {
			fnt[i] = null;
		}
		super.destroy();
	}
}
