package de.pxlab.pxl.display;

import java.io.*;
import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.*;
import de.pxlab.util.*;

/**
 * A base class for sound objects. The presentation of a sound object is started
 * by the respective subclass object of this class and runs in a separate thread
 * until all generated sample points have been presented. This means that Sound
 * objects should always be used as overlay objects on preceding visual objects
 * which remain visible on the screen while the sound is playing.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/03/04
 */
abstract public class SoundDisplay extends Display {
	/**
	 * Sound directory path name.
	 * <p>
	 * Note that the PXLab design file grammar uses the '\'-character in a
	 * string as an escape sequence. This means that strings like 'c:\Images'
	 * are NOT allowed and MUST be written as 'c:/Images'.
	 * 
	 * @see de.pxlab.pxl.FileBase
	 */
	public ExPar Directory = new ExPar(STRING, new ExParValue(""),
			"Sound directory path");
	/**
	 * Sound file name.
	 * 
	 * @see de.pxlab.pxl.FileBase
	 */
	public ExPar FileName = new ExPar(STRING, new ExParValue(
			"%SubjectCode%_%SessionCounter%_%TrialCounter%.wav"),
			"Sound file name");
	/** The volume level of the sound device. */
	public ExPar Volume = new ExPar(PROPORT, new ExParValue(1.0), "Volume");

	/**
	 * Always return false to indicate that this object does not use PXLab's
	 * graphic display caching technology.
	 */
	public boolean isGraphic() {
		return false;
	}

	/**
	 * Create the name of a writable sound output file. The name is derived from
	 * the parameters Directory and FileName.
	 * 
	 * <ul>
	 * 
	 * <li>If Directory is empty then no file name is created.
	 * 
	 * <li>If Directory is equal to 'tmp' or 'temp' then the directory becomes
	 * the value of the respective environment variable whose value is requested
	 * from the operating system.
	 * 
	 * <li>If Directory is an ordinary string then it is assumed to be the
	 * directory for the file.
	 * 
	 * <li>The parameter FileName is the name of the file. If there is no
	 * extension then 'wav' is used as an extension. If the extension is
	 * different from 'wav' then it is changed to 'wav'.
	 * 
	 * </ul>
	 * 
	 * @return the name of a file which may be used to write sound data to it or
	 *         null if the parameter Directory is null or is an empty string.
	 */
	protected String soundOutputFileName() {
		String dir = Directory.getString();
		String p = null;
		if (StringExt.nonEmpty(dir)) {
			p = FileBase.createResourcePath(dir, FileName.getString());
			String ext = FileExt.getExtension(p);
			if (ext != null) {
				if (!ext.toLowerCase().equals("wav")) {
					// Wrong extension
					new FileError(
							"SoundDisplay.soundOutputFileName(): Illegal file type "
									+ p + ", changed to 'WAV'");
					String base = FileExt.getBase(p);
					p = base + ".wav";
				}
			} else {
				// No extension
				p = p + ".wav";
			}
		}
		Debug.show(Debug.FILES,
				"SoundDisplay.soundOutputFileName(): File name: " + p);
		return p;
	}

	abstract public AudioFormat getAudioFormat();

	abstract public ShortBuffer getAudioDataAsShort();

	abstract public byte[] getAudioData();
}
