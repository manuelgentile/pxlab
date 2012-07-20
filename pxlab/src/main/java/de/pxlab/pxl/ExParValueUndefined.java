package de.pxlab.pxl;

/*
 This class creates experimental parameter values which currently are
 undefined. These may appear in node arguments as placeholders for
 values inherited from a higher level node.

 @author H. Irtel
 @version 0.1.0
 */
/*
 08/23/00 
 */
public class ExParValueUndefined extends ExParValue {
	/** Constructor for unknown parameter values. */
	public ExParValueUndefined() {
		setUndefined(true);
	}
}
