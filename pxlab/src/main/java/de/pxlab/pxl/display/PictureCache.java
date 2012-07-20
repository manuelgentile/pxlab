package de.pxlab.pxl.display;

import de.pxlab.util.*;
import de.pxlab.pxl.*;

/**
 * Moves a series of pictures into the picture cache. The Directory parameter
 * MUST be prefixed with the '@'-character for this caching object and for the
 * Display object which uses the pictures. The effective file path used in this
 * caching object must be identical to the effective file path used later.
 * 
 * <p>
 * Here is an example for using the caching mechanism.
 * 
 * <pre>
 *     AssignmentGroup() {
 *       ...
 *       new size = screenHeight()-80;
 *       new dir = "@./images/svg";
 *     }
 * 
 *     Session() {
 *       ...
 *       PictureCache() {
 * 	Directory = dir;
 *         Width = size;
 *         Height = size;
 * 	FileName = ["tiger.svg", "lion.svg", "anne.svg", "batikBatik.svg", "sunRise.svg"];
 *       }
 *     }  
 * 
 *     Trial(TrialCounter, Picture.FileName) {
 *       ...
 *       Picture() {
 * 	Duration = 2000;
 *         Timer = de.pxlab.pxl.TimerCodes.LIMITED_RESPONSE_TIMER;
 * 	Directory = dir;
 *         Width = size;
 *         Height = size;
 *       }
 *     }
 * 
 *   }
 * 
 *   Procedure() {
 *     Session() {
 *       Block() {
 * 	Trial(?, "tiger.svg");
 * 	Trial(?, "lion.svg");
 * 	Trial(?, "anne.svg");
 * 	Trial(?, "batikBatik.svg");
 * 	Trial(?, "sunRise.svg");
 *       }
 *     }
 *   }
 * }
 * </pre>
 * 
 * @version 0.1.0
 */
/*
 * 
 * 2006/02/14
 */
public class PictureCache extends Picture {
	public PictureCache() {
		setTitleAndTopic("Move Pictures into Cache", PICTURE_DSP);
		setVisible(false);
	}

	public boolean isGraphic() {
		return false;
	}
	private int pictIdx;

	protected int create() {
		BitMapElement pict = new BitMapElement(this.Color);
		pictIdx = enterDisplayElement(pict, group[0]);
		defaultTiming(0);
		return pictIdx;
	}

	protected void computeGeometry() {
		BitMapElement p = (BitMapElement) getDisplayElement(pictIdx);
		String[] fn = FileName.getStringArray();
		for (int i = 0; i < fn.length; i++) {
			if (StringExt.nonEmpty(fn[i])) {
				p.setImage(Directory.getString(), fn[i], Width.getInt(),
						Height.getInt());
			}
		}
	}
}
