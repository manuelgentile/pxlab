package de.pxlab.pxl;

/**
 * Codes for defining display and demo topics. Each topic represents a submenu
 * for display selection.
 * 
 * @author H. Irtel
 * @version 0.2.2
 * @see Display
 */
public interface Topics {
	public static final int INTRO_DSP = 0;
	public static final int CLEAR_DSP = 1;
	public static final int PROC_MESSAGE_DSP = 2;
	public static final int ATTEND_DSP = 3;
	public static final int SIMPLE_GEOMETRY_DSP = 4;
	public static final int COMPLEX_GEOMETRY_DSP = 5;
	public static final int SIMPLE_COLOR_MATCHING_DSP = 6;
	public static final int COMPLEX_COLOR_MATCHING_DSP = 7;
	public static final int COLOR_DISCRIMINATION_DSP = 8;
	public static final int LATERAL_INHIBITION_DSP = 9;
	public static final int COLOR_CONTRAST_DSP = 10;
	public static final int ASSIMILATION_DSP = 11;
	public static final int PHOTOMETRY_DSP = 12;
	public static final int ADAPTATION_DSP = 13;
	public static final int COLOR_SPACES_DSP = 14;
	public static final int SPECTRAL_COLOR_DSP = 15;
	public static final int GRATING_DSP = 16;
	public static final int APPARENT_MOTION_DSP = 17;
	public static final int RANDOM_DOT_DSP = 18;
	public static final int LETTER_MATRIX_DSP = 19;
	public static final int SERIAL_TEXT_DSP = 20;
	public static final int TEXT_PAR_DSP = 21;
	public static final int FEEDBACK_DSP = 22;
	public static final int SEARCH_DSP = 23;
	public static final int QUESTIONNAIRE_DSP = 24;
	public static final int PATTERN_IMAGE_DSP = 25;
	public static final int PICTURE_DSP = 26;
	public static final int PROBLEM_SOLVING_DSP = 27;
	public static final int EXTERNAL_DSP = 28;
	public static final int CONTROL_DSP = 29;
	public static final int VISUAL_GAMMA_DSP = 30;
	public static final int DISPLAY_TEST_DSP = 31;
	public static final int AUDIO_DSP = 32;
	public static final int MEDIA_DSP = 33;
	public static final int GUI_COMPONENT_DSP = 34;
	public static final int PARAM_DSP = 35;
	/**
	 * This code may be added to a Display object's TopicCode in order to
	 * indicate that this Display object can't be used as an experimental
	 * Display but is a demonstration Display only.
	 */
	public static final int DEMO = 4096;
	/**
	 * This code may be added to a Display object's TopicCode in order to
	 * indicate that this Display object can't be used as a demonstration
	 * Display but is an experimental Display only.
	 */
	public static final int EXP = DEMO + DEMO;
	/**
	 * This code must be added to a Display object's TopicCode in order to
	 * indicate that this Display object can't be used as an experimental
	 * Display but is a data processiong Display.
	 */
	public static final int DATA = EXP + EXP;
	public static final String[] topicDescription = {
	/* 0 */"Introductory displays",
	/* 1 */"Clear Screen",
	/* 2 */"Procedural Message",
	/* 3 */"Attend Signals",
	/* 4 */"Simple Geometry",
	/* 5 */"Complex Geometry",
	/* 6 */"Simple Color Matching",
	/* 7 */"Complex Color Matching",
	/* 8 */"Color Discrimination",
	/* 9 */"Lateral Inhibition",
	/* 10 */"Color Contrast",
	/* 11 */"Color Assimilation",
	/* 12 */"Photometry",
	/* 13 */"Color Adaptation",
	/* 14 */"Color Spaces",
	/* 15 */"Spectral Color",
	/* 16 */"Gratings",
	/* 17 */"Apparent Motion",
	/* 18 */"Random Dots",
	/* 19 */"Letter Matrix",
	/* 20 */"Serial Text Presentation",
	/* 21 */"Text Paragraph",
	/* 22 */"Feedback",
	/* 23 */"Visual Search",
	/* 24 */"Questionnaires",
	/* 25 */"Pattern Images",
	/* 26 */"Pictures",
	/* 27 */"Problem Solving",
	/* 28 */"External Signals",
	/* 29 */"Response and Error Control",
	/* 30 */"Visual Gamma Measurement",
	/* 31 */"Display Testing",
	/* 32 */"Audio Signals",
	/* 33 */"Audio-Visual Media",
	/* 34 */"GUI Component",
	/* 35 */"Parameter Modification" };
}
