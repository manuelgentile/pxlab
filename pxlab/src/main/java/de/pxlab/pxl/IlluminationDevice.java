package de.pxlab.pxl;

/**
 * Describes an illumination device with a type of gamma function which is
 * essentially different from those found at video monitors. The device also has
 * a set() method for setting the illumination color. The instantiation method
 * is used to connect to the device's hardware.
 * 
 * <p>
 * The gamma correction function used here is modelled after the following
 * paper:
 * 
 * <p>
 * Stanislaw, H. & Olzak, L. A. (1990). Parameteric methods for gamma and
 * inverse gamma correction, with extensions to halftoning. Behavior Research
 * Methods, Instruments & Computers, 22, 402-408.
 * 
 * <p>
 * The equation is:
 * 
 * <pre>
 *         exp[p0 + p1*(ln s) + p2*(ln s)2 + p3*(ln s)3]  if s > s0
 *     e = 
 *         q0 + q1*s + q2*s2                              else
 * </pre>
 * 
 * @author H. Irtel
 * @version 0.1.1
 */
/*
 * 
 * 03/13/03
 */
public class IlluminationDevice extends ColorDeviceTransform {
	private PortIO port;
	private String wascoBoard = "IODA-PCI12K8/extended";

	public IlluminationDevice(int devCode) {
		super();
		setPrimaries(PhosphorChromaticities.OsramColorTubes);
		setDACRange(4095.0);
		port = new PortIO(wascoBoard);
		port.open();
	}

	public void set(PxlColor c) {
		// System.out.println("Setting Illumination device to color " + c);
		set(this.devRGB(c.getComponents()));
	}

	public void set(double[] a) {
		// System.out.println("IlluminationDevice.set([" + a[0] + " ," + a[1] +
		// " ," + a[2] + "])");
		port.analogOut(1, (int) (DACRange * a[0]));
		port.analogOut(2, (int) (DACRange * a[1]));
		port.analogOut(3, (int) (DACRange * a[2]));
	}

	public void close() {
		port.close();
	}

	/**
	 * Return the red channel's linear R value from its RGB space which must be
	 * used to arrive at the given device output.
	 */
	protected double redInverseDev(double x) {
		return (ivg(0, x));
	}

	/**
	 * Return the green channel's linear G value from its RGB space which must
	 * be used to arrive at the given device output.
	 */
	protected double greenInverseDev(double x) {
		return (ivg(1, x));
	}

	/**
	 * Return the blue channel's linear B value from its RGB space which must be
	 * used to arrive at the given device output.
	 */
	protected double blueInverseDev(double x) {
		return (ivg(2, x));
	}
	/**
	 * These parameters are used to linearize the DAC-Input to
	 * Illumination-Output relation. See ivg() what type of gamma function we
	 * use. (Estimates from Jan 30, 1997)
	 */
	private double[][] InvIllumGammaPar = {
			{ -0.017332, 0.859806, 0.208369, 0.0410954 },
			{ -0.0126193, 0.824282, 0.174151, 0.0293856 },
			{ -0.0096314, 0.800906, 0.151357, 0.02209 } };
	/**
	 * The Osram dimmer has a lower limit which is around 10% of its maximum.
	 */
	private static final double DIMMER_OUT_LOW = 0.00;

	/**
	 * Inverse gamma transform for the illumination source. This function
	 * accepts input values in the range [DIMMER_OUT_LOW, 1.0] and returns
	 * output values in the range [0.0, 1.0]. Thus in order to get the DAC
	 * control value for a single channel the required light output x has to be
	 * normalized to the maximum light output x_max and the resulting (x/x_max)
	 * may be used as input to ivg() for finding the DAC control parameter:
	 * DAC-control = ivg(color, x/x_max). The function used here is a polynomial
	 * of degree 3.
	 */
	private double ivg(int i, double x) {
		double lnx, lnx2;
		if (x < DIMMER_OUT_LOW)
			return 0.0;
		if (x > 1.0)
			return 1.0;
		lnx = Math.log(x);
		lnx2 = lnx * lnx;
		return (Math.exp(InvIllumGammaPar[i][0] + InvIllumGammaPar[i][1] * lnx
				+ InvIllumGammaPar[i][2] * lnx2 + InvIllumGammaPar[i][3] * lnx
				* lnx2));
	}
}
