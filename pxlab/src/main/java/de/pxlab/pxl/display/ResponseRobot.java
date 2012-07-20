package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.event.*;
import de.pxlab.pxl.*;

/**
 * A response simulation robot. This display object should be inserted into the
 * display list immediately before a response time measurement display object.
 * It will create a response at the delay specified by the Duration parameter.
 * This display object does not show anything since it is not visible and has
 * the display list control property set. The current implementation uses a
 * background thread for timing.
 * 
 * <p>
 * The current implementation does only simulate a mouse button press/release
 * response.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 09/08/02
 */
public class ResponseRobot extends Display implements Runnable {
	private Thread robotThread = null;

	public ResponseRobot() {
		setTitleAndTopic("Response Robot", CONTROL_DSP | EXP);
		setDisplayListControl();
		setVisible(false);
		Duration.set(500);
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
	}

	public boolean displayListControlState(ResponseController rc) {
		if (robotThread == null) {
			robotThread = new Thread(this);
			robotThread.setPriority(Thread.MIN_PRIORITY);
		}
		robotThread.start();
		return (true);
	}

	/** Implements the Runnable interface. */
	public void run() {
		try {
			// System.out.println("ResponseRobot.run() waiting ...");
			Thread.sleep(Duration.getInt());
		} catch (InterruptedException iex) {
		}
		// System.out.println("ResponseRobot.run()         ... generating event.");
		/*
		 * if (display Panel instanceof RealTimeDisplayPanel) {
		 * RealTimeDisplayPanel rtd = (RealTimeDisplayPanel)display Panel;
		 * rtd.mousePressed(new MouseEvent(rtd, MouseEvent.MOUSE_PRESSED,
		 * HiresClock.getTimeNanos(), InputEvent.BUTTON1_MASK, 0, 0, 1, false));
		 * rtd.mouseReleased(new MouseEvent(rtd, MouseEvent.MOUSE_RELEASED,
		 * HiresClock.getTimeNanos(), InputEvent.BUTTON1_MASK, 0, 0, 1, false));
		 * }
		 */
	}

	/**
	 * Make sure that the response robot's timing thread is destroyed.
	 */
	protected void destroy() {
		if (robotThread != null) {
			try {
				robotThread.join();
			} catch (InterruptedException e) {
			}
			robotThread = null;
		}
		super.destroy();
	}
}
