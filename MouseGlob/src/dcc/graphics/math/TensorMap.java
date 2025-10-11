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

import dcc.graphics.math.Matrix.IndeterminateSystemException;
import dcc.graphics.math.async.Operation2D;

public class TensorMap extends DefaultMap<Matrix> {

	protected ScalarMap m11;
	protected ScalarMap m12;
	protected ScalarMap m21;
	protected ScalarMap m22;
	protected int width;
	protected int height;

	public TensorMap(ScalarMap m11, ScalarMap m12, ScalarMap m21, ScalarMap m22) {
		checkSize(m11, m12);
		checkSize(m11, m21);
		checkSize(m11, m22);

		this.m11 = m11;
		this.m12 = m12;
		this.m21 = m21;
		this.m22 = m22;

		width = m11.width;
		height = m11.height;
	}

	public final ScalarMap determinant() {
		final ScalarMap det = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				det.v[i][j] = get(i, j).determinant();
			}
		}.execute();
		return det;
	}

	public final ScalarMap trace() {
		final ScalarMap trace = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				trace.v[i][j] = get(i, j).trace();
			}
		}.execute();
		return trace;
	}

	public final VectorMap solve(final VectorMap b) {
		final ScalarMap x = new ScalarMap(width, height);
		final ScalarMap y = new ScalarMap(width, height);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				try {
					Vector u = get(i, j).solve(b.get(i, j));
					x.v[i][j] = u.x;
					y.v[i][j] = u.y;
				} catch (IndeterminateSystemException e) {
					x.v[i][j] = 0;
					y.v[i][j] = 0;
				}
			}
		}.execute();
		return new VectorMap(x, y);
	}

	public TensorMap blur(float sigma) {
		return new TensorMap(m11.blur(sigma), m12.blur(sigma), m21.blur(sigma),
				m22.blur(sigma));
	}

	private static void checkSize(ScalarMap a, ScalarMap b) {
		if (a.width != b.width || a.height != b.height)
			throw new IllegalArgumentException(
					"Components should have the same size");
	}

	@Override
	public Matrix get(int i, int j) {
		return new Matrix(m11.v[i][j], m12.v[i][j], m21.v[i][j], m22.v[i][j]);
	}

	@Override
	public Map<Matrix> get(int x, int y, int w, int h) {
		ScalarMap n11 = m11.get(x, y, w, h);
		ScalarMap n12 = m12.get(x, y, w, h);
		ScalarMap n21 = m21.get(x, y, w, h);
		ScalarMap n22 = m22.get(x, y, w, h);
		return new TensorMap(n11, n12, n21, n22);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void release() {
		width = height = 0;
		m11.release();
		m12.release();
		m21.release();
		m22.release();
	}

	class Symmetric extends TensorMap {

		Symmetric(ScalarMap m11, ScalarMap m12, ScalarMap m22) {
			super(m11, m12, m12, m22);
		}

		@Override
		public TensorMap blur(float sigma) {
			return new Symmetric(m11.blur(sigma), m12.blur(sigma),
					m22.blur(sigma));
		}

	}

}
