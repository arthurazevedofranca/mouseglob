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
package dcc.graphics.math.stats;

import java.util.Collection;

import dcc.graphics.math.Map;
import dcc.graphics.math.Matrix;
import dcc.graphics.math.Vector;

public class CentralMoments extends RawMoments {

	/** 2nd order central moment */
	double mu20, mu11, mu02;
	/** 3rd order central moment */
	double mu30, mu21, mu12, mu03;

	@Override
	public void calculate(Map<Double> map, boolean thirdOrder) {
		super.calculate(map, thirdOrder);
		calculate(thirdOrder);
	}

	@Override
	public void calculate(Collection<Vector> samples, boolean thirdOrder) {
		super.calculate(samples, thirdOrder);
		calculate(thirdOrder);
	}

	private void calculate(boolean thirdOrder) {
		double x_ = M10 / M00;
		double y_ = M01 / M00;

		mu20 = M20 - x_ * M10;
		mu11 = M11 - x_ * M01;
		mu02 = M02 - y_ * M01;

		if (thirdOrder) {
			mu30 = M30 - 3 * x_ * M20 + 2 * x_ * x_ * M10;
			mu21 = M21 - 2 * x_ * M11 - y_ * M20 + 2 * x_ * x_ * M01;
			mu12 = M12 - 2 * y_ * M11 - x_ * M02 + 2 * y_ * y_ * M10;
			mu03 = M03 - 3 * y_ * M02 + 2 * y_ * y_ * M01;
		}
	}

	public double getAngle() {
		return 0.5 * Math.atan2(2 * mu11, mu20 - mu02);
	}

	public Matrix getCovarianceMatrix() {
		double varX = mu20 / M00;
		double varY = mu02 / M00;
		double covXY = mu11 / M00;
		return new Matrix(varX, covXY, covXY, varY);
	}

	public double getDirectionalVariance(Vector d) {
		double u = d.x, v = d.y;
		return (u * u * mu20 + 2 * u * v * mu11 + v * v * mu02) / M00;
	}

	public double getDirectionalSkewness(Vector d) {
		double u = d.x, v = d.y;
		double a = u * u * u * mu30 + 3 * u * u * v * mu21;
		double b = 3 * u * v * v * mu12 + v * v * v * mu03;
		double m3 = (a + b) / M00;
		double m2 = getDirectionalVariance(d);
		return m3 / Math.pow(m2, 1.5);
	}
}
