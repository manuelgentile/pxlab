package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/*  @author M. Hodapp
 @version 0.1.0 
 @date 05/24/00 */
public class HFHorLines extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color");

	/** Constructor creating the title of the demo. */
	public HFHorLines() {
		setTitleAndTopic("High Frequency Horizontal Lines: Moir� Test 5",
				DISPLAY_TEST_DSP | DEMO);
	}
	private int s1;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new HorStripedBar(Color1));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle r1 = centeredRect(width, height, width, height);
		HorStripedBar hb1 = (HorStripedBar) getDisplayElement(s1);
		hb1.setLocation(r1.x, r1.y);
		hb1.setSize(r1.width, r1.height);
		hb1.setPhase(2, 1);
	}
}
