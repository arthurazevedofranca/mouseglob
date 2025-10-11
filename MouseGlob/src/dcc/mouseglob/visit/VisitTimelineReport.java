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
package dcc.mouseglob.visit;

import dcc.graphics.series.Series;
import dcc.graphics.series.SeriesListener;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.report.EventTimelineReport;
import dcc.mouseglob.report.ReportInfo;

@ReportInfo("Visit Events")
public class VisitTimelineReport extends EventTimelineReport implements
		SeriesListener {

	private final Time time;

	@Inject
	public VisitTimelineReport(Time time, VisitAnalysis visitAnalysis) {
		super(visitAnalysis);
		this.time = time;
		getPlot().setMinTime(time.getMs(0));
		getPlot().setMaxTime(time.getMs(-1));
		time.addSeriesListener(this);
	}

	@Override
	public void onSeriesChanged(Series series) {
		getPlot().setMaxTime(time.getMs(-1));
		getApplet().redraw();
	}

	@Override
	public void onClose() {
		time.removeSeriesListener(this);
	}

}
