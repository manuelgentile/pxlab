package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * A rapid serial visual presentation (RSVP) object. This can show a series of
 * strings at an optimal rate. The Timer parameter of this Display object should
 * always be set to de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER. The ON-Duration is
 * controlled by the Duration parameter and the OFF-Duration by the parameter
 * OffDuration. If OffDuration is 0 then no blanks are inserted between
 * sequential strings.
 * 
 * @version 0.1.0
 */
public class RapidSerialPresentation extends TextDisplay {
	private static String[] ss = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
			"9" };
	public ExPar CueIndex = new ExPar(STRING, new ExParValue(3),
			"Cue position index ");
	public ExPar OffDuration = new ExPar(DURATION, new ExParValue(0),
			"OFF period duration");

	public RapidSerialPresentation() {
		setTitleAndTopic("Rapid Serial Visual Presentation", SERIAL_TEXT_DSP
				| EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"));
	}
	private int firstDisplayElement;
	private int lastDisplayElement;
	private int firstTimingElement;
	private int lastTimingElement;

	protected int create() {
		firstDisplayElement = nextDisplayElementIndex();
		lastDisplayElement = firstDisplayElement - 1;
		firstTimingElement = nextTimingElementIndex();
		return firstDisplayElement;
	}

	protected void computeGeometry() {
		String[] centerStream = Text.getStringArray();
		int centerStreamSize = centerStream.length;
		int cueIndex = CueIndex.getInt();
		int inx = (OffDuration.getInt() > 0) ? 2 : 1;
		if (lastDisplayElement != (firstDisplayElement + inx * centerStreamSize - 1)) {
			removeDisplayElements(firstDisplayElement);
			removeTimingElements(firstTimingElement);
			int n = firstDisplayElement;
			int k = 0;
			for (int i = 0; i < centerStreamSize; i++) {
				TextElement t = new TextElement(this.Color);
				enterDisplayElement(t, getGroup(k));
				enterTiming(Timer, Duration, k++);
				n++;
				if (OffDuration.getInt() > 0) {
					enterDisplayElement(new EmptyDisplayElement(), getGroup(k));
					enterTiming(Timer, OffDuration, k++);
					n++;
				}
			}
			lastDisplayElement = n - 1;
			lastTimingElement = n - 1;
		}
		for (int i = 0; i < centerStreamSize; i++) {
			TextElement t = (TextElement) (getDisplayElement(firstDisplayElement
					+ inx * i));
			t.setText(centerStream[i]);
			t.setFont(FontFamily.getString(), FontStyle.getInt(),
					FontSize.getInt());
			t.setLocation(LocationX.getInt(), LocationY.getInt());
			t.setReferencePoint(PositionReferenceCodes.BASE_CENTER);
		}
	}
}
