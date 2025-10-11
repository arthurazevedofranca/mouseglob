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
import java.util.List;

import dcc.graphics.math.Vector;

public abstract class Series implements Iterable<Vector> {

	protected static final int INITIAL_SIZE = 1000;

	private List<BoundsListener> boundsListeners;
	private List<SeriesListener> seriesListeners;

	Series() {
		boundsListeners = new ArrayList<BoundsListener>();
		seriesListeners = new ArrayList<SeriesListener>();
	}

	public final void addBoundsListener(BoundsListener listener) {
		synchronized (boundsListeners) {
			boundsListeners.add(listener);
		}
	}

	public final void removeBoundsListener(BoundsListener listener) {
		synchronized (boundsListeners) {
			boundsListeners.remove(listener);
		}
	}

	public final void clearBoundsListeners() {
		synchronized (boundsListeners) {
			boundsListeners.clear();
		}
	}

	protected final void notifyBoundsChanged() {
		synchronized (boundsListeners) {
			for (BoundsListener listener : boundsListeners)
				listener.onBoundsChanged(this);
		}
	}

	public final void addSeriesListener(SeriesListener listener) {
		synchronized (seriesListeners) {
			seriesListeners.add(listener);
		}
	}

	public final void removeSeriesListener(SeriesListener listener) {
		synchronized (seriesListeners) {
			seriesListeners.remove(listener);
		}
	}

	public final void clearSeriesListeners() {
		synchronized (seriesListeners) {
			seriesListeners.clear();
		}
	}

	protected final void notifySeriesChanged() {
		synchronized (seriesListeners) {
			for (SeriesListener listener : seriesListeners)
				listener.onSeriesChanged(this);
		}
	}

	public abstract boolean isEmpty();

	public abstract int size();

	public abstract Vector getPoint(int i);

	public abstract double getMinX();

	public abstract double getMaxX();

	public abstract double getMinY();

	public abstract double getMaxY();

}
