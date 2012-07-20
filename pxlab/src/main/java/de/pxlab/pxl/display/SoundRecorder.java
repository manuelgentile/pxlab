package de.pxlab.pxl.display;

import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.*;
import de.pxlab.util.StringExt;
import de.pxlab.pxl.sound.*;

/**
 * Records sound from the microphone and stores it into a sound file. The name
 * of the sound file is given by the parameter FileName. Its default value is
 * 
 * <pre>
 * FileName = &quot;%SubjectCode%_%SessionCounter%_%TrialCounter%.wav&quot;;
 * </pre>
 * <p>
 * This creates a new file name for every trial.
 * 
 * <p>
 * Recording duration is given by the Display object's Duration parameter.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 2005/03/04
 */
public class SoundRecorder extends SoundDisplay {
	/** The number of channels used by this sound object. */
	public ExPar Channels = new ExPar(GEOMETRY_EDITOR,
			SoundChannelsCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.SoundChannelsCodes.MONO"),
			"Number of channels (1 or 2)");
	/** The sampling rate. */
	public ExPar SampleRate = new ExPar(INTEGER, new ExParValue(22050),
			"Sample rate");

	public SoundRecorder() {
		setTitleAndTopic("Sound recorder", AUDIO_DSP | EXP);
		Directory.set(".");
	}
	private SoundRecorderElement recorder;

	protected int create() {
		recorder = new SoundRecorderElement();
		enterDisplayElement(recorder, group[0]);
		defaultTiming(0);
		return (backgroundFieldIndex);
	}

	protected void computeGeometry() {
		recorder.setProperties(Channels.getInt(), Volume.getDouble(),
				SampleRate.getDouble(), soundOutputFileName());
	}

	public AudioFormat getAudioFormat() {
		return recorder.getAudioFormat();
	}

	public ShortBuffer getAudioDataAsShort() {
		return recorder.getAudioDataAsShort();
	}

	public byte[] getAudioData() {
		return recorder.getAudioData();
	}

	protected void timingGroupFinished(int group) {
		recorder.stop();
	}
}
