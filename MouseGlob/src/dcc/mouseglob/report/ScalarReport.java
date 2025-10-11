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

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dcc.graphics.Color;
import dcc.graphics.math.stats.Statistics;
import dcc.graphics.series.CompoundSeries;
import dcc.graphics.series.SeriesFormatter;
import dcc.graphics.series.SmoothedSeries1D;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.ScalarAnalysis;
import dcc.mouseglob.report.ReportDescriptor.ScalarReportDescriptor;
import dcc.ui.Action;
import dcc.ui.PopupMenu;
import dcc.ui.StatusBar;
import dcc.util.TextTransfer;

@SuppressWarnings("serial")
public class ScalarReport extends SeriesReport {

	private final Time time;
	private final ScalarAnalysis analysis;
	private SmoothedSeries1D smoothedSeries;
	private CompoundSeries compoundSeries;

	public ScalarReport(Time time, ScalarAnalysis analysis) {
		super(time.getAxis(), analysis.getAxis(), false);
		this.time = time;
		this.analysis = analysis;
		smoothedSeries = new SmoothedSeries1D(analysis);
		compoundSeries = new CompoundSeries(time, smoothedSeries);
		addSeries(compoundSeries, Color.RED);

		PopupMenu popupMenu = getPopupMenu();
		popupMenu.add(copyAction);
		popupMenu.add(copyValuesAction);
		popupMenu.add(copyStatisticsAction);
		popupMenu.add(showAreaUnderCurve);

		StatusBar statusBar = getStatusBar();
		JButton button = showAreaUnderCurve.getIconButton();
		Dimension dim = new Dimension(18, 18);
		button.setMinimumSize(dim);
		button.setPreferredSize(dim);
		button.setMaximumSize(dim);
		statusBar.add(button);

		double timeScale = (time.get(-1) - time.get(0)) / time.size();
		double maxSigma = 10 / timeScale; // max sigma = 10 seconds
		SmoothAction smoothAction = new SmoothAction(smoothedSeries, maxSigma,
				getApplet());
		popupMenu.add(smoothAction.getMenu());
	}

	private Action copyAction = new Action("Copy to clipboard") {
		@Override
		public void actionPerformed() {
			SeriesFormatter formatter = new SeriesFormatter();
			formatter.add(time, getXAxis());
			formatter.add(smoothedSeries, getYAxis().getLabel());
			TextTransfer.copy(formatter.format());
		}
	};

	private Action copyValuesAction = new Action(
			"Copy to clipboard (values only)") {
		@Override
		public void actionPerformed() {
			SeriesFormatter formatter = new SeriesFormatter();
			formatter.add(smoothedSeries, getYAxis().getLabel());
			TextTransfer.copy(formatter.format());
		}
	};

	private Action copyStatisticsAction = new Action(
			"Copy statistics to clipboard") {
		@Override
		public void actionPerformed() {
			Statistics stats = smoothedSeries.getStatistics();
			TextTransfer.copy(stats.format());
		}
	};

	private Action showAreaUnderCurve = new Action("Show area under the curve",
			"/resource/areaUnderCurve16.png") {
		@Override
		public void actionPerformed() {
			StatusBar statusBar = getStatusBar();
			JLabel label = (JLabel) ((JPanel) statusBar.getComponent(0))
					.getComponent(0);
			String text = "Area: " + analysis.sum(time);
			label.setText(text);
		}
	};

	@Override
	public ScalarReportDescriptor getDescriptor() {
		return new ScalarReportDescriptor(analysis.getClass());
	}

	@Override
	protected void onClose() {
		smoothedSeries.dispose();
		compoundSeries.dispose();
	}

}
