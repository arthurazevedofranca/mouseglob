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
import dcc.mouseglob.analysis.analyses.DistanceAnalysis;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.mouseglob.report.TextReport;

@ReportInfo("Visit Distance")
@ReportIcon("/resource/textReport16.png")
public class VisitDistanceReport extends TextReport implements SeriesListener {

	private final DistanceAnalysis distance;
	private final VisitAnalysis visitAnalysis;

	@Inject
	public VisitDistanceReport(VisitAnalysis visitAnalysis,
			DistanceAnalysis distance) {
		this.visitAnalysis = visitAnalysis;
		this.distance = distance;
		distance.addSeriesListener(this);
		update();
	}

	private void update() {
		TableBuilder table = new TableBuilder();
		table.addHeader("", "Distance (cm)");
		for (VisitEventClass visit : visitAnalysis.getEventClasses()) {
			Object[] row = new Object[2];
			row[0] = visit.getDescription();
			row[1] = String.format("%.2f", distance.sum(visit.getMask()));
			table.addRow(row);
		}
		setText(table.toString());
	}

	@Override
	public void onSeriesChanged(Series series) {
		update();
	}

	@Override
	public void onClose() {
		distance.removeSeriesListener(this);
	}

}
