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
package dcc.graphics.image;

import java.util.Arrays;

import dcc.graphics.math.ScalarMap;
import dcc.graphics.math.VectorMap;
import dcc.graphics.math.async.FilterHorizontal;
import dcc.graphics.math.async.FilterVertical;
import dcc.graphics.math.async.Operation2D;
import dcc.graphics.pool.DoubleMatrixPool;

public class Gradient extends VectorMap {

	private double[][] buffer;

	public Gradient() {
		super(0, 0);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		DoubleMatrixPool.release(buffer);
		buffer = DoubleMatrixPool.get(width, height);
	}

	public void calculate(ScalarMap map) {
		if (!dimensionsMatch(map))
			setSize(map.getWidth(), map.getHeight());

		dx(map, x.getValues());
		dy(map, y.getValues());
	}

	public void calculate(ScalarMap map, double sigma) {
		if (!dimensionsMatch(map))
			setSize(map.getWidth(), map.getHeight());

		double[] g = FilterFactory.gaussian(sigma);
		double[] dg = FilterFactory.dGaussian(sigma);
		apply(map.getValues(), x.getValues(), buffer, dg, g);
		apply(map.getValues(), y.getValues(), buffer, g, dg);
	}

	private static void dx(final ScalarMap map, final double[][] dst) {
		Arrays.fill(dst[0], 0);
		Arrays.fill(dst[dst.length - 1], 0);

		// d(map)/dx
		new Operation2D(1, map.getWidth() - 1, 0, map.getHeight()) {
			@Override
			public void compute(int x, int y) {
				dst[x][y] = (map.get(x + 1, y) - map.get(x - 1, y)) / 2;
			}
		}.execute();
	}

	private static void dy(final ScalarMap map, final double[][] dst) {
		// d(map)/dy
		new Operation2D(0, map.getWidth(), 1, map.getHeight() - 1) {
			@Override
			public void compute(int x, int y) {
				dst[x][y] = (map.get(x, y + 1) - map.get(x, y - 1)) / 2;
			}
		}.execute();
	}

	private static void apply(double[][] src, double[][] dst,
			double[][] buffer, double[] horz, double[] vert) {
		new FilterHorizontal(src, buffer, horz).execute();
		new FilterVertical(buffer, dst, vert).execute();
	}

}
