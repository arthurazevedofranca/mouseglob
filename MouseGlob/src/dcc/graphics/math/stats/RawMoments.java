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
package dcc.graphics.math.stats;

import java.util.Collection;

import dcc.graphics.math.Map;
import dcc.graphics.math.Vector;

class RawMoments {
	/** 0th order raw moment */
	double M00;
	/** 1st order raw moment */
	double M10, M01;
	/** 2nd order raw moment */
	double M20, M11, M02;
	/** 3rd order raw moment */
	double M30, M21, M12, M03;

	private void reset() {
		M00 = M10 = M01 = M20 = M11 = M02 = 0;
		M30 = M21 = M12 = M03 = 0;
	}

	public void calculate(Map<Double> map) {
		calculate(map, true);
	}

	public void calculate(Map<Double> map, boolean thirdOrder) {
		reset();
		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				double value = map.get(i, j);
				M00 += value;
				M10 += value * i;
				M01 += value * j;
				M20 += value * i * i;
				M11 += value * i * j;
				M02 += value * j * j;
				if (thirdOrder) {
					M30 += value * i * i * i;
					M21 += value * i * i * j;
					M12 += value * i * j * j;
					M03 += value * j * j * j;
				}
			}
		}
	}

	public void calculate(Collection<Vector> samples, boolean thirdOrder) {
		reset();
		for (Vector v : samples) {
			M00++;
			M10 += v.x;
			M01 += v.y;
			M20 += v.x * v.x;
			M11 += v.x * v.y;
			M02 += v.y * v.y;
			if (thirdOrder) {
				M30 += v.x * v.x * v.x;
				M21 += v.x * v.x * v.y;
				M12 += v.x * v.y * v.y;
				M03 += v.y * v.y * v.y;
			}
		}
	}

	public final boolean isEmpty() {
		return M00 == 0;
	}

	public double get0th() {
		return M00;
	}

	public Vector getMean() {
		return new Vector(M10 / M00, M01 / M00);
	}

}
