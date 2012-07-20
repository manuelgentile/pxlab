package de.pxlab.pxl;

/**
 * This is an object which can start an experimental run by calling method
 * runExperiment() of class ExDesign. It is the destination of two types of
 * messages: the first message tells the starter that the experiment has been
 * finished in an orderly manner. The second type of message tells it that there
 * was some error or break request and the experimental run should be stopped
 * immediately.
 * 
 * @author H. Irtel
 * @version 0.2.0
 * @see ExDesign
 */
public interface ExDesignThreadStarter {
	/**
	 * The inner class ExRunThread of class ExDesign calls this method at the
	 * end of its run() method to signal the object which startet the thread,
	 * that the experimental run is finished.
	 * 
	 * @param s
	 *            is true if the experimental run had been stopped early by some
	 *            error or a stop/break request. Is false if no error happened
	 *            and the experiment finished completely.
	 */
	public void experimentFinished(boolean s);

	/**
	 * The running ExDesign object or the PresentationManager object may call
	 * this method to signal the tread starter that it should stop presentation
	 * immediately. This request usually originates from someone having pressed
	 * a STOP button or from the KeeyboardResponseDispatcher which may have
	 * detected a Pause/Break key press.
	 */
	public void stopExperiment();
}
