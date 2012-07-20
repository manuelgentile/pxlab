package de.pxlab.pxl;

/**
 * Bit codes for defining timer properties.
 * 
 * @version 0.3.1
 */
/*
 * 
 * 2005/06/19 removed MEDIA_TIMER_BIT
 * 
 * 2007/02/05 polled devices and mouse wheels
 * 
 * 2008/03/05 added FIXED_DELAY_BIT
 */
public interface TimerBitCodes {
	/** Use no timer. */
	public static final int NO_TIMER_BIT = 0;
	/** Use a clock timer with a fixed duration. */
	public static final int CLOCK_TIMER_BIT = 1;
	/** Use a mouse button press response to stop the timer. */
	public static final int MOUSE_BUTTON_TIMER_BIT = 1 << 2;
	/** Use a keyboard key press response to stop the timer. */
	public static final int KEY_TIMER_BIT = 1 << 3;
	/**
	 * Use a keyboard key press response with a special stop key. Other keys
	 * result in a signal to the Display via the keyResponse() method. The
	 * Display may use non-stop keys to change its properties. The stop key code
	 * is stored in the experimental parameter ExPar.StopKey.
	 */
	public static final int STOP_KEY_TIMER_BIT = 1 << 4;
	/** Use an external button box button press as a timer. */
	public static final int XBUTTON_TIMER_BIT = 1 << 5;
	/**
	 * Response direction code for a key or button press response. Only a
	 * response in this direction will stop the timer.
	 */
	public static final int DOWN_TIMER_BIT = 1 << 6;
	/**
	 * Response direction code for a key or button release response. Only a
	 * response in this direction will stop the timer.
	 */
	public static final int UP_TIMER_BIT = 1 << 7;
	/**
	 * The timing response includes mouse motion. If a timer has this bit set
	 * then mouse motion or button responses result in a signal sent to the
	 * Display object which tells the Display object which mouse event had
	 * happened and where. The methods used for this call are
	 * pointerActivated(), pointerReleased(), pointerDragged(), or
	 * pointerMoved(). The Display object may use this signal to update its
	 * properties or to decide that the display interval should be finished.
	 */
	public static final int MOUSE_TRACKING_BIT = 1 << 8;
	/**
	 * The timer's results are stored back into experimental parameters. A timer
	 * must have this bit set if timing data should be collected.
	 */
	public static final int STORE_TIMER_BIT = 1 << 9;
	/**
	 * Indicates that global response time measurement is startet when this
	 * timer starts.
	 */
	public static final int START_RESPONSE_TIMER_BIT = 1 << 10;
	/**
	 * Indicates that global response time measurement is stopped when this
	 * timer stops.
	 */
	public static final int STOP_RESPONSE_TIMER_BIT = 1 << 11;
	/**
	 * Synchronize the start of this timing group with the refresh cycle of the
	 * video system. This means that drawing of the respective display timing
	 * group only starts after a vertical blanking interval has been detected.
	 */
	public static final int VIDEO_SYNCHRONIZATION_BIT = 1 << 12;
	/**
	 * If set then a spurious response which happens during a clock timer
	 * interval is stored and replaces the timer duration and stopping code
	 * parameter.
	 */
	public static final int WATCH_SPURIOUS_RESPONSES_BIT = 1 << 14;
	/** If set then we expect a serial line input response. */
	public static final int SERIAL_LINE_TIMER_BIT = 1 << 15;
	/** Use the voice key timer. */
	public static final int VOICE_KEY_TIMER_BIT = 1 << 16;
	/** A timer which will synchronize to the media time. */
	public static final int SYNC_TO_MEDIA_TIMER_BIT = 1 << 18;
	/**
	 * Wait until a player/recorder arrives at the end of a media stream.
	 */
	public static final int END_OF_MEDIA_TIMER_BIT = 1 << 19;
	/**
	 * Response includes polled device axis changes. If a timer has this bit set
	 * then polled device responses result in a signal sent to the Display
	 * object which tells the Display object what had happened. The method used
	 * for this call is axisDeltasChanged(). The Display object may use this
	 * signal to update its properties.
	 */
	public static final int AXIS_TRACKING_BIT = 1 << 20;
	/**
	 * Response includes mouse wheel rotation. If a timer has this bit set then
	 * mouse wheel responses result in a signal sent to the Display object which
	 * tells the Display object what had happened. The method used for this call
	 * is axisDeltasChanged(). The Display object may use this signal to update
	 * its properties.
	 */
	public static final int WHEEL_TRACKING_BIT = 1 << 21;
	/**
	 * This is a timer which creates a fixed onset delay for its Display object
	 * and does not control the end of the display interval. So this Display
	 * object behaves similar to a NO_TIMER object whose onset is delayed by the
	 * given timer Duration parameter. The delay interval starts when this
	 * object would be shown if it had a NO_TIMER timer type. The object is
	 * shown in TRANSPARENT mode on the currently active screen and. Any
	 * subsequent screen display will overwrite this object.
	 */
	public static final int FIXED_DELAY_BIT = 1 << 22;
}
