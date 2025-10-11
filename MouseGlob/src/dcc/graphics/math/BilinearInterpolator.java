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

public class BilinearInterpolator {

	private static class Coefficients {

		final int i1, i2, j1, j2;
		final double w11, w12, w21, w22;

		private Coefficients(int i1, int i2, int j1, int j2, double w11,
				double w12, double w21, double w22) {
			this.i1 = i1;
			this.i2 = i2;
			this.j1 = j1;
			this.j2 = j2;
			this.w11 = w11;
			this.w12 = w12;
			this.w21 = w21;
			this.w22 = w22;
		}

	}

	private final double[][] v;
	private final int width;
	private final int height;

	public BilinearInterpolator(ScalarMap map) {
		v = map.v;
		width = map.width;
		height = map.height;
	}

	public double get(double x, double y) {
		Coefficients c = calculateCoefficients(x, y);
		double value = 0;
		value += c.w11 * v[c.i1][c.j1];
		value += c.w12 * v[c.i1][c.j2];
		value += c.w21 * v[c.i2][c.j1];
		value += c.w22 * v[c.i2][c.j2];
		return value;
	}

	public void increment(double x, double y, double value) {
		Coefficients c = calculateCoefficients(x, y);
		v[c.i1][c.j1] += c.w11 * value;
		v[c.i1][c.j2] += c.w12 * value;
		v[c.i2][c.j1] += c.w21 * value;
		v[c.i2][c.j2] += c.w22 * value;
	}

	private Coefficients calculateCoefficients(double x, double y) {
		int i1 = getI1(x);
		int i2 = getI2(x);
		int j1 = getJ1(y);
		int j2 = getJ2(y);

		double w11 = (i2 - x) * (j2 - y);
		double w12 = (i2 - x) * (y - j1);
		double w21 = (x - i1) * (j2 - y);
		double w22 = (x - i1) * (y - j1);

		return new Coefficients(i1, i2, j1, j2, w11, w12, w21, w22);
	}

	private int getI1(double x) {
		return constrain((int) Math.floor(x), 0, width - 2);
	}

	private int getI2(double x) {
		return getI1(x) + 1;
	}

	private int getJ1(double y) {
		return constrain((int) Math.floor(y), 0, height - 2);
	}

	private int getJ2(double y) {
		return getJ1(y) + 1;
	}

	private static int constrain(int x, int min, int max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}

}
