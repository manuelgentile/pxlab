package de.pxlab.pxl;

/**
 * Some static matrices defining standard color devices. (Source:
 * http://www.color.org/wpaper1.html)
 * 
 * Additional source:
 * 
 * Charles A. Poynton (1996) A technical introduction to digital video. New
 * York: Wiley.
 * 
 * @author H. Irtel
 * @version 0.2.2
 */
/*
 * 03/07/00
 * 
 * 12/23/01 added EBU primaries from Poynton's book
 * 
 * 03/13/03 added Osram colored flourescent tubes
 */
public class PhosphorChromaticities {
	/**
	 * EBU Phosphors. The phosphor chromaticities standardized by the European
	 * Broadcasting Union for 625/50 systems. EBU Tech. 3213, EBU standard for
	 * chromaticity tolerances for studio monitors. Geneva: EBU, 1975.
	 */
	public static double[][] EBU = { { 21.26, 0.640, 0.330 },
			{ 71.52, 0.290, 0.600 }, { 7.22, 0.150, 0.060 } };
	public static String EBUName = "EBU";
	/**
	 * FCC 1953 Receiver Phosphors. These are the NTSC primaries as specified in
	 * 1953 and still documented in ITI-R Report 624. These phospohors are
	 * obsolete since newer types offer a greater gamut.
	 */
	public static double[][] FCC1953 = { { 21.26, 0.674, 0.326 },
			{ 71.52, 0.218, 0.712 }, { 7.22, 0.140, 0.080 } };
	public static String FCC1953Name = "FCC 1953 Receiver";
	/**
	 * SMPTE "C". These are the chromaticities recommended for 525/59.94 color
	 * television systems and for HDTV 1125/60 1920x1035 systems according to
	 * SMPTE 240M. Source: SMPTE RP 145-1994.
	 */
	public static double[][] SMPTEC = { { 21.26, 0.630, 0.340 },
			{ 71.52, 0.310, 0.595 }, { 7.22, 0.155, 0.070 } };
	public static String SMPTECName = "SMPTE C";
	/**
	 * CCIR 709. International standard for HDTV primaries. Recommendation ITU-R
	 * BT.709, Basic parameter values for the HDTV standard for the studio and
	 * for international programme exchange. Geneva: ITU, 1990.
	 */
	public static double[][] CCIR709 = { { 21.26, 0.640, 0.330 },
			{ 71.52, 0.300, 0.600 }, { 7.22, 0.150, 0.060 } };
	public static String CCIR709Name = "CCIR 709";
	/** Sony Trinitron (all +-, 0.03) phosphor chromaticities. */
	public static double[][] SonyTrinitron = { { 21.26, 0.621, 0.340 },
			{ 71.52, 0.281, 0.606 }, { 7.22, 0.152, 0.067 } };
	public static String SonyTrinitronName = "Sony Trinitron";
	/** Hitachi CM2198 (all +-, 0.02) phosphor chromaticities. */
	public static double[][] HitachiCM2198 = { { 21.26, 0.624, 0.339 },
			{ 71.52, 0.285, 0.604 }, { 7.22, 0.150, 0.065 } };
	public static String HitachiCM2198Name = "Hitachi CM2198";
	/**
	 * The CIE 1931 RGB system with radiant power ratios 72.0962 : 1.3791 : 1
	 * for the three channels
	 */
	public static String CIE1931RGBName = "CIE 1931 RGB (R=700.0, G=546.1, B=435.8)";
	public static double[][] CIE1931RGB = { { 1.0000, 0.73467, 0.26533 },
			{ 4.5907, 0.27376, 0.71741 }, { 0.0601, 0.16658, 0.08886 } };
	/** Osram colored flourescent tubes */
	public static String OsramColorTubesName = "Osram colored fluorescent tubes";
	public static double[][] OsramColorTubes = {
			{ 182.60, 0.602218, 0.313627 }, { 309.70, 0.339915, 0.516462 },
			{ 57.06, 0.161051, 0.062606 } };
}
