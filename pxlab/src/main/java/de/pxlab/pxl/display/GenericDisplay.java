package de.pxlab.pxl.display;

// import java.awt.*;
import de.pxlab.pxl.*;

/**
 * This is a display object which contains a single display element. The display
 * element class is determined at runtime.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 01/31/04
 */
public class GenericDisplay extends FontDisplay {
	/* Changing the default also requires changing the create() method. */
	private static final String defaultClassName = "de.pxlab.pxl.TextParagraphElement";
	/** Code of the object type. */
	public ExPar ClassName = new ExPar(STRING,
			new ExParValue(defaultClassName), "DisplayElement Class Name");
	/** Object content. */
	public ExPar Content = new ExPar(STRING, new ExParValue(""),
			"Object Content");
	/** Object width. */
	public ExPar Size = new ExPar(HORSCREENSIZE, new ExParValue(800),
			"Width of Text Paragraph");
	/** Horizontal center position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal Center Location");
	/** Vertical center position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical Center Location");

	public GenericDisplay() {
		setTitleAndTopic("Generic Display Object", TEXT_PAR_DSP);
	}
	protected int generic_idx;

	/** Initialize the display list of the demo. */
	protected int create() {
		generic_idx = enterDisplayElement(new TextParagraphElement(this.Color),
				group[0]);
		defaultTiming(0);
		return (generic_idx);
	}

	protected void computeGeometry() {
		boolean displayElementValid = true;
		DisplayElement generic = getDisplayElement(generic_idx);
		String cn = ClassName.getString();
		if (!cn.equals(generic.getClass().getName())) {
			removeDisplayElements(generic_idx);
			generic = (DisplayElement) DisplayElement.load(cn);
			if (generic != null) {
				generic.setColorPar(this.Color);
			} else {
				generic = new TextParagraphElement(Color);
				displayElementValid = false;
			}
			enterDisplayElement(generic, group[0]);
		}
		if (displayElementValid) {
			String currentClass = generic.getClass().getName();
			if (currentClass.equals("de.pxlab.pxl.TextParagraphElement")) {
				double w = Size.getDouble();
				int ww = (w < 1.0) ? (int) (width * w) : (int) w;
				((TextParagraphElement) generic).setProperties(
						Content.getStringArray(), FontFamily.getString(),
						FontStyle.getInt(), FontSize.getInt(),
						LocationX.getInt(), LocationY.getInt(), ww,
						PositionReferenceCodes.MIDDLE_CENTER, // ReferencePoint.getInt(),
						AlignmentCodes.CENTER, // Alignment.getInt(),
						false, // EmphasizeFirstLine
						true, // (Wrapping.getInt() != 0),
						1.0); // LineSkipFactor.getDouble());
			} else if (currentClass.equals("de.pxlab.pxl.BitMapElement")) {
				generic.setLocation(LocationX.getInt(), LocationY.getInt());
				((BitMapElement) generic).setImage(null, Content.getString());
				((BitMapElement) generic)
						.setReferencePoint(PositionReferenceCodes.MIDDLE_CENTER);
			} else {
				new DisplayError("Class " + currentClass
						+ " currently not available in generic Displays.");
				generic = new TextParagraphElement(Color);
				((TextParagraphElement) generic)
						.setText("Generic DisplayElement Error!");
			}
		} else {
			((TextParagraphElement) generic)
					.setText("Generic DisplayElement Error!");
		}
	}
}
