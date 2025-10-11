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

import java.util.ArrayList;
import java.util.List;

import processing.core.PGraphics;
import dcc.graphics.Color;
import dcc.graphics.series.BoundsListener;
import dcc.graphics.series.Series;
import dcc.graphics.series.SeriesListener;

public class SeriesPlot extends Plot1D implements BoundsListener,
		SeriesListener {

	private final List<Series> series;
	private final List<SeriesRenderer> seriesRenderers;

	public SeriesPlot(int x, int y, int width, int height, Axis xAxis,
			Axis yAxis) {
		super(x, y, width, height, xAxis, yAxis);

		series = new ArrayList<Series>();
		seriesRenderers = new ArrayList<SeriesRenderer>();
	}

	public synchronized void add(Series s) {
		add(s, Color.BLACK);
	}

	public synchronized void add(Series s, int color) {
		series.add(s);
		seriesRenderers.add(new SeriesRenderer(s, color));
		s.addBoundsListener(this);
		s.addSeriesListener(this);
		onBoundsChanged(s);
	}

	public synchronized void remove(Series s) {
		series.remove(s);
		SeriesRenderer sr = null;
		for (SeriesRenderer renderer : seriesRenderers) {
			if (renderer.getSeries().equals(s)) {
				sr = renderer;
				break;
			}
		}
		seriesRenderers.remove(sr);
		s.removeBoundsListener(this);
		s.removeSeriesListener(this);
		onBoundsChanged(null);
	}

	public synchronized void clear() {
		for (Series s : series) {
			s.removeBoundsListener(this);
			s.removeSeriesListener(this);
		}
		series.clear();
		seriesRenderers.clear();
		onBoundsChanged(null);
	}

	@Override
	public void onBoundsChanged(Series series) {
		if (xAxis.isAutoMin()) {
			double minX = Double.MAX_VALUE;
			for (Series s : this.series)
				minX = Math.min(minX, s.getMinX());
			xAxis.setMin(minX);
			boundsChanged = true;
		}

		if (xAxis.isAutoMax()) {
			double maxX = -Double.MAX_VALUE;
			for (Series s : this.series)
				maxX = Math.max(maxX, s.getMaxX());
			xAxis.setMax(maxX);
			boundsChanged = true;
		}

		if (yAxis.isAutoMin()) {
			double minY = Double.MAX_VALUE;
			for (Series s : this.series)
				minY = Math.min(minY, s.getMinY());
			yAxis.setMin(minY);
			boundsChanged = true;
		}

		if (yAxis.isAutoMax()) {
			double maxY = -Double.MAX_VALUE;
			for (Series s : this.series)
				maxY = Math.max(maxY, s.getMaxY());
			yAxis.setMax(maxY);
			boundsChanged = true;
		}

		if (boundsChanged)
			notifyRepaintNeeded();
	}

	@Override
	public void onSeriesChanged(Series series) {
		notifyIncrementNeeded();
	}

	@Override
	protected void paintPlotArea(PGraphics g) {
		g.pushStyle();
		for (SeriesRenderer s : seriesRenderers)
			s.paintLine(g, this);
		g.popStyle();
	}

	@Override
	public synchronized void paintIncrement(PGraphics g) {
		g.pushStyle();
		for (SeriesRenderer s : seriesRenderers)
			s.paintIncrement(g, this);
		g.popStyle();
	}

	/**
	 * Returns a snapshot copy of the current series list for export purposes.
	 */
	public synchronized java.util.List<Series> getSeries() {
		return new java.util.ArrayList<Series>(series);
	}
}
