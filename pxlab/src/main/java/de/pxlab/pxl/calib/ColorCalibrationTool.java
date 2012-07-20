package de.pxlab.pxl.calib;

import java.io.*;
import java.util.*;
import java.util.prefs.*;
import java.text.NumberFormat;
import java.util.Locale;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import de.pxlab.awtx.LoadFileDialog;
import de.pxlab.awtx.SaveFileDialog;
import de.pxlab.awtx.MessageDialog;
import de.pxlab.util.StringExt;
import de.pxlab.pxl.gui.AbstractApplicationAction;
import de.pxlab.pxl.gui.AbstractApplicationFrame;

import de.pxlab.pxl.device.GretagMacbethEyeOne;
import de.pxlab.pxl.device.TektronixJ1810;

import de.pxlab.pxl.Debug;
import de.pxlab.pxl.FileBase;
import de.pxlab.pxl.PxlColor;
import de.pxlab.pxl.YxyColor;
import de.pxlab.pxl.DisplayDevice;
import de.pxlab.pxl.ExperimentalDisplayDevice;
import de.pxlab.pxl.PrimaryScreen;
import de.pxlab.pxl.SecondaryScreen;
import de.pxlab.pxl.NonFatalError;

import de.pxlab.pxl.PXLabIcon;
import de.pxlab.pxl.gui.AboutDialog;
import de.pxlab.pxl.gui.DisplayDeviceOptionsMenu;
import de.pxlab.pxl.CalibrationTarget;
import de.pxlab.pxl.ExPar;
import de.pxlab.pxl.ExParValue;
import de.pxlab.pxl.ExParTypeCodes;
import de.pxlab.pxl.ExParDescriptor;
import de.pxlab.pxl.gui.ParameterDialog;

/**
 * A general tool for calibrating color devices. This tool should be able to
 * provide the following services:
 * 
 * <ul>
 * <li>Measure the gamma function of various color generation devices like CRT
 * monitors, TFT monitors or illumination control systems.
 * 
 * <li>It should also be possible to do visual gamma measurement.
 * 
 * <li>In doing this it should be able to create gamma measurement target
 * stimuli for every device and every method of measurement.
 * 
 * <li>It should be possible to aquire measurement data from various measurement
 * devices like the Tektronix Lumacolor or the Gretag Macbeth EyeOne.
 * 
 * <li>It should also be possible to handle different data formats like single
 * valued data correlated with luminance, tristimulus values, or even spectral
 * data.
 * 
 * <li>Gamma data should be shown graphically in a output/luminance graph.
 * 
 * <li>Chromaticity data should be shown in a CIE 1931 xy-chromaticity chart.
 * 
 * <li>Spectral data should be shown in a spectral data chart.
 * 
 * <li>It must be possible to store the data into a data base such that they may
 * be retrieved later.
 * 
 * <li>Gamma function parameters should be estimated for various types of gamma
 * functions and should be compared by goodness of fit indizes.
 * 
 * <li>It should be possible to graphically compare empirical and estimated
 * gamma functions.
 * 
 * <li>Parameter estimates should be stored into parameter files such that they
 * can easily be imported into experimental programs for automatic calibration.
 * 
 * <li>Besides gamma calibration the program should also provide test screens
 * for testing additional color device properties like channel independence,
 * additivity, or timing properties.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
/*
 * 
 * 03/19/03
 * 
 * 2005/03/01 added progress bar
 */
public class ColorCalibrationTool extends AbstractApplicationFrame implements
		CalibrationController, ExParTypeCodes {
	/**
	 * Measure the gamma curve and estimate gamma function parameters.
	 */
	public static final int GAMMA_PARS = 1;
	/** Compute optimized output values for a given color table. */
	public static final int COLOR_TABLE = 2;
	/**
	 * Evaluate earlier measurements or the currently active color transform.
	 */
	public static final int EVALUATION = 3;
	/** Application menus */
	protected JMenu fileMenu, taskMenu, runMenu, testMenu, deviceMenu,
			optionsMenu, helpMenu;
	/** Tells us where the Calibrate menu entry is. */
	protected int calibrateMenuIndex;
	/** Toolab and menu actions. */
	protected Action newFileAction, openColorTableAction,
			saveGammaParametersAction, saveDataAction,
			saveGammaCurveDataAction, saveColorTableAction,
			saveEvaluationDataAction, startCalibrationAction,
			stopCalibrationAction, startEvaluationAction, exitAction,
			helpAction, aboutAction;
	private JProgressBar progressBar;
	public static ExPar MeasurementTask = new ExPar(SMALL_INT, new ExParValue(
			GAMMA_PARS), "Calibration Task");
	public static ExPar MeasurementDevice = new ExPar(STRING, new ExParValue(
			ColorMeasurementDevice.SIMULATION), "Color Measurement Device");
	public static ExPar TargetLocationX = new ExPar(HORSCREENPOS,
			new ExParValue(0), "Horizontal Target Location");
	public static ExPar TargetLocationY = new ExPar(VERSCREENPOS,
			new ExParValue(0), "Vertical Target Location");
	public static ExPar TargetWidth = new ExPar(HORSCREENSIZE, new ExParValue(
			300), "Target Width");
	public static ExPar TargetHeight = new ExPar(VERSCREENSIZE, new ExParValue(
			300), "Target Height");
	private GammaEstimator gammaEstimator;
	/** Holds the data of gamma curve measurements. */
	private ArrayList gammaData;
	/** Holds evaluation data. */
	private ArrayList evalData;
	/** Holds the data of a color table computation. */
	private ArrayList colorTableData;
	/**
	 * A list of PxlColor objects which are used as target or evaluation colors.
	 */
	private ArrayList targetColors = null;
	/**
	 * The name of the file where the target colors have been loaded from.
	 */
	private String targetColorsFileName = null;
	/** Gets the gamma function parameters after estimation. */
	private ArrayList gammaFunctionPars = new ArrayList(8);
	private GammaChart gammaChart;
	private YxyChart xyChart;
	private PrimariesPanel primariesPanel;
	private ParametersPanel parametersPanel;
	private Preferences preferences;
	/** Controls the measurement device. */
	private DeviceMeasurementControl deviceMeasurementControl;
	/** The actual measurement device. */
	private ColorMeasurementDevice colorMeasurementDevice;
	/**
	 * Holds the currently active task type after the task has been started.
	 * Invalid if no calibration task is currently running.
	 */
	private int activeTask;

	public ColorCalibrationTool( /* String[] args */) {
		super(" PXLab Color Calibration Tool",
				(de.pxlab.pxl.RuntimeOptionHandler) null);
		Debug.add(Debug.COLOR_DEVICE);
		Debug.add(Debug.COLOR_GAMUT);
		Debug.add(Debug.FILES);
		PXLabIcon.decorate(this);
		setCalibrationStoppedState();
		ExPar.reset();
		pack();
		setVisible(true);
	}

	// ---------------------------------------------------------------
	// Implementations of the abstract superclass methods
	// ---------------------------------------------------------------
	/**
	 * Get this application's main content panel.
	 * 
	 * @return a panel containing the application's main content.
	 */
	protected JPanel getApplicationPanel() {
		Border b;
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel progBarPanel = new JPanel(new BorderLayout());
		b = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		progBarPanel.setBorder(b);
		progressBar = new JProgressBar(0, 255);
		progressBar.setValue(0);
		progBarPanel.add(progressBar);
		mainPanel.add(progBarPanel, BorderLayout.NORTH);
		JPanel gammaChartPanel = new JPanel(new BorderLayout());
		b = panelBorder(" Gamma Function ");
		gammaChartPanel.setBorder(b);
		gammaChart = new GammaChart();
		gammaChartPanel.add(gammaChart);
		JPanel xyChartPanel = new JPanel(new BorderLayout());
		b = panelBorder(" CIE xy-Chromaticity Chart ");
		xyChartPanel.setBorder(b);
		xyChart = new YxyChart();
		xyChartPanel.add(xyChart);
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				gammaChartPanel, xyChartPanel);
		pane.setResizeWeight(0.6);
		pane.setDividerLocation(0.6);
		pane.setDividerSize(8);
		pane.setOneTouchExpandable(true);
		mainPanel.add(pane);
		JPanel primPanel = new JPanel(new BorderLayout());
		b = panelBorder(" Primaries ");
		primPanel.setBorder(b);
		primariesPanel = new PrimariesPanel();
		primPanel.add(primariesPanel);
		JPanel parsPanel = new JPanel(new BorderLayout());
		b = panelBorder(" Gamma Parameters ");
		parsPanel.setBorder(b);
		parametersPanel = new ParametersPanel();
		parsPanel.add(parametersPanel);
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		bottomPanel.add(primPanel);
		bottomPanel.add(parsPanel);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		return mainPanel;
	}

	private Border panelBorder(String title) {
		Border a = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		Border b = BorderFactory.createTitledBorder(a, title /*
															 * ,
															 * TitledBorder.LEFT
															 * ,
															 * TitledBorder.TOP
															 */);
		return b;
	}

	/**
	 * Create the tool bar and menu action entries. This method is separated
	 * because it might be necessary to create the action entries before the
	 * menu and tool bar can be populated.
	 */
	protected void createActions() {
		loadPreferences();
		deviceMeasurementControl = new DeviceMeasurementControl(this);
		gammaEstimator = new GammaEstimator();
		newFileAction = new NewFileAction();
		openColorTableAction = new OpenColorTableAction();
		saveGammaParametersAction = new SaveGammaParametersAction();
		saveGammaCurveDataAction = new SaveGammaCurveDataAction();
		saveColorTableAction = new SaveColorTableAction();
		saveEvaluationDataAction = new SaveEvaluationDataAction();
		saveDataAction = new SaveDataAction();
		startCalibrationAction = new StartCalibrationAction();
		stopCalibrationAction = new StopCalibrationAction();
		startEvaluationAction = new StartEvaluationAction();
		exitAction = new ExitAction();
		helpAction = new HelpAction();
		aboutAction = new AboutAction();
	}

	private void loadPreferences() {
		preferences = Preferences
				.userNodeForPackage(ColorCalibrationTool.class);
		MeasurementTask.set(preferences.get("MeasurementTask",
				String.valueOf(GAMMA_PARS)));
		MeasurementDevice.set(preferences.get("MeasurementDevice",
				ColorMeasurementDevice.SIMULATION));
	}

	/**
	 * Get this application's file menu.
	 * 
	 * @return the file menu populated by the application.
	 */
	protected JMenu getFileMenu() {
		JMenuItem menuItem = null;
		fileMenu = new JMenu("File");
		Action[] fileActions = { newFileAction, openColorTableAction,
				saveGammaParametersAction, saveGammaCurveDataAction,
				saveColorTableAction, saveEvaluationDataAction };
		for (int i = 0; i < fileActions.length; i++) {
			menuItem = new JMenuItem(fileActions[i]);
			menuItem.setIcon(null);
			fileMenu.add(menuItem);
		}
		fileMenu.addSeparator();
		menuItem = new JMenuItem(exitAction);
		menuItem.setIcon(null);
		fileMenu.add(menuItem);
		return fileMenu;
	}

	/**
	 * Get this application's special menus.
	 * 
	 * @return an array of menus which constitute the application specific
	 *         menus.
	 */
	protected JMenu[] getApplicationMenus() {
		JMenuItem menuItem = null;
		taskMenu = new TaskSelectionMenu();
		runMenu = new JMenu("Calibrate");
		Action[] expactions = { startCalibrationAction, stopCalibrationAction };
		for (int i = 0; i < expactions.length; i++) {
			menuItem = new JMenuItem(expactions[i]);
			menuItem.setIcon(null);
			runMenu.add(menuItem);
		}
		testMenu = new JMenu("Test");
		Action[] tstactions = { startEvaluationAction };
		for (int i = 0; i < tstactions.length; i++) {
			menuItem = new JMenuItem(tstactions[i]);
			menuItem.setIcon(null);
			testMenu.add(menuItem);
		}
		deviceMenu = new MeasurementDeviceSelectionMenu();
		JMenu[] am = { taskMenu, runMenu, testMenu, deviceMenu };
		return am;
	}

	/**
	 * Get this application's help menus.
	 * 
	 * @return a menu containing the application's help submenus.
	 */
	protected JMenu getHelpMenu() {
		JMenuItem menuItem = null;
		helpMenu = new JMenu("Help");
		Action[] helpactions = { helpAction, aboutAction };
		for (int i = 0; i < helpactions.length; i++) {
			menuItem = new JMenuItem(helpactions[i]);
			menuItem.setIcon(null);
			helpMenu.add(menuItem);
		}
		return helpMenu;
	}

	/**
	 * Get this application's options menus.
	 * 
	 * @return a menu containing the application's options submenus.
	 */
	protected JMenu getOptionsMenu() {
		optionsMenu = new JMenu("Options");
		DisplayDeviceOptionsMenu dd = new DisplayDeviceOptionsMenu();
		dd.setDualScreenOptionsEnabled(false);
		optionsMenu.add(dd);
		optionsMenu.add(new TargetOptionsMenuItem());
		optionsMenu.add((JMenu) deviceMeasurementControl.getOptionsMenu());
		/*
		 * JMenuItem[] om = getOptionMenuItems(); for (int i = 0; i < om.length;
		 * i++) { optionsMenu.add(om[i]); }
		 */
		return optionsMenu;
	}
	/*
	 * public void showDeviceInfo() { MessageDialog md = new MessageDialog(new
	 * Frame(), " Device Info", getInfo()); md.setVisible(true);
	 * md.setVisible(false); md.dispose(); }
	 */
	class TaskSelectionMenu extends JMenu implements ItemListener {
		protected JRadioButtonMenuItem gammaParsItem, colorTableItem,
				evaluationItem;
		protected ButtonGroup taskButtonGroup;

		public TaskSelectionMenu() {
			super("Task");
			taskButtonGroup = new ButtonGroup();
			gammaParsItem = new JRadioButtonMenuItem(
					"Estimate Gamma Parameters",
					MeasurementTask.getInt() == GAMMA_PARS);
			gammaParsItem.addItemListener(this);
			taskButtonGroup.add(gammaParsItem);
			colorTableItem = new JRadioButtonMenuItem("Create Color Table",
					MeasurementTask.getInt() == COLOR_TABLE);
			colorTableItem.addItemListener(this);
			taskButtonGroup.add(colorTableItem);
			evaluationItem = new JRadioButtonMenuItem("Evaluate Calibration",
					MeasurementTask.getInt() == EVALUATION);
			evaluationItem.addItemListener(this);
			taskButtonGroup.add(evaluationItem);
			add(gammaParsItem);
			add(colorTableItem);
			add(evaluationItem);
		}

		public void itemStateChanged(ItemEvent e) {
			JMenuItem source = (JMenuItem) (e.getSource());
			// System.out.println(source.getText());
			if (source == gammaParsItem) {
				MeasurementTask.set(GAMMA_PARS);
			} else if (source == colorTableItem) {
				MeasurementTask.set(COLOR_TABLE);
			} else if (source == evaluationItem) {
				MeasurementTask.set(EVALUATION);
			}
			preferences.put("MeasurementTask", MeasurementTask.getString());
		}
	}
	class MeasurementDeviceSelectionMenu extends JMenu implements ItemListener {
		protected JRadioButtonMenuItem eyeOneItem, tektronixJ1810Item,
				simulatedDeviceItem;
		protected ButtonGroup measurementDeviceButtonGroup;

		public MeasurementDeviceSelectionMenu() {
			super("Measurement Device");
			measurementDeviceButtonGroup = new ButtonGroup();
			eyeOneItem = new JRadioButtonMenuItem(
					ColorMeasurementDevice.EYEONE, MeasurementDevice
							.getString().startsWith(
									ColorMeasurementDevice.EYEONE));
			eyeOneItem.addItemListener(this);
			measurementDeviceButtonGroup.add(eyeOneItem);
			tektronixJ1810Item = new JRadioButtonMenuItem(
					ColorMeasurementDevice.TEKTRONIXJ1810, MeasurementDevice
							.getString().startsWith(
									ColorMeasurementDevice.TEKTRONIXJ1810));
			tektronixJ1810Item.addItemListener(this);
			measurementDeviceButtonGroup.add(tektronixJ1810Item);
			simulatedDeviceItem = new JRadioButtonMenuItem(
					ColorMeasurementDevice.SIMULATION, MeasurementDevice
							.getString().startsWith(
									ColorMeasurementDevice.SIMULATION));
			simulatedDeviceItem.addItemListener(this);
			measurementDeviceButtonGroup.add(simulatedDeviceItem);
			add(eyeOneItem);
			add(tektronixJ1810Item);
			add(simulatedDeviceItem);
		}

		public void itemStateChanged(ItemEvent e) {
			JMenuItem source = (JMenuItem) (e.getSource());
			// System.out.println(source.getText());
			if (source == eyeOneItem) {
				MeasurementDevice.set(ColorMeasurementDevice.EYEONE);
			} else if (source == tektronixJ1810Item) {
				MeasurementDevice.set(ColorMeasurementDevice.TEKTRONIXJ1810);
			} else if (source == simulatedDeviceItem) {
				MeasurementDevice.set(ColorMeasurementDevice.SIMULATION);
			}
			preferences.put("MeasurementDevice", MeasurementDevice.getString());
		}
	}
	class TargetOptionsMenuItem extends JMenuItem implements ActionListener {
		public TargetOptionsMenuItem() {
			super("Target Properties");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			System.out
					.println("ColorCalibrationTool.TargetOptionsMenuItem.actionPerformed()");
			ExParDescriptor[] xpd = new ExParDescriptor[4];
			xpd[0] = new ExParDescriptor(
					"de.pxlab.pxl.calib.ColorCalibrationTool.TargetLocationX",
					TargetLocationX, ExParDescriptor.GEOMETRY_EDIT);
			xpd[1] = new ExParDescriptor(
					"de.pxlab.pxl.calib.ColorCalibrationTool.TargetLocationY",
					TargetLocationY, ExParDescriptor.GEOMETRY_EDIT);
			xpd[2] = new ExParDescriptor(
					"de.pxlab.pxl.calib.ColorCalibrationTool.TargetWidth",
					TargetWidth, ExParDescriptor.GEOMETRY_EDIT);
			xpd[3] = new ExParDescriptor(
					"de.pxlab.pxl.calib.ColorCalibrationTool.TargetHeight",
					TargetHeight, ExParDescriptor.GEOMETRY_EDIT);
			ParameterDialog pd = new ParameterDialog(ColorCalibrationTool.this);
			pd.configureFor(xpd);
			// pd.setVisible(true);
			pd.show();
			pd.dispose();
		}
	}
	// ---------------------------------------------------------------
	// Options menu
	// ---------------------------------------------------------------
	private JMenu gammaMeasurementMenu = new JMenu("Gamma Measurement");
	private JMenuItem targetPropertiesItem = new JMenuItem("Target Properties");
	private JMenuItem colorChannelItem = new JMenuItem("Color Channels");
	private JMenuItem stepSizeAndDelayItem = new JMenuItem(
			"Measurement Steps and Timing");
	private JMenuItem gammaMeasurementOptionsItem = new JMenuItem(
			"Gamma Measurement");

	/**
	 * This method is called by the parent frame to get the option menu entries.
	 */
	protected JMenuItem[] getOptionMenuItems() {
		JMenuItem[] mi = new JMenuItem[1];
		OptionMenuHandler omh = new OptionMenuHandler();
		mi[0] = gammaMeasurementMenu;
		addMenuItem(gammaMeasurementMenu, targetPropertiesItem, omh);
		addMenuItem(gammaMeasurementMenu, colorChannelItem, omh);
		addMenuItem(gammaMeasurementMenu, stepSizeAndDelayItem, omh);
		addMenuItem(gammaMeasurementMenu, gammaMeasurementOptionsItem, omh);
		return (mi);
	}

	private void addMenuItem(JMenu m, JMenuItem i, OptionMenuHandler h) {
		i.addActionListener(h);
		m.add(i);
	}
	/** Handles file menu item selections. */
	private class OptionMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JMenuItem mi = (JMenuItem) e.getSource();
			try {
				if (mi == targetPropertiesItem) {
					System.out
							.println("ColorCalibrationTool.OptionMenuHandler(): target properties");
				} else if (mi == colorChannelItem) {
					System.out
							.println("ColorCalibrationTool.OptionMenuHandler(): color channels");
				} else if (mi == stepSizeAndDelayItem) {
					System.out
							.println("ColorCalibrationTool.OptionMenuHandler(): step size and delay");
				} else if (mi == gammaMeasurementOptionsItem) {
					System.out
							.println("ColorCalibrationTool.OptionMenuHandler(): Gamma Measurement");
					// DeviceMeasurementOptions = new
					// DeviceMeasurementOptions((Frame)getParent(),
					// gammaMeasurement);
				}
			} catch (Exception eox) {
			}
		}
	}

	/**
	 * Get this application's tool bar actions.
	 * 
	 * @return an array of actions which are intended to be listed in the
	 *         application's tool bar.
	 */
	protected Action[] getToolBarActions() {
		Action[] actions = { newFileAction, openColorTableAction,
				startCalibrationAction, stopCalibrationAction,
				saveGammaParametersAction, saveDataAction,
				startEvaluationAction };
		return actions;
	}
	public class NewFileAction extends AbstractApplicationAction {
		public NewFileAction() {
			super("New File",
					"/de/pxlab/images/icons/toolbarButtonGraphics/New.png",
					"Prepare new calibration", 'N');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.NewFileAction.actionPerformed()");
			if (gammaFunctionPars != null) {
				gammaFunctionPars.clear();
				gammaChart.setGammaPars(gammaFunctionPars);
			}
			if (gammaData != null) {
				gammaData.clear();
				gammaChart.setGammaData(gammaData);
				xyChart.setColorData(gammaData);
			}
			gammaChart.repaint();
			xyChart.repaint();
		}
	}
	// -------------------------------------------------------
	// Table of Target Colors
	// -------------------------------------------------------
	public class OpenColorTableAction extends AbstractApplicationAction {
		public OpenColorTableAction() {
			super("Open Color Table",
					"/de/pxlab/images/icons/toolbarButtonGraphics/Open.png",
					"Open a color table file", 'O');
		}

		public void actionPerformed(ActionEvent e) {
			System.out
					.println("ColorCalibrationTool.OpenColorTableFileAction.actionPerformed()");
			openColorTable();
		}
	}

	/**
	 * Open a file of target colors. A target color file contains a single
	 * target color description in every line. The last three entries must
	 * contain the Yxy-coordinates for the target color.
	 */
	private void openColorTable() {
		System.out
				.println("ColorCalibrationTool.openColorTable() Opening File ...");
		LoadFileDialog fd = new LoadFileDialog(" Open Target Color Table",
				"dat");
		String fn = fd.getPath();
		if (fd.getFile() != null) {
			String[] t = FileBase.loadStrings("", fn);
			if (t != null && t.length > 0) {
				targetColors = new ArrayList(100);
				ArrayList n = new ArrayList(12);
				double[] p = new double[3];
				for (int i = 0; i < t.length; i++) {
					StringTokenizer st = new StringTokenizer(t[i], " \t,[]");
					while (st.hasMoreTokens()) {
						n.add(st.nextToken());
					}
					int m = n.size();
					if (m >= 3) {
						for (int j = 0; j < 3; j++) {
							try {
								p[j] = Double
										.valueOf((String) n.get(m - 3 + j))
										.doubleValue();
							} catch (NumberFormatException nfx) {
								p[j] = 1.0;
							}
						}
					}
					n.clear();
					targetColors.add(new YxyColor(p));
					System.out.println("ColorDeviceTransform.openColorTable() "
							+ StringExt.valueOf(p));
				}
			}
		}
		fd.dispose();
	}
	public class SaveGammaParametersAction extends AbstractApplicationAction {
		public SaveGammaParametersAction() {
			super("Save Gamma Parameters",
					"/de/pxlab/images/icons/toolbarButtonGraphics/Save.png",
					"Save estimated gamma parameters", 'A');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.SaveAsFileAction.actionPerformed()");
			saveGammaPars();
		}
	}

	public void saveGammaPars() {
		if ((gammaFunctionPars != null) && (gammaFunctionPars.size() != 0)) {
			SaveFileDialog fd = new SaveFileDialog(
					" Save Gamma Function Parameters", null, "dat");
			String fp = fd.getPath();
			try {
				PrintStream pw = new PrintStream(new FileOutputStream(fp));
				printGammaPars(fp, pw);
				pw.close();
			} catch (IOException iox) {
			}
		}
	}
	public class SaveDataAction extends AbstractApplicationAction {
		public SaveDataAction() {
			super("Save Calibration Data",
					"/de/pxlab/images/icons/toolbarButtonGraphics/SaveAs.png",
					"Save calibration data", 'S');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.SaveCalibrationDataAction.actionPerformed()");
			if (MeasurementTask.getInt() == GAMMA_PARS) {
				saveGammaData();
			} else if (MeasurementTask.getInt() == COLOR_TABLE) {
				saveColorTable();
			} else if (MeasurementTask.getInt() == EVALUATION) {
				saveEvaluationData();
			}
		}
	}
	public class SaveGammaCurveDataAction extends AbstractApplicationAction {
		public SaveGammaCurveDataAction() {
			super("Save Gamma Curve Data",
					"/de/pxlab/images/icons/toolbarButtonGraphics/SaveAs.png",
					"Save measured gamma curve data", 'S');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.SaveCalibrationDataAction.actionPerformed()");
			saveGammaData();
		}
	}

	public void saveGammaData() {
		if (gammaData != null) {
			SaveFileDialog fd = new SaveFileDialog(" Save Measurement Data",
					null, "dat");
			String fp = fd.getPath();
			try {
				PrintStream pw = new PrintStream(new FileOutputStream(fp));
				printGammaData(pw);
				pw.close();
			} catch (IOException iox) {
			}
		}
	}
	public class SaveColorTableAction extends AbstractApplicationAction {
		public SaveColorTableAction() {
			super("Save Color Table Data",
					"/de/pxlab/images/icons/toolbarButtonGraphics/SaveAs.png",
					"Save computed color table", 'S');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.SaveCalibrationDataAction.actionPerformed()");
			saveColorTable();
		}
	}

	public void saveColorTable() {
		if (colorTableData != null) {
			SaveFileDialog fd = new SaveFileDialog(" Save Color Table Data",
					null, "dat");
			String fp = fd.getPath();
			try {
				PrintStream pw = new PrintStream(new FileOutputStream(fp));
				printColorTable(pw);
				pw.close();
			} catch (IOException iox) {
			}
		}
	}
	public class SaveEvaluationDataAction extends AbstractApplicationAction {
		public SaveEvaluationDataAction() {
			super("Save Evaluation Data",
					"/de/pxlab/images/icons/toolbarButtonGraphics/SaveAs.png",
					"Save measured evaluation data", 'S');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.SaveCalibrationDataAction.actionPerformed()");
			saveEvaluationData();
		}
	}

	public void saveEvaluationData() {
		if (evalData != null) {
			SaveFileDialog fd = new SaveFileDialog(" Save Evaluation Data",
					null, "dat");
			String fp = fd.getPath();
			try {
				PrintStream pw = new PrintStream(new FileOutputStream(fp));
				printEvaluationData(pw);
				pw.close();
			} catch (IOException iox) {
			}
		}
	}

	public void printGammaPars(String fp, PrintStream out) {
		Date now = new Date();
		String screen = screenPrefix();
		out.println("\t// File: " + fp);
		out.println("\t// Date: " + now.toString());
		// out.println("\tColorDevice = de.pxlab.pxl.ColorDeviceTransformCodes.PARAMETRIC;");
		out.println("\t" + screen + "ColorDeviceDACRange = 255;");
		String[] c = { "Red", "Green", "Blue" };
		// print gamma parameters
		int n = gammaFunctionPars.size();
		for (int i = 0; i < n; i++) {
			double[] pars = (double[]) (gammaFunctionPars.get(i));
			out.println("\t" + screen + c[i % 3] + "Gamma = [" + pars[0] + ", "
					+ pars[1] + "];");
		}
		// print coordinates of color primaries
		n = gammaData.size();
		for (int i = 0; i < n; i++) {
			Object[] data = (Object[]) (gammaData.get(i));
			double[] val = (double[]) (data[0]);
			out.println("\t" + screen + c[i % 3] + "Primary = [" + val[1]
					+ ", " + val[2] + ", " + val[3] + "];");
		}
	}

	private String screenPrefix() {
		int mode = de.pxlab.pxl.Base.getDisplayDeviceType();
		return ((mode == DisplayDevice.FRAMED_WINDOW)
				|| (mode == DisplayDevice.UNFRAMED_WINDOW)
				|| (mode == DisplayDevice.FULL_SCREEN) || (mode == DisplayDevice.FULL_SCREEN_EXCLUSIVE)) ? "PrimaryScreen."
				: "SecondaryScreen.";
	}
	private static NumberFormat dn = NumberFormat.getInstance(Locale.US);
	static {
		dn.setMaximumFractionDigits(4);
		dn.setGroupingUsed(false);
	}

	public void printGammaData(PrintStream out) {
		int n = gammaData.size();
		for (int i = 0; i < n; i++) {
			Object[] data = (Object[]) (gammaData.get(i));
			for (int j = 0; j < data.length; j++) {
				double[] val = (double[]) (data[j]);
				for (int k = 0; k < val.length; k++) {
					out.print(" " + dn.format(val[k]));
				}
				out.println("");
			}
		}
	}

	public void printColorTable(PrintStream out) {
		int n = colorTableData.size();
		for (int i = 0; i < n; i++) {
			double[] d = (double[]) (colorTableData.get(i));
			for (int j = 0; j < d.length; j++) {
				out.print(" " + dn.format(d[j]));
			}
			out.println("");
		}
	}

	public void printEvaluationData(PrintStream out) {
		int n = evalData.size();
		for (int i = 0; i < n; i++) {
			double[] d = (double[]) (evalData.get(i));
			for (int j = 0; j < d.length; j++) {
				out.print(" " + dn.format(d[j]));
			}
			out.println("");
		}
	}

	/**
	 * This will publish the current gamma parameter estimates such that future
	 * device color conversions will use the gamma parameters.
	 */
	public void publishGammaPars() {
		String screen = screenPrefix();
		String parName;
		double[] pars;
		Object[] data;
		// gamma parameters
		if (gammaFunctionPars != null) {
			if (gammaFunctionPars.size() == 3) {
				if (screen.startsWith("Primary")) {
					PrimaryScreen.RedGamma.set((double[]) (gammaFunctionPars
							.get(0)));
					PrimaryScreen.GreenGamma.set((double[]) (gammaFunctionPars
							.get(1)));
					PrimaryScreen.BlueGamma.set((double[]) (gammaFunctionPars
							.get(2)));
				} else {
					SecondaryScreen.RedGamma.set((double[]) (gammaFunctionPars
							.get(0)));
					SecondaryScreen.GreenGamma
							.set((double[]) (gammaFunctionPars.get(1)));
					SecondaryScreen.BlueGamma.set((double[]) (gammaFunctionPars
							.get(2)));
				}
			}
		}
		// primaries
		if (gammaData != null) {
			if (gammaData.size() == 3) {
				Object[] rdata = (Object[]) (gammaData.get(0));
				double[] rval = (double[]) (rdata[0]);
				Object[] gdata = (Object[]) (gammaData.get(1));
				double[] gval = (double[]) (gdata[0]);
				Object[] bdata = (Object[]) (gammaData.get(2));
				double[] bval = (double[]) (bdata[0]);
				if (screen.startsWith("Primary")) {
					PrimaryScreen.RedPrimary.set(new YxyColor(rval[1], rval[2],
							rval[3]));
					PrimaryScreen.GreenPrimary.set(new YxyColor(gval[1],
							gval[2], gval[3]));
					PrimaryScreen.BluePrimary.set(new YxyColor(bval[1],
							bval[2], bval[3]));
				} else {
					SecondaryScreen.RedPrimary.set(new YxyColor(rval[1],
							rval[2], rval[3]));
					SecondaryScreen.GreenPrimary.set(new YxyColor(gval[1],
							gval[2], gval[3]));
					SecondaryScreen.BluePrimary.set(new YxyColor(bval[1],
							bval[2], bval[3]));
				}
			}
		}
	}
	// --------------------------------------------------------------------
	// Start Calibration Action
	// --------------------------------------------------------------------
	public class StartCalibrationAction extends AbstractApplicationAction {
		public StartCalibrationAction() {
			super("Start Calibration",
					"/de/pxlab/images/icons/toolbarButtonGraphics/Play.png",
					"Start calibration cycle", 'E');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.StartCalibrationAction.actionPerformed()");
			startCalibration(MeasurementTask.getInt());
		}
	}

	public void startCalibration(int task) {
		MouseListener mouseListener = new MouseHandler();
		MouseMotionListener mouseMotionListener = null;
		MouseWheelListener mouseWheelListener = null;
		CalibrationTarget target = new ExperimentalDisplayDevice(this,
				mouseListener, mouseMotionListener, mouseWheelListener);
		target.setCalibrationTargetSize(new Dimension(TargetWidth.getInt(),
				TargetHeight.getInt()));
		target.setCalibrationTargetPosition(new Point(TargetLocationX.getInt(),
				TargetLocationY.getInt()));
		progressBar.setValue(0);
		if (colorMeasurementDevice == null)
			colorMeasurementDevice = getMeasurementDevice();
		if (task == GAMMA_PARS) {
			deviceMeasurementControl.startMeasurement(target,
					target.getPrimaryCalibrationCodes(),
					colorMeasurementDevice, progressBar);
		} else if (task == COLOR_TABLE) {
			if (targetColors == null) {
				new NonFatalError("No target color table loaded!");
			} else {
				deviceMeasurementControl.startColorTable(target,
						colorMeasurementDevice, targetColors, progressBar);
			}
		} else if (task == EVALUATION) {
			publishGammaPars();
			deviceMeasurementControl.startEvaluation(target,
					colorMeasurementDevice, targetColors, progressBar);
		}
		activeTask = task;
	}

	private ColorMeasurementDevice getMeasurementDevice() {
		ColorMeasurementDevice d = null;
		String measurementDevice = MeasurementDevice.getString();
		if (measurementDevice.startsWith(ColorMeasurementDevice.EYEONE)) {
			d = new GretagMacbethEyeOne();
		} else if (measurementDevice
				.startsWith(ColorMeasurementDevice.TEKTRONIXJ1810)) {
			d = new TektronixJ1810();
		} else {
			d = new SimulatedColorMeasurementDevice();
		}
		System.out.println("ColorCalibrationTool.getMeasurementDevice(): "
				+ measurementDevice);
		return d;
	}
	// --------------------------------------------------------------------
	// Stop Calibration Action
	// --------------------------------------------------------------------
	public class StopCalibrationAction extends AbstractApplicationAction {
		public StopCalibrationAction() {
			super("Stop Calibration",
					"/de/pxlab/images/icons/toolbarButtonGraphics/Stop.png",
					"Stop the currently running calibration", 'P');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.StopCalibrationAction.actionPerformed()");
			deviceMeasurementControl.stopMeasurement();
		}
	}
	// --------------------------------------------------------------------
	// Evaluation Action
	// --------------------------------------------------------------------
	public class StartEvaluationAction extends AbstractApplicationAction {
		public StartEvaluationAction() {
			super("Start Evaluation",
					"/de/pxlab/images/icons/toolbarButtonGraphics/Reload.png",
					"Start evaluation of current calibration", 'T');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.TestCalibrationAction.actionPerformed()");
			startCalibration(EVALUATION);
		}
	}
	// --------------------------------------------------------------------
	// Exit Action
	// --------------------------------------------------------------------
	public class ExitAction extends AbstractApplicationAction {
		public ExitAction() {
			super("Exit", "/toolbarButtonGraphics/general/Stop24.gif", "Exit",
					'X');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.ExitAction.actionPerformed()");
			if (mayExit())
				System.exit(0);
		}
	}

	public boolean mayExit() {
		try {
			preferences.flush();
		} catch (BackingStoreException bsx) {
		}
		return true;
	}
	// --------------------------------------------------------------------
	// Help Action
	// --------------------------------------------------------------------
	public class HelpAction extends AbstractApplicationAction {
		public HelpAction() {
			super("Help", "/toolbarButtonGraphics/general/Help24.gif", "Help",
					'H');
		}

		public void actionPerformed(ActionEvent e) {
			Dialog m = new MessageDialog(ColorCalibrationTool.this, "Help",
					"Please look into the PXLab Tutorial at http://www.pxlab.de to get help.");
			m.show();
			// System.out.println("ColorCalibrationTool.(): Help action");
		}
	}
	// --------------------------------------------------------------------
	// About Action
	// --------------------------------------------------------------------
	public class AboutAction extends AbstractApplicationAction {
		public AboutAction() {
			super("About PXLab", "/toolbarButtonGraphics/general/About24.gif",
					"About PXLab", 'B');
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("ColorCalibrationTool.(): About action");
			new AboutDialog();
		}
	}

	// -------------------------------------------------------
	// Gamma Function Estimation
	// -------------------------------------------------------
	private void estimateGammaFunctionPars() {
		int n = gammaData.size();
		for (int i = 0; i < n; i++) {
			Object[] d = (Object[]) (gammaData.get(i));
			double[][] data = new double[d.length][];
			for (int j = 0; j < d.length; j++) {
				data[j] = (double[]) (d[j]);
			}
			double[] gpars = new double[2];
			gpars[0] = 2.2;
			gpars[1] = 1.0;
			gammaEstimator.estimate(data, gpars);
			gammaFunctionPars.add(gpars);
		}
		gammaChart.setGammaPars(gammaFunctionPars);
		parametersPanel.setGammaPars(gammaFunctionPars);
		gammaChart.repaint();
	}

	/* ------------------------------------------------------- */
	/* Menu and ToolBar (de)activation */
	/* ------------------------------------------------------- */
	/** Set the toolbar actions to the calibration running state. */
	protected void setCalibrationRunningState() {
		setMenuBarEnabled(false);
		newFileAction.setEnabled(false);
		openColorTableAction.setEnabled(false);
		startCalibrationAction.setEnabled(false);
		stopCalibrationAction.setEnabled(true);
		saveGammaParametersAction.setEnabled(false);
		saveDataAction.setEnabled(false);
		startEvaluationAction.setEnabled(false);
		/*
		 * exitAction.setEnabled(false); helpAction.setEnabled(false);
		 * aboutAction.setEnabled(false);
		 */
		progressBar.setEnabled(true);
	}

	private void setMenuBarEnabled(boolean s) {
		JMenuBar mb = getJMenuBar();
		int n = mb.getMenuCount();
		for (int i = 0; i < n; i++) {
			if (i != calibrateMenuIndex) {
				(mb.getMenu(i)).setEnabled(s);
			}
		}
	}

	/** Set the toolbar actions to the calibration running state. */
	protected void setCalibrationStoppedState() {
		setMenuBarEnabled(true);
		newFileAction.setEnabled(true);
		openColorTableAction.setEnabled(true);
		startCalibrationAction.setEnabled(true);
		stopCalibrationAction.setEnabled(false);
		saveGammaParametersAction.setEnabled(true);
		saveDataAction.setEnabled(true);
		startEvaluationAction.setEnabled(true);
		/*
		 * exitAction.setEnabled(true); helpAction.setEnabled(true);
		 * aboutAction.setEnabled(true);
		 */
		progressBar.setValue(0);
		progressBar.setEnabled(false);
	}

	// ---------------------------------------------------------------
	// CalibrationController implementation
	// ---------------------------------------------------------------
	/** Returns the controlling Frame. */
	public Frame getFrame() {
		return this;
	}

	/**
	 * Tells the controller that gamma measurement for the given channel has
	 * been started.
	 */
	public void measurementStarted(int channel) {
		System.out
				.println("ColorCalibrationTool.measurementStarted(): Channel "
						+ channel + " started. ");
		setCalibrationRunningState();
	}

	/**
	 * Tells the controller that gamma measurement for the given channel has
	 * been finished.
	 */
	public void measurementFinished(int channel, Object[] data) {
		System.out
				.println("ColorCalibrationTool.measurementFinished(): Channel "
						+ channel + " finished. ");
	}

	/**
	 * Tells the controller that gamma measurement has been finished completely
	 * for all channels requested.
	 */
	public void measurementFinished(ArrayList data) {
		System.out.println("ColorCalibrationTool.measurementFinished()");
		if (activeTask == GAMMA_PARS) {
			gammaData = data;
			gammaChart.setGammaData(gammaData);
			xyChart.setColorData(gammaData);
			primariesPanel.setGammaData(gammaData);
			estimateGammaFunctionPars();
			gammaChart.repaint();
			xyChart.repaint();
		} else if (activeTask == COLOR_TABLE) {
			colorTableData = data;
			for (Iterator it = colorTableData.iterator(); it.hasNext();) {
				System.out.println(StringExt.valueOf((double[]) it.next()));
			}
		} else if (activeTask == EVALUATION) {
			evalData = data;
			for (Iterator it = evalData.iterator(); it.hasNext();) {
				System.out.println(StringExt.valueOf((double[]) it.next()));
			}
		}
		activeTask = 0;
		setCalibrationStoppedState();
	}

	/** Tells the controller that gamma measurement has been stopped. */
	public void measurementStopped() {
		System.out.println("ColorCalibrationTool.measurementStopped()");
		activeTask = 0;
		setCalibrationStoppedState();
	}
	// ---------------------------------------------------------------
	// End of CalibrationController implementation
	// ---------------------------------------------------------------
	class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (deviceMeasurementControl != null) {
				if (deviceMeasurementControl.isWaiting4StartKey()) {
					deviceMeasurementControl.startMeasurementProcess();
				}
			}
		}
	}

	// ---------------------------------------------------------------
	// Main entry point
	// ---------------------------------------------------------------
	public static void main(String[] args) {
		// Set the current system's look an feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
					.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		// Make sure that the GUI is created in the event queue
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ColorCalibrationTool();
			}
		});
	}
}
