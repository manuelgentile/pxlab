package de.pxlab.pxl.display;

import java.util.Random;

import de.pxlab.pxl.*;

/**
 * Compute a random result. Type of result and distribution depend on the
 * parameter DistributionType.
 * 
 * <p>
 * If DistributionType is MULTINOMIAL_DISTRIBUTION then we sample a single
 * element from a small set of alternatives according to a multinomial
 * distribution. The probabilities are defined by the parameter
 * ProbabilityDistribution which also implicitly defines the number of
 * alternatives to sample from. This is always one more than the number of
 * entries in ProbabilityDistribution. The sampling set either is the set of
 * integer numbers 0, ..., n, where n is the number of entries in
 * ProbabilityDistribution or the sampling set is the set of entries contained
 * in the parameter ResponseSet if this parameter is defined. The resulting
 * random element is stored in the parameter ResponseCode. If ResponseSet is
 * undefined then the ResponseCode always is an integer number 0, ..., n. If
 * ResponseSet is defined then the ResponseCode is the respective entry in
 * ResponseSet at that index which has been sampled.
 * 
 * <p>
 * Here is an example from a trial which shows one of the three labels "High",
 * "Low", and "Strange" with respective probability 0.6, 0.3, and 0.1:
 * 
 * <pre>
 *     Trial() {
 *       RandomGenerator() {
 *         ProbabilityDistribution = [0.6, 0.3];
 * 	ResponseSet = ["High", "Low", "Strange"];
 *       }
 *       Message() {
 * 	Text = "%Trial.RandomSampler.ResponseCode%";
 *         Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
 *         Duration = 500;
 *       }
 *       ClearScreen:clear() {
 *         Timer = de.pxlab.pxl.TimerCodes.CLOCK_TIMER;
 *         Duration = 100;
 *       }
 *     }
 * </pre>
 * 
 * <p>
 * If DistributionType is EQUAL_DISTRIBUTION_INT or EQUAL_DISTRIBUTION_DOUBLE
 * then a random integer or double is sampled from an equal distribution with
 * LowerLimit and UpperLimit as its range limits. The result is stored in
 * ResponseCode. For EQUAL_DISTRIBUTION_INT both the lower and upper limits are
 * contained in the random generator's range of values. For
 * EQUAL_DISTRIBUTION_DOUBLE only the lower limit is contained in the range, the
 * upper limit is excluded.
 * 
 * <p>
 * If DistributionType is NORMAL_DISTRIBUTION then a normally distributed random
 * number is sampled with expected value defined by parameter Mean and standard
 * deviation defined by parameter StandardDeviation.
 * 
 * @version 0.3.0
 */
/*
 * 
 * 09/01/03
 * 
 * 2005/05/31 added equal distribution for integers and doubles.
 */
public class RandomGenerator extends Display implements RandomGeneratorCodes {
	private static Random rand = null;
	/**
	 * The type of probability distribution used for sampling.
	 * 
	 * @see de.pxlab.pxl.RandomGeneratorCodes
	 */
	public ExPar DistributionType = new ExPar(
			GEOMETRY_EDITOR,
			RandomGeneratorCodes.class,
			new ExParValueConstant(
					"de.pxlab.pxl.RandomGeneratorCodes.MULTINOMIAL_DISTRIBUTION"),
			"Probability distribution type");
	/**
	 * The probability distribution for the intended set of alternatives in a
	 * multinomial distribution. For a multinomial distribution the set of
	 * alternatives is one more than the number of entries in this parameter.
	 * The sum of all entries must be smaller than 1.0 in this case.
	 */
	public ExPar ProbabilityDistribution = new ExPar(PROPORT, new ExParValue(
			0.5), "Probability distribution");
	/**
	 * Lower limit of the random number for equal distributions. The lower
	 * limiting value is contained in the range of random numbers.
	 */
	public ExPar LowerLimit = new ExPar(0, 1000, new ExParValue(100),
			"Lower limit (contained in range)");
	/**
	 * Upper limit of the random number for equal distributions. The upper
	 * limiting value is not contained in the range of random numbers if the
	 * distribution type is EQUAL_DOUBLE. It is contained in the range for
	 * distribution type EQUAL_INT.
	 */
	public ExPar UpperLimit = new ExPar(9, 9999, new ExParValue(999),
			"Upper limit (not in range)");
	/**
	 * Mean value for distributions which are conveniently specified by their
	 * mean value like the normal distribution.
	 */
	public ExPar Mean = new ExPar(-100.0, 100.0, new ExParValue(0.0), "Mean");
	/**
	 * Standard deviation value for distributions which are conveniently
	 * specified by their mean value like the normal distribution.
	 */
	public ExPar StandardDeviation = new ExPar(0.0, 10.0, new ExParValue(1.0),
			"Standard deviation");

	public RandomGenerator() {
		setTitleAndTopic("Random result generator", PARAM_DSP | EXP);
		setVisible(false);
		Timer.set(new ExParValueConstant("de.pxlab.pxl.TimerCodes.NO_TIMER"));
	}

	protected int create() {
		return backgroundFieldIndex;
	}

	protected void computeGeometry() {
		// System.out.println("RandomGenerator.computeGeometry() ResponseSet = "
		// + ResponseSet.getValue().getValue());
		// System.out.println("RandomGenerator.computeGeometry() ProbabilityDistribution = "
		// + ProbabilityDistribution.getValue());
		if (rand == null)
			rand = new Random();
		int type = DistributionType.getInt();
		if (type == MULTINOMIAL_DISTRIBUTION) {
			double[] pd = ProbabilityDistribution.getDoubleArray();
			int n = pd.length;
			if (n < 1)
				return;
			double[] p = new double[n + 1];
			p[0] = pd[0];
			for (int i = 1; i < n; i++)
				p[i] = p[i - 1] + pd[i];
			p[n] = 1.0;
			double r = rand.nextDouble();
			int m;
			int rc = n;
			for (m = 0; m <= n; m++) {
				if (r <= p[m]) {
					rc = m;
					break;
				}
			}
			ExParValue v = ResponseSet.getValue().getValue();
			if (v.isEmpty()) {
				ResponseCode.set(rc);
			} else {
				if (v.length != (n + 1)) {
					new ParameterValueError(
							"RandomGenerator: Length of ResponseSet and length of ProbabilityDistribution don't fit: "
									+ v.length
									+ " != ("
									+ n
									+ " + 1). Using index as result.");
					ResponseCode.set(rc);
				} else {
					ExParValue vv = v.getValueAt(rc);
					if (vv != null) {
						ResponseCode.getValue().set(vv);
					} else {
						ResponseCode.set(-1);
					}
				}
			}
		} else if (type == EQUAL_DISTRIBUTION_INT) {
			int n1 = LowerLimit.getInt();
			int n2 = UpperLimit.getInt();
			int r = rand.nextInt(n2 - n1 + 1) + n1;
			ResponseCode.set(r);
		} else if (type == EQUAL_DISTRIBUTION_DOUBLE) {
			double n1 = LowerLimit.getDouble();
			double n2 = UpperLimit.getDouble();
			double r = (n2 - n1) * rand.nextDouble() + n1;
			ResponseCode.set(r);
		} else if (type == NORMAL_DISTRIBUTION) {
			double mean = Mean.getDouble();
			double sd = StandardDeviation.getDouble();
			double r = sd * rand.nextGaussian() + mean;
			ResponseCode.set(r);
		}
		// System.out.println("RandomGenerator.computeGeometry() r = " +
		// ResponseCode);
	}
}
