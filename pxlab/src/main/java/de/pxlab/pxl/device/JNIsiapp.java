/* JNIsiapp.java -- 3DxWare interface for JAVA
 *
 * Copyright 3Dconnexion 2003
 *
 * Author:  Jim Wick, March/April, 2003
 *
 * This is a suggested interface to the 3DxWare driver for Java.
 *
 * Using JNI and lots of Windows-specific code and mechanisms, this
 * is not in any way portable.
 *
 * It is primarily a JNI wrapper layer over the existing SDK C static 
 * library.  It parallels the design of the 3DxWare SDK for Windows.
 * Most of the functions described in the Windows documentation are
 * supported.  The Windows SDK is not needed, but the Windows SDK
 * documentation would be very useful.
 *
 * Currently the handle the SDK library is kept in global memory in the
 * JNI code (also provided) so only one connection can be made to the
 * driver at a time -- this limitation can easily be eliminated.
 *
 * The basic idea is to dedicate a Java thread for connecting to the
 * driver using this class.  Have that thread will wait in a call to
 * SiWaitForEvent.  When it gets woken up, new data is available from
 * the device and that should be communicated to your Java program
 * in whatever way is appropriate for your application.
 * 
 * Please contact 3Dconnexion:  (www.3dconnexion.com/request/support)
 * with any question or comments.
 */
package de.pxlab.pxl.device;

import java.awt.Component;
import de.pxlab.pxl.FatalError;

/**
 * The Java native code interface for the SpaceMouse adapted to PXLab.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2007/02/07
 */
public class JNIsiapp {
	public static final String JAWT_DLL = "jawt";
	public static final String JNISIAPP_DLL = "JNIsiapp";
	// Static block loads the C DLL that contains all the JNI code
	static {
		try {
			System.loadLibrary(JAWT_DLL);
		} catch (SecurityException sx) {
			new FatalError(
					"The security manager prohibits loading of the library "
							+ JAWT_DLL);
		} catch (UnsatisfiedLinkError ul) {
			new FatalError("Can't find the library " + JAWT_DLL);
		} catch (NullPointerException nx) {
		}
		try {
			System.loadLibrary(JNISIAPP_DLL);
		} catch (SecurityException sx) {
			new FatalError(
					"The security manager prohibits loading of the library "
							+ JNISIAPP_DLL);
		} catch (UnsatisfiedLinkError ul) {
			new FatalError("Can't find the library " + JNISIAPP_DLL);
		} catch (NullPointerException nx) {
		}
	}
	// Constant return values (e.g., JNIsiapp.SpwRetVal.SPW_NO_ERROR)
	public static class SpwRetVal {
		public final static int SPW_NO_ERROR = 0; /* No error. */
		public final static int SPW_ERROR = 1; /* Error -- function failed. */
		public final static int SI_BAD_HANDLE = 2; /* Invalid SpaceWare handle. */
		public final static int SI_BAD_ID = 3; /* Invalid device ID. */
		public final static int SI_BAD_VALUE = 4; /* Invalid argument value. */
		public final static int SI_IS_EVENT = 5; /* Event is a SpaceWare event. */
		public final static int SI_SKIP_EVENT = 6; /*
													 * Skip this SpaceWare
													 * event.
													 */
		public final static int SI_NOT_EVENT = 7; /*
												 * Event is not a SpaceWare
												 * event.
												 */
		public final static int SI_NO_DRIVER = 8; /*
												 * SpaceWare driver is not
												 * running.
												 */
		public final static int SI_NO_RESPONSE = 9; /*
													 * SpaceWare driver is not
													 * responding.
													 */
		public final static int SI_UNSUPPORTED = 10; /*
													 * The function is
													 * unsupported by this
													 * version.
													 */
		public final static int SI_UNINITIALIZED = 11; /*
														 * SpaceWare input
														 * library is
														 * uninitialized.
														 */
		public final static int SI_WRONG_DRIVER = 12; /*
													 * Driver is incorrect for
													 * this SpaceWare version.
													 */
		public final static int SI_INTERNAL_ERROR = 13; /*
														 * Internal SpaceWare
														 * error.
														 */
		public final static int SI_BAD_PROTOCOL = 14; /*
													 * The transport protocol is
													 * unknown.
													 */
		public final static int SI_OUT_OF_MEMORY = 15; /*
														 * Unable to malloc
														 * space required.
														 */
		public final static int SPW_DLL_LOAD_ERROR = 16; /*
														 * Could not load siapp
														 * dlls
														 */
		public final static int SI_NOT_OPEN = 17; /* Spaceball device not open */
		public final static int SI_ITEM_NOT_FOUND = 18; /* Item not found */
	}
	public static class SiEventType {
		public final static int SI_BUTTON_EVENT = 1;
		public final static int SI_MOTION_EVENT = 2;
		public final static int SI_COMBO_EVENT = 3; /* Not implemented */
		public final static int SI_ZERO_EVENT = 4;
		public final static int SI_EXCEPTION_EVENT = 5;
		public final static int SI_OUT_OF_BAND = 6;
		public final static int SI_ORIENTATION_EVENT = 7;
		public final static int SI_KEYBOARD_EVENT = 8;
		public final static int SI_LPFK_EVENT = 9;
	}
	// Indices that can be used to access the mData array in SiSpwEvent
	public final static int SI_TX = 0; /* Translation X value */
	public final static int SI_TY = 1; /* Translation Y value */
	public final static int SI_TZ = 2; /* Translation Z value */
	public final static int SI_RX = 3; /* Rotation X value */
	public final static int SI_RY = 4; /* Rotation Y value */
	public final static int SI_RZ = 5; /* Rotation Z value */
	public final static int SI_UI_ALL_CONTROLS = -1;
	public final static int SI_UI_NO_CONTROLS = 0;
	public class SiButtonData {
		public int last; /* Buttons pressed as of last event */
		public int current; /* Buttons pressed as of this event */
		public int pressed; /* Buttons pressed this event */
		public int released; /* Buttons released this event */
	}
	// 3DxWare Event data
	public class SiSpwEvent {
		public int type; /* event type (one of SiEventType constants) */
		public SiButtonData bData = new SiButtonData(); /* Button data */
		public int[] mData = new int[6]; /* Motion data (index via SI_TX, etc) */
		public int period; /* Period (milliseconds) */
	}
	// Set aside memory for an event
	public SiSpwEvent event = new SiSpwEvent();

	/*
	 * These are wrappers around the Si* calls that are in the 3DxWare SDK. More
	 * specifically, they are JNI wrappers that wrap the Si* calls in the
	 * 3DxWare SDK static library, which in turn, wrap the 3DxWare DLL
	 * functions.
	 */
	public native int SiInitialize();

	public native int SiSetUiMode(int mode);

	public native int SiGetNumDevices();

	// public native int SiOpenWinInit(String caption, String callbackName);
	public native int SiOpenWinInit(Component component, String callbackName);

	public native int SiOpen();

	public native int SiClose();

	public native void SiTerminate();

	public native void SiWaitForEvent();

	public native boolean SiIsNewDataAvail();

	public native int SiGetEvent();

	public native int SiBeep(String beepString);

	public native int SiRezero();

	public native int SiGrabDevice(boolean exclusive);

	public native int SiReleaseDevice();

	public native String SpwErrorString(int errVal);

	/*
	 * This could be used to notify the caller that an event has occurred.
	 * Unfortunately, it doesn't seem to be a very useful mechanism because I
	 * can't seem to notify() another other threads from this callback. ??
	 * Probably bad JVM/Jobj context. If you decide to use it, you have to
	 * define a constant in the JNI code to #ifdefine that code back in.
	 */
	private void eventCallback() {
		// System.out.println("JNIsiapp: In eventCallback---");
	}

	// The main just unit-tests whether the lib can find and start the driver
	public static void main(String[] args) {
		JNIsiapp siapp = new JNIsiapp();
		siapp.SiInitialize();
	}
}
