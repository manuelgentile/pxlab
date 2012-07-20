package de.pxlab.pxl;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
 * A ExperimentalDisplayDevice object contains one or more screens which can be
 * used to show visual stimuli. Since mouse input always is bound to windows the
 * device also is the virtual source of mouse input events. The device also
 * supports the conversion of device independent color coordinates into device
 * dependent coordinates which can be used to show proper colors an the device
 * screen(s).
 * 
 * <p>
 * The default device has a single display screen which may either be the
 * primary or the secondary display screen of a multiple display screen system.
 * If the device is opened in single screen mode then no screen selection
 * methods are needed.
 * 
 * <p>
 * On dual screen systems with both screens being activated we need screen
 * selection methods for drawing. PRIMARY_SCREEN and SECONDARY_SCREEN should be
 * used in thodes cases. In stereoskopic display systems the left screen should
 * be the primary and the right screen should be the secondary screen.
 * 
 * <p>
 * Display device features:
 * 
 * <ul>
 * <li>Provide one or two display screens.
 * 
 * <li>Collect mouse responses and send these to a mouse listener.
 * 
 * <li>Convert device independent color coordinates to device coordinates or
 * support such a conversion for every active screen.
 * 
 * <li>Allow for active screen selection
 * 
 * <li>Provide a drawing context for the currently active screen.
 * 
 * </ul>
 * 
 * @version 0.1.7
 */
/*
 * 
 * 2004/11/04
 * 
 * 2005/02/18 tell Base the size of an open screen device.
 * 
 * 2005/04/08 allow display mode changes
 * 
 * 2005/08/02 switch color transform only if the active screen really changes
 * 
 * 2006/10/31 set DeviceWhitePoint
 * 
 * 2006/12/29 FULL_SCREEN_FRAME
 * 
 * 2007/09/23 allow PxlColor as a calibration target color
 */
public class ExperimentalDisplayDevice implements DisplayDevice,
		CalibrationTarget, CalibrationChannelBitCodes {
	/**
	 * The active screen is that screen where drawing operations are executed.
	 */
	private int activeScreen;
	/**
	 * The display device mode which is used by this ExperimentalDisplayDevice
	 * object. The mode may be modified by the constructor if the requested mode
	 * is not available.
	 */
	private int displayDeviceType;
	/**
	 * The left and right graphics devices of the system graphics environment.
	 * These are created during instantiation.
	 */
	private GraphicsDevice leftDevice, rightDevice;
	private DisplayMode leftDeviceDefaultMode, rightDeviceDefaultMode;
	/**
	 * The left and right windows. These are used as containers for the drawing
	 * canvases. These are created during instantiation. Note that a framed
	 * display device actually uses only the Window properties of the Frame.
	 */
	private Window leftWindow, rightWindow;
	/**
	 * This mode indicates that this ExperimentalDisplayDevice is embedded into
	 * another Container.
	 */
	private static final int EMBEDDED = 99;
	private static final long TIME_LIMIT_4_ACCELERATION = 40000000L;
	/**
	 * If this ExperimentalDisplayDevice is embedded into another Container then
	 * it uses a special Canvas as its drawing surface.
	 */
	private EmbeddedCanvas embeddedCanvas;
	/** Left and right buffer strategies. Created during instantiation. */
	private BufferStrategy leftBufferStrategy, rightBufferStrategy;
	/** Left and right screen color correction transform. */
	private ColorDeviceTransform leftColorDeviceTransform,
			rightColorDeviceTransform;
	private MouseListener mouseListener;
	private MouseMotionListener mouseMotionListener;
	private MouseWheelListener mouseWheelListener;
	private boolean useUndecoratedFrames = true;

	// private Container externalContainer = null;
	/**
	 * Create an experimental display. The display can contain up to two screens
	 * for dual screen applications. Framed and full screen modes are supported
	 * and up to two display screens are possible. The display device also
	 * supports a mouse input listener for handling mouse events.
	 * 
	 * @param owner
	 *            the owner Frame for this display device's windows. This Frame
	 *            is only needed as an owner to all windows created by the
	 *            ExperimentalDisplayDevice object.
	 * @param mouseListener
	 *            the object which handles subject responses with the mouse
	 *            buttons.
	 * @param mouseMotionListener
	 *            the object which handles subject responses with mouse
	 *            movement.
	 * @param mouseWheelListener
	 *            the object which handles subject responses with a mouse wheel.
	 */
	public ExperimentalDisplayDevice(Frame owner, MouseListener mouseListener,
			MouseMotionListener mouseMotionListener,
			MouseWheelListener mouseWheelListener) {
		this(owner, null, mouseListener, mouseMotionListener,
				mouseWheelListener);
	}

	/**
	 * Create an experimental display. The display can contain up to two screens
	 * for dual screen applications. Framed and full screen modes are supported
	 * and up to two display screens are possible. The display device also
	 * supports a mouse input listener for handling mouse events.
	 * 
	 * @param owner
	 *            the owner Frame for this display device's windows. This Frame
	 *            is only needed as an owner to all windows created by the
	 *            ExperimentalDisplayDevice object. This Frame is not used if
	 *            the container argument is non-null since in this case no
	 *            Window objects are created.
	 * @param container
	 *            a Container which contains this ExperimentalDisplayDevice
	 *            object. If this is a stand-alone ExperimentalDisplayDevice
	 *            then this parameter should be set to null. If this
	 *            ExperimentalDisplayDevice is to be contained in an external
	 *            Container then this parameter must be non-null.
	 * @param mouseListener
	 *            the object which handles subject responses with the mouse
	 *            buttons.
	 * @param mouseMotionListener
	 *            the object which handles subject responses with mouse
	 *            movement.
	 * @param mouseWheelListener
	 *            the object which handles subject responses with a mouse wheel.
	 */
	public ExperimentalDisplayDevice(Frame owner, Container container,
			MouseListener mouseListener,
			MouseMotionListener mouseMotionListener,
			MouseWheelListener mouseWheelListener) {
		this.mouseListener = mouseListener;
		this.mouseMotionListener = mouseMotionListener;
		this.mouseWheelListener = mouseWheelListener;
		this.displayDeviceType = Base.getDisplayDeviceType();
		if (container == null) {
			createWindows(owner);
			leftWindow.setLayout(null);
			leftWindow.addFocusListener(new DebugFocusListener("Left window"));
			leftWindow.setIgnoreRepaint(true);
			leftWindow.addMouseListener(mouseListener);
			leftWindow.addMouseMotionListener(mouseMotionListener);
			leftWindow.addMouseWheelListener(mouseWheelListener);
			// System.out.println("ExperimentalDisplayDevice(): Mouse listeners added. ");
			if (isDualScreen()) {
				rightWindow.setLayout(null);
				rightWindow.addFocusListener(new DebugFocusListener(
						"Right window"));
				rightWindow.setIgnoreRepaint(true);
				rightWindow.addMouseListener(mouseListener);
				rightWindow.addMouseMotionListener(mouseMotionListener);
				rightWindow.addMouseWheelListener(mouseWheelListener);
			}
		} else {
			displayDeviceType = EMBEDDED;
			embeddedCanvas = new EmbeddedCanvas();
			embeddedCanvas.setBackground(Color.black);
			container.add(embeddedCanvas);
			container.doLayout();
			embeddedCanvas.addMouseListener(mouseListener);
			embeddedCanvas.addMouseMotionListener(mouseMotionListener);
			embeddedCanvas.addMouseWheelListener(mouseWheelListener);
		}
		// setActiveScreen(PRIMARY_SCREEN);
	}

	/** Dispose of all resources used by this device. */
	public void dispose() {
		if (displayDeviceType == EMBEDDED) {
			embeddedCanvas.removeMouseListener(mouseListener);
			embeddedCanvas.removeMouseMotionListener(mouseMotionListener);
			embeddedCanvas.removeMouseWheelListener(mouseWheelListener);
		} else {
			leftWindow.removeMouseListener(mouseListener);
			leftWindow.removeMouseMotionListener(mouseMotionListener);
			leftWindow.removeMouseWheelListener(mouseWheelListener);
			if (isFullScreenExclusive()) {
				if ((leftDevice != null)
						&& (leftDevice.getFullScreenWindow() != null)) {
					if (leftDeviceDefaultMode != null) {
						leftDevice.setDisplayMode(leftDeviceDefaultMode);
					}
					leftDevice.setFullScreenWindow(null);
				}
			}
			if (leftWindow != null)
				leftWindow.dispose();
			if (isDualScreen()) {
				rightWindow.removeMouseListener(mouseListener);
				rightWindow.removeMouseMotionListener(mouseMotionListener);
				rightWindow.removeMouseWheelListener(mouseWheelListener);
				if (isFullScreenExclusive()) {
					if ((rightDevice != null)
							&& (rightDevice.getFullScreenWindow() != null)) {
						if (rightDeviceDefaultMode != null) {
							rightDevice.setDisplayMode(rightDeviceDefaultMode);
						}
						rightDevice.setFullScreenWindow(null);
					}
				}
				if (rightWindow != null)
					rightWindow.dispose();
			}
		}
	}

	/**
	 * Try to set the display mode to what is requested by displayDeviceType.
	 * The result is that the local variables leftWindow and rightWindow are set
	 * properly.
	 * 
	 * @param owner
	 *            the owner Frame for the windows to be created.
	 */
	private void createWindows(Frame owner) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		Dimension deviceSize = Toolkit.getDefaultToolkit().getScreenSize();
		int dw2 = deviceSize.width / 2;
		int dh2 = deviceSize.height / 2;
		boolean primaryScreen = true;
		boolean dualScreen = false;
		boolean fullScreen = false;
		boolean successful = false;
		int mode = displayDeviceType;
		// System.out.println("ExperimentalDisplayDevice.createWindows(): mode requested: "
		// + displayDeviceType);
		// for (int i = 0; i < gs.length; i++)
		// System.out.println("ExperimentalDisplayDevice.createWindows(): Device "
		// + i + ": " + gs[i].toString());
		do {
			if ((mode == FRAMED_WINDOW) || (mode == UNFRAMED_WINDOW)
					|| (mode == FULL_SCREEN_FRAME)) {
				// System.out.println("ExperimentalDisplayDevice.createWindows(): Single framed screen.");
				Dimension winSize = getWindowSize(mode);
				Frame leftFrame = new Frame(" PXLab");
				leftFrame.setSize(winSize);
				leftFrame.setLocation((deviceSize.width - winSize.width) / 2,
						(deviceSize.height - winSize.height) / 2);
				if (mode == UNFRAMED_WINDOW) {
					leftFrame.setUndecorated(true);
				} else {
					PXLabIcon.decorate(leftFrame);
				}
				leftWindow = leftFrame;
				successful = true;
			} else if ((mode == DUAL_FRAMED_WINDOW)
					|| (mode == DUAL_UNFRAMED_WINDOW)) {
				// System.out.println("ExperimentalDisplayDevice.createWindows(): Dual framed screen.");
				Dimension winSize = getWindowSize(mode);
				Frame leftFrame = new Frame(" Left");
				leftFrame.setSize(winSize);
				leftFrame.setLocation(deviceSize.width / 2 - winSize.width - 1,
						(deviceSize.height - winSize.height) / 2);
				leftFrame.setResizable(false);
				if (mode == DUAL_UNFRAMED_WINDOW) {
					leftFrame.setUndecorated(true);
				} else {
					PXLabIcon.decorate(leftFrame);
				}
				leftWindow = leftFrame;
				Frame rightFrame = new Frame(" Right");
				rightFrame.setSize(winSize);
				rightFrame.setLocation(deviceSize.width / 2 + 1,
						(deviceSize.height - winSize.height) / 2);
				rightFrame.setResizable(false);
				if (mode == DUAL_UNFRAMED_WINDOW) {
					rightFrame.setUndecorated(true);
				} else {
					PXLabIcon.decorate(rightFrame);
				}
				rightWindow = rightFrame;
				dualScreen = true;
				successful = true;
			} else if ((mode == FULL_SCREEN) || (mode == FULL_SCREEN_EXCLUSIVE)) {
				// System.out.println("ExperimentalDisplayDevice.createWindows(): Primary full screen.");
				leftDevice = gs[0];
				fullScreen = true;
				primaryScreen = true;
				successful = true;
			} else if ((mode == FULL_SECONDARY_SCREEN)
					|| (mode == FULL_SECONDARY_SCREEN_EXCLUSIVE)) {
				if (gs.length > 1) {
					// System.out.println("ExperimentalDisplayDevice.createWindows(): Secondary full screen.");
					leftDevice = gs[1];
					fullScreen = true;
					primaryScreen = false;
					successful = true;
				} else {
					mode = (mode == FULL_SECONDARY_SCREEN) ? FULL_SCREEN
							: FULL_SCREEN_EXCLUSIVE;
					// System.out.println("ExperimentalDisplayDevice.createWindows(): No secondary screen device found.");
				}
			} else if ((mode == DUAL_FULL_SCREEN)
					|| (mode == DUAL_FULL_SCREEN_EXCLUSIVE)) {
				// System.out.println("ExperimentalDisplayDevice.createWindows(): Dual full screen.");
				if (gs.length > 1) {
					leftDevice = gs[0];
					rightDevice = gs[1];
					fullScreen = true;
					dualScreen = true;
					successful = true;
					// System.out.println("ExperimentalDisplayDevice.createWindows(): Secondary screen device found.");
				} else {
					mode = DUAL_FRAMED_WINDOW;
					// System.out.println("ExperimentalDisplayDevice.createWindows(): No secondary screen device found.");
				}
			} else {
				// System.out.println("ExperimentalDisplayDevice.createWindows(): Unknown mode requested: "
				// + mode);
				mode = FRAMED_WINDOW;
			}
		} while (!successful);
		if (fullScreen) {
			if (dualScreen) {
				GraphicsConfiguration leftGc = leftDevice
						.getDefaultConfiguration();
				leftWindow = new Window(owner, leftGc);
				leftWindow.setBounds(leftGc.getBounds());
				GraphicsConfiguration rightGc = rightDevice
						.getDefaultConfiguration();
				rightWindow = new Window(owner, rightGc);
				rightWindow.setBounds(rightGc.getBounds());
				// System.out.println("ExperimentalDisplayDevice(): Dual window bounds = "
				// + leftGc.getBounds() + " and " + rightGc.getBounds());
				if ((mode == DUAL_FULL_SCREEN_EXCLUSIVE)
						&& leftDevice.isFullScreenSupported()
						&& rightDevice.isFullScreenSupported()) {
					// System.out.println("ExperimentalDisplayDevice(): Dual full screen exclusive mode.");
					leftDevice.setFullScreenWindow(leftWindow);
					rightDevice.setFullScreenWindow(rightWindow);
					rightDeviceDefaultMode = setDisplayMode(rightDevice);
					leftDeviceDefaultMode = setDisplayMode(leftDevice);
				} else {
					// System.out.println("ExperimentalDisplayDevice.createWindows(): Dual full screen exclusive mode not available.");
					mode = DUAL_FULL_SCREEN;
				}
			} else {
				GraphicsConfiguration gc = leftDevice.getDefaultConfiguration();
				leftWindow = new Window(owner, gc);
				leftWindow.setBounds(gc.getBounds());
				// System.out.println("ExperimentalDisplayDevice.createWindows(): Configuration bounds = "
				// + gc.getBounds());
				if (((mode == FULL_SCREEN_EXCLUSIVE) || (mode == FULL_SECONDARY_SCREEN_EXCLUSIVE))
						&& leftDevice.isFullScreenSupported()) {
					// System.out.println("ExperimentalDisplayDevice.createWindows(): Use Full-Screen Exclusive Mode.");
					leftDevice.setFullScreenWindow(leftWindow);
					leftDeviceDefaultMode = setDisplayMode(leftDevice);
					mode = primaryScreen ? FULL_SCREEN_EXCLUSIVE
							: FULL_SECONDARY_SCREEN_EXCLUSIVE;
				} else {
					// System.out.println("ExperimentalDisplayDevice.createWindows(): Do not use Full-Screen Exclusive Mode.");
					mode = primaryScreen ? FULL_SCREEN : FULL_SECONDARY_SCREEN;
				}
			}
		}
		// System.out.println("ExperimentalDisplayDevice.createWindows(): mode requested: "
		// + displayDeviceType);
		// System.out.println("ExperimentalDisplayDevice.createWindows(): mode satisfied: "
		// + mode);
		displayDeviceType = mode;
	}

	private DisplayMode setDisplayMode(GraphicsDevice device) {
		if (!device.isDisplayChangeSupported()) {
			System.out
					.println("ExperimentalDisplayDevive.setDisplayMode(): Mode change not supported.");
			return null;
		}
		DisplayMode md = null;
		DisplayMode[] mds = device.getDisplayModes();
		/*
		 * for (int i = 0; i < mds.length; i++) {
		 * System.out.println("ExperimentalDisplayDevive.setDisplayMode() " +
		 * mds[i].getWidth() + "x" + mds[i].getHeight() + " pixel " +
		 * mds[i].getRefreshRate() + " Hz " + mds[i].getBitDepth() + " bpp" ); }
		 */
		DisplayMode currentMode = device.getDisplayMode();
		int h = currentMode.getHeight();
		int w = currentMode.getWidth();
		int r = currentMode.getRefreshRate();
		int b = currentMode.getBitDepth();
		if (Base.hasDisplayDeviceRefreshRate()) {
			r = Base.getDisplayDeviceRefreshRate();
		}
		if (Base.hasScreenWidth()) {
			w = Base.getScreenWidth();
		}
		if (Base.hasScreenHeight()) {
			h = Base.getScreenHeight();
		}
		DisplayMode newMode = new DisplayMode(w, h, b, r);
		if (!currentMode.equals(newMode)) {
			for (int i = 0; i < mds.length; i++) {
				if (mds[i].equals(newMode)) {
					try {
						device.setDisplayMode(newMode);
						md = newMode;
						if (Debug.isActive(Debug.TIMING | Debug.HR_TIMING)) {
							System.out
									.println("ExperimentalDisplayDevive.setDisplayMode() "
											+ mds[i].getWidth()
											+ "x"
											+ mds[i].getHeight()
											+ "x"
											+ mds[i].getRefreshRate() + " set.");
						}
						try {
							Thread.sleep(2000);
						} catch (InterruptedException iex) {
						}
						Base.setDisplayDeviceFrameDuration(VideoSystem
								.getFrameDuration());
					} catch (IllegalArgumentException ax) {
						System.out
								.println("ExperimentalDisplayDevive.setDisplayMode(): Can't change mode.");
					} catch (UnsupportedOperationException uox) {
						System.out
								.println("ExperimentalDisplayDevive.setDisplayMode(): Can't change mode.");
					}
				}
			}
		}
		return md;
	}

	private Dimension getWindowSize(int mode) {
		boolean single = ((mode == FRAMED_WINDOW) || (mode == UNFRAMED_WINDOW));
		Dimension deviceSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = single ? ((6 * deviceSize.width) / 8)
				: ((14 * deviceSize.width) / 32);
		int h = single ? ((6 * deviceSize.height) / 8)
				: ((14 * deviceSize.height) / 32);
		if (mode == FULL_SCREEN_FRAME) {
			w = deviceSize.width;
			h = deviceSize.height;
		}
		if (Base.hasScreenWidth()) {
			w = Base.getScreenWidth();
		}
		if (Base.hasScreenHeight()) {
			h = Base.getScreenHeight();
		}
		// System.out.println("ExperimentalDisplayDevice.getWindowSize() for mode "
		// + mode + ": (" + w + ", " + h + ")");
		return new Dimension(w, h);
	}

	private void createBufferStrategy() {
		if (displayDeviceType == EMBEDDED) {
			embeddedCanvas.createBufferStrategy(2);
			leftBufferStrategy = embeddedCanvas.getBufferStrategy();
		} else {
			leftWindow.createBufferStrategy(2);
			leftBufferStrategy = leftWindow.getBufferStrategy();
			if (isDualScreen()) {
				rightWindow.createBufferStrategy(2);
				rightBufferStrategy = rightWindow.getBufferStrategy();
			}
		}
		if (Debug.isActive(Debug.TIMING | Debug.HR_TIMING)) {
			describeBufferStrategy();
		}
	}

	/**
	 * Open the display device. This makes the display screen(s) visible and
	 * selects the primary screen as default drawing screen.
	 */
	public void open() {
		if (displayDeviceType != EMBEDDED) {
			if (leftWindow != null) {
				leftWindow.setCursor(hiddenCursor ? invisibleCursor : Cursor
						.getDefaultCursor());
				leftWindow.setVisible(true);
				leftWindow.toFront();
			}
			if (isDualScreen()) {
				if (rightWindow != null) {
					leftWindow.setCursor(hiddenCursor ? invisibleCursor
							: Cursor.getDefaultCursor());
					rightWindow.setVisible(true);
					rightWindow.toFront();
				}
			}
			// Buffer strategies may only be created after the component
			// is made visible.
		}
		createBufferStrategy();
		// new de.pxlab.awtx.BufferCapabilitiesDialog(new Frame(),
		// leftBufferStrategy);
		initColorTransform();
		activeScreen = 0;
		setActiveScreen(PRIMARY_SCREEN);
		// System.out.println("ExperimentalDisplayDevice.open() Cursor is " +
		// (hiddenCursor? "hidden": "visible"));
		Base.setScreenWidth(getWidth());
		Base.setScreenHeight(getHeight());
		// System.out.println("ExperimentalDisplayDevice.open()");
	}

	public boolean isOpen() {
		return leftBufferStrategy != null;
	}

	/** Close the display device. No resources are disposed. */
	public void close() {
		if (displayDeviceType != EMBEDDED) {
			if (leftWindow != null) {
				leftWindow.setVisible(false);
			}
			if (isDualScreen()) {
				if (rightWindow != null) {
					rightWindow.setVisible(false);
				}
			}
		} else {
			clear(Color.black);
		}
		// System.out.println("ExperimentalDisplayDevice.close()");
	}
	protected static Cursor invisibleCursor = createInvisibleCursor();
	protected boolean hiddenCursor = false;

	public void setHiddenCursor(boolean s) {
		hiddenCursor = s;
		// System.out.println("DisplayDevice.setHiddenCursor(): " + (s? "true":
		// "false"));
	}

	/** Create an invisible cursor. */
	private static Cursor createInvisibleCursor() {
		Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		Cursor cursor = null;
		Dimension bestSize = tk.getBestCursorSize(32, 32);
		int maxColors = tk.getMaximumCursorColors();
		if (maxColors == 0) {
			cursor = Cursor.getDefaultCursor();
		} else {
			Image cursorImage = new BufferedImage(bestSize.width,
					bestSize.height, BufferedImage.TYPE_INT_ARGB);
			cursor = tk.createCustomCursor(cursorImage, new Point(0, 0),
					"Invisible");
		}
		return (cursor);
	}

	/**
	 * Check if the given type is a valid type number for PXLab experimental
	 * display devices.
	 * 
	 * @param type
	 *            the type number to be checked
	 * @return true if the type number is valid and false otherwise. A true
	 *         return value does not mean that the current hardware is able to
	 *         actually run this type.
	 */
	public static boolean isValidType(int type) {
		return (type == DisplayDevice.FRAMED_WINDOW)
				|| (type == DisplayDevice.UNFRAMED_WINDOW)
				|| (type == DisplayDevice.FULL_SCREEN_FRAME)
				|| (type == DisplayDevice.FULL_SCREEN)
				|| (type == DisplayDevice.FULL_SCREEN_EXCLUSIVE)
				|| (type == DisplayDevice.FULL_SECONDARY_SCREEN)
				|| (type == DisplayDevice.FULL_SECONDARY_SCREEN_EXCLUSIVE)
				|| (type == DisplayDevice.DUAL_FULL_SCREEN)
				|| (type == DisplayDevice.DUAL_FULL_SCREEN_EXCLUSIVE)
				|| (type == DisplayDevice.DUAL_FRAMED_WINDOW)
				|| (type == DisplayDevice.DUAL_UNFRAMED_WINDOW);
	}

	public static boolean isFullSecondaryScreen(int d) {
		return (d == DisplayDevice.FULL_SECONDARY_SCREEN)
				|| (d == DisplayDevice.FULL_SECONDARY_SCREEN_EXCLUSIVE);
	}

	/**
	 * Used by the PresentationManager to figure out some restrictions for full
	 * screen exclusive mode.
	 */
	public boolean isFullScreenExclusive() {
		return (displayDeviceType == FULL_SCREEN_EXCLUSIVE)
				|| (displayDeviceType == FULL_SECONDARY_SCREEN_EXCLUSIVE)
				|| (displayDeviceType == DUAL_FULL_SCREEN_EXCLUSIVE);
	}

	private boolean isFullScreen() {
		return (displayDeviceType == DisplayDevice.FULL_SCREEN)
				|| (displayDeviceType == DisplayDevice.FULL_SECONDARY_SCREEN)
				|| (displayDeviceType == DisplayDevice.DUAL_FULL_SCREEN);
	}

	private boolean isDualScreen() {
		return isDualScreen(displayDeviceType);
	}

	public static boolean isDualScreen(int d) {
		return (d == DisplayDevice.DUAL_FULL_SCREEN)
				|| (d == DisplayDevice.DUAL_FULL_SCREEN_EXCLUSIVE)
				|| (d == DisplayDevice.DUAL_FRAMED_WINDOW)
				|| (d == DisplayDevice.DUAL_UNFRAMED_WINDOW);
	}

	private boolean isWindow() {
		return (displayDeviceType == DisplayDevice.FRAMED_WINDOW)
				|| (displayDeviceType == DisplayDevice.UNFRAMED_WINDOW)
				|| (displayDeviceType == DisplayDevice.FULL_SCREEN_FRAME)
				|| (displayDeviceType == DisplayDevice.DUAL_FRAMED_WINDOW)
				|| (displayDeviceType == DisplayDevice.DUAL_UNFRAMED_WINDOW)
				|| (displayDeviceType == EMBEDDED);
	}

	/**
	 * Set the active drawing screen for subsequent drawing operations.
	 * 
	 * @param p
	 *            screen code for drawing. Possible numbers are PRIMARY_SCREEN
	 *            or SECONDARY_SCREEN.
	 */
	public void setActiveScreen(int p) {
		if (p != activeScreen) {
			if ((p == SECONDARY_SCREEN) && isDualScreen()) {
				PxlColor.setDeviceTransform(rightColorDeviceTransform);
				activeScreen = SECONDARY_SCREEN;
			} else {
				PxlColor.setDeviceTransform(leftColorDeviceTransform);
				activeScreen = PRIMARY_SCREEN;
			}
			Debug.show(Debug.COLOR_GAMUT, "Active screen: " + activeScreen);
		}
	}
	/**
	 * Remember the timing group whose image has been preloaded most recently.
	 * This is needed in order to be able to remove the preload flag in those
	 * cases where it has become invalid. This is the case when a timing group
	 * is repainted outside of the PresentationManager in a timing group
	 * controlled by a STOP_KEY_TIMER.
	 */
	private static TimingElement preloadedTimingGroupLeft;
	private static TimingElement preloadedTimingGroupRight;

	public void setPreloadedTimingGroup(TimingElement te) {
		if (activeScreen == PRIMARY_SCREEN) {
			preloadedTimingGroupLeft = te;
		} else {
			preloadedTimingGroupRight = te;
		}
	}

	public void clearPreloadedTimingGroup() {
		if (activeScreen == PRIMARY_SCREEN) {
			if (preloadedTimingGroupLeft != null) {
				preloadedTimingGroupLeft.setImagePreloaded(false);
			}
		} else {
			if (preloadedTimingGroupRight != null) {
				preloadedTimingGroupRight.setImagePreloaded(false);
			}
		}
	}

	/**
	 * Get a Graphics context for drawing onto the currently active screen.
	 * Users have to dispose this Graphics object after use. The content of the
	 * drawing surface is only shown after the method show() has been called.
	 * 
	 * @return a Graphics object which can be used for drawing.
	 */
	public Graphics getGraphics() {
		return getGraphics(activeScreen);
	}

	/**
	 * Get a Graphics context for drawing onto the given screen. Users have to
	 * dispose this Graphics object after use. The content of the drawing
	 * surface is only shown after the method show() has been called.
	 * 
	 * @param k
	 *            the screen for drawing.
	 * @return a Graphics object which can be used for drawing.
	 */
	public Graphics getGraphics(int k) {
		return (k == SECONDARY_SCREEN) ? rightBufferStrategy.getDrawGraphics()
				: leftBufferStrategy.getDrawGraphics();
	}

	/**
	 * Get a component for drawing onto this display device.
	 * 
	 * @param k
	 *            the screen whose drawing surface we have to return.
	 */
	public Component getComponent(int k) {
		// new RuntimeException().printStackTrace();
		Component c = null;
		if (displayDeviceType == EMBEDDED) {
			c = embeddedCanvas;
		} else {
			c = (k == SECONDARY_SCREEN) ? rightWindow : leftWindow;
		}
		// System.out.println("ExperimentalDisplayDevice.getComponent() returns "
		// + c);
		return c;
	}

	/**
	 * Get the currently active drawing surface of this display device.
	 */
	public Component getComponent() {
		return getComponent(activeScreen);
	}

	/**
	 * Get the display device window. This method is protected since no direct
	 * knowledge about the display device should be public. Subclasses may use
	 * the display device's windows to attach objects onto them.
	 * 
	 * @param k
	 *            the screen whose window we have to return.
	 */
	protected Window getWindow(int k) {
		return (k == SECONDARY_SCREEN) ? rightWindow : leftWindow;
	}

	/**
	 * Get the currently active drawing window of this display device. This
	 * method is protected since no direct knowledge about the display device
	 * should be public. Subclasses may use the display device's windows to
	 * attach objects onto them.
	 */
	protected Window getWindow() {
		return getWindow(activeScreen);
	}

	/** Get the current display device screen's DisplayMode. */
	public DisplayMode getDisplayMode() {
		GraphicsDevice d = (activeScreen == SECONDARY_SCREEN) ? rightDevice
				: leftDevice;
		if (d == null) {
			d = getComponent().getGraphicsConfiguration().getDevice();
		}
		return d.getDisplayMode();
	}

	/**
	 * Create a memory buffer which fits this DisplayDevice and satisfies the
	 * timing needs for the given timing element.
	 * 
	 * @param te
	 *            the timing element which will use this memory buffer. This
	 *            affects whether scarce resources like accelerated memory is
	 *            mamanged. If the timing element does vertical retrace
	 *            synchronization or has a very short duration then chances for
	 *            an accelerated buffer will increase.
	 */
	public Image createMemoryBuffer(TimingElement te) {
		GraphicsConfiguration gc = (displayDeviceType == EMBEDDED) ? embeddedCanvas
				.getGraphicsConfiguration() : getWindow()
				.getGraphicsConfiguration();
		int ams = 0;
		if (Debug.isActive(Debug.TIMING)) {
			ams = gc.getDevice().getAvailableAcceleratedMemory();
		}
		boolean accelerationNeeded = false;
		if ((te.getTimerType() & TimerBitCodes.VIDEO_SYNCHRONIZATION_BIT) != 0) {
			accelerationNeeded = true;
		} else {
			long t = te.getIntendedDuration();
			if (t > 0L && t < TIME_LIMIT_4_ACCELERATION) {
				accelerationNeeded = true;
			}
		}
		Image buffer = accelerationNeeded ? (Image) (gc
				.createCompatibleVolatileImage(getWidth(), getHeight()))
				: (Image) (gc.createCompatibleImage(getWidth(), getHeight()));
		if (Debug.isActive(Debug.TIMING)) {
			System.out
					.println("ExperimentalDisplayDevice.createMemoryBuffer(): Acceleration is "
							+ (accelerationNeeded ? "required."
									: "not required."));
			System.out
					.print("ExperimentalDisplayDevice.createMemoryBuffer(): Created ");
			ImageCapabilities imc;
			if (buffer instanceof VolatileImage) {
				System.out.println("volatile image buffer.");
				imc = ((VolatileImage) buffer).getCapabilities();
			} else {
				System.out.println("standard image buffer.");
				imc = ((BufferedImage) buffer).getCapabilities(gc);
			}
			if (imc.isAccelerated()) {
				System.out.println("  The buffer is accelerated.");
			} else {
				System.out.println("  The buffer is not accelerated.");
			}
			if (imc.isTrueVolatile()) {
				System.out.println("  The buffer is true volatile.");
			} else {
				System.out.println("  The buffer is not volatile.");
			}
			int ams2 = gc.getDevice().getAvailableAcceleratedMemory();
			System.out.println("  Accelerated memory available: " + ams / 1024
					+ " KB");
			System.out.println("  Remaining accelerated memory: " + ams2 / 1024
					+ " KB");
		}
		return buffer;
	}

	/** Get the currently active screen's width. */
	public int getWidth() {
		return getWidth(activeScreen);
	}

	/**
	 * Get a screen's width.
	 * 
	 * @param k
	 *            the screen number.
	 */
	public int getWidth(int k) {
		if (displayDeviceType == EMBEDDED) {
			return embeddedCanvas.getWidth();
		} else {
			return ((k == SECONDARY_SCREEN) ? rightWindow : leftWindow)
					.getWidth();
		}
	}

	/** Get the currently active screen's height. */
	public int getHeight() {
		return getHeight(activeScreen);
	}

	/**
	 * Get a screen's height.
	 * 
	 * @param k
	 *            the screen number.
	 */
	public int getHeight(int k) {
		if (displayDeviceType == EMBEDDED) {
			return embeddedCanvas.getHeight();
		} else {
			return ((k == SECONDARY_SCREEN) ? rightWindow : leftWindow)
					.getHeight();
		}
	}

	/** Get the active screen window size. */
	public Dimension getSize() {
		return new Dimension(getWidth(), getHeight());
	}

	/** Get the active screen window location. */
	public Point getLocation() {
		return getLocation(activeScreen);
	}

	/**
	 * Get a screen window's location.
	 * 
	 * @param k
	 *            the screen number.
	 */
	public Point getLocation(int k) {
		if (displayDeviceType == EMBEDDED) {
			return embeddedCanvas.getLocationOnScreen();
		} else {
			return ((k == SECONDARY_SCREEN) ? rightWindow : leftWindow)
					.getLocationOnScreen();
		}
	}

	/**
	 * Re-initialize the display device's color transforms. This method should
	 * be called after this class's static experimental parameters have been
	 * changed.
	 */
	public void initColorTransform() {
		if (isFullSecondaryScreen(displayDeviceType)) {
			// Single screen on secondary device uses 'left' but
			// should use measurements for secondary screen.
			leftColorDeviceTransform = new ScreenColorTransform(
					SecondaryScreen.RedPrimary, SecondaryScreen.GreenPrimary,
					SecondaryScreen.BluePrimary, SecondaryScreen.RedGamma,
					SecondaryScreen.GreenGamma, SecondaryScreen.BlueGamma,
					SecondaryScreen.ColorDeviceDACRange,
					SecondaryScreen.DeviceColorTableFile);
			ExPar.DeviceWhitePoint.set(new PxlColor(leftColorDeviceTransform
					.getWhitePoint()));
		} else {
			leftColorDeviceTransform = new ScreenColorTransform(
					PrimaryScreen.RedPrimary, PrimaryScreen.GreenPrimary,
					PrimaryScreen.BluePrimary, PrimaryScreen.RedGamma,
					PrimaryScreen.GreenGamma, PrimaryScreen.BlueGamma,
					PrimaryScreen.ColorDeviceDACRange,
					PrimaryScreen.DeviceColorTableFile);
			PxlColor a = new PxlColor(leftColorDeviceTransform.getWhitePoint());
			if (isDualScreen()) {
				rightColorDeviceTransform = new ScreenColorTransform(
						SecondaryScreen.RedPrimary,
						SecondaryScreen.GreenPrimary,
						SecondaryScreen.BluePrimary, SecondaryScreen.RedGamma,
						SecondaryScreen.GreenGamma, SecondaryScreen.BlueGamma,
						SecondaryScreen.ColorDeviceDACRange,
						SecondaryScreen.DeviceColorTableFile);
				PxlColor b = new PxlColor(
						rightColorDeviceTransform.getWhitePoint());
				double[] ab = { a.Y, a.x, a.y, b.Y, b.x, b.y };
				ExPar.DeviceWhitePoint.set(ab);
			} else {
				ExPar.DeviceWhitePoint.set(a);
			}
		}
		// System.out.println("ExperimentalDisplayDevice.initColorTransform() DeviceWhitePoint = "
		// + ExPar.DeviceWhitePoint);
	}

	/**
	 * Get the color transform which is used for transforming device independent
	 * color coordinates to device dependent coordinates for this display
	 * device.
	 * 
	 * @return the color transform which is currently used.
	 */
	public ColorDeviceTransform getColorDeviceTransform() {
		return getColorDeviceTransform(activeScreen);
	}

	/**
	 * Get the color transform which is used for transforming device independent
	 * color coordinates to device dependent coordinates for this display
	 * device.
	 * 
	 * @param k
	 *            the screen number whose transform is requested.
	 * @return the color transform which is currently used.
	 */
	public ColorDeviceTransform getColorDeviceTransform(int k) {
		return (k == SECONDARY_SCREEN) ? rightColorDeviceTransform
				: leftColorDeviceTransform;
	}

	/**
	 * Show the content of the most recently used drawing surface. The graphics
	 * context for drawing must be retrieved by calling getGraphics().
	 */
	public void show() {
		// System.out.println("ExperimentalDisplayDevice.show() left buffer");
		leftBufferStrategy.show();
		// leftGraphics = null;
		if (isDualScreen()) {
			// System.out.println("ExperimentalDisplayDevice.show() right buffer");
			rightBufferStrategy.show();
			// rightGraphics = null;
		}
	}

	/**
	 * Clear the whole device to the given color.
	 * 
	 * @param b
	 *            the color filling the whole device surface.
	 */
	public void clear(Color b) {
		Graphics g = getGraphics(PRIMARY_SCREEN);
		g.setColor(b);
		g.fillRect(0, 0, getWidth(PRIMARY_SCREEN), getHeight(PRIMARY_SCREEN));
		g.dispose();
		if (isDualScreen()) {
			g = getGraphics(SECONDARY_SCREEN);
			g.setColor(b);
			g.fillRect(0, 0, getWidth(SECONDARY_SCREEN),
					getHeight(SECONDARY_SCREEN));
			g.dispose();
		}
		show();
	}
	/** A Canvas which is embedded in a Container. */
	public class EmbeddedCanvas extends Canvas {
		public EmbeddedCanvas() {
			setIgnoreRepaint(true);
		}
	}

	public void describeBufferStrategy() {
		System.out.println(bufferStrategyDescription(leftBufferStrategy));
	}

	public String bufferStrategyDescription(BufferStrategy bfs) {
		BufferCapabilities bcap = bfs.getCapabilities();
		ImageCapabilities backcap = bcap.getBackBufferCapabilities();
		ImageCapabilities frontcap = bcap.getFrontBufferCapabilities();
		BufferCapabilities.FlipContents fc = bcap.getFlipContents();
		StringBuffer d = new StringBuffer(300);
		String nl = System.getProperty("line.separator");
		d.append("\nExperimentalDisplayDevice.createBufferStrategy() Buffer Strategy:"
				+ nl);
		d.append("  Buffer " + (bcap.isPageFlipping() ? "uses" : "can't use")
				+ " page flipping." + nl);
		d.append("  Full screen exclusive mode is "
				+ (bcap.isFullScreenRequired() ? "" : "not")
				+ " required for page flipping." + nl);
		d.append("  Multiple back buffers are "
				+ (bcap.isMultiBufferAvailable() ? "" : "not")
				+ " available for page flipping." + nl);
		d.append("  Buffer "
				+ ((fc == null) ? "uses blitting.\n" : ("has flip contents "
						+ fc.toString() + "." + nl)));
		d.append("  Back buffer is " + (backcap.isAccelerated() ? "" : "not")
				+ " accelerated." + nl);
		d.append("  Front buffer is " + (frontcap.isAccelerated() ? "" : "not")
				+ " accelerated." + nl);
		d.append("  Back buffer is " + (backcap.isTrueVolatile() ? "" : "not")
				+ " volatile." + nl);
		d.append("  Front buffer is "
				+ (frontcap.isTrueVolatile() ? "" : "not") + " volatile." + nl);
		return d.toString();
	}
	// ------------------------------------------------------------------
	// CalibrationTarget implementation
	// ------------------------------------------------------------------
	private Dimension calibrationTargetSize = new Dimension(300, 300);
	private Point calibrationTargetPosition = new Point(0, 0);

	public void setCalibrationTargetSize(Dimension size) {
		calibrationTargetSize = size;
	}

	public Dimension getCalibrationTargetSize() {
		return calibrationTargetSize;
	}

	public void setCalibrationTargetPosition(Point p) {
		calibrationTargetPosition = p;
	}

	public Point getCalibrationTargetPosition() {
		return calibrationTargetPosition;
	}

	/**
	 * Get the number of intensity levels of a single channel of the color
	 * calibration target.
	 */
	public int getNumberOfCalibrationChannels() {
		return 3;
	}

	/**
	 * Get the calibration channel codes for the device's primary channels.
	 * 
	 * @return an array of integer values which represent calibration channel
	 *         codes.
	 */
	public int[] getPrimaryCalibrationCodes() {
		int k = getNumberOfCalibrationChannels();
		int m = 1;
		int[] cc = new int[k];
		for (int j = 0; j < k; j++) {
			cc[j] = m;
			m *= 2;
		}
		return cc;
	}

	/**
	 * Get all available calibration channel codes for this device.
	 * 
	 * @return an array of integer values which represent calibration channel
	 *         codes.
	 */
	public int[] getAllCalibrationCodes() {
		int k = getNumberOfCalibrationChannels();
		int m = 1;
		for (int j = 0; j < k; j++)
			m *= 2;
		m--;
		int[] cc = new int[m];
		for (int j = 0; j < m; j++)
			cc[j] = j + 1;
		return cc;
	}

	public int getCalibrationTargetResolution() {
		return 256;
	}

	/** Open the color display device for calibration. */
	public void openDevice() {
		open();
	}

	/** Close the color display devivce after calibration. */
	public void closeDevice() {
		close();
	}

	/** Clear the color calibration target. */
	public void clearCalibrationTarget() {
		clear(Color.black);
	}

	/**
	 * Show the given intensity level at the given channel.
	 * 
	 * @param channel
	 *            the color channels to be used.
	 * @param output
	 *            the intensity level to be used.
	 */
	public void showCalibrationTarget(int channel, int output) {
		Color c = new Color(0, 0, 0);
		if (channel == RED_CHANNEL) {
			c = new Color(output, 0, 0);
		} else if (channel == GREEN_CHANNEL) {
			c = new Color(0, output, 0);
		} else if (channel == BLUE_CHANNEL) {
			c = new Color(0, 0, output);
		} else if (channel == YELLOW_CHANNEL) {
			c = new Color(output, output, 0);
		} else if (channel == CYAN_CHANNEL) {
			c = new Color(0, output, output);
		} else if (channel == MAGENTA_CHANNEL) {
			c = new Color(output, 0, output);
		} else if (channel == WHITE_CHANNEL) {
			c = new Color(output, output, output);
		}
		Graphics g = getGraphics();
		g.setColor(c);
		g.fillOval(getWidth() / 2 + calibrationTargetPosition.x
				- calibrationTargetSize.width / 2, getHeight() / 2
				+ calibrationTargetPosition.y - calibrationTargetSize.height
				/ 2, calibrationTargetSize.width, calibrationTargetSize.height);
		g.dispose();
		show();
	}

	/**
	 * Show a calibration target with the given color.
	 * 
	 * @param c
	 *            the color to be used.
	 */
	public void showCalibrationTarget(PxlColor c) {
		showCalibrationTarget(c.dev());
	}

	/**
	 * Show a calibration target with the given color.
	 * 
	 * @param c
	 *            the color to be used.
	 */
	public void showCalibrationTarget(Color c) {
		Graphics g = getGraphics();
		g.setColor(c);
		g.fillOval(getWidth() / 2 + calibrationTargetPosition.x
				- calibrationTargetSize.width / 2, getHeight() / 2
				+ calibrationTargetPosition.y - calibrationTargetSize.height
				/ 2, calibrationTargetSize.width, calibrationTargetSize.height);
		g.dispose();
		show();
	}

	public void showCalibrationAlignmentPattern() {
		Graphics g = getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(128, 128, 128));
		g.drawLine(0, getHeight() / 2 + calibrationTargetPosition.y,
				getWidth() - 1, getHeight() / 2 + calibrationTargetPosition.y);
		g.drawLine(getWidth() / 2 + calibrationTargetPosition.x, 0, getWidth()
				/ 2 + calibrationTargetPosition.x, getHeight() - 1);
		int s = 100;
		for (int i = 0; (i < 16) && (s < getHeight()); i++) {
			g.drawRect(getWidth() / 2 + calibrationTargetPosition.x - s / 2,
					getHeight() / 2 + calibrationTargetPosition.y - s / 2, s, s);
			s += 50;
		}
		g.dispose();
		show();
	}
}
