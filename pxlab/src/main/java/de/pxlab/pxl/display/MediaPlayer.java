package de.pxlab.pxl.display;

import de.pxlab.pxl.*;

import de.pxlab.util.*;

/*
 import de.pxlab.pxl.sound.*;
 */
/**
 * Superclass for showing time based audiovisual media data. This class relies
 * on the <a href="http://java.sun.com/products/java-media/jmf/">Java Media
 * Framework (JMF)</a> package being installed. Media support depends on the set
 * of codecs which are installed in the system. Look into the JMF documentation
 * for <a href="http://java.sun.com/products/java-media/jmf/2.1.1/formats.html">
 * supported formats</a> or simply try it out.
 * 
 * <p>
 * This Display object does not have a background such that it always behaves as
 * if it had the TRANSPARENT overlay property. The Display is not preloadable
 * and thus it also behaves as if it had the JustInTime flag set.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 2005/05/06
 */
public class MediaPlayer extends Display {
	/** Media file name. */
	public ExPar FileName = new ExPar(STRING, new ExParValue(
			"%SubjectCode%_%SessionCounter%_%TrialCounter%.wav"),
			"Media data file");
	/**
	 * Movie directory path name.
	 * 
	 * <p>
	 * Note that the PXLab design file grammar uses the '\'-character in a
	 * string as an escape sequence. This means that strings like 'c:\Images'
	 * are NOT allowed and MUST be written as 'c:/Images'.
	 */
	public ExPar Directory = new ExPar(STRING, new ExParValue(""),
			"Media directory path");
	/** Horizontal frame position. */
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal frame position");
	/** Vertical frame position. */
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical frame position");
	/**
	 * Movie frame width. If both Width and Height are set to 0 then the movie
	 * will be shown in its predefined size. If both are larger than the
	 * available display device then the movie is shown in full-window size.
	 */
	public ExPar Width = new ExPar(HORSCREENSIZE, new ExParValue(0),
			"Frame width");
	/**
	 * Movie frame height. If both Width and Height are set to 0 then the movie
	 * will be shown in its predefined size. If both are larger than the
	 * available display device then the movie is shown in full-window size.
	 */
	public ExPar Height = new ExPar(VERSCREENSIZE, new ExParValue(0),
			"Frame height");
	/**
	 * Reference point for the frame. See <code>PositionReferenceCodes</code>
	 * for a description. By default the frame center is used as the reference
	 * position.
	 */
	public ExPar ReferencePoint = new ExPar(GEOMETRY_EDITOR,
			PositionReferenceCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.PositionReferenceCodes.MIDDLE_CENTER"),
			"Frame reference point");
	/**
	 * Flag to optimize the movie startup time. If this is set to 1 then
	 * everything is done in order to optimize the movie startup time. This
	 * includes prefetching movie file data, attaching the movie display frame
	 * to the display device window, and showing the first video frame as a
	 * static image in the movie display frame. All this is already done before
	 * the display list which contains the movie display is started. By default
	 * this flag is set to 0 since attaching the movie display frame to the
	 * display device before the movie display starts will overwrite the display
	 * window content created by earlier display objects. Using this flag is
	 * only then safe when preceding display objects do not create screen
	 * content in the movie display frame area and it is acceptable that the
	 * first movie frame is visible before the movie actually starts running.
	 * Depending on the underlying hardware speed setting this parameter may
	 * have no noticable effect.
	 */
	public ExPar FastStart = new ExPar(FLAG, new ExParValue(0),
			"Fast start flag");
	/**
	 * Flag to make the media file play repeatedly until the player is stopped.
	 */
	public ExPar Cycle = new ExPar(FLAG, new ExParValue(0),
			"Cyclic presentation flag");

	/** This class should not be instantiated by experimental design files. */
	protected MediaPlayer() {
	}

	public boolean getCanPreload() {
		return false;
	}

	public boolean isGraphic() {
		return false;
	}
	/**
	 * The key for entering the media player into the runtime registry.
	 */
	protected String mpKey = "MediaPlayerElement";
	protected MediaPlayerElement staticMediaPlayer = null;

	protected int create() {
		removeDisplayElements(backgroundFieldIndex);
		backgroundFieldIndex = -1;
		staticMediaPlayer = (MediaPlayerElement) RuntimeRegistry.get(mpKey);
		if (staticMediaPlayer == null) {
			staticMediaPlayer = new MediaPlayerElement();
			RuntimeRegistry.put(mpKey, staticMediaPlayer);
			// System.out.println("MediaPlayer.create(): New player created: " +
			// staticMediaPlayer);
		} else {
			// System.out.println("MediaPlayer.create(): Player exists: " +
			// staticMediaPlayer);
		}
		enterDisplayElement(staticMediaPlayer, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		String fn = FileName.getString();
		if (StringExt.nonEmpty(fn)) {
			staticMediaPlayer.setProperties(displayDevice, LocationX.getInt(),
					LocationY.getInt(), Width.getInt(), Height.getInt(),
					ReferencePoint.getInt(), Directory.getString(), fn,
					FastStart.getFlag(), Cycle.getFlag(),
					(MediaEventListener) (presentationManager
							.getResponseManager()));
		}
	}
}
