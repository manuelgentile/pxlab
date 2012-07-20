package de.pxlab.pxl.display;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;

import de.pxlab.pxl.*;

/**
 * Capture the current content of the display screen and write it to a file. To
 * capture a screen simply insert this display immediately after the display
 * object which you want to capture:
 * 
 * <pre>
 *     Trial(GaborPatternAnimation.HighColor, 
 *           GaborPatternAnimation.AngleOfRotation, 
 *           GaborPatternAnimation.ResponseTime) {
 *       GaborPatternAnimation() {
 *         Timer = de.pxlab.pxl.TimerCodes.RESPONSE_TIMER;
 * 	Width = 300;
 * 	Height = 300;
 *     	FramesPerCycle = 12;
 * 	FrameDuration = 40;
 * 	AmplitudeModulation = 0;
 *       }
 *       ScreenCapture();
 *       Feedback() {
 * 	Text = "Time = %Trial.GaborPatternAnimation.ResponseTime%";
 * 	Duration = 2000;
 *         Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
 *       }
 *     }
 * </pre>
 * 
 * <p>
 * This captures the GaborPatternAnimation display object in exactly that state
 * which is shown when the response event happens. The names of the capture
 * files are generated automatically. The default file format is PNG.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
public class ScreenCapture extends Display {
	/** Image file name root. */
	public ExPar FileNameRoot = new ExPar(STRING, new ExParValue("PXLab"),
			"Image file name root");
	/**
	 * Image file type. Possible types are 'JPEG', 'PNG', 'BMP', or any other
	 * type supported by the javax.imageio package.
	 */
	public ExPar FileType = new ExPar(STRING, new ExParValue("PNG"),
			"Image file type");
	/**
	 * Image directory path name.
	 * <p>
	 * Note that the PXLab design file grammar uses the '\'-character in a
	 * string as an escape sequence. This means that strings like 'c:\Images'
	 * are NOT allowed and MUST be written as 'c:/Images'.
	 */
	public ExPar Directory = new ExPar(STRING, new ExParValue("C:/tmp"),
			"Image directory");

	public ScreenCapture() {
		setTitleAndTopic("Capture current display screen", CONTROL_DSP | EXP);
		setDisplayListControl();
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}
	private File f;
	private String fileType;

	protected void computeGeometry() {
		String dir = Directory.getString() + File.separator;
		String fnr = FileNameRoot.getString();
		fileType = FileType.getString();
		String ext = "." + fileType.toLowerCase();
		int i = 1;
		NumberFormat idf = NumberFormat.getInstance(Locale.US);
		idf.setMinimumIntegerDigits(5);
		idf.setGroupingUsed(false);
		f = new File(dir, fnr + idf.format(i++) + ext);
		while (f.exists()) {
			f = new File(dir, fnr + idf.format(i++) + ext);
		}
	}

	public boolean displayListControlState(ResponseController rc) {
		if (displayDevice instanceof ExperimentalDisplayDevice) {
			try {
				BufferedImage img = new Robot()
						.createScreenCapture(new Rectangle(
								((ExperimentalDisplayDevice) displayDevice)
										.getLocation(),
								((ExperimentalDisplayDevice) displayDevice)
										.getSize()));
				try {
					javax.imageio.ImageIO.write(img, fileType, f);
				} catch (IOException iox) {
				}
			} catch (AWTException ax) {
			}
		}
		return (true);
	}
}
