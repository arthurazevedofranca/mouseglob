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
import dcc.graphics.Paintable;
import dcc.graphics.ScaledRenderer;
import dcc.graphics.series.SeriesFormatter;
import dcc.graphics.series.SmoothedSeries2D;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.PositionAnalysis;
import dcc.mouseglob.calibration.Calibration;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.ZonesManager;
import dcc.mouseglob.report.DisplayAction;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.mouseglob.report.SeriesReport;
import dcc.mouseglob.report.SmoothAction;
import dcc.ui.Action;
import dcc.ui.PopupMenu;
import dcc.util.TextTransfer;

@ReportInfo("Trajectory")
@ReportIcon("/resource/trajectoryReport16.png")
public class TrajectoryReport extends SeriesReport {

	private final Time time;
	private final SmoothedSeries2D smoothedPosition;

	@Inject
	public TrajectoryReport(Time time, PositionAnalysis position,
			Calibration calibration, BoundariesManager boundariesManager,
			ZonesManager zonesManager) {
		super(position.getXAxis(), position.getYAxis(), true);
		setSize(DEFAULT_WIDTH, DEFAULT_WIDTH);
		this.time = time;
		smoothedPosition = new SmoothedSeries2D(position);
		addSeries(smoothedPosition, Color.BLUE);

		double scale = calibration.getScale();

		Paintable boundariesRenderer = new ScaledRenderer(
				boundariesManager.getRenderer(), scale);
		Paintable zonesRenderer = new ScaledRenderer(
				zonesManager.getBasicRenderer(), scale);
		DisplayAction displayBoundariesAction = new DisplayAction(
				"Display Boundaries", getPlot(), boundariesRenderer);
		DisplayAction displayZonesAction = new DisplayAction("Display Zones",
				getPlot(), zonesRenderer);

		getPopupMenu().add(displayBoundariesAction.getCheckBoxMenuItem());
		getPopupMenu().add(displayZonesAction.getCheckBoxMenuItem());

		PopupMenu popupMenu = getPopupMenu();
		popupMenu.add(copyAction);
		popupMenu.add(copyValuesAction);

		double timeScale = (time.get(-1) - time.get(0)) / time.size();
		double maxSigma = 1 / timeScale;
		SmoothAction smoothAction = new SmoothAction(smoothedPosition,
				maxSigma, getApplet());
		popupMenu.add(smoothAction.getMenu());
	}

	@SuppressWarnings("serial")
	private Action copyAction = new Action("Copy to clipboard") {
		@Override
		public void actionPerformed() {
			SeriesFormatter formatter = new SeriesFormatter();
			formatter.add(time, time.getAxis());
			formatter.add(smoothedPosition.getX(), getXAxis().getLabel(),
					"%.2f");
			formatter.add(smoothedPosition.getY(), getYAxis().getLabel(),
					"%.2f");
			TextTransfer.copy(formatter.format());
		}
	};

	@SuppressWarnings("serial")
	private Action copyValuesAction = new Action(
			"Copy to clipboard (values only)") {
		@Override
		public void actionPerformed() {
			SeriesFormatter formatter = new SeriesFormatter();
			formatter.add(smoothedPosition.getX(), getXAxis().getLabel(),
					"%.2f");
			formatter.add(smoothedPosition.getY(), getYAxis().getLabel(),
					"%.2f");
			TextTransfer.copy(formatter.format());
		}
	};

	@Override
	protected void onClose() {
		smoothedPosition.dispose();
	}

}
