package de.pxlab.pxl;

/**
 * Codes for defining timers for timing groups.
 * 
 * @version 0.3.3
 */
/*
 * 04/19/01 make CLOCK_TIMER also STORE_TIMER_BIT
 * 
 * 07/25/01 introduced VIDEO_SYNC_TIMER_BIT
 * 
 * 07/29/01 renamed all single bit masks such that they start with since this
 * pattern is used to identify bits by interactive code GUIs.
 * 
 * 01/23/02 introduced XBUTTON_TIMER_BIT and added it to all those timers which
 * constitute RESPONSE_TIMER objects
 * 
 * 11/15/02 added KEY_RESPONSE_TIMER and RELEASE_KEY_RESPONSE_TIMER
 * 
 * 02/14/02 added SERIAL_LINE_TIMER_BIT and SERIAL_LINE_INPUT_TIMER;
 * 
 * 05/22/03 changed meaning of VIDEO_SYNCHRONIZATION_BIT and added
 * VIDEO_CYCLE_TIMER
 * 
 * 2005/03/11 added MEDIA_TIMER, VOICE_KEY_TIMER
 * 
 * 2005/06/19 removed MEDIA_TIMER but keep SYNC_TO_MEDIA_TIMER and
 * END_OF_MEDIA_TIMER.
 * 
 * 2008/03/05 added FIXED_DELAY_TIMER, added _TIMER postfix to WHEEL_TRACKING
 * and AXIS_TRACKING
 */
public interface TimerCodes {
	/**
	 * The display elements of this timing group do not use a delay timer. This
	 * means that the Display object's timing group is shown and then the next
	 * timing group is processed immediately without any wait.
	 */
	public static final int NO_TIMER = TimerBitCodes.NO_TIMER_BIT;
	/**
	 * A timer which is controlled by a clock with a fixed delay period. The
	 * display element group remains unchanged until the fixed time period has
	 * passed. The actual time passed is stored in the Display object's
	 * ResponseTime parameter. The ResponseCode parameter gets the value
	 * de.pxlab.pxl.ResponseCodes.TIME_OUT.
	 */
	public static final int CLOCK_TIMER = TimerBitCodes.CLOCK_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * A timer which is controlled by a clock with a fixed delay period. The
	 * display element group remains unchanged until the respective time period
	 * has passed. No timing data are stored.
	 */
	public static final int RAW_CLOCK_TIMER = TimerBitCodes.CLOCK_TIMER_BIT;
	/**
	 * A timer which is controlled by a clock with a fixed delay period and
	 * whose start is synchronized to the beginning of the vertical retrace
	 * interval of the video display. This means that the Display object's
	 * timing group is presented immediately after the vertical retrace signal
	 * has been detected. Thus there may be an initial delay period while the
	 * system waits for the next vertical retrace. This delay is not part of the
	 * fixed intended delay of this clock timer. The actual duration and code of
	 * this timer are stored like that of a CLOCK_TIMER.
	 */
	public static final int VS_CLOCK_TIMER = CLOCK_TIMER
			| TimerBitCodes.VIDEO_SYNCHRONIZATION_BIT;
	/**
	 * A timer which is controlled by a clock with a fixed delay period. The
	 * display element group remains unchanged until the respective time period
	 * has passed. The timer also watches for spurious responses during the
	 * timing interval. The actual time passed is stored in the respective
	 * response time parameter. If there is a spurious response detected then
	 * this response's time and code are stored instead of the timer's actual
	 * time and stopping code.
	 */
	public static final int WATCHING_CLOCK_TIMER = TimerBitCodes.CLOCK_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT
			| TimerBitCodes.WATCH_SPURIOUS_RESPONSES_BIT;
	/**
	 * A timer which waits for a keyboard key press response to stop its delay
	 * interval. The actual duration is stored in ResponseTime and the response
	 * code is stored in parameter ResponseCode.
	 */
	public static final int KEY_RESPONSE_TIMER = TimerBitCodes.KEY_TIMER_BIT
			| TimerBitCodes.DOWN_TIMER_BIT | TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * A timer which waits for a keyboard key release response to stop its delay
	 * interval. The actual duration is stored in ResponseTime and the response
	 * code is stored in parameter ResponseCode.
	 */
	public static final int RELEASE_KEY_RESPONSE_TIMER = TimerBitCodes.KEY_TIMER_BIT
			| TimerBitCodes.UP_TIMER_BIT | TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * This timer waits for a down response on any available response device to
	 * stop its delay interval. The actual duration is stored in ResponseTime
	 * and the response code is stored in parameter ResponseCode.
	 */
	public static final int RESPONSE_TIMER = TimerBitCodes.MOUSE_BUTTON_TIMER_BIT
			| TimerBitCodes.KEY_TIMER_BIT
			| TimerBitCodes.XBUTTON_TIMER_BIT
			| TimerBitCodes.DOWN_TIMER_BIT | TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * A timer which waits for a down response on any available response device
	 * to stop its delay interval and whose Display object's timing group onset
	 * is synchronized to the begin of the vertical retrace interval. The actual
	 * duration is stored in ResponseTime and the response code is stored in
	 * parameter ResponseCode.
	 */
	public static final int VS_RESPONSE_TIMER = RESPONSE_TIMER
			| TimerBitCodes.VIDEO_SYNCHRONIZATION_BIT;
	/**
	 * A timer which waits for a release response on any available response
	 * device to stop its delay interval. The actual duration is stored in
	 * ResponseTime and the response code is stored in parameter ResponseCode.
	 */
	public static final int RELEASE_RESPONSE_TIMER = TimerBitCodes.MOUSE_BUTTON_TIMER_BIT
			| TimerBitCodes.KEY_TIMER_BIT
			| TimerBitCodes.XBUTTON_TIMER_BIT
			| TimerBitCodes.UP_TIMER_BIT | TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * A timer which may both be stopped by a subject's down response action or
	 * by the time out of a clock period whichever comes first. The actual
	 * duration is stored in ResponseTime and the response code is stored in
	 * parameter ResponseCode.
	 */
	public static final int LIMITED_RESPONSE_TIMER = TimerBitCodes.CLOCK_TIMER_BIT
			| RESPONSE_TIMER;
	/**
	 * A timer which waits for any go-signal of the subject. No clock timing or
	 * time out control is involved. The subject's go-response may come from any
	 * available response device and always is a release response. Usually this
	 * response is only used for proceeding within the display. This timer is
	 * identical to a RELEASE_RESPONSE_TIMER but no timing protocol is stored.
	 */
	public static final int GO_TIMER = TimerBitCodes.MOUSE_BUTTON_TIMER_BIT
			| TimerBitCodes.KEY_TIMER_BIT | TimerBitCodes.XBUTTON_TIMER_BIT
			| TimerBitCodes.UP_TIMER_BIT;
	/**
	 * A timer which waits for any go-signal of the subject or the time out of a
	 * clock, whichever comes first. The subject's go-response may come from any
	 * available response device and always is a release action. Similar to a
	 * GO_TIMER but has a limited waiting time.
	 */
	public static final int LIMITED_GO_TIMER = TimerBitCodes.CLOCK_TIMER_BIT
			| GO_TIMER;
	/**
	 * This timer allows a continuous mouse response while a mouse button is
	 * pressed and stops on mouse button release. Some Display objects will make
	 * a continuous update of the stimulus while the mouse button is pressed.
	 * The Display object presentation is stopped when the mouse button is
	 * released. The actual duration is stored in ResponseTime and the response
	 * code is stored in parameter ResponseCode.
	 */
	public static final int MOUSE_TRACKING_TIMER = TimerBitCodes.MOUSE_BUTTON_TIMER_BIT
			| TimerBitCodes.UP_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT
			| TimerBitCodes.MOUSE_TRACKING_BIT;
	/**
	 * This timer allows a continuous mouse wheel response. Some Display objects
	 * will make a continuous update of the stimulus when the mouse wheel is
	 * rotated.
	 */
	public static final int WHEEL_TRACKING_TIMER = TimerBitCodes.WHEEL_TRACKING_BIT;
	/**
	 * This timer allows a continuous keyboard response which is only stopped if
	 * the response key is the key whose code is defined by the experimental
	 * parameter ExPar.StopKey. Other keys result in a call-back signal to the
	 * Display via the keyResponse() method. A typical application is class
	 * de.pxlab.pxl.display.TextInput. The actual duration is stored in
	 * ResponseTime and the response code is stored in parameter ResponseCode.
	 */
	public static final int STOP_KEY_TIMER = TimerBitCodes.STOP_KEY_TIMER_BIT
			| TimerBitCodes.KEY_TIMER_BIT | TimerBitCodes.DOWN_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * This timer behaves like a STOP_KEY_TIMER. Additional continuous display
	 * updates may be made while a mouse button is pressed. Only the code
	 * defined by parameter ExPar.StopKey can stop the display interval.
	 */
	public static final int MOUSE_TRACKING_STOP_KEY_TIMER = TimerBitCodes.STOP_KEY_TIMER_BIT
			| TimerBitCodes.KEY_TIMER_BIT
			| TimerBitCodes.DOWN_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT | TimerBitCodes.MOUSE_TRACKING_BIT;
	/**
	 * This timer behaves like a KEY_RESPONSE_TIMER where continuous display
	 * updates may be made while a mouse button is pressed. Any valid key press
	 * stops the display interval. The actual duration is stored in ResponseTime
	 * and the response code is stored in parameter ResponseCode.
	 */
	public static final int MOUSE_TRACKING_KEY_TIMER = KEY_RESPONSE_TIMER
			| TimerBitCodes.MOUSE_TRACKING_BIT;
	/**
	 * This timer behaves like a RELEASE_KEY_RESPONSE_TIMER where continuous
	 * display updates may be made while a mouse button is pressed. A valid key
	 * press stops the display interval.
	 */
	public static final int MOUSE_TRACKING_RELEASE_KEY_TIMER = RELEASE_KEY_RESPONSE_TIMER
			| TimerBitCodes.MOUSE_TRACKING_BIT;
	/**
	 * This timer waits until a down response on an external device is detected.
	 * This may be a device connected to the serial port or it may be any polled
	 * device, like a gamepad or joystick. The actual duration is stored in
	 * ResponseTime and the response code is stored in parameter ResponseCode.
	 */
	public static final int XBUTTON_TIMER = TimerBitCodes.XBUTTON_TIMER_BIT
			| TimerBitCodes.DOWN_TIMER_BIT | TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * This timer waits until a release response on an external device is
	 * detected. This may be a device connected to the serial port or it may be
	 * any polled device, like a gamepad or joystick. The actual duration is
	 * stored in ResponseTime and the response code is stored in parameter
	 * ResponseCode.
	 */
	public static final int RELEASE_XBUTTON_TIMER = TimerBitCodes.XBUTTON_TIMER_BIT
			| TimerBitCodes.UP_TIMER_BIT | TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * This timer generates a continuous axis change response on a polled
	 * device. A continuous update of the stimulus may be made while the device
	 * is polled.
	 */
	public static final int AXIS_TRACKING_TIMER = TimerBitCodes.AXIS_TRACKING_BIT;
	/**
	 * This timer waits until it detects a complete input from a serial
	 * communication line. This is an input across the serial communication line
	 * which is stopped by the detection of a carriage return character. The
	 * input string is stored in the respective timing group's ResponseCode
	 * parameter.
	 */
	public static final int SERIAL_LINE_INPUT_TIMER = TimerBitCodes.SERIAL_LINE_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * This timer expects its stop response from an audio input channel. It
	 * waits until the sound level passes beyond the value of
	 * ExPar.VoiceKeyThreshold. The audio input channel properties should be
	 * managed outside of PXLab. The actual duration is stored in ResponseTime
	 * and the response code is stored in parameter ResponseCode.
	 */
	public static final int VOICE_KEY_TIMER = TimerBitCodes.VOICE_KEY_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT;
	/**
	 * If this code is ORed into a valid timer code then it indicates that
	 * global response time measurement is startet when this timer starts.
	 */
	public static final int START_RESPONSE_TIMER = TimerBitCodes.START_RESPONSE_TIMER_BIT;
	/**
	 * If this code is ORed into a valid timer code then it indicates that
	 * global response time measurement is stopped when this timer stops. The
	 * global response time is the time difference between this timer's stopping
	 * time and the time when global response timing had last been started. The
	 * actual duration is stored in the global experimental parameter
	 * ResponseTime and the response code is stored in the global experimental
	 * parameter ResponseCode.
	 */
	public static final int STOP_RESPONSE_TIMER = TimerBitCodes.STOP_RESPONSE_TIMER_BIT;
	/**
	 * A timer watching for a media sync point which is created by a sound or
	 * movie processor. The sync event is created when the media processor has
	 * arrived at a certain media time while playing the media data. The media
	 * time is given by the timing group's Duration parameter. Media time is the
	 * time relative to the start of the media.
	 */
	public static final int SYNC_TO_MEDIA_TIMER = TimerBitCodes.SYNC_TO_MEDIA_TIMER_BIT;
	/**
	 * A timer watching for an end of media event which is created by a sound or
	 * movie processor. The end of media event is created when the media
	 * processor has stopped playing media data because the end of the data
	 * stream has been detected and the processor has removed all resources
	 * associated with playing the media data.
	 */
	public static final int END_OF_MEDIA_TIMER = TimerBitCodes.END_OF_MEDIA_TIMER_BIT
			| TimerBitCodes.STORE_TIMER_BIT;
	/** Same as END_OF_MEDIA_TIMER. */
	public static final int STOP_MEDIA_TIMER = END_OF_MEDIA_TIMER;
	/**
	 * A timer which behaves like an END_OF_MEDIA_TIMER but also watches for
	 * spurious responses during media processing like a WATCHING_CLOCK_TIMER.
	 */
	public static final int WATCHING_END_OF_MEDIA_TIMER = END_OF_MEDIA_TIMER
			| TimerBitCodes.WATCH_SPURIOUS_RESPONSES_BIT;
	/**
	 * A timer which creates a fixed onset delay for its Display object timing
	 * group. It does not control the end of the display interval. So this
	 * Display object's timing group behaves similar to a NO_TIMER object whose
	 * onset is delayed by the respective timing group's Duration parameter. The
	 * delay interval starts when this object's timing group would be shown if
	 * it had a NO_TIMER timer type. The object is shown in TRANSPARENT mode on
	 * the currently active screen. If the Display's Execute flag is not set at
	 * presentation time then the object is not shown. Any subsequent screen
	 * display will overwrite this object. This makes it possible to to run a
	 * display list with arbitrary timing methods and concurrently show a single
	 * Display object at a given point in time. The delayed object is treated
	 * like a TRANSPARENT overlay and has no duration. Its Duration parameter
	 * actually is the delay being used. This allows a limited amount of
	 * concurrent processing. Since the delay object is TRANSPARENT it also
	 * makes it possible to selectively remove Display objects from the screen
	 * by overwriting them with blanks bars. This timer automatically disables
	 * preloading for its Display and it should be applied only to single timing
	 * group Display objects. A DisplayList may contain multiple Display objects
	 * using this timer type. Although every fixed delay timer in a DisplayList
	 * has its own presentation thread it should introduce hardly any timing
	 * uncertainty on the main presentation thread since the fixed delay object
	 * threads run at the lowest possible priority.
	 */
	public static final int FIXED_DELAY_TIMER = TimerBitCodes.FIXED_DELAY_BIT;
}
