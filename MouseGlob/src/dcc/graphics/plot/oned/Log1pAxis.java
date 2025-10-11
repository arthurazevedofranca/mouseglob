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
package dcc.graphics.plot.oned;

public class Log1pAxis extends Axis {

	public static Log1pAxis autoscalingMin(double max, double tickSpacing) {
		return new Log1pAxis(max, max, tickSpacing, true, false);
	}

	public static Log1pAxis autoscalingMax(double min, double tickSpacing) {
		return new Log1pAxis(min, min, tickSpacing, false, true);
	}

	public static Log1pAxis autoscaling(double tickSpacing) {
		return new Log1pAxis(0, 0, tickSpacing, true, true);
	}

	public Log1pAxis(double min, double max, double tickSpacing) {
		super(min, max, tickSpacing);
	}

	protected Log1pAxis(double min, double max, double tickSpacing,
			boolean autoMin, boolean autoMax) {
		super(min, max, tickSpacing, autoMin, autoMax);
	}

	@Override
	public float getRelativePosition(double value) {
		if (min == max)
			return 0.5f;
		double lmin = Math.log1p(min);
		double lmax = Math.log1p(max);
		double lvalue = Math.log1p(value);
		if (invert)
			return (float) ((lmax - lvalue) / (lmax - lmin));
		return (float) ((lvalue - lmin) / (lmax - lmin));
	}

	@Override
	public double getValue(double position) {
		double lmin = Math.log1p(min);
		double lmax = Math.log1p(max);
		double lposition = Math.log1p(position);
		if (invert)
			return lmax - lposition * (lmax - lmin);
		return lmin + lposition * (lmax - lmin);
	}

}
