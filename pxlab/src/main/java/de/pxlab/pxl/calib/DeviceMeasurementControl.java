package de.pxlab.pxl.calib;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.text.NumberFormat;
import java.util.Locale;

import de.pxlab.util.StringExt;
import de.pxlab.pxl.CalibrationTarget;
import de.pxlab.pxl.PxlColor;
import de.pxlab.pxl.YxyColor;
import de.pxlab.pxl.RandomColor;
import de.pxlab.pxl.CIELabStar;

/**
 * This class does gamma function measurement. Its main entry point is the
 * startMeasurement() method. This method connects to the color measurement
 * device, opens the calibration target, and then waits for the user to start
 * the measurement thread. This thread cycles through the requested channels and
 * gets measurement data for every output value. After it is finished it signals
 * the controller that it is done and closes the calibration target window.
 */
public class DeviceMeasurementControl implements Runnable {
	/** The owner of the measurement window. */
	private Frame owner;
	/** The owner of the measurement window. */
	private CalibrationController controller;
	/**
	 * If true then the program waits for a separate start key after the
	 * measurement window has been set up. If false then the program starts
	 * measurement immediately when startMeasurement() is called.
	 */
	private boolean wait4StartKey = true;
	/**
	 * Determines the output step size between successive output values. Note
	 * that output values start at that value which is returned by
	 * target.getLimit() and then are decreased by the measurementStep.
	 */
	private int measurementStep = 16;
	/**
	 * The time delay between showing the target and initiating the measurement
	 * after large target color changes.
	 */
	private int largeMeasurementDelay = 1000;
	/**
	 * The time delay between showing the target and initiating the measurement
	 * after small target color changes.
	 */
	private int smallMeasurementDelay = 300;
	/**
	 * This is an array of data tables which contain the measurement data. The
	 * size of this array depends on the number of channels to be measured.
	 * There is one array of double[] data for each channel.
	 */
	private ArrayList dataTables = new ArrayList(7);
	/**
	 * A list of PxlColor objects which are used as target or evaluation colors.
	 */
	private ArrayList targetColors = null;
	/** The calibration target we use. */
	private CalibrationTarget target;
	/** Contains the series of channels which should be measured. */
	private int[] channels;
	/** The color measurement device we use. */
	private ColorMeasurementDevice measurementDevice;
	private boolean measurementDeviceInitialized;
	private int measurementRepetitions = 1;

	private void setMeasurementRepetitions(int n) {
		measurementRepetitions = (n > 0) ? n : 1;
	}
	/**
	 * True if the programm currently is waiting for the start key.
	 */
	private boolean waiting4StartKey = false;
	private JProgressBar progressBar;
	private int progressCount;

	/**
	 * Check whether the programm currently is waiting for the start button/key.
	 */
	public boolean isWaiting4StartKey() {
		return waiting4StartKey;
	}
	/**
	 * True while the measurement process is running. Setting this to false
	 * stops the measurement process.
	 */
	private boolean measurementActive = false;
	/** Selects the task type which is currently running. */
	private int activeTask = 0;
	private static NumberFormat dn = NumberFormat.getInstance(Locale.US);
	static {
		dn.setMaximumFractionDigits(4);
		dn.setGroupingUsed(false);
	}

	/**
	 * Create a devive measurement control object. This does not yet start
	 * measurement.
	 */
	public DeviceMeasurementControl(CalibrationController controller) {
		this.controller = controller;
		createOptions();
		measurementDeviceInitialized = false;
	}

	/**
	 * Start gamma measurement. This method connects to the color measurement
	 * device, opens the calibration target and prepares it for waiting for the
	 * start key. The actual measurement only starts after the start key has
	 * been pressed. This is handled byte this class's action listener.
	 * 
	 * @param target
	 *            the color calibration target.
	 * @param channels
	 *            the channel numbers which have to be measured.
	 * @param measurementDevice
	 *            the device which should be used for measurement.
	 * @param progressBar
	 *            the progress bar object which monitors measurement progress.
	 */
	public void startMeasurement(CalibrationTarget target, int[] channels,
			ColorMeasurementDevice measurementDevice, JProgressBar progressBar) {
		this.target = target;
		this.channels = channels;
		this.measurementDevice = measurementDevice;
		this.progressBar = progressBar;
		progressCount = 0;
		activeTask = ColorCalibrationTool.GAMMA_PARS;
		owner = controller.getFrame();
		if (initMeasurementDevice()) {
			target.openDevice();
			if (wait4StartKey) {
				waiting4StartKey = true;
				target.showCalibrationAlignmentPattern();
				System.out
						.println("DeviceMeasurementControl.startMeasurement(): Waiting ...");
			} else {
				startMeasurementProcess();
			}
		}
	}

	private boolean initMeasurementDevice() {
		if (!measurementDeviceInitialized) {
			System.out
					.println("DeviceMeasurementControl.startMeasurement(): Trying to initialize measurement device ...");
			if (measurementDevice.connect()) {
				measurementDevice.setTristimulusEmittanceMode();
				System.out
						.println("DeviceMeasurementControl.initMeasurementDevice(): Measurement device initialized.");
				measurementDeviceInitialized = true;
			} else {
				System.out
						.println("DeviceMeasurementControl.initMeasurementDevice(): Can't initialize measurement device.");
				measurementDeviceInitialized = false;
			}
		}
		return measurementDeviceInitialized;
	}

	/**
	 * Start gamma measurement. This method connects to the color measurement
	 * device, opens the calibration target and prepares it for waiting for the
	 * start key. The actual measurement only starts after the start key has
	 * been pressed. This is handled byte this class's action listener.
	 * 
	 * @param target
	 *            the color calibration target.
	 * @param measurementDevice
	 *            the device which should be used for measurement.
	 * @param targets
	 *            a list of color targets.
	 * @param progressBar
	 *            the progress bar object which monitors measurement progress.
	 */
	public void startEvaluation(CalibrationTarget target,
			ColorMeasurementDevice measurementDevice, ArrayList targets,
			JProgressBar progressBar) {
		this.target = target;
		this.measurementDevice = measurementDevice;
		this.progressBar = progressBar;
		progressCount = 0;
		activeTask = ColorCalibrationTool.EVALUATION;
		owner = controller.getFrame();
		if (initMeasurementDevice()) {
			target.openDevice();
			// Create target colors after the device has been opened
			// in order to have a valid device transform
			targetColors = (targets == null) ? randomTargetColors() : targets;
			if (wait4StartKey) {
				waiting4StartKey = true;
				target.showCalibrationAlignmentPattern();
				System.out
						.println("DeviceMeasurementControl.startEvaluation(): Waiting ...");
			} else {
				startEvaluationProcess();
			}
		}
	}

	/** Create a list of target colors. */
	private ArrayList randomTargetColors() {
		int nEval = 10;
		ArrayList t = new ArrayList(nEval);
		RandomColor rc = new RandomColor(PxlColor.CS_LabStar);
		double[] r;
		for (int j = 0; j < nEval; j++) {
			r = rc.next();
			t.add(PxlColor.instance(PxlColor.CS_LabStar, r));
		}
		return t;
	}

	/**
	 * Start gamma measurement. This method connects to the color measurement
	 * device, opens the calibration target and prepares it for waiting for the
	 * start key. The actual measurement only starts after the start key has
	 * been pressed. This is handled byte this class's action listener.
	 * 
	 * @param target
	 *            the color calibration target.
	 * @param measurementDevice
	 *            the device which should be used for measurement.
	 * @param targetColors
	 *            the list of target colors to be created.
	 * @param progressBar
	 *            the progress bar object which monitors measurement progress.
	 */
	public void startColorTable(CalibrationTarget target,
			ColorMeasurementDevice measurementDevice, ArrayList targetColors,
			JProgressBar progressBar) {
		this.target = target;
		this.measurementDevice = measurementDevice;
		this.targetColors = targetColors;
		this.progressBar = progressBar;
		progressCount = 0;
		activeTask = ColorCalibrationTool.COLOR_TABLE;
		owner = controller.getFrame();
		if (initMeasurementDevice()) {
			target.openDevice();
			if (wait4StartKey) {
				waiting4StartKey = true;
				target.showCalibrationAlignmentPattern();
				System.out
						.println("DeviceMeasurementControl.startMeasurement(): Waiting ...");
			} else {
				startColorTableProcess();
			}
		}
	}

	/**
	 * This actually starts the measurement thread. This method is executed when
	 * the user is ready to actually start measurement. Note that this method
	 * will always be executed by the event handling thread.
	 */
	public void startMeasurementProcess() {
		System.out
				.println("DeviceMeasurementControl.startMeasurementProcess()");
		if (waiting4StartKey) {
			waiting4StartKey = false;
		}
		Thread mThread = new Thread(this);
		measurementActive = true;
		mThread.start();
	}

	/**
	 * This actually starts the measurement thread. This method is executed when
	 * the user is ready to actually start measurement. Note that this method
	 * will always be executed by the event handling thread.
	 */
	public void startEvaluationProcess() {
		System.out.println("DeviceMeasurementControl.startEvaluationProcess()");
		Thread mThread = new Thread(this);
		measurementActive = true;
		mThread.start();
	}

	/**
	 * This actually starts the measurement thread. This method is executed when
	 * the user is ready to actually start measurement. Note that this method
	 * will always be executed by the event handling thread.
	 */
	public void startColorTableProcess() {
		System.out.println("DeviceMeasurementControl.startColorTableProcess()");
		Thread mThread = new Thread(this);
		measurementActive = true;
		mThread.start();
	}

	public void stopMeasurement() {
		measurementActive = false;
	}

	/**
	 * Implements the Runnable interface. It executes the measurement process
	 * outside of the application's event handling thread. When the measurement
	 * cycle is finished for every channel then the controller is signaled that
	 * measuerement is finsihed and the calibration target is closed.
	 */
	public void run() {
		ArrayList data = null;
		if (activeTask == ColorCalibrationTool.EVALUATION) {
			System.out.println("Evaluation running.");
			setMeasurementRepetitions(2);
			int nEval = targetColors.size();
			data = new ArrayList(nEval);
			for (int j = 0; j < nEval; j++) {
				PxlColor t = (PxlColor) targetColors.get(j);
				double[] tLab = t.toLabStar();
				target.showCalibrationTarget(t);
				waitMS(largeMeasurementDelay);
				double[] m = getMeasurement();
				double[] mLab = new YxyColor(m).toLabStar();
				double[] d = new double[13];
				d[0] = t.getY();
				d[1] = t.getx();
				d[2] = t.gety();
				for (int i = 0; i < 3; i++)
					d[i + 3] = m[i];
				for (int i = 0; i < 3; i++)
					d[i + 6] = tLab[i];
				for (int i = 0; i < 3; i++)
					d[i + 9] = mLab[i];
				d[12] = CIELabStar.deltaE(tLab, mLab);
				data.add(d);
				progressBar.setValue((255 * (j + 1)) / nEval);
			}
		} else if (activeTask == ColorCalibrationTool.COLOR_TABLE) {
			System.out.println("Cumpute color table.");
			int nEval = targetColors.size();
			data = new ArrayList(nEval);
			for (int j = 0; j < nEval; j++) {
				PxlColor t = (PxlColor) targetColors.get(j);
				Color c = nearestColor(t);
				PxlColor m = getMeasurementValue(c);
				data.add(dataArray(t, m, c));
				printNearestColor(t, m, c);
				progressBar.setValue((255 * (j + 1)) / nEval);
			}
		} else if (activeTask == ColorCalibrationTool.GAMMA_PARS) {
			System.out.println("Gamma curve measurement running.");
			data = new ArrayList(7);
			for (int i = 0; measurementActive && (i < channels.length); i++) {
				int channel = channels[i];
				controller.measurementStarted(channel);
				Object[] channelData = getChannelData(channel);
				data.add(channelData);
				controller.measurementFinished(channel, channelData);
			}
		}
		setMeasurementRepetitions(1);
		target.closeDevice();
		// measurementDevice.close();
		if (!measurementActive) {
			controller.measurementStopped();
		} else {
			controller.measurementFinished(data);
		}
		measurementActive = false;
	}

	private Color nearestColor(PxlColor t) {
		Color x = t.dev();
		int[] rgb = new int[3];
		rgb[0] = x.getRed();
		rgb[1] = x.getGreen();
		rgb[2] = x.getBlue();
		double minDelta, newDelta;
		int d, dirCount;
		System.out
				.println("DeviceMeasurementControl.nearestColor() Phase 1: axis directions");
		setMeasurementRepetitions(1);
		for (int w = 0; w < 1; w++) {
			for (int k = 0; k < 3; k++) {
				minDelta = delta(t, rgb);
				d = 1;
				dirCount = 0;
				do {
					rgb[k] += d;
					if (rgb[k] < 0) {
						rgb[k] = 0;
						break;
					} else if (rgb[k] > 255) {
						rgb[k] = 255;
						break;
					}
					newDelta = delta(t, rgb);
					if (newDelta < minDelta) {
						minDelta = newDelta;
					} else {
						d = -d;
						dirCount++;
					}
				} while (dirCount < 2);
				rgb[k] = rc(rgb[k], d);
			}
		}
		System.out
				.println("DeviceMeasurementControl.nearestColor() Phase 2: two-axis plane directions");
		setMeasurementRepetitions(1);
		int k1 = 0, k2 = 1;
		for (int w = 0; w < 1; w++) {
			for (int k = 0; k < 3; k++) {
				if (k == 0) {
					k1 = 1;
					k2 = 2;
				}
				if (k == 1) {
					k1 = 0;
					k2 = 2;
				}
				if (k == 2) {
					k1 = 0;
					k2 = 1;
				}
				minDelta = delta(t, rgb);
				d = 1;
				dirCount = 0;
				do {
					rgb[k1] += d;
					if (rgb[k1] < 0) {
						rgb[k1] = 0;
						break;
					} else if (rgb[k1] > 255) {
						rgb[k1] = 255;
						break;
					}
					rgb[k2] -= d;
					if (rgb[k2] < 0) {
						rgb[k2] = 0;
						break;
					} else if (rgb[k2] > 255) {
						rgb[k2] = 255;
						break;
					}
					newDelta = delta(t, rgb);
					if (newDelta < minDelta) {
						minDelta = newDelta;
					} else {
						d = -d;
						dirCount++;
					}
				} while (dirCount < 2);
				rgb[k1] = rc(rgb[k1], d);
				rgb[k2] = rc(rgb[k2], -d);
			}
		}
		System.out
				.println("DeviceMeasurementControl.nearestColor() Final phase: all directions");
		setMeasurementRepetitions(3);
		int[] rgb2;
		do {
			rgb2 = getMinDelta(t, rgb);
			if (rgb2 != null)
				for (int j = 0; j < 3; j++)
					rgb[j] = rgb2[j];
		} while (rgb2 != null);
		setMeasurementRepetitions(1);
		return new Color(rgb[0], rgb[1], rgb[2]);
	}

	private int[] getMinDelta(PxlColor t, int[] rgb) {
		int[] c = new int[3];
		int[] minc = new int[3];
		double dMin = delta(t, rgb);
		boolean foundMin = false;
		for (int i = 0; i < 8; i++) {
			c[0] = rc(rgb[0], (((i & 1) != 0) ? 1 : -1));
			c[1] = rc(rgb[1], (((i & 2) != 0) ? 1 : -1));
			c[2] = rc(rgb[2], (((i & 4) != 0) ? 1 : -1));
			double d = delta(t, c);
			if (d < dMin) {
				dMin = d;
				for (int j = 0; j < 3; j++)
					minc[j] = c[j];
				foundMin = true;
			}
		}
		System.out.println("getMinDelta() " + t.toString() + "  "
				+ StringExt.valueOf(foundMin ? minc : rgb) + "  "
				+ dn.format(dMin));
		return foundMin ? minc : null;
	}

	private double delta(PxlColor t, int[] rgb) {
		PxlColor m = getMeasurementValue(new Color(rgb[0], rgb[1], rgb[2]));
		double dX = t.getX() - m.getX();
		double dY = t.getY() - m.getY();
		double dZ = t.getZ() - m.getZ();
		double d = dX * dX + dY * dY + dZ * dZ;
		System.out.println("delta() " + t.toString() + "  " + m.toString()
				+ "  " + StringExt.valueOf(rgb) + "  " + dn.format(d));
		return d;
	}

	private PxlColor getMeasurementValue(Color c) {
		target.showCalibrationTarget(c);
		waitMS(smallMeasurementDelay);
		return new YxyColor(getMeasurement());
	}

	private int rc(int b, int d) {
		b += d;
		if (b < 0) {
			b = 0;
		} else if (b > 255) {
			b = 255;
		}
		return b;
	}

	private double[] dataArray(PxlColor t, PxlColor m, Color c) {
		double tYxy[] = t.getYxyComponents();
		double tXYZ[] = t.getComponents();
		double mYxy[] = m.getYxyComponents();
		double tLab[] = t.toLabStar();
		double mLab[] = m.toLabStar();
		double dE = CIELabStar.deltaE(tLab, mLab);
		double d[] = new double[19];
		for (int i = 0; i < 3; i++)
			d[0 + i] = tYxy[i];
		for (int i = 0; i < 3; i++)
			d[3 + i] = mYxy[i];
		for (int i = 0; i < 3; i++)
			d[6 + i] = tLab[i];
		for (int i = 0; i < 3; i++)
			d[9 + i] = mLab[i];
		d[12] = dE;
		for (int i = 0; i < 3; i++)
			d[13 + i] = tXYZ[i];
		d[16] = c.getRed();
		d[17] = c.getGreen();
		d[18] = c.getBlue();
		return d;
	}

	private void printNearestColor(PxlColor t, PxlColor m, Color c) {
		double tLab[] = t.toLabStar();
		double mLab[] = m.toLabStar();
		double dE = CIELabStar.deltaE(tLab, mLab);
		System.out.println("\n");
		System.out.println("      Target = " + t + "   "
				+ StringExt.valueOf(tLab));
		System.out.println(" Measurement = " + m + "   "
				+ StringExt.valueOf(mLab));
		System.out.println("Device value = " + c);
		System.out.println("     Delta E = " + dE);
	}

	/**
	 * Get the data for a single channel.
	 * 
	 * @param channel
	 *            the channel number.
	 * @return an array of double[] data where each single double[] contains as
	 *         its first entry the output value and as entries 1, 2, 3 the
	 *         tristimulus values measured byte the color measurement device.
	 */
	private Object[] getChannelData(int channel) {
		ArrayList data = new ArrayList(256);
		target.clearCalibrationTarget();
		int tlmt = target.getCalibrationTargetResolution() - 1;
		for (int k = tlmt; measurementActive && (k >= 0); k -= measurementStep) {
			target.showCalibrationTarget(channel, k);
			waitMS((k == tlmt) ? largeMeasurementDelay : smallMeasurementDelay);
			double[] m = getMeasurement();
			double[] d = new double[m.length + 1];
			d[0] = (double) k;
			for (int i = 0; i < m.length; i++)
				d[i + 1] = m[i];
			data.add(d);
			progressCount += measurementStep;
			progressBar.setValue(progressCount / channels.length);
		}
		return data.toArray();
	}

	/**
	 * Get the measured color data for the currently visible target.
	 * 
	 * @return an array of double values which describe the current target's
	 *         color.
	 */
	private double[] getMeasurement() {
		long tm1, tm2;
		tm1 = System.currentTimeMillis();
		double v[] = measurementDevice.getTristimulus();
		tm2 = System.currentTimeMillis();
		if (measurementRepetitions > 1) {
			for (int i = 1; i < measurementRepetitions; i++) {
				waitMS(100);
				double[] v2 = measurementDevice.getTristimulus();
				for (int j = 0; j < 3; j++)
					v[j] += v2[j];
			}
			for (int j = 0; j < 3; j++)
				v[j] = v[j] / measurementRepetitions;
		}
		// System.out.println("  " + StringExt.valueOf(v));
		// System.out.println("  Time = " + (tm2-tm1) + "ms");
		return v;
	}

	private void waitMS(int d) {
		try {
			Thread.sleep(d);
		} catch (InterruptedException iex) {
		}
	}

	public void setWait4StartKey(boolean w) {
		wait4StartKey = w;
	}

	public boolean getWait4StartKey() {
		return wait4StartKey;
	}

	public void setMeasurementStep(int s) {
		measurementStep = s;
	}

	public int getMeasurementStep() {
		return measurementStep;
	}

	public void setLargeMeasurementDelay(int d) {
		largeMeasurementDelay = d;
	}

	public int getLargeMeasurementDelay() {
		return largeMeasurementDelay;
	}

	public void setSmallMeasurementDelay(int d) {
		smallMeasurementDelay = d;
	}

	public int getSmallMeasurementDelay() {
		return smallMeasurementDelay;
	}
	private JMenu measurementMenu;
	private JMenuItem wait4StartKeyItem;
	private JMenuItem measurementStepItem;
	private JMenuItem smallDelayItem;
	private JMenuItem largeDelayItem;

	public JMenu getOptionsMenu() {
		return measurementMenu;
	}

	private void createOptions() {
		measurementMenu = new JMenu("Measurement");
		wait4StartKeyItem = new JMenuItem("Wait for Start Key");
		measurementStepItem = new JMenuItem("Step Size");
		smallDelayItem = new JMenuItem("Delay Between Steps");
		largeDelayItem = new JMenuItem("Delay Between Channels");
		OptionMenuHandler omh = new OptionMenuHandler();
		addMenuItem(measurementMenu, wait4StartKeyItem, omh);
		addMenuItem(measurementMenu, measurementStepItem, omh);
		addMenuItem(measurementMenu, smallDelayItem, omh);
		addMenuItem(measurementMenu, largeDelayItem, omh);
	}

	private void addMenuItem(JMenu m, JMenuItem i, OptionMenuHandler h) {
		i.addActionListener(h);
		m.add(i);
	}
	private class OptionMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JMenuItem mi = (JMenuItem) e.getSource();
			try {
				if (mi == wait4StartKeyItem) {
					System.out
							.println("DeviceMeasurementControl.OptionMenuHandler(): wait for start key");
				} else if (mi == measurementStepItem) {
					System.out
							.println("DeviceMeasurementControl.OptionMenuHandler(): measurement step size");
				} else if (mi == smallDelayItem) {
					System.out
							.println("DeviceMeasurementControl.OptionMenuHandler(): small delay");
				} else if (mi == largeDelayItem) {
					System.out
							.println("DeviceMeasurementControl.OptionMenuHandler(): large delay");
				}
			} catch (Exception eox) {
			}
		}
	}
}
