package de.pxlab.pxl;

import java.awt.*;

/**
 * A display device which can be used as a color calibration target.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
public interface CalibrationTarget {
	/** Set the size of the color calibration target patch. */
	public void setCalibrationTargetSize(Dimension size);

	/** Get the size of the color calibration target patch. */
	public Dimension getCalibrationTargetSize();

	/** Set the screen position of the color calibration target patch center. */
	public void setCalibrationTargetPosition(Point p);

	/** Get the screen position of the color calibration target patch. */
	public Point getCalibrationTargetPosition();

	/** Get the number of primary color channels of this calibration target. */
	public int getNumberOfCalibrationChannels();

	/**
	 * Get the calibration channel codes for the device's primary channels.
	 * 
	 * @return an array of integer values which represent calibration channel
	 *         codes.
	 */
	public int[] getPrimaryCalibrationCodes();

	/**
	 * Get all available calibration channel codes for this device.
	 * 
	 * @return an array of integer values which represent calibration channel
	 *         codes.
	 */
	public int[] getAllCalibrationCodes();

	/**
	 * Get the number of intensity levels of a single channel of the color
	 * calibration target.
	 */
	public int getCalibrationTargetResolution();

	/** Open the color display device for calibration. */
	public void openDevice();

	/** Close the color display devivce after calibration. */
	public void closeDevice();

	/** Clear the color calibration target. */
	public void clearCalibrationTarget();

	/**
	 * Show an alignment pattern which helps to position the measurement sensor.
	 */
	public void showCalibrationAlignmentPattern();

	/**
	 * Show the given intensity level at the given channel.
	 * 
	 * @param channel
	 *            the color channels to be used.
	 * @param value
	 *            the intensity level to be used.
	 */
	public void showCalibrationTarget(int channel, int value);

	/**
	 * Show a calibration target with the given color.
	 * 
	 * @param c
	 *            the color to be shown.
	 */
	public void showCalibrationTarget(PxlColor c);

	/**
	 * Show a calibration target with the given color.
	 * 
	 * @param c
	 *            the color to be shown.
	 */
	public void showCalibrationTarget(Color c);
}
