package de.pxlab.pxl;

/**
 * Vertical retrace synchronization methods and video refresh data. This class
 * provides a static VideoSynchronization object which can be used for
 * synchronizing drawing operations to the vertical blanking interval. Since
 * this is a feature which requires native code the class also provides
 * emulation code for those systems which do not have vertical blanking
 * synchronization support.
 * 
 * <p>
 * To work properly the following requirements must be met:
 * 
 * <ol>
 * <li>Only applications can use this code since it requires the native library
 * pxlab.dll which may only be accessed by applications. Proper applets can
 * access the methods from this class but they only will get the emulation code.
 * 
 * <li>The methods used here rely on Windows DirectX functions and thus they
 * only work if DirectX is installed.
 * 
 * </ol>
 * 
 * <p>
 * The vertical retrace blanking interval on my screen with 1600x1200 at 75 Hz
 * is approximately 0.025 ms. The visible interval is approximately 13.31 ms.
 * These times have been measured with a WindowsPerformanceCounter timer object.
 * 
 * @version 0.1.0
 */
public class VideoSystem {
	private static VideoSynchronization vsync = new VideoSynchronizationEmulation();
	static {
		if (Base.isApplication()) {
			try {
				Debug.show(Debug.TIMING,
						"Trying to instantiate DirectDraw video synchronization.");
				vsync = new DirectDrawVideoSynchronization();
				if (!vsync.init()) {
					Debug.show(Debug.TIMING,
							"Can't initialize DirectDraw video synchronization - use emulation.");
					vsync = new VideoSynchronizationEmulation();
				}
				Debug.show(Debug.TIMING,
						"DirectDraw video synchronization instantiated.");
			} catch (NoClassDefFoundError ndf) {
				Syslog.out
						.println("VideoSystem: Can't initialize video synchronization.");
				// System.out.println("VideoSystem: Can't initialize video synchronization.");
				// vsync = new VideoSynchronizationEmulation();
			} catch (SecurityException sex) {
				Syslog.out
						.println("VideoSystem: Can't access pxlab.dll - use emulated video synchronization only.");
				// System.out.println("VideoSystem: Can't access pxlab.dll - use emulated video synchronization only.");
			} catch (UnsatisfiedLinkError ule) {
				Syslog.out
						.println("VideoSystem: Can't find pxlab.dll - use emulated video synchronization only.");
				// System.out.println("VideoSystem: Can't find pxlab.dll - use emulated video synchronization only.");
			}
		} else {
			// Syslog.out.println("VideoSystem: No application - no Direct Draw video synchronization.");
			// System.out.println("VideoSystem: No application - no Direct Draw video synchronization.");
		}
		double fd = getFrameDuration();
		Base.setDisplayDeviceFrameDuration(fd);
		ExPar.VideoFrameDuration.set(fd);
	}

	/** Wait until the next vertical blanking interval starts. */
	public static void waitForBeginOfVerticalBlank() {
		vsync.waitForBeginOfVerticalBlank();
	}

	/**
	 * Wait until the current/next vertical blanking interval is finished.
	 * public static void waitForEndOfVerticalBlank() {
	 * vsync.waitForBeginOfVerticalBlank(); }
	 */
	/**
	 * Check whether we currently are in a vertical blanking interval. Note that
	 * vertical blanking duration is only approximately 0.02 to 0.03 ms. public
	 * static boolean inVerticalBlank() { return vsync.inVerticalBlank(); }
	 */
	public static double getFrameDuration() {
		return vsync.getFrameDuration();
	}
}
