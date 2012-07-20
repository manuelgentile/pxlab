package de.pxlab.pxl;

import java.awt.Image;

/**
 * Describes the timing groups of Display objects.
 * 
 * @author Hans Irtel
 * @version 0.6.1
 * @see Display
 */
/*
 * 01/26/01 fixes for using a compined ExPar for timing type and duration
 * 02/09/01 removed previous "fix" 02/23/01 store response data immediately at
 * runtime 03/30/01 create private ExPar objects for actual delay/code if
 * necessary
 * 
 * 03/04/03 allow for text line responses as they are used with serial input
 * devices.
 * 
 * 2005/04/15 implement image preloading.
 * 
 * 2005/06/16 added timeControl
 * 
 * 2005/06/21 use nanosecond duration values internally
 */
public class TimingElement implements ExParTypeCodes, TimerBitCodes, TimerCodes {
	/**
	 * The experimental parameter which holds the timer code for controlling
	 * this timing element.
	 */
	private ExPar timerExPar = null;
	/**
	 * The type of timer which is used for controlling this timing element.
	 */
	private int timerType = NO_TIMER;
	/**
	 * The experimental parameter which holds this element's intended duration.
	 */
	private ExPar durationExPar = null;
	/** The intended duration of this timing element. */
	private long intendedDuration = 0L;
	/**
	 * The experimental parameter which stores the set of admissible response
	 * codes for this TimingElement.
	 */
	private ExPar responseSetExPar = null;
	/**
	 * The experimental parameter which stores the actual duration of this
	 * TimingElement.
	 */
	private ExPar actualDurationExPar = null;
	/** Dummy parameter for the duration. */
	private ExPar dummyActualDurationExPar = new ExPar(RTDATA,
			new ExParValue(0), "");
	/** Dummy parameter for the response code. */
	private ExPar dummyResponseCodeExPar = new ExPar(RTDATA, new ExParValue(0),
			"");
	/**
	 * The experimental parameter which stores the response code which actually
	 * stopped this element's timing period.
	 */
	private ExPar responseCodeExPar = null;
	/**
	 * The experimental parameter which stores the response position of mouse
	 * responses.
	 */
	private ExPar responsePositionExPar = null;
	/**
	 * Dummy parameter which stores the response position of mouse responses.
	 */
	private ExPar dummyResponsePositionExPar = new ExPar(RTDATA,
			new ExParValue(0, 0), "");
	/**
	 * The index of that DisplayElement in this TimingElement's DisplayElement
	 * list which the intended delay refers to as its zero time point. This
	 * parameter is currently not used.
	 */
	private int refObject = 0;
	/**
	 * Stores the actual time when this timing element's timing group has been
	 * shown.
	 */
	private long timeControl;
	/** A memory image which contains this timing group's image content. */
	private Image memoryBufferImage;

	/**
	 * Set a memory image which contains this timing group's image content.
	 * 
	 * @param m
	 *            the memory image for this timing group.
	 */
	public void setMemoryBufferImage(Image m) {
		/*
		 * Note that this takes about 4 to 5 ms on a 3 GHz Intel D865GBF
		 * mainboard with on-board graphics.
		 */
		if (m == null) {
			if (memoryBufferImage != null) {
				// Debug.time("Prepare to free buffer:       ");
				memoryBufferImage.flush();
				// Debug.time("Buffer is free:               ");
			}
		}
		/**/
		memoryBufferImage = m;
	}

	/**
	 * Get a memory image which contains this timing group's image content.
	 * 
	 * @return the memory image for this timing group or null if the timing
	 *         group does not have such an image.
	 */
	public Image getMemoryBufferImage() {
		return memoryBufferImage;
	}
	/**
	 * Flag to indicate that this timing group's memory image has already been
	 * preloaded into the back buffer of the display device.
	 */
	private boolean imagePreloaded;

	/**
	 * Set a flag to indicate that this timing group's memory image has already
	 * been preloaded into the back buffer of the display device.
	 * 
	 * @param s
	 *            true if image has been preloaded and false if not.
	 */
	public void setImagePreloaded(boolean s) {
		imagePreloaded = s;
	}

	/**
	 * Check whether this timing group's memory image has already been preloaded
	 * into the back buffer of the display device.
	 * 
	 * @return true if image has been preloaded and false if not.
	 */
	public boolean isImagePreloaded() {
		return imagePreloaded;
	}

	public String toString() {
		return ("de.pxlab.pxl.TimingElement [" + "timerExPar=" + timerExPar
				+ ", durationExPar=" + durationExPar + ", actualDurationExPar="
				+ actualDurationExPar + ", intendedDuration="
				+ intendedDuration +
				// ", actualDelay=" + actualDelay +
				", refObject=" + refObject + ", type=" + timerType +
				// ", responseCode=" + responseCode +
				", responseCodeExPar=" + responseCodeExPar);
	}

	/**
	 * This constructor is used in class Display in order to assign a dummy
	 * timing element to demo display objects.
	 */
	public TimingElement() {
		timerType = NO_TIMER;
	}

	public TimingElement(ExPar timer, ExPar duration, int refObj) {
		this(timer, duration, null, refObj, null, null, null);
	}

	/*
	 * public TimingElement(ExPar timer, ExPar duration, int refObj, ExPar
	 * rtime, ExPar rcode) { this(timer, duration, null, refObj, rtime, rcode,
	 * null); }
	 * 
	 * 
	 * public TimingElement(ExPar timer, ExPar duration, int refObj, ExPar
	 * rtime, ExPar rcode, ExPar rpos) { this(timer, duration, null, refObj,
	 * rtime, rcode, rpos); }
	 */
	public TimingElement(ExPar timer, ExPar duration, ExPar responseSet,
			int refObj, ExPar rtime, ExPar rcode, ExPar rpos) {
		timerExPar = timer;
		durationExPar = duration;
		responseSetExPar = responseSet;
		refObject = refObj;
		actualDurationExPar = (rtime == null) ? dummyActualDurationExPar
				: rtime;
		responseCodeExPar = (rcode == null) ? dummyResponseCodeExPar : rcode;
		responsePositionExPar = (rpos == null) ? dummyResponsePositionExPar
				: rpos;
		// System.out.println("Timing element parameter: " + n.toString());
	}

	/**
	 * Reinitialize this TimingElement's intended duration from its
	 * corresponding experimental parameter.
	 */
	public void fixIntendedDuration() {
		if (timerExPar != null) {
			timerType = timerExPar.getInt();
			intendedDuration = (timerType == NO_TIMER) ? 0L
					: (long) durationExPar.getInt() * 1000000L;
		}
		// System.out.println("Delay " + intendedDurationExPar.getHint() +
		// " is " + intendedDurationExPar.getInt() + " ms.");
	}

	/** Return this TimingElement's intended duration value. */
	public long getIntendedDuration() {
		return intendedDuration;
	}

	/**
	 * Set this TimingElement's intended duration parameter.
	 * 
	 * @param duration
	 *            the experimental parameter which gives the duration of this
	 *            TimingElement.
	 */
	public void setIntendedDurationPar(ExPar duration) {
		durationExPar = duration;
	}

	/**
	 * Set this TimingElement's timer parameter.
	 * 
	 * @param timer
	 *            the experimental parameter which holds the timer code for this
	 *            TimingElement.
	 */
	public void setTimerPar(ExPar timer) {
		timerExPar = timer;
	}

	/**
	 * Set this TimingElement's response data. This method is called while
	 * responses are collected and it sends this TimingElement the information
	 * which code stopped the timer and what the actual duration has been.
	 * 
	 * @param duration
	 *            the actual duration of this TimingElement during its display.
	 * @param code
	 *            the code which identifies the event which stopped this
	 *            TimingElement's timer.
	 */
	public void setResponseData(long duration, int code) {
		// System.out.println("TimingElement.setResponseData(): duration = " +
		// duration + ", code = " + code);
		// actualDelay = d;
		actualDurationExPar.set(HiresClock.ms(duration));
		// responseCode = c;
		responseCodeExPar.set(code);
		// System.out.println("TimingElement.setResponseData(): ResponseTime=" +
		// actualDurationExPar.getString());
	}

	/**
	 * Set this TimingElement's response data. This method is called while
	 * responses are collected and it sends this TimingElement the information
	 * which code stopped the timer and what the actual duration has been.
	 * 
	 * @param duration
	 *            the actual duration of this TimingElement during its display.
	 * @param code
	 *            the code which identifies the event which stopped this
	 *            TimingElement's timer.
	 * @param position
	 *            the mouse position at the response event.
	 */
	public void setResponseData(long duration, int code, int[] position) {
		// System.out.println("TimingElement.setResponseData(): duration = " +
		// duration + ", code = " + code);
		// actualDelay = d;
		actualDurationExPar.set(HiresClock.ms(duration));
		// responseCode = c;
		responseCodeExPar.set(code);
		responsePositionExPar.set(position);
		// System.out.println("TimingElement.setResponseData(): ResponseTime=" +
		// actualDurationExPar.getString());
	}

	/**
	 * Set this TimingElement's response data. This method is called while
	 * responses are collected and it sends this TimingElement the information
	 * what the actual duration has been and which text was input.
	 * 
	 * @param duration
	 *            the actual duration of this TimingElement during its display.
	 * @param txt
	 *            the response text.
	 */
	public void setResponseData(long duration, String txt) {
		// System.out.println("TimingElement.setResponseData(): delay=" + d +
		// ", code=" + c);
		// actualDelay = d;
		actualDurationExPar.set(HiresClock.ms(duration));
		// responseCode = c;
		responseCodeExPar.set(txt);
		// System.out.println("TimingElement.setResponseData(): ExPar.ResponseTime="
		// + ExPar.ResponseTime.getString());
		// System.out.println("TimingElement.setResponseData(): ExPar.ResponseTime="
		// + ExPar.get("ResponseTime").getString());
	}

	/**
	 * Set spurious response data if any have been found. Note that these data
	 * override the duration and code parameters of the current timer. This
	 * makes sense because spurious responses may only happen while a clock
	 * timer is used.
	 */
	public void setSpuriousResponseData(long duration, int code) {
		// System.out.println("TimingElement.setResponseData(): delay=" + d +
		// ", code=" + c);
		// actualDelay = d;
		actualDurationExPar.set(HiresClock.ms(duration));
		// responseCode = c;
		responseCodeExPar.set(code);
		// System.out.println("TimingElement.setResponseData(): ExPar.ResponseTime="
		// + ExPar.ResponseTime.getString());
		// System.out.println("TimingElement.setResponseData(): ExPar.ResponseTime="
		// + ExPar.get("ResponseTime").getString());
	}

	/**
	 * Set spurious mouse response data if any have been found. Note that these
	 * data override the duration, code, and position parameters of the current
	 * timer. This makes sense because spurious responses may only happen while
	 * a clock timer is used.
	 */
	public void setSpuriousResponseData(long duration, int code, int[] position) {
		// System.out.println("TimingElement.setResponseData(): delay=" + d +
		// ", code=" + c);
		// actualDelay = d;
		actualDurationExPar.set(HiresClock.ms(duration));
		// responseCode = c;
		responseCodeExPar.set(code);
		responsePositionExPar.set(position);
		// System.out.println("TimingElement.setResponseData(): ExPar.ResponseTime="
		// + ExPar.ResponseTime.getString());
		// System.out.println("TimingElement.setResponseData(): ExPar.ResponseTime="
		// + ExPar.get("ResponseTime").getString());
	}

	public long getActualDuration() {
		return Math.round(actualDurationExPar.getDouble() * 1000000.0);
	}

	public void setActualDuration(long t) {
		actualDurationExPar.set(HiresClock.ms(t));
	}

	/**
	 * Check whether this TimingElement needs a clock for taking its time. This
	 * is true for clock timers.
	 */
	public boolean needsClock() {
		return ((timerType & CLOCK_TIMER_BIT) != 0);
	}

	/**
	 * Check whether this TimingElement needs an input device for taking its
	 * time. This is true for response timers.
	 */
	public boolean needsInput() {
		return (((timerType & MOUSE_BUTTON_TIMER_BIT)
				| (timerType & KEY_TIMER_BIT) | (timerType & XBUTTON_TIMER_BIT)) != 0);
	}

	/**
	 * Check whether this TimingElement stores its actual duration in an
	 * experimental parameter for controlling possible differences betwee the
	 * intended and the actual duration.
	 */
	public boolean storesData() {
		return ((timerType & STORE_TIMER_BIT) != 0);
	}

	/** Get the timer code of this TimingElement. */
	public int getTimerType() {
		return (timerType);
	}

	/**
	 * Get the set of response codes which are admitted by this TimingElement.
	 * 
	 * @return an array of response codes which are allowed to stop this timer.
	 *         Returns null if this TimingElement has no response set defined.
	 */
	public int[] getResponseSet() {
		if (responseSetExPar == null)
			return null;
		ExParValue v = responseSetExPar.getValue();
		return (v.isNotSet() ? null : v.getIntArray());
	}

	/**
	 * Set this TimingElement's experimental parameter which holds the set of
	 * admissible response codes.
	 */
	public void setResponseSetExPar(ExPar p) {
		responseSetExPar = p;
	}

	/** Get this TimingElement's reference object index. */
	public int getRefObject() {
		return (refObject);
	}

	/**
	 * Store the time when this timing element's timing group has been shown.
	 * 
	 * @param t
	 *            the time in nanoseconds when this timing element has been
	 *            shown.
	 */
	public void setTimeControl(long t) {
		timeControl = t;
	}

	/**
	 * Store the actual time when this timing element's timing group has been
	 * shown.
	 * 
	 * @return the time in nanoseconds when this timing element has been shown.
	 */
	public long getTimeControl() {
		return timeControl;
	}
}
