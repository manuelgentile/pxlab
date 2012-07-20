package de.pxlab.pxl.display.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import java.io.*;

import java.util.ArrayList;

import de.pxlab.awtx.*;
import de.pxlab.gui.*;
import de.pxlab.pxl.*;
import de.pxlab.pxl.gui.*;
import de.pxlab.pxl.spectra.*;
import de.pxlab.pxl.display.VisualGammaFields;

/**
 * This is an editor for single Display objects. The editor gets the Display
 * object to edit from an outside source by its method setDisplay(). The editor
 * can edit all display properties by its three main control panels: color,
 * geometry, and timing. The editor knows its parent frame which must implement
 * the DisplayEditorPanelController interface.
 * 
 * @author Hans Irtel
 * @version 0.4.8
 */
/*
 * To do: animated/run displays should return to stepping mode by default
 * 
 * 
 * 10/04/01 two modes: experimental display editor (expMode==true) or vision
 * demos
 * 
 * 10/08/01 Give the presentationPanel object the keyboard focus whenever going
 * into full screen mode. Thus we can use keyboard input to return.
 * 
 * 06/02/02 Fixed bug which ignored the directory path when writing an image to
 * a GIF file.
 * 
 * 07/11/02 Added method getDisplay()
 * 
 * 2004/08/19 added key handling for timing groups in full screen mode
 */
public class DisplayEditorPanel extends PXLabApplicationPanel implements
		ActionListener, ComponentListener, ColorChangeListener,
		/* DisplayPanelController, /* ReturnFromFullScreen, */ClipboardOwner {
	/**
	 * If true then we are an editor for experimental display objects. If false
	 * then we control visual demonstration displays. This means that we can't
	 * run displays in real time mode.
	 */
	private boolean expMode = true;
	/** The ActionBar run button. */
	private Button runButton;
	/** The ActionBar animate button. */
	private Button animationButton;
	/** The ActionBar full screen button. */
	// private Button fullScreenButton;
	/** Contains the timing group selection buttons. */
	private Panel stepButtonPanel;
	/** Select the color editor panel. */
	private Button colorPropertyButton;
	/** Select the geometry editor panel. */
	private Button geometryPropertyButton;
	/** Select the timing editor panel. */
	private Button timingPropertyButton;
	/** Animation button state: no animation is running. */
	private final int INACTIVE = 0;
	/** Animation button state: animation is running. */
	private final int RUNNING = 1;
	/** This is the controller object which controls this editor. */
	private DisplayEditorPanelController controller;
	/**
	 * Color control needs a ColorServer object to handle color adjustments.
	 * This server is defined here since both geometry and color controls may
	 * affect colors.
	 */
	private ColorParServer colorParServer;
	/** The currently active Display object of this control. */
	private Display currentDisplay;
	/**
	 * This panel is the currently active drawing area for all displays. It may
	 * be attached to a subregion of the control panel or to a full screen
	 * window.
	 */
	private EditablePresentationPanel presentationPanel;
	/**
	 * The fullScreenDisplayPanelContainer is a borderless full screen window
	 * which holds the display panel for the full screen display mode.
	 */
	// - private FullScreenDisplayPanelContainer
	// fullScreenDisplayPanelContainer;
	private SpectralDistributionPanel spectralDistributionPanel;
	/**
	 * This is a card panel which holds the property control panels in a card
	 * layout.
	 */
	private PropertyPanel propertyPanel;
	private ExtendedPopupMenu popupMenu;
	private MenuItem showIlluminant;
	private MenuItem showReflectance;
	private MenuItem showEmittance;
	private MenuItem describeDisplay;
	private MenuItem showDisplayParList;
	private MenuItem dumpDisplay;
	private MenuItem copyToSystemClipboard;
	private MenuItem pickImageColor;
	/**
	 * This frame holds display descriptions. It has to be updated whenever it
	 * is visible and a new display is selected.
	 */
	private DescriptionFrame descriptionFrame;
	/**
	 * This frame holds display parameter descriptions. It has to be updated
	 * whenever it is visible and a new display is selected.
	 */
	private DisplayParameterFrame displayParameterFrame;

	/**
	 * Instantiate an editor panel which can handle a single display and allows
	 * modifications of its properties.
	 * 
	 * @param ctrl
	 *            a reference to the object which controls this panel.
	 */
	public DisplayEditorPanel(DisplayEditorPanelController ctrl) {
		this(ctrl, true);
	}

	/**
	 * Instantiate a control panel which can handle a single display and allows
	 * modifications of its properties.
	 * 
	 * @param ctrl
	 *            a reference to the object which controls this panel.
	 * @param xmd
	 *            true for experimental display editing and false for visual
	 *            demos.
	 */
	public DisplayEditorPanel(DisplayEditorPanelController ctrl, boolean xmd) {
		controller = ctrl;
		expMode = xmd;
		title = expMode ? "Display Editor" : "Vision Demonstrations";
		shortTitle = "Dsp";
		// Create this control panel's action bar
		ActionBar ab = new ActionBar();
		if (expMode) {
			runButton = ab.addButton(new RunButtonHandler(), "Run");
		}
		animationButton = ab.addButton(new AnimationButtonHandler(), "Animate");
		// fullScreenButton = ab.addButton(new FullScreenButtonHandler(),
		// "Full");
		stepButtonPanel = new Panel(new GridLayout(1, 0, 0, 0));
		ab.addPanel(stepButtonPanel);
		disableAnimationButton();
		// One of the properties are colors and these are managed by a
		// color server object
		colorParServer = new ColorParServer();
		// Create a display panel and fix it to the CENTER field
		presentationPanel = new EditablePresentationPanel(
				controller.getFrame(), colorParServer /* , this */);
		add(presentationPanel, BorderLayout.CENTER);
		// - fullScreenDisplayPanelContainer = new
		// FullScreenDisplayPanelContainer(this);
		// Create a spectral distribution panel for displays with
		// spectral colors
		spectralDistributionPanel = new SpectralDistributionPanel(
				colorParServer);
		// And fit them together into a card stack in this panel's
		// WEST field
		propertyPanel = new PropertyPanel(presentationPanel, colorParServer,
				expMode);
		add(propertyPanel, BorderLayout.WEST);
		descriptionFrame = new DescriptionFrame();
		displayParameterFrame = new DisplayParameterFrame();
		createPopupMenu();
		add(popupMenu);
		// editableDisplayPanel.setPopupMenu(popupMenu);
		presentationPanel.setPopupMenu(popupMenu);
		colorPropertyButton = ab.addButton(propertyPanel,
				propertyPanel.getColorPropertyLabel());
		geometryPropertyButton = ab.addButton(propertyPanel,
				propertyPanel.getGeometryPropertyLabel());
		timingPropertyButton = ab.addButton(propertyPanel,
				propertyPanel.getTimingPropertyLabel());
		setActionBar(ab);
		// Add listener to watch this panel's show/hide behavior
		addComponentListener(this);
		// Add this panel to the color server's list of color change
		// listeners since we have to redraw the display whenver its
		// color has been changed.
		colorParServer.addColorChangeListener(this);
	}

	/**
	 * Tell the editor panel about the list of display objects available. This
	 * makes it possible to handle some displays in a special way before they
	 * are shown. Calling this method is not necessary for the editor to work
	 * properly.
	 */
	public void setBigList(DisplayCollection dspList) {
		int n = dspList.size();
		Display d;
		// Find the visual gamma displays and propagate them to the
		// color control panel inorder to enable the gamma computation
		// option.
		VisualGammaFields r = null, g = null, b = null;
		for (int i = 0; i < n; i++) {
			d = (Display) dspList.get(i);
			if (d instanceof de.pxlab.pxl.display.VisualGammaRed) {
				r = (VisualGammaFields) d;
			} else if (d instanceof de.pxlab.pxl.display.VisualGammaGreen) {
				g = (VisualGammaFields) d;
			} else if (d instanceof de.pxlab.pxl.display.VisualGammaBlue) {
				b = (VisualGammaFields) d;
			}
		}
		if ((r != null) && (g != null) && (b != null)) {
			propertyPanel.getColorControlPanel()
					.setVisualGammaDisplays(r, g, b);
		}
	}

	public MenuItem[] getFileMenuItems() {
		return (propertyPanel.getFileMenuItems());
	}

	public Display getDisplay() {
		// System.out.println("DisplayEditorPanel.getDisplay(): current display is "
		// + currentDisplay);
		return (currentDisplay);
	}

	/**
	 * Get the color control panel. This is called by a FileMenu object in order
	 * to write the color board content to a file.
	 */
	private ColorControl getColorControlPanel() {
		return (propertyPanel.getColorControlPanel());
	}

	// ---------------------------------------------------------------
	// DisplayPanelController implementation
	// ---------------------------------------------------------------
	public void stopSession() {
		// exDesign.stop();
	}

	public Frame getFrame() {
		return (controller.getFrame());
	}
	// -------------------------------------------------------------
	// Run Button Handling
	// -------------------------------------------------------------
	private int runButtonState = INACTIVE;

	/**
	 * Activate the animation button. This is done whenever a display is shown
	 * which has an animated version.
	 */
	private void enableRunButton() {
		runButton.setEnabled(true);
	}

	/** Disable the animation button. */
	private void disableRunButton() {
		runButton.setEnabled(false);
	}
	/** This class handles button presses on the run button. */
	private class RunButtonHandler implements ActionListener {
		/**
		 * If the run button is enabled and is first pressed then run is
		 * started.
		 */
		public void actionPerformed(ActionEvent e) {
			showRealTimeDisplay();
		}
	}

	/**
	 * Show the current Display object in real time mode with active timing
	 * groups.
	 */
	private void showRealTimeDisplay() {
		disableRunButton();
		disableAnimationButton();
		// disableFullScreenButton();
		disableStepButtons();
		controller.setMenuBarEnabled(false);
		presentationPanel.setEditing(false);
		presentationPanel.clear();
		ArrayList displayList = new ArrayList();
		displayList.add(currentDisplay);
		presentationPanel.showDisplayList(displayList);
		presentationPanel.setDisplay(currentDisplay);
		presentationPanel.setEditing(true);
		enableRunButton();
		if (currentDisplay.isAnimated())
			enableAnimationButton();
		// enableFullScreenButton();
		enableStepButtons();
		controller.setMenuBarEnabled(true);
		presentationPanel.repaint();
	}
	// -------------------------------------------------------------
	// Animation Button
	// -------------------------------------------------------------
	private int animationButtonState = INACTIVE;

	/**
	 * Activate the animation button. This is done whenever a display is shown
	 * which has an animated version.
	 */
	private void enableAnimationButton() {
		animationButton.setEnabled(true);
	}

	/** Disable the animation button. */
	private void disableAnimationButton() {
		animationButton.setEnabled(false);
	}
	/** Thsi class handles button presses on the animation button. */
	private class AnimationButtonHandler implements ActionListener {
		/**
		 * If the animation button is enabled and is first pressed then
		 * animation is started.
		 */
		public void actionPerformed(ActionEvent e) {
			if (animationButtonState == INACTIVE) {
				// animation is not running, so start it
				animationButtonState = RUNNING;
				animationButton.setLabel("Stop");
				// startAnimation();
				startRealTimeAnimation();
			} else {
				// animation is running, so stop is
				// stopAnimation();
				stopRealTimeAnimation();
				animationButtonState = INACTIVE;
				animationButton.setLabel("Animate");
			}
		}
	}

	/**
	 * Start the animated version of this Display. The other mode panel buttons
	 * and demo and color index selection are disabled before animation is
	 * started.
	 */
	/*
	 * private void startAnimation() { disableStepButtons();
	 * controller.setMenuBarEnabled(false); presentationPanel.startAnimation();
	 * }
	 */
	/**
	 * Stop the animated version of this Display and reenable the other mode
	 * panel buttons and demo and color index selection.
	 */
	/*
	 * private void stopAnimation() { presentationPanel.stopAnimation();
	 * controller.setMenuBarEnabled(true); enableStepButtons();
	 * presentationPanel.repaint(); }
	 */
	/**
	 * Show an animation of the current Display object which must be able to do
	 * animations.
	 */
	private void startRealTimeAnimation() {
		if (expMode) {
			disableRunButton();
		}
		// disableAnimationButton();
		// disableFullScreenButton();
		disableStepButtons();
		controller.setMenuBarEnabled(false);
		presentationPanel.setEditing(false);
		presentationPanel.clear();
		presentationPanel.startAnimationPlayer();
	}

	private void stopRealTimeAnimation() {
		presentationPanel.stopAnimationPlayer();
		presentationPanel.setEditing(true);
		if (expMode) {
			enableRunButton();
		}
		// if (display.isAnimated()) enableAnimationButton();
		// enableFullScreenButton();
		enableStepButtons();
		controller.setMenuBarEnabled(true);
		presentationPanel.repaint();
	}

	// -------------------------------------------------------------
	// Full screen button
	// -------------------------------------------------------------
	// private int fullScreenState = INACTIVE;
	/**
	 * Enable the full screen button. This is called whenever we come back from
	 * a display mode where full screen has been disabled.
	 */
	/*
	 * private void enableFullScreenButton() {
	 * fullScreenButton.setEnabled(true); }
	 */
	/**
	 * Disable the full screen button. This is called whenever we want to go
	 * into a display mode where full screen should not be switched on.
	 */
	/*
	 * private void disableFullScreenButton() {
	 * fullScreenButton.setEnabled(false); }
	 */
	/** This class handles the full screen button. */
	/*
	 * private class FullScreenButtonHandler implements ActionListener { public
	 * void actionPerformed(ActionEvent e) { //
	 * System.out.println("Going to full screen."); gotoFullScreen(); } }
	 */
	/**
	 * Switch the display panel to full screen mode. This is done by removing
	 * the presentationPanel from its default container and adding it to the
	 * full screen window.
	 */
	// private void gotoFullScreen () {
	/*
	 * if (fullScreenState == RUNNING) return; this.remove(presentationPanel);
	 * fullScreenDisplayPanelContainer.setDisplayPanel(presentationPanel);
	 * fullScreenState = RUNNING; fullScreenDisplayPanelContainer.open();
	 * presentationPanel.requestFocus();
	 */
	// }
	// -------------------------------------------------------------
	// ReturnFromFullScreen implementation
	// -------------------------------------------------------------
	/*
	 * public boolean isFullScreen() { return(fullScreenState == RUNNING); }
	 */
	/**
	 * Switch the display panel back to normal mode. This is done by removing
	 * the presentationPanel from the full screen window and adding moving it
	 * back to its default container.
	 */
	// public void returnFromFullScreen() {
	/*
	 * if (fullScreenState == INACTIVE) return;
	 * fullScreenDisplayPanelContainer.close();
	 * fullScreenDisplayPanelContainer.removeDisplayPanel();
	 * this.add(presentationPanel, BorderLayout.CENTER); this.validate();
	 * fullScreenState = INACTIVE;
	 */
	// }
	// This is handled later:
	// public void activateTimingGroup(int bc)
	// -------------------------------------------------------------
	// The ColorChangeListener implementation
	// -------------------------------------------------------------
	/**
	 * This implements the ColorChangeListener interface where we listen to
	 * ColorChangeEvents of the ColorParServer. If the current color has changed
	 * then we have to recompute the display's colors and repaint the display.
	 */
	public void colorChanged(ColorChangeEvent e) {
		if (currentDisplay != null)
			currentDisplay.recomputeColors();
		presentationPanel.repaint();
	}

	// -------------------------------------------------------------
	// The ActionBar's ActionListener implementation
	// -------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		Button b = (Button) e.getSource();
		/*
		 * if (b == fullScreenButton) { System.out.println(b.getLabel()); } else
		 */if (b == animationButton) {
			System.out.println(b.getLabel());
		}
	}

	/**
	 * Initialize the stepping button whenever a new display has been selected.
	 */
	private void initSteppingAndAnimationButtons() {
		configureStepButtons();
		if (currentDisplay.isAnimated())
			enableAnimationButton();
		else
			disableAnimationButton();
	}
	// -------------------------------------------------------------
	// Step Buttons
	// -------------------------------------------------------------
	private Button[] stepButtons = null;
	private StepButtonHandler stepButtonHandler = null;

	private void configureStepButtons() {
		if (stepButtonHandler == null)
			stepButtonHandler = new StepButtonHandler();
		stepButtonPanel.removeAll();
		int n = currentDisplay.getTimingGroupCount();
		// if (n > 1) {
		stepButtons = new Button[n + 1];
		stepButtons[0] = new Button("x");
		stepButtons[0].addActionListener(stepButtonHandler);
		stepButtonPanel.add(stepButtons[0]);
		for (int i = 1; i <= n; i++) {
			Button b = new Button(String.valueOf(i));
			b.addActionListener(stepButtonHandler);
			stepButtons[i] = b;
			stepButtonPanel.add(b);
		}
		// } else {
		// stepButtons = null;
		// }
		getActionBar().validate();
	}
	private class StepButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			stepButtonActivated((Button) e.getSource());
		}

		private void stepButtonActivated(Button b) {
			for (int i = 0; i < stepButtons.length; i++) {
				if (b == stepButtons[i]) {
					activateTimingGroup(i);
				}
			}
		}
	}

	public void activateTimingGroup(int bc) {
		if (bc == 0) {
			presentationPanel.setObserveTimingGroups(false);
			presentationPanel.repaint();
		} else {
			for (int i = 1; i < stepButtons.length; i++) {
				if (bc == i) {
					presentationPanel.setObserveTimingGroups(true);
					currentDisplay.setTimingGroup(i - 1);
					presentationPanel.repaint();
				}
			}
		}
	}

	/** Enable the step buttons. */
	private void enableStepButtons() {
		setStepButtonsEnabled(true);
	}

	/** Disable the stepping button. */
	private void disableStepButtons() {
		setStepButtonsEnabled(false);
	}

	private void setStepButtonsEnabled(boolean state) {
		if (stepButtons != null) {
			for (int i = 0; i < stepButtons.length; i++) {
				stepButtons[i].setEnabled(state);
			}
		}
	}

	// -------------------------------------------------------------
	// The Display selection
	// -------------------------------------------------------------
	/**
	 * Set the active display to a display object which has not yet been
	 * initialized. Note that initialization must make sure that the spectral
	 * distribution panel for spectral displays is available since these
	 * register their distributions with charts during instance creation.
	 */
	public void setUninitializedDisplay(Display dsp) {
		dsp.createInstance();
		setDisplay(dsp);
	}

	/**
	 * Set the currently active display of this control panel. Note that this
	 * method expects a display Object which has already been initialized by its
	 * createInstance() method.
	 * 
	 * @param dsp
	 *            the Display object which should be the active display.
	 */
	public void setDisplay(Display dsp) {
		// Release the resourses of the previously active display
		// if (currentDisplay != null) {
		// currentDisplay.destroyInstance();
		// }
		// Set the active display and initialize its
		// geometry
		currentDisplay = dsp;
		// moved recompute() to the presentationPanel
		// currentDisplay.recompute(presentationPanel);
		presentationPanel.setDisplay(currentDisplay);
		presentationPanel.setObserveTimingGroups(false);
		initSteppingAndAnimationButtons();
		if (currentDisplay.canShowSpectralDistributions()) {
			showIlluminant.setEnabled(true);
			showReflectance.setEnabled(true);
			showEmittance.setEnabled(true);
		} else {
			showIlluminant.setEnabled(false);
			showReflectance.setEnabled(false);
			showEmittance.setEnabled(false);
		}
		/*
		 * if (descriptionFrame.isVisible()) {
		 * descriptionFrame.describe(currentDisplay); }
		 * 
		 * if (displayParameterFrame.isVisible()) {
		 * displayParameterFrame.describe(currentDisplay); }
		 */
		// The display panel has to know its display and the property
		// control panels have to be configured for the active display
		propertyPanel.configureFor(currentDisplay);
		if (currentDisplay.hasSpectralDistributions()) {
			initSpectralDistributionPanel();
		} else {
			spectralDistributionPanel.clear();
			spectralDistributionPanel.setVisible(false);
		}
		// Start animated displays with their first timing group
		if (currentDisplay.isAnimated()) {
			presentationPanel.setObserveTimingGroups(true);
			currentDisplay.setTimingGroup(0);
		}
		// Finally issue a repaint message to the display panel
		presentationPanel.repaint();
		// System.out.println("Selected display " + currentDisplayIndex + ": "
		// + currentDisplay.getTitle());
	}

	private void initSpectralDistributionPanel() {
		spectralDistributionPanel.clear();
		SpectralDistributionChart chart = null;
		ArrayList sd = ((SpectralColorDisplay) currentDisplay)
				.getSpectralLightDistributions();
		for (int i = 0; i < sd.size(); i++) {
			Object sdi = sd.get(i);
			if (sdi instanceof SpectralLightSource)
				chart = new SpectralDistributionChart((SpectralLightSource) sdi);
			else
				chart = new SpectralDistributionChart((SpectralLightFilter) sdi);
			spectralDistributionPanel.addSpectralDistributionChart(chart);
		}
		sd = ((SpectralColorDisplay) currentDisplay)
				.getSpectralColorDistributions();
		for (int i = 0; i < sd.size(); i++) {
			Object sdi = sd.get(i);
			chart = new SpectralDistributionChart((FilteredSpectralLight) sdi,
					colorParServer);
			spectralDistributionPanel.addSpectralDistributionChart(chart);
		}
		spectralDistributionPanel.setVisible(true);
	}
	// ---------------------------------------------------------
	// Popup menu creation and action
	// ---------------------------------------------------------
	/**
	 * This is the property view selection Menu which is contained in the main
	 * control frame's menu bar.
	 */
	private Menu displayPropertyMenu;
	/**
	 * This is the options menu which is contained in the main control frame's
	 * menu bar.
	 */
	private Menu optionsMenu;

	private void createPopupMenu() {
		popupMenu = new ExtendedPopupMenu();
		ActionListener pmh = new PopupMenuHandler();
		describeDisplay = new MenuItem("Describe display");
		describeDisplay.addActionListener(pmh);
		popupMenu.add(describeDisplay);
		showDisplayParList = new MenuItem("Show display parameters");
		showDisplayParList.addActionListener(pmh);
		popupMenu.add(showDisplayParList);
		dumpDisplay = new MenuItem("Save display image to file");
		dumpDisplay.addActionListener(pmh);
		popupMenu.add(dumpDisplay);
		popupMenu.addSeparator();
		showIlluminant = new MenuItem("Show Illuminant Spectrum");
		showIlluminant.addActionListener(pmh);
		popupMenu.add(showIlluminant);
		showReflectance = new MenuItem("Show Reflectance Spectrum");
		showReflectance.addActionListener(pmh);
		popupMenu.add(showReflectance);
		showEmittance = new MenuItem("Show Emitted Light Spectrum");
		showEmittance.addActionListener(pmh);
		popupMenu.add(showEmittance);
		popupMenu.addSeparator();
		copyToSystemClipboard = new MenuItem("Copy to System Clipboard");
		copyToSystemClipboard.addActionListener(pmh);
		popupMenu.add(copyToSystemClipboard);
		pickImageColor = new MenuItem("Pick Color from Image");
		pickImageColor.addActionListener(pmh);
		popupMenu.add(pickImageColor);
		// This are the color board copy/paste menu items
		MenuItem[] mi = propertyPanel.getColorControlPanel()
				.getPopupMenuItems();
		popupMenu.addSeparator();
		for (int i = 0; i < mi.length; i++) {
			popupMenu.add(mi[i]);
		}
		// If the controller does not have a menu bar then we add the
		// options menu to the popup menu.
		if (controller.getMenuBar() == null) {
			popupMenu.addSeparator();
			popupMenu.add(getOptionsMenu());
		}
		// System.out.println("Popup menu created. It has " +
		// popupMenu.getItemCount() + " entries.");
	}
	/** This subclass handles item selection from the popup menu. */
	private class PopupMenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			MenuItem s = (MenuItem) e.getSource();
			Point p = popupMenu.getActivationPoint();
			// System.out.println("Popup menu activated: " + s.getLabel());
			if (s == showIlluminant) {
				String n = ((SpectralColorDisplay) currentDisplay)
						.getIlluminantName(currentDisplay.getDisplayElementAt(
								p.x, p.y));
				if (n != null) {
					new SpectralDistributionFrame(n, true);
				}
			} else if (s == showReflectance) {
				String n = ((SpectralColorDisplay) currentDisplay)
						.getReflectanceName(currentDisplay.getDisplayElementAt(
								p.x, p.y));
				if (n != null) {
					new SpectralDistributionFrame(n, false);
				}
			} else if (s == showEmittance) {
				DisplayElement d = currentDisplay.getDisplayElementAt(p.x, p.y);
				String n1 = ((SpectralColorDisplay) currentDisplay)
						.getIlluminantName(d);
				String n2 = ((SpectralColorDisplay) currentDisplay)
						.getReflectanceName(d);
				if ((n1 != null) && (n2 != null)) {
					new SpectralDistributionFrame(n1, n2);
				}
			} else if (s == describeDisplay) {
				descriptionFrame.describe(currentDisplay);
			} else if (s == showDisplayParList) {
				displayParameterFrame.describe(currentDisplay);
			} else if (s == dumpDisplay) {
				saveCurrentDisplayToFile();
			} else if (s == copyToSystemClipboard) {
				copyCurrentColorToSystemClipboard();
			} else if (s == pickImageColor) {
				// p always is relative to the top left window
				DisplayElement d = currentDisplay.getDisplayElementAt(p.x, p.y);
				// System.out.println("Pointer at " + p.x + ", " + p.y);
				Rectangle b = d.getBounds();
				Dimension sz = presentationPanel.getSize();
				int x = p.x - sz.width / 2;
				int y = p.y - sz.height / 2;
				// System.out.println(" in user space at " + x + ", " + y);
				colorParServer.colorAdjusted(this,
						d.getColorAt(x - b.x, y - b.y));
			}
		}
	}

	// --------------------------------------------------------------
	// ClipboardOwner implementation
	// --------------------------------------------------------------
	public void lostOwnership(Clipboard clp, Transferable tf) {
	}

	private void copyCurrentColorToSystemClipboard() {
		PxlColor c = (PxlColor) colorParServer.getActiveColor();
		// Clipboard cb =
		// Base.getRootContainer().getToolkit().getSystemClipboard();
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection s = new StringSelection(c.toString());
		cb.setContents(s, this);
		System.out.println(c.toString());
	}

	public void saveCurrentDisplayToFile() {
		FileDialog fd = new FileDialog(new Frame(), dumpDisplay.getLabel(),
				FileDialog.SAVE);
		fd.setDirectory(".");
		fd.setFile(currentDisplay.getClassName() + ".png");
		fd.show();
		String fn = fd.getFile();
		if (fn != null) {
			presentationPanel.dumpImage(fd.getDirectory() + fn);
		}
		fd.dispose();
	}

	// -------------------------------------------------------------
	// The options menu
	// -------------------------------------------------------------
	public Menu getOptionsMenu() {
		optionsMenu = new Menu("Options");
		optionsMenu.add(propertyPanel.getColorControlPanel().getOptionsMenu());
		// optionsMenu.add(geometryControlPanel.getOptionsMenu());
		// optionsMenu.add(timingControlPanel.getOptionsMenu());
		return (optionsMenu);
	}

	// -------------------------------------------------------------
	// The property panels menu and selection
	// -------------------------------------------------------------
	/** Enable the property selection menu. */
	/*
	 * private void enableDisplayProperty() { //
	 * displayPropertyMenu.setEnabled(true); // enableCycleButton(); }
	 */
	/** Disable the property selection menu. */
	/*
	 * private void disableDisplayProperty() { //
	 * displayPropertyMenu.setEnabled(false); // disableCycleButton(); }
	 */
	// -------------------------------------------------------------
	// The ComponentListener implementation
	// -------------------------------------------------------------
	/**
	 * Whenever this component becomes hidden we disable the display and
	 * property selection menu.
	 */
	public void componentHidden(ComponentEvent e) {
		// controller.setMenuBarEnabled(false);
		// disableDisplayProperty();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
	}

	/**
	 * Whenever this component becomes visible we enable the display and
	 * property selection menu.
	 */
	public void componentShown(ComponentEvent e) {
		// controller.setMenuBarEnabled(true);
		// enableDisplayProperty();
	}
}
