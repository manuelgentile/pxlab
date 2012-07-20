package de.pxlab.tools.eyeone;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;

import de.pxlab.awtx.SaveFileDialog;
import de.pxlab.awtx.MessageDialog;
import de.pxlab.gui.*;
import de.pxlab.pxl.NonFatalError;

import com.gretagmacbeth.eyeone.*;

public class EyeOnePanel extends ApplicationPanel implements EyeOneConstants,
		ActionListener, Runnable {
	// This is our action bar
	private ActionBar actionBar;
	// And this are the action bar buttons
	private Button calibrateButton;
	private Button buttonButton;
	private Button singleMeasureButton;
	private Button printButton;
	private Button saveSpectraButton;
	private Button saveTristimulusButton;
	private Button infoButton;
	private Button clearButton;
	private boolean buttonWait = false;
	private SpectralGraph graph;
	private ArrayList spectralData = new ArrayList(100);
	private ArrayList tristimulusData = new ArrayList(100);
	private int dataCount = 0;
	private int printMode = 2;
	private EyeOne i1;

	public EyeOnePanel() {
		actionBar = new ActionBar();
		// Configure the action bar.
		calibrateButton = actionBar.addButton(this, "Calibrate");
		singleMeasureButton = actionBar.addButton(this, "Measure");
		buttonButton = actionBar.addButton(this, "Button");
		printButton = actionBar.addButton(this, "Print");
		saveSpectraButton = actionBar.addButton(this, "Save Spectra");
		saveTristimulusButton = actionBar.addButton(this, "Save Tristim");
		infoButton = actionBar.addButton(this, "Info");
		clearButton = actionBar.addButton(this, "Clear");
		setActionBar(actionBar);
		graph = new SpectralGraph();
		add(graph, BorderLayout.CENTER);
		i1 = EyeOne.getInstance();
		try {
			if (!i1.isConnected()) {
				System.out
						.println("Device not connected. Please connect device before trying again");
				System.exit(3);
			}
			System.out.println("Device is connected...");
			System.out.println("SKD_Version: " + i1.getOption(I1_VERSION));
			System.out.println("Device serial number: "
					+ i1.getOption(I1_SERIAL_NUMBER));
			setDeviceOption(I1_MEASUREMENT_MODE, I1_SINGLE_REFLECTANCE);
			setDeviceOption(ILLUMINATION_KEY, ILLUMINATION_D65);
			setDeviceOption(OBSERVER_KEY, OBSERVER_TWO_DEGREE);
			setDeviceOption(COLOR_SPACE_KEY, COLOR_SPACE_CIELab);
		} catch (EyeOneException eox) {
			eox.printStackTrace();
			System.exit(3);
		}
	}

	private void setDeviceOption(String key, String value)
			throws EyeOneException {
		System.out.print("Setting Option \'" + key + "\' to ");
		i1.setOption(key, value);
		System.out.println("\'" + i1.getOption(key) + "\'");
	}

	// ---------------------------------------------------------------
	// ActionListener implementation
	// ---------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		Button b = (Button) e.getSource();
		if (b == calibrateButton) {
			try {
				System.out
						.print("EyeOnePanel.actionPerformed(): Calibrate ... ");
				i1.calibrate();
			} catch (EyeOneException eox) {
				new NonFatalError("Calibration Failed!\n" + eox.getMessage());
			}
			System.out.println("done");
		} else if (b == singleMeasureButton) {
			makeSingleMeasurement();
		} else if (b == buttonButton) {
			if (buttonWait) {
				stopButtonWait();
			} else {
				startButtonWait();
			}
		} else if (b == printButton) {
			int n = spectralData.size() - 1;
			if (n >= 0) {
				printSpectrum(System.out, (NamedSpectrum) spectralData.get(n));
			}
		} else if (b == saveSpectraButton) {
			saveSpectra();
		} else if (b == saveTristimulusButton) {
			saveTristimulus();
		} else if (b == infoButton) {
			showInfo();
		} else if (b == clearButton) {
			spectralData.clear();
			tristimulusData.clear();
			dataCount = 0;
			graph.setSpectralData(null);
			graph.repaint();
		}
	}

	private void startButtonWait() {
		buttonWait = true;
		setMeasureButtonState(false);
		buttonButton.setLabel("Off");
		new Thread(this).start();
	}

	private void stopButtonWait() {
		buttonWait = false;
		setMeasureButtonState(true);
		buttonButton.setLabel("Button");
	}

	public void run() {
		System.out.println("Button is ON");
		while (buttonWait) {
			if (i1.getOption(I1_IS_KEY_PRESSED).equals(I1_YES)) {
				makeSingleMeasurement();
				while (i1.getOption(I1_IS_KEY_PRESSED).equals(I1_YES)) {
					delay(10);
				}
			}
			delay(10);
		}
		System.out.println("Button is OFF");
	}

	private void delay(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException iex) {
		}
	}

	private void makeSingleMeasurement() {
		System.out
				.print("EyeOnePanel.actionPerformed(): Start single measurement ... ");
		try {
			i1.triggerMeasurement();
			System.out.print("done, read ...");
			float[] spectrum = i1.getSpectrum(EyeOne.SPOT_INDEX);
			i1.setOption(COLOR_SPACE_KEY, COLOR_SPACE_CIExyY);
			float[] tri_xyY = i1.getTriStimulus(EyeOne.SPOT_INDEX);
			float[] tri_Yxy = new float[3];
			tri_Yxy[0] = tri_xyY[2];
			tri_Yxy[1] = tri_xyY[0];
			tri_Yxy[2] = tri_xyY[1];
			i1.setOption(COLOR_SPACE_KEY, COLOR_SPACE_CIELab);
			float[] tri_Lab = i1.getTriStimulus(EyeOne.SPOT_INDEX);
			System.out.println("done.");
			enterData(spectrum, tri_Yxy, tri_Lab);
			graph.repaint();
			// String v = "[" + Float.toString(tri_Lab[0]) + ", " +
			// Float.toString(tri_Lab[1]) + ", "+ Float.toString(tri_Lab[2]) +
			// "]";
			String v = "[" + Float.toString(tri_Yxy[0]) + ", "
					+ Float.toString(tri_Yxy[1]) + ", "
					+ Float.toString(tri_Yxy[2]) + "]";
			actionBar.showStatus(i1.getOption(COLOR_SPACE_KEY) + "=" + v);
		} catch (EyeOneException eox) {
			new NonFatalError("Measurement Failed!\n" + eox.getMessage());
		}
	}

	private void enterData(float[] s, float[] Yxy, float[] Lab) {
		++dataCount;
		String n = "Data_" + Integer.toString(dataCount);
		spectralData.add(new NamedSpectrum(n, s));
		float[] t = new float[Yxy.length + Lab.length];
		int k = 0;
		for (int i = 0; i < Yxy.length; i++) {
			t[k++] = Yxy[i];
		}
		for (int i = 0; i < Lab.length; i++) {
			t[k++] = Lab[i];
		}
		tristimulusData.add(new NamedSpectrum(n, t));
		graph.setSpectralData(spectralData);
	}

	private void printSpectrum(PrintStream out, NamedSpectrum ns) {
		if (ns != null) {
			if (printMode == 0) {
				// Single spectrum plot data
				int w = 380;
				for (int i = 0; i < ns.data.length; i++) {
					out.println(w + " " + ns.data[i]);
					w += 10;
				}
			} else if (printMode == 1) {
				// Spectrum name and data one entry per line
				int w = 380;
				out.println("\n" + ns.name + " =");
				for (int i = 0; i < ns.data.length; i++) {
					out.println(w + " " + ns.data[i]);
					w += 10;
				}
			} else if (printMode == 2) {
				// one line per spectrum
				out.print(ns.name);
				for (int i = 0; i < ns.data.length; i++) {
					out.print(" " + ns.data[i]);
				}
				out.println("");
			}
		}
	}

	private void saveSpectra() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		SaveFileDialog fd = new SaveFileDialog("Save Spectral Data", "Spectra-"
				+ df.format(new Date()) + ".dat", "dat");
		String fn = fd.getPath();
		fd.setVisible(false);
		fd.dispose();
		if (fn != null) {
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fn,
						false), true);
				for (Iterator it = spectralData.iterator(); it.hasNext();) {
					printSpectrum(out, (NamedSpectrum) it.next());
				}
				out.close();
			} catch (FileNotFoundException fx) {
				new NonFatalError(fx.getMessage());
			}
		}
	}

	private void saveTristimulus() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		SaveFileDialog fd = new SaveFileDialog("Save Tristimulus Data",
				"Tristim-" + df.format(new Date()) + ".dat", "dat");
		String fn = fd.getPath();
		fd.setVisible(false);
		fd.dispose();
		if (fn != null) {
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fn,
						false), true);
				for (Iterator it = tristimulusData.iterator(); it.hasNext();) {
					printSpectrum(out, (NamedSpectrum) it.next());
				}
				out.close();
			} catch (FileNotFoundException fx) {
				new NonFatalError(fx.getMessage());
			}
		}
	}

	private void showInfo() {
		StringBuffer info = new StringBuffer(200);
		try {
			if (i1.isConnected()) {
				info.append("Device is connected.");
				info.append("\nSKD Version: " + i1.getOption(I1_VERSION));
				info.append("\nDevice serial number: "
						+ i1.getOption(I1_SERIAL_NUMBER));
				info.append("\nMeasurement mode: "
						+ i1.getOption(I1_MEASUREMENT_MODE));
			} else {
				info.append("Device is not connected.");
			}
			MessageDialog md = new MessageDialog(new Frame(), " Device Info",
					info.toString());
			md.setVisible(true);
			md.setVisible(false);
			md.dispose();
		} catch (EyeOneException eox) {
			eox.printStackTrace();
			System.exit(3);
		}
	}

	private void setButtonState(boolean s) {
		calibrateButton.setEnabled(s);
		printButton.setEnabled(s);
		saveSpectraButton.setEnabled(s);
		saveTristimulusButton.setEnabled(s);
		infoButton.setEnabled(s);
		clearButton.setEnabled(s);
		buttonButton.setEnabled(s);
		setMeasureButtonState(s);
	}

	private void setMeasureButtonState(boolean s) {
		singleMeasureButton.setEnabled(s);
	}
	// ---------------------------------------------------------------
	// Options menu
	// ---------------------------------------------------------------
	private Menu measurementModeMenu = new Menu("Measurement Mode");
	private Menu illuminationMenu = new Menu("Illumination");
	private Menu observerMenu = new Menu("Observer");
	private Menu colorSpaceMenu = new Menu("Color Space");
	private Menu graphDisplayMenu = new Menu("Graph Display");
	private MenuItem singleEmissionMode = new MenuItem("Single Emission");
	private MenuItem singleReflectanceMode = new MenuItem("Single Reflectance");
	private MenuItem scanningReflectanceMode = new MenuItem(
			"Scanning Reflectance");
	private MenuItem illuminationA = new MenuItem("A");
	private MenuItem illuminationB = new MenuItem("B");
	private MenuItem illuminationC = new MenuItem("C");
	private MenuItem illuminationD65 = new MenuItem("D65");
	private MenuItem observer2 = new MenuItem("2 degree");
	private MenuItem observer10 = new MenuItem("10 degree");
	private MenuItem cs_CIELab = new MenuItem("CIE Lab");
	private MenuItem cs_CIELCh = new MenuItem("CIE LCh");
	private MenuItem cs_CIExyY = new MenuItem("CIE xyY");
	private MenuItem graphDisplaySingle = new MenuItem("Single Graph");
	private MenuItem graphDisplayMultiple = new MenuItem("Multiple Graphs");

	/**
	 * This method is called by the parent frame to get the option menu entries.
	 */
	protected MenuItem[] getOptionMenuItems() {
		MenuItem[] mi = new MenuItem[5];
		OptionMenuHandler omh = new OptionMenuHandler();
		mi[0] = measurementModeMenu;
		mi[1] = illuminationMenu;
		mi[2] = observerMenu;
		mi[3] = colorSpaceMenu;
		mi[4] = graphDisplayMenu;
		addMenuItem(measurementModeMenu, singleEmissionMode, omh);
		addMenuItem(measurementModeMenu, singleReflectanceMode, omh);
		addMenuItem(measurementModeMenu, scanningReflectanceMode, omh);
		addMenuItem(illuminationMenu, illuminationA, omh);
		addMenuItem(illuminationMenu, illuminationB, omh);
		addMenuItem(illuminationMenu, illuminationC, omh);
		addMenuItem(illuminationMenu, illuminationD65, omh);
		addMenuItem(observerMenu, observer2, omh);
		addMenuItem(observerMenu, observer10, omh);
		addMenuItem(colorSpaceMenu, cs_CIELab, omh);
		addMenuItem(colorSpaceMenu, cs_CIELCh, omh);
		addMenuItem(colorSpaceMenu, cs_CIExyY, omh);
		addMenuItem(graphDisplayMenu, graphDisplaySingle, omh);
		addMenuItem(graphDisplayMenu, graphDisplayMultiple, omh);
		return (mi);
	}

	private void addMenuItem(Menu m, MenuItem i, OptionMenuHandler h) {
		i.addActionListener(h);
		m.add(i);
	}
	/** Handles file menu item selections. */
	private class OptionMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			MenuItem mi = (MenuItem) e.getSource();
			try {
				if (mi == singleEmissionMode) {
					setDeviceOption(I1_MEASUREMENT_MODE, "SingleEmission");
					graph.setShowEmission();
				} else if (mi == singleReflectanceMode) {
					setDeviceOption(I1_MEASUREMENT_MODE, "SingleReflectance");
					graph.setShowReflectance();
				} else if (mi == scanningReflectanceMode) {
					setDeviceOption(I1_MEASUREMENT_MODE, "ScanningReflectance");
					graph.setShowReflectance();
				} else if (mi == illuminationA) {
					setDeviceOption(ILLUMINATION_KEY, ILLUMINATION_A);
				} else if (mi == illuminationB) {
					setDeviceOption(ILLUMINATION_KEY, ILLUMINATION_B);
				} else if (mi == illuminationC) {
					setDeviceOption(ILLUMINATION_KEY, ILLUMINATION_C);
				} else if (mi == illuminationD65) {
					setDeviceOption(ILLUMINATION_KEY, ILLUMINATION_D65);
				} else if (mi == observer2) {
					setDeviceOption(OBSERVER_KEY, OBSERVER_TWO_DEGREE);
				} else if (mi == observer10) {
					setDeviceOption(OBSERVER_KEY, OBSERVER_TEN_DEGREE);
				} else if (mi == cs_CIELab) {
					setDeviceOption(COLOR_SPACE_KEY, COLOR_SPACE_CIELab);
				} else if (mi == cs_CIELCh) {
					setDeviceOption(COLOR_SPACE_KEY, COLOR_SPACE_CIELCh);
				} else if (mi == cs_CIExyY) {
					setDeviceOption(COLOR_SPACE_KEY, COLOR_SPACE_CIExyY);
				}
			} catch (EyeOneException eox) {
				new NonFatalError(eox.getMessage());
			}
		}
	}
}
