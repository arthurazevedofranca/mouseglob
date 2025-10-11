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
package dcc.graphics.math;

import dcc.graphics.series.Series;
import dcc.graphics.series.Series2D;
import dcc.graphics.series.SeriesListener;

public class SeriesHeatMap extends HeatMap implements SeriesListener {

	private Series2D series;
	private int last = 1;

	public SeriesHeatMap(int width, int height, Series2D series, double sigma) {
		super(width, height, sigma);
		this.series = series;
		series.addSeriesListener(this);
		onSeriesChanged(series);
	}

	@Override
	public void onSeriesChanged(Series s) {
		int size = series.size();
		if (size <= last)
			return;
		Vector p1 = series.get(last - 1), p2;
		for (int i = last; i < size; i++) {
			p2 = p1;
			p1 = series.get(i);
			double n = p1.distance(p2) + 1;
			for (int j = 0; j < n; j++) {
				double k = j / n;
				Vector p = p1.lerp(p2, k);
				increment((int) p.x, (int) p.y, 1f / n);
			}
		}
		last = size;
	}
}
