package de.pxlab.pxl;

/**
 * Codes for defining experimental parameter types. These type codes are used
 * for finding the possible parameter values for the various types of
 * experimental parameters This is needed for intelligent display object
 * editing.
 * 
 * @version 0.1.9
 * @see ExPar
 * @see de.pxlab.pxl.gui.ParamControlPanel
 */
public interface ExParTypeCodes {
	/** Experimental parameter type code for duration. */
	public static final int UNKNOWN = 0;
	/** Experimental parameter type code for color coordinates. */
	public static final int COLOR = 1;
	/** Experimental parameter type code for computed color coordinates. */
	public static final int DEPCOLOR = 2;
	/**
	 * Experimental parameter type code for screen coordinates/sizes of unknown
	 * orientation.
	 */
	public static final int SCREENPOS = 3;
	/**
	 * Experimental parameter type code for horizontal screen coordinates/width.
	 */
	public static final int HORSCREENPOS = 4;
	/**
	 * Experimental parameter type code for vertical screen coordinates/width.
	 */
	public static final int VERSCREENPOS = 5;
	/** Experimental parameter type code for proportions. */
	public static final int PROPORT = 6;
	public static final int PROPORTION = 6;
	/** Experimental parameter type code for strings. */
	public static final int STRING = 7;
	/** Integer number in the range [0, ..., 255]. */
	public static final int INT_8_BIT = 8;
	/** Experimental parameter type code for duration. */
	public static final int DURATION = 9;
	/**
	 * Experimental parameter type code for screen coordinates/sizes of unknown
	 * orientation.
	 */
	public static final int SCREENSIZE = 10;
	/**
	 * Experimental parameter type code for horizontal screen coordinates/width.
	 */
	public static final int HORSCREENSIZE = 11;
	/**
	 * Experimental parameter type code for vertical screen coordinates/width.
	 */
	public static final int VERSCREENSIZE = 12;
	/**
	 * Type code for parameters which actually are experimental factor names.
	 */
	public static final int EXPFACTOR = 13;
	/**
	 * Type code for parameters which actually are experimental parameter names.
	 */
	public static final int EXPARNAME = 14;
	/** A signed proportion. */
	public static final int SIGNED_PROPORTION = 15;
	/** Small integer numbers: 0, ..., 50. */
	public static final int SMALL_INT = 16;
	/**
	 * Type code for parameters which are collected data and whose values are
	 * not known before a trial has been run.
	 */
	public static final int RTDATA = 17;
	/**
	 * Type code for parameters which are binary flags wich are either false (0)
	 * or true (1).
	 */
	public static final int FLAG = 18;
	/**
	 * Experimental parameter type code for small screen coordinates/sizes of
	 * unknown orientation, like line widths.
	 */
	public static final int SMALL_SCREENSIZE = 19;
	/** Type code for parameters which represent an angle of 0, .. 360 degree. */
	public static final int ANGLE = 20;
	/** Type code for parameters which represent font styles. */
	// public static final int FONTSTYLE = 21;
	/** Type code for parameters which represent font names. */
	public static final int FONTNAME = 22;
	/** Type code for parameters which describe a visual angle. */
	public static final int VISUAL_ANGLE = 23;
	/**
	 * Type code for parameters which contain small double numbers: 0.0, ...,
	 * 50.0.
	 */
	public static final int SMALL_DOUBLE = 24;
	/**
	 * A parameter which has any of the constants in class
	 * PositionReferenceCodes as its value.
	 */
	// public static final int POSITIONREFERENCECODE = 25;
	/**
	 * Type code for parameters which take integer values with explicitly
	 * defined range.
	 */
	public static final int INTEGER = 26;
	/**
	 * Type code for parameters which take double values with explicitly defined
	 * range.
	 */
	public static final int DOUBLE = 27;
	/**
	 * Type code for parameters which describe a small (0 <= a <= 4 deg) visual
	 * angle.
	 */
	public static final int SMALL_VISUAL_ANGLE = 28;
	/**
	 * A parameter which has any of the constants in class TimerCodes as its
	 * value.
	 */
	// public static final int TIMERCODE = TIMER;
	/** Integer number in the range [0, ..., 15]. */
	// public static final int INT_4_BIT = 29;
	/** Integer number in the range [0, ..., 4]. */
	// public static final int INT_4 = 30;
	/**
	 * A parameter which has any of the constants in class StateControlCodes as
	 * its value.
	 */
	// public static final int STATECONTROLCODE = 31;
	/**
	 * A parameter which has any of the constants in class AlignmentCodes as its
	 * value.
	 */
	// public static final int ALIGNMENTCODE = 32;
	/** A parameter which represents the code of a response key. */
	public static final int KEYCODE = 33;
	/** A adaptive procedure code. */
	// public static final int ADPROCEDURE = 34;
	/** A adaptive procedure stopping rule. */
	// public static final int ADSTOPRULE = 35;
	/** A adaptive procedure result computation code. */
	// public static final int ADRESULT = 36;
	/** A adaptive procedure result computation code. */
	// public static final int SCREENCODE = 37;
	/** A parameter which represents the name of a spectral distribution. */
	public static final int SPECTRUM = 38;
	/** A parameter which represents the name of a spectral distribution. */
	// public static final int DITHERINGCODE = 39;
	/** Debug flags parameter. */
	// public static final int DEBUG_FLAGS = 40;
	/** Color adjustment type parameter. */
	// public static final int COLORADJUSTCODE = 41;
	/** Color device code. */
	// public static final int COLORDEVICECODE = 42;
	/** Overlay code. */
	// public static final int OVERLAYCODE = 43;
	/** Screen selection code. */
	// public static final int SCREENSELECTION = 44;
	/** Search pattern arrangement code. */
	// public static final int SEARCHPATTERN = 45;
	/** Generic element code. */
	// public static final int GENERIC = 46;
	/** External signal. */
	// public static final int INTEGER_1_2 = 47;
	// public static final int SOUND_ENVELOPE = 48;
	// public static final int SOUND_WAVE = 49;
	// public static final int SOUND_REC_CMD = 50;
	// public static final int RAND_GENERATOR = 51;
	public static final int COLOR_EDITOR = 52;
	public static final int GEOMETRY_EDITOR = 53;
	public static final int TIMING_EDITOR = 54;
	public static final int[] editor = {
	/* 0 */ExParDescriptor.NO_EDIT,
	/* 1 */ExParDescriptor.COLOR_EDIT,
	/* 2 */ExParDescriptor.COLOR_EDIT,
	/* 3 */ExParDescriptor.GEOMETRY_EDIT,
	/* 4 */ExParDescriptor.GEOMETRY_EDIT,
	/* 5 */ExParDescriptor.GEOMETRY_EDIT,
	/* 6 */ExParDescriptor.GEOMETRY_EDIT,
	/* 7 */ExParDescriptor.GEOMETRY_EDIT,
	/* 8 */ExParDescriptor.TIMING_EDIT,
	/* 9 */ExParDescriptor.TIMING_EDIT,
	/* 10 */ExParDescriptor.GEOMETRY_EDIT,
	/* 11 */ExParDescriptor.GEOMETRY_EDIT,
	/* 12 */ExParDescriptor.GEOMETRY_EDIT,
	/* 13 */ExParDescriptor.NO_EDIT,
	/* 14 */ExParDescriptor.GEOMETRY_EDIT,
	/* 15 */ExParDescriptor.GEOMETRY_EDIT,
	/* 16 */ExParDescriptor.GEOMETRY_EDIT,
	/* 17 */ExParDescriptor.NO_EDIT,
	/* 18 */ExParDescriptor.GEOMETRY_EDIT,
	/* 19 */ExParDescriptor.GEOMETRY_EDIT,
	/* 20 */ExParDescriptor.GEOMETRY_EDIT,
	/* 21 */ExParDescriptor.GEOMETRY_EDIT,
	/* 22 */ExParDescriptor.GEOMETRY_EDIT,
	/* 23 */ExParDescriptor.GEOMETRY_EDIT,
	/* 24 */ExParDescriptor.GEOMETRY_EDIT,
	/* 25 */ExParDescriptor.GEOMETRY_EDIT,
	/* 26 */ExParDescriptor.GEOMETRY_EDIT,
	/* 27 */ExParDescriptor.GEOMETRY_EDIT,
	/* 28 */ExParDescriptor.GEOMETRY_EDIT,
	/* 29 */ExParDescriptor.GEOMETRY_EDIT,
	/* 30 */ExParDescriptor.GEOMETRY_EDIT,
	/* 31 */ExParDescriptor.GEOMETRY_EDIT,
	/* 32 */ExParDescriptor.GEOMETRY_EDIT,
	/* 33 */ExParDescriptor.GEOMETRY_EDIT,
	/* 34 */ExParDescriptor.GEOMETRY_EDIT,
	/* 35 */ExParDescriptor.GEOMETRY_EDIT,
	/* 36 */ExParDescriptor.GEOMETRY_EDIT,
	/* 37 */ExParDescriptor.GEOMETRY_EDIT,
	/* 38 */ExParDescriptor.GEOMETRY_EDIT,
	/* 39 */ExParDescriptor.GEOMETRY_EDIT,
	/* 40 */ExParDescriptor.GEOMETRY_EDIT,
	/* 41 */ExParDescriptor.GEOMETRY_EDIT,
	/* 42 */ExParDescriptor.GEOMETRY_EDIT,
	/* 43 */ExParDescriptor.GEOMETRY_EDIT,
	/* 44 */ExParDescriptor.GEOMETRY_EDIT,
	/* 45 */ExParDescriptor.GEOMETRY_EDIT,
	/* 46 */ExParDescriptor.GEOMETRY_EDIT,
	/* 47 */ExParDescriptor.GEOMETRY_EDIT,
	/* 48 */ExParDescriptor.GEOMETRY_EDIT,
	/* 49 */ExParDescriptor.GEOMETRY_EDIT,
	/* 50 */ExParDescriptor.GEOMETRY_EDIT,
	/* 51 */ExParDescriptor.GEOMETRY_EDIT,
	/* 52 */ExParDescriptor.COLOR_EDIT,
	/* 53 */ExParDescriptor.GEOMETRY_EDIT,
	/* 54 */ExParDescriptor.TIMING_EDIT };
}
