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

import dcc.graphics.image.FilterFactory;

public class HeatMap extends ScalarMap {
	private BilinearInterpolator interpolator;
	private double[][] gaussian;
	private int radius;

	public HeatMap(int width, int height, double sigma) {
		super(width, height);
		reset();
		interpolator = new BilinearInterpolator(this);
		setSigma(sigma);
	}

	public void setSigma(double sigma) {
		gaussian = FilterFactory.gaussian2D(sigma);
		radius = (gaussian.length - 1) / 2;
	}

	public void increment(int x, int y) {
		increment(x, y, 1);
	}

	public void increment(int x, int y, double value) {
		for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++) {
				int tx = x + i, ty = y + j;
				if (tx >= 0 && tx < getWidth() && ty >= 0 && ty < getHeight()) {
					double g = gaussian[i + radius][j + radius];
					v[tx][ty] += value * g;
				}
			}
	}

	public void increment(double x, double y) {
		increment(x, y, 1);
	}

	public void increment(double x, double y, double value) {
		for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++) {
				double tx = x + i, ty = y + j;
				if (tx >= 0 && tx < getWidth() && ty >= 0 && ty < getHeight()) {
					double g = gaussian[i + radius][j + radius];
					interpolator.increment(tx, ty, value * g);
				}
			}
	}
}
