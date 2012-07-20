package de.pxlab.pxl;

import java.awt.*;
import java.net.*;

import de.pxlab.util.*;

/**
 * Plays audiovisual media which are supported by the <a
 * href="http://java.sun.com/products/java-media/jmf/">Java Media Framework
 * (JMF)</a> package. This creates a player for a given media file and then
 * plays the file on request. Media playing runs in its own thread and uses a
 * END_OF_MEDIA_TIMER to signal the PresentationManager that the media is
 * finished.
 * 
 * <p>
 * It is a strict rule that the object which starts the media player also has to
 * close it and free the player's resources by calling the dispose() method!
 * 
 * @version 0.1.2
 */
/*
 * 
 * 2005/05/09
 * 
 * 2005/10/19 removed the ControllerListener interface in order to make sure
 * that this file can be used without the JMF being installed.
 */
public class MediaPlayerElement extends DisplayElement {
	protected javax.media.Player player;
	protected Component visualComponent;
	protected int referenceCode;
	protected MediaEventListener mediaEventListener;
	protected MediaPlayerListener mediaPlayerListener;
	protected Container container;
	protected Panel panel;
	protected Color bgc;
	protected MediaMonitor monitor;
	protected boolean fastStart = false;
	protected boolean cycle = false;
	protected long showTime;
	protected long startTime;
	private static final int PREP_OPEN_MEDIA = 1;
	private static final int PREP_START_MEDIA = 2;
	private static final int PREP_CLOSE_MEDIA = 3;

	public MediaPlayerElement() {
		type = DisplayElement.PICTURE;
		monitor = new MediaMonitor();
	}

	/**
	 * Prepare the media player for the given media file and cache initial media
	 * data in order to get the fastest possible start time. For fastStart
	 * players this method also attaches the player's visual component to the
	 * display device. This may be undesirable since it ovelays the display
	 * screen at the movie display frame. The method returns after the media
	 * data prefetch is complete.
	 */
	public void setProperties(DisplayDevice displayDevice, int x, int y, int w,
			int h, int ref, String dir, String fn, boolean fst, boolean cyc,
			MediaEventListener listener) {
		container = ((ExperimentalDisplayDevice) displayDevice).getWindow();
		setLocation(x, y);
		setSize(w, h);
		referenceCode = ref;
		fastStart = fst;
		cycle = cyc;
		mediaEventListener = listener;
		bgc = ExPar.ScreenBackgroundColor.getPxlColor().dev();
		URL url = FileBase.url(dir, fn);
		if (url != null) {
			panel = new Panel();
			panel.setLayout(null);
			javax.media.MediaLocator mrl = new javax.media.MediaLocator(url);
			try {
				player = javax.media.Manager.createPlayer(mrl);
				Debug.showTime(Debug.MEDIA_TIMING, this, "Player created for "
						+ mrl);
				player.addControllerListener(new MediaPlayerListener(this));
				monitor.prepare(PREP_OPEN_MEDIA);
				player.prefetch();
				monitor.waitForPlayer(3000);
				showTime = 0L;
				startTime = 0L;
			} catch (javax.media.NoPlayerException npe) {
				new FileError(
						"MediaPlayerElement.setProperties(): Could not create player for "
								+ mrl);
				player = null;
			} catch (java.io.IOException iox) {
				new FileError(
						"MediaPlayerElement.setProperties(): Could not find "
								+ mrl);
				player = null;
			}
		}
	}

	/** Synonym to start(). */
	public void show() {
		start();
	}

	/**
	 * Start the player. The player should be in the 'Prefetched' state when it
	 * is started. For non-fastStart players starting the player includes
	 * attaching the player's visual component to the display device.
	 */
	public void start() {
		showTime = HiresClock.getTimeNanos();
		Debug.showTime(Debug.MEDIA_TIMING, this, "MediaPlayerElement.start()");
		if (player != null) {
			if (!fastStart && visualComponent == null) {
				visualComponent = setVisual();
			}
			monitor.prepare(PREP_START_MEDIA);
			player.start();
			monitor.waitForPlayer(3000);
		}
	}

	/**
	 * Stop the player and free all resources associated with the player. The
	 * player can not be continued after this method has been called.
	 */
	public void stop() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "MediaPlayerElement.stop()");
		if (player != null) {
			monitor.prepare(PREP_CLOSE_MEDIA);
			player.stop();
			player.close();
			monitor.waitForPlayer(3000);
		}
	}

	/**
	 * Add a synchronization task to the player. The task will send a
	 * synchronization event of type ResponseCodes.SYNC_MEDIA to the
	 * MediaEventListener when the given media time has been reached.
	 */
	public void setSyncAt(long mediaTimeNanos) {
		new MediaSync(mediaTimeNanos).start();
	}
	private class MediaSync extends Thread {
		long syncMediaTimeNanos;

		public MediaSync(long t) {
			syncMediaTimeNanos = t;
		}
		private WaitLock waitLock = new WaitLock();

		public void run() {
			if (player.getState() == javax.media.Controller.Started) {
				long cmt = player.getMediaNanoseconds();
				if (cmt > 0L) {
					long dt = syncMediaTimeNanos - cmt;
					if (dt > 0L) {
						waitLock.waitForNanos(dt);
						long mt = player.getMediaNanoseconds();
						if (mediaEventListener != null) {
							mediaEventListener
									.mediaActionPerformed(new MediaEvent(
											MediaPlayerElement.this,
											ResponseCodes.SYNC_MEDIA, mt,
											HiresClock.getTimeNanos()));
						}
						Debug.showTime(Debug.MEDIA_TIMING, this,
								"Controller sync [MT=" + mt + "].");
					} else {
						System.out
								.println("MediaPlayerElement.MediaSync.run(): Negative synchronization delay.");
					}
				} else {
					System.out
							.println("MediaPlayerElement.MediaSync.run(): Player not running.");
				}
			} else {
				System.out
						.println("MediaPlayerElement.MediaSync.run(): Player not started.");
			}
		}
	}

	/*
	 * public void dispose() { Debug.showTime(Debug.MEDIA_TIMING, this,
	 * "MediaPlayerElement.dispose()"); if (player != null) { //
	 * monitor.prepare(ResponseCodes.CLOSE_MEDIA); // player.deallocate();
	 * player.close(); // monitor.waitForPlayer(3000); player = null; } }
	 */
	private Component setVisual() {
		Component visualComponent = null;
		if ((visualComponent = player.getVisualComponent()) != null) {
			container.add(panel);
			// This makes the movie frame area identical to the
			// background AFTER the visual component has been removed.
			container.setBackground(bgc);
			// visualComponent.setBackground(bgc);
			// visualComponent.setForeground(bgc);
			panel.setBackground(bgc);
			// panel.setForeground(bgc);
			panel.add(visualComponent);
			Dimension videoSize = visualComponent.getPreferredSize();
			int videoWidth = videoSize.width;
			int videoHeight = videoSize.height;
			int maxWidth = container.getWidth();
			int maxHeight = container.getHeight();
			if (size.width > 0 && size.height > 0) {
				if (size.width < maxWidth && size.height < maxHeight) {
					// Set movie component to requested size
					visualComponent.setBounds(0, 0, size.width, size.height);
					panel.setBounds(
							locX(referenceCode, location.x + maxWidth / 2,
									size.width),
							rectLocY(referenceCode, location.y + maxHeight / 2,
									size.height), size.width, size.height);
				} else {
					// Set movie component to maximum size
					visualComponent.setBounds(0, 0, maxWidth, maxHeight);
					panel.setBounds(0, 0, maxWidth, maxHeight);
				}
			} else {
				// No size requested, set movie component to its preferred size
				visualComponent.setBounds(0, 0, videoWidth, videoHeight);
				panel.setBounds(
						locX(referenceCode, location.x + container.getWidth()
								/ 2, videoWidth),
						rectLocY(referenceCode,
								location.y + container.getHeight() / 2,
								videoHeight), videoWidth, videoHeight);
			}
		} else {
			// System.out.println("MediaPlayerElement.controllerUpdate(): no visual component.");
		}
		return visualComponent;
	}

	/**
	 * Get the delay period between the call to the show()/start() method and
	 * the actual start of the media player as signaled by the 'Started' event.
	 * 
	 * @return the delay period in nanoseconds. If either the show()/start()
	 *         time or the actual start time have not yet beeen set then -1 is
	 *         returned.
	 */
	public long getStartDelay() {
		long t = -1L;
		if (showTime != 0L && startTime != 0) {
			t = startTime - showTime;
		}
		return t;
	}

	// -------------------------------------------------------------------------------
	// Media event handler
	// -------------------------------------------------------------------------------
	protected void realizeCompleteEvent() {
		// Debug.showTime(Debug.MEDIA_TIMING, this,
		// "Controller realize complete.");
		if (fastStart && visualComponent == null) {
			visualComponent = setVisual();
		}
	}

	protected void cachingControlEvent() {
		Debug.showTime(Debug.MEDIA_TIMING, this, "Controller caching control.");
		/*
		 * if (player.getState() > javax.media.Controller.Realizing) return;
		 */
	}

	protected void prefetchCompleteEvent() {
		long mt = player.getMediaNanoseconds();
		/*
		 * if (mediaEventListener != null) {
		 * mediaEventListener.mediaActionPerformed(new
		 * MediaEvent(MediaPlayerElement.this, ResponseCodes.OPEN_MEDIA, mt,
		 * HiresClock.getTimeNanos())); }
		 */
		Debug.showTime(Debug.MEDIA_TIMING, this,
				"Controller prefetch complete [MT=" + mt + "].");
		monitor.playerReady(PREP_OPEN_MEDIA);
	}

	protected void startEvent() {
		startTime = HiresClock.getTimeNanos();
		long mt = player.getMediaNanoseconds();
		/*
		 * if (mediaEventListener != null) {
		 * mediaEventListener.mediaActionPerformed(new
		 * MediaEvent(MediaPlayerElement.this, ResponseCodes.START_MEDIA, mt,
		 * startTime)); }
		 */
		Debug.showTime(Debug.MEDIA_TIMING, this, "Controller started [MT=" + mt
				+ "].");
		monitor.playerReady(PREP_START_MEDIA);
	}

	protected void stopByRequestEvent() {
		long mt = player.getMediaNanoseconds();
		/*
		 * if (mediaEventListener != null) {
		 * mediaEventListener.mediaActionPerformed(new
		 * MediaEvent(MediaPlayerElement.this, ResponseCodes.STOP_MEDIA, mt,
		 * HiresClock.getTimeNanos())); }
		 */
		Debug.showTime(Debug.MEDIA_TIMING, this,
				"Controller stopped by request [MT=" + mt + "].");
	}

	protected void endOfMediaEvent() {
		if (cycle) {
			player.setMediaTime(new javax.media.Time(0L));
			player.start();
		} else {
			long mt = player.getMediaNanoseconds();
			player.close();
			/*
			 * if (mediaEventListener != null) {
			 * mediaEventListener.mediaActionPerformed(new
			 * MediaEvent(MediaPlayerElement.this, ResponseCodes.STOP_MEDIA, mt,
			 * HiresClock.getTimeNanos())); }
			 */
			Debug.showTime(Debug.MEDIA_TIMING, this,
					"Controller found end of media [MT=" + mt + "].");
		}
	}

	protected void controllerClosedEvent() {
		monitor.playerReady(PREP_CLOSE_MEDIA);
		long mt = (player != null) ? player.getMediaNanoseconds() : 0L;
		if (mediaEventListener != null) {
			mediaEventListener.mediaActionPerformed(new MediaEvent(
					MediaPlayerElement.this, ResponseCodes.CLOSE_MEDIA, mt,
					HiresClock.getTimeNanos()));
		}
		if (visualComponent != null) {
			panel.removeAll();
			container.remove(panel);
			visualComponent = null;
		}
		Debug.showTime(Debug.MEDIA_TIMING, this, "Controller closed.");
		// player = null;
	}

	protected void resourceUnavailableEvent(String s) {
		player = null;
		new FileError(s);
	}

	protected void controllerErrorEvent(String s) {
		player = null;
		new FileError(s);
	}
	// -------------------------------------------------------------------------------
	// End of media event handler
	// -------------------------------------------------------------------------------
	private class MediaMonitor {
		private int eventCode;
		private int lastEventCode;
		private boolean tmOut;

		public void prepare(int e) {
			eventCode = e;
			lastEventCode = 0;
			Debug.showTime(Debug.MEDIA_TIMING, this, "Prepare for event "
					+ eventCode + " [" + HiresClock.getTime() + "]");
		}

		public synchronized void waitForPlayer(int timeOut) {
			if (eventCode != 0) {
				tmOut = true;
				try {
					Debug.showTime(Debug.MEDIA_TIMING, this,
							"Waiting until player signals event " + eventCode
									+ " [" + HiresClock.getTime() + "]");
					wait(timeOut);
					if (tmOut) {
						Debug.showTime(Debug.MEDIA_TIMING, this,
								"Player timeout. [" + HiresClock.getTime()
										+ "]");
					} else {
						Debug.showTime(Debug.MEDIA_TIMING, this,
								"Player ready by event " + lastEventCode
										+ ". [" + HiresClock.getTime() + "]");
					}
					eventCode = 0;
				} catch (InterruptedException iex) {
				}
			} else {
				Debug.showTime(Debug.MEDIA_TIMING, this,
						"No waiting necessary. Already found event "
								+ lastEventCode);
			}
		}

		public synchronized void playerReady(int e) {
			if (e == eventCode) {
				lastEventCode = e;
				eventCode = 0;
				tmOut = false;
				notify();
			}
		}
	}
}
