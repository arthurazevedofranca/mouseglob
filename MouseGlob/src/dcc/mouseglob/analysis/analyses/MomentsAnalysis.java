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

import dcc.graphics.math.CoordinateSystem;
import dcc.graphics.math.Map;
import dcc.graphics.math.Matrix;
import dcc.graphics.math.Matrix.Eigendecomposition;
import dcc.graphics.math.Vector;
import dcc.graphics.math.stats.CentralMoments;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.Analysis.AnalysisInfo;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.tracking.Tracker;

@AnalysisInfo("Moments")
public class MomentsAnalysis extends CentralMoments implements Analysis {

	@Inject
	private Tracker tracker;
	@Inject
	private Calibration calibration;

	private Vector mean;
	private Vector majorAxis, minorAxis;
	private double angle;
	private double width, length;

	@Override
	public void update() {
		calculate(tracker.getMap());
	}

	@Override
	public void calculate(Map<Double> map) {
		super.calculate(map, true);

		double scale = calibration.getScale();

		if (isEmpty()) {
			double middleX = scale * (map.getWidth() / 2);
			double middleY = scale * (map.getHeight() / 2);
			mean = new Vector(middleX, middleY);
			return;
		}

		mean = super.getMean().multiply(scale);
		angle = getAngle();

		Matrix sigma = getCovarianceMatrix();
		Eigendecomposition evd = sigma.eigendecomposition();
		majorAxis = evd.e1;
		minorAxis = evd.e2;

		// An ellipse's axes' lengths are equal to 2 standard deviations
		length = 2.0 * Math.sqrt(evd.lambda1) * scale;
		width = 2.0 * Math.sqrt(evd.lambda2) * scale;
	}

	@Override
	public Vector getMean() {
		return mean;
	}

	public double getLength() {
		return length;
	}

	public double getWidth() {
		return width;
	}

	public Vector getMajorAxis() {
		return majorAxis;
	}

	public Vector getMinorAxis() {
		return minorAxis;
	}

	public CoordinateSystem getCoordinateSystem() {
		return new CoordinateSystem(mean, majorAxis.multiply(length),
				minorAxis.multiply(width));
	}

	@Override
	public String toString() {
		return mean + "; " + angle + " rad - " + width + " x " + length;
	}

}
