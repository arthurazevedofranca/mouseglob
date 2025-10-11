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

import dcc.graphics.math.async.Operation2D;

public class Optimizer {

	private final double[][] v;
	private final int width;
	private final int height;

	public Optimizer(ScalarMap map) {
		v = map.v;
		width = map.width;
		height = map.height;
	}

	public Optimizer(double[][] values) {
		v = values;
		width = values.length;
		height = values[0].length;
	}

	public double getMaximumValue() {
		class MaxValue extends Operation2D {
			double max = -Double.MAX_VALUE;

			protected MaxValue() {
				super(width, height);
			}

			@Override
			protected void compute(int i, int j) {
				if (v[i][j] > max)
					max = v[i][j];
			}
		}
		MaxValue maxValue = new MaxValue();
		maxValue.execute();
		return maxValue.max;
	}

	public double getMinimumValue() {
		class MinValue extends Operation2D {
			double min = Double.MAX_VALUE;

			protected MinValue() {
				super(width, height);
			}

			@Override
			protected void compute(int i, int j) {
				if (v[i][j] < min)
					min = v[i][j];
			}
		}
		MinValue minValue = new MinValue();
		minValue.execute();
		return minValue.min;
	}

	public List<Vector> getLocalMaxima() {
		final List<Vector> maxima = new ArrayList<>();
		new Operation2D(1, width - 1, 1, height - 1) {
			@Override
			protected void compute(int i, int j) {
				if (isLocalMaximum(v, i, j))
					maxima.add(new Vector(i, j));
			}
		}.execute();
		return maxima;
	}

	public Vector[] getLocalMaxima(final int n) {
		final Vector[] maxima = new Vector[n];
		final double[] values = new double[n];
		new Operation2D(1, width - 1, 1, height - 1) {
			@Override
			protected void compute(int i, int j) {
				if (isLocalMaximum(v, i, j)) {
					for (int k = 0; k < n; k++) {
						if (v[i][j] > values[k]) {
							for (int l = k + 1; l < n; l++) {
								values[l] = values[l - 1];
								maxima[l] = maxima[l - 1];
							}
							values[k] = v[i][j];
							maxima[k] = new Vector(i, j);
							break;
						}
					}
				}
			}
		}.execute();
		return maxima;
	}

	public List<Vector> getLocalMinima() {
		final List<Vector> minima = new ArrayList<>();
		new Operation2D(1, width - 1, 1, height - 1) {
			@Override
			protected void compute(int i, int j) {
				if (isLocalMinimum(v, i, j))
					minima.add(new Vector(i, j));
			}
		}.execute();
		return minima;
	}

	public Vector getMaximum() {
		class Maximum extends Operation2D {
			double max = -Double.MAX_VALUE;
			int maxI = 0, maxJ = 0;

			protected Maximum() {
				super(width, height);
			}

			@Override
			protected void compute(int i, int j) {
				if (v[i][j] > max) {
					max = v[i][j];
					maxI = i;
					maxJ = j;
				}
			}
		}
		Maximum max = new Maximum();
		max.execute();
		return new Vector(max.maxI, max.maxJ);
	}

	public Vector getMinimum() {
		class Minimum extends Operation2D {
			double min = Double.MAX_VALUE;
			int minI = 0, minJ = 0;

			protected Minimum() {
				super(width, height);
			}

			@Override
			protected void compute(int i, int j) {
				if (v[i][j] < min) {
					min = v[i][j];
					minI = i;
					minJ = j;
				}
			}
		}
		Minimum min = new Minimum();
		min.execute();
		return new Vector(min.minI, min.minJ);
	}

	protected static boolean isLocalMaximum(double[][] v, int i, int j) {
		for (double n : getNeighbors(v, i, j))
			if (v[i][j] <= n)
				return false;
		return true;
	}

	protected static boolean isLocalMinimum(double[][] v, int i, int j) {
		for (double n : getNeighbors(v, i, j))
			if (v[i][j] >= n)
				return false;
		return true;
	}

	private static double[] getNeighbors(double[][] v, int i, int j) {
		double[] neighbors = new double[8];
		neighbors[0] = v[i - 1][j - 1];
		neighbors[1] = v[i][j - 1];
		neighbors[2] = v[i + 1][j - 1];
		neighbors[3] = v[i - 1][j];
		neighbors[4] = v[i + 1][j];
		neighbors[5] = v[i - 1][j + 1];
		neighbors[6] = v[i][j + 1];
		neighbors[7] = v[i + 1][j + 1];
		return neighbors;
	}

}
