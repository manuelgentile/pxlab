package de.pxlab.pxl.display;

import java.awt.*;
import java.util.concurrent.DelayQueue;
import de.pxlab.pxl.*;

/**
 * A target for tracking tasks. Target motion is a sum of sinusoidal components
 * whose frequency, amplitude, and phase may be defined. For persuit tracking a
 * tracking mark may be shown which follows mouse movements. No tracking mark is
 * shown in compensatory tracking. The feedback mark of persuit trackig may be
 * delayed.
 * 
 * @version 0.2.0
 */
/*
 * 
 * 2007/04/12 added delayed feedback
 */
public class TrackingTarget extends FrameAnimation {
	/** Tracking target dot color. */
	public ExPar TargetColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Tracking Target Color");
	/** Tracking target dot size. */
	public ExPar TargetSize = new ExPar(SCREENSIZE, new ExParValue(20),
			"Tracking Target Size");
	/** Persuit mark color. */
	public ExPar MarkColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.YELLOW)), "Tracking Mark Color");
	/**
	 * Size of the persuit mark. If set to 0 then no persuit mark is shown.
	 */
	public ExPar MarkSize = new ExPar(SCREENSIZE, new ExParValue(20),
			"Tracking Mark Size");
	/** Line width of the persuit mark. */
	public ExPar MarkLineWidth = new ExPar(SMALL_SCREENSIZE, new ExParValue(5),
			"Thickness of the Tracking Mark Lines");
	private double[] hsf = { 0.1, 0.3, 0.8, 1.3 };
	/** Array of frequency values for the horizontal sine components. */
	public ExPar HorizontalSineFrequency = new ExPar(0.0, 3.0, new ExParValue(
			hsf), "Horizontal Sine Components Frequency");
	private double[] hsa = { 60.0, 100.0, 40.0, 20.0 };
	/** Array of amplitude values for the horizontal sine components. */
	public ExPar HorizontalSineAmplitude = new ExPar(HORSCREENSIZE,
			new ExParValue(hsa), "Horizontal Sine Components Amplitude");
	/** Array of phase values for the horizontal sine components. */
	public ExPar HorizontalSinePhase = new ExPar(ANGLE, new ExParValue(0.0),
			"Horizontal Sine Components Phase");
	private double[] vsf = { 0.17, 0.4, 0.7, 1.2 };
	/** Array of frequency values for the vertical sine components. */
	public ExPar VerticalSineFrequency = new ExPar(0.0, 3.0,
			new ExParValue(vsf), "Vertical Sine Components Frequency");
	private double[] vsa = { 80.0, 80.0, 60.0, 40.0 };
	/** Array of amplitude values for the vertical sine components. */
	public ExPar VerticalSineAmplitude = new ExPar(VERSCREENSIZE,
			new ExParValue(vsa), "Vertical Sine Components Amplitude");
	/** Array of phase values for the vertical sine components. */
	public ExPar VerticalSinePhase = new ExPar(ANGLE, new ExParValue(0.0),
			"Vertical Sine Components Phase");
	/** If nonzero then the trace of ther target is shown. */
	public ExPar Trace = new ExPar(FLAG, new ExParValue(0), "Trace Flag");
	/**
	 * If nonzero then persuit tracking mode is used, if zero then compensatory
	 * tracking is used.
	 */
	public ExPar Mode = new ExPar(FLAG, new ExParValue(1),
			"Persuit/Compensatory Flag");
	/**
	 * Persuit tracking feedback delay in milliseconds. This parameter is
	 * ignored in compensatory tracking.
	 */
	public ExPar FeedbackDelay = new ExPar(DURATION, new ExParValue(0),
			"Feedback delay duration");
	/**
	 * Root mean squared error. Sampling frequency is identical to animation
	 * frequency.
	 */
	public ExPar RMSError = new ExPar(RTDATA, new ExParValue(0),
			"Root Mean Square Error");
	/**
	 * A String which contains a table of all coordinates of the tracking target
	 * and the persuit mark for a single trial.
	 */
	public ExPar TrackData = new ExPar(RTDATA, new ExParValue(0),
			"Track Data Points");

	public TrackingTarget() {
		setTitleAndTopic("Tracking Target", APPARENT_MOTION_DSP);
		Duration.set(5000);
	}
	protected Oval preTarget;
	protected Oval target;
	protected Cross preTrackingMark;
	protected Cross trackingMark;
	protected DelayQueue delayQueue = null;

	protected int create() {
		preTarget = new Oval(ExPar.ScreenBackgroundColor);
		preTrackingMark = new Cross(ExPar.ScreenBackgroundColor);
		target = new Oval(TargetColor);
		trackingMark = new Cross(MarkColor);
		enterDisplayElement(preTarget, group[0]);
		enterDisplayElement(preTrackingMark, group[0]);
		int s = enterDisplayElement(target, group[0]);
		enterDisplayElement(trackingMark, group[0]);
		int t = enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(Integer.MAX_VALUE);
		return s;
	}
	protected double[] horF;
	protected double[] horA;
	protected double[] horP;
	protected double[] verF;
	protected double[] verA;
	protected double[] verP;
	protected double timeScale;
	protected double phaseScale = Math.PI / 180.0;
	protected int trackingMarkX;
	protected int trackingMarkY;
	protected double sumOfSquaredError;
	protected int nPoints;
	protected boolean persuit;
	protected StringBuffer trackData;
	protected String nl = System.getProperty("line.separator");
	protected int shiftX, shiftY;
	protected long feedbackDelay;

	protected void computeGeometry() {
		persuit = Mode.getFlag();
		int s = TargetSize.getInt();
		shiftX = -s / 2;
		shiftY = -s / 2;
		preTarget.setRect(shiftX, shiftY, s, s);
		target.setRect(shiftX, shiftY, s, s);
		horF = HorizontalSineFrequency.getDoubleArray();
		horA = HorizontalSineAmplitude.getDoubleArray();
		horP = HorizontalSinePhase.getDoubleArray();
		verF = VerticalSineFrequency.getDoubleArray();
		verA = VerticalSineAmplitude.getDoubleArray();
		verP = VerticalSinePhase.getDoubleArray();
		// really:
		// timeScale = 2.0 * Math.PI * FrameDuration.getDouble() / 1000.0;
		timeScale = Math.PI * FrameDuration.getDouble() / 500.0;
		if (Trace.getFlag()) {
			preTarget.setSize(0, 0);
		}
		trackingMarkX = 0;
		trackingMarkY = 0;
		if (persuit) {
			s = MarkSize.getInt();
			int w = MarkLineWidth.getInt();
			preTrackingMark.setRect(trackingMarkX, trackingMarkY, s, s);
			preTrackingMark.setLineWidth(w);
			trackingMark.setRect(trackingMarkX, trackingMarkY, s, s);
			trackingMark.setLineWidth(w);
		}
		sumOfSquaredError = 0.0;
		nPoints = 0;
		trackData = new StringBuffer(10000);
		feedbackDelay = FeedbackDelay.getInt() * 1000000L;
		if (feedbackDelay > 0L) {
			if (delayQueue == null)
				delayQueue = new DelayQueue();
			delayQueue.clear();
		}
	}

	public void computeAnimationFrame(int frame) {
		// long t1 = System.currentTimeMillis();
		double t = frame * timeScale;
		int pn;
		pn = (horP != null) ? horP.length : 0;
		double p;
		double x = 0.0;
		if (horF != null) {
			for (int i = 0; i < horF.length; i++) {
				p = (i < pn) ? (phaseScale * horP[i]) : 0.0;
				if (i < horA.length) {
					x += horA[i] * Math.sin(t * horF[i] + p);
				}
			}
		}
		pn = (verP != null) ? verP.length : 0;
		double y = 0.0;
		if (verF != null) {
			for (int i = 0; i < verF.length; i++) {
				p = (i < pn) ? (phaseScale * verP[i]) : 0.0;
				if (i < verA.length) {
					y += verA[i] * Math.sin(t * verF[i] + p);
				}
			}
		}
		int px = (int) x;
		int py = (int) y;
		int dx, dy;
		if (persuit) {
			// persuit tracking display
			if (feedbackDelay > 0L) {
				delayQueue.put(new DelayedPoint(pointerCurrentX,
						pointerCurrentY, feedbackDelay));
				DelayedPoint cp = (DelayedPoint) delayQueue.poll();
				if (cp != null) {
					DelayedPoint ccp = null;
					while ((ccp = (DelayedPoint) delayQueue.poll()) != null) {
						cp = ccp;
					}
					trackingMarkX = cp.x;
					trackingMarkY = cp.y;
				} else {
					trackingMarkX = 0;
					trackingMarkY = 0;
				}
			} else {
				trackingMarkX = pointerCurrentX;
				trackingMarkY = pointerCurrentY;
			}
			preTarget.setLocation(target.getLocation());
			target.setLocation(px + shiftX, py + shiftY);
			preTrackingMark.setLocation(trackingMark.getLocation());
			trackingMark.setLocation(trackingMarkX, trackingMarkY);
			dx = trackingMarkX - px;
			dy = trackingMarkY - py;
			trackData.append(nl + "\t" + String.valueOf(px) + "\t"
					+ String.valueOf(py) + "\t"
					+ String.valueOf(pointerCurrentX) + "\t"
					+ String.valueOf(pointerCurrentY));
		} else {
			// compensatory tracking display
			dx = px - pointerCurrentX;
			dy = py - pointerCurrentY;
			preTarget.setLocation(target.getLocation());
			target.setLocation(dx + shiftX, dy + shiftY);
			trackData.append(nl + "\t" + String.valueOf(dx) + "\t"
					+ String.valueOf(dy));
		}
		sumOfSquaredError += dx * dx + dy * dy;
		nPoints++;
		// long t2 = System.currentTimeMillis();
		// System.out.println("TrackingTarget.computeAnimationFrame() computation time = "
		// + (t2-t1));
	}

	protected void timingGroupFinished(int g) {
		RMSError.set(Math.sqrt(sumOfSquaredError / nPoints));
		TrackData.set(trackData.toString());
		// TrackData.set("tracking data");
		// System.out.println("TrackingTarget.timingGroupFinished(): " +
		// TrackData.getString());
	}
}
