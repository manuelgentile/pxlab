package de.pxlab.pxl;

import java.awt.Color;

import de.pxlab.util.StringExt;

/**
 * A video screen color correction transform.
 * 
 * @author H. Irtel
 * @version 0.3.0
 */
/*
 * 
 * 2004/11/10
 * 
 * 2007/10/28 new constructor
 */
public class ScreenColorTransform extends ColorDeviceTransform {
	/**
	 * Create a screen color transform for the given screen. Creating a color
	 * device transform implies that the transform from XYZ to the linear RGB
	 * space spanned by the device's basis vectors is initialized and that the
	 * gamma transforms of the color channels are created.
	 * 
	 * @param k
	 *            screen code as defined in class de.pxlab.pxl.DisplayDevice.
	 */
	public ScreenColorTransform(int k) {
		if (k != DisplayDevice.SECONDARY_SCREEN) {
			init(PrimaryScreen.RedPrimary, PrimaryScreen.GreenPrimary,
					PrimaryScreen.BluePrimary, PrimaryScreen.RedGamma,
					PrimaryScreen.GreenGamma, PrimaryScreen.BlueGamma,
					PrimaryScreen.ColorDeviceDACRange,
					PrimaryScreen.DeviceColorTableFile);
		} else {
			init(SecondaryScreen.RedPrimary, SecondaryScreen.GreenPrimary,
					SecondaryScreen.BluePrimary, SecondaryScreen.RedGamma,
					SecondaryScreen.GreenGamma, SecondaryScreen.BlueGamma,
					SecondaryScreen.ColorDeviceDACRange,
					SecondaryScreen.DeviceColorTableFile);
		}
		initSystemColors();
	}

	/**
	 * Create a color device transform for video screens. Creating a color
	 * device implies that the transform from XYZ to the linear RGB space
	 * spanned by the device's basis vectors is initialized and that the gamma
	 * transforms of the color channels are created.
	 */
	public ScreenColorTransform(ExPar redPrimary, ExPar greenPrimary,
			ExPar bluePrimary, ExPar redGamma, ExPar greenGamma,
			ExPar blueGamma, ExPar dacRange, ExPar tableFile) {
		init(redPrimary, greenPrimary, bluePrimary, redGamma, greenGamma,
				blueGamma, dacRange, tableFile);
		initSystemColors();
	}

	/**
	 * Initialize this color device object according to the parameter settings
	 * of the experimental device parameters given in the argument list.
	 */
	private void init(ExPar redPrimary, ExPar greenPrimary, ExPar bluePrimary,
			ExPar redGamma, ExPar greenGamma, ExPar blueGamma, ExPar dacRange,
			ExPar tableFile) {
		double[][] p = new double[3][3];
		double[] q;
		q = redPrimary.getDoubleArray();
		p[0][0] = q[0];
		p[0][1] = q[1];
		p[0][2] = q[2];
		q = greenPrimary.getDoubleArray();
		p[1][0] = q[0];
		p[1][1] = q[1];
		p[1][2] = q[2];
		q = bluePrimary.getDoubleArray();
		p[2][0] = q[0];
		p[2][1] = q[1];
		p[2][2] = q[2];
		setPrimaries(p);
		setDACRange(dacRange.getDouble());
		setGamma(redGamma.getDoubleArray(), greenGamma.getDoubleArray(),
				blueGamma.getDoubleArray());
		String dct = tableFile.getString();
		if (StringExt.nonEmpty(dct))
			setDeviceColorTable(dct);
	}

	private void initSystemColors() {
		white = inverseDev(Color.white);
		lightGray = inverseDev(Color.lightGray);
		gray = inverseDev(Color.gray);
		darkGray = inverseDev(Color.darkGray);
		black = inverseDev(Color.black);
		red = inverseDev(Color.red);
		yellow = inverseDev(Color.yellow);
		green = inverseDev(Color.green);
		magenta = inverseDev(Color.magenta);
		cyan = inverseDev(Color.cyan);
		blue = inverseDev(Color.blue);
	}
}
