package de.pxlab.tools.show;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import de.pxlab.util.*;
import de.pxlab.awtx.*;
import de.pxlab.pxl.*;

/**
 * Run a slide show of images contained in a set of root directories. Allows for
 * interactive control of the replay method.
 * 
 * <p>
 * This application also shows how to use an ExperimentalDisplayDevice to show
 * DisplayElement objects without a PresentationManager.
 * 
 * @version 0.1.2
 * @see de.pxlab.pxl.ExperimentalDisplayDevice
 * @see de.pxlab.pxl.DisplayElement
 */
/*
 * 
 * 2007/01/12
 * 
 * 2007/0411 added option to show the file path
 */
public class SlideShow extends CloseableFrame implements MouseListener,
		MouseMotionListener, MouseWheelListener, KeyEventDispatcher,
		CommandLineOptionHandler {
	private Tray tray;
	private ExperimentalDisplayDevice displayDevice;
	private BitMapElement slide;
	private TextElement text;
	private ExPar textColor;
	private Timer timer;
	private Changer changer;
	private boolean paused;
	// Command line option parameters
	private boolean randomAllSequence = false;
	private boolean randomFileSequence = false;
	private boolean randomDirSequence = false;
	private boolean scaleToScreen = false;
	private boolean nonstop = false;
	private boolean bigSlides = false;
	private boolean showFileName = false;
	private boolean showFileCount = false;
	private String filePath;
	private ArrayList extList;
	private boolean printFileNames = false;
	private int duration = 1400;
	private long minimumFileSize = 0L;
	private int debug = 0;

	public SlideShow(String[] args) {
		super(" PXLab Slide Show");
		ArrayList rootList = new ArrayList();
		extList = new ArrayList();
		// Consume command line arguments
		if ((args != null) && (args.length > 0)) {
			CommandLineParser clp = new CommandLineParser(args, options, this);
			if (clp.hasMoreArgs()) {
				while (clp.hasMoreArgs()) {
					rootList.add(clp.getArg());
				}
			}
		}
		if (extList.size() == 0)
			extList.add("jpg");
		tray = new Tray(rootList, extList, debug);
		int fileCount = tray.size();
		if (showFileCount) {
			System.out.println(fileCount + " Files found.");
		}
		// System.out.println(fileCount + " Files found.");
		if (fileCount == 0) {
			System.out.println("No files found.");
			System.exit(0);
		}
		if (printFileNames)
			tray.print();
		tray.setRandomization(randomDirSequence, randomFileSequence,
				randomAllSequence);
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);
		displayDevice = new ExperimentalDisplayDevice(this, this, this, this);
		setVisible(true);
		displayDevice.open();
		slide = new BitMapElement();
		textColor = new ExPar(ExParTypeCodes.COLOR, new ExParValue(
				new PxlColor(Color.black)), "");
		text = new TextElement(textColor);
		timer = new Timer();
		changer = new Changer();
		timer.schedule(changer, 0L, duration);
		paused = false;
	}
	/** The timer task which changes slides. */
	private class Changer extends TimerTask {
		public Changer() {
		}

		public void run() {
			if (getNextAcceptablePicture(tray)) {
				showNextPicture();
			} else {
				cancel();
				showFinished();
			}
		}
	}

	/**
	 * Restart the timer such that an immediate slide change follows.
	 */
	private void restart() {
		changer.cancel();
		changer = new Changer();
		timer.schedule(changer, 0L, duration);
	}

	/**
	 * Gets the next acceptable file and loads it into a BufferedImage of its
	 * native size.
	 */
	private boolean getNextAcceptablePicture(Tray tray) {
		boolean found = false;
		boolean search = true;
		File f = null;
		while (search) {
			if (tray.hasNext()) {
				String fn = tray.next();
				f = new File(fn);
				if (minimumFileSize > 0L) {
					found = f.length() > minimumFileSize;
				} else {
					found = true;
				}
				search = !found;
			} else {
				search = false;
			}
		}
		if (found) {
			slide.setImage(f.getParent(), f.getName());
			filePath = f.getPath() + " [" + ((f.length() + 1023) / 1024)
					+ " KB]";
			// if (showFileName) System.out.println(filePath);
		}
		return found;
	}

	/** Shows the most recently loaded image file on the screen. */
	private void showNextPicture() {
		int dw = displayDevice.getWidth();
		int dh = displayDevice.getHeight();
		Graphics g = displayDevice.getGraphics();
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, dw, dh);
		// g.setColor(Color.white);
		// g.drawLine(0, 0, dw, dh);
		// slide.setGraphicsContext(displayDevice.getGraphics(), dw, dh);
		slide.setGraphicsContext(g, dw, dh);
		if (scaleToScreen) {
			double iw = slide.getImage().getWidth();
			double ih = slide.getImage().getHeight();
			double sfw = dw / iw;
			double sfh = dh / ih;
			slide.setScalingFactor((sfw < sfh) ? sfw : sfh);
		}
		slide.show();
		if (showFileName) {
			text.setFont(Font.SANS_SERIF, Font.BOLD, 24);
			text.setLocation(-dw / 2 + 10, dh / 2 - 10);
			text.setText(filePath);
			text.setReferencePoint(PositionReferenceCodes.BASE_LEFT);
			text.show();
		}
		displayDevice.show();
	}

	public static void main(String[] args) {
		new SlideShow(args);
	}

	/**
	 * The Changer thread calls this method to tell us that the show is
	 * finished.
	 */
	private void showFinished() {
		// System.out.println("showFinished()");
		displayDevice.close();
		displayDevice.dispose();
		setVisible(false);
		System.exit(0);
	}
	// --------------------------------------------------------------------
	// Command line options
	// --------------------------------------------------------------------
	private String options = "D:abcde:fghjkm:npqrst:w?";

	// --------------------------------------------------------------------
	// Implementation of CommandLineOptionHandler
	// --------------------------------------------------------------------
	/** This method is called for every command line option found. */
	public void commandLineOption(char c, String arg) {
		switch (c) {
		case 'a':
			Base.setDisplayDeviceType(DisplayDevice.FULL_SCREEN_FRAME);
			break;
		// case 'b': bigSlides = true; break;
		case 'b':
			minimumFileSize = 40L * 1024L;
			break;
		case 'c':
			nonstop = true;
			break;
		case 'd':
			Base.setDisplayDeviceType(DisplayDevice.FULL_SECONDARY_SCREEN);
			break;
		case 'e':
			extList.add(arg);
			break;
		case 'f':
			Base.setDisplayDeviceType(DisplayDevice.FULL_SCREEN);
			break;
		case 'g':
			randomDirSequence = true;
			break;
		case 'j':
			showFileCount = true;
			break;
		case 'm':
			minimumFileSize = StringExt.intValue(arg, 0);
			break;
		case 'n':
			showFileName = true;
			break;
		case 'p':
			printFileNames = true;
			break;
		case 'q':
			randomFileSequence = true;
			break;
		case 'r':
			randomAllSequence = true;
			break;
		case 's':
			scaleToScreen = true;
			break;
		case 't':
			duration = StringExt.intValue(arg, 2000);
			break;
		case 'w':
			Base.setDisplayDeviceType(DisplayDevice.UNFRAMED_WINDOW);
			break;
		case 'D':
			debug = debug | StringExt.intValue(arg, -1);
			break;
		case 'k':
			showKeyCommands();
			System.exit(1);
			break;
		case '?':
		case 'h':
			showOptions();
			System.exit(1);
			break;
		default:
			commandLineError(1, "Unknown option " + c);
			System.exit(1);
		}
	}

	/**
	 * This method is called whenever an error is found in the command line
	 * options.
	 */
	public void commandLineError(int e, String s) {
		System.out.println("Command line error: " + s);
		showOptions();
	}

	/** This shows the available command line options. */
	private void showOptions() {
		System.out.println("\nUsage: java " + this.getClass().getName()
				+ " [options] directory");
		System.out.println(" Options are:");
		if (options.indexOf('a') >= 0)
			System.out.println("   -a       use a maximum sized frame display");
		if (options.indexOf('b') >= 0)
			System.out.println("   -b       show big slides only");
		if (options.indexOf('c') >= 0)
			System.out.println("   -c       cycle picture list");
		if (options.indexOf('d') >= 0)
			System.out.println("   -d       use full secondary screen display");
		if (options.indexOf('e') >= 0)
			System.out
					.println("   -e ext   add 'ext' to list of valid extensions");
		if (options.indexOf('f') >= 0)
			System.out.println("   -f       use full screen display");
		if (options.indexOf('j') >= 0)
			System.out.println("   -j       print number of files in the tray");
		if (options.indexOf('m') >= 0)
			System.out.println("   -m n     set minimum file size");
		if (options.indexOf('n') >= 0)
			System.out.println("   -n       show file path in picture");
		if (options.indexOf('p') >= 0)
			System.out.println("   -p       print list of files found");
		if (options.indexOf('g') >= 0)
			System.out.println("   -g       random directory sequence");
		if (options.indexOf('q') >= 0)
			System.out
					.println("   -q       random file sequence within directory");
		if (options.indexOf('r') >= 0)
			System.out.println("   -r       full random sequence");
		if (options.indexOf('s') >= 0)
			System.out.println("   -s       scale image to screen size");
		if (options.indexOf('t') >= 0)
			System.out.println("   -t n     set viewing duration to n ms");
		if (options.indexOf('w') >= 0)
			System.out.println("   -w       use framed window display");
		if (options.indexOf('D') >= 0)
			System.out.println("   -D c     add debug code c");
		if (options.indexOf('?') >= 0)
			System.out.println("   -?       show this help text");
		if (options.indexOf('h') >= 0)
			System.out.println("   -h       show this help text");
	}

	private void showKeyCommands() {
		System.out.println("\nUsage: java " + this.getClass().getName()
				+ " [options] directory");
		System.out.println(" Key Commands:");
		System.out.println("   ESC             exit");
		System.out.println("   ENTER           next picture");
		System.out.println("   SPACE           pause/restart");
		System.out.println("   +               set forward direction");
		System.out.println("   -               set backward direction");
		System.out.println("   PAGE UP         go to previous directory");
		System.out.println("   PAGE DOWN       go to next directory");
		System.out
				.println("   LEFT            show backward picture and proceed backward");
		System.out
				.println("   RIGHT           show forward picture and proceed forward");
		System.out.println("   DIGIT           skip next n pictures");
		System.out.println("   g               toggle directory randomization");
		System.out
				.println("   q               toggle file randomization within directory");
		System.out.println("   r               toggle full randomization");
		System.out.println("");
	}

	// -------------------------------------------------------
	// Implementation of KeyEventDispatcher
	// -------------------------------------------------------
	/** Dispatch the given keyboard event to the proper destination. */
	public boolean dispatchKeyEvent(KeyEvent e) {
		int id = e.getID();
		boolean r = true;
		/*
		 * System.out.println("dispatchKeyEvent(): " + e.getKeyCode() +
		 * " (ID = " + id + ") from " + e.getSource().getClass().getName()); /*
		 */
		if (id == KeyEvent.KEY_PRESSED) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_ESCAPE:
				showFinished();
				break;
			case KeyEvent.VK_ENTER:
				restart();
				break;
			case KeyEvent.VK_ADD:
			case KeyEvent.VK_PLUS:
				tray.setIndexIncrement(1);
				break;
			case KeyEvent.VK_SUBTRACT:
			case KeyEvent.VK_MINUS:
				tray.setIndexIncrement(-1);
				break;
			case KeyEvent.VK_PAGE_UP:
				tray.gotoPreviousDirectory();
				restart();
				break;
			case KeyEvent.VK_PAGE_DOWN:
				tray.gotoNextDirectory();
				restart();
				break;
			case KeyEvent.VK_RIGHT:
				tray.setIndexIncrement(1);
				restart();
				break;
			case KeyEvent.VK_LEFT:
				tray.setIndexIncrement(-1);
				restart();
				break;
			case KeyEvent.VK_1:
				tray.skip(1);
				restart();
				break;
			case KeyEvent.VK_2:
				tray.skip(2);
				restart();
				break;
			case KeyEvent.VK_3:
				tray.skip(3);
				restart();
				break;
			case KeyEvent.VK_4:
				tray.skip(4);
				restart();
				break;
			case KeyEvent.VK_5:
				tray.skip(5);
				restart();
				break;
			case KeyEvent.VK_6:
				tray.skip(6);
				restart();
				break;
			case KeyEvent.VK_7:
				tray.skip(7);
				restart();
				break;
			case KeyEvent.VK_8:
				tray.skip(8);
				restart();
				break;
			case KeyEvent.VK_9:
				tray.skip(9);
				restart();
				break;
			case KeyEvent.VK_SPACE:
				if (paused) {
					changer = new Changer();
					timer.schedule(changer, 0L, duration);
					paused = false;
				} else {
					changer.cancel();
					paused = true;
				}
				break;
			case KeyEvent.VK_G:
				randomDirSequence = !randomDirSequence;
				tray.setRandomization(randomDirSequence, randomFileSequence,
						randomAllSequence);
				break;
			case KeyEvent.VK_Q:
				randomFileSequence = !randomFileSequence;
				tray.setRandomization(randomDirSequence, randomFileSequence,
						randomAllSequence);
				break;
			case KeyEvent.VK_R:
				randomAllSequence = !randomAllSequence;
				tray.setRandomization(randomDirSequence, randomFileSequence,
						randomAllSequence);
				break;
			default:
				r = false;
				break;
			}
		}
		return r;
	}

	// ----------------------------------------------------------
	// Implementation of MouseListener and MouseMotionListener
	// ----------------------------------------------------------
	/**
	 * Invoked when the mouse has been clicked. This means that a button has
	 * been pressed and released.
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/** Invoked when the mouse enters a component. */
	public void mouseEntered(MouseEvent e) {
	}

	/** Invoked when the mouse exits a component. */
	public void mouseExited(MouseEvent e) {
	}

	/** Invoked when a mouse button has been pressed. */
	public void mousePressed(MouseEvent e) {
	}

	/** Invoked when a mouse button has been released. */
	public void mouseReleased(MouseEvent e) {
	}

	/** Invoked when the mouse has been moved with a button down. */
	public void mouseDragged(MouseEvent e) {
	}

	/** Invoked when the mouse has been moved with no button down. */
	public void mouseMoved(MouseEvent e) {
	}

	// ----------------------------------------------------------
	// This is the mouse wheel listener implementation
	// ----------------------------------------------------------
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("ResponseManager.mouseWheelMoved() steps = "
				+ e.getWheelRotation());
	}
}
