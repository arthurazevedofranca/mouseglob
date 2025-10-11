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

import dcc.graphics.math.stats.Statistics;
import dcc.graphics.series.Series;
import dcc.graphics.series.SeriesListener;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.mouseglob.report.TextReport;

@ReportInfo("Visit Statistics")
@ReportIcon("/resource/textReport16.png")
public class VisitDurationStatisticsReport extends TextReport implements
		SeriesListener {

	private final Time time;
	private final VisitAnalysis visitAnalysis;

	@Inject
	public VisitDurationStatisticsReport(Time time, VisitAnalysis visitAnalysis) {
		this.time = time;
		this.visitAnalysis = visitAnalysis;
		time.addSeriesListener(this);
		update(time.getMs(-1));
	}

	private void update(long time) {
		TableBuilder table = new TableBuilder();
		// table.setColumnWidth(60);
		table.addHeader("", "# Entries", "# Exits", "Total<br>Duration (s)",
				"Average<br>Duration (s)", "Standard<br>Deviation (s)",
				"Latency (s)", "Longest (s)", "Shortest (s)");
		for (VisitEventClass visit : visitAnalysis.getEventClasses()) {
			Statistics stats = visit.getDurationStatistics(time);
			Object[] row = new Object[9];
			row[0] = visit.getDescription();
			row[1] = visit.getStartCount();
			row[2] = visit.getEndCount();
			row[3] = formatTime(stats.getSum());
			row[4] = formatTime(stats.getMean());
			row[5] = formatTime(stats.getStandardDeviation());
			row[6] = formatTime(visit.getLatency());
			row[7] = formatTime(stats.getMaximum());
			row[8] = formatTime(stats.getMinimum());
			table.addRow(row);
		}
		setText(table.toString());
	}

	private static String formatTime(double time) {
		if (!isValid(time))
			return "-";
		return String.format("%.2f", time / 1e3);
	}

	private static boolean isValid(double time) {
		return time != 0 && !Double.isInfinite(time) && !Double.isNaN(time);
	}

	@Override
	public void onSeriesChanged(Series series) {
		if (time != null)
			update(time.getMs(-1));
	}

	@Override
	public void onClose() {
		if (time != null)
			time.removeSeriesListener(this);
	}

}
