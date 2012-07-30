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
			//exDesign = new ExDesign("/home/gentile/git/github/pxlab/src/main/resources/iat_manuel.pxd", "-S2");
			exDesign = new ExDesign("/Users/alberto/Documents/workspace/pxlab/pxlab/src/main/resources/BIAF_ASSOLUTO.pxd", "-S2");
			if (exDesign != null) {
				ExRun exRunCommand = new ExRun(new String[] {"-S2","-s7","-gC","-D"}, exDesign);
				//"-w800","-h600", "-R100", "-D time""-S4","-s7","-gC", "-w800","-h600", "-R100", "-D time"
			}
	 	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
