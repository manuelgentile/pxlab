package it.cnr.itd.pxlab;

import java.io.IOException;

import de.pxlab.pxl.ExDesign;
import de.pxlab.pxl.run.ExRun;

public class MyTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExDesign exDesign;
		try {
			exDesign = new ExDesign("/opt/prg/workspaces/staga/pxlab/src/main/resources/iat_manuel.pxd", "-S2");
			if (exDesign != null) {
				ExRun exRunCommand = new ExRun(new String[] {"-S2"}, exDesign);
			}
	 	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
