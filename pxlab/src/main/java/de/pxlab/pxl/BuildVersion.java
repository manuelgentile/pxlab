package de.pxlab.pxl;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Build the version number and increment the build count by one.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 
 * 2005/11/03 Fixed access problems to static final fields in Version.java
 */
public class BuildVersion {
	private static final int versionMajor = 2;
	private static final int versionMinor = 1;
	private static final int versionBugfix = 19;

	public static void main(String[] args) {
		// String year = DateFormat.getDateInstance(DateFormat.LONG,
		// Locale.US).format(new Date(), new StringBuffer(),
		// new FieldPosition(DateFormat.YEAR_FIELD)).toString();
		String year = new SimpleDateFormat("yyyy").format(new Date(),
				new StringBuffer(), new FieldPosition(DateFormat.YEAR_FIELD))
				.toString();
		String date = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)
				.format(new Date());
		String nl = System.getProperty("line.separator");
		try {
			File fp = new File("Version.java");
			PrintWriter p = new PrintWriter(new FileWriter(fp));
			p.println("package de.pxlab.pxl;");
			p.println("// ---------------------------------------------------------");
			p.println("// Created automatically by class de.pxlab.pxl.BuildVersion.");
			p.println("// Do not change manually!");
			p.println("// ---------------------------------------------------------");
			p.println("public class Version {" + nl);
			p.println("    private static int major = " + versionMajor + ";");
			p.println("    private static int minor = " + versionMinor + ";");
			p.println("    private static int bugfx = " + versionBugfix + ";");
			p.println("    private static int build = "
					+ (Version.getBuild() + 1) + ";" + nl);
			p.println("    public static int getMajor() { return major; }");
			p.println("    public static int getMinor() { return minor; }");
			p.println("    public static int getBugfix() { return bugfx; }");
			p.println("    public static int getBuild() { return build; }");
			p.println("    public static String instance() {");
			p.println("        return(major+\".\"+minor+\".\"+bugfx);");
			p.println("    }");
			p.println("    public static String instanceFull() {");
			p.println("        return(major+\".\"+minor+\".\"+bugfx+\".\"+build);");
			p.println("    }");
			p.println("    public static final String javaVersion = \""
					+ (System.getProperty("java.version")) + "\";");
			p.println("    public static boolean isJava1() {return javaVersion.startsWith(\"1.1\");}");
			p.println("    public static String year() {return \"" + year
					+ "\";}");
			p.println("    public static String date() {return \"" + date
					+ "\";}");
			p.println("    public static void main(String[] args) {");
			p.println("      System.out.println(\"PXLab version \" + instance() + \" built for Java version \" + javaVersion);");
			p.println("    }");
			p.println("}");
			p.close();
			System.out.println("BuildVersion wrote " + fp.getPath()
					+ " [Build " + (Version.getBuild() + 1) + "]");
			p = new PrintWriter(new FileWriter("version-info.txt"));
			p.println("PXLab Version " + Version.getMajor() + "."
					+ Version.getMinor() + "." + Version.getBugfix()
					+ " Build " + (Version.getBuild() + 1) + ".");
			p.close();
			p = new PrintWriter(new FileWriter("version-info-rename.bat"));
			p.println("rename pxlab.jar pxlab-" + Version.getMajor() + "_"
					+ Version.getMinor() + "_" + Version.getBugfix() + "_"
					+ (Version.getBuild() + 1) + ".jar");
			p.close();
		} catch (IOException iox) {
		}
	}
}
