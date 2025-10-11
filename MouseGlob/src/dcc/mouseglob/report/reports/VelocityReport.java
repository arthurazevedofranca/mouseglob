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

import dcc.graphics.Color;
import dcc.graphics.math.Vector;
import dcc.graphics.math.stats.Statistics;
import dcc.graphics.series.CompoundSeries;
import dcc.graphics.series.Series1D;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.VelocityAnalysis;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.mouseglob.report.SeriesReport;
import dcc.ui.Action;
import dcc.ui.PopupMenu;
import dcc.util.TextTransfer;

@ReportInfo("Velocity")
@ReportIcon("/resource/trajectoryReport16.png")
public class VelocityReport extends SeriesReport {

	private VelocityAnalysis velocity;

	@SuppressWarnings("serial")
	@Inject
	public VelocityReport(VelocityAnalysis velocity, Time time) {
		super(time.getAxis(), velocity.getYAxis(), false);
		final Series1D vx = velocity.getX();
		final Series1D vy = velocity.getY();
		addSeries(new CompoundSeries(time, vx), Color.BLUE);
		addSeries(new CompoundSeries(time, vy), Color.GREEN);
		setSize(DEFAULT_WIDTH, DEFAULT_WIDTH);
		this.velocity = velocity;

		PopupMenu popupMenu = getPopupMenu();
		popupMenu.add(new Action("Copy to clipboard") {
			@Override
			public void actionPerformed() {
				TextTransfer.copy(getXYText());
			}
		});
		popupMenu.add(new Action("Copy statistics to clipboard") {
			@Override
			public void actionPerformed() {
				Statistics xStats = vx.getStatistics();
				Statistics yStats = vy.getStatistics();
				TextTransfer.copy(xStats.format() + "\n"
						+ yStats.format());
			}
		});
	}

	private String getXYText() {
		StringBuilder sb = new StringBuilder();
		sb.append(getXAxis().getLabel()).append('\t')
				.append(getYAxis().getLabel()).append('\n');
		for (Vector p : velocity)
			sb.append(p.x).append('\t').append(p.y).append('\n');
		return sb.toString();
	}

}
