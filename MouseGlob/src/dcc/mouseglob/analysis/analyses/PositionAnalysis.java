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

import dcc.graphics.math.Kalman2D;
import dcc.graphics.math.Kalman2D.KalmanVelocity;
import dcc.graphics.math.Matrix;
import dcc.graphics.math.Vector;
import dcc.graphics.plot.oned.Axis;
import dcc.graphics.series.Series2D;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.calibration.Calibration;

@AnalysisInfo("Position")
public class PositionAnalysis extends Series2D implements Analysis {

	@Inject
	private MomentsAnalysis moments;
	@Inject
	private Calibration calibration;
	@Inject
	private Time time;
	private Vector corner = Vector.ZERO;
	private double width, height;

	private KalmanVelocity kalmanX;
	private KalmanVelocity kalmanY;

	private Kalman2D kalman;

	public void setCornerPosition(int left, int top) {
		corner = calibration.pxToCm(new Vector(left, top));
	}

	public void setBounds(double width, double height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void update() {
		add(corner.add(moments.getMean()));
	}

	// Using 2D Kalman filter
	public void update1() {
		Vector mean = moments.getMean();
		if (kalman == null) {
			double dynaS = 1; // Dynamic process noise variance
			double measS = 1; // Measurement noise variance
			Matrix I = Matrix.IDENTITY;
			kalman = new Kalman2D(I, I, I.multiply(dynaS * dynaS),
					I.multiply(measS * measS), mean, I.multiply(dynaS * dynaS));
		}
		kalman.predict();
		Vector p = kalman.correct(mean);
		add(corner.add(p));
	}

	// Using 2 1D Kalman filters
	public void update2() {
		double dt = time.size() > 1 ? time.diff(-1) : 1. / 24;
		Vector mean = moments.getMean();
		if (kalmanX == null || kalmanY == null) {
			double dynaS = 10, measS = 1;
			kalmanX = new KalmanVelocity(dynaS * dynaS, measS * measS, dt,
					new Vector(mean.x, 0));
			kalmanY = new KalmanVelocity(dynaS * dynaS, measS * measS, dt,
					new Vector(mean.y, 0));
		}
		kalmanX.setTimeStep(dt);
		kalmanY.setTimeStep(dt);
		kalmanX.predict();
		kalmanY.predict();
		double x = kalmanX.correct(mean.x);
		double y = kalmanY.correct(mean.y);
		add(corner.add(new Vector(x, y)));
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public Series2D getPxTrajectory() {
		return scale(1 / calibration.getScale());
	}

	public Vector getPx(int i) {
		return get(i).multiply(1 / calibration.getScale());
	}

	public Axis getXAxis() {
		return getAxis("X (cm)").setMax(width * calibration.getScale());
	}

	public Axis getYAxis() {
		return getAxis("Y (cm)").setMax(height * calibration.getScale())
				.setInvert(true);
	}

	private static Axis getAxis(String label) {
		return new Axis(0, 0, 10).setFormat("%d").setLabel(label);
	}

}
