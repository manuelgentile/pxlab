package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

public class PowerSupplyTest extends FrameAnimation {
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.BLACK)), "Bar color");
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)),
			"Background frame color");
	public ExPar FullScreenBar = new ExPar(FLAG, new ExParValue(0),
			"Flag to show full screen bar");
	private ExPar Color1 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "");
	private ExPar Color2 = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "");

	public PowerSupplyTest() {
		setTitleAndTopic("Power Supply Test", DISPLAY_TEST_DSP | DEMO);
		FrameDuration.set(500);
		FramesPerCycle.setType(RTDATA);
	}
	protected DisplayElement rect, bar1, bar2;
	protected int border = 8;

	protected int create() {
		rect = new Clear(BackgroundColor);
		bar1 = new Bar(Color1);
		bar2 = new Bar(Color2);
		enterDisplayElement(rect, group[0]);
		int s = enterDisplayElement(bar1, group[0]);
		enterDisplayElement(bar2, group[0]);
		enterTiming(FrameTimer, FrameDuration, 0);
		setFramesPerCycle(2);
		return s;
	}

	protected void computeColors() {
		Color1.set((PxlColor) (Color.getPxlColor().clone()));
		if (FullScreenBar.getFlag()) {
			Color2.set((PxlColor) (Color.getPxlColor().clone()));
		} else {
			Color2.set((PxlColor) (BackgroundColor.getPxlColor().clone()));
		}
	}

	protected void computeGeometry() {
		Rectangle r = centeredRect(width, height, width - 2 * border, height
				- 2 * border);
		int h = r.height / 3;
		bar1.setRect(r);
		bar2.setRect(r.x, r.y + h, r.width, r.height - 2 * h);
	}

	public void computeAnimationFrame(int frame) {
		if (frame == 0) {
			Color1.set(Color.getPxlColor());
			Color2.set(FullScreenBar.getFlag() ? Color.getPxlColor()
					: BackgroundColor.getPxlColor());
		} else {
			Color1.set(BackgroundColor.getPxlColor());
			Color2.set(FullScreenBar.getFlag() ? BackgroundColor.getPxlColor()
					: Color.getPxlColor());
		}
	}
}
