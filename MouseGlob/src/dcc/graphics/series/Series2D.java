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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dcc.graphics.math.Vector;

public class Series2D extends Series implements Iterable<Vector> {

	protected final List<Vector> points;
	private double minX, maxX, minY, maxY;

	public Series2D() {
		points = new ArrayList<Vector>(INITIAL_SIZE);
	}

	public void add(double x, double y) {
		boolean boundsChanged = updateBounds(x, y);
		synchronized (points) {
			points.add(new Vector(x, y));
		}
		if (boundsChanged)
			notifyBoundsChanged();
		notifySeriesChanged();
	}

	public void add(Vector p) {
		add(p.x, p.y);
		notifySeriesChanged();
	}

	void add(double[] xs, double[] ys) {
		boolean boundsChanged = false;
		int n = Math.min(xs.length, ys.length);
		for (int i = 0; i < n; i++) {
			if (updateBounds(xs[i], ys[i]))
				boundsChanged = true;
			points.add(new Vector(xs[i], ys[i]));
		}
		if (boundsChanged)
			notifyBoundsChanged();
		notifySeriesChanged();
	}

	void add(Series2D series) {
		boolean boundsChanged = false;
		for (Vector point : series) {
			if (updateBounds(point.x, point.y))
				boundsChanged = true;
			points.add(point);
		}
		if (boundsChanged)
			notifyBoundsChanged();
		notifySeriesChanged();
	}

	/**
	 * Gets the element specified by {@code index}.
	 * <p>
	 * <li>If {@code index >= 0}, returns the {@code index}-th element from the
	 * series.</li>
	 * <li>Otherwise, returns the {@code (length - |index|)}-th element from the
	 * end of the series.</li>
	 * <p>
	 * Ex.: {@code get(-1)} returns the series' last value.
	 * 
	 * @param index
	 *            - index of the desired element.
	 * @return
	 */
	public Vector get(int index) {
		if (index < 0) {
			index = points.size() + index;
			if (index < 0)
				return null;
		}
		return points.get(index);
	}

	public Vector get(double x) {
		int i = (int) Math.floor(x);
		Vector p1 = points.get(i);
		Vector p2 = points.get(i + 1);
		return p1.lerp(p2, x - i);
	}

	public void set(int index, Vector p) {
		points.set(index, p);
	}

	private boolean updateBounds(double x, double y) {
		boolean boundsChanged = false;
		if (points.isEmpty()) {
			minX = maxX = x;
			minY = maxY = y;
			boundsChanged = true;
		} else {
			if (x < minX) {
				minX = x;
				boundsChanged = true;
			}
			if (x > maxX) {
				maxX = x;
				boundsChanged = true;
			}
			if (y < minY) {
				minY = y;
				boundsChanged = true;
			}
			if (y > maxY) {
				maxY = y;
				boundsChanged = true;
			}
		}
		return boundsChanged;
	}

	@Override
	public boolean isEmpty() {
		return points.isEmpty();
	}

	@Override
	public int size() {
		return points.size();
	}

	public void clear() {
		synchronized (this) {
			points.clear();
		}
		notifyBoundsChanged();
		notifySeriesChanged();
	}

	public Series2D differentiate(Series1D t) {
		Series2D dudt = new Series2D();
		dudt.add(Vector.ZERO);
		int n = Math.min(size(), t.size());
		for (int i = 1; i < n; i++) {
			Vector u0 = get(i);
			Vector u1 = get(i - 1);
			double dx = u0.x - u1.x;
			double dy = u0.y - u1.y;
			double dt = t.diff(i);
			dudt.add(dx / dt, dy / dt);
		}
		return dudt;
	}

	public Series1D magnitude() {
		Series1D magnitude = new Series1D();
		synchronized (points) {
			for (Vector u : points)
				magnitude.add(u.magnitude());
		}
		return magnitude;
	}

	public double length() {
		double length = 0;
		Vector p1, p2 = get(0);
		int n = size();
		for (int i = 1; i < n; i++) {
			p1 = p2;
			p2 = get(i);
			length += p1.distance(p2);
		}
		return length;
	}

	public Series2D scale(double scale) {
		Series2D scaled = new Series2D();
		for (Vector p : this)
			scaled.add(p.multiply(scale));
		return scaled;
	}

	public Series1D getX() {
		Series1D x = new Series1D();
		for (Vector p : this)
			x.add(p.x);
		return x;
	}

	public Series1D getY() {
		Series1D y = new Series1D();
		for (Vector p : this)
			y.add(p.y);
		return y;
	}

	@Override
	public Vector getPoint(int i) {
		return points.get(i);
	}

	@Override
	public double getMinX() {
		return minX;
	}

	@Override
	public double getMaxX() {
		return maxX;
	}

	@Override
	public double getMinY() {
		return minY;
	}

	@Override
	public double getMaxY() {
		return maxY;
	}

	@Override
	public Iterator<Vector> iterator() {
		return points.iterator();
	}

}
