package de.pxlab.pxl;

/**
 * Codes for color device transform selection. These are devices for creating
 * colors.
 */
public interface ColorDeviceTransformCodes {
	/** Device properties are read from experimental parameters. */
	public static final int PARAMETRIC = 0;
	/**
	 * EBU Phosphors. The phosphor chromaticities standardized by the European
	 * Broadcasting Union for 625/50 systems. EBU Tech. 3213, EBU standard for
	 * chromaticity tolerances for studio monitors. Geneva: EBU, 1975.
	 */
	public static final int EBU = 1;
	/**
	 * FCC 1953 Receiver Phosphors. These are the NTSC primaries as specified in
	 * 1953 and still documented in ITI-R Report 624. These phospohors are
	 * obsolete since newer types offer a greater gamut.
	 */
	public static final int FCC1953 = 2;
	/**
	 * SMPTE "C". These are the chromaticities recommended for 525/59.94 color
	 * television systems and for HDTV 1125/60 1920x1035 systems according to
	 * SMPTE 240M. Source: SMPTE RP 145-1994.
	 */
	public static final int SMPTEC = 3;
	/**
	 * CCIR 709. International standard for HDTV primaries. Recommendation ITU-R
	 * BT.709, Basic parameter values for the HDTV standard for the studio and
	 * for international programme exchange. Geneva: ITU, 1990.
	 */
	public static final int CCIR709 = 4;
	/** Sony Trinitron (all +-, 0.03) phosphor chromaticities. */
	public static final int SONY_TRINITRON = 5;
	/** Hitachi CM2198 (all +-, 0.02) phosphor chromaticities. */
	public static final int HITACHI_CM2198 = 6;
	/**
	 * The CIE 1931 RGB system with radiant power ratios 72.0962 : 1.3791 : 1
	 * for the three channels
	 */
	public static final int CIE_1931_RGB = 7;
	/** Osram colored flourescent tubes */
	public static final int OSRAM_COLOR_TUBES = 8;
}
