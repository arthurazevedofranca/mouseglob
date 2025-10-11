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

import dcc.graphics.math.async.Operation2D;

public class VectorMap extends DefaultMap<Vector> {

	protected ScalarMap x, y;
	protected int width, height;

	public VectorMap(int width, int height) {
		this.width = width;
		this.height = height;
		x = new ScalarMap(width, height);
		y = new ScalarMap(width, height);
	}

	public VectorMap(ScalarMap x, ScalarMap y) {
		checkSize(x, y);

		this.x = x;
		this.y = y;
		width = x.width;
		height = x.height;
	}

	protected void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		x.setSize(width, height);
		y.setSize(width, height);
	}

	public void reset() {
		x.reset();
		y.reset();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Vector get(int i, int j) {
		return new Vector(x.v[i][j], y.v[i][j]);
	}

	@Override
	public VectorMap get(int x, int y, int w, int h) {
		return new VectorMap(this.x.get(x, y, w, h), this.y.get(x, y, w, h));
	}

	public ScalarMap getX() {
		return x;
	}

	public ScalarMap getY() {
		return y;
	}

	public ScalarMap getMagnitude() {
		if (width == 0 || height == 0)
			return null;

		final ScalarMap mag = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				mag.v[i][j] = get(i, j).magnitude();
			}
		}.execute();
		return mag;
	}

	public ScalarMap getAngle() {
		if (width == 0 || height == 0)
			return null;

		final ScalarMap angle = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				angle.v[i][j] = get(i, j).angle();
			}
		}.execute();
		return angle;
	}

	public Vector getMean() {
		return new Vector(x.getMean(), y.getMean());
	}

	public ScalarMap dot(final VectorMap v) {
		checkSize(this, v);
		final ScalarMap prod = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				prod.v[i][j] = get(i, j).dot(v.get(i, j));
			}
		}.execute();
		return prod;
	}

	public VectorMap reflect() {
		final VectorMap reflected = new VectorMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				reflected.x.v[i][j] = -x.v[i][j];
				reflected.y.v[i][j] = -y.v[i][j];
			}
		}.execute();
		return reflected;
	}

	public void release() {
		width = height = 0;
		x.release();
		y.release();
	}

	private static void checkSize(DefaultMap<?> a, DefaultMap<?> b) {
		if (!a.dimensionsMatch(b))
			throw new IllegalArgumentException(
					"Components should have the same size");
	}

}
