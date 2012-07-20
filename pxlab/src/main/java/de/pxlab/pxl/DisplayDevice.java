package de.pxlab.pxl;

import java.awt.*;

/**
 * A device which can show experimental display objects.
 * 
 * @version 0.1.1
 */
/*
 * 
 * 2006/12/29 FULL_SCREEN_FRAME
 */
public interface DisplayDevice {
	/** A display window embedded into a decorated frame. */
	public static final int FRAMED_WINDOW = 0;
	/** A display window embedded into an undecorated frame. */
	public static final int UNFRAMED_WINDOW = 1;
	/** An ordinary full screen display window. */
	public static final int FULL_SCREEN = 2;
	/** An ordinary full screen display window. */
	public static final int FULL_SECONDARY_SCREEN = 3;
	/**
	 * A full screen exclusive mode window. This mode should guarantee the best
	 * possible performance on most graphics devices. Note, however, that this
	 * mode should be thoroughly tested before being used, since some display
	 * objects do not work properly with this mode.
	 */
	public static final int FULL_SCREEN_EXCLUSIVE = 4;
	/**
	 * !!! THIS MODE DOES NOT WORK !!! A full screen exclusive mode window on
	 * the secondary screen device. This mode should guarantee the best possible
	 * performance on most graphics devices which can control two independent
	 * dsiplays.
	 */
	public static final int FULL_SECONDARY_SCREEN_EXCLUSIVE = 5;
	/** Two framed windows on a single device. */
	public static final int DUAL_FRAMED_WINDOW = 6;
	/** Two unframed windows on a single device. */
	public static final int DUAL_UNFRAMED_WINDOW = 7;
	/** Two ordinary full screen windows on two different devices. */
	public static final int DUAL_FULL_SCREEN = 8;
	/**
	 * !!! THIS MODE DOES NOT WORK !!! Two full screen exclusive mode windows on
	 * two different devices.
	 */
	public static final int DUAL_FULL_SCREEN_EXCLUSIVE = 9;
	public static final int FULL_SCREEN_FRAME = 10;
	/** Show display on left/primary screen. */
	public static final int PRIMARY_SCREEN = 1;
	/** Show display on right/secondary screen. */
	public static final int SECONDARY_SCREEN = 2;

	public void open();

	public void close();

	public void dispose();

	public void setActiveScreen(int s);

	public void setHiddenCursor(boolean s);

	public Graphics getGraphics();

	public Image createMemoryBuffer(TimingElement t);

	public void show();

	public Component getComponent();
}
