package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * Demonstrates illumination device control.
 * 
 * @version 0.1.1
 */
public class IlluminationDeviceControlDemo extends IlluminationDeviceControl {
	public IlluminationDeviceControlDemo() {
		setTitleAndTopic("Illumination Device Control Demo", EXTERNAL_DSP
				| DEMO);
		Timer.set(de.pxlab.pxl.TimerCodes.NO_TIMER);
	}

	public boolean isGraphic() {
		return true;
	}
	protected int bar;

	protected int create() {
		bar = enterDisplayElement(new Bar(this.Color), group[0]);
		super.create();
		return bar;
	}

	protected void computeGeometry() {
		super.computeGeometry();
		getDisplayElement(bar).setCenterAndSize(0, 0, width, height);
	}
}
