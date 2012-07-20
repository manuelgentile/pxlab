package de.pxlab.pxl;

/**
 * Describes a group of resonses which may be valid responses for a certain
 * response timer. A response has three properties: The device it comes from
 * (mouse or keyboard), the direction (key/button up or down) and the key or
 * button code. If any of these properties is undefined then it is not checked.
 * The direction and device codes are valid if the mask bits contain the
 * response event value bits. The code parameter is valid if it is contained in
 * the currently active array of codes.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see ResponseEvent
 */
public class ResponseEventGroup {
	private int deviceMask;
	private int directionMask;
	private int[] activeCodes = null;
	private int nCodes;
	private boolean checkDevice;
	private boolean checkDirection;
	private boolean checkCodes;

	public ResponseEventGroup() {
		checkDevice = false;
		checkDirection = false;
		checkCodes = false;
	}

	public ResponseEventGroup(int device, int direction, int[] codes) {
		set(device, direction, codes);
	}

	public void set(int device, int direction, int[] codes) {
		Debug.show(Debug.EVENTS, "ResponseEventGroup.set() device = " + device
				+ ", direction = " + direction);
		deviceMask = device;
		checkDevice = (deviceMask != 0);
		directionMask = direction;
		checkDirection = (directionMask != 0);
		checkCodes = ((codes != null) && (codes.length != 0));
		if (checkCodes) {
			activeCodes = codes;
			nCodes = activeCodes.length;
		}
	}

	public int getDeviceMask() {
		return (deviceMask);
	}

	public int getDirectionMask() {
		return (directionMask);
	}

	public int[] getActiveCodes() {
		return (activeCodes);
	}

	/**
	 * Check whether the response event given as an argument is a valid response
	 * for this response code group. A response has three properties: The device
	 * it comes from (mouse or keyboard), the direction (key/button up or down)
	 * and the key or button code. If any of these properties is undefined in
	 * the mask then it is not checked. The direction and device codes are valid
	 * if the mask bits contain the event value bits. The code parameter is
	 * valid if it is contained in the currently active set of codes.
	 * 
	 * @param e
	 *            the response event which should be checked.
	 * @return -1 if the response is invalid. Otherwise the response code is
	 *         returned. If an active code array is defined then the device
	 *         response code is translated into the array index corresponding to
	 *         the respective code.
	 */
	public int getCode(ResponseEvent e) {
		int r = -1;
		if (Debug.isActive(Debug.EVENTS)) {
			System.out.println("ResponseEventGroup.getCode(): ");
			System.out.println("  Device: "
					+ (checkDevice ? "[checked] " : "[not checked] ")
					+ e.getDevice() + " in " + deviceMask + " ?");
			System.out.println("  Direction: "
					+ (checkDirection ? "[checked] " : "[not checked] ")
					+ e.getDirection() + " in " + directionMask + " ?");
			System.out.print("  Code: "
					+ (checkCodes ? "[checked] " : "[not checked] ")
					+ e.getCode() + " in ");
			System.out.print("[");
			if (activeCodes != null) {
				for (int i = 0; i < activeCodes.length; i++)
					System.out.print(" " + activeCodes[i]);
			}
			System.out.println("]");
		}
		if (checkDevice && ((deviceMask & e.getDevice()) == 0)) {
		} else if (checkDirection && ((directionMask & e.getDirection()) == 0)) {
		} else {
			int code = e.getCode();
			if (checkCodes) {
				for (int i = 0; i < nCodes; i++) {
					if (activeCodes[i] == code) {
						r = i;
						break;
					}
				}
			} else {
				r = code;
			}
		}
		if (Debug.isActive(Debug.EVENTS)) {
			System.out.println("  returns " + r);
		}
		return r;
	}
}
