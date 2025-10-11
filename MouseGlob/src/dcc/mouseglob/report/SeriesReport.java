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
package dcc.mouseglob.report;

import dcc.graphics.plot.oned.Axis;
import dcc.graphics.plot.oned.SeriesPlot;
import dcc.graphics.series.Series;

@ReportIcon("/resource/timeSeriesReport16.png")
public class SeriesReport extends PlotReport<SeriesPlot> {

	protected static final int DEFAULT_WIDTH = 500, DEFAULT_HEIGHT = 300;

	private final Axis xAxis, yAxis;

	public SeriesReport(Axis xAxis, Axis yAxis, boolean scalingConstrained) {
		super(new SeriesPlot(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, xAxis, yAxis));
		getPlot().setScalingConstrained(scalingConstrained);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

	public void addSeries(Series series, int color) {
		getPlot().add(series, color);
	}

	public void removeSeries(Series series) {
		getPlot().remove(series);
	}

	public void clearSeries() {
		getPlot().clear();
	}

	protected Axis getXAxis() {
		return xAxis;
	}

	protected Axis getYAxis() {
		return yAxis;
	}

}
