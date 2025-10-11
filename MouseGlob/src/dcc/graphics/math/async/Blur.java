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
package dcc.graphics.math.async;

import dcc.graphics.image.FilterFactory;
import dcc.graphics.math.ScalarMap;

public class Blur {

	private ScalarMap buffer;
	private ScalarMap result;
	private double[] filter;

	public Blur(double sigma) {
		setSigma(sigma);
	}

	public void setSigma(double sigma) {
		filter = FilterFactory.gaussian(sigma);
	}

	public ScalarMap calculate(ScalarMap map, boolean async) {
		int width = map.getWidth(), height = map.getHeight();
		if (buffer == null || !map.dimensionsMatch(buffer))
			buffer = new ScalarMap(width, height);
		if (result == null || !map.dimensionsMatch(result))
			result = new ScalarMap(width, height);

		Operation2D horz = new FilterHorizontal(map.getValues(),
				buffer.getValues(), filter);
		Operation2D vert = new FilterVertical(buffer.getValues(),
				result.getValues(), filter);
		horz.execute(async);
		vert.execute(async);

		return result;
	}
}
