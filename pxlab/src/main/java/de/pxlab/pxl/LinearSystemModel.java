package de.pxlab.pxl;

/**
 * A discrete and time invariant linear system model. For every point t in time
 * a discrete and time invariant linear system has a set of internal states
 * x(t), a set of input values u(t) and a set of output values y(t). There are
 * three matrices which are linear maps: The matrix a maps states x(t) at time t
 * into states x(t+1) at time t+1, the matrix b maps input values u(t) at time t
 * to states x(t+1) at time t+1, and the matrix c maps states x(t) at time t to
 * output values y(t) at time t. These are the equations used (with the
 * exception of t all variables are vectors or matrices):<br>
 * 
 * x(t+1) = a*x(t) + b*u(t) <br>
 * 
 * y(t) = c*x(t)
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 07/11/01
 */
public class LinearSystemModel {
	/**
	 * Compute the output vector from the current state. The mapping is y(t) =
	 * c*x(t).
	 * 
	 * @param xt
	 *            state vector at time t
	 * @param c
	 *            matrix which maps states to output vectors.
	 * @return the output vector at time t.
	 */
	public double[] output(double[] xt, double[] c) {
		int m = xt.length;
		int k = c.length / m;
		double[] yt = new double[k];
		for (int i = 0; i < k; i++) {
			yt[i] = 0.0;
			int row = i * m;
			for (int j = 0; j < m; j++) {
				yt[i] += c[row + j] * xt[j];
			}
		}
		return (yt);
	}

	/**
	 * Compute the next state vector from the current state vector and the
	 * current input vector using the respective state transition function and
	 * input mapping. The mapping is x(t+1) = a*x(t) + b*u(t).
	 * 
	 * @param xt
	 *            state vector at time t
	 * @param a
	 *            state transition matrix
	 * @param ut
	 *            input vector at time t
	 * @param b
	 *            matrix which maps input vectors to states
	 * @return the state vector at time t+1.
	 */
	public double[] nextState(double[] xt, double[] a, double[] ut, double[] b) {
		int m = xt.length;
		int n = b.length / m;
		if ((n != ut.length) || ((m * m) != a.length)) {
			new ParameterValueError(
					"LinearSystemModel: Size Error for Vectors or Matrices!");
			return xt;
		}
		double[] xt1 = new double[m];
		for (int i = 0; i < m; i++) {
			xt1[i] = 0.0;
			int a_row = i * m;
			int b_row = i * n;
			for (int j = 0; j < m; j++) {
				xt1[i] += a[a_row + j] * xt[j];
			}
			for (int j = 0; j < n; j++) {
				xt1[i] += b[b_row + j] * ut[j];
			}
		}
		return xt1;
	}
}
