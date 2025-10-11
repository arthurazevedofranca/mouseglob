/*******************************************************************************
 * Copyright (c) 2016 Daniel Coelho de Castro.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Daniel Coelho de Castro - initial API and implementation
 ******************************************************************************/
package dcc.graphics.math;

import dcc.graphics.math.Matrix;
import dcc.graphics.math.Matrix.SingularMatrixException;
import dcc.graphics.math.Vector;

public class Kalman2D {

	Matrix F, Ft; // Transition matrix
	Matrix H, Ht; // Observation matrix

	Matrix Q; // Process noise covariance matrix
	Matrix R; // Observation noise covariance matrix

	Vector x, x1; // State vector (and a priori)
	Matrix P, P1; // State covariance matrix (and a priori)

	public Kalman2D(Matrix transition, Matrix observation, Matrix procNoiseCov,
			Matrix obsNoiseVar, Vector initialState, Matrix initialCov) {
		setF(transition);
		setH(observation);
		this.Q = procNoiseCov;
		this.R = obsNoiseVar;
		this.x = initialState;
		this.P = initialCov;
	}

	private Kalman2D() {
	}

	protected void setF(Matrix F) {
		this.F = F;
		this.Ft = F.transpose();
	}

	protected void setH(Matrix H) {
		this.H = H;
		this.Ht = H.transpose();
	}

	public Vector predict() {
		x1 = F.multiply(x);
		P1 = F.multiply(P.multiply(Ft)).add(Q);
		return x1;
	}

	public Vector correct(Vector z) {
		try {
			Vector y = z.subtract(H.multiply(x1));
			// Innovation covariance
			Matrix S = H.multiply(P1.multiply(Ht)).add(R);
			// Kalman gain
			Matrix K = P1.multiply(H).multiply(S.inverse());
			x = x1.add(K.multiply(y));
			P = Matrix.IDENTITY.subtract(K.multiply(H)).multiply(P1);
		} catch (SingularMatrixException e) {
			e.printStackTrace();
		}

		return x;
	}

	public static class KalmanVelocity extends Kalman2D {

		private double accVar;

		public KalmanVelocity(double accVar, double obsVar, double dt, Vector x0) {
			this.accVar = accVar;
			setTimeStep(dt);
			setH(new Matrix(1, 0, 0, 0));
			R = Matrix.diagonal(obsVar, obsVar);
			x = x0;
			P = Q;
		}

		public void setTimeStep(double dt) {
			setF(new Matrix(1, dt, 0, 1));
			Vector G = new Vector(dt * dt / 2, dt);
			Q = G.outer(G).multiply(accVar);
		}

		public double correct(double z) {
			return super.correct(new Vector(z, 0)).x;
		}
	}
}
