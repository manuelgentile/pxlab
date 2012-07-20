package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Two strings with an SOA and response time measurement.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 01/26/01 use ExPar color and unified timing
 */
public class TwoStrings extends Display {
	public ExPar SignalText = new ExPar(STRING, new ExParValue("GR�N"),
			"Test signal text string");
	public ExPar SignalColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)),
			"Color of the test signal");
	public ExPar SignalLocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal location of the test signal");
	public ExPar SignalLocationY = new ExPar(VERSCREENPOS,
			new ExParValue(-100), "Vertical location of the test signal");
	public ExPar SignalSize = new ExPar(SCREENSIZE, new ExParValue(140),
			"Size of the test signal");
	public ExPar ProbeText = new ExPar(STRING, new ExParValue("BLAU"),
			"Probe signal text string");
	public ExPar ProbeColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Color of the probe signal");
	public ExPar ProbeLocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal location of the probe signal");
	public ExPar ProbeLocationY = new ExPar(VERSCREENPOS, new ExParValue(200),
			"Vertical location of the probe signal");
	public ExPar ProbeSize = new ExPar(SCREENSIZE, new ExParValue(140),
			"Size of the probe signal");
	public ExPar SOATimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.CLOCK_TIMER"),
			"Timing of interval between successive signal elements onset");
	public ExPar SOADuration = new ExPar(DURATION, new ExParValue(300),
			"Duration of interval between successive signal elements onset");
	public ExPar FontFamily = new ExPar(FONTNAME, new ExParValue("SansSerif"),
			"Text font family");
	public ExPar FontStyle = new ExPar(GEOMETRY_EDITOR, FontStyleCodes.class,
			new ExParValueConstant("de.pxlab.pxl.FontStyleCodes.PLAIN"),
			"Style of the text font");

	/** Cunstructor creating the title of the demo. */
	public TwoStrings() {
		setTitleAndTopic("Two Strings with an SOA", SERIAL_TEXT_DSP);
	}
	private int signal, probe;

	protected int create() {
		// This is the signal
		signal = enterDisplayElement(new TextElement(SignalColor), group[0]
				+ group[1]);
		int t = enterTiming(SOATimer, SOADuration, 0);
		// And this is the probe
		probe = enterDisplayElement(new TextElement(ProbeColor), group[1]);
		t = enterTiming(Timer, Duration, ResponseSet, t, ResponseTime,
				ResponseCode);
		return (signal);
	}

	protected void computeGeometry() {
		// System.out.println("TwoStrings.computeGeometry()");
		String fnn = FontFamily.getString();
		int fnt = FontStyle.getInt();
		TextElement txt;
		txt = (TextElement) getDisplayElement(probe);
		txt.setFont(fnn, fnt, ProbeSize.getInt());
		txt.setLocation(ProbeLocationX.getInt(), ProbeLocationY.getInt());
		txt.setText(ProbeText.getString());
		txt = (TextElement) getDisplayElement(signal);
		txt.setFont(fnn, fnt, SignalSize.getInt());
		txt.setLocation(SignalLocationX.getInt(), SignalLocationY.getInt());
		txt.setText(SignalText.getString());
	}
}
