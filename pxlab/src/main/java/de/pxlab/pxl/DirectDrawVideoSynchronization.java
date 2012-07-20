package de.pxlab.pxl;

/**
 * Windows native code video sychronization based on Microsoft's DirectDraw
 * library. This class should not be used directly but only via
 * VideoSynchronization objects which make sure that only a single object of
 * this class gets instantiated for any application.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see VideoSystem
 */
public class DirectDrawVideoSynchronization implements VideoSynchronization {
	static {
		System.loadLibrary("pxlab");
	}
	private double frameDuration;

	protected DirectDrawVideoSynchronization() {
	}

	public native void waitForBeginOfVerticalBlank();

	public native void waitForEndOfVerticalBlank();

	public native boolean inVerticalBlank();

	public native boolean init();

	public native void close();

	/** Compute the duration of a single video frame. */
	public double getFrameDuration() {
		computeVideoParams();
		return frameDuration;
	}
	private long lastFrameDuration = 0L;

	public void computeVideoParams() {
		int n = 6;
		long t1, t2;
		waitForBeginOfVerticalBlank();
		waitForEndOfVerticalBlank();
		t1 = HiresClock.getTimeNanos();
		for (int i = 0; i < n; i++) {
			waitForBeginOfVerticalBlank();
			waitForEndOfVerticalBlank();
		}
		t2 = HiresClock.getTimeNanos();
		frameDuration = ((double) (t2 - t1)) / ((n) * 1000000.0);
		if ((lastFrameDuration != Math.round(frameDuration))
				&& Debug.isActive(Debug.TIMING)) {
			System.out.println("\nMeasured Video Data:");
			System.out.println("Video Frame Duration = " + frameDuration
					+ " ms");
			System.out.println("Video Refresh Rate =   "
					+ (1000.0 / frameDuration) + " Hz\n");
			lastFrameDuration = Math.round(frameDuration);
		}
	}
}
