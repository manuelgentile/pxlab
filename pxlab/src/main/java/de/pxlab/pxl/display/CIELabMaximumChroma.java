package de.pxlab.pxl.display;

import java.awt.*;

import de.pxlab.pxl.*;

/**
 * Two adjacent bars comparing a color with another color having the same
 * lightness and hue but having maximum chroma in CIELab. The maximum chroma
 * color is defined by the CIELab value of the coordinates defined in parameter
 * Color and the chroma is the maximum chroma which can be shown on the current
 * color device.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 07/25/01
 */
public class CIELabMaximumChroma extends SimpleBar {
	/**
	 * Returns the coordinates of the maximum chroma color relative to the color
	 * defined in parameter Color.
	 */
	public ExPar MaxChromaColor = new ExPar(DEPCOLOR, new ExParValue(
			new ExParExpression(ExParExpression.GRAY)), "Maximum chroma color");

	public CIELabMaximumChroma() {
		setTitleAndTopic("CIELab maximum Chroma color", COLOR_SPACES_DSP);
	}
	private int cielab_bar_idx;

	protected int create() {
		cielab_bar_idx = enterDisplayElement(new Bar(this.MaxChromaColor),
				group[0]);
		return super.create();
	}

	protected void computeColors() {
		double[] LCh = Color.getPxlColor().transform(PxlColor.CS_LabLChStar);
		LCh[1] = CIELabStar.maxChroma(LCh[0], LCh[1], LCh[2]);
		MaxChromaColor.set(PxlColor.instance(PxlColor.CS_LabLChStar, LCh));
	}

	protected void computeGeometry() {
		int w2 = Width.getInt() / 2;
		int h = Height.getInt();
		getDisplayElement(bar_idx).setRect(LocationX.getInt() - w2,
				LocationY.getInt() - h / 2, w2, h);
		getDisplayElement(cielab_bar_idx).setRect(LocationX.getInt(),
				LocationY.getInt() - h / 2, w2, h);
	}
}
