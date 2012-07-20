package de.pxlab.pxl;

/**
 * An external signal like an LED connected to an external control box or a
 * trigger signal used to synchronize external devices. Note that external
 * control boxes are not supported by applets.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 07/04/02 check whether the external control box is open before showing an
 * external signal. This is necessary for editors which do not open the box
 * properly.
 */
public class ExternalControlSignal extends DisplayElement {
	protected int signalCode;
	protected boolean onSignal;
	protected boolean isTrigger;

	/**
	 * Create an external control signal.
	 * 
	 * @param trg
	 *            if true then this is a trigger signal which has an arbitrary
	 *            short duration and needs not be switched off.
	 * @param onOff
	 *            if the parameter trg is false then the signal is a steady
	 *            state on or off signal. If onOff is true then this control
	 *            signal is used to switch on the external signal. If the
	 *            parameter onOff is false then this object is used to switch
	 *            off the signal.
	 */
	public ExternalControlSignal(boolean trg, boolean onOff) {
		type = DisplayElement.EXTERNAL_SIGNAL;
		isTrigger = trg;
		onSignal = onOff;
	}

	/**
	 * Set the steady state signal code. If external control boxes have more
	 * than a single control signal then this code identifies the signal.
	 */
	public void setCode(int code) {
		signalCode = code;
	}

	/** Show this external control box signal. */
	public void show() {
		if (Base.isApplication() && ExternalControlBox.isOpen()) {
			if (isTrigger) {
				ExternalControlBox.trigger();
			} else {
				if (onSignal) {
					ExternalControlBox.setSignal(signalCode);
				} else {
					ExternalControlBox.clearSignal(signalCode);
				}
			}
		}
	}
}
