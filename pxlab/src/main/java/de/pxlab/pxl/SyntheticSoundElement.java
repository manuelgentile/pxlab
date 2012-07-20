package de.pxlab.pxl;

import java.io.*;
import java.nio.*;
import javax.sound.sampled.*;

import de.pxlab.pxl.sound.*;

/**
 * Creates and plays a synthetic sound object.
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 2005/03/04
 */
public class SyntheticSoundElement extends DisplayElement implements
		SoundEnvelopeCodes, SoundWaveCodes {
	/** Sound duration in milliseconds. */
	protected double duration;
	protected AudioFormat audioFormat;
	protected SoundFactory soundFactory = null;
	protected byte[] audioData;
	protected Player player;

	public SyntheticSoundElement() {
		type = DisplayElement.AUDIO_BEEP;
		setColorPar(ExPar.ScreenBackgroundColor);
	}

	/**
	 * Set the properties of the synthetic sound element. This method is called
	 * from computeGeometry() at the computation phase. It creates the audio
	 * data and a Player obejct for playing them.
	 */
	public void setProperties(double ms, int channels, double volume,
			double quiet, double[] gain, int[] envelope, double[] envelopePars,
			int[] wave, double[] wavePars, MediaEventListener mediaEventListener) {
		duration = ms;
		if (channels < 1)
			channels = 1;
		else if (channels > 2)
			channels = 2;
		audioFormat = new AudioFormat(44100.0F, 16, channels, true, true);
		;
		if (channels == 1) {
			// Single channel sounds
			soundFactory = new SoundFactory(audioFormat, createEnvelope(quiet,
					gain[0], envelope[0], 1, envelopePars));
		} else {
			// Dual channel sounds
			soundFactory = new SoundFactory(audioFormat, createEnvelope(quiet,
					gain[0], envelope[0], 1, envelopePars), createEnvelope(
					quiet, (gain.length == 2) ? gain[1] : gain[0],
					(envelope.length == 2) ? envelope[1] : envelope[0], 2,
					envelopePars));
		}
		switch (wave[0]) {
		case WHITE_NOISE_SOUND:
			audioData = soundFactory.getWhiteNoise(duration);
			break;
		case FOURIER_SERIES:
			audioData = soundFactory.getFourierSeries(duration, wavePars);
			break;
		case PURE_TONE:
		default:
			audioData = soundFactory.getSineWave(duration, wavePars[0]);
			break;
		}
		player = new Player(audioData, audioFormat);
		player.setMediaEventListener(mediaEventListener);
	}

	private SoundEnvelope createEnvelope(double q, double gain, int type,
			int n, double[] p) {
		SoundEnvelope e = null;
		int i;
		switch (type) {
		case GAUSSIAN:
			i = parIndex(type, n, 1, p);
			e = new GaussianEnvelope(q, (duration - q) / 2.0, p[i]);
			break;
		case GAUSSIAN2:
			i = parIndex(type, n, 2, p);
			e = new GaussianEnvelope(q, p[i], p[i + 1]);
			break;
		case SINUSOIDAL:
			e = new SinusoidalEnvelope(duration, q);
			break;
		case LINEAR_ASR:
			i = parIndex(type, n, 2, p);
			e = new LinearASREnvelope(duration, q, p[i], p[i + 1]);
			break;
		case GAUSSIAN_ASR:
			i = parIndex(type, n, 4, p);
			e = new GaussianASREnvelope(duration, q, p[i], p[i + 1], p[i + 2],
					p[i + 3]);
			break;
		case SINUSOIDAL_ASR:
			i = parIndex(type, n, 2, p);
			e = new SinusoidalASREnvelope(duration, q, p[i], p[i + 1]);
			break;
		default:
			e = new ConstantEnvelope(q);
		}
		e.setGain(gain);
		return e;
	}

	private int parIndex(int type, int chn, int n, double[] p) {
		int i = (chn == 1) ? 0 : n;
		int required = i + n;
		if (p.length < required) {
			System.out.println("SoundElement: Envelope type " + type
					+ " for channel " + chn + " needs " + required
					+ " parameters and has only " + p.length);
			if (p.length >= n) {
				i = 0;
				System.out
						.println("SoundElement: Using envelope parameters of channel 1");
			} else {
				System.out.println("SoundElement: Must exit");
				System.exit(3);
			}
		}
		return i;
	}

	/** Present this sound object. */
	public void show() {
		player.play();
	}

	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public ShortBuffer getAudioDataAsShort() {
		ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
		return byteBuffer.asShortBuffer();
	}

	public byte[] getAudioData() {
		return audioData;
	}
}
