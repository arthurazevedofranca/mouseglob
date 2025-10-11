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
package dcc.util;

import dcc.graphics.math.Gaussian;

public class AveragingStopwatch extends Stopwatch {

	private int frameCount = 0;
	private long[] times;
	private int windowSize;
	private double[] weights;

	public AveragingStopwatch(int windowSize) {
		this.windowSize = windowSize;
		times = new long[windowSize];
		weights = new double[windowSize];
		float sum = 0;
		for (int i = 0; i < windowSize; i++) {
			weights[i] = Gaussian.g(i - windowSize + 1, windowSize / 3f);
			sum += weights[i];
		}
		for (int i = 0; i < windowSize; i++)
			weights[i] /= sum;
	}

	@Override
	public long toc() {
		times[frameCount++ % windowSize] = super.toc();
		double sum = 0;
		for (int i = 0; i < windowSize; i++)
			sum += weights[i] * times[(frameCount + i) % windowSize];
		return (long) sum;
	}

}
