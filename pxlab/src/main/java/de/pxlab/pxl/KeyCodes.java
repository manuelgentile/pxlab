package de.pxlab.pxl;

/**
 * Codes for defining response key codes.
 * 
 * @version 0.1.2
 */
/*
 * 
 * 10/01/02 to isolate codes from the java AWT
 * 
 * 2007/06/18 added SCREEN_BUTTON, NO_KEY
 */
public interface KeyCodes {
	public static final int NO_KEY = 256 * 256;
	public static final int LEFT_BUTTON = java.awt.event.MouseEvent.BUTTON1;
	public static final int RIGHT_BUTTON = java.awt.event.MouseEvent.BUTTON3;
	public static final int MIDDLE_BUTTON = java.awt.event.MouseEvent.BUTTON2;
	public static final int SCREEN_BUTTON = java.awt.event.MouseEvent.BUTTON3 + 1;
	public static final int ENTER_KEY = java.awt.event.KeyEvent.VK_ENTER;
	public static final int BACK_SPACE_KEY = java.awt.event.KeyEvent.VK_BACK_SPACE;
	public static final int TAB_KEY = java.awt.event.KeyEvent.VK_TAB;
	public static final int CANCEL_KEY = java.awt.event.KeyEvent.VK_CANCEL;
	public static final int CLEAR_KEY = java.awt.event.KeyEvent.VK_CLEAR;
	public static final int SHIFT_KEY = java.awt.event.KeyEvent.VK_SHIFT;
	public static final int CONTROL_KEY = java.awt.event.KeyEvent.VK_CONTROL;
	public static final int ALT_KEY = java.awt.event.KeyEvent.VK_ALT;
	public static final int PAUSE_KEY = java.awt.event.KeyEvent.VK_PAUSE;
	public static final int CAPS_LOCK_KEY = java.awt.event.KeyEvent.VK_CAPS_LOCK;
	public static final int ESCAPE_KEY = java.awt.event.KeyEvent.VK_ESCAPE;
	public static final int SPACE_KEY = java.awt.event.KeyEvent.VK_SPACE;
	public static final int PAGE_UP_KEY = java.awt.event.KeyEvent.VK_PAGE_UP;
	public static final int PAGE_DOWN_KEY = java.awt.event.KeyEvent.VK_PAGE_DOWN;
	public static final int END_KEY = java.awt.event.KeyEvent.VK_END;
	public static final int HOME_KEY = java.awt.event.KeyEvent.VK_HOME;
	public static final int LEFT_KEY = java.awt.event.KeyEvent.VK_LEFT;
	public static final int UP_KEY = java.awt.event.KeyEvent.VK_UP;
	public static final int RIGHT_KEY = java.awt.event.KeyEvent.VK_RIGHT;
	public static final int DOWN_KEY = java.awt.event.KeyEvent.VK_DOWN;
	public static final int NUMPAD0_KEY = java.awt.event.KeyEvent.VK_NUMPAD0;
	public static final int NUMPAD1_KEY = java.awt.event.KeyEvent.VK_NUMPAD1;
	public static final int NUMPAD2_KEY = java.awt.event.KeyEvent.VK_NUMPAD2;
	public static final int NUMPAD3_KEY = java.awt.event.KeyEvent.VK_NUMPAD3;
	public static final int NUMPAD4_KEY = java.awt.event.KeyEvent.VK_NUMPAD4;
	public static final int NUMPAD5_KEY = java.awt.event.KeyEvent.VK_NUMPAD5;
	public static final int NUMPAD6_KEY = java.awt.event.KeyEvent.VK_NUMPAD6;
	public static final int NUMPAD7_KEY = java.awt.event.KeyEvent.VK_NUMPAD7;
	public static final int NUMPAD8_KEY = java.awt.event.KeyEvent.VK_NUMPAD8;
	public static final int NUMPAD9_KEY = java.awt.event.KeyEvent.VK_NUMPAD9;
	public static final int INSERT_KEY = java.awt.event.KeyEvent.VK_INSERT;
	public static final int DELETE_KEY = java.awt.event.KeyEvent.VK_DELETE;
	
	public static final int E_KEY = java.awt.event.KeyEvent.VK_E;
	public static final int I_KEY = java.awt.event.KeyEvent.VK_I;
	
	
}
