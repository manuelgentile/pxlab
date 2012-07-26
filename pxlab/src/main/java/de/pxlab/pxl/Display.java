package de.pxlab.pxl;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import java.util.ArrayList;

/**
 * A stimulus in an experiment is composed of a sequence of Display objects.
 * Every Display object contains a list of DisplayElement objects which
 * constitute the basic geometric objects, like bars or disks of a complex
 * stimulus. The Display also has a list of experimental parameters which are
 * associated with it, like sizes or color values. The class Display is an
 * abstract class and does not have a constructor. Instances are created by
 * calling the constructor of a concrete subclass. The constructor of these
 * subclasses, however, do nothing but create the title and the parameters of
 * the Display. In order to create a concrete Display object the method
 * createInstance() is called which creates the DisplayElement list of the
 * Display and sets its color and timing properties. The display needs to know
 * the target component where it will be shown in order to adapt its size
 * parameters to the target panel size.
 * 
 * <p>
 * Here is a overview of how a Display object comes into existence. This
 * involves three steps:
 * 
 * <ol>
 * <li>Instantiation by calling the Display object's constructor or by calling
 * the static method load() of class DisplaySupport. This creates the Display's
 * name and its experimental parameters which are public fields of a display.
 * 
 * <li>A call of the method createInstance() sends a create() message to the
 * display object and generates its list of display elements. These do have
 * geometry types, colors, and timing properties but do not yet have their
 * geometric, color, and timing propertiese computed. createInstance() also
 * creates the experimental parameter descriptors which classify the parameters
 * of a Display into geometry, color, timing and other types. These
 * classifications are used by Display object editors. Experiments should call
 * the createInstance() method only once for each Display object.
 * 
 * <li>A call to the recompute(DisplayPanel) method finally computes the color,
 * geometry, and timing properties of the display for the given display panel
 * according to the current values of the Display object's experimental
 * parameters. This finally makes it possible to show the display. The various
 * recompute() methods may be called repeatedly. They must be called whenever a
 * Display object should be reinitialized to its current experimental parameter
 * values. Display objects have three major groups of properties: timing, color,
 * and geometry. Each of these has its own method for recomputing since they
 * will usually be modified independently.
 * 
 * <p>
 * The <code>recomputeGeometry(int width, int height)</code> method sets the
 * width and height parameters, sets the Display objects background field and
 * then delegates further computations to the <code>recomputeGeometry()</code>
 * method. The <code>recomputeGeometry()</code> method delegates its work to the
 * method <code>computeGeometry()</code> which must be defined in any subclass
 * of the class Display since it is defined abstract in class Display.
 * 
 * <p>
 * The method <code>recomputeColors()</code> delegates its work to the method
 * <code>computeColors()</code> which also must be defined in a subclass of the
 * class Display if the subclass object has dependent colors. These are colors
 * which are computed from other colors. An example for this are color mixtures
 * which depend on the mixture components.
 * 
 * <p>
 * The method <code>recomputeTiming()</code> computes the Display object's
 * timing properties from the display element groups and the timing parameters
 * defined in the <code>create()</code> method.
 * 
 * 
 * </ol>
 * 
 * <p>
 * The Display can be shown in several different modes:
 * <ul>
 * 
 * <li>Demo Mode: The display is shown without taking care of timing properties.
 * This uses show() to show this display.
 * 
 * <li>Experimental Display Mode: The display is shown with respect to its
 * timing groups. For each single timing step only those display elements are
 * shown which belong to the currently active timing group. A timing group is a
 * subset of a display's display elements which contains only elements with
 * identical timing properties. This uses showGroup() to show the currently
 * active timing group.
 * 
 * <li>Manual Stepping Mode: The display's timing groups are advanced manually.
 * This also uses showGroup() to show this display.
 * 
 * <li>Animation Mode: Displays which can do animation usually will have their
 * own methods to show a single animation frame. This uses showAnimationFrame()
 * to show a single frame.
 * 
 * </ul>
 * 
 * @author H. Irtel
 * @version 0.6.4
 */
/*
 * 01/23/01 added getFirstColorTableIndex() and getColorTableIndexLimit() fixed
 * null-pointer bug in destroyInstance() 01/26/01 removed ColorTable references
 * and use unified Timing ExPars 01/29/01 added instanceName parameter 04/13/01
 * added computeTiming() method for displays 05/06/01 added display list control
 * displays 05/17/01 changed order for recomputation, now: geometry, timing,
 * colors
 * 
 * 05/29/01 entering a DisplayElement to the list becomes group[0] by default.
 * This also affects the background. The background is still shown at every
 * timing group since it is identified by the backgroundFieldIndex.
 * 
 * 05/30/01 added the justInTime flag.
 * 
 * 06/13/01 the background field no longer belongs to any timing group. The
 * showGroup(Graphics) method now calls showGroup() which may be overridden by
 * subclasses.
 * 
 * 06/13/01 The show(Graphics) method now calls show() which may be overridden
 * by subclasses.
 * 
 * 06/27/01 use long for timing group patterns.
 * 
 * 07/11/01 added method timingGroupFinished(int group).
 * 
 * 12/13/01 changed pointerMoved() to pointerMoved() and pointerDragged()
 * 
 * 12/15/01 did some preparations for removing the default background object.
 * 
 * 01/09/02 added the Transparent parameter to suppress the background field
 * under real time display conditions.
 * 
 * 06/14/02 added DurationControl as a separate parameter for the actually
 * measured duration of Display objects controlled by clock timers.
 * 
 * 07/03/02 removed methods for handling spectral colors and created a subclass
 * for these.
 * 
 * 07/05/02 added showBackgroundElement()
 * 
 * 07/15/02 allow control of repaint() calls during pointer movement responses.
 * 
 * 07/18/02 added method setExParFields(...) to make this display a copy of
 * another display (er)
 * 
 * 08/07/02 renamed 'Transparent' to 'Overlay' which better describes what is
 * done.
 * 
 * 09/11/02 Fixed toString() for new instance naming schema
 * 
 * 11/12/02 gets the pointer event time for setPointerActivate- and
 * ReleaseState(int button, Point p, long t)
 * 
 * 06/27/03 added the boundingBox and its methods
 * 
 * 07/10/03 set doubleBuffered = true by default
 * 
 * 08/06/03 added the finished() method
 * 
 * 09/16/03 fixed bug in setExParFields(...)
 * 
 * 2004/12/13 made justInTime an experimental parameter
 * 
 * 2004/12/15 added hasNoTimer flag
 * 
 * 2005/04/15 implement image preloading at runtime
 * 
 * 2005/07/21 ResponsePosition added
 * 
 * 2005/12/08 moved code to inspect experimental parameters from class Display
 * into class DisplaySupport in order to use it for DataAnalysis objects
 * 
 * 2006/01/16 added getAllowTimerStop(). This method is called by the
 * ResponseManager immediately before stopping a timer. This allows the active
 * display to control whether it can be stopped or not.
 * 
 * 2007/05/16 moved setting the TimeControl parameter to the PresentationManager
 * in order to handle tracking timers properly
 * 
 * 2008/03/05 always disable preloading for objects which have a timing group
 * element which has the FIXED_DELAY_BIT set. Added showGroupDelayed() to handle
 * timers using the FIXED_DELAY_BIT.
 */
abstract public class Display extends DisplaySupport implements ExParTypeCodes,
		Topics, TimerBitCodes {
	protected static final int STEREO_NONE = 0;
	protected static final int STEREO_LEFT = 1;
	protected static final int STEREO_RIGHT = 2;
	/**
	 * This defines the timer which will be used to control the duration of this
	 * display. Note that the duration time interval begins when a display
	 * object is drawn and ends only when the next display object is drawn. If
	 * this paramer is NO_TIMER then this display object is drawn and then the
	 * next display object of the display list followes immediately.
	 */
	public ExPar Timer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant(
					"de.pxlab.pxl.TimerCodes.LIMITED_RESPONSE_TIMER"),
			"Display timer type");
	/**
	 * Contains the actual time when this display's timing groups have been
	 * shown. Is an array if the display object contains more than a single
	 * timing group. This parameter is only set after the display list which
	 * contains this display has been shown completely.
	 */
	public ExPar TimeControl = new ExPar(RTDATA, new ExParValue(0),
			"Display timing group show time");
	/** This is the intended duration of this display. */
	public ExPar Duration = new ExPar(DURATION, 0, 5000, new ExParValue(3000),
			"Intended display duration");
	/**
	 * Contains the timing error for every timing group of this Display object
	 * which has a CLOCK_TIMER. Is an array if the display object contains more
	 * than a single timing group. This parameter is only set after the display
	 * list which contains this display has been shown completely.
	 */
	public ExPar TimeError = new ExPar(RTDATA, new ExParValue(0),
			"Timing group duration errors");
	/**
	 * This parameter stores the actual display duration. For response driven
	 * timers this will also be the response time. For clock driven timers this
	 * parameter presents one method to control the timing precision since it
	 * contains the actually measured time interval duration.
	 */
	public ExPar ResponseTime = new ExPar(RTDATA, new ExParValue(0),
			"Actual display duration or response time");
	/**
	 * This is a code generated by the event which stopped the display time
	 * interval. For response driven timers this will be a code generated by the
	 * response device. For mouse buttons the code corresponds to the button
	 * numbers for keyboard keys the code corresponds to the unmodified key
	 * code. If the parameter ResponseSet is defined then the ResponseCode is
	 * translated into the index of the respective device code within the
	 * ResponseSet array.
	 */
	public ExPar ResponseCode = new ExPar(RTDATA, new ExParValue(0),
			"Duration time interval stop code");
	/**
	 * Character code for the response key which stopped the display time
	 * interval on keyboard responses. This code will be identical to the
	 * ResponseCode value in most cases. It will be different from the
	 * ResponseCode value if (a) the respective response key does not have a
	 * code but does have an associated character (this is the case for umlaut
	 * keys) or (b) a modifier like the Alt or Shift key is activated. Note that
	 * there also exist keys which do not have character codes like the Alt or
	 * Shift key. These keys have the character code
	 * java.awt.event.KeyEvent.CHAR_UNDEFIND.
	 * 
	 * @see java.awt.event.KeyEvent
	 */
	public ExPar ResponseChar = new ExPar(RTDATA, new ExParValue(0),
			"Character code of the response key");
	/**
	 * Contains the position of the mouse pointer when a mouse response has been
	 * detected. It contains the key location code in case of keyboard
	 * responses. This may be used to distinguish between keys which appear more
	 * than once on a keyboard like the left and right shift keys or the keys in
	 * the separate numeric keypad. See class java.awt.event.KeyEvent for the
	 * KEY_LOCATION codes.
	 * 
	 * @see java.awt.event.KeyEvent
	 */
	public ExPar ResponsePosition = new ExPar(RTDATA, new ExParValue(0, 0),
			"Mouse pointer position at response");
	/**
	 * The initial value of this parameter can be an array of key codes. In this
	 * case only response codes will be accepted which are contained in this
	 * array. The default value should be undefined otherwise all displays must
	 * use response code control since the ResponseEventGroup.getCode() method
	 * uses the undefined property to switch off code checking.
	 * 
	 * <p>
	 * Note that setting the parameter ResponseSet not only restricts the set of
	 * possible response keys but also enables a translation mechanism. This
	 * translates the response key codes to response code values which
	 * correspond to the position within the array ResponseSet. This means that
	 * if the response set is defined to be [37, 39] and the actual response key
	 * has the code 39 then the parameter ResponseCode is set to 1 which is the
	 * index of the code 39 in the array ResponseSet. The translation mechanism
	 * is not used if ResponseSet is not set.
	 */
	public ExPar ResponseSet = new ExPar(UNKNOWN, new ExParValueNotSet(),
			"Valid response codes");
	/**
	 * Screen selection code for the screen where this display object should be
	 * shown in multiple screen systems.
	 */
	public ExPar Screen = new ExPar(GEOMETRY_EDITOR,
			ScreenSelectionCodes.class, new ExParValue(
					ScreenSelectionCodes.NO_SELECTION),
			"Display screen selection");
	/**
	 * Controls whether this display object's background is drawn or not. If the
	 * flag is true then no background is shown under real time conditions.
	 * Otherwise the background is drawn whenever the display object is drawn.
	 * This does not affect the display drawing methods in editing and non real
	 * time modes.
	 */
	public ExPar Overlay = new ExPar(GEOMETRY_EDITOR, OverlayCodes.class,
			new ExParValueConstant("de.pxlab.pxl.OverlayCodes.NONE"),
			"Overlay code");
	/**
	 * Controls the point in time when this display object's properties are
	 * computed. If true then this is done immediately before the display object
	 * is shown. If this is false then most properties are computed before the
	 * display list is started. Use this flag only if necessary, since it may
	 * reduce timing precision. Setting this flag is necessary whenever a
	 * display parameter value depends on the current value of a parameter of a
	 * display contained in the same display list.
	 */
	public ExPar JustInTime = new ExPar(FLAG, new ExParValue(0),
			"Compute display properties as late as possible");
	/**
	 * Controls whether this display is executed within a display list or not.
	 * Set this flag to 0 to disable execution.
	 */
	public ExPar Execute = new ExPar(FLAG, new ExParValue(1),
			"Execution control flag");
	/**
	 * The PresentationManager object which controls the presentation of this
	 * Display. It is needed since some special Display objects need to access
	 * it. Examples are the DeviceControl class which opens special devices or
	 * some media Display objects like MediaPlayer, SoundFile, SyntheticSound,
	 * or VoiceKey which need to access the ResponseManager.
	 */
	protected PresentationManager presentationManager;
	/**
	 * The DisplayDevice object which is used to show this Display object. It is
	 * needed by Display objects like MediaPlayer or ScreenCapture to access
	 * some special properties if the display device.
	 */
	protected DisplayDevice displayDevice;
	/**
	 * This is the Component we are drawing on. Note that this is only rarely
	 * used. An example is class TextImage which needs a memory drawing buffer
	 * for this Component.
	 */
	protected Component displayComponent;
	/**
	 * This is the width of the component where this display will be shown. This
	 * is updated by calls to recomputeGeometry(int, int).
	 */
	protected int width;
	/**
	 * This is the height of the component where this display will be shown.
	 * This is updated by calls to recomputeGeometry(int, int).
	 */
	protected int height;
	/**
	 * Index of this display's background field in the list of display elements.
	 */
	// Don't change the initial value of -1, since this is used
	protected int backgroundFieldIndex = -1;
	/**
	 * This is the graphics context we will use to paint the display list of
	 * this display. This reference is updated by the methods
	 * setGraphicsContext(), show(Graphics), showGroup(Graphics), and
	 * showAnimation(Graphics, int).
	 */
	protected Graphics graphics;
	protected Graphics2D graphics2D;
	/**
	 * This flag activates display object repainting while the pointer is moved
	 * during the respective response types. It should only be switched on if
	 * the display must be updated while the response pointer is being moved.
	 */
	// protected boolean repaintOnPointerMovement = false;
	/**
	 * The display element list is the list of display elements contained in
	 * this display. It is protected since subclasses which have their own
	 * show() method want to access it.
	 */
	protected ArrayList displayElementList = new ArrayList();
	/**
	 * This is the index of the last timing group of display elements in the
	 * timing group list. This corresponds to the number of timing steps in this
	 * display.
	 */
	private int lastTimingGroupIndex;
	/**
	 * The list of timing elements of this display. This list has an entry for
	 * every timing group of this display.
	 */
	private ArrayList timingList = new ArrayList();
	/**
	 * This is the bounding box of this Display object AFTER it has been drawn!
	 */
	protected Rectangle boundingBox;
	/**
	 * If true then this Display will have its screen button Rectangle defined.
	 * This will result in a timer stop for mouse button events while a mouse
	 * tracking timer is active.
	 */
	private boolean hasScreenButton = false;
	/**
	 * A rectangle on the screen which can be used as a stop button for mouse
	 * tracking responses.
	 */
	protected Rectangle screenButton;

	/**
	 * Defines a rectangle on the screen which can be used as a stop button for
	 * mouse tracking responses.
	 */
	protected void setScreenButton(Rectangle b) {
		if (b != null) {
			screenButton = b;
			hasScreenButton = true;
		} else {
			hasScreenButton = false;
		}
	}

	protected Rectangle getScreenButton() {
		return screenButton;
	}

	/**
	 * Set the adjustable flag to indicate whether this display contains an
	 * adjustable element while a trial is run.
	 */
	public void setAdjustable(boolean a) {
		adjustable = a;
	}

	/**
	 * Returns true if this display contains an adjustable element while a trial
	 * is run.
	 */
	public boolean getAdjustable() {
		return (adjustable);
	}
	/**
	 * Run time flag to indicate that this display is an adjustable trial
	 * display. This flag is set when the runtime context is created.
	 */
	private boolean adjustable = false;
	/**
	 * This is true if this Display is the first of a possible sequence of
	 * Display objects which are overlays and which contain an adjustable
	 * Display object. This is necessary such that Display objects which are
	 * overlays may be adjustable.
	 */
	protected boolean startAdjustable = false;
	/** Experimental parameter which is adjustable/adaptive. */
	private ExPar dynExPar = null;

	/**
	 * Set the adaptive/adjustable experimental parameter. This parameter is
	 * marked by the adaptive/adjustable prefix in the parameter file and is set
	 * at runtime.
	 * 
	 * @param x
	 *            the experimental parameter which is to be set as the
	 *            adaptive/adjustable parameter of this display.
	 */
	public void setDynExPar(ExPar x) {
		// System.out.println("Display.setDynExPar() Setting dynamic parameter "
		// + x.toString());
		dynExPar = x;
	}

	/**
	 * Get the adaptive/adjustable experimental parameter.
	 * 
	 * @return the experimental parameter which is to be used as the
	 *         adaptive/adjustable parameter of this display.
	 */
	public ExPar getDynExPar() {
		return (dynExPar);
	}
	/**
	 * Flag to indicate that this is a visible display in the sense that its
	 * show() and showGroup() methods have to be called whenever the display is
	 * contained in a display list. If this flag is false then the display's
	 * recompute() method is called but its show() and showGroup() method is not
	 * called when running the display list.
	 */
	private boolean visible = true;

	/**
	 * Set the flag to indicate that this is a visible display. Invisible
	 * displays are recomputed while running a display list but they are not
	 * shown! This is used by Feedback or ResponseControl displays which can
	 * insert processing features into a display list.
	 * 
	 * @param v
	 *            if true then this display will be recomputed and shown, if
	 *            false then the display will be recomputed but will not be
	 *            shown.
	 */
	public void setVisible(boolean v) {
		visible = v;
	}

	/**
	 * Get the visible flag.
	 * 
	 * @return true if this display should be recomputed and shown and false if
	 *         the display should only be recomputed but not shown.
	 */
	public boolean getVisible() {
		return (visible);
	}
	/**
	 * Flag to indicate that this is a display list control display.
	 */
	private boolean displayListControl = false;

	/**
	 * Make this a display list control display. Display list control display
	 * objects have a method named displayListControlState() which is called
	 * immediately before the display is shown at runtime. If this method
	 * returns false then display list processing is stopped after this display
	 * has been shown. Showing the display may be prevented by calling
	 * setVisible(false) from within the displayListControlState() method. This
	 * makes it possible to check for certain events which should stop trial
	 * presentation. An example is a response error during an attention period.
	 */
	protected void setDisplayListControl() {
		displayListControl = true;
	}

	/** Get the display list control indicator flag. */
	public boolean getDisplayListControl() {
		return (displayListControl);
	}
	/**
	 * Activate repainting of the display during pointer movement. This method
	 * should be called by subclasses like tracking tasks, which have to repaint
	 * while the response pointer is being moved. protected void
	 * setRepaintOnPointerMovement(boolean p) { repaintOnPointerMovement = p; }
	 */
	/**
	 * A Display object which is painted transparently after each timing group
	 * of this Display object has been painted. This can work like an overlay
	 * for a complete display list. It is activated by setting the Overlay
	 * parameter to OverlayCodes.DISPLAY_LIST.
	 */
	private Display listOverlay = null;

	/** Set a permanent transparent overlay display for this Display object. */
	public void setListOverlay(Display ovrlay) {
		listOverlay = ovrlay;
	}

	public boolean isNoOverlay() {
		int v = Overlay.getInt();
		return v == OverlayCodes.NONE || v == OverlayCodes.CLEAR_DISPLAY_LIST;
	}

	public boolean isJoinOverlay() {
		return (Overlay.getInt() == OverlayCodes.JOIN);
	}

	public boolean isListOverlay() {
		return (Overlay.getInt() == OverlayCodes.DISPLAY_LIST);
	}

	public boolean isClearListOverlay() {
		return (Overlay.getInt() == OverlayCodes.CLEAR_DISPLAY_LIST);
	}

	public boolean isTransparentOverlay() {
		return (Overlay.getInt() == OverlayCodes.TRANSPARENT);
	}
	/**
	 * True if this Display object is followed by a TRANSPARENT overlay in a
	 * Display list.
	 */
	protected boolean transparentOverlayFollows;

	public void setTransparentOverlayFollows(boolean s) {
		transparentOverlayFollows = s;
	}

	public boolean getTransparentOverlayFollows() {
		return transparentOverlayFollows;
	}

	/**
	 * Return true if this display object requires graphic screen resources for
	 * being shown. This is not the case for acoustic and other non-graphic
	 * Display objects.
	 */
	public boolean isGraphic() {
		return true;
	}
	/**
	 * True if this Display object's timing groups may be preloaded as a memory
	 * image into the screen back buffer at runtime. This is set and used by
	 * PresentationManager.showDisplayList() and
	 * PresentationManager.showDisplayOverlays() when computing the Display
	 * objects of a DisplayList and showing Display objects. Currently only
	 * visible graphic objects may be preloaded. Display list control objects,
	 * animated and adjustable Display objects, objects which have JustInTime
	 * set, and objects which are not visible or non-Graphic are not
	 * preloadable.
	 */
	private boolean canPreload;

	/**
	 * Set this Display object's preload flag.
	 * 
	 * @param s
	 *            if true then this Display object can have an image which is
	 *            preloaded at runtime. If false then this Display must be
	 *            computed at runtime immediately before it is shown.
	 */
	public void setCanPreload(boolean s) {
		canPreload = s;
	}

	/**
	 * Get this Display object's preload flag.
	 * 
	 * @return true if this Display object can have an image which is preloaded
	 *         at runtime and false if this Display must be computed at runtime
	 *         immediately before it is shown.
	 */
	public boolean getCanPreload() {
		return canPreload;
	}
	/**
	 * A link to the next Display in a list which should be drawn simultanously
	 * because it is a JOIN overlay of this Display object. This link is
	 * set/used only during Display presentation.
	 */
	protected Display next = null;
	/**
	 * This flag is true if this Display currently is fully recomputed at the
	 * initial call of method recompute(Component). It indicates that all
	 * properties are recomputed. Display objects which partially recompute its
	 * properties may use this to decide which properties they actually have to
	 * recompute.
	 */
	private boolean fullRecompute;

	protected void setFullRecompute(boolean s) {
		fullRecompute = s;
	}

	protected boolean getFullRecompute() {
		return fullRecompute;
	}

	/**
	 * Get the flag which allows a timer to stop this display. Display objects
	 * may prevent timers to stop them if stopping requires a certain display
	 * state. An example being display objects which require activation of one
	 * or more control elements before they may be stopped.
	 * 
	 * @return true if it currently is possible to stop this display.
	 */
	public boolean getAllowTimerStop(int rc) {
		return true;
	}
	// -----------------------------------------------------------------
	// Creating an Instance, Initialization and Updating a Display
	// -----------------------------------------------------------------
	private int initialDisplayElementIndex;

	/**
	 * Create the display's list of display elements and timing groups. The
	 * createInstance() method is called only once for each instance of a
	 * display and it is called before the display is attached to any display
	 * panel. It calls the subclass's create() method for creating the list of
	 * display element objects and associating experimental parameters with
	 * colors, timing and geometric properties of the display. This method also
	 * inserts a background field as the first display element of each display
	 * element list. No experimental parameter values are actually used during
	 * creation. Only list entries are prepared and parameter names are
	 * associated.
	 */
	public void createInstance() {
		// System.out.println("createInstance " + title);
		// Make sure that the display and timing element lists are empty
		destroyInstance();
		// Create this Display object's ExPar descriptor array for
		// later use
		createExParFields();
		// Always enter the background field first
		enterBackgroundField();
		// Then ask the display for initialization. This enters the
		// display element types into the display list and associates
		// the display elements with their color indices. No size
		// dependent computations are done at this point.
		initialDisplayElementIndex = create();
	}

	/**
	 * Create the display element list and the timing element list for this
	 * display. This method is called only once per instance. Its task is to
	 * create the list of display and timing elements. Colors and timing
	 * properties are associated with their respective experimental parameter
	 * names here but are not yet initialized from the experimental parameter
	 * table. This is done later.
	 */
	abstract protected int create();

	public DisplayElement getInitialDisplayElement() {
		return (getDisplayElement(initialDisplayElementIndex));
	}

	/**
	 * Compute all display properties for the DisplayDevice of the given
	 * PresentationManager. This method is called by the PresentationManager's
	 * showDisplayList() method and from class EditablePresentationPanel after a
	 * new Display object has been set or after the panel has been resized.
	 * 
	 * @param presentationManager
	 *            the PresentationManager which runs this Display object. It is
	 *            needed here in order to get the drawing surface of its
	 *            DisplayDevice and for some Display objects which need to
	 *            access the PresentationManager directly like the DeviceControl
	 *            class.
	 */
	public void recompute(PresentationManager presentationManager) {
		this.presentationManager = presentationManager;
		displayDevice = presentationManager.getDisplayDevice();
		recompute(displayDevice.getComponent());
	}

	/**
	 * This computes all display properties which have associated experimental
	 * parameters from the current values in the experimental parameter table.
	 * This method is used to (re)compute a single instance of a display such
	 * that it may be used with different sets of parameter values. This method
	 * is called from the DisplaySelectionDialog class showSelectedDisplay()
	 * method.
	 * 
	 * @param displayComponent
	 *            is the Component we will be drawing on. It is needed since
	 *            some Display objects need to adjust their size according to
	 *            the Component size.
	 */
	public void recompute(Component displayComponent) {
		this.displayComponent = displayComponent;
		Dimension size = displayComponent.getSize();
		width = size.width;
		height = size.height;
		setFullRecompute(true);
		// This (re)computes the geometric properties of this display
		// according to the experimental parameter values.
		recomputeGeometry(width, height);
		// Then (re)compute the timing properties of this display
		recomputeTiming();
		// Now compute the dependent colors of this display if necessary
		recomputeColors();
		setFullRecompute(false);
	}

	/**
	 * Recompute the current display's display elements from their current
	 * experimental parameter values for a display panel of the given size. This
	 * method is called whenever a display object is created first and whenever
	 * the size of the display panel may have changed.
	 */
	public void recomputeGeometry(int w, int h) {
		// System.out.println("Display.recomputeGeometry(int, int)");
		width = w;
		height = h;
		if (backgroundFieldIndex >= 0) {
			computeBackgroundField();
		}
		// System.out.println("Computing shapes for size " + w + "x" + h);
		recomputeGeometry();
	}

	/**
	 * Recompute this display's display elements from the current experimental
	 * parameter values. This method may be called repeatedly during adjustable
	 * displays. It assumes that the display panel has not changed!
	 */
	public void recomputeGeometry() {
		// System.out.println("Display.recomputeGeometry()");
		computeGeometry();
	}

	/**
	 * A Display's computeGeometry() method is called whenever the size of the
	 * receiving display panel is initialized or has been changed, or a geometry
	 * parameter of the display has been adjusted. This method does not
	 * recompute the background.
	 */
	abstract protected void computeGeometry();

	protected void computeBackgroundField() {
		getDisplayElement(backgroundFieldIndex).setRect(-width / 2,
				-height / 2, width, height);
	}

	/**
	 * Recompute the dependent colors because one of the independent colors has
	 * been changed.
	 */
	public void recomputeColors() {
		computeColors();
	}

	/**
	 * A display's computeColors() method is called at display creation and
	 * whenever a color has been changed. Displays which have dependent colors
	 * must override this method in order to compute the dependent colors in the
	 * color table.
	 */
	protected void computeColors() {
	};

	/**
	 * Display objects which want to use their private background color
	 * parameter should use this method to set the background display element's
	 * color parameter.
	 */
	public void setBackgroundColorPar(ExPar p) {
		getDisplayElement(backgroundFieldIndex).setColorPar(p);
	}
	// -----------------------------------------------------------------
	// Timing properties
	// -----------------------------------------------------------------
	/**
	 * True if this display uses only NO_TIMER timers in its timing groups. This
	 * parameter is set by the recomputeTiming() method. If set then this
	 * display may be fused with subsequent displays at runtime.
	 */
	private boolean hasNoTimer;

	/**
	 * Get the TimingElement at the given index in the timingList.
	 */
	public TimingElement getTimingElement(int i) {
		return ((TimingElement) timingList.get(i));
	}

	/**
	 * Recompute the timing parameters from the experimental parameter values.
	 */
	public void recomputeTiming() {
		TimingElement tp;
		// Figure out how many timing groups this display has and
		// remember this for later.
		lastTimingGroupIndex = findLastTimingGroupIndex();
		int tls = timingList.size();
		if (tls == 0) {
			// There is no timing defined, this seems to be a demo display
			// So fill up the timing list for missing entries if necessary
			while (timingList.size() <= lastTimingGroupIndex) {
				timingList.add(new TimingElement());
			}
		} else if (tls != (lastTimingGroupIndex + 1)) {
			new InternalError("Number of Timing Groups ("
					+ (lastTimingGroupIndex + 1)
					+ ") and Timing Element List Size (" + tls + ") differ in "
					+ getClass().getName() + "!");
		}
		// Now call the display's computeTiming method.
		computeTiming();
		hasNoTimer = true;
		// Then get the timing parameter values from the experimental
		// parameter table.
		for (int i = 0; i <= lastTimingGroupIndex; i++) {
			tp = (TimingElement) timingList.get(i);
			tp.fixIntendedDuration();
			tp.setTimeControl(0L);
			tp.setMemoryBufferImage(null);
			tp.setImagePreloaded(false);
			int type = tp.getTimerType();
			hasNoTimer = hasNoTimer && (type == TimerCodes.NO_TIMER);
			// System.out.println(tp.toString());
			if ((type & (MOUSE_TRACKING_BIT | WHEEL_TRACKING_BIT
					| AXIS_TRACKING_BIT | FIXED_DELAY_BIT)) != 0) {
				setCanPreload(false);
			}
		}
	}

	/**
	 * A display's computeTiming() method is called at display creation and
	 * whenever a timing element has been changed.
	 */
	protected void computeTiming() {
	};

	/** Set this display's time control parameter from the timing element data. */
	public void setTimeControl() {
		int n = timingList.size();
		double[] tc = new double[n];
		for (int i = 0; i < n; i++) {
			tc[i] = HiresClock.ms(((TimingElement) timingList.get(i))
					.getTimeControl());
			// System.out.println("Display.setTimeControl() " +
			// getInstanceName() + " [" + i + "] = " + tc[i]);
		}
		TimeControl.set(tc);
	}

	/** Show this display's current timing parameters. */
	public void printTiming() {
		TimingElement tp;
		int n = timingList.size();
		for (int i = 0; i < n; i++) {
			tp = (TimingElement) timingList.get(i);
			System.out.println(tp.toString());
		}
	}
	private static final int MEDIA_TIMER_MASK = TimerBitCodes.SYNC_TO_MEDIA_TIMER_BIT
			| TimerBitCodes.END_OF_MEDIA_TIMER_BIT;

	/** Make sure that this object uses a MEDIA_TIMER. */
	protected void check4MediaTimer() {
		if ((Timer.getInt() & MEDIA_TIMER_MASK) == 0) {
			new ParameterValueError(
					"Media objects have to use a MEDIA_TIMER as their timer. "
							+ getInstanceName() + " does not!");
		}
	}

	// -----------------------------------------------------------------
	// Display list control methods
	// -----------------------------------------------------------------
	/**
	 * Compute the display list control state and send it to the display timer
	 * if necessary. This method must be overridden by control displays. This
	 * method is called before this display's list of display elements are
	 * shown. If the method returns true then this display's list of display
	 * elements is not shown by the display panel!
	 * 
	 * @param rc
	 *            the ResponseController object which controls response timing.
	 * @return true if the trial should procede as usual. Returns false if the
	 *         trial should be stopped. Note that in the latter case this
	 *         display will be shown if and only if its visible flag is set to
	 *         true.
	 */
	public boolean displayListControlState(ResponseController rc) {
		return true;
	}

	// -----------------------------------------------------------------
	// Destroying and Updating a Display
	// -----------------------------------------------------------------
	/**
	 * Destroy this instance of the display because the user has selected
	 * another one to view. Clear the color table if requested and also give the
	 * display a chance to do some cleaning up.
	 */
	public void destroyInstance() {
		// First give subclasses a chance to do their own cleanup.
		destroy();
		// Clear the list of parameters
		// parList.clear();
		// Clear the timing list
		timingList.clear();
		// Clear the display list
		displayElementList.clear();
	}

	/**
	 * Give the display instance a chance to do some cleaning up of resources.
	 * Subclasses may use this to do some special cleaning up.
	 */
	protected void destroy() {
	}

	// -----------------------------------------------------------------
	// The bounding box
	// -----------------------------------------------------------------
	/**
	 * Clear this Display object's bounding box. This method should be called by
	 * all methods which show the display object on the screen immediately
	 * before they start to show the object.
	 */
	protected void clearBoundingBox() {
		boundingBox = null;
	}

	/**
	 * Add the given rectangle to this Display's current bounding box.
	 * 
	 * @param b
	 *            a rectangle which should be added to the current bounding box.
	 */
	protected void updateBoundingBox(Rectangle b) {
		if (boundingBox == null) {
			boundingBox = b;
		} else {
			boundingBox = boundingBox.union(b);
		}
	}

	/**
	 * Return this Display object's bounding box which is only valid after the
	 * object has been shown on the screen. Note that each Display object
	 * contains a background element which, however, is NOT covered by its
	 * bounding box.
	 * 
	 * @return a recangle which covers all DisplayElement objects of this
	 *         Display. It is only valid after the Display object has been shown
	 *         on screen.
	 */
	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	// -----------------------------------------------------------------
	// Showing the Display
	// -----------------------------------------------------------------
	/**
	 * Set the display context for showing display elements. The graphics
	 * context in the argument must be valid.
	 */
	protected void setGraphicsContext(Graphics g) {
		graphics = g;
		graphics2D = (Graphics2D) graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		DisplayElement.setGraphicsContext(g, width, height);
		// DisplayElement.setGraphicsContext(g, width, height,
		// displayDevice.getWindow());
	}

	protected void disposeGraphicsContext() {
		// System.out.print("Display.disposeGraphicsContext()");
		if (graphics == null) {
			// System.out.println(" was null.");
		} else {
			graphics.dispose();
			// System.out.println(" done.");
		}
	}

	/**
	 * Draws the background if it has been defined and then draws all objects in
	 * the list of DisplayElements. This method ignores the timing group
	 * patterns. Drawing is delegated to the DisplayElement objects.
	 * 
	 * @param g
	 *            a currently valid Graphics context for painting.
	 */
	public void show(Graphics g) {
		// System.out.println("Display.show(Graphics)");
		// First set the current display context for all display
		// elements to be shown
		setGraphicsContext(g);
		// Draw background first
		if ((backgroundFieldIndex >= 0)) {
			// System.out.println("Display.show(Graphics) showing background");
			((DisplayElement) displayElementList.get(backgroundFieldIndex))
					.show();
		}
		show();
	}

	/**
	 * Draws all objects in the list of DisplayElements but the background.
	 * Drawing is delegated to the DisplayElement objects.
	 */
	public void show() {
		if (ExPar.Stereographic.getFlag()) {
			showStereo();
		} else {
			// show() ----------------------------------------------------------
			DisplayElement de;
			// System.out.println("Display.show()");
			// System.out.println(" width = " + width + ", height = " + height);
			clearBoundingBox();
			int n = displayElementList.size();
			for (int i = backgroundFieldIndex + 1; i < n; i++) {
				de = (DisplayElement) displayElementList.get(i);
				// System.out.println("showing DisplayElement type " +
				// de.getType());
				// System.out.println("      size: " + de.getSize());
				// System.out.println("  location: " + de.getLocation());
				// System.out.println("     color: " + de.getColorIndex());
				de.show();
				updateBoundingBox(de.getBounds());
			}
			// show() ----------------------------------------------------------
		}
	}

	protected void showStereo() {
		computeStereographicGeometry(STEREO_LEFT);
		graphics.translate(-width / 4, 0);
		{
			// show() ----------------------------------------------------------
			DisplayElement de;
			// System.out.println("Display.show()");
			// System.out.println(" width = " + width + ", height = " + height);
			clearBoundingBox();
			int n = displayElementList.size();
			for (int i = backgroundFieldIndex + 1; i < n; i++) {
				de = (DisplayElement) displayElementList.get(i);
				// System.out.println("showing DisplayElement type " +
				// de.getType());
				// System.out.println("      size: " + de.getSize());
				// System.out.println("  location: " + de.getLocation());
				// System.out.println("     color: " + de.getColorIndex());
				de.show();
				updateBoundingBox(de.getBounds());
			}
			// show() ----------------------------------------------------------
		}
		computeStereographicGeometry(STEREO_RIGHT);
		graphics.translate(width / 2, 0);
		{
			// show() ----------------------------------------------------------
			DisplayElement de;
			// System.out.println("Display.show()");
			// System.out.println(" width = " + width + ", height = " + height);
			clearBoundingBox();
			int n = displayElementList.size();
			for (int i = backgroundFieldIndex + 1; i < n; i++) {
				de = (DisplayElement) displayElementList.get(i);
				// System.out.println("showing DisplayElement type " +
				// de.getType());
				// System.out.println("      size: " + de.getSize());
				// System.out.println("  location: " + de.getLocation());
				// System.out.println("     color: " + de.getColorIndex());
				de.show();
				updateBoundingBox(de.getBounds());
			}
			// show() ----------------------------------------------------------
		}
		graphics.translate(-width / 4, 0);
	}

	// -----------------------------------------------------------------
	// Display Element Selection
	// -----------------------------------------------------------------
	/**
	 * Find the display element whose bounding rectangle contains the specified
	 * point in the display panel. Note that bounding rectangles are only
	 * defined <em>after</em> a display has been painted. This method tries to
	 * find the smallest display element which contains the given pixel
	 * position.
	 * 
	 * @param x
	 *            horizontal pixel position.
	 * @param y
	 *            vertical pixel position.
	 * @return the DisplayElement object at the given pixel position.
	 */
	public DisplayElement getDisplayElementAt(int x, int y) {
		Rectangle b;
		DisplayElement d;
		// Compensate for the shifted origin of the display panel
		x -= width / 2;
		y -= height / 2;
		// System.out.println("Mouse at (" + x + "," + y + ")");
		// We search from backwards since later shapes are painted on
		// top of earlier shapes if there is an overlap
		for (int i = displayElementList.size() - 1; i >= 0; i--) {
			d = (DisplayElement) displayElementList.get(i);
			b = d.getBounds();
			// System.out.println("Checking shape at index " + i);
			// System.out.println("Bounds are: " + b);
			// if (gc.hit(mr, b, false)) {
			if (b.contains(x, y)) {
				// System.out.println("Hit at index " + i);
				return (d);
			}
		}
		// No hit was found!
		return (null);
	}

	// -----------------------------------------------------------------
	// Display Element Groups for Timing
	// -----------------------------------------------------------------
	/**
	 * Allows the display manager to figure out whether this instance of a
	 * display has timing groups.
	 */
	public boolean canStep() {
		return (lastTimingGroupIndex >= 0);
	}

	/**
	 * Figure out the number of timing groups of this display. This method is
	 * called by recomputeTiming() when a display is created and is used only to
	 * set the global variable lastTimingGroupIndex. This variable thus contains
	 * information about the number of timing groups in a Display object.
	 */
	private int findLastTimingGroupIndex() {
		DisplayElement d;
		long fsp = 0L, sp;
		int i;
		for (i = 0; i < displayElementList.size(); i++) {
			d = (DisplayElement) displayElementList.get(i);
			sp = d.getTimingGroupPattern();
			if (sp > fsp)
				fsp = sp;
		}
		for (i = timingGroupLimit - 1; i >= 0; i--)
			if ((fsp & group[i]) != 0L)
				break;
		// System.out.println("lastGroupIndex = " + i);
		return (i);
	}
	/**
	 * Contains the currently active timing group pattern. Timing group patterns
	 * are binary bit patterns with a single bit set. The bit position indicates
	 * the sequential position of the timing group in the series of timing
	 * groups of a Display object. Display objects without timing groups always
	 * have a 0 timing group pattern.
	 */
	protected long activeTimingGroup = 0L;
	protected int activeTimingGroupIndex = 0;

	/**
	 * Move the timing group display state to the next group of display
	 * elements. This method is called whenever the next timing group of a
	 * display should be displayed. The next group may be the first in a
	 * sequence of timing groups. In this case the group display state is set to
	 * the first group. In all other cases the timing group pattern is advanced
	 * to the next group. This method should only be called either for the first
	 * step in a timing cycle or as long as the previous call has returned true.
	 * 
	 * @return true if the step we moved to actually exists as a display element
	 *         timing group
	 */
	public boolean nextTimingGroup() {
		// System.out.println("Display: nextGroup() for step " +
		// activeTimingGroup );
		boolean r;
		if (activeTimingGroup == 0L) {
			// This is the first call in the sequence
			activeTimingGroup = 1L;
			activeTimingGroupIndex = 0;
			r = true;
		} else if (activeTimingGroup >= group[lastTimingGroupIndex]) {
			// We already are at the last step
			activeTimingGroup = 0L;
			activeTimingGroupIndex = -1;
			r = false;
		} else {
			activeTimingGroup = (activeTimingGroup << 1);
			activeTimingGroupIndex++;
			r = true;
		}
		// System.out.println("  Next step is " + activeTimingGroup);
		// if (!r) System.out.println("  There is no other step.");
		// return true if the state we shifted to is not the last one.
		return (r);
	}

	/**
	 * Set the currently active timing group pattern to the i-th timing group of
	 * this Display.
	 */
	public void setTimingGroup(int i) {
		activeTimingGroup = getGroup(i);
		activeTimingGroupIndex = i;
	}

	/**
	 * Reset the active timing group pattern. This is called whenever the
	 * display is changed or when a Display's presentation starts in order to
	 * make sure that this display is in a definite timing display state.
	 */
	public void clearTimingGroup() {
		activeTimingGroup = 0L;
		activeTimingGroupIndex = -1;
		// System.out.println("Display: Stepping started.");
	}

	/**
	 * Return the timing group pattern of the last timing group of this display.
	 */
	public long getLastTimingGroup() {
		return (group[lastTimingGroupIndex]);
	}

	/** Return the index of the last timing group of this display. */
	public int getLastTimingGroupIndex() {
		return (lastTimingGroupIndex);
	}

	/** Return the number of timing groups of this display. */
	public int getTimingGroupCount() {
		return (lastTimingGroupIndex + 1);
	}

	/**
	 * Return the timing group pattern of the i-th display element of this
	 * display.
	 */
	protected long getTimingGroupPattern(int i) {
		return (((DisplayElement) displayElementList.get(i))
				.getTimingGroupPattern());
	}

	/** Get the timing list entry for the given step. */
	public TimingElement getActiveTimingElement() {
		return ((TimingElement) timingList.get(activeTimingGroupIndex));
	}

	/**
	 * Show the current timing group of display elements. If the timing group
	 * has its image preloaded into the back buffer then the only thing done is
	 * to switch on the back buffer. If the timing group has a memory image then
	 * the memory image is shown. If none of this is the case then the display
	 * elements are painted into the back buffer and then the back buffer is
	 * switched on.
	 * 
	 * <p>
	 * To be done: Implement TRANSPARENT overlays for JustInTime Display
	 * objects. This means that a JustInTime object has to store its image if it
	 * is followed by a TRANSPARENT Display. This display may then use the image
	 * to initialize its background before painting.
	 * 
	 * @return the nanosecond time when the timing group has been shown.
	 */
	public long showCurrentTimingGroup() {
		// System.out.println("Display.showCurrentTimingGroup(): " +
		// getInstanceName());
		TimingElement te = getActiveTimingElement();
		int timerType = te.getTimerType();
		boolean vsync = (timerType & TimerBitCodes.VIDEO_SYNCHRONIZATION_BIT) != 0;
		if (te.isImagePreloaded()) {
			// The image is already preloaded, we only have to switch buffers
			// System.out.println("Display.showCurrentTimingGroup(): show preloaded image for "
			// + getInstanceName());
			//System.out.println(vsync);
			
			if (vsync)
				VideoSystem.waitForBeginOfVerticalBlank();
			displayDevice.show();
			Debug.time("Preloaded image shown: -----> ");
		} else {
			
			Image mb = te.getMemoryBufferImage();
			if (mb != null) {
				// We get here for objects which can do preloading but
				// had not been preloaded, this may be the first
				// element in a DisplayList since these may be
				// preloadable but can't actually be preloaded.
				// System.out.println("Display.showCurrentTimingGroup(): show memory buffer for "
				// + getInstanceName());
				Graphics2D g = (Graphics2D) displayDevice.getGraphics();
				g.drawImage(mb, 0, 0, null);
				if (vsync)
					VideoSystem.waitForBeginOfVerticalBlank();
				displayDevice.show();
				Debug.time("Image buffer shown: --------> ");
				g.dispose();
			} else {
				// We get here for objects which can't do
				// preloading. These do not have an associated memory
				// buffer.
				// System.out.println("Display.showCurrentTimingGroup(): paint "
				// + getInstanceName());
				// Get new graphics context if this is not an overlay
				if ((timerType & TimerBitCodes.FIXED_DELAY_BIT) != 0) {
					showGroupDelayed(te);
				} else if (isTransparentOverlay()) {
					// non-preloadable TRANSPARENT overlays must be
					// treated special, since otherwise the
					// MOUSE_TRACKING_TIMER would not work for them
					// System.out.println("Display.showCurrentTimingGroup(): TRANSPARENT overlay");
					Graphics g = displayDevice.getComponent().getGraphics();
					setGraphicsContext(g);
					showGroup();
					Debug.time("Transparent overlay shown: -> ");
					g.dispose();
				} else {
					/**/
					if (!isJoinOverlay() && isGraphic()) {
						// System.out.println("Display.showCurrentTimingGroup(): no JOIN overlay");
						Graphics gr = displayDevice.getGraphics();
						showGroup(gr);
					} else {
						// System.out.println("Display.showCurrentTimingGroup(): JOIN overlay");
						if (Execute.getFlag()) {
							showGroup();
						}
						if (!isGraphic())
							Debug.time("Non-Graphic Display shown: -> ");
					}
					if (listOverlay != null)
						listOverlay.show();
					// Show page and dispose of the graphics context if no
					// overlay follows
					if (next == null) {
						// System.out.println("Display.showCurrentTimingGroup(): show page");
						if (vsync)
							VideoSystem.waitForBeginOfVerticalBlank();
						if (isGraphic()) {
							displayDevice.show();
							Debug.time("Display painted and shown: -> ");
							disposeGraphicsContext();
						}
					} else {
						// An overlay follows
						if (hasNoTimer) {
							// don't show the buffer now, first paint the next
							// overlay
						} else {
							// this is an error which is treated elsewhere
						}
					}
				}
			}
		}
		
		long tmc = HiresClock.getTimeNanos();
		// Moved to the PresentationManager in order to handle tracking timers
		// properly
		// te.setTimeControl(tmc);
		te.setMemoryBufferImage(null);
		return tmc;
	}

	/**
	 * Start a thread which will show the currently active timing group of this
	 * Display object delayed by this timing element's duration.
	 * 
	 * @param te
	 *            the timing element which controls the currently active timing
	 *            group.
	 */
	private void showGroupDelayed(TimingElement te) {
		DelayedShow delayedShow = new DelayedShow(this, displayDevice,
				HiresClock.getTimeNanos() + te.getIntendedDuration());
		delayedShow.start();
	}

	/**
	 * Tell the timing group whose image had been preloaded by this Display
	 * object that the background buffer had become invalid and thus it should
	 * behave as if it never had been preloaded. This is the case when a timing
	 * group is repainted outside of the PresentationManager in a timing group
	 * controlled by a STOP_KEY_TIMER.
	 */
	public void clearNextTimingGroupImagePreload() {
		// System.out.println("Display[" + getInstanceName() +
		// "].clearNextTimingGroupImagePreload()");
		((ExperimentalDisplayDevice) displayDevice).clearPreloadedTimingGroup();
	}

	/**
	 * Load the given timing group's image into the display device's back
	 * buffer.
	 * 
	 * @param te
	 *            the timing element whose image should be loaded. The timing
	 *            element is marked as having its image preloaded.
	 */
	public void preloadTimingGroupImage(TimingElement te) {
		// System.out.println("Display[" + getInstanceName() +
		// "].preloadTimingGroupImage()");
		if (te != null) {
			// System.out.println("Display.preloadTimingGroupImage(): TimingElement exists");
			Image mb = te.getMemoryBufferImage();
			if (mb != null) {
				// System.out.println("Display.preloadTimingGroupImage(): MemoryBufferImage exists");
				long t1 = 0L, t2;
				boolean dbg = Debug.isActive(Debug.TIMING);
				if (dbg)
					t1 = HiresClock.getTimeNanos();
				Graphics g = displayDevice.getGraphics();
				g.drawImage(mb, 0, 0, null);
				g.dispose();
				if (dbg) {
					t2 = HiresClock.getTimeNanos();
					String m = "Std. Image preloading time:   ";
					ImageCapabilities imc;
					if (mb instanceof VolatileImage) {
						imc = ((VolatileImage) mb).getCapabilities();
						if (imc.isAccelerated()) {
							m = "Acc. image preloading time:   ";
						}
					}
					Debug.timeMsg(m + HiresClock.ms(t1, t2));
				}
				te.setImagePreloaded(true);
				((ExperimentalDisplayDevice) displayDevice)
						.setPreloadedTimingGroup(te);
				// System.out.println("Display.preloadTimingGroupImage(): " +
				// ((Object)nextTimingElement));
			} else {
				// System.out.println("Display.preloadTimingGroupImage(): no MemoryBufferImage");
			}
		} else {
			// System.out.println("Display.preloadTimingGroupImage(): no TimingElement");
		}
	}

	/**
	 * Removes the objects which belong to the current timing group of the
	 * currently active activeDisplay. This is currently not used since every
	 * timing group starts with a clear screen object.
	 */
	public void clearCurrentTimingGroup() {
	}

	/**
	 * First shows the background element if necessary and then shows all
	 * objects belonging to the current timing group. The background element is
	 * not shown of this Display object has its Overlay flag set.
	 * 
	 * @param g
	 *            the Graphics context for painting.
	 */
	public void showGroup(Graphics g) {
		// First set the current display context for all display
		// elements to be shown
		// System.out.println("Display.showGroup(Graphics):" +
		// getClass().getName());
		setGraphicsContext(g);
		// Draw background without regard to its timing group pattern - it does
		// not have any
		showBackgroundElement();
		showGroup();
	}

	/**
	 * Shows the currently active timing group as it is defined by the current
	 * value of activeTimingGroup. Subclasses which want to modify the timing
	 * group display method should override this method. This method assumes
	 * that the graphics context has already been set and the background has
	 * been drawn.
	 */
	public void showGroup() {
		if (ExPar.Stereographic.getFlag()) {
			showGroupStereo();
		} else {
			// showGroup()
			// ---------------------------------------------------------------------
			// System.out.println("Display.showGroup()");
			DisplayElement de;
			long timingGroupPattern;
			int n = displayElementList.size();
			// Then draw the rest of the display list if it fits the pattern
			for (int i = 0; i < n; i++) {
				// System.out.println("Display.showGroup(): i=" + i);
				// System.out.println("Display.showGroup(): backgroundFieldIndex="
				// + backgroundFieldIndex);
				de = (DisplayElement) displayElementList.get(i);
				timingGroupPattern = de.getTimingGroupPattern();
				// System.out.println("Display.showGroup(): groupPattern=" +
				// groupPattern + ", state = " + activeTimingGroup);
				if ((timingGroupPattern & activeTimingGroup) != 0L) {
					de.show();
					updateBoundingBox(de.getBounds());
				}
			}
			// showGroup()
			// ---------------------------------------------------------------------
		}
	}

	protected void showGroupStereo() {
		computeStereographicGeometry(STEREO_LEFT);
		graphics.translate(-width / 4, 0);
		{
			// showGroup()
			// ---------------------------------------------------------------------
			// System.out.println("Display.showGroup()");
			DisplayElement de;
			long timingGroupPattern;
			int n = displayElementList.size();
			// Then draw the rest of the display list if it fits the pattern
			for (int i = 0; i < n; i++) {
				// System.out.println("Display.showGroup(): i=" + i);
				// System.out.println("Display.showGroup(): backgroundFieldIndex="
				// + backgroundFieldIndex);
				de = (DisplayElement) displayElementList.get(i);
				timingGroupPattern = de.getTimingGroupPattern();
				// System.out.println("Display.showGroup(): groupPattern=" +
				// groupPattern + ", state = " + activeTimingGroup);
				if ((timingGroupPattern & activeTimingGroup) != 0L) {
					de.show();
					updateBoundingBox(de.getBounds());
				}
			}
			// showGroup()
			// ---------------------------------------------------------------------
		}
		computeStereographicGeometry(STEREO_RIGHT);
		graphics.translate(width / 2, 0);
		{
			// showGroup()
			// ---------------------------------------------------------------------
			// System.out.println("Display.showGroup()");
			DisplayElement de;
			long timingGroupPattern;
			int n = displayElementList.size();
			// Then draw the rest of the display list if it fits the pattern
			for (int i = 0; i < n; i++) {
				// System.out.println("Display.showGroup(): i=" + i);
				// System.out.println("Display.showGroup(): backgroundFieldIndex="
				// + backgroundFieldIndex);
				de = (DisplayElement) displayElementList.get(i);
				timingGroupPattern = de.getTimingGroupPattern();
				// System.out.println("Display.showGroup(): groupPattern=" +
				// groupPattern + ", state = " + activeTimingGroup);
				if ((timingGroupPattern & activeTimingGroup) != 0L) {
					de.show();
					updateBoundingBox(de.getBounds());
				}
			}
			// showGroup()
			// ---------------------------------------------------------------------
		}
		graphics.translate(-width / 4, 0);
	}

	/**
	 * Show only the background element assuming that a proper graphics context
	 * is set. This method is needed for animations which have to show their
	 * background before starting since the animation itself does not show the
	 * background.
	 */
	protected void showBackgroundElement() {
		// System.out.println("Display.showBackgroundElement()");
		// Draw background without regard to its timing group pattern - it does
		// not have any
		if ((Overlay.getInt() == OverlayCodes.NONE)
				&& (backgroundFieldIndex >= 0)) {
			/*
			 * System.out.println(
			 * "Display.showBackgroundElement() showing background");
			 * System.out.println("Display.showBackgroundElement() color = " +
			 * ((DisplayElement)displayElementList.get(backgroundFieldIndex)).
			 * getColorPar().getPxlColor());
			 */
			((DisplayElement) displayElementList.get(backgroundFieldIndex))
					.show();
		}
	}

	/**
	 * This method is called when the display of the currently active timing
	 * group has been finished. It may be overridden by Display objects which
	 * need to do some clean up after a timing group has been finished.
	 * 
	 * @param group
	 *            the index of the timing group which has been run.
	 */
	protected void timingGroupFinished(int group) {
		Debug.show(Debug.EVENTS, this.toString() + " timing group " + group
				+ " finished with code " + ResponseCode.getString());
	}

	/**
	 * Called when the complete display presentation has been finished including
	 * adjustment methods. Currently only for adjustment displays: It is called
	 * when the stop key has been detected.
	 */
	protected void finished() {
		Debug.show(Debug.EVENTS, this.toString() + " finished.");
	}

	// -----------------------------------------------------------------
	// Some displays can show the spectrum of their illuminant and the
	// reflectance functions of their surfaces
	// -----------------------------------------------------------------
	/**
	 * Check whether this display can tell us its illuminant name and the name
	 * of the reflectance functions of its display elements.
	 * 
	 * @return true if the display has an illuminant and has reflectance
	 *         functions, false otherwise.
	 */
	public boolean canShowSpectralDistributions() {
		return (false);
	}

	/**
	 * Check whether this Display object has a spectral color distribution to
	 * show in a spectral color distribution panel.
	 * 
	 * @return true if the display has a spectral color distribution to show
	 *         such that the editor should open a spectral color distribution
	 *         panel for it.
	 */
	public boolean hasSpectralDistributions() {
		return false;
	}

	// -----------------------------------------------------------------
	// Stereographic Displays
	// -----------------------------------------------------------------
	/**
	 * Allows the display panel to figure out whether this instance of a display
	 * has two stereographic versions. Must be overridden by displays which are
	 * able to draw stereographic versions.
	 */
	public boolean isStereo() {
		return (false);
	}

	/**
	 * Compute the left/right eye stereographic modifications of the geometry of
	 * this display. Note that this method is called after the computeGeometry()
	 * method has already been called for this display.
	 * <P>
	 * Should be overriden by display objects which can show stereographic
	 * versions.
	 * 
	 * @param s
	 *            indicates which stereographic channel to compute. O means that
	 *            left and right eye get the same information while 1 creates
	 *            the left and 2 creates the right eye view.
	 */
	protected void computeStereographicGeometry(int s) {
		computeGeometry();
	}

	// -----------------------------------------------------------------
	// Animated Displays
	// -----------------------------------------------------------------
	/**
	 * Allows the display panel to figure out whether this instance of a display
	 * has an animated version. Must be overridden by displays which are
	 * animated.
	 * 
	 * <p>
	 * Note that animated Display objects must not use the parameters Timer,
	 * Duration, ResponseSet, ResponseTime, ResponseCode for its internal timing
	 * groups since these are used to describe the timing properties of the
	 * whole Display object. The RealTimeDisplayPanel's AnimationPlayer
	 * retrieves these parameter values from the Display class directly.
	 */
	public boolean isAnimated() {
		return (false);
	}

	/**
	 * This method is called by RealTimeDisplayPanel objects from within the
	 * AnimationPlayer's run() method whenever a new animation frame should be
	 * computed. The Display object should respond by computing its geometry and
	 * color properties for the given frame. The AnimationPlayer then shows the
	 * Display object using the showGroup() method for drawing Display objects
	 * with timing groups being observed.
	 */
	public void computeAnimationFrame(int frame) {
	}

	/**
	 * This method is called by the DisplayPanel class's AnimationPlayer object
	 * to show the given animation frame of this display in the given graphics
	 * context.
	 * <p>
	 * Note that this method is obsolete and should no longer be used.
	 */
	public void showAnimationFrame(Graphics g, int frame) {
		setGraphicsContext(g);
		showAnimationFrame(frame);
	}

	/**
	 * Animated displays have to override this in order to run frame depenendent
	 * animations.
	 * <p>
	 * Note that this method is obsolete and should no longer be used. It should
	 * be replaced by using computeAnimationFrame().
	 */
	public void showAnimationFrame(int n) {
		show();
	}
	/** Number of animation frames per single display cycle. */
	protected int framesPerCycle;
	/** Increment for counting animation frames. */
	protected int frameIncrement = 1;

	/** Time delay between successive animation frames. */
	// protected int frameDelay = 20;
	/**
	 * Set the number of frames which should be computed and displayed for a
	 * single animation cycle.
	 */
	public void setFramesPerCycle(int fpc) {
		framesPerCycle = fpc;
	}

	/** Return the number of animation frames per display cycle. */
	public int getFramesPerCycle() {
		return (framesPerCycle);
	}

	/**
	 * Set the number of frames which should be computed and displayed for a
	 * single motion cycle.
	 */
	public void setFrameIncrement(int f) {
		frameIncrement = f;
	}

	/**
	 * Get the number of frames which should be computed and displayed for a
	 * single motion cycle.
	 */
	public int getFrameIncrement() {
		return (frameIncrement);
	}

	// -----------------------------------------------------------------
	// Access Methods for Display Elements
	// -----------------------------------------------------------------
	/**
	 * Return the index of the next display element entered into the display
	 * list.
	 */
	protected int nextDisplayElementIndex() {
		return (displayElementList.size());
	}

	/** Return the number of display elements in this display. */
	protected int displayElementCount() {
		return (displayElementList.size());
	}

	/** Returns the display's list of display elements. */
	public ArrayList getDisplayElementList() {
		return (displayElementList);
	}

	/**
	 * Enter the default background field into the display element list. Its
	 * color is defined by the global parameter ScreenBackgroundColor. The
	 * background field does not belong to any timing group.
	 */
	protected int enterBackgroundField() {
		backgroundFieldIndex = displayElementList.size();
		displayElementList.add(new Bar(ExPar.ScreenBackgroundColor));
		return (backgroundFieldIndex);
	}

	/**
	 * Enter the given DisplayElement object into the display's list of
	 * DisplayElements. Returns the index of this object in the list.
	 */
	protected int enterDisplayElement(DisplayElement p) {
		int i = displayElementList.size();
		p.setTimingGroupPattern(group[0]);
		displayElementList.add(p);
		// System.out.println("Entered Shape at index " + i);
		return (i);
	}

	/**
	 * Enter the given DisplayElement object into the display's list of
	 * DisplayElements and also define this element's stepping pattern. Returns
	 * the index of this object in the list.
	 */
	protected int enterDisplayElement(DisplayElement p, long s) {
		int i = displayElementList.size();
		p.setTimingGroupPattern(s);
		displayElementList.add(p);
		// System.out.println("Entered Shape at index " + i);
		return (i);
	}

	/**
	 * Get the DisplayElement at the given index in the displayElementList.
	 */
	public DisplayElement getDisplayElement(int i) {
		return ((DisplayElement) displayElementList.get(i));
	}

	/**
	 * Get the index of the given DisplayElement in the displayElementList.
	 */
	public int getIndexOf(DisplayElement d) {
		return (displayElementList.indexOf(d));
	}

	/**
	 * Return the color index of the i-th DisplayElement of this display.
	 */
	protected ExPar getColorParOfDisplayElement(int i) {
		return (((DisplayElement) displayElementList.get(i)).getColorPar());
	}

	/**
	 * Remove all display elements starting from the given index first from the
	 * display's list of DisplayElements.
	 * 
	 * @param first
	 *            index of first element to remove.
	 */
	protected void removeDisplayElements(int first) {
		for (int i = displayElementList.size() - 1; i >= first; i--)
			displayElementList.remove(i);
	}

	/**
	 * Remove all display with index values from first to last (inclusive) from
	 * the display's list of DisplayElements.
	 * 
	 * @param first
	 *            index of first element to remove.
	 * @param last
	 *            index of last element to remove.
	 */
	protected void removeDisplayElements(int first, int last) {
		if (last >= displayElementList.size())
			last = displayElementList.size() - 1;
		for (int i = last; i >= first; i--)
			displayElementList.remove(i);
	}

	// -----------------------------------------------------------------
	// Access Methods for Timing Elements
	// -----------------------------------------------------------------
	/**
	 * Enter the given experimental parameter name into the timing list for this
	 * display.
	 * 
	 * <p>
	 * Every display element of a display list belongs at least to one timing
	 * group. A timing group is a group of display elements which always are
	 * shown simultanously. The stepping display method shows the timing groups
	 * in sequence. A timing controlled display method also shows the display
	 * list by its timing groups. Each timing group has associated timing
	 * parameters which controll when this group is switched on. The timing
	 * parameter of a display element group defines the onset time difference
	 * between this group and another group, usually the group immediately shown
	 * before.
	 * 
	 * @param timing
	 *            the experimental parameter which defines the given timing
	 *            group's timing type.
	 * @param duration
	 *            the duration of the given timing group's timing interval.
	 * @param refIndex
	 *            the index in the timing list of that timing group to which the
	 *            given timing parameter refers to as its starting time point
	 *            for time measurement.
	 * @return the index in the timing list where the experimental parameter was
	 *         entered.
	 */
	public int enterTiming(ExPar timing, ExPar duration, int refIndex) {
		int i = timingList.size();
		timingList.add(new TimingElement(timing, duration, refIndex));
		return (i);
	}

	public int enterTiming(ExPar timing, ExPar duration, int refIndex,
			ExPar rtime, ExPar rcode) {
		int i = timingList.size();
		timingList.add(new TimingElement(timing, duration, this.ResponseSet,
				refIndex, rtime, rcode, this.ResponsePosition));
		return (i);
	}

	public int enterTiming(ExPar timing, ExPar duration, ExPar responseSet,
			int refIndex, ExPar rtime, ExPar rcode) {
		int i = timingList.size();
		timingList.add(new TimingElement(timing, duration, responseSet,
				refIndex, rtime, rcode, this.ResponsePosition));
		return (i);
	}

	public int enterTiming(ExPar timing, ExPar duration, int refIndex,
			ExPar rtime, ExPar rcode, ExPar rpos) {
		int i = timingList.size();
		timingList.add(new TimingElement(timing, duration, this.ResponseSet,
				refIndex, rtime, rcode, rpos));
		return (i);
	}

	public int enterTiming(ExPar timing, ExPar duration, ExPar responseSet,
			int refIndex, ExPar rtime, ExPar rcode, ExPar rpos) {
		int i = timingList.size();
		timingList.add(new TimingElement(timing, duration, responseSet,
				refIndex, rtime, rcode, rpos));
		return (i);
	}

	public int defaultTiming(int refIndex) {
		return (enterTiming(this.Timer, this.Duration, this.ResponseSet,
				refIndex, this.ResponseTime, this.ResponseCode,
				this.ResponsePosition));
	}

	public int defaultTiming() {
		return (enterTiming(this.Timer, this.Duration, this.ResponseSet, 0,
				this.ResponseTime, this.ResponseCode, this.ResponsePosition));
	}

	/**
	 * Return the index of the next timing element entered into the timing list.
	 */
	protected int nextTimingElementIndex() {
		return (timingList.size());
	}

	/** Get the timing list entry for the given step. */
	public TimingElement getTiming(int i) {
		// System.out.println("Display.getTiming() for " + getInstanceName() +
		// " group " + i);
		// System.out.println("Display.getTiming() list size is " +
		// timingList.size());
		return (TimingElement) ((timingList.size() > i) ? timingList.get(i)
				: null);
	}

	/**
	 * Remove all timing elements with index values starting from first from the
	 * display's list of TimingElements.
	 * 
	 * @param first
	 *            index of first element to remove.
	 */
	protected void removeTimingElements(int first) {
		removeTimingElements(first, timingList.size() - 1);
	}

	/**
	 * Remove all timing elements with index values from first to last
	 * (inclusive) from the display's list of TimingElements.
	 * 
	 * @param first
	 *            index of first element to remove.
	 * @param last
	 *            index of last element to remove.
	 */
	protected void removeTimingElements(int first, int last) {
		if (last >= timingList.size())
			last = timingList.size() - 1;
		for (int i = last; i >= first; i--)
			timingList.remove(i);
	}

	/**
	 * Get a valid timing element which is made up from the timing parameters
	 * Timer, Duration, ResponseSet, ResponseTime, ResponseCode of this class.
	 * This method is used to define the timing of Display objects which are
	 * shown repeatedly like those which are shown by an animation player. In
	 * this case the timing groups define the timing within the display of a
	 * single cycle but this timing element defines the timing of the animation
	 * sequence.
	 * 
	 * @return a TimingElement object which may be used to define the Display
	 *         object's timing without using the Display's timing groups.
	 */
	public TimingElement getDisplayTiming() {
		TimingElement te = new TimingElement(Timer, Duration, ResponseSet, 0,
				ResponseTime, ResponseCode, ResponsePosition);
		te.fixIntendedDuration();
		return te;
	}
	// -----------------------------------------------------------------
	// Pointer Interaction Support
	// -----------------------------------------------------------------
	protected int pointerActivationButton, pointerActivationX,
			pointerActivationY, pointerReleaseButton, pointerReleaseX,
			pointerReleaseY, pointerCurrentButton, pointerCurrentX,
			pointerCurrentY;
	protected ResponseEvent pointerResponseEvent;
	protected boolean pointerActive = false;
	protected long pointerEventTime;

	/**
	 * This defines the state of the pointer when it was last activated. This
	 * corresponds to the position when the mouse button was last pressed.
	 * 
	 * @return true if the Display object should be redrawn and false if not.
	 */
	public boolean setPointerActivationState(ResponseEvent e, long t) {
		pointerResponseEvent = e;
		pointerActivationButton = e.getCode();
		Point p = e.getLocation();
		pointerActivationX = p.x - width / 2;
		pointerActivationY = p.y - height / 2;
		pointerActive = true;
		pointerEventTime = t;
		Debug.show(Debug.EVENTS, "Display.setPointerActivationState() b = "
				+ pointerActivationButton + " at (" + pointerActivationX + ", "
				+ pointerActivationY + ")");
		boolean r = false;
		if (hasScreenButton
				&& screenButton
						.contains(pointerActivationX, pointerActivationY)) {
			e.setCode(KeyCodes.SCREEN_BUTTON);
			Debug.show(Debug.EVENTS,
					"Display.setPointerActivationState(): Screen button activated.");
			screenButtonPressed();
		} else {
			r = pointerActivated();
		}
		return r;
	}

	public boolean setPointerReleaseState(ResponseEvent e, long t) {
		pointerResponseEvent = e;
		pointerReleaseButton = e.getCode();
		Point p = e.getLocation();
		pointerReleaseX = p.x - width / 2;
		pointerReleaseY = p.y - height / 2;
		pointerActive = false;
		pointerEventTime = t;
		Debug.show(Debug.EVENTS, "Display.setPointerReleaseState() b = "
				+ pointerReleaseButton + " at (" + pointerReleaseX + ", "
				+ pointerReleaseY + ")");
		boolean r = false;
		if (hasScreenButton
				&& screenButton.contains(pointerReleaseX, pointerReleaseY)) {
			e.setCode(KeyCodes.SCREEN_BUTTON);
			Debug.show(Debug.EVENTS,
					"Display.setPointerReleaseState(): Screen button activated.");
			screenButtonReleased();
		} else {
			r = pointerReleased();
		}
		return r;
	}

	/**
	 * This defines the current state of the pointer device if it has bee
	 * changed by dragging the pointer with button down.
	 */
	public boolean setPointerDraggedState(ResponseEvent e) {
		pointerResponseEvent = e;
		pointerCurrentButton = e.getCode();
		Point p = e.getLocation();
		pointerCurrentX = p.x - width / 2;
		pointerCurrentY = p.y - height / 2;
		Debug.show(Debug.EVENTS, "Display.setPointerDraggedState() b = "
				+ pointerCurrentButton + " at (" + pointerCurrentX + ", "
				+ pointerCurrentY + ")");
		return pointerDragged();
	}

	/**
	 * This defines the current state of the pointer device if it has bee
	 * changed by moving the pointer with button up.
	 */
	public boolean setPointerMovedState(ResponseEvent e) {
		pointerResponseEvent = e;
		pointerCurrentButton = e.getCode();
		Point p = e.getLocation();
		pointerCurrentX = p.x - width / 2;
		pointerCurrentY = p.y - height / 2;
		Debug.show(Debug.EVENTS, "Display.setPointerMovedState() b = "
				+ pointerCurrentButton + " at (" + pointerCurrentX + ", "
				+ pointerCurrentY + ")");
		return pointerMoved();
	}

	/**
	 * This should be overridden by subclasses if they want to intercept pointer
	 * activation responses.
	 */
	protected boolean pointerActivated() {
		return false;
	}

	/**
	 * This should be overridden by subclasses if they want to intercept pointer
	 * dragging responses.
	 */
	protected boolean pointerDragged() {
		return false;
	}

	/**
	 * This should be overridden by subclasses if they want to intercept pointer
	 * motion responses.
	 */
	protected boolean pointerMoved() {
		return false;
	}

	/**
	 * This should be overridden by subclasses if they want to intercept pointer
	 * release responses.
	 */
	protected boolean pointerReleased() {
		return false;
	}

	/**
	 * Signal the Display object that the mouse pointer has been pressed inside
	 * the screen button area.
	 */
	protected void screenButtonPressed() {
	}

	/**
	 * Signal the Display object that the mouse pointer has been released inside
	 * the screen button area.
	 */
	protected void screenButtonReleased() {
	}

	/**
	 * Signals the display object that a response key has been pressed which was
	 * not identical to the StopKey.
	 * 
	 * @return true if the current timing group should be shown again and false
	 *         if not.
	 */
	public boolean setKeyResponse(KeyEvent keyEvent) {
		Debug.show(Debug.EVENTS,
				"Display.setKeyResponse() code = " + keyEvent.getKeyCode());
		return keyResponse(keyEvent);
	}

	/**
	 * This method is here to be overridden by display objects which want to
	 * respond to single key strokes which do NOT stop the display timer. The
	 * display object may change its appearance and the currently active timing
	 * group will be redrawn afterwards.
	 * 
	 * @return true if the current timing group should be shown again and false
	 *         if not.
	 */
	protected boolean keyResponse(KeyEvent keyEvent) {
		computeGeometry();
		return true;
	}
	protected double[] axisDeltas;
	protected boolean[] polledButtonStates;
	protected int changedPolledButtonIndex;

	public boolean setAxisDeltas(double[] d) {
		axisDeltas = d;
		return axisDeltasChanged();
	}

	public boolean setPolledButtonStates(int cbi, boolean[] b) {
		changedPolledButtonIndex = cbi;
		polledButtonStates = b;
		return buttonsPolled();
	}

	/**
	 * This should be overridden by subclasses if they want to intercept polled
	 * device axis responses.
	 */
	protected boolean axisDeltasChanged() {
		return false;
	}

	/**
	 * This should be overridden by subclasses if they want to intercept polled
	 * button responses.
	 */
	protected boolean buttonsPolled() {
		return false;
	}

	public String toString() {
		return (instanceName != null) ? instanceName : getClass().getName();
	}
}
