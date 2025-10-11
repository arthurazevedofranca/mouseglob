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

import dcc.graphics.math.Matrix;
import dcc.graphics.math.ScalarMap;
import dcc.graphics.math.TensorMap;
import dcc.graphics.math.async.Operation2D;

public class CornerDetector extends ScalarMap {

	private final Gradient gradient;

	public CornerDetector() {
		super(0, 0);
		gradient = new Gradient();
	}

	public void calculate(ScalarMap map, double sigma) {
		if (!dimensionsMatch(map))
			setSize(map.getWidth(), map.getHeight());

		gradient.calculate(map);

		ScalarMap Ix = gradient.getX();
		ScalarMap Iy = gradient.getY();

		ScalarMap gIx2 = Ix.multiply(Ix).blur(sigma);
		ScalarMap gIxIy = Ix.multiply(Iy).blur(sigma);
		ScalarMap gIy2 = Iy.multiply(Iy).blur(sigma);

		// Structure tensor:
		// [gIx2 gIxIy]
		// [gIxIy gIy2]
		final TensorMap structure = new TensorMap(gIx2, gIxIy, gIxIy, gIy2);
		new Operation2D(width, height) {
			@Override
			protected void compute(int i, int j) {
				v[i][j] = noble(structure.get(i, j));
			}
		}.execute();

		gIx2.release();
		gIy2.release();
		gIxIy.release();
		structure.release();
	}

	private static double noble(Matrix matrix) {
		double det = matrix.determinant();
		double trace = matrix.trace();

		// Noble's corner measure
		return 2 * det / (trace + 1);
	}

	@SuppressWarnings("unused")
	private static double harris(Matrix matrix, double sensitivity) {
		double det = matrix.determinant();
		double trace = matrix.trace();

		// Harris' corner measure
		// sensitivity is usually between 0.05 and 0.14
		return det - sensitivity * trace * trace;
	}

}
