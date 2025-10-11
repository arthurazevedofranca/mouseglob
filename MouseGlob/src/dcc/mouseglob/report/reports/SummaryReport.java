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
package dcc.mouseglob.report.reports;

import dcc.graphics.series.Series;
import dcc.graphics.series.SeriesListener;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.DistanceAnalysis;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.mouseglob.report.TextReport;

@ReportInfo("Summary")
@ReportIcon("/resource/textReport16.png")
public class SummaryReport extends TextReport implements SeriesListener {

	private DistanceAnalysis distance;
	private Time time;

	@Inject
	public SummaryReport(DistanceAnalysis distance, Time time) {
		this.distance = distance;
		this.time = time;
		time.addSeriesListener(this);
		update();
	}

	@Override
	public void onSeriesChanged(Series series) {
		update();
	}

	private void update() {
		double ds = distance.sum();
		double dt = time.get(-1) - time.get(0);
		TableBuilder table = new TableBuilder();
		table.addRow("Total displacement:", format(ds), "cm");
		table.addRow("Average speed:", format(ds / dt), "cm/s");
		setText(table.toString());
	}

	@Override
	public void onClose() {
		time.removeSeriesListener(this);
	}

	private static String format(double x) {
		return String.format("%.2f", x);
	}

}
