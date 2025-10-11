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

import dcc.graphics.math.ScalarMap;
import dcc.graphics.math.TensorMap;
import dcc.graphics.math.VectorMap;

public class MotionFlow extends VectorMap {

	private ScalarMap current, previous;
	private Gradient gradient;

	public MotionFlow() {
		super(0, 0);

		current = new ScalarMap(0, 0);
		previous = new ScalarMap(0, 0);
		gradient = new Gradient();
	}

	public void calculate(ScalarMap map, double sigma) {
		if (previous != null && previous.dimensionsMatch(map)) {
			if (!this.dimensionsMatch(map))
				setSize(map.getWidth(), map.getHeight());

			reset();
			current.copy(map);

			gradient.calculate(current);

			ScalarMap Ix = gradient.getX();
			ScalarMap Iy = gradient.getY();

			ScalarMap It = current.subtract(previous);

			ScalarMap gIx2 = Ix.multiply(Ix).blur(sigma);
			ScalarMap gIy2 = Iy.multiply(Iy).blur(sigma);
			ScalarMap gIxIy = Ix.multiply(Iy).blur(sigma);

			ScalarMap gIxIt = Ix.multiply(It).blur(sigma);
			ScalarMap gIyIt = Iy.multiply(It).blur(sigma);

			TensorMap structure = new TensorMap(gIx2, gIxIy, gIxIy, gIy2);

			// Solve the system:
			// [gIx2 gIxIy].[x,y]' = [gIxIt,gIyIt]'
			// [gIxIy gIy2]
			VectorMap flow = structure.solve(new VectorMap(gIxIt, gIyIt))
					.reflect();

			this.x = flow.getX();
			this.y = flow.getY();

			It.release();
			gIx2.release();
			gIy2.release();
			gIxIy.release();
			gIxIt.release();
			gIyIt.release();
			structure.release();
		}

		previous.copy(map);
	}

}
