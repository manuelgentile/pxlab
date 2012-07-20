package de.pxlab.pxl;

import java.awt.Frame;

/*
 import javax.swing.JOptionPane;
 import javax.swing.ImageIcon;
 */

import de.pxlab.awtx.MessageDialog;

/**
 * This is a modal dialog which is called upon non-fatal errors.
 * 
 * @author H. Irtel
 * @version 0.2.2
 */
public class NonFatalError extends MessageDialog {
	/**
	 * Create an error dialog window showing the given error message. The window
	 * is closed when the user presses the 'close' button. The dialog blocks
	 * program execution until it is closed.
	 */
	public NonFatalError(String message) {
		super(Base.getFrame(), " Error", message);
		Syslog.out.println("NonFatalError: " + message);
		setVisible(true);
	}
}
/*
 * public class NonFatalError { public NonFatalError(String message) {
 * JOptionPane.showMessageDialog(Base.getFrame(), message, " Non Fatal Error",
 * JOptionPane.WARNING_MESSAGE, new ImageIcon(FileBase.loadImage(
 * "/de/pxlab/images/icons/dialogGraphics/Warning48.png"))); } }
 */
