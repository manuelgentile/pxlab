package it.cnr.itd.pxlab;

import java.io.IOException;

import de.pxlab.pxl.ExDesign;
import de.pxlab.pxl.run.ExRun;
 
public class MyTestDuration {
	/** 
	 * @param args
	 */
	public static void main(String[] args) {
		ExDesign exDesign;
		try {
			exDesign = new ExDesign("/home/gentile/git/github/pxlab/src/main/resources/duration.pxd", "-S2");
			
			if (exDesign != null) {
				ExRun exRunCommand = new ExRun(new String[] {"-S4","-s7","-gC", "-w800","-h600", "-R100", "-D time"}, exDesign);
				//"-w800","-h600", "-R100", "-D time"
			}
	 	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
