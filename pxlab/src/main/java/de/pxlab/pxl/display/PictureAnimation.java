package de.pxlab.pxl.display;

import java.awt.*;
import java.util.*;

import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * An animation made up from a series of pictures. The animation is controlled
 * by OnDuration and offDuration timing values. If OffDuration is 0 then images
 * follow each other without a gap. If OffDuration is non-zero then a colored
 * rectangle is shown in the off period between images.
 * 
 * <p>
 * The picture file names are given in parameter FileName as an array of string
 * values.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class PictureAnimation extends Picture {
	/**
	 * Timer for controlling the ON and OFF periods. This should always be set
	 * to TimerCodes.VS_CLOCK_TIMER if vertical blanking is required and to
	 * TimerCodes.RAW_CLOCK_TIMER f no synchronization is required.
	 */
	public ExPar OnOffTimer = new ExPar(TIMING_EDITOR, TimerCodes.class,
			new ExParValueConstant("de.pxlab.pxl.TimerCodes.VS_CLOCK_TIMER"),
			"On/Off interval timer");
	/** ON period duration. */
	public ExPar OnDuration = new ExPar(DURATION, 0, 300, new ExParValue(60),
			"On interval duration");
	/**
	 * OFF period duration. If this is 0 then no OFF image is shown. If this is
	 * non-zero then a colored bar is shown in the off period. The bar's size is
	 * identical to the first picture's size.
	 */
	public ExPar OffDuration = new ExPar(DURATION, 0, 500, new ExParValue(200),
			"Off interval duration");

	public boolean isAnimated() {
		return (true);
	}

	public PictureAnimation() {
		setTitleAndTopic("Picture animation", PICTURE_DSP);
		ClearFileBaseOnRecompute.set(1);
	}
	private BitMapElement pict;
	private Bar bar = null;
	private int barIdx;
	private int numberOfPictures = 0;

	protected int create() {
		pict = new BitMapElement(this.Color);
		int pictIdx = enterDisplayElement(pict, group[0]);
		enterTiming(OnOffTimer, OnDuration, 0);
		return (pictIdx);
	}

	protected void computeGeometry() {
		int offDuration = OffDuration.getInt();
		if ((offDuration > 0) && (bar != null)) {
			// no change
		} else if ((offDuration == 0) && (bar == null)) {
			// no change
		} else if (offDuration > 0) {
			bar = new Bar(Color);
			barIdx = enterDisplayElement(bar, group[1]);
			enterTiming(OnOffTimer, OffDuration, 1);
		} else {
			bar = null;
			removeDisplayElements(barIdx);
			removeTimingElements(1);
		}
		String fn[] = FileName.getStringArray();
		int n = fn.length;
		setFramesPerCycle(n);
		setFrameIncrement(1);
		for (int i = 1; i < n; i++) {
			if (StringExt.nonEmpty(fn[i])) {
				// This loads the image into the image cash
				pict.setImage(Directory.getString(), fn[i]);
			}
		}
		if (StringExt.nonEmpty(fn[0])) {
			// This loads the image into the image cash
			pict.setImage(Directory.getString(), fn[0]);
		}
		pict.setLocation(LocationX.getInt(), LocationY.getInt());
		pict.setReferencePoint(ReferencePoint.getInt());
		if (bar != null) {
			Point p = pict.getLocation();
			Dimension s = pict.getSize();
			bar.setCenterAndSize(p.x, p.y, s.width, s.height);
		}
	}

	public void computeAnimationFrame(int frame) {
		pict.setImage(Directory.getString(), FileName.getStringArray()[frame]);
	}
}
