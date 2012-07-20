package de.pxlab.pxl.display;

import java.util.ArrayList;
import de.pxlab.pxl.*;

/**
 * Shows a sequence of Self-Assessment Manikin (SAM) scales after Lang (1980)
 * and collects responses by SAM image selection. The following features are
 * supported:
 * 
 * <ul>
 * 
 * <li>All 3 SAM scales for valence, arousal, and dominance may be presented.
 * Subsets are possible.
 * 
 * <li>Presentation sequence may be controlled.
 * 
 * <li>The valence scale may be shown in full body or in portrait mode.
 * 
 * <li>Each scale may contain 5, 7 or 9 value levels. Each value level is
 * described by a single SAM image.
 * 
 * <li>There also is a version with 9 levels showing only 5 images combined with
 * 9 special selection points to select a scale level.
 * 
 * <li>Colors, size, position, and line width my be defined.
 * 
 * <li>Images are fully scalable without any loss in resolution.
 * 
 * <li>Data collection may include response times and selection times for every
 * single selection or deselection response.
 * 
 * <li>SAM images replicate and extend the images published by Bradley & Lang
 * (1994) and Lang, Bradley, and Cuthbert (2005, Fig. 1).
 * 
 * </ul>
 * 
 * <p>
 * Bradley, M. M. & Lang, P. J. (1994). Measuring emotion: The Self-Assessment
 * Manikin and the semantic differential. Journal of Behavior Therapy &
 * Experimental Psychiatry, 25, 49-59.
 * 
 * <p>
 * Lang, P. J. (1980). Behavioral treatment and bio-behavioral assessment:
 * computer applications. In J. B. Sidowski, J. H. Johnson, & T. A. Williams
 * (Eds.), Technology in mental health care delivery systems (pp. 119-l37).
 * Norwood, NJ: Ablex.
 * 
 * <p>
 * Lang, P. J., Bradley, M. M., & Cuthbert, B. N. (2005). International
 * affective picture system (IAPS): Instruction manual and affective ratings.
 * Technical Report A-6. University of Florida, Gainesvile, FL.
 * 
 * @version 0.3.1
 * @see de.pxlab.pxl.SAMImageFactory
 */
/*
 * 
 * 2008/02/21 added 7 level version
 */
public class SAM extends PictureMatrix implements SAMScaleCodes {
	/** Line drawing color. */
	public ExPar LineColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.BLACK)), "Drawing line color");
	/** Frame line color. */
	public ExPar FrameColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Frame line color");
	/** Color used to indicate that an image is selected. */
	public ExPar SelectedColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.LIGHT_GRAY)),
			"Selected background color");
	/** Background color. */
	public ExPar BackgroundColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.WHITE)), "Background color");
	/** Drawing line width. */
	public ExPar LineWidth = new ExPar(SMALL_DOUBLE, new ExParValue(1.5),
			"Width of lines");
	/** Frame line width. */
	public ExPar FrameLineWidth = new ExPar(SMALL_DOUBLE, new ExParValue(2.0),
			"Width of frame lines");
	/**
	 * Self-Assessment-Manikin scale dimensions to be used: valence, arousal, or
	 * dominance. See <code>SAMScaleCodes</code> for the respective codes.
	 * Defines which scales are presented. The sequence of codes in this array
	 * also defines the presentation sequence.
	 */
	public ExPar Scale = new ExPar(GEOMETRY_EDITOR, SAMScaleCodes.class,
			new ExParValueConstant("de.pxlab.pxl.SAMScaleCodes.VALENCE"),
			"Scale type");
	/**
	 * Portrait flag. If true then the valence scale uses a portrait image
	 * instead of a full body image.
	 */
	public ExPar Portrait = new ExPar(FLAG, new ExParValue(0), "Portrait flag");
	/**
	 * Narrow border flag. If true then a narrow border is used. The narrow
	 * border is not apropriate for the dominance scale. Narrow border should be
	 * used only if the dominance scale is not used. A narrow border slightly
	 * increases the SAM images.
	 */
	public ExPar NarrowBorder = new ExPar(FLAG, new ExParValue(0),
			"Narrow border flag");
	/**
	 * Activate special selection points below the SAM images. If true then the
	 * image itself is not marked as selected but there is a special selection
	 * point below every image for selection. In this case the even numbered
	 * levels of the scale do not show images but are blank and are smaller than
	 * the odd numbered levels.
	 */
	public ExPar SelectionPoints = new ExPar(FLAG, new ExParValue(0),
			"Use special selection points");
	/**
	 * Response time for the second scale, if there is one. The respone time for
	 * the first scale is contained in parameter ResponseTime.
	 */
	public ExPar ResponseTime2 = new ExPar(RTDATA, new ExParValue(0),
			"Response time for second scale");
	/**
	 * Response code for the second scale, if there is one. The respone code for
	 * the first scale is contained in parameter ResponseCode.
	 */
	public ExPar ResponseCode2 = new ExPar(RTDATA, new ExParValue(0),
			"Response code for second scale");
	/**
	 * Response time for the third scale, if there is one. The respone time for
	 * the first scale is contained in parameter ResponseTime.
	 */
	public ExPar ResponseTime3 = new ExPar(RTDATA, new ExParValue(0),
			"Response time for third scale");
	/**
	 * Response code for the third scale, if there is one. The respone code for
	 * the first scale is contained in parameter ResponseCode.
	 */
	public ExPar ResponseCode3 = new ExPar(RTDATA, new ExParValue(0),
			"Response code for third scale");
	protected int nScales = 0;

	public SAM() {
		setTitleAndTopic("Selectable SAM picture scales", PICTURE_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.MOUSE_TRACKING_RELEASE_KEY_TIMER"));
		NumberOfColumns.set(5);
		HorizontalGap.set(0);
		Width.set(138);
		ReferencePoint.set(new ExParValueConstant(
				"de.pxlab.pxl.PositionReferenceCodes.BASE_CENTER"));
		LocationY.getValue().set(
				new ExParValue(new ExParExpression(
						ExParExpression.SCREEN_BOTTOM)));
	}
	protected SAMImageFactory samImageFactory;

	protected int create() {
		samImageFactory = new SAMImageFactory();
		return super.create();
	}

	/*
	 * protected void computeColors() {
	 * samImageFactory.setColors(LineColor.getDevColor(),
	 * FrameColor.getDevColor(), SelectedColor.getDevColor(),
	 * BackgroundColor.getDevColor()); computeGeometry(); }
	 */
	protected void computeGeometry() {
		SelectionType
				.set(de.pxlab.pxl.SelectionTypeCodes.SINGLE_IN_ROW_SELECTION);
		samImageFactory.setValencePortrait(Portrait.getFlag());
		samImageFactory.setNarrowBorder(NarrowBorder.getFlag());
		samImageFactory.setFrameLineWidth(FrameLineWidth.getDouble());
		samImageFactory.setLineWidth(LineWidth.getDouble());
		samImageFactory.setSelectionPoints(SelectionPoints.getFlag());
		samImageFactory.setColors(LineColor.getDevColor(),
				FrameColor.getDevColor(), SelectedColor.getDevColor(),
				BackgroundColor.getDevColor());
		rowOverlay = true;
		super.computeGeometry();
	}

	protected void setImages() {
		int nc = NumberOfColumns.getInt();
		if ((nc != 5) && (nc != 7) && (nc != 9)) {
			nc = 5;
		}
		int[] scale = Scale.getIntArray();
		if ((scale.length <= 0) || (scale.length > 3)) {
			new ParameterValueError(
					"SAM.setImages() scale code missing or too many scales: "
							+ scale.length);
			scale = new int[1];
			scale[0] = de.pxlab.pxl.SAMScaleCodes.VALENCE;
		} else
			for (int i = 0; i < scale.length; i++) {
				if ((scale[i] != de.pxlab.pxl.SAMScaleCodes.VALENCE)
						&& (scale[i] != de.pxlab.pxl.SAMScaleCodes.AROUSAL)
						&& (scale[i] != de.pxlab.pxl.SAMScaleCodes.DOMINANCE)) {
					new ParameterValueError(
							"SAM.setImages() illegal scale code: " + scale[i]);
					scale[i] = de.pxlab.pxl.SAMScaleCodes.VALENCE;
				}
			}
		int np = nc * scale.length;
		int k;
		if ((np != nPics) && (np > 0)) {
			removeDisplayElements(pictIdx);
			pics = new BitMapElement[np];
			k = 0;
			for (int i = 0; i < scale.length; i++) {
				for (int j = 0; j < nc; j++) {
					pics[k] = new BitMapElement();
					pics[k].setColorPar(ExPar.ScreenBackgroundColor);
					enterDisplayElement(pics[k], group[i]);
					k++;
				}
			}
			nPics = np;
		}
		if (scale.length != nScales) {
			removeTimingElements(teIdx);
			int t = enterTiming(Timer, Duration, ResponseSet, 0, ResponseTime,
					ResponseCode);
			if (scale.length > 1) {
				t = enterTiming(Timer, Duration, ResponseSet, t, ResponseTime2,
						ResponseCode2);
			}
			if (scale.length > 2) {
				t = enterTiming(Timer, Duration, ResponseSet, t, ResponseTime3,
						ResponseCode3);
			}
			nScales = scale.length;
		}
		int w = Width.getInt();
		k = 0;
		for (int j = 0; j < scale.length; j++) {
			int type = scale[j];
			if (nc == 5) {
				for (int i = 0; i < nc; i++) {
					pics[k].setImage(samImageFactory.instance(type, i + i + 1,
							false, w));
					pics[k].setSelectedImage(samImageFactory.instance(type, i
							+ i + 1, true, w));
					k++;
				}
			} else if (nc == 7) {
				for (int i = 0; i < nc; i++) {
					pics[k].setImage(samImageFactory.instance(type,
							4.0 * i / 3.0 + 1.0, false, w));
					pics[k].setSelectedImage(samImageFactory.instance(type,
							4.0 * i / 3.0 + 1.0, true, w));
					k++;
				}
			} else if (nc == 9) {
				for (int i = 0; i < nc; i++) {
					pics[k].setImage(samImageFactory.instance(type, i + 1,
							false, w));
					pics[k].setSelectedImage(samImageFactory.instance(type,
							i + 1, true, w));
					k++;
				}
			}
		}
	}

	protected void timingGroupFinished(int group) {
		if (group < (nScales - 1))
			return;
		super.timingGroupFinished(group);
	}

	protected int[] getSelection() {
		ArrayList s = new ArrayList(5);
		int nc = NumberOfColumns.getInt();
		for (int i = 0; i < nPics; i++) {
			if (pics[i].isSelected()) {
				int si = i % nc + 1;
				if (nc == 5) {
					// si = 2*si - 1;
					s.add(new Integer(si));
				} else if (nc == 7) {
					// si = 2*si - 1;
					s.add(new Integer(si));
				} else if (nc == 9) {
					s.add(new Integer(si));
				}
			}
		}
		int n = s.size();
		int[] k = new int[n];
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				k[i] = ((Integer) s.get(i)).intValue();
			}
		}
		return k;
	}
}
