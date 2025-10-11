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

import java.util.Arrays;

public class Quantiles {

	private final double[] data;
	private final int n;

	public Quantiles(double[] data) {
		Arrays.sort(data);
		this.data = data;
		n = data.length;
	}

	public double getMinimum() {
		return data[0];
	}

	public double getLowerQuartile() {
		return getQuantile(0.25);
	}

	public double getMedian() {
		return getQuantile(0.5);
	}

	public double getUpperQuartile() {
		return getQuantile(0.75);
	}

	public double getMaximum() {
		return data[n - 1];
	}

	public double getInterquartileRange() {
		return getUpperQuartile() - getLowerQuartile();
	}

	public double getQuantile(double p) {
		double h = (n - 1) * p;
		if (h <= 0)
			return data[0];
		if (h >= n - 1)
			return data[n - 1];
		double hf = Math.floor(h);
		int hfi = (int) hf;
		return data[hfi] + (h - hf) * (data[hfi + 1] - data[hfi]);
	}

}
