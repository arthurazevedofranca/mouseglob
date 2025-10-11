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

import java.util.Iterator;

import dcc.graphics.math.Vector;

public class CompoundSeries extends Series implements BoundsListener,
		SeriesListener {

	private Series1D xSeries, ySeries;

	public CompoundSeries(Series1D x, Series1D y) {
		this.xSeries = x;
		this.ySeries = y;

		xSeries.addBoundsListener(this);
		ySeries.addBoundsListener(this);

		xSeries.addSeriesListener(this);
		ySeries.addSeriesListener(this);

		onBoundsChanged(this);
	}

	@Override
	public boolean isEmpty() {
		return xSeries.isEmpty() || ySeries.isEmpty();
	}

	@Override
	public int size() {
		return Math.min(xSeries.size(), ySeries.size());
	}

	@Override
	public void onBoundsChanged(Series series) {
		notifyBoundsChanged();
	}

	@Override
	public void onSeriesChanged(Series series) {
		notifySeriesChanged();
	}

	@Override
	public Vector getPoint(int i) {
		return new Vector(xSeries.get(i), ySeries.get(i));
	}

	@Override
	public double getMinX() {
		return xSeries.getMinY();
	}

	@Override
	public double getMaxX() {
		return xSeries.getMaxY();
	}

	@Override
	public double getMinY() {
		return ySeries.getMinY();
	}

	@Override
	public double getMaxY() {
		return ySeries.getMaxY();
	}

	public void dispose() {
		xSeries.removeBoundsListener(this);
		ySeries.removeBoundsListener(this);
		xSeries.removeSeriesListener(this);
		ySeries.removeSeriesListener(this);
		clearBoundsListeners();
		clearSeriesListeners();
	}

	@Override
	public Iterator<Vector> iterator() {
		return new CompoundSeriesIterator();
	}

	private class CompoundSeriesIterator implements Iterator<Vector> {

		private int i;

		@Override
		public boolean hasNext() {
			return i < size();
		}

		@Override
		public Vector next() {
			double x = xSeries.get(i);
			double y = ySeries.get(i);
			i++;
			return new Vector(x, y);
		}

		@Override
		public void remove() {
			xSeries.remove(i);
			ySeries.remove(i);
		}

	}

}
