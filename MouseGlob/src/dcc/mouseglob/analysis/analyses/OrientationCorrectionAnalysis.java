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
package dcc.mouseglob.analysis.analyses;

import static java.lang.Math.PI;

import java.util.Random;

import dcc.graphics.binary.BinarySeries;
import dcc.graphics.math.CoordinateSystem;
import dcc.graphics.math.Vector;
import dcc.graphics.series.Series1D;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;

@AnalysisInfo("Orientation")
public class OrientationCorrectionAnalysis implements Analysis {

	private static final double W_MAX = 0.25;
	private static final double LAMBDA = 0.005; // 0.112;

	@Inject
	private MomentsAnalysis moments;
	@Inject
	private VelocityAnalysis velocity;
	// TODO Use displacement to make orientation more robust
	// @Inject
	// private DisplacementAnalysis displacement;
	private CoordinateSystem coordinateSystem;
	private boolean isOriented = false;

	private Series1D phi;
	private Series1D theta;
	private int T = 0;

	private BinarySeries[] p;
	private Series1D[] C;

	public OrientationCorrectionAnalysis() {
		p = new BinarySeries[2];
		C = new Series1D[2];
		for (int i = 0; i < 2; i++) {
			p[i] = new BinarySeries();
			C[i] = new Series1D();
		}
		phi = new Series1D();
		theta = new Series1D();
	}

	private static final int BOOL[] = new int[] { 0, 1 };

	// Viterbi-like orientation correction
	// Branson et al. (2009)
	public void update2() {
		if (T == 0) {
			C[0].add(0);
			C[1].add(0);
			p[0].add(false);
			p[1].add(true);
			phi.add(0);
			theta.add(moments.getAngle());
		} else {
			coordinateSystem = moments.getCoordinateSystem();

			Vector v = velocity.get(-1);
			double w = Math.min(W_MAX, LAMBDA * v.dot(v));
			phi.add(v.angle());
			theta.add(moments.getAngle());

			double phi_t = phi.get(-1);

			double theta_t = theta.get(-1);
			double theta_t1 = theta.get(-2);

			double[] J = new double[2];
			for (int s_t : BOOL) {
				for (int s_t1 : BOOL) {
					double J1 = J1(w, theta_t, phi_t, s_t);
					double J2 = J2(w, theta_t, theta_t1, s_t, s_t1);
					J[s_t1] = C[s_t1].get(T - 1) + J1 + J2;
				}
				C[s_t].add(Math.min(J[0], J[1]));
				p[s_t].add(J[0] > J[1]);
			}
			if (C[0].get(T) > C[1].get(T))
				coordinateSystem = coordinateSystem.rotate180();
			isOriented = true;
		}
		T++;
	}

	// Branson et al. (2009)
	@Override
	public void update() {
		if (T == 0) {
			phi.add(0);
			theta.add(moments.getAngle());
		} else {
			Vector v = velocity.get(-1);
			phi.add(v.angle());
			theta.add(moments.getAngle());
			coordinateSystem = moments.getCoordinateSystem();

			C[0].clear();
			C[1].clear();
			p[0].clear();
			p[1].clear();
			C[0].add(0);
			C[1].add(0);
			p[0].add(false);
			p[1].add(true);
			for (int t = 1; t <= T; t++) {
				v = velocity.get(t);
				double w = Math.min(W_MAX, LAMBDA * v.dot(v));

				double phi_t = phi.get(t);

				double theta_t = theta.get(t);
				double theta_t1 = theta.get(t - 1);

				double[] J = new double[2];
				for (int s_t : BOOL) {
					for (int s_t1 : BOOL) {
						double J1 = J1(w, theta_t, phi_t, s_t);
						double J2 = J2(w, theta_t, theta_t1, s_t, s_t1);
						J[s_t1] = C[s_t1].get(t - 1) + J1 + J2;
					}
					C[s_t].add(Math.min(J[0], J[1]));
					p[s_t].add(J[0] > J[1]);
				}
			}
			if (C[0].get(T) > C[1].get(T))
				coordinateSystem = coordinateSystem.rotate180();
			isOriented = true;
		}
		T++;
	}

	private double J1(double w, double theta, double phi, int s) {
		return w * Math.abs(wrap(theta + s * PI - phi));
	}

	private double J2(double w, double theta, double theta1, int s, int s1) {
		return (1 - w) * Math.abs(wrap(theta + s * PI - theta1 - s1 * PI));
	}

	private static double wrap(double a) {
		a = (a + PI) % (2 * PI);
		return a <= 0 ? a + PI : a - PI;
	}

	private static double halfWrap(double a) {
		a = (a + PI / 2) % PI;
		return a <= 0 ? a + PI / 2 : a - PI / 2;
	}

	public static void main(String[] args) {
		class MockMoments extends MomentsAnalysis {
			Random random = new Random();
			double angle = 0;

			@Override
			public double getAngle() {
				double da = random.nextGaussian() * PI / 4;
				angle = halfWrap(angle + da);
				return angle;
			}

			@Override
			public CoordinateSystem getCoordinateSystem() {
				return new CoordinateSystem(Vector.ZERO, Vector.I, Vector.J);
			}
		}
		OrientationCorrectionAnalysis orientation = new OrientationCorrectionAnalysis();
		orientation.moments = new MockMoments();
		orientation.velocity = new VelocityAnalysis();
		Random random = new Random();
		for (int t = 0; t < 100; t++) {
			Vector v = new Vector(random.nextGaussian(), random.nextGaussian())
					.multiply(1);
			orientation.velocity.add(v);
			orientation.update();
		}
	}

	boolean isOriented() {
		return isOriented;
	}

	Vector getMajorAxis() {
		return coordinateSystem.getE1();
	}

	Vector getMinorAxis() {
		return coordinateSystem.getE2();
	}

	CoordinateSystem getCoordinateSystem() {
		return coordinateSystem;
	}

}
