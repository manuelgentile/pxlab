package de.pxlab.stat;

/*
 Module:       chisq.c
 Purpose:      compute approximations to chisquare distribution probabilities
 Contents:     pochisq(), critchi()
 Uses:         poz() in z.c (Algorithm 209)
 Programmer:   Gary Perlman
 Organization: Wang Institute, Tyngsboro, MA 01879
 Tester:       compile with -DCHISQTEST to include main program
 Copyright:    none
 Tabstops:     4
 */
/** Static methods for the chi square distribution. */
public class ChiSquareDist {
	/* accuracy of critchi approximation */
	private static final double CHI_EPSILON = 0.000001;
	/* maximum chi square value */
	private static final double CHI_MAX = 99999.0;
	/* log (sqrt (pi)) */
	private static final double LOG_SQRT_PI = 0.5723649429247000870717135;
	/* 1 / sqrt (pi) */
	private static final double I_SQRT_PI = 0.5641895835477562869480795;
	/* max value to represent exp (x) */
	private static final double BIGX = 20.0;

	private static double ex(double x) {
		return (((x) < -BIGX) ? 0.0 : Math.exp(x));
	}

	/*
	 * double Prob[] = { .10, .05, .01, .005, .001, -1.0 }; public static void
	 * main (String[] args) { int df; int p; printf ("%-4s ", "df"); for (p = 0;
	 * Prob[p] > 0.0; p++) printf ("%8.3f ", Prob[p]); putchar ('\n'); for (df =
	 * 1; df < 30; df++) { printf ("%4d ", df); for (p = 0; Prob[p] > 0.0; p++)
	 * printf ("%8.3f ", critchi (Prob[p], df)); putchar ('\n'); } }
	 */
	/**
	 * Probability of chi sqaure value. Algorithm: Compute probability of chi
	 * square value. Adapted from: Hill, I. D. and Pike, M. C. Algorithm 299
	 * Collected Algorithms for the CACM 1967 p. 243 Updated for rounding errors
	 * based on remark in ACM TOMS June 1985, page 185
	 * 
	 * @param x
	 *            obtained chi-square value.
	 * @param df
	 *            degrees of freedom.
	 */
	public static double probabilityOf(double x, int df) {
		double a, y = 0.0, s;
		double e, c, z;
		boolean even; /* true if df is an even number */
		if (x <= 0.0 || df < 1)
			return (1.0);
		a = 0.5 * x;
		even = ((2 * (df / 2)) == df);
		if (df > 1)
			y = ex(-a);
		s = (even ? y : (2.0 * ZDist.probabilityOf(-Math.sqrt(x))));
		if (df > 2) {
			x = 0.5 * (df - 1.0);
			z = (even ? 1.0 : 0.5);
			if (a > BIGX) {
				e = (even ? 0.0 : LOG_SQRT_PI);
				c = Math.log(a);
				while (z <= x) {
					e = Math.log(z) + e;
					s += ex(c * z - a - e);
					z += 1.0;
				}
				return (s);
			} else {
				e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
				c = 0.0;
				while (z <= x) {
					e = e * (a / z);
					c = c + e;
					z += 1.0;
				}
				return (c * y + s);
			}
		} else
			return (s);
	}

	/** Compute critical chi square value to produce given p */
	public static double criticalValueFor(double p, int df) {
		double minchisq = 0.0;
		double maxchisq = CHI_MAX;
		double chisqval;
		if (p <= 0.0)
			return (maxchisq);
		else if (p >= 1.0)
			return (0.0);
		chisqval = df / Math.sqrt(p); /* fair first value */
		while (maxchisq - minchisq > CHI_EPSILON) {
			if (probabilityOf(chisqval, df) < p)
				maxchisq = chisqval;
			else
				minchisq = chisqval;
			chisqval = (maxchisq + minchisq) * 0.5;
		}
		return (chisqval);
	}
}
