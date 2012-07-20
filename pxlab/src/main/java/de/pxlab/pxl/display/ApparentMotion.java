package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * An abstract superclass for apparent motion demos.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 06/27/01
 */
abstract public class ApparentMotion extends Display {
	/** Color of moving objects. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Color of the target object");
	/**
	 * Timer for controlling the ON and OFF periods. This should always be set
	 * to de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER.
	 */
	public ExPar OnOffTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"On/Off interval timer");
	/** ON period duration. */
	public ExPar OnDuration = new ExPar(DURATION, 0, 300, new ExParValue(60),
			"On interval duration");
	/** OFF period duration. */
	public ExPar OffDuration = new ExPar(DURATION, 0, 500, new ExParValue(200),
			"Off interval duration");

	public boolean isAnimated() {
		return (true);
	}
}
