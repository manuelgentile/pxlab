package de.pxlab.pxl;

import java.util.ArrayList;

/**
 * Objects which implement this interface can control spurious responses.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public interface ResponseController {
	/**
	 * Set the state for collecting responses during in-active response timer
	 * periods.
	 * 
	 * @param state
	 *            if true then spurious responses are collected, if false they
	 *            are ignored.
	 */
	public void setWatchSpuriousResponses(boolean state);

	/**
	 * Check whether spuriuos responses have been found since the last clear()
	 * message.
	 * 
	 * @return true if there was a response in the inactive ResponseTimer state.
	 */
	public boolean hasSpuriousResponse();

	/**
	 * Get the first spuriuos responses.
	 * 
	 * @return the first spurious response found or null if there was none since
	 *         the last clear() message.
	 */
	public ResponseEvent getFirstSpuriousResponse();

	/**
	 * Get the most recent spuriuos responses.
	 * 
	 * @return the latest spurious response found or null if there was none
	 *         since the last clear() message.
	 */
	public ResponseEvent getLastSpuriousResponse();

	/**
	 * Get the spuriuos responses since the last clear() message.
	 * 
	 * @return the arra of spurious responses found since the last clear()
	 *         message.
	 */
	public ArrayList getSpuriousResponse();
}
