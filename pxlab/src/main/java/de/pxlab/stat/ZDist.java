package de.pxlab.stat;

/*HEADER
 Module:       z.c
 Purpose:      compute approximations to normal z distribution probabilities
 Programmer:   Gary Perlman
 Organization: Wang Institute, Tyngsboro, MA 01879
 Tester:       compile with -DZTEST to include main program
 Copyright:    none
 Tabstops:     4
 */
/** Static methods for the standard normal distribution. */
public class ZDist {
	/* accuracy of critz approximation */
	public static final double Z_EPSILON = 0.000001;
	/* maximum meaningful z value */
	public static final double Z_MAX = 6.0;

	/*
	 * #ifdef ZTEST main () { double z; printf ("%4s  %10s  %10s  %10s\n", "z",
	 * "poz(z)", "poz(-z)", "z'"); for (z = 0.0; z <= Z_MAX; z += .01) { printf
	 * ("%4.2f  %10.6f  %10.6f  %10.6f\n", z, poz (z), poz (-z), critz (poz
	 * (z))); } } #endif ZTEST
	 */
	/**
	 * Robability of normal z value. Algorithm adapted from a polynomial
	 * approximation in: Ibbetson D, Algorithm 209 Collected Algorithms of the
	 * CACM 1963 p. 616
	 * 
	 * Note: This routine has six digit accuracy, so it is only useful for
	 * absolute z values < 6. For z values >= to 6.0, poz() returns 0.0.
	 */
	/** Returns cumulative probability from -oo to z */
	public static double probabilityOf(double z) {
		double y, x, w;
		if (z == 0.0)
			x = 0.0;
		else {
			y = 0.5 * Math.abs(z);
			if (y >= (Z_MAX * 0.5))
				x = 1.0;
			else if (y < 1.0) {
				w = y * y;
				x = ((((((((0.000124818987 * w - 0.001075204047) * w + 0.005198775019)
						* w - 0.019198292004)
						* w + 0.059054035642)
						* w - 0.151968751364)
						* w + 0.319152932694)
						* w - 0.531923007300)
						* w + 0.797884560593)
						* y * 2.0;
			} else {
				y -= 2.0;
				x = (((((((((((((-0.000045255659 * y + 0.000152529290) * y - 0.000019538132)
						* y - 0.000676904986)
						* y + 0.001390604284)
						* y - 0.000794620820)
						* y - 0.002034254874)
						* y + 0.006549791214)
						* y - 0.010557625006)
						* y + 0.011630447319)
						* y - 0.009279453341)
						* y + 0.005353579108)
						* y - 0.002141268741)
						* y + 0.000535310849)
						* y + 0.999936657524;
			}
		}
		return (z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5));
	}

	/*
	 * Compute critical z value to produce given probability. Algorithm: Begin
	 * with upper and lower limits for z values (maxz and minz) set to extremes.
	 * Choose a z value (zval) between the extremes. Compute the probability of
	 * the z value. Set minz or maxz, based on whether the probability is less
	 * than or greater than the desired p. Continue adjusting the extremes until
	 * they are within Z_EPSILON of each other.
	 * 
	 * @param p critical probability level.
	 * 
	 * @return z such that abs(probabilityOf(p) - z) <= .000001
	 */
	public static double criticalValueFor(double p) {
		double minz = -Z_MAX; /* minimum of range of z */
		double maxz = Z_MAX; /* maximum of range of z */
		double zval = 0.0; /* computed/returned z value */
		double pval; /* prob (z) function, pval := poz (zval) */
		if (p <= 0.0 || p >= 1.0)
			return (0.0);
		while (maxz - minz > Z_EPSILON) {
			pval = probabilityOf(zval);
			if (pval > p)
				maxz = zval;
			else
				minz = zval;
			zval = (maxz + minz) * 0.5;
		}
		return (zval);
	}
}
