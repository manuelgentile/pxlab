package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/*  @author M. Hodapp
 @version 0.1.0 
 @date 05/24/00 */
public class LFVertLines extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color1");

	/** Constructor creating the title of the demo. */
	public LFVertLines() {
		setTitleAndTopic("Low Frequency Vertical Lines: Moir� Test 2",
				DISPLAY_TEST_DSP | DEMO);
	}
	private int s1;

	/** Initialize the display list of the demo. */
	protected int create() {
		s1 = enterDisplayElement(new VerStripedBar(Color1));
		return (s1);
	}

	protected void computeGeometry() {
		Rectangle r1 = centeredRect(width, height, width, height);
		VerStripedBar vb1 = (VerStripedBar) getDisplayElement(s1);
		vb1.setLocation(r1.x, r1.y);
		vb1.setSize(r1.width, r1.height);
		vb1.setPhase(4, 2);
	}
}
