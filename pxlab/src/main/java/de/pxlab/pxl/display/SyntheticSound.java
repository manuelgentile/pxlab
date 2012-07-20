package de.pxlab.pxl.display;

import java.io.*;
import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.*;
import de.pxlab.pxl.sound.*;

/**
 * A synthetic sound object which is defined by an envelope function modulating
 * a base wave type. Envelope functions may be defined independently for the two
 * available channels.
 * 
 * 
 * <p>
 * Envelope types are defined by parameter Envelope and the envelope parameters
 * are defined by EnvelopePars. If the sound signal has two channels then the
 * channels my have independent envelope functions. In this case Envelope may be
 * an array with two envelope type values. If two independent enevlopes are to
 * be defined then the parameter EvelopePars contains parameters of both
 * channels. The parameters for the left channel come first and the parameters
 * for the right channel follow.
 * 
 * <p>
 * The following envelope functions are supported:
 * 
 * <dl>
 * 
 * <dt>SoundEnvelopeCodes.CONSTANT
 * <dd>A constant function.
 * 
 * <dt>SoundEnvelopeCodes.GAUSSIAN
 * <dd>A Gaussian envelope whose maximum is at half of the sound Duration. The
 * parameter EnvelopePars contains only a single value, the standard deviation
 * of the Gaussian.
 * 
 * <dt>SoundEnvelopeCodes.GAUSSIAN2
 * <dd>A Gaussian envelope whose maximum position depends on a parameter. The
 * parameter EnvelopePars contains the maximum position and the standard
 * deviation.
 * 
 * <dt>SoundEnvelopeCodes.SINUSOIDAL
 * <dd>A sinusoidal envelope which has no parameters.
 * 
 * <dt>SoundEnvelopeCodes.LINEAR_ASR
 * <dd>An envelope which has a linear attack, sustain, and release period. The
 * parameters are the attack and the release duration. The sustain duration is
 * computed from the sound's total duration.
 * 
 * <dt>SoundEnvelopeCodes.GAUSSIAN_ASR
 * <dd>An envelope which has a Gaussian attack and release period. The
 * parameters are the attack duration, the standard deviation of the attack
 * Gaussian, the release duration, and the standard deviation of the release
 * Gaussian.
 * 
 * <dt>SoundEnvelopeCodes.SINUSOIDAL_ASR
 * <dd>An envelope which has a sinusoidal attack and release period. The
 * parameters are the attack and the release duration. The sustain duration is
 * computed from the sound's total duration.
 * 
 * </dl>
 * 
 * 
 * <p>
 * The following wave types are supported:
 * 
 * <dl>
 * 
 * <dt>SoundWaveCodes.SINGLE_SINE
 * <dd>A sine wave with the frequency as its only parameter.
 * 
 * <dt>SoundWaveCodes.FOURIER_SERIES
 * <dd>A Fourier series. The Fourier series is defined by
 * 
 * <P>
 * S(t) = Sum(k = 1, ..., M) of [ak * cos(k * 2 * Pi * f0 * t) + bk * sin(k * 2
 * * Pi * f0 * t)].
 * 
 * <p>
 * The respective WavePars array contains the following values:
 * 
 * <p>
 * WavePars = [M, f0, a1, b1, a2, b2, ..., aM, bM]
 * 
 * <p>
 * The parameters must be such that the series strictly remains in the range (-2
 * <= y <= +2). Here is an example for a square wave with Gaussian envelope:
 * 
 * <pre>
 *         Trial("Gaussian Envelope\nSynthetic Square Wave", 1, 0, 
 * 		de.pxlab.pxl.SoundEnvelopeCodes.GAUSSIAN, [200.0],
 *                 de.pxlab.pxl.SoundWaveCodes.FOURIER_SERIES, 
 * 		[9.0, 400.0, 
 * 		0.0, 1.0, 
 * 		0.0, 0.0, 
 * 		0.0, 0.3333, 
 * 		0.0, 0.0, 
 * 		0.0, 0.2, 
 * 		0.0, 0.0, 
 * 		0.0, 0.14286, 
 * 		0.0, 0.0, 
 * 		0.0, 0.11111], ?);
 * </pre>
 * 
 * <dt>SoundWaveCodes.WHITE_NOISE
 * <dd>Pure white noise. The parameter WavePars is not used in this case.
 * 
 * </dl>
 * 
 * <p>
 * In every case a channel's signal is the product of the channel's envelope
 * function with the wave function.
 * 
 * <p>
 * This object always has to use a TimerCodes.END_OF_MEDIA_TIMER.
 * 
 * @author H. Irtel
 * @version 0.1.1
 * @see de.pxlab.pxl.SoundEnvelopeCodes
 * @see de.pxlab.pxl.SoundWaveCodes
 */
/*
 * 
 * 2005/03/04
 * 
 * 2005/05/03 fixed sound event handling to use TimerCodes.END_OF_MEDIA_TIMER
 */
public class SyntheticSound extends SoundDisplay {
	/** The number of channels used by this sound object. */
	public ExPar Channels = new ExPar(GEOMETRY_EDITOR,
			SoundChannelsCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.SoundChannelsCodes.MONO"),
			"Number of channels (1 or 2)");
	/**
	 * Duration of a quiet lead period for the sound object. This may be used
	 * for devices which present an audible noise when started too fast. The
	 * quiet lead is included in the stimulus Duration. This means that the
	 * actual duration of an envelope functions is Duration-QuietLead.
	 */
	public ExPar QuietLead = new ExPar(DOUBLE, new ExParValue(0.0),
			"Quiet lead period of envelope");
	/**
	 * The gain value for this sound object. Gain values satisfy 0 <= gain <=
	 * 1.0. The gain is the maximum intensity of a sound relative to the sound
	 * device's volume level.
	 */
	public ExPar Gain = new ExPar(PROPORT, new ExParValue(1.0), "Gain");
	/**
	 * The type of the sound envelope function.
	 * 
	 * @see de.pxlab.pxl.SoundEnvelopeCodes
	 */
	public ExPar Envelope = new ExPar(GEOMETRY_EDITOR,
			SoundEnvelopeCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.SoundEnvelopeCodes.CONSTANT"),
			"Envelope function type");
	/** The parameters of the sound envelope function. */
	public ExPar EnvelopePars = new ExPar(DOUBLE, new ExParValue(1.0),
			"Envelope parameters");
	/**
	 * The wave type of the sound.
	 * 
	 * @see de.pxlab.pxl.SoundWaveCodes
	 */
	public ExPar Wave = new ExPar(GEOMETRY_EDITOR, SoundWaveCodes.class,
			new ExParValueConstant("de.pxlab.pxl.SoundWaveCodes.PURE_TONE"),
			"Sound wave type");
	/** The parameters of the sound wave. */
	public ExPar WavePars = new ExPar(DOUBLE, new ExParValue(1000.0),
			"Sound wave type parameters");

	public SyntheticSound() {
		setTitleAndTopic("Synthetic Sound", AUDIO_DSP | EXP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.END_OF_MEDIA_TIMER"));
	}
	private SyntheticSoundElement soundElement;

	protected int create() {
		soundElement = new SyntheticSoundElement();
		enterDisplayElement(soundElement, group[0]);
		defaultTiming(0);
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		check4MediaTimer();
		soundElement
				.setProperties(Duration.getDouble(), Channels.getInt(), Volume
						.getDouble(), QuietLead.getDouble(), Gain
						.getDoubleArray(), Envelope.getIntArray(), EnvelopePars
						.getDoubleArray(), Wave.getIntArray(), WavePars
						.getDoubleArray(),
						(MediaEventListener) (presentationManager
								.getResponseManager()));
	}

	public AudioFormat getAudioFormat() {
		return soundElement.getAudioFormat();
	}

	public ShortBuffer getAudioDataAsShort() {
		return soundElement.getAudioDataAsShort();
	}

	public byte[] getAudioData() {
		return soundElement.getAudioData();
	}

	protected void timingGroupFinished(int group) {
		String sfn = soundOutputFileName();
		if (sfn != null) {
			byte[] audioData = getAudioData();
			AudioFormat audioFormat = getAudioFormat();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					audioData);
			AudioInputStream audioStream = new AudioInputStream(inputStream,
					audioFormat, audioData.length / audioFormat.getFrameSize());
			try {
				AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE,
						new File(sfn));
			} catch (IOException iox) {
				System.out
						.println("SyntheticSound.timingGroupFinished(): Error writing audio data file "
								+ sfn);
			}
		}
	}
}
