package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * 256 device intensity steps.
 * 
 * @version 0.1.0
 */
/*
 * 
 * 07/11/18
 */
public class DeviceIntensitySteps extends Display implements
		CalibrationChannelBitCodes {
	public ExPar ChannelBitCodes = new ExPar(GEOMETRY_EDITOR,
			CalibrationChannelBitCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.CalibrationChannelBitCodes.WHITE_CHANNEL"),
			"Color channel bit codes");
	public ExPar IntensityStep = new ExPar(1, 8, new ExParValue(4),
			"Intensity step size (power of 2)");

	public DeviceIntensitySteps() {
		setTitleAndTopic("Device Intensity Steps", DISPLAY_TEST_DSP | DEMO);
	}
	private int step;
	private boolean blue, green, red;

	protected int create() {
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		int b = ChannelBitCodes.getInt();
		red = (b & RED_CHANNEL) != 0;
		green = (b & GREEN_CHANNEL) != 0;
		blue = (b & BLUE_CHANNEL) != 0;
		step = largestPowerOf2(IntensityStep.getInt());
	}

	private int largestPowerOf2(int x) {
		int y = 0;
		if (x > 0) {
			y++;
			for (int i = 2; i < Integer.MAX_VALUE / 4; i = i << 1) {
				if ((x & i) != 0)
					y = i;
			}
		}
		return y;
	}

	public void show(Graphics g) {
		super.show(g);
		int n = 256 / step;
		int dx = step * (width / 256);
		int dy = height / 3;
		int dy0 = 2 * dy;
		int w1 = (n - 1) * dx;
		int x1 = -w1 / 2;
		int y1 = -dy / 2;
		int x0 = x1 - dx / 2;
		int y0 = -dy0 / 2;
		g.setColor(devColor(0));
		g.fillRect(x0, y0, dx, dy0);
		x0 += dx;
		for (int i = step; i < 256; i += step) {
			g.setColor(devColor(i - step + step - 1));
			g.fillRect(x0, y0, dx, dy0);
			x0 += dx;
			g.setColor(devColor(i + step - 1));
			g.fillRect(x1, y1, dx, dy);
			x1 += dx;
		}
	}

	private Color devColor(int c) {
		return new Color(red ? c : 0, green ? c : 0, blue ? c : 0);
	}
}
