package de.pxlab.pxl;

/**
 * Listens to media controller events.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
public interface MediaEventListener {
	/** The media controller has created some media action. */
	public void mediaActionPerformed(MediaEvent e);
}
