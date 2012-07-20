package de.pxlab.pxl;

import java.util.ArrayList;

/**
 * Runtime debugging support.
 * 
 * @version 0.2.7
 */
/*
 * 
 * 02/13/01 added munselldata
 * 
 * 2005/10/28 added FACTORS
 * 
 * 2005/11/15 added ADAPTIVE and ARGVALUES
 * 
 * 2005/12/09 added DATA
 * 
 * 2006/03/02 added CACHE
 * 
 * 2006/10/13 added EDIT
 * 
 * 2007/09/23 Debug.COLDEV
 */
public class Debug {
	// -----------------------------------------------
	// Debug masks
	// -----------------------------------------------
	public static final long NO_DEBUGGING = 0;
	/** Show gain/loose focus events. */
	public static final long FOCUS = 1;
	/** Show display list timing protocol. */
	public static final long TIMING = FOCUS + FOCUS;
	/** Show display list timing protocol using nanosecond resolution. */
	public static final long HR_TIMING = TIMING + TIMING;
	/** Extended parser protocol. */
	public static final long PARSER = HR_TIMING + HR_TIMING;
	/** Procedure unit arguments push/pop operations. */
	public static final long PUSH_POP = PARSER + PARSER;
	/** Node creation protocol. */
	public static final long CREATE_NODE = PUSH_POP + PUSH_POP;
	/** DisplayList protocol. */
	public static final long DISPLAY_LIST = CREATE_NODE + CREATE_NODE;
	/** Show stack trace for fatal errors. */
	public static final long ERR_STACK_TRACE = DISPLAY_LIST + DISPLAY_LIST;
	/** Show file reading operations. */
	public static final long FILES = ERR_STACK_TRACE + ERR_STACK_TRACE;
	/**
	 * Print a protocol of session/block states, subject error states, and
	 * feedback conditions.
	 */
	public static final long STATE_CTRL = FILES + FILES;
	/**
	 * Set a signal whenever a color is requested which cannot be shown on the
	 * current color display device.
	 */
	public static final long COLOR_GAMUT = STATE_CTRL + STATE_CTRL;
	/** Show voice key levels. */
	public static final long VOICE_KEY = COLOR_GAMUT + COLOR_GAMUT;
	/** Show Media Timing. */
	public static final long MEDIA_TIMING = VOICE_KEY + VOICE_KEY;
	/**
	 * Show some display properties of certain complicated Display objects.
	 */
	public static final long DSP_PROPS = MEDIA_TIMING + MEDIA_TIMING;
	/** Trace expression evaluation. */
	public static final long EXPR = DSP_PROPS + DSP_PROPS;
	/** Trace assignment execution. */
	public static final long ASSIGNMENT = EXPR + EXPR;
	/** Log external event detection. */
	public static final long EVENTS = ASSIGNMENT + ASSIGNMENT;
	/** Factor levels and condition table protocol. */
	public static final long FACTORS = EVENTS + EVENTS;
	/** Adaptive procedure debugging. */
	public static final long ADAPTIVE = FACTORS + FACTORS;
	/** Show DisplayList argument values after the display list has been run. */
	public static final long ARGVALUES = ADAPTIVE + ADAPTIVE;
	/** Debug data analysis objects. */
	public static final long DATA = ARGVALUES + ARGVALUES;
	/** Show text frames and position reference. */
	public static final long TEXTPOS = DATA + DATA;
	/** Debug design tree factory. */
	public static final long FACTORY = TEXTPOS + TEXTPOS;
	/** Log file cache usage. */
	public static final long CACHE = FACTORY + FACTORY;
	/** Tree editing operations. */
	public static final long EDIT = CACHE + CACHE;
	/** Watch symbol table creation. */
	public static final long SYMBOL_TABLE = EDIT + EDIT;
	/** Multiple session generation */
	public static final long MULTISESSION = SYMBOL_TABLE + SYMBOL_TABLE;
	/** Static Base parameters */
	public static final long BASE = MULTISESSION + MULTISESSION;
	/** Color Device Transform Changes */
	public static final long COLOR_DEVICE = BASE + BASE;
	// -----------------------------------------------
	// End of Debug masks
	// -----------------------------------------------
	// -----------------------------------------------
	//
	// -----------------------------------------------
	private static boolean timerCheck = false;
	private static boolean nanoDebugTimer = false;
	private static ArrayList timingLog = new ArrayList(200);
	private static boolean memoryMonitorIsRunning = false;
	private static boolean showImmediately = false;
	// The higher the debug level the more messages are shown
	private static long mask = NO_DEBUGGING;
	// private static long mask = PARSER;
	private static boolean debugLayout = false;

	public static boolean layout() {
		return debugLayout;
	}

	public static void setDebugLayout(boolean b) {
		debugLayout = b;
	}

	/**
	 * Show the given debug message from the given source object iff the current
	 * debug mask pattern contains the given mask.
	 */
	public static void show(long v, Object a, String msg) {
		if ((mask & v) != 0L) {
			/*
			 * Thread ct = Thread.currentThread(); if (a != null)
			 * Syslog.out.println(ct + " " + a.getClass().getName() + ": " +
			 * msg); else Syslog.out.println(ct + " " + msg);
			 */
			if (a != null)
				Syslog.out.println(a.getClass().getName() + ": " + msg);
			else
				Syslog.out.println(msg);
		}
	}

	/**
	 * Show the given debug message iff the current debug mask pattern contains
	 * the given mask.
	 */
	public static void show(long v, String msg) {
		show(v, null, msg);
	}

	/**
	 * Show the given debug message iff the current debug mask pattern contains
	 * the given mask.
	 */
	public static void showTime(long v, Object a, String msg) {
		if ((mask & v) != 0L) {
			Thread ct = Thread.currentThread();
			Syslog.out.println(ct + " " + a.getClass().getName() + ": " + msg
					+ " [" + HiresClock.getTimeNanos() + "]");
		}
	}

	/** Set the debug mask pattern. */
	public static void setMask(long a) {
		mask = a;
		Syslog.out.println("Debug.setMask(): mask = " + mask);
	}

	/** Add a debug mask pattern. */
	public static void add(long a) {
		mask = mask | a;
		// System.out.println("Debug.add(): mask = " + mask);
	}

	/** Remove a debug mask pattern. */
	public static void remove(long a) {
		mask = mask & (~a);
		Syslog.out.println("Debug.remove(): mask = " + mask);
	}

	/**
	 * Check whether the current debug mask pattern contains the given mask.
	 */
	public static boolean isActive(long v) {
		return ((mask & v) != 0);
	}

	/**
	 * Check whether the current mask pattern contains any active mask.
	 */
	public static boolean isActive() {
		return (mask != NO_DEBUGGING);
	}

	/**
	 * Start a memory monitor dialog which continuously shows the amount of heap
	 * memory available.
	 */
	public static void startMemoryMonitor() {
		if (!memoryMonitorIsRunning) {
			new de.pxlab.util.MemoryMonitor();
			memoryMonitorIsRunning = true;
		}
	}

	/**
	 * Check which component, if any, in the given component's layout hierarchy
	 * has the keyboard focus.
	 */
	public static void checkFocus(java.awt.Component c) {
	}

	/**
	 * Set a debug option. Debug options are started by '-D' and have an
	 * argument which tells the system which debugging option should be set.
	 * Arguments are not case sensitive and only the first x characters are used
	 * to identify the option.
	 */
	public static void add(String p) {
		DebugOptionCodeMap ocm = new DebugOptionCodeMap();
		if (ocm.hasCodeFor(p)) {
			add((long) (ocm.getCode()));
		}
		System.out.println("Debug.add(): " + p + " mask = " + mask);
	}

	public static void showOptions() {
		DebugOptionCodeMap ocm = new DebugOptionCodeMap();
		System.out.println(ocm.toString());
	}

	public static void time() {
		if (timerCheck) {
			TimeLogEntry t = new TimeLogEntry(HiresClock.getTimeNanos());
			timingLog.add(t);
			if (showImmediately) {
				System.out.println(t.toString());
			}
		}
	}

	public static void time(String s) {
		if (timerCheck) {
			TimeLogEntry t = new TimeLogEntry(s, HiresClock.getTimeNanos());
			timingLog.add(t);
			if (showImmediately) {
				System.out.println(t.toString());
			}
		}
	}

	public static void timeMsg(String s) {
		if (timerCheck) {
			TimeLogEntry t = new TimeLogEntry(s);
			timingLog.add(t);
			if (showImmediately) {
				System.out.println(t.toString());
			}
		}
	}

	public static void clearTiming() {
		clearTiming(null);
	}

	public static void clearTiming(String s) {
		if (Debug.isActive(TIMING) || Debug.isActive(HR_TIMING)) {
			timerCheck = true;
			nanoDebugTimer = Debug.isActive(HR_TIMING);
		} else {
			timerCheck = false;
			nanoDebugTimer = false;
		}
		if (timerCheck) {
			timingLog.clear();
			if (s != null)
				time(s);
		}
	}

	/** Print the current entries of the timing log array. */
	public static void printTiming() {
		if (timerCheck) {
			int n = timingLog.size();
			for (int i = 0; i < n; i++) {
				System.out.println(((TimeLogEntry) (timingLog.get(i))));
			}
			System.out.print("\n");
			timingLog.clear();
		}
	}
	private static class TimeLogEntry {
		String m;
		long t;

		public TimeLogEntry(long tm) {
			this(null, tm);
		}

		public TimeLogEntry(String mm) {
			this(mm, 0L);
		}

		public TimeLogEntry(String mm, long tm) {
			m = mm;
			t = tm;
		}

		public String toString() {
			if (t > 0L) {
				String tm = String.valueOf(nanoDebugTimer ? t : Math
						.round(HiresClock.ms(t)));
				return (m != null) ? m + tm : tm;
			} else {
				return m;
			}
		}
	}
}
