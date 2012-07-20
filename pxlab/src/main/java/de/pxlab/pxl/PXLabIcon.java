package de.pxlab.pxl;

import java.awt.*;

/**
 * Creates an image of the PXLab logo.
 * 
 * @version 0.1.0
 * @author H. Irtel
 */
public class PXLabIcon {
	private static Image imageBuffer = null;
	private static int size = 0;

	/**
	 * Set the given frame's icon to be the PXLabIcon.
	 * 
	 * @param frame
	 *            the frame object which should be decorated.
	 */
	public static void decorate(Frame frame) {
		frame.addNotify();
		frame.setIconImage(instance(frame, 32));
	}

	/**
	 * Create a new instance of the PXLab icon for the given component. Note
	 * that this component's peer must be available in order to create the
	 * image. This means that addNotify() has been called before this method is
	 * called.
	 * 
	 * @param component
	 *            the component which acts as an ImageObserver for this image.
	 * @param size
	 *            the size of the icon.
	 */
	public static Image instance(Component component, int size) {
		if (PXLabIcon.size == size)
			return imageBuffer;
		imageBuffer = component.createImage(size, size);
		if (imageBuffer == null)
			System.out
					.println("PXLabImage.instance() can't create image buffer.");
		PXLabIcon.size = size;
		Graphics g = imageBuffer.getGraphics();
		if (g == null)
			System.out.println("PXLabImage.instance() can't create Graphics.");
		Rectangle cRect0 = new Rectangle(0, 0, size, size);
		Rectangle cRect1 = DisplaySupport.innerRect(cRect0, 0.8);
		cRect1.y = (int) (cRect0.y + (cRect0.height - cRect1.height) * 0.75);
		Rectangle cRect2 = DisplaySupport.innerRect(cRect1, 6.0 / 8.0);
		cRect2.y = (int) (cRect1.y + (cRect1.height - cRect2.height) * 0.75);
		Rectangle cRect3 = DisplaySupport.innerRect(cRect2, 2.0 / 3.0);
		cRect3.y = (int) (cRect2.y + (cRect2.height - cRect3.height) * 0.75);
		g.setColor(new Color(255, 240, 60));
		g.fillRect(cRect0.x, cRect0.y, cRect0.width, cRect0.height);
		g.setColor(new Color(254, 223, 0));
		g.fillRect(cRect1.x, cRect1.y, cRect1.width, cRect1.height);
		g.setColor(new Color(255, 203, 44));
		g.fillRect(cRect2.x, cRect2.y, cRect2.width, cRect2.height);
		g.setColor(new Color(253, 184, 16));
		g.fillRect(cRect3.x, cRect3.y, cRect3.width, cRect3.height);
		g.dispose();
		return (imageBuffer);
	}
}
