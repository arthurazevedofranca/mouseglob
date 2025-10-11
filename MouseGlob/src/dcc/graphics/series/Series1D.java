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

import dcc.graphics.binary.BinaryMask1D;
import dcc.graphics.math.Vector;
import dcc.graphics.math.stats.Statistics;
import dcc.util.DoubleList;

public class Series1D extends Series {

	protected final DoubleList values;
	private double min, max;

	public Series1D() {
		values = new DoubleList(INITIAL_SIZE);
	}

	public Series1D(double[] values) {
		this.values = new DoubleList(values.length);
		add(values);
	}

	Series1D(DoubleList values) {
		this.values = new DoubleList(values.size());
		add(values);
	}

	public void add(double value) {
		updateBounds(value);
		values.add(value);
		notifyBoundsChanged();
		notifySeriesChanged();
	}

	public void add(double[] values) {
		for (double value : values) {
			updateBounds(value);
			this.values.add(value);
		}
		notifyBoundsChanged();
		notifySeriesChanged();
	}

	public void add(Iterable<Double> values) {
		for (double value : values) {
			updateBounds(value);
			this.values.add(value);
		}
		notifyBoundsChanged();
		notifySeriesChanged();
	}

	public void add(Series1D series) {
		add(series.values);
	}

	void add(DoubleList values) {
		int n = values.size();
		for (int i = 0; i < n; i++) {
			double value = values.get(i);
			updateBounds(value);
			this.values.add(value);
		}
		notifyBoundsChanged();
		notifySeriesChanged();
	}

	public void remove(int index) {
		values.remove(index);
		notifySeriesChanged();
	}

	/**
	 * Gets the element specified by {@code index}.
	 * <p>
	 * <li>If {@code index >= 0}, returns the {@code index}-th element from the
	 * series.</li>
	 * <li>Otherwise, returns the {@code (|index|-1)}-th element from the end of
	 * the series.</li>
	 * 
	 * @param index
	 *            - index of the desired element.
	 * @return
	 */
	public double get(int index) {
		synchronized (values) {
			if (index < 0)
				return values.get(values.size() + index);
			return values.get(index);
		}
	}

	public double get(double x) {
		int i = (int) Math.floor(x);
		double y1 = values.get(i);
		double y2 = values.get(i + 1);
		return y1 + (x - i) * (y2 - y1);
	}

	public void set(int index, double value) {
		values.set(index, value);
	}

	/**
	 * Gets the backward difference at {@code index}. Equivalent to:
	 * {@code get(index)-get(index-1)}.
	 * <p>
	 * <li>If {@code index >= 0}, returns the {@code index}-th difference from
	 * the series.</li>
	 * <li>Otherwise, returns the {@code (|index|-1)}-th difference from the end
	 * of the series.</li>
	 * 
	 * @param index
	 *            - index of the desired difference.
	 * @return
	 */
	public double diff(int index) {
		return get(index) - get(index - 1);
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	public void clear() {
		values.clear();
	}

	public Series1D differentiate(Series1D x) {
		Series1D dydx = new Series1D();
		dydx.add(0);
		int n = Math.min(size(), x.size());
		for (int i = 1; i < n; i++)
			dydx.add(diff(i) / x.diff(i));
		return dydx;
	}

	public Series1D scale(double scale) {
		Series1D scaled = new Series1D();
		int n = size();
		for (int i = 0; i < n; i++)
			scaled.add(get(i) * scale);
		return scaled;
	}

	public Series1D cumulate() {
		Series1D cumulative = new Series1D();
		double sum = 0;
		int n = size();
		for (int i = 0; i < n; i++) {
			sum += get(i);
			cumulative.add(sum);
		}
		return cumulative;
	}

	public double sum() {
		double sum = 0;
		int n = size();
		for (int i = 0; i < n; i++)
			sum += get(i);
		return sum;
	}

	public double sum(Series1D x) {
		double sum = 0;
		int n = size();
		double x0 = x.get(0), x1;
		double y0 = get(0), y1;
		for (int i = 1; i < n; i++) {
			x1 = x.get(i);
			y1 = get(i);
			sum += (x1 - x0) * (y1 + y0) / 2;
			x0 = x1;
			y0 = y1;
		}
		return sum;
	}

	public double sum(BinaryMask1D mask) {
		double sum = 0;
		int n = size();
		for (int i = 0; i < n; i++)
			if (mask.get(i))
				sum += get(i);
		return sum;
	}

	public double rmse(Series1D s) {
		double e = 0;
		int n = Math.min(size(), s.size());
		for (int i = 0; i < n; i++) {
			double d = s.get(i) - get(i);
			e += d * d;
		}
		return Math.sqrt(e / n);
	}

	public double mae(Series1D s) {
		double e = 0;
		int n = Math.min(size(), s.size());
		for (int i = 0; i < n; i++) {
			double d = s.get(i) - get(i);
			e += Math.abs(d);
		}
		return e / n;
	}

	@Override
	public Vector getPoint(int i) {
		return new Vector(i, get(i));
	}

	private void updateBounds(double value) {
		if (values.isEmpty()) {
			min = max = value;
		} else {
			min = Math.min(value, min);
			max = Math.max(value, max);
		}
	}

	@Override
	public double getMinX() {
		return 0;
	}

	@Override
	public double getMaxX() {
		return size() - 1;
	}

	@Override
	public double getMinY() {
		return min;
	}

	@Override
	public double getMaxY() {
		return max;
	}

	public synchronized final Statistics getStatistics() {
		Statistics stats = new Statistics();
		int n = size();
		for (int i = 0; i < n; i++)
			stats.add(get(i));
		return stats;
	}

	@Override
	public Iterator<Vector> iterator() {
		return new Series1DIterator();
	}

	private class Series1DIterator implements Iterator<Vector> {

		private int i;

		@Override
		public boolean hasNext() {
			return i < size();
		}

		@Override
		public Vector next() {
			return getPoint(i++);
		}

		@Override
		public void remove() {
			Series1D.this.remove(i);
		}

	}

}
