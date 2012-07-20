package de.pxlab.pxl;

/**
 * A filled rectangle with a 3D-look.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class Bar3D extends Bar {
	public Bar3D(ExPar i) {
		super(i);
	}

	/**
	 * Show this Bar. The display context has been set in the static area of
	 * class DisplayElement. This method can show a dithered version of this Bar
	 * if dithering has been set.
	 */
	public void show() {
		if (dither != null) {
			super.show();
		} else {
			if ((size.width > 0) && (size.height > 0)) {
				graphics.setColor(colorPar.getDevColor());
				graphics.fill3DRect(location.x, location.y, size.width,
						size.height, true);
			}
			setBounds(location.x, location.y, size.width, size.height);
			showSelection();
		}
	}
}
