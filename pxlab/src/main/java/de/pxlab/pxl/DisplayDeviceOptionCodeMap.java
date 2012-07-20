package de.pxlab.pxl;

import de.pxlab.util.OptionCodeMap;

/**
 * Maps sloppy verbal descriptions of display device modes to their respective
 * numeric values.
 * 
 * @version 0.4.0
 */
public class DisplayDeviceOptionCodeMap extends OptionCodeMap {
	public DisplayDeviceOptionCodeMap() {
		super("Display Device Options", true);
		put("framed", DisplayDevice.FRAMED_WINDOW,
				"framed window on primary screen");
		put("unframed", DisplayDevice.UNFRAMED_WINDOW,
				"unframed window on primary screen");
		put("fullscreen", DisplayDevice.FULL_SCREEN,
				"full screen mode on primary screen");
		put("exclusive", DisplayDevice.FULL_SCREEN_EXCLUSIVE,
				"full screen exclusive mode on primary screen");
		put("secondary", DisplayDevice.FULL_SECONDARY_SCREEN,
				"full screen mode on secondary screen");
		put("secexclusive", DisplayDevice.FULL_SECONDARY_SCREEN_EXCLUSIVE,
				"full screen exclusive mode on secondary screen");
		put("dualframed", DisplayDevice.DUAL_FRAMED_WINDOW,
				"dual framed windows on primary screen");
		put("dualunframed", DisplayDevice.DUAL_UNFRAMED_WINDOW,
				"dual unframed window on primary screen");
		put("dualfull", DisplayDevice.DUAL_FULL_SCREEN, "dual full screen mode");
		put("dualexclusive", DisplayDevice.DUAL_FULL_SCREEN_EXCLUSIVE,
				"dual full screen exclusive mode");
	}
}
