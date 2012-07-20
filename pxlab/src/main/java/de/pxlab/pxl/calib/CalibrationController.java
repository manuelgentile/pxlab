package de.pxlab.pxl.calib;

import java.awt.*;
import java.util.*;

public interface CalibrationController {
	/**
	 * Returns the controlling Frame. This must be a container which is allowed
	 * to get the keyboard focus since the GammaMeasurement object will attach a
	 * keyboard listener to it.
	 */
	public Frame getFrame();

	/**
	 * Tells the controller that gamma measurement for the given channel has
	 * been started.
	 */
	public void measurementStarted(int channel);

	/**
	 * Tells the controller that gamma measurement for the given channel has
	 * been finished.
	 */
	public void measurementFinished(int channel, Object[] data);

	/**
	 * Tells the controller that gamma measurement has been finished completely
	 * for all channels requested.
	 */
	public void measurementFinished(ArrayList dataTables);

	/** Tells the controller that measurement has been stopped prematurely. */
	public void measurementStopped();
}
