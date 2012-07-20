package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/*  @author M. Hodapp
 @version 0.1.0 
 @date 05/18/00 */
public class LinChStairs extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new YxyColor(42.16,
			0.286, 0.253)), "First ramp color");
	public ExPar Color8 = new ExPar(COLOR, new ExParValue(new YxyColor(48.14,
			0.437, 0.372)), "Last ramp color");
	public ExPar Color2 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Second ramp color");
	public ExPar Color3 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Third ramp color");
	public ExPar Color4 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Fourth ramp color");
	public ExPar Color5 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Fifth ramp color");
	public ExPar Color6 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Sixth ramp color");
	public ExPar Color7 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.black)), "Seventh ramp color");

	public LinChStairs() {
		setTitleAndTopic("Linear Chromatic Stairs", LATERAL_INHIBITION_DSP
				| DEMO);
	}
	private int s1, s2, s3, s4, s5, s6, s7, s8;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new Bar(Color1));
		s2 = enterDisplayElement(new Bar(Color2));
		s3 = enterDisplayElement(new Bar(Color3));
		s4 = enterDisplayElement(new Bar(Color4));
		s5 = enterDisplayElement(new Bar(Color5));
		s6 = enterDisplayElement(new Bar(Color6));
		s7 = enterDisplayElement(new Bar(Color7));
		s8 = enterDisplayElement(new Bar(Color8));
		return (s1);
	}

	protected void computeColors() {
		PxlColor[] ramp = Color1.getPxlColor().linearRampTo(
				Color8.getPxlColor(), 8);
		for (int i = 0; i < 8; i++)
			Color2.set(ramp[1]);
		Color3.set(ramp[2]);
		Color4.set(ramp[3]);
		Color5.set(ramp[4]);
		Color6.set(ramp[5]);
		Color7.set(ramp[6]);
	}

	protected void computeGeometry() {
		// get the squares
		if (width > height) {
			int b = (int) (width / 16.0);
			int h = (int) (height / 2.0);
			int YPos = (int) (-height / 8.0);
			Rectangle r = centeredRect(width, height, 8 * b, h);
			Rectangle r1 = new Rectangle(r.x, r.y, b, h);
			Rectangle r2 = new Rectangle(r.x + b, r.y, b, h);
			Rectangle r3 = new Rectangle(r.x + 2 * b, r.y, b, h);
			Rectangle r4 = new Rectangle(r.x + 3 * b, r.y, b, h);
			Rectangle r5 = new Rectangle(r.x + 4 * b, r.y, b, h);
			Rectangle r6 = new Rectangle(r.x + 5 * b, r.y, b, h);
			Rectangle r7 = new Rectangle(r.x + 6 * b, r.y, b, h);
			Rectangle r8 = new Rectangle(r.x + 7 * b, r.y, b, h);
			getDisplayElement(s1).setRect(r1);
			getDisplayElement(s2).setRect(r2);
			getDisplayElement(s3).setRect(r3);
			getDisplayElement(s4).setRect(r4);
			getDisplayElement(s5).setRect(r5);
			getDisplayElement(s6).setRect(r6);
			getDisplayElement(s7).setRect(r7);
			getDisplayElement(s8).setRect(r8);
		} else {
			int b = (int) (2.0 * width / 4.0);
			int h = (int) (height / 16.0);
			Rectangle r = centeredRect(width, height, b, 8 * h);
			Rectangle r1 = new Rectangle(r.x, r.y, b, h);
			Rectangle r2 = new Rectangle(r.x, r.y + h, b, h);
			Rectangle r3 = new Rectangle(r.x, r.y + 2 * h, b, h);
			Rectangle r4 = new Rectangle(r.x, r.y + 3 * h, b, h);
			Rectangle r5 = new Rectangle(r.x, r.y + 4 * h, b, h);
			Rectangle r6 = new Rectangle(r.x, r.y + 5 * h, b, h);
			Rectangle r7 = new Rectangle(r.x, r.y + 6 * h, b, h);
			Rectangle r8 = new Rectangle(r.x, r.y + 7 * h, b, h);
			getDisplayElement(s1).setRect(r1);
			getDisplayElement(s2).setRect(r2);
			getDisplayElement(s3).setRect(r3);
			getDisplayElement(s4).setRect(r4);
			getDisplayElement(s5).setRect(r5);
			getDisplayElement(s6).setRect(r6);
			getDisplayElement(s7).setRect(r7);
			getDisplayElement(s8).setRect(r8);
		}
	}
}
