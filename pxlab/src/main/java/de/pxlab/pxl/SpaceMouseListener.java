package de.pxlab.pxl;

/**
 * A listener for events genarated by a SpaceMouse.
 * 
 * @version 0.1.0
 */
public interface SpaceMouseListener {
	public void spaceMouseAxis(SpaceMouseEvent e);

	public void spaceMouseButton(SpaceMouseEvent e);

	public void spaceMouseZero(SpaceMouseEvent e);
}
