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

import java.util.ArrayList;
import java.util.List;

public final class LinearScan {

	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	private final int n;
	private final double[] values;

	public LinearScan(ScalarMap map, double x1, double y1, double x2,
			double y2, int nSamples) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.n = nSamples;

		values = linearScan(map, x1, y1, x2, y2, nSamples);
	}

	public LinearScan(ScalarMap map, Vector p1, Vector p2, int nSamples) {
		this(map, p1.x, p1.y, p2.x, p2.y, nSamples);
	}

	public double[] getValues() {
		return values.clone();
	}

	public double getAverage() {
		double sum = 0;
		for (int i = 0; i < values.length; i++)
			sum += values[i];
		return sum / values.length;
	}

	public Vector getMean() {
		return getPoint(getMeanIndex(values));
	}

	public Vector getMaximum() {
		return getPoint(getMaximumIndex(values));
	}

	public List<Vector> getLocalMaxima() {
		List<Vector> maxima = new ArrayList<>();
		for (int i : getLocalMaxima(values)) {
			maxima.add(getPoint(i));
		}
		return maxima;
	}

	private Vector getPoint(double index) {
		double k = index / (n - 1);
		return new Vector(x1 + k * (x2 - x1), y1 + k * (y2 - y1));
	}

	private static double getMeanIndex(double[] v) {
		double sum = 0;
		double s = 0;
		for (int i = 0; i < v.length; i++) {
			sum += i * v[i];
			s += v[i];
		}
		if (s == 0)
			return (double) v.length / 2;
		return sum / s;
	}

	private static int getMaximumIndex(double[] v) {
		double max = v[0];
		int m = 0;
		for (int i = 0; i < v.length; i++)
			if (v[i] > max) {
				max = v[i];
				m = i;
			}
		return m;
	}

	private static List<Integer> getLocalMaxima(double[] values) {
		double mean = 0;
		for (double f : values)
			mean += f;
		mean /= values.length;
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < values.length; i++) {
			if (isLocalMaximum(values, mean, i))
				indexes.add(i);
		}
		return indexes;
	}

	private static boolean isLocalMaximum(double[] values, double mean, int i) {
		double threshold = 0.5;
		boolean isLeft = true;
		boolean isRight = true;
		if (i != 0)
			isLeft = values[i] > values[i - 1];
		if (i != values.length - 1)
			isRight = values[i] > values[i + 1];
		return isLeft && isRight && values[i] > threshold * mean;
	}

	private static double[] linearScan(ScalarMap map, double x1, double y1,
			double x2, double y2, int n) {
		double[] scan = new double[n];
		BilinearInterpolator interpolator = new BilinearInterpolator(map);
		for (int i = 0; i < n; i++) {
			double k = (double) i / (n - 1);
			scan[i] = interpolator.get(x1 + k * (x2 - x1), y1 + k * (y2 - y1));
		}
		return scan;
	}

}
