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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import dcc.graphics.plot.oned.BarPlot;
import dcc.graphics.series.Series;
import dcc.graphics.series.Series1D;
import dcc.graphics.series.SeriesFormatter;
import dcc.graphics.series.SeriesListener;
import dcc.inject.Inject;
import dcc.mouseglob.analysis.Dataset.Time;
import dcc.mouseglob.analysis.analyses.DistanceAnalysis;
import dcc.mouseglob.report.PlotReport;
import dcc.mouseglob.report.ReportIcon;
import dcc.mouseglob.report.ReportInfo;
import dcc.ui.Action;
import dcc.util.TextTransfer;

@ReportInfo("Binned Distance")
@ReportIcon("/resource/binnedSeriesReport16.png")
@SuppressWarnings("serial")
public class BinnedDistanceReport extends PlotReport<BarPlot> implements
		SeriesListener {

	protected static final int DEFAULT_WIDTH = 500, DEFAULT_HEIGHT = 300;

	private final Time time;
	private final DistanceAnalysis distance;

	private final Series1D binFrom, binTo, binValue;

	private final JComboBox<Integer> comboBox;

	private double t0;
	private double binWidth = 5;

	@Inject
	public BinnedDistanceReport(Time time, DistanceAnalysis distance) {
		super(new BarPlot(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, time.getAxis(),
				distance.getAxis()));
		this.time = time;
		this.distance = distance;
		time.addSeriesListener(this);

		binFrom = new Series1D();
		binTo = new Series1D();
		binValue = new Series1D();

		BarPlot plot = getPlot();
		t0 = time.get(0);
		plot.getXAxis().setMin(t0);
		plot.setGridLines(true, false);

		getPopupMenu().add(copyAction);
		getPopupMenu().add(copyValuesAction);

		comboBox = new JComboBox<Integer>(new Integer[] { 1, 2, 5, 10, 30, 60,
				120, 300, 600 });
		comboBox.setEditable(true);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					binWidth = (Integer) comboBox.getSelectedItem();
					calculate(binWidth);
				} catch (Exception e) {
				}
			}
		});
		comboBox.setPreferredSize(new Dimension(60, 17));
		comboBox.setSelectedIndex(2);

		getStatusBar().add(new JLabel("Bin width (s):"));
		getStatusBar().add(comboBox);

		calculate(binWidth);
	}

	private Action copyAction = new Action("Copy bins to clipboard") {
		@Override
		public void actionPerformed() {
			SeriesFormatter formatter = new SeriesFormatter();
			String timeFormat = time.getAxis().getFormat();
			formatter.add(binFrom, "From (s)", timeFormat);
			formatter.add(binTo, "To (s)", timeFormat);
			formatter.add(binValue, "Distance (cm)", "%.2f");
			TextTransfer.copy(formatter.format());
		}
	};

	private Action copyValuesAction = new Action(
			"Copy bins to clipboard (values only)") {
		@Override
		public void actionPerformed() {
			SeriesFormatter formatter = new SeriesFormatter();
			formatter.add(binValue, "Distance (cm)", "%.2f");
			TextTransfer.copy(formatter.format());
		}
	};

	private void calculate(double dt) {
		BarPlot plot = getPlot();
		plot.clear();
		binFrom.clear();
		binTo.clear();
		binValue.clear();
		int n = time.size();
		int i = 0;
		while (i < n - 1) {
			double t1 = time.get(i), t2;
			double sum = 0;
			do {
				sum += distance.get(i);
				i++;
				t2 = time.get(i);
			} while (i < n - 1 && t2 - t1 < dt);
			plot.addBar(t1, t2, sum);
			binFrom.add(t1);
			binTo.add(t2);
			binValue.add(sum);
		}
		double tmax = t0 + binWidth * Math.ceil((time.get(-1) - t0) / binWidth);
		plot.getXAxis().setMax(tmax);
	}

	@Override
	public void onSeriesChanged(Series series) {
		calculate(binWidth);
	}

	@Override
	public void onClose() {
		time.removeSeriesListener(this);
	}

}
