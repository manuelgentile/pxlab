package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import de.pxlab.awtx.*;
import de.pxlab.util.StringExt;

/**
 * Controls display list presentation.
 * 
 * @version 0.8.10
 * @see DisplayList
 * @see TimingMonitor
 */
/*
 * 
 * 2004/10/27
 * 
 * 2005/03/08 fixed showAndWait() such that on WATCHING_CLOCK_TIMERS which have
 * STOP_RESPONSE_TIMER_BIT set the ResponseTime and ResponseCode parameters are
 * set properly when spurious response events have been found.
 * 
 * 2005/04/15 implement timing group image creation and preloading
 * 
 * 2005/05/02 fixed bug with activeResponseTiming
 * 
 * 2005/07/11 fixed bug with (first) Block presentation
 * 
 * 2005/07/14 fixed bug with two JustInTime display objects in sequence where
 * the second is a JOIN overlay to the first. This requires that preloading in
 * showAndWait() is only then being done if the respective Display object's next
 * pointer is null.
 * 
 * 2005/07/21 store mouse position for mouse responses.
 * 
 * 2005/09/19 If an animated display's timer type is END_OF_MEDIA_TIMER, then
 * the animation player stops after a cycle is completed.
 * 
 * 2006/06/19 restrict use of SYNC_TO/ and END_OF_MEDIA_TIMER
 * 
 * 2007/02/11 polled devices and SpaceMouse
 * 
 * 2007/05/16 set TimeControl here in order to handle tracking timers correctly
 * 
 * 2008/03/05 modified showAndWait() to handle FIXED_DELAY_BIT timer objects
 */
public class PresentationManager implements ExDesignProcessor, TimerBitCodes {
	/**
	 * This is the object which starts an experimental run. It is needed here in
	 * order to handle keyboard break events from our private keyboard event
	 * dispatcher.
	 */
	private ExDesignThreadStarter exDesignThreadStarter = null;
	/** The display device we work with. */
	private DisplayDevice displayDevice;
	/**
	 * An external container which holds the drawing surface of the display
	 * device. This is nun-null only if an external container is used to hold
	 * the display surface which is the case for applets.
	 */
	private Container surfaceContainer;
	/**
	 * The timing monitor object which is used to synchronize response
	 * collection.
	 */
	private TimingMonitor timingMonitor;
	/**
	 * Starting time of a response interval which spans multiple Display
	 * objects. This is activated byte the START_RESPONSE_TIMER_BIT.
	 */
	private long responseIntervalStart;
	/**
	 * This is a shorthand for timing events which need response device timing.
	 */
	private static final int responseTimerMask = (MOUSE_BUTTON_TIMER_BIT
			| KEY_TIMER_BIT | XBUTTON_TIMER_BIT | SERIAL_LINE_TIMER_BIT);
	private static final int mediaTimerMask = (SYNC_TO_MEDIA_TIMER_BIT | END_OF_MEDIA_TIMER_BIT);
	/**
	 * A serial communication device which is able to send text strings as
	 * responses to this panel.
	 */
	private SerialCommunicationDevice serialDev = null;
	private ResponseManager responseManager;
	/**
	 * This dispatcher links into the system focus manager, picks up all
	 * keyboard events and sends these to the response manager.
	 */
	protected KeyboardResponseDispatcher keyboardResponseDispatcher;
	private boolean protocol = false;
	private PrintWriter protocolWriter;
	/**
	 * The owner Frame of this PresentationManager's DisplayDevice. The only
	 * purpose of this Frame object is to send it to the
	 * ExperimentalDisplayDevice which needs it as a parent for its Windows.
	 */
	private Frame owner;
	/**
	 * If this flag becomes true then the stimulus presentation is stopped as
	 * soon as possible.
	 */
	private boolean stopPresentation = false;

	/**
	 * Create a new PresentationManager which later creates its own
	 * ExperimentalDisplayDevice drawing surface. This constructor is called by
	 * the classes de.pxlab.pxl.run.ExRun, de.pxlab.pxl.run.Starter, and
	 * de.pxlab.pxl.design.ExDesignEditorPanel. This case uses late
	 * instantiation of the DisplayDevice
	 * 
	 * @param owner
	 *            the owner Frame of this PresentationManager
	 * @param threadStarter
	 *            the object which will start the thread for running an
	 *            experiment. It is needed here in order to allow our private
	 *            keyboard event dispatcher to stop an experimental run.
	 */
	public PresentationManager(Frame owner, ExDesignThreadStarter threadStarter) {
		super();
		this.owner = owner;
		this.exDesignThreadStarter = threadStarter;
		timingMonitor = new TimingMonitor();
		responseManager = new ResponseManager(this, timingMonitor);
		displayDevice = null;
		openProtocolFile();
		Debug.clearTiming();
	}

	/**
	 * Create a new PresentationManager for cases where experiments are run and
	 * there is an external container for the experimental display device. This
	 * constructor is called by the class de.pxlab.pxl.run.ExRunApplet.
	 * 
	 * @param owner
	 *            the owner Frame of this PresentationManager
	 * @param threadStarter
	 *            the object which will start the thread for running an
	 *            experiment. It is needed here in order to allow our private
	 *            keyboard event dispatcher to stop an experimental run.
	 * @param container
	 *            a Container which will hold the experimental display device's
	 *            drawing surface.
	 */
	public PresentationManager(Frame owner,
			ExDesignThreadStarter threadStarter, Container container) {
		super();
		this.owner = owner;
		this.exDesignThreadStarter = threadStarter;
		surfaceContainer = container;
		timingMonitor = new TimingMonitor();
		responseManager = new ResponseManager(this, timingMonitor);
		displayDevice = new ExperimentalDisplayDevice(owner, container,
				responseManager, responseManager, responseManager);
		Debug.clearTiming();
	}

	/**
	 * Create a new PresentationManager using the given DisplayDevice object to
	 * show stimuli. This constructor is called by the class
	 * de.pxlab.pxl.display.editor.EditablePresentationPanel.
	 * 
	 * @param owner
	 *            the owner Frame of this PresentationManager
	 * @param device
	 *            the DisplayDevice which will be used by this
	 *            PresentationManager
	 */
	public PresentationManager(Frame owner, DisplayDevice device) {
		super();
		this.owner = owner;
		timingMonitor = new TimingMonitor();
		responseManager = new ResponseManager(this, timingMonitor);
		displayDevice = device;
		Debug.clearTiming();
	}

	/** Stop the currently running presentation. */
	public void stop() {
		Debug.show(Debug.EVENTS, "PresentationManager.stop()");
		stopPresentation = true;
		timingMonitor.stop();
	}

	/**
	 * Dispose off all resources used by this presentation manager.
	 */
	public void dispose() {
		if (surfaceContainer != null) {
			surfaceContainer.remove(displayDevice.getComponent());
		}
		if (displayDevice != null) {
			displayDevice.dispose();
		}
		// System.out.println("PresentationManager.dispose() finished.");
	}

	/**
	 * Get this presentation manager's ResponseManager.
	 * 
	 * @return the ResponseManager.
	 */
	public ResponseManager getResponseManager() {
		return responseManager;
	}

	/**
	 * Get this presentation manager's DisplayDevice.
	 * 
	 * @return the DisplayDevice.
	 */
	public DisplayDevice getDisplayDevice() {
		return displayDevice;
	}

	// ---------------------------------------------------------------
	// BreakListener implementation
	// ---------------------------------------------------------------
	/**
	 * Someone has pressed the BREAK key combination while an experiment was
	 * running.
	 */
	public void breakPerformed(KeyEvent e) {
		stop();
	}

	// ------------------------------------------------------------------------
	// ExDesignProcessor implementation
	// ------------------------------------------------------------------------
	/**
	 * This method is called immediately after the design tree has been created
	 * even before the run time tree exists. Methods which rely on global
	 * parameter assignments should not be called from here but from
	 * startSession().
	 * 
	 * @param p
	 *            the ProcedueStart node for this design.
	 * @param d
	 *            the Display object list for the start of the procedure.
	 * @return a code indicating how the start of the experiment has been
	 *         evaluated.
	 */
	public int startProcedure(ExDesignNode p, ArrayList d) {
		open();
		ExPar.ProcedureTime.set(HiresClock.getTimeDouble());
		int returnCode = showDisplayList(d);
		if (protocol)
			protocolWriter.println(p.toString());
		return (returnCode);
	}

	/**
	 * This method is called when the active sessions have been run and the
	 * experiment is about to be finished.
	 * 
	 * @param p
	 *            the ProcedureEnd node for this design.
	 * @param d
	 *            the Display object list for the end of the procedure.
	 * @return a code indicating how the end of the experiment has been
	 *         evaluated.
	 */
	public int endProcedure(ExDesignNode p, ArrayList d) {
		int returnCode = showDisplayList(d);
		close();
		// System.out.println("PresentationManager.endExperiment()");
		return (returnCode);
	}

	/**
	 * This method starts the active session of the experiment. It is called
	 * after the run time tree has been created which means that all global
	 * parameter assignments have been executed.
	 * 
	 * @param p
	 *            the ExDesignNode for this session.
	 * @param d
	 *            the Display object list for the start of a session.
	 * @return a code indicating how the session start has been evaluated.
	 */
	public int startSession(ExDesignNode p, ArrayList d) {
		int returnCode;
		// System.out.println("PresentationManager.startSession() " +
		// p.toString());
		if (protocol)
			protocolWriter.println(PxlColor.getDeviceTransform().toString());
		ExPar.SessionTime.set(HiresClock.getTimeDouble());
		returnCode = showDisplayList(d);
		if (protocol)
			protocolWriter.println(p.toString());
		return (returnCode);
	}

	/**
	 * This method ends the active session of this experiment.
	 * 
	 * @param p
	 *            the ExDesignNode for this session.
	 * @param d
	 *            the Display object list for the end of a session.
	 * @return a code indicating how the session end has been evaluated.
	 */
	public int endSession(ExDesignNode p, ArrayList d) {
		int returnCode;
		// System.out.println("PresentationManager.endSession()");
		returnCode = showDisplayList(d);
		return (returnCode);
	}

	/**
	 * This method is called whenever a new block is started.
	 * 
	 * @param p
	 *            the ExDesignNode for this block.
	 * @param d
	 *            the Display object list for the start of a block.
	 * @return a code indicating how the block start has been evaluated.
	 */
	public int startBlock(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		// System.out.println("PresentationManager.startBlock() " + p);
		// System.out.println("Block type: " + p.getTypeModifier());
		ExPar.BlockTime.set(HiresClock.getTimeDouble());
		if (!p.containsTypeModifier(ExDesignNode.FirstBlock)) {
			returnCode = showDisplayList(d);
		}
		if (protocol)
			protocolWriter.println(p.toString());
		return (returnCode);
	}

	/**
	 * This method is called when a block has been finished.
	 * 
	 * @param p
	 *            the ExDesignNode for this block.
	 * @param d
	 *            the Display object list for the end of a block.
	 * @return a code indicating how the block end has been evaluated.
	 */
	public int endBlock(ExDesignNode p, ArrayList d) {
		int returnCode = 0;
		// System.out.println("PresentationManager.endBlock() " + p);
		// System.out.println("Block type: " + p.getTypeModifier());
		if (!p.containsTypeModifier(ExDesignNode.LastBlock)) {
			returnCode = showDisplayList(d);
		}
		if (protocol)
			protocolWriter.println(p.toString());
		return (returnCode);
	}

	/**
	 * This method is called to run a single trial. Note that the trial
	 * arguments have been pushed immediately before this method is called. The
	 * caller of this method will check for adaptive fixups and also will write
	 * the trial data to the data file. It then will pop the trial arguments
	 * back. This method should return a flag to indicate whether this trial
	 * should be repeated.
	 * 
	 * @param p
	 *            the ExDesignNode for this trial.
	 * @param d
	 *            the Display object list for a trial.
	 * @return a code indicating how the trial has been evaluated.
	 */
	public int runTrial(ExDesignNode p, ArrayList d) {
		int returnCode;
		// System.out.println("PresentationManager.runTrial() start");
		// System.out.println("      " + p.toString());
		// System.out.println("      " + p.toCurrentValueString());
		ExPar.TrialTime.set(HiresClock.getTimeDouble());
		returnCode = showDisplayList(d);
		// System.out.println("PresentationManager.runTrial() done");
		// System.out.println("      " + p.toCurrentValueString());
		// System.out.println("      " + p.toString());
		// System.out.println("PresentationManager.runTrial(): Protocol is " +
		// (protocol? "ON": "OFF"));
		if (protocol)
			System.out.println(" p: " + p.toCurrentValueString());
		return (returnCode);
	}

	// ------------------------------------------------------------------------
	// Some support methods for the ExDesignProcessor
	// ------------------------------------------------------------------------
	// private FocusChangeListener focusChangeListener;
	/**
	 * Activate the presentation manager: capture all keyboard input and open
	 * the display device.
	 */
	private void open() {
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		/*
		 * focusChangeListener = new FocusChangeListener();
		 * kfm.addVetoableChangeListener(focusChangeListener);
		 */
		keyboardResponseDispatcher = new KeyboardResponseDispatcher(
				exDesignThreadStarter, responseManager);
		kfm.addKeyEventDispatcher(keyboardResponseDispatcher);
		Debug.show(Debug.FOCUS,
				"PresentationManager.open(): new key event dispatcher installed.");
		// showFocus();
		if (displayDevice == null) {
			displayDevice = new ExperimentalDisplayDevice(owner,
					responseManager, responseManager, responseManager);
		}
		displayDevice.setHiddenCursor(ExPar.HideCursor.getFlag());
		displayDevice.open();
	}

	/**
	 * Deactivate the presentation manager. Re-enable standard keyboard focus
	 * management and close the display device. Whenever an experiment has been
	 * finished normally then this method is called by the PresentationManager
	 * itself. If, however, an experimental run is stopped asynchronously then
	 * the creator of any PresentationManager instance has to call this method
	 * in order to make sure that the system's KeyboardFocusManager works
	 * correctly.
	 */
	public void close() {
		displayDevice.close();
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kfm.removeKeyEventDispatcher(keyboardResponseDispatcher);
		Debug.show(Debug.FOCUS,
				"PresentationManager.close(): private key event dispatcher removed.");
		// kfm.removeVetoableChangeListener(focusChangeListener);
	}

	/*
	 * private class FocusChangeListener implements
	 * java.beans.VetoableChangeListener { public void
	 * vetoableChange(java.beans.PropertyChangeEvent evt) throws
	 * java.beans.PropertyVetoException { String p = evt.getPropertyName();
	 * System.out.println("Trying to change " + p + " from " + evt.getOldValue()
	 * + " to " + evt.getNewValue()); if (p.equals("focusOwner") ||
	 * p.equals("focusedWindow") || p.equals("permanentFocusOwner") ||
	 * p.equals("activeWindow")) { throw new
	 * java.beans.PropertyVetoException("No!", evt); } } }
	 */
	private void showFocus() {
		KeyboardFocusManager k = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		Window wa = k.getActiveWindow();
		Window wf = k.getFocusedWindow();
		Component c = k.getFocusOwner();
		String s = "Active Window = "
				+ ((wa == null) ? "null" : wa.getClass().getName()) + "\n"
				+ "Focused Window = "
				+ ((wf == null) ? "null" : wf.getClass().getName()) + "\n"
				+ "Focus Owner = "
				+ ((c == null) ? "null" : c.getClass().getName());
		System.out.println(s);
	}

	private void openProtocolFile() {
		String ps = ExPar.ProtocolFileName.getString();
		if (StringExt.nonEmpty(ps)) {
			try {
				PrintWriter pw = new PrintWriter(new FileWriter(ps), true);
				setProtocol(true, pw);
			} catch (IOException iox) {
				new FileError("Can't open protocol file " + ps);
			}
		} else {
			setProtocol(ExPar.RuntimeProtocol.getFlag());
		}
		// System.out.println("PresentationManager.<init>: Protocol is " +
		// (protocol? "ON": "OFF"));
	}

	private void setProtocol(boolean state) {
		protocol = state;
		if (state)
			protocolWriter = new PrintWriter(System.out);
		// System.out.println("PresentationManager.setProtocol(boolean): Protocol is "
		// + (protocol? "ON": "OFF"));
	}

	private void setProtocol(boolean state, PrintWriter stream) {
		protocol = state;
		if (state)
			protocolWriter = stream;
		// System.out.println("PresentationManager.setProtocol(boolean, PrintWriter): Protocol is "
		// + (protocol? "ON": "OFF"));
	}

	// ----------------------------------------------------------
	// Open/close external control box
	// ----------------------------------------------------------
	/**
	 * Open an external response box connected to the serial port.
	 * 
	 * @param port
	 *            serial port identifier. Valid names are 'COM1' or 'COM2' under
	 *            Windows and 'Serial A' or 'Serial B' on sun workstations.
	 */
	public boolean openExternalControlBox(String port) {
		boolean ret = false;
		if (Base.isApplication()) {
			try {
				// System.out.println("PresentationManager.openExternalControlBox()");
				if (ExternalControlBox.open(port)) {
					ExternalControlBox
							.addExternalButtonListener(responseManager);
				}
				ret = true;
			} catch (NoClassDefFoundError ndf) {
				Syslog.out
						.println("PresentationManager.openExternalControlBox(): Java Communications API not found. No external control box available.");
			}
		}
		return ret;
	}

	/** Close the external control box. */
	public void closeExternalControlBox() {
		if (ExternalControlBox.isOpen())
			ExternalControlBox.close();
	}
	// ----------------------------------------------------------
	// Polled response device implementation
	// ----------------------------------------------------------
	private static final String XDEV = "dev";

	/**
	 * Open a connection to a polled device.
	 * 
	 * @param type
	 *            device type.
	 * @param id
	 *            device id.
	 * @param del
	 *            polling delay for the device.
	 * @param axisLimit
	 *            the maximum axis delta value to be expected for the device
	 *            axes.
	 */
	public void openPolledDevice(int type, int id, int del, double axisLimit) {
		Object key = RuntimeRegistry.createKey(XDEV, type, id);
		if (!RuntimeRegistry.containsKey(key)) {
			PolledDevice pd = null;
			switch (type) {
			case DeviceCodes.DI_DEVICE:
			case DeviceCodes.GAMEPAD:
			case DeviceCodes.JOYSTICK:
			case DeviceCodes.RUMBLE_PAD:
			case DeviceCodes.DI_SPACE_MOUSE:
				pd = new DIDevice(type, id, axisLimit);
				break;
			}
			if (pd != null) {
				pd.setPollingDelay(del);
				Debug.show(
						Debug.EVENTS,
						"PresentationManager.openPolledDevive(): Device "
								+ pd.getName() + " opened.");
				responseManager.addPolledDevice(pd);
				RuntimeRegistry.put(key, pd);
			} else {
				new FatalError(
						"PresentationManager.openPolledDevive(): Device type "
								+ type + " is unknown.");
			}
		} else {
			new FatalError("PresentationManager.openPolledDevive(): Device "
					+ type + " [" + id + "] already open.");
		}
	}

	/**
	 * Reset a polled device to a well defined state.
	 * 
	 * @param type
	 *            device type.
	 * @param id
	 *            device id.
	 */
	public void resetPolledDevice(int type, int id) {
		Object key = RuntimeRegistry.createKey(XDEV, type, id);
		PolledDevice pd = (PolledDevice) RuntimeRegistry.get(key);
		if (pd != null) {
			pd.reset();
			Debug.show(Debug.EVENTS,
					"PresentationManager.resetPolledDevive(): Device " + type
							+ " [" + id + "] reset.");
		}
	}

	/**
	 * Close the connection to a polled device and stop polling.
	 * 
	 * @param type
	 *            device type.
	 * @param id
	 *            device id.
	 */
	public void closePolledDevice(int type, int id) {
		Object key = RuntimeRegistry.createKey(XDEV, type, id);
		PolledDevice pd = (PolledDevice) RuntimeRegistry.remove(key);
		if (pd != null) {
			pd.close();
			responseManager.removePolledDevice(pd);
			Debug.show(Debug.EVENTS,
					"PresentationManager.closePolledDevive(): Device " + type
							+ " [" + id + "] closed.");
		}
	}

	// ----------------------------------------------------------
	// SpaceMouse device implementation
	// ----------------------------------------------------------
	/**
	 * Open a connection to a SpaceMouse device.
	 * 
	 * @param type
	 *            device type.
	 * @param id
	 *            device id.
	 * @param del
	 *            polling delay for the space mouse thread.
	 * @param axisLimit
	 *            the maximum axis delta value to be expected for the SpaceMouse
	 *            axes.
	 */
	public void openSpaceMouse(int type, int id, int del, double axisLimit) {
		Object key = RuntimeRegistry.createKey(XDEV, type, id);
		if (!RuntimeRegistry.containsKey(key)) {
			SpaceMouse pd = new SpaceMouse(displayDevice.getComponent(),
					responseManager);
			pd.setDelay(del);
			SpaceMouseEvent.setAxisLimit(axisLimit);
			Debug.show(
					Debug.EVENTS,
					"PresentationManager.openSpaceMouse(): Device "
							+ pd.getName() + " opened.");
			responseManager.addSpaceMouse(pd);
			RuntimeRegistry.put(key, pd);
			pd.open();
		} else {
			new FatalError("PresentationManager.openSpaceMouse(): Device "
					+ type + " [" + id + "] already open.");
		}
	}

	/**
	 * Reset a connected SpaceMouse device to a well defined state.
	 * 
	 * @param type
	 *            device type.
	 * @param id
	 *            device id.
	 */
	public void resetSpaceMouse(int type, int id) {
		Object key = RuntimeRegistry.createKey(XDEV, type, id);
		SpaceMouse pd = (SpaceMouse) RuntimeRegistry.get(key);
		if (pd != null) {
			pd.reset();
			Debug.show(Debug.EVENTS,
					"PresentationManager.resetPolledDevive(): Device " + type
							+ " [" + id + "] reset.");
		}
	}

	/**
	 * Close the connection to a SpaceMouse device.
	 * 
	 * @param type
	 *            device type.
	 * @param id
	 *            device id.
	 */
	public void closeSpaceMouse(int type, int id) {
		Object key = RuntimeRegistry.createKey(XDEV, type, id);
		SpaceMouse pd = (SpaceMouse) RuntimeRegistry.remove(key);
		if (pd != null) {
			pd.close();
			responseManager.removeSpaceMouse(pd);
			Debug.show(Debug.EVENTS,
					"PresentationManager.closePolledDevive(): Device " + type
							+ " [" + id + "] closed.");
		}
	}
	// ----------------------------------------------------------
	// Serial communication device implementation
	// ----------------------------------------------------------
	private SerialCommunicationDevice serialCommDevice;

	public boolean openSerialCommunicationDevice(String port) {
		boolean ret = false;
		if (Base.isApplication()) {
			try {
				// System.out.println("PresentationManager.openSerialCommunicationDevice()");
				serialCommDevice = new SerialCommunicationDevice(port,
						responseManager);
				ret = true;
				// System.out.println("PresentationManager(): serial communication device "
				// + port + " installed.");
			} catch (NoClassDefFoundError ndf) {
				Syslog.out
						.println("PresentationManager.openSerialCommunicationDevice(): Java Communication API classes not found. No serial communication device available.");
			}
		}
		return ret;
	}

	public void closeSerialCommunicationDevice() {
		if ((serialCommDevice != null) && (serialCommDevice.isOpen())) {
			serialCommDevice.close();
		}
	}

	public SerialCommunicationDevice getSerialCommunicationDevice() {
		return serialCommDevice;
	}

	// ------------------------------------------------------------------------
	// Experimental Display List Support
	// ------------------------------------------------------------------------
	/**
	 * Show the given Display object list for an experiment. This method is
	 * called whenever someone wants to show a list of Display objects under
	 * timing control. The Display objects' geometry need not be computed for
	 * this panel before calling this method since this will be done here. This
	 * is the only way to allow information collected within a Display list
	 * being used in the same Display list but possibly some step later.
	 * 
	 * @param displayList
	 *            the list of Display objects to run. Usually this will be an
	 *            instance of class DisplayList.
	 * @return a code which tells the caller whether the display list was shown
	 *         without break or whether someone has requested that the display
	 *         list presentation be stopped.
	 */
	public int showDisplayList(ArrayList displayList) {
		Debug.clearTiming("Start display list:           ");
		if (displayList == null)
			return DSPL_OK;
		stopPresentation = false;
		TimingElement timingElement = null;
		int dls = displayList.size();
		// System.out.println("PresentationManager.showDisplayList(): running "
		// + dls + " display objects.");
		/*
		 * First step: Create the showList. The showList is an array list of
		 * Display object chains. Each entry in the list represents a display
		 * series which actually gets a single image screen. Every Display
		 * object chain consists of a series of Display objects where the first
		 * one is a non-JOIN-overlay and all other objects are JOIN-overlay
		 * objects.
		 */
		ArrayList showList = new ArrayList(10);
		Display dsp;
		Display nextDsp;
		Display previousDisplay = null;
		Display listOverlay = null;
		Display theAdjustable = null;
		for (int i = 0; i < dls; i++) {
			dsp = (Display) displayList.get(i);
			// Assume we may use preloading by default
			dsp.setCanPreload(true);
			dsp.setTransparentOverlayFollows(false);
			// dsp.next ist the pointer to a JOIN-overlay for dsp, assume no
			// overlay
			dsp.next = null;
			// Assume that this display does not start an overlay sequence
			// containing an adjustable display
			dsp.startAdjustable = false;
			// Make sure that the first entry of the showList is not a JOIN or
			// TRANSPARENT overlay
			if (i == 0) {
				if (dsp.isJoinOverlay()) {
					new ParameterValueError(
							"The first display object in a list can't be a JOIN overlay: "
									+ dsp.getInstanceName());
					return DSPL_STOPPED;
				} else if (dsp.isTransparentOverlay()) {
					new ParameterValueError(
							"The first display object in a list can't be a TRANSPARENT overlay: "
									+ dsp.getInstanceName());
					return DSPL_STOPPED;
				}
			}
			// Some displays may generally not being preloaded. We exclude them
			// from preloading
			if (dsp.getDisplayListControl() || dsp.JustInTime.getFlag()
					|| dsp.isAnimated() || !dsp.isGraphic()) {
				dsp.setCanPreload(false);
				// This is a workaround for a bug of the Java 1.5.0 full screen
				// exclusive mode handling
				if (dsp.isTransparentOverlay()) {
					if (((ExperimentalDisplayDevice) displayDevice)
							.isFullScreenExclusive())
						new ParameterValueError(
								"Not preloadable TRANSPARENT overlay objects are not supported in full screen exclusive mode: "
										+ dsp.getInstanceName());
				}
			}
			// Recompute all displays for the current display panel as long
			// as they do not have the JustInTime attribute set.
			if (!dsp.JustInTime.getFlag()) {
				displayDevice.setActiveScreen(dsp.Screen.getInt());
				dsp.recompute(this);
			} else if (dsp.isListOverlay()) {
				new ParameterValueError(
						"DISPLAY_LIST overlay objects can't have the JustInTime flag set: "
								+ dsp.getInstanceName());
			}
			if (dsp.isClearListOverlay()) {
				listOverlay = null;
			}
			if (dsp.isListOverlay()) {
				listOverlay = dsp;
			} else {
				dsp.setListOverlay(listOverlay);
				if (dsp.isJoinOverlay() && (previousDisplay != null)) {
					// This is a JOIN-overlay for the previous display object.
					if (dsp.JustInTime.getFlag()) {
						// JOIN overlays which are JustInTime must be preceded
						// by JustInTime display objects.
						if (!previousDisplay.JustInTime.getFlag()) {
							new ParameterValueError(
									"JOIN overlays which are JustInTime must be preceded by JustInTime display objects: "
											+ dsp.getInstanceName());
						}
					}
					previousDisplay.next = dsp;
					// System.out.println("PresentationManager.showDisplayList(): adding overlay "
					// + dsp.getInstanceName());
				} else {
					// This is not a JOIN overlay so add it to the showList
					showList.add(dsp);
					// System.out.println("PresentationManager.showDisplayList(): adding non-overlay "
					// + dsp.getInstanceName());
				}
				if (dsp.getAdjustable()) {
					theAdjustable = dsp;
					// Mark the showList entry which starts this
					// sequnece of of JOIN overlays as starting a
					// sequence which contains an adjustable
					Display d = (Display) (showList.get(showList.size() - 1));
					d.startAdjustable = true;
					// Mark the showList entry as not preloadable
					d.setCanPreload(false);
				}
				if (dsp.isTransparentOverlay() && (previousDisplay != null)) {
					previousDisplay.setTransparentOverlayFollows(true);
				}
				previousDisplay = dsp;
			}
		}
		if (stopPresentation)
			return DSPL_STOPPED;
		/* At this point we have created the showList array. */
		int sls = showList.size();
		Image memoryBuffer = null;
		Image lastMemoryBuffer = null;
		Graphics2D g = null;
		/*
		 * Second step: Create a memory buffer image for every Display object in
		 * the showList which may be preloaded. Actually the memory buffer
		 * created here is added to the last Display object in a JOIN-overlay
		 * chain while the preloadable property is read from the first element
		 * in the chain. For non-JOIN-overlay objects the last and the first
		 * element in the chain will be one and the same object. Note that the
		 * Execute flag is ignored at this point since the Execute flag is a
		 * just-in-time feature. An exception are JOIN-overlay objects. If they
		 * have Execute set to 0 then they are not painted such that the memory
		 * buffer does not contain them. Objects which have the visible property
		 * not set are not preloaded at this point.
		 */
		// System.out.println("PresentationManager.showDisplayList(): prepare preloading.");
		for (int i = 0; i < sls; i++) {
			dsp = (Display) showList.get(i);
			// The head of each chain decides on preloading
			if (dsp.getCanPreload()) {
				while (dsp != null) {
					if (dsp.getVisible()) {
						// System.out.println("PresentationManager.showDisplayList(): preloading "
						// + dsp.getInstanceName());
						int tgn = dsp.getLastTimingGroupIndex();
						dsp.clearBoundingBox();
						dsp.clearTimingGroup();
						for (int groupIndex = 0; groupIndex <= tgn; groupIndex++) {
							// System.out.println("PresentationManager.showDisplayList(): preloading group "
							// + groupIndex);
							timingElement = dsp.getTiming(groupIndex);
							dsp.nextTimingGroup();
							if (dsp.isJoinOverlay()) {
								// This is a JOIN overlay so we simply add it to
								// the current Graphics context
								if (dsp.Execute.getFlag()) {
									dsp.showGroup();
								}
								if (dsp.next == null) {
									// This is the last element in the chain so
									// add the list overlay
									if (listOverlay != null)
										listOverlay.show();
									// and send the memory buffer to the timing
									// element
									// System.out.println("PresentationManager.showDisplayList(): set image for "
									// + dsp.getInstanceName());
									timingElement
											.setMemoryBufferImage(memoryBuffer);
									g.dispose();
									memoryBuffer = null;
								}
							} else {
								// This is a non-JOIN-overlay Display object
								if (memoryBuffer == null) {
									memoryBuffer = timingElement
											.getMemoryBufferImage();
									if (memoryBuffer == null) {
										// and we have to create a new memory
										// buffer for the Display object's
										// screen
										displayDevice
												.setActiveScreen(dsp.Screen
														.getInt());
										Debug.show(Debug.TIMING,
												"PresentationManager.showDisplayList(): Creating buffer for "
														+ dsp.getInstanceName()
														+ " [" + groupIndex
														+ "]");
										memoryBuffer = displayDevice
												.createMemoryBuffer(timingElement);
									}
									// and we also have to create a new Graphics
									// context for painting
									g = (Graphics2D) (memoryBuffer
											.getGraphics());
									if (dsp.isTransparentOverlay()) {
										// If this is a TRANSPARENT overlay we
										// have to load the previous buffer
										// first
										if (lastMemoryBuffer != null) {
											g.drawImage(lastMemoryBuffer, 0, 0,
													null);
										} else {
											System.out
													.println("Last memory buffer is missing!");
										}
									}
								}
								// Then paint the current timing group onto the
								// current Graphics context.
								dsp.showGroup(g);
								// Add the list overlay if necessary
								if (listOverlay != null)
									listOverlay.show();
								if (dsp.next == null) {
									/*
									 * This Display object is no JOIN-overlay
									 * and no JOIN-overlay follows so send the
									 * memory buffer to the timing element which
									 * saves it for later preloading.
									 */
									// System.out.println("PresentationManager.showDisplayList(): set image for "
									// + dsp.getInstanceName());
									timingElement
											.setMemoryBufferImage(memoryBuffer);
									g.dispose();
									lastMemoryBuffer = memoryBuffer;
									memoryBuffer = null;
								} else {
									/*
									 * This Display is no JOIN-overlay but a
									 * JOIN-overlay follows, so nothing must be
									 * sent to the timing group.
									 */
								}
							} // isJoinOverlay()
						} // for groupIndex
					} // Visible
					dsp = dsp.next;
				} // while dsp != null
			} // canPreload()
		}
		if (stopPresentation)
			return DSPL_STOPPED;
		/*
		 * Ready for the 3rd step: We finally show the showList of Display
		 * objects. Note that we observe the Execute flag here, since this is
		 * just-in-time. For a chain of JOIN-overlays this means that the
		 * complete chain is ignored if the first Display object has its Execute
		 * parameter set to 0. Display objects which are animated or adjustable
		 * have their own show methods. Other Display objects are sent to the
		 * showDisplayOverlays() method which shows a single chain of
		 * JOIN-overlays.
		 */
		responseManager.clearTiming();
		boolean continueList = true;
		int di;
		for (di = 0; (di < sls) && continueList && !stopPresentation; di++) {
			dsp = (Display) showList.get(di);
			if (dsp.Execute.getFlag()) {
				if (dsp.isAnimated()) {
					continueList = showAnimatedDisplay(dsp);
				} else if (dsp.startAdjustable) {
					continueList = showAdjustableDisplay(dsp, theAdjustable);
				} else {
					if (di < (sls - 1)) {
						nextDsp = (Display) showList.get(di + 1);
						if (nextDsp.isGraphic()) {
						} else {
							nextDsp = null;
						}
					} else {
						nextDsp = null;
					}
					continueList = showDisplayOverlays(dsp, nextDsp);
				}
			} // Execute
		}
		if (stopPresentation)
			return DSPL_STOPPED;
		/*
		 * The remaining part only serves to fix the duration values for timing
		 * elements which have a CLOCK_TIMER. The fix is done by looking at the
		 * timing element which follows a CLOCK_TIMER timing element and use its
		 * time control data to compute the actual presentation duration of a
		 * CLOCK_TIMER element. This is not done for non-CLOCK_TIMER elements
		 * since in this case we do want to have the actual response time as the
		 * duration independent of waht happened on the screen.
		 */
		int iLimit = ((di < sls) ? di : sls) - 1;
		for (int i = 0; i < iLimit; i++) {
			dsp = (Display) showList.get(i);
			nextDsp = (Display) showList.get(i + 1);
			if (dsp.Execute.getFlag() && dsp.getVisible()
					&& nextDsp.Execute.getFlag() && nextDsp.getVisible()) {
				if (dsp.next != null) {
					do {
						dsp = dsp.next;
					} while (dsp.next != null);
				}
				if (nextDsp.next != null) {
					do {
						nextDsp = nextDsp.next;
					} while (nextDsp.next != null);
				}
				int tgn = dsp.getLastTimingGroupIndex();
				double[] tmerr = new double[tgn + 1];
				for (int groupIndex = 0; groupIndex <= tgn; groupIndex++) {
					timingElement = dsp.getTiming(groupIndex);
					int tt = timingElement.getTimerType();
					boolean stt = (tt == TimerCodes.CLOCK_TIMER)
							|| (tt == TimerCodes.RAW_CLOCK_TIMER)
							|| (tt == TimerCodes.VS_CLOCK_TIMER);
					TimingElement nextTimingElement = (groupIndex < tgn) ? dsp
							.getTiming(groupIndex + 1) : nextDsp.getTiming(0);
					if ((nextTimingElement != null) && stt) {
						long actualDuration = nextTimingElement
								.getTimeControl()
								- timingElement.getTimeControl();
						timingElement.setActualDuration(actualDuration);
						tmerr[groupIndex] = HiresClock.ms(
								timingElement.getIntendedDuration(),
								actualDuration);
					}
				}
				/*
				for (double d:tmerr)
				System.out.print(d+"\t");
				System.out.println();
				*/
				dsp.TimeError.set(tmerr);
			} // Execute
		}
		Debug.printTiming();
		return stopPresentation ? DSPL_STOPPED : DSPL_OK;
	}

	/**
	 * Show a Display object and its JOIN-overlays. The argument is a Display
	 * object which is not an overlay but may possibly have overlays linked to
	 * it via its next pointer.
	 * 
	 * @param dsp
	 *            the Display object which is to be shown.
	 * @param nextDsp
	 *            the Display object which follows the first argument Display
	 *            object in the DisplayList and is preloadable. This parameter
	 *            must be null if no image creation and preloading is possible.
	 * @return true if the current list should be continued. false if any
	 *         display in the current chain signals a break of the list via its
	 *         display list control method.
	 */
	private boolean showDisplayOverlays(Display dsp, Display nextDsp) {
		/*
		 * Find the Display object containing the preloadable image if possible.
		 * Only the last Display object in a JOIN-overlay chain has a
		 * preloadable image.
		 */
		if (dsp.getCanPreload()) {
			if (dsp.next != null) {
				do {
					dsp = dsp.next;
				} while (dsp.next != null);
			}
		}
		/*
		 * Also look at the next Display object's JOIN-overlay chain and find
		 * the Display containing the preloadable image if one exists.
		 */
		if ((nextDsp != null) && nextDsp.getCanPreload()) {
			if (nextDsp.next != null) {
				do {
					nextDsp = nextDsp.next;
				} while (nextDsp.next != null);
			}
		}
		/*
		 * At this point dsp is the Display object to be shown and may possibly
		 * have a preloadable image and nextDsp is non-null if it exists and has
		 * a preloadable image.
		 */
		/*
		 * This flag tells us whether a DisplayList control object has requested
		 * to stop display list presentation or not.
		 */
		boolean continueList = true;
		boolean execute;
		do {
			execute = true;
			// If dsp is a just-in-time Display object then compute its
			// properties now.
			if (dsp.JustInTime.getFlag()) {
				// System.out.println("PresentationManager.showDisplayList(): just-in-time-computing "
				// + dsp.getInstanceName());
				// if (dsp.next != null)
				// System.out.println("PresentationManager.showDisplayList():   and "
				// + dsp.next.getInstanceName());
				displayDevice.setActiveScreen(dsp.Screen.getInt());
				// System.out.println("PresentationManager.showDisplayOverlays(): recompute "
				// + dsp.getInstanceName());
				dsp.recompute(this);
				// We will not show JustInTime objects which have
				// their Execute flag set to 0.
				execute = dsp.Execute.getFlag();
				/*
				 * Display ndsp = dsp.next; while (ndsp != null) {
				 * System.out.println
				 * ("PresentationManager.showDisplayOverlays(): recompute " +
				 * ndsp.getInstanceName()); ndsp.recompute(this); ndsp =
				 * ndsp.next; }
				 */
			}
			// System.out.println("PresentationManager.showDisplayList(): execute = "
			// + execute);
			// If dsp is a DisplayList control object then execute its control
			// method
			if (dsp.getDisplayListControl()) {
				// Call its display list control method and record
				// the return state. The argument is a
				// ResponseController implementation
				continueList = continueList
						&& dsp.displayListControlState(responseManager);
			}
			/*
			 * The timing groups of the current Display objects are shown if
			 * this is a 'visible' Display object.
			 */
			if (/* execute && */dsp.getVisible()) {
				showDisplayElementGroups(dsp, nextDsp);
			}
			// Go to the next Display object in the chain if it exists
			dsp = dsp.next;
		} while (dsp != null && !stopPresentation);
		return continueList;
	}

	// ----------------------------------------------------------
	// These methods show single Display objects
	// ----------------------------------------------------------
	/**
	 * Run the currently active display as an animation. The duration of the
	 * animated display is controlled by the Display class's global timing
	 * parameters. The internal timing is determined by the timing parameters of
	 * the display object's timing groups as usual. Note, however, that the
	 * timing groups use only CLOCK_TIMERs as their timer types.
	 * 
	 * @return true.
	 */
	private boolean showAnimatedDisplay(Display dsp) {
		if (dsp.getAdjustable()) {
			// Get timing for the animation sequence
			TimingElement te = dsp.getDisplayTiming();
			te.setResponseSetExPar(ExPar.AdjustmentStopKey);
			// System.out.println("PresentationManager.showAnimatedDisplay(): TimingElement = "
			// + te.toString());
			// System.out.println("PresentationManager.showAnimatedDisplay(): Intended delay = "
			// + te.getIntendedDuration());
			// System.out.println("PresentationManager.showAnimatedDisplay(): Timer = "
			// + te.getTimerType());
			AdjustmentControl adc = new AdjustmentControl(dsp);
			responseManager.enableAdjustmentResponse(adc);
			// This starts and stops the animation player
			Debug.time("\n--- " + dsp.getInstanceName()
					+ "\nPrepare:                      ");
			showAndWait(te, true, dsp, null, null);
			dsp.timingGroupFinished(0);
			responseManager.disableAdjustmentResponse();
		} else {
			TimingElement te = dsp.getDisplayTiming();
			// This starts and stops the animation player
			Debug.time("\n--- " + dsp.getInstanceName()
					+ "\nPrepare:                      ");
			showAndWait(te, true, dsp, null, null);
			dsp.timingGroupFinished(0);
		}
		return true;
	}

	/**
	 * Repeatedly show the current Display object which contains an adjustible
	 * parameter and recompute the Display after each run. The Display object is
	 * shown and the adjustible parameter is modified until the stop key is
	 * pressed.
	 * 
	 * @return true if the display list should be continued or false if a
	 *         display list control method has requested to stop the display
	 *         list presentation.
	 */
	private boolean showAdjustableDisplay(Display dsp, Display adjustable) {
		AdjustmentControl adc = new AdjustmentControl(adjustable);
		responseManager.enableAdjustmentResponse(adc);
		boolean continueList = showDisplayOverlays(dsp, null);
		while (!stopPresentation && adc.doAdjust()) {
			adjustable.recomputeGeometry();
			adjustable.recomputeTiming();
			adjustable.recomputeColors();
			continueList = showDisplayOverlays(dsp, null);
		}
		responseManager.disableAdjustmentResponse();
		return continueList;
	}

	/**
	 * Show the timing groups of the given Display under exact timing control.
	 * This requires that the timing parameters for each step of the display
	 * have been defined and computed. This method is called by the
	 * showDisplayOverlays() method which itself is called by ExDesignProcessor
	 * objects. Thus this is the method which runs the currently installed
	 * Display on this PresentationManager object under experimental conditions.
	 * 
	 * @param dsp
	 *            the Display whose timing groups are to be shown.
	 * @param nextDsp
	 *            a Display object following the first argument Display object
	 *            in the DisplayList and is preloadable. This must be null if no
	 *            timing group image creation and preloading is possible.
	 */
	private void showDisplayElementGroups(Display dsp, Display nextDsp) {
		// System.out.println("PresentationManager.showDisplayElementGroups() of "
		// + dsp.getInstanceName());
		int tgn = dsp.getLastTimingGroupIndex();
		boolean canPreload = dsp.getCanPreload();
		String dbg = "";
		if (!canPreload)
			dsp.clearBoundingBox();
		dsp.clearTimingGroup();
		displayDevice.setActiveScreen(dsp.Screen.getInt());
		TimingElement timingElement, nextTimingElement;
		// Walk through the timing groups
		for (int groupIndex = 0; (groupIndex <= tgn) && !stopPresentation; groupIndex++) {
			// Get the current timing group
			timingElement = dsp.getTiming(groupIndex);
			// and get the next timing group if it exists
			nextTimingElement = (groupIndex < tgn) ? dsp
					.getTiming(groupIndex + 1) : null;
			// Remember whether this timing element has a preloadable image
			if (canPreload && (timingElement.getMemoryBufferImage() != null)) {
				dbg = "[M]";
			}
			// Set the Display object's currently active timing group
			dsp.nextTimingGroup();
			Debug.time("\n--- " + dsp.getInstanceName() + "[" + groupIndex
					+ "] " + dbg + "\nPrepare timing group:         ");
			// and show the timing group
			showAndWait(timingElement, false, dsp, nextDsp, nextTimingElement);
			Debug.time("Timing group finished:        ");
			dsp.timingGroupFinished(groupIndex);
		}
		// Remember the presentation times
		dsp.setTimeControl();
	}

	// ----------------------------------------------------------
	// This is the main timing method
	// ----------------------------------------------------------
	/**
	 * Shows the currently active timing group of display element objects of the
	 * given display object and then waits until a proper timing event stops
	 * display presentation. It calls the display object's
	 * showCurrentTimingGroup() method to show the current timing group or
	 * starts an animation player for the given display. After the timing group
	 * has been shown the ResponseManager is prepared for the proper timing
	 * event. Then this method tries to preload the next timing group's display
	 * image and corrects timing group duration for the preloading time. Finally
	 * this method calls the TimingMonitor class's waitForTimingEvent() method
	 * in order to wait until the timing group's timing interval has been
	 * finished. When coming back from the TimingMonitor object's wait the
	 * TimingMonitor is asked for the stop event's duration and code parameters
	 * and the display object's experimental parameters are set accordingly.
	 * 
	 * @param timingElement
	 *            the TimingElement which controls the currently active
	 *            DisplayElement group.
	 * @param animated
	 *            if true then this method starts the animation thread at the
	 *            beginning of the given time interval and stops it after the
	 *            requested duration has passed. If false then the currently
	 *            active DisplayElement group is painted.
	 * @param dsp
	 *            the Display object which contains the given timing group.
	 * @param nextDsp
	 *            the Display object which follows the previous argument in the
	 *            DisplayList and is preloadable. Must be null if no timing
	 *            group image preloading is possible.
	 * @param nextTimingElement
	 *            the timing element which follows the first argument in the
	 *            current Display objects's list of timing groups and has a
	 *            preloadable image. Must be null if no timing group image
	 *            preloading is possible.
	 */
	private void showAndWait(TimingElement timingElement, boolean animated,
			Display dsp, Display nextDsp, TimingElement nextTimingElement) {
		int r = 0;
		int timerType = timingElement.getTimerType();
		long duration = timingElement.getIntendedDuration();
		long duration_original = duration ;
		
		boolean useClockTimer = ((timerType & CLOCK_TIMER_BIT) != 0);
		boolean useResponseTimer = ((timerType & responseTimerMask) != 0);
		boolean useMediaTimer = ((timerType & mediaTimerMask) != 0);
		boolean useFixedDelayTimer = ((timerType & FIXED_DELAY_BIT) != 0);
		int[] responseSet = timingElement.getResponseSet();
		long startTime = 0L;
		long stopTime;
		boolean mouseResponse = false;
		int[] tePosition = null;
		/*
		 * Moved here in order to allow media timers like END_OF_MEDIA_TIMER to
		 * work properly.
		 */
		if (useResponseTimer || useMediaTimer) {
			responseManager.prepareEventTiming(dsp, timerType, responseSet);
		}
		if (animated) {
			startTime = startAnimationPlayer(dsp, timerType);
		} else {
			startTime = dsp.showCurrentTimingGroup();
		}
		
		timingElement.setTimeControl(startTime);
		Debug.timeMsg("Intended duration:            " + duration);
		if ((timerType & START_RESPONSE_TIMER_BIT) != 0) {
			responseIntervalStart = startTime;
			responseManager.setResponseIntervalStart(startTime);
		}
		if ((timerType & VOICE_KEY_TIMER_BIT) != 0) {
			responseManager.startVoiceKey(duration);
			responseManager.prepareEventTiming(dsp,
					TimerCodes.END_OF_MEDIA_TIMER, responseSet);
		}
		// Debug.time();
		if (dsp.next == null) {
			// System.out.println("PresentationManger.showAndWait() " +
			// dsp.getInstanceName());
			if (nextTimingElement != null) {
				// Preload image for next timing element
				// System.out.println("PresentationManger.showAndWait() preload image for "
				// + dsp.getInstanceName());
				dsp.preloadTimingGroupImage(nextTimingElement);
				duration -= (int) (HiresClock.getTimeNanos() - startTime);
				if ((nextTimingElement.getTimerType() & VIDEO_SYNCHRONIZATION_BIT) != 0)
					duration -= 4000000;
				if (duration < 0L)
					duration = 0L;
			} else if (nextDsp != null) {
				// Preload image for first timing element of next Display object
				nextTimingElement = nextDsp.getTiming(0);
				// System.out.println("PresentationManger.showAndWait() preload image for "
				// + nextDsp.getInstanceName());
				nextDsp.preloadTimingGroupImage(nextTimingElement);
				duration -= (int) (HiresClock.getTimeNanos() - startTime);
				if ((nextTimingElement != null)
						&& (nextTimingElement.getTimerType() & VIDEO_SYNCHRONIZATION_BIT) != 0)
					duration -= 4000000;
				if (duration < 0L)
					duration = 0L;
			}
		}
		// If no timer is defined then return immediately
		if ((timerType == TimerCodes.NO_TIMER) || useFixedDelayTimer) {
			return;
		}
		/*
		if (duration_original==60000000)
		System.out.println("Durata:"+duration);*/
		Debug.timeMsg("Fixed duration:               " + duration);
		// Debug.time("Effective wait: " + String.valueOf(duration) + " at ");
		timingMonitor.waitForTimingEvent(timerType, duration, useResponseTimer,
				responseManager);
		// responseManager.disableResponseTiming();
		// System.out.println("PresentationManager.showAndWait() timing disabled!");
		stopTime = timingMonitor.getTimingEventTime();
		// System.out.println("PresentationManager.showAndWait() " +
		// dsp.getInstanceName() + " startTime = " + startTime);
		// System.out.println("PresentationManager.showAndWait() " +
		// dsp.getInstanceName() + " stopTime = " + stopTime);
		// System.out.println("PresentationManager.showAndWait() intended duration = "
		// + duration);
		if (animated)
			// stopAnimation();
			stopAnimationPlayer();
		else
			dsp.clearCurrentTimingGroup();
		int teCode = timingMonitor.getTimingEventCode();
		mouseResponse = (timingMonitor.getTimingEventType() & MOUSE_BUTTON_TIMER_BIT) != 0;
		if (mouseResponse) {
			tePosition = DisplayElement.convertPoint(timingMonitor
					.getTimingEventPosition());
		}
		if ((timerType & STOP_RESPONSE_TIMER_BIT) != 0) {
			ExPar.ResponseTime.set(HiresClock.ms(stopTime
					- responseIntervalStart));
			ExPar.ResponseCode.set(teCode);
			if (mouseResponse)
				ExPar.ResponsePosition.set(tePosition);
		}
		if ((timerType & STORE_TIMER_BIT) != 0) {
			if ((timerType & SERIAL_LINE_TIMER_BIT) != 0) {
				timingElement.setResponseData(stopTime - startTime,
						timingMonitor.getTimingEventText());
			} else {
				if (mouseResponse) {
					timingElement.setResponseData(stopTime - startTime, teCode,
							tePosition);
				} else {
					timingElement.setResponseData(stopTime - startTime, teCode);
				}
			}
		}
		if ((timerType & WATCH_SPURIOUS_RESPONSES_BIT) != 0) {
			if (timingMonitor.hasSpuriousResponseEvent()) {
				mouseResponse = (timingMonitor.getSpuriousResponseEventType() & MOUSE_BUTTON_TIMER_BIT) != 0;
				if (mouseResponse) {
					tePosition = DisplayElement.convertPoint(timingMonitor
							.getSpuriousResponseEventPosition());
				}
				if (mouseResponse) {
					timingElement.setSpuriousResponseData(
							timingMonitor.getSpuriousResponseEventTime()
									- startTime,
							timingMonitor.getSpuriousResponseEventCode(),
							tePosition);
				} else {
					timingElement.setSpuriousResponseData(
							timingMonitor.getSpuriousResponseEventTime()
									- startTime,
							timingMonitor.getSpuriousResponseEventCode());
				}
				if ((timerType & STOP_RESPONSE_TIMER_BIT) != 0) {
					ExPar.ResponseTime.set(HiresClock.ms(timingMonitor
							.getSpuriousResponseEventTime()
							- responseIntervalStart));
					ExPar.ResponseCode.set(timingMonitor
							.getSpuriousResponseEventCode());
					if (mouseResponse)
						ExPar.ResponsePosition.set(tePosition);
				}
			}
		}
	}
	// ----------------------------------------------------------
	// The real time animation player for animated Display objects
	// ----------------------------------------------------------
	/** Animation displays run in their own private Thread. */
	private AnimationPlayer animationPlayer;

	/**
	 * Start the animation player for the given Display object. This creates an
	 * animation player and starts it for the given Display object. The
	 * animation player runs in its own thread at the lowest possible priority.
	 * 
	 * @return the nanosecond time when the player had been started.
	 */
	public long startAnimationPlayer(Display dsp, int timerType) {
		animationPlayer = new AnimationPlayer(dsp, displayDevice, timerType,
				responseManager);
		animationPlayer.setPlaying(true);
		animationPlayer.start();
		// System.out.println("PresentationManager.startAnimationPlayer() ...");
		return HiresClock.getTimeNanos();
	}

	/**
	 * Stop the animation player for the current Display object. This sends a
	 * signal to the animation player thread to stop cycling through the
	 * DisplayElement list of the current Display. The method waits until the
	 * animation player thread finishes and then returns.
	 */
	public void stopAnimationPlayer() {
		animationPlayer.setPlaying(false);
		if (animationPlayer != null) {
			try {
				animationPlayer.join();
			} catch (InterruptedException e) {
			}
			// System.out.println("PresentationManager.stopAnimationPlayer(): stopped");
			animationPlayer = null;
		}
	}
}
