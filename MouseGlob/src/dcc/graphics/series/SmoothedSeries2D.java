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
package dcc.graphics.series;

import dcc.graphics.image.FilterFactory;
import dcc.graphics.math.Vector;
import dcc.graphics.math.async.Operation1D;

public class SmoothedSeries2D extends Series2D implements SmoothedSeries {

	private final Series2D series;
	private double sigma;
	private boolean isSmoothed;

	public SmoothedSeries2D(Series2D series) {
		this.series = series;
		series.addSeriesListener(this);
		series.addBoundsListener(this);
		isSmoothed = false;
		unsmooth();
	}

	@Override
	public void smooth(double sigma) {
		isSmoothed = true;
		this.sigma = sigma;
		synchronized (this) {
			final int n = size();
			final double[] filteredX = new double[n];
			final double[] filteredY = new double[n];
			final double[] filter = FilterFactory.gaussian(sigma);
			final int r = (filter.length - 1) / 2;
			new Operation1D(n) {
				@Override
				protected void compute(int i) {
					double valueX = 0, valueY = 0;
					for (int k = -r; k <= r; k++) {
						int index = Math.min(Math.max(i + k, 0), n - 1);
						Vector point = series.get(index);
						valueX += filter[k + r] * point.x;
						valueY += filter[k + r] * point.y;
					}
					filteredX[i] = valueX;
					filteredY[i] = valueY;
				}
			}.execute();
			clear();
			add(filteredX, filteredY);
		}
	}

	@Override
	public void unsmooth() {
		isSmoothed = false;
		synchronized (points) {
			clear();
			add(series);
		}
	}

	@Override
	public void onSeriesChanged(Series series) {
		if (isSmoothed) {
			smooth(sigma);
		} else {
			unsmooth();
		}
		notifySeriesChanged();
	}

	@Override
	public void onBoundsChanged(Series series) {
		notifyBoundsChanged();
	}

	public void dispose() {
		series.removeSeriesListener(this);
		series.removeBoundsListener(this);
		clearBoundsListeners();
		clearSeriesListeners();
	}

}
