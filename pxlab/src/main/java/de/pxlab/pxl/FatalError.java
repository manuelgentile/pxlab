package de.pxlab.pxl;

import java.awt.Frame;

import de.pxlab.awtx.MessageDialog;

/**
 * This is a modal dialog which is called upon fatal errors.
 * 
 * @author H. Irtel
 * @version 0.2.3
 */
public class FatalError extends MessageDialog {
	/**
	 * Create a fatal error dialog window showing the given error message. The
	 * window is closed when the user presses the 'close' button. The dialog
	 * blocks program execution until it is closed.
	 */
	public FatalError(String message) {
		super(Base.getFrame(), " Error", message);
		setButtonLabel("  Exit  ");
		if (Debug.isActive(Debug.ERR_STACK_TRACE)) {
			new RuntimeException().printStackTrace();
		}
		setVisible(true);
	}

	public void closeDialog() {
		super.closeDialog();
		System.exit(3);
	}
}
