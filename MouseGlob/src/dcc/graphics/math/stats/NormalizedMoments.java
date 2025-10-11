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

public class NormalizedMoments extends CentralMoments {

	/** 2nd order normalized moment */
	double eta20, eta11, eta02;
	/** 3rd order normalized moment */
	double eta30, eta21, eta12, eta03;

	@Override
	public void calculate(Map<Double> map, boolean thirdOrder) {
		super.calculate(map, thirdOrder);
		calculate(thirdOrder);
	}

	@Override
	public void calculate(Collection<Vector> samples, boolean thirdOrder) {
		super.calculate(samples, thirdOrder);
		calculate(thirdOrder);
	}

	private void calculate(boolean thirdOrder) {
		double M002 = M00 * M00;
		eta20 = mu20 / M002;
		eta11 = mu11 / M002;
		eta02 = mu02 / M002;

		if (thirdOrder) {
			double M0025 = Math.pow(M00, 2.5);
			eta30 = mu30 / M0025;
			eta21 = mu21 / M0025;
			eta12 = mu12 / M0025;
			eta03 = mu03 / M0025;
		}
	}

}
