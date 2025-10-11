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
import dcc.graphics.math.async.Operation1D;

public class SmoothedSeries1D extends Series1D implements SmoothedSeries {

	private final Series1D series;
	private double sigma;
	private boolean isSmoothed;

	public SmoothedSeries1D(Series1D series) {
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
		final int n = size();
		final double[] filtered = new double[n];
		final double[] filter = FilterFactory.gaussian(sigma);
		final int r = (filter.length - 1) / 2;
		new Operation1D(n) {
			@Override
			protected void compute(int i) {
				double value = 0;
				for (int k = -r; k <= r; k++) {
					int index = Math.min(Math.max(i + k, 0), n - 1);
					value += filter[k + r] * series.get(index);
				}
				filtered[i] = value;
			}
		}.execute();
		synchronized (values) {
			clear();
			add(filtered);
		}
	}

	@Override
	public void unsmooth() {
		isSmoothed = false;
		synchronized (values) {
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
