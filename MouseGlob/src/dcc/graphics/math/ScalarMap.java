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

import java.util.Arrays;

import dcc.graphics.Box;
import dcc.graphics.image.FilterFactory;
import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.math.async.FilterHorizontal;
import dcc.graphics.math.async.FilterVertical;
import dcc.graphics.math.async.Operation1D;
import dcc.graphics.math.async.Operation2D;
import dcc.graphics.pool.DoubleMatrixPool;

public class ScalarMap extends DefaultMap<Double> {

	protected double[][] v;
	protected int width;
	protected int height;

	public ScalarMap(int width, int height) {
		setSize(width, height);
	}

	public ScalarMap(GrayscaleImage image) {
		set(image);
	}

	public void setSize(int width, int height) {
		if (this.width == width && this.height == height)
			return;
		this.width = width;
		this.height = height;
		DoubleMatrixPool.release(v);
		v = DoubleMatrixPool.get(width, height);
	}

	public void reset() {
		new Operation1D(width) {
			@Override
			protected void compute(int i) {
				Arrays.fill(v[i], 0);
			}
		}.execute();
	}

	public void set(final GrayscaleImage image) {
		setSize(image.getWidth(), image.getHeight());
		new Operation2D(width, height) {
			@Override
			protected void compute(int x, int y) {
				v[x][y] = image.value(x, y);
			}
		}.execute();
	}

	public void copy(final ScalarMap source) {
		if (!dimensionsMatch(source))
			setSize(source.width, source.height);
		new Operation1D(width) {
			@Override
			protected void compute(int i) {
				System.arraycopy(source.v[i], 0, v[i], 0, height);
			}
		}.execute();
	}

	protected void set(int i, int j, double value) {
		v[i][j] = value;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public double[][] getValues() {
		return v;
	}

	@Override
	public Double get(int i, int j) {
		return v[i][j];
	}

	@Override
	public ScalarMap get(int x, int y, int w, int h) {
		ScalarMap capture = new ScalarMap(w, h);
		w = Math.min(w, width - x);
		h = Math.min(h, height - y);
		for (int i = 0; i < w; i++)
			System.arraycopy(v[x + i], y, capture.v[i], 0, h);

		return capture;
	}

	public double get(Vector p) {
		return v[(int) p.x][(int) p.y];
	}

	public ScalarMap add(final ScalarMap map) {
		if (!dimensionsMatch(map))
			return null;

		final ScalarMap add = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int x, int y) {
				add.v[x][y] = v[x][y] + map.v[x][y];
			}
		}.execute();

		return add;
	}

	public ScalarMap add(final double a) {
		final ScalarMap add = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int x, int y) {
				add.v[x][y] = v[x][y] + a;
			}
		}.execute();

		return add;
	}

	public ScalarMap subtract(final ScalarMap map) {
		if (!dimensionsMatch(map))
			return null;

		final ScalarMap sub = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int x, int y) {
				sub.v[x][y] = v[x][y] - map.v[x][y];
			}
		}.execute();

		return sub;
	}

	public ScalarMap difference(final ScalarMap map) {
		if (!dimensionsMatch(map))
			return null;

		final ScalarMap diff = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int x, int y) {
				diff.v[x][y] = Math.abs(v[x][y] - map.v[x][y]);
			}
		}.execute();

		return diff;
	}

	public ScalarMap multiply(final ScalarMap map) {
		if (!dimensionsMatch(map))
			return null;
		final ScalarMap mult = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int x, int y) {
				mult.v[x][y] = v[x][y] * map.v[x][y];
			}
		}.execute();
		return mult;
	}

	public ScalarMap multiply(final double a) {
		final ScalarMap mult = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int x, int y) {
				mult.v[x][y] = v[x][y] * a;
			}
		}.execute();
		return mult;
	}

	public ScalarMap blur(double sigma) {
		double[][] temp = DoubleMatrixPool.get(width, height);
		ScalarMap g = new ScalarMap(width, height);
		double[] filter = FilterFactory.gaussian(sigma);

		new FilterHorizontal(v, temp, filter).execute();
		new FilterVertical(temp, g.v, filter).execute();

		DoubleMatrixPool.release(temp);

		return g;
	}

	public double getSum() {
		class Summation extends Operation2D {
			private double sum = 0;

			private Summation() {
				super(width, height);
			}

			@Override
			protected void compute(int x, int y) {
				if (!Double.isNaN(v[x][y]))
					sum += v[x][y];
			}
		}

		Summation summation = new Summation();
		summation.execute();

		return summation.sum;
	}

	public double getSum(Box box) {
		Box mapBounds = Box.fromSize(width, height);
		final Box.Int bounds = mapBounds.clamp(box).toInt();
		class Summation extends Operation2D {
			private double sum = 0;

			private Summation() {
				super(bounds.left, bounds.right, bounds.top, bounds.bottom);
			}

			@Override
			protected void compute(int x, int y) {
				if (!Double.isNaN(v[x][y]))
					sum += v[x][y];
			}
		}

		Summation summation = new Summation();
		summation.execute();

		return summation.sum;
	}

	public double getMean() {
		return getSum() / (width * height);
	}

	public synchronized void release() {
		width = 0;
		height = 0;
		DoubleMatrixPool.release(v);
		v = null;
	}

}
