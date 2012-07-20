package de.pxlab.util;

/*********************************************************************/
/* 	f u n c t i o n     p r a x i s                              */
/*                                                                   */
/* praxis is a general purpose routine for the minimization of a     */
/* function in several variables. the algorithm used is a modifi-    */
/* cation of conjugate gradient search method by powell. the changes */
/* are due to r.p. brent, who gives an algol-w program, which served */
/* as a basis for this function.                                     */
/*                                                                   */
/* references:                                                       */
/*     - powell, m.j.d., 1964. an efficient method for finding       */
/*       the minimum of a function in several variables without      */
/*       calculating derivatives, CompResultsr journal, 7, 155-162       */
/*     - brent, r.p., 1973. algorithms for minimization without      */
/*       derivatives, prentice hall, englewood cliffs.               */
/*                                                                   */
/*     problems, suggestions or improvements are always wellcome     */
/*                       karl gegenfurtner   07/08/87                */
/*                                           c - version             */
/* Modified to satisfy the ANSI standard: Sept. 88 (H. Irtel)	     */
/*********************************************************************/
/*                                                                   */
/* usage: min = praxis(fun, x, n);                                   */
/*                                                                   */
/*  fun        the function to be minimized. fun is called from      */
/*             praxis with x and n as arguments                      */
/*  x          a double array containing the initial guesses for     */
/*             the minimum, which will contain the solution on       */
/*             return                                                */
/*  n          an integer specifying the number of unknown           */
/*             parameters                                            */
/*  min        praxis returns the least calculated value of fun      */
/*                                                                   */
/* some additional global variables control some more aspects of     */
/* the inner workings of praxis. setting them is optional, they      */
/* are all set to some reasonable default values given below.        */
/*                                                                   */
/*   prin      controls the printed output from the routine.         */
/*             0 -> no output                                        */
/*             1 -> print only starting and final values             */
/*             2 -> detailed map of the minimization process         */
/*             3 -> print also eigenvalues and vectors of the        */
/*                  search directions                                */
/*             the default value is 1                                */
/*  tol        is the tolerance allowed for the precision of the     */
/*             solution. praxis returns if the criterion             */
/*             2 * ||x[k]-x[k-1]|| <= Math.sqrt(macheps) * ||x[k]|| + tol */
/*             is fulfilled more than ktm times.                     */
/*             the default value depends on the machine precision    */
/*  ktm        see just above. default is 1, and a value of 4 leads  */
/*             to a very(!) cautious stopping criterion.             */
/*  step       is a steplength parameter and should be set equal     */
/*             to the expected distance from the solution.           */
/*             exceptionally small or large values of step lead to   */
/*             slower convergence on the first few iterations        */
/*             the default value for step is 1.0                     */
/*  scbd       is a scaling parameter. 1.0 is the default and        */
/*             indicates no scaling. if the scales for the different */
/*             parameters are very different, scbd should be set to  */
/*             a value of about 10.0.                                */
/*  illc       should be set to true (1) if the problem is known to  */
/*             be ill-conditioned. the default is false (0). this    */
/*             variable is automatically set, when praxis finds      */
/*             the problem to be ill-conditioned during iterations.  */
/*  maxfun     is the maximum number of calls to fun allowed. praxis */
/*             will return after maxfun calls to fun even when the   */
/*             minimum is not yet found. the default value of 0      */
/*             indicates no limit on the number of calls.            */
/*             this return condition is only checked every n         */
/*             iterations.                                           */
/*                                                                   */
/*********************************************************************/
public class Praxis {
	private static final int NPRAXIS = 50;
	private static final double EPSILON = 1.0e-10;
	private static final double SQREPSILON = 1.0e-20;
	/* control parameters */
	private double tol = SQREPSILON, scbd = 1.0, step = 1.0;
	private int ktm = 1, prin = 0, maxfun = 0;
	private boolean illc = false;
	/* some global variables */
	private int i, j, k, k2, nl, nf, kl, kt;
	private double s, sl, dn, dmin, fx, f1, lds, ldt, sf, df, qf1, qd0, qd1,
			qa, qb, qc, m2, m4, small, vsmall, large, vlarge, ldfac, t2;
	private double[] d = new double[NPRAXIS], y = new double[NPRAXIS],
			z = new double[NPRAXIS], q0 = new double[NPRAXIS],
			q1 = new double[NPRAXIS], tflin = new double[NPRAXIS];
	private double[][] v = new double[NPRAXIS][NPRAXIS];
	/* these will be set by praxis to point to it's arguments */
	private int n;
	private double[] x;
	private PraxisFunction fun;
	/* these will be set by praxis to the global control parameters */
	private double h, macheps, t;
	/* Reference arguments of min() */
	private double[] min_d2 = new double[1];
	private double[] min_x1 = new double[1];
	private java.util.Random random = new java.util.Random();

	private void sort() /* d and v in descending order */
	{
		int k, i, j;
		double s;
		for (i = 0; i < n - 1; i++) {
			k = i;
			s = d[i];
			for (j = i + 1; j < n; j++) {
				if (d[j] > s) {
					k = j;
					s = d[j];
				}
			}
			if (k > i) {
				d[k] = d[i];
				d[i] = s;
				for (j = 0; j < n; j++) {
					s = v[j][i];
					v[j][i] = v[j][k];
					v[j][k] = s;
				}
			}
		}
	}

	private void printf(String s) {
		System.out.print(s);
	}

	private void print() /* print a line of traces */
	{
		printf("\n");
		printf("... chi square reduced to ... " + fx + "\n");
		printf("... after " + nf + " function calls ...\n");
		printf("... including " + nl + " linear searches ...\n");
		vecprint("... current values of x ...", x, n);
	}

	private void matprint(String s, double v[][], int n) {
		int k, i;
		printf(s + "\n");
		for (k = 0; k < n; k++) {
			for (i = 0; i < n; i++) {
				printf(v[k][i] + " ");
			}
			printf("\n");
		}
	}

	private void vecprint(String s, double x[], int n) {
		int i;
		printf(s + "\n");
		for (i = 0; i < n; i++)
			printf(x[i] + " ");
		printf("\n");
	}

	private double flin(double l, int j) {
		int i;
		// double[] tflin = new double[NPRAXIS];
		if (j != -1) { /* linear search */
			for (i = 0; i < n; i++)
				tflin[i] = x[i] + l * v[i][j];
		} else { /* search along parabolic space curve */
			qa = l * (l - qd1) / (qd0 * (qd0 + qd1));
			qb = (l + qd0) * (qd1 - l) / (qd0 * qd1);
			qc = l * (l + qd0) / (qd1 * (qd0 + qd1));
			for (i = 0; i < n; i++)
				tflin[i] = qa * q0[i] + qb * x[i] + qc * q1[i];
		}
		nf++;
		return fun.valueOf(tflin);
	}

	private void min(int j, int nits, double[] ref_d2, double[] ref_x1,
			double f1, boolean fk) {
		int k, i;
		boolean dz;
		double x2, xm, f0, f2, fm, d1, t2, s, sf1, sx1;
		double d2 = ref_d2[0], x1 = ref_x1[0];
		sf1 = f1;
		sx1 = x1;
		k = 0;
		xm = 0.0;
		fm = f0 = fx;
		dz = d2 < macheps;
		/* find step size */
		s = 0.0;
		for (i = 0; i < n; i++)
			s += x[i] * x[i];
		s = Math.sqrt(s);
		if (dz)
			t2 = m4 * Math.sqrt(Math.abs(fx) / dmin + s * ldt) + m2 * ldt;
		else
			t2 = m4 * Math.sqrt(Math.abs(fx) / (d2) + s * ldt) + m2 * ldt;
		s = s * m4 + t;
		if (dz && t2 > s)
			t2 = s;
		if (t2 < small)
			t2 = small;
		if (t2 > 0.01 * h)
			t2 = 0.01 * h;
		if (fk && (f1 <= fm)) {
			xm = x1;
			fm = f1;
		}
		if (!fk || (Math.abs(x1) < t2)) {
			x1 = (x1 > 0.0 ? t2 : -t2);
			f1 = flin(x1, j);
		}
		if (f1 <= fm) {
			xm = x1;
			fm = f1;
		}
		// next_step:
		boolean jc1 = false, jc2 = false;
		do {
			if (dz) {
				x2 = (f0 < f1 ? -(x1) : 2 * (x1));
				f2 = flin(x2, j);
				if (f2 <= fm) {
					xm = x2;
					fm = f2;
				}
				d2 = (x2 * (f1 - f0) - (x1) * (f2 - f0))
						/ ((x1) * x2 * ((x1) - x2));
			}
			d1 = (f1 - f0) / (x1) - x1 * d2;
			dz = true;
			if (d2 <= small) {
				x2 = (d1 < 0.0 ? h : -h);
			} else {
				x2 = -0.5 * d1 / (d2);
			}
			if (Math.abs(x2) > h)
				x2 = (x2 > 0.0 ? h : -h);
			// try_this:
			do {
				f2 = flin(x2, j);
				jc1 = (k < nits) && (f2 > f0);
				if (jc1)
					k++;
				jc2 = (f0 < f1) && (x1 * x2 > 0.0);
				if (jc1 && !jc2) {
					x2 *= 0.5;
				}
			} while (jc1 && !jc2);
		} while (jc1 && jc2);
		nl++;
		if (f2 > fm)
			x2 = xm;
		else
			fm = f2;
		if (Math.abs(x2 * (x2 - x1)) > small) {
			d2 = (x2 * (f1 - f0) - x1 * (fm - f0)) / (x1 * x2 * (x1 - x2));
		} else {
			if (k > 0)
				d2 = 0.0;
		}
		if (d2 <= small)
			d2 = small;
		x1 = x2;
		fx = fm;
		if (sf1 < fx) {
			fx = sf1;
			x1 = sx1;
		}
		if (j != -1)
			for (i = 0; i < n; i++)
				x[i] += (x1) * v[i][j];
		ref_d2[0] = d2;
		ref_x1[0] = x1;
	}

	private void quad() /* look for a minimum along the curve q0, q1, q2 */
	{
		int i;
		double l, s;
		s = fx;
		fx = qf1;
		qf1 = s;
		qd1 = 0.0;
		for (i = 0; i < n; i++) {
			s = x[i];
			l = q1[i];
			x[i] = l;
			q1[i] = s;
			qd1 = qd1 + (s - l) * (s - l);
		}
		s = 0.0;
		qd1 = Math.sqrt(qd1);
		l = qd1;
		if (qd0 > 0.0 && qd1 > 0.0 && nl >= 3 * n * n) {
			min_d2[0] = s;
			min_x1[0] = l;
			// min(-1, 2, &s, &l, qf1, 1);
			min(-1, 2, min_d2, min_x1, qf1, true);
			s = min_d2[0];
			l = min_x1[0];
			qa = l * (l - qd1) / (qd0 * (qd0 + qd1));
			qb = (l + qd0) * (qd1 - l) / (qd0 * qd1);
			qc = l * (l + qd0) / (qd1 * (qd0 + qd1));
		} else {
			fx = qf1;
			qa = qb = 0.0;
			qc = 1.0;
		}
		qd0 = qd1;
		for (i = 0; i < n; i++) {
			s = q0[i];
			q0[i] = x[i];
			x[i] = qa * s + qb * x[i] + qc * q1[i];
		}
	}

	public double minimize(PraxisFunction _fun, double[] _x) {
		n = _x.length;
		d = new double[n];
		y = new double[n];
		z = new double[n];
		q0 = new double[n];
		q1 = new double[n];
		tflin = new double[n];
		v = new double[n][n];
		/* init global extern variables and parameters */
		macheps = EPSILON;
		h = step;
		t = tol;
		x = _x;
		fun = _fun;
		small = macheps * macheps;
		vsmall = small * small;
		large = 1.0 / small;
		vlarge = 1.0 / vsmall;
		m2 = Math.sqrt(macheps);
		m4 = Math.sqrt(m2);
		ldfac = (illc ? 0.1 : 0.01);
		nl = kt = 0;
		nf = 1;
		fx = fun.valueOf(x);
		qf1 = fx;
		t2 = small + Math.abs(t);
		t = t2;
		dmin = small;
		boolean jc1 = false, jc2 = false, jc3 = false;
		if (h < 100.0 * t)
			h = 100.0 * t;
		ldt = h;
		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				v[i][j] = (i == j ? 1.0 : 0.0);
		d[0] = 0.0;
		qd0 = 0.0;
		for (i = 0; i < n; i++)
			q1[i] = x[i];
		if (prin > 1) {
			printf("\n------------- enter function praxis -----------\n");
			printf("... current parameter settings ...\n");
			printf("... scaling ... " + scbd + "\n");
			printf("...   tol   ... " + t + "\n");
			printf("... maxstep ... " + h + "\n");
			printf("...   illc  ... " + illc + "\n");
			printf("...   ktm   ... " + ktm + "\n");
			printf("... maxfun  ... " + maxfun + "\n");
		}
		if (prin > 0)
			print();
		mloop: while (true) {
			sf = d[0];
			s = d[0] = 0.0;
			/* minimize along first direction */
			// min(0, 2, &d[0], &s, fx, 0);
			min_x1[0] = s;
			min(0, 2, d, min_x1, fx, false);
			s = min_x1[0];
			if (s <= 0.0)
				for (i = 0; i < n; i++)
					v[i][0] = -v[i][0];
			if ((sf <= (0.9 * d[0])) || ((0.9 * sf) >= d[0]))
				for (i = 1; i < n; i++)
					d[i] = 0.0;
			for (k = 1; k < n; k++) {
				for (i = 0; i < n; i++)
					y[i] = x[i];
				sf = fx;
				illc = illc || (kt > 0);
				// next_step:
				do {
					kl = k;
					df = 0.0;
					if (illc) { /* random step to get off resolution valley */
						for (i = 0; i < n; i++) {
							z[i] = (0.1 * ldt + t2
									* Math.pow(10.0, (double) kt))
									* (random.nextDouble() - 0.5);
							s = z[i];
							for (j = 0; j < n; j++)
								x[j] += s * v[j][i];
						}
						fx = fun.valueOf(x);
						nf++;
					}
					/* minimize along non-conjugate directions */
					for (k2 = k; k2 < n; k2++) {
						sl = fx;
						s = 0.0;
						min_d2[0] = d[k2];
						min_x1[0] = s;
						// min(k2, 2, &d[k2], &s, fx, 0);
						min(k2, 2, min_d2, min_x1, fx, false);
						d[k2] = min_d2[0];
						s = min_x1[0];
						if (illc) {
							double szk = s + z[k2];
							s = d[k2] * szk * szk;
						} else
							s = sl - fx;
						if (df < s) {
							df = s;
							kl = k2;
						}
					}
					jc1 = !illc && (df < Math.abs(100.0 * macheps * fx));
					if (jc1)
						illc = true;
				} while (jc1);
				if ((k == 1) && (prin > 1))
					vecprint("\n... New Direction ...", d, n);
				/* minimize along conjugate directions */
				for (k2 = 0; k2 <= k - 1; k2++) {
					s = 0.0;
					min_d2[0] = d[k2];
					min_x1[0] = s;
					// min(k2, 2, &d[k2], &s, fx, 0);
					min(k2, 2, min_d2, min_x1, fx, false);
					d[k2] = min_d2[0];
					s = min_x1[0];
				}
				f1 = fx;
				fx = sf;
				lds = 0.0;
				for (i = 0; i < n; i++) {
					sl = x[i];
					x[i] = y[i];
					y[i] = sl - y[i];
					sl = y[i];
					lds = lds + sl * sl;
				}
				lds = Math.sqrt(lds);
				if (lds > small) {
					for (i = kl - 1; i >= k; i--) {
						for (j = 0; j < n; j++)
							v[j][i + 1] = v[j][i];
						d[i + 1] = d[i];
					}
					d[k] = 0.0;
					for (i = 0; i < n; i++)
						v[i][k] = y[i] / lds;
					min_d2[0] = d[k];
					min_x1[0] = lds;
					// min(k, 4, &d[k], &lds, f1, 1);
					min(k, 4, min_d2, min_x1, f1, true);
					d[k] = min_d2[0];
					lds = min_x1[0];
					if (lds <= 0.0) {
						lds = -lds;
						for (i = 0; i < n; i++)
							v[i][k] = -v[i][k];
					}
				}
				ldt = ldfac * ldt;
				if (ldt < lds)
					ldt = lds;
				if (prin > 1)
					print();
				t2 = 0.0;
				for (i = 0; i < n; i++)
					t2 += x[i] * x[i];
				t2 = m2 * Math.sqrt(t2) + t;
				if (ldt > (0.5 * t2))
					kt = 0;
				else
					kt++;
				if (kt > ktm)
					break mloop;
				if ((maxfun > 0) && (nf > maxfun)) {
					if (prin > 0)
						printf("\n... maximum number of function calls reached ...\n");
					break mloop;
				}
			}
			/* try quadratic extrapolation in case */
			/* we are stuck in a curved valley */
			quad();
			dn = 0.0;
			for (i = 0; i < n; i++) {
				d[i] = 1.0 / Math.sqrt(d[i]);
				if (dn < d[i])
					dn = d[i];
			}
			if (prin > 2)
				matprint("\n... New Matrix of Directions ...", v, n);
			for (j = 0; j < n; j++) {
				s = d[j] / dn;
				for (i = 0; i < n; i++)
					v[i][j] *= s;
			}
			if (scbd > 1.0) { /* scale axis to reduce condition number */
				s = vlarge;
				for (i = 0; i < n; i++) {
					sl = 0.0;
					for (j = 0; j < n; j++)
						sl += v[i][j] * v[i][j];
					z[i] = Math.sqrt(sl);
					if (z[i] < m4)
						z[i] = m4;
					if (s > z[i])
						s = z[i];
				}
				for (i = 0; i < n; i++) {
					sl = s / z[i];
					z[i] = 1.0 / sl;
					if (z[i] > scbd) {
						sl = 1.0 / scbd;
						z[i] = scbd;
					}
				}
			}
			for (i = 1; i < n; i++)
				for (j = 0; j <= i - 1; j++) {
					s = v[i][j];
					v[i][j] = v[j][i];
					v[j][i] = s;
				}
			minfit(n, macheps, vsmall, v, d);
			if (scbd > 1.0) {
				for (i = 0; i < n; i++) {
					s = z[i];
					for (j = 0; j < n; j++)
						v[i][j] *= s;
				}
				for (i = 0; i < n; i++) {
					s = 0.0;
					for (j = 0; j < n; j++)
						s += v[j][i] * v[j][i];
					s = Math.sqrt(s);
					d[i] *= s;
					s = 1.0 / s;
					for (j = 0; j < n; j++)
						v[j][i] *= s;
				}
			}
			for (i = 0; i < n; i++) {
				if ((dn * d[i]) > large)
					d[i] = vsmall;
				else if ((dn * d[i]) < small)
					d[i] = vlarge;
				else
					d[i] = Math.pow(dn * d[i], -2.0);
			}
			sort(); /* the new eigenvalues and eigenvectors */
			dmin = d[n - 1];
			if (dmin < small)
				dmin = small;
			illc = (m2 * d[0]) > dmin;
			if ((prin > 2) && (scbd > 1.0))
				vecprint("\n... Scale Factors ...", z, n);
			if (prin > 2)
				vecprint("\n... Eigenvalues of A ...", d, n);
			if (prin > 2)
				matprint("\n... Eigenvectors of A ...", v, n);
		} // end of main loop
		if (prin > 0) {
			vecprint("\n... Final solution is ...", x, n);
			printf("\n... ChiSq reduced to " + fx + " ...\n");
			printf("... after " + nf + " function calls.\n");
		}
		return (fx);
	}

	// static int minfit_count = 0;
	/* singular value decomposition */
	private void minfit(int n, double eps, double tol, double ab[][],
			double q[]) {
		int l = 0, kt, l2, i, j, k;
		double c, f, g, h, s, x, y, z;
		double[] e = new double[n];
		// minfit_count++;
		// System.out.println(minfit_count + "th call of minfit()");
		/* householder's reduction to bidiagonal form */
		x = g = 0.0;
		for (i = 0; i < n; i++) {
			e[i] = g;
			s = 0.0;
			l = i + 1;
			for (j = i; j < n; j++)
				s += ab[j][i] * ab[j][i];
			if (s < tol) {
				g = 0.0;
			} else {
				f = ab[i][i];
				if (f < 0.0)
					g = Math.sqrt(s);
				else
					g = -Math.sqrt(s);
				h = f * g - s;
				ab[i][i] = f - g;
				for (j = l; j < n; j++) {
					f = 0.0;
					for (k = i; k < n; k++)
						f += ab[k][i] * ab[k][j];
					f /= h;
					for (k = i; k < n; k++)
						ab[k][j] += f * ab[k][i];
				}
			}
			q[i] = g;
			s = 0.0;
			if (i < n)
				for (j = l; j < n; j++)
					s += ab[i][j] * ab[i][j];
			if (s < tol) {
				g = 0.0;
			} else {
				f = ab[i][i + 1];
				if (f < 0.0)
					g = Math.sqrt(s);
				else
					g = -Math.sqrt(s);
				h = f * g - s;
				ab[i][i + 1] = f - g;
				for (j = l; j < n; j++)
					e[j] = ab[i][j] / h;
				for (j = l; j < n; j++) {
					s = 0.0;
					for (k = l; k < n; k++)
						s += ab[j][k] * ab[i][k];
					for (k = l; k < n; k++)
						ab[j][k] += s * e[k];
				}
			}
			y = Math.abs(q[i]) + Math.abs(e[i]);
			if (y > x)
				x = y;
		}
		/* accumulation of right hand transformations */
		for (i = n - 1; i >= 0; i--) {
			if (g != 0.0) {
				h = ab[i][i + 1] * g;
				for (j = l; j < n; j++)
					ab[j][i] = ab[i][j] / h;
				for (j = l; j < n; j++) {
					s = 0.0;
					for (k = l; k < n; k++)
						s += ab[i][k] * ab[k][j];
					for (k = l; k < n; k++)
						ab[k][j] += s * ab[k][i];
				}
			}
			for (j = l; j < n; j++)
				ab[i][j] = ab[j][i] = 0.0;
			ab[i][i] = 1.0;
			g = e[i];
			l = i;
		}
		/* diagonalization to bidiagonal form */
		eps *= x;
		for (k = n - 1; k >= 0; k--) {
			kt = 0;
			// TestFsplitting:
			while (true) {
				if (++kt > 30) {
					e[k] = 0.0;
					printf("\n+++ qr failed\n");
				}
				boolean jc1 = false;
				boolean jc2 = false;
				for (l2 = k; l2 >= 0; l2--) {
					l = l2;
					jc1 = (Math.abs(e[l]) <= eps);
					if (jc1) {
						// goto TestFconvergence;
						jc2 = true;
						break;
					}
					if (!jc1 && (Math.abs(q[l - 1]) <= eps))
						break; /* goto Cancellation; */
				}
				if (!jc2) {
					/* Cancellation: */
					c = 0.0;
					s = 1.0;
					for (i = l; i <= k; i++) {
						f = s * e[i];
						e[i] *= c;
						if (Math.abs(f) <= eps)
							// goto TestFconvergence;
							break;
						g = q[i];
						if (Math.abs(f) < Math.abs(g)) {
							double fg = f / g;
							h = Math.abs(g) * Math.sqrt(1.0 + fg * fg);
						} else {
							double gf = g / f;
							h = (f != 0.0 ? Math.abs(f)
									* Math.sqrt(1.0 + gf * gf) : 0.0);
						}
						q[i] = h;
						if (h == 0.0) {
							h = 1.0;
							g = 1.0;
						}
						c = g / h;
						s = -f / h;
					}
				}
				// TestFconvergence:
				z = q[k];
				if (l == k)
					break; // goto Convergence;
				/* shift from bottom 2x2 minor */
				x = q[l];
				y = q[k - l];
				g = e[k - 1];
				h = e[k];
				f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2.0 * h * y);
				g = Math.sqrt(f * f + 1.0);
				if (f <= 0.0)
					f = ((x - z) * (x + z) + h * (y / (f - g) - h)) / x;
				else
					f = ((x - z) * (x + z) + h * (y / (f + g) - h)) / x;
				/* next qr transformation */
				s = c = 1.0;
				for (i = l + 1; i <= k; i++) {
					g = e[i];
					y = q[i];
					h = s * g;
					g *= c;
					if (Math.abs(f) < Math.abs(h)) {
						double fh = f / h;
						z = Math.abs(h) * Math.sqrt(1.0 + fh * fh);
					} else {
						double hf = h / f;
						z = (f != 0.0 ? Math.abs(f) * Math.sqrt(1.0 + hf * hf)
								: 0.0);
					}
					e[i - 1] = z;
					if (z == 0.0)
						f = z = 1.0;
					c = f / z;
					s = h / z;
					f = x * c + g * s;
					g = -x * s + g * c;
					h = y * s;
					y *= c;
					for (j = 0; j < n; j++) {
						x = ab[j][i - 1];
						z = ab[j][i];
						ab[j][i - 1] = x * c + z * s;
						ab[j][i] = -x * s + z * c;
					}
					if (Math.abs(f) < Math.abs(h)) {
						double fh = f / h;
						z = Math.abs(h) * Math.sqrt(1.0 + fh * fh);
					} else {
						double hf = h / f;
						z = (f != 0.0 ? Math.abs(f) * Math.sqrt(1.0 + hf * hf)
								: 0.0);
					}
					q[i - 1] = z;
					if (z == 0.0)
						z = f = 1.0;
					c = f / z;
					s = h / z;
					f = c * g + s * y;
					x = -s * g + c * y;
				}
				e[l] = 0.0;
				e[k] = f;
				q[k] = x;
			} // goto TestFsplitting;
				// Convergence:
			if (z < 0.0) {
				q[k] = -z;
				for (j = 0; j < n; j++)
					ab[j][k] = -ab[j][k];
			}
		}
	}

	public double minimize(PraxisFunction1 fun, double[] funPar, double a,
			double b) {
		double t, eps;
		double c, d, e, m, p, q, r, tol, t2, u, v, w, x, fu, fv, fw, fx;
		eps = EPSILON;
		t = SQREPSILON;
		c = 0.381966;
		x = a + c * (b - a);
		w = v = x;
		e = 0.0;
		fx = fun.valueOf(x);
		fw = fv = fx;
		m = 0.5 * (a + b);
		tol = eps * Math.abs(x) + t;
		t2 = 2.0 * tol;
		while (Math.abs(x - m) > t2 - 0.5 * (b - a)) {
			r = q = p = 0.0;
			if (Math.abs(e) > tol) {
				r = (x - w) * (fx - fv);
				q = (x - v) * (fx - fw);
				p = (x - v) * q - (x - w) * r;
				q = 2.0 * (q - r);
				if (q > 0.0)
					p = -p;
				else
					q = -q;
			}
			if ((Math.abs(p) < Math.abs(0.5 * q * r)) && (p < (q * (a - x)))
					&& (p < (q * (b - x)))) {
				d = p / q;
				u = x + d;
				if (((u - a) < t2) || ((b - u) < t2)) {
					d = (x < m ? tol : -tol);
				}
			} else {
				e = (x < m ? b - x : a - x);
				d = c * e;
			}
			if (Math.abs(d) >= tol)
				u = x + d;
			else
				u = (d > 0.0 ? x + tol : x - tol);
			fu = fun.valueOf(u);
			if (fu <= fx) {
				if (u < x)
					b = x;
				else
					a = x;
				v = w;
				fv = fw;
				w = x;
				fw = fx;
				x = u;
				fx = fu;
			} else {
				if (u < x)
					a = u;
				else
					b = u;
				if ((fu <= fw) || (w == x)) {
					v = w;
					fv = fw;
					w = u;
					fw = fu;
				} else {
					if ((fu <= fv) || (v == w) || (v == x)) {
						v = u;
						fv = fu;
					}
				}
			}
			m = 0.5 * (a + b);
			tol = eps * Math.abs(x) + t;
			t2 = 2.0 * tol;
		}
		funPar[0] = x;
		return fx;
	}

	public void setPrintControl(int p) {
		prin = ((p >= 0) && (p < 4)) ? p : 0;
	}

	public int getPrintControl() {
		return prin;
	}

	public void setTolerance(double t) {
		tol = t;
	}

	public double getTolerance() {
		return tol;
	}

	public void setCautiousness(int p) {
		ktm = ((p > 0) && (p < 5)) ? p : 0;
	}

	public int getCautiousness() {
		return ktm;
	}

	public void setInitialStepSize(double s) {
		step = s;
	}

	public double getInitialStepSize() {
		return step;
	}

	public void setScaling(double s) {
		scbd = s;
	}

	public double getScaling() {
		return scbd;
	}

	public void setIllConditioned(boolean c) {
		illc = c;
	}

	public boolean getIllConditioned() {
		return illc;
	}

	public void setMaximumNumberOfFunctionCalls(int p) {
		maxfun = p;
	}

	public int getMaximumNumberOfFunctionCalls() {
		return maxfun;
	}
}
