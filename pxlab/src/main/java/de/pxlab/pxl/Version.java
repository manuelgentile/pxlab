package de.pxlab.pxl;

// ---------------------------------------------------------
// Created automatically by class de.pxlab.pxl.BuildVersion.
// Do not change manually!
// ---------------------------------------------------------
public class Version {
	private static int major = 2;
	private static int minor = 1;
	private static int bugfx = 19;
	private static int build = 3926;

	public static int getMajor() {
		return major;
	}

	public static int getMinor() {
		return minor;
	}

	public static int getBugfix() {
		return bugfx;
	}

	public static int getBuild() {
		return build;
	}

	public static String instance() {
		return (major + "." + minor + "." + bugfx);
	}

	public static String instanceFull() {
		return (major + "." + minor + "." + bugfx + "." + build);
	}
	public static final String javaVersion = "1.6.0_05";

	public static boolean isJava1() {
		return javaVersion.startsWith("1.1");
	}

	public static String year() {
		return "2008";
	}

	public static String date() {
		return "Apr 16, 2008";
	}

	public static void main(String[] args) {
		System.out.println("PXLab version " + instance()
				+ " built for Java version " + javaVersion);
	}
}
