package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A bar filled with a color defined by device color coordinates.
 * 
 * @author H. Irtel
 * @version 0.3.1
 */
/*
 * 02/14/01
 */
public class DeviceColorBar extends SimpleBar {
	public ExPar RedValue = new ExPar(INTEGER, 0, 255, new ExParValue(128),
			"Red device color value");
	public ExPar GreenValue = new ExPar(INTEGER, 0, 255, new ExParValue(128),
			"Green device color value");
	public ExPar BlueValue = new ExPar(INTEGER, 0, 255, new ExParValue(128),
			"Blue device color value");

	public DeviceColorBar() {
		setTitleAndTopic("Bar filled by device color", SIMPLE_GEOMETRY_DSP);
		this.Color.setType(DEPCOLOR);
	}

	protected void computeColors() {
		this.Color.set(new PxlColor(new Color(RedValue.getInt(), GreenValue
				.getInt(), BlueValue.getInt())));
	}
}
