package de.pxlab.pxl.sound;

import java.nio.*;
import javax.sound.sampled.*;

/**
 * A factory for creating synthetic sound signals. Sound signals have an
 * AudioFormat object which determines the sample rate and the number of
 * channels and they have an envelope function and a wave function.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 2005/03/03
 */
public class SoundFactory {
	private AudioFormat audioFormat;
	private SoundEnvelope leftEnvelope;
	private SoundEnvelope rightEnvelope;
	private int nSamples;
	private int nChannels;
	private int nBytes;
	private byte[] audioDataBuffer;
	private ByteBuffer byteBuffer;
	private ShortBuffer shortBuffer;
	/** Time step for sound computation. Size given in seconds. */
	private double timeStep;

	/** Create a sound factory for the given AudioFormat. */
	public SoundFactory(AudioFormat format) {
		setAudioFormat(format);
	}

	/** Create a sound factory for the given AudioFormat and SoundEnvelope. */
	public SoundFactory(AudioFormat format, SoundEnvelope envelope) {
		setAudioFormat(format);
		setEnvelope(envelope, envelope);
	}

	/**
	 * Create a sound factory for the given AudioFormat and SoundEnvelope.
	 * 
	 * @param format
	 *            the AudioFormat to be used.
	 * @param leftEnvelope
	 *            the left channel SoundEnvelope.
	 * @param rightEnvelope
	 *            the right channel SoundEnvelope.
	 */
	public SoundFactory(AudioFormat format, SoundEnvelope leftEnvelope,
			SoundEnvelope rightEnvelope) {
		setAudioFormat(format);
		setEnvelope(leftEnvelope, rightEnvelope);
	}

	/** Set the SoundEnvelope of this factory. */
	public void setEnvelope(SoundEnvelope leftEnvelope,
			SoundEnvelope rightEnvelope) {
		this.leftEnvelope = leftEnvelope;
		this.rightEnvelope = rightEnvelope;
	}

	private void setAudioFormat(AudioFormat format) {
		if (format.getSampleSizeInBits() != 16) {
			System.out
					.println("SoundFactory supports 16-bit audio formats only.");
			System.exit(3);
		}
		if (format.getSampleRate() != 44100.0F) {
			System.out
					.println("SoundFactory supports 44100 samples per second audio formats only.");
			System.exit(3);
		}
		// Hier noch andere checks einfuegen
		audioFormat = format;
	}
	/**
	 * A scaling factor which moves the range (-1 <= x <= +1) onto the range of
	 * short integers.
	 */
	private static final double SHORT_SCALE_FACTOR = 32766.0;
	/**
	 * A scaling factor which moves the range (-2 <= x <= +2) onto the range of
	 * short integers.
	 */
	private static final double SHORT_SCALE_FACTOR_2 = 16383.0;

	/**
	 * Create a pure sinusoidal tone of the given duration and frequency using
	 * the current AudioFormat and SoundEnvelope.
	 * 
	 * @param s
	 *            duration in milliseconds,
	 * @param f
	 *            pure tone frequency.
	 * @return a byte buffer containing the audio samples in the current
	 *         AudioFormat.
	 */
	public byte[] getSineWave(double s, double f) {
		computeSoundParams(s);
		double x = 0.0;
		double y;
		if (nChannels == 1) {
			double e1;
			for (int i = 0; i < nSamples; i++) {
				e1 = leftEnvelope.valueOf(x);
				y = Math.sin(2 * Math.PI * f * x);
				shortBuffer.put((short) (Math
						.round(e1 * y * SHORT_SCALE_FACTOR)));
				// if ((i%4410) == 0) System.out.println(i + ": " + e1 + " x " +
				// y);
				x += timeStep;
			}
		} else if (nChannels == 2) {
			double e1, e2;
			for (int i = 0; i < nSamples; i++) {
				e1 = leftEnvelope.valueOf(x);
				e2 = rightEnvelope.valueOf(x);
				y = Math.sin(2 * Math.PI * f * x) * SHORT_SCALE_FACTOR;
				shortBuffer.put((short) (Math.round(e1 * y)));
				shortBuffer.put((short) (Math.round(e2 * y)));
				x += timeStep;
			}
		}
		return audioDataBuffer;
	}

	/**
	 * Create a Fourier series synthetic tone of the given duration using the
	 * current AudioFormat and SoundEnvelope.
	 * 
	 * @param s
	 *            duration in milliseconds,
	 * @param p
	 *            parameter array of the Fourier series. The parameters must be
	 *            such that the series strictly remains in the range (-2 <= y <=
	 *            +2).
	 * @return a byte buffer containing the audio samples in the current
	 *         AudioFormat.
	 */
	public byte[] getFourierSeries(double s, double[] p) {
		computeSoundParams(s);
		int m = (int) Math.round(p[0]);
		if (p.length < 2 * (m + 1)) {
			System.out
					.println("SoundFactory.getFourierSeries(): not enough parameters for Fourier series of "
							+ m + " components.");
			new RuntimeException().printStackTrace();
		}
		double Pi2f0 = 2 * Math.PI * p[1];
		double jPi2f0x;
		double x = 0.0;
		double a, b, y;
		if (nChannels == 1) {
			double e1;
			for (int i = 0; i < nSamples; i++) {
				e1 = leftEnvelope.valueOf(x);
				y = 0.0;
				for (int j = 1; j <= m; j++) {
					a = p[j + j];
					b = p[j + j + 1];
					if (a != 0.0 || b != 0.0) {
						jPi2f0x = j * Pi2f0 * x;
						if (a != 0.0)
							y += a * Math.cos(jPi2f0x);
						if (b != 0.0)
							y += b * Math.sin(jPi2f0x);
					}
				}
				shortBuffer.put((short) (Math.round(e1 * y
						* SHORT_SCALE_FACTOR_2)));
				// if ((i%4410) == 0) System.out.println(i + ": " + e1 + " x " +
				// y);
				x += timeStep;
			}
		} else if (nChannels == 2) {
			double e1, e2;
			for (int i = 0; i < nSamples; i++) {
				e1 = leftEnvelope.valueOf(x);
				e2 = rightEnvelope.valueOf(x);
				y = 0.0;
				for (int j = 1; j <= m; j++) {
					a = p[j + j];
					b = p[j + j + 1];
					if (a != 0.0 || b != 0.0) {
						jPi2f0x = j * Pi2f0 * x;
						if (a != 0.0)
							y += a * Math.cos(jPi2f0x);
						if (b != 0.0)
							y += b * Math.sin(jPi2f0x);
					}
				}
				y *= SHORT_SCALE_FACTOR_2;
				shortBuffer.put((short) (Math.round(e1 * y)));
				shortBuffer.put((short) (Math.round(e2 * y)));
				x += timeStep;
			}
		}
		return audioDataBuffer;
	}

	/**
	 * Create a white noise signal of the given duration using the current
	 * AudioFormat and SoundEnvelope.
	 * 
	 * @param s
	 *            duration in seconds,
	 * @return a byte buffer containing the audio samples in the current
	 *         AudioFormat.
	 */
	public byte[] getWhiteNoise(double s) {
		computeSoundParams(s);
		double x = 0.0;
		if (nChannels == 1) {
			double y1, e1;
			for (int i = 0; i < nSamples; i++) {
				e1 = leftEnvelope.valueOf(x);
				y1 = noise(x) * SHORT_SCALE_FACTOR;
				shortBuffer.put((short) (Math.round(e1 * y1)));
				x += timeStep;
			}
		} else if (nChannels == 2) {
			double e1, e2, y1, y2;
			for (int i = 0; i < nSamples; i++) {
				e1 = leftEnvelope.valueOf(x);
				e2 = rightEnvelope.valueOf(x);
				y1 = noise(x) * SHORT_SCALE_FACTOR;
				y2 = noise(x) * SHORT_SCALE_FACTOR;
				shortBuffer.put((short) (Math.round(e1 * y1)));
				shortBuffer.put((short) (Math.round(e2 * y2)));
				x += timeStep;
			}
		}
		return audioDataBuffer;
	}

	private double noise(double x) {
		return 2.0 * Math.random() - 1.0;
	}

	/**
	 * Create the parameters and buffers needed for a sound of the given
	 * duration.
	 * 
	 * @param s
	 *            duration in milliseconds.
	 */
	private void computeSoundParams(double s) {
		nSamples = numberOfSamples(s);
		nChannels = audioFormat.getChannels();
		nBytes = nSamples * 2 * nChannels;
		audioDataBuffer = new byte[nBytes];
		byteBuffer = ByteBuffer.wrap(audioDataBuffer);
		shortBuffer = byteBuffer.asShortBuffer();
		timeStep = s / (1000.0 * nSamples);
		/*
		 * System.out.println("SoundFactory.computeSoundParams(): samples = " +
		 * nSamples);
		 * System.out.println("SoundFactory.computeSoundParams(): channels = " +
		 * nChannels);
		 * System.out.println("SoundFactory.computeSoundParams(): bytes = " +
		 * nBytes);
		 * System.out.println("SoundFactory.computeSoundParams(): timeStep = " +
		 * timeStep); /*
		 */
	}

	/**
	 * Compute the number of samples required for a given signal duration. This
	 * is the sample rate multiplied by the duration.
	 * 
	 * @param d
	 *            duration in milliseconds.
	 * @return the number of samples for the given duration.
	 */
	public int numberOfSamples(double d) {
		return (int) ((d * audioFormat.getSampleRate()) / 1000.0);
	}
}
