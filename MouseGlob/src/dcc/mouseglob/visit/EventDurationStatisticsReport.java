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

import dcc.event.EventManager.TimedEventManager;
import dcc.event.TimedEventClass;
import dcc.graphics.binary.BinaryMask1D;
import dcc.graphics.math.stats.Statistics;
import dcc.graphics.series.Series;
import dcc.graphics.series.SeriesListener;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.DistanceAnalysis;
import dcc.mouseglob.report.TextReport;

public class EventDurationStatisticsReport extends TextReport implements
		SeriesListener {

	private final Time time;
	private final DistanceAnalysis distance;
	private final TimedEventManager<?> eventManager;

	public EventDurationStatisticsReport(Time time,
			TimedEventManager<?> eventManager, DistanceAnalysis distance) {
		this.time = time;
		this.eventManager = eventManager;
		this.distance = distance;
		if (time != null) {
			time.addSeriesListener(this);
			update(time.getMs(-1));
		}
	}

	public void update(long time) {
		TableBuilder table = new TableBuilder();
		// table.setColumnWidth(60);
		table.addHeader("", "# Entries", "# Exits", "Total Duration (s)",
				"Avg. Duration (s)", "Std. Deviation (s)", "Latency (s)",
				"Longest (s)", "Shortest (s)", "Distance (cm)");
		for (TimedEventClass c : eventManager.getEventClasses()) {
			Statistics stats = c.getDurationStatistics(time);
			Object[] row = new Object[10];
			row[0] = c.getDescription();
			row[1] = c.getStartCount();
			row[2] = c.getEndCount();
			row[3] = formatTime(stats.getSum());
			row[4] = formatTime(stats.getMean());
			row[5] = formatTime(stats.getStandardDeviation());
			row[6] = formatTime(c.getLatency());
			row[7] = formatTime(stats.getMaximum());
			row[8] = formatTime(stats.getMinimum());
			BinaryMask1D mask = TimedEventSeriesFactory.bsFrom(c, this.time);
			row[9] = String.format("%.2f", distance.sum(mask));
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
			update((long) (time.get(-1) * 1000));
	}

	@Override
	public void onClose() {
		if (time != null)
			time.removeSeriesListener(this);
	}

}
