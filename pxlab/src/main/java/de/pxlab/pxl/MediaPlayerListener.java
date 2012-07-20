package de.pxlab.pxl;

/**
 * Provides a ControllerListener for the MediaPlayerElement. This must be
 * separate from the MediaPlayerElement since otherwise the JMF must be present
 * whenever a de.pxlab.pxl.display.MediaPlayer object is instantiated.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see MediaPlayerElement
 */
public class MediaPlayerListener implements javax.media.ControllerListener {
	protected MediaPlayerElement mpe;

	public MediaPlayerListener(MediaPlayerElement mpe) {
		this.mpe = mpe;
	}

	// ------------------------------------------------------------------
	// implements the ControllerListener interface
	// ------------------------------------------------------------------
	public synchronized void controllerUpdate(javax.media.ControllerEvent event) {
		// Debug.showTime(Debug.MEDIA_TIMING, this, "ControllerEvent " + event);
		if (event instanceof javax.media.RealizeCompleteEvent) {
			mpe.realizeCompleteEvent();
		} else if (event instanceof javax.media.CachingControlEvent) {
			mpe.cachingControlEvent();
		} else if (event instanceof javax.media.PrefetchCompleteEvent) {
			mpe.prefetchCompleteEvent();
		} else if (event instanceof javax.media.StartEvent) {
			mpe.startEvent();
		} else if (event instanceof javax.media.EndOfMediaEvent) {
			mpe.endOfMediaEvent();
		} else if (event instanceof javax.media.StopByRequestEvent) {
			mpe.stopByRequestEvent();
		} else if (event instanceof javax.media.ControllerClosedEvent) {
			mpe.controllerClosedEvent();
		} else if (event instanceof javax.media.ResourceUnavailableEvent) {
			mpe.resourceUnavailableEvent(((javax.media.ResourceUnavailableEvent) event)
					.getMessage());
		} else if (event instanceof javax.media.ControllerErrorEvent) {
			mpe.controllerErrorEvent(((javax.media.ControllerErrorEvent) event)
					.getMessage());
		}
	}
}
