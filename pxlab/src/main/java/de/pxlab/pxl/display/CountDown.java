package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

/**
 * A blinking dot which can be used as a count down mechanism. The Timer
 * parameter of this Display object should always be set to
 * de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER. The ON-Duration is controlled by the
 * Duration parameter and the OFF-Duration by the parameter OffDuration. Note
 * that the off-phase of the last period is not shown!
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class CountDown extends Display {
	/**
	 * The number of on/off periods. The off-phase of the last period is not
	 * shown!
	 */
	public ExPar NumberOfPeriods = new ExPar(SMALL_INT, new ExParValue(5),
			"Number of periods");
	/** Off-phase duration. */
	public ExPar OffDuration = new ExPar(DURATION, new ExParValue(500),
			"OFF period duration");
	/** Dot color. */
	public ExPar Color = new ExPar(COLOR, new ExParValue(new ExParExpression(
			ExParExpression.LIGHT_GRAY)), "Color of the dot");
	/** Horizontal location of the dot center. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal location");
	/** Vertical location of the dot center. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical location");
	/** Dot diameter. */
	public ExPar Size = new ExPar(SCREENSIZE, new ExParValue(40), "Dot size");

	public CountDown() {
		setTitleAndTopic("Count Down Sequence", ATTEND_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"));
	}
	private int firstDisplayElement;
	private int lastDisplayElement;
	private int firstTimingElement;
	private int lastTimingElement;

	protected int create() {
		firstDisplayElement = nextDisplayElementIndex();
		firstTimingElement = nextTimingElementIndex();
		return (firstDisplayElement);
	}

	protected void computeGeometry() {
		int n = NumberOfPeriods.getInt();
		removeDisplayElements(firstDisplayElement, lastDisplayElement);
		removeTimingElements(firstTimingElement, lastTimingElement);
		int size = Size.getInt();
		int x = LocationX.getInt() - size / 2;
		int y = LocationY.getInt() - size / 2;
		for (int i = 0; i < n; i++) {
			enterDisplayElement(new Oval(this.Color, x, y, size, size),
					getGroup(i + i));
			enterTiming(Timer, Duration, i + i);
			if (i < (n - 1)) {
				enterDisplayElement(new Clear(), getGroup(i + i + 1));
				enterTiming(Timer, OffDuration, i + i + 1);
			}
		}
		lastDisplayElement = firstDisplayElement + 2 * n - 2;
		lastTimingElement = firstTimingElement + 2 * n - 2;
	}
}
